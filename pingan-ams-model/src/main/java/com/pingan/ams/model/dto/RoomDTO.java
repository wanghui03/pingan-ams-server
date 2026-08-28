package com.pingan.ams.model.dto;

import com.pingan.ams.model.enums.RoomStatus;
import com.pingan.ams.model.enums.RoomType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 房间DTO
 */
@Data
public class RoomDTO {

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
    @NotBlank(message = "房间号不能为空")
    private String roomNo;

    /**
     * 房源类型
     */
    @NotNull(message = "房源类型不能为空")
    private RoomType roomType;

    /**
     * 房源状态
     */
    private RoomStatus status;

    /**
     * 面积（平方米）
     */
    @DecimalMin(value = "0", message = "面积不能小于0")
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
    @DecimalMin(value = "0", message = "月租金不能小于0")
    private BigDecimal monthlyRent;

    /**
     * 押金
     */
    @DecimalMin(value = "0", message = "押金不能小于0")
    private BigDecimal deposit;

    /**
     * 配套设施（JSON字符串）
     */
    private String facilities;

    /**
     * 描述
     */
    private String description;

    /**
     * 图片URLs（JSON数组字符串）
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
}
