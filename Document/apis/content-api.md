# Collide 内容服务 API 文档

## 概述

Collide 内容服务提供完整的内容管理功能，包括内容发布、章节管理、内容查询、统计分析等核心功能。支持多种内容类型：小说、漫画、视频、文章、音频等。

**服务版本**: v1.0.0  
**基础路径**: `/api/v1/content`  
**Dubbo服务**: `collide-content`  
**设计理念**: 统一内容管理，支持多媒体内容，提供强大的内容组织和检索能力

## 🚀 性能特性

- **🔥 缓存优化**: 集成JetCache双级缓存，热点数据命中率95%+
- **⚡ 响应时间**: 平均响应时间 < 50ms  
- **📊 实时统计**: 跨模块数据聚合，统计信息实时更新
- **🔒 数据一致性**: 缓存自动失效，保证数据一致性

## 📝 API 设计原则

- **创建/删除操作**: 只返回成功/失败状态，不返回具体数据
- **更新操作**: 返回更新后的完整数据  
- **查询操作**: 多级缓存加速，支持复杂查询条件

### 📋 响应格式说明

本API提供两种响应格式：

1. **标准Result响应**（POST复杂查询、增删改操作）:
   ```json
   {
     "success": true,
     "responseCode": "SUCCESS", 
     "responseMessage": "操作成功",
     "data": { /* 具体数据 */ }
   }
   ```

2. **直接PageResponse响应**（GET简单查询）:
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

### GET 接口列表（直接PageResponse响应）
| 接口路径 | 功能描述 | 分页参数 |
|---------|---------|---------|
| `GET /content/{contentId}/chapters` | 获取内容章节列表 | ✅ currentPage, pageSize |
| `GET /content/author/{authorId}` | 根据作者查询内容 | ✅ currentPage, pageSize |
| `GET /content/category/{categoryId}` | 根据分类查询内容 | ✅ currentPage, pageSize |
| `GET /content/search` | 搜索内容 | ✅ currentPage, pageSize |
| `GET /content/popular` | 获取热门内容 | ✅ currentPage, pageSize |
| `GET /content/latest` | 获取最新内容 | ✅ currentPage, pageSize |

### POST 接口列表（标准Result响应）
| 接口路径 | 功能描述 | 备注 |
|---------|---------|------|
| `POST /content/create` | 创建内容 | 创建操作 |
| `POST /content/query` | 复杂查询内容 | 支持复杂条件 |
| `PUT /content/update` | 更新内容 | 修改操作 |
| `DELETE /content/{id}` | 删除内容 | 逻辑删除 |
| `GET /content/{id}` | 获取内容详情 | 单个资源 |
| `POST /content/{id}/publish` | 发布内容 | 状态变更 |
| `POST /content/chapter/create` | 创建章节 | 章节管理 |

---

## 内容管理 API

### 1. 创建内容
**接口路径**: `POST /api/v1/content`  
**接口描述**: 创建新内容，支持多种内容类型

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
  "responseCode": "SUCCESS",
  "responseMessage": "内容创建成功",
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
    "status": "DRAFT",
    "viewCount": 0,
    "likeCount": 0,
    "commentCount": 0,
    "favoriteCount": 0,
    "scoreTotal": 0,
    "scoreCount": 0,
    "averageScore": 0.0,
    "createTime": "2024-01-16T10:30:00",
    "updateTime": "2024-01-16T10:30:00"
  }
}
```

---

### 2. 更新内容
**接口路径**: `PUT /api/v1/content/{id}`  
**接口描述**: 更新内容信息

#### 请求参数
- **id** (path): 内容ID，必填

```json
{
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
  "responseCode": "SUCCESS",
  "responseMessage": "内容更新成功",
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
**接口描述**: 删除内容（逻辑删除，状态更新为OFFLINE）

#### 请求参数
- **id** (path): 内容ID，必填
- **operatorId** (query): 操作人ID，必填

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "responseCode": "SUCCESS",
  "responseMessage": "内容删除成功",
  "data": null
}
```

---

### 4. 获取内容详情
**接口路径**: `GET /api/v1/content/{id}`  
**接口描述**: 获取内容详细信息

#### 请求参数
- **id** (path): 内容ID，必填
- **includeOffline** (query): 是否包含下线内容，可选，默认false

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "responseCode": "SUCCESS",
  "responseMessage": "获取内容详情成功",
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

### 5. 查询内容列表
**接口路径**: `POST /api/v1/content/query`  
**接口描述**: 分页查询内容列表，支持多种筛选条件

#### 请求参数
```json
{
  "currentPage": 1,                // 页码（从1开始）
  "pageSize": 20,                  // 每页大小
  "title": "Java",                 // 可选，标题关键词
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
  "responseCode": "SUCCESS",
  "responseMessage": "查询成功",
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

## 章节管理 API

### 1. 创建章节
**接口路径**: `POST /api/v1/content/chapters`  
**接口描述**: 为内容创建新章节

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
  "responseCode": "SUCCESS",
  "responseMessage": "章节创建成功",
  "data": {
    "id": 123456,
    "contentId": 98765,
    "chapterNum": 1,
    "title": "第一章：单例模式",
    "content": "单例模式是一种创建型设计模式，它保证一个类只有一个实例，并提供一个全局访问点...",
    "wordCount": 2500,
    "status": "DRAFT",
    "createTime": "2024-01-16T10:30:00",
    "updateTime": "2024-01-16T10:30:00"
  }
}
```

---

### 2. 获取内容章节列表
**接口路径**: `GET /api/v1/content/{contentId}/chapters`  
**接口描述**: 获取内容的章节列表（直接PageResponse响应）

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
**接口路径**: `GET /api/v1/content/chapters/{chapterId}`  
**接口描述**: 获取章节详细信息

#### 请求参数
- **chapterId** (path): 章节ID，必填

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "responseCode": "SUCCESS",
  "responseMessage": "获取章节详情成功",
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
**接口路径**: `POST /api/v1/content/chapters/{chapterId}/publish`  
**接口描述**: 发布章节

#### 请求参数
- **chapterId** (path): 章节ID，必填
- **authorId** (query): 作者ID，必填

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "responseCode": "SUCCESS",
  "responseMessage": "章节发布成功",
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
**接口描述**: 增加内容浏览量

#### 请求参数
- **id** (path): 内容ID，必填
- **increment** (query): 增加数量，可选，默认1

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "responseCode": "SUCCESS",
  "responseMessage": "浏览量更新成功",
  "data": 15421
}
```

