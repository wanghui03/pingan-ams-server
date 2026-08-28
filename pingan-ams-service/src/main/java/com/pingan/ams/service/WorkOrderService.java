package com.pingan.ams.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.pingan.ams.model.dto.WorkOrderDTO;
import com.pingan.ams.model.entity.WorkOrder;
import com.pingan.ams.model.vo.WorkOrderVO;

/**
 * 工单 Service
 */
public interface WorkOrderService extends IService<WorkOrder> {

    /**
     * 创建工单（租客提交）
     */
    Long createWorkOrder(Long tenantId, Long userId, WorkOrderDTO workOrderDTO);

    /**
     * 获取工单详情
     */
    WorkOrderVO getWorkOrderDetail(Long tenantId, Long workOrderId);

    /**
     * 分页查询工单
     */
    Page<WorkOrderVO> listWorkOrders(Long tenantId, Integer page, Integer size, Integer status, Integer orderType, Long userId);

    /**
     * 分配工单
     */
    void assignWorkOrder(Long tenantId, Long workOrderId, Long handlerId);

    /**
     * 处理工单
     */
    void handleWorkOrder(Long tenantId, Long workOrderId, String handleResult, String handleImages);

    /**
     * 完成工单
     */
    void completeWorkOrder(Long tenantId, Long workOrderId);

    /**
     * 评价工单
     */
    void rateWorkOrder(Long tenantId, Long userId, Long workOrderId, Integer rating, String ratingContent);
}
