# 消息会话管理 REST API 文档

## 概述

消息会话管理REST API提供了完整的会话系统HTTP接口，管理用户间的会话状态、未读计数和会话列表。基于message-simple.sql的t_message_session表设计，提供高效的会话管理服务。

- **模块**: 消息会话管理 (Message Session Management)
- **控制器**: MessageSessionController
- **基础路径**: `/api/v1/sessions`
- **版本**: 2.0.0
- **总接口数**: 22个

## 功能特性

- ✅ **会话管理**: 创建/更新会话、查询会话、归档管理
- ✅ **会话查询**: 用户会话列表、活跃会话、未读会话
- ✅ **统计功能**: 未读会话数、总会话数、总未读数
- ✅ **状态管理**: 最后消息更新、未读计数管理
- ✅ **批量操作**: 批量归档、批量取消归档
- ✅ **系统功能**: 会话清理、索引重建、健康检查

---

## 1. 会话创建和管理

### 1.1 创建或更新会话

**接口描述**: 如果会话不存在则创建，存在则更新

- **请求方式**: `POST`
- **请求路径**: `/api/v1/sessions`
- **请求类型**: `application/json`

**请求参数**:
```json
{
  "userId": 1001,           // 用户ID (必填)
  "otherUserId": 1002,      // 对方用户ID (必填)
  "sessionType": "PRIVATE", // 会话类型 (可选): PRIVATE/GROUP
  "lastMessageId": 500,     // 最后消息ID (可选)
  "lastMessageTime": "2024-01-30 10:30:00" // 最后消息时间 (可选)
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "sessionId": 1001,
    "userId": 1001,
    "otherUserId": 1002,
    "sessionType": "PRIVATE",
    "lastMessageId": 500,
    "lastMessageTime": "2024-01-30 10:30:00",
    "unreadCount": 0,
    "isArchived": false,
    "createTime": "2024-01-30 10:00:00",
    "updateTime": "2024-01-30 10:30:00"
  }
}
```

**错误码**:
- `USER_NOT_FOUND`: 用户不存在
- `SESSION_CREATE_FAILED`: 会话创建失败

---

### 1.2 获取会话详情

**接口描述**: 根据用户ID查询会话信息

- **请求方式**: `GET`
- **请求路径**: `/api/v1/sessions`

**查询参数**:
- `userId`: 用户ID (必填)
- `otherUserId`: 对方用户ID (必填)

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "sessionId": 1001,
    "userId": 1001,
    "otherUserId": 1002,
    "sessionType": "PRIVATE",
    "lastMessageId": 500,
    "lastMessageContent": "最后一条消息内容",
    "lastMessageTime": "2024-01-30 10:30:00",
    "unreadCount": 3,
    "isArchived": false,
    "createTime": "2024-01-30 10:00:00",
    "updateTime": "2024-01-30 10:30:00"
  }
}
```

---

### 1.3 更新会话信息

**接口描述**: 更新会话的状态和信息

- **请求方式**: `PUT`
- **请求路径**: `/api/v1/sessions/{sessionId}`
- **请求类型**: `application/json`

**路径参数**:
- `sessionId`: 会话ID (必填)

**请求参数**:
```json
{
  "updateType": "LAST_MESSAGE",           // 更新类型 (必填)
  "lastMessageId": 501,                   // 最后消息ID (可选)
  "lastMessageTime": "2024-01-30 10:35:00", // 最后消息时间 (可选)
  "unreadCount": 5,                       // 未读数量 (可选)
  "isArchived": false                     // 是否归档 (可选)
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "sessionId": 1001,
    "lastMessageId": 501,
    "lastMessageTime": "2024-01-30 10:35:00",
    "unreadCount": 5,
    "updateTime": "2024-01-30 10:35:00"
  }
}
```

---

### 1.4 更新会话归档状态

**接口描述**: 设置或取消会话归档

- **请求方式**: `PUT`
- **请求路径**: `/api/v1/sessions/{sessionId}/archive`

**路径参数**:
- `sessionId`: 会话ID (必填)

**查询参数**:
- `isArchived`: 是否归档 (必填)
- `userId`: 操作用户ID (必填)

**响应示例**:
```json
{
  "code": 200,
  "message": "归档设置成功",
  "data": null
}
```

---

## 2. 会话查询

### 2.1 条件查询用户会话

**接口描述**: 支持按归档状态、未读状态筛选

- **请求方式**: `POST`
- **请求路径**: `/api/v1/sessions/query`
- **请求类型**: `application/json`

**请求参数**:
```json
{
  "userId": 1001,                     // 用户ID (必填)
  "isArchived": false,                // 是否归档 (可选)
  "hasUnread": true,                  // 是否有未读 (可选)
  "sessionType": "PRIVATE",           // 会话类型 (可选)
  "startTime": "2024-01-01 00:00:00", // 开始时间 (可选)
  "endTime": "2024-01-31 23:59:59",   // 结束时间 (可选)
  "currentPage": 1,                   // 当前页码 (默认1)
  "pageSize": 20                      // 页面大小 (默认20)
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
    "totalPage": 3,
    "totalCount": 56,
    "datas": [
      {
        "sessionId": 1001,
        "userId": 1001,
        "otherUserId": 1002,
        "sessionType": "PRIVATE",
        "lastMessageContent": "最后一条消息",
        "lastMessageTime": "2024-01-30 10:30:00",
        "unreadCount": 3,
        "isArchived": false,
        "createTime": "2024-01-30 10:00:00"
      }
    ]
  }
}
```

---

### 2.2 获取用户会话列表

**接口描述**: 分页获取用户的会话列表

- **请求方式**: `GET`
- **请求路径**: `/api/v1/sessions/user/{userId}`

**路径参数**:
- `userId`: 用户ID (必填)

**查询参数**:
- `isArchived`: 是否归档 (可选)
- `hasUnread`: 是否有未读 (可选)
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
        "sessionId": 1001,
        "otherUserId": 1002,
        "otherUserNickname": "张三",
        "otherUserAvatar": "https://...",
        "lastMessageContent": "最后一条消息",
        "lastMessageTime": "2024-01-30 10:30:00",
        "unreadCount": 3,
        "isArchived": false
      }
    ]
  }
}
```

