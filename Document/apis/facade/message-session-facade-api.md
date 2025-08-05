# 消息会话门面服务 Facade API 文档

## 概述

消息会话门面服务接口(MessageSessionFacadeService)是会话模块的核心业务接口，提供统一的Result<T>包装和完整的会话管理功能。基于message-simple.sql的t_message_session表设计，管理用户间的会话状态、未读计数和会话列表。

- **模块**: 消息会话门面服务 (Message Session Facade Service)
- **接口**: MessageSessionFacadeService
- **包路径**: `com.gig.collide.api.message.MessageSessionFacadeService`
- **版本**: 2.0.0
- **总方法数**: 17个

## 功能特性

- ✅ **统一返回值**: 所有方法使用Result<T>包装返回结果
- ✅ **会话管理**: 创建/更新会话、查询会话、归档管理
- ✅ **会话查询**: 用户会话列表、活跃会话、未读会话
- ✅ **统计功能**: 未读会话数、总会话数、总未读数
- ✅ **状态管理**: 最后消息更新、未读计数管理
- ✅ **批量操作**: 批量归档、批量取消归档
- ✅ **系统功能**: 会话清理、索引重建、健康检查

---

## 1. 会话创建和管理

### 1.1 createOrUpdateSession

**方法签名**:
```java
Result<MessageSessionResponse> createOrUpdateSession(MessageSessionCreateRequest request)
```

**功能描述**: 创建或更新会话，如果会话不存在则创建，存在则更新

**参数说明**:
- `request`: 会话创建请求对象
  - `userId`: 用户ID (必填)
  - `otherUserId`: 对方用户ID (必填)
  - `sessionType`: 会话类型 (可选): PRIVATE/GROUP
  - `lastMessageId`: 最后消息ID (可选)
  - `lastMessageTime`: 最后消息时间 (可选)

**返回值**: `Result<MessageSessionResponse>` - 包装的会话响应对象

**业务逻辑**:
1. 验证用户是否存在
2. 检查是否已存在会话
3. 如不存在则创建新会话
4. 如存在则更新会话信息
5. 初始化会话统计数据

**成功响应示例**:
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

**使用场景**:
- 用户首次发送消息时自动创建会话
- 消息到达时更新会话状态
- 系统会话同步

---

### 1.2 getSessionByUserIds

**方法签名**:
```java
Result<MessageSessionResponse> getSessionByUserIds(Long userId, Long otherUserId)
```

**功能描述**: 根据用户ID获取会话详情

**参数说明**:
- `userId`: 用户ID (必填)
- `otherUserId`: 对方用户ID (必填)

**返回值**: `Result<MessageSessionResponse>` - 包装的会话详情

**业务逻辑**:
1. 根据两个用户ID查找会话
2. 验证用户查看权限
3. 返回会话详细信息
4. 包含最后消息内容

**使用场景**:
- 进入聊天界面前获取会话信息
- 检查会话是否存在
- 会话状态查询

---

### 1.3 updateSession

**方法签名**:
```java
Result<MessageSessionResponse> updateSession(MessageSessionUpdateRequest request)
```

**功能描述**: 更新会话信息

**参数说明**:
- `request`: 更新请求对象
  - `sessionId`: 会话ID (必填)
  - `updateType`: 更新类型 (必填)
  - `lastMessageId`: 最后消息ID (可选)
  - `lastMessageTime`: 最后消息时间 (可选)
  - `unreadCount`: 未读数量 (可选)
  - `isArchived`: 是否归档 (可选)

**返回值**: `Result<MessageSessionResponse>` - 包装的更新后会话信息

**业务逻辑**:
1. 验证会话是否存在
2. 检查更新权限
3. 根据更新类型执行相应操作
4. 更新时间戳

**使用场景**:
- 消息到达时更新会话
- 修改会话设置
- 会话状态变更

---

### 1.4 updateArchiveStatus

**方法签名**:
```java
Result<Void> updateArchiveStatus(Long sessionId, Boolean isArchived, Long userId)
```

**功能描述**: 更新会话归档状态

**参数说明**:
- `sessionId`: 会话ID (必填)
- `isArchived`: 是否归档 (必填)
- `userId`: 操作用户ID (必填)

**返回值**: `Result<Void>` - 操作结果

**业务逻辑**:
1. 验证会话和用户权限
2. 更新归档状态
3. 记录操作日志
4. 更新缓存

**使用场景**:
- 会话归档功能
- 会话管理
- 清理聊天列表

---

## 2. 会话查询

### 2.1 queryUserSessions

**方法签名**:
```java
Result<PageResponse<MessageSessionResponse>> queryUserSessions(MessageSessionQueryRequest request)
```

**功能描述**: 分页查询用户会话列表，支持按归档状态、未读状态筛选

