package com.pingan.ams.admin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pingan.ams.common.annotation.Log;
import com.pingan.ams.common.result.Result;
import com.pingan.ams.common.utils.SecurityUtils;
import com.pingan.ams.model.dto.TenantUserDTO;
import com.pingan.ams.model.vo.TenantUserVO;
import com.pingan.ams.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 租客管理控制器
 */
@Tag(name = "租客管理", description = "租客信息查询和管理相关接口")
@RestController
@RequestMapping("/tenant-user")
@RequiredArgsConstructor
public class TenantUserController {

    private final UserService userService;

    @Operation(summary = "分页查询租客列表")
    @GetMapping("/list")
    public Result<Page<TenantUserVO>> listTenantUsers(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword) {
        Long tenantId = SecurityUtils.getCurrentTenantId();
        Page<TenantUserVO> pageResult = userService.listTenantUsers(tenantId, page, size, keyword);
        return Result.success(pageResult);
    }

    @Operation(summary = "获取租客详情")
    @GetMapping("/{userId}")
    public Result<TenantUserVO> getTenantUserDetail(@PathVariable Long userId) {
        Long tenantId = SecurityUtils.getCurrentTenantId();
        TenantUserVO detail = userService.getTenantUserDetail(tenantId, userId);
        return Result.success(detail);
    }

    @Log(module = "租客管理", operation = "状态变更", description = "启用/禁用租客")
    @Operation(summary = "启用/禁用租客")
    @PutMapping("/{userId}/status")
    public Result<Void> updateTenantUserStatus(
            @PathVariable Long userId,
            @RequestParam Integer status) {
        Long tenantId = SecurityUtils.getCurrentTenantId();
        userService.updateTenantUserStatus(tenantId, userId, status);
        return Result.success();
    }

    @Log(module = "租客管理", operation = "创建", description = "创建新租客")
    @Operation(summary = "创建租客")
    @PostMapping
    public Result<Long> createTenantUser(@Valid @RequestBody TenantUserDTO tenantUserDTO) {
        Long tenantId = SecurityUtils.getCurrentTenantId();
        Long userId = userService.createTenantUser(tenantId, tenantUserDTO);
        return Result.success(userId);
    }

    @Log(module = "租客管理", operation = "更新", description = "更新租客信息")
    @Operation(summary = "更新租客")
    @PutMapping("/{userId}")
    public Result<Void> updateTenantUser(@PathVariable Long userId, @Valid @RequestBody TenantUserDTO tenantUserDTO) {
        Long tenantId = SecurityUtils.getCurrentTenantId();
        userService.updateTenantUser(tenantId, userId, tenantUserDTO);
        return Result.success();
    }
}
