package com.pingan.ams.admin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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

    @Operation(summary = "创建合同")
    @PostMapping
    public Result<Long> createContract(@Valid @RequestBody ContractDTO contractDTO) {
        Long tenantId = SecurityUtils.getCurrentTenantId();
        Long contractId = contractService.createContract(tenantId, contractDTO);
        return Result.success(contractId);
    }

    @Operation(summary = "更新合同")
    @PutMapping("/{contractId}")
    public Result<Void> updateContract(@PathVariable Long contractId, @Valid @RequestBody ContractDTO contractDTO) {
        Long tenantId = SecurityUtils.getCurrentTenantId();
        contractService.updateContract(tenantId, contractId, contractDTO);
        return Result.success();
    }

    @Operation(summary = "获取合同详情")
    @GetMapping("/{contractId}")
    public Result<ContractVO> getContractDetail(@PathVariable Long contractId) {
        Long tenantId = SecurityUtils.getCurrentTenantId();
        ContractVO contractVO = contractService.getContractDetail(tenantId, contractId);
        return Result.success(contractVO);
    }

    @Operation(summary = "分页查询合同列表")
    @GetMapping("/list")
    public Result<Page<ContractVO>> listContracts(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Long roomId) {
        Long tenantId = SecurityUtils.getCurrentTenantId();
        Page<ContractVO> pageResult = contractService.listContracts(tenantId, page, size, status, userId, roomId);
        return Result.success(pageResult);
    }

    @Operation(summary = "终止合同")
    @PutMapping("/{contractId}/terminate")
    public Result<Void> terminateContract(@PathVariable Long contractId, @RequestParam(required = false) String reason) {
        Long tenantId = SecurityUtils.getCurrentTenantId();
        contractService.terminateContract(tenantId, contractId, reason);
        return Result.success();
    }
}
