# Content Controller REST API 文档

**控制器**: ContentController  
**版本**: 2.0.0 (简洁版)  
**基础路径**: `/api/content`  
**接口数量**: 28个  
**更新时间**: 2024-01-01  

## 🚀 概述

内容管理控制器提供内容的增删改查、状态管理、统计分析等REST API接口。支持多种内容类型的管理，包括内容发布、审核、统计等核心功能。

**支持的内容类型**:
- `NOVEL` - 小说
- `COMIC` - 漫画  
- `VIDEO` - 视频
- `AUDIO` - 音频
- `ARTICLE` - 文章

## 📋 接口分类

| 分类 | 接口数量 | 功能描述 |
|------|----------|----------|
| **内容管理** | 7个 | 创建、更新、删除、查询、发布、下线 |
| **章节管理** | 5个 | 章节创建、查询、发布 |
| **统计管理** | 7个 | 浏览量、点赞、评论、收藏、评分统计 |
| **内容查询** | 6个 | 作者内容、分类内容、搜索、热门、最新 |
| **数据同步** | 3个 | 作者信息、分类信息、内容审核 |

---

## 🔧 1. 内容管理 (7个接口)

### 1.1 创建内容

**接口**: `POST /api/content`

**描述**: 创建新的内容，支持多种内容类型

**请求参数**:
```json
{
  "title": "内容标题",
  "description": "内容描述",
  "contentType": "NOVEL",
  "content": "内容正文",
  "authorId": 1001,
  "categoryId": 201,
  "tags": ["标签1", "标签2"],
  "coverImage": "https://example.com/cover.jpg",
  "isPublic": true,
  "allowComment": true
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

**错误处理**:
- `CREATE_CONTENT_API_FAILED`: 创建内容API调用失败

### 1.2 更新内容

**接口**: `PUT /api/content`

**描述**: 更新已有内容的信息

**请求参数**:
```json
{
  "id": 12345,
  "title": "更新后的标题",
  "description": "更新后的描述",
  "content": "更新后的内容",
  "tags": ["新标签1", "新标签2"],
  "coverImage": "https://example.com/new-cover.jpg"
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 12345,
    "title": "更新后的标题",
    "description": "更新后的描述",
    "contentType": "NOVEL",
    "status": "DRAFT",
    "updateTime": "2024-01-01T12:00:00"
  }
}
```

### 1.3 删除内容

**接口**: `DELETE /api/content/{contentId}`

**描述**: 逻辑删除指定内容

**路径参数**:
- `contentId` (Long): 内容ID

**查询参数**:
- `operatorId` (Long): 操作人ID

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

### 1.4 获取内容详情

**接口**: `GET /api/content/{contentId}`

**描述**: 根据内容ID获取内容详细信息

**路径参数**:
- `contentId` (Long): 内容ID

**查询参数**:
- `includeOffline` (Boolean, 默认false): 是否包含下线内容

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 12345,
    "title": "我的小说",
    "description": "这是一部精彩的小说",
    "contentType": "NOVEL",
    "status": "PUBLISHED",
    "authorId": 1001,
    "authorNickname": "知名作家",
    "authorAvatar": "https://example.com/avatar.jpg",
    "categoryId": 201,
    "categoryName": "玄幻小说",
    "tags": ["玄幻", "热血", "升级"],
    "coverImage": "https://example.com/cover.jpg",
    "viewCount": 10000,
    "likeCount": 500,
    "commentCount": 200,
    "favoriteCount": 800,
    "score": 8.5,
    "scoreCount": 150,
    "wordCount": 100000,
    "chapterCount": 50,
    "isPaid": true,
    "createTime": "2024-01-01T10:00:00",
    "publishTime": "2024-01-01T11:00:00",
    "updateTime": "2024-01-15T14:30:00"
  }
}
```

### 1.5 查询内容列表

**接口**: `POST /api/content/query`

**描述**: 根据条件分页查询内容

