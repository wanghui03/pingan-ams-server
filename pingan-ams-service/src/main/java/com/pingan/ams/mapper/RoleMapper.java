package com.pingan.ams.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pingan.ams.model.entity.Role;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 角色 Mapper
 */
@Mapper
public interface RoleMapper extends BaseMapper<Role> {

    /**
     * 查询用户的角色列表
     */
    @Select("SELECT r.* FROM ams_role r " +
            "INNER JOIN ams_user_role ur ON r.id = ur.role_id " +
            "WHERE ur.user_id = #{userId} AND r.deleted = 0")
    List<Role> selectRolesByUserId(@Param("userId") Long userId);

    /**
     * 查询角色关联的用户数
     */
    @Select("SELECT COUNT(*) FROM ams_user_role WHERE role_id = #{roleId}")
    Long countUsersByRoleId(@Param("roleId") Long roleId);
}
