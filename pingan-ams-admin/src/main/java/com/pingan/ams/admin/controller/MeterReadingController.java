package com.pingan.ams.admin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pingan.ams.common.annotation.RequirePermission;
import com.pingan.ams.common.result.Result;
import com.pingan.ams.common.utils.SecurityUtils;
import com.pingan.ams.model.dto.MeterReadingDTO;
import com.pingan.ams.model.vo.MeterReadingVO;
import com.pingan.ams.service.MeterReadingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 抄表记录 Controller
 */
@Tag(name = "抄表管理")
@RestController
@RequestMapping("/meter-reading")
@RequiredArgsConstructor
public class MeterReadingController {

    private final MeterReadingService meterReadingService;

    @Operation(summary = "新增抄表记录")
    @PostMapping
    @RequirePermission("meter-reading:create")
    public Result<Long> addMeterReading(@RequestBody MeterReadingDTO dto) {
        Long tenantId = SecurityUtils.getQueryTenantId();
        Long id = meterReadingService.addMeterReading(tenantId, dto);
        return Result.success(id);
    }

    @Operation(summary = "获取抄表记录详情")
    @GetMapping("/{id}")
    @RequirePermission("meter-reading:detail")
    public Result<MeterReadingVO> getMeterReadingDetail(@PathVariable Long id) {
        Long tenantId = SecurityUtils.getQueryTenantId();
        MeterReadingVO detail = meterReadingService.getMeterReadingDetail(tenantId, id);
        return Result.success(detail);
    }

    @Operation(summary = "分页查询抄表记录")
    @GetMapping("/list")
    @RequirePermission("meter-reading:view")
    public Result<Page<MeterReadingVO>> listMeterReadings(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long roomId,
            @RequestParam(required = false) Integer meterType) {
        Long tenantId = SecurityUtils.getQueryTenantId();
        Page<MeterReadingVO> result = meterReadingService.listMeterReadings(tenantId, page, size, roomId, meterType);
        return Result.success(result);
    }

    @Operation(summary = "为抄表记录生成账单")
    @PostMapping("/{id}/generate-bill")
    @RequirePermission("meter-reading:generate-bill")
    public Result<Long> generateBill(@PathVariable Long id) {
        Long tenantId = SecurityUtils.getQueryTenantId();
        Long billId = meterReadingService.generateBill(tenantId, id);
        return Result.success(billId);
    }
}
