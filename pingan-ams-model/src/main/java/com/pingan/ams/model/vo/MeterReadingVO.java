package com.pingan.ams.model.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 抄表记录VO
 */
@Data
public class MeterReadingVO {

    /**
     * 记录ID
     */
    private Long id;

    /**
     * 房间ID
     */
    private Long roomId;

    /**
     * 房间号
     */
    private String roomNo;

    /**
     * 楼栋名称
     */
    private String buildingName;

    /**
     * 抄表类型：1-水表 2-电表
     */
    private Integer meterType;

    /**
     * 抄表类型描述
     */
    private String meterTypeDesc;

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
    private LocalDateTime createTime;
}
