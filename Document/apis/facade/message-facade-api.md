# 消息门面服务 Facade API 文档

## 概述

消息门面服务接口(MessageFacadeService)是消息模块的核心业务接口，提供统一的Result<T>包装和完整的消息功能。基于message-simple.sql的无连表设计，支持私信、留言板、消息回复等功能。

- **模块**: 消息门面服务 (Message Facade Service)
- **接口**: MessageFacadeService
- **包路径**: `com.gig.collide.api.message.MessageFacadeService`
- **版本**: 2.0.0
- **总方法数**: 25个

## 功能特性

- ✅ **统一返回值**: 所有方法使用Result<T>包装返回结果
- ✅ **消息发送**: 私信、回复、留言板消息
- ✅ **消息查询**: 聊天记录、条件查询、关键词搜索
- ✅ **消息管理**: 更新、删除、置顶、标记已读
- ✅ **批量操作**: 批量已读、批量删除、会话全部已读
- ✅ **统计功能**: 未读数统计、发送/接收统计
- ✅ **会话管理**: 最近聊天用户、最新消息
- ✅ **系统管理**: 过期消息清理、健康检查

---

## 1. 消息发送

### 1.1 sendMessage

**方法签名**:
```java
Result<MessageResponse> sendMessage(MessageCreateRequest request)
```

**功能描述**: 发送消息，支持文本、图片、文件等多种消息类型，包含用户验证、权限检查、消息过滤等业务逻辑

**参数说明**:
- `request`: 消息创建请求对象
  - `senderId`: 发送者ID (必填)
  - `receiverId`: 接收者ID (必填)
  - `messageType`: 消息类型 (必填): TEXT/IMAGE/FILE/VOICE
  - `content`: 消息内容 (必填)
  - `attachments`: 附件信息 (可选)

**返回值**: `Result<MessageResponse>` - 包装的消息响应对象

**业务逻辑**:
1. 验证发送者和接收者是否存在
2. 检查发送权限（是否被拉黑、是否允许陌生人消息）
3. 过滤敏感内容
4. 保存消息到数据库
5. 更新相关会话状态
6. 发送实时通知（可选）

**成功响应示例**:
```json
{
  "code": 200,
  "message": "发送成功",
  "data": {
    "messageId": 1001,
    "senderId": 1001,
    "receiverId": 1002,
    "content": "Hello!",
    "messageType": "TEXT",
    "status": "SENT",
    "createTime": "2024-01-30 10:30:00"
  }
}
```

**错误响应示例**:
```json
{
  "code": 403,
  "message": "无发送权限",
  "errorCode": "MESSAGE_SEND_PERMISSION_DENIED"
}
```

---

### 1.2 replyMessage

**方法签名**:
```java
Result<MessageResponse> replyMessage(MessageCreateRequest request)
```

**功能描述**: 回复指定的消息，建立回复关系

**参数说明**:
- `request`: 回复消息创建请求对象
  - `replyToId`: 被回复的消息ID (必填)
  - 其他字段同sendMessage

**返回值**: `Result<MessageResponse>` - 包装的回复消息响应对象

**业务逻辑**:
1. 验证被回复的消息是否存在
2. 检查回复权限
3. 创建回复消息并建立关联关系
4. 通知原消息发送者

**使用场景**:
- 用户回复私信
- 留言板回复功能
- 群组消息回复

---

### 1.3 sendWallMessage

**方法签名**:
```java
Result<MessageResponse> sendWallMessage(MessageCreateRequest request)
```

**功能描述**: 发送到用户留言板的公开消息

**参数说明**:
- `request`: 留言消息创建请求对象
  - `messageType`: 固定为 "WALL"
  - 其他字段同sendMessage

**返回值**: `Result<MessageResponse>` - 包装的留言消息响应对象

**业务逻辑**:
1. 验证留言权限
2. 设置消息为公开可见
3. 保存到留言板
4. 通知留言板主人

**使用场景**:
- 用户个人主页留言
- 公开留言功能
- 访客留言

---

## 2. 消息查询

### 2.1 getMessageById

**方法签名**:
```java
Result<MessageResponse> getMessageById(Long messageId, Long userId)
```

**功能描述**: 根据ID获取消息详情

**参数说明**:
- `messageId`: 消息ID (必填)
- `userId`: 查看者用户ID，用于权限验证 (必填)

**返回值**: `Result<MessageResponse>` - 包装的消息详情

**业务逻辑**:
1. 验证消息是否存在
2. 检查查看权限（是否为消息参与者）
3. 标记消息为已读（如果是接收者查看）
4. 返回消息详情

**使用场景**:
- 查看具体消息详情
- 消息转发前预览
- 消息举报功能

---

### 2.2 queryMessages

**方法签名**:
```java
Result<PageResponse<MessageResponse>> queryMessages(MessageQueryRequest request)
```

