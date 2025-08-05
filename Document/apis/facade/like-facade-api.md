# 点赞模块 Facade 接口文档

## 接口概述

点赞门面服务接口提供完整的点赞业务功能，基于单表设计，与底层Mapper完全对应，支持分布式调用和缓存优化。

**服务名称**: `LikeFacadeService`

**版本**: v2.0.0 (MySQL 8.0 优化版)

**协议**: Dubbo RPC

**特性**:
- ✅ 与底层Mapper方法完全对应
- ✅ 支持用户、目标对象、作者三个维度的查询
- ✅ 支持时间范围查询和批量操作
- ✅ 分布式缓存集成 (JetCache)
- ✅ 跨模块服务调用 (用户验证)
- ✅ 统一的命名规范和参数传递

---

## 🎯 核心业务接口

### 1. 添加点赞

**方法签名**:
```java
Result<LikeResponse> addLike(LikeRequest request)
```

**接口描述**: 用户对内容、评论或动态进行点赞，支持信息冗余存储

**参数说明**:

**LikeRequest**:
| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| userId | Long | ✅ | 点赞用户ID |
| likeType | String | ✅ | 点赞类型: CONTENT/COMMENT/DYNAMIC |
| targetId | Long | ✅ | 目标对象ID |
| targetTitle | String | ❌ | 目标对象标题（冗余存储） |
| targetAuthorId | Long | ❌ | 目标对象作者ID（冗余存储） |
| userNickname | String | ❌ | 用户昵称（冗余存储） |
| userAvatar | String | ❌ | 用户头像（冗余存储） |

**返回结果**:
```java
Result<LikeResponse> // 点赞记录信息
```

**LikeResponse**:
| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | 点赞记录ID |
| userId | Long | 点赞用户ID |
| likeType | String | 点赞类型 |
| targetId | Long | 目标对象ID |
| targetTitle | String | 目标对象标题 |
| targetAuthorId | Long | 目标对象作者ID |
| userNickname | String | 用户昵称 |
| userAvatar | String | 用户头像 |
| status | String | 状态: active/cancelled |
| createTime | LocalDateTime | 创建时间 |
| updateTime | LocalDateTime | 更新时间 |

**业务逻辑**:
1. 验证用户是否存在
2. 检查是否已存在点赞记录
3. 如果存在且为取消状态，则重新激活
4. 如果不存在，创建新的点赞记录
5. 缓存失效处理

**异常处理**:
- `USER_NOT_FOUND`: 用户不存在
- `LIKE_PARAM_ERROR`: 参数验证失败
- `LIKE_ADD_ERROR`: 添加点赞失败

---

### 2. 取消点赞

**方法签名**:
```java
Result<Void> cancelLike(LikeCancelRequest request)
```

**接口描述**: 取消用户的点赞，将状态更新为cancelled

**参数说明**:

**LikeCancelRequest**:
| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| userId | Long | ✅ | 点赞用户ID |
| likeType | String | ✅ | 点赞类型 |
| targetId | Long | ✅ | 目标对象ID |

**返回结果**:
```java
Result<Void> // 操作结果
```

**业务逻辑**:
1. 验证用户是否存在
2. 查找活跃的点赞记录
3. 更新状态为cancelled
4. 缓存失效处理

**异常处理**:
- `USER_NOT_FOUND`: 用户不存在
- `LIKE_CANCEL_FAILED`: 取消失败，未找到记录
- `LIKE_CANCEL_ERROR`: 取消点赞失败

---

### 3. 切换点赞状态

**方法签名**:
```java
Result<LikeResponse> toggleLike(LikeToggleRequest request)
```

**接口描述**: 智能切换点赞状态，如果已点赞则取消，如果未点赞则添加

**参数说明**:

**LikeToggleRequest**:
| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| userId | Long | ✅ | 点赞用户ID |
| likeType | String | ✅ | 点赞类型 |
| targetId | Long | ✅ | 目标对象ID |
| targetTitle | String | ❌ | 目标对象标题 |
| targetAuthorId | Long | ❌ | 目标对象作者ID |
| userNickname | String | ❌ | 用户昵称 |
| userAvatar | String | ❌ | 用户头像 |

**返回结果**:
```java
Result<LikeResponse> // 点赞记录信息 (取消操作时返回null)
```

**业务逻辑**:
1. 验证用户是否存在
2. 检查当前点赞状态
3. 如果已点赞，执行取消操作，返回null
4. 如果未点赞，执行添加操作，返回点赞记录
5. 缓存失效处理

