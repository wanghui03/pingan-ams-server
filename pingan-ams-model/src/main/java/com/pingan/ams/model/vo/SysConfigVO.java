package com.pingan.ams.model.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 系统配置VO
 */
@Data
public class SysConfigVO {

    private Long id;

    private String configKey;

    private String configValue;

    private String description;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
