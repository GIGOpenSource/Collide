# Collide 评论服务 API 文档

## 概述

Collide 评论服务提供完整的评论管理功能，包括评论发布、回复、查询、管理等核心功能。支持多级回复、评论审核、敏感词过滤等高级特性。

**服务版本**: v1.0.0  
**基础路径**: `/api/v1/comments`  
**Dubbo服务**: `collide-comment`  
**设计理念**: 灵活的评论系统，支持多种内容类型的评论，提供完整的评论生态

### 📋 响应格式说明

本API提供两种响应格式：

1. **标准Result响应**（POST复杂查询、增删改操作）:
   ```json
   {
     "success": true,
     "responseCode": "SUCCESS", 
     "responseMessage": "操作成功",
     "data": { /* 具体数据 */ }
   }
   ```

2. **直接PageResponse响应**（GET简单查询）:
   ```json
   {
     "success": true,
     "datas": [ /* 数据列表 */ ],
     "currentPage": 1,
     "pageSize": 20,
     "totalPage": 3,
     "total": 50
   }
   ```

---

## 接口概览

### GET 接口列表（直接PageResponse响应）
| 接口路径 | 功能描述 | 分页参数 |
|---------|---------|---------|
| `GET /comments/target/{targetId}` | 获取目标对象评论列表 | ✅ currentPage, pageSize |
| `GET /comments/{commentId}/replies` | 获取评论回复列表 | ✅ currentPage, pageSize |
| `GET /comments/tree/{targetId}` | 获取评论树形结构 | ✅ currentPage, pageSize |
| `GET /comments/user/{userId}` | 获取用户评论列表 | ✅ currentPage, pageSize |
| `GET /comments/user/{userId}/replies` | 获取用户收到的回复 | ✅ currentPage, pageSize |
| `GET /comments/search` | 搜索评论 | ✅ currentPage, pageSize |
| `GET /comments/popular` | 获取热门评论 | ✅ currentPage, pageSize |
| `GET /comments/latest` | 获取最新评论 | ✅ currentPage, pageSize |

### POST 接口列表（标准Result响应）
| 接口路径 | 功能描述 | 备注 |
|---------|---------|------|
| `POST /comments` | 发布评论 | 创建操作 |
| `POST /comments/query` | 复杂查询评论 | 支持复杂条件 |
| `PUT /comments/{id}` | 更新评论 | 修改操作 |
| `DELETE /comments/{id}` | 删除评论 | 逻辑删除 |
| `GET /comments/{id}` | 获取评论详情 | 单个资源 |

---

## 评论管理 API

### 1. 发布评论
**接口路径**: `POST /api/v1/comments`  
**接口描述**: 发布新评论或回复

