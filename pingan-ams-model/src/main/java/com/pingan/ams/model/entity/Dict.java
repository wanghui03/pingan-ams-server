package com.pingan.ams.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 字典表
 */
@Data
@TableName("ams_dict")
public class Dict {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 字典类型（如：room_status, contract_status）
     */
    private String dictType;

    /**
     * 字典编码（如：0, 1, 2）
     */
    private String dictCode;

    /**
     * 字典名称（如：空置, 已预订, 已入住）
     */
    private String dictName;

    /**
     * 排序
     */
    private Integer sortOrder;

    /**
     * 状态：0-禁用 1-启用
     */
    private Integer status;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
