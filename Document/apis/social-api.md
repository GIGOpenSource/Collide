# Collide 社交服务 API 文档

## 概述

Collide 社交服务提供完整的社交功能，包括内容发布、互动管理、统计查询等核心功能。支持多媒体内容、付费内容、用户互动等特性。

**服务版本**: v2.0.0  
**基础路径**: `/api/social`  
**Dubbo服务**: `collide-social`  
**设计理念**: 构建活跃的社交生态，促进用户互动和内容传播  

### 🎯 核心特性
- **🔄 跨模块集成**: 通过Dubbo服务调用用户、标签等模块
- **⚡ 缓存优化**: 集成JetCache分布式缓存，提升查询性能
- **📊 数据聚合**: 智能聚合用户互动记录，提供统一的社交数据视图
- **🔒 权限控制**: 细粒度的隐私控制和内容权限管理
- **📱 响应式设计**: 支持分页、排序、筛选等灵活的数据查询方式
- **💰 付费内容**: 支持付费内容和金币支付模式

### 🆕 v2.0.0 新增功能
- ✅ **DDD架构重构**: 采用领域驱动设计，清晰的分层架构
- ✅ **批量操作优化**: 提供批量查询接口，减少网络请求
- ✅ **统计数据诊断**: 新增统计数据诊断和修复功能
- ✅ **用户信息增强**: 集成用户服务，自动填充作者信息
- ✅ **标签系统集成**: 支持内容标签展示和查询

---

## 内容管理 API

### 1. 创建内容
**接口路径**: `POST /api/social/content/create`  
**接口描述**: 发布新的社交内容

#### 请求参数
```json
{
  "userId": 12345,                    // 必填，发布者用户ID
  "categoryId": 100,                  // 必填，分类ID
  "contentType": 1,                   // 必填，内容类型：1-短视频,2-长视频,3-图片,4-文字
  "title": "今天学习了Java设计模式",    // 必填，内容标题
  "description": "分享我的学习心得",    // 可选，详细描述
  "mediaUrls": "[\"url1\", \"url2\"]", // 可选，媒体文件URL列表(JSON格式)
  "coverUrl": "https://example.com/cover.jpg", // 可选，封面图URL
  "duration": 120,                    // 可选，视频时长(秒)
  "mediaInfo": "{\"width\":1920,\"height\":1080}", // 可选，媒体信息(JSON格式)
  "isPaid": 0,                        // 可选，是否付费：0-免费,1-付费
  "price": 10,                        // 可选，价格(金币)
  "freeDuration": 30,                 // 可选，免费试看时长(秒)
  "privacy": 1                        // 可选，隐私设置：1-公开,2-仅关注者,3-私密
}
```

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "内容创建成功",
  "data": 789012
}
```

---

### 2. 更新内容
**接口路径**: `PUT /api/social/content/update`  
**接口描述**: 更新已发布的内容

#### 请求参数
```json
{
  "contentId": 789012,                // 必填，内容ID
  "userId": 12345,                    // 必填，用户ID
  "title": "修改后的标题",             // 可选，新标题
  "description": "修改后的描述",       // 可选，新描述
  "mediaUrls": "[\"new_url\"]",       // 可选，新的媒体URL列表
  "coverUrl": "new_cover.jpg",        // 可选，新封面
  "duration": 150,                    // 可选，新时长
  "mediaInfo": "{\"quality\":\"HD\"}", // 可选，新媒体信息
  "isPaid": 1,                        // 可选，是否付费
  "price": 20,                        // 可选，新价格
  "freeDuration": 60,                 // 可选，免费时长
  "privacy": 2                        // 可选，隐私设置
}
```

#### 响应示例
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "内容更新成功",
  "data": true
}
```

---

### 3. 删除内容
**接口路径**: `DELETE /api/social/content/{contentId}`  
**接口描述**: 删除指定内容

#### 请求参数
- **contentId** (path): 内容ID，必填
- **userId** (query): 用户ID，必填