#### 请求参数
```json
{
  "targetType": "content",              // 必填，评论目标类型：content/user/goods
  "targetId": 98765,                   // 必填，评论目标ID
  "content": "这篇文章写得很好！",       // 必填，评论内容
  "parentId": null,                    // 可选，父评论ID（回复时需要）
  "replyToUserId": null,               // 可选，回复的用户ID
  "userId": 12345,                     // 必填，评论者用户ID
  "authorId": 67890,                   // 可选，被评论内容的作者ID
  "attachments": [                     // 可选，附件列表
    {
      "type": "image",
      "url": "https://example.com/image.jpg"
    }
  ]
}
```

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "responseCode": "SUCCESS",
  "responseMessage": "评论发布成功",
  "data": {
    "id": 123456,
    "targetType": "content",
    "targetId": 98765,
    "content": "这篇文章写得很好！",
    "parentId": null,
    "replyToUserId": null,
    "userId": 12345,
    "username": "techuser",
    "userNickname": "技术爱好者",
    "userAvatar": "https://example.com/avatar.jpg",
    "authorId": 67890,
    "status": "published",
    "likeCount": 0,
    "replyCount": 0,
    "level": 1,
    "isAuthorReply": false,
    "attachments": [],
    "createTime": "2024-01-16T10:30:00",
    "updateTime": "2024-01-16T10:30:00"
  }
}
```

---

### 2. 更新评论
**接口路径**: `PUT /api/v1/comments/{id}`  
**接口描述**: 更新评论内容

#### 请求参数
- **id** (path): 评论ID，必填

```json
{
  "content": "修改后的评论内容",        // 必填，新的评论内容
  "userId": 12345                   // 必填，操作用户ID（需要是评论者本人）
}
```

---

### 3. 删除评论
**接口路径**: `DELETE /api/v1/comments/{id}`  
**接口描述**: 删除评论（逻辑删除）

#### 请求参数
- **id** (path): 评论ID，必填
- **userId** (query): 操作用户ID，必填

---

### 4. 获取评论详情
**接口路径**: `GET /api/v1/comments/{id}`  
**接口描述**: 获取评论详细信息

#### 请求参数
- **id** (path): 评论ID，必填

---

### 5. 查询评论列表
**接口路径**: `POST /api/v1/comments/query`  
**接口描述**: 分页查询评论列表

#### 请求参数
```json
{
  "currentPage": 1,                // 页码（从1开始）
  "pageSize": 20,                  // 每页大小
  "targetType": "content",         // 可选，目标类型
  "targetId": 98765,               // 可选，目标ID
  "userId": 12345,                 // 可选，评论者ID
  "parentId": null,                // 可选，父评论ID（null为顶级评论）
  "status": "published",           // 可选，状态
  "keyword": "关键词",             // 可选，内容关键词搜索
  "orderBy": "createTime",         // 可选，排序字段
  "orderDirection": "DESC"         // 可选，排序方向
}
```

---

## 回复管理 API

### 1. 回复评论
**接口路径**: `POST /api/v1/comments/{id}/reply`  
**接口描述**: 回复指定评论

#### 请求参数
- **id** (path): 被回复的评论ID，必填

```json
{
  "content": "我来回复这个评论",        // 必填，回复内容
  "userId": 67890,                   // 必填，回复者用户ID
  "replyToUserId": 12345             // 可选，指定回复的用户ID
}
```

---

### 2. 获取目标对象评论列表
**接口路径**: `GET /api/v1/comments/target/{targetId}`  
**接口描述**: 获取指定目标对象的评论列表（直接PageResponse响应）

#### 请求参数
- **targetId** (path): 目标对象ID，必填
- **commentType** (query): 评论类型，可选
- **parentCommentId** (query): 父评论ID，可选，默认0
- **currentPage** (query): 页码，可选，默认1
- **pageSize** (query): 页面大小，可选，默认20

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "datas": [
    {
      "id": 123456,
      "targetType": "content",
      "targetId": 98765,
      "content": "这篇文章写得很好！",
      "userId": 12345,
      "userNickname": "技术爱好者",
      "userAvatar": "https://example.com/avatar.jpg",
      "status": "published",
      "likeCount": 15,
      "replyCount": 3,
      "level": 1,
      "createTime": "2024-01-16T10:30:00"
    }
  ],
  "currentPage": 1,
  "pageSize": 20,
  "totalPage": 5,
  "total": 89
}
```

---

### 3. 获取评论回复列表
**接口路径**: `GET /api/v1/comments/{commentId}/replies`  
**接口描述**: 获取指定评论的回复列表（直接PageResponse响应）

#### 请求参数
- **commentId** (path): 评论ID，必填
- **currentPage** (query): 页码，可选，默认1
- **pageSize** (query): 页面大小，可选，默认20

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "datas": [
    {
      "id": 123457,
      "parentCommentId": 123456,
      "content": "我来回复这个评论",
      "userId": 67890,
      "userNickname": "用户B",
      "replyToUserId": 12345,
      "replyToUserNickname": "技术爱好者",
      "level": 2,
      "likeCount": 5,
      "createTime": "2024-01-16T11:00:00"
    }
  ],
  "currentPage": 1,
  "pageSize": 20,
  "totalPage": 1,
  "total": 3
}
```

---

### 4. 获取评论树形结构
**接口路径**: `GET /api/v1/comments/tree/{targetId}`  
**接口描述**: 获取指定目标的评论树形结构（直接PageResponse响应）

#### 请求参数
- **targetId** (path): 目标对象ID，必填
- **commentType** (query): 评论类型，可选
- **maxDepth** (query): 最大层级深度，可选，默认3
- **currentPage** (query): 页码，可选，默认1
- **pageSize** (query): 页面大小，可选，默认20

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "datas": [
    {
      "id": 123456,
      "content": "主评论内容",
      "userId": 12345,
      "userNickname": "用户A",
      "level": 1,
      "createTime": "2024-01-16T10:30:00",
      "children": [
        {
          "id": 123457,
          "content": "回复内容",
          "userId": 67890,
          "userNickname": "用户B",
          "replyToUserId": 12345,
          "level": 2,
          "createTime": "2024-01-16T11:00:00",
          "children": []
        }
      ]
    }
  ],
  "currentPage": 1,
  "pageSize": 20,
  "totalPage": 2,
  "total": 25
}
```

