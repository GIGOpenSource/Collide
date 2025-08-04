# Comment Facade API 接口文档

**版本**: 5.0.0  
**更新时间**: 2024-01-01  
**服务类型**: Dubbo RPC 服务  
**服务接口**: `com.gig.collide.api.comment.CommentFacadeService`  
**服务实现**: `com.gig.collide.comment.facade.CommentFacadeServiceImpl`  

## 🚀 概述

评论门面服务提供完整的评论功能RPC接口，作为微服务间调用的标准接口。通过Dubbo框架提供高性能、高可用的分布式服务调用能力。

**服务配置**:
- **版本**: 5.0.0
- **超时时间**: 5000ms
- **协议**: Dubbo
- **序列化**: Hessian2
- **负载均衡**: 随机

## 📋 接口分类

| 分类 | 方法数量 | 功能描述 |
|------|----------|----------|
| **评论基础操作** | 4个 | 创建、更新、删除、查询评论 |
| **目标对象评论查询** | 3个 | 获取目标内容的评论列表和树形结构 |
| **用户评论查询** | 2个 | 获取用户发表的评论和收到的回复 |
| **统计功能** | 6个 | 点赞数、回复数、评论计数功能 |
| **高级功能** | 5个 | 搜索、热门、最新、范围查询 |
| **数据分析功能** | 3个 | 统计信息、回复关系、热度排行 |
| **管理功能** | 4个 | 批量操作、同步、清理功能 |

---

## 🔧 评论基础操作 (4个方法)

### 1.1 createComment

**方法签名**: 
```java
Result<CommentResponse> createComment(CommentCreateRequest request)
```

**功能描述**: 创建新评论，支持根评论和回复评论

**参数说明**:
- `request` (CommentCreateRequest): 创建评论请求对象
  - `targetId` (Long): 目标对象ID
  - `commentType` (String): 评论类型 (CONTENT/DYNAMIC)
  - `content` (String): 评论内容
  - `parentCommentId` (Long): 父评论ID，0表示根评论
  - `replyToUserId` (Long): 回复目标用户ID
  - `userId` (Long): 评论用户ID

**返回值**: `Result<CommentResponse>` - 包含创建的评论信息

**异常情况**:
- `IllegalArgumentException`: 参数验证失败
- `CommentCreateException`: 评论创建失败

**调用示例**:
```java
@DubboReference
private CommentFacadeService commentFacadeService;

CommentCreateRequest request = new CommentCreateRequest();
request.setTargetId(12345L);
request.setCommentType("CONTENT");
request.setContent("这是一条评论");
request.setUserId(1001L);

Result<CommentResponse> result = commentFacadeService.createComment(request);
```

### 1.2 updateComment

**方法签名**: 
```java
Result<CommentResponse> updateComment(CommentUpdateRequest request)
```

**功能描述**: 更新评论内容

**参数说明**:
- `request` (CommentUpdateRequest): 更新评论请求对象
  - `id` (Long): 评论ID
  - `content` (String): 新的评论内容
  - `userId` (Long): 操作用户ID

**返回值**: `Result<CommentResponse>` - 包含更新后的评论信息

### 1.3 deleteComment

**方法签名**: 
```java
Result<Void> deleteComment(Long commentId, Long userId)
```

**功能描述**: 逻辑删除评论，将状态更新为DELETED

**参数说明**:
- `commentId` (Long): 评论ID
- `userId` (Long): 操作用户ID

**返回值**: `Result<Void>` - 删除操作结果

### 1.4 getCommentById

**方法签名**: 
```java
Result<CommentResponse> getCommentById(Long commentId)
```

**功能描述**: 根据ID获取评论详情

**参数说明**:
- `commentId` (Long): 评论ID

**返回值**: `Result<CommentResponse>` - 评论详情信息

---

## 📝 目标对象评论查询 (3个方法)

### 2.1 getTargetComments

**方法签名**: 
```java
Result<PageResponse<CommentResponse>> getTargetComments(Long targetId, String commentType, 
                                                       Long parentCommentId, Integer currentPage, Integer pageSize)
```

**功能描述**: 获取目标对象的评论列表

