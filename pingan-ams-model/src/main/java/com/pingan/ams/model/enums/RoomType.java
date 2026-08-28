package com.pingan.ams.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 房源类型枚举
 */
@Getter
@AllArgsConstructor
public enum RoomType {

    COMMERCIAL(1, "商品房"),
    RESETTLEMENT(2, "拆迁房"),
    SELF_BUILT(3, "自建房"),
    APARTMENT(4, "公寓");

    @EnumValue
    private final Integer code;
    private final String desc;
}
