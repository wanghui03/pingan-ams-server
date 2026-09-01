package com.pingan.ams.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 合同变更历史表
 */
@Data
@TableName("ams_contract_change_history")
public class ContractChangeHistory {

    /**
     * ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 合同ID
     */
    private Long contractId;

    /**
     * 变更ID
     */
    private Long changeId;

    /**
     * 操作类型：CREATE-创建/APPROVE-审批/REJECT-驳回/EXECUTE-执行
     */
    private String actionType;

    /**
     * 操作描述
     */
    private String actionDesc;

    /**
     * 操作人ID
     */
    private Long operatorId;

    /**
     * 操作人姓名
     */
    private String operatorName;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
