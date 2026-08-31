package com.pingan.ams.admin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pingan.ams.common.annotation.Log;
import com.pingan.ams.common.result.Result;
import com.pingan.ams.common.utils.SecurityUtils;
import com.pingan.ams.model.dto.ChangePasswordDTO;
import com.pingan.ams.model.dto.StaffDTO;
import com.pingan.ams.model.vo.UserVO;
import com.pingan.ams.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 员工管理控制器
 */
@Tag(name = "员工管理", description = "租户管理员管理员工相关接口")
@RestController
@RequestMapping("/staff")
@RequiredArgsConstructor
public class StaffController {

    private final UserService userService;

    @Log(module = "员工管理", operation = "修改密码", description = "修改登录密码")
    @Operation(summary = "修改密码")
    @PostMapping("/change-password")
    public Result<Void> changePassword(@Valid @RequestBody ChangePasswordDTO dto) {
        Long userId = SecurityUtils.getCurrentUserId();
        userService.changePassword(userId, dto.getOldPassword(), dto.getNewPassword());
        return Result.success();
    }

    @Operation(summary = "分页查询员工列表")
    @GetMapping("/list")
    public Result<Page<UserVO>> listStaff(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword) {
        Long tenantId = SecurityUtils.getCurrentTenantId();
        Page<UserVO> pageResult = userService.listStaff(tenantId, page, size, keyword);
        return Result.success(pageResult);
    }

    @Log(module = "员工管理", operation = "创建", description = "创建新员工")
    @Operation(summary = "创建员工")
    @PostMapping
    public Result<Long> createStaff(@Valid @RequestBody StaffDTO staffDTO) {
        Long tenantId = SecurityUtils.getCurrentTenantId();
        Long staffId = userService.createStaff(tenantId, staffDTO);
        return Result.success(staffId);
    }

    @Log(module = "员工管理", operation = "更新", description = "更新员工信息")
    @Operation(summary = "更新员工")
    @PutMapping("/{staffId}")
    public Result<Void> updateStaff(@PathVariable Long staffId, @Valid @RequestBody StaffDTO staffDTO) {
        Long tenantId = SecurityUtils.getCurrentTenantId();
        userService.updateStaff(tenantId, staffId, staffDTO);
        return Result.success();
    }

    @Log(module = "员工管理", operation = "删除", description = "删除员工")
    @Operation(summary = "删除员工")
    @DeleteMapping("/{staffId}")
    public Result<Void> deleteStaff(@PathVariable Long staffId) {
        Long tenantId = SecurityUtils.getCurrentTenantId();
        userService.deleteStaff(tenantId, staffId);
        return Result.success();
    }

    @Log(module = "员工管理", operation = "状态变更", description = "启用/禁用员工")
    @Operation(summary = "启用/禁用员工")
    @PutMapping("/{staffId}/status")
    public Result<Void> updateStaffStatus(
            @PathVariable Long staffId,
            @RequestParam Integer status) {
        Long tenantId = SecurityUtils.getCurrentTenantId();
        userService.updateStaffStatus(tenantId, staffId, status);
        return Result.success();
    }
}
