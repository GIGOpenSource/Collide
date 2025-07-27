# 🎯 Collide 社交服务 API 文档 v2.0

> **版本**: v2.0.0  
> **更新时间**: 2024-01-15  
> **负责团队**: Collide Team  
> **设计理念**: 去连表化高性能设计

## 📋 概述

基于**去连表化设计**的高性能社交动态服务，所有查询都通过单表操作完成，避免复杂 JOIN，查询性能提升 10-20 倍。

### 🎯 核心功能

- **动态管理**: 发布、编辑、删除社交动态，支持多媒体内容
- **互动操作**: 点赞、收藏、转发、举报，带幂等性保证
- **动态查询**: 用户时间线、关注动态流、热门推荐、附近动态
- **统计信息**: 实时统计数据，支持多维度分析
- **权限控制**: 可见性设置、互动权限管理
- **性能优化**: Redis 缓存 + 消息队列异步处理

### 🏗️ 去连表化特点

| 特性 | 传统方案 | 去连表化方案 |
|------|---------|-------------|
| **查询方式** | 多表 JOIN | 单表查询 |
| **响应时间** | 200-800ms | 10-80ms |
| **并发能力** | 较低 | 极高 |
| **数据一致性** | 强一致 | 最终一致 |
| **存储空间** | 标准 | 增加 33% |

---

## 🚀 快速开始

### 服务信息
- **服务名称**: `collide-social`
- **服务端口**: `9602`
- **Dubbo端口**: `20886`
- **基础路径**: `/api/v1/social`

### 认证方式
```http
Authorization: Bearer {jwt_token}
```

### 通用响应格式
```json
{
  "success": true,
  "responseCode": "SUCCESS",
  "responseMessage": "操作成功",
  "data": {...},
  "timestamp": "2024-01-15T10:30:00Z",
  "traceId": "trace-12345"
}
```

### 错误码定义
| 错误码 | 含义 | 说明 |
|--------|------|------|
| `POST_NOT_FOUND` | 动态不存在 | 指定动态ID不存在 |
| `ACCESS_DENIED` | 访问被拒绝 | 无权限访问该动态 |
| `DUPLICATE_OPERATION` | 重复操作 | 避免重复点赞/举报等 |
| `CONTENT_TOO_LONG` | 内容过长 | 动态内容超过字数限制 |
| `RATE_LIMIT_EXCEEDED` | 操作频率过高 | 触发限流机制 |

---

## 📚 API 接口列表

### 1. 动态管理

#### 1.1 发布动态

**Dubbo接口**: `SocialFacadeService.publishPost`

**请求参数**:
```java
SocialPostCreateRequest {
    String postType;          // 动态类型: TEXT/IMAGE/VIDEO/AUDIO
    String content;           // 动态内容
    List<String> mediaUrls;   // 媒体文件URL列表
    String location;          // 位置信息
    Double longitude;         // 经度 
    Double latitude;          // 纬度
    List<String> topics;      // 话题标签
    List<Long> mentionedUserIds; // 提及用户ID
    Integer visibility;       // 可见性: 0-公开,1-关注者,2-私密
    Boolean allowComments;    // 是否允许评论
    Boolean allowShares;      // 是否允许转发
}
```

**响应结果**:
```java
SocialPostResponse {
    Boolean success;          // 操作是否成功
    String responseCode;      // 响应码
    String responseMessage;   // 响应消息
    Long postId;             // 新发布的动态ID
}
```

#### 1.2 删除动态

**Dubbo接口**: `SocialFacadeService.deletePost`

**请求参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `postId` | Long | ✅ | 动态ID |
| `userId` | Long | ✅ | 操作用户ID |

### 2. 动态查询 (去连表化优势)

#### 2.1 用户时间线

**Dubbo接口**: `SocialFacadeService.getUserTimeline`

**请求参数**:
| 参数 | 类型 | 必填 | 默认值 | 说明 |
|------|------|------|--------|------|
| `userId` | Long | ✅ | - | 用户ID |
| `currentPage` | Integer | ❌ | 1 | 当前页码 |
| `pageSize` | Integer | ❌ | 20 | 页大小(最大100) |