---

### 5. 获取用户评论列表
**接口路径**: `GET /api/v1/comments/user/{userId}`  
**接口描述**: 获取指定用户的评论列表（直接PageResponse响应）

#### 请求参数
- **userId** (path): 用户ID，必填
- **commentType** (query): 评论类型，可选
- **status** (query): 评论状态，可选，默认NORMAL
- **currentPage** (query): 页码，可选，默认1
- **pageSize** (query): 页面大小，可选，默认20

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "datas": [
    {
      "id": 123456,
      "targetType": "content",
      "targetId": 98765,
      "content": "我的评论内容",
      "userId": 12345,
      "status": "published",
      "likeCount": 10,
      "replyCount": 2,
      "createTime": "2024-01-16T10:30:00"
    }
  ],
  "currentPage": 1,
  "pageSize": 20,
  "totalPage": 3,
  "total": 45
}
```

---

### 6. 获取用户收到的回复
**接口路径**: `GET /api/v1/comments/user/{userId}/replies`  
**接口描述**: 获取用户收到的回复列表（直接PageResponse响应）

#### 请求参数
- **userId** (path): 用户ID，必填
- **currentPage** (query): 页码，可选，默认1
- **pageSize** (query): 页面大小，可选，默认20

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "datas": [
    {
      "id": 123457,
      "content": "回复给你的评论",
      "userId": 67890,
      "userNickname": "回复者",
      "replyToUserId": 12345,
      "parentCommentId": 123456,
      "targetId": 98765,
      "createTime": "2024-01-16T11:00:00"
    }
  ],
  "currentPage": 1,
  "pageSize": 20,
  "totalPage": 1,
  "total": 8
}
```

---

### 7. 搜索评论
**接口路径**: `GET /api/v1/comments/search`  
**接口描述**: 根据关键词搜索评论（直接PageResponse响应）

#### 请求参数
- **keyword** (query): 搜索关键词，必填
- **commentType** (query): 评论类型，可选
- **targetId** (query): 目标对象ID，可选
- **currentPage** (query): 页码，可选，默认1
- **pageSize** (query): 页面大小，可选，默认20

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "datas": [
    {
      "id": 123456,
      "content": "包含关键词的评论内容",
      "userId": 12345,
      "userNickname": "用户名",
      "targetId": 98765,
      "likeCount": 5,
      "createTime": "2024-01-16T10:30:00"
    }
  ],
  "currentPage": 1,
  "pageSize": 20,
  "totalPage": 2,
  "total": 32
}
```

---

### 8. 获取热门评论
**接口路径**: `GET /api/v1/comments/popular`  
**接口描述**: 获取热门评论列表（直接PageResponse响应）

#### 请求参数
- **targetId** (query): 目标对象ID，可选
- **commentType** (query): 评论类型，可选
- **timeRange** (query): 时间范围（天），可选，默认7
- **currentPage** (query): 页码，可选，默认1
- **pageSize** (query): 页面大小，可选，默认20

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "datas": [
    {
      "id": 123456,
      "content": "热门评论内容",
      "userId": 12345,
      "userNickname": "热门用户",
      "likeCount": 156,
      "replyCount": 23,
      "hotScore": 89.5,
      "createTime": "2024-01-15T14:30:00"
    }
  ],
  "currentPage": 1,
  "pageSize": 20,
  "totalPage": 1,
  "total": 15
}
```

---

### 9. 获取最新评论
**接口路径**: `GET /api/v1/comments/latest`  
**接口描述**: 获取最新评论列表（直接PageResponse响应）

