package com.pingan.ams.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 合同变更申请表
 */
@Data
@TableName("ams_contract_change")
public class ContractChange {

    /**
     * 变更ID
     */
    @TableId(type = IdType.AUTO)
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
     * 变更单号
     */
    private String changeNo;

    /**
     * 变更类型：1-续约 2-转租 3-提前退租
     */
    private Integer changeType;

    /**
     * 状态：0-待审核 1-已通过 2-已驳回 3-已执行
     */
    private Integer status;

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

    /**
     * 申请人ID
     */
    private Long applicantId;

    /**
     * 审批人ID
     */
    private Long approverId;

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
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 删除标记：0-未删除 1-已删除
     */
    @TableLogic
    private Integer deleted;
}
