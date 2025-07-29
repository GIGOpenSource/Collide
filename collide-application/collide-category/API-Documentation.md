# Collide 分类服务 API 文档

## 概述

Collide 分类服务提供完整的分类管理功能，包括分类的创建、查询、更新、删除，支持层级分类管理，分类统计，以及内容分类关联等核心功能。

**服务版本**: v2.0  
**基础路径**: `/api/v1/categories`  
**设计理念**: 统一分类管理，支持无限级分类层次，提供高效的分类查询和管理

---

## 分类基础功能 API

### 1. 创建分类
**接口路径**: `POST /api/v1/categories`  
**接口描述**: 创建新的分类，支持设置父分类建立层级关系

#### 请求参数
```json
{
  "name": "Java编程",                // 必填，分类名称（1-50字符）
  "description": "Java编程语言相关内容", // 可选，分类描述
  "parentId": 1001,                 // 可选，父分类ID（创建子分类时需要）
  "iconUrl": "https://example.com/icons/java.png", // 可选，分类图标URL
  "coverUrl": "https://example.com/covers/java.jpg", // 可选，分类封面URL
  "sort": 10,                       // 可选，排序值（默认0）
  "status": "active",               // 可选，状态（active/inactive，默认active）
  "operatorId": 12345               // 必填，操作人ID
}
```

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "responseCode": "SUCCESS",
  "responseMessage": "创建成功",
  "data": {
    "id": 12345,
    "name": "Java编程",
    "description": "Java编程语言相关内容",
    "parentId": 1001,
    "parentName": "编程语言",
    "level": 2,
    "path": "1001,12345",
    "iconUrl": "https://example.com/icons/java.png",
    "coverUrl": "https://example.com/covers/java.jpg",
    "sort": 10,
    "status": "active",
    "contentCount": 0,
    "createTime": "2024-01-16T10:30:00",
    "updateTime": "2024-01-16T10:30:00",
    "operatorId": 12345
  }
}
```

**失败响应 (400)**:
```json
{
  "success": false,
  "responseCode": "CATEGORY_NAME_EXISTS",
  "responseMessage": "分类名称已存在"
}
```

---

### 2. 更新分类
**接口路径**: `PUT /api/v1/categories/{categoryId}`  
**接口描述**: 更新分类信息，支持部分字段更新

#### 路径参数
- **categoryId** (path): 分类ID，必填

#### 请求参数
```json
{
  "name": "Java高级编程",             // 可选，分类名称
  "description": "Java高级编程技术",   // 可选，分类描述
  "iconUrl": "https://example.com/icons/java-advanced.png", // 可选，分类图标URL
  "sort": 15,                       // 可选，排序值
  "status": "active",               // 可选，状态
  "operatorId": 12345               // 必填，操作人ID
}
```

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "responseCode": "SUCCESS",
  "responseMessage": "更新成功",
  "data": {
    "id": 12345,
    "name": "Java高级编程",
    "description": "Java高级编程技术",
    "parentId": 1001,
    "parentName": "编程语言",
    "level": 2,
    "path": "1001,12345",
    "iconUrl": "https://example.com/icons/java-advanced.png",
    "coverUrl": "https://example.com/covers/java.jpg",
    "sort": 15,
    "status": "active",
    "contentCount": 156,
    "createTime": "2024-01-16T10:30:00",
    "updateTime": "2024-01-16T15:45:00",
    "operatorId": 12345
  }
}
```

---

### 3. 删除分类
**接口路径**: `DELETE /api/v1/categories/{categoryId}`  
**接口描述**: 逻辑删除分类，如果有子分类或内容关联会拒绝删除

#### 路径参数
- **categoryId** (path): 分类ID，必填

