package com.pingan.ams.common.aspect;

import com.pingan.ams.common.annotation.RequirePermission;
import com.pingan.ams.common.exception.BusinessException;
import com.pingan.ams.common.result.ResultCode;
import com.pingan.ams.common.utils.SecurityUtils;
import com.pingan.ams.service.RoleService;
import com.pingan.ams.service.SysConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

/**
 * 权限校验切面
 * 支持通过系统配置开启/关闭权限控制
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class PermissionAspect {

    private final RoleService roleService;
    private final SysConfigService sysConfigService;

    /**
     * 权限配置键
     */
    private static final String PERMISSION_ENABLED_KEY = "permission_enabled";

    @Before("@annotation(com.pingan.ams.common.annotation.RequirePermission)")
    public void checkPermission(JoinPoint joinPoint) {
        // 检查是否开启权限控制
        boolean permissionEnabled = sysConfigService.getBooleanConfig(PERMISSION_ENABLED_KEY, false);
        if (!permissionEnabled) {
            log.debug("权限控制已关闭，跳过权限校验");
            return;
        }

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        RequirePermission annotation = method.getAnnotation(RequirePermission.class);
        
        if (annotation == null) {
            return;
        }

        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }

        // 获取用户权限
        List<String> userPermissions = roleService.getUserPermissionCodes(userId);
        String[] requiredPermissions = annotation.value();
        RequirePermission.Logic logic = annotation.logic();

        boolean hasPermission;
        if (logic == RequirePermission.Logic.AND) {
            // 所有权限都需要
            hasPermission = Arrays.stream(requiredPermissions)
                    .allMatch(userPermissions::contains);
        } else {
            // 任一权限即可
            hasPermission = Arrays.stream(requiredPermissions)
                    .anyMatch(userPermissions::contains);
        }

        if (!hasPermission) {
            log.warn("用户 {} 没有权限: {}", userId, Arrays.toString(requiredPermissions));
            throw new BusinessException(ResultCode.NO_PERMISSION);
        }
    }
}
