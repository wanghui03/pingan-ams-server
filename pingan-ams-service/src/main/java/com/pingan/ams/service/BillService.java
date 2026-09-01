package com.pingan.ams.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.pingan.ams.model.dto.BillDTO;
import com.pingan.ams.model.entity.Bill;
import com.pingan.ams.model.vo.BillVO;

import java.math.BigDecimal;

/**
 * 账单 Service
 */
public interface BillService extends IService<Bill> {

    /**
     * 创建账单（手动创建，用于水电费等其他费用）
     */
    Long createBill(Long tenantId, BillDTO billDTO);

    /**
     * 获取账单详情
     */
    BillVO getBillDetail(Long tenantId, Long billId);

    /**
     * 分页查询账单
     */
    Page<BillVO> listBills(Long tenantId, Integer page, Integer size, Integer status, Integer billType, Long userId);

    /**
     * 标记账单已支付
     */
    void markAsPaid(Long tenantId, Long billId, BigDecimal paidAmount, String transactionNo);

    /**
     * 取消账单
     */
    void cancelBill(Long tenantId, Long billId, String reason);

    /**
     * 获取待支付账单总数
     */
    BigDecimal getUnpaidAmount(Long tenantId);

    /**
     * 根据合同自动生成租金账单（合同审核通过时调用）
     */
    void generateRentBills(Long contractId);

    /**
     * 自动生成所有到期合同的账单（定时任务调用）
     */
    void autoGenerateBills();

    /**
     * 处理逾期账单（定时任务调用）
     */
    void processOverdueBills();
}
