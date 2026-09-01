package com.pingan.ams.common.result;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 返回码枚举
 */
@Getter
@AllArgsConstructor
public enum ResultCode {

    SUCCESS(200, "操作成功"),
    ERROR(500, "操作失败"),
    
    // 认证相关 1xxx
    UNAUTHORIZED(1001, "未登录或登录已过期"),
    FORBIDDEN(1003, "无权限访问"),
    TOKEN_INVALID(1004, "Token无效"),
    TOKEN_EXPIRED(1005, "Token已过期"),
    
    // 参数校验 2xxx
    PARAM_ERROR(2001, "参数错误"),
    PARAM_MISSING(2002, "缺少必要参数"),
    
    // 业务相关 3xxx
    DATA_NOT_FOUND(3001, "数据不存在"),
    DATA_DUPLICATE(3002, "数据已存在"),
    STATUS_ERROR(3003, "状态异常"),
    NO_PERMISSION(3004, "无权限执行此操作"),
    CONTRACT_STATUS_ERROR(3005, "合同状态异常"),
    // 第三方服务 4xxx
    SMS_SEND_ERROR(4001, "短信发送失败"),
    OSS_UPLOAD_ERROR(4002, "文件上传失败");

    private final Integer code;
    private final String message;
}