**功能描述**: 分页查询消息，支持多维度条件查询

**参数说明**:
- `request`: 查询请求对象
  - `senderId`: 发送者ID (可选)
  - `receiverId`: 接收者ID (可选)
  - `messageType`: 消息类型 (可选)
  - `status`: 消息状态 (可选)
  - `keyword`: 搜索关键词 (可选)
  - `startTime`: 开始时间 (可选)
  - `endTime`: 结束时间 (可选)
  - `currentPage`: 当前页码 (默认1)
  - `pageSize`: 页面大小 (默认20)

**返回值**: `Result<PageResponse<MessageResponse>>` - 包装的分页查询结果

**业务逻辑**:
1. 构建动态查询条件
2. 执行分页查询
3. 过滤用户无权查看的消息
4. 组装分页响应

**使用场景**:
- 消息列表展示
- 消息搜索功能
- 管理后台消息管理

---

### 2.3 getChatHistory

**方法签名**:
```java
Result<PageResponse<MessageResponse>> getChatHistory(Long userId1, Long userId2, String status, Integer currentPage, Integer pageSize)
```

**功能描述**: 查询两用户间的聊天记录

**参数说明**:
- `userId1`: 用户1ID (必填)
- `userId2`: 用户2ID (必填)
- `status`: 消息状态 (可选)
- `currentPage`: 当前页码 (默认1)
- `pageSize`: 页面大小 (默认20)

**返回值**: `Result<PageResponse<MessageResponse>>` - 包装的聊天记录分页

**业务逻辑**:
1. 验证两个用户是否存在
2. 查询两用户间的消息记录
3. 按时间倒序排列
4. 标记查看者的未读消息为已读

**使用场景**:
- 聊天窗口消息加载
- 聊天记录回看
- 消息同步

---

### 2.4 getWallMessages

**方法签名**:
```java
Result<PageResponse<MessageResponse>> getWallMessages(Long receiverId, String status, Integer currentPage, Integer pageSize)
```

**功能描述**: 查询用户留言板消息

**参数说明**:
- `receiverId`: 接收者ID (必填)
- `status`: 消息状态 (可选)
- `currentPage`: 当前页码 (默认1)
- `pageSize`: 页面大小 (默认20)

**返回值**: `Result<PageResponse<MessageResponse>>` - 包装的留言板消息分页

**使用场景**:
- 个人主页留言板展示
- 留言管理
- 留言板消息审核

---

### 2.5 getMessageReplies

**方法签名**:
```java
Result<PageResponse<MessageResponse>> getMessageReplies(Long replyToId, String status, Integer currentPage, Integer pageSize)
```

**功能描述**: 查询消息回复列表

**参数说明**:
- `replyToId`: 原消息ID (必填)
- `status`: 消息状态 (可选)
- `currentPage`: 当前页码 (默认1)
- `pageSize`: 页面大小 (默认20)

**返回值**: `Result<PageResponse<MessageResponse>>` - 包装的回复列表分页

**使用场景**:
- 消息回复展示
- 评论型消息功能
- 消息串联查看

---

### 2.6 searchMessages

**方法签名**:
```java
Result<PageResponse<MessageResponse>> searchMessages(Long userId, String keyword, String status, Integer currentPage, Integer pageSize)
```

**功能描述**: 搜索用户消息，支持消息内容关键词搜索

**参数说明**:
- `userId`: 用户ID (必填)
- `keyword`: 搜索关键词 (必填)
- `status`: 消息状态 (可选)
- `currentPage`: 当前页码 (默认1)
- `pageSize`: 页面大小 (默认20)

**返回值**: `Result<PageResponse<MessageResponse>>` - 包装的搜索结果分页

**业务逻辑**:
1. 使用全文搜索索引
2. 高亮关键词
3. 按相关度和时间排序
4. 过滤已删除消息

**使用场景**:
- 消息内容搜索
- 查找历史对话
- 关键信息检索

---

## 3. 消息管理

### 3.1 updateMessage

**方法签名**:
```java
Result<MessageResponse> updateMessage(MessageUpdateRequest request)
```

**功能描述**: 更新消息，支持更新消息内容（仅限发送者）

**参数说明**:
- `request`: 更新请求对象
  - `messageId`: 消息ID (必填)
  - `operatorId`: 操作者ID (必填)
  - `content`: 新内容 (可选)
  - `status`: 新状态 (可选)

**返回值**: `Result<MessageResponse>` - 包装的更新后消息

**业务逻辑**:
1. 验证操作权限（仅发送者可更新）
2. 检查消息是否允许编辑（时间限制）
3. 记录编辑历史
4. 通知接收者消息已编辑

**使用场景**:
- 消息内容纠错
- 撤回并重新编辑
- 消息状态管理

---

