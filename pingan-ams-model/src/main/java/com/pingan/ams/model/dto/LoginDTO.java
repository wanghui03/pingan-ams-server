package com.pingan.ams.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 登录请求DTO
 */
@Data
public class LoginDTO {

    /**
     * 微信授权码
     */
    @NotBlank(message = "授权码不能为空")
    private String code;

    /**
     * 手机号（可选，用于绑定）
     */
    private String phone;

    /**
     * 租户ID（可选，用于多租户场景）
     */
    private Long tenantId;
}
