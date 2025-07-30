# Collide 内容服务 API 文档

## 概述

Collide 内容服务提供完整的内容管理功能，包括内容发布、章节管理、内容查询、统计分析等核心功能。支持多种内容类型：小说、漫画、视频、文章、音频等。

**服务版本**: v2.0.0 (简洁版)  
**基础路径**: `/api/v1/content`  
**Dubbo服务**: `collide-content`  
**设计理念**: 基于content-simple.sql的双表设计，提供HTTP REST接口，支持评分功能、章节管理、内容审核

## 🚀 性能特性

- **🔥 跨模块集成**: 集成点赞、收藏服务，提供一站式内容互动功能
- **⚡ 简洁设计**: 双表设计，优化查询性能  
- **📊 实时统计**: 跨模块数据聚合，统计信息实时更新
- **🔒 数据一致性**: 统计数据自动同步，保证数据一致性

## 📝 API 设计原则

- **创建/删除操作**: 只返回成功/失败状态，不返回具体数据 (Result<Void>)
- **更新操作**: 返回更新后的完整数据 (Result<ContentResponse>)
- **查询操作**: 分为两种响应格式，支持复杂查询条件

### 📋 响应格式说明

本API提供两种响应格式：

1. **标准Result响应**（POST复杂查询、增删改操作）:
   ```json
   {
     "success": true,
     "code": "SUCCESS", 
     "message": "操作成功",
     "data": { /* 具体数据 */ }
   }
   ```

2. **直接PageResponse响应**（GET简单分页查询）:
   ```json
   {
     "success": true,
     "datas": [ /* 数据列表 */ ],
     "currentPage": 1,
     "pageSize": 20,
     "totalPage": 3,
     "total": 50
   }
   ```

---

## 接口概览

### 内容管理接口 (7个)
| 接口路径 | 方法 | 功能描述 | 响应格式 |
|---------|-----|---------|----------|
| `POST /create` | POST | 创建内容 | Result<Void> |
| `PUT /update` | PUT | 更新内容 | Result<ContentResponse> |
| `DELETE /{id}` | DELETE | 删除内容 | Result<Void> |
| `GET /{id}` | GET | 获取内容详情 | Result<ContentResponse> |
| `POST /query` | POST | 分页查询内容 | Result<PageResponse<ContentResponse>> |
| `POST /{id}/publish` | POST | 发布内容 | Result<ContentResponse> |
| `POST /{id}/offline` | POST | 下线内容 | Result<Void> |

### 章节管理接口 (4个)
| 接口路径 | 方法 | 功能描述 | 响应格式 |
|---------|-----|---------|----------|
| `POST /chapter/create` | POST | 创建章节 | Result<Void> |
| `GET /{contentId}/chapters` | GET | 获取内容章节列表 | PageResponse<ChapterResponse> |
| `GET /chapter/{id}` | GET | 获取章节详情 | Result<ChapterResponse> |
| `POST /chapter/{id}/publish` | POST | 发布章节 | Result<ChapterResponse> |

### 统计管理接口 (6个)
| 接口路径 | 方法 | 功能描述 | 响应格式 |
|---------|-----|---------|----------|
| `POST /{id}/view` | POST | 增加浏览量 | Result<Long> |
| `POST /{id}/like-count` | POST | 增加点赞数 | Result<Long> |
| `POST /{id}/comment` | POST | 增加评论数 | Result<Long> |
| `POST /{id}/favorite-count` | POST | 增加收藏数 | Result<Long> |
| `POST /{id}/score` | POST | 更新评分 | Result<Double> |
| `GET /{id}/statistics` | GET | 获取内容统计 | Result<Map<String, Object>> |

### 内容查询接口 (5个) - 直接PageResponse响应
| 接口路径 | 方法 | 功能描述 | 响应格式 |
|---------|-----|---------|----------|
| `GET /author/{authorId}` | GET | 根据作者查询内容 | PageResponse<ContentResponse> |
| `GET /category/{categoryId}` | GET | 根据分类查询内容 | PageResponse<ContentResponse> |
| `GET /search` | GET | 搜索内容 | PageResponse<ContentResponse> |
| `GET /popular` | GET | 获取热门内容 | PageResponse<ContentResponse> |
| `GET /latest` | GET | 获取最新内容 | PageResponse<ContentResponse> |

