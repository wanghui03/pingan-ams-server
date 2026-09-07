package com.pingan.ams.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.pingan.ams.model.enums.RoomStatus;
import com.pingan.ams.model.enums.RoomType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 房间表
 */
@Data
@TableName("ams_room")
public class Room {

    @TableId(type = IdType.AUTO)
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
    @EnumValue
    private RoomType roomType;

    /**
     * 房源状态
     */
    @EnumValue
    private RoomStatus status;

    /**
     * 面积（平方米）
     */
    private BigDecimal area;

    /**
     * 户型（如：一室一厅）
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
     * 配套设施（JSON格式）
     */
    private String facilities;

    /**
     * 描述
     */
    private String description;

    /**
     * 图片URLs（JSON数组）
     */
    private String images;

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
     * 水费单价（元/吨）
     */
    private BigDecimal waterPrice;

    /**
     * 电费单价（元/度）
     */
    private BigDecimal electricityPrice;

    /**
     * 上次水表读数
     */
    private BigDecimal lastWaterReading;

    /**
     * 上次电表读数
     */
    private BigDecimal lastElectricityReading;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 删除标记：0-未删除 1-已删除
     */
    @TableLogic
    private Integer deleted;
}