**请求参数**:
```json
{
  "title": "搜索标题",
  "contentType": "NOVEL",
  "status": "PUBLISHED",
  "authorId": 1001,
  "categoryId": 201,
  "tags": ["玄幻"],
  "keyword": "搜索关键词",
  "minScore": 8.0,
  "maxScore": 10.0,
  "orderBy": "viewCount",
  "orderDirection": "DESC",
  "currentPage": 1,
  "pageSize": 20
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 12345,
        "title": "我的小说",
        "description": "精彩小说",
        "contentType": "NOVEL",
        "status": "PUBLISHED",
        "authorNickname": "知名作家",
        "categoryName": "玄幻小说",
        "viewCount": 10000,
        "score": 8.5,
        "publishTime": "2024-01-01T11:00:00"
      }
    ],
    "totalCount": 100,
    "totalPage": 5,
    "currentPage": 1,
    "pageSize": 20
  }
}
```

### 1.6 发布内容

**接口**: `PUT /api/content/{contentId}/publish`

**描述**: 发布指定内容

**路径参数**:
- `contentId` (Long): 内容ID

**查询参数**:
- `authorId` (Long): 作者ID

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 12345,
    "status": "PUBLISHED",
    "publishTime": "2024-01-01T11:00:00"
  }
}
```

### 1.7 下线内容

**接口**: `PUT /api/content/{contentId}/offline`

**描述**: 下线指定内容

**路径参数**:
- `contentId` (Long): 内容ID

**查询参数**:
- `operatorId` (Long): 操作人ID

---

## 📚 2. 章节管理 (5个接口)

### 2.1 创建章节

**接口**: `POST /api/content/chapters`

**描述**: 为内容创建新章节

**请求参数**:
```json
{
  "contentId": 12345,
  "chapterNum": 1,
  "title": "第一章 开始",
  "content": "章节内容正文...",
  "wordCount": 2000,
  "authorId": 1001,
  "isFree": true,
  "summary": "章节简介"
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

### 2.2 获取内容章节

**接口**: `GET /api/content/{contentId}/chapters`

**描述**: 分页获取指定内容的章节列表

**路径参数**:
- `contentId` (Long): 内容ID

**查询参数**:
- `status` (String, 可选): 章节状态 (DRAFT/PUBLISHED/DELETED)
- `currentPage` (Integer): 当前页码
- `pageSize` (Integer): 页面大小

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 67890,
        "contentId": 12345,
        "chapterNum": 1,
        "title": "第一章 开始",
        "wordCount": 2000,
        "status": "PUBLISHED",
        "isFree": true,
        "createTime": "2024-01-01T10:00:00",
        "publishTime": "2024-01-01T11:00:00"
      }
    ],
    "totalCount": 25,
    "currentPage": 1,
    "pageSize": 20
  }
}
```

### 2.3 获取章节详情

**接口**: `GET /api/content/chapters/{chapterId}`

**描述**: 根据章节ID获取章节详细信息

**路径参数**:
- `chapterId` (Long): 章节ID

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 67890,
    "contentId": 12345,
    "chapterNum": 1,
    "title": "第一章 开始",
    "content": "章节内容正文...",
    "wordCount": 2000,
    "status": "PUBLISHED",
    "isFree": true,
    "summary": "章节简介",
    "viewCount": 1000,
    "likeCount": 50,
    "createTime": "2024-01-01T10:00:00",
    "publishTime": "2024-01-01T11:00:00"
  }
}
```

### 2.4 发布章节

**接口**: `PUT /api/content/chapters/{chapterId}/publish`

**描述**: 发布指定章节

**路径参数**:
- `chapterId` (Long): 章节ID

**查询参数**:
- `authorId` (Long): 作者ID

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 67890,
    "status": "PUBLISHED",
    "publishTime": "2024-01-01T11:00:00"
  }
}
```

---

## 📊 3. 统计管理 (7个接口)

### 3.1 增加浏览量

**接口**: `PUT /api/content/{contentId}/views`

**描述**: 增加内容的浏览量

**路径参数**:
- `contentId` (Long): 内容ID

**查询参数**:
- `increment` (Integer): 增量

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": 10001
}
```

