# 平安公寓管理系统 - 后端服务

## 项目结构

```
pingan-ams-server/
├── pingan-ams-common/          # 公共模块
│   ├── constant/               # 常量定义
│   ├── exception/              # 异常处理
│   ├── result/                 # 统一返回结果
│   └── utils/                  # 工具类（JWT、安全等）
├── pingan-ams-model/           # 实体模型模块
│   ├── dto/                    # 数据传输对象（7个）
│   ├── entity/                 # 数据库实体（7个）
│   ├── enums/                  # 枚举类（5个）
│   └── vo/                     # 视图对象（7个）
├── pingan-ams-service/         # 业务服务模块
│   ├── config/                 # 配置类（MyBatis Plus、JWT拦截器等）
│   ├── mapper/                 # 数据访问层（7个）
│   └── service/                # 业务接口及实现（5个模块）
├── pingan-ams-admin/           # 管理后台API模块
│   ├── controller/             # 控制器（8个）
│   └── resources/              # 配置文件
└── sql/                        # 数据库脚本
```

## 技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| Spring Boot | 3.2.0 | 基础框架 |
| JDK | 17 | 运行环境 |
| MyBatis Plus | 3.5.5 | ORM框架 |
| MySQL | 8.0 | 数据库 |
| Redis | 6.0+ | 缓存 |
| Druid | 1.2.20 | 连接池 |
| Knife4j | 4.3.0 | API文档 |
| JWT | 0.12.3 | 认证授权 |
| Hutool | 5.8.24 | 工具库 |

## 快速开始

### 1. 环境要求

- JDK 17+
- MySQL 8.0+
- Redis 6.0+
- Maven 3.6+

### 2. 初始化数据库

```bash
# 执行初始化脚本
mysql -u root -p < sql/init.sql
```

### 3. 修改配置

编辑 `pingan-ams-admin/src/main/resources/application.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/pingan_ams?...
    username: your_username
    password: your_password
  data:
    redis:
      host: localhost
      port: 6379
```

### 4. 启动项目

```bash
# 编译项目
cd pingan-ams-server
mvn clean install

# 启动管理后台
cd pingan-ams-admin
mvn spring-boot:run
```

### 5. 访问接口文档

启动成功后访问: http://localhost:8080/api/doc.html

## 已实现模块

### 1. 认证模块

| 接口 | 方法 | 说明 |
|------|------|------|
| /api/auth/login | POST | 微信登录 |
| /api/auth/logout | POST | 退出登录 |

### 2. 用户模块

| 接口 | 方法 | 说明 |
|------|------|------|
| /api/user/info | GET | 获取当前用户信息 |
| /api/user/info | PUT | 更新用户信息 |
| /api/user/auth | POST | 实名认证 |

### 3. 楼栋模块

| 接口 | 方法 | 说明 |
|------|------|------|
| /api/building | POST | 创建楼栋 |
| /api/building/{id} | PUT | 更新楼栋 |
| /api/building/{id} | GET | 获取楼栋详情 |
| /api/building/list | GET | 分页查询楼栋 |
| /api/building/all | GET | 获取所有楼栋 |
| /api/building/{id} | DELETE | 删除楼栋 |

### 4. 房间模块

| 接口 | 方法 | 说明 |
|------|------|------|
| /api/room | POST | 创建房间 |
| /api/room/{id} | PUT | 更新房间 |
| /api/room/{id} | GET | 获取房间详情 |
| /api/room/list | GET | 分页查询房间 |
| /api/room/{id} | DELETE | 删除房间 |

### 5. 合同模块

| 接口 | 方法 | 说明 |
|------|------|------|
| /api/contract | POST | 创建合同 |
| /api/contract/{id} | PUT | 更新合同 |
| /api/contract/{id} | GET | 获取合同详情 |
| /api/contract/list | GET | 分页查询合同 |
| /api/contract/{id}/terminate | PUT | 终止合同 |
| /api/contract/{id}/generate-bills | POST | 根据合同生成账单 |

### 6. 账单模块

| 接口 | 方法 | 说明 |
|------|------|------|
| /api/bill | POST | 创建账单 |
| /api/bill/{id} | GET | 获取账单详情 |
| /api/bill/list | GET | 分页查询账单 |
| /api/bill/{id}/pay | PUT | 标记账单已支付 |
| /api/bill/{id}/cancel | PUT | 取消账单 |
| /api/bill/unpaid-amount | GET | 获取待支付总金额 |
| /api/bill/generate-rent-bills | POST | 根据合同生成租金账单 |

### 7. 工单模块

| 接口 | 方法 | 说明 |
|------|------|------|
| /api/work-order | POST | 创建工单 |
| /api/work-order/{id} | GET | 获取工单详情 |
| /api/work-order/list | GET | 分页查询工单 |
| /api/work-order/{id}/assign | PUT | 分配工单 |
| /api/work-order/{id}/handle | PUT | 处理工单 |
| /api/work-order/{id}/rate | PUT | 评价工单 |

### 8. 系统模块

| 接口 | 方法 | 说明 |
|------|------|------|
| /api/system/health | GET | 健康检查 |

## 核心功能

### 多租户数据隔离

所有业务表都包含 `tenant_id` 字段，所有查询都带上租户ID，确保数据完全隔离。

### JWT 认证

1. 前端调用 `/api/auth/login` 获取 Token
2. 后续请求在 Header 中携带 `Authorization: Bearer <token>`
3. JWT拦截器验证 Token 并设置当前用户信息

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
| 1xxx | 认证相关 |
| 2xxx | 参数校验 |
| 3xxx | 业务相关 |
| 4xxx | 第三方服务 |

## 配置说明

```yaml
pingan:
  ams:
    # 实名认证是否开启人脸识别
    face-auth-enabled: false
    # JWT密钥
    jwt-secret: your-secret-key
    # Token有效期（天）
    token-expire-days: 7
```

## 待开发功能

- [ ] 微信API对接（获取openid、手机号等）
- [ ] 腾讯电子签对接
- [ ] 文件上传（OSS）
- [ ] 消息推送（微信模板消息）
- [ ] 数据统计报表
- [ ] 智能硬件对接（预留接口）
- [ ] 短信验证码
- [ ] 合同审批流程

## 开发规范

1. **统一返回格式**: 使用 `Result<T>` 包装返回结果
2. **异常处理**: 业务异常使用 `BusinessException`
3. **参数校验**: 使用 `@Valid` + JSR303 注解
4. **分页查询**: 使用 MyBatis Plus 分页插件
5. **逻辑删除**: 所有表使用 `deleted` 字段标记删除
6. **多租户隔离**: 所有查询必须带上 `tenantId`
