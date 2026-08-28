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
            bill.setDueDate(currentDate.plusDays(5)); // 5天内支付
            bill.setPaidAmount(BigDecimal.ZERO);

            this.save(bill);

            currentDate = billEndDate;
        }
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