**参数说明**:
- `targetId` (Long): 目标对象ID
- `commentType` (String): 评论类型，可选 (CONTENT/DYNAMIC)
- `parentCommentId` (Long): 父评论ID，0表示获取根评论
- `currentPage` (Integer): 当前页码
- `pageSize` (Integer): 页面大小

**返回值**: `Result<PageResponse<CommentResponse>>` - 分页评论列表

### 2.2 getCommentReplies

**方法签名**: 
```java
Result<PageResponse<CommentResponse>> getCommentReplies(Long parentCommentId, Integer currentPage, Integer pageSize)
```

**功能描述**: 获取评论的回复列表

**参数说明**:
- `parentCommentId` (Long): 父评论ID
- `currentPage` (Integer): 当前页码
- `pageSize` (Integer): 页面大小

**返回值**: `Result<PageResponse<CommentResponse>>` - 分页回复列表

### 2.3 getCommentTree

**方法签名**: 
```java
Result<PageResponse<CommentResponse>> getCommentTree(Long targetId, String commentType, 
                                                    Integer maxDepth, Integer currentPage, Integer pageSize)
```

**功能描述**: 获取目标对象的评论树，返回带层级结构的评论列表

**参数说明**:
- `targetId` (Long): 目标对象ID
- `commentType` (String): 评论类型
- `maxDepth` (Integer): 最大层级深度
- `currentPage` (Integer): 当前页码
- `pageSize` (Integer): 页面大小

**返回值**: `Result<PageResponse<CommentResponse>>` - 分页评论树结构

---

## 👤 用户评论查询 (2个方法)

### 3.1 getUserComments

**方法签名**: 
```java
Result<PageResponse<CommentResponse>> getUserComments(Long userId, String commentType, 
                                                     Integer currentPage, Integer pageSize)
```

**功能描述**: 获取用户的评论列表

**参数说明**:
- `userId` (Long): 用户ID
- `commentType` (String): 评论类型，可选
- `currentPage` (Integer): 当前页码
- `pageSize` (Integer): 页面大小

**返回值**: `Result<PageResponse<CommentResponse>>` - 用户评论分页列表

### 3.2 getUserReplies

**方法签名**: 
```java
Result<PageResponse<CommentResponse>> getUserReplies(Long userId, Integer currentPage, Integer pageSize)
```

**功能描述**: 获取用户收到的回复

**参数说明**:
- `userId` (Long): 用户ID
- `currentPage` (Integer): 当前页码
- `pageSize` (Integer): 页面大小

**返回值**: `Result<PageResponse<CommentResponse>>` - 用户回复分页列表

---

## 📊 统计功能 (6个方法)

### 4.1 increaseLikeCount

**方法签名**: 
```java
Result<Integer> increaseLikeCount(Long commentId, Integer increment)
```

**功能描述**: 增加评论点赞数

**参数说明**:
- `commentId` (Long): 评论ID
- `increment` (Integer): 增加数量

**返回值**: `Result<Integer>` - 更新后的点赞数

### 4.2 increaseReplyCount

**方法签名**: 
```java
Result<Integer> increaseReplyCount(Long commentId, Integer increment)
```

**功能描述**: 增加回复数

**参数说明**:
- `commentId` (Long): 评论ID
- `increment` (Integer): 增加数量

**返回值**: `Result<Integer>` - 更新后的回复数

### 4.3 countTargetComments

**方法签名**: 
```java
Result<Long> countTargetComments(Long targetId, String commentType)
```

**功能描述**: 统计目标对象的评论数

**参数说明**:
- `targetId` (Long): 目标对象ID
- `commentType` (String): 评论类型

**返回值**: `Result<Long>` - 评论数量

### 4.4 countUserComments

**方法签名**: 
```java
Result<Long> countUserComments(Long userId, String commentType)
```

**功能描述**: 统计用户评论数

**参数说明**:
- `userId` (Long): 用户ID
- `commentType` (String): 评论类型，可选

**返回值**: `Result<Long>` - 用户评论数量

---

## 🔍 高级功能 (5个方法)

### 5.1 searchComments

