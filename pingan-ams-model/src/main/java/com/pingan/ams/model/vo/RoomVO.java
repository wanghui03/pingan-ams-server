package com.pingan.ams.model.vo;

import com.pingan.ams.model.enums.RoomStatus;
import com.pingan.ams.model.enums.RoomType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 房间VO
 */
@Data
public class RoomVO {

    /**
     * 房间ID
     */
    private Long id;

    /**
     * 租户ID
     */
    private Long tenantId;

    /**
     * 楼栋ID
     */
    private Long buildingId;

    /**
     * 楼栋名称
     */
    private String buildingName;

    /**
     * 楼层
     */
    private Integer floor;

    /**
     * 房间号
     */
    private String roomNo;

    /**
     * 房源类型
     */
    private RoomType roomType;

    /**
     * 房源类型描述
     */
    private String roomTypeDesc;

    /**
     * 房源状态
     */
    private RoomStatus status;

    /**
     * 房源状态描述
     */
    private String statusDesc;

    /**
     * 面积（平方米）
     */
    private BigDecimal area;

    /**
     * 户型
     */
    private String layout;

    /**
     * 朝向
     */
    private String orientation;

    /**
     * 月租金
     */
    private BigDecimal monthlyRent;

    /**
     * 押金
     */
    private BigDecimal deposit;

    /**
     * 配套设施
     */
    private List<String> facilities;

    /**
     * 描述
     */
    private String description;

    /**
     * 图片URLs
     */
    private List<String> images;

    /**
     * 详细地址
     */
    private String address;

    /**
     * 经度
     */
    private BigDecimal longitude;

    /**
     * 纬度
     */
    private BigDecimal latitude;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
