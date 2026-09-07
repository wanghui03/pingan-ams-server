-- ============================================
-- 平安公寓管理系统 - 数据库初始化脚本
-- 版本：1.1
-- 日期：2026-09-02
-- ============================================

-- 如果数据库存在则先删除
DROP DATABASE IF EXISTS pingan_ams;

-- 创建数据库
CREATE DATABASE pingan_ams DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE pingan_ams;

-- ============================================
-- 1. 租户表（公寓运营商/房东）
-- ============================================
CREATE TABLE ams_tenant (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(100) NOT NULL COMMENT '租户名称（公司名或个人姓名）',
  type INT NOT NULL COMMENT '租户类型：1-企业 2-个人',
  contact_person VARCHAR(50) COMMENT '联系人',
  contact_phone VARCHAR(20) COMMENT '联系电话',
  email VARCHAR(100) COMMENT '邮箱',
  address VARCHAR(255) COMMENT '地址',
  status INT DEFAULT 1 COMMENT '状态：0-禁用 1-启用',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted INT DEFAULT 0 COMMENT '删除标记：0-未删除 1-已删除'
) COMMENT='租户表';

-- ============================================
-- 2. 证件表（通用，支持租户、员工、租客）
-- ============================================
CREATE TABLE ams_certificate (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  owner_type INT NOT NULL COMMENT '所有者类型：1-租户 2-员工 3-租客',
  owner_id BIGINT NOT NULL COMMENT '所有者 ID（租户 ID/员工 ID/租客 ID）',
  cert_type INT NOT NULL COMMENT '证件类型：1-营业执照 2-身份证 3-护照 4-其他',
  cert_no VARCHAR(100) NOT NULL COMMENT '证件编号',
  cert_name VARCHAR(100) NOT NULL COMMENT '证件名称',
  cert_file VARCHAR(255) DEFAULT NULL COMMENT '证件附件标识（文件 ID 或 URL）',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted INT DEFAULT 0 COMMENT '删除标记',
  UNIQUE KEY uk_owner (owner_type, owner_id)
) COMMENT='证件表';

-- ============================================
-- 3. 用户表
-- ============================================
CREATE TABLE ams_user (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  tenant_id BIGINT NOT NULL COMMENT '租户 ID',
  username VARCHAR(50) NOT NULL COMMENT '用户名',
  password VARCHAR(100) NOT NULL COMMENT '密码',
  nickname VARCHAR(50) COMMENT '昵称',
  real_name VARCHAR(50) COMMENT '真实姓名',
  phone VARCHAR(20) COMMENT '手机号',
  email VARCHAR(100) COMMENT '邮箱',
  avatar VARCHAR(255) COMMENT '头像 URL',
  user_type INT DEFAULT 2 COMMENT '用户类型：0-超级管理员 1-租户管理员 2-普通用户',
  status INT DEFAULT 1 COMMENT '状态：0-禁用 1-启用',
  last_login_time DATETIME COMMENT '最后登录时间',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted INT DEFAULT 0 COMMENT '删除标记',
  UNIQUE KEY uk_username (username)
) COMMENT='用户表';

-- ============================================
-- 4. 角色表（全局）
-- ============================================
CREATE TABLE ams_role (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  role_name VARCHAR(50) NOT NULL COMMENT '角色名称',
  role_code VARCHAR(50) NOT NULL COMMENT '角色编码',
  description VARCHAR(255) COMMENT '角色描述',
  status INT DEFAULT 1 COMMENT '状态：0-禁用 1-启用',
  sort_order INT DEFAULT 0 COMMENT '排序',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted INT DEFAULT 0 COMMENT '删除标记',
  UNIQUE KEY uk_role_code (role_code)
) COMMENT='角色表';

-- ============================================
-- 5. 权限表
-- ============================================
CREATE TABLE ams_permission (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  permission_name VARCHAR(50) NOT NULL COMMENT '权限名称',
  permission_code VARCHAR(100) NOT NULL COMMENT '权限编码',
  permission_type INT DEFAULT 1 COMMENT '权限类型：1-菜单 2-按钮 3-接口',
  parent_id BIGINT DEFAULT 0 COMMENT '父权限 ID',
  path VARCHAR(255) COMMENT '路由路径',
  component VARCHAR(255) COMMENT '组件路径',
  icon VARCHAR(50) COMMENT '图标',
  sort_order INT DEFAULT 0 COMMENT '排序',
  status INT DEFAULT 1 COMMENT '状态：0-禁用 1-启用',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_permission_code (permission_code)
) COMMENT='权限表';