**方法签名**: 
```java
Result<PageResponse<CommentResponse>> searchComments(String keyword, String commentType, 
                                                    Long targetId, Integer currentPage, Integer pageSize)
```

**功能描述**: 搜索评论，根据评论内容搜索

**参数说明**:
- `keyword` (String): 搜索关键词
- `commentType` (String): 评论类型，可选
- `targetId` (Long): 目标对象ID，可选
- `currentPage` (Integer): 当前页码
- `pageSize` (Integer): 页面大小

**返回值**: `Result<PageResponse<CommentResponse>>` - 搜索结果分页列表

### 5.2 getPopularComments

**方法签名**: 
```java
Result<PageResponse<CommentResponse>> getPopularComments(Long targetId, String commentType, 
                                                       Integer timeRange, Integer currentPage, Integer pageSize)
```

**功能描述**: 获取热门评论，根据点赞数排序

**参数说明**:
- `targetId` (Long): 目标对象ID
- `commentType` (String): 评论类型
- `timeRange` (Integer): 时间范围（天）
- `currentPage` (Integer): 当前页码
- `pageSize` (Integer): 页面大小

**返回值**: `Result<PageResponse<CommentResponse>>` - 热门评论分页列表

### 5.3 getLatestComments

**方法签名**: 
```java
Result<PageResponse<CommentResponse>> getLatestComments(Long targetId, String commentType, 
                                                      Integer currentPage, Integer pageSize)
```

**功能描述**: 获取最新评论

**参数说明**:
- `targetId` (Long): 目标对象ID，可选
- `commentType` (String): 评论类型，可选
- `currentPage` (Integer): 当前页码
- `pageSize` (Integer): 页面大小

**返回值**: `Result<PageResponse<CommentResponse>>` - 最新评论分页列表

### 5.4 getCommentsByLikeCountRange

**方法签名**: 
```java
Result<PageResponse<CommentResponse>> getCommentsByLikeCountRange(Integer minLikeCount, Integer maxLikeCount,
                                                                String commentType, Long targetId,
                                                                Integer currentPage, Integer pageSize)
```

**功能描述**: 根据点赞数范围查询评论

**参数说明**:
- `minLikeCount` (Integer): 最小点赞数
- `maxLikeCount` (Integer): 最大点赞数
- `commentType` (String): 评论类型，可选
- `targetId` (Long): 目标对象ID，可选
- `currentPage` (Integer): 当前页码
- `pageSize` (Integer): 页面大小

**返回值**: `Result<PageResponse<CommentResponse>>` - 符合条件的评论分页列表

### 5.5 getCommentsByTimeRange

**方法签名**: 
```java
Result<PageResponse<CommentResponse>> getCommentsByTimeRange(LocalDateTime startTime, LocalDateTime endTime,
                                                           String commentType, Long targetId,
                                                           Integer currentPage, Integer pageSize)
```

**功能描述**: 根据时间范围查询评论

**参数说明**:
- `startTime` (LocalDateTime): 开始时间
- `endTime` (LocalDateTime): 结束时间
- `commentType` (String): 评论类型，可选
- `targetId` (Long): 目标对象ID，可选
- `currentPage` (Integer): 当前页码
- `pageSize` (Integer): 页面大小

**返回值**: `Result<PageResponse<CommentResponse>>` - 时间范围内的评论分页列表

---

## 📈 数据分析功能 (3个方法)

### 6.1 getCommentStatistics

**方法签名**: 
```java
Result<Map<String, Object>> getCommentStatistics(Long targetId, String commentType, Long userId,
                                                LocalDateTime startTime, LocalDateTime endTime)
```

**功能描述**: 获取评论统计信息

**参数说明**:
- `targetId` (Long): 目标对象ID，可选
- `commentType` (String): 评论类型，可选
- `userId` (Long): 用户ID，可选
- `startTime` (LocalDateTime): 开始时间，可选
- `endTime` (LocalDateTime): 结束时间，可选

**返回值**: `Result<Map<String, Object>>` - 统计信息Map

