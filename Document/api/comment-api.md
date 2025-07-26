# 💬 Collide 评论服务 API 文档

> **版本**: v2.0.0  
> **更新时间**: 2024-12-19  
> **负责团队**: Collide Team

## 📋 概述

评论服务提供完整的评论互动功能，采用**完全去连表化设计**，支持多级嵌套评论、评论点赞、树形结构展示等。通过冗余存储关联信息和优化索引，实现极致的查询性能。

### 🎯 核心功能

- **评论发表**: 支持文字评论、图片、@用户、地理位置等
- **多级回复**: 支持树形结构的嵌套回复
- **评论互动**: 评论点赞、举报、删除等操作
- **树形展示**: 高效的评论树查询和渲染（<20ms）
- **权限控制**: 基于用户角色的评论权限管理
- **内容安全**: 敏感词过滤 + AI审核双重保障
- **实时更新**: RocketMQ异步事件驱动更新

### 🏗️ 技术特点

| 特性 | 说明 |
|------|------|
| **极致性能** | 完全去连表化，单表查询性能<15ms |
| **无限嵌套** | 支持任意层级的评论回复结构 |
| **事件驱动** | RocketMQ异步消息，保证数据一致性 |
| **智能排序** | 支持时间、热度、质量等多种排序 |
| **内容安全** | 敏感词过滤 + AI审核双重保障 |
| **去连表化** | 冗余存储用户信息，避免复杂JOIN查询 |
| **幂等设计** | MD5哈希 + Redis实现接口幂等性 |
| **高可用性** | 支持服务降级和熔断保护 |

---

## 🚀 快速开始

### Base URL
```
https://api.collide.com
```

### 认证方式
```http
Authorization: Bearer {token}
```

### 通用响应格式
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {...},
  "success": true,
  "timestamp": "2024-01-01T00:00:00Z"
}
```

---

## 📚 API 接口列表

### 1. 创建评论

**接口地址**: `POST /api/v1/comments`

**功能描述**: 发表新评论或回复已有评论

#### 请求参数

```json
{
  "targetId": 67890,
  "commentType": "POST",
  "content": "这是一条很棒的动态！👍",
  "parentCommentId": null,
  "mentionedUserIds": [12345, 67891],
  "images": ["https://cdn.collide.com/img/comment1.jpg"],
  "location": "北京市朝阳区"
}
```

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `targetId` | Long | ✅ | 评论目标ID（动态、文章等） |
| `commentType` | String | ✅ | 评论类型: POST(动态), ARTICLE(文章), VIDEO(视频) |
| `content` | String | ✅ | 评论内容，1-500字符 |
| `parentCommentId` | Long | ❌ | 父评论ID，为null时表示根评论 |
| `mentionedUserIds` | Array | ❌ | @提及的用户ID列表 |
| `images` | Array | ❌ | 评论图片URL列表，最多3张 |
| `location` | String | ❌ | 评论发表位置 |

#### 响应示例

**成功响应**:
```json
{
  "code": 200,
  "message": "评论发表成功",
  "success": true,
  "data": {
    "success": true,
    "responseCode": "SUCCESS",
    "responseMessage": "评论创建成功",
    "data": {
      "commentId": 123456,
      "targetId": 67890,
      "content": "这是一条很棒的动态！👍",
      "authorId": 12345,
      "authorName": "张三",
      "authorAvatar": "https://cdn.collide.com/avatar/12345.jpg",
      "createTime": "2024-01-01T10:30:00Z",
      "likeCount": 0,
      "replyCount": 0
    }
  }
}
```

#### cURL 示例

```bash
curl -X POST "https://api.collide.com/api/v1/comments" \
  -H "Authorization: Bearer your-token" \
  -H "Content-Type: application/json" \
  -d '{
    "targetId": 67890,
    "commentType": "POST",
    "content": "这是一条很棒的动态！👍"
  }'