---

### 2. 增加点赞数
**接口路径**: `POST /api/v1/content/{id}/like`  
**接口描述**: 增加内容点赞数

#### 请求参数
- **id** (path): 内容ID，必填
- **increment** (query): 增加数量，可选，默认1

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "responseCode": "SUCCESS",
  "responseMessage": "点赞数更新成功",
  "data": 893
}
```

---

### 3. 增加评论数
**接口路径**: `POST /api/v1/content/{id}/comment`  
**接口描述**: 增加内容评论数

#### 请求参数
- **id** (path): 内容ID，必填
- **increment** (query): 增加数量，可选，默认1

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "responseCode": "SUCCESS",
  "responseMessage": "评论数更新成功",
  "data": 157
}
```

---

### 4. 增加收藏数
**接口路径**: `POST /api/v1/content/{id}/favorite`  
**接口描述**: 增加内容收藏数

#### 请求参数
- **id** (path): 内容ID，必填
- **increment** (query): 增加数量，可选，默认1

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "responseCode": "SUCCESS",
  "responseMessage": "收藏数更新成功",
  "data": 235
}
```

---

### 5. 更新评分
**接口路径**: `POST /api/v1/content/{id}/score`  
**接口描述**: 更新内容评分

#### 请求参数
- **id** (path): 内容ID，必填
- **score** (query): 评分值（1-10），必填

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "responseCode": "SUCCESS",
  "responseMessage": "评分更新成功",
  "data": 8.5
}
```

---

### 6. 获取统计信息
**接口路径**: `GET /api/v1/content/{id}/statistics`  
**接口描述**: 获取内容统计信息

#### 请求参数
- **id** (path): 内容ID，必填

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "responseCode": "SUCCESS",
  "responseMessage": "获取统计信息成功",
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
**接口描述**: 查询作者的内容列表（直接PageResponse响应）

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
**接口描述**: 查询分类下的内容列表（直接PageResponse响应）

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
**接口描述**: 根据关键词搜索内容（直接PageResponse响应）

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
- **timeRange** (query): 时间范围（天），可选
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

### 1. 更新作者信息
**接口路径**: `POST /api/v1/content/sync/author`  
**接口描述**: 同步更新内容中的作者冗余信息

#### 请求参数
```json
{
  "authorId": 12345,                              // 必填，作者ID
  "nickname": "新昵称",                           // 必填，新昵称
  "avatar": "https://example.com/new-avatar.jpg"  // 必填，新头像
}
```

---

### 2. 更新分类信息
**接口路径**: `POST /api/v1/content/sync/category`  
**接口描述**: 同步更新内容中的分类冗余信息

#### 请求参数
- **categoryId** (query): 分类ID，必填
- **categoryName** (query): 新分类名称，必填

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "responseCode": "SUCCESS",
  "responseMessage": "分类信息同步成功",
  "data": 25
}
```

---

### 3. 内容审核
**接口路径**: `POST /api/v1/content/{id}/review`  
**接口描述**: 审核内容

#### 请求参数
- **id** (path): 内容ID，必填

```json
{
  "reviewStatus": "APPROVED",          // 必填，审核状态：APPROVED/REJECTED
  "reviewerId": 67890,                 // 必填，审核人ID
  "reviewComment": "内容质量良好，通过审核"  // 可选，审核意见
}
```

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "responseCode": "SUCCESS",
  "responseMessage": "内容审核完成",
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

## 错误码说明

| 错误码 | 说明 |
|--------|------|
| CONTENT_NOT_FOUND | 内容不存在 |
| CHAPTER_NOT_FOUND | 章节不存在 |
| CONTENT_ALREADY_PUBLISHED | 内容已发布 |
| CHAPTER_NUM_EXISTS | 章节号已存在 |
| INVALID_CONTENT_TYPE | 无效的内容类型 |
| AUTHOR_PERMISSION_DENIED | 作者权限不足 |
| CONTENT_UNDER_REVIEW | 内容正在审核中 |
| CONTENT_REJECTED | 内容审核未通过 |

---

**最后更新**: 2024-01-16  
**文档版本**: v1.0.0