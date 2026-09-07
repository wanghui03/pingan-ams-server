-- ============================================
-- 平安公寓管理系统 - 初始化数据脚本
-- 版本: 1.0
-- 日期: 2026-09-02
-- ============================================

USE pingan_ams;

-- ============================================
-- 1. 系统配置数据
-- ============================================
INSERT INTO ams_sys_config (config_key, config_value, description) VALUES
('permission_enabled', 'false', '是否开启权限控制'),
('system_name', '平安公寓管理系统', '系统名称'),
('system_logo', '/images/logo.png', '系统Logo'),
('default_password', '123456', '默认密码'),
('bill_reminder_days', '3', '账单到期提前提醒天数'),
('contract_reminder_days', '30', '合同到期提前提醒天数'),
('upload_max_size', '10', '上传文件最大大小(MB)'),
('upload_file_types', 'jpg,jpeg,png,gif,pdf,doc,docx,xls,xlsx', '允许上传的文件类型');

-- ============================================
-- 2. 角色数据
-- ============================================
INSERT INTO ams_role (role_name, role_code, description, sort_order) VALUES
('超级管理员', 'super_admin', '系统超级管理员，拥有所有权限', 1),
('租户管理员', 'tenant_admin', '租户管理员，管理本租户的所有业务', 2),
('财务人员', 'finance', '负责账单、收款等财务相关工作', 3),
('运营人员', 'operator', '负责房源、合同、工单等运营工作', 4),
('普通员工', 'staff', '普通员工，拥有基础操作权限', 5);

-- ============================================
-- 3. 权限数据
-- ============================================
INSERT INTO ams_permission (permission_name, permission_code, permission_type, parent_id, path, icon, sort_order) VALUES
-- 系统管理
('系统管理', 'system', 1, 0, '/system', 'Setting', 1),
('用户管理', 'system:user', 1, 1, '/system/user', 'User', 1),
('用户查看', 'system:user:view', 2, 2, NULL, NULL, 1),
('用户创建', 'system:user:create', 2, 2, NULL, NULL, 2),
('用户编辑', 'system:user:edit', 2, 2, NULL, NULL, 3),
('用户删除', 'system:user:delete', 2, 2, NULL, NULL, 4),
('角色管理', 'system:role', 1, 1, '/system/role', 'UserFilled', 2),
('角色查看', 'system:role:view', 2, 7, NULL, NULL, 1),
('角色创建', 'system:role:create', 2, 7, NULL, NULL, 2),
('角色编辑', 'system:role:edit', 2, 7, NULL, NULL, 3),
('角色删除', 'system:role:delete', 2, 7, NULL, NULL, 4),
('权限管理', 'system:permission', 1, 1, '/system/permission', 'Lock', 3),
('系统配置', 'system:config', 1, 1, '/system/config', 'Tools', 4),
('配置查看', 'system:config:view', 2, 14, NULL, NULL, 1),
('配置编辑', 'system:config:edit', 2, 14, NULL, NULL, 2),
('字典管理', 'system:dict', 1, 1, '/system/dict', 'Collection', 5),
('字典查看', 'dict:view', 2, 17, NULL, NULL, 1),
('字典创建', 'dict:create', 2, 17, NULL, NULL, 2),
('字典编辑', 'dict:edit', 2, 17, NULL, NULL, 3),
('字典删除', 'dict:delete', 2, 17, NULL, NULL, 4),
('操作日志', 'system:log', 1, 1, '/system/log', 'Document', 6),

-- 租户管理
('租户管理', 'tenant', 1, 0, '/tenant', 'OfficeBuilding', 2),
('租户查看', 'tenant:view', 2, 23, NULL, NULL, 1),
('租户创建', 'tenant:create', 2, 23, NULL, NULL, 2),
('租户编辑', 'tenant:edit', 2, 23, NULL, NULL, 3),
('租户删除', 'tenant:delete', 2, 23, NULL, NULL, 4),
('租户证件', 'tenant:certificate', 1, 23, '/tenant/certificate', 'Tickets', 5),
('证件查看', 'tenant:certificate:view', 2, 28, NULL, NULL, 1),
('证件编辑', 'tenant:certificate:edit', 2, 28, NULL, NULL, 2),
('证件删除', 'tenant:certificate:delete', 2, 28, NULL, NULL, 3),

-- 房源管理
('房源管理', 'property', 1, 0, '/property', 'House', 3),
('楼栋管理', 'property:building', 1, 32, '/property/building', 'OfficeBuilding', 1),
('楼栋查看', 'building:view', 2, 33, NULL, NULL, 1),
('楼栋创建', 'building:create', 2, 33, NULL, NULL, 2),
('楼栋编辑', 'building:edit', 2, 33, NULL, NULL, 3),
('楼栋删除', 'building:delete', 2, 33, NULL, NULL, 4),
('房间管理', 'property:room', 1, 32, '/property/room', 'Key', 2),
('房间查看', 'room:view', 2, 38, NULL, NULL, 1),
('房间创建', 'room:create', 2, 38, NULL, NULL, 2),
('房间编辑', 'room:edit', 2, 38, NULL, NULL, 3),
('房间删除', 'room:delete', 2, 38, NULL, NULL, 4),

