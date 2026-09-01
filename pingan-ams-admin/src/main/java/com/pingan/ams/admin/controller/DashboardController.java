package com.pingan.ams.admin.controller;

import com.pingan.ams.common.result.Result;
import com.pingan.ams.common.utils.SecurityUtils;
import com.pingan.ams.model.vo.DashboardStatsVO;
import com.pingan.ams.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 首页统计控制器
 */
@Tag(name = "首页统计", description = "首页数据统计相关接口")
@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @Operation(summary = "获取首页统计数据")
    @GetMapping("/stats")
    public Result<DashboardStatsVO> getStats() {
        Long tenantId = SecurityUtils.getQueryTenantId();
        DashboardStatsVO stats = dashboardService.getStats(tenantId);
        return Result.success(stats);
    }
}
