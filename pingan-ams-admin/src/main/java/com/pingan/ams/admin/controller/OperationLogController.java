package com.pingan.ams.admin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pingan.ams.common.result.Result;
import com.pingan.ams.model.vo.OperationLogVO;
import com.pingan.ams.service.OperationLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 操作日志 Controller
 */
@Tag(name = "操作日志管理")
@RestController
@RequestMapping("/operation-log")
@RequiredArgsConstructor
public class OperationLogController {

    private final OperationLogService operationLogService;

    @Operation(summary = "分页查询操作日志")
    @GetMapping("/list")
    public Result<Page<OperationLogVO>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String module,
            @RequestParam(required = false) Integer status) {
        Page<OperationLogVO> result = operationLogService.listLogs(page, size, username, module, status);
        return Result.success(result);
    }

    @Operation(summary = "清空操作日志")
    @DeleteMapping("/clear")
    public Result<Void> clear() {
        operationLogService.clearLogs();
        return Result.success(null);
    }
}