### 数据同步接口 (3个)
| 接口路径 | 方法 | 功能描述 | 响应格式 |
|---------|-----|---------|----------|
| `POST /sync/author` | POST | 同步作者信息 | Result<Integer> |
| `POST /sync/category` | POST | 同步分类信息 | Result<Integer> |
| `POST /{id}/review` | POST | 审核内容 | Result<ContentResponse> |

### 跨模块功能增强接口 (5个) 🔥
| 接口路径 | 方法 | 功能描述 | 响应格式 |
|---------|-----|---------|----------|
| `GET /{id}/like/status` | GET | 获取用户点赞状态 | Result<Boolean> |
| `POST /{id}/like` | POST | 点赞内容 | Result<Boolean> |
| `GET /{id}/favorite/status` | GET | 获取用户收藏状态 | Result<Boolean> |
| `POST /{id}/favorite` | POST | 收藏内容 | Result<Boolean> |
| `GET /{id}/interaction` | GET | 获取用户互动状态 | Result<Map<String, Object>> |

---

## 内容管理 API

### 1. 创建内容
**接口路径**: `POST /api/v1/content/create`  
**接口描述**: 创建新内容，支持多种类型：NOVEL、COMIC、VIDEO、ARTICLE、AUDIO

#### 请求参数
```json
{
  "title": "Java设计模式详解",                    // 必填，内容标题
  "description": "深入讲解23种设计模式的应用",      // 可选，内容描述
  "contentType": "NOVEL",                        // 必填，内容类型：NOVEL/COMIC/VIDEO/ARTICLE/AUDIO
  "contentData": "{\"chapters\": 10}",          // 可选，内容数据JSON
  "coverUrl": "https://example.com/cover.jpg",  // 可选，封面图片URL
  "tags": "[\"Java\", \"设计模式\"]",           // 可选，标签JSON数组
  "authorId": 12345,                            // 必填，作者用户ID
  "categoryId": 1001,                           // 必填，分类ID
  "status": "DRAFT"                             // 可选，状态：DRAFT/PUBLISHED/OFFLINE
}
```

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "内容创建成功",
  "data": null
}
```

**失败响应 (400)**:
```json
{
  "success": false,
  "code": "CONTENT_CREATE_ERROR",
  "message": "内容创建失败"
}
```

---

### 2. 更新内容
**接口路径**: `PUT /api/v1/content/update`  
**接口描述**: 更新内容信息，支持部分字段更新

#### 请求参数
```json
{
  "id": 98765,                                   // 必填，内容ID
  "title": "Java设计模式完全指南",                   // 可选，更新标题
  "description": "全面覆盖GOF 23种设计模式",        // 可选，更新描述
  "contentData": "{\"chapters\": 15}",           // 可选，更新内容数据
  "coverUrl": "https://example.com/new-cover.jpg", // 可选，更新封面
  "tags": "[\"Java\", \"设计模式\", \"GOF\"]",    // 可选，更新标签
  "categoryId": 1002,                             // 可选，更新分类
  "status": "PUBLISHED"                           // 可选，更新状态
}
```

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "内容更新成功",
  "data": {
    "id": 98765,
    "title": "Java设计模式完全指南",
    "description": "全面覆盖GOF 23种设计模式",
    "contentType": "NOVEL",
    "contentData": "{\"chapters\": 15}",
    "coverUrl": "https://example.com/new-cover.jpg",
    "tags": "[\"Java\", \"设计模式\", \"GOF\"]",
    "authorId": 12345,
    "authorNickname": "技术达人",
    "categoryId": 1002,
    "categoryName": "编程进阶",
    "status": "PUBLISHED",
    "updateTime": "2024-01-16T11:30:00"
  }
}
```

---

### 3. 删除内容
**接口路径**: `DELETE /api/v1/content/{id}`  
**接口描述**: 逻辑删除内容（设为OFFLINE状态）

