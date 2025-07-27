# Category 模块 API 接口文档

## 📋 目录
- [模块概述](#模块概述)
- [数据库设计](#数据库设计)
- [接口列表](#接口列表)
- [数据模型](#数据模型)
- [错误码定义](#错误码定义)
- [使用示例](#使用示例)

---

## 📚 模块概述

Category 模块是 Collide 社交平台的内容分类管理系统，采用**严格去连表化设计**，为平台内容提供层级化的分类组织结构，支持多级分类、分类统计、排序管理等核心功能。

### 核心设计原则
- **严格去连表化**: 所有必要信息冗余存储在单表内，避免复杂联表查询
- **高性能查询**: 基于单表查询，响应速度快，扩展性好
- **数据一致性**: 通过应用层控制数据一致性，避免数据库层面复杂约束
- **缓存友好**: 单表数据结构便于缓存设计和管理

### 主要功能
- **层级分类管理**: 支持最多5层的树形分类结构
- **分类CRUD操作**: 创建、查询、更新、删除分类
- **分类树结构**: 获取完整的分类树或指定节点的子分类
- **分类统计**: 实时统计每个分类下的内容数量
- **热门分类**: 基于内容数量的热门分类推荐
- **分类搜索**: 支持分类名称模糊搜索
- **排序管理**: 支持自定义分类排序顺序
- **幂等性操作**: 支持幂等性更新和删除操作

### 支持的分类层级
- **1级分类**: 根分类（如：技术、生活、娱乐）
- **2级分类**: 二级分类（如：Java、Python、前端开发）
- **3级分类**: 三级分类（如：Spring Boot、Vue.js、React）
- **4-5级分类**: 更细粒度的分类细分

---

## 🗄️ 数据库设计

### 分类表 (t_category) - 严格去连表化设计

| 字段名 | 类型 | 是否必填 | 默认值 | 说明 |
|--------|------|----------|--------|------|
| id | BIGINT UNSIGNED | 是 | AUTO_INCREMENT | 分类ID，主键 |
| parent_id | BIGINT UNSIGNED | 否 | 0 | 父分类ID，0表示根分类 |
| name | VARCHAR(50) | 是 | - | 分类名称 |
| description | VARCHAR(500) | 否 | - | 分类描述 |
| icon_url | VARCHAR(255) | 否 | - | 分类图标URL |
| cover_url | VARCHAR(255) | 否 | - | 分类封面URL |
| sort_order | INT UNSIGNED | 否 | 0 | 排序顺序，数值越小越靠前 |
| level | TINYINT UNSIGNED | 是 | 1 | 分类层级（1-5） |
| path | VARCHAR(500) | 是 | '/' | 分类路径，用/分隔 |
| content_count | BIGINT UNSIGNED | 是 | 0 | 该分类下的内容数量 |
| status | VARCHAR(20) | 是 | 'ACTIVE' | 分类状态：ACTIVE、INACTIVE |
| version | INT UNSIGNED | 是 | 1 | 版本号（乐观锁） |
| creator_id | BIGINT UNSIGNED | 否 | - | 创建者ID |
| creator_name | VARCHAR(50) | 否 | - | 创建者名称（冗余存储） |
| last_modifier_id | BIGINT UNSIGNED | 否 | - | 最后修改者ID |
| last_modifier_name | VARCHAR(50) | 否 | - | 最后修改者名称（冗余存储） |
| create_time | TIMESTAMP | 是 | CURRENT_TIMESTAMP | 创建时间 |
| update_time | TIMESTAMP | 是 | CURRENT_TIMESTAMP | 更新时间 |

### 索引设计
- **主键索引**: `PRIMARY KEY (id)`
- **唯一索引**: `UNIQUE KEY uk_parent_name (parent_id, name)`
- **父分类索引**: `KEY idx_parent_id (parent_id)`
- **状态索引**: `KEY idx_status (status)`
- **层级索引**: `KEY idx_level (level)`
- **内容数量索引**: `KEY idx_content_count (content_count)`
- **创建者索引**: `KEY idx_creator_id (creator_id)`
- **排序索引**: `KEY idx_sort_order (sort_order, create_time)`
- **时间索引**: `KEY idx_create_time (create_time)`, `KEY idx_update_time (update_time)`

---

## 🔗 接口列表

### 基础分类管理

| 功能 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 创建分类 | POST | `/api/v1/categories` | 创建新的分类 |
| 更新分类 | PUT | `/api/v1/categories/{categoryId}` | 更新指定分类信息 |
| 删除分类 | DELETE | `/api/v1/categories/{categoryId}` | 删除指定分类 |
| 查询分类详情 | GET | `/api/v1/categories/{categoryId}` | 根据ID查询分类详情 |

### 分类查询与搜索

| 功能 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 分页查询分类 | GET | `/api/v1/categories` | 分页查询分类列表 |
| 获取分类树 | GET | `/api/v1/categories/tree` | 获取完整分类树结构 |
| 获取子分类树 | GET | `/api/v1/categories/tree?parentId={parentId}` | 获取指定父分类的子分类树 |
| 获取热门分类 | GET | `/api/v1/categories/hot` | 获取热门分类列表 |
| 搜索分类 | GET | `/api/v1/categories/search` | 根据关键词搜索分类 |

---

## 📊 接口详情

### 1. 创建分类

**接口地址**: `POST /api/v1/categories`

**请求参数**:
```json
{
  "parentId": 0,                    // 父分类ID，可选，默认0（根分类）
  "name": "技术",                   // 分类名称，必填，最大50字符
  "description": "技术相关内容分类", // 分类描述，可选，最大500字符
  "iconUrl": "https://example.com/tech-icon.png", // 图标URL，可选
  "coverUrl": "https://example.com/tech-cover.jpg", // 封面URL，可选
  "sortOrder": 1,                   // 排序顺序，可选，默认0
  "sort": 1,                        // 排序值（兼容性字段），可选
  "status": 1,                      // 状态：0-禁用，1-启用，可选
  "creatorId": 1001,               // 创建者ID，可选
  "creatorName": "admin"           // 创建者名称，可选（冗余存储）
}
```

**响应结果**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": 12345,                    // 新创建的分类ID
  "timestamp": 1640995200000,
  "traceId": "trace-123456"
}
```

### 2. 更新分类

**接口地址**: `PUT /api/v1/categories/{categoryId}`

**路径参数**:
- `categoryId`: 分类ID

**请求参数**:
```json
{
  "name": "技术开发",               // 分类名称，可选
  "description": "技术开发相关内容", // 分类描述，可选
  "iconUrl": "https://example.com/new-tech-icon.png", // 图标URL，可选
  "coverUrl": "https://example.com/new-tech-cover.jpg", // 封面URL，可选
  "sortOrder": 2,                   // 排序顺序，可选
  "sort": 2,                        // 排序值（兼容性字段），可选
  "status": "ACTIVE",              // 状态，可选：ACTIVE、INACTIVE
  "lastModifierId": 1002,          // 修改者ID，可选
  "lastModifierName": "editor"     // 修改者名称，可选（冗余存储）
}
```

**响应结果**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": null,
  "timestamp": 1640995200000,
  "traceId": "trace-123456"
}
```

### 3. 删除分类

**接口地址**: `DELETE /api/v1/categories/{categoryId}`

**路径参数**:
- `categoryId`: 分类ID

**响应结果**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": null,
  "timestamp": 1640995200000,
  "traceId": "trace-123456"
}
```

### 4. 查询分类详情

**接口地址**: `GET /api/v1/categories/{categoryId}`

**路径参数**:
- `categoryId`: 分类ID

**响应结果**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 12345,                    // 分类ID（兼容性字段）
    "categoryId": 12345,            // 分类ID（主字段）
    "parentId": 0,
    "name": "技术",
    "description": "技术相关内容分类",
    "iconUrl": "https://example.com/tech-icon.png",
    "coverUrl": "https://example.com/tech-cover.jpg",
    "sortOrder": 1,
    "sort": 1,                      // 排序值（兼容性字段）
    "level": 1,
    "path": "/技术",
    "contentCount": 1250,
    "status": "ACTIVE",             // 分类状态（字符串类型）
    "statusInt": 1,                 // 分类状态（整数类型，兼容性字段）
    "version": 1,
    "creatorId": 1001,
    "creatorName": "admin",
    "lastModifierId": 1002,
    "lastModifierName": "editor",
    "createTime": "2024-01-01T10:00:00",
    "updateTime": "2024-01-01T12:00:00"
  },
  "timestamp": 1640995200000,
  "traceId": "trace-123456"
}
```

### 5. 分页查询分类

**接口地址**: `GET /api/v1/categories`

**查询参数**:
- `pageNo`: 页码，默认1
- `pageSize`: 每页大小，默认20，最大100
- `parentId`: 父分类ID，可选
- `level`: 分类层级，可选
- `status`: 分类状态，可选（ACTIVE/INACTIVE）
- `name`: 分类名称关键词，可选
- `creatorId`: 创建者ID，可选

**响应结果**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "records": [
      {
        "id": 1,
        "categoryId": 1,
        "parentId": 0,
        "name": "技术",
        "description": "技术相关内容分类",
        "iconUrl": "https://example.com/tech-icon.png",
        "coverUrl": "https://example.com/tech-cover.jpg",
        "sortOrder": 1,
        "sort": 1,
        "level": 1,
        "path": "/技术",
        "contentCount": 5000,
        "status": "ACTIVE",
        "statusInt": 1,
        "version": 1,
        "creatorId": 1001,
        "creatorName": "admin",
        "lastModifierId": null,
        "lastModifierName": null,
        "createTime": "2024-01-01T10:00:00",
        "updateTime": "2024-01-01T10:00:00"
      }
    ],
    "total": 100,
    "pageNo": 1,
    "pageSize": 20,
    "totalPages": 5
  },
  "timestamp": 1640995200000,
  "traceId": "trace-123456"
}
```

### 6. 获取分类树

**接口地址**: `GET /api/v1/categories/tree`

**查询参数**:
- `parentId`: 父分类ID，可选，默认null（获取完整树）

**响应结果**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "categoryId": 1,
      "parentId": 0,
      "name": "技术",
      "description": "技术相关内容分类",
      "iconUrl": "https://example.com/tech-icon.png",
      "coverUrl": "https://example.com/tech-cover.jpg",
      "sortOrder": 1,
      "level": 1,
      "path": "/技术",
      "contentCount": 5000,
      "status": "ACTIVE",
      "createTime": "2024-01-01T10:00:00",
      "updateTime": "2024-01-01T10:00:00",
      "children": [
        {
          "categoryId": 10,
          "parentId": 1,
          "name": "Java",
          "description": "Java编程语言相关",
          "iconUrl": "https://example.com/java-icon.png",
          "coverUrl": "https://example.com/java-cover.jpg",
          "sortOrder": 1,
          "level": 2,
          "path": "/技术/Java",
          "contentCount": 2000,
          "status": "ACTIVE",
          "createTime": "2024-01-01T10:05:00",
          "updateTime": "2024-01-01T10:05:00",
          "children": [
            {
              "categoryId": 100,
              "parentId": 10,
              "name": "Spring Boot",
              "description": "Spring Boot框架相关",
              "iconUrl": "https://example.com/springboot-icon.png",
              "coverUrl": "https://example.com/springboot-cover.jpg",
              "sortOrder": 1,
              "level": 3,
              "path": "/技术/Java/Spring Boot",
              "contentCount": 800,
              "status": "ACTIVE",
              "createTime": "2024-01-01T10:10:00",
              "updateTime": "2024-01-01T10:10:00",
              "children": []
            }
          ]
        }
      ]
    }
  ],
  "timestamp": 1640995200000,
  "traceId": "trace-123456"
}
```

### 7. 获取热门分类

**接口地址**: `GET /api/v1/categories/hot`

**查询参数**:
- `limit`: 返回数量限制，默认10，最大50

**响应结果**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "id": 1,
      "categoryId": 1,
      "parentId": 0,
      "name": "技术",
      "description": "技术相关内容分类",
      "iconUrl": "https://example.com/tech-icon.png",
      "coverUrl": "https://example.com/tech-cover.jpg",
      "sortOrder": 1,
      "sort": 1,
      "level": 1,
      "path": "/技术",
      "contentCount": 5000,
      "status": "ACTIVE",
      "statusInt": 1,
      "version": 1,
      "creatorId": 1001,
      "creatorName": "admin",
      "createTime": "2024-01-01T10:00:00",
      "updateTime": "2024-01-01T10:00:00"
    }
  ],
  "timestamp": 1640995200000,
  "traceId": "trace-123456"
}
```

### 8. 搜索分类

**接口地址**: `GET /api/v1/categories/search`

**查询参数**:
- `keyword`: 搜索关键词，必填
- `limit`: 返回数量限制，默认20，最大100

**响应结果**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "id": 10,
      "categoryId": 10,
      "parentId": 1,
      "name": "Java",
      "description": "Java编程语言相关",
      "iconUrl": "https://example.com/java-icon.png",
      "coverUrl": "https://example.com/java-cover.jpg",
      "sortOrder": 1,
      "sort": 1,
      "level": 2,
      "path": "/技术/Java",
      "contentCount": 2000,
      "status": "ACTIVE",
      "statusInt": 1,
      "version": 1,
      "creatorId": 1001,
      "creatorName": "admin",
      "createTime": "2024-01-01T10:05:00",
      "updateTime": "2024-01-01T10:05:00"
    }
  ],
  "timestamp": 1640995200000,
  "traceId": "trace-123456"
}
```

