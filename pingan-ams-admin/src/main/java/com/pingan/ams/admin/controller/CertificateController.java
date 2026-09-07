package com.pingan.ams.admin.controller;

import com.pingan.ams.common.annotation.Log;
import com.pingan.ams.common.annotation.RequirePermission;
import com.pingan.ams.common.result.Result;
import com.pingan.ams.model.dto.CertificateDTO;
import com.pingan.ams.model.vo.CertificateVO;
import com.pingan.ams.service.CertificateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 证件管理 Controller
 */
@Tag(name = "证件管理")
@RestController
@RequestMapping("/certificate")
@RequiredArgsConstructor
public class CertificateController {

    private final CertificateService certificateService;

    @Operation(summary = "保存或更新证件信息")
    @PostMapping
    @RequirePermission("certificate:edit")
    @Log(module = "证件管理", operation = "保存证件", description = "保存或更新证件信息")
    public Result<Long> saveOrUpdateCertificate(@Valid @RequestBody CertificateDTO dto) {
        Long id = certificateService.saveOrUpdateCertificate(dto);
        return Result.success(id);
    }

    @Operation(summary = "获取证件信息")
    @GetMapping
    @RequirePermission("certificate:view")
    public Result<CertificateVO> getCertificate(
            @RequestParam Integer ownerType,
            @RequestParam Long ownerId) {
        CertificateVO certificate = certificateService.getCertificate(ownerType, ownerId);
        return Result.success(certificate);
    }

    @Operation(summary = "删除证件信息")
    @DeleteMapping
    @RequirePermission("certificate:delete")
    @Log(module = "证件管理", operation = "删除证件", description = "删除证件信息")
    public Result<Void> deleteCertificate(
            @RequestParam Integer ownerType,
            @RequestParam Long ownerId) {
        certificateService.deleteCertificate(ownerType, ownerId);
        return Result.success();
    }
}