#### 响应示例
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "内容删除成功",
  "data": true
}
```

---

### 4. 获取内容详情
**接口路径**: `GET /api/social/content/{contentId}`  
**接口描述**: 获取内容详细信息，自动增加浏览数

#### 请求参数
- **contentId** (path): 内容ID，必填
- **viewerUserId** (query): 查看者用户ID，可选

#### 响应示例
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "查询成功",
  "data": {
    "id": 789012,
    "userId": 12345,
    "categoryId": 100,
    "categoryPath": "技术/Java",
    "contentType": 1,
    "title": "今天学习了Java设计模式",
    "description": "分享我的学习心得",
    "mediaUrls": "[\"url1\", \"url2\"]",
    "coverUrl": "https://example.com/cover.jpg",
    "duration": 120,
    "mediaInfo": "{\"width\":1920,\"height\":1080}",
    "isPaid": 0,
    "price": 10,
    "freeDuration": 30,
    "purchaseCount": 0,
    "likeCount": 156,
    "commentCount": 42,
    "shareCount": 18,
    "favoriteCount": 89,
    "viewCount": 2341,
    "recommendScore": 85.6,
    "qualityScore": 92.3,
    "status": 1,
    "privacy": 1,
    "createTime": "2024-01-16T10:30:00",
    "updateTime": "2024-01-16T10:30:00",
    "authorName": "技术达人",
    "authorAvatar": "https://example.com/avatar.jpg",
    "isLiked": false,
    "isFavorited": false,
    "isPurchased": false,
    "tags": [
      {
        "id": 1,
        "tagName": "Java",
        "tagIcon": "java-icon.png",
        "weight": 100
      },
      {
        "id": 2,
        "tagName": "设计模式",
        "tagIcon": "pattern-icon.png",
        "weight": 90
      }
    ]
  }
}
```

---

### 5. 分页查询内容
**接口路径**: `POST /api/social/content/query`  
**接口描述**: 分页查询内容列表

#### 请求参数
```json
{
  "currentPage": 1,                   // 页码（从1开始）
  "pageSize": 20,                     // 每页大小
  "userId": 12345,                    // 可选，查询指定用户的内容
  "categoryId": 100,                  // 可选，查询指定分类的内容
  "contentType": 1,                   // 可选，内容类型过滤
  "keyword": "Java",                  // 可选，搜索关键词
  "queryType": 1,                     // 可选，查询类型：1-最新,2-热门,3-搜索
  "viewerUserId": 67890,              // 可选，查看者ID（用于权限检查）
  "sortBy": "create_time",            // 可选，排序字段
  "sortOrder": "DESC"                 // 可选，排序方向
}
```

#### 响应示例
```json
{
  "success": true,
  "datas": [
    {
      "id": 789012,
      "userId": 12345,
      "title": "今天学习了Java设计模式",
      "coverUrl": "https://example.com/cover.jpg",
      "likeCount": 156,
      "viewCount": 2341,
      "authorName": "技术达人",
      "authorAvatar": "https://example.com/avatar.jpg",
      "createTime": "2024-01-16T10:30:00"
    }
  ],
  "total": 1250,
  "currentPage": 1,
  "pageSize": 20,
  "totalPage": 63
}
```

---

### 6. 获取用户内容列表
**接口路径**: `GET /api/social/content/user/{userId}`  
**接口描述**: 获取指定用户的内容列表

#### 请求参数
- **userId** (path): 用户ID，必填
- **currentPage** (query): 页码，可选，默认1
- **pageSize** (query): 页面大小，可选，默认20
- **viewerUserId** (query): 查看者用户ID，可选

---

### 7. 获取分类内容列表
**接口路径**: `GET /api/social/content/category/{categoryId}`  
**接口描述**: 获取指定分类下的内容列表

#### 请求参数
- **categoryId** (path): 分类ID，必填
- **currentPage** (query): 页码，可选，默认1
- **pageSize** (query): 页面大小，可选，默认20
- **viewerUserId** (query): 查看者用户ID，可选

---

### 8. 获取热门内容
**接口路径**: `GET /api/social/content/hot`  
**接口描述**: 获取热门内容列表

#### 请求参数
- **currentPage** (query): 页码，可选，默认1
- **pageSize** (query): 页面大小，可选，默认20
- **viewerUserId** (query): 查看者用户ID，可选

---

### 9. 获取最新内容
**接口路径**: `GET /api/social/content/latest`  
**接口描述**: 获取最新内容列表

#### 请求参数
- **currentPage** (query): 页码，可选，默认1
- **pageSize** (query): 页面大小，可选，默认20
- **viewerUserId** (query): 查看者用户ID，可选

---

### 10. 搜索内容
**接口路径**: `GET /api/social/content/search`  
**接口描述**: 根据关键词搜索内容

