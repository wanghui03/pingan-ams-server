package com.pingan.ams.admin.controller;

import com.pingan.ams.common.result.Result;
import com.pingan.ams.model.dto.SysConfigDTO;
import com.pingan.ams.model.vo.SysConfigVO;
import com.pingan.ams.service.SysConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 系统配置 Controller
 */
@Tag(name = "系统配置管理")
@RestController
@RequestMapping("/sys-config")
@RequiredArgsConstructor
public class SysConfigController {

    private final SysConfigService sysConfigService;

    @Operation(summary = "获取配置列表")
    @GetMapping("/list")
    public Result<List<SysConfigVO>> list() {
        List<SysConfigVO> list = sysConfigService.listConfigs(null);
        return Result.success(list);
    }

    @Operation(summary = "获取配置值")
    @GetMapping("/value")
    public Result<String> getValue(@RequestParam String key) {
        String value = sysConfigService.getConfigValue(key);
        return Result.success(value);
    }

    @Operation(summary = "保存配置")
    @PostMapping
    public Result<Void> save(@Valid @RequestBody SysConfigDTO dto) {
        sysConfigService.saveOrUpdateConfig(null, dto);
        return Result.success(null);
    }

    @Operation(summary = "删除配置")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        sysConfigService.deleteConfig(id);
        return Result.success(null);
    }
}