**统计信息包含**:
```java
{
  "totalComments": 1000,           // 总评论数
  "totalLikes": 5000,              // 总点赞数
  "totalReplies": 800,             // 总回复数
  "avgLikePerComment": 5.0,        // 平均点赞数
  "topUsers": [...],               // 活跃用户排行
  "dailyStats": [...],             // 每日统计
  "commentTypeStats": {...}        // 评论类型统计
}
```

### 6.2 getUserReplyRelations

**方法签名**: 
```java
Result<List<Map<String, Object>>> getUserReplyRelations(Long userId, LocalDateTime startTime, LocalDateTime endTime)
```

**功能描述**: 查询用户回复关系

**参数说明**:
- `userId` (Long): 用户ID，可选
- `startTime` (LocalDateTime): 开始时间，可选
- `endTime` (LocalDateTime): 结束时间，可选

**返回值**: `Result<List<Map<String, Object>>>` - 回复关系列表

**关系信息包含**:
```java
[
  {
    "fromUserId": 1001,
    "fromUserNickname": "用户A",
    "toUserId": 1002,
    "toUserNickname": "用户B",
    "replyCount": 5,
    "lastReplyTime": "2024-01-01T10:00:00"
  }
]
```

### 6.3 getCommentHotRanking

**方法签名**: 
```java
Result<List<Map<String, Object>>> getCommentHotRanking(String commentType, Long targetId,
                                                      LocalDateTime startTime, LocalDateTime endTime, Integer limit)
```

**功能描述**: 查询评论热度排行

**参数说明**:
- `commentType` (String): 评论类型，可选
- `targetId` (Long): 目标对象ID，可选
- `startTime` (LocalDateTime): 开始时间，可选
- `endTime` (LocalDateTime): 结束时间，可选
- `limit` (Integer): 限制数量

**返回值**: `Result<List<Map<String, Object>>>` - 热度排行列表

**排行信息包含**:
```java
[
  {
    "commentId": 12345,
    "content": "评论内容",
    "likeCount": 100,
    "replyCount": 50,
    "hotScore": 150.5,
    "createTime": "2024-01-01T10:00:00",
    "userNickname": "用户昵称"
  }
]
```

---

## ⚙️ 管理功能 (4个方法)

### 7.1 batchUpdateCommentStatus

**方法签名**: 
```java
Result<Integer> batchUpdateCommentStatus(List<Long> commentIds, String status)
```

**功能描述**: 批量更新评论状态

**参数说明**:
- `commentIds` (List<Long>): 评论ID列表
- `status` (String): 新状态 (NORMAL/HIDDEN/DELETED)

**返回值**: `Result<Integer>` - 影响行数

### 7.2 batchDeleteTargetComments

**方法签名**: 
```java
Result<Integer> batchDeleteTargetComments(Long targetId, String commentType)
```

**功能描述**: 批量删除目标对象的评论

**参数说明**:
- `targetId` (Long): 目标对象ID
- `commentType` (String): 评论类型

**返回值**: `Result<Integer>` - 影响行数

### 7.3 updateUserInfo

**方法签名**: 
```java
Result<Integer> updateUserInfo(Long userId, String nickname, String avatar)
```

**功能描述**: 更新用户信息（同步冗余字段）

**参数说明**:
- `userId` (Long): 用户ID
- `nickname` (String): 新昵称
- `avatar` (String): 新头像

**返回值**: `Result<Integer>` - 影响行数

### 7.4 updateReplyToUserInfo

**方法签名**: 
```java
Result<Integer> updateReplyToUserInfo(Long replyToUserId, String nickname, String avatar)
```

**功能描述**: 更新回复目标用户信息（同步冗余字段）

**参数说明**:
- `replyToUserId` (Long): 回复目标用户ID
- `nickname` (String): 新昵称
- `avatar` (String): 新头像

**返回值**: `Result<Integer>` - 影响行数

### 7.5 cleanDeletedComments

**方法签名**: 
```java
Result<Integer> cleanDeletedComments(Integer days, Integer limit)
```

**功能描述**: 清理已删除的评论（物理删除）

**参数说明**:
- `days` (Integer): 删除多少天前的数据
- `limit` (Integer): 限制删除数量

