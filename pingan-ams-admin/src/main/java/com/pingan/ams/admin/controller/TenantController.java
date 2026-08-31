package com.pingan.ams.admin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pingan.ams.common.annotation.Log;
import com.pingan.ams.common.result.Result;
import com.pingan.ams.common.utils.SecurityUtils;
import com.pingan.ams.model.dto.TenantDTO;
import com.pingan.ams.model.vo.TenantVO;
import com.pingan.ams.service.TenantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 租户管理控制器（平台管理员使用）
 */
@Tag(name = "租户管理", description = "平台管理员管理租户相关接口")
@RestController
@RequestMapping("/platform/tenant")
@RequiredArgsConstructor
public class TenantController {

    private final TenantService tenantService;

    @Log(module = "租户管理", operation = "创建", description = "创建新租户")
    @Operation(summary = "创建租户")
    @PostMapping
    public Result<Long> createTenant(@Valid @RequestBody TenantDTO tenantDTO) {
        Long tenantId = tenantService.createTenant(tenantDTO);
        return Result.success(tenantId);
    }

    @Log(module = "租户管理", operation = "更新", description = "更新租户信息")
    @Operation(summary = "更新租户")
    @PutMapping("/{tenantId}")
    public Result<Void> updateTenant(@PathVariable Long tenantId, @Valid @RequestBody TenantDTO tenantDTO) {
        tenantService.updateTenant(tenantId, tenantDTO);
        return Result.success();
    }

    @Log(module = "租户管理", operation = "删除", description = "删除租户")
    @Operation(summary = "删除租户")
    @DeleteMapping("/{tenantId}")
    public Result<Void> deleteTenant(@PathVariable Long tenantId) {
        tenantService.deleteTenant(tenantId);
        return Result.success();
    }

    @Operation(summary = "获取租户详情")
    @GetMapping("/{tenantId}")
    public Result<TenantVO> getTenantDetail(@PathVariable Long tenantId) {
        TenantVO tenantVO = tenantService.getTenantDetail(tenantId);
        return Result.success(tenantVO);
    }

    @Operation(summary = "分页查询租户列表")
    @GetMapping("/list")
    public Result<Page<TenantVO>> listTenants(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword) {
        Page<TenantVO> pageResult = tenantService.listTenants(page, size, keyword);
        return Result.success(pageResult);
    }

    @Operation(summary = "获取所有租户（下拉选择用）")
    @GetMapping("/all")
    public Result<List<TenantVO>> getAllTenants() {
        List<TenantVO> list = tenantService.getAllTenants();
        return Result.success(list);
    }

    @Log(module = "租户管理", operation = "状态变更", description = "启用/禁用租户")
    @Operation(summary = "启用/禁用租户")
    @PutMapping("/{tenantId}/status")
    public Result<Void> updateTenantStatus(
            @PathVariable Long tenantId,
            @RequestParam Integer status) {
        tenantService.updateTenantStatus(tenantId, status);
        return Result.success();
    }
}