#### 请求参数
- **keyword** (query): 搜索关键词，必填
- **currentPage** (query): 页码，可选，默认1
- **pageSize** (query): 页面大小，可选，默认20
- **viewerUserId** (query): 查看者用户ID，可选

---

### 11. 批量获取内容
**接口路径**: `POST /api/social/content/batch`  
**接口描述**: 批量获取指定ID的内容信息

#### 请求参数
```json
[789012, 789013, 789014]
```

**Query参数**:
- **viewerUserId** (query): 查看者用户ID，可选

#### 响应示例
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "查询成功",
  "data": [
    {
      "id": 789012,
      "title": "Java学习心得",
      // ... 其他字段
    }
  ]
}
```

---

### 12. 获取内容统计信息
**接口路径**: `GET /api/social/content/{contentId}/stats`  
**接口描述**: 获取内容的统计信息

#### 请求参数
- **contentId** (path): 内容ID，必填
- **viewerUserId** (query): 查看者用户ID，可选

#### 响应示例
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "查询成功",
  "data": {
    "contentId": 789012,
    "likeCount": 156,
    "commentCount": 42,
    "favoriteCount": 89,
    "shareCount": 18,
    "viewCount": 2341,
    "isLiked": false,
    "isFavorited": false,
    "isCommented": false,
    "hotScore": 85.6,
    "ranking": 12
  }
}
```

---

### 13. 批量获取内容统计信息
**接口路径**: `POST /api/social/content/stats/batch`  
**接口描述**: 批量获取多个内容的统计信息

#### 请求参数
```json
[789012, 789013, 789014]
```

**Query参数**:
- **viewerUserId** (query): 查看者用户ID，可选

---

### 14. 检查内容访问权限
**接口路径**: `GET /api/social/content/{contentId}/access`  
**接口描述**: 检查用户是否有权访问指定内容

#### 请求参数
- **contentId** (path): 内容ID，必填
- **userId** (query): 用户ID，必填

---

### 15. 获取用户内容数量
**接口路径**: `GET /api/social/content/count/user/{userId}`  
**接口描述**: 获取用户发布的内容总数

#### 请求参数
- **userId** (path): 用户ID，必填

---

### 16. 获取分类内容数量
**接口路径**: `GET /api/social/content/count/category/{categoryId}`  
**接口描述**: 获取指定分类下的内容总数

#### 请求参数
- **categoryId** (path): 分类ID，必填

---

### 17. 诊断内容统计数据
**接口路径**: `POST /api/social/content/{contentId}/diagnose`  
**接口描述**: 诊断内容统计数据的一致性（调试用）

#### 请求参数
- **contentId** (path): 内容ID，必填

---

### 18. 修复内容统计数据
**接口路径**: `POST /api/social/content/{contentId}/fix-stats`  
**接口描述**: 修复内容统计数据（调试用）

#### 请求参数
- **contentId** (path): 内容ID，必填

---

## 社交互动 API

### 🔄 社交互动生态概述

Collide 社交模块构建了完整的用户互动生态，通过多维度的互动机制促进用户参与和内容传播：

#### 核心互动类型
- **👍 点赞系统**: 支持对内容的点赞和取消点赞，支持不同点赞类型
- **💬 评论系统**: 支持多级评论、回复和嵌套讨论  
- **📤 分享系统**: 支持内容转发和二次传播
- **⭐ 收藏系统**: 支持用户收藏感兴趣的内容到不同收藏夹

### 1. 点赞内容
**接口路径**: `POST /api/social/interaction/like`  
**接口描述**: 给内容点赞

#### 请求参数
```json
{
  "userId": 67890,                    // 必填，用户ID
  "contentId": 789012,                // 必填，内容ID
  "likeType": 1                       // 可选，点赞类型：1-普通点赞,2-超级点赞
}
```