---

### 2.3 获取用户活跃会话

**接口描述**: 查询指定时间后有消息交互的会话

- **请求方式**: `GET`
- **请求路径**: `/api/v1/sessions/user/{userId}/active`

**路径参数**:
- `userId`: 用户ID (必填)

**查询参数**:
- `sinceTime`: 起始时间 (可选, 格式: yyyy-MM-dd HH:mm:ss)
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
        "sessionId": 1001,
        "otherUserId": 1002,
        "lastMessageTime": "2024-01-30 15:30:00",
        "unreadCount": 2,
        "isActive": true
      }
    ]
  }
}
```

---

### 2.4 获取用户未读会话

**接口描述**: 获取有未读消息的会话列表

- **请求方式**: `GET`
- **请求路径**: `/api/v1/sessions/user/{userId}/unread`

**路径参数**:
- `userId`: 用户ID (必填)

**查询参数**:
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
    "totalCount": 8,
    "datas": [
      {
        "sessionId": 1001,
        "otherUserId": 1002,
        "lastMessageContent": "未读的消息",
        "lastMessageTime": "2024-01-30 15:25:00",
        "unreadCount": 5
      }
    ]
  }
}
```

---

## 3. 统计功能

### 3.1 统计未读会话数

**接口描述**: 获取用户的未读会话总数

- **请求方式**: `GET`
- **请求路径**: `/api/v1/sessions/user/{userId}/unread/count`

**路径参数**:
- `userId`: 用户ID (必填)

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": 8
}
```

---

### 3.2 统计用户会话总数

**接口描述**: 获取用户的会话总数

- **请求方式**: `GET`
- **请求路径**: `/api/v1/sessions/user/{userId}/count`

**路径参数**:
- `userId`: 用户ID (必填)

**查询参数**:
- `isArchived`: 是否归档 (可选)

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": 45
}
```

---

### 3.3 获取用户总未读数

**接口描述**: 统计用户所有会话的未读消息总数

- **请求方式**: `GET`
- **请求路径**: `/api/v1/sessions/user/{userId}/unread/total`

