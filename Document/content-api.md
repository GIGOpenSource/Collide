# 内容模块 API 接口文档

## 1. 概述

内容模块提供完整的内容管理功能，支持小说、漫画、短视频、长视频四种内容类型。包含内容的创建、审核、发布、查询、统计和推荐等功能。

**Base URL**: `/api/content`

## 2. 内容管理接口

### 2.1 创建内容

**接口地址**: `POST /content/create`

**请求参数**:
```json
{
  "contentType": "novel|comic|short_video|long_video",
  "title": "内容标题",
  "description": "内容描述",
  "coverImage": "封面图URL",
  "contentUrl": "内容URL",
  "duration": 120,
  "fileSize": 1024000,
  "tags": ["标签1", "标签2"],
  "category": "分类",
  "isOriginal": true,
  "ageRating": "all",
  "language": "zh",
  "submitForReview": false,
  "extendInfo": "{\"chapters\": 10}"
}
```

**响应示例**:
```json
{
  "success": true,
  "message": "创建成功",
  "contentId": 12345
}
```

### 2.2 更新内容

**接口地址**: `PUT /content/update`

**请求参数**:
```json
{
  "contentId": 12345,
  "title": "新标题",
  "description": "新描述",
  "tags": ["新标签1", "新标签2"],
  "extendInfo": "{\"chapters\": 15}"
}
```

**响应示例**:
```json
{
  "success": true,
  "message": "更新成功",
  "contentId": 12345
}
```

### 2.3 删除内容

**接口地址**: `DELETE /content/{contentId}`

**路径参数**:
- `contentId`: 内容ID

**响应示例**:
```json
{
  "success": true,
  "message": "删除成功"
}
```

### 2.4 提交审核

**接口地址**: `POST /content/{contentId}/submit-review`

**路径参数**:
- `contentId`: 内容ID

**响应示例**:
```json
{
  "success": true,
  "message": "提交审核成功",
  "contentId": 12345,
  "reviewId": 67890
}
```

## 3. 内容查询接口

### 3.1 根据ID查询内容

**接口地址**: `GET /content/{contentId}`

**路径参数**:
- `contentId`: 内容ID

**响应示例**:
```json
{
  "success": true,
  "contentInfo": {
    "contentId": 12345,
    "contentType": "novel",
    "title": "示例小说",
    "description": "小说描述",
    "coverImage": "cover.jpg",
    "contentUrl": "novel/1",
    "status": "PUBLISHED",
    "creatorUserId": 1001,
    "creatorUsername": "author001",
    "tags": ["科幻", "穿越"],
    "category": "科幻小说",
    "isOriginal": true,
    "language": "zh",
    "viewCount": 1000,
    "likeCount": 100,
    "collectCount": 50,
    "shareCount": 20,
    "rating": 4.5,
    "ratingCount": 100,
    "publishTime": "2024-01-01T10:00:00",
    "createTime": "2024-01-01T08:00:00",
    "updateTime": "2024-01-01T09:00:00"
  }
}
```

### 3.2 分页查询内容

**接口地址**: `GET /content/page`

**查询参数**:
- `contentType`: 内容类型（可选）
- `status`: 内容状态（可选）
- `category`: 分类（可选）
- `titleKeyword`: 标题关键词（可选）
- `page`: 页码（默认1）
- `size`: 每页大小（默认10）
- `sortField`: 排序字段（默认createTime）
- `sortDirection`: 排序方向（默认desc）

**响应示例**:
```json
{
  "success": true,
  "contentList": [
    {
      "contentId": 12345,
      "contentType": "novel",
      "title": "示例小说",
      "coverImage": "cover.jpg",
      "status": "PUBLISHED",
      "viewCount": 1000,
      "likeCount": 100,
      "rating": 4.5,
      "publishTime": "2024-01-01T10:00:00"
    }
  ],
  "total": 100,
  "currentPage": 1,
  "pageSize": 10
}
```

### 3.3 查询用户内容

**接口地址**: `GET /content/user/{userId}`

**路径参数**:
- `userId`: 用户ID

**查询参数**:
- `page`: 页码（默认1）
- `size`: 每页大小（默认10）
- `status`: 内容状态（可选）

**响应示例**:
```json
{
  "success": true,
  "contentList": [
    {
      "contentId": 12345,
      "title": "我的作品",
      "status": "PUBLISHED",
      "viewCount": 1000,
      "createTime": "2024-01-01T08:00:00"
    }
  ],
  "total": 5,
  "currentPage": 1,
  "pageSize": 10
}
```

## 4. 内容审核接口

### 4.1 审核内容

**接口地址**: `POST /content/review`

