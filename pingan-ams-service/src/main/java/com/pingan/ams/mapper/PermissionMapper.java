package com.pingan.ams.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pingan.ams.model.entity.Permission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 权限 Mapper
 */
@Mapper
public interface PermissionMapper extends BaseMapper<Permission> {

    /**
     * 查询角色的权限列表
     */
    @Select("SELECT p.* FROM ams_permission p " +
            "INNER JOIN ams_role_permission rp ON p.id = rp.permission_id " +
            "WHERE rp.role_id = #{roleId} AND p.deleted = 0")
    List<Permission> selectPermissionsByRoleId(@Param("roleId") Long roleId);

    /**
     * 查询用户的权限列表（通过角色关联）
     */
    @Select("SELECT DISTINCT p.* FROM ams_permission p " +
            "INNER JOIN ams_role_permission rp ON p.id = rp.permission_id " +
            "INNER JOIN ams_user_role ur ON rp.role_id = ur.role_id " +
            "WHERE ur.user_id = #{userId} AND p.deleted = 0")
    List<Permission> selectPermissionsByUserId(@Param("userId") Long userId);

    /**
     * 查询用户的权限编码列表
     */
    @Select("SELECT DISTINCT p.code FROM ams_permission p " +
            "INNER JOIN ams_role_permission rp ON p.id = rp.permission_id " +
            "INNER JOIN ams_user_role ur ON rp.role_id = ur.role_id " +
            "WHERE ur.user_id = #{userId} AND p.deleted = 0")
    List<String> selectPermissionCodesByUserId(@Param("userId") Long userId);
}
