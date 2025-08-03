# 评论模块 API 接口文档

## 📋 概述

本文档描述了评论模块的客户端API接口，提供完整的评论功能，包括评论的创建、更新、删除、查询和统计等功能。

**基础信息：**
- **服务名称：** 评论服务 (Comment Service)
- **API版本：** v1
- **基础路径：** `/api/v1/comments`
- **协议：** HTTP/HTTPS
- **数据格式：** JSON

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
        "targetType": "CONTENT",
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
        "targetType": "CONTENT",
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
        "targetType": "CONTENT",
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
        "targetType": "CONTENT",
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
        "targetType": "CONTENT",
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

#### 4.4 统计用户评论数
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
        "targetType": "CONTENT",
        "parentCommentId": 0,
        "content": "包含关键词的评论",
        "userId": 456,
        "userName": "用户名",
        "userAvatar": "头像URL",
        "likeCount": 5,
        "replyCount": 2,
        "status": "NORMAL",
        "createTime": "2024-01-01T10:00:00Z"
      }
    ],
    "total": 5,
    "currentPage": 1,
    "pageSize": 20,
    "totalPages": 1
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
        "targetType": "CONTENT",
        "parentCommentId": 0,
        "content": "热门评论",
        "userId": 456,
        "userName": "用户名",
        "userAvatar": "头像URL",
        "likeCount": 100,
        "replyCount": 20,
        "status": "NORMAL",
        "createTime": "2024-01-01T10:00:00Z"
      }
    ],
    "total": 10,
    "currentPage": 1,
    "pageSize": 20,
    "totalPages": 1
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
        "targetType": "CONTENT",
        "parentCommentId": 0,
        "content": "最新评论",
        "userId": 456,
        "userName": "用户名",
        "userAvatar": "头像URL",
        "likeCount": 0,
        "replyCount": 0,
        "status": "NORMAL",
        "createTime": "2024-01-01T12:00:00Z"
      }
    ],
    "total": 50,
    "currentPage": 1,
    "pageSize": 20,
    "totalPages": 3
  }
}
```

## 📊 数据模型

### CommentResponse 评论响应对象
```json
{
  "id": "评论ID",
  "targetId": "目标对象ID",
  "targetType": "目标类型（CONTENT/DYNAMIC）",
  "parentCommentId": "父评论ID（0表示根评论）",
  "content": "评论内容",
  "userId": "用户ID",
  "userName": "用户名",
  "userAvatar": "用户头像",
  "likeCount": "点赞数",
  "replyCount": "回复数",
  "status": "状态（NORMAL/DELETED）",
  "createTime": "创建时间",
  "updateTime": "更新时间"
}
```

### CommentCreateRequest 创建评论请求
```json
{
  "targetId": "目标对象ID",
  "targetType": "目标类型",
  "parentCommentId": "父评论ID",
  "content": "评论内容",
  "userId": "用户ID",
  "userName": "用户名",
  "userAvatar": "用户头像"
}
```

### CommentUpdateRequest 更新评论请求
```json
{
  "id": "评论ID",
  "content": "更新后的内容"
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
| v1.0.0 | 2024-01-01 | 初始版本 |
| v1.1.0 | 2024-01-15 | 增加搜索功能 |
| v2.0.0 | 2024-02-01 | C端简洁版，移除管理接口 |

## 📞 技术支持

如有问题，请联系：
- **邮箱：** support@collide.com
- **文档：** https://docs.collide.com/comment-api
- **GitHub：** https://github.com/collide/comment-service 