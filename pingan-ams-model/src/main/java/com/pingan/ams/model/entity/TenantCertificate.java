package com.pingan.ams.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 租户证件表
 */
@Data
@TableName("ams_tenant_certificate")
public class TenantCertificate {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 租户ID
     */
    private Long tenantId;

    /**
     * 证件类型：1-营业执照 2-身份证
     */
    private Integer certType;

    /**
     * 企业名称（营业执照时填写）
     */
    private String companyName;

    /**
     * 统一社会信用码（营业执照时填写）
     */
    private String creditCode;

    /**
     * 身份证姓名（身份证时填写）
     */
    private String idCardName;

    /**
     * 身份证号码（身份证时填写）
     */
    private String idCardNo;

    /**
     * 证件图片URL
     */
    private String certImage;

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
