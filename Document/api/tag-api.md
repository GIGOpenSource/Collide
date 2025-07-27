# Tag 模块 API 接口文档

> **⚠️ 重要说明**: Tag 模块采用强制去连表设计（No-Join Design），所有查询均基于单表操作。
> API 响应中不包含任何连表查询的冗余字段（如 categoryName 等），如需关联数据请通过单独的 API 调用获取。

## 📋 目录
- [模块概述](#模块概述)
- [数据库设计](#数据库设计)
- [接口列表](#接口列表)
- [数据模型](#数据模型)
- [错误码定义](#错误码定义)
- [使用示例](#使用示例)

---

## 📚 模块概述

Tag 模块是 Collide 社交平台的标签管理系统，提供标签的创建、管理和用户兴趣标签的个性化推荐功能，支持内容标签化、用户兴趣画像构建和智能内容推荐。

### 主要功能
- **标签管理**: 创建、更新、删除、查询标签信息
- **标签分类**: 支持内容标签、兴趣标签、系统标签三大类型
- **热度计算**: 基于使用频次的标签热度算法
- **用户兴趣**: 用户兴趣标签管理和兴趣画像构建
- **智能推荐**: 基于标签的内容推荐和用户匹配
- **标签搜索**: 支持标签名称的模糊搜索
- **视觉定制**: 支持标签颜色和图标的自定义设置

### 支持的标签类型
- **CONTENT**: 内容标签，用于内容分类和标记
- **INTEREST**: 兴趣标签，用于用户兴趣管理
- **SYSTEM**: 系统标签，平台预设的功能性标签

### 技术特色
- **去连表设计**: 强制采用单表查询，避免复杂的连表操作
- **高性能查询**: 基于索引优化的快速单表查询
- **热度算法**: 基于使用次数和时间衰减的热度计算
- **兴趣画像**: 用户多维度兴趣偏好建模
- **智能推荐**: 协同过滤和内容匹配算法
- **缓存优化**: 热门标签和用户兴趣的缓存策略

---

## 🗄️ 数据库设计

### 标签表 (t_tag)

| 字段名 | 类型 | 是否必填 | 默认值 | 说明 |
|--------|------|----------|--------|------|
| id | BIGINT | 是 | AUTO_INCREMENT | 标签ID，主键 |
| name | VARCHAR(100) | 是 | - | 标签名称 |
| description | TEXT | 否 | - | 标签描述 |
| color | VARCHAR(20) | 否 | #1890ff | 标签颜色（十六进制） |
| icon_url | VARCHAR(500) | 否 | - | 标签图标URL |
| tag_type | ENUM | 是 | content | 标签类型：content、interest、system |
| category_id | BIGINT | 否 | - | 所属分类ID（仅存储ID，不做连表查询） |
| usage_count | BIGINT | 是 | 0 | 使用次数 |
| heat_score | DECIMAL(10,2) | 是 | 0.00 | 热度分数 |
| status | ENUM | 是 | active | 标签状态：active、inactive |
| create_time | TIMESTAMP | 是 | CURRENT_TIMESTAMP | 创建时间 |
| update_time | TIMESTAMP | 是 | CURRENT_TIMESTAMP | 更新时间 |

### 用户兴趣标签表 (t_user_interest_tag)

| 字段名 | 类型 | 是否必填 | 默认值 | 说明 |
|--------|------|----------|--------|------|
| id | BIGINT | 是 | AUTO_INCREMENT | 记录ID，主键 |
| user_id | BIGINT | 是 | - | 用户ID（仅存储ID，不做连表查询） |
| tag_id | BIGINT | 是 | - | 标签ID（仅存储ID，不做连表查询） |
| interest_score | DECIMAL(5,2) | 是 | 0.00 | 兴趣分数（0-100） |
| interest_source | VARCHAR(50) | 是 | - | 兴趣来源：manual、behavior、system |
| create_time | TIMESTAMP | 是 | CURRENT_TIMESTAMP | 创建时间 |
| update_time | TIMESTAMP | 是 | CURRENT_TIMESTAMP | 更新时间 |

### 索引设计
- **标签表索引**:
  - `PRIMARY KEY (id)`
  - `UNIQUE KEY uk_name_type (name, tag_type)`
  - `KEY idx_tag_type (tag_type)`
  - `KEY idx_category_id (category_id)`
  - `KEY idx_usage_count (usage_count)`
  - `KEY idx_heat_score (heat_score)`
- **用户兴趣标签表索引**:
  - `PRIMARY KEY (id)`
  - `UNIQUE KEY uk_user_tag (user_id, tag_id)`
  - `KEY idx_user_id (user_id)`
  - `KEY idx_tag_id (tag_id)`
  - `KEY idx_interest_score (interest_score)`

---

## 🔗 接口列表

### 标签管理

| 功能 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 创建标签 | POST | `/api/v1/tags` | 创建新的标签 |
| 更新标签 | PUT | `/api/v1/tags/{tagId}` | 更新指定标签信息 |
| 删除标签 | DELETE | `/api/v1/tags/{tagId}` | 删除指定标签 |
| 查询标签详情 | GET | `/api/v1/tags/{tagId}` | 根据ID查询标签详情 |

### 标签查询与搜索

| 功能 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 分页查询标签 | GET | `/api/v1/tags` | 分页查询标签列表 |
| 按类型查询标签 | GET | `/api/v1/tags/type/{tagType}` | 查询指定类型的标签 |
| 获取热门标签 | GET | `/api/v1/tags/hot` | 获取热门标签列表 |
| 搜索标签 | GET | `/api/v1/tags/search` | 根据关键词搜索标签 |

### 用户兴趣标签

| 功能 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 获取用户兴趣标签 | GET | `/api/v1/tags/interest/{userId}` | 获取用户的兴趣标签 |
| 设置用户兴趣标签 | POST | `/api/v1/tags/interest/{userId}` | 批量设置用户兴趣标签 |
| 添加用户兴趣标签 | POST | `/api/v1/tags/interest/{userId}/{tagId}` | 添加单个用户兴趣标签 |
| 移除用户兴趣标签 | DELETE | `/api/v1/tags/interest/{userId}/{tagId}` | 移除用户兴趣标签 |

---

## 📊 接口详情

### 1. 创建标签

**接口地址**: `POST /api/v1/tags`

**请求参数**:
```json
{
  "name": "人工智能",                           // 标签名称，必填
  "description": "人工智能相关的内容和技术",    // 标签描述，可选
  "color": "#FF6B6B",                          // 标签颜色，可选，默认#1890ff
  "iconUrl": "https://example.com/ai-icon.png", // 标签图标URL，可选
  "tagType": "content",                        // 标签类型，必填：content/interest/system
  "categoryId": 123,                           // 所属分类ID，可选
  "sort": 1,                                   // 排序值，可选，默认0
  "status": 1                                  // 状态：0-禁用，1-启用，可选，默认1
}
```

**响应结果**:
```json
{
  "code": 200,
  "message": "创建成功",
  "data": 12345,                               // 新创建的标签ID
  "timestamp": 1705312200000,
  "traceId": "trace-123456"
}
```

### 2. 更新标签

**接口地址**: `PUT /api/v1/tags/{tagId}`

**路径参数**:
- `tagId`: 标签ID

**请求参数**:
```json
{
  "name": "人工智能AI",                        // 更新的标签名称，可选
  "description": "AI人工智能技术与应用",       // 更新的描述，可选
  "color": "#4ECDC4",                          // 更新的颜色，可选
  "iconUrl": "https://example.com/new-ai-icon.png", // 更新的图标，可选
  "categoryId": 456,                           // 更新的分类ID，可选
  "sort": 2,                                   // 更新的排序值，可选
  "status": 1                                  // 更新的状态，可选
}
```

**响应结果**:
```json
{
  "code": 200,
  "message": "更新成功",
  "data": null,
  "timestamp": 1705314000000,
  "traceId": "trace-123456"
}
```

### 3. 删除标签

**接口地址**: `DELETE /api/v1/tags/{tagId}`

**路径参数**:
- `tagId`: 标签ID

**响应结果**:
```json
{
  "code": 200,
  "message": "删除成功",
  "data": null,
  "timestamp": 1705314000000,
  "traceId": "trace-123456"
}
```

### 4. 查询标签详情

**接口地址**: `GET /api/v1/tags/{tagId}`

**路径参数**:
- `tagId`: 标签ID

**响应结果**:
```json
{
  "code": 200,
  "message": "查询成功",
  "data": {
    "id": 12345,
    "tagId": 12345,
    "name": "人工智能",
    "description": "人工智能相关的内容和技术",
    "color": "#FF6B6B",
    "iconUrl": "https://example.com/ai-icon.png",
    "tagType": "content",
    "categoryId": 123,
    "usageCount": 1500,
    "heatScore": 85.5,
    "status": "active",
    "createTime": "2024-01-15T10:30:00",
    "updateTime": "2024-01-15T10:30:00"
  },
  "timestamp": 1705312200000,
  "traceId": "trace-123456"
}
```

### 5. 分页查询标签

**接口地址**: `GET /api/v1/tags`

**查询参数**:
- `pageNo`: 页码，默认1
- `pageSize`: 每页大小，默认20，最大100
- `tagType`: 标签类型，可选
- `categoryId`: 分类ID，可选
- `status`: 状态，可选（active/inactive）
- `keyword`: 关键词搜索，可选
- `sortBy`: 排序字段，默认createTime
- `sortOrder`: 排序方向，默认DESC

**响应结果**:
```json
{
  "code": 200,
  "message": "查询成功",
  "data": {
    "records": [
      {
        "id": 1,
        "tagId": 1,
        "name": "人工智能",
        "description": "AI相关技术",
        "color": "#FF6B6B",
        "iconUrl": "https://example.com/ai-icon.png",
        "tagType": "content",
        "categoryId": 123,
        "categoryName": "科技",
        "usageCount": 1500,
        "heatScore": 85.5,
        "status": "active",
        "createTime": "2024-01-15T10:30:00",
        "updateTime": "2024-01-15T10:30:00"
      }
    ],
    "total": 150,
    "pageNo": 1,
    "pageSize": 20,
    "totalPages": 8,
    "hasNext": true,
    "hasPrev": false
  },
  "timestamp": 1705312200000,
  "traceId": "trace-123456"
}
```

### 6. 按类型查询标签

**接口地址**: `GET /api/v1/tags/type/{tagType}`

**路径参数**:
- `tagType`: 标签类型（content/interest/system）

**查询参数**:
- `limit`: 返回数量限制，默认50，最大200
- `sortBy`: 排序字段，默认heatScore
- `sortOrder`: 排序方向，默认DESC

**响应结果**:
```json
{
  "code": 200,
  "message": "查询成功",
  "data": [
    {
      "id": 1,
      "tagId": 1,
      "name": "人工智能",
      "description": "AI相关技术",
      "color": "#FF6B6B",
      "iconUrl": "https://example.com/ai-icon.png",
      "tagType": "content",
      "categoryId": 123,
      "categoryName": "科技",
      "usageCount": 1500,
      "heatScore": 85.5,
      "status": "active",
      "createTime": "2024-01-15T10:30:00",
      "updateTime": "2024-01-15T10:30:00"
    }
  ],
  "timestamp": 1705312200000,
  "traceId": "trace-123456"
}
```

### 7. 获取热门标签

**接口地址**: `GET /api/v1/tags/hot`

**查询参数**:
- `tagType`: 标签类型，可选
- `limit`: 返回数量限制，默认20，最大100
- `timeRange`: 时间范围，可选值：7d、30d、90d，默认30d

**响应结果**:
```json
{
  "code": 200,
  "message": "查询成功",
  "data": [
    {
      "id": 1,
      "tagId": 1,
      "name": "人工智能",
      "description": "AI相关技术",
      "color": "#FF6B6B",
      "iconUrl": "https://example.com/ai-icon.png",
      "tagType": "content",
      "categoryId": 123,
      "categoryName": "科技",
      "usageCount": 1500,
      "heatScore": 98.5,
      "status": "active",
      "createTime": "2024-01-15T10:30:00",
      "updateTime": "2024-01-15T10:30:00"
    },
    {
      "id": 2,
      "tagId": 2,
      "name": "机器学习",
      "description": "ML机器学习技术",
      "color": "#4ECDC4",
      "iconUrl": "https://example.com/ml-icon.png",
      "tagType": "content",
      "categoryId": 123,
      "categoryName": "科技",
      "usageCount": 1200,
      "heatScore": 92.3,
      "status": "active",
      "createTime": "2024-01-15T09:30:00",
      "updateTime": "2024-01-15T09:30:00"
    }
  ],
  "timestamp": 1705312200000,
  "traceId": "trace-123456"
}
```

### 8. 搜索标签

**接口地址**: `GET /api/v1/tags/search`

**查询参数**:
- `keyword`: 搜索关键词，必填
- `tagType`: 标签类型，可选
- `limit`: 返回数量限制，默认20，最大100

**响应结果**:
```json
{
  "code": 200,
  "message": "查询成功",
  "data": [
    {
      "id": 1,
      "tagId": 1,
      "name": "人工智能",
      "description": "AI相关技术",
      "color": "#FF6B6B",
      "iconUrl": "https://example.com/ai-icon.png",
      "tagType": "content",
      "categoryId": 123,
      "categoryName": "科技",
      "usageCount": 1500,
      "heatScore": 85.5,
      "status": "active",
      "createTime": "2024-01-15T10:30:00",
      "updateTime": "2024-01-15T10:30:00"
    }
  ],
  "timestamp": 1705312200000,
  "traceId": "trace-123456"
}
```

### 9. 获取用户兴趣标签

**接口地址**: `GET /api/v1/tags/interest/{userId}`

**路径参数**:
- `userId`: 用户ID

**查询参数**:
- `limit`: 返回数量限制，默认50，最大200
- `minScore`: 兴趣分数下限，默认0
- `sortBy`: 排序字段，默认interestScore
- `sortOrder`: 排序方向，默认DESC

**响应结果**:
```json
{
  "code": 200,
  "message": "查询成功",
  "data": [
    {
      "id": 1,
      "tagId": 1,
      "name": "人工智能",
      "description": "AI相关技术",
      "color": "#FF6B6B",
      "iconUrl": "https://example.com/ai-icon.png",
      "tagType": "interest",
      "categoryId": 123,
      "categoryName": "科技",
      "usageCount": 1500,
      "heatScore": 85.5,
      "status": "active",
      "interestScore": 95.5,
      "interestSource": "behavior",
      "createTime": "2024-01-15T10:30:00",
      "updateTime": "2024-01-15T10:30:00"
    },
    {
      "id": 2,
      "tagId": 2,
      "name": "摄影",
      "description": "摄影技术与艺术",
      "color": "#45B7D1",
      "iconUrl": "https://example.com/photo-icon.png",
      "tagType": "interest",
      "categoryId": 456,
      "categoryName": "艺术",
      "usageCount": 800,
      "heatScore": 75.2,
      "status": "active",
      "interestScore": 88.0,
      "interestSource": "manual",
      "createTime": "2024-01-15T09:30:00",
      "updateTime": "2024-01-15T09:30:00"
    }
  ],
  "timestamp": 1705312200000,
  "traceId": "trace-123456"
}
```

### 10. 设置用户兴趣标签

**接口地址**: `POST /api/v1/tags/interest/{userId}`

**路径参数**:
- `userId`: 用户ID

**请求参数**:
```json
{
  "tagIds": [1, 2, 3, 4, 5],                   // 标签ID列表，必填
  "interestScore": 80.0,                        // 默认兴趣分数，可选，默认50.0
  "interestSource": "manual",                   // 兴趣来源，可选，默认manual
  "replaceAll": true                           // 是否替换所有兴趣标签，可选，默认false
}
```

**响应结果**:
```json
{
  "code": 200,
  "message": "设置成功",
  "data": {
    "userId": 123456,
    "totalTags": 5,
    "successCount": 5,
    "failureCount": 0,
    "results": [
      {
        "tagId": 1,
        "success": true,
        "message": "设置成功"
      },
      {
        "tagId": 2,
        "success": true,
        "message": "设置成功"
      }
    ]
  },
  "timestamp": 1705312200000,
  "traceId": "trace-123456"
}
```

### 11. 添加用户兴趣标签

**接口地址**: `POST /api/v1/tags/interest/{userId}/{tagId}`

**路径参数**:
- `userId`: 用户ID
- `tagId`: 标签ID

**请求参数**:
```json
{
  "interestScore": 85.0,                       // 兴趣分数，可选，默认50.0
  "interestSource": "behavior"                 // 兴趣来源，可选，默认manual
}
```

**响应结果**:
```json
{
  "code": 200,
  "message": "添加成功",
  "data": {
    "userId": 123456,
    "tagId": 1,
    "interestScore": 85.0,
    "interestSource": "behavior",
    "createTime": "2024-01-15T10:30:00Z"
  },
  "timestamp": 1705312200000,
  "traceId": "trace-123456"
}
```

### 12. 移除用户兴趣标签

**接口地址**: `DELETE /api/v1/tags/interest/{userId}/{tagId}`

**路径参数**:
- `userId`: 用户ID
- `tagId`: 标签ID

**响应结果**:
```json
{
  "code": 200,
  "message": "移除成功",
  "data": null,
  "timestamp": 1705314000000,
  "traceId": "trace-123456"
}
```

---

## 📋 数据模型

### TagInfo - 标签信息

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | Long | 标签ID |
| tagId | Long | 标签ID（兼容性字段） |
| name | String | 标签名称 |
| description | String | 标签描述 |
| color | String | 标签颜色（十六进制） |
| iconUrl | String | 标签图标URL |
| tagType | String | 标签类型 |
| categoryId | Long | 所属分类ID（仅存储ID，不做连表查询） |
| usageCount | Long | 使用次数 |
| heatScore | BigDecimal | 热度分数 |
| status | String | 标签状态 |
| interestScore | BigDecimal | 用户兴趣分数（用户兴趣标签专用） |
| interestSource | String | 兴趣来源（用户兴趣标签专用） |
| createTime | LocalDateTime | 创建时间 |
| updateTime | LocalDateTime | 更新时间 |

### TagCreateRequest - 创建标签请求

| 字段名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| name | String | 是 | 标签名称，最大100字符 |
| description | String | 否 | 标签描述 |
| color | String | 否 | 标签颜色，十六进制格式，默认#1890ff |
| iconUrl | String | 否 | 标签图标URL |
| tagType | String | 是 | 标签类型：content/interest/system |
| categoryId | Long | 否 | 所属分类ID |
| sort | Integer | 否 | 排序值，默认0 |
| status | Integer | 否 | 状态：0-禁用，1-启用，默认1 |

### TagUpdateRequest - 更新标签请求

| 字段名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| tagId | Long | 是 | 标签ID |
| name | String | 否 | 标签名称 |
| description | String | 否 | 标签描述 |
| color | String | 否 | 标签颜色 |
| iconUrl | String | 否 | 标签图标URL |
| categoryId | Long | 否 | 所属分类ID |
| sort | Integer | 否 | 排序值 |
| status | Integer | 否 | 状态 |

### UserInterestTagRequest - 用户兴趣标签请求

| 字段名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| tagIds | List<Long> | 是 | 标签ID列表 |
| interestScore | Double | 否 | 默认兴趣分数，默认50.0 |
| interestSource | String | 否 | 兴趣来源：manual/behavior/system |
| replaceAll | Boolean | 否 | 是否替换所有兴趣标签，默认false |

---

## ❌ 错误码定义

### 标签相关错误码

| 错误码 | HTTP状态码 | 错误信息 | 说明 |
|--------|------------|----------|------|
| TAG_001 | 400 | 标签名称不能为空 | 创建或更新标签时名称为空 |
| TAG_002 | 400 | 标签名称长度不能超过100个字符 | 标签名称过长 |
| TAG_003 | 400 | 标签类型无效 | 标签类型不在支持范围内 |
| TAG_004 | 409 | 该类型下标签名称已存在 | 同类型下存在重名标签 |
| TAG_005 | 404 | 标签不存在 | 指定的标签ID不存在 |
| TAG_006 | 400 | 标签颜色格式错误 | 颜色值不是有效的十六进制格式 |
| TAG_007 | 400 | 无法删除正在使用的标签 | 尝试删除有内容关联的标签 |
| TAG_008 | 400 | 分类不存在 | 指定的分类ID不存在 |
| TAG_009 | 400 | 系统标签不允许删除 | 尝试删除系统预设标签 |
| TAG_010 | 400 | 兴趣分数必须在0-100之间 | 兴趣分数超出有效范围 |

### 用户兴趣标签相关错误码

| 错误码 | HTTP状态码 | 错误信息 | 说明 |
|--------|------------|----------|------|
| INTEREST_001 | 400 | 兴趣标签列表不能为空 | 设置兴趣标签时列表为空 |
| INTEREST_002 | 400 | 兴趣标签数量不能超过50个 | 兴趣标签过多 |
| INTEREST_003 | 404 | 用户兴趣标签不存在 | 指定的用户兴趣标签关系不存在 |
| INTEREST_004 | 400 | 兴趣来源无效 | 兴趣来源不在支持范围内 |
| INTEREST_005 | 409 | 用户兴趣标签已存在 | 重复添加相同的兴趣标签 |

### 通用错误码

| 错误码 | HTTP状态码 | 错误信息 | 说明 |
|--------|------------|----------|------|
| PARAM_INVALID | 400 | 参数无效 | 请求参数格式错误 |
| USER_NOT_FOUND | 404 | 用户不存在 | 指定用户ID不存在 |
| RESOURCE_NOT_FOUND | 404 | 资源不存在 | 请求的资源不存在 |
| DUPLICATED | 409 | 资源重复 | 资源已存在 |
| SYSTEM_ERROR | 500 | 系统内部错误 | 服务器内部异常 |

---

## 🔧 使用示例

### 1. 创建内容标签

```bash
curl -X POST "http://localhost:8080/api/v1/tags" \
  -H "Authorization: Bearer {token}" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "人工智能",
    "description": "AI人工智能相关技术和应用",
    "color": "#FF6B6B",
    "iconUrl": "https://example.com/ai-icon.png",
    "tagType": "content",
    "categoryId": 1,
    "status": 1
  }'
```

### 2. 创建兴趣标签

```bash
curl -X POST "http://localhost:8080/api/v1/tags" \
  -H "Authorization: Bearer {token}" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "摄影",
    "description": "摄影技术与艺术欣赏",
    "color": "#45B7D1",
    "tagType": "interest",
    "status": 1
  }'
```

### 3. 查询热门标签

```bash
curl -X GET "http://localhost:8080/api/v1/tags/hot?tagType=content&limit=20&timeRange=30d" \
  -H "Authorization: Bearer {token}"
```

### 4. 搜索标签

```bash
curl -X GET "http://localhost:8080/api/v1/tags/search?keyword=AI&tagType=content&limit=10" \
  -H "Authorization: Bearer {token}"
```

### 5. 获取用户兴趣标签

```bash
curl -X GET "http://localhost:8080/api/v1/tags/interest/123456?limit=50&minScore=60" \
  -H "Authorization: Bearer {token}"
```

### 6. 设置用户兴趣标签

```bash
curl -X POST "http://localhost:8080/api/v1/tags/interest/123456" \
  -H "Authorization: Bearer {token}" \
  -H "Content-Type: application/json" \
  -d '{
    "tagIds": [1, 2, 3, 4, 5],
    "interestScore": 80.0,
    "interestSource": "manual",
    "replaceAll": false
  }'
```

### 7. 添加单个用户兴趣标签

```bash
curl -X POST "http://localhost:8080/api/v1/tags/interest/123456/1" \
  -H "Authorization: Bearer {token}" \
  -H "Content-Type: application/json" \
  -d '{
    "interestScore": 90.0,
    "interestSource": "behavior"
  }'
```

### 8. 按类型查询标签

```bash
curl -X GET "http://localhost:8080/api/v1/tags/type/interest?limit=50&sortBy=heatScore&sortOrder=DESC" \
  -H "Authorization: Bearer {token}"
```

---

## 📝 注意事项

1. **标签唯一性**: 同一类型下的标签名称必须唯一，不同类型可以重名
2. **标签类型**: content用于内容标记，interest用于用户画像，system为系统预设
3. **热度计算**: 基于使用次数、时间衰减和用户互动的综合算法
4. **兴趣分数**: 范围0-100，表示用户对该标签的兴趣程度
5. **兴趣来源**: manual-用户手动设置，behavior-行为分析，system-系统推荐
6. **删除限制**: 正在被内容使用的标签无法直接删除，需要先清理关联
7. **系统标签**: 系统预设的功能性标签不允许删除或修改核心属性
8. **缓存策略**: 热门标签缓存30分钟，用户兴趣标签缓存1小时
9. **推荐算法**: 基于协同过滤和内容匹配的混合推荐算法
10. **标签上限**: 单个用户最多设置50个兴趣标签，防止画像过于分散

---

**文档版本**: 1.0.0  
**最后更新**: 2024-01-15  
**维护团队**: Collide Backend Team