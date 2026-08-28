package com.pingan.ams.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.pingan.ams.model.enums.BillStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 账单表
 */
@Data
@TableName("ams_bill")
public class Bill {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 租户ID
     */
    private Long tenantId;

    /**
     * 账单编号
     */
    private String billNo;

    /**
     * 合同ID
     */
    private Long contractId;

    /**
     * 用户ID（租客）
     */
    private Long userId;

    /**
     * 房间ID
     */
    private Long roomId;

    /**
     * 账单类型：1-租金 2-水电费 3-押金 4-其他
     */
    private Integer billType;

    /**
     * 账单状态
     */
    @EnumValue
    private BillStatus status;

    /**
     * 账单金额
     */
    private BigDecimal amount;

    /**
     * 计费开始日期
     */
    private LocalDate startDate;

    /**
     * 计费结束日期
     */
    private LocalDate endDate;

    /**
     * 应付日期
     */
    private LocalDate dueDate;

    /**
     * 实付金额
     */
    private BigDecimal paidAmount;

    /**
     * 支付时间
     */
    private LocalDateTime paidTime;

    /**
     * 支付流水号
     */
    private String transactionNo;

    /**
     * 备注
     */
    private String remark;

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
