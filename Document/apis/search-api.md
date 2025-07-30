# Collide 搜索服务 API 文档

## 概述

Collide 搜索服务提供强大的全文搜索功能，包括内容搜索、用户搜索、商品搜索、智能推荐、搜索历史管理、Tag混合搜索等核心功能。基于Elasticsearch构建，提供高性能、高相关性的搜索体验。

**服务版本**: v2.0.0 (缓存增强版)  
**基础路径**: `/api/v1/search`  
**Dubbo服务**: `collide-search` (version: 1.0.0)  
**设计理念**: 智能搜索引擎，提供精准匹配和个性化推荐，优化用户发现体验

## 🎯 核心特性

- **🚀 高性能缓存**: 基于JetCache的分布式缓存，显著提升搜索响应速度
- **🔍 智能搜索**: 支持全文搜索、模糊匹配、智能推荐
- **🏷️ Tag混合搜索**: 支持按标签同时搜索用户和内容，提供聚合结果
- **📈 搜索分析**: 完整的搜索统计和用户行为分析
- **⚡ 实时建议**: 智能搜索建议和热门关键词推荐
- **🔄 跨模块集成**: 通过Dubbo与用户、内容模块深度集成

## 🆕 v2.0.0 新增功能

- **Tag混合搜索**: 支持按标签同时搜索用户和内容的聚合接口
- **JetCache缓存**: 全面的缓存策略，提升搜索性能
- **API设计优化**: 统一使用 `currentPage` 参数，对齐系统设计风格
- **Swagger文档**: 完整的OpenAPI 3.0文档支持

---

## 通用搜索 API

### 1. 全站搜索
**接口路径**: `POST /api/v1/search`  
**接口描述**: 全站统一搜索入口，支持多类型内容搜索

