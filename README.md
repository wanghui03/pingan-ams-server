# 平安公寓管理系统 - 后端服务

## 项目简介

平安公寓管理系统是一个多租户SaaS化公寓运营平台，支持专业公寓运营商和个人房东使用。系统采用前后端分离架构，后端基于 Spring Boot 3.2.0 + JDK 17 构建。

## 项目结构

```
pingan-ams-server/
├── pingan-ams-common/          # 公共模块
│   ├── constant/               # 常量定义
│   ├── exception/              # 异常处理
│   ├── result/                 # 统一返回结果
│   └── utils/                  # 工具类（JWT、密码加密等）
├── pingan-ams-model/           # 实体模型模块
│   ├── dto/                    # 数据传输对象（9个）
│   ├── entity/                 # 数据库实体（7个）
│   ├── enums/                  # 枚举类（5个）
│   └── vo/                     # 视图对象（8个）
├── pingan-ams-service/         # 业务服务模块
│   ├── config/                 # 配置类（MyBatis Plus、JWT拦截器、密码编码器等）
│   ├── mapper/                 # 数据访问层（7个）
│   └── service/                # 业务接口及实现（7个模块）
├── pingan-ams-admin/           # 管理后台API模块
│   ├── controller/             # 控制器（9个）
│   └── resources/              # 配置文件
└── sql/                        # 数据库脚本
    ├── init.sql                # 初始化脚本（含默认数据）
    └── fix_admin_password.sql  # 密码修复脚本
```

## 技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| Spring Boot | 3.2.0 | 基础框架 |
| JDK | 17 | 运行环境（必须） |
| MyBatis Plus | 3.5.5 | ORM框架 |
| MySQL | 8.0 | 数据库 |
| Redis | 6.0+ | 缓存（可选） |
| Druid | 1.2.20 | 连接池 |
| Knife4j | 4.3.0 | API文档 |
| JWT (jjwt) | 0.12.3 | 认证授权 |
| Spring Security Crypto | 6.x | BCrypt密码加密 |
| Hutool | 5.8.24 | 工具库 |

## 快速开始

### 1. 环境要求

- **JDK 17+**（必须，不支持JDK 8）
- MySQL 8.0+
- Maven 3.6+
- Redis 6.0+（可选）

### 2. 初始化数据库

```bash
# 连接MySQL并执行初始化脚本
mysql -u root -p < sql/init.sql
```

初始化脚本会创建：
- 数据库 `pingan_ams`
- 7张核心业务表
- 默认租户（ID=1）
- 超级管理员账号：`superadmin / admin123`
- 租户管理员账号：`admin / admin123`

### 3. 修改配置

编辑 `pingan-ams-admin/src/main/resources/application.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/pingan_ams?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
    username: root
    password: your_password
```

### 4. 启动项目

**方式一：IDEA启动（推荐）**

1. 用IDEA打开项目
2. 配置 Project SDK 为 JDK 17
3. 运行 `PingAnAmsAdminApplication` 主类

**方式二：Maven命令**

```bash
# 设置JDK 17环境变量（PowerShell）
$env:JAVA_HOME = "C:\Program Files\Java\jdk-17"
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"

# 编译项目
cd pingan-ams-server
mvn clean compile -DskipTests

# 启动管理后台
mvn spring-boot:run -pl pingan-ams-admin
```

### 5. 访问接口文档

启动成功后访问: **http://localhost:8080/api/doc.html**

## 系统账号

| 账号 | 密码 | 角色 | 权限范围 |
|------|------|------|---------|
| superadmin | admin123 | 超级管理员 | 管理所有租户，平台级配置 |
| admin | admin123 | 租户管理员 | 管理本租户所有数据 |

## 已实现模块

### 1. 认证模块 ✅

| 接口 | 方法 | 说明 |
|------|------|------|
| /auth/wx/login | POST | 微信登录（小程序端，通过openid） |
| /auth/admin/login | POST | PC端登录（账号密码，BCrypt校验） |
| /auth/logout | POST | 退出登录 |

**认证流程：**
- 小程序端：微信授权码 → 获取openid → 匹配用户 → 生成Token
- PC端：username + password → BCrypt校验 → 匹配用户 → 生成Token
- 同一用户可同时拥有openid和username/password，支持多端登录

