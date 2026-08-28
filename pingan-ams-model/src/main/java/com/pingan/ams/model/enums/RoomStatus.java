package com.pingan.ams.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 房源状态枚举
 */
@Getter
@AllArgsConstructor
public enum RoomStatus {

    VACANT(0, "空置"),
    RESERVED(1, "已预订"),
    OCCUPIED(2, "已入住"),
    MAINTENANCE(3, "维修中");

    @EnumValue
    private final Integer code;
    private final String desc;
}
