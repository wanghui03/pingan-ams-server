package com.pingan.ams.service;

import com.pingan.ams.model.vo.DashboardStatsVO;

/**
 * 首页统计 Service
 */
public interface DashboardService {

    /**
     * 获取首页统计数据
     */
    DashboardStatsVO getStats(Long tenantId);
}
