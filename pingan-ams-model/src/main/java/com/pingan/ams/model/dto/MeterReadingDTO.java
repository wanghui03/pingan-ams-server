package com.pingan.ams.model.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 抄表记录DTO
 */
@Data
public class MeterReadingDTO {

    /**
     * 房间ID
     */
    private Long roomId;

    /**
     * 抄表类型：1-水表 2-电表
     */
    private Integer meterType;

    /**
     * 本次读数
     */
    private BigDecimal currentReading;

    /**
     * 抄表日期
     */
    private LocalDate readingDate;

    /**
     * 是否生成账单：0-否 1-是
     */
    private Integer generateBill;

    /**
     * 备注
     */
    private String remark;
}
