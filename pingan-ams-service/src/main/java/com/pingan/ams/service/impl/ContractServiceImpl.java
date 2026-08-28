package com.pingan.ams.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pingan.ams.common.exception.BusinessException;
import com.pingan.ams.common.result.ResultCode;
import com.pingan.ams.mapper.*;
import com.pingan.ams.model.dto.ContractDTO;
import com.pingan.ams.model.entity.*;
import com.pingan.ams.model.enums.ContractStatus;
import com.pingan.ams.model.enums.RoomStatus;
import com.pingan.ams.model.vo.ContractVO;
import com.pingan.ams.service.ContractService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 合同 Service 实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ContractServiceImpl extends ServiceImpl<ContractMapper, Contract> implements ContractService {

    private final UserMapper userMapper;
    private final RoomMapper roomMapper;
    private final BuildingMapper buildingMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createContract(Long tenantId, ContractDTO contractDTO) {
        // 检查房间是否存在且可用
        Room room = roomMapper.selectById(contractDTO.getRoomId());
        if (room == null || !room.getTenantId().equals(tenantId)) {
            throw new BusinessException("房间不存在");
        }
        if (room.getStatus() != RoomStatus.VACANT) {
            throw new BusinessException("房间已被占用，无法创建合同");
        }

        // 检查租客是否存在
        User user = userMapper.selectById(contractDTO.getUserId());
        if (user == null || !user.getTenantId().equals(tenantId)) {
            throw new BusinessException("租客不存在");
        }

        // 创建合同
        Contract contract = new Contract();
        BeanUtils.copyProperties(contractDTO, contract);
        contract.setTenantId(tenantId);
        contract.setContractNo(generateContractNo());
        contract.setStatus(ContractStatus.DRAFT);
        contract.setSignStatus(0);

        this.save(contract);

        // 更新房间状态为已预订
        room.setStatus(RoomStatus.RESERVED);
        roomMapper.updateById(room);

        return contract.getId();
    }

    @Override
    public void updateContract(Long tenantId, Long contractId, ContractDTO contractDTO) {
        Contract contract = this.getContractByIdAndTenantId(contractId, tenantId);
        
        if (contract.getStatus() != ContractStatus.DRAFT) {
            throw new BusinessException("只有草稿状态的合同可以修改");
        }

        BeanUtils.copyProperties(contractDTO, contract);
        this.updateById(contract);
    }

    @Override
    public ContractVO getContractDetail(Long tenantId, Long contractId) {
        Contract contract = this.getContractByIdAndTenantId(contractId, tenantId);
        return convertToVO(contract);
    }

    @Override
    public Page<ContractVO> listContracts(Long tenantId, Integer page, Integer size, Integer status, Long userId, Long roomId) {
        Page<Contract> pageParam = new Page<>(page, size);

        LambdaQueryWrapper<Contract> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Contract::getTenantId, tenantId);

        if (status != null) {
            wrapper.eq(Contract::getStatus, status);
        }
        if (userId != null) {
            wrapper.eq(Contract::getUserId, userId);
        }
        if (roomId != null) {
            wrapper.eq(Contract::getRoomId, roomId);
        }

        wrapper.orderByDesc(Contract::getCreateTime);

        Page<Contract> contractPage = this.page(pageParam, wrapper);

        Page<ContractVO> voPage = new Page<>(contractPage.getCurrent(), contractPage.getSize(), contractPage.getTotal());
        List<ContractVO> voList = contractPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        voPage.setRecords(voList);

        return voPage;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void terminateContract(Long tenantId, Long contractId, String reason) {
        Contract contract = this.getContractByIdAndTenantId(contractId, tenantId);

        if (contract.getStatus() == ContractStatus.TERMINATED) {
            throw new BusinessException("合同已终止");
        }

        contract.setStatus(ContractStatus.TERMINATED);
        contract.setRemark(reason);
        this.updateById(contract);

        // 更新房间状态为空置
        Room room = roomMapper.selectById(contract.getRoomId());
        if (room != null) {
            room.setStatus(RoomStatus.VACANT);
            roomMapper.updateById(room);
        }
    }

    @Override
    public Contract getActiveContractByRoomId(Long roomId) {
        return this.getOne(new LambdaQueryWrapper<Contract>()
                .eq(Contract::getRoomId, roomId)
                .eq(Contract::getStatus, ContractStatus.ACTIVE)
                .last("LIMIT 1"));
    }

    @Override
    public Contract getActiveContractByUserId(Long userId) {
        return this.getOne(new LambdaQueryWrapper<Contract>()
                .eq(Contract::getUserId, userId)
                .eq(Contract::getStatus, ContractStatus.ACTIVE)
                .last("LIMIT 1"));
    }

    private Contract getContractByIdAndTenantId(Long contractId, Long tenantId) {
        Contract contract = this.getById(contractId);
        if (contract == null || !contract.getTenantId().equals(tenantId)) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        return contract;
    }

    private String generateContractNo() {
        String prefix = "HT";
        String date = LocalDate.now().toString().replace("-", "");
        String random = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        return prefix + date + random;
    }

    private ContractVO convertToVO(Contract contract) {
        ContractVO vo = new ContractVO();
        BeanUtils.copyProperties(contract, vo);

        // 设置状态描述
        if (contract.getStatus() != null) {
            vo.setStatusDesc(contract.getStatus().getDesc());
        }

        // 设置支付方式描述
        if (contract.getPaymentMethod() != null) {
            vo.setPaymentMethodDesc(getPaymentMethodDesc(contract.getPaymentMethod()));
        }

        // 计算剩余天数
        if (contract.getStatus() == ContractStatus.ACTIVE && contract.getEndDate() != null) {
            long remainDays = ChronoUnit.DAYS.between(LocalDate.now(), contract.getEndDate());
            vo.setRemainDays((int) Math.max(0, remainDays));
        }

        // 获取房间信息
        Room room = roomMapper.selectById(contract.getRoomId());
        if (room != null) {
            vo.setRoomNo(room.getRoomNo());
            Building building = buildingMapper.selectById(room.getBuildingId());
            if (building != null) {
                vo.setBuildingName(building.getName());
            }
        }

        // 获取租客信息
        User user = userMapper.selectById(contract.getUserId());
        if (user != null) {
            vo.setTenantName(user.getRealName() != null ? user.getRealName() : user.getNickname());
        }

        return vo;
    }

    private String getPaymentMethodDesc(Integer paymentMethod) {
        return switch (paymentMethod) {
            case 1 -> "月付";
            case 2 -> "季付";
            case 3 -> "半年付";
            case 4 -> "年付";
            default -> "未知";
        };
    }
}
