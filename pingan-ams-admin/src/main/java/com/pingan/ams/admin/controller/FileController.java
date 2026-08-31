package com.pingan.ams.admin.controller;

import com.pingan.ams.common.result.Result;
import com.pingan.ams.model.vo.FileUploadVO;
import com.pingan.ams.service.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件上传 Controller
 */
@Tag(name = "文件上传")
@RestController
@RequestMapping("/file")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @Operation(summary = "上传文件")
    @PostMapping("/upload")
    public Result<FileUploadVO> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "directory", defaultValue = "common") String directory) {
        FileUploadVO result = fileService.uploadFile(file, directory);
        return Result.success(result);
    }

    @Operation(summary = "上传身份证照片")
    @PostMapping("/upload/idcard")
    public Result<FileUploadVO> uploadIdCard(@RequestParam("file") MultipartFile file) {
        FileUploadVO result = fileService.uploadFile(file, "idcard");
        return Result.success(result);
    }

    @Operation(summary = "上传工单图片")
    @PostMapping("/upload/workorder")
    public Result<FileUploadVO> uploadWorkOrderImage(@RequestParam("file") MultipartFile file) {
        FileUploadVO result = fileService.uploadFile(file, "workorder");
        return Result.success(result);
    }
}
