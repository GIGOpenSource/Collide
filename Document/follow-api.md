# Collide 关注服务接口文档

## 目录

- [服务介绍](#服务介绍)
- [统一响应格式](#统一响应格式)
- [接口列表](#接口列表)
  - [关注用户](#关注用户)
  - [取消关注用户](#取消关注用户)
  - [查询关注状态](#查询关注状态)
  - [获取关注列表](#获取关注列表)
  - [获取粉丝列表](#获取粉丝列表)
  - [获取关注统计](#获取关注统计)
- [错误码说明](#错误码说明)
- [数据字典](#数据字典)
- [注意事项](#注意事项)

---

## 服务介绍

**服务名称：** collide-follow  
**服务端口：** 8083  
**网关访问：** http://localhost:8081  
**服务描述：** 负责用户关注功能，包括关注/取消关注、关注状态查询、关注列表管理、粉丝列表管理、关注统计等功能

---

## 统一响应格式

**普通响应格式：**
```json
{
    "code": "string",      // 响应码
    "success": boolean,    // 是否成功
    "message": "string",   // 响应消息
    "data": object        // 响应数据
}
```

**分页响应格式：**
```json
{
    "code": "string",
    "success": boolean,
    "message": "string",
    "data": [],           // 数据列表
    "total": number,      // 总记录数
    "current": number,    // 当前页码
    "size": number,       // 每页大小
    "pages": number       // 总页数
}
```

---

## 接口列表

### 关注用户

**接口地址：** `POST /follow/follow/{followedUserId}`

**请求头：**
```http
Authorization: Bearer {token}
```

**路径参数：**
| 参数名 | 类型 | 必填 | 描述 | 示例 |
|-------|------|------|------|------|
| followedUserId | Long | 是 | 被关注用户ID | 123 |

**请求示例：**
```http
POST /follow/follow/123
Authorization: Bearer eyJ0eXAiOiJKV1Q...
```

**响应示例：**
```json
{
    "code": "SUCCESS",
    "success": true,
    "message": "SUCCESS",
    "data": true
}
```

**可能的错误：**
- `CANNOT_FOLLOW_SELF`: 不能关注自己
- `ALREADY_FOLLOWED`: 已经关注了该用户
- `USER_NOT_EXIST`: 用户不存在
- `FOLLOW_LIMIT_EXCEEDED`: 关注数量超过限制

**说明：**
- 需要登录认证
- 通过Token自动获取当前用户ID作为关注者
- 不能关注自己
- 支持重复关注（恢复已取消的关注）
- 关注成功会更新双方的统计数据

---

### 取消关注用户

**接口地址：** `DELETE /follow/unfollow/{followedUserId}`

**请求头：**
```http
Authorization: Bearer {token}
```

**路径参数：**
| 参数名 | 类型 | 必填 | 描述 | 示例 |
|-------|------|------|------|------|
| followedUserId | Long | 是 | 被取消关注用户ID | 123 |

**请求示例：**
```http
DELETE /follow/unfollow/123
Authorization: Bearer eyJ0eXAiOiJKV1Q...
```

**响应示例：**
```json
{
    "code": "SUCCESS",
    "success": true,
    "message": "SUCCESS",
    "data": true
}
```

**可能的错误：**
- `NOT_FOLLOWED`: 尚未关注该用户
- `USER_NOT_EXIST`: 用户不存在

**说明：**
- 需要登录认证
- 通过Token自动获取当前用户ID作为关注者
- 使用软删除，数据可恢复
- 取消关注会更新双方的统计数据

---

### 查询关注状态

**接口地址：** `GET /follow/status/{followedUserId}`

**请求头：**
```http
Authorization: Bearer {token}
```

**路径参数：**
| 参数名 | 类型 | 必填 | 描述 | 示例 |
|-------|------|------|------|------|
| followedUserId | Long | 是 | 查询目标用户ID | 123 |

**请求示例：**
```http
GET /follow/status/123
Authorization: Bearer eyJ0eXAiOiJKV1Q...
```

**响应示例：**
```json
{
    "code": "SUCCESS",
    "success": true,
    "message": "SUCCESS",
    "data": true    // true-已关注，false-未关注
}
```

**说明：**
- 需要登录认证
- 返回当前用户是否关注目标用户
- 查询结果实时准确

---

### 获取关注列表

**接口地址：** `GET /follow/following`

**请求头：**
```http
Authorization: Bearer {token}
```

**请求参数：**
| 参数名 | 类型 | 必填 | 描述 | 默认值 | 示例 |
|-------|------|------|------|-------|------|
| currentPage | Integer | 否 | 当前页码 | 1 | 1 |
| pageSize | Integer | 否 | 每页大小 | 10 | 20 |

**请求示例：**
```http
GET /follow/following?currentPage=1&pageSize=10
Authorization: Bearer eyJ0eXAiOiJKV1Q...
```

**响应示例：**
```json
{
    "code": "SUCCESS",
    "success": true,
    "message": "SUCCESS",
    "data": [
        {
            "followId": 123,                        // 关注记录ID
            "followerUserId": 1001,                 // 关注者用户ID
            "followedUserId": 2001,                 // 被关注者用户ID
            "followedUserName": "张三",              // 被关注者用户名
            "followedUserAvatar": "https://...",    // 被关注者头像
            "followedUserBio": "用户简介",           // 被关注者简介
            "followType": "NORMAL",                 // 关注类型
            "isMutualFollow": true,                 // 是否互相关注
            "followTime": "2024-01-01T12:00:00",    // 关注时间
            "followerCount": 100,                   // 被关注者粉丝数
            "followingCount": 50                    // 被关注者关注数
        }
    ],
    "total": 100,        // 总记录数
    "current": 1,        // 当前页码
    "size": 10,          // 每页大小
    "pages": 10          // 总页数
}
```

**说明：**
- 需要登录认证
- 查询当前用户的关注列表
- 按关注时间倒序排列
- 支持分页查询
- 包含被关注用户的基本信息和统计数据

---

### 获取粉丝列表

**接口地址：** `GET /follow/followers`

**请求头：**
```http
Authorization: Bearer {token}
```

**请求参数：**
| 参数名 | 类型 | 必填 | 描述 | 默认值 | 示例 |
|-------|------|------|------|-------|------|
| currentPage | Integer | 否 | 当前页码 | 1 | 1 |
| pageSize | Integer | 否 | 每页大小 | 10 | 20 |

**请求示例：**
```http
GET /follow/followers?currentPage=1&pageSize=10
Authorization: Bearer eyJ0eXAiOiJKV1Q...
```

**响应示例：**
```json
{
    "code": "SUCCESS",
    "success": true,
    "message": "SUCCESS",
    "data": [
        {
            "followId": 124,                        // 关注记录ID
            "followerUserId": 3001,                 // 关注者用户ID
            "followedUserId": 1001,                 // 被关注者用户ID
            "followerUserName": "李四",              // 关注者用户名
            "followerUserAvatar": "https://...",    // 关注者头像
            "followerUserBio": "用户简介",           // 关注者简介
            "followType": "NORMAL",                 // 关注类型
            "isMutualFollow": false,                // 是否互相关注
            "followTime": "2024-01-01T12:00:00",    // 关注时间
            "followerCount": 80,                    // 关注者粉丝数
            "followingCount": 120                   // 关注者关注数
        }
    ],
    "total": 50,         // 总记录数
    "current": 1,        // 当前页码
    "size": 10,          // 每页大小
    "pages": 5           // 总页数
}
```

**说明：**
- 需要登录认证
- 查询当前用户的粉丝列表
- 按关注时间倒序排列
- 支持分页查询
- 包含关注者的基本信息和统计数据

---

### 获取关注统计

**接口地址：** `GET /follow/statistics`

**请求头：**
```http
Authorization: Bearer {token}
```

**请求示例：**
```http
GET /follow/statistics
Authorization: Bearer eyJ0eXAiOiJKV1Q...
```

**响应示例：**
```json
{
    "code": "SUCCESS",
    "success": true,
    "message": "SUCCESS",
    "data": {
        "followingCount": 150,    // 我关注的人数
        "followerCount": 200      // 关注我的人数
    }
}
```

**说明：**
- 需要登录认证
- 获取当前用户的关注统计信息
- 数据来源于统计表，响应快速
- 统计数据实时更新

---

## 错误码说明

### 关注相关错误码

| 错误码 | 描述 | HTTP状态码 | 说明 |
|-------|------|-----------|------|
| CANNOT_FOLLOW_SELF | 不能关注自己 | 200 | 防止用户关注自己 |
| ALREADY_FOLLOWED | 已经关注了该用户 | 200 | 重复关注同一用户 |
| NOT_FOLLOWED | 尚未关注该用户 | 200 | 取消关注不存在的关注关系 |
| USER_NOT_EXIST | 用户不存在 | 200 | 目标用户不存在 |
| FOLLOW_LIMIT_EXCEEDED | 关注数量超过限制 | 200 | 超过最大关注数量限制 |
| SYSTEM_ERROR | 系统异常 | 200 | 系统内部错误 |

### 通用错误码

| 错误码 | 描述 | HTTP状态码 |
|-------|------|-----------|
| SUCCESS | 成功 | 200 |
| USER_NOT_LOGIN | 用户未登录 | 401 |

---

## 数据字典

### 关注状态 (FollowStatusEnum)

| 状态值 | 描述 | 说明 |
|-------|------|------|
| NOT_FOLLOWED | 未关注 | 用户之间没有关注关系 |
| FOLLOWED | 已关注 | 单向关注关系 |
| MUTUAL_FOLLOWED | 互相关注 | 双向关注关系 |

### 关注类型 (FollowTypeEnum)

| 类型值 | 描述 | 说明 |
|-------|------|------|
| NORMAL | 普通关注 | 默认关注类型 |
| SPECIAL | 特别关注 | 特殊关注，可用于推荐算法 |

### 关注信息对象 (FollowInfo)

| 字段名 | 类型 | 描述 | 是否必填 |
|-------|------|------|----------|
| followId | Long | 关注记录ID | 是 |
| followerUserId | Long | 关注者用户ID | 是 |
| followedUserId | Long | 被关注者用户ID | 是 |
| followedUserName | String | 被关注者用户名 | 否 |
| followedUserAvatar | String | 被关注者头像URL | 否 |
| followedUserBio | String | 被关注者简介 | 否 |
| followType | FollowTypeEnum | 关注类型 | 是 |
| isMutualFollow | Boolean | 是否互相关注 | 否 |
| followTime | Date | 关注时间 | 是 |
| followerCount | Integer | 被关注者粉丝数 | 否 |
| followingCount | Integer | 被关注者关注数 | 否 |

### 粉丝信息对象 (FollowerInfo)

| 字段名 | 类型 | 描述 | 是否必填 |
|-------|------|------|----------|
| followId | Long | 关注记录ID | 是 |
| followerUserId | Long | 关注者用户ID | 是 |
| followedUserId | Long | 被关注者用户ID | 是 |
| followerUserName | String | 关注者用户名 | 否 |
| followerUserAvatar | String | 关注者头像URL | 否 |
| followerUserBio | String | 关注者简介 | 否 |
| followType | FollowTypeEnum | 关注类型 | 是 |
| isMutualFollow | Boolean | 是否互相关注 | 否 |
| followTime | Date | 关注时间 | 是 |
| followerCount | Integer | 关注者粉丝数 | 否 |
| followingCount | Integer | 关注者关注数 | 否 |

### 关注统计对象 (FollowStatisticsVO)

| 字段名 | 类型 | 描述 | 是否必填 |
|-------|------|------|----------|
| followingCount | Integer | 关注数量 | 是 |
| followerCount | Integer | 粉丝数量 | 是 |

---

## 注意事项

### 1. 数据安全
- **权限控制：** 所有接口都需要用户登录认证
- **防刷机制：** 关注/取消关注操作有频率限制
- **数据隔离：** 用户只能操作自己的关注关系

### 2. 性能优化
- **缓存机制：** 关注状态和统计数据使用缓存
- **分页查询：** 关注列表和粉丝列表强制分页，避免大数据量问题
- **统计表设计：** 使用独立统计表提高查询性能
- **索引优化：** 关键查询字段添加数据库索引

### 3. 数据一致性
- **事务保证：** 关注/取消关注操作使用数据库事务
- **统计同步：** 关注操作会同步更新统计数据
- **软删除：** 使用状态字段标记删除，保证数据可恢复

### 4. 业务规则
- **自关注限制：** 不允许用户关注自己
- **重复关注：** 允许重复关注（恢复已取消的关注）
- **关注上限：** 支持配置关注数量上限
- **互关检测：** 自动检测和标记互相关注关系

### 5. 扩展性设计
- **关注类型：** 支持普通关注和特别关注
- **用户信息：** 预留用户详细信息字段
- **统计扩展：** 支持更多维度的统计分析
- **推荐算法：** 关注数据可用于用户推荐

### 6. 监控和运维
- **操作日志：** 关键操作有详细日志记录
- **性能监控：** 接口响应时间和成功率监控
- **数据校验：** 定期校验统计数据准确性
- **异常告警：** 异常情况实时告警

### 7. 开发调试
- **接口测试：** 提供完整的接口测试用例
- **Mock数据：** 支持测试环境数据初始化
- **错误处理：** 完善的错误码和异常处理机制

### 8. 集成说明
- **用户服务集成：** 需要调用用户服务获取用户详细信息
- **消息推送：** 关注成功可触发消息推送
- **数据同步：** 与其他业务模块的数据同步机制 