#### 请求参数
- **id** (path): 内容ID，必填
- **operatorId** (query): 操作人ID，必填

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "内容删除成功",
  "data": null
}
```

---

### 4. 获取内容详情
**接口路径**: `GET /api/v1/content/{id}`  
**接口描述**: 根据ID获取内容详情

#### 请求参数
- **id** (path): 内容ID，必填
- **includeOffline** (query): 是否包含下线内容，可选，默认false

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "获取内容详情成功",
  "data": {
    "id": 98765,
    "title": "Java设计模式详解",
    "description": "深入讲解23种设计模式的应用",
    "contentType": "NOVEL",
    "contentData": "{\"chapters\": 10}",
    "coverUrl": "https://example.com/cover.jpg",
    "tags": "[\"Java\", \"设计模式\"]",
    "authorId": 12345,
    "authorNickname": "技术达人",
    "authorAvatar": "https://example.com/avatar.jpg",
    "categoryId": 1001,
    "categoryName": "编程技术",
    "status": "PUBLISHED",
    "reviewStatus": "APPROVED",
    "viewCount": 15420,
    "likeCount": 892,
    "commentCount": 156,
    "favoriteCount": 234,
    "scoreTotal": 850,
    "scoreCount": 100,
    "averageScore": 8.5,
    "createTime": "2024-01-15T14:20:00",
    "updateTime": "2024-01-16T10:30:00",
    "publishTime": "2024-01-16T10:30:00"
  }
}
```

---

### 5. 分页查询内容
**接口路径**: `POST /api/v1/content/query`  
**接口描述**: 根据条件分页查询内容列表

#### 请求参数
```json
{
  "currentPage": 1,                // 页码（从1开始）
  "pageSize": 20,                  // 每页大小
  "keyword": "Java",               // 可选，搜索关键词
  "contentType": "NOVEL",          // 可选，内容类型
  "authorId": 12345,               // 可选，作者ID
  "categoryId": 1001,              // 可选，分类ID
  "status": "PUBLISHED",           // 可选，状态
  "reviewStatus": "APPROVED",      // 可选，审核状态
  "orderBy": "createTime",         // 可选，排序字段
  "orderDirection": "DESC"         // 可选，排序方向
}
```

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "查询成功",
  "data": {
    "success": true,
    "datas": [
      {
        "id": 98765,
        "title": "Java设计模式详解",
        "description": "深入讲解23种设计模式的应用",
        "contentType": "NOVEL",
        "authorId": 12345,
        "authorNickname": "技术达人",
        "categoryId": 1001,
        "categoryName": "编程技术",
        "status": "PUBLISHED",
        "reviewStatus": "APPROVED",
        "viewCount": 15420,
        "likeCount": 892,
        "createTime": "2024-01-15T14:20:00"
      }
    ],
    "currentPage": 1,
    "pageSize": 20,
    "totalPage": 8,
    "total": 156
  }
}
```

---

### 6. 发布内容
**接口路径**: `POST /api/v1/content/{id}/publish`  
**接口描述**: 将草稿状态的内容发布上线

#### 请求参数
- **id** (path): 内容ID，必填
- **authorId** (query): 作者ID，必填

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "内容发布成功",
  "data": {
    "id": 98765,
    "title": "Java设计模式详解",
    "status": "PUBLISHED",
    "publishTime": "2024-01-16T10:30:00"
  }
}
```

---

### 7. 下线内容
**接口路径**: `POST /api/v1/content/{id}/offline`  
**接口描述**: 将已发布的内容下线

#### 请求参数
- **id** (path): 内容ID，必填
- **operatorId** (query): 操作人ID，必填

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "内容下线成功",
  "data": null
}
```

---

## 章节管理 API

### 1. 创建章节
**接口路径**: `POST /api/v1/content/chapter/create`  
**接口描述**: 为小说、漫画等多章节内容创建新章节

#### 请求参数
```json
{
  "contentId": 98765,                           // 必填，内容ID
  "chapterNum": 1,                             // 必填，章节号
  "title": "第一章：单例模式",                   // 必填，章节标题
  "content": "单例模式是一种创建型设计模式...",    // 必填，章节内容
  "wordCount": 2500,                           // 可选，字数（自动计算）
  "status": "DRAFT"                            // 可选，状态：DRAFT/PUBLISHED
}
```

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "章节创建成功",
  "data": null
}
```

---

### 2. 获取内容章节列表
**接口路径**: `GET /api/v1/content/{contentId}/chapters`  
**接口描述**: 分页获取指定内容的章节列表（直接PageResponse响应）

