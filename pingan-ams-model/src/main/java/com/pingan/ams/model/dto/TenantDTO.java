package com.pingan.ams.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 租户管理DTO
 */
@Data
public class TenantDTO {

    /**
     * 租户ID（更新时使用）
     */
    private Long id;

    /**
     * 租户名称
     */
    @NotBlank(message = "租户名称不能为空")
    private String name;

    /**
     * 租户类型：1-企业 2-个人
     */
    @NotNull(message = "租户类型不能为空")
    private Integer type;

    /**
     * 联系人
     */
    @NotBlank(message = "联系人不能为空")
    private String contactPerson;

    /**
     * 联系电话
     */
    @NotBlank(message = "联系电话不能为空")
    private String contactPhone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 地址
     */
    private String address;

    /**
     * 状态：0-禁用 1-启用
     */
    private Integer status;
}
