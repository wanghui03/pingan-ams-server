package com.pingan.ams.common.constant;

/**
 * 通用常量
 */
public interface CommonConstant {

    /**
     * 成功
     */
    int SUCCESS = 1;

    /**
     * 失败
     */
    int FAIL = 0;

    /**
     * 默认分页大小
     */
    int DEFAULT_PAGE_SIZE = 10;

    /**
     * 最大分页大小
     */
    int MAX_PAGE_SIZE = 100;

    /**
     * Token前缀
     */
    String TOKEN_PREFIX = "Bearer ";

    /**
     * Redis Key前缀 - 用户Token
     */
    String REDIS_USER_TOKEN = "user:token:";

    /**
     * Redis Key前缀 - 验证码
     */
    String REDIS_SMS_CODE = "sms:code:";
}