**参数说明**:
- `request`: 查询请求对象
  - `userId`: 用户ID (必填)
  - `isArchived`: 是否归档 (可选)
  - `hasUnread`: 是否有未读 (可选)
  - `sessionType`: 会话类型 (可选)
  - `startTime`: 开始时间 (可选)
  - `endTime`: 结束时间 (可选)
  - `currentPage`: 当前页码 (默认1)
  - `pageSize`: 页面大小 (默认20)

**返回值**: `Result<PageResponse<MessageSessionResponse>>` - 包装的分页查询结果

**业务逻辑**:
1. 构建动态查询条件
2. 执行分页查询
3. 填充对方用户信息
4. 按最后消息时间排序

**使用场景**:
- 聊天列表展示
- 会话搜索功能
- 管理后台会话管理

---

### 2.2 getUserSessions

**方法签名**:
```java
Result<PageResponse<MessageSessionResponse>> getUserSessions(Long userId, Boolean isArchived, Boolean hasUnread, Integer currentPage, Integer pageSize)
```

**功能描述**: 获取用户的所有会话

**参数说明**:
- `userId`: 用户ID (必填)
- `isArchived`: 是否归档 (可选)
- `hasUnread`: 是否有未读 (可选)
- `currentPage`: 当前页码 (默认1)
- `pageSize`: 页面大小 (默认20)

**返回值**: `Result<PageResponse<MessageSessionResponse>>` - 包装的会话列表分页

**业务逻辑**:
1. 查询用户所有会话
2. 根据条件过滤
3. 填充最后消息内容
4. 计算未读数量

**使用场景**:
- 主页聊天列表
- 会话管理界面
- 消息中心展示

---

### 2.3 getActiveSessions

**方法签名**:
```java
Result<PageResponse<MessageSessionResponse>> getActiveSessions(Long userId, LocalDateTime sinceTime, Integer currentPage, Integer pageSize)
```

**功能描述**: 获取用户的活跃会话，查询指定时间后有消息交互的会话

**参数说明**:
- `userId`: 用户ID (必填)
- `sinceTime`: 起始时间 (可选)
- `currentPage`: 当前页码 (默认1)
- `pageSize`: 页面大小 (默认20)

**返回值**: `Result<PageResponse<MessageSessionResponse>>` - 包装的活跃会话分页

**业务逻辑**:
1. 查询指定时间后有更新的会话
2. 标记活跃状态
3. 按活跃度排序
4. 过滤无效会话

**使用场景**:
- 活跃联系人展示
- 推荐聊天对象
- 用户行为分析

---

### 2.4 getUnreadSessions

**方法签名**:
```java
Result<PageResponse<MessageSessionResponse>> getUnreadSessions(Long userId, Integer currentPage, Integer pageSize)
```

**功能描述**: 获取用户的未读会话列表

**参数说明**:
- `userId`: 用户ID (必填)
- `currentPage`: 当前页码 (默认1)
- `pageSize`: 页面大小 (默认20)

**返回值**: `Result<PageResponse<MessageSessionResponse>>` - 包装的未读会话分页

**业务逻辑**:
1. 查询有未读消息的会话
2. 按未读数量排序
3. 显示未读消息摘要
4. 高亮未读状态

**使用场景**:
- 未读消息提醒
- 消息中心未读列表
- 优先处理列表

---

## 3. 统计功能

### 3.1 getUnreadSessionCount

**方法签名**:
```java
Result<Long> getUnreadSessionCount(Long userId)
```

**功能描述**: 统计用户的未读会话数

**参数说明**:
- `userId`: 用户ID (必填)

**返回值**: `Result<Long>` - 未读会话总数

**业务逻辑**:
1. 查询用户所有未读会话
2. 排除归档会话
3. 缓存统计结果
4. 实时更新计数

**使用场景**:
- 会话列表角标
- 首页未读提示
- 消息中心统计

---

### 3.2 getUserSessionCount

**方法签名**:
```java
Result<Long> getUserSessionCount(Long userId, Boolean isArchived)
```

**功能描述**: 统计用户的总会话数

**参数说明**:
- `userId`: 用户ID (必填)
- `isArchived`: 是否归档 (可选)

**返回值**: `Result<Long>` - 会话总数

**使用场景**:
- 用户活跃度统计
- 会话管理统计
- 数据分析报告

---

### 3.3 getTotalUnreadCount

**方法签名**:
```java
Result<Long> getTotalUnreadCount(Long userId)
```

**功能描述**: 获取用户所有会话的未读总数

**参数说明**:
- `userId`: 用户ID (必填)

**返回值**: `Result<Long>` - 所有会话的未读消息总数

**业务逻辑**:
1. 汇总所有会话的未读数
2. 排除归档会话
3. 实时计算总数
4. 缓存结果优化性能

**使用场景**:
- 应用角标显示
- 总体未读统计
- 消息汇总报告

