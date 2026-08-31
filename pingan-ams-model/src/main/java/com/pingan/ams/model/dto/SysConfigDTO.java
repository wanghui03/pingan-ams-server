package com.pingan.ams.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 系统配置DTO
 */
@Data
public class SysConfigDTO {

    /**
     * 配置键
     */
    @NotBlank(message = "配置键不能为空")
    private String configKey;

    /**
     * 配置值
     */
    @NotBlank(message = "配置值不能为空")
    private String configValue;

    /**
     * 配置描述
     */
    private String description;
}
