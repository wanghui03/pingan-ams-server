package com.pingan.ams.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pingan.ams.model.entity.RolePermission;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 角色权限关联 Mapper
 */
@Mapper
public interface RolePermissionMapper extends BaseMapper<RolePermission> {

    /**
     * 删除角色的权限关联
     */
    @Delete("DELETE FROM ams_role_permission WHERE role_id = #{roleId}")
    int deleteByRoleId(@Param("roleId") Long roleId);
}
