# Collide 消息服务 API 文档

## 概述

Collide 消息服务提供完整的私信消息和留言板功能，基于简洁的单表设计，支持文本、图片、文件等多种消息类型，以及消息回复、置顶、搜索等高级功能。

**服务版本**: v2.0.0 (简洁版)  
**基础路径**: `/api/v1/message`  
**Dubbo服务**: `collide-message` (version: 1.0.0)  
**设计理念**: 基于message-simple.sql的单表设计，实现核心私信功能

## 🎯 核心特性

- **🚀 高性能缓存**: 基于JetCache的分布式缓存，显著提升消息查询速度
- **💬 私信功能**: 支持一对一私信通信，多种消息类型
- **📝 留言板**: 用户个人页面留言功能，支持置顶管理
- **💾 消息回复**: 支持层级回复和回复链查询
- **🔍 消息搜索**: 全文搜索用户消息内容
- **📊 实时统计**: 未读消息数、消息统计等实时数据
- **🔄 会话管理**: 智能会话列表，按最新消息时间排序

## 🆕 v2.0.0 新增功能

- **简洁设计**: 基于单表设计，优化性能和维护成本
- **JetCache缓存**: 全面的缓存策略，提升消息查询性能
- **API设计优化**: 统一使用 `currentPage` 参数，对齐系统设计风格
- **Swagger文档**: 完整的OpenAPI 3.0文档支持

---

## 消息发送 API

### 1. 发送消息
**接口路径**: `POST /api/v1/message/send`  
**接口描述**: 发送私信消息，支持文本、图片、文件等类型