#### 响应示例
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "点赞成功",
  "data": true
}
```

---

### 2. 取消点赞
**接口路径**: `DELETE /api/social/interaction/like`  
**接口描述**: 取消对内容的点赞

#### 请求参数
- **userId** (query): 用户ID，必填
- **contentId** (query): 内容ID，必填

---

### 3. 检查点赞状态
**接口路径**: `GET /api/social/interaction/like/check`  
**接口描述**: 检查用户是否已点赞该内容

#### 请求参数
- **userId** (query): 用户ID，必填
- **contentId** (query): 内容ID，必填

---

### 4. 获取内容点赞列表
**接口路径**: `GET /api/social/interaction/like/{contentId}`  
**接口描述**: 获取内容的点赞用户列表

#### 请求参数
- **contentId** (path): 内容ID，必填
- **currentPage** (query): 页码，可选，默认1
- **pageSize** (query): 页面大小，可选，默认20

#### 响应示例
```json
{
  "success": true,
  "datas": [
    {
      "id": 1001,
      "userId": 67890,
      "contentId": 789012,
      "contentOwnerId": 12345,
      "createTime": "2024-01-16T15:30:00",
      "userName": "coding_lover",
      "userAvatar": "https://example.com/avatar2.jpg",
      "likeType": 1
    }
  ],
  "total": 156,
  "currentPage": 1,
  "pageSize": 20,
  "totalPage": 8
}
```

---

### 5. 收藏内容
**接口路径**: `POST /api/social/interaction/favorite`  
**接口描述**: 收藏内容

#### 请求参数
```json
{
  "userId": 67890,                    // 必填，用户ID
  "contentId": 789012,                // 必填，内容ID
  "folderId": 123,                    // 可选，收藏夹ID
  "folderName": "技术学习"             // 可选，收藏夹名称
}
```

---

### 6. 取消收藏
**接口路径**: `DELETE /api/social/interaction/favorite`  
**接口描述**: 取消收藏内容

#### 请求参数
- **userId** (query): 用户ID，必填
- **contentId** (query): 内容ID，必填

---

### 7. 检查收藏状态
**接口路径**: `GET /api/social/interaction/favorite/check`  
**接口描述**: 检查用户是否已收藏该内容

#### 请求参数
- **userId** (query): 用户ID，必填
- **contentId** (query): 内容ID，必填

---

### 8. 获取用户收藏列表
**接口路径**: `GET /api/social/interaction/favorite/user/{userId}`  
**接口描述**: 获取用户的收藏列表

#### 请求参数
- **userId** (path): 用户ID，必填
- **currentPage** (query): 页码，可选，默认1
- **pageSize** (query): 页面大小，可选，默认20

---

### 9. 分享内容
**接口路径**: `POST /api/social/interaction/share`  
**接口描述**: 分享内容

#### 请求参数
```json
{
  "userId": 67890,                    // 必填，用户ID
  "contentId": 789012,                // 必填，内容ID
  "shareType": 1,                     // 可选，分享类型
  "sharePlatform": "wechat",          // 可选，分享平台
  "shareComment": "推荐一个好内容"     // 可选，分享评论
}
```

---

### 10. 获取内容分享列表
**接口路径**: `GET /api/social/interaction/share/{contentId}`  
**接口描述**: 获取内容的分享记录

#### 请求参数
- **contentId** (path): 内容ID，必填
- **currentPage** (query): 页码，可选，默认1
- **pageSize** (query): 页面大小，可选，默认20

---

### 11. 创建评论
**接口路径**: `POST /api/social/interaction/comment`  
**接口描述**: 对内容或评论进行评论

#### 请求参数
```json
{
  "userId": 67890,                    // 必填，用户ID
  "contentId": 789012,                // 必填，内容ID
  "parentCommentId": null,            // 可选，父评论ID（回复评论时使用）
  "replyToUserId": null,              // 可选，回复的用户ID
  "commentText": "说得很好！",         // 必填，评论内容
  "commentImages": "[\"img1.jpg\"]"   // 可选，评论图片(JSON格式)
}
```

#### 响应示例
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "评论成功",
  "data": 2001
}
```

---

### 12. 删除评论
**接口路径**: `DELETE /api/social/interaction/comment/{commentId}`  
**接口描述**: 删除指定评论

#### 请求参数
- **commentId** (path): 评论ID，必填
- **userId** (query): 用户ID，必填

---

### 13. 获取内容评论列表
**接口路径**: `GET /api/social/interaction/comment/{contentId}`  
**接口描述**: 获取内容的评论列表

#### 请求参数
- **contentId** (path): 内容ID，必填
- **currentPage** (query): 页码，可选，默认1
- **pageSize** (query): 页面大小，可选，默认20

#### 响应示例
```json
{
  "success": true,
  "datas": [
    {
      "id": 2001,
      "userId": 67890,
      "contentId": 789012,
      "contentOwnerId": 12345,
      "createTime": "2024-01-16T16:30:00",
      "userName": "coding_lover",
      "userAvatar": "https://example.com/avatar2.jpg",
      "parentCommentId": null,
      "replyToUserId": null,
      "commentText": "说得很好！",
      "commentImages": "[\"img1.jpg\"]",
      "likeCount": 5,
      "replyCount": 2,
      "status": 1,
      "updateTime": "2024-01-16T16:30:00"
    }
  ],
  "total": 42,
  "currentPage": 1,
  "pageSize": 20,
  "totalPage": 3
}
```

