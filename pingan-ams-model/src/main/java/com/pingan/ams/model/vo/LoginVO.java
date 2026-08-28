package com.pingan.ams.model.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 登录返回VO
 */
@Data
public class LoginVO {

    /**
     * Token
     */
    private String token;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 租户ID
     */
    private Long tenantId;

    /**
     * 用户类型：1-租客 2-员工 3-管理员
     */
    private Integer userType;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 是否新用户（首次登录）
     */
    private Boolean isNewUser;

    /**
     * 过期时间
     */
    private LocalDateTime expireTime;
}
