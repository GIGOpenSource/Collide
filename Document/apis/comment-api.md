# Collide 评论服务 API 文档

## 概述

Collide 评论服务提供完整的评论管理功能，包括评论发布、回复、查询、管理等核心功能。支持多级回复、评论审核、敏感词过滤等高级特性。

**服务版本**: v1.0.0  
**基础路径**: `/api/v1/comments`  
**Dubbo服务**: `collide-comment`  
**设计理念**: 灵活的评论系统，支持多种内容类型的评论，提供完整的评论生态

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
  "pageNum": 1,                    // 页码（从1开始）
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

### 2. 获取评论回复
**接口路径**: `GET /api/v1/comments/{id}/replies`  
**接口描述**: 获取评论的回复列表

#### 请求参数
- **id** (path): 评论ID，必填
- **pageNum** (query): 页码，可选，默认1
- **pageSize** (query): 页面大小，可选，默认10

---

### 3. 获取回复树
**接口路径**: `GET /api/v1/comments/{id}/tree`  
**接口描述**: 获取完整的评论回复树结构

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

### 3. 获取热门评论
**接口路径**: `GET /api/v1/comments/hot`  
**接口描述**: 获取热门评论列表

#### 请求参数
- **targetType** (query): 目标类型，可选
- **targetId** (query): 目标ID，可选
- **timeRange** (query): 时间范围（天），可选，默认7
- **pageNum** (query): 页码，可选
- **pageSize** (query): 页面大小，可选

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
- **pageNum** (query): 页码，可选
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