---

### 14. 获取评论回复列表
**接口路径**: `GET /api/social/interaction/comment/{parentCommentId}/replies`  
**接口描述**: 获取指定评论的回复列表

#### 请求参数
- **parentCommentId** (path): 父评论ID，必填
- **currentPage** (query): 页码，可选，默认1
- **pageSize** (query): 页面大小，可选，默认20

---

### 15. 获取用户互动状态
**接口路径**: `GET /api/social/interaction/status`  
**接口描述**: 获取用户对指定内容的互动状态

#### 请求参数
- **userId** (query): 用户ID，必填
- **contentId** (query): 内容ID，必填

#### 响应示例
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "查询成功",
  "data": {
    "userId": 67890,
    "contentId": 789012,
    "liked": true,
    "favorited": false,
    "commented": true,
    "shared": false,
    "likeTime": "2024-01-16T15:30:00",
    "favoriteTime": null,
    "commentTime": "2024-01-16T16:30:00",
    "shareTime": null
  }
}
```

---

### 16. 批量获取用户互动状态
**接口路径**: `POST /api/social/interaction/status/batch`  
**接口描述**: 批量获取用户对多个内容的互动状态

#### 请求参数
- **userId** (query): 用户ID，必填
- **Body**: 内容ID列表

```json
[789012, 789013, 789014]
```

#### 响应示例
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "查询成功",
  "data": {
    "789012": {
      "userId": 67890,
      "contentId": 789012,
      "liked": true,
      "favorited": false,
      "commented": true,
      "shared": false
    },
    "789013": {
      "userId": 67890,
      "contentId": 789013,
      "liked": false,
      "favorited": true,
      "commented": false,
      "shared": false
    }
  }
}
```

---

## 响应格式说明

### 1. 单个数据响应格式 (Result)
```json
{
  "success": true,          // 是否成功
  "code": "SUCCESS",        // 响应码
  "message": "操作成功",    // 响应消息
  "data": {}               // 响应数据
}
```

### 2. 分页数据响应格式 (PageResponse)
```json
{
  "success": true,          // 是否成功
  "datas": [],             // 数据列表
  "total": 1250,           // 总记录数
  "currentPage": 1,        // 当前页码
  "pageSize": 20,          // 每页大小
  "totalPage": 63          // 总页数
}
```

---

## 错误码说明

| 错误码 | 说明 |
|--------|------|
| SUCCESS | 操作成功 |
| CONTENT_NOT_FOUND | 内容不存在 |
| CONTENT_DELETED | 内容已删除 |
| CONTENT_PERMISSION_DENIED | 内容访问权限不足 |
| USER_NOT_FOUND | 用户不存在 |
| ALREADY_LIKED | 已经点赞过 |
| ALREADY_FAVORITED | 已经收藏过 |
| ALREADY_SHARED | 已经分享过 |
| COMMENT_NOT_FOUND | 评论不存在 |
| CONTENT_TOO_LONG | 内容过长 |
| MEDIA_UPLOAD_FAILED | 媒体文件上传失败 |
| USER_BLOCKED | 用户被屏蔽 |
| SENSITIVE_CONTENT | 内容包含敏感信息 |
| INTERACTION_QUERY_ERROR | 互动记录查询失败 |
| CACHE_ERROR | 缓存服务异常 |
| PAYMENT_REQUIRED | 需要付费购买 |
| INSUFFICIENT_COINS | 金币不足 |

---

## 数据类型说明

### 内容类型 (contentType)
- **1**: 短视频
- **2**: 长视频  
- **3**: 图片
- **4**: 文字

### 查询类型 (queryType)
- **1**: 最新
- **2**: 热门
- **3**: 搜索

### 互动类型 (interactionType)
- **1**: 点赞
- **2**: 收藏
- **3**: 分享
- **4**: 评论

### 隐私设置 (privacy)
- **1**: 公开
- **2**: 仅关注者
- **3**: 私密

### 付费状态 (isPaid)
- **0**: 免费
- **1**: 付费

---

**最后更新**: 2024-01-16  
**文档版本**: v2.0.0 (DDD架构重构版)