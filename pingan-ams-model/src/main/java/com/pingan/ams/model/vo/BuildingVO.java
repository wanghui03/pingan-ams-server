package com.pingan.ams.model.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 楼栋VO
 */
@Data
public class BuildingVO {

    private Long id;

    private Long tenantId;

    private String name;

    private String address;

    private Integer totalFloors;

    private String description;

    private Integer status;

    private Integer roomCount;

    private Integer vacantCount;

    private LocalDateTime createTime;
}
