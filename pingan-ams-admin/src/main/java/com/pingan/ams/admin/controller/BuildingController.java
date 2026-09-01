package com.pingan.ams.admin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pingan.ams.common.annotation.Log;
import com.pingan.ams.common.annotation.RequirePermission;
import com.pingan.ams.common.result.Result;
import com.pingan.ams.common.utils.SecurityUtils;
import com.pingan.ams.model.dto.BuildingDTO;
import com.pingan.ams.model.vo.BuildingVO;
import com.pingan.ams.service.BuildingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 楼栋控制器
 */
@Tag(name = "楼栋管理", description = "楼栋相关接口")
@RestController
@RequestMapping("/building")
@RequiredArgsConstructor
public class BuildingController {

    private final BuildingService buildingService;

    @Log(module = "楼栋管理", operation = "创建", description = "创建新楼栋")
    @RequirePermission("building:create")
    @Operation(summary = "创建楼栋")
    @PostMapping
    public Result<Long> createBuilding(@Valid @RequestBody BuildingDTO buildingDTO) {
        Long tenantId = SecurityUtils.getCurrentTenantId();
        Long buildingId = buildingService.createBuilding(tenantId, buildingDTO);
        return Result.success(buildingId);
    }

    @Log(module = "楼栋管理", operation = "更新", description = "更新楼栋信息")
    @RequirePermission("building:edit")
    @Operation(summary = "更新楼栋")
    @PutMapping("/{buildingId}")
    public Result<Void> updateBuilding(@PathVariable Long buildingId, @Valid @RequestBody BuildingDTO buildingDTO) {
        Long tenantId = SecurityUtils.getQueryTenantId();
        buildingService.updateBuilding(tenantId, buildingId, buildingDTO);
        return Result.success();
    }

    @RequirePermission("building:detail")
    @Operation(summary = "获取楼栋详情")
    @GetMapping("/{buildingId}")
    public Result<BuildingVO> getBuildingDetail(@PathVariable Long buildingId) {
        Long tenantId = SecurityUtils.getQueryTenantId();
        BuildingVO buildingVO = buildingService.getBuildingDetail(tenantId, buildingId);
        return Result.success(buildingVO);
    }

    @RequirePermission("building:list")
    @Operation(summary = "分页查询楼栋列表")
    @GetMapping("/list")
    public Result<Page<BuildingVO>> listBuildings(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Long tenantId = SecurityUtils.getQueryTenantId();
        Page<BuildingVO> pageResult = buildingService.listBuildings(tenantId, page, size);
        return Result.success(pageResult);
    }

    @RequirePermission("building:list")
    @Operation(summary = "获取所有楼栋（下拉选择用）")
    @GetMapping("/all")
    public Result<List<BuildingVO>> getAllBuildings() {
        Long tenantId = SecurityUtils.getQueryTenantId();
        List<BuildingVO> list = buildingService.getAllBuildings(tenantId);
        return Result.success(list);
    }

    @Log(module = "楼栋管理", operation = "删除", description = "删除楼栋")
    @RequirePermission("building:delete")
    @Operation(summary = "删除楼栋")
    @DeleteMapping("/{buildingId}")
    public Result<Void> deleteBuilding(@PathVariable Long buildingId) {
        Long tenantId = SecurityUtils.getQueryTenantId();
        buildingService.deleteBuilding(tenantId, buildingId);
        return Result.success();
    }
}
