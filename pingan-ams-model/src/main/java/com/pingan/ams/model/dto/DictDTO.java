package com.pingan.ams.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 字典DTO
 */
@Data
public class DictDTO {

    /**
     * 字典ID（更新时必填）
     */
    private Long id;

    /**
     * 字典类型
     */
    @NotBlank(message = "字典类型不能为空")
    private String dictType;

    /**
     * 字典编码
     */
    @NotBlank(message = "字典编码不能为空")
    private String dictCode;

    /**
     * 字典名称
     */
    @NotBlank(message = "字典名称不能为空")
    private String dictName;

    /**
     * 排序
     */
    private Integer sortOrder;

    /**
     * 状态：0-禁用 1-启用
     */
    private Integer status;

    /**
     * 备注
     */
    private String remark;
}