#### 请求参数
- **contentId** (path): 内容ID，必填
- **status** (query): 章节状态，可选
- **currentPage** (query): 页码，可选，默认1
- **pageSize** (query): 页面大小，可选，默认20

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "datas": [
    {
      "id": 123456,
      "contentId": 98765,
      "chapterNum": 1,
      "title": "第一章：单例模式",
      "content": "单例模式是一种创建型设计模式...",
      "wordCount": 2500,
      "status": "PUBLISHED",
      "publishTime": "2024-01-16T10:30:00",
      "createTime": "2024-01-15T14:20:00"
    }
  ],
  "currentPage": 1,
  "pageSize": 20,
  "totalPage": 5,
  "total": 89
}
```

---

### 3. 获取章节详情
**接口路径**: `GET /api/v1/content/chapter/{id}`  
**接口描述**: 根据章节ID获取章节详情

#### 请求参数
- **id** (path): 章节ID，必填

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "获取章节详情成功",
  "data": {
    "id": 123456,
    "contentId": 98765,
    "chapterNum": 1,
    "title": "第一章：单例模式",
    "content": "单例模式是一种创建型设计模式，它保证一个类只有一个实例，并提供一个全局访问点...",
    "wordCount": 2500,
    "status": "PUBLISHED",
    "publishTime": "2024-01-16T10:30:00",
    "createTime": "2024-01-15T14:20:00",
    "updateTime": "2024-01-16T09:15:00"
  }
}
```

---

### 4. 发布章节
**接口路径**: `POST /api/v1/content/chapter/{id}/publish`  
**接口描述**: 发布指定章节

#### 请求参数
- **id** (path): 章节ID，必填
- **authorId** (query): 作者ID，必填

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "章节发布成功",
  "data": {
    "id": 123456,
    "contentId": 98765,
    "chapterNum": 1,
    "title": "第一章：单例模式",
    "status": "PUBLISHED",
    "publishTime": "2024-01-16T10:30:00"
  }
}
```

---

## 统计管理 API

### 1. 增加浏览量
**接口路径**: `POST /api/v1/content/{id}/view`  
**接口描述**: 增加内容的浏览量统计

#### 请求参数
- **id** (path): 内容ID，必填
- **increment** (query): 增加数量，可选，默认1

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "浏览量更新成功",
  "data": 15421
}
```

---

### 2. 增加点赞数
**接口路径**: `POST /api/v1/content/{id}/like-count`  
**接口描述**: 增加内容的点赞数统计

#### 请求参数
- **id** (path): 内容ID，必填
- **increment** (query): 增加数量，可选，默认1

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "点赞数更新成功",
  "data": 893
}
```

---

### 3. 增加评论数
**接口路径**: `POST /api/v1/content/{id}/comment`  
**接口描述**: 增加内容的评论数统计

#### 请求参数
- **id** (path): 内容ID，必填
- **increment** (query): 增加数量，可选，默认1

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "评论数更新成功",
  "data": 157
}
```

---

### 4. 增加收藏数
**接口路径**: `POST /api/v1/content/{id}/favorite-count`  
**接口描述**: 增加内容的收藏数统计

#### 请求参数
- **id** (path): 内容ID，必填
- **increment** (query): 增加数量，可选，默认1

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "收藏数更新成功",
  "data": 235
}
```

---

### 5. 更新评分
**接口路径**: `POST /api/v1/content/{id}/score`  
**接口描述**: 为内容添加评分，支持1-10分评分系统

#### 请求参数
- **id** (path): 内容ID，必填
- **score** (query): 评分值(1-10)，必填

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "评分更新成功",
  "data": 8.5
}
```

---

### 6. 获取内容统计
**接口路径**: `GET /api/v1/content/{id}/statistics`  
**接口描述**: 获取内容的完整统计信息，包括评分