**返回值**: `Result<Integer>` - 删除数量

---

## 🎯 数据模型

### CommentResponse 

```java
public class CommentResponse {
    private Long id;                      // 评论ID
    private Long targetId;                // 目标对象ID
    private String commentType;           // 评论类型
    private String content;               // 评论内容
    private Long userId;                  // 用户ID
    private String userNickname;          // 用户昵称
    private String userAvatar;            // 用户头像
    private Long parentCommentId;         // 父评论ID
    private Long replyToUserId;           // 回复目标用户ID
    private String replyToUserNickname;   // 回复目标用户昵称
    private Integer likeCount;            // 点赞数
    private Integer replyCount;           // 回复数
    private String status;                // 状态
    private LocalDateTime createTime;     // 创建时间
    private LocalDateTime updateTime;     // 更新时间
    
    // 嵌套回复列表（评论树用）
    private List<CommentResponse> replies;
}
```

### PageResponse<T>

```java
public class PageResponse<T> {
    private List<T> records;              // 数据列表
    private Long totalCount;              // 总记录数
    private Integer totalPage;            // 总页数
    private Integer currentPage;          // 当前页码
    private Integer pageSize;             // 页面大小
    private Boolean hasNext;              // 是否有下一页
    private Boolean hasPrevious;          // 是否有上一页
}
```

### Result<T>

```java
public class Result<T> {
    private Integer code;                 // 响应码
    private String message;               // 响应消息
    private T data;                       // 响应数据
    private Long timestamp;               // 时间戳
    
    // 静态工厂方法
    public static <T> Result<T> success(T data);
    public static <T> Result<T> error(String code, String message);
}
```

## 🚨 异常处理

### 服务异常类型

| 异常类型 | 错误代码 | 描述 |
|----------|----------|------|
| `CommentNotFoundException` | `COMMENT_NOT_FOUND` | 评论不存在 |
| `InvalidParameterException` | `INVALID_PARAMETER` | 参数验证失败 |
| `PermissionDeniedException` | `PERMISSION_DENIED` | 权限不足 |
| `CommentCreateException` | `CREATE_COMMENT_ERROR` | 创建评论失败 |
| `CommentUpdateException` | `UPDATE_COMMENT_ERROR` | 更新评论失败 |
| `CommentDeleteException` | `DELETE_COMMENT_ERROR` | 删除评论失败 |
| `SearchException` | `SEARCH_ERROR` | 搜索功能异常 |
| `StatisticsException` | `STATISTICS_ERROR` | 统计功能异常 |

### 错误处理示例

```java
try {
    Result<CommentResponse> result = commentFacadeService.createComment(request);
    if (result.getCode() == 200) {
        // 处理成功逻辑
        CommentResponse comment = result.getData();
    } else {
        // 处理错误逻辑
        log.error("创建评论失败: {}", result.getMessage());
    }
} catch (Exception e) {
    log.error("调用评论服务异常", e);
    // 服务降级或异常处理
}
```

## 🔧 服务配置

### Provider配置

```yaml
dubbo:
  application:
    name: collide-comment
  registry:
    address: nacos://localhost:8848
  protocol:
    name: dubbo
    port: 20881
  provider:
    timeout: 5000
    retries: 2
    loadbalance: random
```

### Consumer配置

```yaml
dubbo:
  application:
    name: collide-consumer
  registry:
    address: nacos://localhost:8848
  consumer:
    timeout: 5000
    retries: 2
    check: false
```

### 代码配置

```java
@Configuration
public class CommentDubboConfiguration {
    
    @Bean
    @DubboService(version = "5.0.0", timeout = 5000)
    public CommentFacadeService commentFacadeService() {
        return new CommentFacadeServiceImpl();
    }
}
```

## 📝 使用示例

### Spring Boot集成