---

## 📋 数据模型

### CategoryInfo - 分类信息（严格去连表化）

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | Long | 分类ID（兼容性字段） |
| categoryId | Long | 分类ID（主字段） |
| parentId | Long | 父分类ID |
| name | String | 分类名称 |
| description | String | 分类描述 |
| iconUrl | String | 分类图标URL |
| coverUrl | String | 分类封面URL |
| sortOrder | Integer | 排序顺序 |
| sort | Integer | 排序值（兼容性字段） |
| level | Integer | 分类层级 |
| path | String | 分类路径 |
| contentCount | Long | 内容数量 |
| status | String | 分类状态（字符串类型） |
| statusInt | Integer | 分类状态（整数类型，兼容性字段） |
| version | Integer | 版本号（乐观锁） |
| creatorId | Long | 创建者ID |
| creatorName | String | 创建者名称（冗余存储） |
| lastModifierId | Long | 最后修改者ID |
| lastModifierName | String | 最后修改者名称（冗余存储） |
| createTime | LocalDateTime | 创建时间 |
| updateTime | LocalDateTime | 更新时间 |

### CategoryTree - 分类树节点

继承 CategoryInfo 的所有字段，额外包含：

| 字段名 | 类型 | 说明 |
|--------|------|------|
| children | List<CategoryTree> | 子分类列表 |

