package com.pingan.ams.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 员工管理DTO
 */
@Data
public class StaffDTO {

    /**
     * 用户ID（更新时使用）
     */
    private Long id;

    /**
     * 登录账号
     */
    @NotBlank(message = "登录账号不能为空")
    private String username;

    /**
     * 登录密码（创建时必填）
     */
    private String password;

    /**
     * 真实姓名
     */
    @NotBlank(message = "真实姓名不能为空")
    private String realName;

    /**
     * 手机号
     */
    @NotBlank(message = "手机号不能为空")
    private String phone;

    /**
     * 用户类型：2-员工 3-管理员
     */
    @NotNull(message = "用户类型不能为空")
    private Integer userType;

    /**
     * 状态：0-禁用 1-启用
     */
    private Integer status;
}
