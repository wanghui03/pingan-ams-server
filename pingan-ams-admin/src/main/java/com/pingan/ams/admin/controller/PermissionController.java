package com.pingan.ams.admin.controller;

import com.pingan.ams.common.annotation.Log;
import com.pingan.ams.common.result.Result;
import com.pingan.ams.model.entity.Permission;
import com.pingan.ams.mapper.PermissionMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 权限管理 Controller
 */
@Tag(name = "权限管理")
@RestController
@RequestMapping("/permission")
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionMapper permissionMapper;

    @Operation(summary = "获取所有权限列表")
    @GetMapping("/list")
    public Result<List<Permission>> listPermissions() {
        return Result.success(permissionMapper.selectList(null));
    }

    @Operation(summary = "获取权限树形结构")
    @GetMapping("/tree")
    public Result<List<Permission>> getPermissionTree() {
        List<Permission> allPermissions = permissionMapper.selectList(null);
        return Result.success(buildTree(allPermissions, 0L));
    }

    @Operation(summary = "创建权限")
    @Log(module = "权限管理", operation = "创建", description = "创建新权限")
    @PostMapping
    public Result<Void> createPermission(@RequestBody Permission permission) {
        permissionMapper.insert(permission);
        return Result.success();
    }

    @Operation(summary = "更新权限")
    @Log(module = "权限管理", operation = "更新", description = "更新权限信息")
    @PutMapping("/{permissionId}")
    public Result<Void> updatePermission(@PathVariable Long permissionId, @RequestBody Permission permission) {
        permission.setId(permissionId);
        permissionMapper.updateById(permission);
        return Result.success();
    }

    @Operation(summary = "删除权限")
    @Log(module = "权限管理", operation = "删除", description = "删除权限")
    @DeleteMapping("/{permissionId}")
    public Result<Void> deletePermission(@PathVariable Long permissionId) {
        permissionMapper.deleteById(permissionId);
        return Result.success();
    }

    /**
     * 构建权限树
     */
    private List<Permission> buildTree(List<Permission> allPermissions, Long parentId) {
        return allPermissions.stream()
                .filter(p -> p.getParentId().equals(parentId))
                .peek(p -> p.setChildren(buildTree(allPermissions, p.getId())))
                .toList();
    }
}
