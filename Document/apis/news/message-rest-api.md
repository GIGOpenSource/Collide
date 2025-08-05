# 消息管理 REST API 文档

## 概述

消息管理REST API提供了完整的消息系统HTTP接口，支持私信、留言板、消息回复等功能。基于message-simple.sql的无连表设计，提供高性能的消息服务。

- **模块**: 消息管理 (Message Management)
- **控制器**: MessageController
- **基础路径**: `/api/v1/messages`
- **版本**: 2.0.0
- **总接口数**: 25个

## 功能特性

- ✅ **消息发送**: 私信、回复、留言板消息
- ✅ **消息查询**: 聊天记录、条件查询、关键词搜索
- ✅ **消息管理**: 更新、删除、置顶、标记已读
- ✅ **批量操作**: 批量已读、批量删除、会话全部已读
- ✅ **统计功能**: 未读数统计、发送/接收统计
- ✅ **会话管理**: 最近聊天用户、最新消息
- ✅ **系统管理**: 过期消息清理、健康检查

---

## 1. 消息发送

### 1.1 发送消息

**接口描述**: 发送消息，支持文本、图片、文件等多种消息类型

- **请求方式**: `POST`
- **请求路径**: `/api/v1/messages/send`
- **请求类型**: `application/json`

**请求参数**:
```json
{
  "senderId": 1001,           // 发送者ID (必填)
  "receiverId": 1002,         // 接收者ID (必填)
  "messageType": "TEXT",      // 消息类型 (必填): TEXT/IMAGE/FILE/VOICE
  "content": "Hello!",        // 消息内容 (必填)
  "attachments": {            // 附件信息 (可选)
    "fileUrl": "https://...",
    "fileName": "image.jpg",
    "fileSize": 1024
  }
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "messageId": 1001,
    "senderId": 1001,
    "receiverId": 1002,
    "messageType": "TEXT",
    "content": "Hello!",
    "status": "SENT",
    "createTime": "2024-01-30 10:30:00"
  }
}
```

**错误码**:
- `USER_NOT_FOUND`: 用户不存在
- `MESSAGE_SEND_PERMISSION_DENIED`: 无发送权限
- `MESSAGE_CONTENT_INVALID`: 消息内容无效

---

### 1.2 回复消息

**接口描述**: 回复指定的消息，建立回复关系

- **请求方式**: `POST`
- **请求路径**: `/api/v1/messages/reply`
- **请求类型**: `application/json`

**请求参数**:
```json
{
  "senderId": 1001,
  "receiverId": 1002,
  "messageType": "TEXT",
  "content": "回复内容",
  "replyToId": 500           // 被回复的消息ID (必填)
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "messageId": 1002,
    "replyToId": 500,
    "content": "回复内容",
    "createTime": "2024-01-30 10:31:00"
  }
}
```

---

### 1.3 发送留言板消息

**接口描述**: 发送到用户留言板的公开消息

- **请求方式**: `POST`
- **请求路径**: `/api/v1/messages/wall`
- **请求类型**: `application/json`

**请求参数**:
```json
{
  "senderId": 1001,
  "receiverId": 1002,
  "messageType": "WALL",
  "content": "留言内容"
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "messageId": 1003,
    "messageType": "WALL",
    "content": "留言内容",
    "isPublic": true,
    "createTime": "2024-01-30 10:32:00"
  }
}
```

---

## 2. 消息查询

### 2.1 获取消息详情

**接口描述**: 根据消息ID获取详细信息

- **请求方式**: `GET`
- **请求路径**: `/api/v1/messages/{messageId}`

**路径参数**:
- `messageId`: 消息ID (必填)