### 3.2 增加点赞数

**接口**: `PUT /api/content/{contentId}/likes`

**描述**: 增加内容的点赞数

**路径参数**:
- `contentId` (Long): 内容ID

**查询参数**:
- `increment` (Integer): 增量

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": 501
}
```

### 3.3 增加评论数

**接口**: `PUT /api/content/{contentId}/comments`

**描述**: 增加内容的评论数

### 3.4 增加收藏数

**接口**: `PUT /api/content/{contentId}/favorites`

**描述**: 增加内容的收藏数

### 3.5 更新评分

**接口**: `PUT /api/content/{contentId}/score`

**描述**: 更新内容的评分

**路径参数**:
- `contentId` (Long): 内容ID

**查询参数**:
- `score` (Integer): 评分值（1-10）

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": 8.6
}
```

### 3.6 获取内容统计

**接口**: `GET /api/content/{contentId}/statistics`

**描述**: 获取内容的统计信息

**路径参数**:
- `contentId` (Long): 内容ID

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "viewCount": 10000,
    "likeCount": 500,
    "commentCount": 200,
    "favoriteCount": 800,
    "score": 8.5,
    "scoreCount": 150,
    "chapterCount": 25,
    "totalWordCount": 50000,
    "dailyViews": 150,
    "weeklyViews": 1000,
    "monthlyViews": 3000,
    "growthRate": 0.15
  }
}
```

---

## 🔍 4. 内容查询 (6个接口)

### 4.1 查询作者内容

**接口**: `GET /api/content/author/{authorId}`

**描述**: 分页查询指定作者的内容

**路径参数**:
- `authorId` (Long): 作者ID

**查询参数**:
- `contentType` (String, 可选): 内容类型
- `status` (String, 可选): 内容状态
- `currentPage` (Integer): 当前页码
- `pageSize` (Integer): 页面大小

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 12345,
        "title": "作者的作品1",
        "contentType": "NOVEL",
        "status": "PUBLISHED",
        "viewCount": 10000,
        "score": 8.5,
        "publishTime": "2024-01-01T11:00:00"
      }
    ],
    "totalCount": 5,
    "currentPage": 1,
    "pageSize": 20
  }
}
```

### 4.2 查询分类内容

**接口**: `GET /api/content/category/{categoryId}`

**描述**: 分页查询指定分类的内容

**路径参数**:
- `categoryId` (Long): 分类ID

**查询参数**:
- `contentType` (String, 可选): 内容类型
- `currentPage` (Integer): 当前页码
- `pageSize` (Integer): 页面大小

### 4.3 搜索内容

**接口**: `GET /api/content/search`

**描述**: 根据关键词搜索内容

**查询参数**:
- `keyword` (String): 搜索关键词
- `contentType` (String, 可选): 内容类型
- `currentPage` (Integer): 当前页码
- `pageSize` (Integer): 页面大小

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 12345,
        "title": "包含关键词的标题",
        "description": "包含关键词的描述",
        "contentType": "NOVEL",
        "status": "PUBLISHED",
        "score": 8.5,
        "highlight": {
          "title": "包含<em>关键词</em>的标题",
          "description": "包含<em>关键词</em>的描述"
        }
      }
    ],
    "totalCount": 20,
    "currentPage": 1,
    "pageSize": 20
  }
}
```

### 4.4 获取热门内容

**接口**: `GET /api/content/popular`

**描述**: 分页获取热门内容

**查询参数**:
- `contentType` (String, 可选): 内容类型
- `timeRange` (Integer, 可选): 时间范围（天）
- `currentPage` (Integer): 当前页码
- `pageSize` (Integer): 页面大小

### 4.5 获取最新内容

**接口**: `GET /api/content/latest`

**描述**: 分页获取最新发布的内容

**查询参数**:
- `contentType` (String, 可选): 内容类型
- `currentPage` (Integer): 当前页码
- `pageSize` (Integer): 页面大小

---

## 🔄 5. 数据同步 (3个接口)

### 5.1 同步作者信息

**接口**: `PUT /api/content/sync/author/{authorId}`

**描述**: 同步更新作者信息到内容表

**路径参数**:
- `authorId` (Long): 作者ID

**查询参数**:
- `nickname` (String): 作者昵称
- `avatar` (String, 可选): 作者头像URL

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": 15
}
```