### CategoryCreateRequest - 创建分类请求

| 字段名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| parentId | Long | 否 | 父分类ID，默认0 |
| name | String | 是 | 分类名称，最大50字符 |
| description | String | 否 | 分类描述，最大500字符 |
| iconUrl | String | 否 | 分类图标URL |
| coverUrl | String | 否 | 分类封面URL |
| sortOrder | Integer | 否 | 排序顺序，默认0 |
| sort | Integer | 否 | 排序值（兼容性字段） |
| status | Integer | 否 | 状态：0-禁用，1-启用 |
| creatorId | Long | 否 | 创建者ID |
| creatorName | String | 否 | 创建者名称（冗余存储） |

### CategoryUpdateRequest - 更新分类请求

| 字段名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| categoryId | Long | 是 | 分类ID |
| name | String | 否 | 分类名称 |
| description | String | 否 | 分类描述 |
| iconUrl | String | 否 | 分类图标URL |
| coverUrl | String | 否 | 分类封面URL |
| sortOrder | Integer | 否 | 排序顺序 |
| sort | Integer | 否 | 排序值（兼容性字段） |
| status | String | 否 | 状态：ACTIVE、INACTIVE |
| lastModifierId | Long | 否 | 修改者ID |
| lastModifierName | String | 否 | 修改者名称（冗余存储） |