---

## 4. 会话状态管理

### 4.1 updateLastMessage

**方法签名**:
```java
Result<Void> updateLastMessage(Long userId, Long otherUserId, Long lastMessageId, LocalDateTime lastMessageTime)
```

**功能描述**: 更新会话的最后消息信息，在新消息到达时自动调用

**参数说明**:
- `userId`: 用户ID (必填)
- `otherUserId`: 对方用户ID (必填)
- `lastMessageId`: 最后消息ID (必填)
- `lastMessageTime`: 最后消息时间 (必填)

**返回值**: `Result<Void>` - 操作结果

**业务逻辑**:
1. 查找或创建会话
2. 更新最后消息信息
3. 调整会话排序
4. 同步双方会话状态

**使用场景**:
- 消息发送后自动调用
- 会话状态同步
- 消息到达通知

---

### 4.2 incrementUnreadCount

**方法签名**:
```java
Result<Void> incrementUnreadCount(Long userId, Long otherUserId)
```

**功能描述**: 增加会话的未读计数

**参数说明**:
- `userId`: 用户ID (必填)
- `otherUserId`: 对方用户ID (必填)

**返回值**: `Result<Void>` - 操作结果

**业务逻辑**:
1. 查找用户会话
2. 增加未读计数
3. 更新会话时间戳
4. 触发未读通知

**使用场景**:
- 接收消息时自动调用
- 未读数量维护
- 实时消息推送

---

### 4.3 clearUnreadCount

**方法签名**:
```java
Result<Void> clearUnreadCount(Long userId, Long otherUserId)
```

**功能描述**: 清零会话的未读计数，用户查看消息时调用

**参数说明**:
- `userId`: 用户ID (必填)
- `otherUserId`: 对方用户ID (必填)

**返回值**: `Result<Void>` - 操作结果

**业务逻辑**:
1. 查找用户会话
2. 清零未读计数
3. 更新最后查看时间
4. 发送已读回执

**使用场景**:
- 进入聊天界面时
- 消息已读确认
- 未读状态清除

---

### 4.4 handleNewMessage

**方法签名**:
```java
Result<Void> handleNewMessage(Long senderId, Long receiverId, Long messageId, LocalDateTime messageTime)
```

**功能描述**: 处理新消息事件，自动创建或更新相关用户的会话状态

**参数说明**:
- `senderId`: 发送者ID (必填)
- `receiverId`: 接收者ID (必填)
- `messageId`: 消息ID (必填)
- `messageTime`: 消息时间 (必填)

**返回值**: `Result<Void>` - 处理结果

**业务逻辑**:
1. 更新发送者会话的最后消息
2. 更新接收者会话的最后消息和未读数
3. 创建不存在的会话
4. 同步会话状态

**使用场景**:
- 消息发送完成后调用
- 会话状态自动维护
- 消息事件处理

---

## 5. 会话清理

### 5.1 deleteEmptySessions

**方法签名**:
```java
Result<Integer> deleteEmptySessions(Long userId)
```

**功能描述**: 删除空会话，删除没有消息记录的会话

**参数说明**:
- `userId`: 用户ID (可选，为空则清理所有用户)

**返回值**: `Result<Integer>` - 删除的会话数量

**业务逻辑**:
1. 查找没有消息的会话
2. 验证会话状态
3. 批量删除空会话
4. 清理相关缓存

**使用场景**:
- 定时数据清理
- 会话列表优化
- 存储空间释放

---

### 5.2 deleteArchivedSessions

**方法签名**:
```java
Result<Integer> deleteArchivedSessions(LocalDateTime beforeTime, Long userId)
```

**功能描述**: 删除指定时间前的归档会话

**参数说明**:
- `beforeTime`: 截止时间 (必填)
- `userId`: 用户ID (可选)

**返回值**: `Result<Integer>` - 删除的会话数量

**业务逻辑**:
1. 查找过期的归档会话
2. 验证删除条件
3. 物理删除会话数据
4. 清理关联数据

**使用场景**:
- 定期归档清理
- 数据库维护
- 存储优化

---

### 5.3 batchArchiveSessions

**方法签名**:
```java
Result<Void> batchArchiveSessions(List<Long> sessionIds, Long userId)
```

**功能描述**: 批量归档会话

**参数说明**:
- `sessionIds`: 会话ID列表 (必填)
- `userId`: 操作用户ID (必填)

**返回值**: `Result<Void>` - 批量操作结果

**业务逻辑**:
1. 验证所有会话的权限
2. 批量更新归档状态
3. 记录操作日志
4. 更新相关缓存

**使用场景**:
- 会话批量管理
- 清理会话列表
- 批量归档操作

---

### 5.4 batchUnarchiveSessions

