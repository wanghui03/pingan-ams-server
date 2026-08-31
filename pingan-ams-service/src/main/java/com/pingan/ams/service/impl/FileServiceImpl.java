package com.pingan.ams.service.impl;

import com.pingan.ams.common.exception.BusinessException;
import com.pingan.ams.model.vo.FileUploadVO;
import com.pingan.ams.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * 文件服务实现 - 本地存储
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    @Value("${pingan.ams.upload.path:./uploads}")
    private String uploadPath;

    @Value("${pingan.ams.upload.url-prefix:http://localhost:8088/api/files}")
    private String urlPrefix;

    @Value("${pingan.ams.upload.max-size:10485760}")
    private Long maxSize;

    @Override
    public FileUploadVO uploadFile(MultipartFile file, String directory) {
        if (file.isEmpty()) {
            throw new BusinessException("文件不能为空");
        }

        if (file.getSize() > maxSize) {
            throw new BusinessException("文件大小不能超过 " + (maxSize / 1024 / 1024) + "MB");
        }

        // 生成文件路径
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        
        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String fileName = UUID.randomUUID().toString().replace("-", "") + extension;
        String relativePath = directory + "/" + datePath + "/" + fileName;
        
        // 创建目录
        File destDir = new File(uploadPath + "/" + directory + "/" + datePath);
        if (!destDir.exists()) {
            destDir.mkdirs();
        }

        // 保存文件
        File destFile = new File(uploadPath + "/" + relativePath);
        try {
            file.transferTo(destFile);
        } catch (IOException e) {
            log.error("文件上传失败", e);
            throw new BusinessException("文件上传失败");
        }

        // 返回结果
        FileUploadVO result = new FileUploadVO();
        result.setUrl(getFileUrl(relativePath));
        result.setFileName(originalFilename);
        result.setFileSize(file.getSize());
        result.setFileType(file.getContentType());

        return result;
    }

    @Override
    public void deleteFile(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            return;
        }

        // 从URL中提取相对路径
        String relativePath = fileUrl.replace(urlPrefix + "/", "");
        File file = new File(uploadPath + "/" + relativePath);
        
        if (file.exists()) {
            file.delete();
        }
    }

    @Override
    public String getFileUrl(String filePath) {
        return urlPrefix + "/" + filePath;
    }
}