### 3.2 deleteMessage

**方法签名**:
```java
Result<Void> deleteMessage(Long messageId, Long userId)
```

**功能描述**: 删除消息，逻辑删除，支持发送者和接收者删除

**参数说明**:
- `messageId`: 消息ID (必填)
- `userId`: 操作用户ID (必填)

**返回值**: `Result<Void>` - 删除结果

**业务逻辑**:
1. 验证删除权限（发送者或接收者）
2. 执行逻辑删除
3. 更新相关统计数据
4. 记录删除日志

**使用场景**:
- 消息撤回
- 不当消息删除
- 清理聊天记录

---

### 3.3 markAsRead

**方法签名**:
```java
Result<Void> markAsRead(Long messageId, Long userId)
```

**功能描述**: 标记消息为已读

**参数说明**:
- `messageId`: 消息ID (必填)
- `userId`: 用户ID (必填)

**返回值**: `Result<Void>` - 操作结果

**业务逻辑**:
1. 验证用户是否为消息接收者
2. 更新消息已读状态
3. 记录已读时间
4. 更新会话未读计数
5. 发送已读回执（如果开启）

**使用场景**:
- 消息查看时自动标记
- 手动标记已读
- 批量已读操作

---

### 3.4 updatePinnedStatus

**方法签名**:
```java
Result<Void> updatePinnedStatus(Long messageId, Boolean isPinned, Long userId)
```

**功能描述**: 更新消息置顶状态

**参数说明**:
- `messageId`: 消息ID (必填)
- `isPinned`: 是否置顶 (必填)
- `userId`: 操作用户ID (必填)

**返回值**: `Result<Void>` - 操作结果

**业务逻辑**:
1. 验证置顶权限
2. 更新置顶状态
3. 调整置顶消息排序
4. 记录操作日志

**使用场景**:
- 重要消息置顶
- 公告消息管理
- 消息优先级设置

---

## 4. 批量操作

### 4.1 batchMarkAsRead

**方法签名**:
```java
Result<Void> batchMarkAsRead(List<Long> messageIds, Long userId)
```

**功能描述**: 批量标记消息为已读

**参数说明**:
- `messageIds`: 消息ID列表 (必填)
- `userId`: 用户ID (必填)

**返回值**: `Result<Void>` - 批量操作结果

**业务逻辑**:
1. 验证所有消息的权限
2. 批量更新已读状态
3. 统计更新会话未读计数
4. 发送批量已读回执

**使用场景**:
- 全部已读功能
- 清空未读标记
- 批量消息管理

---

### 4.2 batchDeleteMessages

**方法签名**:
```java
Result<Void> batchDeleteMessages(List<Long> messageIds, Long userId)
```

**功能描述**: 批量删除消息

**参数说明**:
- `messageIds`: 消息ID列表 (必填)
- `userId`: 操作用户ID (必填)

**返回值**: `Result<Void>` - 批量删除结果

**业务逻辑**:
1. 验证每条消息的删除权限
2. 批量执行逻辑删除
3. 更新相关统计数据
4. 记录批量删除日志

**使用场景**:
- 清空聊天记录
- 批量消息删除
- 消息清理功能

---

### 4.3 markSessionAsRead

**方法签名**:
```java
Result<Void> markSessionAsRead(Long receiverId, Long senderId)
```

**功能描述**: 标记会话中所有消息为已读

**参数说明**:
- `receiverId`: 接收者ID (必填)
- `senderId`: 发送者ID (必填)

**返回值**: `Result<Void>` - 操作结果

**业务逻辑**:
1. 查找两用户间的所有未读消息
2. 批量标记为已读
3. 清零会话未读计数
4. 发送已读通知

**使用场景**:
- 进入聊天窗口时
- 会话已读功能
- 消息同步

---

## 5. 统计功能

### 5.1 getUnreadMessageCount

**方法签名**:
```java
Result<Long> getUnreadMessageCount(Long userId)
```

**功能描述**: 统计用户未读消息数

**参数说明**:
- `userId`: 用户ID (必填)

**返回值**: `Result<Long>` - 未读消息总数

**业务逻辑**:
1. 查询用户所有未读消息
2. 按消息类型分类统计
3. 缓存统计结果
4. 实时更新计数

**使用场景**:
- 消息角标显示
- 首页未读提示
- 消息中心统计

---

### 5.2 getUnreadCountWithUser

**方法签名**:
```java
Result<Long> getUnreadCountWithUser(Long receiverId, Long senderId)
```

**功能描述**: 统计与某用户的未读消息数

**参数说明**:
- `receiverId`: 接收者ID (必填)
- `senderId`: 发送者ID (必填)

**返回值**: `Result<Long>` - 与特定用户的未读消息数

**使用场景**:
- 聊天列表角标
- 特定会话未读提示
- 消息提醒功能

---