#### 请求参数
- **targetId** (query): 目标对象ID，可选
- **commentType** (query): 评论类型，可选
- **currentPage** (query): 页码，可选，默认1
- **pageSize** (query): 页面大小，可选，默认20

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "datas": [
    {
      "id": 123458,
      "content": "最新发布的评论",
      "userId": 12345,
      "userNickname": "新用户",
      "targetId": 98765,
      "likeCount": 0,
      "replyCount": 0,
      "createTime": "2024-01-16T15:45:00"
    }
  ],
  "currentPage": 1,
  "pageSize": 20,
  "totalPage": 10,
  "total": 200
}
```

---

### 10. 获取回复树（原接口保持兼容）
**接口路径**: `GET /api/v1/comments/{id}/tree`  
**接口描述**: 获取完整的评论回复树结构（标准Result响应）

#### 响应示例
```json
{
  "success": true,
  "data": {
    "id": 123456,
    "content": "主评论内容",
    "userId": 12345,
    "userNickname": "用户A",
    "createTime": "2024-01-16T10:30:00",
    "replies": [
      {
        "id": 123457,
        "content": "回复内容",
        "userId": 67890,
        "userNickname": "用户B",
        "replyToUserId": 12345,
        "replyToUserNickname": "用户A",
        "createTime": "2024-01-16T11:00:00",
        "replies": []
      }
    ]
  }
}
```

---

## 评论统计 API

### 1. 获取目标评论统计
**接口路径**: `GET /api/v1/comments/statistics/target`  
**接口描述**: 获取指定目标的评论统计信息

#### 请求参数
- **targetType** (query): 目标类型，必填
- **targetId** (query): 目标ID，必填

#### 响应示例
```json
{
  "success": true,
  "data": {
    "targetType": "content",
    "targetId": 98765,
    "totalCount": 156,           // 总评论数
    "topLevelCount": 89,         // 顶级评论数
    "replyCount": 67,            // 回复数
    "userCount": 134,            // 参与评论的用户数
    "averageLevel": 1.8,         // 平均评论层级
    "latestCommentTime": "2024-01-16T15:30:00"
  }
}
```

---

### 2. 获取用户评论统计
**接口路径**: `GET /api/v1/comments/statistics/user/{userId}`  
**接口描述**: 获取用户的评论统计信息

#### 请求参数
- **userId** (path): 用户ID，必填

---

## 评论审核 API

### 1. 审核评论
**接口路径**: `POST /api/v1/comments/{id}/review`  
**接口描述**: 审核评论内容

#### 请求参数
- **id** (path): 评论ID，必填

```json
{
  "reviewStatus": "approved",          // 必填，审核状态：approved/rejected
  "reviewerId": 99999,                 // 必填，审核人ID
  "reviewComment": "内容符合规范",      // 可选，审核意见
  "rejectReason": null                 // 可选，拒绝原因
}
```

---

### 2. 批量审核评论
**接口路径**: `POST /api/v1/comments/batch-review`  
**接口描述**: 批量审核评论

#### 请求参数
```json
{
  "commentIds": [123456, 123457, 123458],  // 必填，评论ID列表
  "reviewStatus": "approved",              // 必填，审核状态
  "reviewerId": 99999,                     // 必填，审核人ID
  "reviewComment": "批量审核通过"           // 可选，审核意见
}
```

---

### 3. 获取待审核评论
**接口路径**: `GET /api/v1/comments/pending-review`  
**接口描述**: 获取待审核评论列表

#### 请求参数
- **currentPage** (query): 页码，可选
- **pageSize** (query): 页面大小，可选
- **targetType** (query): 目标类型，可选

---

## 评论互动 API

### 1. 点赞评论
**接口路径**: `POST /api/v1/comments/{id}/like`  
**接口描述**: 给评论点赞

#### 请求参数
- **id** (path): 评论ID，必填
- **userId** (query): 用户ID，必填

---

### 2. 取消点赞
**接口路径**: `DELETE /api/v1/comments/{id}/like`  
**接口描述**: 取消评论点赞

---

### 3. 举报评论
**接口路径**: `POST /api/v1/comments/{id}/report`  
**接口描述**: 举报不当评论

#### 请求参数
- **id** (path): 评论ID，必填

```json
{
  "userId": 12345,                    // 必填，举报用户ID
  "reason": "spam",                   // 必填，举报原因：spam/inappropriate/harassment
  "description": "内容不当"           // 可选，详细描述
}
```

---

## 评论设置 API

### 1. 设置评论权限
**接口路径**: `POST /api/v1/comments/settings/permission`  
**接口描述**: 设置目标的评论权限

#### 请求参数
```json
{
  "targetType": "content",           // 必填，目标类型
  "targetId": 98765,                // 必填，目标ID
  "allowComment": true,             // 必填，是否允许评论
  "allowReply": true,               // 必填，是否允许回复
  "needReview": false,              // 必填，是否需要审核
  "operatorId": 67890               // 必填，操作人ID
}
```

---

### 2. 获取评论设置
**接口路径**: `GET /api/v1/comments/settings`  
**接口描述**: 获取目标的评论设置

#### 请求参数
- **targetType** (query): 目标类型，必填
- **targetId** (query): 目标ID，必填

---

## 使用示例

### 获取内容评论的完整流程
```javascript
// 1. 获取某个内容的评论列表（第一页）
const comments = await api.get('/api/v1/comments/target/98765', {
  params: {
    commentType: 'CONTENT',
    currentPage: 1,
    pageSize: 20
  }
});

