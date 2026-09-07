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
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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
    public Long createRole(RoleDTO roleDTO) {
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Role::getRoleCode, roleDTO.getRoleCode());
        if (this.count(wrapper) > 0) {
            throw new BusinessException("角色编码已存在");
        }

        Role role = new Role();
        BeanUtils.copyProperties(roleDTO, role);
        role.setStatus(roleDTO.getStatus() != null ? roleDTO.getStatus() : 1);

        this.save(role);

        if (roleDTO.getPermissionIds() != null && !roleDTO.getPermissionIds().isEmpty()) {
            assignPermissions(role.getId(), roleDTO.getPermissionIds());
        }

        return role.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateRole(Long roleId, RoleDTO roleDTO) {
        Role role = this.getById(roleId);
        if (role == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }

        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Role::getRoleCode, roleDTO.getRoleCode())
                .ne(Role::getId, roleId);
        if (this.count(wrapper) > 0) {
            throw new BusinessException("角色编码已存在");
        }

        BeanUtils.copyProperties(roleDTO, role);
        this.updateById(role);

        if (roleDTO.getPermissionIds() != null) {
            assignPermissions(roleId, roleDTO.getPermissionIds());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "userPermissions", allEntries = true)
    public void deleteRole(Long roleId) {
        Role role = this.getById(roleId);
        if (role == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }

        Long userCount = baseMapper.countUsersByRoleId(roleId);
        if (userCount > 0) {
            throw new BusinessException("该角色下还有 " + userCount + " 个用户，无法删除");
        }

        this.removeById(roleId);
        rolePermissionMapper.deleteByRoleId(roleId);
    }

    @Override
    public RoleVO getRoleDetail(Long roleId) {
        Role role = this.getById(roleId);
        if (role == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        return convertToVO(role);
    }

    @Override
    public Page<RoleVO> listRoles(Integer page, Integer size, String keyword) {
        Page<Role> pageParam = new Page<>(page, size);

        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
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
    public List<RoleVO> getAllRoles() {
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Role::getStatus, 1)
                .orderByAsc(Role::getSortOrder);

        return this.list(wrapper).stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "userPermissions", allEntries = true)
    public void assignPermissions(Long roleId, List<Long> permissionIds) {
        rolePermissionMapper.deleteByRoleId(roleId);

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
    @CacheEvict(value = "userPermissions", allEntries = true)
    public void assignUserRoles(Long userId, List<Long> roleIds) {
        userRoleMapper.deleteByUserId(userId);

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
    @Cacheable(value = "userPermissions", key = "#userId")
    public List<String> getUserPermissionCodes(Long userId) {
        log.debug("查询用户 {} 的权限（未命中缓存）", userId);
        return permissionMapper.selectPermissionCodesByUserId(userId);
    }

    private RoleVO convertToVO(Role role) {
        RoleVO vo = new RoleVO();
        BeanUtils.copyProperties(role, vo);

        vo.setStatusDesc(role.getStatus() == 1 ? "启用" : "禁用");
        vo.setUserCount(baseMapper.countUsersByRoleId(role.getId()));

        List<Permission> permissions = permissionMapper.selectPermissionsByRoleId(role.getId());
        vo.setPermissionIds(permissions.stream()
                .map(Permission::getId)
                .collect(Collectors.toList()));

        return vo;
    }
}