-- 租客管理
('租客管理', 'tenant_user', 1, 0, '/tenant-user', 'Avatar', 4),
('租客查看', 'tenant_user:view', 2, 43, NULL, NULL, 1),
('租客创建', 'tenant_user:create', 2, 43, NULL, NULL, 2),
('租客编辑', 'tenant_user:edit', 2, 43, NULL, NULL, 3),
('租客删除', 'tenant_user:delete', 2, 43, NULL, NULL, 4),

-- 合同管理
('合同管理', 'contract', 1, 0, '/contract', 'Document', 5),
('合同查看', 'contract:view', 2, 48, NULL, NULL, 1),
('合同创建', 'contract:create', 2, 48, NULL, NULL, 2),
('合同编辑', 'contract:edit', 2, 48, NULL, NULL, 3),
('合同删除', 'contract:delete', 2, 48, NULL, NULL, 4),
('合同审核', 'contract:audit', 2, 48, NULL, NULL, 5),
('合同变更', 'contract:change', 2, 48, NULL, NULL, 6),

-- 账单管理
('账单管理', 'bill', 1, 0, '/bill', 'Money', 6),
('账单查看', 'bill:view', 2, 55, NULL, NULL, 1),
('账单创建', 'bill:create', 2, 55, NULL, NULL, 2),
('账单编辑', 'bill:edit', 2, 55, NULL, NULL, 3),
('账单删除', 'bill:delete', 2, 55, NULL, NULL, 4),
('账单收款', 'bill:pay', 2, 55, NULL, NULL, 5),
('抄表管理', 'meter-reading', 1, 55, '/meter-reading', 'Odometer', 7),
('抄表查看', 'meter-reading:view', 2, 62, NULL, NULL, 1),
('抄表创建', 'meter-reading:create', 2, 62, NULL, NULL, 2),
('生成账单', 'meter-reading:generate-bill', 2, 62, NULL, NULL, 3),

-- 工单管理
('工单管理', 'workorder', 1, 0, '/workorder', 'Tools', 7),
('工单查看', 'workorder:view', 2, 66, NULL, NULL, 1),
('工单创建', 'workorder:create', 2, 66, NULL, NULL, 2),
('工单编辑', 'workorder:edit', 2, 66, NULL, NULL, 3),
('工单分配', 'workorder:assign', 2, 66, NULL, NULL, 4),

-- 通知管理
('通知管理', 'notification', 1, 0, '/notification', 'Bell', 8),
('通知查看', 'notification:view', 2, 71, NULL, NULL, 1),
('通知创建', 'notification:create', 2, 71, NULL, NULL, 2);

-- ============================================
-- 4. 字典数据
-- ============================================
-- 房间状态
INSERT INTO ams_dict (dict_type, dict_code, dict_name, sort_order) VALUES
('room_status', '0', '空置', 1),
('room_status', '1', '已预订', 2),
('room_status', '2', '已入住', 3),
('room_status', '3', '维修中', 4);

-- 合同状态
INSERT INTO ams_dict (dict_type, dict_code, dict_name, sort_order) VALUES
('contract_status', '0', '待审核', 1),
('contract_status', '1', '生效中', 2),
('contract_status', '2', '已到期', 3),
('contract_status', '3', '已终止', 4);

-- 账单类型
INSERT INTO ams_dict (dict_type, dict_code, dict_name, sort_order) VALUES
('bill_type', '1', '租金', 1),
('bill_type', '2', '水费', 2),
('bill_type', '3', '电费', 3),
('bill_type', '4', '押金', 4),
('bill_type', '5', '其他', 5);

-- 账单状态
INSERT INTO ams_dict (dict_type, dict_code, dict_name, sort_order) VALUES
('bill_status', '0', '待支付', 1),
('bill_status', '1', '已支付', 2),
('bill_status', '2', '已逾期', 3),
('bill_status', '3', '已取消', 4);

-- 支付方式
INSERT INTO ams_dict (dict_type, dict_code, dict_name, sort_order) VALUES
('payment_method', '1', '月付', 1),
('payment_method', '2', '季付', 2),
('payment_method', '3', '半年付', 3),
('payment_method', '4', '年付', 4);

-- 账单支付方式
INSERT INTO ams_dict (dict_type, dict_code, dict_name, sort_order) VALUES
('payment_type', '1', '现金', 1),
('payment_type', '2', '微信', 2),
('payment_type', '3', '支付宝', 3),
('payment_type', '4', '银行转账', 4);