// 2. 获取某个评论的回复
const replies = await api.get('/api/v1/comments/123456/replies', {
  params: {
    currentPage: 1,
    pageSize: 10
  }
});

// 3. 发布新评论
const newComment = await api.post('/api/v1/comments', {
  targetType: 'content',
  targetId: 98765,
  content: '这篇文章写得很好！',
  userId: 12345
});

// 4. 回复评论
const reply = await api.post('/api/v1/comments', {
  targetType: 'content',
  targetId: 98765,
  content: '我来回复这个评论',
  parentId: 123456,
  replyToUserId: 12345,
  userId: 67890
});
```

### 获取用户相关评论
```javascript
// 获取用户发布的所有评论
const userComments = await api.get('/api/v1/comments/user/12345', {
  params: {
    currentPage: 1,
    pageSize: 20,
    status: 'NORMAL'
  }
});

// 获取用户收到的回复
const userReplies = await api.get('/api/v1/comments/user/12345/replies', {
  params: {
    currentPage: 1,
    pageSize: 20
  }
});
```

### 搜索和热门评论
```javascript
// 搜索评论
const searchResults = await api.get('/api/v1/comments/search', {
  params: {
    keyword: 'Java',
    currentPage: 1,
    pageSize: 15
  }
});

// 获取热门评论
const popularComments = await api.get('/api/v1/comments/popular', {
  params: {
    timeRange: 7,
    currentPage: 1,
    pageSize: 10
  }
});

// 获取最新评论
const latestComments = await api.get('/api/v1/comments/latest', {
  params: {
    currentPage: 1,
    pageSize: 20
  }
});
```

### 复杂查询示例
```javascript
// 使用POST方式进行复杂查询
const complexQuery = await api.post('/api/v1/comments/query', {
  targetId: 98765,
  commentType: 'CONTENT',
  status: 'published',
  currentPage: 1,
  pageSize: 20,
  orderBy: 'like_count',
  orderDirection: 'DESC'
});
```

---

## 错误码说明

| 错误码 | 说明 |
|--------|------|
| COMMENT_NOT_FOUND | 评论不存在 |
| COMMENT_DELETED | 评论已删除 |
| COMMENT_PERMISSION_DENIED | 评论权限不足 |
| COMMENT_UNDER_REVIEW | 评论正在审核中 |
| COMMENT_TOO_LONG | 评论内容过长 |
| COMMENT_CONTAINS_SENSITIVE_WORDS | 评论包含敏感词 |
| TARGET_NOT_ALLOW_COMMENT | 目标不允许评论 |
| USER_COMMENT_BLOCKED | 用户被禁止评论 |
| REPLY_LEVEL_TOO_DEEP | 回复层级过深 |
| DUPLICATE_COMMENT | 重复评论 |

---

**最后更新**: 2024-01-16  
**文档版本**: v1.0.0