**异常处理**:
- `USER_NOT_FOUND`: 用户不存在
- `LIKE_TOGGLE_PARAM_ERROR`: 参数验证失败
- `LIKE_TOGGLE_ERROR`: 切换操作失败

---

### 4. 检查点赞状态

**方法签名**:
```java
Result<Boolean> checkLikeStatus(Long userId, String likeType, Long targetId)
```

**接口描述**: 查询用户是否已对目标对象点赞

**参数说明**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| userId | Long | ✅ | 用户ID |
| likeType | String | ✅ | 点赞类型 |
| targetId | Long | ✅ | 目标对象ID |

**返回结果**:
```java
Result<Boolean> // 是否已点赞
```

**缓存策略**: 30分钟缓存，键: `user:like:status:{userId}:{likeType}:{targetId}`

**业务逻辑**:
1. 验证用户是否存在
2. 查询点赞状态（优先从缓存获取）
3. 返回boolean结果

**异常处理**:
- `USER_NOT_FOUND`: 用户不存在
- `LIKE_CHECK_ERROR`: 检查状态失败

---

## 📋 查询接口

### 5. 分页查询用户点赞记录

**方法签名**:
```java
Result<PageResponse<LikeResponse>> findUserLikes(Long userId, String likeType, String status, 
                                                Integer currentPage, Integer pageSize)
```

**接口描述**: 分页查询指定用户的点赞记录

**参数说明**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| userId | Long | ✅ | 用户ID |
| likeType | String | ❌ | 点赞类型筛选（可选） |
| status | String | ❌ | 状态筛选（可选） |
| currentPage | Integer | ✅ | 当前页码 |
| pageSize | Integer | ✅ | 页面大小 |

**返回结果**:
```java
Result<PageResponse<LikeResponse>> // 分页点赞记录
```

**PageResponse**:
| 字段 | 类型 | 说明 |
|------|------|------|
| datas | List\<LikeResponse\> | 数据列表 |
| total | Long | 总记录数 |
| currentPage | Integer | 当前页码 |
| pageSize | Integer | 页面大小 |
| totalPage | Integer | 总页数 |

**缓存策略**: 5分钟缓存，键: `user:likes:{userId}:{likeType}:{status}:{currentPage}:{pageSize}`

**索引优化**: 使用 `idx_user_type_status_time` 复合索引

**业务逻辑**:
1. 验证用户是否存在
2. 调用Service层分页查询
3. 转换为PageResponse格式
4. 缓存查询结果

---

### 6. 分页查询目标对象点赞记录

**方法签名**:
```java
Result<PageResponse<LikeResponse>> findTargetLikes(Long targetId, String likeType, String status,
                                                  Integer currentPage, Integer pageSize)
```

**接口描述**: 分页查询指定目标对象的点赞记录

**参数说明**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| targetId | Long | ✅ | 目标对象ID |
| likeType | String | ✅ | 点赞类型 |
| status | String | ❌ | 状态筛选（可选） |
| currentPage | Integer | ✅ | 当前页码 |
| pageSize | Integer | ✅ | 页面大小 |

**返回结果**: 同用户点赞记录查询

**缓存策略**: 5分钟缓存，键: `target:likes:{targetId}:{likeType}:{status}:{currentPage}:{pageSize}`

**索引优化**: 使用 `idx_target_type_status_time` 复合索引

---

### 7. 分页查询作者作品点赞记录

**方法签名**:
```java
Result<PageResponse<LikeResponse>> findAuthorLikes(Long targetAuthorId, String likeType, String status,
                                                  Integer currentPage, Integer pageSize)
```

**接口描述**: 分页查询指定作者作品的点赞记录

**参数说明**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| targetAuthorId | Long | ✅ | 作品作者ID |
| likeType | String | ❌ | 点赞类型筛选（可选） |
| status | String | ❌ | 状态筛选（可选） |
| currentPage | Integer | ✅ | 当前页码 |
| pageSize | Integer | ✅ | 页面大小 |

**返回结果**: 同用户点赞记录查询

**缓存策略**: 5分钟缓存，键: `author:likes:{targetAuthorId}:{likeType}:{status}:{currentPage}:{pageSize}`

**索引优化**: 使用 `idx_author_type_status_time` 复合索引

**业务逻辑**:
1. 验证作者是否存在
2. 调用Service层分页查询
3. 转换为PageResponse格式
4. 缓存查询结果

---

## 📊 统计接口

### 8. 统计目标对象点赞数量

**方法签名**:
```java
Result<Long> countTargetLikes(Long targetId, String likeType)
```

