package com.pingan.ams.common.annotation;

import java.lang.annotation.*;

/**
 * 权限校验注解
 * 标注在Controller方法上，用于校验用户是否有指定权限
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequirePermission {
    /**
     * 权限编码，多个编码之间是"且"的关系
     */
    String[] value();

    /**
     * 逻辑关系：AND-所有权限都需要，OR-任一权限即可
     */
    Logic logic() default Logic.AND;

    enum Logic {
        AND, OR
    }
}