#### 查询参数
- **operatorId** (query): 操作人ID，必填

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "responseCode": "SUCCESS",
  "responseMessage": "删除成功"
}
```

**失败响应 (400)**:
```json
{
  "success": false,
  "responseCode": "CATEGORY_HAS_CHILDREN",
  "responseMessage": "分类下存在子分类，无法删除"
}
```

---

### 4. 获取分类详情
**接口路径**: `GET /api/v1/categories/{categoryId}`  
**接口描述**: 根据分类ID获取分类详细信息

#### 路径参数
- **categoryId** (path): 分类ID，必填

#### 查询参数
- **includeInactive** (query): 是否包含非活跃状态分类，默认false

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "responseCode": "SUCCESS",
  "responseMessage": "查询成功",
  "data": {
    "id": 12345,
    "name": "Java编程",
    "description": "Java编程语言相关内容",
    "parentId": 1001,
    "parentName": "编程语言",
    "level": 2,
    "path": "1001,12345",
    "iconUrl": "https://example.com/icons/java.png",
    "coverUrl": "https://example.com/covers/java.jpg",
    "sort": 10,
    "status": "active",
    "contentCount": 156,
    "createTime": "2024-01-16T10:30:00",
    "updateTime": "2024-01-16T15:45:00",
    "operatorId": 12345
  }
}
```

---

## 分类查询功能 API

### 5. 分页查询分类（POST方式）
**接口路径**: `POST /api/v1/categories/query`  
**接口描述**: 支持复杂条件的分类分页查询

#### 请求参数
```json
{
  "name": "Java",                   // 可选，分类名称模糊查询
  "parentId": 1001,                 // 可选，父分类ID
  "level": 2,                       // 可选，分类层级
  "status": "active",               // 可选，状态筛选
  "startTime": "2024-01-01T00:00:00", // 可选，创建时间开始
  "endTime": "2024-01-31T23:59:59",   // 可选，创建时间结束
  "pageNum": 1,                     // 必填，页码（从1开始）
  "pageSize": 20,                   // 必填，每页大小
  "orderBy": "sort",                // 可选，排序字段（sort/createTime/updateTime）
  "orderDirection": "ASC"           // 可选，排序方向（ASC/DESC）
}
```

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "responseCode": "SUCCESS",
  "responseMessage": "查询成功",
  "data": {
    "total": 156,
    "pageNum": 1,
    "pageSize": 20,
    "pages": 8,
    "list": [
      {
        "id": 12345,
        "name": "Java编程",
        "description": "Java编程语言相关内容",
        "parentId": 1001,
        "parentName": "编程语言",
        "level": 2,
        "path": "1001,12345",
        "iconUrl": "https://example.com/icons/java.png",
        "sort": 10,
        "status": "active",
        "contentCount": 156,
        "createTime": "2024-01-16T10:30:00",
        "updateTime": "2024-01-16T15:45:00"
      }
    ]
  }
}
```

---

### 6. 简单查询分类（GET方式）
**接口路径**: `GET /api/v1/categories`  
**接口描述**: 支持基本参数的分类查询，适用于简单的列表展示

#### 查询参数
- **parentId** (query): 父分类ID，可选
- **status** (query): 状态筛选，默认active
- **pageNum** (query): 页码，默认1
- **pageSize** (query): 每页大小，默认20
- **orderBy** (query): 排序字段，默认sort
- **orderDirection** (query): 排序方向，默认ASC

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "responseCode": "SUCCESS",
  "responseMessage": "查询成功",
  "data": {
    "total": 50,
    "pageNum": 1,
    "pageSize": 20,
    "pages": 3,
    "list": [
      {
        "id": 1001,
        "name": "编程语言",
        "description": "各种编程语言分类",
        "parentId": null,
        "level": 1,
        "sort": 1,
        "status": "active",
        "contentCount": 1250,
        "createTime": "2024-01-01T00:00:00"
      }
    ]
  }
}
```

---

## 层级分类功能 API

### 7. 获取根分类列表
**接口路径**: `GET /api/v1/categories/root`  
**接口描述**: 获取所有顶级分类（parentId为null的分类）

