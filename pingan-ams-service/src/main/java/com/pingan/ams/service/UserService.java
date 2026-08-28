package com.pingan.ams.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pingan.ams.model.dto.LoginDTO;
import com.pingan.ams.model.dto.UserDTO;
import com.pingan.ams.model.entity.User;
import com.pingan.ams.model.vo.LoginVO;
import com.pingan.ams.model.vo.UserVO;

/**
 * 用户 Service
 */
public interface UserService extends IService<User> {

    /**
     * 微信登录
     */
    LoginVO login(LoginDTO loginDTO);

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
}
