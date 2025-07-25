# Collide API 文档说明

## 📖 API文档中心

Collide 项目已集成 **Swagger UI**，提供在线API文档和接口测试功能，让开发更加高效！

## 🚀 快速访问

### 主要入口
- **API文档导航**: http://localhost:8080/swagger-ui.html
- **网关访问**: http://localhost:8080/docs

### 各服务直接访问
| 服务名称 | Swagger UI | OpenAPI 规范 | 端口 |
|---------|-----------|-------------|------|
| **认证服务** | http://localhost:8080/auth/swagger-ui/index.html | http://localhost:8080/auth/v3/api-docs | 9502 |
| **业务聚合** | http://localhost:8080/app/swagger-ui/index.html | http://localhost:8080/app/v3/api-docs | 9503 |
| **API网关** | http://localhost:8080/actuator | - | 8080 |

## 🎯 功能特性

### ✨ 已实现功能
- **多服务聚合**: 所有微服务的API文档统一管理
- **分组展示**: 按业务模块分组（用户、认证、内容、社交等）
- **在线测试**: 直接在浏览器中测试API接口
- **认证支持**: 集成 Sa-Token 认证，支持Bearer Token
- **实时监控**: 服务状态实时显示
- **响应式设计**: 支持移动端访问

### 🔧 配置说明
项目使用 **SpringDoc OpenAPI 3** 替代传统的SpringFox，具有更好的性能和Spring Boot 3兼容性。

#### 配置文件位置
```
collide-common/collide-web/src/main/resources/swagger.yml
```

#### 主要配置项
```yaml
springdoc:
  api-docs:
    enabled: true
    path: /v3/api-docs
  swagger-ui:
    enabled: true
    path: /swagger-ui.html
    try-it-out-enabled: true
  paths-to-match: /api/**
  packages-to-scan: com.gig.collide
```

## 📝 使用指南

### 1. 启动服务
```bash
# 启动基础环境
docker-compose -f middleware/docker-compose.yml up -d

# 启动业务服务
mvn clean package -DskipTests
java -jar collide-application/collide-app/target/collide-app-1.0.0-SNAPSHOT.jar
java -jar collide-auth/target/collide-auth-1.0.0-SNAPSHOT.jar
java -jar collide-gateway/target/collide-gateway-1.0.0-SNAPSHOT.jar
```

### 2. 访问文档
1. 打开浏览器访问: http://localhost:8080/swagger-ui.html
2. 选择需要查看的服务
3. 浏览API接口文档

### 3. 测试接口
1. 在Swagger UI中找到需要测试的接口
2. 点击接口展开详情
3. 点击 **"Try it out"** 按钮
4. 填写请求参数
5. 点击 **"Execute"** 执行测试
6. 查看响应结果

### 4. 认证配置
对于需要认证的接口：
1. 点击右上角的 **"Authorize"** 按钮
2. 选择 **Bearer Token** 认证方式
3. 输入从登录接口获取的token
4. 格式：`Bearer your_token_here`

## 🎨 API分组说明

### 01-用户服务 (`/api/v1/users/**`, `/api/v1/files/**`)
- 用户注册、登录、信息管理
- 文件上传功能
- 用户激活功能

### 02-认证服务 (`/api/v1/auth/**`)
- 用户认证
- Token管理
- 权限控制

### 03-内容服务 (`/api/v1/content/**`)
- 内容发布、编辑、删除
- 内容查询和展示

### 04-社交服务 (`/api/v1/social/**`)
- 关注/取消关注
- 点赞/收藏
- 评论功能
- 社交动态

### 05-业务聚合 (`/api/v1/business/**`, `/api/v1/test/**`)
- 业务聚合接口
- 系统测试接口

### 99-系统监控 (`/actuator/**`)
- 健康检查
- 性能监控
- 应用信息

## 🔍 常见问题

### Q: 无法访问Swagger UI？
**A**: 检查以下项目：
1. 服务是否正常启动
2. 端口是否被占用
3. 网关路由配置是否正确
4. 防火墙设置

### Q: 接口测试返回401未授权？
**A**: 对于需要认证的接口：
1. 先调用登录接口获取token
2. 在Swagger UI中配置Authorization
3. 确保token格式正确

### Q: 某些接口显示不全？
**A**: 检查：
1. Controller类是否添加了`@Tag`注解
2. 接口方法是否添加了`@Operation`注解
3. 包扫描路径是否包含该Controller

### Q: 跨域问题？
**A**: 网关已配置CORS，如果仍有问题：
1. 检查网关CORS配置
2. 确认请求头设置
3. 查看浏览器控制台错误

## 🛠️ 开发指南

### 添加新接口文档
1. 在Controller类上添加`@Tag`注解
```java
@Tag(name = "模块名称", description = "模块描述")
@RestController
public class YourController {
```

2. 在接口方法上添加`@Operation`注解
```java
@Operation(summary = "接口名称", description = "接口详细描述")
@GetMapping("/api/path")
public Result<Object> yourMethod() {
```

3. 为DTO添加`@Schema`注解
```java
@Schema(description = "用户信息")
public class UserInfo {
    @Schema(description = "用户ID", example = "123456")
    private Long userId;
}
```

### 自定义API分组
在`SwaggerConfiguration`中添加新的分组：
```java
@Bean
public GroupedOpenApi customApi() {
    return GroupedOpenApi.builder()
            .group("06-自定义服务")
            .pathsToMatch("/api/v1/custom/**")
            .build();
}
```

## 📚 相关资源

- [SpringDoc OpenAPI 官方文档](https://springdoc.org/)
- [OpenAPI 3.0 规范](https://swagger.io/specification/)
- [Sa-Token 文档](https://sa-token.cc/)
- [Spring Cloud Gateway 文档](https://spring.io/projects/spring-cloud-gateway)

---

**🎉 现在就访问 [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html) 开始探索 Collide API 吧！** 