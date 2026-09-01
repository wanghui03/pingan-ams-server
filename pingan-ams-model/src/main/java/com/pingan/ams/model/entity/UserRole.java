package com.pingan.ams.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户角色关联表
 */
@Data
@TableName("ams_user_role")
public class UserRole {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 角色ID
     */
    private Long roleId;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