**路径参数**:
- `userId`: 用户ID (必填)

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": 23
}
```

---

## 4. 会话状态管理

### 4.1 更新会话最后消息

**接口描述**: 在新消息到达时自动调用

- **请求方式**: `PUT`
- **请求路径**: `/api/v1/sessions/last-message`

**查询参数**:
- `userId`: 用户ID (必填)
- `otherUserId`: 对方用户ID (必填)
- `lastMessageId`: 最后消息ID (必填)
- `lastMessageTime`: 最后消息时间 (必填, 格式: yyyy-MM-dd HH:mm:ss)

**响应示例**:
```json
{
  "code": 200,
  "message": "更新成功",
  "data": null
}
```

---

### 4.2 增加会话未读计数

**接口描述**: 新消息到达时增加未读数

- **请求方式**: `PUT`
- **请求路径**: `/api/v1/sessions/unread/increment`

**查询参数**:
- `userId`: 用户ID (必填)
- `otherUserId`: 对方用户ID (必填)

**响应示例**:
```json
{
  "code": 200,
  "message": "更新成功",
  "data": null
}
```

---

### 4.3 清零会话未读计数

**接口描述**: 用户查看消息时调用

- **请求方式**: `PUT`
- **请求路径**: `/api/v1/sessions/unread/clear`

**查询参数**:
- `userId`: 用户ID (必填)
- `otherUserId`: 对方用户ID (必填)

**响应示例**:
```json
{
  "code": 200,
  "message": "清零成功",
  "data": null
}
```

---

### 4.4 处理新消息事件

**接口描述**: 自动创建或更新相关用户的会话状态

- **请求方式**: `POST`
- **请求路径**: `/api/v1/sessions/new-message`

**查询参数**:
- `senderId`: 发送者ID (必填)
- `receiverId`: 接收者ID (必填)
- `messageId`: 消息ID (必填)
- `messageTime`: 消息时间 (必填, 格式: yyyy-MM-dd HH:mm:ss)

**响应示例**:
```json
{
  "code": 200,
  "message": "处理成功",
  "data": null
}
```

---

## 5. 会话清理

### 5.1 删除空会话

**接口描述**: 删除没有消息记录的会话

- **请求方式**: `DELETE`
- **请求路径**: `/api/v1/sessions/empty`

**查询参数**:
- `userId`: 用户ID (可选，为空则清理所有用户)

**响应示例**:
```json
{
  "code": 200,
  "message": "清理完成",
  "data": 5
}
```

---

### 5.2 删除归档会话

**接口描述**: 删除指定时间前的归档会话

- **请求方式**: `DELETE`
- **请求路径**: `/api/v1/sessions/archived`

**查询参数**:
- `beforeTime`: 截止时间 (必填, 格式: yyyy-MM-dd HH:mm:ss)
- `userId`: 用户ID (可选)

**响应示例**:
```json
{
  "code": 200,
  "message": "删除完成",
  "data": 12
}
```

---

### 5.3 批量归档会话

**接口描述**: 批量设置会话为归档状态

- **请求方式**: `PUT`
- **请求路径**: `/api/v1/sessions/batch/archive`
- **请求类型**: `application/json`

**查询参数**:
- `userId`: 操作用户ID (必填)

**请求参数**:
```json
[1001, 1002, 1003, 1004]    // 会话ID列表
```

**响应示例**:
```json
{
  "code": 200,
  "message": "批量归档成功",
  "data": null
}
```

---

### 5.4 批量取消归档会话

**接口描述**: 批量取消会话的归档状态

- **请求方式**: `PUT`
- **请求路径**: `/api/v1/sessions/batch/unarchive`
- **请求类型**: `application/json`

**查询参数**:
- `userId`: 操作用户ID (必填)

**请求参数**:
```json
[1001, 1002, 1003, 1004]    // 会话ID列表
```

**响应示例**:
```json
{
  "code": 200,
  "message": "批量取消归档成功",
  "data": null
}
```

---

## 6. 系统功能

### 6.1 重建会话索引

**接口描述**: 系统维护功能，重新计算会话的统计信息

- **请求方式**: `POST`
- **请求路径**: `/api/v1/sessions/rebuild-index`

**查询参数**:
- `userId`: 用户ID (可选)

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": "会话索引重建完成，处理了45个会话"
}
```

---

### 6.2 系统健康检查

**接口描述**: 检查会话系统运行状态

- **请求方式**: `GET`
- **请求路径**: `/api/v1/sessions/health`

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": "会话系统运行正常"
}
```

---

## 通用错误码

| 错误码 | 错误信息 | 说明 |
|-------|---------|------|
| 400 | 请求参数错误 | 参数格式不正确或缺少必填参数 |
| 401 | 未授权访问 | 需要登录或权限不足 |
| 403 | 禁止访问 | 用户被禁用或无权限 |
| 404 | 资源不存在 | 会话或用户不存在 |
| 429 | 请求过于频繁 | 触发限流规则 |
| 500 | 服务器内部错误 | 系统异常 |

## 业务错误码

| 错误码 | 错误信息 | 说明 |
|-------|---------|------|
| USER_NOT_FOUND | 用户不存在 | 指定的用户ID不存在 |
| SESSION_NOT_FOUND | 会话不存在 | 指定的会话ID不存在 |
| SESSION_CREATE_FAILED | 会话创建失败 | 创建会话时发生错误 |
| SESSION_UPDATE_PERMISSION_DENIED | 无会话更新权限 | 只有会话参与者可以更新 |
| SESSION_ARCHIVE_FAILED | 会话归档失败 | 归档操作失败 |
| SESSION_DUPLICATE | 会话已存在 | 两用户间已存在会话 |
| UNREAD_COUNT_INVALID | 未读数量无效 | 未读数量不能为负数 |

---

**文档版本**: v2.0.0  
**最后更新**: 2024-01-30  
**维护者**: GIG Team