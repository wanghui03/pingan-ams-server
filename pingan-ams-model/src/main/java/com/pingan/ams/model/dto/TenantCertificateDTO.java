package com.pingan.ams.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 租户证件DTO
 */
@Data
public class TenantCertificateDTO {

    /**
     * 证件类型：1-营业执照 2-身份证
     */
    @NotNull(message = "证件类型不能为空")
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
}