#### 请求参数
```json
{
  "keyword": "Java编程",                   // 必填，搜索关键词
  "searchType": "all",                    // 可选，搜索类型：all/content/user/goods/tag
  "userId": 12345,                        // 可选，用户ID（用于个性化搜索）
  "currentPage": 1,                      // 可选，页码，默认1
  "pageSize": 20,                        // 可选，页面大小，默认20
  "sortBy": "relevance",                 // 可选，排序方式：relevance/time/popularity/rating
  "timeRange": 365,                      // 可选，时间范围（天），默认不限制
  "filters": {                           // 可选，搜索过滤条件
    "categoryId": 1001,
    "priceMin": 50.00,
    "priceMax": 200.00,
    "status": "published"
  },
  "highlightEnabled": true,              // 可选，是否高亮关键词，默认true
  "suggestionEnabled": true              // 可选，是否返回搜索建议，默认true
}
```

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "responseCode": "SUCCESS",
  "responseMessage": "搜索成功",
  "data": {
    "keyword": "Java编程",
    "searchType": "all",
    "totalCount": 156,
    "searchTime": 45,                    // 搜索耗时（毫秒）
    "hasMore": true,
    "currentPage": 1,
    "pageSize": 20,
    "results": [
      {
        "id": "content_98765",
        "type": "content",
        "title": "<em>Java</em><em>编程</em>实战教程",
        "description": "从零基础到高级开发的完整<em>Java</em>教程...",
        "url": "/content/98765",
        "score": 9.8,
        "author": "技术达人",
        "publishTime": "2024-01-15T10:30:00",
        "tags": ["Java", "编程", "教程"],
        "extra": {
          "categoryName": "编程技术",
          "viewCount": 1250,
          "likeCount": 89
        }
      },
      {
        "id": "goods_789012",
        "type": "goods",
        "title": "<em>Java</em><em>编程</em>入门到精通",
        "description": "专业的<em>Java</em>学习资料，包含视频和文档...",
        "url": "/goods/789012",
        "score": 9.5,
        "price": 99.00,
        "seller": "教育机构A",
        "extra": {
          "originalPrice": 129.00,
          "soldCount": 567,
          "rating": 4.8
        }
      }
    ],
    "aggregations": {                    // 聚合统计
      "typeCount": {
        "content": 89,
        "goods": 45,
        "user": 22
      },
      "categoryCount": {
        "编程技术": 67,
        "教育培训": 34,
        "其他": 55
      }
    },
    "suggestions": [                     // 搜索建议
      "Java编程入门",
      "Java编程实战",
      "Java编程教程"
    ],
    "relatedKeywords": [                 // 相关关键词
      "Spring框架",
      "MyBatis",
      "Java基础"
    ]
  }
}
```

---

### 2. 搜索建议
**接口路径**: `GET /api/v1/search/suggestions`  
**接口描述**: 获取搜索关键词建议

#### 请求参数
- **keyword** (query): 搜索关键词前缀，必填
- **searchType** (query): 搜索类型，可选
- **limit** (query): 返回数量，可选，默认10

#### 响应示例
```json
{
  "success": true,
  "data": {
    "keyword": "Java",
    "suggestions": [
      {
        "text": "Java编程",
        "score": 95,
        "count": 1250                    // 搜索次数
      },
      {
        "text": "Java基础",
        "score": 88,
        "count": 890
      },
      {
        "text": "Java面试",
        "score": 82,
        "count": 670
      }
    ]
  }
}
```

---

### 3. 热门搜索
**接口路径**: `GET /api/v1/search/hot`  
**接口描述**: 获取热门搜索关键词

#### 请求参数
- **searchType** (query): 搜索类型，可选
- **timeRange** (query): 时间范围（天），可选，默认7
- **limit** (query): 返回数量，可选，默认10

#### 响应示例
```json
{
  "success": true,
  "data": [
    {
      "keyword": "Java编程",
      "searchCount": 15680,
      "trend": "up",                     // 趋势：up/down/stable
      "ranking": 1,
      "growthRate": 15.8                 // 增长率（%）
    },
    {
      "keyword": "Python入门",
      "searchCount": 12340,
      "trend": "stable",
      "ranking": 2,
      "growthRate": 2.3
    }
  ]
}
```

---

## 内容搜索 API

### 1. 搜索内容
**接口路径**: `POST /api/v1/search/content`  
**接口描述**: 专门搜索内容（文章、视频等）

#### 请求参数
```json
{
  "keyword": "Java设计模式",             // 必填，搜索关键词
  "contentType": "NOVEL",               // 可选，内容类型
  "categoryId": 1001,                   // 可选，分类ID
  "authorId": 12345,                    // 可选，作者ID
  "status": "published",                // 可选，状态
  "minScore": 4.0,                      // 可选，最低评分
  "hasChapters": true,                  // 可选，是否有章节
  "sortBy": "relevance",                // 可选，排序方式
  "currentPage": 1,
  "pageSize": 20
}
```

---

### 2. 相关内容推荐
**接口路径**: `GET /api/v1/search/content/{contentId}/related`  
**接口描述**: 获取相关内容推荐

#### 请求参数
- **contentId** (path): 内容ID，必填
- **limit** (query): 推荐数量，可选，默认10
- **userId** (query): 用户ID，可选（用于个性化推荐）

---

## 商品搜索 API

### 1. 搜索商品
**接口路径**: `POST /api/v1/search/goods`  
**接口描述**: 专门搜索商品

#### 请求参数
```json
{
  "keyword": "编程教程",                 // 必填，搜索关键词
  "categoryId": 1001,                   // 可选，分类ID
  "brandId": 2001,                      // 可选，品牌ID
  "priceMin": 50.00,                    // 可选，最低价格
  "priceMax": 200.00,                   // 可选，最高价格
  "hasStock": true,                     // 可选，是否有库存
  "minRating": 4.0,                     // 可选，最低评分
  "goodsType": "digital",               // 可选，商品类型
  "tags": ["编程", "Java"],              // 可选，标签筛选
  "sortBy": "sales",                    // 可选，排序：relevance/price/sales/rating/time
  "sortOrder": "desc",                  // 可选，排序方向
  "currentPage": 1,
  "pageSize": 20
}
```

---

### 2. 商品推荐
**接口路径**: `GET /api/v1/search/goods/recommend`  
**接口描述**: 商品推荐

#### 请求参数
- **userId** (query): 用户ID，可选
- **goodsId** (query): 商品ID，可选（基于商品推荐相似商品）
- **categoryId** (query): 分类ID，可选
- **type** (query): 推荐类型：hot/similar/collaborative，可选
- **limit** (query): 推荐数量，可选，默认10

---

## 用户搜索 API

### 1. 搜索用户
**接口路径**: `POST /api/v1/search/users`  
**接口描述**: 搜索用户

#### 请求参数
```json
{
  "keyword": "技术达人",                 // 必填，搜索关键词
  "userType": "blogger",                // 可选，用户类型：normal/blogger/expert
  "isVerified": true,                   // 可选，是否认证用户
  "hasAvatar": true,                    // 可选，是否有头像
  "minFollowers": 100,                  // 可选，最少粉丝数
  "sortBy": "followers",                // 可选，排序：relevance/followers/activity/join_time
  "currentPage": 1,
  "pageSize": 20
}
```

---

### 2. 用户推荐
**接口路径**: `GET /api/v1/search/users/recommend`  
**接口描述**: 推荐用户关注

#### 请求参数
- **userId** (query): 当前用户ID，必填
- **type** (query): 推荐类型：similar/hot/new，可选
- **limit** (query): 推荐数量，可选，默认10

---

## Tag混合搜索 API

### 1. Tag混合搜索
**接口路径**: `GET /api/v1/search/tag/{tag}/mixed`  
**接口描述**: 根据标签同时搜索用户和内容，返回聚合结果

#### 请求参数
- **tag** (path): 标签名称，必填
- **currentPage** (query): 当前页码，可选，默认1
- **pageSize** (query): 页面大小，可选，默认20

#### 响应示例
```json
{
  "success": true,
  "datas": [
    {
      "type": "user",
      "id": 12345,
      "name": "技术达人",
      "avatar": "https://example.com/avatar.jpg",
      "bio": "专注Java技术分享",
      "followers": 1250,
      "isVerified": true,
      "tags": ["Java", "编程", "技术"]
    },
    {
      "type": "content",
      "id": 98765,
      "title": "Java编程实战教程",
      "description": "从零基础到高级开发的完整Java教程",
      "author": "技术达人",
      "viewCount": 15680,
      "likeCount": 892,
      "tags": ["Java", "编程", "教程"]
    }
  ],
  "total": 156,
  "currentPage": 1,
  "pageSize": 20,
  "totalPage": 8
}
```

**特性说明**:
- ⚡ **20分钟缓存**: 提升响应速度
- 🔄 **智能聚合**: 自动平衡用户和内容结果
- 🏷️ **精准匹配**: 基于标签的精确搜索

---

### 2. Tag搜索用户
**接口路径**: `GET /api/v1/search/tag/{tag}/users`  
**接口描述**: 根据标签搜索相关用户

#### 请求参数
- **tag** (path): 标签名称，必填
- **currentPage** (query): 当前页码，可选，默认1
- **pageSize** (query): 页面大小，可选，默认20

#### 响应示例
```json
{
  "success": true,
  "datas": [
    {
      "id": 12345,
      "name": "技术达人",
      "avatar": "https://example.com/avatar.jpg",
      "bio": "专注Java技术分享",
      "followers": 1250,
      "following": 89,
      "isVerified": true,
      "tags": ["Java", "编程", "技术"],
      "joinTime": "2023-01-15T10:30:00"
    }
  ],
  "total": 45,
  "currentPage": 1,
  "pageSize": 20,
  "totalPage": 3
}
```

**特性说明**:
- ⚡ **20分钟缓存**: 用户搜索结果缓存
- 🎯 **个性化排序**: 根据用户活跃度和影响力排序

---

### 3. Tag搜索内容
**接口路径**: `GET /api/v1/search/tag/{tag}/contents`  
**接口描述**: 根据标签搜索相关内容

#### 请求参数
- **tag** (path): 标签名称，必填
- **currentPage** (query): 当前页码，可选，默认1
- **pageSize** (query): 页面大小，可选，默认20

#### 响应示例
```json
{
  "success": true,
  "datas": [
    {
      "id": 98765,
      "title": "Java编程实战教程",
      "description": "从零基础到高级开发的完整Java教程",
      "contentType": "ARTICLE",
      "author": "技术达人",
      "authorId": 12345,
      "viewCount": 15680,
      "likeCount": 892,
      "commentCount": 156,
      "publishTime": "2024-01-15T10:30:00",
      "tags": ["Java", "编程", "教程"],
      "status": "published"
    }
  ],
  "total": 89,
  "currentPage": 1,
  "pageSize": 20,
  "totalPage": 5
}
```

**特性说明**:
- ⚡ **20分钟缓存**: 内容搜索结果缓存
- 📈 **智能排序**: 根据热度、时间、相关性综合排序

---

## 搜索历史 API

### 1. 保存搜索历史
**接口路径**: `POST /api/v1/search/history`  
**接口描述**: 保存用户搜索历史

#### 请求参数
```json
{
  "userId": 12345,                      // 必填，用户ID
  "keyword": "Java编程",                // 必填，搜索关键词
  "searchType": "content",              // 必填，搜索类型
  "resultCount": 156,                   // 可选，搜索结果数量
  "searchTime": 45,                     // 可选，搜索耗时
  "clientInfo": {                       // 可选，客户端信息
    "platform": "app",
    "version": "1.0.0",
    "deviceId": "device123"
  }
}
```

---

### 2. 获取搜索历史
**接口路径**: `GET /api/v1/search/history`  
**接口描述**: 分页获取用户搜索历史记录

#### 请求参数
- **userId** (query): 用户ID，必填
- **searchType** (query): 搜索类型，可选
- **currentPage** (query): 当前页码，可选，默认1
- **pageSize** (query): 页面大小，可选，默认20

#### 响应示例
```json
{
  "success": true,
  "datas": [
    {
      "id": 1001,
      "keyword": "Java编程",
      "searchType": "content",
      "searchTime": "2024-01-16T10:30:00",
      "resultCount": 156
    },
    {
      "id": 1002,
      "keyword": "Python入门",
      "searchType": "content",
      "searchTime": "2024-01-15T15:20:00",
      "resultCount": 89
    }
  ],
  "total": 25,
  "currentPage": 1,
  "pageSize": 20,
  "totalPage": 2
}
```

**特性说明**:
- ⚡ **60分钟缓存**: 搜索历史结果缓存
- 🔄 **自动清理**: 缓存失效时自动清理相关数据

---

### 3. 清除搜索历史
**接口路径**: `DELETE /api/v1/search/history/{userId}`  
**接口描述**: 清除用户搜索历史

#### 请求参数
- **userId** (path): 用户ID，必填
- **historyIds** (query): 指定历史记录ID列表，可选（为空则清除全部）

---

## 搜索分析 API

### 1. 搜索统计
**接口路径**: `GET /api/v1/search/statistics`  
**接口描述**: 获取搜索统计信息

#### 请求参数
- **timeRange** (query): 时间范围（天），可选，默认30
- **searchType** (query): 搜索类型，可选

#### 响应示例
```json
{
  "success": true,
  "data": {
    "totalSearches": 125000,             // 总搜索次数
    "uniqueKeywords": 8900,              // 独特关键词数
    "uniqueUsers": 15600,                // 搜索用户数
    "averageResultCount": 45.6,          // 平均搜索结果数
    "averageSearchTime": 82,             // 平均搜索耗时（毫秒）
    "clickThroughRate": 68.5,            // 点击率（%）
    "zeroResultRate": 5.2,               // 零结果率（%）
    "topKeywords": [                     // 热门关键词
      {
        "keyword": "Java编程",
        "count": 15680,
        "percentage": 12.5
      }
    ],
    "searchTrend": [                     // 搜索趋势
      {
        "date": "2024-01-16",
        "count": 4500
      }
    ]
  }
}
```

---

### 2. 关键词分析
**接口路径**: `GET /api/v1/search/analytics/keywords`  
**接口描述**: 关键词分析

#### 请求参数
- **keyword** (query): 关键词，可选
- **timeRange** (query): 时间范围，可选
- **searchType** (query): 搜索类型，可选

---

### 3. 用户搜索行为分析
**接口路径**: `GET /api/v1/search/analytics/user/{userId}`  
**接口描述**: 分析用户搜索行为

#### 请求参数
- **userId** (path): 用户ID，必填
- **timeRange** (query): 时间范围，可选

---

## 搜索配置 API

### 1. 更新搜索配置
**接口路径**: `POST /api/v1/search/config`  
**接口描述**: 更新搜索引擎配置

#### 请求参数
```json
{
  "maxResultCount": 1000,              // 最大搜索结果数
  "defaultPageSize": 20,               // 默认页面大小
  "maxPageSize": 100,                  // 最大页面大小
  "searchTimeout": 5000,               // 搜索超时时间（毫秒）
  "enableHighlight": true,             // 是否启用高亮
  "enableSuggestion": true,            // 是否启用搜索建议
  "enableAnalytics": true,             // 是否启用搜索分析
  "stopWords": ["的", "了", "在"],      // 停用词列表
  "synonyms": {                        // 同义词配置
    "Java": ["java", "JAVA"],
    "编程": ["程序设计", "coding"]
  }
}
```

---

### 2. 获取搜索配置
**接口路径**: `GET /api/v1/search/config`  
**接口描述**: 获取当前搜索配置

---

### 3. 重建搜索索引
**接口路径**: `POST /api/v1/search/index/rebuild`  
**接口描述**: 重建搜索索引

#### 请求参数
```json
{
  "indexType": "content",              // 必填，索引类型：content/goods/user/all
  "isFullRebuild": true,               // 必填，是否全量重建
  "batchSize": 1000,                   // 可选，批次大小
  "operatorId": 99999                  // 必填，操作人ID
}
```

---

## 🚀 缓存性能特性

### 缓存策略
- **搜索结果缓存**: 30分钟，提升常用搜索的响应速度
- **搜索历史缓存**: 60分钟，优化用户历史查询性能
- **热门搜索缓存**: 15分钟，快速响应热搜需求
- **搜索建议缓存**: 120分钟，长期缓存提升输入体验
- **Tag混合搜索缓存**: 20分钟，平衡实时性和性能

### 缓存优化
- **分布式缓存**: 基于JetCache的分布式缓存架构
- **智能失效**: 删除和更新操作自动清理相关缓存
- **缓存预热**: 系统自动预热热门搜索数据
- **性能监控**: 实时监控缓存命中率和响应时间

### 性能指标
- **搜索响应时间**: < 100ms (缓存命中)
- **缓存命中率**: > 85%
- **并发支持**: 10,000+ 并发搜索请求
- **数据同步**: 缓存数据与数据库实时同步

---

## 错误码说明

| 错误码 | 说明 |
|--------|------|
| SEARCH_KEYWORD_EMPTY | 搜索关键词为空 |
| SEARCH_KEYWORD_TOO_SHORT | 搜索关键词过短 |
| SEARCH_KEYWORD_TOO_LONG | 搜索关键词过长 |
| SEARCH_TYPE_INVALID | 搜索类型无效 |
| SEARCH_TIMEOUT | 搜索超时 |
| SEARCH_ENGINE_ERROR | 搜索引擎错误 |
| INDEX_NOT_EXISTS | 搜索索引不存在 |
| SEARCH_QUOTA_EXCEEDED | 搜索配额超限 |
| SEARCH_FILTER_INVALID | 搜索过滤条件无效 |
| SEARCH_SORT_INVALID | 搜索排序参数无效 |
| TAG_SEARCH_ERROR | Tag搜索失败 |
| MIXED_SEARCH_ERROR | 混合搜索失败 |
| CACHE_ERROR | 缓存系统错误 |
| USER_NOT_FOUND | 用户不存在 |
| CONTENT_NOT_FOUND | 内容不存在 |
| SEARCH_HISTORY_ERROR | 搜索历史操作失败 |
| HOT_SEARCH_ERROR | 热门搜索获取失败 |
| SUGGESTION_ERROR | 搜索建议获取失败 |
| PREFERENCE_ERROR | 用户偏好获取失败 |

---

## 🔄 API设计原则

### 统一响应格式
- **查询操作**: 直接返回 `PageResponse<Data>` 格式
- **创建/删除操作**: 返回 `Result<Void>` 格式（仅状态码）
- **更新操作**: 返回 `Result<Data>` 格式（包含更新后数据）

### 分页参数规范
- **currentPage**: 当前页码，从1开始
- **pageSize**: 每页大小，默认20，最大100
- **total**: 总记录数
- **totalPage**: 总页数

### 缓存键命名规范
- **格式**: `模块:功能:参数`
- **示例**: `search:result:keyword:Java:type:content:page:1`
- **过期策略**: 根据数据更新频率设置合理的过期时间

---

**最后更新**: 2024-01-16  
**文档版本**: v2.0.0