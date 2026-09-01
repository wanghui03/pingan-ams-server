package com.pingan.ams.model.dto;

import lombok.Data;

/**
 * 站内通知DTO
 */
@Data
public class NotificationDTO {

    /**
     * 接收人ID
     */
    private Long userId;

    /**
     * 通知标题
     */
    private String title;

    /**
     * 通知内容
     */
    private String content;

    /**
     * 通知类型：1-账单 2-合同 3-工单 4-系统
     */
    private Integer type;

    /**
     * 关联业务类型：bill/contract/workorder
     */
    private String bizType;

    /**
     * 关联业务ID
     */
    private Long bizId;
}
