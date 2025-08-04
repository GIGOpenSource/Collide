# Comment REST API 接口文档

**版本**: 5.0.0  
**更新时间**: 2024-01-01  
**基础路径**: `/api/v1/comments`  
**服务**: collide-comment  

## 🚀 概述

评论模块REST API提供完整的评论功能HTTP接口，包括基础操作、高级查询、统计分析、管理功能。支持评论类型：`CONTENT`（内容评论）、`DYNAMIC`（动态评论）。

## 📋 接口分类

| 分类 | 接口数量 | 功能描述 |
|------|----------|----------|
| **基础操作** | 4个 | 创建、更新、删除、查询评论 |
| **目标对象查询** | 3个 | 获取目标内容的评论列表和树形结构 |
| **用户评论查询** | 2个 | 获取用户发表的评论和收到的回复 |
| **统计功能** | 5个 | 点赞数、回复数、评论计数功能 |
| **高级功能** | 3个 | 搜索、热门、最新评论查询 |
| **新增查询** | 2个 | 按点赞数、时间范围查询 |
| **数据分析** | 3个 | 统计信息、回复关系、热度排行 |
| **管理功能** | 5个 | 批量操作、同步、清理功能 |

---

## 🔧 基础操作 (4个接口)

### 1.1 创建评论

**接口**: `POST /api/v1/comments`

**描述**: 创建新评论，支持根评论和回复评论

**请求参数**:
```json
{
  "targetId": 12345,
  "commentType": "CONTENT",
  "content": "这是一条评论内容",
  "parentCommentId": 0,
  "replyToUserId": null,
  "userId": 1001
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 67890,
    "targetId": 12345,
    "commentType": "CONTENT",
    "content": "这是一条评论内容",
    "userId": 1001,
    "userNickname": "用户昵称",
    "createTime": "2024-01-01T10:00:00",
    "likeCount": 0,
    "replyCount": 0,
    "status": "NORMAL"
  }
}
```

### 1.2 更新评论

**接口**: `PUT /api/v1/comments/{commentId}`

**描述**: 更新评论内容

**路径参数**:
- `commentId` (Long): 评论ID

**请求参数**:
```json
{
  "content": "更新后的评论内容",
  "userId": 1001
}
```

### 1.3 删除评论

**接口**: `DELETE /api/v1/comments/{commentId}`

**描述**: 逻辑删除评论，将状态更新为DELETED

**路径参数**:
- `commentId` (Long): 评论ID

**查询参数**:
- `userId` (Long): 操作用户ID

### 1.4 获取评论详情

**接口**: `GET /api/v1/comments/{commentId}`

**描述**: 根据ID获取评论详情

**路径参数**:
- `commentId` (Long): 评论ID

---

## 📝 目标对象查询 (3个接口)

### 2.1 获取目标对象评论列表

**接口**: `GET /api/v1/comments/target/{targetId}`

**描述**: 获取指定内容或动态的评论列表

**路径参数**:
- `targetId` (Long): 目标对象ID