#### 请求参数
- **id** (path): 内容ID，必填

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "获取统计信息成功",
  "data": {
    "contentId": 98765,
    "viewCount": 15420,
    "likeCount": 892,
    "commentCount": 156,
    "favoriteCount": 234,
    "shareCount": 45,
    "scoreTotal": 850,
    "scoreCount": 100,
    "averageScore": 8.5,
    "hotScore": 95.8,
    "todayViewCount": 156,
    "weekViewCount": 1205,
    "monthViewCount": 4586,
    "lastUpdateTime": "2024-01-16T15:30:00"
  }
}
```

---

## 内容查询 API

### 1. 根据作者查询内容
**接口路径**: `GET /api/v1/content/author/{authorId}`  
**接口描述**: 分页查询指定作者的内容列表（直接PageResponse响应）

#### 请求参数
- **authorId** (path): 作者ID，必填
- **contentType** (query): 内容类型，可选
- **status** (query): 状态，可选
- **currentPage** (query): 页码，可选，默认1
- **pageSize** (query): 页面大小，可选，默认20

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "datas": [
    {
      "id": 98765,
      "title": "Java设计模式详解",
      "description": "深入讲解23种设计模式的应用",
      "contentType": "NOVEL",
      "authorId": 12345,
      "authorNickname": "技术大师",
      "categoryId": 1001,
      "categoryName": "编程技术",
      "status": "PUBLISHED",
      "viewCount": 15420,
      "likeCount": 892,
      "commentCount": 156,
      "favoriteCount": 234,
      "createTime": "2024-01-15T14:20:00",
      "publishTime": "2024-01-16T10:30:00"
    }
  ],
  "currentPage": 1,
  "pageSize": 20,
  "totalPage": 8,
  "total": 145
}
```

---

### 2. 根据分类查询内容
**接口路径**: `GET /api/v1/content/category/{categoryId}`  
**接口描述**: 分页查询指定分类的内容列表（直接PageResponse响应）

#### 请求参数
- **categoryId** (path): 分类ID，必填
- **contentType** (query): 内容类型，可选
- **currentPage** (query): 页码，可选，默认1
- **pageSize** (query): 页面大小，可选，默认20

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "datas": [
    {
      "id": 98765,
      "title": "Java设计模式详解",
      "description": "深入讲解23种设计模式的应用",
      "contentType": "NOVEL",
      "authorId": 12345,
      "authorNickname": "技术大师",
      "categoryId": 1001,
      "categoryName": "编程技术",
      "status": "PUBLISHED",
      "viewCount": 15420,
      "likeCount": 892,
      "createTime": "2024-01-15T14:20:00"
    }
  ],
  "currentPage": 1,
  "pageSize": 20,
  "totalPage": 3,
  "total": 56
}
```

---

### 3. 搜索内容
**接口路径**: `GET /api/v1/content/search`  
**接口描述**: 根据关键词搜索内容（标题、描述、标签）（直接PageResponse响应）

#### 请求参数
- **keyword** (query): 搜索关键词，必填
- **contentType** (query): 内容类型，可选
- **currentPage** (query): 页码，可选，默认1
- **pageSize** (query): 页面大小，可选，默认20

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "datas": [
    {
      "id": 98765,
      "title": "Java设计模式详解",
      "description": "深入讲解23种设计模式的应用",
      "contentType": "NOVEL",
      "authorId": 12345,
      "authorNickname": "技术大师",
      "viewCount": 15420,
      "likeCount": 892,
      "createTime": "2024-01-15T14:20:00"
    }
  ],
  "currentPage": 1,
  "pageSize": 20,
  "totalPage": 2,
  "total": 32
}
```

---

### 4. 获取热门内容
**接口路径**: `GET /api/v1/content/popular`  
**接口描述**: 根据综合热度排序获取热门内容（直接PageResponse响应）

#### 请求参数
- **contentType** (query): 内容类型，可选
- **timeRange** (query): 时间范围(天)，可选
- **currentPage** (query): 页码，可选，默认1
- **pageSize** (query): 页面大小，可选，默认20

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "datas": [
    {
      "id": 98765,
      "title": "Java设计模式详解",
      "description": "深入讲解23种设计模式的应用",
      "contentType": "NOVEL",
      "authorId": 12345,
      "authorNickname": "技术大师",
      "viewCount": 25600,
      "likeCount": 1892,
      "hotScore": 95.8,
      "createTime": "2024-01-15T14:20:00"
    }
  ],
  "currentPage": 1,
  "pageSize": 20,
  "totalPage": 1,
  "total": 15
}
```

---

### 5. 获取最新内容
**接口路径**: `GET /api/v1/content/latest`  
**接口描述**: 按发布时间排序获取最新内容（直接PageResponse响应）

#### 请求参数
- **contentType** (query): 内容类型，可选
- **currentPage** (query): 页码，可选，默认1
- **pageSize** (query): 页面大小，可选，默认20

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "datas": [
    {
      "id": 98766,
      "title": "最新发布的内容",
      "description": "刚刚发布的新内容",
      "contentType": "ARTICLE",
      "authorId": 12346,
      "authorNickname": "新作者",
      "viewCount": 156,
      "likeCount": 23,
      "createTime": "2024-01-16T15:45:00",
      "publishTime": "2024-01-16T16:00:00"
    }
  ],
  "currentPage": 1,
  "pageSize": 20,
  "totalPage": 10,
  "total": 200
}
```

