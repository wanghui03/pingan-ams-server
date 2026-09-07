package com.pingan.ams.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 证件表（通用，支持租户、员工、租客）
 */
@Data
@TableName("ams_certificate")
public class Certificate {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 所有者类型：1-租户 2-员工 3-租客
     */
    private Integer ownerType;

    /**
     * 所有者 ID（租户 ID/员工 ID/租客 ID）
     */
    private Long ownerId;

    /**
     * 证件类型：1-营业执照 2-身份证 3-护照 4-其他
     */
    private Integer certType;

    /**
     * 证件编号
     */
    private String certNo;

    /**
     * 证件名称
     */
    private String certName;

    /**
     * 证件附件标识（文件 ID 或 URL）
     */
    private String certFile;

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

    /**
     * 删除标记：0-未删除 1-已删除
     */
    @TableLogic
    private Integer deleted;
}