#### 查询参数
- **pageNum** (query): 页码，默认1
- **pageSize** (query): 每页大小，默认20
- **orderBy** (query): 排序字段，默认sort
- **orderDirection** (query): 排序方向，默认ASC

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "responseCode": "SUCCESS",
  "responseMessage": "查询成功",
  "data": {
    "total": 10,
    "pageNum": 1,
    "pageSize": 20,
    "pages": 1,
    "list": [
      {
        "id": 1001,
        "name": "编程语言",
        "description": "各种编程语言分类",
        "parentId": null,
        "level": 1,
        "sort": 1,
        "status": "active",
        "contentCount": 1250,
        "createTime": "2024-01-01T00:00:00"
      }
    ]
  }
}
```

---

### 8. 获取子分类列表
**接口路径**: `GET /api/v1/categories/{categoryId}/children`  
**接口描述**: 获取指定分类的直接子分类列表

#### 路径参数
- **categoryId** (path): 父分类ID，必填

#### 查询参数
- **status** (query): 状态筛选，默认active
- **orderBy** (query): 排序字段，默认sort

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "responseCode": "SUCCESS",
  "responseMessage": "查询成功",
  "data": [
    {
      "id": 12345,
      "name": "Java编程",
      "description": "Java编程语言相关内容",
      "parentId": 1001,
      "level": 2,
      "sort": 10,
      "status": "active",
      "contentCount": 156
    },
    {
      "id": 12346,
      "name": "Python编程",
      "description": "Python编程语言相关内容",
      "parentId": 1001,
      "level": 2,
      "sort": 20,
      "status": "active",
      "contentCount": 203
    }
  ]
}
```

---

### 9. 获取分类树形结构
**接口路径**: `GET /api/v1/categories/tree`  
**接口描述**: 获取完整的分类树形结构，包含所有层级关系

#### 查询参数
- **maxLevel** (query): 最大层级深度，默认不限制
- **includeContentCount** (query): 是否包含内容数量统计，默认true

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "responseCode": "SUCCESS",
  "responseMessage": "查询成功",
  "data": [
    {
      "id": 1001,
      "name": "编程语言",
      "level": 1,
      "sort": 1,
      "contentCount": 1250,
      "children": [
        {
          "id": 12345,
          "name": "Java编程",
          "level": 2,
          "sort": 10,
          "contentCount": 156,
          "children": [
            {
              "id": 12347,
              "name": "Spring框架",
              "level": 3,
              "sort": 1,
              "contentCount": 45,
              "children": []
            }
          ]
        }
      ]
    }
  ]
}
```

---

## 分类统计功能 API

### 10. 获取分类统计信息
**接口路径**: `GET /api/v1/categories/{categoryId}/statistics`  
**接口描述**: 获取分类的详细统计信息，包括内容数量、子分类数量等

#### 路径参数
- **categoryId** (path): 分类ID，必填

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "responseCode": "SUCCESS",
  "responseMessage": "查询成功",
  "data": {
    "categoryId": 12345,
    "categoryName": "Java编程",
    "directContentCount": 156,      // 直接关联的内容数量
    "totalContentCount": 423,       // 包含子分类的总内容数量
    "directChildrenCount": 8,       // 直接子分类数量
    "totalChildrenCount": 25,       // 包含所有后代的子分类数量
    "maxLevel": 5,                  // 最大层级深度
    "lastContentTime": "2024-01-16T15:30:00", // 最新内容时间
    "hotScore": 87.5,               // 热度评分
    "growthRate": 12.5              // 增长率（%）
  }
}
```

---