**查询参数**:
- `commentType` (String, 可选): 评论类型 (CONTENT/DYNAMIC)
- `parentCommentId` (Long, 默认0): 父评论ID，0表示获取根评论
- `currentPage` (Integer, 默认1): 页码
- `pageSize` (Integer, 默认20): 页面大小

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [...],
    "totalPage": 5,
    "totalCount": 100,
    "currentPage": 1,
    "pageSize": 20
  }
}
```

### 2.2 获取评论回复列表

**接口**: `GET /api/v1/comments/{commentId}/replies`

**描述**: 获取指定评论的回复列表

**路径参数**:
- `commentId` (Long): 父评论ID

**查询参数**:
- `currentPage` (Integer, 默认1): 页码
- `pageSize` (Integer, 默认20): 页面大小

### 2.3 获取评论树形结构

**接口**: `GET /api/v1/comments/tree/{targetId}`

**描述**: 获取带层级结构的评论树

**路径参数**:
- `targetId` (Long): 目标对象ID

**查询参数**:
- `commentType` (String, 可选): 评论类型
- `maxDepth` (Integer, 默认3): 最大层级深度
- `currentPage` (Integer, 默认1): 页码
- `pageSize` (Integer, 默认20): 页面大小

---

## 👤 用户评论查询 (2个接口)

### 3.1 获取用户评论列表

**接口**: `GET /api/v1/comments/user/{userId}`

**描述**: 获取用户发表的所有评论

**路径参数**:
- `userId` (Long): 用户ID

**查询参数**:
- `commentType` (String, 可选): 评论类型
- `currentPage` (Integer, 默认1): 页码
- `pageSize` (Integer, 默认20): 页面大小

### 3.2 获取用户收到的回复

**接口**: `GET /api/v1/comments/user/{userId}/replies`

**描述**: 获取用户收到的所有回复

**路径参数**:
- `userId` (Long): 用户ID

**查询参数**:
- `currentPage` (Integer, 默认1): 页码
- `pageSize` (Integer, 默认20): 页面大小

---

## 📊 统计功能 (5个接口)

### 4.1 增加点赞数

**接口**: `PUT /api/v1/comments/{commentId}/like`

**描述**: 增加评论的点赞数

**路径参数**:
- `commentId` (Long): 评论ID

**查询参数**:
- `increment` (Integer, 默认1): 增加数量

**响应**:
```json
{
  "code": 200,
  "message": "success",
  "data": 15
}
```

### 4.2 减少点赞数

**接口**: `PUT /api/v1/comments/{commentId}/unlike`

**描述**: 减少评论的点赞数

**路径参数**:
- `commentId` (Long): 评论ID

**查询参数**:
- `decrement` (Integer, 默认1): 减少数量

### 4.3 增加回复数

**接口**: `PUT /api/v1/comments/{commentId}/reply-count`

**描述**: 增加评论的回复数

**路径参数**:
- `commentId` (Long): 评论ID

**查询参数**:
- `increment` (Integer, 默认1): 增加数量

### 4.4 统计目标对象评论数

**接口**: `GET /api/v1/comments/count/target/{targetId}`

**描述**: 统计指定目标对象的评论总数

**路径参数**:
- `targetId` (Long): 目标对象ID

**查询参数**:
- `commentType` (String, 可选): 评论类型

### 4.5 统计用户评论数

**接口**: `GET /api/v1/comments/count/user/{userId}`

**描述**: 统计用户发表的评论总数

**路径参数**:
- `userId` (Long): 用户ID

**查询参数**:
- `commentType` (String, 可选): 评论类型

---

## 🔍 高级功能 (3个接口)

### 5.1 搜索评论

**接口**: `GET /api/v1/comments/search`

**描述**: 根据关键词搜索评论内容

**查询参数**:
- `keyword` (String): 搜索关键词
- `commentType` (String, 可选): 评论类型
- `targetId` (Long, 可选): 目标对象ID
- `currentPage` (Integer, 默认1): 页码
- `pageSize` (Integer, 默认20): 页面大小

### 5.2 获取热门评论

**接口**: `GET /api/v1/comments/popular`

**描述**: 获取热门评论列表，按点赞数排序

**查询参数**:
- `targetId` (Long, 可选): 目标对象ID
- `commentType` (String, 可选): 评论类型
- `timeRange` (Integer, 默认7): 时间范围（天）
- `currentPage` (Integer, 默认1): 页码
- `pageSize` (Integer, 默认20): 页面大小

### 5.3 获取最新评论

**接口**: `GET /api/v1/comments/latest`

**描述**: 获取最新评论列表，按时间排序

**查询参数**:
- `targetId` (Long, 可选): 目标对象ID
- `commentType` (String, 可选): 评论类型
- `currentPage` (Integer, 默认1): 页码
- `pageSize` (Integer, 默认20): 页面大小

---

## 🎯 新增查询 (2个接口)

### 6.1 根据点赞数范围查询

**接口**: `GET /api/v1/comments/like-range`

**描述**: 查询指定点赞数范围内的评论

**查询参数**:
- `minLikeCount` (Integer, 可选): 最小点赞数
- `maxLikeCount` (Integer, 可选): 最大点赞数
- `commentType` (String, 可选): 评论类型
- `targetId` (Long, 可选): 目标对象ID
- `currentPage` (Integer, 默认1): 页码
- `pageSize` (Integer, 默认20): 页面大小

### 6.2 根据时间范围查询

**接口**: `GET /api/v1/comments/time-range`

**描述**: 查询指定时间范围内的评论

**查询参数**:
- `startTime` (DateTime, 可选): 开始时间 (ISO格式: 2024-01-01T00:00:00)
- `endTime` (DateTime, 可选): 结束时间 (ISO格式: 2024-01-01T23:59:59)
- `commentType` (String, 可选): 评论类型
- `targetId` (Long, 可选): 目标对象ID
- `currentPage` (Integer, 默认1): 页码
- `pageSize` (Integer, 默认20): 页面大小

---

## 📈 数据分析 (3个接口)

### 7.1 获取评论统计信息

**接口**: `GET /api/v1/comments/statistics`

**描述**: 获取综合统计信息

**查询参数**:
- `targetId` (Long, 可选): 目标对象ID
- `commentType` (String, 可选): 评论类型
- `userId` (Long, 可选): 用户ID
- `startTime` (DateTime, 可选): 开始时间
- `endTime` (DateTime, 可选): 结束时间

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "totalComments": 1000,
    "totalLikes": 5000,
    "totalReplies": 800,
    "avgLikePerComment": 5.0,
    "topUsers": [...],
    "dailyStats": [...]
  }
}
```

