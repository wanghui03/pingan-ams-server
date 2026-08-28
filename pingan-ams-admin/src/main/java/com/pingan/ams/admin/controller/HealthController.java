package com.pingan.ams.admin.controller;

import com.pingan.ams.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 健康检查控制器
 */
@Tag(name = "系统管理", description = "系统相关接口")
@RestController
@RequestMapping("/system")
public class HealthController {

    @Operation(summary = "健康检查")
    @GetMapping("/health")
    public Result<Map<String, Object>> health() {
        Map<String, Object> info = new HashMap<>();
        info.put("name", "平安公寓管理系统");
        info.put("version", "1.0.0");
        info.put("status", "UP");
        return Result.success(info);
    }
}
