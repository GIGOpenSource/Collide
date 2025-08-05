# 消息设置管理 REST API 文档

## 概述

消息设置管理REST API提供了完整的消息偏好设置HTTP接口，管理用户的消息权限控制和个性化配置。基于message-simple.sql的t_message_setting表设计，提供灵活的消息设置服务。

- **模块**: 消息设置管理 (Message Setting Management)
- **控制器**: MessageSettingController
- **基础路径**: `/api/v1/message-settings`
- **版本**: 2.0.0
- **总接口数**: 19个

## 功能特性

- ✅ **设置管理**: 创建/更新用户设置、查询设置、初始化默认设置
- ✅ **单项设置**: 陌生人消息、已读回执、消息通知单独控制
- ✅ **权限验证**: 发送权限检查、各种设置状态查询
- ✅ **设置模板**: 重置默认、复制设置、批量初始化
- ✅ **设置分析**: 统计信息、设置历史记录
- ✅ **系统功能**: 设置同步、健康检查
- ✅ **便捷操作**: 快速切换、推荐设置

---

## 1. 设置管理

### 1.1 创建或更新消息设置

**接口描述**: 如果设置不存在则创建默认设置，存在则更新

- **请求方式**: `POST`
- **请求路径**: `/api/v1/message-settings`
- **请求类型**: `application/json`

**请求参数**:
```json
{
  "userId": 1001,                    // 用户ID (必填)
  "allowStrangerMsg": true,          // 是否允许陌生人发消息 (可选)
  "autoReadReceipt": true,           // 是否自动发送已读回执 (可选)
  "messageNotification": true,       // 是否开启消息通知 (可选)
  "soundNotification": true,         // 是否开启声音通知 (可选)
  "vibrationNotification": true      // 是否开启震动通知 (可选)
}
```

**响应示例**:
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

**错误码**:
- `USER_NOT_FOUND`: 用户不存在
- `SETTING_CREATE_FAILED`: 设置创建失败

---

### 1.2 获取用户消息设置

**接口描述**: 如果不存在则返回默认设置

- **请求方式**: `GET`
- **请求路径**: `/api/v1/message-settings/user/{userId}`

**路径参数**:
- `userId`: 用户ID (必填)

**响应示例**:
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

---

### 1.3 更新用户消息设置

**接口描述**: 更新用户的消息偏好设置

- **请求方式**: `PUT`
- **请求路径**: `/api/v1/message-settings/user/{userId}`
- **请求类型**: `application/json`

**路径参数**:
- `userId`: 用户ID (必填)

**请求参数**:
```json
{
  "allowStrangerMsg": false,         // 是否允许陌生人发消息 (可选)
  "autoReadReceipt": false,          // 是否自动发送已读回执 (可选)
  "messageNotification": true,       // 是否开启消息通知 (可选)
  "soundNotification": false,        // 是否开启声音通知 (可选)
  "vibrationNotification": true      // 是否开启震动通知 (可选)
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "settingId": 1001,
    "userId": 1001,
    "allowStrangerMsg": false,
    "autoReadReceipt": false,
    "messageNotification": true,
    "updateTime": "2024-01-30 10:35:00"
  }
}
```

---

### 1.4 初始化用户默认设置

**接口描述**: 为新用户创建默认的消息设置

- **请求方式**: `POST`
- **请求路径**: `/api/v1/message-settings/user/{userId}/init`

**路径参数**:
- `userId`: 用户ID (必填)

**响应示例**:
```json
{
  "code": 200,
  "message": "初始化成功",
  "data": {
    "settingId": 1002,
    "userId": 1001,
    "allowStrangerMsg": true,
    "autoReadReceipt": true,
    "messageNotification": true,
    "soundNotification": true,
    "vibrationNotification": true,
    "createTime": "2024-01-30 10:40:00"
  }
}
```

---

## 2. 单项设置更新

### 2.1 更新陌生人消息设置

**接口描述**: 控制是否允许陌生人发送消息

- **请求方式**: `PUT`
- **请求路径**: `/api/v1/message-settings/user/{userId}/stranger-message`

**路径参数**:
- `userId`: 用户ID (必填)

**查询参数**:
- `allowStrangerMsg`: 是否允许陌生人发消息 (必填)

**响应示例**:
```json
{
  "code": 200,
  "message": "设置更新成功",
  "data": null
}
```

---

### 2.2 更新已读回执设置

**接口描述**: 控制是否自动发送已读回执

- **请求方式**: `PUT`
- **请求路径**: `/api/v1/message-settings/user/{userId}/read-receipt`

**路径参数**:
- `userId`: 用户ID (必填)

