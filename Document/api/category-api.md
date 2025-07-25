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

Category 模块是 Collide 社交平台的内容分类管理系统，为平台内容提供层级化的分类组织结构，支持多级分类、分类统计、排序管理等核心功能。

### 主要功能
- **层级分类管理**: 支持最多5层的树形分类结构
- **分类CRUD操作**: 创建、查询、更新、删除分类
- **分类树结构**: 获取完整的分类树或指定节点的子分类
- **分类统计**: 实时统计每个分类下的内容数量
- **热门分类**: 基于内容数量的热门分类推荐
- **分类搜索**: 支持分类名称模糊搜索
- **排序管理**: 支持自定义分类排序顺序

### 支持的分类层级
- **1级分类**: 根分类（如：小说、漫画、视频）
- **2级分类**: 二级分类（如：现代言情、古代言情）
- **3级分类**: 三级分类（如：都市现代、校园青春）
- **4-5级分类**: 更细粒度的分类细分

---

## 🗄️ 数据库设计

### 分类表 (t_category)

| 字段名 | 类型 | 是否必填 | 默认值 | 说明 |
|--------|------|----------|--------|------|
| id | BIGINT | 是 | AUTO_INCREMENT | 分类ID，主键 |
| parent_id | BIGINT | 否 | 0 | 父分类ID，0表示根分类 |
| name | VARCHAR(100) | 是 | - | 分类名称 |
| description | TEXT | 否 | - | 分类描述 |
| icon_url | VARCHAR(500) | 否 | - | 分类图标URL |
| cover_url | VARCHAR(500) | 否 | - | 分类封面URL |
| sort_order | INT | 否 | 0 | 排序顺序，数值越小越靠前 |
| level | TINYINT | 是 | 1 | 分类层级（1-5） |
| path | VARCHAR(500) | 是 | - | 分类路径，用/分隔 |
| content_count | BIGINT | 是 | 0 | 该分类下的内容数量 |
| status | ENUM | 是 | active | 分类状态：active、inactive |
| create_time | TIMESTAMP | 是 | CURRENT_TIMESTAMP | 创建时间 |
| update_time | TIMESTAMP | 是 | CURRENT_TIMESTAMP | 更新时间 |

