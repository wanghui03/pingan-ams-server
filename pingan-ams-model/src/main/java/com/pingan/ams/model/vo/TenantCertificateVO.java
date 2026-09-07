package com.pingan.ams.model.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 租户证件VO
 */
@Data
public class TenantCertificateVO {

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
     * 证件类型描述
     */
    private String certTypeDesc;

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
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
