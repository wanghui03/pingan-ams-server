package com.pingan.ams.admin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pingan.ams.common.annotation.RequirePermission;
import com.pingan.ams.common.result.Result;
import com.pingan.ams.common.utils.SecurityUtils;
import com.pingan.ams.model.dto.NotificationDTO;
import com.pingan.ams.model.vo.NotificationVO;
import com.pingan.ams.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 站内通知控制器
 */
@Tag(name = "站内通知管理", description = "站内通知相关接口")
@RestController
@RequestMapping("/notification")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @Operation(summary = "创建通知")
    @PostMapping
    @RequirePermission("notification:create")
    public Result<Long> createNotification(@RequestBody NotificationDTO dto) {
        Long tenantId = SecurityUtils.getQueryTenantId();
        Long notificationId = notificationService.createNotification(tenantId, dto);
        return Result.success(notificationId);
    }

    @Operation(summary = "标记通知为已读")
    @PutMapping("/{notificationId}/read")
    @RequirePermission("notification:read")
    public Result<Void> markAsRead(@PathVariable Long notificationId) {
        Long tenantId = SecurityUtils.getQueryTenantId();
        notificationService.markAsRead(tenantId, notificationId);
        return Result.success();
    }

    @Operation(summary = "标记所有通知为已读")
    @PutMapping("/read-all")
    @RequirePermission("notification:read")
    public Result<Void> markAllAsRead() {
        Long tenantId = SecurityUtils.getQueryTenantId();
        Long userId = SecurityUtils.getCurrentUserId();
        notificationService.markAllAsRead(tenantId, userId);
        return Result.success();
    }

    @Operation(summary = "删除通知")
    @DeleteMapping("/{notificationId}")
    @RequirePermission("notification:delete")
    public Result<Void> deleteNotification(@PathVariable Long notificationId) {
        Long tenantId = SecurityUtils.getQueryTenantId();
        notificationService.deleteNotification(tenantId, notificationId);
        return Result.success();
    }

    @Operation(summary = "分页查询通知列表")
    @GetMapping("/list")
    @RequirePermission("notification:view")
    public Result<Page<NotificationVO>> listNotifications(
            @RequestParam(required = false) Integer type,
            @RequestParam(required = false) Integer isRead,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Long tenantId = SecurityUtils.getQueryTenantId();
        Long userId = SecurityUtils.getCurrentUserId();
        Page<NotificationVO> result = notificationService.listNotifications(tenantId, userId, type, isRead, page, size);
        return Result.success(result);
    }

    @Operation(summary = "获取未读通知数量")
    @GetMapping("/unread-count")
    @RequirePermission("notification:view")
    public Result<Long> getUnreadCount() {
        Long userId = SecurityUtils.getCurrentUserId();
        Long count = notificationService.getUnreadCount(userId);
        return Result.success(count);
    }
}
