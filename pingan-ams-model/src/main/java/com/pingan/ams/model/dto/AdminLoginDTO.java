package com.pingan.ams.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * PC端登录请求DTO
 */
@Data
public class AdminLoginDTO {

    /**
     * 登录账号
     */
    @NotBlank(message = "账号不能为空")
    private String username;

    /**
     * 登录密码
     */
    @NotBlank(message = "密码不能为空")
    private String password;
}