### 索引设计
- **主键索引**: `PRIMARY KEY (id)`
- **父分类索引**: `KEY idx_parent_id (parent_id)`
- **排序索引**: `KEY idx_sort_order (sort_order)`
- **层级索引**: `KEY idx_level (level)`
- **状态索引**: `KEY idx_status (status)`
- **内容数量索引**: `KEY idx_content_count (content_count)`

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
| 获取子分类树 | GET | `/api/v1/categories/tree/{parentId}` | 获取指定父分类的子分类树 |
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
  "name": "现代言情",                // 分类名称，必填
  "description": "现代背景的言情小说", // 分类描述，可选
  "iconUrl": "https://example.com/icon.png", // 图标URL，可选
  "coverUrl": "https://example.com/cover.jpg", // 封面URL，可选
  "sortOrder": 1,                   // 排序顺序，可选，默认0
  "status": 1                       // 状态：0-禁用，1-启用，可选，默认1
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
  "name": "现代都市言情",             // 分类名称，可选
  "description": "现代都市背景的言情小说", // 分类描述，可选
  "iconUrl": "https://example.com/new-icon.png", // 图标URL，可选
  "coverUrl": "https://example.com/new-cover.jpg", // 封面URL，可选
  "sortOrder": 2,                   // 排序顺序，可选
  "status": 1                       // 状态，可选
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
    "id": 12345,
    "categoryId": 12345,
    "parentId": 0,
    "name": "现代言情",
    "description": "现代背景的言情小说",
    "iconUrl": "https://example.com/icon.png",
    "coverUrl": "https://example.com/cover.jpg",
    "sortOrder": 1,
    "level": 2,
    "path": "/0/1",
    "contentCount": 1250,
    "status": "active",
    "createTime": "2024-01-01T10:00:00",
    "updateTime": "2024-01-01T10:00:00"
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
- `status`: 分类状态，可选（active/inactive）
- `keyword`: 关键词搜索，可选

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
        "name": "小说",
        "description": "各类小说作品",
        "iconUrl": "https://example.com/novel-icon.png",
        "coverUrl": "https://example.com/novel-cover.jpg",
        "sortOrder": 1,
        "level": 1,
        "path": "/0",
        "contentCount": 5000,
        "status": "active",
        "createTime": "2024-01-01T10:00:00",
        "updateTime": "2024-01-01T10:00:00"
      }
    ],
    "total": 100,
    "pageNo": 1,
    "pageSize": 20,
    "totalPages": 5,
    "hasNext": true,
    "hasPrev": false
  },
  "timestamp": 1640995200000,
  "traceId": "trace-123456"
}
```

### 6. 获取分类树

**接口地址**: `GET /api/v1/categories/tree`

**查询参数**:
- `parentId`: 父分类ID，可选，默认0（获取完整树）

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
      "name": "小说",
      "description": "各类小说作品",
      "iconUrl": "https://example.com/novel-icon.png",
      "coverUrl": "https://example.com/novel-cover.jpg",
      "sortOrder": 1,
      "level": 1,
      "path": "/0",
      "contentCount": 5000,
      "status": "active",
      "children": [
        {
          "id": 2,
          "categoryId": 2,
          "parentId": 1,
          "name": "言情小说",
          "description": "言情类小说",
          "iconUrl": "https://example.com/romance-icon.png",
          "coverUrl": "https://example.com/romance-cover.jpg",
          "sortOrder": 1,
          "level": 2,
          "path": "/0/1",
          "contentCount": 2000,
          "status": "active",
          "children": [
            {
              "id": 3,
              "categoryId": 3,
              "parentId": 2,
              "name": "现代言情",
              "description": "现代背景的言情小说",
              "iconUrl": "https://example.com/modern-romance-icon.png",
              "coverUrl": "https://example.com/modern-romance-cover.jpg",
              "sortOrder": 1,
              "level": 3,
              "path": "/0/1/2",
              "contentCount": 1200,
              "status": "active",
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
      "name": "小说",
      "description": "各类小说作品",
      "iconUrl": "https://example.com/novel-icon.png",
      "coverUrl": "https://example.com/novel-cover.jpg",
      "sortOrder": 1,
      "level": 1,
      "path": "/0",
      "contentCount": 5000,
      "status": "active",
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
      "id": 2,
      "categoryId": 2,
      "parentId": 1,
      "name": "言情小说",
      "description": "言情类小说",
      "iconUrl": "https://example.com/romance-icon.png",
      "coverUrl": "https://example.com/romance-cover.jpg",
      "sortOrder": 1,
      "level": 2,
      "path": "/0/1",
      "contentCount": 2000,
      "status": "active",
      "createTime": "2024-01-01T10:00:00",
      "updateTime": "2024-01-01T10:00:00"
    }
  ],
  "timestamp": 1640995200000,
  "traceId": "trace-123456"
}
```

---

## 📋 数据模型

### CategoryInfo - 分类信息

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | Long | 分类ID |
| categoryId | Long | 分类ID（兼容性字段） |
| parentId | Long | 父分类ID |
| name | String | 分类名称 |
| description | String | 分类描述 |
| iconUrl | String | 分类图标URL |
| coverUrl | String | 分类封面URL |
| sortOrder | Integer | 排序顺序 |
| level | Integer | 分类层级 |
| path | String | 分类路径 |
| contentCount | Long | 内容数量 |
| status | String | 分类状态 |
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
| name | String | 是 | 分类名称 |
| description | String | 否 | 分类描述 |
| iconUrl | String | 否 | 分类图标URL |
| coverUrl | String | 否 | 分类封面URL |
| sortOrder | Integer | 否 | 排序顺序，默认0 |
| status | Integer | 否 | 状态：0-禁用，1-启用 |

### CategoryUpdateRequest - 更新分类请求

