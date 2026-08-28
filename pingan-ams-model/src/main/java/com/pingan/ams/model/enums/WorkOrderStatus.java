package com.pingan.ams.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 工单状态枚举
 */
@Getter
@AllArgsConstructor
public enum WorkOrderStatus {

    PENDING(0, "待处理"),
    PROCESSING(1, "处理中"),
    COMPLETED(2, "已完成"),
    CLOSED(3, "已关闭");

    @EnumValue
    private final Integer code;
    private final String desc;
}
