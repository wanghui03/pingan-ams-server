package com.pingan.ams.common.utils;

import com.pingan.ams.common.constant.CommonConstant;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

/**
 * 安全工具类
 */
public class SecurityUtils {

    private static final String USER_ID_ATTR = "currentUserId";
    private static final String TENANT_ID_ATTR = "currentTenantId";
    private static final String USER_TYPE_ATTR = "currentUserType";
    private static final String USERNAME_ATTR = "currentUsername";

    /**
     * 获取当前用户ID
     */
    public static Long getUserId() {
        return getCurrentUserId();
    }

    /**
     * 获取当前用户ID
     */
    public static Long getCurrentUserId() {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            Object userId = attributes.getAttribute(USER_ID_ATTR, RequestAttributes.SCOPE_REQUEST);
            if (userId != null) {
                return (Long) userId;
            }
        }
        return null;
    }

    /**
     * 获取当前租户ID
     */
    public static Long getTenantId() {
        return getCurrentTenantId();
    }

    /**
     * 获取当前租户ID
     */
    public static Long getCurrentTenantId() {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            Object tenantId = attributes.getAttribute(TENANT_ID_ATTR, RequestAttributes.SCOPE_REQUEST);
            if (tenantId != null) {
                return (Long) tenantId;
            }
        }
        return null;
    }

    /**
     * 获取当前用户类型
     */
    public static Integer getCurrentUserType() {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            Object userType = attributes.getAttribute(USER_TYPE_ATTR, RequestAttributes.SCOPE_REQUEST);
            if (userType != null) {
                return (Integer) userType;
            }
        }
        return null;
    }

    /**
     * 获取当前用户名
     */
    public static String getCurrentUsername() {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            Object username = attributes.getAttribute(USERNAME_ATTR, RequestAttributes.SCOPE_REQUEST);
            if (username != null) {
                return (String) username;
            }
        }
        return null;
    }

    /**
     * 设置当前用户信息
     */
    public static void setCurrentUser(Long userId, Long tenantId, Integer userType) {
        setCurrentUser(userId, tenantId, userType, null);
    }

    /**
     * 设置当前用户信息（含用户名）
     */
    public static void setCurrentUser(Long userId, Long tenantId, Integer userType, String username) {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            attributes.setAttribute(USER_ID_ATTR, userId, RequestAttributes.SCOPE_REQUEST);
            attributes.setAttribute(TENANT_ID_ATTR, tenantId, RequestAttributes.SCOPE_REQUEST);
            attributes.setAttribute(USER_TYPE_ATTR, userType, RequestAttributes.SCOPE_REQUEST);
            if (username != null) {
                attributes.setAttribute(USERNAME_ATTR, username, RequestAttributes.SCOPE_REQUEST);
            }
        }
    }

    /**
     * 从请求中获取Token
     */
    public static String getTokenFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith(CommonConstant.TOKEN_PREFIX)) {
            return authHeader.substring(CommonConstant.TOKEN_PREFIX.length());
        }
        return null;
    }

    /**
     * 判断是否已登录
     */
    public static boolean isAuthenticated() {
        return getCurrentUserId() != null;
    }
}
