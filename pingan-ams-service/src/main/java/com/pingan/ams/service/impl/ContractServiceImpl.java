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
    private final BillMapper billMapper;

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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void submitContract(Long tenantId, Long contractId) {
        Contract contract = this.getContractByIdAndTenantId(contractId, tenantId);
        
        if (contract.getStatus() != ContractStatus.DRAFT) {
            throw new BusinessException("只有草稿状态的合同可以提交审核");
        }

        contract.setStatus(ContractStatus.PENDING);
        this.updateById(contract);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approveContract(Long tenantId, Long contractId) {
        Contract contract = this.getContractByIdAndTenantId(contractId, tenantId);
        
        if (contract.getStatus() != ContractStatus.PENDING) {
            throw new BusinessException("只有待审核状态的合同可以审核通过");
        }

        contract.setStatus(ContractStatus.ACTIVE);
        this.updateById(contract);

        // 更新房间状态为已入住
        Room room = roomMapper.selectById(contract.getRoomId());
        if (room != null) {
            room.setStatus(RoomStatus.OCCUPIED);
            roomMapper.updateById(room);
        }

        // 生成首期账单
        generateFirstBill(contract);
    }

    /**
     * 生成首期账单（合同审核通过时调用）
     */
    private void generateFirstBill(Contract contract) {
        int monthsPerBill = getMonthsPerBill(contract.getPaymentMethod());

        Bill bill = new Bill();
        bill.setTenantId(contract.getTenantId());
        bill.setBillNo(generateBillNo());
        bill.setContractId(contract.getId());
        bill.setUserId(contract.getUserId());
        bill.setRoomId(contract.getRoomId());
        bill.setBillType(1); // 租金
        bill.setStatus(BillStatus.UNPAID);
        bill.setAmount(contract.getMonthlyRent().multiply(BigDecimal.valueOf(monthsPerBill)));
        bill.setStartDate(contract.getStartDate());
        bill.setEndDate(contract.getStartDate().plusMonths(monthsPerBill));
        bill.setBillDate(LocalDate.now());
        bill.setDueDate(contract.getStartDate().plusDays(5)); // 5天内支付
        bill.setPaidAmount(BigDecimal.ZERO);

        // 保存到数据库（需要通过 ContractService 调用 BillService）
        // 这里直接操作 mapper 避免循环依赖
        billMapper.insert(bill);

        log.info("合同 {} 审核通过，已生成首期账单 {}", contract.getContractNo(), bill.getBillNo());
    }

    private int getMonthsPerBill(Integer paymentMethod) {
        return switch (paymentMethod) {
            case 1 -> 1;  // 月付
            case 2 -> 3;  // 季付
            case 3 -> 6;  // 半年付
            case 4 -> 12; // 年付
            default -> 1;
        };
    }

    private String generateBillNo() {
        String prefix = "BL";
        String date = LocalDate.now().toString().replace("-", "");
        String random = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        return prefix + date + random;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void rejectContract(Long tenantId, Long contractId, String reason) {
        Contract contract = this.getContractByIdAndTenantId(contractId, tenantId);
        
        if (contract.getStatus() != ContractStatus.PENDING) {
            throw new BusinessException("只有待审核状态的合同可以审核驳回");
        }

        contract.setStatus(ContractStatus.DRAFT);
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
    @Transactional(rollbackFor = Exception.class)
    public void processExpiredContracts() {
        LocalDate today = LocalDate.now();
        
        // 查找所有生效中但已过期的合同
        List<Contract> expiredContracts = this.list(new LambdaQueryWrapper<Contract>()
                .eq(Contract::getStatus, ContractStatus.ACTIVE)
                .lt(Contract::getEndDate, today));

        for (Contract contract : expiredContracts) {
            contract.setStatus(ContractStatus.EXPIRED);
            this.updateById(contract);

            // 更新房间状态为空置
            Room room = roomMapper.selectById(contract.getRoomId());
            if (room != null) {
                room.setStatus(RoomStatus.VACANT);
                roomMapper.updateById(room);
            }

            log.info("合同 {} 已到期，状态已更新", contract.getContractNo());
        }
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
