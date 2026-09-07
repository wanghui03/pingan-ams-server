package com.pingan.ams.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pingan.ams.common.exception.BusinessException;
import com.pingan.ams.common.result.ResultCode;
import com.pingan.ams.mapper.*;
import com.pingan.ams.model.dto.ContractDTO;
import com.pingan.ams.model.entity.*;
import com.pingan.ams.model.enums.BillStatus;
import com.pingan.ams.model.enums.ContractStatus;
import com.pingan.ams.model.enums.RoomStatus;
import com.pingan.ams.model.vo.ContractVO;
import com.pingan.ams.service.ContractService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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
    private final NotificationMapper notificationMapper;

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
        if (tenantId != null) {
            wrapper.eq(Contract::getTenantId, tenantId);
        }

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

        // 生成租金账单
        Bill rentBill = new Bill();
        rentBill.setTenantId(contract.getTenantId());
        rentBill.setBillNo(generateBillNo());
        rentBill.setContractId(contract.getId());
        rentBill.setUserId(contract.getUserId());
        rentBill.setRoomId(contract.getRoomId());
        rentBill.setBillType(1); // 租金
        rentBill.setStatus(BillStatus.UNPAID);
        rentBill.setAmount(contract.getMonthlyRent().multiply(BigDecimal.valueOf(monthsPerBill)));
        rentBill.setStartDate(contract.getStartDate());
        rentBill.setEndDate(contract.getStartDate().plusMonths(monthsPerBill));
        rentBill.setBillDate(LocalDate.now());
        rentBill.setDueDate(contract.getStartDate().plusDays(5)); // 5天内支付
        rentBill.setPaidAmount(BigDecimal.ZERO);
        billMapper.insert(rentBill);

        // 生成押金账单（如果押金大于0）
        if (contract.getDeposit() != null && contract.getDeposit().compareTo(BigDecimal.ZERO) > 0) {
            Bill depositBill = new Bill();
            depositBill.setTenantId(contract.getTenantId());
            depositBill.setBillNo(generateBillNo());
            depositBill.setContractId(contract.getId());
            depositBill.setUserId(contract.getUserId());
            depositBill.setRoomId(contract.getRoomId());
            depositBill.setBillType(3); // 押金
            depositBill.setStatus(BillStatus.UNPAID);
            depositBill.setAmount(contract.getDeposit());
            depositBill.setStartDate(contract.getStartDate());
            depositBill.setEndDate(contract.getEndDate());
            depositBill.setBillDate(LocalDate.now());
            depositBill.setDueDate(contract.getStartDate().plusDays(5)); // 5天内支付
            depositBill.setPaidAmount(BigDecimal.ZERO);
            billMapper.insert(depositBill);

            log.info("合同 {} 审核通过，已生成首期租金账单 {} 和押金账单 {}", 
                    contract.getContractNo(), rentBill.getBillNo(), depositBill.getBillNo());
        } else {
            log.info("合同 {} 审核通过，已生成首期租金账单 {}", contract.getContractNo(), rentBill.getBillNo());
        }
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

    @Override
    public void sendContractExpiryReminders() {
        LocalDate today = LocalDate.now();
        LocalDate reminderDate = today.plusDays(30);

        // 查找30天内到期的生效中合同
        LambdaQueryWrapper<Contract> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Contract::getStatus, ContractStatus.ACTIVE)
                .ge(Contract::getEndDate, today)
                .le(Contract::getEndDate, reminderDate);

        List<Contract> expiringContracts = this.list(wrapper);

        for (Contract contract : expiringContracts) {
            User user = userMapper.selectById(contract.getUserId());
            if (user == null) continue;

            String userName = user.getRealName() != null ? user.getRealName() : user.getNickname();
            long daysLeft = ChronoUnit.DAYS.between(today, contract.getEndDate());

            // 获取房间信息
            Room room = roomMapper.selectById(contract.getRoomId());
            String roomInfo = "";
            if (room != null) {
                Building building = buildingMapper.selectById(room.getBuildingId());
                roomInfo = (building != null ? building.getName() : "") + room.getRoomNo();
            }

            Notification notification = new Notification();
            notification.setTenantId(contract.getTenantId());
            notification.setUserId(contract.getUserId());
            notification.setTitle("合同到期提醒");
            notification.setContent(String.format(
                    "尊敬的%s，您在%s的合同（编号：%s）将于%s到期，剩余%d天。如需续租请尽快联系管理员。",
                    userName,
                    roomInfo,
                    contract.getContractNo(),
                    contract.getEndDate(),
                    daysLeft
            ));
            notification.setType(2); // 合同类型
            notification.setBizType("contract");
            notification.setBizId(contract.getId());
            notification.setIsRead(0);

            notificationMapper.insert(notification);
            log.info("已发送合同到期提醒：合同 {}，剩余 {} 天", contract.getContractNo(), daysLeft);
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
            vo.setStatus(contract.getStatus().getCode());
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
