# 外卖点餐系统 (Sky Take-out)

一个基于 **Spring Boot + MyBatis + Redis + MySQL + Vue** 的外卖订餐系统，包含**管理端**与**用户端**两套前端，实现从菜品管理、下单、支付到订单处理与数据统计的完整业务闭环。

## 项目结构

```
takeaway_order_system
├── back/                          # 后端工程
│   └── sky-take-out/
│       ├── pom.xml                # 父工程（依赖版本管理）
│       ├── sky-common/            # 公共模块：常量、异常、工具类、通用返回结果
│       ├── sky-pojo/              # 实体模块：Entity、DTO、VO
│       └── sky-server/            # 服务端模块：Controller、Service、Mapper、配置等
└── front/                         # 前端工程
    └── nginx-1.20.2/              # Nginx 及打包后的 Vue 静态资源
        └── html/sky/              # 管理端 + 用户端页面
```

## 技术栈

| 分类       | 技术                                                            |
| ---------- | --------------------------------------------------------------- |
| 后端框架   | Spring Boot 2.7.3、Spring MVC、Spring WebSocket                  |
| 持久层     | MyBatis、MySQL 8、Druid 连接池、PageHelper 分页                 |
| 缓存       | Redis (Spring Data Redis)                                       |
| 认证鉴权   | JWT (双端：管理端 token / 用户端 authentication)                 |
| 接口文档   | Knife4j (Swagger)                                               |
| 文件存储   | 阿里云 OSS                                                     |
| 支付       | 微信支付 API v3                                                  |
| 报表导出   | Apache POI                                                      |
| 其他       | Lombok、Fastjson、AspectJ (AOP 自动填充)、WebSocket             |
| 前端       | Vue（Nginx 1.20.2 承载，已配置反向代理）                        |

## 功能特性

### 管理端 (Admin)
- 员工管理：登录、增删改查、账号状态管理
- 分类管理：菜品分类的增删改查
- 菜品管理：菜品及口味的新增、修改、启售/停售、图片上传
- 套餐管理：套餐的增删改查及启售/停售
- 订单管理：订单搜索、详情、状态流转、接单/拒单/派送/完成/取消
- 数据概览：今日运营数据、订单管理等概览统计
- 报表统计：营业额统计、用户统计、订单统计、销量 Top10，并支持 Excel 导出

### 用户端 (User)
- 微信登录
- 浏览菜品 / 套餐，加入购物车
- 地址簿管理
- 提交订单、下单支付
- 历史订单与订单详情查询、再来一单
- 店铺营业状态查询

### 通用能力
- **JWT 双端认证**：管理端与用户端使用独立的密钥与令牌名，配合拦截器统一鉴权
- **AOP 自动填充**：公共字段（创建时间、更新时间、创建人、更新人）自动写入
- **WebSocket 通知**：下单后实时推送新订单提醒（`/ws/{token}`）
- **统一响应 & 异常处理**：全局 `Result` 返回结构 + 全局异常处理器

## 环境要求

- JDK 8+
- Maven 3.6+
- MySQL 8.0
- Redis 5.0+
- Nginx 1.20.2（前端，也可使用其他静态服务器）

## 快速开始

### 1. 准备数据库

创建数据库并导入表结构：

```sql
CREATE DATABASE IF NOT EXISTS sky_take_out DEFAULT CHARSET utf8mb4;
```

> 数据库表结构请参考项目提供的 SQL 初始化脚本（`employee`、`category`、`dish`、`setmeal`、`orders` 等表）。

### 2. 启动 Redis

确保 Redis 已启动，默认连接 `localhost:6379`。

### 3. 配置后端

后端配置位于 `back/sky-take-out/sky-server/src/main/resources/application.yml` 与 `application-dev.yml`，
关键配置均通过**环境变量**注入，例如：

| 环境变量               | 说明                 | 默认值                   |
| ---------------------- | -------------------- | ------------------------ |
| `DB_USERNAME`          | MySQL 用户名         | `root`                   |
| `DB_PASSWORD`          | MySQL 密码           | `123456`                 |
| `REDIS_HOST`           | Redis 地址           | `localhost`              |
| `REDIS_PORT`           | Redis 端口           | `6379`                   |
| `REDIS_PASSWORD`       | Redis 密码           | `123456`                 |
| `REDIS_DATABASE`       | Redis 库             | `5`                      |
| `OSS_ACCESS_KEY_ID`    | 阿里云 OSS KeyId     | 空                       |
| `OSS_ACCESS_KEY_SECRET`| 阿里云 OSS KeySecret | 空                       |
| `WECHAT_APPID`         | 微信小程序 AppID     | 空                       |
| `WECHAT_SECRET`        | 微信小程序 Secret    | 空                       |

### 4. 启动后端

```bash
cd back/sky-take-out
mvn clean package -DskipTests
java -jar sky-server/target/sky-server-1.0-SNAPSHOT.jar
```

或使用 IDE 直接运行 `com.sky.SkyApplication`。后端默认监听 `8080` 端口。

接口文档地址：`http://localhost:8080/doc.html`

### 5. 启动前端

```bash
cd front/nginx-1.20.2
nginx.exe
```

访问 `http://localhost`（默认 80 端口）。

> **注意**：Nginx 目录路径中**不能包含中文**，否则无法正常运行。
> Nginx 已配置反向代理，将 `/api/` 转发到后端管理端、`/user/` 转发到用户端、`/ws/` 转发到 WebSocket。

## 常用账号

| 角色   | 账号   | 密码   |
| ------ | ------ | ------ |
| 管理员 | admin  | 123456 |

> 更多账号信息请参考数据库初始化脚本。

## 接口分组

- **管理端接口**：`/admin/**`（登录除外，均需 JWT 校验）
- **用户端接口**：`/user/**`（登录除外，均需 JWT 校验）
- **文件上传**：`/admin/common/upload`
- **WebSocket**：`/ws/{token}`

## 许可证

本项目仅用于学习交流。