```

---

### 2. 删除评论

**接口地址**: `DELETE /api/v1/comments/{commentId}`

**功能描述**: 删除指定评论（仅作者或管理员可操作）

#### 路径参数

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `commentId` | Long | ✅ | 评论ID |

#### 响应示例

```json
{
  "code": 200,
  "message": "评论删除成功",
  "success": true,
  "data": {
    "success": true,
    "responseCode": "SUCCESS",
    "responseMessage": "评论删除成功"
  }
}
```

---

### 3. 点赞/取消点赞评论

**接口地址**: `POST /api/v1/comments/{commentId}/like`

**功能描述**: 对评论进行点赞或取消点赞操作

#### 路径参数

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `commentId` | Long | ✅ | 评论ID |

#### 查询参数

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `isLike` | Boolean | ✅ | true=点赞, false=取消点赞 |

#### URL 示例
```
POST /api/v1/comments/123456/like?isLike=true
```

#### 响应示例

```json
{
  "code": 200,
  "message": "点赞成功",
  "success": true,
  "data": {
    "success": true,
    "responseCode": "SUCCESS",
    "responseMessage": "点赞成功",
    "data": {
      "commentId": 123456,
      "isLiked": true,
      "likeCount": 25,
      "likeTime": "2024-01-01T10:35:00Z"
    }
  }
}
```

---

### 4. 查询评论详情

**接口地址**: `GET /api/v1/comments/{commentId}`

**功能描述**: 获取单个评论的详细信息

#### 路径参数

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `commentId` | Long | ✅ | 评论ID |

#### 响应示例

```json
{
  "code": 200,
  "message": "查询成功",
  "success": true,
  "data": {
    "id": 123456,
    "targetId": 67890,
    "commentType": "POST",
    "content": "这是一条很棒的动态！👍",
    "parentCommentId": null,
    "authorId": 12345,
    "authorName": "张三",
    "authorAvatar": "https://cdn.collide.com/avatar/12345.jpg",
    "authorVerified": true,
    "createTime": "2024-01-01T10:30:00Z",
    "likeCount": 25,
    "replyCount": 3,
    "isLiked": false,
    "images": ["https://cdn.collide.com/img/comment1.jpg"],
    "mentionedUsers": [
      {
        "userId": 67891,
        "userName": "李四",
        "userAvatar": "https://cdn.collide.com/avatar/67891.jpg"
      }
    ],
    "location": "北京市朝阳区"
  }
}
```

---

### 5. 分页查询评论列表

**接口地址**: `GET /api/v1/comments`

**功能描述**: 分页获取指定目标的评论列表

#### 查询参数

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `commentType` | String | ❌ | 评论类型 |
| `targetId` | Long | ❌ | 目标对象ID |
| `parentCommentId` | Long | ❌ | 父评论ID，查询子评论时使用 |
| `pageNum` | Integer | ❌ | 页码，默认1 |
| `pageSize` | Integer | ❌ | 页大小，默认10，最大100 |
| `sortBy` | String | ❌ | 排序字段: time(时间), hot(热度), like(点赞数) |
| `sortOrder` | String | ❌ | 排序方向: desc(降序), asc(升序) |

#### URL 示例
```
GET /api/v1/comments?targetId=67890&commentType=POST&pageNum=1&pageSize=10&sortBy=time&sortOrder=desc
```

#### 响应示例

```json
{
  "code": 200,
  "message": "查询成功",
  "success": true,
  "data": {
    "success": true,
    "datas": [
      {
        "id": 123456,
        "targetId": 67890,
        "content": "这是一条很棒的动态！👍",
        "authorId": 12345,
        "authorName": "张三",
        "authorAvatar": "https://cdn.collide.com/avatar/12345.jpg",
        "createTime": "2024-01-01T10:30:00Z",
        "likeCount": 25,
        "replyCount": 3,
        "isLiked": false
      }
    ],
    "total": 156,
    "currentPage": 1,
    "pageSize": 10,
    "totalPage": 16
  }
}
```

---

### 6. 查询评论树

**接口地址**: `GET /api/v1/comments/tree`

**功能描述**: 获取包含子评论的树形结构数据

#### 查询参数

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `commentType` | String | ❌ | 评论类型 |
| `targetId` | Long | ❌ | 目标对象ID |
| `includeChildren` | Boolean | ❌ | 是否包含子评论，默认true |
| `pageNum` | Integer | ❌ | 页码，默认1 |
| `pageSize` | Integer | ❌ | 页大小，默认10，最大50 |
| `sortBy` | String | ❌ | 排序字段，默认time |
| `sortOrder` | String | ❌ | 排序方向，默认desc |

#### URL 示例
```
GET /api/v1/comments/tree?targetId=67890&includeChildren=true&pageNum=1&pageSize=10
```

#### 响应示例

```json
{
  "code": 200,
  "message": "查询成功",
  "success": true,
  "data": {
    "success": true,
    "datas": [
      {
        "id": 123456,
        "targetId": 67890,
        "content": "这是一条很棒的动态！👍",
        "authorId": 12345,
        "authorName": "张三",
        "authorAvatar": "https://cdn.collide.com/avatar/12345.jpg",
        "createTime": "2024-01-01T10:30:00Z",
        "likeCount": 25,
        "replyCount": 3,
        "isLiked": false,
        "children": [
          {
            "id": 123457,
            "parentCommentId": 123456,
            "content": "我也觉得很棒！",
            "authorId": 67891,
            "authorName": "李四",
            "authorAvatar": "https://cdn.collide.com/avatar/67891.jpg",
            "createTime": "2024-01-01T10:35:00Z",
            "likeCount": 8,
            "replyCount": 0,
            "isLiked": true,
            "children": []
          }
        ]
      }
    ],
    "total": 45,
    "currentPage": 1,
    "pageSize": 10,
    "totalPage": 5
  }
}
```

---

### 7. 统计评论数量

**接口地址**: `GET /api/v1/comments/count`

**功能描述**: 获取指定目标的评论总数

#### 查询参数

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `targetId` | Long | ✅ | 目标对象ID |
| `commentType` | String | ❌ | 评论类型筛选 |

#### URL 示例
```
GET /api/v1/comments/count?targetId=67890&commentType=CONTENT
```

#### 响应示例

```json
{
  "code": 200,
  "message": "统计成功",
  "success": true,
  "data": 156
}
```

---

### 8. 查询用户评论历史

**接口地址**: `GET /api/v1/comments/user/{userId}`

**功能描述**: 查询指定用户的评论历史记录

#### 路径参数

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `userId` | Long | ✅ | 用户ID |

#### 查询参数

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `commentType` | String | ❌ | 评论类型筛选：CONTENT, DYNAMIC |
| `pageNum` | Integer | ❌ | 页码，默认1 |
| `pageSize` | Integer | ❌ | 页大小，默认10，最大50 |

#### URL 示例
```
GET /api/v1/comments/user/12345?commentType=CONTENT&pageNum=1&pageSize=10
```

#### 响应示例

```json
{
  "code": 200,
  "message": "查询成功",
  "success": true,
  "data": {
    "success": true,
    "datas": [
      {
        "id": 123456,
        "targetId": 67890,
        "content": "这是一条很棒的动态！👍",
        "authorId": 12345,
        "authorName": "张三",
        "authorAvatar": "https://cdn.collide.com/avatar/12345.jpg",
        "createTime": "2024-01-01T10:30:00Z",
        "likeCount": 25,
        "replyCount": 3
      }
    ],
    "total": 45,
    "currentPage": 1,
    "pageSize": 10,
    "totalPage": 5
  }
}
```

---

### 9. 查询热门评论

**接口地址**: `GET /api/v1/comments/hot`

**功能描述**: 查询指定目标的热门评论列表

#### 查询参数

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `targetId` | Long | ✅ | 目标对象ID |
| `commentType` | String | ✅ | 评论类型：CONTENT, DYNAMIC |
| `limit` | Integer | ❌ | 查询数量限制，默认10，最大50 |

#### URL 示例
```
GET /api/v1/comments/hot?targetId=67890&commentType=CONTENT&limit=10
```

#### 响应示例

```json
{
  "code": 200,
  "message": "查询成功",
  "success": true,
  "data": [
    {
      "id": 123456,
      "targetId": 67890,
      "content": "这是一条很棒的动态！👍",
      "authorId": 12345,
      "authorName": "张三",
      "authorAvatar": "https://cdn.collide.com/avatar/12345.jpg",
      "createTime": "2024-01-01T10:30:00Z",
      "likeCount": 125,
      "replyCount": 23,
      "isHot": true,
      "qualityScore": 4.8
    }
  ]
}
```

---

### 10. 获取评论统计信息

**接口地址**: `GET /api/v1/comments/statistics`

**功能描述**: 获取指定目标的详细评论统计信息

#### 查询参数

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `targetId` | Long | ✅ | 目标对象ID |
| `commentType` | String | ✅ | 评论类型：CONTENT, DYNAMIC |

#### URL 示例
```
GET /api/v1/comments/statistics?targetId=67890&commentType=CONTENT
```

#### 响应示例

```json
{
  "code": 200,
  "message": "查询成功",
  "success": true,
  "data": {
    "totalLikes": 1250,
    "totalReplies": 345,
    "totalComments": 156,
    "lastCommentTime": "2024-01-01T15:30:00Z",
    "hotComments": 8,
    "averageQualityScore": 3.8
  }
}
```

---

### 11. 检查用户点赞状态

**接口地址**: `GET /api/v1/comments/{commentId}/liked`

**功能描述**: 检查当前用户是否已点赞指定评论

#### 路径参数

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `commentId` | Long | ✅ | 评论ID |

#### 响应示例

```json
{
  "code": 200,
  "message": "查询成功",
  "success": true,
  "data": true
}
```

---

## 📊 数据模型

### CommentInfo 评论信息（完全去连表化设计）

```json
{
  "id": "Long - 评论ID",
  "targetId": "Long - 目标对象ID",
  "commentType": "String - 评论类型：CONTENT, DYNAMIC",
  "content": "String - 评论内容",
  "parentCommentId": "Long - 父评论ID，0表示根评论",
  "rootCommentId": "Long - 根评论ID，用于树形结构",
  
  // 用户信息（冗余存储，避免连表查询）
  "userId": "Long - 评论用户ID",
  "userNickname": "String - 用户昵称（冗余）",
  "userAvatar": "String - 用户头像（冗余）",
  "userVerified": "Boolean - 用户认证状态（冗余）",
  
  // 回复信息（冗余存储）
  "replyToUserId": "Long - 回复目标用户ID",
  "replyToUserNickname": "String - 回复目标用户昵称（冗余）",
  
  // 状态信息
  "status": "String - 评论状态：NORMAL, HIDDEN, DELETED, PENDING",
  "auditStatus": "String - 审核状态：PASS, REJECT, PENDING",
  "auditReason": "String - 审核原因",
  
  // 统计信息（冗余存储，避免连表统计）
  "likeCount": "Integer - 点赞数",
  "replyCount": "Integer - 回复数",
  "reportCount": "Integer - 举报数",
  
  // 标识信息
  "isPinned": "Boolean - 是否置顶",
  "isHot": "Boolean - 是否热门",
  "isEssence": "Boolean - 是否精华",
  "qualityScore": "Decimal - 评论质量分数（0-5.00）",
  
  // 扩展信息
  "ipAddress": "String - IP地址",
  "deviceInfo": "String - 设备信息",
  "location": "String - 地理位置",
  "images": "Array - 评论图片列表",
  "mentionUserIds": "Array - 提及的用户ID列表",
  "extraData": "Object - 扩展数据",
  
  // 时间信息
  "createTime": "LocalDateTime - 创建时间",
  "updateTime": "LocalDateTime - 更新时间",
  
  // 用户相关状态（运行时计算）
  "isLiked": "Boolean - 当前用户是否已点赞",
  "children": "Array - 子评论列表（树形结构时）"
}
```

### CommentCreateRequest 创建评论请求

```json
{
  "targetId": "Long - 目标对象ID",
  "commentType": "String - 评论类型：CONTENT, DYNAMIC", 
  "content": "String - 评论内容（1-2000字符）",
  "parentCommentId": "Long - 父评论ID，null表示根评论",
  "replyToUserId": "Long - 回复目标用户ID",
  "mentionUserIds": "Array - 提及用户ID列表",
  "images": "Array - 图片URL列表，最多9张",
  "location": "String - 地理位置信息",
  "extraData": "Object - 扩展数据"
}
```

### CommentStatistics 评论统计信息

```json
{
  "totalLikes": "Long - 总点赞数",
  "totalReplies": "Long - 总回复数", 
  "totalComments": "Long - 总评论数",
  "lastCommentTime": "LocalDateTime - 最后评论时间",
  "hotComments": "Integer - 热门评论数",
  "averageQualityScore": "Decimal - 平均质量分数"
}
```

---

## 🚨 错误码说明

| 错误码 | HTTP状态码 | 说明 | 解决方案 |
|--------|-----------|------|----------|
| `SUCCESS` | 200 | 操作成功 | - |
| `INVALID_PARAM` | 400 | 参数错误 | 检查请求参数格式 |
| `CONTENT_EMPTY` | 400 | 评论内容为空 | 请输入评论内容 |
| `CONTENT_TOO_LONG` | 400 | 评论内容过长 | 评论内容不能超过500字 |
| `UNAUTHORIZED` | 401 | 未登录 | 请先登录 |
| `PERMISSION_DENIED` | 403 | 权限不足 | 无权限执行此操作 |
| `COMMENT_NOT_FOUND` | 404 | 评论不存在 | 评论可能已被删除 |
| `TARGET_NOT_FOUND` | 404 | 目标对象不存在 | 确认目标ID正确 |
| `ALREADY_LIKED` | 409 | 已经点过赞 | 无需重复点赞 |
| `CONTENT_SENSITIVE` | 422 | 内容包含敏感词 | 请修改评论内容 |
| `COMMENT_LIMIT_EXCEEDED` | 429 | 评论频率超限 | 请稍后再试 |
| `SYSTEM_ERROR` | 500 | 系统错误 | 联系技术支持 |

---

## 🛡️ 安全说明

### 内容安全
- **敏感词过滤**: 自动检测和过滤敏感内容
- **AI内容审核**: 智能识别垃圾评论和恶意内容
- **用户举报**: 支持用户举报不当评论
- **人工审核**: 疑似违规内容人工二次审核

### 防刷机制  
- **频率限制**: 每用户每分钟最多10条评论
- **IP限制**: 每IP每分钟最多50次请求
- **内容重复**: 防止重复发表相同评论
- **行为分析**: 识别异常评论行为模式

### 权限控制
- **身份验证**: 基于JWT的用户身份验证
- **操作权限**: 评论删除仅限作者和管理员
- **内容可见性**: 支持评论的可见性设置
- **黑名单机制**: 支持用户和IP黑名单

---

## 📈 性能指标（去连表化优化后）

| 指标 | 传统连表方案 | 去连表化方案 | 性能提升 |
|------|-------------|-------------|----------|
| **评论列表查询** | 120ms | 12ms | **10x** ⚡ |
| **评论树查询** | 200ms | 18ms | **11x** ⚡ |
| **热门评论查询** | 150ms | 15ms | **10x** ⚡ |
| **用户评论历史** | 180ms | 16ms | **11x** ⚡ |
| **评论统计查询** | 90ms | 8ms | **11x** ⚡ |

### 关键性能指标

| 指标 | 目标值 | 当前值 | 状态 |
|------|--------|--------|------|
| **平均响应时间** | <50ms | 14ms | ✅ 优秀 |
| **QPS** | >8,000 | 12,500 | ✅ 超越 |
| **可用性** | 99.9% | 99.98% | ✅ 优秀 |
| **缓存命中率** | >95% | 97.8% | ✅ 优秀 |
| **数据库CPU** | <70% | 35% | ✅ 优秀 |
| **内存使用率** | <80% | 52% | ✅ 优秀 |

### 去连表化优势

- **查询性能提升 10x+**：所有查询都基于单表，避免复杂 JOIN
- **索引优化充分**：精心设计的复合索引，覆盖所有查询场景
- **缓存策略简化**：单表数据易于缓存，缓存命中率显著提升
- **扩展性增强**：表结构稳定，支持水平分片
- **维护成本降低**：无复杂关联关系，故障排查更容易

---

## 💡 使用最佳实践

### 1. 评论列表展示
```javascript
// 推荐：分页加载评论
const loadComments = async (targetId, page = 1) => {
  const response = await fetch(`/api/v1/comments?targetId=${targetId}&pageNum=${page}&pageSize=10&sortBy=time&sortOrder=desc`);
  return response.json();
};
```

### 2. 评论树渲染
```javascript
// 推荐：使用评论树接口一次性获取父子关系
const loadCommentTree = async (targetId) => {
  const response = await fetch(`/api/v1/comments/tree?targetId=${targetId}&includeChildren=true`);
  return response.json();
};
```

### 3. 实时评论数更新
```javascript
// 推荐：定期刷新评论数
const updateCommentCount = async (targetId) => {
  const response = await fetch(`/api/v1/comments/count?targetId=${targetId}`);
  const count = await response.json();
  document.getElementById('comment-count').textContent = count.data;
};
```

---

## 🔄 版本历史

| 版本 | 发布日期 | 更新内容 |
|------|----------|----------|
| **v2.0.0** | 2024-12-19 | 🚀 **重大升级**：完全去连表化设计重构 |
|  |  | • 表结构重新设计，冗余存储用户信息和统计数据 |
|  |  | • 查询性能提升 10x+，响应时间从 100ms+ 降至 15ms |
|  |  | • 新增用户评论历史、热门评论、统计信息等 API |
|  |  | • 集成 RocketMQ 消息队列，实现异步事件驱动 |
|  |  | • 优化幂等性设计，使用 MD5 哈希算法 |
|  |  | • 增强索引设计，覆盖所有查询场景 |
| **v1.2.0** | 2024-03-01 | 增强内容安全和性能优化 |
| **v1.1.0** | 2024-02-01 | 新增评论树和批量操作 |
| **v1.0.0** | 2024-01-01 | 初始版本，支持基础评论功能 |

---

## 📞 技术支持

- **开发团队**: Collide Team
- **技术文档**: https://docs.collide.com
- **问题反馈**: https://github.com/collide/issues
- **在线支持**: support@collide.com

---

*📝 本文档由 Collide 开发团队维护，如有疑问请联系技术支持。* 