-- ============================================
-- 6. 用户角色关联表
-- ============================================
CREATE TABLE ams_user_role (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL COMMENT '用户 ID',
  role_id BIGINT NOT NULL COMMENT '角色 ID',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uk_user_role (user_id, role_id)
) COMMENT='用户角色关联表';

-- ============================================
-- 7. 角色权限关联表
-- ============================================
CREATE TABLE ams_role_permission (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  role_id BIGINT NOT NULL COMMENT '角色 ID',
  permission_id BIGINT NOT NULL COMMENT '权限 ID',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uk_role_permission (role_id, permission_id)
) COMMENT='角色权限关联表';

-- ============================================
-- 8. 楼栋表
-- ============================================
CREATE TABLE ams_building (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  tenant_id BIGINT NOT NULL COMMENT '租户 ID',
  name VARCHAR(50) NOT NULL COMMENT '楼栋名称',
  address VARCHAR(255) COMMENT '地址',
  total_floors INT COMMENT '总楼层',
  total_rooms INT COMMENT '总房间数',
  description VARCHAR(500) COMMENT '描述',
  status INT DEFAULT 1 COMMENT '状态：0-禁用 1-启用',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted INT DEFAULT 0 COMMENT '删除标记'
) COMMENT='楼栋表';

-- ============================================
-- 9. 房间表
-- ============================================
CREATE TABLE ams_room (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  tenant_id BIGINT NOT NULL COMMENT '租户 ID',
  building_id BIGINT NOT NULL COMMENT '楼栋 ID',
  room_no VARCHAR(20) NOT NULL COMMENT '房间号',
  floor INT NOT NULL COMMENT '楼层',
  room_type INT COMMENT '房源类型：1-商品房 2-拆迁房 3-自建房 4-公寓',
  area DECIMAL(10,2) COMMENT '面积（平方米）',
  layout VARCHAR(20) COMMENT '户型（如：一室一厅）',
  monthly_rent DECIMAL(10,2) COMMENT '月租金',
  deposit DECIMAL(10,2) COMMENT '押金',
  water_price DECIMAL(10,2) DEFAULT NULL COMMENT '水费单价（元/吨）',
  electricity_price DECIMAL(10,2) DEFAULT NULL COMMENT '电费单价（元/度）',
  last_water_reading DECIMAL(10,2) DEFAULT 0 COMMENT '上次水表读数',
  last_electricity_reading DECIMAL(10,2) DEFAULT 0 COMMENT '上次电表读数',
  orientation VARCHAR(10) COMMENT '朝向',
  address VARCHAR(255) COMMENT '详细地址',
  description TEXT COMMENT '描述',
  status INT DEFAULT 0 COMMENT '状态：0-空置 1-已预订 2-已入住 3-维修中',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted INT DEFAULT 0 COMMENT '删除标记',
  UNIQUE KEY uk_building_room (building_id, room_no)
) COMMENT='房间表';

-- ============================================
-- 10. 租客表
-- ============================================
CREATE TABLE ams_tenant_user (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  tenant_id BIGINT NOT NULL COMMENT '租户 ID',
  name VARCHAR(50) NOT NULL COMMENT '姓名',
  phone VARCHAR(20) NOT NULL COMMENT '手机号',
  id_card VARCHAR(20) COMMENT '身份证号',
  emergency_contact VARCHAR(50) COMMENT '紧急联系人',
  emergency_phone VARCHAR(20) COMMENT '紧急联系人电话',
  occupation VARCHAR(50) COMMENT '职业',
  remark VARCHAR(500) COMMENT '备注',
  status INT DEFAULT 1 COMMENT '状态：0-禁用 1-启用',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted INT DEFAULT 0 COMMENT '删除标记',
  UNIQUE KEY uk_phone (phone)
) COMMENT='租客表';

