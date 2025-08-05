# Follow Facade API 接口文档

## 文档信息
- **模块名称**: 关注管理门面服务 (FollowFacadeService)
- **服务版本**: 2.0.0 (简洁版)
- **文档版本**: 2.0.0
- **最后更新**: 2024-01-01

## 概述

关注门面服务接口基于 `follow-simple.sql` 的单表设计，实现核心关注功能的门面层封装。提供统一的 `Result<T>` 响应格式，集成缓存优化和完整的业务逻辑处理。

### 服务特点
- 统一的 `Result<T>` 响应格式
- JetCache 缓存集成优化
- 完整的参数校验和业务规则
- 冗余字段设计避免关联查询
- 支持批量操作和复杂查询
- Dubbo 服务调用支持

### 技术架构
- **框架**: Spring Boot + MyBatis-Plus
- **缓存**: JetCache (Redis)
- **RPC**: Dubbo
- **数据库**: MySQL 8.0/8.4 with optimized indexes

## 接口列表

### 1. 基础关注操作

#### 1.1 关注用户

**方法签名**: `Result<FollowResponse> followUser(FollowCreateRequest request)`

**功能描述**: 用户关注另一个用户，建立关注关系，支持关注者和被关注者信息冗余存储

**请求参数**:
```java
public class FollowCreateRequest {
    @NotNull(message = "关注者ID不能为空")
    private Long followerId;
    
    @NotNull(message = "被关注者ID不能为空") 
    private Long followeeId;
    
    private String nickname;     // 昵称（冗余字段）
    private String avatar;       // 头像（冗余字段）
}
```

**响应数据**:
```java
public class FollowResponse {
    private Long id;                    // 关注关系ID
    private Long followerId;            // 关注者ID
    private Long followeeId;            // 被关注者ID
    private String followerNickname;    // 关注者昵称
    private String followerAvatar;      // 关注者头像
    private String followeeNickname;    // 被关注者昵称
    private String followeeAvatar;      // 被关注者头像
    private String status;              // 关注状态 (active/cancelled)
    private LocalDateTime followTime;   // 关注时间
    private LocalDateTime updateTime;   // 更新时间
}
```

**业务逻辑**:
1. 参数校验（非空验证、用户存在性）
2. 检查是否重复关注
3. 获取用户信息进行冗余存储
4. 创建关注关系记录
5. 更新用户关注统计
6. 缓存失效处理

**缓存策略**:
- 缓存失效: 关注关系缓存、统计缓存
- 缓存预热: 新建关注关系信息

**错误处理**:
- `FOLLOW_INVALID_PARAM`: 参数无效
- `FOLLOW_USER_NOT_EXIST`: 用户不存在
- `FOLLOW_ALREADY_EXISTS`: 关注关系已存在
- `FOLLOW_SELF_NOT_ALLOWED`: 不能关注自己

---

#### 1.2 取消关注

**方法签名**: `Result<Void> unfollowUser(FollowDeleteRequest request)`

**功能描述**: 用户取消关注另一个用户，将关注状态更新为cancelled

**请求参数**:
```java
public class FollowDeleteRequest {
    @NotNull(message = "关注者ID不能为空")
    private Long followerId;
    
    @NotNull(message = "被关注者ID不能为空")
    private Long followeeId;
}
```

**响应数据**: `Result<Void>` (仅返回操作状态)

**业务逻辑**:
1. 参数校验
2. 检查关注关系是否存在
3. 更新关注状态为 `cancelled`
4. 更新用户关注统计
5. 缓存失效处理

**缓存策略**:
- 缓存失效: 关注关系缓存、统计缓存、列表缓存

**错误处理**:
- `UNFOLLOW_NOT_EXISTS`: 关注关系不存在
- `UNFOLLOW_ALREADY_CANCELLED`: 已经取消关注

---

### 2. 关注状态查询

#### 2.1 检查关注状态

**方法签名**: `Result<Boolean> checkFollowStatus(Long followerId, Long followeeId)`

**功能描述**: 查询用户是否已关注目标用户

**请求参数**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| followerId | Long | ✅ | 关注者ID |
| followeeId | Long | ✅ | 被关注者ID |

**响应数据**: `Result<Boolean>` (true: 已关注, false: 未关注)

**业务逻辑**:
1. 参数校验
2. 查询关注关系表（仅查询active状态）
3. 返回关注状态

**缓存策略**:
- 缓存键: `follow:status:{followerId}:{followeeId}`
- 缓存时间: 30分钟
- 缓存失效: 关注/取消关注时失效

