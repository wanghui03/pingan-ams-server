package com.pingan.ams.service;

import com.pingan.ams.model.vo.FileUploadVO;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件服务
 */
public interface FileService {

    /**
     * 上传文件
     */
    FileUploadVO uploadFile(MultipartFile file, String directory);

    /**
     * 删除文件
     */
    void deleteFile(String fileUrl);

    /**
     * 获取文件完整URL
     */
    String getFileUrl(String filePath);
}
