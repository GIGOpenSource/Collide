# 消息设置门面服务 Facade API 文档

## 概述

消息设置门面服务接口(MessageSettingFacadeService)是消息设置模块的核心业务接口，提供统一的Result<T>包装和完整的消息设置功能。基于message-simple.sql的t_message_setting表设计，管理用户的消息偏好设置和权限控制。

- **模块**: 消息设置门面服务 (Message Setting Facade Service)
- **接口**: MessageSettingFacadeService
- **包路径**: `com.gig.collide.api.message.MessageSettingFacadeService`
- **版本**: 2.0.0
- **总方法数**: 15个

## 功能特性

- ✅ **统一返回值**: 所有方法使用Result<T>包装返回结果
- ✅ **设置管理**: 创建/更新用户设置、查询设置、初始化默认设置
- ✅ **单项设置**: 陌生人消息、已读回执、消息通知单独控制
- ✅ **权限验证**: 发送权限检查、各种设置状态查询
- ✅ **设置模板**: 重置默认、复制设置、批量初始化
- ✅ **设置分析**: 统计信息、设置历史记录
- ✅ **系统功能**: 设置同步、健康检查

---

## 1. 设置管理

### 1.1 createOrUpdateSetting

**方法签名**:
```java
Result<MessageSettingResponse> createOrUpdateSetting(MessageSettingCreateRequest request)
```

**功能描述**: 创建或更新用户消息设置，如果设置不存在则创建默认设置，存在则更新

**参数说明**:
- `request`: 设置创建请求对象
  - `userId`: 用户ID (必填)
  - `allowStrangerMsg`: 是否允许陌生人发消息 (可选，默认true)
  - `autoReadReceipt`: 是否自动发送已读回执 (可选，默认true)
  - `messageNotification`: 是否开启消息通知 (可选，默认true)
  - `soundNotification`: 是否开启声音通知 (可选，默认true)
  - `vibrationNotification`: 是否开启震动通知 (可选，默认true)

**返回值**: `Result<MessageSettingResponse>` - 包装的设置响应对象

**业务逻辑**:
1. 验证用户是否存在
2. 检查是否已有设置
3. 如不存在则创建默认设置
4. 如存在则更新指定字段
5. 记录设置变更历史

