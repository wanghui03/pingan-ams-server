package com.pingan.ams.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pingan.ams.model.dto.LoginDTO;
import com.pingan.ams.model.dto.StaffDTO;
import com.pingan.ams.model.dto.TenantUserDTO;
import com.pingan.ams.model.dto.UserDTO;
import com.pingan.ams.model.entity.User;
import com.pingan.ams.model.vo.LoginVO;
import com.pingan.ams.model.vo.TenantUserVO;
import com.pingan.ams.model.vo.UserVO;

/**
 * 用户 Service
 */
public interface UserService extends IService<User> {

    /**
     * 微信登录（小程序端）
     */
    LoginVO wxLogin(LoginDTO loginDTO);

    /**
     * PC端账号密码登录
     */
    LoginVO adminLogin(String username, String password);

    /**
     * 获取用户信息
     */
    UserVO getUserInfo(Long userId);

    /**
     * 更新用户信息
     */
    void updateUserInfo(Long userId, UserDTO userDTO);

    /**
     * 实名认证
     */
    void realNameAuth(Long userId, UserDTO userDTO);

    /**
     * 修改密码
     */
    void changePassword(Long userId, String oldPassword, String newPassword);

    /**
     * 分页查询员工列表
     */
    com.baomidou.mybatisplus.extension.plugins.pagination.Page<UserVO> listStaff(Long tenantId, Integer page, Integer size, String keyword);

    /**
     * 创建员工
     */
    Long createStaff(Long tenantId, StaffDTO staffDTO);

    /**
     * 更新员工
     */
    void updateStaff(Long tenantId, Long staffId, StaffDTO staffDTO);

    /**
     * 删除员工
     */
    void deleteStaff(Long tenantId, Long staffId);

    /**
     * 启用/禁用员工
     */
    void updateStaffStatus(Long tenantId, Long staffId, Integer status);

    /**
     * 分页查询租客列表
     */
    com.baomidou.mybatisplus.extension.plugins.pagination.Page<TenantUserVO> listTenantUsers(Long tenantId, Integer page, Integer size, String keyword);

    /**
     * 获取租客详情
     */
    TenantUserVO getTenantUserDetail(Long tenantId, Long userId);

    /**
     * 启用/禁用租客
     */
    void updateTenantUserStatus(Long tenantId, Long userId, Integer status);

    /**
     * 创建租客
     */
    Long createTenantUser(Long tenantId, TenantUserDTO tenantUserDTO);

    /**
     * 更新租客
     */
    void updateTenantUser(Long tenantId, Long userId, TenantUserDTO tenantUserDTO);
}