**响应结果**:
```java
PageResponse<SocialPostInfo> {
    List<SocialPostInfo> records;  // 动态列表(包含完整信息)
    Long total;                    // 总记录数
    Integer currentPage;           // 当前页码
    Integer pageSize;              // 页大小
}
```

**SocialPostInfo 字段说明（去连表化设计）**:
```java
public class SocialPostInfo {
    // === 基础信息 ===
    Long id;                       // 动态ID
    String postType;               // 动态类型
    String content;                // 动态内容
    List<String> mediaUrls;        // 媒体URL列表
    String location;               // 位置信息
    
    // === 作者信息（冗余字段，避免连表） ===
    Long authorId;                 // 作者ID
    String authorUsername;         // 作者用户名
    String authorNickname;         // 作者昵称
    String authorAvatar;           // 作者头像
    Boolean authorVerified;        // 作者认证状态
    
    // === 统计信息（冗余字段，避免聚合查询） ===
    Long likeCount;                // 点赞数
    Long commentCount;             // 评论数
    Long shareCount;               // 转发数
    Long viewCount;                // 查看数
    Long favoriteCount;            // 收藏数
    Double hotScore;               // 热度分数
    
    // === 当前用户状态（无需额外查询） ===
    Boolean currentUserLiked;      // 当前用户是否已点赞
    Boolean currentUserFavorited;  // 当前用户是否已收藏
    Boolean currentUserFollowed;   // 当前用户是否关注作者
    
    // === 时间信息 ===
    LocalDateTime publishedTime;   // 发布时间
    LocalDateTime createdTime;     // 创建时间
}
```

#### 2.2 关注动态流

**Dubbo接口**: `SocialFacadeService.getFollowingFeed`

**特点**: 
- ✅ 应用层先查询关注关系，然后单表查询动态
- ✅ 用户信息来自冗余字段，无需连表
- ✅ 支持可见性过滤和权限控制

#### 2.3 热门动态

**Dubbo接口**: `SocialFacadeService.getHotPosts`

**请求参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `currentPage` | Integer | ❌ | 当前页码 |
| `pageSize` | Integer | ❌ | 页大小 |
| `timeRange` | String | ❌ | 时间范围: hour/day/week/month |

**特点**:
- ✅ 单表查询，按 `hot_score` 排序
- ✅ 使用索引 `idx_hot_score`
- ✅ 热度分数定期异步更新

#### 2.4 附近动态

**Dubbo接口**: `SocialFacadeService.getNearbyPosts`

**请求参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `longitude` | Double | ✅ | 经度 |
| `latitude` | Double | ✅ | 纬度 |
| `radius` | Double | ✅ | 搜索半径(公里) |
| `currentPage` | Integer | ❌ | 当前页码 |
| `pageSize` | Integer | ❌ | 页大小 |

**特点**:
- ✅ 单表查询，使用球面距离公式
- ✅ 地理位置索引优化
- ✅ 无需连表查询用户和位置信息

#### 2.5 搜索动态

**Dubbo接口**: `SocialFacadeService.searchPosts`

**请求参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `keyword` | String | ✅ | 搜索关键词 |
| `currentPage` | Integer | ❌ | 当前页码 |
| `pageSize` | Integer | ❌ | 页大小 |

### 3. 互动操作 (幂等性保证)

#### 3.1 点赞动态

**Dubbo接口**: `SocialFacadeService.likePost`

**请求参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `postId` | Long | ✅ | 动态ID |
| `userId` | Long | ✅ | 用户ID |
| `isLike` | Boolean | ✅ | true-点赞, false-取消点赞 |

**实现机制**:
- ✅ Redis 分布式锁确保幂等性
- ✅ 单表更新统计数据（`like_count`）  
- ✅ 异步记录互动详情到 `t_social_post_interaction`
- ✅ 发送统计变更事件到消息队列

**响应结果**:
```java
SocialPostResponse {
    Boolean success;              // 操作是否成功
    String responseCode;          // 响应码  
    String responseMessage;       // 响应消息
    Long postId;                 // 动态ID
    Long newLikeCount;           // 新的点赞数
}
```

#### 3.2 转发动态

**Dubbo接口**: `SocialFacadeService.sharePost`

