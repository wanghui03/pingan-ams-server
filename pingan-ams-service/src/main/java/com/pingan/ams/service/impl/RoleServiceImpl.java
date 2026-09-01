package com.pingan.ams.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pingan.ams.common.exception.BusinessException;
import com.pingan.ams.common.result.ResultCode;
import com.pingan.ams.mapper.*;
import com.pingan.ams.model.dto.RoleDTO;
import com.pingan.ams.model.entity.*;
import com.pingan.ams.model.vo.RoleVO;
import com.pingan.ams.service.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 角色 Service 实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

    private final RolePermissionMapper rolePermissionMapper;
    private final UserRoleMapper userRoleMapper;
    private final PermissionMapper permissionMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createRole(Long tenantId, RoleDTO roleDTO) {
        // 检查角色编码是否已存在
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Role::getTenantId, tenantId)
                .eq(Role::getRoleCode, roleDTO.getRoleCode());
        if (this.count(wrapper) > 0) {
            throw new BusinessException("角色编码已存在");
        }

        Role role = new Role();
        BeanUtils.copyProperties(roleDTO, role);
        role.setTenantId(tenantId);
        role.setStatus(roleDTO.getStatus() != null ? roleDTO.getStatus() : 1);

        this.save(role);

        // 分配权限
        if (roleDTO.getPermissionIds() != null && !roleDTO.getPermissionIds().isEmpty()) {
            assignPermissions(tenantId, role.getId(), roleDTO.getPermissionIds());
        }

        return role.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateRole(Long tenantId, Long roleId, RoleDTO roleDTO) {
        Role role = this.getRoleByIdAndTenantId(roleId, tenantId);

        // 检查角色编码是否已被其他角色使用
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Role::getTenantId, tenantId)
                .eq(Role::getRoleCode, roleDTO.getRoleCode())
                .ne(Role::getId, roleId);
        if (this.count(wrapper) > 0) {
            throw new BusinessException("角色编码已存在");
        }

        BeanUtils.copyProperties(roleDTO, role);
        this.updateById(role);

        // 更新权限
        if (roleDTO.getPermissionIds() != null) {
            assignPermissions(tenantId, roleId, roleDTO.getPermissionIds());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteRole(Long tenantId, Long roleId) {
        Role role = this.getRoleByIdAndTenantId(roleId, tenantId);

        // 检查是否有用户使用该角色
        Long userCount = baseMapper.countUsersByRoleId(roleId);
        if (userCount > 0) {
            throw new BusinessException("该角色下还有 " + userCount + " 个用户，无法删除");
        }

        this.removeById(roleId);

        // 删除角色权限关联
        rolePermissionMapper.deleteByRoleId(roleId);
    }

    @Override
    public RoleVO getRoleDetail(Long tenantId, Long roleId) {
        Role role = this.getRoleByIdAndTenantId(roleId, tenantId);
        return convertToVO(role);
    }

    @Override
    public Page<RoleVO> listRoles(Long tenantId, Integer page, Integer size, String keyword) {
        Page<Role> pageParam = new Page<>(page, size);

        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        // 查询全局角色（tenant_id IS NULL）或当前租户的角色
        wrapper.and(w -> w.isNull(Role::getTenantId).or().eq(Role::getTenantId, tenantId));

        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(Role::getRoleName, keyword)
                    .or()
                    .like(Role::getRoleCode, keyword);
        }

        wrapper.orderByAsc(Role::getSortOrder);

        Page<Role> rolePage = this.page(pageParam, wrapper);

        Page<RoleVO> voPage = new Page<>(rolePage.getCurrent(), rolePage.getSize(), rolePage.getTotal());
        List<RoleVO> voList = rolePage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        voPage.setRecords(voList);

        return voPage;
    }

    @Override
    public List<RoleVO> getAllRoles(Long tenantId) {
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        // 查询全局角色（tenant_id IS NULL）或当前租户的角色
        wrapper.and(w -> w.isNull(Role::getTenantId).or().eq(Role::getTenantId, tenantId))
                .eq(Role::getStatus, 1)
                .orderByAsc(Role::getSortOrder);

        return this.list(wrapper).stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignPermissions(Long tenantId, Long roleId, List<Long> permissionIds) {
        // 先删除原有权限
        rolePermissionMapper.deleteByRoleId(roleId);

        // 添加新权限
        if (permissionIds != null && !permissionIds.isEmpty()) {
            for (Long permissionId : permissionIds) {
                RolePermission rp = new RolePermission();
                rp.setRoleId(roleId);
                rp.setPermissionId(permissionId);
                rolePermissionMapper.insert(rp);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignUserRoles(Long userId, List<Long> roleIds) {
        // 先删除原有角色
        userRoleMapper.deleteByUserId(userId);

        // 添加新角色
        if (roleIds != null && !roleIds.isEmpty()) {
            for (Long roleId : roleIds) {
                UserRole ur = new UserRole();
                ur.setUserId(userId);
                ur.setRoleId(roleId);
                userRoleMapper.insert(ur);
            }
        }
    }

    @Override
    public List<RoleVO> getUserRoles(Long userId) {
        List<Role> roles = baseMapper.selectRolesByUserId(userId);
        return roles.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getUserPermissionCodes(Long userId) {
        return permissionMapper.selectPermissionCodesByUserId(userId);
    }

    private Role getRoleByIdAndTenantId(Long roleId, Long tenantId) {
        Role role = this.getById(roleId);
        if (role == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        // 超级管理员可以管理所有角色，租户管理员只能管理自己的角色
        if (role.getTenantId() != null && !role.getTenantId().equals(tenantId)) {
            throw new BusinessException(ResultCode.NO_PERMISSION);
        }
        return role;
    }

    private RoleVO convertToVO(Role role) {
        RoleVO vo = new RoleVO();
        BeanUtils.copyProperties(role, vo);

        // 状态描述
        vo.setStatusDesc(role.getStatus() == 1 ? "启用" : "禁用");

        // 关联用户数
        vo.setUserCount(baseMapper.countUsersByRoleId(role.getId()));

        // 关联权限ID列表
        List<Permission> permissions = permissionMapper.selectPermissionsByRoleId(role.getId());
        vo.setPermissionIds(permissions.stream()
                .map(Permission::getId)
                .collect(Collectors.toList()));

        return vo;
    }
}