| 字段名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| categoryId | Long | 是 | 分类ID |
| name | String | 否 | 分类名称 |
| description | String | 否 | 分类描述 |
| iconUrl | String | 否 | 分类图标URL |
| coverUrl | String | 否 | 分类封面URL |
| sortOrder | Integer | 否 | 排序顺序 |
| status | Integer | 否 | 状态：0-禁用，1-启用 |

---

## ❌ 错误码定义

### 分类相关错误码

| 错误码 | HTTP状态码 | 错误信息 | 说明 |
|--------|------------|----------|------|
| CATEGORY_001 | 400 | 分类名称不能为空 | 创建或更新分类时名称为空 |
| CATEGORY_002 | 400 | 分类名称长度不能超过100个字符 | 分类名称过长 |
| CATEGORY_003 | 400 | 父分类不存在 | 指定的父分类ID不存在 |
| CATEGORY_004 | 400 | 分类层级不能超过5层 | 创建分类时层级超过限制 |
| CATEGORY_005 | 409 | 该层级下分类名称已存在 | 同一父分类下存在重名分类 |
| CATEGORY_006 | 404 | 分类不存在 | 指定的分类ID不存在 |
| CATEGORY_007 | 400 | 无法删除包含子分类的分类 | 尝试删除有子分类的分类 |
| CATEGORY_008 | 400 | 无法删除包含内容的分类 | 尝试删除有内容的分类 |
| CATEGORY_009 | 400 | 不能将分类移动到自己的子分类下 | 更新分类时形成循环引用 |
| CATEGORY_010 | 400 | 分类状态无效 | 状态值不在有效范围内 |

### 通用错误码

| 错误码 | HTTP状态码 | 错误信息 | 说明 |
|--------|------------|----------|------|
| PARAM_INVALID | 400 | 参数无效 | 请求参数格式错误 |
| RESOURCE_NOT_FOUND | 404 | 资源不存在 | 请求的资源不存在 |
| DUPLICATED | 409 | 资源重复 | 资源已存在 |
| SYSTEM_ERROR | 500 | 系统内部错误 | 服务器内部异常 |

---

## 🔧 使用示例

### 1. 创建一级分类（小说）

```bash
curl -X POST http://localhost:8080/api/v1/categories \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {token}" \
  -d '{
    "parentId": 0,
    "name": "小说",
    "description": "各类小说作品",
    "iconUrl": "https://example.com/novel-icon.png",
    "sortOrder": 1,
    "status": 1
  }'
```

### 2. 创建二级分类（言情小说）

```bash
curl -X POST http://localhost:8080/api/v1/categories \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {token}" \
  -d '{
    "parentId": 1,
    "name": "言情小说",
    "description": "言情类小说",
    "iconUrl": "https://example.com/romance-icon.png",
    "sortOrder": 1,
    "status": 1
  }'
```

### 3. 获取完整分类树

```bash
curl -X GET "http://localhost:8080/api/v1/categories/tree" \
  -H "Authorization: Bearer {token}"
```

### 4. 搜索分类

```bash
curl -X GET "http://localhost:8080/api/v1/categories/search?keyword=言情&limit=10" \
  -H "Authorization: Bearer {token}"
```

### 5. 获取热门分类

```bash
curl -X GET "http://localhost:8080/api/v1/categories/hot?limit=10" \
  -H "Authorization: Bearer {token}"
```

---

## 📝 注意事项

1. **层级限制**: 分类最多支持5层，超过此限制的创建请求会被拒绝
2. **名称唯一性**: 同一父分类下的子分类名称必须唯一
3. **删除限制**: 包含子分类或内容的分类无法直接删除，需要先清理子分类和内容
4. **状态管理**: 禁用父分类会影响其下所有子分类的可见性
5. **排序规则**: sortOrder 数值越小排序越靠前，相同数值按创建时间排序
6. **路径格式**: 分类路径以/分隔，格式为 `/parent1/parent2/parent3`
7. **统计更新**: 内容数量统计会在内容发布/删除时异步更新

---

**文档版本**: 1.0.0  
**最后更新**: 2024-01-15  
**维护团队**: Collide Backend Team