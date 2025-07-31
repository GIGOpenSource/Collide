# Collide 内容服务 API 文档

## 概述

Collide 内容服务提供完整的内容管理功能，包括内容发布、章节管理、内容查询、统计分析、**内容付费**和**购买管理**等核心功能。支持多种内容类型：小说、漫画、视频、文章、音频等。

**服务版本**: v2.0.0 (内容付费版)  
**基础路径**: `/api/v1/content`  
**Dubbo服务**: `collide-content`  
**设计理念**: 基于content-simple.sql的双表设计，支持内容付费、用户购买、权限验证等完整商业化功能

## 🚀 性能特性

- **🔥 跨模块集成**: 集成点赞、收藏服务，提供一站式内容互动功能
- **⚡ 简洁设计**: 双表设计，优化查询性能  
- **📊 实时统计**: 跨模块数据聚合，统计信息实时更新
- **🔒 数据一致性**: 统计数据自动同步，保证数据一致性
- **🛠️ JSON序列化优化**: tags和contentData字段正确序列化为JSON格式，避免转义字符串问题
- **💰 内容付费体系**: 支持多种付费模式：免费、金币付费、VIP免费、限时付费
- **🛒 购买管理**: 完整的购买流程、权限验证、订单处理和退款管理
- **🎯 个性化推荐**: 基于用户购买历史和行为的智能内容推荐

## 📝 v2.0.0 更新日志

**💰 内容付费体系** (最新)
- 🎉 **新增ContentPaymentController**: 完整的付费配置管理API (19个接口)
- 🛒 **新增ContentPurchaseController**: 用户购买和权限管理API (15个接口)
- 🎯 **多种付费模式**: FREE、COIN_PAY、VIP_FREE、TIME_LIMITED
- 💳 **购买流程管理**: 购买、试读、权限验证、订单处理、退款
- 📊 **销售统计分析**: 销量排行、收入统计、转化率分析、价格优化建议
- 🔐 **权限验证体系**: 批量权限检查、访问记录、有效期管理

**🛠️ JSON序列化修复**
- ✅ **tags字段**: 从 `"[\"技术\", \"编程\"]"` 正确序列化为 `["技术", "编程"]`
- ✅ **contentData字段**: 从 `"{\"sections\": 10}"` 正确序列化为 `{"sections": 10}`
- 🔧 **技术实现**: 使用 `@JsonRawValue` 注解确保JSON字段正确输出
- ⚡ **性能优化**: 在转换方法中增加空值安全处理

**🏗️ 控制器架构优化**
- 📋 **职责分离**: ContentController专注内容核心管理，PaymentController管理付费配置，PurchaseController处理购买业务
- 🛤️ **路径规范**: `/api/v1/content`、`/api/v1/content/payment`、`/api/v1/content/purchase`
- 🔄 **协同工作**: 三个控制器协同提供完整的内容商业化解决方案

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

> **📋 控制器架构说明**  
> Collide内容服务采用**分层控制器架构**，确保职责单一和功能清晰：
> - **ContentController** (`/api/v1/content`) - 内容核心管理 (30个接口)
> - **ContentPaymentController** (`/api/v1/content/payment`) - 付费配置管理 (19个接口) 💰
> - **ContentPurchaseController** (`/api/v1/content/purchase`) - 购买业务管理 (15个接口) 🛒

---

## ContentController - 内容核心管理 (30个接口)

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

### 基础统计接口 (6个)
| 接口路径 | 方法 | 功能描述 | 响应格式 |
|---------|-----|---------|----------|
| `POST /{id}/view` | POST | 增加浏览量 | Result<Long> |
| `POST /{id}/like-count` | POST | 增加点赞数 | Result<Long> |
| `POST /{id}/comment` | POST | 增加评论数 | Result<Long> |
| `POST /{id}/favorite-count` | POST | 增加收藏数 | Result<Long> |
| `POST /{id}/score` | POST | 更新评分 | Result<Double> |
| `GET /{id}/statistics` | GET | 获取内容基础统计 | Result<Map<String, Object>> |

### 内容检索接口 (5个) - 直接PageResponse响应
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