---

#### 2.2 获取关注关系详情

**方法签名**: `Result<FollowResponse> getFollowRelation(Long followerId, Long followeeId)`

**功能描述**: 获取关注关系的详细信息

**请求参数**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| followerId | Long | ✅ | 关注者ID |
| followeeId | Long | ✅ | 被关注者ID |

**响应数据**: `Result<FollowResponse>` (完整的关注关系信息)

**业务逻辑**:
1. 参数校验
2. 查询关注关系详细信息
3. 返回完整的关注关系数据

**缓存策略**:
- 缓存键: `follow:detail:{followerId}:{followeeId}`
- 缓存时间: 10分钟

---

### 3. 分页查询

#### 3.1 分页查询关注记录

**方法签名**: `Result<PageResponse<FollowResponse>> queryFollows(FollowQueryRequest request)`

**功能描述**: 支持按关注者、被关注者、状态等条件查询

**请求参数**:
```java
public class FollowQueryRequest {
    private Long followerId;        // 关注者ID（可选）
    private Long followeeId;        // 被关注者ID（可选）
    private String status;          // 关注状态（可选）
    private Integer currentPage;    // 页码（默认1）
    private Integer pageSize;       // 页面大小（默认20）
    private String sortBy;          // 排序字段（可选）
    private String sortOrder;       // 排序方向（可选）
}
```

**响应数据**: `Result<PageResponse<FollowResponse>>` (分页关注记录列表)

**业务逻辑**:
1. 参数校验和默认值设置
2. 构建动态查询条件
3. 执行分页查询
4. 返回分页结果

**缓存策略**:
- 缓存键: `follow:query:{hashCode(request)}`
- 缓存时间: 5分钟

---

#### 3.2 获取用户的关注列表

**方法签名**: `Result<PageResponse<FollowResponse>> getFollowing(Long followerId, Integer currentPage, Integer pageSize)`

**功能描述**: 查询某用户关注的所有人（我关注了谁）

**请求参数**:
| 参数名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| followerId | Long | ✅ | - | 关注者ID |
| currentPage | Integer | ❌ | 1 | 页码 |
| pageSize | Integer | ❌ | 20 | 页面大小 |

**响应数据**: `Result<PageResponse<FollowResponse>>` (关注列表分页数据)

**业务逻辑**:
1. 参数校验
2. 查询用户的关注列表（status = 'active'）
3. 按关注时间倒序排列
4. 返回分页结果

**缓存策略**:
- 缓存键: `follow:following:{followerId}:{currentPage}:{pageSize}`
- 缓存时间: 10分钟

---

#### 3.3 获取用户的粉丝列表

**方法签名**: `Result<PageResponse<FollowResponse>> getFollowers(Long followeeId, Integer currentPage, Integer pageSize)`

**功能描述**: 查询关注某用户的所有人（谁关注了我）

**请求参数**:
| 参数名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| followeeId | Long | ✅ | - | 被关注者ID |
| currentPage | Integer | ❌ | 1 | 页码 |
| pageSize | Integer | ❌ | 20 | 页面大小 |

**响应数据**: `Result<PageResponse<FollowResponse>>` (粉丝列表分页数据)

**业务逻辑**:
1. 参数校验
2. 查询关注该用户的粉丝列表（status = 'active'）
3. 按关注时间倒序排列
4. 返回分页结果

**缓存策略**:
- 缓存键: `follow:followers:{followeeId}:{currentPage}:{pageSize}`
- 缓存时间: 10分钟

---

### 4. 统计信息

#### 4.1 获取用户关注数量

**方法签名**: `Result<Long> getFollowingCount(Long followerId)`

**功能描述**: 统计用户关注的人数

**请求参数**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| followerId | Long | ✅ | 关注者ID |

**响应数据**: `Result<Long>` (关注数量)

**业务逻辑**:
1. 参数校验
2. 统计用户关注的活跃关注关系数量
3. 返回统计结果

**缓存策略**:
- 缓存键: `follow:following:count:{followerId}`
- 缓存时间: 30分钟

---

#### 4.2 获取用户粉丝数量

**方法签名**: `Result<Long> getFollowersCount(Long followeeId)`

**功能描述**: 统计关注某用户的人数

**请求参数**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| followeeId | Long | ✅ | 被关注者ID |

**响应数据**: `Result<Long>` (粉丝数量)

**业务逻辑**:
1. 参数校验
2. 统计关注该用户的活跃关注关系数量
3. 返回统计结果

