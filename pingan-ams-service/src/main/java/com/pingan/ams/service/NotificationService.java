package com.pingan.ams.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.pingan.ams.model.dto.NotificationDTO;
import com.pingan.ams.model.entity.Notification;
import com.pingan.ams.model.vo.NotificationVO;

/**
 * 站内通知 Service
 */
public interface NotificationService extends IService<Notification> {

    /**
     * 创建通知
     * @param tenantId 租户ID
     * @param dto 通知信息
     * @return 通知ID
     */
    Long createNotification(Long tenantId, NotificationDTO dto);

    /**
     * 标记通知为已读
     * @param tenantId 租户ID
     * @param notificationId 通知ID
     */
    void markAsRead(Long tenantId, Long notificationId);

    /**
     * 标记所有通知为已读
     * @param tenantId 租户ID
     * @param userId 用户ID
     */
    void markAllAsRead(Long tenantId, Long userId);

    /**
     * 删除通知
     * @param tenantId 租户ID
     * @param notificationId 通知ID
     */
    void deleteNotification(Long tenantId, Long notificationId);

    /**
     * 分页查询通知列表
     * @param tenantId 租户ID
     * @param userId 用户ID
     * @param type 通知类型（可选）
     * @param isRead 是否已读（可选）
     * @param page 页码
     * @param size 每页数量
     * @return 分页结果
     */
    Page<NotificationVO> listNotifications(Long tenantId, Long userId, Integer type, Integer isRead, Integer page, Integer size);

    /**
     * 获取未读通知数量
     * @param userId 用户ID
     * @return 未读数量
     */
    Long getUnreadCount(Long userId);

    /**
     * 发布系统公告（发送给租户下所有用户）
     * @param tenantId 租户ID
     * @param dto 通知信息（userId会被忽略，会发给所有用户）
     */
    void publishAnnouncement(Long tenantId, NotificationDTO dto);
}
