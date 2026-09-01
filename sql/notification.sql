-- ========================================
-- 站内通知表
-- ========================================
DROP TABLE IF EXISTS ams_notification;
CREATE TABLE ams_notification (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '通知ID',
    tenant_id BIGINT NOT NULL COMMENT '租户ID',
    user_id BIGINT NOT NULL COMMENT '接收人ID',
    title VARCHAR(200) NOT NULL COMMENT '通知标题',
    content TEXT COMMENT '通知内容',
    type TINYINT NOT NULL COMMENT '通知类型：1-账单 2-合同 3-工单 4-系统',
    biz_type VARCHAR(50) COMMENT '关联业务类型：bill/contract/workorder',
    biz_id BIGINT COMMENT '关联业务ID',
    is_read TINYINT NOT NULL DEFAULT 0 COMMENT '是否已读：0-未读 1-已读',
    read_time DATETIME COMMENT '阅读时间',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记：0-未删除 1-已删除',
    INDEX idx_tenant_id (tenant_id),
    INDEX idx_user_id (user_id),
    INDEX idx_type (type),
    INDEX idx_is_read (is_read),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='站内通知表';
