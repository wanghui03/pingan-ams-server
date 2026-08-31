package com.pingan.ams.admin.controller;

import com.pingan.ams.common.annotation.Log;
import com.pingan.ams.common.result.Result;
import com.pingan.ams.model.dto.AdminLoginDTO;
import com.pingan.ams.model.dto.LoginDTO;
import com.pingan.ams.model.vo.LoginVO;
import com.pingan.ams.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 */
@Tag(name = "认证管理", description = "登录注册相关接口")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @Log(module = "认证", operation = "微信登录", description = "小程序端登录")
    @Operation(summary = "微信登录（小程序端）")
    @PostMapping("/wx/login")
    public Result<LoginVO> wxLogin(@Valid @RequestBody LoginDTO loginDTO) {
        LoginVO loginVO = userService.wxLogin(loginDTO);
        return Result.success(loginVO);
    }

    @Log(module = "认证", operation = "PC登录", description = "PC端账号密码登录")
    @Operation(summary = "PC端登录（账号密码）")
    @PostMapping("/admin/login")
    public Result<LoginVO> adminLogin(@Valid @RequestBody AdminLoginDTO loginDTO) {
        LoginVO loginVO = userService.adminLogin(loginDTO.getUsername(), loginDTO.getPassword());
        return Result.success(loginVO);
    }

    @Log(module = "认证", operation = "退出登录", description = "退出系统")
    @Operation(summary = "退出登录")
    @PostMapping("/logout")
    public Result<Void> logout() {
        // TODO: 将Token加入黑名单
        return Result.success();
    }
}
