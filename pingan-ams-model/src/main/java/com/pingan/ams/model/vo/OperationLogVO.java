package com.pingan.ams.model.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 操作日志VO
 */
@Data
public class OperationLogVO {

    private Long id;

    private Long tenantId;

    private String tenantName;

    private Long userId;

    private String username;

    private String module;

    private String operation;

    private String description;

    private String method;

    private String params;

    private String result;

    private String ip;

    private Integer status;

    private String statusDesc;

    private String errorMsg;

    private LocalDateTime operateTime;

    private Long costTime;
}