### 11. 获取全局分类统计
**接口路径**: `GET /api/v1/categories/statistics/global`  
**接口描述**: 获取系统全局的分类统计信息

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "responseCode": "SUCCESS",
  "responseMessage": "查询成功",
  "data": {
    "totalCategories": 1250,        // 总分类数量
    "activeCategories": 1100,       // 活跃分类数量
    "rootCategories": 15,           // 根分类数量
    "maxLevel": 6,                  // 最大层级
    "avgChildrenPerCategory": 3.2,  // 平均子分类数量
    "totalContentAssociations": 45230, // 总内容关联数
    "categoriesWithoutContent": 85, // 无内容的分类数量
    "lastUpdateTime": "2024-01-16T15:45:00"
  }
}
```

---

## 分类内容关联 API

### 12. 获取分类下的内容列表
**接口路径**: `GET /api/v1/categories/{categoryId}/contents`  
**接口描述**: 获取指定分类下的内容列表，支持分页

#### 路径参数
- **categoryId** (path): 分类ID，必填

#### 查询参数
- **includeChildren** (query): 是否包含子分类的内容，默认false
- **contentType** (query): 内容类型筛选，可选
- **pageNum** (query): 页码，默认1
- **pageSize** (query): 每页大小，默认20
- **orderBy** (query): 排序字段，默认createTime
- **orderDirection** (query): 排序方向，默认DESC

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "responseCode": "SUCCESS",
  "responseMessage": "查询成功",
  "data": {
    "total": 156,
    "pageNum": 1,
    "pageSize": 20,
    "pages": 8,
    "list": [
      {
        "contentId": 54321,
        "title": "Java基础教程",
        "contentType": "ARTICLE",
        "author": "张三",
        "createTime": "2024-01-16T10:30:00",
        "viewCount": 1250,
        "likeCount": 89
      }
    ]
  }
}
```

---

## 错误码说明

| 错误码 | 错误描述 | 解决方案 |
|--------|----------|----------|
| CATEGORY_NOT_FOUND | 分类不存在 | 检查分类ID是否正确 |
| CATEGORY_NAME_EXISTS | 分类名称已存在 | 使用不同的分类名称 |
| CATEGORY_HAS_CHILDREN | 分类存在子分类 | 先删除所有子分类 |
| CATEGORY_HAS_CONTENT | 分类存在关联内容 | 先移除所有关联内容 |
| PARENT_CATEGORY_NOT_FOUND | 父分类不存在 | 检查父分类ID是否正确 |
| CATEGORY_LEVEL_EXCEEDED | 分类层级超出限制 | 减少分类层级深度 |
| CATEGORY_PERMISSION_DENIED | 无分类操作权限 | 检查用户权限 |

---

## 数据模型

### CategoryResponse
```typescript
interface CategoryResponse {
  id: number;                    // 分类ID
  name: string;                  // 分类名称
  description?: string;          // 分类描述
  parentId?: number;             // 父分类ID
  parentName?: string;           // 父分类名称
  level: number;                 // 分类层级（从1开始）
  path: string;                  // 分类路径（ID路径，用逗号分隔）
  iconUrl?: string;              // 图标URL
  coverUrl?: string;             // 封面URL
  sort: number;                  // 排序值
  status: string;                // 状态（active/inactive）
  contentCount: number;          // 关联内容数量
  createTime: string;            // 创建时间（ISO 8601格式）
  updateTime: string;            // 更新时间（ISO 8601格式）
  operatorId?: number;           // 操作人ID
  children?: CategoryResponse[]; // 子分类列表（树形结构时使用）
}
```

---

## 使用示例

### 创建分类层级结构
```javascript
// 1. 创建根分类
const rootCategory = await createCategory({
  name: "编程语言",
  description: "各种编程语言分类",
  sort: 1,
  operatorId: 12345
});

// 2. 创建子分类
const childCategory = await createCategory({
  name: "Java编程",
  description: "Java编程语言相关内容",
  parentId: rootCategory.data.id,
  sort: 10,
  operatorId: 12345
});

// 3. 获取分类树
const categoryTree = await getCategoryTree();
```

### 查询分类内容
```javascript
// 查询Java分类下的所有内容（包含子分类）
const contents = await getCategoryContents(javaCategory.id, {
  includeChildren: true,
  contentType: "ARTICLE",
  pageNum: 1,
  pageSize: 20
});
```