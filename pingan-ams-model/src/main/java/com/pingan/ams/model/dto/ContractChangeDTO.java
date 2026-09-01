package com.pingan.ams.model.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 合同变更申请DTO
 */
@Data
public class ContractChangeDTO {

    /**
     * 原合同ID
     */
    private Long contractId;

    /**
     * 变更类型：1-续约 2-转租 3-提前退租
     */
    private Integer changeType;

    /**
     * 新开始日期（续约时）
     */
    private LocalDate newStartDate;

    /**
     * 新结束日期（续约时）
     */
    private LocalDate newEndDate;

    /**
     * 新月租金（续约时）
     */
    private BigDecimal newMonthlyRent;

    /**
     * 新租客ID（转租时）
     */
    private Long newUserId;

    /**
     * 转租手续费（转租时）
     */
    private BigDecimal transferFee;

    /**
     * 退租日期（提前退租时）
     */
    private LocalDate terminateDate;

    /**
     * 违约金（提前退租时）
     */
    private BigDecimal penaltyAmount;

    /**
     * 押金退还金额（提前退租时）
     */
    private BigDecimal depositRefund;

    /**
     * 变更原因
     */
    private String reason;
}