### CategoryQueryRequest - 分类查询请求

| 字段名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| pageNo | Integer | 否 | 页码，默认1 |
| pageSize | Integer | 否 | 每页大小，默认20 |
| parentId | Long | 否 | 父分类ID |
| level | Integer | 否 | 分类层级 |
| status | String | 否 | 分类状态 |
| name | String | 否 | 分类名称关键词 |
| creatorId | Long | 否 | 创建者ID |

---

## ❌ 错误码定义

### 分类相关错误码

| 错误码 | HTTP状态码 | 错误信息 | 说明 |
|--------|------------|----------|------|
| CATEGORY_NAME_EMPTY | 400 | 分类名称不能为空 | 创建或更新分类时名称为空 |
| CATEGORY_NAME_TOO_LONG | 400 | 分类名称不能超过50个字符 | 分类名称过长 |
| CATEGORY_DESCRIPTION_TOO_LONG | 400 | 分类描述不能超过500个字符 | 分类描述过长 |
| CATEGORY_PARENT_NOT_EXISTS | 400 | 父分类不存在 | 指定的父分类ID不存在 |
| CATEGORY_LEVEL_EXCEED | 400 | 分类层级不能超过5层 | 创建分类时层级超过限制 |
| CATEGORY_NAME_EXISTS | 409 | 该层级下分类名称已存在 | 同一父分类下存在重名分类 |
| CATEGORY_NOT_FOUND | 404 | 分类不存在 | 指定的分类ID不存在 |
| CATEGORY_HAS_CHILDREN | 400 | 无法删除包含子分类的分类 | 尝试删除有子分类的分类 |
| CATEGORY_HAS_CONTENT | 400 | 无法删除包含内容的分类 | 尝试删除有内容的分类 |
| CATEGORY_CIRCULAR_REFERENCE | 400 | 不能将分类移动到自己的子分类下 | 更新分类时形成循环引用 |
| CATEGORY_STATUS_INVALID | 400 | 分类状态无效 | 状态值不在有效范围内 |
| CATEGORY_UPDATE_CONFLICT | 409 | 分类更新冲突，请重试 | 并发更新导致版本冲突 |