### 5.2 同步分类信息

**接口**: `PUT /api/content/sync/category/{categoryId}`

**描述**: 同步更新分类信息到内容表

**路径参数**:
- `categoryId` (Long): 分类ID

**查询参数**:
- `categoryName` (String): 分类名称

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": 8
}
```

### 5.3 审核内容

**接口**: `PUT /api/content/{contentId}/review`

**描述**: 审核指定内容

**路径参数**:
- `contentId` (Long): 内容ID

**查询参数**:
- `reviewStatus` (String): 审核状态 (APPROVED/REJECTED)
- `reviewerId` (Long): 审核人ID
- `reviewComment` (String, 可选): 审核意见

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 12345,
    "reviewStatus": "APPROVED",
    "reviewerId": 2001,
    "reviewComment": "内容优质，通过审核",
    "reviewTime": "2024-01-01T15:00:00",
    "status": "PUBLISHED"
  }
}
```

---

## 🎯 数据模型

### ContentResponse 内容响应对象
```json
{
  "id": "内容ID",
  "title": "内容标题",
  "description": "内容描述",
  "contentType": "内容类型（NOVEL/COMIC/VIDEO/AUDIO/ARTICLE）",
  "content": "内容正文",
  "status": "状态（DRAFT/PUBLISHED/OFFLINE）",
  "authorId": "作者ID",
  "authorNickname": "作者昵称",
  "authorAvatar": "作者头像",
  "categoryId": "分类ID",
  "categoryName": "分类名称",
  "tags": "标签列表",
  "coverImage": "封面图片URL",
  "viewCount": "浏览量",
  "likeCount": "点赞数",
  "commentCount": "评论数",
  "favoriteCount": "收藏数",
  "score": "评分",
  "scoreCount": "评分人数",
  "wordCount": "字数",
  "chapterCount": "章节数",
  "isPaid": "是否付费",
  "isPublic": "是否公开",
  "allowComment": "是否允许评论",
  "reviewStatus": "审核状态",
  "reviewComment": "审核意见",
  "createTime": "创建时间",
  "publishTime": "发布时间",
  "updateTime": "更新时间"
}
```

### ChapterResponse 章节响应对象
```json
{
  "id": "章节ID",
  "contentId": "内容ID",
  "chapterNum": "章节号",
  "title": "章节标题",
  "content": "章节内容",
  "summary": "章节简介",
  "wordCount": "字数",
  "status": "状态（DRAFT/PUBLISHED/DELETED）",
  "isFree": "是否免费",
  "viewCount": "浏览量",
  "likeCount": "点赞数",
  "createTime": "创建时间",
  "publishTime": "发布时间",
  "updateTime": "更新时间"
}
```

## 🚨 错误代码

