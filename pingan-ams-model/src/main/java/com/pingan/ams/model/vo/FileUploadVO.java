package com.pingan.ams.model.vo;

import lombok.Data;

/**
 * 文件上传结果VO
 */
@Data
public class FileUploadVO {

    /**
     * 文件URL
     */
    private String url;

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 文件大小（字节）
     */
    private Long fileSize;

    /**
     * 文件类型
     */
    private String fileType;
}