### 通用错误码

| 错误码 | HTTP状态码 | 错误信息 | 说明 |
|--------|------------|----------|------|
| INVALID_PARAMETER | 400 | 参数无效 | 请求参数格式错误 |
| RESOURCE_NOT_FOUND | 404 | 资源不存在 | 请求的资源不存在 |
| DUPLICATED | 409 | 资源重复 | 资源已存在 |
| SYSTEM_ERROR | 500 | 系统内部错误 | 服务器内部异常 |

---

## 🔧 使用示例

### 1. 创建一级分类（技术）

```bash
curl -X POST http://localhost:9505/api/v1/categories \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {token}" \
  -d '{
    "parentId": 0,
    "name": "技术",
    "description": "技术相关内容分类",
    "iconUrl": "https://example.com/tech-icon.png",
    "sortOrder": 1,
    "sort": 1,
    "status": 1,
    "creatorId": 1001,
    "creatorName": "admin"
  }'
```

### 2. 创建二级分类（Java）

```bash
curl -X POST http://localhost:9505/api/v1/categories \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {token}" \
  -d '{
    "parentId": 1,
    "name": "Java",
    "description": "Java编程语言相关",
    "iconUrl": "https://example.com/java-icon.png",
    "sortOrder": 1,
    "sort": 1,
    "creatorId": 1001,
    "creatorName": "admin"
  }'
```

