package com.pingan.ams.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pingan.ams.common.exception.BusinessException;
import com.pingan.ams.common.result.ResultCode;
import com.pingan.ams.common.utils.JwtUtils;
import com.pingan.ams.common.utils.PasswordUtils;
import com.pingan.ams.mapper.UserMapper;
import com.pingan.ams.model.dto.LoginDTO;
import com.pingan.ams.model.dto.StaffDTO;
import com.pingan.ams.model.dto.TenantUserDTO;
import com.pingan.ams.model.dto.UserDTO;
import com.pingan.ams.model.entity.User;
import com.pingan.ams.model.vo.LoginVO;
import com.pingan.ams.model.vo.TenantUserVO;
import com.pingan.ams.model.vo.UserVO;
import com.pingan.ams.service.RoleService;
import com.pingan.ams.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户 Service 实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final RoleService roleService;

    @Override
    public LoginVO wxLogin(LoginDTO loginDTO) {
        // TODO: 调用微信API获取真实openid
        String openid = "mock_openid_" + loginDTO.getCode();

        // 租户ID默认为1
        Long tenantId = loginDTO.getTenantId() != null ? loginDTO.getTenantId() : 1L;

        // 通过openid查询用户
        User user = this.getOne(new LambdaQueryWrapper<User>()
                .eq(User::getOpenid, openid));

        boolean isNewUser = false;

        if (user == null) {
            // 新用户,创建用户记录
            isNewUser = true;
            user = new User();
            user.setOpenid(openid);
            user.setTenantId(tenantId);
            user.setUserType(1); // 默认租客
            user.setStatus(1);
            user.setAuthStatus(0);
            user.setLastLoginTime(LocalDateTime.now());
            this.save(user);
        } else {
            // 更新登录时间
            user.setLastLoginTime(LocalDateTime.now());
            this.updateById(user);
            tenantId = user.getTenantId();
        }

        return buildLoginVO(user, isNewUser);
    }

    @Override
    public LoginVO adminLogin(String username, String password) {
        // 通过username查询用户
        User user = this.getOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, username));

        if (user == null) {
            throw new BusinessException("账号不存在");
        }

        // 校验密码
        if (user.getPassword() == null || !PasswordUtils.matches(password, user.getPassword())) {
            throw new BusinessException("密码错误");
        }

        // 校验状态
        if (user.getStatus() != 1) {
            throw new BusinessException("账号已被禁用");
        }

        // 更新登录时间
        user.setLastLoginTime(LocalDateTime.now());
        this.updateById(user);

        return buildLoginVO(user, false);
    }

    @Override
    public UserVO getUserInfo(Long userId) {
        User user = this.getById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }

        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);

        // 身份证号脱敏
        if (userVO.getIdCard() != null && userVO.getIdCard().length() > 8) {
            userVO.setIdCard(userVO.getIdCard().substring(0, 4) + "****" + userVO.getIdCard().substring(userVO.getIdCard().length() - 4));
        }

        return userVO;
    }

    @Override
    public void updateUserInfo(Long userId, UserDTO userDTO) {
        User user = this.getById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }

        if (userDTO.getNickname() != null) {
            user.setNickname(userDTO.getNickname());
        }
        if (userDTO.getAvatar() != null) {
            user.setAvatar(userDTO.getAvatar());
        }
        if (userDTO.getPhone() != null) {
            user.setPhone(userDTO.getPhone());
        }

        this.updateById(user);
    }

    @Override
    public void realNameAuth(Long userId, UserDTO userDTO) {
        User user = this.getById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }

        if (user.getAuthStatus() == 1) {
            throw new BusinessException("已完成实名认证，无需重复操作");
        }

        if (userDTO.getRealName() == null || userDTO.getIdCard() == null) {
            throw new BusinessException(ResultCode.PARAM_MISSING);
        }

        user.setRealName(userDTO.getRealName());
        user.setIdCard(userDTO.getIdCard());
        user.setIdCardFrontUrl(userDTO.getIdCardFrontUrl());
        user.setIdCardBackUrl(userDTO.getIdCardBackUrl());
        user.setFacePhotoUrl(userDTO.getFacePhotoUrl());

        user.setAuthStatus(1);
        this.updateById(user);
    }

    @Override
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = this.getById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }

        // 验证旧密码
        if (user.getPassword() == null || !PasswordUtils.matches(oldPassword, user.getPassword())) {
            throw new BusinessException("原密码错误");
        }

        // 更新密码
        user.setPassword(PasswordUtils.encode(newPassword));
        this.updateById(user);
    }

    @Override
    public Page<UserVO> listStaff(Long tenantId, Integer page, Integer size, String keyword) {
        Page<User> pageParam = new Page<>(page, size);

        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        if (tenantId != null) {
            wrapper.eq(User::getTenantId, tenantId);
        }
        wrapper.in(User::getUserType, 2, 3); // 只查询员工和管理员

        if (keyword != null && !keyword.trim().isEmpty()) {
            wrapper.and(w -> w.like(User::getRealName, keyword)
                    .or().like(User::getPhone, keyword)
                    .or().like(User::getUsername, keyword));
        }

        wrapper.orderByDesc(User::getCreateTime);

        Page<User> userPage = this.page(pageParam, wrapper);

        Page<UserVO> voPage = new Page<>(userPage.getCurrent(), userPage.getSize(), userPage.getTotal());
        voPage.setRecords(userPage.getRecords().stream()
                .map(this::convertToUserVO)
                .collect(java.util.stream.Collectors.toList()));

        return voPage;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createStaff(Long tenantId, StaffDTO staffDTO) {
        // 检查用户名是否已存在
        User existing = this.getOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, staffDTO.getUsername())
                .eq(User::getTenantId, tenantId));
        if (existing != null) {
            throw new BusinessException("用户名已存在");
        }

        User user = new User();
        user.setTenantId(tenantId);
        user.setUsername(staffDTO.getUsername());
        user.setPassword(PasswordUtils.encode(staffDTO.getPassword()));
        user.setRealName(staffDTO.getRealName());
        user.setPhone(staffDTO.getPhone());
        user.setUserType(staffDTO.getUserType());
        user.setStatus(staffDTO.getStatus() != null ? staffDTO.getStatus() : 1);
        user.setAuthStatus(1);

        this.save(user);
        return user.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStaff(Long tenantId, Long staffId, StaffDTO staffDTO) {
        User user = this.getById(staffId);
        if (user == null || !user.getTenantId().equals(tenantId)) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }

        // 检查用户名是否与其他用户重复
        User existing = this.getOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, staffDTO.getUsername())
                .eq(User::getTenantId, tenantId)
                .ne(User::getId, staffId));
        if (existing != null) {
            throw new BusinessException("用户名已存在");
        }

        user.setUsername(staffDTO.getUsername());
        user.setRealName(staffDTO.getRealName());
        user.setPhone(staffDTO.getPhone());
        user.setUserType(staffDTO.getUserType());
        if (staffDTO.getStatus() != null) {
            user.setStatus(staffDTO.getStatus());
        }

        this.updateById(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteStaff(Long tenantId, Long staffId) {
        User user = this.getById(staffId);
        if (user == null || !user.getTenantId().equals(tenantId)) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }

        // 不能删除自己
        Long currentUserId = com.pingan.ams.common.utils.SecurityUtils.getCurrentUserId();
        if (staffId.equals(currentUserId)) {
            throw new BusinessException("不能删除当前登录用户");
        }

        this.removeById(staffId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStaffStatus(Long tenantId, Long staffId, Integer status) {
        User user = this.getById(staffId);
        if (user == null || !user.getTenantId().equals(tenantId)) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }

        user.setStatus(status);
        this.updateById(user);
    }

    @Override
    public Page<TenantUserVO> listTenantUsers(Long tenantId, Integer page, Integer size, String keyword) {
        Page<User> pageParam = new Page<>(page, size);

        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        if (tenantId != null) {
            wrapper.eq(User::getTenantId, tenantId);
        }
        wrapper.eq(User::getUserType, 1); // 只查询租客

        if (keyword != null && !keyword.trim().isEmpty()) {
            wrapper.and(w -> w.like(User::getRealName, keyword)
                    .or().like(User::getPhone, keyword)
                    .or().like(User::getNickname, keyword));
        }

        wrapper.orderByDesc(User::getCreateTime);

        Page<User> userPage = this.page(pageParam, wrapper);

        Page<TenantUserVO> voPage = new Page<>(userPage.getCurrent(), userPage.getSize(), userPage.getTotal());
        voPage.setRecords(userPage.getRecords().stream()
                .map(this::convertToTenantUserVO)
                .collect(java.util.stream.Collectors.toList()));

        return voPage;
    }

    @Override
    public TenantUserVO getTenantUserDetail(Long tenantId, Long userId) {
        User user = this.getById(userId);
        if (user == null || !user.getTenantId().equals(tenantId)) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }

        return convertToTenantUserVO(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateTenantUserStatus(Long tenantId, Long userId, Integer status) {
        User user = this.getById(userId);
        if (user == null || !user.getTenantId().equals(tenantId)) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }

        user.setStatus(status);
        this.updateById(user);
    }

    @Override
    @org.springframework.transaction.annotation.Transactional(rollbackFor = Exception.class)
    public Long createTenantUser(Long tenantId, TenantUserDTO tenantUserDTO) {
        // 检查手机号是否已存在
        User existing = this.getOne(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<User>()
                .eq(User::getPhone, tenantUserDTO.getPhone())
                .eq(User::getTenantId, tenantId));
        if (existing != null) {
            throw new BusinessException("该手机号已存在");
        }

        User user = new User();
        user.setTenantId(tenantId);
        user.setRealName(tenantUserDTO.getRealName());
        user.setPhone(tenantUserDTO.getPhone());
        user.setIdCard(tenantUserDTO.getIdCard());
        user.setUserType(1); // 租客
        user.setStatus(tenantUserDTO.getStatus() != null ? tenantUserDTO.getStatus() : 1);
        user.setAuthStatus(tenantUserDTO.getIdCard() != null ? 1 : 0);

        this.save(user);
        return user.getId();
    }

    @Override
    @org.springframework.transaction.annotation.Transactional(rollbackFor = Exception.class)
    public void updateTenantUser(Long tenantId, Long userId, TenantUserDTO tenantUserDTO) {
        User user = this.getById(userId);
        if (user == null || !user.getTenantId().equals(tenantId)) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }

        // 检查手机号是否与其他用户重复
        User existing = this.getOne(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<User>()
                .eq(User::getPhone, tenantUserDTO.getPhone())
                .eq(User::getTenantId, tenantId)
                .ne(User::getId, userId));
        if (existing != null) {
            throw new BusinessException("该手机号已存在");
        }

        user.setRealName(tenantUserDTO.getRealName());
        user.setPhone(tenantUserDTO.getPhone());
        user.setIdCard(tenantUserDTO.getIdCard());
        if (tenantUserDTO.getStatus() != null) {
            user.setStatus(tenantUserDTO.getStatus());
        }

        this.updateById(user);
    }

    /**
     * 转换为UserVO
     */
    private UserVO convertToUserVO(User user) {
        UserVO vo = new UserVO();
        vo.setId(user.getId());
        vo.setTenantId(user.getTenantId());
        vo.setUsername(user.getUsername());
        vo.setRealName(user.getRealName());
        vo.setPhone(user.getPhone());
        vo.setNickname(user.getNickname());
        vo.setAvatar(user.getAvatar());
        vo.setUserType(user.getUserType());
        vo.setStatus(user.getStatus());
        vo.setAuthStatus(user.getAuthStatus());
        vo.setCreateTime(user.getCreateTime());

        // 设置描述
        if (user.getUserType() != null) {
            String userTypeDesc = switch (user.getUserType()) {
                case 0 -> "超级管理员";
                case 1 -> "租客";
                case 2 -> "员工";
                case 3 -> "管理员";
                default -> "未知";
            };
            vo.setUserTypeDesc(userTypeDesc);
        }

        if (user.getStatus() != null) {
            vo.setStatusDesc(user.getStatus() == 1 ? "启用" : "禁用");
        }

        return vo;
    }

    /**
     * 转换为TenantUserVO
     */
    private TenantUserVO convertToTenantUserVO(User user) {
        TenantUserVO vo = new TenantUserVO();
        vo.setId(user.getId());
        vo.setTenantId(user.getTenantId());
        vo.setUsername(user.getUsername());
        vo.setRealName(user.getRealName());
        vo.setPhone(user.getPhone());
        vo.setNickname(user.getNickname());
        vo.setIdCard(user.getIdCard());
        vo.setAuthStatus(user.getAuthStatus());
        vo.setStatus(user.getStatus());
        vo.setLastLoginTime(user.getLastLoginTime());
        vo.setCreateTime(user.getCreateTime());

        // 设置描述
        if (user.getAuthStatus() != null) {
            vo.setAuthStatusDesc(user.getAuthStatus() == 1 ? "已认证" : "未认证");
        }

        if (user.getStatus() != null) {
            vo.setStatusDesc(user.getStatus() == 1 ? "启用" : "禁用");
        }

        return vo;
    }

    /**
     * 构建登录返回VO
     */
    private LoginVO buildLoginVO(User user, boolean isNewUser) {
        String token = JwtUtils.generateToken(user.getId(), user.getTenantId(), user.getUserType(), user.getUsername());

        LoginVO loginVO = new LoginVO();
        loginVO.setToken(token);
        loginVO.setUserId(user.getId());
        loginVO.setTenantId(user.getTenantId());
        loginVO.setUserType(user.getUserType());
        loginVO.setUsername(user.getUsername());
        loginVO.setRealName(user.getRealName());
        loginVO.setNickname(user.getNickname());
        loginVO.setAvatar(user.getAvatar());
        loginVO.setIsNewUser(isNewUser);
        loginVO.setExpireTime(LocalDateTime.now().plusDays(7));

        // 获取用户角色和权限
        try {
            List<String> roles = roleService.getUserRoles(user.getId()).stream()
                    .map(r -> r.getRoleCode())
                    .toList();
            List<String> permissions = roleService.getUserPermissionCodes(user.getId());
            
            loginVO.setRoles(roles);
            loginVO.setPermissions(permissions);
        } catch (Exception e) {
            log.warn("获取用户角色权限失败: {}", e.getMessage());
        }

        return loginVO;
    }
}