### 社交集成接口 (5个) 🔥
| 接口路径 | 方法 | 功能描述 | 响应格式 |
|---------|-----|---------|----------|
| `GET /{id}/like/status` | GET | 获取用户点赞状态 | Result<Boolean> |
| `POST /{id}/like` | POST | 点赞内容 | Result<Boolean> |
| `GET /{id}/favorite/status` | GET | 获取用户收藏状态 | Result<Boolean> |
| `POST /{id}/favorite` | POST | 收藏内容 | Result<Boolean> |
| `GET /{id}/interaction` | GET | 获取用户互动状态 | Result<Map<String, Object>> |

---

## ContentPaymentController - 付费配置管理 (19个接口) 💰

### 付费配置管理 (6个)
| 接口路径 | 方法 | 功能描述 | 响应格式 |
|---------|-----|---------|----------|
| `POST /payment/config` | POST | 创建付费配置 | Result<ContentPaymentConfigResponse> |
| `PUT /payment/config/{configId}` | PUT | 更新付费配置 | Result<ContentPaymentConfigResponse> |
| `DELETE /payment/config/{configId}` | DELETE | 删除付费配置 | Result<Void> |
| `GET /payment/config/content/{contentId}` | GET | 获取付费配置详情 | Result<ContentPaymentConfigResponse> |
| `POST /payment/config/batch` | POST | 批量设置付费配置 | Result<Map<String, Object>> |
| `GET /payment/price/content/{contentId}` | GET | 获取内容价格信息 | Result<Map<String, Object>> |

### 付费内容查询 (4个)
| 接口路径 | 方法 | 功能描述 | 响应格式 |
|---------|-----|---------|----------|
| `POST /payment/content/query` | POST | 查询付费内容列表 | Result<PageResponse<PaidContentResponse>> |
| `GET /payment/content/free` | GET | 获取免费内容列表 | Result<PageResponse<PaidContentResponse>> |
| `GET /payment/content/vip` | GET | 获取VIP内容列表 | Result<PageResponse<PaidContentResponse>> |
| `GET /payment/content/discounted` | GET | 获取折扣内容列表 | Result<PageResponse<PaidContentResponse>> |

### 推荐与统计 (5个)
| 接口路径 | 方法 | 功能描述 | 响应格式 |
|---------|-----|---------|----------|
| `GET /payment/recommend/hot` | GET | 获取热门付费内容 | Result<List<PaidContentResponse>> |
| `GET /payment/stats/sales-ranking` | GET | 获取销售排行榜 | Result<List<ContentPaymentConfigResponse>> |
| `GET /payment/stats/overview` | GET | 获取付费统计概览 | Result<Map<String, Object>> |
| `GET /payment/analysis/revenue/content/{contentId}` | GET | 获取内容收益分析 | Result<Map<String, Object>> |
| `GET /payment/analysis/market-trend` | GET | 获取市场趋势分析 | Result<Map<String, Object>> |

### 配置状态管理 (4个)
| 接口路径 | 方法 | 功能描述 | 响应格式 |
|---------|-----|---------|----------|
| `PUT /payment/config/content/{contentId}/enable` | PUT | 启用付费配置 | Result<Void> |
| `PUT /payment/config/content/{contentId}/disable` | PUT | 禁用付费配置 | Result<Void> |
| `PUT /payment/config/batch/status` | PUT | 批量更新配置状态 | Result<Map<String, Object>> |
| `PUT /payment/config/sync/content/{contentId}` | PUT | 同步内容状态 | Result<Void> |

---

## ContentPurchaseController - 购买业务管理 (15个接口) 🛒

### 购买功能 (3个)
| 接口路径 | 方法 | 功能描述 | 响应格式 |
|---------|-----|---------|----------|
| `POST /purchase/buy` | POST | 购买内容 | Result<Map<String, Object>> |
| `GET /purchase/info` | GET | 获取购买信息 | Result<Map<String, Object>> |
| `POST /purchase/trial` | POST | 申请试读 | Result<Map<String, Object>> |

### 权限验证 (3个)
| 接口路径 | 方法 | 功能描述 | 响应格式 |
|---------|-----|---------|----------|
| `GET /purchase/access/check` | GET | 检查访问权限 | Result<Map<String, Object>> |
| `POST /purchase/access/batch-check` | POST | 批量检查访问权限 | Result<Map<Long, Boolean>> |
| `POST /purchase/access/record` | POST | 记录内容访问 | Result<Void> |

