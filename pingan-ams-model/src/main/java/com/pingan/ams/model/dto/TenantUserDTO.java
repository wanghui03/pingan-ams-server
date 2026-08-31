package com.pingan.ams.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 租客管理DTO
 */
@Data
public class TenantUserDTO {

    /**
     * 用户ID（更新时使用）
     */
    private Long id;

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
     * 身份证号
     */
    private String idCard;

    /**
     * 状态：0-禁用 1-启用
     */
    private Integer status;
}