**查询参数**:
- `userId`: 查看者用户ID (必填)

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "messageId": 1001,
    "senderId": 1001,
    "receiverId": 1002,
    "content": "消息内容",
    "messageType": "TEXT",
    "status": "READ",
    "isPinned": false,
    "createTime": "2024-01-30 10:30:00",
    "readTime": "2024-01-30 10:35:00"
  }
}
```

---

### 2.2 条件查询消息

**接口描述**: 支持多维度条件查询

- **请求方式**: `POST`
- **请求路径**: `/api/v1/messages/query`
- **请求类型**: `application/json`

**请求参数**:
```json
{
  "senderId": 1001,                    // 发送者ID (可选)
  "receiverId": 1002,                  // 接收者ID (可选)
  "messageType": "TEXT",               // 消息类型 (可选)
  "status": "UNREAD",                  // 消息状态 (可选)
  "keyword": "关键词",                  // 搜索关键词 (可选)
  "startTime": "2024-01-01 00:00:00", // 开始时间 (可选)
  "endTime": "2024-01-31 23:59:59",   // 结束时间 (可选)
  "currentPage": 1,                    // 当前页码 (默认1)
  "pageSize": 20                       // 页面大小 (默认20)
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "currentPage": 1,
    "pageSize": 20,
    "totalPage": 5,
    "totalCount": 98,
    "datas": [
      {
        "messageId": 1001,
        "senderId": 1001,
        "receiverId": 1002,
        "content": "消息内容",
        "messageType": "TEXT",
        "status": "READ",
        "createTime": "2024-01-30 10:30:00"
      }
    ]
  }
}
```

---

### 2.3 查询聊天记录

**接口描述**: 获取两用户间的消息记录

- **请求方式**: `GET`
- **请求路径**: `/api/v1/messages/chat`

**查询参数**:
- `userId1`: 用户1ID (必填)
- `userId2`: 用户2ID (必填)
- `status`: 消息状态 (可选)
- `currentPage`: 当前页码 (默认1)
- `pageSize`: 页面大小 (默认20)

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "currentPage": 1,
    "pageSize": 20,
    "totalCount": 45,
    "datas": [
      {
        "messageId": 1001,
        "senderId": 1001,
        "receiverId": 1002,
        "content": "Hello!",
        "createTime": "2024-01-30 10:30:00"
      },
      {
        "messageId": 1002,
        "senderId": 1002,
        "receiverId": 1001,
        "content": "Hi there!",
        "createTime": "2024-01-30 10:31:00"
      }
    ]
  }
}
```

---

### 2.4 查询留言板消息

**接口描述**: 获取用户留言板的消息

- **请求方式**: `GET`
- **请求路径**: `/api/v1/messages/wall/{receiverId}`

**路径参数**:
- `receiverId`: 接收者ID (必填)

**查询参数**:
- `status`: 消息状态 (可选)
- `currentPage`: 当前页码 (默认1)
- `pageSize`: 页面大小 (默认20)

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "currentPage": 1,
    "pageSize": 20,
    "totalCount": 12,
    "datas": [
      {
        "messageId": 1003,
        "senderId": 1001,
        "receiverId": 1002,
        "content": "留言内容",
        "messageType": "WALL",
        "isPublic": true,
        "createTime": "2024-01-30 10:32:00"
      }
    ]
  }
}
```

---

### 2.5 查询消息回复

**接口描述**: 获取消息的回复列表

- **请求方式**: `GET`
- **请求路径**: `/api/v1/messages/{replyToId}/replies`

**路径参数**:
- `replyToId`: 原消息ID (必填)

**查询参数**:
- `status`: 消息状态 (可选)
- `currentPage`: 当前页码 (默认1)
- `pageSize`: 页面大小 (默认20)

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "currentPage": 1,
    "pageSize": 20,
    "totalCount": 3,
    "datas": [
      {
        "messageId": 1004,
        "replyToId": 500,
        "senderId": 1002,
        "content": "回复内容1",
        "createTime": "2024-01-30 10:33:00"
      }
    ]
  }
}
```

---

### 2.6 搜索用户消息

**接口描述**: 支持消息内容关键词搜索

- **请求方式**: `GET`
- **请求路径**: `/api/v1/messages/search`

