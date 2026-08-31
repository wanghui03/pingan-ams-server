package com.pingan.ams.common.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT 工具类
 */
@Slf4j
public class JwtUtils {

    private static final String SECRET = "pingan-ams-jwt-secret-key-2024-must-be-at-least-256-bits";
    private static final long EXPIRATION = 7 * 24 * 60 * 60 * 1000; // 7天

    private static final SecretKey KEY = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));

    /**
     * 生成Token
     */
    public static String generateToken(Long userId, Long tenantId, Integer userType, String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("tenantId", tenantId);
        claims.put("userType", userType);
        claims.put("username", username != null ? username : "");

        return Jwts.builder()
                .claims(claims)
                .subject(String.valueOf(userId))
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(KEY)
                .compact();
    }

    /**
     * 生成Token（向后兼容）
     */
    public static String generateToken(Long userId, Long tenantId, Integer userType) {
        return generateToken(userId, tenantId, userType, null);
    }

    /**
     * 解析Token
     */
    public static Claims parseToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(KEY)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            log.warn("Token已过期");
            throw e;
        } catch (JwtException e) {
            log.warn("Token无效: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * 获取用户ID
     */
    public static Long getUserId(String token) {
        Claims claims = parseToken(token);
        return claims.get("userId", Long.class);
    }

    /**
     * 获取租户ID
     */
    public static Long getTenantId(String token) {
        Claims claims = parseToken(token);
        return claims.get("tenantId", Long.class);
    }

    /**
     * 获取用户类型
     */
    public static Integer getUserType(String token) {
        Claims claims = parseToken(token);
        return claims.get("userType", Integer.class);
    }

    /**
     * 验证Token是否有效
     */
    public static boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
