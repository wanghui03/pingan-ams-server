package com.pingan.ams.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pingan.ams.common.exception.BusinessException;
import com.pingan.ams.common.result.ResultCode;
import com.pingan.ams.mapper.NotificationMapper;
import com.pingan.ams.mapper.UserMapper;
import com.pingan.ams.model.dto.NotificationDTO;
import com.pingan.ams.model.entity.Notification;
import com.pingan.ams.model.entity.User;
import com.pingan.ams.model.vo.NotificationVO;
import com.pingan.ams.service.NotificationService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 站内通知 Service 实现
 */
@Service
public class NotificationServiceImpl extends ServiceImpl<NotificationMapper, Notification> implements NotificationService {

    @Autowired
    private UserMapper userMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createNotification(Long tenantId, NotificationDTO dto) {
        Notification notification = new Notification();
        BeanUtils.copyProperties(dto, notification);
        notification.setTenantId(tenantId);
        notification.setIsRead(0);

        this.save(notification);
        return notification.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markAsRead(Long tenantId, Long notificationId) {
        Notification notification = this.getById(notificationId);
        if (notification == null || !notification.getTenantId().equals(tenantId)) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND.getCode(), "通知不存在");
        }

        notification.setIsRead(1);
        notification.setReadTime(LocalDateTime.now());
        this.updateById(notification);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markAllAsRead(Long tenantId, Long userId) {
        LambdaQueryWrapper<Notification> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Notification::getTenantId, tenantId);
        wrapper.eq(Notification::getUserId, userId);
        wrapper.eq(Notification::getIsRead, 0);

        List<Notification> notifications = this.list(wrapper);
        for (Notification notification : notifications) {
            notification.setIsRead(1);
            notification.setReadTime(LocalDateTime.now());
        }
        this.updateBatchById(notifications);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteNotification(Long tenantId, Long notificationId) {
        Notification notification = this.getById(notificationId);
        if (notification == null || !notification.getTenantId().equals(tenantId)) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND.getCode(), "通知不存在");
        }

        this.removeById(notificationId);
    }

    @Override
    public Page<NotificationVO> listNotifications(Long tenantId, Long userId, Integer type, Integer isRead, Integer page, Integer size) {
        LambdaQueryWrapper<Notification> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Notification::getTenantId, tenantId);
        wrapper.eq(Notification::getUserId, userId);

        if (type != null) {
            wrapper.eq(Notification::getType, type);
        }
        if (isRead != null) {
            wrapper.eq(Notification::getIsRead, isRead);
        }

        wrapper.orderByDesc(Notification::getCreateTime);

        Page<Notification> notificationPage = this.page(new Page<>(page, size), wrapper);

        Page<NotificationVO> voPage = new Page<>(notificationPage.getCurrent(), notificationPage.getSize(), notificationPage.getTotal());
        List<NotificationVO> voList = notificationPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        voPage.setRecords(voList);

        return voPage;
    }

    @Override
    public Long getUnreadCount(Long userId) {
        return baseMapper.countUnread(userId);
    }

    /**
     * 转换为VO
     */
    private NotificationVO convertToVO(Notification notification) {
        NotificationVO vo = new NotificationVO();
        BeanUtils.copyProperties(notification, vo);

        // 获取接收人姓名
        User user = userMapper.selectById(notification.getUserId());
        if (user != null) {
            vo.setUserName(user.getRealName());
        }

        // 设置类型描述
        switch (notification.getType()) {
            case 1:
                vo.setTypeDesc("账单");
                break;
            case 2:
                vo.setTypeDesc("合同");
                break;
            case 3:
                vo.setTypeDesc("工单");
                break;
            case 4:
                vo.setTypeDesc("系统");
                break;
        }

        return vo;
    }
}