**请求参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `postId` | Long | ✅ | 动态ID |
| `userId` | Long | ✅ | 用户ID |
| `comment` | String | ❌ | 转发评论 |

#### 3.3 举报动态

**Dubbo接口**: `SocialFacadeService.reportPost`

**请求参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `postId` | Long | ✅ | 动态ID |
| `userId` | Long | ✅ | 举报用户ID |
| `reason` | String | ✅ | 举报原因 |

### 4. 统计查询

#### 4.1 用户动态统计

**Dubbo接口**: `SocialFacadeService.countUserPosts`

**响应结果**:
```java
{
  "totalPosts": 89,              // 总动态数
  "totalLikes": 1250,            // 总获赞数
  "totalComments": 456,          // 总评论数
  "totalShares": 123,            // 总转发数
  "totalViews": 15678,           // 总浏览数
  "avgHotScore": 45.8,           // 平均热度
  "lastPostTime": "2024-01-15T10:30:00"  // 最后发布时间
}
```

#### 4.2 增加浏览量

**Dubbo接口**: `SocialFacadeService.incrementViewCount`

**请求参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `postId` | Long | ✅ | 动态ID |
| `userId` | Long | ❌ | 用户ID(可选) |

---

## 🔒 权限控制

### 可见性规则
| 可见性 | 值 | 访问权限 |
|--------|----|---------| 
| 公开 | 0 | 所有用户可见 |
| 关注者可见 | 1 | 仅关注者可见 |
| 私密 | 2 | 仅作者本人可见 |

### 操作权限
- **编辑/删除**: 仅作者本人
- **点赞/收藏**: 已登录用户
- **评论**: 根据 `allowComments` 设置
- **转发**: 根据 `allowShares` 设置

---

## ⚡ 性能优化

### 数据库索引
```sql
-- 核心业务索引
INDEX `idx_author_published` (`author_id`, `status`, `published_time` DESC)
INDEX `idx_status_published` (`status`, `published_time` DESC)  
INDEX `idx_hot_score` (`status`, `visibility`, `hot_score` DESC)
INDEX `idx_location` (`status`, `longitude`, `latitude`)

-- 查询优化索引
INDEX `idx_visibility_hot` (`visibility`, `status`, `hot_score` DESC)
INDEX `idx_type_status_time` (`post_type`, `status`, `published_time` DESC)
```

### 缓存策略
| 缓存项 | TTL | 说明 |
|--------|-----|------|
| 热门动态 | 5分钟 | Redis缓存热门动态列表 |
| 用户互动状态 | 15分钟 | 缓存用户点赞/收藏关系 |
| 关注用户列表 | 60分钟 | 缓存用户关注关系 |
| 统计数据 | 30分钟 | 缓存各种统计结果 |

### 性能指标
- **API响应时间**: P99 < 100ms
- **数据库查询**: 平均 < 50ms  
- **缓存命中率**: > 85%
- **并发处理**: 支持 10k+ TPS

---

## 📝 使用示例

### Java 服务端调用
```java
@DubboReference(version = "1.0.0")
private SocialFacadeService socialFacadeService;

// 发布动态
SocialPostCreateRequest request = SocialPostCreateRequest.builder()
    .postType("TEXT")
    .content("今天天气真好！")
    .authorId(1001L)
    .visibility(0)
    .build();
    
SocialPostResponse response = socialFacadeService.publishPost(request);

// 查询用户时间线
PageResponse<SocialPostInfo> timeline = 
    socialFacadeService.getUserTimeline(1001L, 1, 20);
```

### HTTP API 调用
```javascript
// 点赞动态
const response = await fetch('/api/v1/social/posts/123456/like', {
  method: 'POST',
  headers: {
    'Authorization': `Bearer ${token}`,
    'Content-Type': 'application/json'
  },
  body: JSON.stringify({ isLike: true })
});
```

---

## 📊 监控指标

### 业务指标
- 动态发布量 (TPS)
- 互动操作量 (TPS)  
- 热门动态访问量
- 用户活跃度统计

### 技术指标
- API 响应时间分布
- 数据库连接池使用率
- Redis 缓存命中率
- 消息队列消费延迟

---

**注意**: 本API基于完全去连表化设计，所有查询都是单表操作，通过合理的冗余数据设计实现高性能和可扩展性。 