| 错误码 | 描述 | 解决方案 |
|--------|------|----------|
| CREATE_CONTENT_API_FAILED | 创建内容API调用失败 | 检查请求参数和权限 |
| UPDATE_CONTENT_API_FAILED | 更新内容API调用失败 | 确认内容存在且有权限 |
| DELETE_CONTENT_API_FAILED | 删除内容API调用失败 | 确认内容存在且有权限 |
| GET_CONTENT_API_FAILED | 获取内容详情API调用失败 | 检查内容ID是否正确 |
| QUERY_CONTENTS_API_FAILED | 查询内容列表API调用失败 | 检查查询参数格式 |
| PUBLISH_CONTENT_API_FAILED | 发布内容API调用失败 | 确认内容状态和权限 |
| OFFLINE_CONTENT_API_FAILED | 下线内容API调用失败 | 确认内容状态和权限 |
| CREATE_CHAPTER_API_FAILED | 创建章节API调用失败 | 检查章节参数和权限 |
| GET_CHAPTERS_API_FAILED | 获取内容章节API调用失败 | 检查内容ID |
| GET_CHAPTER_API_FAILED | 获取章节详情API调用失败 | 检查章节ID |
| PUBLISH_CHAPTER_API_FAILED | 发布章节API调用失败 | 确认章节状态和权限 |
| INCREASE_VIEW_COUNT_API_FAILED | 增加浏览量API调用失败 | 检查内容ID和增量 |
| INCREASE_LIKE_COUNT_API_FAILED | 增加点赞数API调用失败 | 检查内容ID和增量 |
| INCREASE_COMMENT_COUNT_API_FAILED | 增加评论数API调用失败 | 检查内容ID和增量 |
| INCREASE_FAVORITE_COUNT_API_FAILED | 增加收藏数API调用失败 | 检查内容ID和增量 |
| UPDATE_SCORE_API_FAILED | 更新评分API调用失败 | 检查评分值范围（1-10） |
| GET_STATISTICS_API_FAILED | 获取内容统计API调用失败 | 检查内容ID |
| GET_AUTHOR_CONTENTS_API_FAILED | 查询作者内容API调用失败 | 检查作者ID |
| GET_CATEGORY_CONTENTS_API_FAILED | 查询分类内容API调用失败 | 检查分类ID |
| SEARCH_CONTENTS_API_FAILED | 搜索内容API调用失败 | 检查搜索关键词 |
| GET_POPULAR_CONTENTS_API_FAILED | 获取热门内容API调用失败 | 检查查询参数 |
| GET_LATEST_CONTENTS_API_FAILED | 获取最新内容API调用失败 | 检查查询参数 |
| UPDATE_AUTHOR_INFO_API_FAILED | 同步作者信息API调用失败 | 检查作者信息格式 |
| UPDATE_CATEGORY_INFO_API_FAILED | 同步分类信息API调用失败 | 检查分类信息格式 |
| REVIEW_CONTENT_API_FAILED | 审核内容API调用失败 | 检查审核参数和权限 |

## 📈 性能优化建议

1. **缓存策略**: 内容详情、热门内容等查询接口建议使用Redis缓存
2. **分页优化**: 大量数据查询时建议使用游标分页
3. **搜索优化**: 使用Elasticsearch进行全文搜索
4. **图片优化**: 封面图片建议使用CDN加速
5. **统计优化**: 浏览量等统计可以异步更新

## 🔧 使用示例

### JavaScript 示例
```javascript
// 创建内容
const createContent = async (contentData) => {
  const response = await fetch('/api/content', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': 'Bearer ' + token
    },
    body: JSON.stringify(contentData)
  });
  return response.json();
};

// 获取内容详情
const getContentDetail = async (contentId) => {
  const response = await fetch(`/api/content/${contentId}`);
  return response.json();
};

// 搜索内容
const searchContents = async (keyword, page = 1) => {
  const response = await fetch(
    `/api/content/search?keyword=${encodeURIComponent(keyword)}&currentPage=${page}&pageSize=20`
  );
  return response.json();
};
```

### cURL 示例
```bash
# 创建内容
curl -X POST "/api/content" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer your-token" \
  -d '{
    "title": "我的小说",
    "contentType": "NOVEL",
    "authorId": 1001,
    "categoryId": 201
  }'

# 获取内容详情
curl -X GET "/api/content/12345"

# 搜索内容
curl -X GET "/api/content/search?keyword=玄幻&currentPage=1&pageSize=10"
```

---

**联系信息**:  
- 控制器: ContentController  
- 版本: 2.0.0 (简洁版)  
- 维护: GIG团队  
- 更新: 2024-01-01