### 购买记录查询 (5个)
| 接口路径 | 方法 | 功能描述 | 响应格式 |
|---------|-----|---------|----------|
| `POST /purchase/user/purchases` | POST | 查询用户购买记录 | Result<PageResponse<ContentPurchaseResponse>> |
| `GET /purchase/{purchaseId}` | GET | 获取购买记录详情 | Result<ContentPurchaseResponse> |
| `GET /purchase/user/{userId}/valid` | GET | 查询用户有效购买 | Result<List<ContentPurchaseResponse>> |
| `GET /purchase/stats/user/{userId}` | GET | 获取用户购买统计 | Result<Map<String, Object>> |
| `GET /purchase/stats/popular` | GET | 获取热门购买内容排行 | Result<List<Map<String, Object>>> |

### 订单处理 (2个)
| 接口路径 | 方法 | 功能描述 | 响应格式 |
|---------|-----|---------|----------|
| `POST /purchase/order/{orderId}/success` | POST | 处理订单支付成功 | Result<ContentPurchaseResponse> |
| `POST /purchase/{purchaseId}/refund` | POST | 处理退款申请 | Result<Void> |

### 个性化推荐 (2个)
| 接口路径 | 方法 | 功能描述 | 响应格式 |
|---------|-----|---------|----------|
| `GET /purchase/user/{userId}/recommendations` | GET | 获取用户内容推荐 | Result<List<Long>> |
| `GET /purchase/suggestion/{userId}` | GET | 获取购买建议 | Result<Map<String, Object>> |

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
  "contentData": {"chapters": 15},               // 可选，更新内容数据JSON对象
  "coverUrl": "https://example.com/new-cover.jpg", // 可选，更新封面
  "tags": ["Java", "设计模式", "GOF"],            // 可选，更新标签JSON数组
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

## 付费配置管理 API 💰

### 1. 创建付费配置
**接口路径**: `POST /api/v1/content/payment/config`  
**接口描述**: 为内容设置付费策略和价格

#### 请求参数
```json
{
  "contentId": 98765,                           // 必填，内容ID
  "paymentType": "COIN_PAY",                   // 必填，付费类型：FREE/COIN_PAY/VIP_FREE/TIME_LIMITED
  "coinPrice": 100,                            // 金币价格
  "originalPrice": 150,                        // 原价（用于折扣显示）
  "trialEnabled": true,                        // 是否支持试读
  "trialContent": "试读内容...",                // 试读内容
  "vipFree": false,                           // VIP是否免费
  "vipOnly": false,                           // 是否VIP专享
  "permanentAccess": true,                     // 是否永久访问
  "validDays": 30,                            // 有效天数（非永久时使用）
  "discountRate": 0.8,                        // 折扣率
  "discountStartTime": "2024-01-20T00:00:00", // 折扣开始时间
  "discountEndTime": "2024-01-30T23:59:59"    // 折扣结束时间
}
```

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "付费配置创建成功",
  "data": {
    "id": 123456,
    "contentId": 98765,
    "paymentType": "COIN_PAY",
    "paymentTypeDesc": "金币付费",
    "coinPrice": 100,
    "originalPrice": 150,
    "hasDiscount": true,
    "effectivePrice": 120,
    "trialEnabled": true,
    "vipFree": false,
    "permanentAccess": true,
    "status": "ACTIVE",
    "createTime": "2024-01-16T10:30:00"
  }
}
```

---

### 2. 查询付费内容列表
**接口路径**: `POST /api/v1/content/payment/content/query`  
**接口描述**: 根据付费类型、价格等条件查询内容

#### 请求参数
```json
{
  "userId": 12345,                            // 可选，用户ID（用于个性化）
  "paymentType": "COIN_PAY",                  // 可选，付费类型
  "contentType": "NOVEL",                     // 可选，内容类型
  "categoryId": 1001,                         // 可选，分类ID
  "minPrice": 50,                             // 可选，最小价格
  "maxPrice": 200,                            // 可选，最大价格
  "vipFreeOnly": false,                       // 可选，是否只显示VIP免费
  "trialEnabledOnly": false,                  // 可选，是否只显示支持试读
  "keyword": "Java",                          // 可选，关键词搜索
  "sortBy": "price_asc",                      // 排序：price_asc/price_desc/sales_desc/hot
  "page": 1,                                  // 页码
  "size": 20                                  // 每页大小
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
        "contentId": 98765,
        "paymentType": "COIN_PAY",
        "paymentTypeDesc": "金币付费",
        "coinPrice": 100,
        "originalPrice": 150,
        "hasDiscount": true,
        "title": "Java设计模式详解",
        "coverUrl": "https://example.com/cover.jpg",
        "authorNickname": "技术大师",
        "categoryName": "编程技术"
      }
    ],
    "currentPage": 1,
    "pageSize": 20,
    "totalPage": 3,
    "total": 56
  }
}
```

---

### 3. 获取内容价格信息
**接口路径**: `GET /api/v1/content/payment/price/content/{contentId}`  
**接口描述**: 获取指定内容的价格详情

#### 请求参数
- **contentId** (path): 内容ID，必填
- **userId** (query): 用户ID（用于个性化价格），可选

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "获取价格信息成功",
  "data": {
    "contentId": 98765,
    "paymentType": "COIN_PAY",
    "coinPrice": 100,
    "originalPrice": 150,
    "actualPrice": 120,
    "hasDiscount": true,
    "discountRate": 0.8,
    "vipFree": false,
    "trialEnabled": true,
    "permanentAccess": true,
    "validDays": null,
    "userCanAccess": false,
    "userVipStatus": false
  }
}
```

