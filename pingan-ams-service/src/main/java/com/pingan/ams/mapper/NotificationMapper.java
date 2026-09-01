package com.pingan.ams.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pingan.ams.model.entity.Notification;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 站内通知 Mapper
 */
@Mapper
public interface NotificationMapper extends BaseMapper<Notification> {

    /**
     * 统计未读通知数量
     */
    @Select("SELECT COUNT(*) FROM ams_notification WHERE user_id = #{userId} AND is_read = 0 AND deleted = 0")
    Long countUnread(@Param("userId") Long userId);
}
