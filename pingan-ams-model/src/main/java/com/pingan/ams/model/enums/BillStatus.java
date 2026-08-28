package com.pingan.ams.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 账单状态枚举
 */
@Getter
@AllArgsConstructor
public enum BillStatus {

    UNPAID(0, "待支付"),
    PAID(1, "已支付"),
    OVERDUE(2, "已逾期"),
    CANCELLED(3, "已取消");

    @EnumValue
    private final Integer code;
    private final String desc;
}