**请求参数**:
```json
{
  "contentId": 12345,
  "reviewStatus": "APPROVED|REJECTED",
  "reviewComment": "审核意见",
  "reviewReason": "审核原因",
  "violationType": "违规类型",
  "needReReview": false
}
```

**响应示例**:
```json
{
  "success": true,
  "message": "审核完成",
  "contentId": 12345,
  "reviewId": 67890
}
```

### 4.2 查询待审核内容

**接口地址**: `GET /content/pending-review`

**查询参数**:
- `page`: 页码（默认1）
- `size`: 每页大小（默认10）
- `contentType`: 内容类型（可选）
- `priority`: 优先级（可选）

**响应示例**:
```json
{
  "success": true,
  "contentList": [
    {
      "contentId": 12345,
      "title": "待审核内容",
      "contentType": "novel",
      "submitTime": "2024-01-01T08:00:00",
      "priority": 1
    }
  ],
  "total": 50,
  "currentPage": 1,
  "pageSize": 10
}
```

### 4.3 查询审核记录

**接口地址**: `GET /content/{contentId}/review-history`

**路径参数**:
- `contentId`: 内容ID

**响应示例**:
```json
{
  "success": true,
  "reviewList": [
    {
      "reviewId": 67890,
      "reviewStatus": "APPROVED",
      "reviewerUsername": "reviewer001",
      "reviewComment": "内容符合规范",
      "reviewRound": 1,
      "submitTime": "2024-01-01T08:00:00",
      "finishTime": "2024-01-01T09:00:00"
    }
  ]
}
```

## 5. 用户交互接口

### 5.1 点赞内容

**接口地址**: `POST /content/{contentId}/like`

**路径参数**:
- `contentId`: 内容ID

**响应示例**:
```json
{
  "success": true,
  "message": "点赞成功"
}
```

### 5.2 取消点赞

**接口地址**: `DELETE /content/{contentId}/like`

**路径参数**:
- `contentId`: 内容ID

**响应示例**:
```json
{
  "success": true,
  "message": "取消点赞成功"
}
```

### 5.3 收藏内容

**接口地址**: `POST /content/{contentId}/collect`

**路径参数**:
- `contentId`: 内容ID

**响应示例**:
```json
{
  "success": true,
  "message": "收藏成功"
}
```

### 5.4 取消收藏

**接口地址**: `DELETE /content/{contentId}/collect`

**路径参数**:
- `contentId`: 内容ID

**响应示例**:
```json
{
  "success": true,
  "message": "取消收藏成功"
}
```

### 5.5 分享内容

**接口地址**: `POST /content/{contentId}/share`

**路径参数**:
- `contentId`: 内容ID

**响应示例**:
```json
{
  "success": true,
  "message": "分享成功"
}
```

### 5.6 查看内容

**接口地址**: `POST /content/{contentId}/view`

**路径参数**:
- `contentId`: 内容ID

**请求参数**:
```json
{
  "duration": 120,
  "completionRate": 0.8
}
```

**响应示例**:
```json
{
  "success": true,
  "message": "记录查看成功"
}
```

## 6. 内容推荐接口

### 6.1 个性化推荐

**接口地址**: `GET /content/recommend/personal`

**查询参数**:
- `limit`: 推荐数量（默认10）

**响应示例**:
```json
{
  "success": true,
  "contentList": [
    {
      "contentId": 12345,
      "title": "推荐内容",
      "contentType": "novel",
      "coverImage": "cover.jpg",
      "rating": 4.5,
      "recommendReason": "基于你的阅读偏好"
    }
  ]
}
```

### 6.2 按类型推荐

**接口地址**: `GET /content/recommend/by-type`

**查询参数**:
- `contentType`: 内容类型
- `limit`: 推荐数量（默认10）

**响应示例**:
```json
{
  "success": true,
  "contentList": [
    {
      "contentId": 12345,
      "title": "小说推荐",
      "contentType": "novel",
      "coverImage": "cover.jpg",
      "rating": 4.5
    }
  ]
}
```

### 6.3 热门内容推荐

**接口地址**: `GET /content/recommend/hot`

**查询参数**:
- `contentType`: 内容类型（可选）
- `limit`: 推荐数量（默认10）

**响应示例**:
```json
{
  "success": true,
  "contentList": [
    {
      "contentId": 12345,
      "title": "热门内容",
      "contentType": "short_video",
      "coverImage": "cover.jpg",
      "viewCount": 100000,
      "hotScore": 95.5
    }
  ]
}
```

### 6.4 最新内容推荐

**接口地址**: `GET /content/recommend/latest`

**查询参数**:
- `contentType`: 内容类型（可选）
- `limit`: 推荐数量（默认10）

