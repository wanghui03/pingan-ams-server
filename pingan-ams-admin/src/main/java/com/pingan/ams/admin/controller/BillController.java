package com.pingan.ams.admin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pingan.ams.common.annotation.Log;
import com.pingan.ams.common.annotation.RequirePermission;
import com.pingan.ams.common.result.Result;
import com.pingan.ams.common.utils.SecurityUtils;
import com.pingan.ams.model.dto.BillDTO;
import com.pingan.ams.model.vo.BillVO;
import com.pingan.ams.service.BillService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

/**
 * 账单控制器
 */
@Tag(name = "账单管理", description = "账单相关接口")
@RestController
@RequestMapping("/bill")
@RequiredArgsConstructor
public class BillController {

    private final BillService billService;

    @Log(module = "账单管理", operation = "创建", description = "创建新账单")
    @RequirePermission("bill:create")
    @Operation(summary = "创建账单")
    @PostMapping
    public Result<Long> createBill(@Valid @RequestBody BillDTO billDTO) {
        Long tenantId = SecurityUtils.getQueryTenantId();
        Long billId = billService.createBill(tenantId, billDTO);
        return Result.success(billId);
    }

    @RequirePermission("bill:detail")
    @Operation(summary = "获取账单详情")
    @GetMapping("/{billId}")
    public Result<BillVO> getBillDetail(@PathVariable Long billId) {
        Long tenantId = SecurityUtils.getQueryTenantId();
        BillVO billVO = billService.getBillDetail(tenantId, billId);
        return Result.success(billVO);
    }

    @RequirePermission("bill:list")
    @Operation(summary = "分页查询账单列表")
    @GetMapping("/list")
    public Result<Page<BillVO>> listBills(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Integer billType,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Long roomId) {
        Long tenantId = SecurityUtils.getQueryTenantId();
        Page<BillVO> pageResult = billService.listBills(tenantId, page, size, status, billType, userId, roomId);
        return Result.success(pageResult);
    }

    @Log(module = "账单管理", operation = "确认收款", description = "标记账单已支付")
    @RequirePermission("bill:pay")
    @Operation(summary = "标记账单已支付")
    @PutMapping("/{billId}/pay")
    public Result<Void> markAsPaid(
            @PathVariable Long billId,
            @RequestParam BigDecimal paidAmount,
            @RequestParam(required = false) String transactionNo) {
        Long tenantId = SecurityUtils.getQueryTenantId();
        billService.markAsPaid(tenantId, billId, paidAmount, transactionNo);
        return Result.success();
    }

    @Log(module = "账单管理", operation = "取消", description = "取消账单")
    @RequirePermission("bill:cancel")
    @Operation(summary = "取消账单")
    @PutMapping("/{billId}/cancel")
    public Result<Void> cancelBill(@PathVariable Long billId, @RequestParam(required = false) String reason) {
        Long tenantId = SecurityUtils.getQueryTenantId();
        billService.cancelBill(tenantId, billId, reason);
        return Result.success();
    }

    @RequirePermission("bill:detail")
    @Operation(summary = "获取待支付总金额")
    @GetMapping("/unpaid-amount")
    public Result<BigDecimal> getUnpaidAmount() {
        Long tenantId = SecurityUtils.getQueryTenantId();
        BigDecimal amount = billService.getUnpaidAmount(tenantId);
        return Result.success(amount);
    }

    @Log(module = "账单管理", operation = "生成账单", description = "根据合同自动生成租金账单")
    @RequirePermission("bill:create")
    @Operation(summary = "根据合同自动生成租金账单")
    @PostMapping("/generate-rent-bills")
    public Result<Void> generateRentBills(@RequestParam Long contractId) {
        billService.generateRentBills(contractId);
        return Result.success();
    }

    @Log(module = "账单管理", operation = "发送提醒", description = "手动发送账单支付提醒")
    @RequirePermission("bill:pay")
    @Operation(summary = "手动发送账单提醒")
    @PostMapping("/{billId}/remind")
    public Result<Void> sendBillReminder(@PathVariable Long billId) {
        Long tenantId = SecurityUtils.getQueryTenantId();
        billService.sendBillReminder(tenantId, billId);
        return Result.success();
    }
}
