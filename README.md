<p align="center">
  <img src="docs/images/logo.png" width="420" alt="Y-Blog Logo" />
</p>

<h1 align="center">Y-Blog · Backend</h1>

<p align="center">为博客社区的用户端、管理端与审核端提供 API、实时消息及内容治理能力。</p>

<p align="center">
  <img src="https://img.shields.io/badge/Spring_Boot-2.7.3-6db33f?logo=springboot&logoColor=white" alt="Spring Boot 2.7.3" />
  <img src="https://img.shields.io/badge/MySQL-8-4479a1?logo=mysql&logoColor=white" alt="MySQL 8" />
  <img src="https://img.shields.io/badge/License-AGPL--3.0--only-blue" alt="AGPL-3.0-only" />
</p>

<p align="center">
  <a href="https://github.com/worlt/Y-Blog-Frontend">前端仓库</a> ·
  <a href="#快速开始">快速开始</a> ·
  <a href="#配置说明">配置说明</a> ·
  <a href="SECURITY.md">安全与隐私</a>
</p>

## 项目介绍

Y-Blog 是一个前后端分离的博客社区。本仓库基于 Spring Boot 和 MyBatis，统一支持用户创作与互动、管理后台、审核工作台，以及 WebSocket 私信、缓存和第三方服务接入。

项目主要用于学习与实践，当前依赖版本沿用现有源码。生产部署前需要独立进行依赖升级、安全评估和数据合规检查，不应把本项目视作开箱即用的生产安全方案。

## 功能概览

| 模块 | 能力 |
| --- | --- |
| 身份与账户 | 用户、管理员、审核员登录，JWT 校验，邮件验证码与滑块验证码 |
| 博客内容 | 文章发布与查询、分类标签、审核状态、文章统计 |
| 社区互动 | 评论与回复、点赞/点踩、收藏、关注、分享记录、举报与申诉 |
| 即时通信 | WebSocket 消息推送、会话与消息管理、撤回处理 |
| 内容治理 | 审核记录、举报处理、敏感词过滤、XSS 清理 |
| 平台运营 | 统计接口、网站配置、账户权限、建议反馈与投票 |
| 基础设施 | Redis/Caffeine 缓存、定时任务、邮件发送、S3 兼容对象存储 |
| AI 接入 | 硅基流动兼容接口，需部署者配置 API Key |

## 技术栈

| 分类 | 实现 |
| --- | --- |
| 核心框架 | Spring Boot 2.7.3、Spring MVC |
| 数据持久化 | MyBatis、MySQL 8、Druid、PageHelper |
| 缓存 | Spring Data Redis、Lettuce、Caffeine |
| 认证与通信 | JWT、WebSocket |
| 接口文档 | Springfox / Swagger 2 |
| 文件与内容 | AWS SDK S3、Jsoup、sensitive-word |
| 工程与测试 | Maven Wrapper、Lombok、Spring Boot Test、JUnit / Mockito |

版本以 [pom.xml](pom.xml) 为准。Java 源码编译目标为 8；开发环境可使用 JDK 17，部署时应保持构建与运行 JDK 兼容。

## 仓库结构

```text
Y-Blog-Backend/
├── src/main/java/com/blog/
│   ├── controller/       # admin、auditor、user 及公共 API
│   ├── service/          # 业务接口及实现
│   ├── mapper/           # MyBatis 数据访问
│   ├── pojo/             # DTO、实体与 VO
│   ├── config/           # 缓存、Web、对象存储及文档配置
│   ├── interceptor/      # 认证及请求拦截
│   ├── properties/       # 配置映射
│   ├── server/           # WebSocket 实时消息服务
│   └── utils/            # 内容过滤、文件校验等工具
├── src/main/resources/
│   ├── application.yml
│   ├── application-dev.example.yml
│   ├── mapper/           # SQL 映射
│   └── static/           # 数据库结构与手动迁移脚本
├── src/test/             # 控制器、工具等测试
├── docs/images/          # 可公开的文档素材
├── pom.xml
├── SECURITY.md
├── NOTICE
└── LICENSE
```

## 快速开始

### 1. 准备环境

