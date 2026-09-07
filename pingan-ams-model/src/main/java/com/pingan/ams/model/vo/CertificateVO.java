package com.pingan.ams.model.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 证件 VO
 */
@Data
public class CertificateVO {

    private Long id;

    /**
     * 所有者类型：1-租户 2-员工 3-租客
     */
    private Integer ownerType;

    /**
     * 所有者类型描述
     */
    private String ownerTypeDesc;

    /**
     * 所有者 ID
     */
    private Long ownerId;

    /**
     * 证件类型：1-营业执照 2-身份证 3-护照 4-其他
     */
    private Integer certType;

    /**
     * 证件类型描述
     */
    private String certTypeDesc;

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
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
