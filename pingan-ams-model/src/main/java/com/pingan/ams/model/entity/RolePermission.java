package com.pingan.ams.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 角色权限关联表
 */
@Data
@TableName("ams_role_permission")
public class RolePermission {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 角色ID
     */
    private Long roleId;

    /**
     * 权限ID
     */
    private Long permissionId;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
