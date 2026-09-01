package com.pingan.ams.model.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 合同变更申请VO
 */
@Data
public class ContractChangeVO {

    /**
     * 变更ID
     */
    private Long id;

    /**
     * 租户ID
     */
    private Long tenantId;

    /**
     * 原合同ID
     */
    private Long contractId;

    /**
     * 原合同编号
     */
    private String contractNo;

    /**
     * 变更单号
     */
    private String changeNo;

    /**
     * 变更类型：1-续约 2-转租 3-提前退租
     */
    private Integer changeType;

    /**
     * 变更类型描述
     */
    private String changeTypeDesc;

    /**
     * 状态：0-待审核 1-已通过 2-已驳回 3-已执行
     */
    private Integer status;

    /**
     * 状态描述
     */
    private String statusDesc;

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
     * 新租客姓名（转租时）
     */
    private String newUserName;

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

    /**
     * 申请人ID
     */
    private Long applicantId;

    /**
     * 申请人姓名
     */
    private String applicantName;

    /**
     * 审批人ID
     */
    private Long approverId;

    /**
     * 审批人姓名
     */
    private String approverName;

    /**
     * 审批时间
     */
    private LocalDateTime approveTime;

    /**
     * 审批意见
     */
    private String approveRemark;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
