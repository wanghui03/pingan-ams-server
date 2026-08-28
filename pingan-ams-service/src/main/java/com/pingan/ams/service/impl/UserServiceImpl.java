package com.pingan.ams.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pingan.ams.common.exception.BusinessException;
import com.pingan.ams.common.result.ResultCode;
import com.pingan.ams.common.utils.JwtUtils;
import com.pingan.ams.mapper.UserMapper;
import com.pingan.ams.model.dto.LoginDTO;
import com.pingan.ams.model.dto.UserDTO;
import com.pingan.ams.model.entity.User;
import com.pingan.ams.model.vo.LoginVO;
import com.pingan.ams.model.vo.UserVO;
import com.pingan.ams.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 用户 Service 实现
 */
@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Override
    public LoginVO login(LoginDTO loginDTO) {
        // TODO: 调用微信API获取openid
        String openid = "mock_openid_" + loginDTO.getCode();
        
        // 查询用户是否存在
        User user = this.getOne(new LambdaQueryWrapper<User>()
                .eq(User::getOpenid, openid));
        
        boolean isNewUser = false;
        
        if (user == null) {
            // 新用户，创建用户记录
            isNewUser = true;
            user = new User();
            user.setOpenid(openid);
            user.setTenantId(loginDTO.getTenantId() != null ? loginDTO.getTenantId() : 1L);
            user.setUserType(1); // 默认租客
            user.setStatus(1);
            user.setAuthStatus(0);
            user.setLastLoginTime(LocalDateTime.now());
            this.save(user);
        } else {
            // 更新登录时间
            user.setLastLoginTime(LocalDateTime.now());
            this.updateById(user);
        }
        
        // 生成Token
        String token = JwtUtils.generateToken(user.getId(), user.getTenantId(), user.getUserType());
        
        // 构建返回结果
        LoginVO loginVO = new LoginVO();
        loginVO.setToken(token);
        loginVO.setUserId(user.getId());
        loginVO.setTenantId(user.getTenantId());
        loginVO.setUserType(user.getUserType());
        loginVO.setNickname(user.getNickname());
        loginVO.setAvatar(user.getAvatar());
        loginVO.setIsNewUser(isNewUser);
        loginVO.setExpireTime(LocalDateTime.now().plusDays(7));
        
        return loginVO;
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
        
        // 校验必填字段
        if (userDTO.getRealName() == null || userDTO.getIdCard() == null) {
            throw new BusinessException(ResultCode.PARAM_MISSING);
        }
        
        user.setRealName(userDTO.getRealName());
        user.setIdCard(userDTO.getIdCard());
        user.setIdCardFrontUrl(userDTO.getIdCardFrontUrl());
        user.setIdCardBackUrl(userDTO.getIdCardBackUrl());
        user.setFacePhotoUrl(userDTO.getFacePhotoUrl());
        
        // TODO: 如果开启了人脸识别，需要调用第三方人脸比对API
        
        user.setAuthStatus(1);
        this.updateById(user);
    }
}
