package com.pingan.ams.model.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 租客信息VO
 */
@Data
public class TenantUserVO {

    /**
     * 用户ID
     */
    private Long id;

    /**
     * 租户ID
     */
    private Long tenantId;

    /**
     * 登录账号
     */
    private String username;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 身份证号（脱敏）
     */
    private String idCard;

    /**
     * 实名认证状态：0-未认证 1-已认证
     */
    private Integer authStatus;

    /**
     * 实名认证状态描述
     */
    private String authStatusDesc;

    /**
     * 状态：0-禁用 1-启用
     */
    private Integer status;

    /**
     * 状态描述
     */
    private String statusDesc;

    /**
     * 最后登录时间
     */
    private LocalDateTime lastLoginTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 当前合同数
     */
    private Long contractCount;

    /**
     * 当前待付账单数
     */
    private Long unpaidBillCount;
}
