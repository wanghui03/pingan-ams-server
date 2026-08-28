package com.pingan.ams.admin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pingan.ams.common.result.Result;
import com.pingan.ams.common.utils.SecurityUtils;
import com.pingan.ams.model.dto.RoomDTO;
import com.pingan.ams.model.vo.RoomVO;
import com.pingan.ams.service.RoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 房间控制器
 */
@Tag(name = "房间管理", description = "房间相关接口")
@RestController
@RequestMapping("/room")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    @Operation(summary = "创建房间")
    @PostMapping
    public Result<Long> createRoom(@Valid @RequestBody RoomDTO roomDTO) {
        Long tenantId = SecurityUtils.getCurrentTenantId();
        Long roomId = roomService.createRoom(tenantId, roomDTO);
        return Result.success(roomId);
    }

    @Operation(summary = "更新房间")
    @PutMapping("/{roomId}")
    public Result<Void> updateRoom(@PathVariable Long roomId, @Valid @RequestBody RoomDTO roomDTO) {
        Long tenantId = SecurityUtils.getCurrentTenantId();
        roomService.updateRoom(tenantId, roomId, roomDTO);
        return Result.success();
    }

    @Operation(summary = "获取房间详情")
    @GetMapping("/{roomId}")
    public Result<RoomVO> getRoomDetail(@PathVariable Long roomId) {
        Long tenantId = SecurityUtils.getCurrentTenantId();
        RoomVO roomVO = roomService.getRoomDetail(tenantId, roomId);
        return Result.success(roomVO);
    }

    @Operation(summary = "分页查询房间列表")
    @GetMapping("/list")
    public Result<Page<RoomVO>> listRooms(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Integer roomType) {
        Long tenantId = SecurityUtils.getCurrentTenantId();
        Page<RoomVO> pageResult = roomService.listRooms(tenantId, page, size, status, roomType);
        return Result.success(pageResult);
    }

    @Operation(summary = "删除房间")
    @DeleteMapping("/{roomId}")
    public Result<Void> deleteRoom(@PathVariable Long roomId) {
        Long tenantId = SecurityUtils.getCurrentTenantId();
        roomService.deleteRoom(tenantId, roomId);
        return Result.success();
    }
}