- JDK **17**（源码目标 Java 8）。
- MySQL **8.0**：结构脚本使用 `utf8mb4_0900_ai_ci`，不要直接当成 MySQL 5.7 / MariaDB 兼容脚本。
- Redis，以及运行 Maven Wrapper 时下载依赖所需的网络连接。
- 按需准备 SMTP 邮箱、S3 兼容对象存储、微信及 AI 服务配置。

### 2. 获取源码

```bash
git clone https://github.com/worlt/Y-Blog-Backend.git
cd Y-Blog-Backend
```

### 3. 初始化数据库

先创建独立数据库，再在 MySQL 客户端中导入结构：

```sql
CREATE DATABASE y_blog CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
USE y_blog;
SOURCE src/main/resources/static/blog.sql;
SOURCE src/main/resources/static/suggestion_module.sql;
```

> `SOURCE` 是 MySQL 客户端命令，路径相对于客户端启动目录。也可以在数据库 GUI 中依次执行这两个脚本。
>
> **`blog.sql` 包含 `DROP TABLE`，仅用于空库初始化，不要在已有生产数据库上直接执行。** 脚本只有表结构，不包含真实文章、私信、账户或默认管理员数据。

`blog.sql` 已包含 `reviewer_id`，新建数据库不要重复执行 `add_blog_reviewer_id.sql`。其余 `database_optimization*` 和 `performance_indexes*` 是手动优化脚本，不需要全部执行，使用前应核对现有索引并备份。

**首次账户**：仓库不提供默认登录凭据，也没有自动管理员初始化流程。用户注册依赖邮件服务；管理员需由部署者在私有环境自行初始化。请结合 `AdminServiceImpl` 与 `admin` 表定义设置账户及密码格式，不要把真实账户的初始化 SQL 提交到仓库。现有密码哈希方案需在生产使用前评估和升级，详见 [安全说明](SECURITY.md)。

### 4. 配置本地运行环境

复制安全示例，然后设置自己的环境变量。**不要覆盖已有的私有配置。**

```bash
# Bash：仅在文件不存在时复制
[ -e src/main/resources/application-dev.yml ] || \
  cp src/main/resources/application-dev.example.yml src/main/resources/application-dev.yml
```

```powershell
# PowerShell
if (!(Test-Path src/main/resources/application-dev.yml)) {
    Copy-Item src/main/resources/application-dev.example.yml src/main/resources/application-dev.yml
}
```

编辑 `application-dev.yml`，按下文配置数据库、JWT 和第三方服务。此文件被 Git 忽略；示例中没有真实凭据，必填的凭据环境变量没有可复用的默认值。

### 5. 启动后端

```bash
# macOS / Linux
./mvnw spring-boot:run
```

```powershell
# Windows
.\mvnw.cmd spring-boot:run
```

默认开发端口为 `8088`，与三个前端的代理配置保持一致。启动失败时先检查 JDK、数据库连接、Redis 和未解析的配置占位符。

## 配置说明

公开的 [application.yml](src/main/resources/application.yml) 包含配置结构和引用；真实值由私有 profile 或环境变量提供。主配置默认激活 `dev`。

| 配置组 | 示例中使用的环境变量 | 用途 |
| --- | --- | --- |
| HTTP | `SERVER_PORT` | 后端端口，默认 `8088` |
| MySQL | `DB_URL`、`DB_USERNAME`、`DB_PASSWORD` | JDBC 连接与独立数据库账户 |
| Redis | `REDIS_HOST`、`REDIS_PORT`、`REDIS_DATABASE`、`REDIS_PASSWORD` | 缓存连接 |
| 邮件 | `MAIL_HOST`、`MAIL_PORT`、`MAIL_USERNAME`、`MAIL_PASSWORD` | SMTP 服务，密码通常为授权码 |
| JWT | `JWT_ADMIN_SECRET`、`JWT_AUDITOR_SECRET`、`JWT_USER_SECRET` | 三类用户各自的随机签名密钥 |
| 对象存储 | `S3_ENDPOINT`、`S3_REGION`、`S3_ACCESS_KEY`、`S3_SECRET_KEY`、`S3_BUCKET`、`S3_PUBLIC_URL` | 缤纷云或 S3 兼容存储 |
| 微信 | `WECHAT_APP_ID`、`WECHAT_APP_SECRET` | 微信相关配置 |
| AI | `SILICONFLOW_API_KEY` | AI 服务访问凭据 |