**查询参数**:
- `userId`: 用户ID (必填)
- `keyword`: 搜索关键词 (必填)
- `status`: 消息状态 (可选)
- `currentPage`: 当前页码 (默认1)
- `pageSize`: 页面大小 (默认20)

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "currentPage": 1,
    "pageSize": 20,
    "totalCount": 5,
    "datas": [
      {
        "messageId": 1005,
        "senderId": 1001,
        "receiverId": 1002,
        "content": "包含关键词的消息内容",
        "createTime": "2024-01-30 10:34:00"
      }
    ]
  }
}
```

---

## 3. 消息管理

### 3.1 更新消息

**接口描述**: 支持更新消息内容（仅限发送者）

- **请求方式**: `PUT`
- **请求路径**: `/api/v1/messages/{messageId}`
- **请求类型**: `application/json`

**路径参数**:
- `messageId`: 消息ID (必填)

**请求参数**:
```json
{
  "operatorId": 1001,         // 操作者ID (必填)
  "content": "更新后的内容",    // 新内容 (可选)
  "status": "EDITED"          // 新状态 (可选)
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "messageId": 1001,
    "content": "更新后的内容",
    "status": "EDITED",
    "updateTime": "2024-01-30 10:35:00"
  }
}
```

---

### 3.2 删除消息

**接口描述**: 逻辑删除，支持发送者和接收者删除

- **请求方式**: `DELETE`
- **请求路径**: `/api/v1/messages/{messageId}`

**路径参数**:
- `messageId`: 消息ID (必填)

**查询参数**:
- `userId`: 操作用户ID (必填)

**响应示例**:
```json
{
  "code": 200,
  "message": "删除成功",
  "data": null
}
```

---

### 3.3 标记消息已读

**接口描述**: 将指定消息标记为已读状态

- **请求方式**: `PUT`
- **请求路径**: `/api/v1/messages/{messageId}/read`

**路径参数**:
- `messageId`: 消息ID (必填)

**查询参数**:
- `userId`: 用户ID (必填)

**响应示例**:
```json
{
  "code": 200,
  "message": "标记成功",
  "data": null
}
```

---

### 3.4 更新消息置顶状态

**接口描述**: 设置或取消消息置顶

- **请求方式**: `PUT`
- **请求路径**: `/api/v1/messages/{messageId}/pin`

**路径参数**:
- `messageId`: 消息ID (必填)

**查询参数**:
- `isPinned`: 是否置顶 (必填)
- `userId`: 操作用户ID (必填)

**响应示例**:
```json
{
  "code": 200,
  "message": "置顶设置成功",
  "data": null
}
```

---

## 4. 批量操作

### 4.1 批量标记消息已读

**接口描述**: 批量将消息标记为已读状态

- **请求方式**: `PUT`
- **请求路径**: `/api/v1/messages/batch/read`
- **请求类型**: `application/json`

**查询参数**:
- `userId`: 用户ID (必填)

**请求参数**:
```json
[1001, 1002, 1003, 1004]    // 消息ID列表
```

**响应示例**:
```json
{
  "code": 200,
  "message": "批量标记成功",
  "data": null
}
```

---

### 4.2 批量删除消息

**接口描述**: 批量逻辑删除消息

- **请求方式**: `DELETE`
- **请求路径**: `/api/v1/messages/batch`
- **请求类型**: `application/json`

**查询参数**:
- `userId`: 操作用户ID (必填)

**请求参数**:
```json
[1001, 1002, 1003, 1004]    // 消息ID列表
```

**响应示例**:
```json
{
  "code": 200,
  "message": "批量删除成功",
  "data": null
}
```

---

### 4.3 标记会话已读

**接口描述**: 将会话中所有消息标记为已读

- **请求方式**: `PUT`
- **请求路径**: `/api/v1/messages/session/read`

**查询参数**:
- `receiverId`: 接收者ID (必填)
- `senderId`: 发送者ID (必填)

**响应示例**:
```json
{
  "code": 200,
  "message": "会话标记成功",
  "data": null
}
```

---

## 5. 统计功能

### 5.1 统计未读消息数

**接口描述**: 获取用户的未读消息总数

- **请求方式**: `GET`
- **请求路径**: `/api/v1/messages/unread/count`

**查询参数**:
- `userId`: 用户ID (必填)

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": 12
}
```

---

### 5.2 统计与用户的未读消息数

**接口描述**: 获取与特定用户的未读消息数

- **请求方式**: `GET`
- **请求路径**: `/api/v1/messages/unread/count/with`