### 2. 租户管理模块 ✅（仅超级管理员）

| 接口 | 方法 | 说明 |
|------|------|------|
| /platform/tenant | POST | 创建租户 |
| /platform/tenant/{id} | PUT | 更新租户 |
| /platform/tenant/{id} | DELETE | 删除租户 |
| /platform/tenant/{id} | GET | 获取租户详情 |
| /platform/tenant/list | GET | 分页查询租户 |
| /platform/tenant/all | GET | 获取所有租户 |
| /platform/tenant/{id}/status | PUT | 启用/禁用租户 |

### 3. 用户模块 ✅

| 接口 | 方法 | 说明 |
|------|------|------|
| /user/info | GET | 获取当前用户信息 |
| /user/info | PUT | 更新用户信息 |
| /user/auth | POST | 实名认证 |

### 4. 楼栋模块 ✅

| 接口 | 方法 | 说明 |
|------|------|------|
| /building | POST | 创建楼栋 |
| /building/{id} | PUT | 更新楼栋 |
| /building/{id} | GET | 获取楼栋详情 |
| /building/list | GET | 分页查询楼栋 |
| /building/all | GET | 获取所有楼栋 |
| /building/{id} | DELETE | 删除楼栋 |

### 5. 房间模块 ✅

| 接口 | 方法 | 说明 |
|------|------|------|
| /room | POST | 创建房间 |
| /room/{id} | PUT | 更新房间 |
| /room/{id} | GET | 获取房间详情 |
| /room/list | GET | 分页查询房间（支持状态、类型筛选） |
| /room/{id} | DELETE | 删除房间 |

### 6. 合同模块 ✅

| 接口 | 方法 | 说明 |
|------|------|------|
| /contract | POST | 创建合同（自动锁定房间） |
| /contract/{id} | PUT | 更新合同 |
| /contract/{id} | GET | 获取合同详情 |
| /contract/list | GET | 分页查询合同 |
| /contract/{id}/terminate | PUT | 终止合同（释放房间） |
| /contract/{id}/generate-bills | POST | 根据合同生成账单 |

### 7. 账单模块 ✅

| 接口 | 方法 | 说明 |
|------|------|------|
| /bill | POST | 创建账单 |
| /bill/{id} | GET | 获取账单详情 |
| /bill/list | GET | 分页查询账单 |
| /bill/{id}/pay | PUT | 标记账单已支付 |
| /bill/{id}/cancel | PUT | 取消账单 |
| /bill/unpaid-amount | GET | 获取待支付总金额 |
| /bill/generate-rent-bills | POST | 根据合同生成租金账单 |

### 8. 工单模块 ✅

| 接口 | 方法 | 说明 |
|------|------|------|
| /work-order | POST | 创建工单 |
| /work-order/{id} | GET | 获取工单详情 |
| /work-order/list | GET | 分页查询工单 |
| /work-order/{id}/assign | PUT | 分配工单 |
| /work-order/{id}/handle | PUT | 处理工单 |
| /work-order/{id}/rate | PUT | 评价工单 |

### 9. 系统模块 ✅

| 接口 | 方法 | 说明 |
|------|------|------|
| /system/health | GET | 健康检查 |

## 核心功能

### 多租户数据隔离

- 所有业务表都包含 `tenant_id` 字段
- 所有查询、新增、修改、删除都带上 `tenantId`
- 基于JWT Token中的 `tenantId` 实现数据隔离
- 超级管理员（userType=0）可以管理所有租户

### 双模式认证

**小程序端登录（微信）：**
1. 小程序调用微信登录获取code
2. 后端通过code获取openid（目前为mock）
3. 通过openid匹配或创建用户
4. 生成JWT Token返回

**PC端登录（账号密码）：**
1. 前端提交username + password
2. 后端通过username查找用户
3. 使用BCrypt校验密码
4. 生成JWT Token返回

**多端登录支持：**
- 同一用户可以同时拥有openid和username/password
- 微信登录通过openid匹配用户
- 账号登录通过username匹配用户
- 两种方式匹配到同一个用户记录，数据完全一致