---

### 4. 获取销售排行榜
**接口路径**: `GET /api/v1/content/payment/stats/sales-ranking`  
**接口描述**: 按销量排序的内容排行榜

#### 请求参数
- **limit** (query): 返回数量，可选，默认10

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "code": "SUCCESS", 
  "message": "获取销售排行榜成功",
  "data": [
    {
      "contentId": 98765,
      "title": "Java设计模式详解",
      "paymentType": "COIN_PAY",
      "coinPrice": 100,
      "totalSales": 1250,
      "totalRevenue": 125000,
      "ranking": 1,
      "salesGrowth": 15.8
    }
  ]
}
```

---

## 购买业务管理 API 🛒

### 1. 购买内容
**接口路径**: `POST /api/v1/content/purchase/buy`  
**接口描述**: 用户购买付费内容，验证权限、价格等

#### 请求参数
```json
{
  "userId": 12345,                            // 必填，用户ID
  "contentId": 98765,                         // 必填，内容ID
  "confirmedPrice": 100,                      // 必填，用户确认的价格
  "paymentMethod": "COIN",                    // 必填，支付方式：COIN/ALIPAY/WECHAT
  "couponId": 123,                           // 可选，优惠券ID
  "deviceInfo": "iPhone 13",                 // 可选，设备信息
  "clientIp": "192.168.1.1"                 // 可选，客户端IP
}
```

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "购买成功",
  "data": {
    "orderId": 789456,
    "orderNo": "ORDER_20240116_001",
    "purchaseId": 654321,
    "status": "PAID",
    "actualPrice": 100,
    "paymentUrl": null,
    "validUntil": "2024-02-16T10:30:00",
    "accessGranted": true
  }
}
```

---

### 2. 获取购买信息
**接口路径**: `GET /api/v1/content/purchase/info`  
**接口描述**: 获取内容的购买信息，包括价格、折扣、权限等

#### 请求参数
- **userId** (query): 用户ID，必填
- **contentId** (query): 内容ID，必填

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "获取购买信息成功",
  "data": {
    "contentId": 98765,
    "title": "Java设计模式详解",
    "paymentType": "COIN_PAY",
    "originalPrice": 150,
    "currentPrice": 120,
    "actualPrice": 100,
    "hasDiscount": true,
    "discountRate": 0.8,
    "userBalance": 500,
    "canAfford": true,
    "alreadyPurchased": false,
    "vipCanAccess": false,
    "trialAvailable": true,
    "supportedPayments": ["COIN", "ALIPAY", "WECHAT"]
  }
}
```

---

### 3. 检查访问权限
**接口路径**: `GET /api/v1/content/purchase/access/check`  
**接口描述**: 验证用户是否可以访问指定内容

#### 请求参数
- **userId** (query): 用户ID，必填
- **contentId** (query): 内容ID，必填

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "权限检查完成",
  "data": {
    "hasAccess": true,
    "accessType": "PURCHASED",
    "purchaseId": 654321,
    "validUntil": "2024-02-16T10:30:00",
    "remainingDays": 25,
    "trialUsed": false,
    "reason": "用户已购买此内容"
  }
}
```

