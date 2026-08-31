package com.pingan.ams.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pingan.ams.common.annotation.Log;
import com.pingan.ams.common.utils.SecurityUtils;
import com.pingan.ams.model.entity.OperationLog;
import com.pingan.ams.service.OperationLogService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 * 操作日志切面
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class OperationLogAspect {

    private final OperationLogService operationLogService;
    private final ObjectMapper objectMapper;

    @Around("@annotation(com.pingan.ams.common.annotation.Log)")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        long startTime = System.currentTimeMillis();

        OperationLog operationLog = new OperationLog();
        operationLog.setOperateTime(LocalDateTime.now());

        try {
            // 执行方法
            Object result = point.proceed();

            // 记录成功日志
            operationLog.setStatus(1);
            operationLog.setResult("成功");
            operationLog.setCostTime(System.currentTimeMillis() - startTime);

            saveLog(point, operationLog);

            return result;
        } catch (Throwable e) {
            // 记录失败日志
            operationLog.setStatus(0);
            operationLog.setErrorMsg(e.getMessage());
            operationLog.setCostTime(System.currentTimeMillis() - startTime);

            saveLog(point, operationLog);

            throw e;
        }
    }

    private void saveLog(ProceedingJoinPoint point, OperationLog operationLog) {
        try {
            // 获取注解信息
            MethodSignature signature = (MethodSignature) point.getSignature();
            Method method = signature.getMethod();
            Log logAnnotation = method.getAnnotation(Log.class);

            if (logAnnotation != null) {
                operationLog.setModule(logAnnotation.module());
                operationLog.setOperation(logAnnotation.operation());
                operationLog.setDescription(logAnnotation.description());
            }

            // 获取请求信息
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                operationLog.setMethod(request.getMethod());
                operationLog.setIp(getIpAddress(request));
            }

            // 获取用户信息
            Long userId = SecurityUtils.getCurrentUserId();
            String username = SecurityUtils.getCurrentUsername();
            Long tenantId = SecurityUtils.getCurrentTenantId();

            operationLog.setUserId(userId);
            operationLog.setUsername(username);
            operationLog.setTenantId(tenantId);

            // 获取参数
            Object[] args = point.getArgs();
            if (args != null && args.length > 0) {
                try {
                    String params = objectMapper.writeValueAsString(args);
                    if (params.length() > 2000) {
                        params = params.substring(0, 2000);
                    }
                    operationLog.setParams(params);
                } catch (Exception e) {
                    log.warn("序列化参数失败", e);
                }
            }

            // 异步保存日志
            operationLogService.log(operationLog);
        } catch (Exception e) {
            log.error("保存操作日志失败", e);
        }
    }

    private String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