---

## 数据同步 API

### 1. 同步作者信息
**接口路径**: `POST /api/v1/content/sync/author`  
**接口描述**: 更新内容表中的冗余作者信息

#### 请求参数
- **authorId** (query): 作者ID，必填
- **nickname** (query): 新昵称，必填
- **avatar** (query): 新头像，可选

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "作者信息同步成功",
  "data": 25
}
```

---

### 2. 同步分类信息
**接口路径**: `POST /api/v1/content/sync/category`  
**接口描述**: 更新内容表中的冗余分类信息

#### 请求参数
- **categoryId** (query): 分类ID，必填
- **categoryName** (query): 新分类名称，必填

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "分类信息同步成功",
  "data": 25
}
```

---

### 3. 审核内容
**接口路径**: `POST /api/v1/content/{id}/review`  
**接口描述**: 内容审核，更新审核状态

#### 请求参数
- **id** (path): 内容ID，必填
- **reviewStatus** (query): 审核状态：APPROVED、REJECTED，必填
- **reviewerId** (query): 审核人ID，必填
- **reviewComment** (query): 审核意见，可选

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "内容审核完成",
  "data": {
    "id": 98765,
    "title": "Java设计模式详解",
    "status": "PUBLISHED",
    "reviewStatus": "APPROVED",
    "reviewerId": 67890,
    "reviewComment": "内容质量良好，通过审核",
    "reviewTime": "2024-01-16T12:00:00"
  }
}
```

---

## 跨模块功能增强 API 🔥

> **📌 重要说明**：点赞和收藏功能相关接口
> 
> **点赞功能**：
> - `POST /{id}/like-count` - **统计管理接口**，用于系统内部增加/减少点赞数统计
> - `POST /{id}/like` - **用户交互接口**，用于用户点赞/取消点赞内容，会自动同步统计数据
> 
> **收藏功能**：
> - `POST /{id}/favorite-count` - **统计管理接口**，用于系统内部增加/减少收藏数统计
> - `POST /{id}/favorite` - **用户交互接口**，用于用户收藏/取消收藏内容，会自动同步统计数据

### 1. 获取用户点赞状态
**接口路径**: `GET /api/v1/content/{id}/like/status`  
**接口描述**: 检查用户是否已点赞该内容

#### 请求参数
- **id** (path): 内容ID，必填
- **userId** (query): 用户ID，必填

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "查询成功",
  "data": true
}
```

---

### 2. 点赞内容
**接口路径**: `POST /api/v1/content/{id}/like`  
**接口描述**: 用户点赞/取消点赞内容

#### 请求参数
- **id** (path): 内容ID，必填
- **userId** (query): 用户ID，必填

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "点赞成功",
  "data": true
}
```

**取消点赞响应 (200)**:
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "取消点赞成功",
  "data": false
}
```

---

### 3. 获取用户收藏状态
**接口路径**: `GET /api/v1/content/{id}/favorite/status`  
**接口描述**: 检查用户是否已收藏该内容

#### 请求参数
- **id** (path): 内容ID，必填
- **userId** (query): 用户ID，必填

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "查询成功",
  "data": false
}
```

---

### 4. 收藏内容
**接口路径**: `POST /api/v1/content/{id}/favorite`  
**接口描述**: 用户收藏/取消收藏内容

#### 请求参数
- **id** (path): 内容ID，必填
- **userId** (query): 用户ID，必填

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "收藏成功",
  "data": true
}
```