---

### 4. 查询用户购买记录
**接口路径**: `POST /api/v1/content/purchase/user/purchases`  
**接口描述**: 分页查询用户的购买历史

#### 请求参数
```json
{
  "userId": 12345,                           // 必填，用户ID
  "contentType": "NOVEL",                    // 可选，内容类型
  "status": "ACTIVE",                        // 可选，购买状态：ACTIVE/EXPIRED/REFUNDED
  "onlyValid": true,                         // 可选，是否只查询有效购买
  "sortBy": "purchase_time",                 // 可选，排序字段
  "sortOrder": "DESC",                       // 可选，排序方向
  "page": 1,                                 // 页码
  "size": 20                                 // 每页大小
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
        "purchaseId": 654321,
        "orderId": 789456,
        "orderNo": "ORDER_20240116_001",
        "contentId": 98765,
        "contentTitle": "Java设计模式详解",
        "coinAmount": 100,
        "actualAmount": 100,
        "purchaseTime": "2024-01-16T10:30:00",
        "validUntil": "2024-02-16T10:30:00",
        "status": "ACTIVE",
        "accessCount": 15,
        "lastAccessTime": "2024-01-20T15:45:00"
      }
    ],
    "currentPage": 1,
    "pageSize": 20,
    "totalPage": 3,
    "total": 56
  }
}
```

---

### 5. 获取用户购买统计
**接口路径**: `GET /api/v1/content/purchase/stats/user/{userId}`  
**接口描述**: 统计用户的购买数量、消费金额等

