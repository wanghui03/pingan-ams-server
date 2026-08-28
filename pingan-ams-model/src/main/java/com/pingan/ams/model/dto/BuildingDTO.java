package com.pingan.ams.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 楼栋DTO
 */
@Data
public class BuildingDTO {

    @NotBlank(message = "楼栋名称不能为空")
    private String name;

    private String address;

    private Integer totalFloors;

    private String description;
}
