package com.pingan.ams.admin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pingan.ams.common.annotation.RequirePermission;
import com.pingan.ams.common.result.Result;
import com.pingan.ams.common.utils.SecurityUtils;
import com.pingan.ams.model.dto.ContractChangeDTO;
import com.pingan.ams.model.vo.ContractChangeVO;
import com.pingan.ams.service.ContractChangeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 合同变更控制器
 */
@Tag(name = "合同变更管理", description = "合同变更申请、审批、执行相关接口")
@RestController
@RequestMapping("/contract-change")
public class ContractChangeController {

    @Autowired
    private ContractChangeService contractChangeService;

    @Operation(summary = "创建合同变更申请")
    @PostMapping
    @RequirePermission("contract:change")
    public Result<Long> createChange(@RequestBody ContractChangeDTO dto) {
        Long tenantId = SecurityUtils.getQueryTenantId();
        Long userId = SecurityUtils.getCurrentUserId();
        Long changeId = contractChangeService.createChange(tenantId, userId, dto);
        return Result.success(changeId);
    }

    @Operation(summary = "审批合同变更申请")
    @PutMapping("/{changeId}/approve")
    @RequirePermission("contract:change:approve")
    public Result<Void> approveChange(
            @PathVariable Long changeId,
            @RequestParam Boolean approved,
            @RequestParam(required = false) String remark) {
        Long tenantId = SecurityUtils.getQueryTenantId();
        Long userId = SecurityUtils.getCurrentUserId();
        contractChangeService.approveChange(tenantId, userId, changeId, approved, remark);
        return Result.success();
    }

    @Operation(summary = "执行合同变更")
    @PutMapping("/{changeId}/execute")
    @RequirePermission("contract:change:execute")
    public Result<Void> executeChange(@PathVariable Long changeId) {
        Long tenantId = SecurityUtils.getQueryTenantId();
        Long userId = SecurityUtils.getCurrentUserId();
        contractChangeService.executeChange(tenantId, userId, changeId);
        return Result.success();
    }

    @Operation(summary = "获取变更申请详情")
    @GetMapping("/{changeId}")
    @RequirePermission("contract:change:view")
    public Result<ContractChangeVO> getChangeDetail(@PathVariable Long changeId) {
        Long tenantId = SecurityUtils.getQueryTenantId();
        ContractChangeVO vo = contractChangeService.getChangeDetail(tenantId, changeId);
        return Result.success(vo);
    }

    @Operation(summary = "分页查询变更申请列表")
    @GetMapping("/list")
    @RequirePermission("contract:change:view")
    public Result<Page<ContractChangeVO>> listChanges(
            @RequestParam(required = false) Long contractId,
            @RequestParam(required = false) Integer changeType,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Long tenantId = SecurityUtils.getQueryTenantId();
        Page<ContractChangeVO> result = contractChangeService.listChanges(tenantId, contractId, changeType, status, page, size);
        return Result.success(result);
    }

    @Operation(summary = "根据合同ID查询变更申请列表")
    @GetMapping("/contract/{contractId}")
    @RequirePermission("contract:change:view")
    public Result<List<ContractChangeVO>> listChangesByContractId(@PathVariable Long contractId) {
        Long tenantId = SecurityUtils.getQueryTenantId();
        List<ContractChangeVO> list = contractChangeService.listChangesByContractId(tenantId, contractId);
        return Result.success(list);
    }
}
