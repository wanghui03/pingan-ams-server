package com.pingan.ams.admin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pingan.ams.common.annotation.Log;
import com.pingan.ams.common.annotation.RequirePermission;
import com.pingan.ams.common.result.Result;
import com.pingan.ams.common.utils.SecurityUtils;
import com.pingan.ams.model.dto.WorkOrderDTO;
import com.pingan.ams.model.vo.WorkOrderVO;
import com.pingan.ams.service.WorkOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 工单控制器
 */
@Tag(name = "工单管理", description = "工单相关接口")
@RestController
@RequestMapping("/work-order")
@RequiredArgsConstructor
public class WorkOrderController {

    private final WorkOrderService workOrderService;

    @Log(module = "工单管理", operation = "创建", description = "创建新工单")
    @RequirePermission("workorder:create")
    @Operation(summary = "创建工单")
    @PostMapping
    public Result<Long> createWorkOrder(@Valid @RequestBody WorkOrderDTO workOrderDTO) {
        Long tenantId = SecurityUtils.getQueryTenantId();
        Long userId = SecurityUtils.getCurrentUserId();
        Long workOrderId = workOrderService.createWorkOrder(tenantId, userId, workOrderDTO);
        return Result.success(workOrderId);
    }

    @RequirePermission("workorder:detail")
    @Operation(summary = "获取工单详情")
    @GetMapping("/{workOrderId}")
    public Result<WorkOrderVO> getWorkOrderDetail(@PathVariable Long workOrderId) {
        Long tenantId = SecurityUtils.getQueryTenantId();
        WorkOrderVO workOrderVO = workOrderService.getWorkOrderDetail(tenantId, workOrderId);
        return Result.success(workOrderVO);
    }

    @RequirePermission("workorder:list")
    @Operation(summary = "分页查询工单列表")
    @GetMapping("/list")
    public Result<Page<WorkOrderVO>> listWorkOrders(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Integer orderType,
            @RequestParam(required = false) Long userId) {
        Long tenantId = SecurityUtils.getQueryTenantId();
        Page<WorkOrderVO> pageResult = workOrderService.listWorkOrders(tenantId, page, size, status, orderType, userId);
        return Result.success(pageResult);
    }

    @Log(module = "工单管理", operation = "分配", description = "分配工单处理人")
    @RequirePermission("workorder:assign")
    @Operation(summary = "分配工单")
    @PutMapping("/{workOrderId}/assign")
    public Result<Void> assignWorkOrder(@PathVariable Long workOrderId, @RequestParam Long handlerId) {
        Long tenantId = SecurityUtils.getQueryTenantId();
        workOrderService.assignWorkOrder(tenantId, workOrderId, handlerId);
        return Result.success();
    }

    @Log(module = "工单管理", operation = "处理", description = "处理工单")
    @RequirePermission("workorder:complete")
    @Operation(summary = "处理工单")
    @PutMapping("/{workOrderId}/handle")
    public Result<Void> handleWorkOrder(
            @PathVariable Long workOrderId,
            @RequestParam String handleResult,
            @RequestParam(required = false) String handleImages) {
        Long tenantId = SecurityUtils.getQueryTenantId();
        workOrderService.handleWorkOrder(tenantId, workOrderId, handleResult, handleImages);
        return Result.success();
    }

    @Log(module = "工单管理", operation = "评价", description = "评价工单")
    @RequirePermission("workorder:rate")
    @Operation(summary = "评价工单")
    @PutMapping("/{workOrderId}/rate")
    public Result<Void> rateWorkOrder(
            @PathVariable Long workOrderId,
            @RequestParam Integer rating,
            @RequestParam(required = false) String ratingContent) {
        Long tenantId = SecurityUtils.getQueryTenantId();
        Long userId = SecurityUtils.getCurrentUserId();
        workOrderService.rateWorkOrder(tenantId, userId, workOrderId, rating, ratingContent);
        return Result.success();
    }
}