-- 工单状态
INSERT INTO ams_dict (dict_type, dict_code, dict_name, sort_order) VALUES
('workorder_status', '0', '待处理', 1),
('workorder_status', '1', '处理中', 2),
('workorder_status', '2', '已完成', 3),
('workorder_status', '3', '已关闭', 4);

-- 工单分类
INSERT INTO ams_dict (dict_type, dict_code, dict_name, sort_order) VALUES
('workorder_category', '1', '水电', 1),
('workorder_category', '2', '家具', 2),
('workorder_category', '3', '家电', 3),
('workorder_category', '4', '其他', 4);

-- 工单优先级
INSERT INTO ams_dict (dict_type, dict_code, dict_name, sort_order) VALUES
('workorder_priority', '1', '紧急', 1),
('workorder_priority', '2', '普通', 2),
('workorder_priority', '3', '低', 3);

-- 房源类型
INSERT INTO ams_dict (dict_type, dict_code, dict_name, sort_order) VALUES
('room_type', '1', '商品房', 1),
('room_type', '2', '拆迁房', 2),
('room_type', '3', '自建房', 3),
('room_type', '4', '公寓', 4);

-- 租户类型
INSERT INTO ams_dict (dict_type, dict_code, dict_name, sort_order) VALUES
('tenant_type', '1', '企业', 1),
('tenant_type', '2', '个人', 2);

-- 证件类型
INSERT INTO ams_dict (dict_type, dict_code, dict_name, sort_order) VALUES
('cert_type', '1', '营业执照', 1),
('cert_type', '2', '身份证', 2);

-- 通知类型
INSERT INTO ams_dict (dict_type, dict_code, dict_name, sort_order) VALUES
('notification_type', '1', '系统通知', 1),
('notification_type', '2', '账单通知', 2),
('notification_type', '3', '工单通知', 3),
('notification_type', '4', '合同通知', 4);

-- 合同变更类型
INSERT INTO ams_dict (dict_type, dict_code, dict_name, sort_order) VALUES
('contract_change_type', '1', '续租', 1),
('contract_change_type', '2', '转租', 2),
('contract_change_type', '3', '退租', 3),
('contract_change_type', '4', '调价', 4);

-- 抄表类型
INSERT INTO ams_dict (dict_type, dict_code, dict_name, sort_order) VALUES
('meter_type', '1', '水表', 1),
('meter_type', '2', '电表', 2);

-- ============================================
-- 5. 初始管理员账号
-- ============================================
-- 插入租户（超级管理员所属）
INSERT INTO ams_tenant (name, type, contact_person, contact_phone, status) VALUES
('系统管理员', 2, '管理员', '13800138000', 1);

-- 插入超级管理员账号（密码: admin123）
INSERT INTO ams_user (tenant_id, username, password, nickname, real_name, phone, user_type, status) VALUES
(1, 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '超级管理员', '管理员', '13800138000', 0, 1);

-- 为超级管理员分配角色
INSERT INTO ams_user_role (user_id, role_id) VALUES (1, 1);

-- 为超级管理员角色分配所有权限
INSERT INTO ams_role_permission (role_id, permission_id)
SELECT 1, id FROM ams_permission;

-- ============================================
-- 6. 角色权限分配
-- ============================================
-- 租户管理员权限（除系统管理外的所有权限）
INSERT INTO ams_role_permission (role_id, permission_id)
SELECT 2, id FROM ams_permission WHERE permission_code NOT LIKE 'system:%' AND permission_code != 'system';

-- 财务人员权限
INSERT INTO ams_role_permission (role_id, permission_id)
SELECT 3, id FROM ams_permission WHERE permission_code IN (
  'bill', 'bill:view', 'bill:create', 'bill:edit', 'bill:pay',
  'meter-reading', 'meter-reading:view', 'meter-reading:create', 'meter-reading:generate-bill',
  'contract', 'contract:view',
  'tenant_user', 'tenant_user:view',
  'notification', 'notification:view'
);

-- 运营人员权限
INSERT INTO ams_role_permission (role_id, permission_id)
SELECT 4, id FROM ams_permission WHERE permission_code IN (
  'property', 'property:building', 'property:room',
  'building:view', 'building:create', 'building:edit',
  'room:view', 'room:create', 'room:edit',
  'tenant_user', 'tenant_user:view', 'tenant_user:create', 'tenant_user:edit',
  'contract', 'contract:view', 'contract:create', 'contract:edit', 'contract:audit',
  'workorder', 'workorder:view', 'workorder:create', 'workorder:edit', 'workorder:assign',
  'notification', 'notification:view', 'notification:create'
);

-- 普通员工权限
INSERT INTO ams_role_permission (role_id, permission_id)
SELECT 5, id FROM ams_permission WHERE permission_code IN (
  'room:view',
  'tenant_user:view',
  'contract:view',
  'bill:view',
  'workorder:view', 'workorder:create',
  'notification', 'notification:view'
);
