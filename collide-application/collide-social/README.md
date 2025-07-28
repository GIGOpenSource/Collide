# Collide Social Module

Collide社交动态模块，提供社交动态发布、互动、推荐等功能。

## 功能特性

### 核心功能
- ✅ 动态发布（文本、图片、视频、转发）
- ✅ 动态互动（点赞、收藏、转发、浏览）
- ✅ 动态查询（分页、搜索、推荐）
- ✅ 热度计算（智能热度分数算法）
- ✅ 缓存优化（Redis缓存提升性能）
- ✅ 完全去连表化设计（通过冗余数据避免复杂JOIN）

### 技术架构
- **框架**: Spring Boot 3.x + MyBatis-Plus
- **数据库**: MySQL 8.0+
- **缓存**: Redis 7.0+
- **服务治理**: Dubbo + Nacos
- **消息队列**: RocketMQ（可选）
- **监控**: Prometheus + Grafana

## 模块结构

```
collide-social/
├── src/main/java/com/gig/collide/social/
│   ├── CollideSocialApplication.java          # 启动类
│   ├── controller/                            # 控制器层
│   │   ├── SocialPostController.java          # 动态管理接口
│   │   └── SocialInteractionController.java   # 互动管理接口
│   ├── facade/                               # 门面服务层
│   │   ├── SocialPostFacadeServiceImpl.java   # 动态门面服务
│   │   └── SocialInteractionFacadeServiceImpl.java # 互动门面服务
│   ├── domain/                               # 领域层
│   │   ├── entity/                           # 实体类
│   │   │   ├── SocialPost.java               # 动态实体
│   │   │   ├── SocialPostInteraction.java    # 互动实体
│   │   │   └── convertor/                    # 实体转换器
│   │   └── service/                          # 领域服务
│   │       ├── SocialPostService.java        # 动态服务接口
│   │       ├── SocialInteractionService.java # 互动服务接口
│   │       └── impl/                         # 服务实现
│   └── infrastructure/                       # 基础设施层
│       └── mapper/                           # 数据访问层
│           ├── SocialPostMapper.java         # 动态Mapper
│           └── SocialPostInteractionMapper.java # 互动Mapper
├── src/main/resources/
│   ├── mapper/                               # MyBatis映射文件
│   │   ├── social_post_sqlmap.xml
│   │   └── social_post_interaction_sqlmap.xml
│   ├── application.yml                       # 应用配置
│   └── bootstrap.yml                         # 启动配置
├── Dockerfile                                # Docker部署文件
└── pom.xml                                  # Maven配置
```

## 数据库设计

### 核心表结构

#### 1. 社交动态主表 (t_social_post)
- 完全去连表化设计，冗余存储作者信息
- 包含动态内容、媒体文件、位置、话题等信息
- 直接存储统计数据（点赞数、评论数等）
- 支持热度分数计算和排序

#### 2. 互动记录表 (t_social_post_interaction)
- 记录用户与动态的所有互动行为
- 冗余存储用户和动态信息
- 支持互动状态管理（激活/取消）
- 提供详细的互动审计日志

## API 接口

### 动态管理接口

#### 创建动态
```http
POST /api/social/posts/create
Content-Type: application/json

{
    "authorId": 123,
    "postType": "TEXT",
    "content": "这是一条测试动态",
    "visibility": 0,
    "allowComments": true,
    "allowShares": true
}
```

#### 查询动态详情
```http
GET /api/social/posts/detail/123?viewerUserId=456
```

#### 获取热门动态
```http
GET /api/social/posts/hot?pageNum=1&pageSize=20
```

### 互动管理接口

#### 点赞动态
```http
POST /api/social/interactions/quick/like?postId=123&userId=456
```

#### 查询动态点赞用户
```http
GET /api/social/interactions/post/123/likes?pageNum=1&pageSize=20
```

## 部署指南

### 1. 环境要求
- JDK 21+
- MySQL 8.0+
- Redis 7.0+
- Nacos 2.0+

### 2. 数据库初始化
```sql
-- 执行SQL脚本创建数据库和表
source sql/social/social-module-complete.sql
```

### 3. 配置修改
修改 `application.yml` 中的数据库和Redis连接信息：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/collide_social
    username: root
    password: your_password
  redis:
    host: localhost
    port: 6379
```

### 4. 启动服务
```bash
# 开发环境
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# 生产环境
java -jar collide-social-1.0.0-SNAPSHOT.jar --spring.profiles.active=prod
```

### 5. Docker 部署
```bash
# 构建镜像
docker build -t collide-social:1.0.0 .

# 运行容器
docker run -d \
  --name collide-social \
  -p 8083:8083 \
  -p 20883:20883 \
  -e DB_HOST=mysql_host \
  -e DB_USERNAME=root \
  -e DB_PASSWORD=password \
  -e REDIS_HOST=redis_host \
  collide-social:1.0.0
```

## 性能优化

### 1. 缓存策略
- 动态详情缓存：30分钟
- 用户今日发布计数缓存：24小时
- 互动限制缓存：1小时

### 2. 数据库优化
- 合理设计索引，支持高频查询
- 使用分区表处理大数据量
- 定期清理过期数据

### 3. 查询优化
- 完全避免连表查询
- 使用冗余数据换取查询性能
- 分页查询优化

## 监控告警

### 健康检查
- HTTP: `GET /social/actuator/health`
- 响应时间监控
- 数据库连接监控

### 关键指标
- 动态发布TPS
- 互动操作TPS
- 平均响应时间
- 错误率统计

## 注意事项

1. **数据一致性**: 由于采用去连表化设计，需要注意冗余数据的同步更新
2. **性能考虑**: 热门动态查询和推荐算法可能消耗较多CPU资源
3. **存储空间**: 冗余存储会增加存储空间需求
4. **扩展性**: 设计支持水平扩展，可通过分库分表处理超大数据量

## 开发指南

### 添加新功能
1. 在API模块定义接口和DTO
2. 在domain层实现业务逻辑
3. 在facade层提供Dubbo服务
4. 在controller层提供HTTP接口
5. 更新mapper和SQL映射文件

### 代码规范
- 遵循SOLID原则
- 保持单一职责
- 充分的单元测试覆盖
- 详细的日志记录

---

*更多详细信息请参考项目文档和API文档。* 