**成功响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "settingId": 1001,
    "userId": 1001,
    "allowStrangerMsg": true,
    "autoReadReceipt": true,
    "messageNotification": true,
    "soundNotification": true,
    "vibrationNotification": true,
    "createTime": "2024-01-30 10:00:00",
    "updateTime": "2024-01-30 10:30:00"
  }
}
```

**使用场景**:
- 用户首次使用消息功能时创建默认设置
- 用户修改消息偏好设置
- 系统批量初始化用户设置

---

### 1.2 getUserSetting

**方法签名**:
```java
Result<MessageSettingResponse> getUserSetting(Long userId)
```

**功能描述**: 根据用户ID获取消息设置，如果不存在则返回默认设置

**参数说明**:
- `userId`: 用户ID (必填)

**返回值**: `Result<MessageSettingResponse>` - 包装的用户设置

**业务逻辑**:
1. 验证用户是否存在
2. 查询用户设置
3. 如不存在则返回系统默认设置
4. 缓存设置信息提高性能

**使用场景**:
- 获取用户当前消息设置
- 设置页面数据加载
- 权限验证前的设置查询

---

### 1.3 updateUserSetting

**方法签名**:
```java
Result<MessageSettingResponse> updateUserSetting(MessageSettingUpdateRequest request)
```

**功能描述**: 更新用户消息设置

**参数说明**:
- `request`: 更新请求对象
  - `userId`: 用户ID (必填)
  - `allowStrangerMsg`: 是否允许陌生人发消息 (可选)
  - `autoReadReceipt`: 是否自动发送已读回执 (可选)
  - `messageNotification`: 是否开启消息通知 (可选)
  - `soundNotification`: 是否开启声音通知 (可选)
  - `vibrationNotification`: 是否开启震动通知 (可选)

**返回值**: `Result<MessageSettingResponse>` - 包装的更新后设置

**业务逻辑**:
1. 验证用户权限
2. 检查设置值的有效性
3. 更新指定字段
4. 清除相关缓存
5. 记录变更历史

**使用场景**:
- 用户在设置页面修改偏好
- 系统自动调整用户设置
- 批量设置更新

---

### 1.4 initDefaultSetting

**方法签名**:
```java
Result<MessageSettingResponse> initDefaultSetting(Long userId)
```

**功能描述**: 初始化用户默认设置，为新用户创建默认的消息设置

**参数说明**:
- `userId`: 用户ID (必填)

**返回值**: `Result<MessageSettingResponse>` - 包装的创建的默认设置

**业务逻辑**:
1. 验证用户是否存在
2. 检查是否已有设置
3. 创建系统默认设置
4. 记录初始化日志

**使用场景**:
- 新用户注册时自动调用
- 用户首次进入消息设置
- 系统数据初始化

---

## 2. 单项设置更新

### 2.1 updateStrangerMessageSetting

**方法签名**:
```java
Result<Void> updateStrangerMessageSetting(Long userId, Boolean allowStrangerMsg)
```

**功能描述**: 更新陌生人消息设置

**参数说明**:
- `userId`: 用户ID (必填)
- `allowStrangerMsg`: 是否允许陌生人发消息 (必填)

**返回值**: `Result<Void>` - 操作结果

**业务逻辑**:
1. 验证用户存在性
2. 更新陌生人消息设置
3. 清除权限验证缓存
4. 记录设置变更

**使用场景**:
- 用户单独控制陌生人消息权限
- 隐私设置快速开关
- 安全相关设置调整

---

### 2.2 updateReadReceiptSetting

**方法签名**:
```java
Result<Void> updateReadReceiptSetting(Long userId, Boolean autoReadReceipt)
```

**功能描述**: 更新已读回执设置

**参数说明**:
- `userId`: 用户ID (必填)
- `autoReadReceipt`: 是否自动发送已读回执 (必填)

**返回值**: `Result<Void>` - 操作结果

**业务逻辑**:
1. 验证用户权限
2. 更新已读回执设置
3. 影响后续消息的已读回执行为
4. 记录设置变更

**使用场景**:
- 隐私控制设置
- 消息阅读行为配置
- 社交偏好调整

---

### 2.3 updateNotificationSetting

**方法签名**:
```java
Result<Void> updateNotificationSetting(Long userId, Boolean messageNotification)
```

**功能描述**: 更新消息通知设置

**参数说明**:
- `userId`: 用户ID (必填)
- `messageNotification`: 是否开启消息通知 (必填)

**返回值**: `Result<Void>` - 操作结果

**业务逻辑**:
1. 验证用户存在性
2. 更新通知设置
3. 影响推送通知行为
4. 同步设备通知配置

**使用场景**:
- 通知开关控制
- 免打扰模式设置
- 推送策略调整

---

### 2.4 updateBatchSettings

**方法签名**:
```java
Result<Void> updateBatchSettings(Long userId, Boolean allowStrangerMsg, Boolean autoReadReceipt, Boolean messageNotification)
```

**功能描述**: 批量更新用户设置，支持同时更新多个设置项

**参数说明**:
- `userId`: 用户ID (必填)
- `allowStrangerMsg`: 是否允许陌生人发消息 (可选)
- `autoReadReceipt`: 是否自动发送已读回执 (可选)
- `messageNotification`: 是否开启消息通知 (可选)

**返回值**: `Result<Void>` - 批量操作结果

**业务逻辑**:
1. 验证用户权限
2. 批量更新非空字段
3. 原子性操作保证一致性
4. 清除相关缓存

**使用场景**:
- 设置页面一次性保存
- 系统批量调整设置
- 快速设置模板应用

---

## 3. 权限验证

### 3.1 canSendMessage

**方法签名**:
```java
Result<Boolean> canSendMessage(Long senderId, Long receiverId)
```

**功能描述**: 检查是否允许发送消息，根据用户设置和关系验证是否可以向目标用户发送消息

**参数说明**:
- `senderId`: 发送者ID (必填)
- `receiverId`: 接收者ID (必填)

**返回值**: `Result<Boolean>` - 是否允许发送

**业务逻辑**:
1. 验证两个用户是否存在
2. 获取接收者的陌生人消息设置
3. 如果允许陌生人消息，直接返回true
4. 如果不允许，检查是否为好友关系
5. 缓存权限验证结果

**权限判断逻辑**:
```
if (接收者允许陌生人消息) {
    return true;
} else if (发送者是接收者的好友) {
    return true;
} else {
    return false;
}
```

**使用场景**:
- 发送消息前的权限检查
- 聊天功能入口控制
- 消息发送按钮状态控制

---

### 3.2 isStrangerMessageAllowed

**方法签名**:
```java
Result<Boolean> isStrangerMessageAllowed(Long userId)
```

**功能描述**: 检查是否允许陌生人消息

**参数说明**:
- `userId`: 用户ID (必填)

**返回值**: `Result<Boolean>` - 是否允许陌生人发消息

**业务逻辑**:
1. 查询用户设置
2. 返回陌生人消息设置状态
3. 缓存查询结果

**使用场景**:
- 隐私设置状态显示
- 权限控制判断
- 设置页面状态同步

---

### 3.3 isAutoReadReceiptEnabled

**方法签名**:
```java
Result<Boolean> isAutoReadReceiptEnabled(Long userId)
```

**功能描述**: 检查是否开启自动已读回执

**参数说明**:
- `userId`: 用户ID (必填)

**返回值**: `Result<Boolean>` - 是否开启自动已读回执

**使用场景**:
- 消息已读处理逻辑
- 已读回执发送控制
- 设置状态查询

---

### 3.4 isMessageNotificationEnabled

**方法签名**:
```java
Result<Boolean> isMessageNotificationEnabled(Long userId)
```

**功能描述**: 检查是否开启消息通知

**参数说明**:
- `userId`: 用户ID (必填)

**返回值**: `Result<Boolean>` - 是否开启消息通知

**使用场景**:
- 推送通知发送控制
- 通知设置状态查询
- 免打扰模式判断

---

### 3.5 checkAllSettings

**方法签名**:
```java
Result<MessageSettingResponse> checkAllSettings(Long userId)
```

**功能描述**: 批量检查用户设置状态

**参数说明**:
- `userId`: 用户ID (必填)

**返回值**: `Result<MessageSettingResponse>` - 设置状态汇总

**业务逻辑**:
1. 一次性获取用户所有设置
2. 组装完整的设置响应
3. 优化性能减少多次查询

**使用场景**:
- 设置页面数据加载
- 权限批量验证
- 设置状态一览

---

## 4. 设置模板

### 4.1 resetToDefault

**方法签名**:
```java
Result<Void> resetToDefault(Long userId)
```

**功能描述**: 重置用户设置为默认值

**参数说明**:
- `userId`: 用户ID (必填)

**返回值**: `Result<Void>` - 操作结果

**业务逻辑**:
1. 获取系统默认设置
2. 重置用户所有设置项
3. 清除相关缓存
4. 记录重置操作

**使用场景**:
- 用户手动重置设置
- 问题排查时恢复默认
- 设置页面重置功能

---

### 4.2 getDefaultSetting

**方法签名**:
```java
Result<MessageSettingResponse> getDefaultSetting()
```

**功能描述**: 获取默认消息设置

**返回值**: `Result<MessageSettingResponse>` - 默认设置

**业务逻辑**:
1. 返回系统预定义的默认设置
2. 缓存默认设置提高性能
3. 支持动态配置调整

**默认设置值**:
```json
{
  "allowStrangerMsg": true,
  "autoReadReceipt": true,
  "messageNotification": true,
  "soundNotification": true,
  "vibrationNotification": true
}
```

**使用场景**:
- 新用户设置初始化
- 设置重置参考
- 系统配置管理

---

### 4.3 copySettingFromUser

**方法签名**:
```java
Result<Void> copySettingFromUser(Long fromUserId, Long toUserId)
```

**功能描述**: 复制设置到新用户，从模板用户复制设置到新用户

**参数说明**:
- `fromUserId`: 模板用户ID (必填)
- `toUserId`: 目标用户ID (必填)

**返回值**: `Result<Void>` - 操作结果

**业务逻辑**:
1. 验证两个用户都存在
2. 获取模板用户设置
3. 复制到目标用户
4. 记录复制操作

**使用场景**:
- 企业用户批量设置
- 家庭账户设置同步
- 设置模板应用

---

### 4.4 batchInitSettings

**方法签名**:
```java
Result<Integer> batchInitSettings(List<Long> userIds)
```

**功能描述**: 批量初始化用户设置，为多个用户批量创建默认设置

**参数说明**:
- `userIds`: 用户ID列表 (必填)

**返回值**: `Result<Integer>` - 成功初始化的用户数量

**业务逻辑**:
1. 验证所有用户存在性
2. 过滤已有设置的用户
3. 批量创建默认设置
4. 返回成功数量

**使用场景**:
- 系统批量用户导入
- 新功能上线时批量初始化
- 数据修复和补全

---

## 5. 设置分析

### 5.1 getSettingStatistics

**方法签名**:
```java
Result<Map<String, Object>> getSettingStatistics()
```

**功能描述**: 获取设置统计信息，统计各设置项的启用情况

**返回值**: `Result<Map<String, Object>>` - 统计信息

**统计信息示例**:
```json
{
  "totalUsers": 10000,
  "allowStrangerMsgCount": 8500,
  "allowStrangerMsgRate": 85.0,
  "autoReadReceiptCount": 9200,
  "autoReadReceiptRate": 92.0,
  "messageNotificationCount": 9500,
  "messageNotificationRate": 95.0,
  "soundNotificationCount": 7800,
  "soundNotificationRate": 78.0,
  "vibrationNotificationCount": 8200,
  "vibrationNotificationRate": 82.0
}
```

**使用场景**:
- 系统运营数据分析
- 用户行为统计
- 产品功能优化参考

---

### 5.2 getSettingHistory

**方法签名**:
```java
Result<List<MessageSettingResponse>> getSettingHistory(Long userId, Integer limit)
```

**功能描述**: 获取用户设置历史，查看用户设置的变更记录

**参数说明**:
- `userId`: 用户ID (必填)
- `limit`: 限制数量 (默认10)

**返回值**: `Result<List<MessageSettingResponse>>` - 设置历史记录列表

**业务逻辑**:
1. 查询用户设置变更历史
2. 按时间倒序排列
3. 限制返回数量
4. 包含操作时间和变更内容

**使用场景**:
- 设置变更审计
- 用户行为分析
- 问题排查和回溯

---

## 6. 系统功能

### 6.1 syncUserSetting

**方法签名**:
```java
Result<String> syncUserSetting(Long userId)
```

**功能描述**: 同步用户设置，同步用户在其他系统中的设置

**参数说明**:
- `userId`: 用户ID (必填)

**返回值**: `Result<String>` - 同步结果描述

**业务逻辑**:
1. 从其他系统获取用户设置
2. 比较并合并设置差异
3. 更新本地设置
4. 记录同步日志

**使用场景**:
- 多系统设置同步
- 用户迁移设置
- 设置数据修复

---

### 6.2 healthCheck

**方法签名**:
```java
Result<String> healthCheck()
```

**功能描述**: 消息设置系统健康检查

**返回值**: `Result<String>` - 系统状态描述

**业务逻辑**:
1. 检查数据库连接
2. 验证缓存服务
3. 测试设置功能
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
| SETTING_NOT_FOUND | 设置不存在 | 指定的设置ID不存在 |
| SETTING_CREATE_FAILED | 设置创建失败 | 创建设置时发生错误 |
| SETTING_UPDATE_FAILED | 设置更新失败 | 更新设置时发生错误 |
| SETTING_PERMISSION_DENIED | 设置权限不足 | 只能修改自己的设置 |
| SETTING_VALUE_INVALID | 设置值无效 | 设置值不在允许范围内 |
| STRANGER_MESSAGE_BLOCKED | 陌生人消息被阻止 | 接收者不允许陌生人发送消息 |

### 系统错误码

| 错误码 | 错误信息 | 说明 |
|-------|---------|------|
| 500 | 系统内部错误 | 服务器异常 |
| 503 | 服务不可用 | 系统维护中 |
| 429 | 请求过于频繁 | 触发限流规则 |

---

## 最佳实践

### 1. 权限验证
```java
// 发送消息前的权限检查
public boolean checkSendPermission(Long senderId, Long receiverId) {
    try {
        Result<Boolean> result = messageSettingFacadeService.canSendMessage(senderId, receiverId);
        return result.isSuccess() && result.getData();
    } catch (Exception e) {
        log.error("权限检查失败", e);
        return false; // 默认拒绝
    }
}
```

### 2. 设置初始化
```java
// 新用户注册时自动初始化设置
@EventListener
public void handleUserRegistered(UserRegisteredEvent event) {
    Long userId = event.getUserId();
    Result<MessageSettingResponse> result = 
        messageSettingFacadeService.initDefaultSetting(userId);
    
    if (result.isSuccess()) {
        log.info("用户设置初始化成功: userId={}", userId);
    } else {
        log.error("用户设置初始化失败: userId={}, error={}", userId, result.getMessage());
    }
}
```

### 3. 缓存优化
```java
// 权限验证结果缓存
@Cacheable(value = "messagePermission", key = "#senderId + ':' + #receiverId")
public Boolean checkCachedPermission(Long senderId, Long receiverId) {
    Result<Boolean> result = messageSettingFacadeService.canSendMessage(senderId, receiverId);
    return result.isSuccess() ? result.getData() : false;
}
```

### 4. 批量操作
```java
// 批量设置初始化的安全操作
public void batchInitUserSettings(List<Long> userIds) {
    if (userIds.size() > MAX_BATCH_SIZE) {
        // 分批处理避免超时
        Lists.partition(userIds, MAX_BATCH_SIZE).forEach(batch -> {
            try {
                messageSettingFacadeService.batchInitSettings(batch);
                Thread.sleep(100); // 避免过快请求
            } catch (Exception e) {
                log.error("批量初始化失败: batch={}", batch, e);
            }
        });
    } else {
        messageSettingFacadeService.batchInitSettings(userIds);
    }
}
```

### 5. 设置变更通知
```java
// 设置变更后的通知处理
public void updateSettingWithNotification(Long userId, Boolean allowStrangerMsg) {
    Result<Void> result = messageSettingFacadeService.updateStrangerMessageSetting(userId, allowStrangerMsg);
    
    if (result.isSuccess()) {
        // 清除相关缓存
        clearPermissionCache(userId);
        
        // 发送设置变更通知
        notifySettingChanged(userId, "stranger_message", allowStrangerMsg);
        
        log.info("陌生人消息设置更新成功: userId={}, allowStrangerMsg={}", userId, allowStrangerMsg);
    } else {
        log.error("设置更新失败: {}", result.getMessage());
    }
}
```

---

**文档版本**: v2.0.0  
**最后更新**: 2024-01-30  
**维护者**: GIG Team