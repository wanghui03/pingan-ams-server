package com.pingan.ams.admin.controller;

import com.pingan.ams.common.annotation.Log;
import com.pingan.ams.common.annotation.RequirePermission;
import com.pingan.ams.common.result.Result;
import com.pingan.ams.common.utils.SecurityUtils;
import com.pingan.ams.model.dto.TenantCertificateDTO;
import com.pingan.ams.model.vo.TenantCertificateVO;
import com.pingan.ams.service.TenantCertificateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 租户证件 Controller
 */
@Tag(name = "租户证件管理")
@RestController
@RequestMapping("/tenant/certificate")
@RequiredArgsConstructor
public class TenantCertificateController {

    private final TenantCertificateService tenantCertificateService;

    @Operation(summary = "保存或更新证件信息")
    @PostMapping
    @RequirePermission("tenant:certificate:edit")
    @Log(module = "租户证件管理", operation = "保存证件", description = "保存或更新租户证件信息")
    public Result<Long> saveOrUpdateCertificate(@Valid @RequestBody TenantCertificateDTO dto) {
        Long tenantId = SecurityUtils.getQueryTenantId();
        Long id = tenantCertificateService.saveOrUpdateCertificate(tenantId, dto);
        return Result.success(id);
    }

    @Operation(summary = "获取证件信息")
    @GetMapping
    @RequirePermission("tenant:certificate:view")
    public Result<TenantCertificateVO> getCertificate() {
        Long tenantId = SecurityUtils.getQueryTenantId();
        TenantCertificateVO certificate = tenantCertificateService.getCertificate(tenantId);
        return Result.success(certificate);
    }

    @Operation(summary = "删除证件信息")
    @DeleteMapping
    @RequirePermission("tenant:certificate:delete")
    @Log(module = "租户证件管理", operation = "删除证件", description = "删除租户证件信息")
    public Result<Void> deleteCertificate() {
        Long tenantId = SecurityUtils.getQueryTenantId();
        tenantCertificateService.deleteCertificate(tenantId);
        return Result.success();
    }
}