**查询参数**:
- `autoReadReceipt`: 是否自动发送已读回执 (必填)

**响应示例**:
```json
{
  "code": 200,
  "message": "设置更新成功",
  "data": null
}
```

---

### 2.3 更新消息通知设置

**接口描述**: 控制是否开启消息通知

- **请求方式**: `PUT`
- **请求路径**: `/api/v1/message-settings/user/{userId}/notification`

**路径参数**:
- `userId`: 用户ID (必填)

**查询参数**:
- `messageNotification`: 是否开启消息通知 (必填)

**响应示例**:
```json
{
  "code": 200,
  "message": "设置更新成功",
  "data": null
}
```

---

### 2.4 批量更新用户设置

**接口描述**: 同时更新多个设置项

- **请求方式**: `PUT`
- **请求路径**: `/api/v1/message-settings/user/{userId}/batch`

**路径参数**:
- `userId`: 用户ID (必填)

**查询参数**:
- `allowStrangerMsg`: 是否允许陌生人发消息 (可选)
- `autoReadReceipt`: 是否自动发送已读回执 (可选)
- `messageNotification`: 是否开启消息通知 (可选)

**响应示例**:
```json
{
  "code": 200,
  "message": "批量更新成功",
  "data": null
}
```

---

## 3. 权限验证

### 3.1 检查发送消息权限

**接口描述**: 根据用户设置和关系验证是否可以向目标用户发送消息

- **请求方式**: `GET`
- **请求路径**: `/api/v1/message-settings/permission/send`

**查询参数**:
- `senderId`: 发送者ID (必填)
- `receiverId`: 接收者ID (必填)

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": true
}
```

**业务逻辑说明**:
- 如果接收者允许陌生人消息：返回 `true`
- 如果接收者不允许陌生人消息，但发送者是好友：返回 `true`
- 否则返回 `false`

---

### 3.2 检查陌生人消息权限

**接口描述**: 查询用户是否允许陌生人发送消息

- **请求方式**: `GET`
- **请求路径**: `/api/v1/message-settings/user/{userId}/stranger-message/allowed`

**路径参数**:
- `userId`: 用户ID (必填)

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": true
}
```

---

### 3.3 检查已读回执设置

**接口描述**: 查询用户是否开启自动已读回执

- **请求方式**: `GET`
- **请求路径**: `/api/v1/message-settings/user/{userId}/read-receipt/enabled`

**路径参数**:
- `userId`: 用户ID (必填)

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": true
}
```

---

### 3.4 检查消息通知设置

**接口描述**: 查询用户是否开启消息通知

- **请求方式**: `GET`
- **请求路径**: `/api/v1/message-settings/user/{userId}/notification/enabled`

**路径参数**:
- `userId`: 用户ID (必填)

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": true
}
```

---

### 3.5 批量检查用户设置

**接口描述**: 一次性获取用户所有设置状态

- **请求方式**: `GET`
- **请求路径**: `/api/v1/message-settings/user/{userId}/status`

**路径参数**:
- `userId`: 用户ID (必填)

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "userId": 1001,
    "allowStrangerMsg": true,
    "autoReadReceipt": true,
    "messageNotification": true,
    "soundNotification": true,
    "vibrationNotification": true
  }
}
```

---

## 4. 设置模板

### 4.1 重置用户设置

**接口描述**: 将用户设置重置为系统默认值

- **请求方式**: `PUT`
- **请求路径**: `/api/v1/message-settings/user/{userId}/reset`

**路径参数**:
- `userId`: 用户ID (必填)

**响应示例**:
```json
{
  "code": 200,
  "message": "重置成功",
  "data": null
}
```

---

### 4.2 获取默认设置

**接口描述**: 获取系统的默认消息设置

- **请求方式**: `GET`
- **请求路径**: `/api/v1/message-settings/default`

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "allowStrangerMsg": true,
    "autoReadReceipt": true,
    "messageNotification": true,
    "soundNotification": true,
    "vibrationNotification": true
  }
}
```

---

### 4.3 复制用户设置

**接口描述**: 从模板用户复制设置到新用户

- **请求方式**: `POST`
- **请求路径**: `/api/v1/message-settings/copy`

**查询参数**:
- `fromUserId`: 模板用户ID (必填)
- `toUserId`: 目标用户ID (必填)

**响应示例**:
```json
{
  "code": 200,
  "message": "复制成功",
  "data": null
}
```

---

### 4.4 批量初始化用户设置

**接口描述**: 为多个用户批量创建默认设置

- **请求方式**: `POST`
- **请求路径**: `/api/v1/message-settings/batch/init`
- **请求类型**: `application/json`

