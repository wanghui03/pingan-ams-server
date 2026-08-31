package com.pingan.ams.model.vo;

import lombok.Data;

/**
 * 首页统计VO
 */
@Data
public class DashboardStatsVO {

    /**
     * 楼栋总数
     */
    private Long buildingCount = 0L;

    /**
     * 房间总数
     */
    private Long roomCount = 0L;

    /**
     * 已入住房间数
     */
    private Long occupiedRoomCount = 0L;

    /**
     * 空置房间数
     */
    private Long vacantRoomCount = 0L;

    /**
     * 入住率
     */
    private String occupancyRate = "0%";

    /**
     * 合同总数
     */
    private Long contractCount = 0L;

    /**
     * 生效中合同数
     */
    private Long activeContractCount = 0L;

    /**
     * 待收金额（待支付账单总金额）
     */
    private java.math.BigDecimal unpaidAmount = java.math.BigDecimal.ZERO;

    /**
     * 已收金额（已支付账单总金额）
     */
    private java.math.BigDecimal paidAmount = java.math.BigDecimal.ZERO;

    /**
     * 逾期账单数
     */
    private Long overdueBillCount = 0L;

    /**
     * 待处理工单数
     */
    private Long pendingWorkOrderCount = 0L;
}
