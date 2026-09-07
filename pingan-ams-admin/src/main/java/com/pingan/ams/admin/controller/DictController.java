package com.pingan.ams.admin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pingan.ams.common.annotation.Log;
import com.pingan.ams.common.annotation.RequirePermission;
import com.pingan.ams.common.result.Result;
import com.pingan.ams.model.dto.DictDTO;
import com.pingan.ams.model.vo.DictVO;
import com.pingan.ams.service.DictService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 字典管理 Controller
 */
@Tag(name = "字典管理")
@RestController
@RequestMapping("/dict")
@RequiredArgsConstructor
public class DictController {

    private final DictService dictService;

    @Operation(summary = "创建字典")
    @PostMapping
    @RequirePermission("dict:create")
    @Log(module = "字典管理", operation = "创建", description = "创建字典")
    public Result<Long> createDict(@Valid @RequestBody DictDTO dto) {
        return Result.success(dictService.createDict(dto));
    }

    @Operation(summary = "更新字典")
    @PutMapping("/{id}")
    @RequirePermission("dict:edit")
    @Log(module = "字典管理", operation = "更新", description = "更新字典")
    public Result<Void> updateDict(@PathVariable Long id, @Valid @RequestBody DictDTO dto) {
        dictService.updateDict(id, dto);
        return Result.success();
    }

    @Operation(summary = "删除字典")
    @DeleteMapping("/{id}")
    @RequirePermission("dict:delete")
    @Log(module = "字典管理", operation = "删除", description = "删除字典")
    public Result<Void> deleteDict(@PathVariable Long id) {
        dictService.deleteDict(id);
        return Result.success();
    }

    @Operation(summary = "获取字典详情")
    @GetMapping("/{id}")
    @RequirePermission("dict:view")
    public Result<DictVO> getDictDetail(@PathVariable Long id) {
        return Result.success(dictService.getDictDetail(id));
    }

    @Operation(summary = "分页查询字典")
    @GetMapping("/list")
    @RequirePermission("dict:view")
    public Result<Page<DictVO>> listDicts(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String dictType,
            @RequestParam(required = false) String keyword) {
        return Result.success(dictService.listDicts(page, size, dictType, keyword));
    }

    @Operation(summary = "根据字典类型获取字典列表")
    @GetMapping("/type/{dictType}")
    public Result<List<DictVO>> getDictsByType(@PathVariable String dictType) {
        return Result.success(dictService.getDictsByType(dictType));
    }

    @Operation(summary = "获取所有字典类型")
    @GetMapping("/types")
    public Result<List<String>> getAllDictTypes() {
        return Result.success(dictService.getAllDictTypes());
    }
}