### JWT 认证

1. Token有效期：7天
2. Token内容：userId、tenantId、userType
3. 请求头格式：`Authorization: Bearer <token>`
4. JWT拦截器自动验证Token并设置当前用户信息

### 密码安全

- 使用BCrypt加密存储密码
- 每次加密生成不同的盐值
- 登录时比对加密后的密码

### 统一返回格式

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {},
  "timestamp": 1234567890
}
```

### 错误码规范

| 范围 | 说明 |
|------|------|
| 200 | 成功 |
| 500 | 系统错误 |
| 1001-1005 | 认证相关（未登录、Token无效、Token过期等） |
| 2001-2005 | 参数校验 |
| 3001-3005 | 业务相关（数据不存在、状态错误等） |
| 4001-4005 | 第三方服务 |

## 用户角色

| userType | 角色 | 登录方式 | 权限范围 |
|----------|------|---------|---------|
| 0 | 超级管理员 | PC端账号密码 | 管理所有租户，平台级配置 |
| 1 | 租客 | 微信小程序 | 只能看自己的数据 |
| 2 | 员工/管家 | PC端账号密码 | 管理分配的楼栋 |
| 3 | 租户管理员 | PC端账号密码 | 管理本租户所有数据 |

## 数据库表说明

| 表名 | 说明 | 关键字段 |
|------|------|----------|
| ams_tenant | 租户表 | id, name, type(1企业/2个人), status |
| ams_user | 用户表 | id, tenant_id, openid, username, password, user_type(0超管/1租客/2员工/3管理员) |
| ams_building | 楼栋表 | id, tenant_id, name, room_count, status |
| ams_room | 房间表 | id, tenant_id, building_id, room_type(1商品房/2拆迁房/3自建房/4公寓), status(0空置/1已预订/2已入住/3维修中) |
| ams_contract | 合同表 | id, tenant_id, user_id, room_id, status(0草稿/1待审核/2生效/3到期/4终止) |
| ams_bill | 账单表 | id, tenant_id, bill_type(1租金/2水电/3押金/4其他), status(0待支付/1已支付/2逾期/3取消) |
| ams_work_order | 工单表 | id, tenant_id, order_type(1报修/2投诉/3咨询/4其他), status(0待处理/1处理中/2已完成/3已关闭) |

## 待开发功能

- [ ] 微信API真实对接（替换mock openid）
- [ ] 腾讯电子签对接
- [ ] 文件上传（OSS/MinIO）
- [ ] 消息推送（微信模板消息）
- [ ] 数据统计报表（完善首页统计）
- [ ] RBAC权限控制（基于角色的菜单/按钮权限）
- [ ] 员工管理（租户管理员管理下属员工）
- [ ] 租客端小程序开发
- [ ] 合同审批流程
- [ ] 智能硬件对接（预留接口）
- [ ] 修改密码、找回密码功能

## 常见问题

### Q1: 启动时报"无效的目标发行版: 17"

**原因：** 系统默认JDK版本不是17

**解决：** 
- 方式1：设置环境变量 `JAVA_HOME=C:\Program Files\Java\jdk-17`
- 方式2：在IDEA中配置 Project SDK 为 JDK 17

### Q2: 登录时提示"密码错误"

**原因：** 数据库中密码哈希值不正确

**解决：** 执行 `sql/fix_admin_password.sql` 更新密码

### Q3: 调用接口提示"tenant_id为null"

**原因：** 请求头中没有携带Token，或Token已过期

**解决：** 
1. 先调用登录接口获取Token
2. 在请求头中添加 `Authorization: Bearer <token>`

## 开发规范

1. **统一返回格式**: 使用 `Result<T>` 包装返回结果
2. **异常处理**: 业务异常使用 `BusinessException`
3. **参数校验**: 使用 `@Valid` + JSR303 注解
4. **分页查询**: 使用 MyBatis Plus 分页插件
5. **逻辑删除**: 所有表使用 `deleted` 字段标记删除
6. **多租户隔离**: 所有查询必须带上 `tenantId`
7. **密码加密**: 使用BCrypt加密存储密码

---

**最后更新：** 2026-08-28  
**版本：** v2.0
