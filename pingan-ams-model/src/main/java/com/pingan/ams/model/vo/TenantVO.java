package com.pingan.ams.model.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 租户管理VO
 */
@Data
public class TenantVO {

    /**
     * 租户ID
     */
    private Long id;

    /**
     * 租户名称
     */
    private String name;

    /**
     * 租户类型：1-企业 2-个人
     */
    private Integer type;

    /**
     * 租户类型描述
     */
    private String typeDesc;

    /**
     * 联系人
     */
    private String contactPerson;

    /**
     * 联系电话
     */
    private String contactPhone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 地址
     */
    private String address;

    /**
     * 状态：0-禁用 1-启用
     */
    private Integer status;

    /**
     * 状态描述
     */
    private String statusDesc;

    /**
     * 房间总数
     */
    private Integer roomCount;

    /**
     * 入住率
     */
    private String occupancyRate;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