**缓存策略**:
- 缓存键: `follow:followers:count:{followeeId}`
- 缓存时间: 30分钟

---

#### 4.3 获取用户关注统计信息

**方法签名**: `Result<Map<String, Object>> getFollowStatistics(Long userId)`

**功能描述**: 包含关注数和粉丝数的完整统计

**请求参数**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| userId | Long | ✅ | 用户ID |

**响应数据**: `Result<Map<String, Object>>` (统计信息Map)

**响应示例**:
```json
{
  "followingCount": 120,
  "followersCount": 85,
  "mutualFollowCount": 45,
  "recentFollowingCount": 10,
  "recentFollowersCount": 8
}
```

**业务逻辑**:
1. 参数校验
2. 并行查询关注数和粉丝数
3. 查询互相关注数量
4. 查询最近7天新增关注/粉丝数
5. 返回综合统计信息

**缓存策略**:
- 缓存键: `follow:statistics:{userId}`
- 缓存时间: 10分钟

---

### 5. 批量操作

#### 5.1 批量检查关注状态

**方法签名**: `Result<Map<Long, Boolean>> batchCheckFollowStatus(Long followerId, List<Long> followeeIds)`

**功能描述**: 检查用户对多个目标用户的关注状态

**请求参数**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| followerId | Long | ✅ | 关注者ID |
| followeeIds | List&lt;Long&gt; | ✅ | 被关注者ID列表（最多100个） |

**响应数据**: `Result<Map<Long, Boolean>>` (followeeId -> isFollowing 的映射)

**业务逻辑**:
1. 参数校验（ID列表不能超过100个）
2. 批量查询关注关系状态
3. 构建结果映射
4. 返回批量检查结果

**缓存策略**:
- 单个关注关系缓存复用
- 批量结果缓存5分钟

**限制规则**:
- 单次最多检查100个用户
- 超过限制返回参数错误

---

### 6. 高级功能

#### 6.1 获取互相关注的好友

**方法签名**: `Result<PageResponse<FollowResponse>> getMutualFollows(Long userId, Integer currentPage, Integer pageSize)`

**功能描述**: 查询双向关注关系（互关好友）

**请求参数**:
| 参数名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| userId | Long | ✅ | - | 用户ID |
| currentPage | Integer | ❌ | 1 | 页码 |
| pageSize | Integer | ❌ | 20 | 页面大小 |

**响应数据**: `Result<PageResponse<FollowResponse>>` (互关好友列表)

**业务逻辑**:
1. 参数校验
2. 查询用户关注的人列表
3. 查询关注该用户的人列表
4. 计算交集得到互关好友
5. 返回分页结果

**缓存策略**:
- 缓存键: `follow:mutual:{userId}:{currentPage}:{pageSize}`
- 缓存时间: 15分钟

---

#### 6.2 根据昵称搜索关注关系

**方法签名**: `Result<PageResponse<FollowResponse>> searchByNickname(Long followerId, Long followeeId, String nicknameKeyword, Integer currentPage, Integer pageSize)`

**功能描述**: 根据关注者或被关注者昵称进行模糊搜索

**请求参数**:
| 参数名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| followerId | Long | ❌ | - | 关注者ID（可选） |
| followeeId | Long | ❌ | - | 被关注者ID（可选） |
| nicknameKeyword | String | ✅ | - | 昵称关键词 |
| currentPage | Integer | ❌ | 1 | 页码 |
| pageSize | Integer | ❌ | 20 | 页面大小 |

**响应数据**: `Result<PageResponse<FollowResponse>>` (搜索结果列表)

**业务逻辑**:
1. 参数校验（关键词不能为空）
2. 构建模糊搜索条件
3. 使用全文索引进行搜索
4. 返回搜索结果

**缓存策略**:
- 缓存键: `follow:search:{hashCode(params)}`
- 缓存时间: 5分钟

**搜索规则**:
- 支持关注者昵称模糊搜索
- 支持被关注者昵称模糊搜索
- 支持组合条件搜索

---

### 7. 管理功能

#### 7.1 清理已取消的关注记录

**方法签名**: `Result<Integer> cleanCancelledFollows(Integer days)`

**功能描述**: 物理删除cancelled状态的记录（可选功能）

**请求参数**:
| 参数名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| days | Integer | ❌ | 30 | 保留天数 |

**响应数据**: `Result<Integer>` (清理的记录数量)

**业务逻辑**:
1. 参数校验（保留天数 > 0）
2. 查询需要清理的记录
3. 执行物理删除
4. 返回清理数量

