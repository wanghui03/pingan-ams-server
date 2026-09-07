package com.pingan.ams.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 证件 DTO
 */
@Data
public class CertificateDTO {

    /**
     * 所有者类型：1-租户 2-员工 3-租客
     */
    @NotNull(message = "所有者类型不能为空")
    private Integer ownerType;

    /**
     * 所有者 ID
     */
    @NotNull(message = "所有者 ID 不能为空")
    private Long ownerId;

    /**
     * 证件类型：1-营业执照 2-身份证 3-护照 4-其他
     */
    @NotNull(message = "证件类型不能为空")
    private Integer certType;

    /**
     * 证件编号
     */
    @NotBlank(message = "证件编号不能为空")
    private String certNo;

    /**
     * 证件名称
     */
    @NotBlank(message = "证件名称不能为空")
    private String certName;

    /**
     * 证件附件标识（文件 ID 或 URL）
     */
    private String certFile;
}
