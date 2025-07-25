# Content 模块 API 接口文档

## 📋 目录
- [模块概述](#模块概述)
- [数据库设计](#数据库设计)
- [接口列表](#接口列表)
- [数据模型](#数据模型)
- [错误码定义](#错误码定义)
- [使用示例](#使用示例)

---

## 📚 模块概述

Content 模块是 Collide 社交平台的核心内容管理系统，负责多媒体内容的创建、发布、审核、推荐和用户互动等功能。

### 主要功能
- **内容管理**: 支持小说、漫画、视频、图文、音频等多种内容类型
- **内容审核**: 完整的审核工作流，支持审核状态管理
- **用户交互**: 点赞、收藏、分享、评论等社交功能
- **内容发现**: 推荐算法、热门排行、分类浏览、搜索功能
- **统计分析**: 详细的内容数据统计和分析

### 支持的内容类型
- **NOVEL**: 小说文本内容
- **COMIC**: 漫画图片内容
- **SHORT_VIDEO**: 短视频内容
- **LONG_VIDEO**: 长视频内容
- **ARTICLE**: 图文混排内容
- **AUDIO**: 音频内容

---

## 🗄️ 数据库设计

### 内容主表 (t_content)

| 字段名 | 类型 | 是否必填 | 默认值 | 说明 |
|--------|------|----------|--------|------|
| id | BIGINT | 是 | AUTO_INCREMENT | 内容ID，主键 |
| title | VARCHAR(200) | 是 | - | 内容标题 |
| description | TEXT | 否 | - | 内容描述/摘要 |
| content_type | VARCHAR(50) | 是 | - | 内容类型枚举 |
| content_data | LONGTEXT | 否 | - | 内容数据JSON |
| cover_url | VARCHAR(500) | 否 | - | 封面图片URL |
| author_id | BIGINT | 是 | - | 作者用户ID |
| category_id | BIGINT | 否 | - | 分类ID |
| tags | TEXT | 否 | - | 标签JSON数组 |
| status | VARCHAR(50) | 是 | DRAFT | 内容状态 |
| review_status | VARCHAR(50) | 是 | PENDING | 审核状态 |
| view_count | BIGINT | 是 | 0 | 查看数 |
| like_count | BIGINT | 是 | 0 | 点赞数 |
| comment_count | BIGINT | 是 | 0 | 评论数 |
| share_count | BIGINT | 是 | 0 | 分享数 |
| favorite_count | BIGINT | 是 | 0 | 收藏数 |

### 内容状态枚举

#### ContentStatus
- **DRAFT**: 草稿状态，作者编辑中
- **PENDING**: 待审核状态，提交审核
- **PUBLISHED**: 已发布状态，公开可见
- **REJECTED**: 审核拒绝状态
- **OFFLINE**: 已下架状态

#### ReviewStatus  
- **PENDING**: 待审核
- **APPROVED**: 审核通过
- **REJECTED**: 审核拒绝

---

## 🔗 接口列表

### REST API 接口

#### 内容管理接口

| 接口 | 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|------|
| 创建内容 | POST | `/api/v1/content` | 创建新内容 | 登录用户 |
| 更新内容 | PUT | `/api/v1/content/{contentId}` | 更新内容信息 | 作者本人 |
| 删除内容 | DELETE | `/api/v1/content/{contentId}` | 删除内容 | 作者本人 |
| 发布内容 | POST | `/api/v1/content/{contentId}/publish` | 发布内容 | 作者本人 |

#### 内容浏览接口

| 接口 | 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|------|
| 查看内容详情 | GET | `/api/v1/content/{contentId}` | 获取内容详情 | 公开 |
| 获取内容列表 | GET | `/api/v1/content` | 分页获取内容列表 | 公开 |
| 获取用户内容 | GET | `/api/v1/content/user/{authorId}` | 获取指定用户内容 | 公开 |
| 获取我的内容 | GET | `/api/v1/content/my` | 获取当前用户内容 | 登录用户 |

#### 用户交互接口

| 接口 | 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|------|
| 点赞内容 | POST | `/api/v1/content/{contentId}/like` | 点赞内容 | 登录用户 |
| 收藏内容 | POST | `/api/v1/content/{contentId}/favorite` | 收藏内容 | 登录用户 |
| 分享内容 | POST | `/api/v1/content/{contentId}/share` | 分享内容 | 登录用户 |
| 获取统计 | GET | `/api/v1/content/{contentId}/statistics` | 获取内容统计 | 公开 |

### RPC 接口 (Dubbo)

#### ContentFacadeService

| 接口方法 | 说明 | 参数 | 返回值 |
|----------|------|------|--------|
| createContent | 创建内容 | ContentCreateRequest | ContentResponse |
| updateContent | 更新内容 | ContentUpdateRequest | ContentResponse |
| deleteContent | 删除内容 | ContentDeleteRequest | ContentResponse |
| publishContent | 发布内容 | ContentPublishRequest | ContentResponse |
| queryContent | 查询内容详情 | ContentQueryRequest | ContentQueryResponse&lt;ContentInfo&gt; |
| pageQueryContents | 分页查询内容 | ContentQueryRequest | PageResponse&lt;ContentInfo&gt; |
| queryUserContents | 查询用户内容 | ContentQueryRequest | PageResponse&lt;ContentInfo&gt; |
| getContentStatistics | 获取内容统计 | Long contentId | ContentQueryResponse&lt;ContentStatistics&gt; |
| likeContent | 点赞内容 | ContentLikeRequest | ContentResponse |
| favoriteContent | 收藏内容 | ContentFavoriteRequest | ContentResponse |
| shareContent | 分享内容 | ContentShareRequest | ContentResponse |

---

## 📋 数据模型

### 请求模型

#### ContentCreateRequest
```json
{
  "title": "内容标题",
  "description": "内容描述",
  "contentType": "NOVEL",
  "contentData": {
    "chapters": [
      {
        "title": "第一章",
        "content": "章节内容...",
        "wordCount": 2000
      }
    ]
  },
  "coverUrl": "https://example.com/cover.jpg",
  "authorId": 123,
  "categoryId": 1,
  "tags": ["玄幻", "修仙", "热血"]
}
```

#### ContentQueryRequest
```json
{
  "contentId": 123,
  "queryType": "LATEST",
  "contentType": "NOVEL",
  "authorId": 456,
  "categoryId": 1,
  "keyword": "搜索关键词",
  "status": "PUBLISHED",
  "pageNo": 1,
  "pageSize": 20,
  "viewContent": true
}
```

### 响应模型

#### ContentInfo
```json
{
  "id": 123,
  "title": "修仙传奇",
  "description": "一个关于修仙的故事...",
  "contentType": "NOVEL",
  "contentData": {
    "totalChapters": 100,
    "totalWords": 200000,
    "lastUpdateChapter": "第100章"
  },
  "coverUrl": "https://example.com/cover.jpg",
  "authorId": 456,
  "author": {
    "id": 456,
    "username": "author_001",
    "nickname": "知名作者",
    "avatar": "https://example.com/avatar.jpg"
  },
  "categoryId": 1,
  "categoryName": "玄幻小说",
  "tags": ["玄幻", "修仙", "热血"],
  "status": "PUBLISHED",
  "reviewStatus": "APPROVED",
  "viewCount": 50000,
  "likeCount": 1200,
  "commentCount": 300,
  "shareCount": 150,
  "favoriteCount": 800,
  "createTime": "2024-01-01T10:00:00",
  "updateTime": "2024-01-15T16:30:00",
  "publishedTime": "2024-01-02T09:00:00",
  "liked": true,
  "favorited": false,
  "recommended": true
}
```

#### ContentStatistics
```json
{
  "contentId": 123,
  "totalViews": 50000,
  "todayViews": 500,
  "totalLikes": 1200,
  "todayLikes": 15,
  "totalComments": 300,
  "todayComments": 8,
  "totalShares": 150,
  "todayShares": 3,
  "totalFavorites": 800,
  "todayFavorites": 12,
  "avgViewDuration": 320.5,
  "bounceRate": 0.25,
  "completionRate": 0.78,
  "dailyStats": {
    "2024-01-15": 500,
    "2024-01-14": 480,
    "2024-01-13": 520
  },
  "userBehaviorStats": {
    "newUsers": 120,
    "returningUsers": 380,
    "avgReadTime": 18.5
  },
  "updateTime": "2024-01-15T23:59:59"
}
```

### 内容数据格式

#### 小说内容 (NOVEL)
```json
{
  "synopsis": "小说简介",
  "totalChapters": 100,
  "totalWords": 200000,
  "chapters": [
    {
      "id": 1,
      "title": "第一章 开始",
      "content": "章节内容...",
      "wordCount": 2000,
      "publishTime": "2024-01-01T10:00:00"
    }
  ],
  "writingStatus": "ONGOING",
  "updateFrequency": "DAILY"
}
```

#### 漫画内容 (COMIC)
```json
{
  "totalEpisodes": 50,
  "episodes": [
    {
      "id": 1,
      "title": "第1话",
      "pages": [
        "https://example.com/page1.jpg",
        "https://example.com/page2.jpg"
      ],
      "publishTime": "2024-01-01T10:00:00"
    }
  ],
  "style": "COLOR",
  "orientation": "VERTICAL"
}
```

#### 视频内容 (SHORT_VIDEO/LONG_VIDEO)
```json
{
  "videoUrl": "https://example.com/video.mp4",
  "duration": 300,
  "resolution": "1920x1080",
  "format": "MP4",
  "thumbnails": [
    "https://example.com/thumb1.jpg",
    "https://example.com/thumb2.jpg"
  ],
  "subtitles": [
    {
      "language": "zh-CN",
      "url": "https://example.com/subtitle.srt"
    }
  ]
}
```

---

## ⚠️ 错误码定义

### 内容相关错误码

| 错误码 | 错误信息 | 说明 |
|--------|----------|------|
| CONTENT_NOT_FOUND | 内容不存在 | 指定的内容ID不存在 |
| CONTENT_ACCESS_DENIED | 无访问权限 | 用户无权限访问此内容 |
| CONTENT_EDIT_NOT_ALLOWED | 内容不可编辑 | 当前状态下内容不允许编辑 |
| CONTENT_PUBLISH_NOT_ALLOWED | 内容不可发布 | 内容未通过审核或状态不允许发布 |
| CONTENT_ALREADY_PUBLISHED | 内容已发布 | 内容已经处于发布状态 |
| CONTENT_TITLE_REQUIRED | 标题不能为空 | 内容标题为必填项 |
| CONTENT_TITLE_TOO_LONG | 标题过长 | 标题长度超过限制 |
| CONTENT_TYPE_INVALID | 内容类型无效 | 不支持的内容类型 |
| CONTENT_CATEGORY_INVALID | 分类无效 | 指定的分类不存在或无效 |
| CONTENT_TAG_TOO_MANY | 标签过多 | 标签数量超过限制 |

### 交互相关错误码

| 错误码 | 错误信息 | 说明 |
|--------|----------|------|
| CONTENT_ALREADY_LIKED | 已经点赞 | 用户已对此内容点赞 |
| CONTENT_ALREADY_FAVORITED | 已经收藏 | 用户已收藏此内容 |
| CONTENT_LIKE_FAILED | 点赞失败 | 点赞操作失败 |
| CONTENT_FAVORITE_FAILED | 收藏失败 | 收藏操作失败 |
| CONTENT_SHARE_NOT_ALLOWED | 不允许分享 | 内容设置不允许分享 |

---

## 🔧 使用示例

### 创建内容

#### 请求示例
```bash
curl -X POST "http://localhost:8080/api/v1/content" \
  -H "Authorization: Bearer your-token" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "我的第一本小说",
    "description": "这是一个精彩的修仙故事",
    "contentType": "NOVEL",
    "contentData": {
      "synopsis": "主角从凡人修炼成仙的传奇故事",
      "chapters": [
        {
          "title": "第一章 初入修仙界",
          "content": "在一个平凡的村庄里...",
          "wordCount": 2000
        }
      ]
    },
    "coverUrl": "https://example.com/novel-cover.jpg",
    "categoryId": 1,
    "tags": ["玄幻", "修仙", "升级流"]
  }'
```

#### 响应示例
```json
{
  "code": 200,
  "success": true,
  "message": "创建成功",
  "data": 123,
  "timestamp": "2024-01-15T10:30:00"
}
```

### 获取内容列表

#### 请求示例
```bash
curl -X GET "http://localhost:8080/api/v1/content?type=HOT&contentType=NOVEL&pageNo=1&pageSize=10"
```

#### 响应示例
```json
{
  "code": 200,
  "success": true,
  "message": "查询成功",
  "data": {
    "records": [
      {
        "id": 123,
        "title": "修仙传奇",
        "description": "一个关于修仙的故事...",
        "contentType": "NOVEL",
        "coverUrl": "https://example.com/cover.jpg",
        "author": {
          "id": 456,
          "username": "author_001",
          "nickname": "知名作者"
        },
        "categoryName": "玄幻小说",
        "tags": ["玄幻", "修仙", "热血"],
        "viewCount": 50000,
        "likeCount": 1200,
        "publishedTime": "2024-01-02T09:00:00"
      }
    ],
    "total": 100,
    "size": 10,
    "current": 1,
    "pages": 10
  },
  "timestamp": "2024-01-15T10:30:00"
}
```

### 点赞内容

#### 请求示例
```bash
curl -X POST "http://localhost:8080/api/v1/content/123/like" \
  -H "Authorization: Bearer your-token"
```

#### 响应示例
```json
{
  "code": 200,
  "success": true,
  "message": "点赞成功",
  "data": true,
  "timestamp": "2024-01-15T10:30:00"
}
```

### 获取内容统计

#### 请求示例
```bash
curl -X GET "http://localhost:8080/api/v1/content/123/statistics"
```

#### 响应示例
```json
{
  "code": 200,
  "success": true,
  "message": "查询成功",
  "data": {
    "contentId": 123,
    "totalViews": 50000,
    "todayViews": 500,
    "totalLikes": 1200,
    "todayLikes": 15,
    "totalInteractions": 2450,
    "todayInteractions": 38,
    "updateTime": "2024-01-15T23:59:59"
  },
  "timestamp": "2024-01-15T10:30:00"
}
```

---

## 📈 性能优化建议

### 查询优化
- 使用适当的索引策略
- 分页查询避免深分页
- 热门内容使用缓存
- 统计数据异步计算

### 存储优化
- 大文件内容使用对象存储
- JSON数据合理设计结构
- 定期清理过期数据
- 数据归档策略

### 缓存策略
- 热门内容Redis缓存
- 用户个性化推荐缓存
- 统计数据定期更新
- CDN加速静态资源

---

*本文档描述了 Content 模块的完整 API 接口规范，包含数据模型、错误处理、使用示例等详细信息。如有疑问请联系技术团队。* 