**响应示例**:
```json
{
  "success": true,
  "contentList": [
    {
      "contentId": 12345,
      "title": "最新内容",
      "contentType": "comic",
      "coverImage": "cover.jpg",
      "publishTime": "2024-01-01T10:00:00"
    }
  ]
}
```

### 6.5 相关内容推荐

**接口地址**: `GET /content/{contentId}/recommend/related`

**路径参数**:
- `contentId`: 内容ID

**查询参数**:
- `limit`: 推荐数量（默认10）

**响应示例**:
```json
{
  "success": true,
  "contentList": [
    {
      "contentId": 67890,
      "title": "相关内容",
      "contentType": "novel",
      "coverImage": "cover.jpg",
      "similarity": 0.85
    }
  ]
}
```

## 7. 内容统计接口

### 7.1 查询内容统计

**接口地址**: `GET /content/{contentId}/statistics`

**路径参数**:
- `contentId`: 内容ID

**响应示例**:
```json
{
  "success": true,
  "statistics": {
    "contentId": 12345,
    "viewCount": 10000,
    "likeCount": 1000,
    "collectCount": 500,
    "shareCount": 200,
    "commentCount": 300,
    "rating": 4.5,
    "ratingCount": 100,
    "hotScore": 85.5,
    "qualityScore": 90.0,
    "recommendIndex": 88.0
  }
}
```

### 7.2 查询用户内容统计

**接口地址**: `GET /content/user/{userId}/statistics`

**路径参数**:
- `userId`: 用户ID

**响应示例**:
```json
{
  "success": true,
  "statistics": {
    "totalContent": 10,
    "publishedContent": 8,
    "totalViewCount": 50000,
    "totalLikeCount": 5000,
    "totalCollectCount": 2500,
    "avgRating": 4.3
  }
}
```

## 8. 错误码说明

| 错误码 | 说明 |
|--------|------|
| 40001 | 参数错误 |
| 40002 | 内容不存在 |
| 40003 | 无权限操作 |
| 40004 | 内容状态不允许操作 |
| 40005 | 审核状态不正确 |
| 50001 | 服务器内部错误 |
| 50002 | 数据库操作失败 |
| 50003 | 文件上传失败 |

## 9. 状态码说明

### 9.1 内容状态 (ContentStatus)
- `DRAFT` (1): 草稿
- `PENDING_REVIEW` (2): 待审核
- `PUBLISHED` (3): 已发布
- `REJECTED` (4): 审核拒绝
- `UNPUBLISHED` (5): 已下架
- `DELETED` (6): 已删除

### 9.2 审核状态 (ContentReviewStatus)
- `PENDING` (1): 待审核
- `REVIEWING` (2): 审核中
- `APPROVED` (3): 审核通过
- `REJECTED` (4): 审核拒绝
- `RE_REVIEW` (5): 需要重审

### 9.3 内容类型 (ContentType)
- `novel`: 小说
- `comic`: 漫画
- `short_video`: 短视频
- `long_video`: 长视频

## 10. 使用示例

### 10.1 完整的内容发布流程

```javascript
// 1. 创建内容
const createResponse = await fetch('/api/content/create', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    contentType: 'novel',
    title: '我的第一部小说',
    description: '这是一部精彩的小说',
    contentUrl: '/novels/my-first-novel',
    tags: ['玄幻', '修真'],
    category: '玄幻小说',
    isOriginal: true
  })
});

// 2. 提交审核
const reviewResponse = await fetch(`/api/content/${contentId}/submit-review`, {
  method: 'POST'
});

// 3. 查询审核状态
const statusResponse = await fetch(`/api/content/${contentId}/review-history`);

// 4. 审核通过后查看内容
const contentResponse = await fetch(`/api/content/${contentId}`);
```

### 10.2 用户交互示例

```javascript
// 用户查看内容
await fetch(`/api/content/${contentId}/view`, {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    duration: 300,
    completionRate: 1.0
  })
});

// 用户点赞
await fetch(`/api/content/${contentId}/like`, {
  method: 'POST'
});

// 用户收藏
await fetch(`/api/content/${contentId}/collect`, {
  method: 'POST'
});
```

### 10.3 推荐系统使用示例

```javascript
// 获取个性化推荐
const personalRecommend = await fetch('/api/content/recommend/personal?limit=20');

// 获取热门内容
const hotContent = await fetch('/api/content/recommend/hot?contentType=short_video&limit=10');

// 获取相关推荐
const relatedContent = await fetch(`/api/content/${contentId}/recommend/related?limit=5`);
```

这个API文档涵盖了内容模块的所有核心功能，为前端开发和第三方集成提供了完整的接口规范。 