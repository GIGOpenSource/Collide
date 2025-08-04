# 评论模块 API 接口文档

## 📋 概述

本文档描述了评论模块的客户端API接口，提供完整的评论功能，包括评论的创建、更新、删除、查询、统计分析和管理等功能。

**基础信息：**
- **服务名称：** 评论服务 (Comment Service)
- **API版本：** v5.0.0
- **基础路径：** `/api/v1/comments`
- **协议：** HTTP/HTTPS
- **数据格式：** JSON
- **支持评论类型：** CONTENT（内容评论）、DYNAMIC（动态评论）

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

## 🔐 认证与授权

### 认证方式
- **Bearer Token：** 在请求头中携带 `Authorization: Bearer {token}`
- **用户ID：** 部分接口需要在参数中传递 `userId`

### 权限要求
- 创建评论：需要登录用户
- 更新/删除评论：需要是评论作者或管理员
- 查询评论：公开接口，无需认证

## 📝 请求/响应格式

### 标准响应格式
```json
{
  "code": 200,
  "message": "success",
  "data": {
    // 具体数据
  }
}
```

### 分页响应格式
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [],
    "total": 100,
    "currentPage": 1,
    "pageSize": 20,
    "totalPages": 5
  }
}
```

## 🚀 API 接口详情

### 1. 评论基础操作

#### 1.1 创建评论
**接口地址：** `POST /api/v1/comments`

**请求参数：**
```json
{
  "targetId": 123,
  "targetType": "CONTENT",
  "parentCommentId": 0,
  "content": "这是一条评论内容",
  "userId": 456,
  "userName": "用户名",
  "userAvatar": "头像URL"
}
```

**响应示例：**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 789,
    "targetId": 123,
    "targetType": "CONTENT",
    "parentCommentId": 0,
    "content": "这是一条评论内容",
    "userId": 456,
    "userName": "用户名",
    "userAvatar": "头像URL",
    "likeCount": 0,
    "replyCount": 0,
    "status": "NORMAL",
    "createTime": "2024-01-01T10:00:00Z",
    "updateTime": "2024-01-01T10:00:00Z"
  }
}
```

#### 1.2 更新评论
**接口地址：** `PUT /api/v1/comments/{commentId}`

**路径参数：**
- `commentId`: 评论ID

**请求参数：**
```json
{
  "content": "更新后的评论内容"
}
```

**响应示例：**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 789,
    "content": "更新后的评论内容",
    "updateTime": "2024-01-01T11:00:00Z"
  }
}
```

#### 1.3 删除评论
**接口地址：** `DELETE /api/v1/comments/{commentId}`

**路径参数：**
- `commentId`: 评论ID

**查询参数：**
- `userId`: 操作用户ID

**响应示例：**
```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

#### 1.4 获取评论详情
**接口地址：** `GET /api/v1/comments/{commentId}`

**路径参数：**
- `commentId`: 评论ID

**响应示例：**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 789,
    "targetId": 123,
    "targetType": "CONTENT",
    "parentCommentId": 0,
    "content": "评论内容",
    "userId": 456,
    "userName": "用户名",
    "userAvatar": "头像URL",
    "likeCount": 5,
    "replyCount": 2,
    "status": "NORMAL",
    "createTime": "2024-01-01T10:00:00Z",
    "updateTime": "2024-01-01T10:00:00Z"
  }
}
```

### 2. 目标对象评论查询

#### 2.1 获取目标对象的评论列表
**接口地址：** `GET /api/v1/comments/target/{targetId}`

**路径参数：**
- `targetId`: 目标对象ID

**查询参数：**
- `commentType`: 评论类型（可选，CONTENT/DYNAMIC）
- `parentCommentId`: 父评论ID（可选，默认0表示根评论）
- `currentPage`: 当前页码（可选，默认1）
- `pageSize`: 页面大小（可选，默认20）

**响应示例：**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 789,
        "targetId": 123,
        "commentType": "CONTENT",
        "parentCommentId": 0,
        "content": "评论内容",
        "userId": 456,
        "userName": "用户名",
        "userAvatar": "头像URL",
        "likeCount": 5,
        "replyCount": 2,
        "status": "NORMAL",
        "createTime": "2024-01-01T10:00:00Z"
      }
    ],
    "total": 50,
    "currentPage": 1,
    "pageSize": 20,
    "totalPages": 3
  }
}
```

