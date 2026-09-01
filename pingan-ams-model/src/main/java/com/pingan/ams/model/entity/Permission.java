package com.pingan.ams.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 权限表（菜单+按钮）
 */
@Data
@TableName("ams_permission")
public class Permission {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 父权限ID（0表示顶级）
     */
    private Long parentId;

    /**
     * 权限名称
     */
    private String name;

    /**
     * 权限编码
     */
    private String code;

    /**
     * 类型：1-菜单 2-按钮 3-接口
     */
    private Integer type;

    /**
     * 路由路径（菜单类型）
     */
    private String path;

    /**
     * 组件路径（菜单类型）
     */
    private String component;

    /**
     * 图标（菜单类型）
     */
    private String icon;

    /**
     * 排序
     */
    private Integer sortOrder;

    /**
     * 是否可见：0-隐藏 1-显示
     */
    private Integer visible;

    /**
     * 状态：0-禁用 1-启用
     */
    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;

    /**
     * 子权限列表（非数据库字段，用于树形结构）
     */
    @TableField(exist = false)
    private java.util.List<Permission> children;
}