### 5.3 getSentMessageCount

**方法签名**:
```java
Result<Long> getSentMessageCount(Long userId, LocalDateTime startTime, LocalDateTime endTime)
```

**功能描述**: 统计用户发送的消息数

**参数说明**:
- `userId`: 用户ID (必填)
- `startTime`: 开始时间 (可选)
- `endTime`: 结束时间 (可选)

**返回值**: `Result<Long>` - 发送消息数量

**使用场景**:
- 用户活跃度统计
- 消息使用报告
- 限流控制参考

---

### 5.4 getReceivedMessageCount

**方法签名**:
```java
Result<Long> getReceivedMessageCount(Long userId, LocalDateTime startTime, LocalDateTime endTime)
```

**功能描述**: 统计用户接收的消息数

**参数说明**:
- `userId`: 用户ID (必填)
- `startTime`: 开始时间 (可选)
- `endTime`: 结束时间 (可选)

**返回值**: `Result<Long>` - 接收消息数量

**使用场景**:
- 用户受欢迎度统计
- 消息接收报告
- 用户行为分析

---

## 6. 会话管理

### 6.1 getRecentChatUsers

**方法签名**:
```java
Result<List<Long>> getRecentChatUsers(Long userId, Integer limit)
```

**功能描述**: 获取用户最近的聊天用户列表

**参数说明**:
- `userId`: 用户ID (必填)
- `limit`: 限制数量 (默认20)

**返回值**: `Result<List<Long>>` - 最近聊天用户ID列表

**业务逻辑**:
1. 按最后消息时间排序
2. 过滤已删除用户
3. 缓存结果提高性能
4. 支持分页加载

**使用场景**:
- 聊天列表展示
- 快速选择聊天对象
- 最近联系人功能

---

### 6.2 getLatestMessage

**方法签名**:
```java
Result<MessageResponse> getLatestMessage(Long userId1, Long userId2)
```

**功能描述**: 获取两用户间的最新消息

**参数说明**:
- `userId1`: 用户1ID (必填)
- `userId2`: 用户2ID (必填)

**返回值**: `Result<MessageResponse>` - 最新消息详情

**使用场景**:
- 聊天列表最后消息显示
- 会话预览
- 消息同步检查

---

## 7. 系统管理

### 7.1 cleanupExpiredMessages

**方法签名**:
```java
Result<Integer> cleanupExpiredMessages(LocalDateTime beforeTime)
```

**功能描述**: 清理过期删除消息，系统定时任务调用，物理删除过期的已删除消息

**参数说明**:
- `beforeTime`: 截止时间 (必填)

**返回值**: `Result<Integer>` - 清理的消息数量

**业务逻辑**:
1. 查找指定时间前的已删除消息
2. 物理删除消息及相关数据
3. 清理相关索引和缓存
4. 记录清理日志

**使用场景**:
- 定时数据清理
- 存储空间优化
- 数据库维护

---

### 7.2 healthCheck

**方法签名**:
```java
Result<String> healthCheck()
```

**功能描述**: 消息系统健康检查

**返回值**: `Result<String>` - 系统状态描述

**业务逻辑**:
1. 检查数据库连接
2. 验证缓存服务
3. 测试消息队列
4. 统计系统负载

**使用场景**:
- 系统监控
- 健康状态检查
- 故障诊断

---

## 错误码说明

### 业务错误码

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

### 系统错误码

| 错误码 | 错误信息 | 说明 |
|-------|---------|------|
| 500 | 系统内部错误 | 服务器异常 |
| 503 | 服务不可用 | 系统维护中 |
| 429 | 请求过于频繁 | 触发限流规则 |

---

## 最佳实践

### 1. 异常处理
```java
try {
    Result<MessageResponse> result = messageFacadeService.sendMessage(request);
    if (result.isSuccess()) {
        // 处理成功逻辑
        MessageResponse message = result.getData();
    } else {
        // 处理失败逻辑
        log.error("发送消息失败: {}", result.getMessage());
    }
} catch (Exception e) {
    log.error("消息服务异常", e);
}
```

### 2. 权限检查
```java
// 发送消息前检查权限
Result<Boolean> canSend = messageSettingFacadeService.canSendMessage(senderId, receiverId);
if (canSend.isSuccess() && canSend.getData()) {
    // 执行发送逻辑
}
```

### 3. 批量操作
```java
// 批量操作时注意数据量控制
List<Long> messageIds = getMessageIds();
if (messageIds.size() > MAX_BATCH_SIZE) {
    // 分批处理
    Lists.partition(messageIds, MAX_BATCH_SIZE).forEach(batch -> {
        messageFacadeService.batchMarkAsRead(batch, userId);
    });
}
```

---

**文档版本**: v2.0.0  
**最后更新**: 2024-01-30  
**维护者**: GIG Team