### 7.2 查询用户回复关系

**接口**: `GET /api/v1/comments/reply-relations`

**描述**: 查询用户之间的回复关系网络

**查询参数**:
- `userId` (Long, 可选): 用户ID
- `startTime` (DateTime, 可选): 开始时间
- `endTime` (DateTime, 可选): 结束时间

### 7.3 查询评论热度排行

**接口**: `GET /api/v1/comments/hot-ranking`

**描述**: 获取评论热度排行榜

**查询参数**:
- `commentType` (String, 可选): 评论类型
- `targetId` (Long, 可选): 目标对象ID
- `startTime` (DateTime, 可选): 开始时间
- `endTime` (DateTime, 可选): 结束时间
- `limit` (Integer, 默认10): 排行榜数量

---

## ⚙️ 管理功能 (5个接口)

### 8.1 批量更新评论状态

**接口**: `PUT /api/v1/comments/batch/status`

**描述**: 批量更新多个评论的状态

**请求参数**:
```json
{
  "commentIds": [1, 2, 3, 4, 5],
  "status": "HIDDEN"
}
```

**查询参数**:
- `status` (String): 新状态 (NORMAL/HIDDEN/DELETED)

### 8.2 批量删除目标评论

**接口**: `DELETE /api/v1/comments/batch/target/{targetId}`

**描述**: 批量删除指定目标对象的所有评论

**路径参数**:
- `targetId` (Long): 目标对象ID

**查询参数**:
- `commentType` (String, 可选): 评论类型

### 8.3 更新用户信息

**接口**: `PUT /api/v1/comments/sync/user/{userId}`

**描述**: 同步更新评论中的用户冗余信息

**路径参数**:
- `userId` (Long): 用户ID

**查询参数**:
- `nickname` (String, 可选): 新昵称
- `avatar` (String, 可选): 新头像URL

### 8.4 更新回复目标用户信息

**接口**: `PUT /api/v1/comments/sync/reply-to-user/{replyToUserId}`

**描述**: 同步更新评论中的回复目标用户冗余信息

**路径参数**:
- `replyToUserId` (Long): 回复目标用户ID

**查询参数**:
- `nickname` (String, 可选): 新昵称
- `avatar` (String, 可选): 新头像URL

### 8.5 清理已删除评论

**接口**: `DELETE /api/v1/comments/cleanup`

**描述**: 物理删除已标记删除的评论数据

**查询参数**:
- `days` (Integer, 默认30): 删除多少天前的数据
- `limit` (Integer, 默认1000): 限制删除数量

---

## 🚨 错误代码

| 错误代码 | HTTP状态码 | 描述 |
|----------|------------|------|
| `COMMENT_NOT_FOUND` | 404 | 评论不存在 |
| `INVALID_PARAMETER` | 400 | 参数错误 |
| `PERMISSION_DENIED` | 403 | 权限不足 |
| `CREATE_COMMENT_ERROR` | 500 | 创建评论失败 |
| `UPDATE_COMMENT_ERROR` | 500 | 更新评论失败 |
| `DELETE_COMMENT_ERROR` | 500 | 删除评论失败 |
| `SEARCH_ERROR` | 500 | 搜索功能异常 |
| `STATISTICS_ERROR` | 500 | 统计功能异常 |

## 📝 使用示例

### cURL 示例

```bash
# 创建评论
curl -X POST "http://localhost:8080/api/v1/comments" \
  -H "Content-Type: application/json" \
  -d '{
    "targetId": 12345,
    "commentType": "CONTENT",
    "content": "这是一条测试评论",
    "userId": 1001
  }'

# 获取目标评论
curl -X GET "http://localhost:8080/api/v1/comments/target/12345?commentType=CONTENT&currentPage=1&pageSize=10"

# 搜索评论
curl -X GET "http://localhost:8080/api/v1/comments/search?keyword=测试&currentPage=1&pageSize=10"

# 获取统计信息
curl -X GET "http://localhost:8080/api/v1/comments/statistics?targetId=12345&commentType=CONTENT"
```

### JavaScript 示例

```javascript
// 创建评论
const createComment = async (data) => {
  const response = await fetch('/api/v1/comments', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(data)
  });
  return response.json();
};

// 获取评论列表
const getComments = async (targetId, options = {}) => {
  const params = new URLSearchParams({
    currentPage: options.page || 1,
    pageSize: options.size || 20,
    ...options
  });
  
  const response = await fetch(`/api/v1/comments/target/${targetId}?${params}`);
  return response.json();
};
```

## 🔗 相关文档

- [Comment Facade API 文档](./comment-facade-api.md)
- [评论数据模型](../models/comment-model.md)
- [MySQL索引优化](../database/comment-indexes.md)

---

**联系信息**:  
- 项目: Collide评论系统  
- 版本: 5.0.0  
- 维护: Collide开发团队