#### 请求参数
```json
{
  "senderId": 12345,                     // 必填，发送者用户ID
  "receiverId": 67890,                   // 必填，接收者用户ID
  "messageType": "TEXT",                 // 必填，消息类型：TEXT/IMAGE/FILE/AUDIO/VIDEO
  "content": "Hello，很高兴认识你！",        // 必填，消息内容
  "extraData": {                         // 可选，扩展数据
    "fileName": "document.pdf",
    "fileSize": 1024000,
    "filePath": "/uploads/files/xxx.pdf"
  },
  "replyToId": null,                     // 可选，回复的消息ID（普通发送时为null）
  "messageCategory": "PRIVATE"           // 可选，消息分类：PRIVATE/WALL/SYSTEM
}
```

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "responseCode": "SUCCESS",
  "responseMessage": "消息发送成功",
  "data": {
    "id": 98765,
    "senderId": 12345,
    "receiverId": 67890,
    "messageType": "TEXT",
    "content": "Hello，很高兴认识你！",
    "status": "SENT",
    "createTime": "2024-01-16T10:30:00",
    "isRead": false,
    "isPinned": false,
    "replyToId": null,
    "messageCategory": "PRIVATE",
    "extraData": {}
  }
}
```

**特性说明**:
- ⚡ **实时发送**: 消息立即投递到接收方
- 🔒 **权限验证**: 自动验证发送者身份和接收者有效性
- 📝 **多类型支持**: 文本、图片、文件、音频、视频等

---

### 2. 回复消息
**接口路径**: `POST /api/v1/message/reply`  
**接口描述**: 回复指定消息

#### 请求参数
```json
{
  "senderId": 67890,                     // 必填，回复者用户ID
  "receiverId": 12345,                   // 必填，接收者用户ID
  "messageType": "TEXT",                 // 必填，消息类型
  "content": "谢谢你的消息！",             // 必填，回复内容
  "replyToId": 98765                     // 必填，被回复的消息ID
}
```

#### 响应示例
```json
{
  "success": true,
  "data": {
    "id": 98766,
    "senderId": 67890,
    "receiverId": 12345,
    "messageType": "TEXT",
    "content": "谢谢你的消息！",
    "status": "SENT",
    "createTime": "2024-01-16T10:35:00",
    "replyToId": 98765,
    "messageCategory": "PRIVATE"
  }
}
```

**特性说明**:
- 🔗 **回复链**: 自动建立消息回复关系
- 📝 **回复追踪**: 支持查询消息的所有回复

---

## 消息查询 API

### 1. 获取消息详情
**接口路径**: `GET /api/v1/message/{messageId}`  
**接口描述**: 根据消息ID获取详细信息

#### 请求参数
- **messageId** (path): 消息ID，必填
- **userId** (query): 查看者用户ID，必填

#### 响应示例
```json
{
  "success": true,
  "data": {
    "id": 98765,
    "senderId": 12345,
    "receiverId": 67890,
    "messageType": "TEXT",
    "content": "Hello，很高兴认识你！",
    "status": "READ",
    "createTime": "2024-01-16T10:30:00",
    "readTime": "2024-01-16T10:32:00",
    "isRead": true,
    "isPinned": false,
    "replyToId": null,
    "messageCategory": "PRIVATE"
  }
}
```

**特性说明**:
- 🔒 **权限控制**: 仅消息参与者可查看
- ⚡ **5分钟缓存**: 消息详情缓存，提升查询速度

---

### 2. 分页查询消息
**接口路径**: `POST /api/v1/message/query`  
**接口描述**: 根据条件分页查询消息列表

#### 请求参数
```json
{
  "senderId": 12345,                     // 可选，发送者ID
  "receiverId": 67890,                   // 可选，接收者ID
  "messageType": "TEXT",                 // 可选，消息类型
  "status": "SENT",                      // 可选，消息状态：SENT/READ/deleted
  "isPinned": false,                     // 可选，是否置顶
  "replyToId": null,                     // 可选，回复的消息ID
  "keyword": "关键词",                    // 可选，搜索关键词
  "startTime": "2024-01-15 00:00:00",    // 可选，开始时间
  "endTime": "2024-01-16 23:59:59",      // 可选，结束时间
  "orderBy": "create_time",              // 可选，排序字段
  "orderDirection": "DESC",              // 可选，排序方向：ASC/DESC
  "currentPage": 1,                      // 可选，当前页码，默认1
  "pageSize": 20                         // 可选，页面大小，默认20
}
```

#### 响应示例
```json
{
  "success": true,
  "datas": [
    {
      "id": 98765,
      "senderId": 12345,
      "receiverId": 67890,
      "messageType": "TEXT",
      "content": "Hello，很高兴认识你！",
      "status": "READ",
      "createTime": "2024-01-16T10:30:00",
      "isRead": true
    }
  ],
  "total": 156,
  "currentPage": 1,
  "pageSize": 20,
  "totalPage": 8
}
```

**特性说明**:
- ⚡ **10分钟缓存**: 查询结果缓存
- 🔍 **多条件查询**: 支持多字段组合查询
- 📄 **分页优化**: 高效的分页查询机制

---

### 3. 获取聊天记录
**接口路径**: `GET /api/v1/message/chat`  
**接口描述**: 获取两个用户之间的聊天记录

#### 请求参数
- **userId1** (query): 用户1 ID，必填
- **userId2** (query): 用户2 ID，必填
- **currentPage** (query): 当前页码，可选，默认1
- **pageSize** (query): 页面大小，可选，默认20

#### 响应示例
```json
{
  "success": true,
  "datas": [
    {
      "id": 98766,
      "senderId": 67890,
      "receiverId": 12345,
      "messageType": "TEXT",
      "content": "谢谢你的消息！",
      "status": "READ",
      "createTime": "2024-01-16T10:35:00",
      "replyToId": 98765
    },
    {
      "id": 98765,
      "senderId": 12345,
      "receiverId": 67890,
      "messageType": "TEXT",
      "content": "Hello，很高兴认识你！",
      "status": "read",
      "createTime": "2024-01-16T10:30:00"
    }
  ],
  "total": 25,
  "currentPage": 1,
  "pageSize": 20,
  "totalPage": 2
}
```

**特性说明**:
- ⚡ **15分钟缓存**: 聊天记录缓存
- 🕒 **时间排序**: 按时间倒序排列，最新消息在前
- 🔒 **隐私保护**: 仅参与聊天的用户可查看

---

### 4. 获取用户留言板
**接口路径**: `GET /api/v1/message/wall/{userId}`  
**接口描述**: 获取用户个人页面的留言（包含置顶消息）

#### 请求参数
- **userId** (path): 用户ID，必填
- **currentPage** (query): 当前页码，可选，默认1
- **pageSize** (query): 页面大小，可选，默认20

#### 响应示例
```json
{
  "success": true,
  "datas": [
    {
      "id": 98770,
      "senderId": 11111,
      "receiverId": 12345,
      "messageType": "TEXT",
      "content": "恭喜获得认证！",
      "status": "read",
      "createTime": "2024-01-16T09:00:00",
      "isPinned": true,
      "messageCategory": "WALL"
    },
    {
      "id": 98769,
      "senderId": 22222,
      "receiverId": 12345,
      "messageType": "TEXT", 
      "content": "你的文章写得很棒！",
      "status": "read",
      "createTime": "2024-01-16T08:30:00",
      "isPinned": false,
      "messageCategory": "WALL"
    }
  ],
  "total": 45,
  "currentPage": 1,
  "pageSize": 20,
  "totalPage": 3
}
```

**特性说明**:
- 📌 **置顶优先**: 置顶消息优先显示
- ⚡ **10分钟缓存**: 留言板消息缓存
- 🌍 **公开访问**: 任何用户都可查看他人留言板

---

### 5. 获取消息回复
**接口路径**: `GET /api/v1/message/replies/{messageId}`  
**接口描述**: 获取指定消息的所有回复

#### 请求参数
- **messageId** (path): 原消息ID，必填
- **currentPage** (query): 当前页码，可选，默认1
- **pageSize** (query): 页面大小，可选，默认20

#### 响应示例
```json
{
  "success": true,
  "datas": [
    {
      "id": 98766,
      "senderId": 67890,
      "receiverId": 12345,
      "messageType": "TEXT",
      "content": "谢谢你的消息！",
      "status": "read",
      "createTime": "2024-01-16T10:35:00",
      "replyToId": 98765
    },
    {
      "id": 98767,
      "senderId": 11111,
      "receiverId": 12345,
      "messageType": "TEXT",
      "content": "我也想参与讨论",
      "status": "SENT",
      "createTime": "2024-01-16T10:40:00",
      "replyToId": 98765
    }
  ],
  "total": 5,
  "currentPage": 1,
  "pageSize": 20,
  "totalPage": 1
}
```

**特性说明**:
- ⚡ **15分钟缓存**: 回复列表缓存
- 🔗 **回复链**: 清晰的回复关系链
- 🕒 **时间排序**: 按回复时间正序排列

---

### 6. 搜索消息
**接口路径**: `GET /api/v1/message/search`  
**接口描述**: 在用户消息中搜索关键词

#### 请求参数
- **userId** (query): 用户ID，必填
- **keyword** (query): 搜索关键词，必填
- **currentPage** (query): 当前页码，可选，默认1
- **pageSize** (query): 页面大小，可选，默认20

#### 响应示例
```json
{
  "success": true,
  "datas": [
    {
      "id": 98765,
      "senderId": 12345,
      "receiverId": 67890,
      "messageType": "TEXT",
      "content": "Hello，很高兴<em>认识</em>你！",     // 关键词高亮
      "status": "read",
      "createTime": "2024-01-16T10:30:00",
      "isRead": true
    }
  ],
  "total": 8,
  "currentPage": 1,
  "pageSize": 20,
  "totalPage": 1
}
```

**特性说明**:
- 🔍 **全文搜索**: 支持消息内容全文检索
- ⚡ **30分钟缓存**: 搜索结果缓存
- 🎯 **关键词高亮**: 搜索关键词自动高亮显示

---

## 会话管理 API

### 1. 获取用户会话列表
**接口路径**: `GET /api/v1/message/sessions/{userId}`  
**接口描述**: 获取用户的所有聊天会话

#### 请求参数
- **userId** (path): 用户ID，必填
- **currentPage** (query): 当前页码，可选，默认1
- **pageSize** (query): 页面大小，可选，默认20

#### 响应示例
```json
{
  "success": true,
  "datas": [
    {
      "id": 1001,
      "userId": 12345,
      "otherUserId": 67890,
      "otherUserName": "张三",
      "otherUserAvatar": "https://example.com/avatar.jpg",
      "lastMessageId": 98766,
      "lastMessageContent": "谢谢你的消息！",
      "lastMessageType": "TEXT",
      "lastMessageTime": "2024-01-16T10:35:00",
      "unreadCount": 2,
      "isDeleted": false,
      "createTime": "2024-01-15T10:00:00",
      "updateTime": "2024-01-16T10:35:00"
    },
    {
      "id": 1002,
      "userId": 12345,
      "otherUserId": 11111,
      "otherUserName": "李四",
      "otherUserAvatar": "https://example.com/avatar2.jpg",
      "lastMessageId": 98768,
      "lastMessageContent": "明天见！",
      "lastMessageType": "TEXT",
      "lastMessageTime": "2024-01-16T09:20:00",
      "unreadCount": 0,
      "isDeleted": false
    }
  ],
  "total": 15,
  "currentPage": 1,
  "pageSize": 20,
  "totalPage": 1
}
```

**特性说明**:
- ⚡ **20分钟缓存**: 会话列表缓存
- 🕒 **智能排序**: 按最新消息时间排序
- 📊 **未读统计**: 实时显示每个会话的未读消息数
- 👤 **用户信息**: 自动获取对方用户基本信息

---

## 消息状态 API

### 1. 标记消息已读
**接口路径**: `PUT /api/v1/message/{messageId}/read`  
**接口描述**: 将指定消息标记为已读状态

#### 请求参数
- **messageId** (path): 消息ID，必填
- **userId** (query): 用户ID，必填

#### 响应示例
```json
{
  "success": true,
  "responseCode": "SUCCESS",
  "responseMessage": "消息已标记为已读"
}
```

**特性说明**:
- ⚡ **实时更新**: 立即更新消息读取状态
- 🔄 **缓存同步**: 自动清理相关缓存

---

### 2. 批量标记已读
**接口路径**: `PUT /api/v1/message/batch-read`  
**接口描述**: 批量将消息标记为已读状态

#### 请求参数
```json
{
  "messageIds": [98765, 98766, 98767],   // 必填，消息ID列表
  "userId": 12345                        // 作为query参数
}
```

#### 响应示例
```json
{
  "success": true,
  "responseCode": "SUCCESS",
  "responseMessage": "批量标记已读成功"
}
```

**特性说明**:
- ⚡ **批量处理**: 高效处理多条消息
- 🔄 **事务安全**: 确保批量操作的原子性

---

### 3. 置顶/取消置顶消息
**接口路径**: `PUT /api/v1/message/{messageId}/pin`  
**接口描述**: 设置消息的置顶状态，用于留言板功能

#### 请求参数
- **messageId** (path): 消息ID，必填
- **userId** (query): 用户ID，必填
- **isPinned** (query): 是否置顶，必填

#### 响应示例
```json
{
  "success": true,
  "responseCode": "SUCCESS",
  "responseMessage": "消息置顶状态已更新"
}
```

**特性说明**:
- 📌 **置顶管理**: 灵活的消息置顶功能
- 🔒 **权限控制**: 仅消息接收者可设置置顶
- 🔄 **缓存刷新**: 自动刷新留言板缓存

---

### 4. 删除消息
**接口路径**: `DELETE /api/v1/message/{messageId}`  
**接口描述**: 逻辑删除指定消息

#### 请求参数
- **messageId** (path): 消息ID，必填
- **userId** (query): 用户ID，必填

#### 响应示例
```json
{
  "success": true,
  "responseCode": "SUCCESS",
  "responseMessage": "消息删除成功"
}
```

**特性说明**:
- 🗑️ **逻辑删除**: 保留数据，仅更新状态
- 🔒 **权限控制**: 仅消息参与者可删除
- 🔄 **缓存清理**: 自动清理相关缓存

---

## 统计信息 API

### 1. 获取未读消息数
**接口路径**: `GET /api/v1/message/unread-count/{userId}`  
**接口描述**: 获取用户的未读消息总数

#### 请求参数
- **userId** (path): 用户ID，必填

#### 响应示例
```json
{
  "success": true,
  "data": 15
}
```

**特性说明**:
- ⚡ **5分钟缓存**: 未读数统计缓存
- 📊 **实时统计**: 准确的未读消息计数

---

### 2. 获取与特定用户的未读消息数
**接口路径**: `GET /api/v1/message/unread-count/{userId}/with/{otherUserId}`  
**接口描述**: 获取与某个用户的未读消息数

#### 请求参数
- **userId** (path): 用户ID，必填
- **otherUserId** (path): 对方用户ID，必填

#### 响应示例
```json
{
  "success": true,
  "data": 3
}
```

**特性说明**:
- ⚡ **5分钟缓存**: 用户间未读数缓存
- 🎯 **精确统计**: 特定会话的未读消息数

---

### 3. 获取消息统计信息
**接口路径**: `GET /api/v1/message/statistics/{userId}`  
**接口描述**: 获取用户的消息统计数据

#### 请求参数
- **userId** (path): 用户ID，必填

#### 响应示例
```json
{
  "success": true,
  "data": {
    "totalSent": 1250,                   // 总发送消息数
    "totalReceived": 890,                // 总接收消息数
    "unreadCount": 15,                   // 未读消息数
    "activeSessionCount": 8,             // 活跃会话数
    "todaySent": 25,                     // 今日发送数
    "todayReceived": 18,                 // 今日接收数
    "weekSent": 156,                     // 本周发送数
    "weekReceived": 123,                 // 本周接收数
    "monthSent": 678,                    // 本月发送数
    "monthReceived": 545,                // 本月接收数
    "wallMessageCount": 45,              // 留言板消息数
    "pinnedMessageCount": 3,             // 置顶消息数
    "lastMessageTime": "2024-01-16T10:35:00"  // 最后消息时间
  }
}
```

**特性说明**:
- ⚡ **30分钟缓存**: 统计数据缓存
- 📊 **全面统计**: 多维度的消息数据分析
- 🕒 **时间维度**: 按日、周、月的统计数据

---

## 🚀 缓存性能特性

### 缓存策略
- **消息详情缓存**: 5分钟，提升单条消息查询速度
- **消息列表缓存**: 10分钟，优化列表查询性能
- **聊天记录缓存**: 15分钟，加速会话消息加载
- **会话列表缓存**: 20分钟，快速显示用户会话
- **搜索结果缓存**: 30分钟，优化搜索响应时间
- **统计数据缓存**: 30分钟，减少统计查询压力
- **未读数缓存**: 5分钟，实时更新未读消息数

### 缓存优化
- **分布式缓存**: 基于JetCache的分布式缓存架构
- **智能失效**: 消息状态变更时自动清理相关缓存
- **缓存预热**: 系统自动预热热门会话数据
- **性能监控**: 实时监控缓存命中率和响应时间

### 性能指标
- **消息查询响应时间**: < 50ms (缓存命中)
- **缓存命中率**: > 90%
- **并发支持**: 5,000+ 并发消息处理
- **数据同步**: 缓存数据与数据库实时同步

---

## 错误码说明

| 错误码 | 说明 |
|--------|------|
| INVALID_PARAM | 请求参数无效 |
| SEND_FAILED | 消息发送失败 |
| MESSAGE_NOT_FOUND | 消息不存在 |
| ACCESS_DENIED | 无权限访问 |
| MARK_FAILED | 标记已读失败 |
| BATCH_MARK_FAILED | 批量标记已读失败 |
| DELETE_FAILED | 删除消息失败 |
| PIN_FAILED | 设置置顶失败 |
| USER_NOT_FOUND | 用户不存在 |
| RECEIVER_NOT_FOUND | 接收者不存在 |
| INVALID_MESSAGE_TYPE | 无效的消息类型 |
| MESSAGE_TOO_LONG | 消息内容过长 |
| REPLY_TARGET_NOT_FOUND | 回复目标消息不存在 |
| SEND_TO_SELF_ERROR | 不能给自己发送消息 |
| SYSTEM_ERROR | 系统错误 |

---

## 🔄 API设计原则

### 统一响应格式
- **查询操作**: 直接返回 `PageResponse<Data>` 格式
- **创建操作**: 返回 `Result<Data>` 格式（包含创建的数据）
- **状态更新操作**: 返回 `Result<Void>` 格式（仅状态码）

### 分页参数规范
- **currentPage**: 当前页码，从1开始
- **pageSize**: 每页大小，默认20，最大100
- **total**: 总记录数
- **totalPage**: 总页数

### 缓存键命名规范
- **格式**: `message:功能:参数`
- **示例**: `message:detail:id:98765`, `message:chat:user1:12345:user2:67890`
- **过期策略**: 根据数据更新频率设置合理的过期时间

### 消息类型规范
- **TEXT**: 文本消息
- **IMAGE**: 图片消息
- **FILE**: 文件消息
- **AUDIO**: 音频消息
- **VIDEO**: 视频消息

### 消息状态规范
- **SENT**: 已发送
- **READ**: 已读
- **DELETED**: 已删除

### 消息分类规范
- **PRIVATE**: 私信消息
- **WALL**: 留言板消息
- **SYSTEM**: 系统消息

---

**最后更新**: 2024-01-16  
**文档版本**: v2.0.0
