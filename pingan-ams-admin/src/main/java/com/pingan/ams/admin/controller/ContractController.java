package com.pingan.ams.admin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pingan.ams.common.annotation.Log;
import com.pingan.ams.common.annotation.RequirePermission;
import com.pingan.ams.common.result.Result;
import com.pingan.ams.common.utils.SecurityUtils;
import com.pingan.ams.model.dto.ContractDTO;
import com.pingan.ams.model.vo.ContractVO;
import com.pingan.ams.service.ContractService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 合同控制器
 */
@Tag(name = "合同管理", description = "合同相关接口")
@RestController
@RequestMapping("/contract")
@RequiredArgsConstructor
public class ContractController {

    private final ContractService contractService;

    @Log(module = "合同管理", operation = "创建", description = "创建新合同")
    @RequirePermission("contract:create")
    @Operation(summary = "创建合同")
    @PostMapping
    public Result<Long> createContract(@Valid @RequestBody ContractDTO contractDTO) {
        Long tenantId = SecurityUtils.getQueryTenantId();
        Long contractId = contractService.createContract(tenantId, contractDTO);
        return Result.success(contractId);
    }

    @Log(module = "合同管理", operation = "更新", description = "更新合同信息")
    @RequirePermission("contract:edit")
    @Operation(summary = "更新合同")
    @PutMapping("/{contractId}")
    public Result<Void> updateContract(@PathVariable Long contractId, @Valid @RequestBody ContractDTO contractDTO) {
        Long tenantId = SecurityUtils.getQueryTenantId();
        contractService.updateContract(tenantId, contractId, contractDTO);
        return Result.success();
    }

    @RequirePermission("contract:detail")
    @Operation(summary = "获取合同详情")
    @GetMapping("/{contractId}")
    public Result<ContractVO> getContractDetail(@PathVariable Long contractId) {
        Long tenantId = SecurityUtils.getQueryTenantId();
        ContractVO contractVO = contractService.getContractDetail(tenantId, contractId);
        return Result.success(contractVO);
    }

    @RequirePermission("contract:list")
    @Operation(summary = "分页查询合同列表")
    @GetMapping("/list")
    public Result<Page<ContractVO>> listContracts(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Long roomId) {
        Long tenantId = SecurityUtils.getQueryTenantId();
        Page<ContractVO> pageResult = contractService.listContracts(tenantId, page, size, status, userId, roomId);
        return Result.success(pageResult);
    }

    @Log(module = "合同管理", operation = "终止", description = "终止合同")
    @RequirePermission("contract:terminate")
    @Operation(summary = "终止合同")
    @PutMapping("/{contractId}/terminate")
    public Result<Void> terminateContract(@PathVariable Long contractId, @RequestParam(required = false) String reason) {
        Long tenantId = SecurityUtils.getQueryTenantId();
        contractService.terminateContract(tenantId, contractId, reason);
        return Result.success();
    }

    @Log(module = "合同管理", operation = "提交审核", description = "合同提交审核")
    @RequirePermission("contract:submit")
    @Operation(summary = "提交审核")
    @PutMapping("/{contractId}/submit")
    public Result<Void> submitContract(@PathVariable Long contractId) {
        Long tenantId = SecurityUtils.getQueryTenantId();
        contractService.submitContract(tenantId, contractId);
        return Result.success();
    }

    @Log(module = "合同管理", operation = "审核通过", description = "合同审核通过")
    @RequirePermission("contract:approve")
    @Operation(summary = "审核通过")
    @PutMapping("/{contractId}/approve")
    public Result<Void> approveContract(@PathVariable Long contractId) {
        Long tenantId = SecurityUtils.getQueryTenantId();
        contractService.approveContract(tenantId, contractId);
        return Result.success();
    }

    @Log(module = "合同管理", operation = "审核驳回", description = "合同审核驳回")
    @RequirePermission("contract:reject")
    @Operation(summary = "审核驳回")
    @PutMapping("/{contractId}/reject")
    public Result<Void> rejectContract(@PathVariable Long contractId, @RequestParam(required = false) String reason) {
        Long tenantId = SecurityUtils.getQueryTenantId();
        contractService.rejectContract(tenantId, contractId, reason);
        return Result.success();
    }
}
