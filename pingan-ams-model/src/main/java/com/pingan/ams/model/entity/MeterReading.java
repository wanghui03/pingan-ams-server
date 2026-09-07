package com.pingan.ams.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 抄表记录表
 */
@Data
@TableName("ams_meter_reading")
public class MeterReading {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 租户ID
     */
    private Long tenantId;

    /**
     * 房间ID
     */
    private Long roomId;

    /**
     * 抄表类型：1-水表 2-电表
     */
    private Integer meterType;

    /**
     * 上次读数
     */
    private BigDecimal previousReading;

    /**
     * 本次读数
     */
    private BigDecimal currentReading;

    /**
     * 用量
     */
    @TableField("meter_usage")
    private BigDecimal meterUsage;

    /**
     * 单价
     */
    private BigDecimal unitPrice;

    /**
     * 费用
     */
    private BigDecimal amount;

    /**
     * 抄表日期
     */
    private LocalDate readingDate;

    /**
     * 是否已生成账单：0-否 1-是
     */
    private Integer billGenerated;

    /**
     * 关联账单ID
     */
    private Long billId;

    /**
     * 操作人ID
     */
    private Long operatorId;

    /**
     * 操作人姓名
     */
    private String operatorName;

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
