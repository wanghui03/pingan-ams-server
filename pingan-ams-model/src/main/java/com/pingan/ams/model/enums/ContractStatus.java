package com.pingan.ams.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 合同状态枚举
 */
@Getter
@AllArgsConstructor
public enum ContractStatus {

    DRAFT(0, "草稿"),
    PENDING(1, "待审核"),
    ACTIVE(2, "生效中"),
    EXPIRED(3, "已到期"),
    TERMINATED(4, "已终止");

    @EnumValue
    private final Integer code;
    private final String desc;
}
