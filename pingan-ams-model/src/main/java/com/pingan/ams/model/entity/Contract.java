package com.pingan.ams.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.pingan.ams.model.enums.ContractStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 合同表
 */
@Data
@TableName("ams_contract")
public class Contract {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 租户ID
     */
    private Long tenantId;

    /**
     * 合同编号
     */
    private String contractNo;

    /**
     * 用户ID（租客）
     */
    private Long userId;

    /**
     * 房间ID
     */
    private Long roomId;

    /**
     * 合同状态
     */
    @EnumValue
    private ContractStatus status;

    /**
     * 开始日期
     */
    private LocalDate startDate;

    /**
     * 结束日期
     */
    private LocalDate endDate;

    /**
     * 月租金
     */
    private BigDecimal monthlyRent;

    /**
     * 押金
     */
    private BigDecimal deposit;

    /**
     * 支付方式：1-月付 2-季付 3-半年付 4-年付
     */
    private Integer paymentMethod;

    /**
     * 合同文件URL
     */
    private String contractFileUrl;

    /**
     * 腾讯电子签合同ID
     */
    private String tencentContractId;

    /**
     * 签署状态：0-未签署 1-已签署
     */
    private Integer signStatus;

    /**
     * 签署时间
     */
    private LocalDateTime signTime;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 删除标记：0-未删除 1-已删除
     */
    @TableLogic
    private Integer deleted;
}
