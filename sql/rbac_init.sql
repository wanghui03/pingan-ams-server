-- ========================================
-- RBAC权限控制相关表
-- ========================================

-- 角色表
DROP TABLE IF EXISTS ams_role;
CREATE TABLE ams_role (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '角色ID',
    tenant_id BIGINT COMMENT '租户ID（NULL表示全局角色）',
    role_name VARCHAR(50) NOT NULL COMMENT '角色名称',
    role_code VARCHAR(50) NOT NULL COMMENT '角色编码',
    description VARCHAR(200) COMMENT '角色描述',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用 1-启用',
    sort_order INT NOT NULL DEFAULT 0 COMMENT '排序',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记：0-未删除 1-已删除',
    UNIQUE INDEX uk_role_code (tenant_id, role_code),
    INDEX idx_tenant_id (tenant_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- 权限表（菜单+按钮）
DROP TABLE IF EXISTS ams_permission;
CREATE TABLE ams_permission (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '权限ID',
    parent_id BIGINT DEFAULT 0 COMMENT '父权限ID（0表示顶级）',
    name VARCHAR(50) NOT NULL COMMENT '权限名称',
    code VARCHAR(100) NOT NULL COMMENT '权限编码',
    type TINYINT NOT NULL COMMENT '类型：1-菜单 2-按钮 3-接口',
    path VARCHAR(200) COMMENT '路由路径（菜单类型）',
    component VARCHAR(200) COMMENT '组件路径（菜单类型）',
    icon VARCHAR(50) COMMENT '图标（菜单类型）',
    sort_order INT NOT NULL DEFAULT 0 COMMENT '排序',
    visible TINYINT NOT NULL DEFAULT 1 COMMENT '是否可见：0-隐藏 1-显示',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用 1-启用',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记：0-未删除 1-已删除',
    UNIQUE INDEX uk_code (code),
    INDEX idx_parent_id (parent_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限表';

-- 用户角色关联表
DROP TABLE IF EXISTS ams_user_role;
CREATE TABLE ams_user_role (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE INDEX uk_user_role (user_id, role_id),
    INDEX idx_user_id (user_id),
    INDEX idx_role_id (role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

-- 角色权限关联表
DROP TABLE IF EXISTS ams_role_permission;
CREATE TABLE ams_role_permission (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    permission_id BIGINT NOT NULL COMMENT '权限ID',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE INDEX uk_role_permission (role_id, permission_id),
    INDEX idx_role_id (role_id),
    INDEX idx_permission_id (permission_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色权限关联表';

-- ========================================
-- 插入默认权限数据（菜单+按钮）
-- ========================================
INSERT INTO ams_permission (id, parent_id, name, code, type, path, component, icon, sort_order, visible) VALUES
-- 一级菜单
(1, 0, '首页', 'dashboard', 1, '/dashboard', 'dashboard/index', 'HomeFilled', 1, 1),
(2, 0, '楼栋管理', 'building', 1, '/building', 'building/index', 'OfficeBuilding', 2, 1),
(3, 0, '房间管理', 'room', 1, '/room', 'room/index', 'House', 3, 1),
(4, 0, '合同管理', 'contract', 1, '/contract', 'contract/index', 'Document', 4, 1),
(5, 0, '账单管理', 'bill', 1, '/bill', 'bill/index', 'Money', 5, 1),
(6, 0, '工单管理', 'workorder', 1, '/workorder', 'workorder/index', 'Tickets', 6, 1),
(7, 0, '租客管理', 'tenantUser', 1, '/tenant-user', 'tenant-user/index', 'User', 7, 1),
(8, 0, '员工管理', 'staff', 1, '/staff', 'staff/index', 'Avatar', 8, 1),
(9, 0, '租户管理', 'tenant', 1, '/tenant', 'tenant/index', 'Platform', 9, 1),
(10, 0, '系统管理', 'system', 1, '', '', 'Setting', 10, 1),
-- 二级菜单（系统管理下）
(11, 10, '系统配置', 'sysConfig', 1, '/config', 'config/index', 'Tools', 1, 1),
(12, 10, '操作日志', 'operationLog', 1, '/log', 'log/index', 'Notebook', 2, 1),
(13, 10, '角色管理', 'role', 1, '/role', 'role/index', 'UserFilled', 3, 1),
(14, 10, '权限管理', 'permission', 1, '/permission', 'permission/index', 'Lock', 4, 1),
-- 按钮权限（楼栋管理）
(101, 2, '新增楼栋', 'building:create', 2, '', '', '', 1, 1),
(102, 2, '编辑楼栋', 'building:update', 2, '', '', '', 2, 1),
(103, 2, '删除楼栋', 'building:delete', 2, '', '', '', 3, 1),
-- 按钮权限（房间管理）
(104, 3, '新增房间', 'room:create', 2, '', '', '', 1, 1),
(105, 3, '编辑房间', 'room:update', 2, '', '', '', 2, 1),
(106, 3, '删除房间', 'room:delete', 2, '', '', '', 3, 1),
-- 按钮权限（合同管理）
(107, 4, '新增合同', 'contract:create', 2, '', '', '', 1, 1),
(108, 4, '编辑合同', 'contract:update', 2, '', '', '', 2, 1),
(109, 4, '提交审核', 'contract:submit', 2, '', '', '', 3, 1),
(110, 4, '审核通过', 'contract:approve', 2, '', '', '', 4, 1),
(111, 4, '审核驳回', 'contract:reject', 2, '', '', '', 5, 1),
(112, 4, '终止合同', 'contract:terminate', 2, '', '', '', 6, 1),
-- 按钮权限（账单管理）
(113, 5, '新增账单', 'bill:create', 2, '', '', '', 1, 1),
(114, 5, '确认收款', 'bill:pay', 2, '', '', '', 2, 1),
(115, 5, '取消账单', 'bill:cancel', 2, '', '', '', 3, 1),
-- 按钮权限（工单管理）
(116, 6, '新增工单', 'workorder:create', 2, '', '', '', 1, 1),
(117, 6, '分配工单', 'workorder:assign', 2, '', '', '', 2, 1),
(118, 6, '处理工单', 'workorder:handle', 2, '', '', '', 3, 1),
-- 按钮权限（租客管理）
(119, 7, '新增租客', 'tenantUser:create', 2, '', '', '', 1, 1),
(120, 7, '编辑租客', 'tenantUser:update', 2, '', '', '', 2, 1),
(121, 7, '启禁用租客', 'tenantUser:status', 2, '', '', '', 3, 1),
-- 按钮权限（员工管理）
(122, 8, '新增员工', 'staff:create', 2, '', '', '', 1, 1),
(123, 8, '编辑员工', 'staff:update', 2, '', '', '', 2, 1),
(124, 8, '删除员工', 'staff:delete', 2, '', '', '', 3, 1),
(125, 8, '启禁用员工', 'staff:status', 2, '', '', '', 4, 1),
-- 按钮权限（租户管理）
(126, 9, '新增租户', 'tenant:create', 2, '', '', '', 1, 1),
(127, 9, '编辑租户', 'tenant:update', 2, '', '', '', 2, 1),
(128, 9, '删除租户', 'tenant:delete', 2, '', '', '', 3, 1),
(129, 9, '启禁用租户', 'tenant:status', 2, '', '', '', 4, 1),
-- 按钮权限（系统配置）
(130, 11, '新增配置', 'sysConfig:create', 2, '', '', '', 1, 1),
(131, 11, '编辑配置', 'sysConfig:update', 2, '', '', '', 2, 1),
(132, 11, '删除配置', 'sysConfig:delete', 2, '', '', '', 3, 1),
-- 按钮权限（角色管理）
(133, 13, '新增角色', 'role:create', 2, '', '', '', 1, 1),
(134, 13, '编辑角色', 'role:update', 2, '', '', '', 2, 1),
(135, 13, '删除角色', 'role:delete', 2, '', '', '', 3, 1),
(136, 13, '分配权限', 'role:assign', 2, '', '', '', 4, 1);

-- ========================================
-- 插入默认角色
-- ========================================
INSERT INTO ams_role (tenant_id, role_name, role_code, description, sort_order) VALUES
-- 平台层角色
(NULL, '超级管理员', 'super_admin', '平台超级管理员，拥有所有权限', 1),
(NULL, '平台运营', 'platform_operator', '负责平台日常运维、租户审核', 2),
-- 租户层角色
(NULL, '租户管理员', 'tenant_admin', '租户最高权限，管理本租户所有数据', 3),
(NULL, '店长/经理', 'store_manager', '管理单个门店/片区的楼栋和员工', 4),
(NULL, '管家', 'housekeeper', '日常服务租客，处理工单、带看房', 5),
(NULL, '财务', 'finance', '负责账单管理、收款确认、报表导出', 6),
(NULL, '维修工', 'maintenance', '处理报修工单', 7),
-- 特殊角色
(NULL, '个人房东', 'individual_landlord', '管理自己的少量房源，权限较简单', 8);

-- ========================================
-- 为超级管理员分配所有权限
-- ========================================
INSERT INTO ams_role_permission (role_id, permission_id)
SELECT 1, id FROM ams_permission;

-- ========================================
-- 为平台运营分配权限（租户管理+系统配置）
-- ========================================
INSERT INTO ams_role_permission (role_id, permission_id) VALUES
(2, 1),   -- 首页
(2, 9),   -- 租户管理
(2, 126), -- 新增租户
(2, 127), -- 编辑租户
(2, 128), -- 删除租户
(2, 129), -- 启禁用租户
(2, 10),  -- 系统管理
(2, 11),  -- 系统配置
(2, 12),  -- 操作日志
(2, 130), -- 新增配置
(2, 131), -- 编辑配置
(2, 132); -- 删除配置

-- ========================================
-- 为租户管理员分配大部分权限（排除租户管理）
-- ========================================
INSERT INTO ams_role_permission (role_id, permission_id)
SELECT 3, id FROM ams_permission WHERE code NOT LIKE 'tenant:%' AND code != 'tenant' AND code != 'platform:%';

-- ========================================
-- 为店长/经理分配权限（管理楼栋、房间、员工）
-- ========================================
INSERT INTO ams_role_permission (role_id, permission_id) VALUES
(4, 1),   -- 首页
(4, 2),   -- 楼栋管理
(4, 3),   -- 房间管理
(4, 4),   -- 合同管理
(4, 5),   -- 账单管理（查看）
(4, 6),   -- 工单管理
(4, 7),   -- 租客管理
(4, 8),   -- 员工管理
(4, 101), -- 新增楼栋
(4, 102), -- 编辑楼栋
(4, 103), -- 删除楼栋
(4, 104), -- 新增房间
(4, 105), -- 编辑房间
(4, 106), -- 删除房间
(4, 107), -- 新增合同
(4, 108), -- 编辑合同
(4, 109), -- 提交审核
(4, 110), -- 审核通过
(4, 111), -- 审核驳回
(4, 112), -- 终止合同
(4, 116), -- 新增工单
(4, 117), -- 分配工单
(4, 118), -- 处理工单
(4, 119), -- 新增租客
(4, 120), -- 编辑租客
(4, 121), -- 启禁用租客
(4, 122), -- 新增员工
(4, 123), -- 编辑员工
(4, 124), -- 删除员工
(4, 125); -- 启禁用员工

-- ========================================
-- 为管家分配基础运营权限
-- ========================================
INSERT INTO ams_role_permission (role_id, permission_id) VALUES
(5, 1),   -- 首页
(5, 2),   -- 楼栋管理（查看）
(5, 3),   -- 房间管理（查看）
(5, 4),   -- 合同管理（查看）
(5, 5),   -- 账单管理（查看）
(5, 6),   -- 工单管理
(5, 7),   -- 租客管理
(5, 107), -- 新增合同
(5, 109), -- 提交审核
(5, 113), -- 新增账单
(5, 114), -- 确认收款
(5, 116), -- 新增工单
(5, 117), -- 分配工单
(5, 118), -- 处理工单
(5, 119), -- 新增租客
(5, 120); -- 编辑租客

-- ========================================
-- 为财务人员分配财务相关权限
-- ========================================
INSERT INTO ams_role_permission (role_id, permission_id) VALUES
(6, 1),   -- 首页
(6, 4),   -- 合同管理（查看）
(6, 5),   -- 账单管理
(6, 113), -- 新增账单
(6, 114), -- 确认收款
(6, 115); -- 取消账单

-- ========================================
-- 为维修工分配工单相关权限
-- ========================================
INSERT INTO ams_role_permission (role_id, permission_id) VALUES
(7, 1),   -- 首页
(7, 6),   -- 工单管理
(7, 116), -- 新增工单
(7, 117), -- 分配工单
(7, 118); -- 处理工单

-- ========================================
-- 为个人房东分配简化权限
-- ========================================
INSERT INTO ams_role_permission (role_id, permission_id) VALUES
(8, 1),   -- 首页
(8, 2),   -- 楼栋管理
(8, 3),   -- 房间管理
(8, 4),   -- 合同管理
(8, 5),   -- 账单管理
(8, 6),   -- 工单管理
(8, 7),   -- 租客管理
(8, 101), -- 新增楼栋
(8, 102), -- 编辑楼栋
(8, 103), -- 删除楼栋
(8, 104), -- 新增房间
(8, 105), -- 编辑房间
(8, 106), -- 删除房间
(8, 107), -- 新增合同
(8, 108), -- 编辑合同
(8, 109), -- 提交审核
(8, 110), -- 审核通过
(8, 111), -- 审核驳回
(8, 112), -- 终止合同
(8, 113), -- 新增账单
(8, 114), -- 确认收款
(8, 115), -- 取消账单
(8, 116), -- 新增工单
(8, 117), -- 分配工单
(8, 118), -- 处理工单
(8, 119), -- 新增租客
(8, 120), -- 编辑租客
(8, 121); -- 启禁用租客

-- ========================================
-- 为默认管理员分配超级管理员角色
-- ========================================
INSERT INTO ams_user_role (user_id, role_id) VALUES
(1, 1),  -- superadmin -> 超级管理员
(2, 2);  -- admin -> 租户管理员