**接口描述**: 统计指定目标对象的点赞数量

**参数说明**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| targetId | Long | ✅ | 目标对象ID |
| likeType | String | ✅ | 点赞类型 |

**返回结果**:
```java
Result<Long> // 点赞数量
```

**缓存策略**: 15分钟缓存，键: `target:like:count:{targetId}:{likeType}`

**索引优化**: 使用 `idx_target_type_status` 覆盖索引

**业务逻辑**:
1. 查询活跃状态的点赞数量
2. 缓存统计结果

---

### 9. 统计用户点赞数量

**方法签名**:
```java
Result<Long> countUserLikes(Long userId, String likeType)
```

**接口描述**: 统计指定用户的点赞数量

**参数说明**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| userId | Long | ✅ | 用户ID |
| likeType | String | ❌ | 点赞类型筛选（可选） |

**返回结果**:
```java
Result<Long> // 点赞数量
```

**缓存策略**: 15分钟缓存，键: `user:like:count:{userId}:{likeType}`

**索引优化**: 使用 `idx_user_type_status` 覆盖索引

**业务逻辑**:
1. 验证用户是否存在
2. 查询活跃状态的点赞数量
3. 缓存统计结果

---

### 10. 统计作者作品被点赞数量

**方法签名**:
```java
Result<Long> countAuthorLikes(Long targetAuthorId, String likeType)
```

**接口描述**: 统计指定作者作品的被点赞数量

**参数说明**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| targetAuthorId | Long | ✅ | 作品作者ID |
| likeType | String | ❌ | 点赞类型筛选（可选） |

**返回结果**:
```java
Result<Long> // 被点赞数量
```

**缓存策略**: 15分钟缓存，键: `author:like:count:{targetAuthorId}:{likeType}`

**索引优化**: 使用 `idx_author_type_status` 覆盖索引

**业务逻辑**:
1. 验证作者是否存在
2. 查询活跃状态的被点赞数量
3. 缓存统计结果

---

## 🔍 扩展接口

### 11. 批量检查点赞状态

**方法签名**:
```java
Result<Map<Long, Boolean>> batchCheckLikeStatus(Long userId, String likeType, List<Long> targetIds)
```

**接口描述**: 批量检查用户对多个目标对象的点赞状态

**参数说明**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| userId | Long | ✅ | 用户ID |
| likeType | String | ✅ | 点赞类型 |
| targetIds | List\<Long\> | ✅ | 目标对象ID列表 |

**返回结果**:
```java
Result<Map<Long, Boolean>> // 目标ID -> 是否已点赞的映射
```

**缓存策略**: 10分钟缓存，键: `batch:like:status:{userId}:{likeType}:hash({targetIds})`

**索引优化**: 使用 `uk_user_target` 唯一索引进行IN查询

**业务逻辑**:
1. 验证用户是否存在
2. 批量查询点赞状态
3. 构建targetId -> isLiked的Map
4. 缓存查询结果

**性能优化**: 
- 单次最多支持100个targetId
- 使用IN查询减少数据库访问次数
- 缓存整个批次查询结果

---

### 12. 查询时间范围内的点赞记录

**方法签名**:
```java
Result<List<LikeResponse>> findByTimeRange(LocalDateTime startTime, LocalDateTime endTime,
                                          String likeType, String status)
```

**接口描述**: 查询指定时间范围内的点赞记录

**参数说明**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| startTime | LocalDateTime | ✅ | 开始时间 |
| endTime | LocalDateTime | ✅ | 结束时间 |
| likeType | String | ❌ | 点赞类型筛选（可选） |
| status | String | ❌ | 状态筛选（可选） |

**返回结果**:
```java
Result<List<LikeResponse>> // 点赞记录列表
```

**缓存策略**: 5分钟缓存，键: `time:range:likes:hash({startTime}:{endTime}:{likeType}:{status})`

**索引优化**: 使用 `idx_time_type_status` 复合索引

**业务逻辑**:
1. 参数验证（时间范围不超过90天）
2. 按时间范围查询点赞记录
3. 转换为LikeResponse列表
4. 缓存查询结果

**使用场景**:
- 数据分析和统计报表
- 用户行为分析
- 热门内容统计

---

## 🏗️ 架构设计

### 分层结构
```
LikeFacadeService (Facade层)
    ↓
LikeService (业务层)
    ↓
LikeMapper (数据访问层)
    ↓
t_like (数据表)
```