**取消收藏响应 (200)**:
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "取消收藏成功",
  "data": false
}
```

---

### 5. 获取用户互动状态
**接口路径**: `GET /api/v1/content/{id}/interaction`  
**接口描述**: 一次性获取用户对该内容的点赞、收藏状态

#### 请求参数
- **id** (path): 内容ID，必填
- **userId** (query): 用户ID，必填

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "获取用户互动状态成功",
  "data": {
    "contentId": 98765,
    "userId": 12345,
    "isLiked": true,
    "isFavorited": false,
    "likeCount": 892,
    "favoriteCount": 234
  }
}
```

---

## 错误码说明

### 内容相关错误码
| 错误码 | 说明 | 解决方案 |
|--------|------|----------|
| CONTENT_NOT_FOUND | 内容不存在 | 检查内容ID是否正确 |
| CONTENT_CREATE_ERROR | 内容创建失败 | 检查必填参数和数据格式 |
| CONTENT_UPDATE_ERROR | 内容更新失败 | 检查内容状态和权限 |
| CONTENT_DELETE_ERROR | 内容删除失败 | 检查内容状态和操作权限 |
| CONTENT_PUBLISH_ERROR | 内容发布失败 | 检查内容状态和作者权限 |
| CONTENT_OFFLINE_ERROR | 内容下线失败 | 检查内容状态和操作权限 |

### 章节相关错误码
| 错误码 | 说明 | 解决方案 |
|--------|------|----------|
| CHAPTER_NOT_FOUND | 章节不存在 | 检查章节ID是否正确 |
| CHAPTER_CREATE_ERROR | 章节创建失败 | 检查章节号是否重复 |
| CHAPTER_PUBLISH_ERROR | 章节发布失败 | 检查章节状态和作者权限 |
| CHAPTER_NUM_EXISTS | 章节号已存在 | 使用不同的章节号 |

### 统计相关错误码
| 错误码 | 说明 | 解决方案 |
|--------|------|----------|
| STATISTICS_UPDATE_ERROR | 统计更新失败 | 检查内容ID和统计类型 |
| SCORE_INVALID_RANGE | 评分超出范围 | 评分值应在1-10之间 |
| STATISTICS_GET_ERROR | 获取统计失败 | 检查内容ID是否存在 |

### 查询相关错误码
| 错误码 | 说明 | 解决方案 |
|--------|------|----------|
| QUERY_PARAMETER_ERROR | 查询参数错误 | 检查分页参数和筛选条件 |
| SEARCH_KEYWORD_EMPTY | 搜索关键词为空 | 提供有效的搜索关键词 |
| AUTHOR_NOT_FOUND | 作者不存在 | 检查作者ID是否正确 |
| CATEGORY_NOT_FOUND | 分类不存在 | 检查分类ID是否正确 |

### 权限相关错误码
| 错误码 | 说明 | 解决方案 |
|--------|------|----------|
| AUTHOR_PERMISSION_DENIED | 作者权限不足 | 检查操作人是否为内容作者 |
| REVIEW_PERMISSION_DENIED | 审核权限不足 | 检查是否有审核权限 |
| OPERATION_PERMISSION_DENIED | 操作权限不足 | 检查用户权限 |

### 跨模块功能错误码 🔥
| 错误码 | 说明 | 解决方案 |
|--------|------|----------|
| ADD_LIKE_FAILED | 点赞失败 | 检查用户和内容状态 |
| CANCEL_LIKE_FAILED | 取消点赞失败 | 检查点赞状态 |
| TOGGLE_LIKE_FAILED | 切换点赞状态失败 | 检查点赞服务状态 |
| ADD_FAVORITE_FAILED | 收藏失败 | 检查用户和内容状态 |
| REMOVE_FAVORITE_FAILED | 取消收藏失败 | 检查收藏状态 |
| TOGGLE_FAVORITE_FAILED | 切换收藏状态失败 | 检查收藏服务状态 |
| INTERACTION_STATUS_FAILED | 获取互动状态失败 | 检查服务连接状态 |

### 数据同步错误码
| 错误码 | 说明 | 解决方案 |
|--------|------|----------|
| SYNC_AUTHOR_ERROR | 同步作者信息失败 | 检查作者数据完整性 |
| SYNC_CATEGORY_ERROR | 同步分类信息失败 | 检查分类数据完整性 |
| REVIEW_STATUS_ERROR | 审核状态错误 | 使用有效的审核状态 |

---