### 3. 更新分类

```bash
curl -X PUT http://localhost:9505/api/v1/categories/1 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {token}" \
  -d '{
    "name": "技术开发",
    "description": "技术开发相关内容",
    "sortOrder": 2,
    "sort": 2,
    "status": "ACTIVE",
    "lastModifierId": 1002,
    "lastModifierName": "editor"
  }'
```

### 4. 获取完整分类树

```bash
curl -X GET "http://localhost:9505/api/v1/categories/tree" \
  -H "Authorization: Bearer {token}"
```

### 5. 搜索分类

```bash
curl -X GET "http://localhost:9505/api/v1/categories/search?keyword=Java&limit=10" \
  -H "Authorization: Bearer {token}"
```

### 6. 获取热门分类

```bash
curl -X GET "http://localhost:9505/api/v1/categories/hot?limit=10" \
  -H "Authorization: Bearer {token}"
```

### 7. 分页查询分类

```bash
curl -X GET "http://localhost:9505/api/v1/categories?pageNo=1&pageSize=20&parentId=1&status=ACTIVE" \
  -H "Authorization: Bearer {token}"
```

---

## 📝 注意事项

### 设计原则
1. **严格去连表化**: 所有查询基于单表，避免JOIN操作
2. **冗余存储**: 创建者、修改者等信息冗余存储，避免关联查询
3. **应用层控制**: 数据一致性通过业务逻辑层控制
4. **乐观锁机制**: 使用version字段实现并发控制和幂等性
5. **兼容性设计**: 提供多种字段格式以支持不同客户端需求

### 业务规则
1. **层级限制**: 分类最多支持5层，超过此限制的创建请求会被拒绝
2. **名称唯一性**: 同一父分类下的子分类名称必须唯一
3. **删除限制**: 包含子分类或内容的分类无法直接删除，需要先清理子分类和内容
4. **状态管理**: 禁用父分类会影响其下所有子分类的可见性
5. **排序规则**: sortOrder 数值越小排序越靠前，相同数值按创建时间排序
6. **路径格式**: 分类路径以/分隔，格式为 `/parent1/parent2/parent3`

### 性能优化
1. **单表查询**: 所有查询都是单表操作，性能优异
2. **索引优化**: 基于查询场景设计了完整的索引结构
3. **缓存友好**: 单表数据结构便于Redis缓存
4. **分页限制**: 分页查询最大pageSize为100，避免大批量数据查询

### 数据一致性
1. **冗余数据同步**: 当用户信息变更时，需要同步更新分类表中的冗余字段
2. **内容数量统计**: content_count字段需要在内容发布/删除时异步更新
3. **幂等性保证**: 通过version字段和应用层逻辑保证操作幂等性

### 兼容性说明
1. **双字段支持**: `id`/`categoryId`、`sort`/`sortOrder`、`status`/`statusInt` 等双字段设计保证新老客户端兼容
2. **类型转换**: 状态字段支持字符串和整数两种类型，自动转换
3. **可选字段**: 兼容性字段均为可选，不影响核心功能使用

---

**文档版本**: 2.1.0 - 完整兼容性版本  
**最后更新**: 2024-12-28  
**维护团队**: Collide Backend Team  
**设计理念**: No JOIN, High Performance, Simple & Fast, Full Compatibility