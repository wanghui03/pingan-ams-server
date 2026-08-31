package com.pingan.ams.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pingan.ams.common.exception.BusinessException;
import com.pingan.ams.common.result.ResultCode;
import com.pingan.ams.mapper.*;
import com.pingan.ams.model.dto.BillDTO;
import com.pingan.ams.model.entity.*;
import com.pingan.ams.model.enums.BillStatus;
import com.pingan.ams.model.vo.BillVO;
import com.pingan.ams.service.BillService;
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
 * 账单 Service 实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BillServiceImpl extends ServiceImpl<BillMapper, Bill> implements BillService {

    private final ContractMapper contractMapper;
    private final UserMapper userMapper;
    private final RoomMapper roomMapper;
    private final BuildingMapper buildingMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createBill(Long tenantId, BillDTO billDTO) {
        // 检查合同
        Contract contract = contractMapper.selectById(billDTO.getContractId());
        if (contract == null || !contract.getTenantId().equals(tenantId)) {
            throw new BusinessException("合同不存在");
        }

        Bill bill = new Bill();
        BeanUtils.copyProperties(billDTO, bill);
        bill.setTenantId(tenantId);
        bill.setBillNo(generateBillNo());
        bill.setUserId(contract.getUserId());
        bill.setRoomId(contract.getRoomId());
        bill.setStatus(BillStatus.UNPAID);
        bill.setPaidAmount(BigDecimal.ZERO);

        this.save(bill);
        return bill.getId();
    }

    @Override
    public BillVO getBillDetail(Long tenantId, Long billId) {
        Bill bill = this.getBillByIdAndTenantId(billId, tenantId);
        return convertToVO(bill);
    }

    @Override
    public Page<BillVO> listBills(Long tenantId, Integer page, Integer size, Integer status, Integer billType, Long userId) {
        Page<Bill> pageParam = new Page<>(page, size);

        LambdaQueryWrapper<Bill> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Bill::getTenantId, tenantId);

        if (status != null) {
            wrapper.eq(Bill::getStatus, status);
        }
        if (billType != null) {
            wrapper.eq(Bill::getBillType, billType);
        }
        if (userId != null) {
            wrapper.eq(Bill::getUserId, userId);
        }

        wrapper.orderByDesc(Bill::getCreateTime);

        Page<Bill> billPage = this.page(pageParam, wrapper);

        Page<BillVO> voPage = new Page<>(billPage.getCurrent(), billPage.getSize(), billPage.getTotal());
        List<BillVO> voList = billPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        voPage.setRecords(voList);

        return voPage;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markAsPaid(Long tenantId, Long billId, BigDecimal paidAmount, String transactionNo) {
        Bill bill = this.getBillByIdAndTenantId(billId, tenantId);

        if (bill.getStatus() == BillStatus.PAID) {
            throw new BusinessException("账单已支付");
        }

        bill.setStatus(BillStatus.PAID);
        bill.setPaidAmount(paidAmount);
        bill.setPaidTime(java.time.LocalDateTime.now());
        bill.setTransactionNo(transactionNo);

        this.updateById(bill);

        // 支付完成后，生成下一期账单
        generateNextBill(bill);
    }

    /**
     * 根据已支付账单生成下一期账单
     */
    private void generateNextBill(Bill paidBill) {
        // 只处理租金账单
        if (paidBill.getBillType() != 1) {
            return;
        }

        // 获取合同信息
        Contract contract = contractMapper.selectById(paidBill.getContractId());
        if (contract == null || contract.getStatus() != com.pingan.ams.model.enums.ContractStatus.ACTIVE) {
            return;
        }

        // 计算下一期账单的日期
        LocalDate nextStartDate = paidBill.getEndDate();
        if (!nextStartDate.isBefore(contract.getEndDate())) {
            // 合同已到期，不再生成账单
            return;
        }

        // 检查是否已存在下一期账单
        LambdaQueryWrapper<Bill> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Bill::getContractId, contract.getId())
               .eq(Bill::getBillType, 1)
               .eq(Bill::getStartDate, nextStartDate);
        if (this.count(wrapper) > 0) {
            return;
        }

        int monthsPerBill = getMonthsPerBill(contract.getPaymentMethod());
        LocalDate nextEndDate = nextStartDate.plusMonths(monthsPerBill);
        if (nextEndDate.isAfter(contract.getEndDate())) {
            nextEndDate = contract.getEndDate();
        }

        Bill nextBill = new Bill();
        nextBill.setTenantId(contract.getTenantId());
        nextBill.setBillNo(generateBillNo());
        nextBill.setContractId(contract.getId());
        nextBill.setUserId(contract.getUserId());
        nextBill.setRoomId(contract.getRoomId());
        nextBill.setBillType(1); // 租金
        nextBill.setStatus(BillStatus.UNPAID);
        nextBill.setAmount(contract.getMonthlyRent().multiply(BigDecimal.valueOf(monthsPerBill)));
        nextBill.setStartDate(nextStartDate);
        nextBill.setEndDate(nextEndDate);
        nextBill.setBillDate(LocalDate.now());
        nextBill.setDueDate(nextStartDate.plusDays(5)); // 5天内支付
        nextBill.setPaidAmount(BigDecimal.ZERO);

        this.save(nextBill);
        log.info("账单 {} 已支付，已生成下一期账单 {}", paidBill.getBillNo(), nextBill.getBillNo());
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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelBill(Long tenantId, Long billId, String reason) {
        Bill bill = this.getBillByIdAndTenantId(billId, tenantId);

        if (bill.getStatus() == BillStatus.PAID) {
            throw new BusinessException("已支付的账单不能取消");
        }

        bill.setStatus(BillStatus.CANCELLED);
        bill.setRemark(reason);

        this.updateById(bill);
    }

    @Override
    public BigDecimal getUnpaidAmount(Long tenantId) {
        LambdaQueryWrapper<Bill> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Bill::getTenantId, tenantId);
        wrapper.eq(Bill::getStatus, BillStatus.UNPAID);

        List<Bill> unpaidBills = this.list(wrapper);
        return unpaidBills.stream()
                .map(Bill::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void generateRentBills(Long contractId) {
        Contract contract = contractMapper.selectById(contractId);
        if (contract == null) {
            throw new BusinessException("合同不存在");
        }

        // 根据支付方式生成账单
        int monthsPerBill = switch (contract.getPaymentMethod()) {
            case 1 -> 1;  // 月付
            case 2 -> 3;  // 季付
            case 3 -> 6;  // 半年付
            case 4 -> 12; // 年付
            default -> 1;
        };

        LocalDate currentDate = contract.getStartDate();
        LocalDate endDate = contract.getEndDate();

        while (currentDate.isBefore(endDate)) {
            LocalDate billEndDate = currentDate.plusMonths(monthsPerBill);
            if (billEndDate.isAfter(endDate)) {
                billEndDate = endDate;
            }

            Bill bill = new Bill();
            bill.setTenantId(contract.getTenantId());
            bill.setBillNo(generateBillNo());
            bill.setContractId(contractId);
            bill.setUserId(contract.getUserId());
            bill.setRoomId(contract.getRoomId());
            bill.setBillType(1); // 租金
            bill.setStatus(BillStatus.UNPAID);
            bill.setAmount(contract.getMonthlyRent().multiply(BigDecimal.valueOf(monthsPerBill)));
            bill.setStartDate(currentDate);
            bill.setEndDate(billEndDate);
            bill.setBillDate(currentDate); // 账单日期为周期开始
            bill.setDueDate(currentDate.plusDays(5)); // 5天内支付
            bill.setPaidAmount(BigDecimal.ZERO);

            this.save(bill);

            currentDate = billEndDate;
        }
    }

    @Override
    public void autoGenerateBills() {
        // 查找所有生效中的合同
        List<Contract> activeContracts = contractMapper.selectList(
            new LambdaQueryWrapper<Contract>()
                .eq(Contract::getStatus, com.pingan.ams.model.enums.ContractStatus.ACTIVE)
        );

        for (Contract contract : activeContracts) {
            // 检查该合同今天是否需要生成账单
            if (shouldGenerateBillToday(contract)) {
                // 检查是否已生成过
                if (!hasBillForCurrentPeriod(contract)) {
                    generateCurrentPeriodBill(contract);
                    log.info("为合同 {} 自动生成账单", contract.getContractNo());
                }
            }
        }
    }

    /**
     * 判断今天是否应该为该合同生成账单
     */
    private boolean shouldGenerateBillToday(Contract contract) {
        LocalDate today = LocalDate.now();
        int monthsPerBill = getMonthsPerBill(contract.getPaymentMethod());

        // 从合同开始日期计算，今天是否是某个账单周期的开始日期
        LocalDate checkDate = contract.getStartDate();
        while (checkDate.isBefore(contract.getEndDate())) {
            if (checkDate.equals(today)) {
                return true;
            }
            checkDate = checkDate.plusMonths(monthsPerBill);
        }
        return false;
    }

    /**
     * 检查当前周期是否已有账单
     */
    private boolean hasBillForCurrentPeriod(Contract contract) {
        LocalDate today = LocalDate.now();
        LambdaQueryWrapper<Bill> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Bill::getContractId, contract.getId())
               .eq(Bill::getBillType, 1)
               .le(Bill::getStartDate, today)
               .gt(Bill::getEndDate, today);
        return this.count(wrapper) > 0;
    }

    /**
     * 为当前周期生成账单
     */
    private void generateCurrentPeriodBill(Contract contract) {
        LocalDate today = LocalDate.now();
        int monthsPerBill = getMonthsPerBill(contract.getPaymentMethod());

        // 找到当前周期的开始日期
        LocalDate periodStart = contract.getStartDate();
        while (periodStart.plusMonths(monthsPerBill).isBefore(today)) {
            periodStart = periodStart.plusMonths(monthsPerBill);
        }

        LocalDate periodEnd = periodStart.plusMonths(monthsPerBill);
        if (periodEnd.isAfter(contract.getEndDate())) {
            periodEnd = contract.getEndDate();
        }

        Bill bill = new Bill();
        bill.setTenantId(contract.getTenantId());
        bill.setBillNo(generateBillNo());
        bill.setContractId(contract.getId());
        bill.setUserId(contract.getUserId());
        bill.setRoomId(contract.getRoomId());
        bill.setBillType(1);
        bill.setStatus(BillStatus.UNPAID);
        bill.setAmount(contract.getMonthlyRent().multiply(BigDecimal.valueOf(monthsPerBill)));
        bill.setStartDate(periodStart);
        bill.setEndDate(periodEnd);
        bill.setBillDate(today);
        bill.setDueDate(today.plusDays(5));
        bill.setPaidAmount(BigDecimal.ZERO);

        this.save(bill);
    }

    @Override
    public void processOverdueBills() {
        LocalDate today = LocalDate.now();

        // 查找所有待支付但已逾期的账单
        List<Bill> overdueBills = this.list(
            new LambdaQueryWrapper<Bill>()
                .eq(Bill::getStatus, BillStatus.UNPAID)
                .lt(Bill::getDueDate, today)
        );

        for (Bill bill : overdueBills) {
            bill.setStatus(BillStatus.OVERDUE);
            this.updateById(bill);
            log.info("账单 {} 已逾期，状态已更新", bill.getBillNo());
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

    private Bill getBillByIdAndTenantId(Long billId, Long tenantId) {
        Bill bill = this.getById(billId);
        if (bill == null || !bill.getTenantId().equals(tenantId)) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        return bill;
    }

    private String generateBillNo() {
        String prefix = "BL";
        String date = LocalDate.now().toString().replace("-", "");
        String random = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        return prefix + date + random;
    }

    private BillVO convertToVO(Bill bill) {
        BillVO vo = new BillVO();
        BeanUtils.copyProperties(bill, vo);

        // 设置状态描述
        if (bill.getStatus() != null) {
            vo.setStatusDesc(bill.getStatus().getDesc());
        }

        // 设置账单类型描述
        if (bill.getBillType() != null) {
            vo.setBillTypeDesc(getBillTypeDesc(bill.getBillType()));
        }

        // 计算逾期天数
        if (bill.getStatus() == BillStatus.UNPAID && bill.getDueDate() != null) {
            if (LocalDate.now().isAfter(bill.getDueDate())) {
                int overdueDays = (int) ChronoUnit.DAYS.between(bill.getDueDate(), LocalDate.now());
                vo.setOverdueDays(overdueDays);
            }
        }

        // 获取房间信息
        Room room = roomMapper.selectById(bill.getRoomId());
        if (room != null) {
            vo.setRoomNo(room.getRoomNo());
        }

        // 获取租客信息
        User user = userMapper.selectById(bill.getUserId());
        if (user != null) {
            vo.setTenantName(user.getRealName() != null ? user.getRealName() : user.getNickname());
        }

        return vo;
    }

    private String getBillTypeDesc(Integer billType) {
        return switch (billType) {
            case 1 -> "租金";
            case 2 -> "水电费";
            case 3 -> "押金";
            case 4 -> "其他";
            default -> "未知";
        };
    }
}