**安全限制**:
- 只能清理cancelled状态的记录
- 必须超过指定保留天数
- 单次最多清理1000条记录

---

#### 7.2 更新用户信息（冗余字段同步）

**方法签名**: `Result<Integer> updateUserInfo(Long userId, String nickname, String avatar)`

**功能描述**: 当用户信息变更时，同步更新关注表中的冗余信息

**请求参数**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| userId | Long | ✅ | 用户ID |
| nickname | String | ❌ | 新昵称 |
| avatar | String | ❌ | 新头像 |

**响应数据**: `Result<Integer>` (更新成功的记录数)

**业务逻辑**:
1. 参数校验
2. 更新该用户作为关注者的记录
3. 更新该用户作为被关注者的记录
4. 缓存失效处理
5. 返回更新记录数

**缓存策略**:
- 失效相关的关注关系缓存
- 失效相关的列表缓存

---

### 8. 验证和检查

#### 8.1 查询用户间的关注关系链

**方法签名**: `Result<List<FollowResponse>> getRelationChain(Long userIdA, Long userIdB)`

**功能描述**: 检查两个用户之间的双向关注关系

**请求参数**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| userIdA | Long | ✅ | 用户A ID |
| userIdB | Long | ✅ | 用户B ID |

**响应数据**: `Result<List<FollowResponse>>` (关注关系链列表，0-2条记录)

**业务逻辑**:
1. 参数校验
2. 查询A关注B的关系
3. 查询B关注A的关系
4. 返回存在的关注关系

**缓存策略**:
- 缓存键: `follow:relation:chain:{userIdA}:{userIdB}`
- 缓存时间: 10分钟

---

#### 8.2 验证关注请求参数

**方法签名**: `Result<String> validateFollowRequest(FollowCreateRequest request)`

**功能描述**: 校验请求参数的有效性

**请求参数**: `FollowCreateRequest` (关注请求对象)

**响应数据**: `Result<String>` (验证结果信息)

**验证规则**:
1. 参数非空校验
2. 用户ID有效性校验
3. 用户存在性校验
4. 业务规则校验（不能关注自己等）

**返回信息**:
- 成功: "参数验证通过"
- 失败: 具体的错误信息

---

#### 8.3 检查是否可以关注

**方法签名**: `Result<String> checkCanFollow(Long followerId, Long followeeId)`

**功能描述**: 检查业务规则是否允许关注

**请求参数**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| followerId | Long | ✅ | 关注者ID |
| followeeId | Long | ✅ | 被关注者ID |

**响应数据**: `Result<String>` (检查结果信息)

**检查规则**:
1. 用户是否存在
2. 是否尝试关注自己
3. 是否已经关注
4. 用户是否被拉黑等

**返回信息**:
- 成功: "可以关注"
- 失败: 具体的限制原因

---

#### 8.4 检查是否已经存在关注关系

**方法签名**: `Result<Boolean> existsFollowRelation(Long followerId, Long followeeId)`

**功能描述**: 包括已取消的关注关系

**请求参数**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| followerId | Long | ✅ | 关注者ID |
| followeeId | Long | ✅ | 被关注者ID |

**响应数据**: `Result<Boolean>` (是否存在关注关系)

**业务逻辑**:
1. 查询所有状态的关注关系
2. 返回是否存在记录

**与checkFollowStatus的区别**:
- `checkFollowStatus`: 只检查active状态
- `existsFollowRelation`: 检查所有状态（包括cancelled）

---

#### 8.5 重新激活已取消的关注关系

**方法签名**: `Result<Boolean> reactivateFollow(Long followerId, Long followeeId)`

**功能描述**: 将cancelled状态的关注重新设置为active

**请求参数**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| followerId | Long | ✅ | 关注者ID |
| followeeId | Long | ✅ | 被关注者ID |

**响应数据**: `Result<Boolean>` (是否成功重新激活)

**业务逻辑**:
1. 查找cancelled状态的关注关系
2. 更新状态为active
3. 更新关注时间
4. 缓存失效处理
5. 更新统计信息

**缓存策略**:
- 失效相关关注关系缓存
- 失效统计信息缓存

---

## 缓存架构

### 缓存分层

1. **L1 缓存（本地缓存）**:
   - 热点数据
   - 缓存时间: 1-5分钟

2. **L2 缓存（Redis）**:
   - 分布式缓存
   - 缓存时间: 5-30分钟

### 缓存键设计