```java
@Service
public class CommentBusinessService {
    
    @DubboReference(version = "5.0.0", timeout = 5000)
    private CommentFacadeService commentFacadeService;
    
    public void processComment(Long targetId, String content, Long userId) {
        // 创建评论
        CommentCreateRequest request = new CommentCreateRequest();
        request.setTargetId(targetId);
        request.setCommentType("CONTENT");
        request.setContent(content);
        request.setUserId(userId);
        
        Result<CommentResponse> result = commentFacadeService.createComment(request);
        
        if (result.getCode() == 200) {
            CommentResponse comment = result.getData();
            log.info("评论创建成功: {}", comment.getId());
            
            // 增加目标对象的评论数
            // TODO: 调用内容服务更新评论数
            
        } else {
            log.error("评论创建失败: {}", result.getMessage());
            throw new BusinessException(result.getMessage());
        }
    }
    
    public PageResponse<CommentResponse> getCommentPage(Long targetId, Integer page, Integer size) {
        Result<PageResponse<CommentResponse>> result = 
            commentFacadeService.getTargetComments(targetId, "CONTENT", 0L, page, size);
            
        if (result.getCode() == 200) {
            return result.getData();
        } else {
            log.error("获取评论列表失败: {}", result.getMessage());
            return new PageResponse<>();
        }
    }
}
```

### 异步调用示例

```java
@Service
public class AsyncCommentService {
    
    @DubboReference(version = "5.0.0", async = true)
    private CommentFacadeService commentFacadeService;
    
    @Async
    public CompletableFuture<Void> processCommentAsync(Long targetId, String content, Long userId) {
        return CompletableFuture.runAsync(() -> {
            // 异步处理评论逻辑
            CommentCreateRequest request = new CommentCreateRequest();
            request.setTargetId(targetId);
            request.setContent(content);
            request.setUserId(userId);
            
            Result<CommentResponse> result = commentFacadeService.createComment(request);
            
            if (result.getCode() == 200) {
                log.info("异步创建评论成功");
                // 发送消息通知
                notificationService.sendCommentNotification(result.getData());
            }
        });
    }
}
```

## 🚀 性能优化建议

### 1. 缓存策略

```java
@Service
public class CachedCommentService {
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    @DubboReference
    private CommentFacadeService commentFacadeService;
    
    public PageResponse<CommentResponse> getTargetCommentsWithCache(Long targetId, Integer page) {
        String cacheKey = String.format("comments:target:%d:page:%d", targetId, page);
        
        // 先从缓存获取
        PageResponse<CommentResponse> cached = 
            (PageResponse<CommentResponse>) redisTemplate.opsForValue().get(cacheKey);
        
        if (cached != null) {
            return cached;
        }
        
        // 缓存未命中，调用远程服务
        Result<PageResponse<CommentResponse>> result = 
            commentFacadeService.getTargetComments(targetId, "CONTENT", 0L, page, 20);
        
        if (result.getCode() == 200) {
            PageResponse<CommentResponse> data = result.getData();
            // 缓存结果，过期时间5分钟
            redisTemplate.opsForValue().set(cacheKey, data, Duration.ofMinutes(5));
            return data;
        }
        
        return new PageResponse<>();
    }
}
```

### 2. 批量操作

```java
// 批量获取评论统计
public Map<Long, Long> batchGetCommentCounts(List<Long> targetIds) {
    Map<Long, Long> counts = new HashMap<>();
    
    for (Long targetId : targetIds) {
        Result<Long> result = commentFacadeService.countTargetComments(targetId, "CONTENT");
        if (result.getCode() == 200) {
            counts.put(targetId, result.getData());
        }
    }
    
    return counts;
}
```

### 3. 连接池配置

```yaml
dubbo:
  provider:
    connections: 10      # 每个提供者的最大连接数
    accepts: 1000        # 服务提供方最大可接受连接数
  consumer:
    connections: 5       # 对每个提供者的最大连接数
    sticky: false        # 粘滞连接
```

## 🔗 相关文档

- [Comment REST API 文档](./comment-rest-api.md)
- [评论数据模型](../models/comment-model.md)
- [Dubbo服务配置](../config/dubbo-config.md)
- [服务监控指南](../monitoring/comment-monitoring.md)

---

**联系信息**:  
- 项目: Collide评论系统  
- 版本: 5.0.0  
- 维护: Collide开发团队  
- 更新: 2024-01-01