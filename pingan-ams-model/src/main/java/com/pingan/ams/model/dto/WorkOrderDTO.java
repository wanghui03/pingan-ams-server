package com.pingan.ams.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 工单DTO
 */
@Data
public class WorkOrderDTO {

    @NotNull(message = "房间ID不能为空")
    private Long roomId;

    @NotNull(message = "租客ID不能为空")
    private Long userId;

    /**
     * 工单类型：1-报修 2-投诉 3-咨询 4-其他
     */
    @NotNull(message = "工单类型不能为空")
    private Integer orderType;

    @NotBlank(message = "标题不能为空")
    private String title;

    private String description;

    /**
     * 图片URLs（JSON数组字符串）
     */
    private String images;
}