## 使用示例

### 基础内容操作
```javascript
// 创建内容
const createResponse = await fetch('/api/v1/content/create', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    title: "Java设计模式详解",
    description: "深入讲解23种设计模式的应用",
    contentType: "NOVEL",
    authorId: 12345,
    categoryId: 1001,
    status: "DRAFT"
  })
});

// 获取内容详情
const detailResponse = await fetch('/api/v1/content/98765?includeOffline=false');
const detail = await detailResponse.json();

// 发布内容
const publishResponse = await fetch('/api/v1/content/98765/publish?authorId=12345', {
  method: 'POST'
});
```

### 章节管理
```javascript
// 创建章节
const chapterResponse = await fetch('/api/v1/content/chapter/create', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    contentId: 98765,
    chapterNum: 1,
    title: "第一章：单例模式",
    content: "单例模式是一种创建型设计模式...",
    status: "DRAFT"
  })
});

// 获取章节列表（直接PageResponse）
const chaptersResponse = await fetch('/api/v1/content/98765/chapters?currentPage=1&pageSize=20');
const chapters = await chaptersResponse.json(); // 直接是PageResponse格式
```

### 内容查询（直接PageResponse）
```javascript
// 搜索内容
const searchResponse = await fetch('/api/v1/content/search?keyword=Java&contentType=NOVEL&currentPage=1&pageSize=20');
const searchResult = await searchResponse.json(); // 直接是PageResponse格式

// 获取热门内容
const popularResponse = await fetch('/api/v1/content/popular?contentType=NOVEL&currentPage=1&pageSize=20');
const popular = await popularResponse.json(); // 直接是PageResponse格式
```

### 跨模块功能增强 🔥
```javascript
// 获取用户互动状态（一次性获取点赞、收藏状态）
const interactionResponse = await fetch('/api/v1/content/98765/interaction?userId=12345');
const interaction = await interactionResponse.json();
console.log('互动状态:', interaction.data);

// 切换点赞状态
const likeResponse = await fetch('/api/v1/content/98765/like?userId=12345', {
  method: 'POST'
});
const likeResult = await likeResponse.json();
console.log('点赞状态:', likeResult.data); // true/false

// 切换收藏状态
const favoriteResponse = await fetch('/api/v1/content/98765/favorite?userId=12345', {
  method: 'POST'
});
const favoriteResult = await favoriteResponse.json();
console.log('收藏状态:', favoriteResult.data); // true/false
```

### 统计管理
```javascript
// 增加浏览量
const viewResponse = await fetch('/api/v1/content/98765/view?increment=1', {
  method: 'POST'
});

// 增加点赞数（统计）
const likeCountResponse = await fetch('/api/v1/content/98765/like-count?increment=1', {
  method: 'POST'
});

// 增加收藏数（统计）
const favoriteCountResponse = await fetch('/api/v1/content/98765/favorite-count?increment=1', {
  method: 'POST'
});

// 更新评分
const scoreResponse = await fetch('/api/v1/content/98765/score?score=9', {
  method: 'POST'
});

// 获取完整统计信息
const statsResponse = await fetch('/api/v1/content/98765/statistics');
const stats = await statsResponse.json();
console.log('统计信息:', stats.data);
```

---

## 版本更新日志

### v2.0.0 (2024-01-16) - 简洁版
🎉 **重大更新**:
- 🔥 新增跨模块功能增强API (5个接口)
- ⚡ 基于content-simple.sql的双表设计优化
- 📊 完善评分功能、章节管理、内容审核
- 🚀 集成点赞、收藏服务，提供一站式内容互动功能

🔥 **跨模块功能增强**:
- 用户点赞状态查询和切换
- 用户收藏状态查询和切换
- 一次性获取用户互动状态
- 实时统计数据同步

⚡ **架构优化**:
- 双表设计，优化查询性能
- 统一响应格式 (Result<T> 和 PageResponse<T>)
- 增强的错误码体系
- 跨模块数据聚合

### v1.0.0 (2024-01-01) - 基础版
- 基础内容管理功能（创建、更新、删除、查询）
- 章节管理和发布功能
- 内容统计和评分系统

---

**最后更新**: 2024-01-16  
**文档版本**: v2.0.0 (简洁版)  
**控制器版本**: ContentController v2.0.0