### 跨模块依赖
```java
// 用户验证服务
@Autowired
private UserFacadeService userFacadeService;

// 内容服务（预留扩展）
@Autowired
private ContentFacadeService contentFacadeService;

// 评论服务（预留扩展）
@Autowired
private CommentFacadeService commentFacadeService;
```

### 缓存架构
```
JetCache (分布式缓存)
    ↓
Redis (远程缓存) + Caffeine (本地缓存)
    ↓
Cache Strategy: BOTH (L2缓存策略)
```

---

## 🚨 异常处理

### 业务异常
| 异常码 | 说明 | 处理策略 |
|--------|------|----------|
| `USER_NOT_FOUND` | 用户不存在 | 参数验证失败，返回错误信息 |
| `AUTHOR_NOT_FOUND` | 作者不存在 | 参数验证失败，返回错误信息 |
| `LIKE_PARAM_ERROR` | 参数验证失败 | 参数格式错误，返回详细错误信息 |

### 系统异常
| 异常码 | 说明 | 处理策略 |
|--------|------|----------|
| `LIKE_ADD_ERROR` | 添加点赞失败 | 记录错误日志，返回通用错误信息 |
| `LIKE_CANCEL_ERROR` | 取消点赞失败 | 记录错误日志，返回通用错误信息 |
| `LIKE_TOGGLE_ERROR` | 切换状态失败 | 记录错误日志，返回通用错误信息 |
| `*_QUERY_ERROR` | 查询操作失败 | 记录错误日志，返回通用错误信息 |
| `*_COUNT_ERROR` | 统计操作失败 | 记录错误日志，返回通用错误信息 |

### 异常处理原则
1. **参数验证**: 在Service层进行严格的参数验证
2. **用户验证**: 通过UserFacadeService验证用户存在性
3. **缓存降级**: 缓存失败时直接查询数据库
4. **日志记录**: 记录完整的错误信息和调用链路
5. **优雅降级**: 非核心功能失败不影响主流程

---

## 📈 性能指标

### SLA指标
| 接口类型 | 响应时间 | 可用性 | QPS |
|----------|----------|--------|-----|
| 核心操作接口 | < 100ms | 99.9% | 10,000 |
| 查询接口 | < 200ms | 99.9% | 20,000 |
| 统计接口 | < 150ms | 99.5% | 5,000 |
| 批量接口 | < 300ms | 99.5% | 1,000 |

### 缓存命中率
- **点赞状态查询**: > 90%
- **统计数据查询**: > 85%
- **分页查询**: > 80%
- **批量状态查询**: > 75%

### 数据库性能
- **主键查询**: < 1ms
- **索引查询**: < 5ms
- **复合查询**: < 20ms
- **统计查询**: < 50ms

---

## 🔧 配置说明

### Dubbo配置
```yaml
dubbo:
  consumer:
    timeout: 3000
    retries: 0
    check: false
  provider:
    timeout: 3000
    version: 1.0.0
```

### 缓存配置
```yaml
jetcache:
  statIntervalMinutes: 15
  areaInCacheName: false
  local:
    default:
      type: caffeine
      keyConvertor: fastjson
  remote:
    default:
      type: redis
      keyConvertor: fastjson
      valueEncoder: java
      valueDecoder: java
```

---

## 📝 使用示例

### Spring集成示例
```java
@Service
public class BusinessService {
    
    @DubboReference(version = "1.0.0")
    private LikeFacadeService likeFacadeService;
    
    public void addLike(Long userId, Long contentId) {
        LikeRequest request = new LikeRequest();
        request.setUserId(userId);
        request.setLikeType("CONTENT");
        request.setTargetId(contentId);
        
        Result<LikeResponse> result = likeFacadeService.addLike(request);
        if (result.getSuccess()) {
            // 处理成功逻辑
        } else {
            // 处理失败逻辑
        }
    }
}
```

### 批量查询示例
```java
public Map<Long, Boolean> checkMultipleLikes(Long userId, List<Long> contentIds) {
    Result<Map<Long, Boolean>> result = likeFacadeService.batchCheckLikeStatus(
        userId, "CONTENT", contentIds
    );
    
    return result.getSuccess() ? result.getData() : Collections.emptyMap();
}
```

---

## 🔗 相关文档

- [点赞REST API文档](../news/like-rest-api.md)
- [点赞数据库设计](../database/like-schema.md)
- [缓存策略设计](../cache/like-cache.md)
- [性能监控指南](../monitor/like-performance.md)

---

**最后更新**: 2024-01-16  
**文档版本**: v2.0.0  
**维护团队**: GIG Team