-- ============================================
-- 11. 合同表
-- ============================================
CREATE TABLE ams_contract (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  tenant_id BIGINT NOT NULL COMMENT '租户 ID',
  contract_no VARCHAR(50) NOT NULL COMMENT '合同编号',
  user_id BIGINT NOT NULL COMMENT '租客 ID',
  room_id BIGINT NOT NULL COMMENT '房间 ID',
  start_date DATE NOT NULL COMMENT '开始日期',
  end_date DATE NOT NULL COMMENT '结束日期',
  monthly_rent DECIMAL(10,2) NOT NULL COMMENT '月租金',
  deposit DECIMAL(10,2) COMMENT '押金',
  payment_method INT DEFAULT 1 COMMENT '支付方式：1-月付 2-季付 3-半年付 4-年付',
  status INT DEFAULT 0 COMMENT '状态：0-待审核 1-生效中 2-已到期 3-已终止',
  sign_date DATE COMMENT '签约日期',
  remark VARCHAR(500) COMMENT '备注',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted INT DEFAULT 0 COMMENT '删除标记',
  UNIQUE KEY uk_contract_no (contract_no)
) COMMENT='合同表';

-- ============================================
-- 12. 合同变更表
-- ============================================
CREATE TABLE ams_contract_change (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  tenant_id BIGINT NOT NULL COMMENT '租户 ID',
  contract_id BIGINT NOT NULL COMMENT '合同 ID',
  change_type INT NOT NULL COMMENT '变更类型：1-续租 2-转租 3-退租 4-调价',
  change_date DATE NOT NULL COMMENT '变更日期',
  old_value TEXT COMMENT '变更前内容（JSON）',
  new_value TEXT COMMENT '变更后内容（JSON）',
  reason VARCHAR(500) COMMENT '变更原因',
  status INT DEFAULT 0 COMMENT '状态：0-待审核 1-已通过 2-已拒绝',
  operator_id BIGINT COMMENT '操作人 ID',
  operator_name VARCHAR(50) COMMENT '操作人姓名',
  remark VARCHAR(500) COMMENT '备注',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted INT DEFAULT 0 COMMENT '删除标记'
) COMMENT='合同变更表';

-- ============================================
-- 13. 账单表
-- ============================================
CREATE TABLE ams_bill (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  tenant_id BIGINT NOT NULL COMMENT '租户 ID',
  bill_no VARCHAR(50) NOT NULL COMMENT '账单编号',
  contract_id BIGINT COMMENT '合同 ID',
  user_id BIGINT NOT NULL COMMENT '租客 ID',
  room_id BIGINT NOT NULL COMMENT '房间 ID',
  bill_type INT NOT NULL COMMENT '账单类型：1-租金 2-水费 3-电费 4-押金 5-其他',
  amount DECIMAL(10,2) NOT NULL COMMENT '账单金额',
  paid_amount DECIMAL(10,2) DEFAULT 0 COMMENT '已付金额',
  bill_date DATE NOT NULL COMMENT '账单日期',
  due_date DATE NOT NULL COMMENT '到期日期',
  start_date DATE COMMENT '计费开始日期',
  end_date DATE COMMENT '计费结束日期',
  status INT DEFAULT 0 COMMENT '状态：0-待支付 1-已支付 2-已逾期 3-已取消',
  payment_date DATETIME COMMENT '支付时间',
  payment_method INT COMMENT '支付方式：1-现金 2-微信 3-支付宝 4-银行转账',
  transaction_no VARCHAR(100) COMMENT '交易流水号',
  remark VARCHAR(500) COMMENT '备注',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted INT DEFAULT 0 COMMENT '删除标记',
  UNIQUE KEY uk_bill_no (bill_no)
) COMMENT='账单表';

-- ============================================
-- 14. 抄表记录表
-- ============================================
CREATE TABLE ams_meter_reading (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  tenant_id BIGINT NOT NULL COMMENT '租户 ID',
  room_id BIGINT NOT NULL COMMENT '房间 ID',
  meter_type INT NOT NULL COMMENT '抄表类型：1-水表 2-电表',
  previous_reading DECIMAL(10,2) NOT NULL COMMENT '上次读数',
  current_reading DECIMAL(10,2) NOT NULL COMMENT '本次读数',
  meter_usage DECIMAL(10,2) NOT NULL COMMENT '用量',
  unit_price DECIMAL(10,2) NOT NULL COMMENT '单价',
  amount DECIMAL(10,2) NOT NULL COMMENT '费用',
  reading_date DATE NOT NULL COMMENT '抄表日期',
  bill_generated INT DEFAULT 0 COMMENT '是否已生成账单：0-否 1-是',
  bill_id BIGINT DEFAULT NULL COMMENT '关联账单 ID',
  operator_id BIGINT DEFAULT NULL COMMENT '操作人 ID',
  operator_name VARCHAR(50) DEFAULT NULL COMMENT '操作人姓名',
  remark VARCHAR(255) DEFAULT NULL COMMENT '备注',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted INT DEFAULT 0 COMMENT '删除标记',
  INDEX idx_tenant_id (tenant_id),
  INDEX idx_room_id (room_id),
  INDEX idx_reading_date (reading_date)
) COMMENT='抄表记录表';