**方法签名**:
```java
Result<Void> batchUnarchiveSessions(List<Long> sessionIds, Long userId)
```

**功能描述**: 批量取消归档会话

**参数说明**:
- `sessionIds`: 会话ID列表 (必填)
- `userId`: 操作用户ID (必填)

**返回值**: `Result<Void>` - 批量操作结果

**业务逻辑**:
1. 验证所有会话的权限
2. 批量取消归档状态
3. 恢复会话排序
4. 更新显示状态

**使用场景**:
- 恢复归档会话
- 批量会话管理
- 会话状态恢复

---

## 6. 系统功能

### 6.1 rebuildSessionIndex

**方法签名**:
```java
Result<String> rebuildSessionIndex(Long userId)
```

**功能描述**: 重建会话索引，系统维护功能，重新计算会话的统计信息

**参数说明**:
- `userId`: 用户ID (可选)

**返回值**: `Result<String>` - 处理结果描述

**业务逻辑**:
1. 重新计算未读数量
2. 更新最后消息信息
3. 重建搜索索引
4. 修复数据不一致

**使用场景**:
- 系统维护
- 数据修复
- 索引重建

---

### 6.2 healthCheck

**方法签名**:
```java
Result<String> healthCheck()
```

**功能描述**: 会话系统健康检查

**返回值**: `Result<String>` - 系统状态描述

**业务逻辑**:
1. 检查数据库连接
2. 验证缓存服务
3. 测试会话功能
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
| USER_NOT_FOUND | 用户不存在 | 指定的用户ID不存在 |
| SESSION_NOT_FOUND | 会话不存在 | 指定的会话ID不存在 |
| SESSION_CREATE_FAILED | 会话创建失败 | 创建会话时发生错误 |
| SESSION_UPDATE_PERMISSION_DENIED | 无会话更新权限 | 只有会话参与者可以更新 |
| SESSION_ARCHIVE_FAILED | 会话归档失败 | 归档操作失败 |
| SESSION_DUPLICATE | 会话已存在 | 两用户间已存在会话 |
| UNREAD_COUNT_INVALID | 未读数量无效 | 未读数量不能为负数 |

### 系统错误码

| 错误码 | 错误信息 | 说明 |
|-------|---------|------|
| 500 | 系统内部错误 | 服务器异常 |
| 503 | 服务不可用 | 系统维护中 |
| 429 | 请求过于频繁 | 触发限流规则 |

---

## 最佳实践

### 1. 会话状态管理
```java
// 新消息到达时的完整处理流程
public void handleMessageArrived(Long senderId, Long receiverId, Long messageId) {
    try {
        // 1. 处理新消息事件
        Result<Void> result = messageSessionFacadeService.handleNewMessage(
            senderId, receiverId, messageId, LocalDateTime.now());
        
        if (result.isSuccess()) {
            // 2. 发送实时推送
            pushNotification(receiverId, messageId);
            log.info("消息处理完成: messageId={}", messageId);
        } else {
            log.error("消息处理失败: {}", result.getMessage());
        }
    } catch (Exception e) {
        log.error("消息处理异常", e);
    }
}
```

### 2. 未读数量管理
```java
// 进入聊天界面时清零未读数
public void enterChatRoom(Long userId, Long otherUserId) {
    Result<Void> result = messageSessionFacadeService.clearUnreadCount(userId, otherUserId);
    if (result.isSuccess()) {
        // 更新UI显示
        updateUnreadBadge(userId);
    }
}
```

### 3. 批量操作
```java
// 批量归档时的安全操作
public void batchArchive(List<Long> sessionIds, Long userId) {
    if (sessionIds.size() > MAX_BATCH_SIZE) {
        // 分批处理避免超时
        Lists.partition(sessionIds, MAX_BATCH_SIZE).forEach(batch -> {
            messageSessionFacadeService.batchArchiveSessions(batch, userId);
        });
    } else {
        messageSessionFacadeService.batchArchiveSessions(sessionIds, userId);
    }
}
```

### 4. 会话列表优化
```java
// 获取会话列表时的性能优化
public PageResponse<MessageSessionResponse> getSessionList(Long userId, int page, int size) {
    // 使用缓存提高性能
    String cacheKey = "sessions:" + userId + ":" + page + ":" + size;
    PageResponse<MessageSessionResponse> cached = cacheService.get(cacheKey);
    
    if (cached != null) {
        return cached;
    }
    
    Result<PageResponse<MessageSessionResponse>> result = 
        messageSessionFacadeService.getUserSessions(userId, false, null, page, size);
    
    if (result.isSuccess()) {
        cacheService.set(cacheKey, result.getData(), Duration.ofMinutes(5));
        return result.getData();
    }
    
    return new PageResponse<>();
}
```

---

**文档版本**: v2.0.0  
**最后更新**: 2024-01-30  
**维护者**: GIG Team