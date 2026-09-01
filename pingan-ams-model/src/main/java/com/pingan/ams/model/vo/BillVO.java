package com.pingan.ams.model.vo;

import com.pingan.ams.model.enums.BillStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 账单VO
 */
@Data
public class BillVO {

    private Long id;

    private String billNo;

    private Long contractId;

    private String contractNo;

    private Long userId;

    private String tenantName;

    private Long roomId;

    private String roomNo;

    private Integer billType;

    private String billTypeDesc;

    private Integer status;

    private String statusDesc;

    private BigDecimal amount;

    private LocalDate startDate;

    private LocalDate endDate;

    private LocalDate billDate;

    private LocalDate dueDate;

    private Integer overdueDays;

    private BigDecimal paidAmount;

    private LocalDateTime paidTime;

    private String remark;

    private LocalDateTime createTime;
}