-- ============================================
-- 15. 工单表
-- ============================================
CREATE TABLE ams_work_order (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  tenant_id BIGINT NOT NULL COMMENT '租户 ID',
  order_no VARCHAR(50) NOT NULL COMMENT '工单编号',
  user_id BIGINT NOT NULL COMMENT '报修人 ID',
  room_id BIGINT NOT NULL COMMENT '房间 ID',
  title VARCHAR(100) NOT NULL COMMENT '工单标题',
  category INT COMMENT '报修分类：1-水电 2-家具 3-家电 4-其他',
  description TEXT COMMENT '问题描述',
  images TEXT COMMENT '图片 URL 列表（JSON 数组）',
  priority INT DEFAULT 2 COMMENT '优先级：1-紧急 2-普通 3-低',
  status INT DEFAULT 0 COMMENT '状态：0-待处理 1-处理中 2-已完成 3-已关闭',
  assignee_id BIGINT COMMENT '处理人 ID',
  assignee_name VARCHAR(50) COMMENT '处理人姓名',
  complete_time DATETIME COMMENT '完成时间',
  result TEXT COMMENT '处理结果',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted INT DEFAULT 0 COMMENT '删除标记',
  UNIQUE KEY uk_order_no (order_no)
) COMMENT='工单表';

-- ============================================
-- 16. 通知表
-- ============================================
CREATE TABLE ams_notification (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  tenant_id BIGINT NOT NULL COMMENT '租户 ID',
  user_id BIGINT NOT NULL COMMENT '接收用户 ID',
  title VARCHAR(100) NOT NULL COMMENT '通知标题',
  content TEXT COMMENT '通知内容',
  type INT DEFAULT 1 COMMENT '通知类型：1-系统通知 2-账单通知 3-工单通知 4-合同通知',
  biz_type VARCHAR(50) COMMENT '业务类型',
  biz_id BIGINT COMMENT '业务 ID',
  is_read INT DEFAULT 0 COMMENT '是否已读：0-未读 1-已读',
  read_time DATETIME COMMENT '阅读时间',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted INT DEFAULT 0 COMMENT '删除标记',
  INDEX idx_user_read (user_id, is_read)
) COMMENT='通知表';

-- ============================================
-- 17. 系统配置表（全局）
-- ============================================
CREATE TABLE ams_sys_config (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  config_key VARCHAR(100) NOT NULL COMMENT '配置键',
  config_value VARCHAR(500) NOT NULL COMMENT '配置值',
  description VARCHAR(255) COMMENT '配置描述',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_config_key (config_key)
) COMMENT='系统配置表';

-- ============================================
-- 18. 字典表
-- ============================================
CREATE TABLE ams_dict (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  dict_type VARCHAR(50) NOT NULL COMMENT '字典类型',
  dict_code VARCHAR(50) NOT NULL COMMENT '字典编码',
  dict_name VARCHAR(100) NOT NULL COMMENT '字典名称',
  sort_order INT DEFAULT 0 COMMENT '排序',
  status INT DEFAULT 1 COMMENT '状态：0-禁用 1-启用',
  remark VARCHAR(255) DEFAULT NULL COMMENT '备注',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_type_code (dict_type, dict_code),
  INDEX idx_dict_type (dict_type)
) COMMENT='字典表';

-- ============================================
-- 19. 操作日志表
-- ============================================
CREATE TABLE ams_operation_log (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  tenant_id BIGINT COMMENT '租户 ID',
  user_id BIGINT COMMENT '操作用户 ID',
  username VARCHAR(50) COMMENT '用户名',
  module VARCHAR(50) COMMENT '模块',
  operation VARCHAR(50) COMMENT '操作',
  method VARCHAR(200) COMMENT '请求方法',
  params TEXT COMMENT '请求参数',
  ip VARCHAR(50) COMMENT 'IP 地址',
  status INT DEFAULT 1 COMMENT '状态：0-失败 1-成功',
  error_msg TEXT COMMENT '错误信息',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) COMMENT='操作日志表';