第三方功能并没有统一的“自动禁用”开关，部分配置会在启动时被读取。即使暂时不调用某项功能，也需要满足其配置解析要求；不要把未配置的功能描述为可直接使用。

### Redis 配置注意事项

Spring Boot 2.7 使用 `spring.redis.*` 绑定 Redis 自动配置。主配置还引用了项目的 `blog.redis.*` 并保留 `spring.data.redis.*` 结构，因此示例同时提供标准 `spring.redis` 和 `blog.redis` 映射，避免只填写后者而导致实际连接仍使用默认地址。

### 密钥与日志

- JWT 密钥至少使用 32 字节随机数据，三类用户分别生成，不能复用测试密钥。
- 不要将 SMTP、对象存储、AI Key 或真实数据写入 README、截图、前端或提交历史。
- 示例将 MyBatis 日志设为 `WARN`；这**不能替代业务日志脱敏**，上线前仍需检查 DTO、令牌和验证码是否被输出。
- 如凭据曾提交过，先轮换，再清理历史；加入 `.gitignore` 并不能让旧凭据失效。

## 接口文档与前端联调

- 后端通过 Swagger 2 按角色生成接口文档；启动后可检查 `http://localhost:8088/swagger-ui/index.html`，具体以当前 Springfox 映射为准。
- 示例使用 `ant_path_matcher` 以兼容现有 Springfox 配置。
- API 按用户端、管理端、审核端组织；前端使用 `/api` 前缀，由代理移除后转发到后端。
- WebSocket 路径为 `/ws/{userId}`。生产代理需要支持 Upgrade，并使用 HTTPS/WSS。
- 三类令牌请求头分别为 `userToken`、`adminToken` 和 `auditorToken`。

前端启动方式见 [Y-Blog-Frontend](https://github.com/worlt/Y-Blog-Frontend)。生产环境不建议公开管理接口和 Swagger 文档。

## 测试、构建与部署

```bash
# macOS / Linux
./mvnw -Dspring.profiles.active=test test
./mvnw -Dspring.profiles.active=test clean package
java -jar target/blog-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev
```

```powershell
# Windows
.\mvnw.cmd -Dspring.profiles.active=test test
.\mvnw.cmd -Dspring.profiles.active=test clean package
java -jar target/blog-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev
```

测试显式使用 `test` profile，避免读取本地 `dev` 配置；不要让测试连接生产数据库。测试目录包含控制器和工具测试。部分测试使用 Mock，部分可能需要 Spring 上下文或可用的外部服务；`application-test.yml` 中的值是测试样例，不是生产配置。

上面的 `dev` 启动命令用于本地验证。生产运行时请使用独立的外部配置/profile，限制数据库和 Redis 的访问，配置 HTTPS/WSS、日志脱敏与保留策略，并验证备份恢复。不要打包或上传含真实凭据的运行配置、日志、数据库备份和 JAR 到源码仓库。

## 贡献与反馈

欢迎提交 Issue 和 PR。请提供所用提交、脱敏后的复现步骤及测试结果；数据库变更需要说明是否破坏现有数据以及如何迁移。

安全问题请通过 [SECURITY.md](SECURITY.md) 指定的邮箱私下报告，不要在公开讨论中粘贴敏感内容。

## 许可证

本仓库原创代码采用 **GNU Affero General Public License v3.0 only**，SPDX 标识为 **`AGPL-3.0-only`**。完整条款见 [LICENSE](LICENSE)，版权声明见 [NOTICE](NOTICE)。

AGPL 允许商业使用，但分发或通过网络提供修改版时，需要遵守适用的源码提供、许可证及版权声明义务。`only` 明确限定第 3 版。第三方依赖仍使用各自许可证，具体合规要求以许可证原文为准。

## 作者

**worlt** · [GitHub](https://github.com/worlt) · **2516930419@qq.com**
