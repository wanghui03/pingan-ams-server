package com.pingan.ams.model.dto;

import com.pingan.ams.model.enums.ContractStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 合同DTO
 */
@Data
public class ContractDTO {

    @NotNull(message = "租客ID不能为空")
    private Long userId;

    @NotNull(message = "房间ID不能为空")
    private Long roomId;

    @NotNull(message = "开始日期不能为空")
    private LocalDate startDate;

    @NotNull(message = "结束日期不能为空")
    private LocalDate endDate;

    @NotNull(message = "月租金不能为空")
    @DecimalMin(value = "0", message = "月租金不能小于0")
    private BigDecimal monthlyRent;

    @DecimalMin(value = "0", message = "押金不能小于0")
    private BigDecimal deposit;

    /**
     * 支付方式：1-月付 2-季付 3-半年付 4-年付
     */
    @NotNull(message = "支付方式不能为空")
    private Integer paymentMethod;

    private String remark;
}