#### 2.2 获取评论回复列表
**接口地址：** `GET /api/v1/comments/{commentId}/replies`

**路径参数：**
- `commentId`: 评论ID

**查询参数：**
- `currentPage`: 当前页码（可选，默认1）
- `pageSize`: 页面大小（可选，默认20）

**响应示例：**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 790,
        "targetId": 123,
        "commentType": "CONTENT",
        "parentCommentId": 789,
        "content": "回复内容",
        "userId": 457,
        "userName": "回复用户",
        "userAvatar": "头像URL",
        "likeCount": 1,
        "replyCount": 0,
        "status": "NORMAL",
        "createTime": "2024-01-01T11:00:00Z"
      }
    ],
    "total": 10,
    "currentPage": 1,
    "pageSize": 20,
    "totalPages": 1
  }
}
```

#### 2.3 获取评论树形结构
**接口地址：** `GET /api/v1/comments/tree/{targetId}`

**路径参数：**
- `targetId`: 目标对象ID

**查询参数：**
- `commentType`: 评论类型（可选）
- `maxDepth`: 最大层级深度（可选，默认3）
- `currentPage`: 当前页码（可选，默认1）
- `pageSize`: 页面大小（可选，默认20）

**响应示例：**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 789,
        "targetId": 123,
        "commentType": "CONTENT",
        "parentCommentId": 0,
        "content": "根评论",
        "userId": 456,
        "userName": "用户名",
        "userAvatar": "头像URL",
        "likeCount": 5,
        "replyCount": 2,
        "status": "NORMAL",
        "createTime": "2024-01-01T10:00:00Z",
        "children": [
          {
            "id": 790,
            "parentCommentId": 789,
            "content": "子评论",
            "userId": 457,
            "userName": "子用户",
            "userAvatar": "头像URL",
            "likeCount": 1,
            "replyCount": 0,
            "status": "NORMAL",
            "createTime": "2024-01-01T11:00:00Z"
          }
        ]
      }
    ],
    "total": 20,
    "currentPage": 1,
    "pageSize": 20,
    "totalPages": 1
  }
}
```

### 3. 用户评论查询

#### 3.1 获取用户评论列表
**接口地址：** `GET /api/v1/comments/user/{userId}`

**路径参数：**
- `userId`: 用户ID

**查询参数：**
- `commentType`: 评论类型（可选）
- `currentPage`: 当前页码（可选，默认1）
- `pageSize`: 页面大小（可选，默认20）

**响应示例：**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 789,
        "targetId": 123,
        "commentType": "CONTENT",
        "parentCommentId": 0,
        "content": "用户评论",
        "userId": 456,
        "userName": "用户名",
        "userAvatar": "头像URL",
        "likeCount": 5,
        "replyCount": 2,
        "status": "NORMAL",
        "createTime": "2024-01-01T10:00:00Z"
      }
    ],
    "total": 30,
    "currentPage": 1,
    "pageSize": 20,
    "totalPages": 2
  }
}
```

#### 3.2 获取用户收到的回复
**接口地址：** `GET /api/v1/comments/user/{userId}/replies`

**路径参数：**
- `userId`: 用户ID

**查询参数：**
- `currentPage`: 当前页码（可选，默认1）
- `pageSize`: 页面大小（可选，默认20）

**响应示例：**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 790,
        "targetId": 123,
        "commentType": "CONTENT",
        "parentCommentId": 789,
        "content": "回复内容",
        "userId": 457,
        "userName": "回复用户",
        "userAvatar": "头像URL",
        "likeCount": 1,
        "replyCount": 0,
        "status": "NORMAL",
        "createTime": "2024-01-01T11:00:00Z"
      }
    ],
    "total": 15,
    "currentPage": 1,
    "pageSize": 20,
    "totalPages": 1
  }
}
```

### 4. 统计功能

#### 4.1 增加点赞数
**接口地址：** `PUT /api/v1/comments/{commentId}/like`

**路径参数：**
- `commentId`: 评论ID

**查询参数：**
- `increment`: 增加数量（可选，默认1）

**响应示例：**
```json
{
  "code": 200,
  "message": "success",
  "data": 6
}
```

#### 4.2 减少点赞数
**接口地址：** `PUT /api/v1/comments/{commentId}/unlike`

**路径参数：**
- `commentId`: 评论ID

