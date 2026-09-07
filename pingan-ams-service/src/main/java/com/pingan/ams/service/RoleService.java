package com.pingan.ams.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.pingan.ams.model.dto.RoleDTO;
import com.pingan.ams.model.entity.Role;
import com.pingan.ams.model.vo.RoleVO;

import java.util.List;

/**
 * 角色 Service
 */
public interface RoleService extends IService<Role> {

    /**
     * 创建角色
     */
    Long createRole(RoleDTO roleDTO);

    /**
     * 更新角色
     */
    void updateRole(Long roleId, RoleDTO roleDTO);

    /**
     * 删除角色
     */
    void deleteRole(Long roleId);

    /**
     * 获取角色详情
     */
    RoleVO getRoleDetail(Long roleId);

    /**
     * 分页查询角色
     */
    Page<RoleVO> listRoles(Integer page, Integer size, String keyword);

    /**
     * 获取所有角色（下拉选择）
     */
    List<RoleVO> getAllRoles();

    /**
     * 分配角色权限
     */
    void assignPermissions(Long roleId, List<Long> permissionIds);

    /**
     * 为用户分配角色
     */
    void assignUserRoles(Long userId, List<Long> roleIds);

    /**
     * 获取用户的角色列表
     */
    List<RoleVO> getUserRoles(Long userId);

    /**
     * 获取用户的权限编码列表
     */
    List<String> getUserPermissionCodes(Long userId);
}
