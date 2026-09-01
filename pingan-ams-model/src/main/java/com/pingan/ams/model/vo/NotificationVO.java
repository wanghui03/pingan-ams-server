package com.pingan.ams.model.vo;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 站内通知VO
 */
@Data
public class NotificationVO {

    /**
     * 通知ID
     */
    private Long id;

    /**
     * 租户ID
     */
    private Long tenantId;

    /**
     * 接收人ID
     */
    private Long userId;

    /**
     * 接收人姓名
     */
    private String userName;

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
     * 通知类型描述
     */
    private String typeDesc;

    /**
     * 关联业务类型：bill/contract/workorder
     */
    private String bizType;

    /**
     * 关联业务ID
     */
    private Long bizId;

    /**
     * 是否已读：0-未读 1-已读
     */
    private Integer isRead;

    /**
     * 阅读时间
     */
    private LocalDateTime readTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
