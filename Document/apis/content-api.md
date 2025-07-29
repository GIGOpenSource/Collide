# Collide 内容服务 API 文档

## 概述

Collide 内容服务提供完整的内容管理功能，包括内容发布、章节管理、内容查询、统计分析等核心功能。支持多种内容类型：小说、漫画、视频、文章、音频等。

**服务版本**: v1.0.0  
**基础路径**: `/api/v1/content`  
**Dubbo服务**: `collide-content`  
**设计理念**: 统一内容管理，支持多媒体内容，提供强大的内容组织和检索能力

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

---

### 3. 删除内容
**接口路径**: `DELETE /api/v1/content/{id}`  
**接口描述**: 删除内容（逻辑删除，状态更新为OFFLINE）

#### 请求参数
- **id** (path): 内容ID，必填
- **operatorId** (query): 操作人ID，必填

---

### 4. 获取内容详情
**接口路径**: `GET /api/v1/content/{id}`  
**接口描述**: 获取内容详细信息

#### 请求参数
- **id** (path): 内容ID，必填
- **includeOffline** (query): 是否包含下线内容，可选，默认false

---

### 5. 查询内容列表
**接口路径**: `POST /api/v1/content/query`  
**接口描述**: 分页查询内容列表，支持多种筛选条件

#### 请求参数
```json
{
  "pageNum": 1,                    // 页码（从1开始）
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

---

### 2. 获取内容章节列表
**接口路径**: `GET /api/v1/content/{contentId}/chapters`  
**接口描述**: 获取内容的章节列表

#### 请求参数
- **contentId** (path): 内容ID，必填
- **status** (query): 章节状态，可选
- **pageNum** (query): 页码，可选，默认1
- **pageSize** (query): 页面大小，可选，默认20

---

### 3. 获取章节详情
**接口路径**: `GET /api/v1/content/chapters/{chapterId}`  
**接口描述**: 获取章节详细信息

#### 请求参数
- **chapterId** (path): 章节ID，必填

---

### 4. 发布章节
**接口路径**: `POST /api/v1/content/chapters/{chapterId}/publish`  
**接口描述**: 发布章节

#### 请求参数
- **chapterId** (path): 章节ID，必填
- **authorId** (query): 作者ID，必填

---

## 统计管理 API

### 1. 增加浏览量
**接口路径**: `POST /api/v1/content/{id}/view`  
**接口描述**: 增加内容浏览量

#### 请求参数
- **id** (path): 内容ID，必填
- **increment** (query): 增加数量，可选，默认1

---

### 2. 增加点赞数
**接口路径**: `POST /api/v1/content/{id}/like`  
**接口描述**: 增加内容点赞数

---

### 3. 增加评论数
**接口路径**: `POST /api/v1/content/{id}/comment`  
**接口描述**: 增加内容评论数

---

### 4. 增加收藏数
**接口路径**: `POST /api/v1/content/{id}/favorite`  
**接口描述**: 增加内容收藏数

---

### 5. 更新评分
**接口路径**: `POST /api/v1/content/{id}/score`  
**接口描述**: 更新内容评分

#### 请求参数
- **id** (path): 内容ID，必填
- **score** (query): 评分值（1-10），必填

---

### 6. 获取统计信息
**接口路径**: `GET /api/v1/content/{id}/statistics`  
**接口描述**: 获取内容统计信息

---

## 内容查询 API

### 1. 根据作者查询内容
**接口路径**: `GET /api/v1/content/author/{authorId}`  
**接口描述**: 查询作者的内容列表

#### 请求参数
- **authorId** (path): 作者ID，必填
- **contentType** (query): 内容类型，可选
- **status** (query): 状态，可选
- **pageNum** (query): 页码，可选
- **pageSize** (query): 页面大小，可选

---

### 2. 根据分类查询内容
**接口路径**: `GET /api/v1/content/category/{categoryId}`  
**接口描述**: 查询分类下的内容列表

---

### 3. 搜索内容
**接口路径**: `POST /api/v1/content/search`  
**接口描述**: 搜索内容

#### 请求参数
```json
{
  "keyword": "Java设计模式",        // 必填，搜索关键词
  "contentType": "NOVEL",          // 可选，内容类型
  "pageNum": 1,                    // 可选，页码
  "pageSize": 20                   // 可选，页面大小
}
```

---

### 4. 获取热门内容
**接口路径**: `GET /api/v1/content/popular`  
**接口描述**: 获取热门内容列表

#### 请求参数
- **contentType** (query): 内容类型，可选
- **timeRange** (query): 时间范围（天），可选，默认7
- **pageNum** (query): 页码，可选
- **pageSize** (query): 页面大小，可选

---

### 5. 获取最新内容
**接口路径**: `GET /api/v1/content/latest`  
**接口描述**: 获取最新内容列表

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