**查询参数**:
- `receiverId`: 接收者ID (必填)
- `senderId`: 发送者ID (必填)

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": 3
}
```

---

### 5.3 统计发送消息数

**接口描述**: 统计用户在指定时间范围内发送的消息数

- **请求方式**: `GET`
- **请求路径**: `/api/v1/messages/sent/count`

**查询参数**:
- `userId`: 用户ID (必填)
- `startTime`: 开始时间 (可选, 格式: yyyy-MM-dd HH:mm:ss)
- `endTime`: 结束时间 (可选, 格式: yyyy-MM-dd HH:mm:ss)

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": 56
}
```

---

### 5.4 统计接收消息数

**接口描述**: 统计用户在指定时间范围内接收的消息数

- **请求方式**: `GET`
- **请求路径**: `/api/v1/messages/received/count`

**查询参数**:
- `userId`: 用户ID (必填)
- `startTime`: 开始时间 (可选, 格式: yyyy-MM-dd HH:mm:ss)
- `endTime`: 结束时间 (可选, 格式: yyyy-MM-dd HH:mm:ss)

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": 34
}
```

---

## 6. 会话管理

### 6.1 获取最近聊天用户

**接口描述**: 获取用户最近聊天的用户列表

- **请求方式**: `GET`
- **请求路径**: `/api/v1/messages/recent/users`

**查询参数**:
- `userId`: 用户ID (必填)
- `limit`: 限制数量 (默认20)

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": [1002, 1003, 1004, 1005]
}
```

---

### 6.2 获取最新消息

**接口描述**: 获取两用户间的最新一条消息

- **请求方式**: `GET`
- **请求路径**: `/api/v1/messages/latest`

**查询参数**:
- `userId1`: 用户1ID (必填)
- `userId2`: 用户2ID (必填)

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "messageId": 1010,
    "senderId": 1001,
    "receiverId": 1002,
    "content": "最新消息内容",
    "createTime": "2024-01-30 15:30:00"
  }
}
```

---

## 7. 系统管理

### 7.1 清理过期消息

**接口描述**: 物理删除指定时间前的已删除消息

- **请求方式**: `DELETE`
- **请求路径**: `/api/v1/messages/cleanup`

**查询参数**:
- `beforeTime`: 截止时间 (必填, 格式: yyyy-MM-dd HH:mm:ss)

**响应示例**:
```json
{
  "code": 200,
  "message": "清理完成",
  "data": 23
}
```

---

### 7.2 系统健康检查

**接口描述**: 检查消息系统运行状态

- **请求方式**: `GET`
- **请求路径**: `/api/v1/messages/health`

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": "消息系统运行正常"
}
```

---

## 通用错误码

| 错误码 | 错误信息 | 说明 |
|-------|---------|------|
| 400 | 请求参数错误 | 参数格式不正确或缺少必填参数 |
| 401 | 未授权访问 | 需要登录或权限不足 |
| 403 | 禁止访问 | 用户被禁用或无权限 |
| 404 | 资源不存在 | 消息或用户不存在 |
| 429 | 请求过于频繁 | 触发限流规则 |
| 500 | 服务器内部错误 | 系统异常 |

## 业务错误码

| 错误码 | 错误信息 | 说明 |
|-------|---------|------|
| USER_NOT_FOUND | 用户不存在 | 发送者或接收者不存在 |
| MESSAGE_NOT_FOUND | 消息不存在 | 指定的消息ID不存在 |
| MESSAGE_SEND_PERMISSION_DENIED | 无发送权限 | 被接收者设置拒收陌生人消息 |
| MESSAGE_CONTENT_INVALID | 消息内容无效 | 内容为空或包含敏感词 |
| MESSAGE_UPDATE_PERMISSION_DENIED | 无更新权限 | 只有发送者可以更新消息 |
| MESSAGE_DELETE_PERMISSION_DENIED | 无删除权限 | 只有发送者和接收者可以删除 |
| ATTACHMENT_UPLOAD_FAILED | 附件上传失败 | 附件大小超限或格式不支持 |
| MESSAGE_REPLY_TARGET_NOT_FOUND | 回复目标不存在 | 被回复的消息不存在 |

---

**文档版本**: v2.0.0  
**最后更新**: 2024-01-30  
**维护者**: GIG Team