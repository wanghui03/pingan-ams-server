package com.pingan.ams.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pingan.ams.model.entity.UserRole;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 用户角色关联 Mapper
 */
@Mapper
public interface UserRoleMapper extends BaseMapper<UserRole> {

    /**
     * 删除用户的角色关联
     */
    @Delete("DELETE FROM ams_user_role WHERE user_id = #{userId}")
    int deleteByUserId(@Param("userId") Long userId);
}
