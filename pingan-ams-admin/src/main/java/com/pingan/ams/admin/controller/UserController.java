package com.pingan.ams.admin.controller;

import com.pingan.ams.common.result.Result;
import com.pingan.ams.common.utils.SecurityUtils;
import com.pingan.ams.model.dto.UserDTO;
import com.pingan.ams.model.vo.UserVO;
import com.pingan.ams.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 用户控制器
 */
@Tag(name = "用户管理", description = "用户相关接口")
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "获取当前用户信息")
    @GetMapping("/info")
    public Result<UserVO> getUserInfo() {
        Long userId = SecurityUtils.getCurrentUserId();
        UserVO userVO = userService.getUserInfo(userId);
        return Result.success(userVO);
    }

    @Operation(summary = "更新用户信息")
    @PutMapping("/info")
    public Result<Void> updateUserInfo(@Valid @RequestBody UserDTO userDTO) {
        Long userId = SecurityUtils.getCurrentUserId();
        userService.updateUserInfo(userId, userDTO);
        return Result.success();
    }

    @Operation(summary = "实名认证")
    @PostMapping("/auth")
    public Result<Void> realNameAuth(@Valid @RequestBody UserDTO userDTO) {
        Long userId = SecurityUtils.getCurrentUserId();
        userService.realNameAuth(userId, userDTO);
        return Result.success();
    }
}
