package com.pingan.ams.admin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pingan.ams.common.annotation.Log;
import com.pingan.ams.common.result.Result;
import com.pingan.ams.common.utils.SecurityUtils;
import com.pingan.ams.model.dto.RoleDTO;
import com.pingan.ams.model.vo.RoleVO;
import com.pingan.ams.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 角色管理 Controller
 */
@Tag(name = "角色管理")
@RestController
@RequestMapping("/role")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @Operation(summary = "创建角色")
    @Log(module = "角色管理", operation = "创建", description = "创建新角色")
    @PostMapping
    public Result<Long> createRole(@Valid @RequestBody RoleDTO roleDTO) {
        return Result.success(roleService.createRole(roleDTO));
    }

    @Operation(summary = "更新角色")
    @Log(module = "角色管理", operation = "更新", description = "更新角色信息")
    @PutMapping("/{roleId}")
    public Result<Void> updateRole(@PathVariable Long roleId, @Valid @RequestBody RoleDTO roleDTO) {
        roleService.updateRole(roleId, roleDTO);
        return Result.success();
    }

    @Operation(summary = "删除角色")
    @Log(module = "角色管理", operation = "删除", description = "删除角色")
    @DeleteMapping("/{roleId}")
    public Result<Void> deleteRole(@PathVariable Long roleId) {
        roleService.deleteRole(roleId);
        return Result.success();
    }

    @Operation(summary = "获取角色详情")
    @GetMapping("/{roleId}")
    public Result<RoleVO> getRoleDetail(@PathVariable Long roleId) {
        return Result.success(roleService.getRoleDetail(roleId));
    }

    @Operation(summary = "分页查询角色")
    @GetMapping("/list")
    public Result<Page<RoleVO>> listRoles(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword) {
        return Result.success(roleService.listRoles(page, size, keyword));
    }

    @Operation(summary = "获取所有角色（下拉选择）")
    @GetMapping("/all")
    public Result<List<RoleVO>> getAllRoles() {
        return Result.success(roleService.getAllRoles());
    }

    @Operation(summary = "分配角色权限")
    @Log(module = "角色管理", operation = "分配权限", description = "为角色分配权限")
    @PostMapping("/{roleId}/permissions")
    public Result<Void> assignPermissions(
            @PathVariable Long roleId,
            @RequestBody List<Long> permissionIds) {
        roleService.assignPermissions(roleId, permissionIds);
        return Result.success();
    }

    @Operation(summary = "获取用户的角色列表")
    @GetMapping("/user/{userId}")
    public Result<List<RoleVO>> getUserRoles(@PathVariable Long userId) {
        return Result.success(roleService.getUserRoles(userId));
    }

    @Operation(summary = "为用户分配角色")
    @Log(module = "用户管理", operation = "分配角色", description = "为用户分配角色")
    @PostMapping("/user/{userId}/assign")
    public Result<Void> assignUserRoles(
            @PathVariable Long userId,
            @RequestBody List<Long> roleIds) {
        roleService.assignUserRoles(userId, roleIds);
        return Result.success();
    }

    @Operation(summary = "获取当前用户的权限编码列表")
    @GetMapping("/my/permissions")
    public Result<List<String>> getMyPermissions() {
        Long userId = SecurityUtils.getCurrentUserId();
        return Result.success(roleService.getUserPermissionCodes(userId));
    }
}
