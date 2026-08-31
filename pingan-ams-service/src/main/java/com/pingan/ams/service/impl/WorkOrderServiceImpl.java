package com.pingan.ams.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pingan.ams.common.exception.BusinessException;
import com.pingan.ams.common.result.ResultCode;
import com.pingan.ams.mapper.*;
import com.pingan.ams.model.dto.WorkOrderDTO;
import com.pingan.ams.model.entity.*;
import com.pingan.ams.model.enums.WorkOrderStatus;
import com.pingan.ams.model.vo.WorkOrderVO;
import com.pingan.ams.service.WorkOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 工单 Service 实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WorkOrderServiceImpl extends ServiceImpl<WorkOrderMapper, WorkOrder> implements WorkOrderService {

    private final UserMapper userMapper;
    private final RoomMapper roomMapper;
    private final BuildingMapper buildingMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createWorkOrder(Long tenantId, Long userId, WorkOrderDTO workOrderDTO) {
        // 检查房间
        Room room = roomMapper.selectById(workOrderDTO.getRoomId());
        if (room == null || !room.getTenantId().equals(tenantId)) {
            throw new BusinessException("房间不存在");
        }

        // 检查租客
        User user = userMapper.selectById(workOrderDTO.getUserId());
        if (user == null || !user.getTenantId().equals(tenantId)) {
            throw new BusinessException("租客不存在");
        }

        WorkOrder workOrder = new WorkOrder();
        BeanUtils.copyProperties(workOrderDTO, workOrder);
        workOrder.setTenantId(tenantId);
        workOrder.setUserId(workOrderDTO.getUserId()); // 使用DTO中的userId
        workOrder.setOrderNo(generateOrderNo());
        workOrder.setStatus(WorkOrderStatus.PENDING);

        this.save(workOrder);
        return workOrder.getId();
    }

    @Override
    public WorkOrderVO getWorkOrderDetail(Long tenantId, Long workOrderId) {
        WorkOrder workOrder = this.getWorkOrderByIdAndTenantId(workOrderId, tenantId);
        return convertToVO(workOrder);
    }

    @Override
    public Page<WorkOrderVO> listWorkOrders(Long tenantId, Integer page, Integer size, Integer status, Integer orderType, Long userId) {
        Page<WorkOrder> pageParam = new Page<>(page, size);

        LambdaQueryWrapper<WorkOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WorkOrder::getTenantId, tenantId);

        if (status != null) {
            wrapper.eq(WorkOrder::getStatus, status);
        }
        if (orderType != null) {
            wrapper.eq(WorkOrder::getOrderType, orderType);
        }
        if (userId != null) {
            wrapper.eq(WorkOrder::getUserId, userId);
        }

        wrapper.orderByDesc(WorkOrder::getCreateTime);

        Page<WorkOrder> workOrderPage = this.page(pageParam, wrapper);

        Page<WorkOrderVO> voPage = new Page<>(workOrderPage.getCurrent(), workOrderPage.getSize(), workOrderPage.getTotal());
        List<WorkOrderVO> voList = workOrderPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        voPage.setRecords(voList);

        return voPage;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignWorkOrder(Long tenantId, Long workOrderId, Long handlerId) {
        WorkOrder workOrder = this.getWorkOrderByIdAndTenantId(workOrderId, tenantId);

        if (workOrder.getStatus() != WorkOrderStatus.PENDING) {
            throw new BusinessException("只能分配待处理的工单");
        }

        workOrder.setHandlerId(handlerId);
        workOrder.setStatus(WorkOrderStatus.PROCESSING);

        this.updateById(workOrder);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handleWorkOrder(Long tenantId, Long workOrderId, String handleResult, String handleImages) {
        WorkOrder workOrder = this.getWorkOrderByIdAndTenantId(workOrderId, tenantId);

        if (workOrder.getStatus() != WorkOrderStatus.PROCESSING) {
            throw new BusinessException("工单不在处理中状态");
        }

        workOrder.setHandleResult(handleResult);
        workOrder.setHandleImages(handleImages);
        workOrder.setHandleTime(LocalDateTime.now());
        workOrder.setStatus(WorkOrderStatus.COMPLETED);

        this.updateById(workOrder);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void completeWorkOrder(Long tenantId, Long workOrderId) {
        WorkOrder workOrder = this.getWorkOrderByIdAndTenantId(workOrderId, tenantId);

        workOrder.setStatus(WorkOrderStatus.COMPLETED);
        workOrder.setHandleTime(LocalDateTime.now());

        this.updateById(workOrder);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void rateWorkOrder(Long tenantId, Long userId, Long workOrderId, Integer rating, String ratingContent) {
        WorkOrder workOrder = this.getWorkOrderByIdAndTenantId(workOrderId, tenantId);

        if (!workOrder.getUserId().equals(userId)) {
            throw new BusinessException(ResultCode.FORBIDDEN);
        }

        if (workOrder.getStatus() != WorkOrderStatus.COMPLETED) {
            throw new BusinessException("只能评价已完成的工单");
        }

        workOrder.setRating(rating);
        workOrder.setRatingContent(ratingContent);

        this.updateById(workOrder);
    }

    private WorkOrder getWorkOrderByIdAndTenantId(Long workOrderId, Long tenantId) {
        WorkOrder workOrder = this.getById(workOrderId);
        if (workOrder == null || !workOrder.getTenantId().equals(tenantId)) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        return workOrder;
    }

    private String generateOrderNo() {
        String prefix = "WO";
        String date = java.time.LocalDate.now().toString().replace("-", "");
        String random = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        return prefix + date + random;
    }

    private WorkOrderVO convertToVO(WorkOrder workOrder) {
        WorkOrderVO vo = new WorkOrderVO();
        BeanUtils.copyProperties(workOrder, vo);

        // 设置状态描述
        if (workOrder.getStatus() != null) {
            vo.setStatusDesc(workOrder.getStatus().getDesc());
        }

        // 设置工单类型描述
        if (workOrder.getOrderType() != null) {
            vo.setOrderTypeDesc(getOrderTypeDesc(workOrder.getOrderType()));
        }

        // 获取房间信息
        Room room = roomMapper.selectById(workOrder.getRoomId());
        if (room != null) {
            vo.setRoomNo(room.getRoomNo());
            // 获取楼栋信息
            Building building = buildingMapper.selectById(room.getBuildingId());
            if (building != null) {
                vo.setBuildingName(building.getName());
            }
        }

        // 获取租客信息
        User user = userMapper.selectById(workOrder.getUserId());
        if (user != null) {
            vo.setTenantName(user.getRealName() != null ? user.getRealName() : user.getNickname());
        }

        // 获取处理人信息
        if (workOrder.getHandlerId() != null) {
            User handler = userMapper.selectById(workOrder.getHandlerId());
            if (handler != null) {
                vo.setHandlerName(handler.getRealName() != null ? handler.getRealName() : handler.getNickname());
            }
        }

        return vo;
    }

    private String getOrderTypeDesc(Integer orderType) {
        return switch (orderType) {
            case 1 -> "报修";
            case 2 -> "投诉";
            case 3 -> "咨询";
            case 4 -> "其他";
            default -> "未知";
        };
    }
}
