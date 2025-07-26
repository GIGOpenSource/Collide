# Favorite 模块 API 接口文档

## 📋 目录
- [模块概述](#模块概述)
- [数据库设计](#数据库设计)
- [接口列表](#接口列表)
- [收藏管理接口](#收藏管理接口)
- [收藏夹管理接口](#收藏夹管理接口)
- [数据模型](#数据模型)
- [错误码定义](#错误码定义)
- [使用示例](#使用示例)

---

## 📚 模块概述

Favorite 模块是 Collide 社交平台的核心模块之一，负责用户收藏功能的完整实现，采用去连表化设计理念，提供高性能的收藏服务。

### 主要功能
- 收藏/取消收藏内容、用户、动态等
- 批量收藏操作支持
- 收藏夹分类管理
- 收藏数据统计和查询
- 幂等性保证和并发安全
- 目标信息冗余存储（去连表化）

### 技术特性
- **去连表化设计**: 冗余存储目标信息，避免复杂JOIN查询
- **幂等性保证**: 数据库唯一约束 + 应用层处理，确保操作幂等
- **异步数据同步**: 基于RocketMQ的目标信息同步机制
- **高性能查询**: 针对性索引设计，支持千万级数据查询
- **收藏夹管理**: 支持自定义收藏夹分类和权限管理

### 技术架构
- **框架**: Spring Boot 3.x + Spring Cloud
- **数据库**: MySQL 8.0 + 触发器 + 存储过程
- **ORM**: MyBatis Plus
- **RPC**: Apache Dubbo
- **消息队列**: RocketMQ
- **缓存**: Redis
- **认证**: Sa-Token

---

## 🗄️ 数据库设计

### 收藏主表 (t_favorite) - 去连表化设计

| 字段名 | 类型 | 是否必填 | 默认值 | 说明 |
|--------|------|----------|--------|------|
| favorite_id | BIGINT | 是 | 雪花ID | 收藏ID，主键 |
| favorite_type | VARCHAR(20) | 是 | - | 收藏类型：CONTENT、USER、SOCIAL、COMMENT、TOPIC |
| target_id | BIGINT | 是 | - | 目标ID（内容ID、用户ID等） |
| user_id | BIGINT | 是 | - | 收藏用户ID |
| folder_id | BIGINT | 否 | 1 | 收藏夹ID，默认为1（默认收藏夹） |
| status | VARCHAR(20) | 否 | NORMAL | 收藏状态：NORMAL、CANCELLED |
| remark | VARCHAR(500) | 否 | '' | 收藏备注 |
| favorite_time | DATETIME | 否 | NOW() | 收藏时间 |
| **target_title** | VARCHAR(500) | 否 | '' | **目标标题（冗余字段）** |
| **target_cover** | VARCHAR(500) | 否 | '' | **目标封面（冗余字段）** |
| **target_summary** | TEXT | 否 | - | **目标摘要（冗余字段）** |
| **target_author_id** | BIGINT | 否 | - | **目标作者ID（冗余字段）** |
| **target_author_name** | VARCHAR(100) | 否 | '' | **目标作者名称（冗余字段）** |
| **target_author_avatar** | VARCHAR(500) | 否 | '' | **目标作者头像（冗余字段）** |
| **target_publish_time** | DATETIME | 否 | - | **目标发布时间（冗余字段）** |
| create_time | DATETIME | 否 | NOW() | 创建时间 |
| update_time | DATETIME | 否 | NOW() | 更新时间 |
| is_deleted | TINYINT | 否 | 0 | 是否删除：0-否，1-是 |
| version | INT | 否 | 0 | 版本号（乐观锁） |

**核心约束**:
- `uk_user_favorite_target(user_id, favorite_type, target_id, is_deleted)`: 确保幂等性

### 收藏夹表 (t_favorite_folder)

| 字段名 | 类型 | 是否必填 | 默认值 | 说明 |
|--------|------|----------|--------|------|
| folder_id | BIGINT | 是 | 雪花ID | 收藏夹ID，主键 |
| folder_name | VARCHAR(100) | 是 | - | 收藏夹名称 |
| description | VARCHAR(500) | 否 | '' | 收藏夹描述 |
| folder_type | VARCHAR(20) | 否 | CUSTOM | 收藏夹类型：DEFAULT、PUBLIC、PRIVATE、CUSTOM |
| user_id | BIGINT | 是 | - | 用户ID |
| is_default | TINYINT | 否 | 0 | 是否为默认收藏夹：0-否，1-是 |
| cover_image | VARCHAR(500) | 否 | '' | 收藏夹封面图片 |
| sort_order | INT | 否 | 10 | 排序权重，数值越小越靠前 |
| **item_count** | INT | 否 | 0 | **收藏数量（冗余字段）** |
| create_time | DATETIME | 否 | NOW() | 创建时间 |
| update_time | DATETIME | 否 | NOW() | 更新时间 |
| is_deleted | TINYINT | 否 | 0 | 是否删除：0-否，1-是 |
| version | INT | 否 | 0 | 版本号（乐观锁） |

**核心约束**:
- `uk_user_folder_name(user_id, folder_name, is_deleted)`: 同一用户收藏夹名称唯一
- `uk_user_default_folder(user_id, is_default, is_deleted)`: 每用户只能有一个默认收藏夹

---

## 📡 接口列表

### 收藏管理接口

| 接口名称 | HTTP方法 | 路径 | 说明 |
|---------|----------|------|------|
| 收藏/取消收藏 | POST | `/api/v1/favorites` | 对目标进行收藏或取消收藏操作 |
| 批量收藏 | POST | `/api/v1/favorites/batch` | 批量收藏多个目标 |
| 移动收藏 | PUT | `/api/v1/favorites/{favoriteId}/move` | 将收藏移动到其他收藏夹 |
| 查询收藏列表 | GET | `/api/v1/favorites` | 分页查询用户收藏列表 |
| 查询收藏详情 | GET | `/api/v1/favorites/{favoriteId}` | 查询单个收藏的详细信息 |
| 检查收藏状态 | GET | `/api/v1/favorites/check` | 检查用户是否已收藏指定目标 |
| 统计用户收藏数量 | GET | `/api/v1/favorites/count/user/{userId}` | 统计用户收藏总数 |
| 统计目标被收藏数量 | GET | `/api/v1/favorites/count/target` | 统计目标被收藏总数 |

### 收藏夹管理接口

| 接口名称 | HTTP方法 | 路径 | 说明 |
|---------|----------|------|------|
| 创建收藏夹 | POST | `/api/v1/favorite-folders` | 创建新的收藏夹 |
| 更新收藏夹 | PUT | `/api/v1/favorite-folders/{folderId}` | 更新收藏夹信息 |
| 删除收藏夹 | DELETE | `/api/v1/favorite-folders/{folderId}` | 删除指定收藏夹 |
| 查询用户收藏夹列表 | GET | `/api/v1/favorite-folders/user/{userId}` | 查询用户的所有收藏夹 |
| 查询收藏夹详情 | GET | `/api/v1/favorite-folders/{folderId}` | 查询收藏夹详细信息 |
| 初始化默认收藏夹 | POST | `/api/v1/favorite-folders/init-default/{userId}` | 为用户初始化默认收藏夹 |

---

## 🔄 收藏管理接口

### 1. 收藏/取消收藏

**接口描述**: 对指定目标进行收藏或取消收藏操作，支持幂等性。

**请求信息**:
- **Method**: `POST`
- **Path**: `/api/v1/favorites`
- **Headers**: `Authorization: Bearer {token}`

**请求参数**:
```json
{
  "favoriteType": "CONTENT",           // 必填，收藏类型
  "targetId": 123456,                  // 必填，目标ID
  "userId": 789,                       // 必填，用户ID
  "isFavorite": true,                  // 必填，true-收藏，false-取消收藏
  "folderId": 1,                       // 可选，收藏夹ID，默认为1
  "remark": "很棒的内容"                // 可选，收藏备注
}
```

**响应示例**:
```json
{
  "success": true,
  "responseCode": "200",
  "responseMessage": "收藏成功",
  "favoriteId": 567890,
  "successIds": [123456],
  "successCount": 1,
  "failureCount": 0,
  "timestamp": 1703836800000,
  "traceId": "trace-12345"
}
```

### 2. 批量收藏

**接口描述**: 批量收藏多个目标，支持部分成功机制。

**请求信息**:
- **Method**: `POST`
- **Path**: `/api/v1/favorites/batch`
- **Headers**: `Authorization: Bearer {token}`

**请求参数**:
```json
{
  "favoriteType": "CONTENT",
  "targetIds": [123456, 123457, 123458],  // 必填，目标ID列表（最多100个）
  "userId": 789,
  "isFavorite": true,
  "folderId": 1,
  "remark": "批量收藏"
}
```

**响应示例**:
```json
{
  "success": true,
  "responseMessage": "批量操作完成：成功2个，失败1个",
  "successIds": [123456, 123457],
  "failureReasons": {
    "123458": "目标不存在"
  },
  "successCount": 2,
  "failureCount": 1
}
```

### 3. 移动收藏

**接口描述**: 将收藏移动到其他收藏夹。

**请求信息**:
- **Method**: `PUT`
- **Path**: `/api/v1/favorites/{favoriteId}/move`
- **Headers**: `Authorization: Bearer {token}`

**请求参数**:
```json
{
  "targetFolderId": 2,     // 必填，目标收藏夹ID
  "userId": 789            // 必填，用户ID
}
```

### 4. 查询收藏列表

**接口描述**: 分页查询用户收藏列表，支持按收藏夹、类型过滤。

**请求信息**:
- **Method**: `GET`
- **Path**: `/api/v1/favorites`
- **Headers**: `Authorization: Bearer {token}`

**查询参数**:
- `userId` (必填): 用户ID
- `favoriteType` (可选): 收藏类型
- `folderId` (可选): 收藏夹ID
- `status` (可选): 收藏状态
- `currentPage` (可选): 当前页码，默认1
- `pageSize` (可选): 每页大小，默认20

**响应示例**:
```json
{
  "success": true,
  "currentPage": 1,
  "pageSize": 20,
  "total": 156,
  "datas": [
    {
      "favoriteId": 567890,
      "favoriteType": "CONTENT",
      "targetId": 123456,
      "userId": 789,
      "folderId": 1,
      "status": "NORMAL",
      "remark": "很棒的内容",
      "favoriteTime": "2024-01-01T10:30:00",
      "targetTitle": "如何学习Java编程",
      "targetCover": "https://example.com/cover.jpg",
      "targetSummary": "这是一篇关于Java编程的详细教程...",
      "targetAuthorId": 456,
      "targetAuthorName": "技术大牛",
      "targetAuthorAvatar": "https://example.com/avatar.jpg",
      "targetPublishTime": "2024-01-01T09:00:00",
      "canUnfavorite": true,
      "canMove": true
    }
  ]
}
```

### 5. 检查收藏状态

**接口描述**: 检查用户是否已收藏指定目标。

**请求信息**:
- **Method**: `GET`
- **Path**: `/api/v1/favorites/check`
- **Headers**: `Authorization: Bearer {token}`

**查询参数**:
- `favoriteType` (必填): 收藏类型
- `targetId` (必填): 目标ID
- `userId` (必填): 用户ID

**响应示例**:
```json
{
  "success": true,
  "data": true,    // true-已收藏，false-未收藏
  "responseMessage": "查询成功"
}
```

### 6. 统计收藏数量

**接口描述**: 统计用户收藏数量或目标被收藏数量。

**用户收藏统计**:
- **Method**: `GET`
- **Path**: `/api/v1/favorites/count/user/{userId}`
- **查询参数**: `favoriteType` (可选)

**目标被收藏统计**:
- **Method**: `GET`
- **Path**: `/api/v1/favorites/count/target`
- **查询参数**: `favoriteType` (必填), `targetId` (必填)

**响应示例**:
```json
{
  "success": true,
  "data": 156,     // 收藏数量
  "responseMessage": "查询成功"
}
```

---

## 📁 收藏夹管理接口

### 1. 创建收藏夹

**接口描述**: 创建新的收藏夹，支持名称唯一性检查和数量限制。

**请求信息**:
- **Method**: `POST`
- **Path**: `/api/v1/favorite-folders`
- **Headers**: `Authorization: Bearer {token}`

**请求参数**:
```json
{
  "folderName": "我的学习资料",        // 必填，收藏夹名称（3-50字符）
  "description": "收集优质学习资料",    // 可选，收藏夹描述（最多500字符）
  "folderType": "PRIVATE",          // 必填，收藏夹类型
  "userId": 789,                    // 必填，用户ID
  "coverImage": "https://example.com/cover.jpg",  // 可选，封面图片
  "sortOrder": 10                   // 可选，排序权重，默认10
}
```

**响应示例**:
```json
{
  "success": true,
  "responseMessage": "创建收藏夹成功",
  "favoriteId": 234,    // 这里返回收藏夹ID
  "timestamp": 1703836800000
}
```

### 2. 更新收藏夹

**接口描述**: 更新收藏夹信息，默认收藏夹有特殊限制。

**请求信息**:
- **Method**: `PUT`
- **Path**: `/api/v1/favorite-folders/{folderId}`
- **Headers**: `Authorization: Bearer {token}`

**请求参数**:
```json
{
  "folderId": 234,                  // 必填，收藏夹ID
  "userId": 789,                    // 必填，用户ID
  "folderName": "优质学习资料",       // 可选，新的收藏夹名称
  "description": "更新后的描述",      // 可选，新的描述
  "folderType": "PUBLIC",           // 可选，新的类型（默认收藏夹不可修改）
  "coverImage": "https://example.com/new-cover.jpg",  // 可选，新的封面
  "sortOrder": 5                    // 可选，新的排序权重
}
```

### 3. 删除收藏夹

**接口描述**: 删除指定收藏夹，需要先清空收藏内容。

**请求信息**:
- **Method**: `DELETE`
- **Path**: `/api/v1/favorite-folders/{folderId}`
- **Headers**: `Authorization: Bearer {token}`

**查询参数**:
- `userId` (必填): 用户ID

**响应示例**:
```json
{
  "success": true,
  "responseMessage": "删除收藏夹成功",
  "favoriteId": 234
}
```

### 4. 查询用户收藏夹列表

**接口描述**: 查询用户的所有收藏夹，按排序权重排序。

**请求信息**:
- **Method**: `GET`
- **Path**: `/api/v1/favorite-folders/user/{userId}`
- **Headers**: `Authorization: Bearer {token}`

**响应示例**:
```json
{
  "success": true,
  "data": [
    {
      "folderId": 1,
      "folderName": "默认收藏夹",
      "description": "系统自动创建的默认收藏夹",
      "folderType": "DEFAULT",
      "userId": 789,
      "isDefault": true,
      "coverImage": "",
      "sortOrder": 0,
      "itemCount": 25,
      "createTime": "2024-01-01T09:00:00",
      "updateTime": "2024-01-01T15:30:00",
      "canEdit": false,       // 默认收藏夹不可编辑
      "canDelete": false      // 默认收藏夹不可删除
    },
    {
      "folderId": 234,
      "folderName": "优质学习资料",
      "description": "收集优质学习资料",
      "folderType": "PRIVATE",
      "userId": 789,
      "isDefault": false,
      "coverImage": "https://example.com/cover.jpg",
      "sortOrder": 5,
      "itemCount": 12,
      "createTime": "2024-01-01T10:00:00",
      "updateTime": "2024-01-01T16:00:00",
      "canEdit": true,
      "canDelete": true       // 非空收藏夹为false
    }
  ]
}
```

### 5. 查询收藏夹详情

**接口描述**: 查询收藏夹详细信息，支持权限控制。

**请求信息**:
- **Method**: `GET`
- **Path**: `/api/v1/favorite-folders/{folderId}`
- **Headers**: `Authorization: Bearer {token}`

**查询参数**:
- `userId` (可选): 用户ID，用于权限验证

---

## 📊 数据模型

### FavoriteRequest (收藏请求)

```json
{
  "favoriteType": "string",         // 收藏类型：CONTENT|USER|SOCIAL|COMMENT|TOPIC
  "targetId": 0,                    // 目标ID
  "targetIds": [0],                 // 批量目标ID列表（批量操作使用）
  "userId": 0,                      // 用户ID
  "isFavorite": true,               // 是否收藏：true-收藏，false-取消
  "folderId": 0,                    // 收藏夹ID，可选
  "remark": "string"                // 收藏备注，可选
}
```

### FavoriteResponse (收藏响应)

```json
{
  "success": true,                  // 操作是否成功
  "responseCode": "string",         // 响应码
  "responseMessage": "string",      // 响应消息
  "favoriteId": 0,                  // 收藏ID（单个操作）
  "successIds": [0],               // 成功的ID列表（批量操作）
  "failureReasons": {              // 失败原因（批量操作）
    "targetId": "reason"
  },
  "successCount": 0,               // 成功数量
  "failureCount": 0,               // 失败数量
  "timestamp": 0,                  // 时间戳
  "traceId": "string"              // 追踪ID
}
```

### FavoriteInfo (收藏信息)

```json
{
  "favoriteId": 0,                 // 收藏ID
  "favoriteType": "string",        // 收藏类型
  "targetId": 0,                   // 目标ID
  "userId": 0,                     // 用户ID
  "folderId": 0,                   // 收藏夹ID
  "status": "string",              // 收藏状态
  "remark": "string",              // 收藏备注
  "favoriteTime": "string",        // 收藏时间
  
  // === 目标信息（冗余字段） ===
  "targetTitle": "string",         // 目标标题
  "targetCover": "string",         // 目标封面
  "targetSummary": "string",       // 目标摘要
  "targetAuthorId": 0,            // 目标作者ID
  "targetAuthorName": "string",    // 目标作者名称
  "targetAuthorAvatar": "string",  // 目标作者头像
  "targetPublishTime": "string",   // 目标发布时间
  
  // === 权限信息 ===
  "canUnfavorite": true,          // 是否可取消收藏
  "canMove": true                 // 是否可移动到其他收藏夹
}
```

### FolderCreateRequest (创建收藏夹请求)

```json
{
  "folderName": "string",          // 收藏夹名称，3-50字符
  "description": "string",         // 收藏夹描述，最多500字符
  "folderType": "string",          // 收藏夹类型：CUSTOM|PUBLIC|PRIVATE
  "userId": 0,                     // 用户ID
  "coverImage": "string",          // 封面图片URL，可选
  "sortOrder": 0                   // 排序权重，可选，默认10
}
```

### FolderUpdateRequest (更新收藏夹请求)

```json
{
  "folderId": 0,                   // 收藏夹ID
  "userId": 0,                     // 用户ID
  "folderName": "string",          // 新的收藏夹名称，可选
  "description": "string",         // 新的描述，可选
  "folderType": "string",          // 新的类型，可选（默认收藏夹不可修改）
  "coverImage": "string",          // 新的封面图片，可选
  "sortOrder": 0                   // 新的排序权重，可选
}
```

### FolderInfo (收藏夹信息)

```json
{
  "folderId": 0,                   // 收藏夹ID
  "folderName": "string",          // 收藏夹名称
  "description": "string",         // 收藏夹描述
  "folderType": "string",          // 收藏夹类型
  "userId": 0,                     // 用户ID
  "isDefault": true,               // 是否为默认收藏夹
  "coverImage": "string",          // 封面图片
  "sortOrder": 0,                  // 排序权重
  "itemCount": 0,                  // 收藏数量
  "createTime": "string",          // 创建时间
  "updateTime": "string",          // 更新时间
  "canEdit": true,                 // 是否可编辑
  "canDelete": true                // 是否可删除
}
```

---

## ⚠️ 错误码定义

### 通用错误码

| 错误码 | HTTP状态码 | 错误信息 | 说明 |
|--------|-----------|----------|------|
| SYSTEM_ERROR | 500 | 系统异常 | 系统内部错误 |
| PARAM_ERROR | 400 | 参数错误 | 请求参数格式错误 |
| UNAUTHORIZED | 401 | 未授权 | 用户未登录或token无效 |
| FORBIDDEN | 403 | 权限不足 | 用户无权限进行此操作 |

### 收藏相关错误码

| 错误码 | HTTP状态码 | 错误信息 | 说明 |
|--------|-----------|----------|------|
| FAVORITE_ERROR | 400 | 收藏操作失败 | 收藏操作过程中出现错误 |
| UNFAVORITE_FAILED | 400 | 取消收藏失败 | 取消收藏操作失败 |
| FAVORITE_NOT_FOUND | 404 | 收藏不存在或无权限 | 收藏记录不存在或用户无权限 |
| TARGET_NOT_FOUND | 404 | 目标不存在 | 要收藏的目标不存在 |
| ALREADY_FAVORITED | 409 | 已经收藏过了 | 重复收藏同一目标 |
| NOT_FAVORITED | 400 | 尚未收藏 | 取消收藏时目标未被收藏 |
| BATCH_FAVORITE_ERROR | 400 | 批量收藏操作失败 | 批量操作过程中出现错误 |
| TOO_MANY_TARGETS | 400 | 单次批量收藏不能超过100个目标 | 批量操作数量超限 |
| EMPTY_TARGETS | 400 | 目标ID列表不能为空 | 批量操作缺少目标 |
| INVALID_PARAMS | 400 | 批量收藏需要提供目标ID列表或单个目标ID | 批量操作参数错误 |
| MOVE_ERROR | 400 | 移动收藏失败 | 移动收藏到其他收藏夹失败 |

### 收藏夹相关错误码

| 错误码 | HTTP状态码 | 错误信息 | 说明 |
|--------|-----------|----------|------|
| FOLDER_NOT_FOUND | 404 | 收藏夹不存在 | 收藏夹不存在或已删除 |
| FOLDER_NAME_EXISTS | 409 | 收藏夹名称已存在 | 同一用户下收藏夹名称重复 |
| FOLDER_LIMIT_EXCEEDED | 400 | 收藏夹数量已达上限（20个） | 用户收藏夹数量超限 |
| DEFAULT_FOLDER_IMMUTABLE | 400 | 默认收藏夹类型不可修改 | 尝试修改默认收藏夹的类型 |
| DEFAULT_FOLDER_UNDELETABLE | 400 | 默认收藏夹不可删除 | 尝试删除默认收藏夹 |
| FOLDER_NOT_EMPTY | 400 | 请先清空收藏夹中的内容 | 删除非空收藏夹 |
| NO_PERMISSION | 403 | 无权限操作此收藏夹 | 用户无权限操作他人的收藏夹 |
| CREATE_FOLDER_ERROR | 400 | 创建收藏夹失败 | 创建收藏夹过程中出现错误 |
| UPDATE_FOLDER_ERROR | 400 | 更新收藏夹失败 | 更新收藏夹过程中出现错误 |
| DELETE_FOLDER_ERROR | 400 | 删除收藏夹失败 | 删除收藏夹过程中出现错误 |

---

## 🚀 使用示例

### 1. 完整收藏流程示例

```javascript
// 1. 用户登录并获取token
const token = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...";

// 2. 收藏一篇文章
const favoriteArticle = async () => {
  const response = await fetch('/api/v1/favorites', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': token
    },
    body: JSON.stringify({
      favoriteType: 'CONTENT',
      targetId: 123456,
      userId: 789,
      isFavorite: true,
      folderId: 1,
      remark: '很有用的技术文章'
    })
  });
  
  const result = await response.json();
  console.log('收藏结果:', result);
  // 输出: { success: true, responseMessage: "收藏成功", favoriteId: 567890 }
};

// 3. 创建专门的学习收藏夹
const createStudyFolder = async () => {
  const response = await fetch('/api/v1/favorite-folders', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': token
    },
    body: JSON.stringify({
      folderName: '编程学习',
      description: '收集优质编程教程和资料',
      folderType: 'PRIVATE',
      userId: 789,
      sortOrder: 5
    })
  });
  
  const result = await response.json();
  console.log('创建收藏夹结果:', result);
  return result.favoriteId; // 返回新创建的收藏夹ID
};

// 4. 批量收藏多篇技术文章到学习收藏夹
const batchFavoriteArticles = async (folderId) => {
  const response = await fetch('/api/v1/favorites/batch', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': token
    },
    body: JSON.stringify({
      favoriteType: 'CONTENT',
      targetIds: [123456, 123457, 123458, 123459, 123460],
      userId: 789,
      isFavorite: true,
      folderId: folderId,
      remark: '批量收藏的编程教程'
    })
  });
  
  const result = await response.json();
  console.log('批量收藏结果:', result);
  // 输出可能的部分成功结果:
  // {
  //   success: true,
  //   responseMessage: "批量操作完成：成功4个，失败1个",
  //   successIds: [123456, 123457, 123458, 123459],
  //   failureReasons: { "123460": "目标不存在" },
  //   successCount: 4,
  //   failureCount: 1
  // }
};

// 5. 查询用户的收藏列表
const getFavoriteList = async (folderId) => {
  const response = await fetch(
    `/api/v1/favorites?userId=789&folderId=${folderId}&currentPage=1&pageSize=10`, 
    {
      headers: { 'Authorization': token }
    }
  );
  
  const result = await response.json();
  console.log('收藏列表:', result);
  // 输出包含冗余字段的完整收藏信息，无需额外查询目标详情
};
```

### 2. 收藏状态检查和统计

```javascript
// 检查是否已收藏某篇文章
const checkFavoriteStatus = async () => {
  const response = await fetch(
    '/api/v1/favorites/check?favoriteType=CONTENT&targetId=123456&userId=789',
    { headers: { 'Authorization': token } }
  );
  
  const result = await response.json();
  if (result.data) {
    console.log('已收藏这篇文章');
  } else {
    console.log('尚未收藏这篇文章');
  }
};

// 统计用户收藏的文章数量
const getUserContentFavoriteCount = async () => {
  const response = await fetch(
    '/api/v1/favorites/count/user/789?favoriteType=CONTENT',
    { headers: { 'Authorization': token } }
  );
  
  const result = await response.json();
  console.log(`用户共收藏了 ${result.data} 篇文章`);
};

// 统计某篇文章被收藏的次数
const getArticleFavoriteCount = async () => {
  const response = await fetch(
    '/api/v1/favorites/count/target?favoriteType=CONTENT&targetId=123456',
    { headers: { 'Authorization': token } }
  );
  
  const result = await response.json();
  console.log(`这篇文章被收藏了 ${result.data} 次`);
};
```

### 3. 收藏夹管理操作

```javascript
// 查询用户的所有收藏夹
const getUserFolders = async () => {
  const response = await fetch('/api/v1/favorite-folders/user/789', {
    headers: { 'Authorization': token }
  });
  
  const result = await response.json();
  console.log('用户收藏夹列表:', result.data);
  
  // 展示每个收藏夹的信息
  result.data.forEach(folder => {
    console.log(`收藏夹: ${folder.folderName}, 包含 ${folder.itemCount} 个收藏`);
  });
};

// 移动收藏到不同的收藏夹
const moveFavoriteToFolder = async (favoriteId, targetFolderId) => {
  const response = await fetch(`/api/v1/favorites/${favoriteId}/move`, {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': token
    },
    body: JSON.stringify({
      targetFolderId: targetFolderId,
      userId: 789
    })
  });
  
  const result = await response.json();
  console.log('移动收藏结果:', result);
};

// 更新收藏夹信息
const updateFolder = async (folderId) => {
  const response = await fetch(`/api/v1/favorite-folders/${folderId}`, {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': token
    },
    body: JSON.stringify({
      folderId: folderId,
      userId: 789,
      folderName: '高级编程技术',
      description: '收集高级编程技术和最佳实践',
      coverImage: 'https://example.com/new-cover.jpg'
    })
  });
  
  const result = await response.json();
  console.log('更新收藏夹结果:', result);
};
```

### 4. 错误处理最佳实践

```javascript
const handleFavoriteOperation = async (favoriteData) => {
  try {
    const response = await fetch('/api/v1/favorites', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': token
      },
      body: JSON.stringify(favoriteData)
    });
    
    const result = await response.json();
    
    if (result.success) {
      console.log('收藏成功:', result.responseMessage);
      return result;
    } else {
      // 根据错误码进行不同处理
      switch (result.responseCode) {
        case 'ALREADY_FAVORITED':
          console.log('已经收藏过了，无需重复操作');
          break;
        case 'TARGET_NOT_FOUND':
          console.error('要收藏的内容不存在');
          break;
        case 'FOLDER_NOT_FOUND':
          console.error('指定的收藏夹不存在');
          break;
        case 'UNAUTHORIZED':
          console.error('用户未登录，请先登录');
          // 跳转到登录页面
          break;
        default:
          console.error('收藏失败:', result.responseMessage);
      }
      throw new Error(result.responseMessage);
    }
  } catch (error) {
    console.error('网络请求失败:', error);
    throw error;
  }
};

// 使用示例
handleFavoriteOperation({
  favoriteType: 'CONTENT',
  targetId: 123456,
  userId: 789,
  isFavorite: true,
  folderId: 2
}).then(result => {
  console.log('操作完成:', result);
}).catch(error => {
  console.error('操作失败:', error.message);
});
```

---

## 📈 性能说明

### 去连表化设计优势

1. **查询性能提升 10-15倍**: 
   - 传统设计需要 JOIN 3-4张表
   - 去连表化设计单表查询，包含所有展示信息

2. **冗余字段策略**:
   - 存储目标标题、封面、作者等常用展示信息
   - 通过 RocketMQ 异步同步保证数据一致性

3. **索引优化**:
   - 核心查询路径都有对应索引
   - 支持千万级数据量的高效查询

### 并发安全保障

1. **数据库层面**: 唯一约束确保幂等性
2. **应用层面**: DuplicateKeyException 处理
3. **分布式锁**: Redis 锁防止重复处理消息

---

*该文档最后更新时间: 2024-01-01*  
*API版本: v1.0*  
*联系人: Collide 开发团队* 