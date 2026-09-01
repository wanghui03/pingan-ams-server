-- ========================================
-- 合同变更相关表
-- ========================================

-- 合同变更申请表
DROP TABLE IF EXISTS ams_contract_change;
CREATE TABLE ams_contract_change (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '变更ID',
    tenant_id BIGINT NOT NULL COMMENT '租户ID',
    contract_id BIGINT NOT NULL COMMENT '原合同ID',
    change_no VARCHAR(50) NOT NULL COMMENT '变更单号',
    change_type TINYINT NOT NULL COMMENT '变更类型：1-续约 2-转租 3-提前退租',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0-待审核 1-已通过 2-已驳回 3-已执行',
    
    -- 续约相关
    new_start_date DATE COMMENT '新开始日期（续约时）',
    new_end_date DATE COMMENT '新结束日期（续约时）',
    new_monthly_rent DECIMAL(10,2) COMMENT '新月租金（续约时）',
    
    -- 转租相关
    new_user_id BIGINT COMMENT '新租客ID（转租时）',
    transfer_fee DECIMAL(10,2) COMMENT '转租手续费（转租时）',
    
    -- 提前退租相关
    terminate_date DATE COMMENT '退租日期（提前退租时）',
    penalty_amount DECIMAL(10,2) COMMENT '违约金（提前退租时）',
    deposit_refund DECIMAL(10,2) COMMENT '押金退还金额（提前退租时）',
    
    reason VARCHAR(500) COMMENT '变更原因',
    applicant_id BIGINT NOT NULL COMMENT '申请人ID',
    approver_id BIGINT COMMENT '审批人ID',
    approve_time DATETIME COMMENT '审批时间',
    approve_remark VARCHAR(500) COMMENT '审批意见',
    
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记：0-未删除 1-已删除',
    
    UNIQUE INDEX uk_change_no (change_no),
    INDEX idx_tenant_id (tenant_id),
    INDEX idx_contract_id (contract_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='合同变更申请表';

-- 合同变更历史表
DROP TABLE IF EXISTS ams_contract_change_history;
CREATE TABLE ams_contract_change_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
    contract_id BIGINT NOT NULL COMMENT '合同ID',
    change_id BIGINT COMMENT '变更ID',
    action_type VARCHAR(50) NOT NULL COMMENT '操作类型：CREATE-创建/APPROVE-审批/REJECT-驳回/EXECUTE-执行',
    action_desc VARCHAR(500) COMMENT '操作描述',
    operator_id BIGINT NOT NULL COMMENT '操作人ID',
    operator_name VARCHAR(50) COMMENT '操作人姓名',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_contract_id (contract_id),
    INDEX idx_change_id (change_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='合同变更历史表';