**请求参数**:
```json
[1001, 1002, 1003, 1004]    // 用户ID列表
```

**响应示例**:
```json
{
  "code": 200,
  "message": "批量初始化成功",
  "data": 4
}
```

---

## 5. 设置分析

### 5.1 获取设置统计

**接口描述**: 统计各设置项的启用情况

- **请求方式**: `GET`
- **请求路径**: `/api/v1/message-settings/statistics`

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
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
}
```

---

### 5.2 获取用户设置历史

**接口描述**: 查看用户设置的变更记录

- **请求方式**: `GET`
- **请求路径**: `/api/v1/message-settings/user/{userId}/history`

**路径参数**:
- `userId`: 用户ID (必填)

**查询参数**:
- `limit`: 限制数量 (默认10)

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "settingId": 1001,
      "userId": 1001,
      "allowStrangerMsg": false,
      "autoReadReceipt": true,
      "messageNotification": true,
      "updateTime": "2024-01-30 15:30:00",
      "operation": "UPDATE"
    },
    {
      "settingId": 1001,
      "userId": 1001,
      "allowStrangerMsg": true,
      "autoReadReceipt": true,
      "messageNotification": true,
      "updateTime": "2024-01-30 10:00:00",
      "operation": "CREATE"
    }
  ]
}
```

---

## 6. 系统功能

### 6.1 同步用户设置

**接口描述**: 同步用户在其他系统中的设置

- **请求方式**: `POST`
- **请求路径**: `/api/v1/message-settings/user/{userId}/sync`

**路径参数**:
- `userId`: 用户ID (必填)

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": "用户设置同步完成"
}
```

---

### 6.2 系统健康检查

**接口描述**: 检查消息设置系统运行状态

- **请求方式**: `GET`
- **请求路径**: `/api/v1/message-settings/health`

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": "消息设置系统运行正常"
}
```

---

## 7. 便捷操作

### 7.1 快速切换通知设置

**接口描述**: 一键开启或关闭所有消息通知

- **请求方式**: `PUT`
- **请求路径**: `/api/v1/message-settings/user/{userId}/notifications/toggle`

**路径参数**:
- `userId`: 用户ID (必填)

**查询参数**:
- `enabled`: 是否开启通知 (必填)

**响应示例**:
```json
{
  "code": 200,
  "message": "通知设置切换成功",
  "data": null
}
```

---

### 7.2 获取推荐设置

**接口描述**: 根据用户行为推荐最佳设置配置

- **请求方式**: `GET`
- **请求路径**: `/api/v1/message-settings/user/{userId}/recommended`

**路径参数**:
- `userId`: 用户ID (必填)

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "userId": 1001,
    "allowStrangerMsg": false,
    "autoReadReceipt": true,
    "messageNotification": true,
    "soundNotification": false,
    "vibrationNotification": true,
    "reason": "基于您的使用习惯推荐"
  }
}
```

---

### 7.3 应用推荐设置

**接口描述**: 应用系统推荐的设置配置

- **请求方式**: `PUT`
- **请求路径**: `/api/v1/message-settings/user/{userId}/apply-recommended`

**路径参数**:
- `userId`: 用户ID (必填)

**响应示例**:
```json
{
  "code": 200,
  "message": "推荐设置应用成功",
  "data": null
}
```

---

## 通用错误码

| 错误码 | 错误信息 | 说明 |
|-------|---------|------|
| 400 | 请求参数错误 | 参数格式不正确或缺少必填参数 |
| 401 | 未授权访问 | 需要登录或权限不足 |
| 403 | 禁止访问 | 用户被禁用或无权限 |
| 404 | 资源不存在 | 设置或用户不存在 |
| 429 | 请求过于频繁 | 触发限流规则 |
| 500 | 服务器内部错误 | 系统异常 |

## 业务错误码

| 错误码 | 错误信息 | 说明 |
|-------|---------|------|
| USER_NOT_FOUND | 用户不存在 | 指定的用户ID不存在 |
| SETTING_NOT_FOUND | 设置不存在 | 指定的设置ID不存在 |
| SETTING_CREATE_FAILED | 设置创建失败 | 创建设置时发生错误 |
| SETTING_UPDATE_FAILED | 设置更新失败 | 更新设置时发生错误 |
| SETTING_PERMISSION_DENIED | 设置权限不足 | 只能修改自己的设置 |
| SETTING_VALUE_INVALID | 设置值无效 | 设置值不在允许范围内 |
| STRANGER_MESSAGE_BLOCKED | 陌生人消息被阻止 | 接收者不允许陌生人发送消息 |

---

**文档版本**: v2.0.0  
**最后更新**: 2024-01-30  
**维护者**: GIG Team