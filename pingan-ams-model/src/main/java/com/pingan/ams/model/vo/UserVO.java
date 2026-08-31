package com.pingan.ams.model.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户信息VO
 */
@Data
public class UserVO {

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
     * 头像
     */
    private String avatar;

    /**
     * 用户类型：0-超级管理员 1-租客 2-员工 3-管理员
     */
    private Integer userType;

    /**
     * 用户类型描述
     */
    private String userTypeDesc;

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
}
