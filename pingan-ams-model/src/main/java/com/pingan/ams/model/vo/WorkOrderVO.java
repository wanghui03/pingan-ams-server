package com.pingan.ams.model.vo;

import com.pingan.ams.model.enums.WorkOrderStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 工单VO
 */
@Data
public class WorkOrderVO {

    private Long id;

    private String orderNo;

    private Long userId;

    private String tenantName;

    private Long roomId;

    private String roomNo;

    private String buildingName;

    private Integer orderType;

    private String orderTypeDesc;

    private WorkOrderStatus status;

    private String statusDesc;

    private String title;

    private String description;

    private List<String> images;

    private Long handlerId;

    private String handlerName;

    private String handleResult;

    private List<String> handleImages;

    private LocalDateTime handleTime;

    private Integer rating;

    private String ratingContent;

    private LocalDateTime createTime;
}