**查询参数：**
- `decrement`: 减少数量（可选，默认1）

**响应示例：**
```json
{
  "code": 200,
  "message": "success",
  "data": 4
}
```

#### 4.3 统计目标对象评论数
**接口地址：** `GET /api/v1/comments/count/target/{targetId}`

**路径参数：**
- `targetId`: 目标对象ID

**查询参数：**
- `commentType`: 评论类型（可选）

**响应示例：**
```json
{
  "code": 200,
  "message": "success",
  "data": 50
}
```

#### 4.3 增加回复数
**接口地址：** `PUT /api/v1/comments/{commentId}/reply-count`

**路径参数：**
- `commentId`: 评论ID

**查询参数：**
- `increment`: 增加数量（可选，默认1）

**响应示例：**
```json
{
  "code": 200,
  "message": "success",
  "data": 8
}
```

#### 4.4 统计目标对象评论数
**接口地址：** `GET /api/v1/comments/count/target/{targetId}`

**路径参数：**
- `targetId`: 目标对象ID

**查询参数：**
- `commentType`: 评论类型（可选）

**响应示例：**
```json
{
  "code": 200,
  "message": "success",
  "data": 50
}
```

#### 4.5 统计用户评论数
**接口地址：** `GET /api/v1/comments/count/user/{userId}`

**路径参数：**
- `userId`: 用户ID

**查询参数：**
- `commentType`: 评论类型（可选）

**响应示例：**
```json
{
  "code": 200,
  "message": "success",
  "data": 30
}
```

### 5. 高级功能

#### 5.1 搜索评论
**接口地址：** `GET /api/v1/comments/search`

**查询参数：**
- `keyword`: 搜索关键词（必填）
- `commentType`: 评论类型（可选）
- `targetId`: 目标对象ID（可选）
- `currentPage`: 当前页码（可选，默认1）
- `pageSize`: 页面大小（可选，默认20）

**响应示例：**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 789,
        "targetId": 123,
        "commentType": "CONTENT",
        "parentCommentId": 0,
        "content": "包含关键词的评论",
        "userId": 456,
        "userNickname": "用户名",
        "userAvatar": "头像URL",
        "likeCount": 5,
        "replyCount": 2,
        "status": "NORMAL",
        "createTime": "2024-01-01T10:00:00Z"
      }
    ],
    "totalCount": 5,
    "currentPage": 1,
    "pageSize": 20,
    "totalPage": 1
  }
}
```

#### 5.2 获取热门评论
**接口地址：** `GET /api/v1/comments/popular`

**查询参数：**
- `targetId`: 目标对象ID（可选）
- `commentType`: 评论类型（可选）
- `timeRange`: 时间范围（天，可选，默认7）
- `currentPage`: 当前页码（可选，默认1）
- `pageSize`: 页面大小（可选，默认20）

**响应示例：**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 789,
        "targetId": 123,
        "commentType": "CONTENT",
        "parentCommentId": 0,
        "content": "热门评论",
        "userId": 456,
        "userNickname": "用户名",
        "userAvatar": "头像URL",
        "likeCount": 100,
        "replyCount": 20,
        "status": "NORMAL",
        "createTime": "2024-01-01T10:00:00Z"
      }
    ],
    "totalCount": 10,
    "currentPage": 1,
    "pageSize": 20,
    "totalPage": 1
  }
}
```

#### 5.3 获取最新评论
**接口地址：** `GET /api/v1/comments/latest`

**查询参数：**
- `targetId`: 目标对象ID（可选）
- `commentType`: 评论类型（可选）
- `currentPage`: 当前页码（可选，默认1）
- `pageSize`: 页面大小（可选，默认20）