#### 请求参数
- **userId** (path): 用户ID，必填

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "获取用户购买统计成功",
  "data": {
    "userId": 12345,
    "totalPurchases": 25,
    "totalAmount": 2500,
    "activePurchases": 20,
    "expiredPurchases": 5,
    "favoriteCategory": "编程技术",
    "averagePrice": 100,
    "monthlySpending": 500,
    "lastPurchaseTime": "2024-01-20T10:30:00",
    "membershipLevel": "GOLD"
  }
}
```

---

### 6. 获取用户内容推荐
**接口路径**: `GET /api/v1/content/purchase/user/{userId}/recommendations`  
**接口描述**: 基于购买历史为用户推荐内容

#### 请求参数
- **userId** (path): 用户ID，必填
- **limit** (query): 推荐数量，可选，默认10

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "code": "SUCCESS", 
  "message": "获取推荐成功",
  "data": [98766, 98767, 98768, 98769, 98770]
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

### 付费配置相关错误码 💰
| 错误码 | 说明 | 解决方案 |
|--------|------|----------|
| PAYMENT_CONFIG_NOT_FOUND | 付费配置不存在 | 检查内容是否已配置付费 |
| PAYMENT_CONFIG_CREATE_ERROR | 创建付费配置失败 | 检查配置参数和内容状态 |
| PAYMENT_CONFIG_UPDATE_ERROR | 更新付费配置失败 | 检查配置ID和权限 |
| PAYMENT_CONFIG_DELETE_ERROR | 删除付费配置失败 | 检查配置状态和依赖关系 |
| PAYMENT_TYPE_INVALID | 付费类型无效 | 使用有效的付费类型 |
| PRICE_INVALID_RANGE | 价格超出有效范围 | 检查价格设置 |
| DISCOUNT_CONFIG_ERROR | 折扣配置错误 | 检查折扣时间和比例 |
| VIP_CONFIG_CONFLICT | VIP配置冲突 | 检查VIP免费和专享设置 |

### 购买业务相关错误码 🛒
| 错误码 | 说明 | 解决方案 |
|--------|------|----------|
| PURCHASE_FAILED | 购买失败 | 检查用户余额和内容状态 |
| INSUFFICIENT_BALANCE | 余额不足 | 用户需要充值 |
| ALREADY_PURCHASED | 已经购买过此内容 | 检查购买记录 |
| CONTENT_NOT_FOR_SALE | 内容不可购买 | 检查内容付费配置 |
| PRICE_CHANGED | 价格已变更 | 重新获取最新价格 |
| ORDER_NOT_FOUND | 订单不存在 | 检查订单ID |
| ORDER_ALREADY_PAID | 订单已支付 | 避免重复支付 |
| PAYMENT_TIMEOUT | 支付超时 | 重新发起支付 |
| REFUND_NOT_ALLOWED | 不允许退款 | 检查退款政策和时限 |
| ACCESS_DENIED | 访问被拒绝 | 用户没有访问权限 |
| ACCESS_EXPIRED | 访问已过期 | 需要重新购买 |
| TRIAL_ALREADY_USED | 试读已使用 | 每个用户只能试读一次 |
| TRIAL_NOT_AVAILABLE | 试读不可用 | 内容不支持试读 |

### 推荐系统错误码 🎯
| 错误码 | 说明 | 解决方案 |
|--------|------|----------|
| RECOMMENDATION_FAILED | 推荐失败 | 检查推荐服务状态 |
| INSUFFICIENT_DATA | 数据不足 | 用户购买历史较少 |
| RECOMMENDATION_EMPTY | 无可推荐内容 | 扩大推荐范围 |

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

### 社交功能增强 🔥
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

### 付费配置管理 💰
```javascript
// 创建付费配置
const paymentConfigResponse = await fetch('/api/v1/content/payment/config', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    contentId: 98765,
    paymentType: "COIN_PAY",
    coinPrice: 100,
    originalPrice: 150,
    trialEnabled: true,
    vipFree: false,
    permanentAccess: true,
    discountRate: 0.8,
    discountStartTime: "2024-01-20T00:00:00",
    discountEndTime: "2024-01-30T23:59:59"
  })
});

// 获取内容价格信息
const priceResponse = await fetch('/api/v1/content/payment/price/content/98765?userId=12345');
const priceInfo = await priceResponse.json();
console.log('价格信息:', priceInfo.data);

// 查询付费内容列表
const paidContentResponse = await fetch('/api/v1/content/payment/content/query', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    paymentType: "COIN_PAY",
    minPrice: 50,
    maxPrice: 200,
    sortBy: "price_asc",
    page: 1,
    size: 20
  })
});

// 获取销售排行榜
const salesRankingResponse = await fetch('/api/v1/content/payment/stats/sales-ranking?limit=10');
const salesRanking = await salesRankingResponse.json();
console.log('销售排行:', salesRanking.data);
```

### 购买业务管理 🛒
```javascript
// 获取购买信息（购买前确认）
const purchaseInfoResponse = await fetch('/api/v1/content/purchase/info?userId=12345&contentId=98765');
const purchaseInfo = await purchaseInfoResponse.json();
console.log('购买信息:', purchaseInfo.data);

// 购买内容
const buyResponse = await fetch('/api/v1/content/purchase/buy', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    userId: 12345,
    contentId: 98765,
    confirmedPrice: 100,
    paymentMethod: "COIN",
    deviceInfo: "iPhone 13",
    clientIp: "192.168.1.1"
  })
});
const buyResult = await buyResponse.json();
console.log('购买结果:', buyResult.data);

// 检查访问权限
const accessResponse = await fetch('/api/v1/content/purchase/access/check?userId=12345&contentId=98765');
const accessInfo = await accessResponse.json();
console.log('访问权限:', accessInfo.data);

// 查询用户购买记录
const purchasesResponse = await fetch('/api/v1/content/purchase/user/purchases', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    userId: 12345,
    onlyValid: true,
    sortBy: "purchase_time",
    sortOrder: "DESC",
    page: 1,
    size: 20
  })
});
const purchases = await purchasesResponse.json();
console.log('购买记录:', purchases.data);

