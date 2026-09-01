package com.pingan.ams.model.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 账单DTO
 */
@Data
public class BillDTO {

    @NotNull(message = "合同ID不能为空")
    private Long contractId;

    /**
     * 账单类型：1-租金 2-水电费 3-押金 4-其他
     */
    @NotNull(message = "账单类型不能为空")
    private Integer billType;

    @NotNull(message = "账单金额不能为空")
    @DecimalMin(value = "0.01", message = "账单金额必须大于0")
    private BigDecimal amount;

    private LocalDate startDate;

    private LocalDate endDate;

    private LocalDate dueDate;

    private LocalDate billDate;

    private String remark;
}