**响应示例：**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 789,
        "targetId": 123,
        "commentType": "CONTENT",
        "parentCommentId": 0,
        "content": "最新评论",
        "userId": 456,
        "userNickname": "用户名",
        "userAvatar": "头像URL",
        "likeCount": 0,
        "replyCount": 0,
        "status": "NORMAL",
        "createTime": "2024-01-01T12:00:00Z"
      }
    ],
    "totalCount": 50,
    "currentPage": 1,
    "pageSize": 20,
    "totalPage": 3
  }
}
```

### 6. 新增查询功能

#### 6.1 根据点赞数范围查询评论
**接口地址：** `GET /api/v1/comments/like-range`

**查询参数：**
- `minLikeCount`: 最小点赞数（可选）
- `maxLikeCount`: 最大点赞数（可选）
- `commentType`: 评论类型（可选）
- `targetId`: 目标对象ID（可选）
- `currentPage`: 当前页码（可选，默认1）
- `pageSize`: 页面大小（可选，默认20）

**响应示例：**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 789,
        "targetId": 123,
        "commentType": "CONTENT",
        "content": "高点赞评论",
        "userId": 456,
        "userNickname": "用户名",
        "likeCount": 50,
        "replyCount": 10,
        "status": "NORMAL",
        "createTime": "2024-01-01T10:00:00Z"
      }
    ],
    "totalCount": 15,
    "currentPage": 1,
    "pageSize": 20,
    "totalPage": 1
  }
}
```

#### 6.2 根据时间范围查询评论
**接口地址：** `GET /api/v1/comments/time-range`

**查询参数：**
- `startTime`: 开始时间（可选，ISO格式：2024-01-01T00:00:00）
- `endTime`: 结束时间（可选，ISO格式：2024-01-01T23:59:59）
- `commentType`: 评论类型（可选）
- `targetId`: 目标对象ID（可选）
- `currentPage`: 当前页码（可选，默认1）
- `pageSize`: 页面大小（可选，默认20）

**响应示例：**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 789,
        "targetId": 123,
        "commentType": "CONTENT",
        "content": "时间范围内的评论",
        "userId": 456,
        "userNickname": "用户名",
        "likeCount": 5,
        "replyCount": 2,
        "status": "NORMAL",
        "createTime": "2024-01-01T10:00:00Z"
      }
    ],
    "totalCount": 25,
    "currentPage": 1,
    "pageSize": 20,
    "totalPage": 2
  }
}
```

### 7. 数据分析功能

#### 7.1 获取评论统计信息
**接口地址：** `GET /api/v1/comments/statistics`

**查询参数：**
- `targetId`: 目标对象ID（可选）
- `commentType`: 评论类型（可选）
- `userId`: 用户ID（可选）
- `startTime`: 开始时间（可选）
- `endTime`: 结束时间（可选）

**响应示例：**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "totalComments": 1000,
    "totalLikes": 5000,
    "totalReplies": 800,
    "avgLikePerComment": 5.0,
    "topUsers": [
      {
        "userId": 1001,
        "userNickname": "活跃用户1",
        "commentCount": 50
      }
    ],
    "dailyStats": [
      {
        "date": "2024-01-01",
        "commentCount": 100,
        "likeCount": 500
      }
    ],
    "commentTypeStats": {
      "CONTENT": 600,
      "DYNAMIC": 400
    }
  }
}
```

#### 7.2 查询用户回复关系
**接口地址：** `GET /api/v1/comments/reply-relations`

**查询参数：**
- `userId`: 用户ID（可选）
- `startTime`: 开始时间（可选）
- `endTime`: 结束时间（可选）

**响应示例：**
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "fromUserId": 1001,
      "fromUserNickname": "用户A",
      "toUserId": 1002,
      "toUserNickname": "用户B",
      "replyCount": 5,
      "lastReplyTime": "2024-01-01T10:00:00Z"
    }
  ]
}
```

#### 7.3 查询评论热度排行
**接口地址：** `GET /api/v1/comments/hot-ranking`

**查询参数：**
- `commentType`: 评论类型（可选）
- `targetId`: 目标对象ID（可选）
- `startTime`: 开始时间（可选）
- `endTime`: 结束时间（可选）
- `limit`: 排行榜数量（可选，默认10）

**响应示例：**
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "commentId": 12345,
      "content": "热门评论内容",
      "likeCount": 100,
      "replyCount": 50,
      "hotScore": 150.5,
      "createTime": "2024-01-01T10:00:00Z",
      "userNickname": "用户昵称"
    }
  ]
}
```

### 8. 管理功能

#### 8.1 批量更新评论状态
**接口地址：** `PUT /api/v1/comments/batch/status`

**查询参数：**
- `status`: 新状态（NORMAL/HIDDEN/DELETED）

**请求体：**
```json
[1, 2, 3, 4, 5]
```

**响应示例：**
```json
{
  "code": 200,
  "message": "success",
  "data": 5
}
```

#### 8.2 批量删除目标评论
**接口地址：** `DELETE /api/v1/comments/batch/target/{targetId}`