// 获取用户内容推荐
const recommendationsResponse = await fetch('/api/v1/content/purchase/user/12345/recommendations?limit=10');
const recommendations = await recommendationsResponse.json();
console.log('推荐内容ID列表:', recommendations.data);

// 获取用户购买统计
const userStatsResponse = await fetch('/api/v1/content/purchase/stats/user/12345');
const userStats = await userStatsResponse.json();
console.log('用户购买统计:', userStats.data);
```

### 完整购买流程示例
```javascript
// 完整的内容购买流程
async function purchaseContent(userId, contentId) {
  try {
    // 1. 获取购买信息
    const infoResponse = await fetch(`/api/v1/content/purchase/info?userId=${userId}&contentId=${contentId}`);
    const info = await infoResponse.json();
    
    if (!info.success) {
      throw new Error(info.message);
    }
    
    const purchaseInfo = info.data;
    console.log('购买信息:', purchaseInfo);
    
    // 2. 检查用户是否已购买
    if (purchaseInfo.alreadyPurchased) {
      console.log('用户已购买此内容');
      return;
    }
    
    // 3. 检查余额是否充足
    if (!purchaseInfo.canAfford) {
      console.log('用户余额不足，需要充值');
      return;
    }
    
    // 4. 确认购买
    const confirmPurchase = confirm(`确认购买《${purchaseInfo.title}》，价格：${purchaseInfo.actualPrice}金币？`);
    if (!confirmPurchase) {
      return;
    }
    
    // 5. 发起购买
    const buyResponse = await fetch('/api/v1/content/purchase/buy', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        userId: userId,
        contentId: contentId,
        confirmedPrice: purchaseInfo.actualPrice,
        paymentMethod: "COIN"
      })
    });
    
    const buyResult = await buyResponse.json();
    
    if (buyResult.success) {
      console.log('购买成功！', buyResult.data);
      // 6. 可以立即访问内容
      window.location.href = `/content/${contentId}`;
    } else {
      console.error('购买失败:', buyResult.message);
    }
    
  } catch (error) {
    console.error('购买流程出错:', error);
  }
}

// 使用示例
purchaseContent(12345, 98765);
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

### v2.0.0 (2024-01-31) - 内容付费版
🎉 **重大更新**:
- 💰 **完整付费体系**: 新增ContentPaymentController (19个接口) 和 ContentPurchaseController (15个接口)
- 🛒 **购买业务管理**: 完整的购买流程、权限验证、订单处理、退款管理
- 🎯 **智能推荐系统**: 基于用户购买历史和行为的个性化内容推荐
- 🔐 **权限验证体系**: 批量权限检查、访问记录、有效期管理
- 📊 **销售统计分析**: 销量排行、收入统计、转化率分析、价格优化建议

💰 **内容付费功能**:
- 多种付费模式：FREE、COIN_PAY、VIP_FREE、TIME_LIMITED
- 价格策略管理：原价、折扣价、VIP专享、限时优惠
- 试读功能：支持内容试读，提升转化率
- 永久/限时访问：灵活的访问权限控制

🛒 **购买业务功能**:
- 购买流程：价格确认、余额检查、订单生成、支付处理
- 权限管理：实时权限验证、批量权限检查、访问记录
- 订单管理：支付成功处理、退款申请、订单状态追踪
- 用户统计：购买统计、消费分析、会员等级管理

🔥 **社交功能增强**:
- 用户点赞状态查询和切换
- 用户收藏状态查询和切换
- 一次性获取用户互动状态
- 实时统计数据同步

🏗️ **架构优化**:
- 三层控制器架构：职责分离，扩展性强
- 统一响应格式 (Result<T> 和 PageResponse<T>)
- 增强的错误码体系：覆盖付费、购买、推荐场景
- 跨模块数据聚合：实时统计更新

### v1.0.0 (2024-01-01) - 基础版
- 基础内容管理功能（创建、更新、删除、查询）
- 章节管理和发布功能
- 内容统计和评分系统

---

**最后更新**: 2024-01-31  
**文档版本**: v2.0.0 (内容付费版)  
**控制器架构**: ContentController + ContentPaymentController + ContentPurchaseController
**总接口数**: 64个 (内容核心30个 + 付费配置19个 + 购买业务15个)