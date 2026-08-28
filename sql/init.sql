-- ========================================
-- 平安公寓管理系统 - 数据库初始化脚本
-- ========================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS pingan_ams DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE pingan_ams;

-- ========================================
-- 租户表
-- ========================================
DROP TABLE IF EXISTS ams_tenant;
CREATE TABLE ams_tenant (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '租户ID',
    name VARCHAR(100) NOT NULL COMMENT '租户名称',
    type TINYINT NOT NULL DEFAULT 1 COMMENT '租户类型：1-企业 2-个人',
    contact_person VARCHAR(50) COMMENT '联系人',
    contact_phone VARCHAR(20) COMMENT '联系电话',
    email VARCHAR(100) COMMENT '邮箱',
    address VARCHAR(255) COMMENT '地址',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用 1-启用',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记：0-未删除 1-已删除',
    INDEX idx_name (name),
    INDEX idx_phone (contact_phone)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='租户表';

-- ========================================
-- 用户表
-- ========================================
DROP TABLE IF EXISTS ams_user;
CREATE TABLE ams_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID',
    tenant_id BIGINT NOT NULL COMMENT '租户ID',
    openid VARCHAR(100) COMMENT '微信OpenID',
    unionid VARCHAR(100) COMMENT '微信UnionID',
    phone VARCHAR(20) COMMENT '手机号',
    nickname VARCHAR(50) COMMENT '昵称',
    avatar VARCHAR(500) COMMENT '头像',
    user_type TINYINT NOT NULL DEFAULT 1 COMMENT '用户类型：1-租客 2-员工 3-管理员',
    real_name VARCHAR(50) COMMENT '真实姓名',
    id_card VARCHAR(20) COMMENT '身份证号',
    id_card_front_url VARCHAR(500) COMMENT '身份证正面照片URL',
    id_card_back_url VARCHAR(500) COMMENT '身份证反面照片URL',
    face_photo_url VARCHAR(500) COMMENT '人脸照片URL',
    auth_status TINYINT NOT NULL DEFAULT 0 COMMENT '实名认证状态：0-未认证 1-已认证',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用 1-启用',
    last_login_time DATETIME COMMENT '最后登录时间',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记：0-未删除 1-已删除',
    UNIQUE INDEX uk_openid (openid),
    INDEX idx_tenant_id (tenant_id),
    INDEX idx_phone (phone)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- ========================================
-- 楼栋表
-- ========================================
DROP TABLE IF EXISTS ams_building;
CREATE TABLE ams_building (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '楼栋ID',
    tenant_id BIGINT NOT NULL COMMENT '租户ID',
    name VARCHAR(100) NOT NULL COMMENT '楼栋名称',
    address VARCHAR(255) COMMENT '地址',
    total_floors INT COMMENT '总楼层',
    description VARCHAR(500) COMMENT '描述',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用 1-启用',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记：0-未删除 1-已删除',
    INDEX idx_tenant_id (tenant_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='楼栋表';

-- ========================================
-- 房间表
-- ========================================
DROP TABLE IF EXISTS ams_room;
CREATE TABLE ams_room (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '房间ID',
    tenant_id BIGINT NOT NULL COMMENT '租户ID',
    building_id BIGINT COMMENT '楼栋ID',
    floor INT COMMENT '楼层',
    room_no VARCHAR(20) NOT NULL COMMENT '房间号',
    room_type TINYINT NOT NULL COMMENT '房源类型：1-商品房 2-拆迁房 3-自建房 4-公寓',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '房源状态：0-空置 1-已预订 2-已入住 3-维修中',
    area DECIMAL(10,2) COMMENT '面积（平方米）',
    layout VARCHAR(50) COMMENT '户型',
    orientation VARCHAR(20) COMMENT '朝向',
    monthly_rent DECIMAL(10,2) COMMENT '月租金',
    deposit DECIMAL(10,2) COMMENT '押金',
    facilities JSON COMMENT '配套设施',
    description TEXT COMMENT '描述',
    images JSON COMMENT '图片URLs',
    address VARCHAR(255) COMMENT '详细地址',
    longitude DECIMAL(10,6) COMMENT '经度',
    latitude DECIMAL(10,6) COMMENT '纬度',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记：0-未删除 1-已删除',
    INDEX idx_tenant_id (tenant_id),
    INDEX idx_building_id (building_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='房间表';

-- ========================================
-- 合同表
-- ========================================
DROP TABLE IF EXISTS ams_contract;
CREATE TABLE ams_contract (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '合同ID',
    tenant_id BIGINT NOT NULL COMMENT '租户ID',
    contract_no VARCHAR(50) NOT NULL COMMENT '合同编号',
    user_id BIGINT NOT NULL COMMENT '用户ID（租客）',
    room_id BIGINT NOT NULL COMMENT '房间ID',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '合同状态：0-草稿 1-待审核 2-生效中 3-已到期 4-已终止',
    start_date DATE NOT NULL COMMENT '开始日期',
    end_date DATE NOT NULL COMMENT '结束日期',
    monthly_rent DECIMAL(10,2) NOT NULL COMMENT '月租金',
    deposit DECIMAL(10,2) COMMENT '押金',
    payment_method TINYINT COMMENT '支付方式：1-月付 2-季付 3-半年付 4-年付',
    contract_file_url VARCHAR(500) COMMENT '合同文件URL',
    tencent_contract_id VARCHAR(100) COMMENT '腾讯电子签合同ID',
    sign_status TINYINT NOT NULL DEFAULT 0 COMMENT '签署状态：0-未签署 1-已签署',
    sign_time DATETIME COMMENT '签署时间',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记：0-未删除 1-已删除',
    UNIQUE INDEX uk_contract_no (contract_no),
    INDEX idx_tenant_id (tenant_id),
    INDEX idx_user_id (user_id),
    INDEX idx_room_id (room_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='合同表';

-- ========================================
-- 账单表
-- ========================================
DROP TABLE IF EXISTS ams_bill;
CREATE TABLE ams_bill (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '账单ID',
    tenant_id BIGINT NOT NULL COMMENT '租户ID',
    bill_no VARCHAR(50) NOT NULL COMMENT '账单编号',
    contract_id BIGINT NOT NULL COMMENT '合同ID',
    user_id BIGINT NOT NULL COMMENT '用户ID（租客）',
    room_id BIGINT NOT NULL COMMENT '房间ID',
    bill_type TINYINT NOT NULL COMMENT '账单类型：1-租金 2-水电费 3-押金 4-其他',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '账单状态：0-待支付 1-已支付 2-已逾期 3-已取消',
    amount DECIMAL(10,2) NOT NULL COMMENT '账单金额',
    start_date DATE COMMENT '计费开始日期',
    end_date DATE COMMENT '计费结束日期',
    due_date DATE COMMENT '应付日期',
    paid_amount DECIMAL(10,2) DEFAULT 0 COMMENT '实付金额',
    paid_time DATETIME COMMENT '支付时间',
    transaction_no VARCHAR(100) COMMENT '支付流水号',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记：0-未删除 1-已删除',
    UNIQUE INDEX uk_bill_no (bill_no),
    INDEX idx_tenant_id (tenant_id),
    INDEX idx_contract_id (contract_id),
    INDEX idx_user_id (user_id),
    INDEX idx_status (status),
    INDEX idx_due_date (due_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='账单表';

-- ========================================
-- 工单表
-- ========================================
DROP TABLE IF EXISTS ams_work_order;
CREATE TABLE ams_work_order (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '工单ID',
    tenant_id BIGINT NOT NULL COMMENT '租户ID',
    order_no VARCHAR(50) NOT NULL COMMENT '工单编号',
    user_id BIGINT NOT NULL COMMENT '用户ID（租客）',
    room_id BIGINT NOT NULL COMMENT '房间ID',
    order_type TINYINT NOT NULL COMMENT '工单类型：1-报修 2-投诉 3-咨询 4-其他',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '工单状态：0-待处理 1-处理中 2-已完成 3-已关闭',
    title VARCHAR(100) NOT NULL COMMENT '标题',
    description TEXT COMMENT '描述',
    images JSON COMMENT '图片URLs',
    handler_id BIGINT COMMENT '处理人ID',
    handle_result TEXT COMMENT '处理结果',
    handle_images JSON COMMENT '处理图片',
    handle_time DATETIME COMMENT '处理时间',
    rating TINYINT COMMENT '评价分数（1-5）',
    rating_content VARCHAR(500) COMMENT '评价内容',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记：0-未删除 1-已删除',
    UNIQUE INDEX uk_order_no (order_no),
    INDEX idx_tenant_id (tenant_id),
    INDEX idx_user_id (user_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工单表';

-- ========================================
-- 插入默认租户
-- ========================================
INSERT INTO ams_tenant (name, type, contact_person, contact_phone, status) 
VALUES ('默认租户', 1, '管理员', '13800138000', 1);