**路径参数：**
- `targetId`: 目标对象ID

**查询参数：**
- `commentType`: 评论类型（可选）

**响应示例：**
```json
{
  "code": 200,
  "message": "success",
  "data": 25
}
```

#### 8.3 更新用户信息
**接口地址：** `PUT /api/v1/comments/sync/user/{userId}`

**路径参数：**
- `userId`: 用户ID

**查询参数：**
- `nickname`: 新昵称（可选）
- `avatar`: 新头像URL（可选）

**响应示例：**
```json
{
  "code": 200,
  "message": "success",
  "data": 10
}
```

#### 8.4 更新回复目标用户信息
**接口地址：** `PUT /api/v1/comments/sync/reply-to-user/{replyToUserId}`

**路径参数：**
- `replyToUserId`: 回复目标用户ID

**查询参数：**
- `nickname`: 新昵称（可选）
- `avatar`: 新头像URL（可选）

**响应示例：**
```json
{
  "code": 200,
  "message": "success",
  "data": 8
}
```

#### 8.5 清理已删除评论
**接口地址：** `DELETE /api/v1/comments/cleanup`

**查询参数：**
- `days`: 删除多少天前的数据（可选，默认30）
- `limit`: 限制删除数量（可选，默认1000）

**响应示例：**
```json
{
  "code": 200,
  "message": "success",
  "data": 150
}
```

## 📊 数据模型

### CommentResponse 评论响应对象
```json
{
  "id": "评论ID",
  "targetId": "目标对象ID",
  "commentType": "评论类型（CONTENT/DYNAMIC）",
  "parentCommentId": "父评论ID（0表示根评论）",
  "content": "评论内容",
  "userId": "用户ID",
  "userNickname": "用户昵称",
  "userAvatar": "用户头像",
  "replyToUserId": "回复目标用户ID",
  "replyToUserNickname": "回复目标用户昵称",
  "likeCount": "点赞数",
  "replyCount": "回复数",
  "status": "状态（NORMAL/HIDDEN/DELETED）",
  "createTime": "创建时间",
  "updateTime": "更新时间",
  "replies": "子回复列表（评论树用）"
}
```

### CommentCreateRequest 创建评论请求
```json
{
  "targetId": "目标对象ID",
  "commentType": "评论类型（CONTENT/DYNAMIC）",
  "parentCommentId": "父评论ID（0表示根评论）",
  "content": "评论内容",
  "userId": "用户ID",
  "replyToUserId": "回复目标用户ID（可选）"
}
```

### CommentUpdateRequest 更新评论请求
```json
{
  "id": "评论ID",
  "content": "更新后的内容",
  "userId": "操作用户ID"
}
```

### PageResponse 分页响应对象
```json
{
  "records": "数据列表",
  "totalCount": "总记录数",
  "totalPage": "总页数",
  "currentPage": "当前页码",
  "pageSize": "页面大小",
  "hasNext": "是否有下一页",
  "hasPrevious": "是否有上一页"
}
```

## ⚠️ 错误码说明

| 错误码 | 说明 | 解决方案 |
|--------|------|----------|
| 200 | 成功 | - |
| 400 | 请求参数错误 | 检查请求参数格式 |
| 401 | 未授权 | 检查认证信息 |
| 403 | 权限不足 | 检查用户权限 |
| 404 | 资源不存在 | 检查评论ID是否正确 |
| 500 | 服务器内部错误 | 联系技术支持 |

### 业务错误码

| 错误码 | 说明 | 解决方案 |
|--------|------|----------|
| COMMENT_NOT_FOUND | 评论不存在 | 检查评论ID是否正确 |
| INVALID_PARAMETER | 参数验证失败 | 检查请求参数是否符合要求 |
| PERMISSION_DENIED | 权限不足 | 确认操作用户权限 |
| CREATE_COMMENT_ERROR | 创建评论失败 | 检查评论内容和目标对象 |
| UPDATE_COMMENT_ERROR | 更新评论失败 | 确认评论是否存在且有权限 |
| DELETE_COMMENT_ERROR | 删除评论失败 | 确认评论是否存在且有权限 |
| SEARCH_ERROR | 搜索功能异常 | 检查搜索关键词格式 |
| STATISTICS_ERROR | 统计功能异常 | 检查统计参数设置 |
| LIKE_COUNT_RANGE_ERROR | 点赞数范围查询失败 | 检查点赞数范围参数 |
| TIME_RANGE_ERROR | 时间范围查询失败 | 检查时间格式和范围 |
| BATCH_OPERATION_ERROR | 批量操作失败 | 检查批量操作参数 |
| SYNC_USER_INFO_ERROR | 同步用户信息失败 | 检查用户信息格式 |
| CLEANUP_ERROR | 清理操作失败 | 检查清理参数设置 |

