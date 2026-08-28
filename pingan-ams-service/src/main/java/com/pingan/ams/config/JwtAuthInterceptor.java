package com.pingan.ams.config;

import com.pingan.ams.common.constant.CommonConstant;
import com.pingan.ams.common.utils.JwtUtils;
import com.pingan.ams.common.utils.SecurityUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * JWT 认证拦截器
 */
@Slf4j
@Component
public class JwtAuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 放行OPTIONS预检请求
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String token = SecurityUtils.getTokenFromRequest(request);
        
        if (token == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":1001,\"message\":\"未登录或登录已过期\"}");
            return false;
        }

        try {
            // 验证Token
            if (!JwtUtils.validateToken(token)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"code\":1004,\"message\":\"Token无效\"}");
                return false;
            }

            // 解析Token
            Claims claims = JwtUtils.parseToken(token);
            Long userId = claims.get("userId", Long.class);
            Long tenantId = claims.get("tenantId", Long.class);
            Integer userType = claims.get("userType", Integer.class);

            // 设置当前用户信息
            SecurityUtils.setCurrentUser(userId, tenantId, userType);

            return true;
        } catch (Exception e) {
            log.error("Token验证失败: {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":1005,\"message\":\"Token已过期\"}");
            return false;
        }
    }
}
