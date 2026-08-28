package com.pingan.ams.admin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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

    @Operation(summary = "创建工单")
    @PostMapping
    public Result<Long> createWorkOrder(@Valid @RequestBody WorkOrderDTO workOrderDTO) {
        Long tenantId = SecurityUtils.getCurrentTenantId();
        Long userId = SecurityUtils.getCurrentUserId();
        Long workOrderId = workOrderService.createWorkOrder(tenantId, userId, workOrderDTO);
        return Result.success(workOrderId);
    }

    @Operation(summary = "获取工单详情")
    @GetMapping("/{workOrderId}")
    public Result<WorkOrderVO> getWorkOrderDetail(@PathVariable Long workOrderId) {
        Long tenantId = SecurityUtils.getCurrentTenantId();
        WorkOrderVO workOrderVO = workOrderService.getWorkOrderDetail(tenantId, workOrderId);
        return Result.success(workOrderVO);
    }

    @Operation(summary = "分页查询工单列表")
    @GetMapping("/list")
    public Result<Page<WorkOrderVO>> listWorkOrders(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Integer orderType,
            @RequestParam(required = false) Long userId) {
        Long tenantId = SecurityUtils.getCurrentTenantId();
        Page<WorkOrderVO> pageResult = workOrderService.listWorkOrders(tenantId, page, size, status, orderType, userId);
        return Result.success(pageResult);
    }

    @Operation(summary = "分配工单")
    @PutMapping("/{workOrderId}/assign")
    public Result<Void> assignWorkOrder(@PathVariable Long workOrderId, @RequestParam Long handlerId) {
        Long tenantId = SecurityUtils.getCurrentTenantId();
        workOrderService.assignWorkOrder(tenantId, workOrderId, handlerId);
        return Result.success();
    }

    @Operation(summary = "处理工单")
    @PutMapping("/{workOrderId}/handle")
    public Result<Void> handleWorkOrder(
            @PathVariable Long workOrderId,
            @RequestParam String handleResult,
            @RequestParam(required = false) String handleImages) {
        Long tenantId = SecurityUtils.getCurrentTenantId();
        workOrderService.handleWorkOrder(tenantId, workOrderId, handleResult, handleImages);
        return Result.success();
    }

    @Operation(summary = "评价工单")
    @PutMapping("/{workOrderId}/rate")
    public Result<Void> rateWorkOrder(
            @PathVariable Long workOrderId,
            @RequestParam Integer rating,
            @RequestParam(required = false) String ratingContent) {
        Long tenantId = SecurityUtils.getCurrentTenantId();
        Long userId = SecurityUtils.getCurrentUserId();
        workOrderService.rateWorkOrder(tenantId, userId, workOrderId, rating, ratingContent);
        return Result.success();
    }
}