| 缓存类型 | 缓存键格式 | 过期时间 |
|----------|------------|----------|
| 关注状态 | `follow:status:{followerId}:{followeeId}` | 30分钟 |
| 关注详情 | `follow:detail:{followerId}:{followeeId}` | 10分钟 |
| 关注列表 | `follow:following:{followerId}:{page}:{size}` | 10分钟 |
| 粉丝列表 | `follow:followers:{followeeId}:{page}:{size}` | 10分钟 |
| 统计信息 | `follow:statistics:{userId}` | 10分钟 |
| 互关列表 | `follow:mutual:{userId}:{page}:{size}` | 15分钟 |

### 缓存失效策略

1. **主动失效**:
   - 关注/取消关注时失效相关缓存
   - 用户信息更新时失效相关缓存

2. **被动失效**:
   - TTL 自动过期
   - 内存不足时 LRU 淘汰

## 错误码规范

### 通用错误码

| 错误码 | 说明 | HTTP状态码 |
|--------|------|------------|
| SUCCESS | 操作成功 | 200 |
| INVALID_PARAM | 参数无效 | 400 |
| USER_NOT_EXIST | 用户不存在 | 404 |
| SYSTEM_ERROR | 系统错误 | 500 |

### 关注业务错误码

| 错误码 | 说明 | HTTP状态码 |
|--------|------|------------|
| FOLLOW_ALREADY_EXISTS | 关注关系已存在 | 400 |
| FOLLOW_SELF_NOT_ALLOWED | 不能关注自己 | 400 |
| FOLLOW_LIMIT_EXCEEDED | 关注数量超过限制 | 400 |
| UNFOLLOW_NOT_EXISTS | 关注关系不存在 | 404 |
| UNFOLLOW_ALREADY_CANCELLED | 已经取消关注 | 400 |
| BATCH_SIZE_EXCEEDED | 批量操作数量超限 | 400 |

## 性能指标

### 响应时间要求

| 操作类型 | 响应时间要求 | 说明 |
|----------|-------------|------|
| 单个查询 | < 50ms | 缓存命中时 |
| 列表查询 | < 200ms | 分页查询 |
| 批量操作 | < 500ms | 100个以内 |
| 统计查询 | < 100ms | 缓存优化 |

### 并发能力

- **QPS**: 10,000+
- **并发用户**: 50,000+
- **缓存命中率**: > 95%

## 监控指标

### 关键指标

1. **业务指标**:
   - 关注成功率
   - 查询响应时间
   - 缓存命中率

2. **技术指标**:
   - 接口调用量
   - 错误率
   - 数据库连接数

3. **告警规则**:
   - 关注成功率 < 99%
   - 平均响应时间 > 200ms
   - 缓存命中率 < 90%

## 使用示例

### Java 调用示例

```java
@Service
public class FollowBusinessService {
    
    @DubboReference
    private FollowFacadeService followFacadeService;
    
    /**
     * 用户关注操作
     */
    public boolean followUser(Long followerId, Long followeeId) {
        FollowCreateRequest request = new FollowCreateRequest();
        request.setFollowerId(followerId);
        request.setFolloweeId(followeeId);
        
        Result<FollowResponse> result = followFacadeService.followUser(request);
        return result.getSuccess();
    }
    
    /**
     * 获取用户关注列表
     */
    public PageResponse<FollowResponse> getUserFollowing(Long userId, int page, int size) {
        Result<PageResponse<FollowResponse>> result = 
            followFacadeService.getFollowing(userId, page, size);
        
        if (result.getSuccess()) {
            return result.getData();
        }
        return new PageResponse<>();
    }
    
    /**
     * 批量检查关注状态
     */
    public Map<Long, Boolean> batchCheckFollowStatus(Long userId, List<Long> targetIds) {
        Result<Map<Long, Boolean>> result = 
            followFacadeService.batchCheckFollowStatus(userId, targetIds);
        
        if (result.getSuccess()) {
            return result.getData();
        }
        return Collections.emptyMap();
    }
}
```

## 版本变更记录

### v2.0.0 (2024-01-01)
- ✅ 完整的关注功能实现
- ✅ 缓存优化和性能提升
- ✅ 批量操作支持
- ✅ 完善的错误处理
- ✅ 冗余字段设计

### v1.0.0 (2023-12-01)
- ✅ 基础关注功能
- ✅ 简单的查询接口

---

**总结**: Follow Facade API提供了20个核心方法，覆盖关注管理的所有业务场景，具有完整的缓存优化、错误处理和性能保障，支持高并发和大用户量的业务需求。