## 🔧 使用示例

### JavaScript 示例
```javascript
// 创建评论
const createComment = async (commentData) => {
  const response = await fetch('/api/v1/comments', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': 'Bearer ' + token
    },
    body: JSON.stringify(commentData)
  });
  return response.json();
};

// 获取评论列表
const getComments = async (targetId, page = 1) => {
  const response = await fetch(`/api/v1/comments/target/${targetId}?currentPage=${page}`);
  return response.json();
};
```

### cURL 示例
```bash
# 创建评论
curl -X POST /api/v1/comments \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer your-token" \
  -d '{
    "targetId": 123,
    "targetType": "CONTENT",
    "content": "这是一条评论",
    "userId": 456,
    "userName": "用户名"
  }'

# 获取评论列表
curl -X GET "/api/v1/comments/target/123?currentPage=1&pageSize=20"
```

## 📈 性能说明

- **响应时间：** 大部分接口响应时间 < 100ms
- **并发支持：** 支持 1000+ QPS
- **缓存策略：** 查询接口支持缓存
- **分页限制：** 单次查询最多返回 100 条记录

## 🔄 版本历史

| 版本 | 日期 | 更新内容 |
|------|------|----------|
| v1.0.0 | 2024-01-01 | 初始版本，基础评论功能 |
| v1.1.0 | 2024-01-15 | 增加搜索功能 |
| v2.0.0 | 2024-02-01 | C端简洁版，移除管理接口 |
| v3.0.0 | 2024-03-01 | 增加统计分析功能 |
| v4.0.0 | 2024-04-01 | 优化数据库索引，提升性能 |
| v5.0.0 | 2024-05-01 | 规范版，增加完整的25个接口，包含数据分析和管理功能 |

### v5.0.0 更新详情

**新增接口（10个）：**
- 增加回复数: `PUT /comments/{commentId}/reply-count`
- 点赞数范围查询: `GET /comments/like-range`
- 时间范围查询: `GET /comments/time-range`
- 评论统计信息: `GET /comments/statistics`
- 用户回复关系: `GET /comments/reply-relations`
- 评论热度排行: `GET /comments/hot-ranking`
- 批量更新状态: `PUT /comments/batch/status`
- 批量删除目标评论: `DELETE /comments/batch/target/{targetId}`
- 更新用户信息: `PUT /comments/sync/user/{userId}`
- 更新回复目标用户信息: `PUT /comments/sync/reply-to-user/{replyToUserId}`
- 清理已删除评论: `DELETE /comments/cleanup`

**优化改进：**
- 统一数据模型，使用 `commentType` 替代 `targetType`
- 统一分页响应格式，使用 `totalCount`, `totalPage`
- 增加完整的业务错误码支持
- 支持时间参数的ISO格式化
- 增强批量操作和管理功能
- 添加数据分析和统计功能

## 📈 性能指标

- **响应时间：** 大部分接口响应时间 < 50ms
- **并发支持：** 支持 5000+ QPS
- **缓存策略：** 查询接口支持Redis缓存，缓存时间5分钟
- **分页限制：** 单次查询最多返回 100 条记录
- **数据库优化：** 使用MySQL 8.0优化索引，支持全文搜索
- **监控告警：** 支持Prometheus监控和告警

## 📞 技术支持

如有问题，请联系：
- **项目：** Collide评论系统 v5.0.0
- **邮箱：** support@collide.com
- **文档：** https://docs.collide.com/comment-api
- **GitHub：** https://github.com/collide/comment-service
- **维护团队：** Collide开发团队

## 🔗 相关文档

- [Comment Facade API 文档](../../Document/apis/comment-facade-api.md)
- [Comment REST API 文档](../../Document/apis/comment-rest-api.md)
- [评论数据模型](../../Document/models/comment-model.md)
- [MySQL索引优化](../../sql/comment/comment-simple.sql) 