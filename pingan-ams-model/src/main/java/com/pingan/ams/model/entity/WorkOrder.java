package com.pingan.ams.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.pingan.ams.model.enums.WorkOrderStatus;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 工单表
 */
@Data
@TableName("ams_work_order")
public class WorkOrder {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 租户ID
     */
    private Long tenantId;

    /**
     * 工单编号
     */
    private String orderNo;

    /**
     * 用户ID（租客）
     */
    private Long userId;

    /**
     * 房间ID
     */
    private Long roomId;

    /**
     * 工单类型：1-报修 2-投诉 3-咨询 4-其他
     */
    private Integer orderType;

    /**
     * 工单状态
     */
    @EnumValue
    private WorkOrderStatus status;

    /**
     * 标题
     */
    private String title;

    /**
     * 描述
     */
    private String description;

    /**
     * 图片URLs（JSON数组）
     */
    private String images;

    /**
     * 处理人ID
     */
    private Long handlerId;

    /**
     * 处理结果
     */
    private String handleResult;

    /**
     * 处理图片（JSON数组）
     */
    private String handleImages;

    /**
     * 处理时间
     */
    private LocalDateTime handleTime;

    /**
     * 评价分数（1-5）
     */
    private Integer rating;

    /**
     * 评价内容
     */
    private String ratingContent;

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
