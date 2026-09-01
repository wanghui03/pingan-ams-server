package com.pingan.ams.model.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 角色VO
 */
@Data
public class RoleVO {

    private Long id;

    /**
     * 租户ID
     */
    private Long tenantId;

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 角色编码
     */
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
     * 状态描述
     */
    private String statusDesc;

    /**
     * 排序
     */
    private Integer sortOrder;

    /**
     * 关联的权限ID列表
     */
    private List<Long> permissionIds;

    /**
     * 关联用户数
     */
    private Long userCount;

    private LocalDateTime createTime;
}
