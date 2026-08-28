package com.pingan.ams.admin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * 平安公寓管理系统 - 管理后台启动类
 */
@SpringBootApplication
@ComponentScan(basePackages = "com.pingan.ams")
@MapperScan("com.pingan.ams.mapper")
public class PingAnAmsAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(PingAnAmsAdminApplication.class, args);
        System.out.println("""
            ========================================
               平安公寓管理系统 - 管理后台启动成功
            ========================================
            """);
    }
}
