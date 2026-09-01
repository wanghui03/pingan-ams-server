package com.pingan.ams.admin.controller;

import com.pingan.ams.common.annotation.Log;
import com.pingan.ams.common.annotation.RequirePermission;
import com.pingan.ams.common.result.Result;
import com.pingan.ams.common.utils.SecurityUtils;
import com.pingan.ams.service.ExportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * 数据导出控制器
 */
@Tag(name = "数据导出", description = "数据导出相关接口")
@RestController
@RequestMapping("/export")
@RequiredArgsConstructor
public class ExportController {

    private final ExportService exportService;

    @Log(module = "数据导出", operation = "导出合同", description = "导出合同数据")
    @RequirePermission("export:contract")
    @Operation(summary = "导出合同数据")
    @GetMapping("/contracts")
    public ResponseEntity<byte[]> exportContracts(
            @RequestParam(required = false) Integer status) {
        Long tenantId = SecurityUtils.getCurrentTenantId();
        byte[] data = exportService.exportContracts(tenantId, status);
        
        String filename = URLEncoder.encode("合同数据.xlsx", StandardCharsets.UTF_8);
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(data);
    }

    @Log(module = "数据导出", operation = "导出账单", description = "导出账单数据")
    @RequirePermission("export:bill")
    @Operation(summary = "导出账单数据")
    @GetMapping("/bills")
    public ResponseEntity<byte[]> exportBills(
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Integer billType) {
        Long tenantId = SecurityUtils.getCurrentTenantId();
        byte[] data = exportService.exportBills(tenantId, status, billType);
        
        String filename = URLEncoder.encode("账单数据.xlsx", StandardCharsets.UTF_8);
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(data);
    }

    @Log(module = "数据导出", operation = "导出工单", description = "导出工单数据")
    @RequirePermission("export:workorder")
    @Operation(summary = "导出工单数据")
    @GetMapping("/workorders")
    public ResponseEntity<byte[]> exportWorkOrders(
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Integer orderType) {
        Long tenantId = SecurityUtils.getCurrentTenantId();
        byte[] data = exportService.exportWorkOrders(tenantId, status, orderType);
        
        String filename = URLEncoder.encode("工单数据.xlsx", StandardCharsets.UTF_8);
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(data);
    }
}
