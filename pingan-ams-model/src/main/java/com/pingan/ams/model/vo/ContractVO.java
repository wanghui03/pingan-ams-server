package com.pingan.ams.model.vo;

import com.pingan.ams.model.enums.ContractStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 合同VO
 */
@Data
public class ContractVO {

    private Long id;

    private String contractNo;

    private Long userId;

    private String tenantName;

    private Long roomId;

    private String roomNo;

    private String buildingName;

    private ContractStatus status;

    private String statusDesc;

    private LocalDate startDate;

    private LocalDate endDate;

    private Integer remainDays;

    private BigDecimal monthlyRent;

    private BigDecimal deposit;

    private Integer paymentMethod;

    private String paymentMethodDesc;

    private Integer signStatus;

    private LocalDateTime signTime;

    private String remark;

    private LocalDateTime createTime;
}
