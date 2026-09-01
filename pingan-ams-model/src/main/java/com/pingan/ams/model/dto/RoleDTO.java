package com.pingan.ams.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

/**
 * 角色DTO
 */
@Data
public class RoleDTO {

    /**
     * 角色名称
     */
    @NotBlank(message = "角色名称不能为空")
    private String roleName;

    /**
     * 角色编码
     */
    @NotBlank(message = "角色编码不能为空")
    private String roleCode;

    /**
     * 角色描述
     */
    private String description;

    /**
     * 状态：0-禁用 1-启用
     */
    private Integer status;

    /**
     * 排序
     */
    private Integer sortOrder;

    /**
     * 关联的权限ID列表
     */
    private List<Long> permissionIds;
}
