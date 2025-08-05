# Content Controller REST API 文档

**控制器**: ContentController  
**版本**: 2.0.0 (极简版)  
**基础路径**: `/api/content`  
**接口数量**: 19个  
**更新时间**: 2024-01-01  

## 🚀 概述

内容管理控制器 - 极简版，基于12个核心Facade方法设计的精简API。支持多种内容类型：NOVEL、COMIC、VIDEO、ARTICLE、AUDIO。

**设计理念**:
- **极简设计**: 19个API接口替代原有21个接口
- **万能查询**: 单个查询接口替代多个具体查询接口
- **统一管理**: 状态、统计的集中管理接口
- **高效批量**: 支持批量操作，提升性能

**主要功能**:
- **内容管理**: 创建、更新、删除、状态管理
- **万能查询**: 一个接口替代多个具体查询，支持多种排序（含新增的观看数、分享数等）
- **搜索推荐**: 搜索和推荐功能
- **统计管理**: 统一的统计信息管理
- **数据同步**: 外部数据同步

## 📋 接口分类

| 分类 | 接口数量 | 功能描述 |
|------|----------|----------|
| **核心CRUD功能** | 4个 | 内容的增删改查 |
| **万能查询功能** | 6个 | 条件查询、搜索、推荐 + 3个便民接口 |
| **状态管理功能** | 4个 | 状态更新、批量操作 + 2个便民接口 |
| **统计管理功能** | 3个 | 统计更新、浏览量增加 + 1个便民接口 |
| **数据同步功能** | 2个 | 外部数据同步 + 1个便民接口 |

---

## 🔧 1. 核心CRUD功能 (4个接口)

### 1.1 创建内容

**接口**: `POST /api/content`

**描述**: 创建新的内容，支持多种内容类型

**请求体**:
```json
{
  "title": "我的玄幻小说",
  "description": "这是一部精彩的玄幻小说",
  "contentType": "NOVEL",
  "content": "内容正文...",
  "authorId": 1001,
  "categoryId": 201,
  "tags": ["玄幻", "热血", "升级"],
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

### 1.2 更新内容

**接口**: `PUT /api/content`

**描述**: 更新已有内容的信息

**请求体**:
```json
{
  "id": 12345,
  "title": "更新后的标题",
  "description": "更新后的描述",
  "content": "更新后的内容...",
  "tags": ["玄幻", "热血", "升级", "新标签"],
  "coverImage": "https://example.com/new-cover.jpg",
  "isPublic": true,
  "allowComment": true
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

### 1.3 获取内容详情

**接口**: `GET /api/content/{contentId}`

**描述**: 根据内容ID获取内容详细信息

**路径参数**:
- `contentId` (Long): 内容ID

**查询参数**:
- `includeOffline` (Boolean, 可选): 是否包含下线内容，默认false

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 12345,
    "title": "我的玄幻小说",
    "description": "这是一部精彩的玄幻小说",
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
    "shareCount": 150,
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

### 1.4 删除内容

**接口**: `DELETE /api/content/{contentId}`

**描述**: 逻辑删除指定内容

**路径参数**:
- `contentId` (Long): 内容ID

**查询参数**:
- `operatorId` (Long, 必需): 操作人ID

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

---

## 🔍 2. 万能查询功能 (6个接口)

### 2.1 万能条件查询内容 ⭐

**接口**: `GET /api/content/query`

**描述**: 根据多种条件查询内容列表，替代所有具体查询API，支持新增的排序字段

**核心功能**: 
- 替代`getContentsByAuthor`、`getContentsByCategory`、`getPopularContents`、`getLatestContents`等方法
- 支持按观看数、点赞数、收藏数、分享数等新增字段排序
- 支持时间范围筛选（热门内容）

**查询参数**:
- `authorId` (Long, 可选): 作者ID
- `categoryId` (Long, 可选): 分类ID
- `contentType` (String, 可选): 内容类型
- `status` (String, 可选): 状态
- `reviewStatus` (String, 可选): 审核状态
- `minScore` (Double, 可选): 最小评分
- `timeRange` (Integer, 可选): 时间范围天数（用于热门内容筛选）
- `orderBy` (String, 可选): 排序字段（createTime、updateTime、viewCount、likeCount、favoriteCount、shareCount、commentCount、score），默认"createTime"
- `orderDirection` (String, 可选): 排序方向（ASC、DESC），默认"DESC"
- `currentPage` (Integer, 必需): 当前页码
- `pageSize` (Integer, 必需): 页面大小

**调用示例**:
```bash
# 查询指定作者的小说（按发布时间排序）
GET /api/content/query?authorId=1001&contentType=NOVEL&status=PUBLISHED&orderBy=publishTime&orderDirection=DESC&currentPage=1&pageSize=20

# 查询热门内容（最近7天，按观看数排序）
GET /api/content/query?status=PUBLISHED&timeRange=7&orderBy=viewCount&orderDirection=DESC&currentPage=1&pageSize=50

# 查询高评分内容（评分>8.0，按评分排序）
GET /api/content/query?status=PUBLISHED&minScore=8.0&orderBy=score&orderDirection=DESC&currentPage=1&pageSize=20

# 查询最多分享的内容（按分享数排序）
GET /api/content/query?status=PUBLISHED&orderBy=shareCount&orderDirection=DESC&currentPage=1&pageSize=30

# 查询最受欢迎的内容（按收藏数排序）
GET /api/content/query?status=PUBLISHED&orderBy=favoriteCount&orderDirection=DESC&currentPage=1&pageSize=20
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
        "title": "我的玄幻小说",
        "description": "这是一部精彩的玄幻小说",
        "contentType": "NOVEL",
        "status": "PUBLISHED",
        "authorNickname": "知名作家",
        "categoryName": "玄幻小说",
        "viewCount": 10000,
        "likeCount": 500,
        "favoriteCount": 800,
        "shareCount": 150,
        "score": 8.5,
        "createTime": "2024-01-01T10:00:00"
      }
    ],
    "totalCount": 100,
    "totalPage": 5,
    "currentPage": 1,
    "pageSize": 20,
    "hasNext": true,
    "hasPrevious": false
  }
}
```

### 2.2 搜索内容 ⭐

**接口**: `GET /api/content/search`

**描述**: 根据标题、描述、标签进行搜索

**查询参数**:
- `keyword` (String, 必需): 搜索关键词
- `contentType` (String, 可选): 内容类型
- `currentPage` (Integer, 必需): 当前页码
- `pageSize` (Integer, 必需): 页面大小

**调用示例**:
```bash
# 搜索包含"玄幻"的小说
GET /api/content/search?keyword=玄幻&contentType=NOVEL&currentPage=1&pageSize=20

# 全局搜索
GET /api/content/search?keyword=修炼&currentPage=1&pageSize=50
```

### 2.3 获取推荐内容 ⭐

**接口**: `GET /api/content/recommendations`

**描述**: 基于用户行为和内容特征获取推荐内容

**查询参数**:
- `userId` (Long, 必需): 用户ID
- `excludeContentIds` (String, 可选): 排除的内容ID列表（逗号分隔）
- `limit` (Integer, 可选): 返回数量限制，默认10

**调用示例**:
```bash
GET /api/content/recommendations?userId=1001&excludeContentIds=12345,12346&limit=10
```

### 2.4 获取作者的内容（便民接口）

**接口**: `GET /api/content/author/{authorId}`

**描述**: 便民接口，获取指定作者的内容

**路径参数**:
- `authorId` (Long): 作者ID

**查询参数**:
- `contentType` (String, 可选): 内容类型
- `status` (String, 可选): 状态，默认"PUBLISHED"
- `currentPage` (Integer, 必需): 当前页码
- `pageSize` (Integer, 必需): 页面大小

**内部实现**: 调用万能查询接口

### 2.5 获取分类的内容（便民接口）

**接口**: `GET /api/content/category/{categoryId}`

**描述**: 便民接口，获取指定分类的内容

**路径参数**:
- `categoryId` (Long): 分类ID

**查询参数**:
- `contentType` (String, 可选): 内容类型
- `currentPage` (Integer, 必需): 当前页码
- `pageSize` (Integer, 必需): 页面大小

### 2.6 获取热门内容（便民接口）

**接口**: `GET /api/content/popular`

**描述**: 便民接口，获取热门内容

**查询参数**:
- `contentType` (String, 可选): 内容类型
- `timeRange` (Integer, 可选): 时间范围（天），默认7
- `currentPage` (Integer, 必需): 当前页码
- `pageSize` (Integer, 必需): 页面大小

**内部实现**: 调用万能查询接口，按观看数排序

---

## ⚙️ 3. 状态管理功能 (4个接口)

### 3.1 更新内容状态 ⭐

**接口**: `PUT /api/content/{contentId}/status`

**描述**: 统一状态管理，可实现发布、审核、下线等操作

**路径参数**:
- `contentId` (Long): 内容ID

**请求体**:
```json
{
  "status": "PUBLISHED",
  "reviewStatus": "APPROVED",
  "operatorId": 2001,
  "comment": "内容审核通过"
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": true
}
```

### 3.2 批量更新状态 ⭐

**接口**: `PUT /api/content/batch/status`

**描述**: 批量更新内容状态

**请求体**:
```json
{
  "ids": [12345, 12346, 12347],
  "status": "PUBLISHED"
}
```

### 3.3 发布内容（便民接口）

**接口**: `PUT /api/content/{contentId}/publish`

**描述**: 便民接口，发布指定内容

**路径参数**:
- `contentId` (Long): 内容ID

**查询参数**:
- `authorId` (Long, 必需): 作者ID

**内部实现**: 调用状态更新接口，设置状态为"PUBLISHED"

### 3.4 下线内容（便民接口）

**接口**: `PUT /api/content/{contentId}/offline`

**描述**: 便民接口，下线指定内容

**路径参数**:
- `contentId` (Long): 内容ID

**查询参数**:
- `operatorId` (Long, 必需): 操作人ID

**内部实现**: 调用状态更新接口，设置状态为"OFFLINE"

---

## 📊 4. 统计管理功能 (3个接口)

### 4.1 更新内容统计信息 ⭐

**接口**: `PUT /api/content/{contentId}/stats`

**描述**: 统一统计管理，可实现各种统计数据更新

**路径参数**:
- `contentId` (Long): 内容ID

**请求体**:
```json
{
  "viewCount": 1,
  "likeCount": 1,
  "commentCount": 1,
  "favoriteCount": 1,
  "score": 8.5
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": true
}
```

### 4.2 增加浏览量 ⭐

**接口**: `PUT /api/content/{contentId}/view`

**描述**: 最常用的统计操作单独提供，优化性能

**路径参数**:
- `contentId` (Long): 内容ID

**查询参数**:
- `increment` (Integer, 可选): 增量，默认1

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": 10001
}
```

### 4.3 获取内容统计信息（便民接口）

**接口**: `GET /api/content/{contentId}/stats`

**描述**: 便民接口，获取内容的统计信息

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
    "shareCount": 150,
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

## 🔄 5. 数据同步功能 (2个接口)

### 5.1 同步外部数据 ⭐

**接口**: `PUT /api/content/sync/{syncType}/{targetId}`

**描述**: 统一数据同步，可实现作者信息、分类信息等同步

**路径参数**:
- `syncType` (String): 同步类型（AUTHOR、CATEGORY）
- `targetId` (Long): 目标ID（作者ID或分类ID）

**请求体**:
```json
{
  "nickname": "新的作家昵称",
  "avatar": "https://example.com/new-avatar.jpg"
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": 15
}
```

### 5.2 更新作者信息（便民接口）

**接口**: `PUT /api/content/sync/author/{authorId}`

**描述**: 便民接口，同步作者信息

**路径参数**:
- `authorId` (Long): 作者ID

**请求体**:
```json
{
  "nickname": "新的作家昵称",
  "avatar": "https://example.com/new-avatar.jpg"
}
```

**内部实现**: 调用同步外部数据接口

---

## 🎯 数据模型

### ContentResponse 内容响应对象
```json
{
  "id": 12345,                    // 内容ID
  "title": "我的玄幻小说",          // 内容标题
  "description": "这是一部精彩的玄幻小说", // 内容描述
  "contentType": "NOVEL",         // 内容类型
  "content": "内容正文...",        // 内容正文
  "status": "PUBLISHED",          // 状态
  "authorId": 1001,               // 作者ID
  "authorNickname": "知名作家",    // 作者昵称
  "authorAvatar": "https://example.com/avatar.jpg", // 作者头像
  "categoryId": 201,              // 分类ID
  "categoryName": "玄幻小说",      // 分类名称
  "tags": ["玄幻", "热血", "升级"], // 标签列表
  "coverImage": "https://example.com/cover.jpg", // 封面图片URL
  "viewCount": 10000,             // 浏览量
  "likeCount": 500,               // 点赞数
  "commentCount": 200,            // 评论数
  "favoriteCount": 800,           // 收藏数
  "shareCount": 150,              // 分享数
  "score": 8.5,                   // 评分
  "scoreCount": 150,              // 评分人数
  "wordCount": 100000,            // 字数
  "chapterCount": 50,             // 章节数
  "isPaid": true,                 // 是否付费
  "isPublic": true,               // 是否公开
  "allowComment": true,           // 是否允许评论
  "reviewStatus": "APPROVED",     // 审核状态
  "reviewComment": "审核通过",     // 审核意见
  "createTime": "2024-01-01T10:00:00", // 创建时间
  "publishTime": "2024-01-01T11:00:00", // 发布时间
  "updateTime": "2024-01-15T14:30:00"   // 更新时间
}
```

### ContentCreateRequest 内容创建请求对象
```json
{
  "title": "我的玄幻小说",         // 内容标题（必填）
  "description": "这是一部精彩的玄幻小说", // 内容描述
  "contentType": "NOVEL",        // 内容类型（必填）
  "content": "内容正文...",       // 内容正文
  "authorId": 1001,              // 作者ID（必填）
  "categoryId": 201,             // 分类ID（必填）
  "tags": ["玄幻", "热血", "升级"], // 标签列表
  "coverImage": "https://example.com/cover.jpg", // 封面图片URL
  "isPublic": true,              // 是否公开，默认true
  "allowComment": true           // 是否允许评论，默认true
}
```

### ContentUpdateRequest 内容更新请求对象
```json
{
  "id": 12345,                   // 内容ID（必填）
  "title": "更新后的标题",        // 内容标题
  "description": "更新后的描述",   // 内容描述
  "content": "更新后的内容...",    // 内容正文
  "tags": ["玄幻", "热血", "升级", "新标签"], // 标签列表
  "coverImage": "https://example.com/new-cover.jpg", // 封面图片URL
  "isPublic": true,              // 是否公开
  "allowComment": true           // 是否允许评论
}
```

## 🚨 错误代码

| HTTP状态码 | 错误码 | 描述 | 解决方案 |
|-----------|--------|------|----------|
| 400 | INVALID_PARAMETER | 参数验证失败 | 检查请求参数的格式和必填项 |
| 404 | CONTENT_NOT_FOUND | 内容不存在 | 检查内容ID是否正确 |
| 404 | AUTHOR_NOT_FOUND | 作者不存在 | 检查作者ID是否正确 |
| 404 | CATEGORY_NOT_FOUND | 分类不存在 | 检查分类ID是否正确 |
| 500 | CONTENT_CREATE_FAILED | 内容创建失败 | 检查数据完整性和权限 |
| 500 | CONTENT_UPDATE_FAILED | 内容更新失败 | 确认内容存在且有权限 |
| 500 | CONTENT_DELETE_FAILED | 内容删除失败 | 确认内容存在且有权限 |
| 400 | INVALID_CONTENT_STATUS | 内容状态无效 | 检查状态值是否正确 |
| 403 | INSUFFICIENT_PERMISSION | 权限不足 | 确认操作权限 |
| 500 | STATISTICS_UPDATE_FAILED | 统计更新失败 | 检查统计参数 |
| 500 | SEARCH_FAILED | 搜索失败 | 检查搜索参数 |
| 500 | SYNC_DATA_FAILED | 数据同步失败 | 检查同步参数和数据格式 |

## 📈 接口使用示例

### 内容管理
```javascript
// 创建内容
async function createContent(contentData) {
    const response = await fetch('/api/content', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(contentData)
    });
    return response.json();
}

// 获取内容详情
async function getContentDetail(contentId) {
    const response = await fetch(`/api/content/${contentId}`);
    return response.json();
}
```

### 万能查询
```javascript
// 获取热门内容（按观看数排序）
async function getHotContents(contentType, days = 7, page = 1, size = 20) {
    const params = new URLSearchParams({
        status: 'PUBLISHED',
        timeRange: days,
        orderBy: 'viewCount',
        orderDirection: 'DESC',
        currentPage: page,
        pageSize: size
    });
    
    if (contentType) {
        params.append('contentType', contentType);
    }
    
    const response = await fetch(`/api/content/query?${params}`);
    return response.json();
}

// 获取最受欢迎内容（按收藏数排序）
async function getPopularContents(page = 1, size = 20) {
    const params = new URLSearchParams({
        status: 'PUBLISHED',
        orderBy: 'favoriteCount',
        orderDirection: 'DESC',
        currentPage: page,
        pageSize: size
    });
    
    const response = await fetch(`/api/content/query?${params}`);
    return response.json();
}

// 获取最多分享内容（按分享数排序）
async function getMostSharedContents(page = 1, size = 20) {
    const params = new URLSearchParams({
        status: 'PUBLISHED',
        orderBy: 'shareCount',
        orderDirection: 'DESC',
        currentPage: page,
        pageSize: size
    });
    
    const response = await fetch(`/api/content/query?${params}`);
    return response.json();
}
```

### 统计操作
```javascript
// 增加浏览量
async function increaseViewCount(contentId) {
    const response = await fetch(`/api/content/${contentId}/view`, {
        method: 'PUT'
    });
    return response.json();
}

// 更新统计信息
async function updateStats(contentId, stats) {
    const response = await fetch(`/api/content/${contentId}/stats`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(stats)
    });
    return response.json();
}
```

### 状态管理
```javascript
// 发布内容
async function publishContent(contentId, authorId) {
    const response = await fetch(`/api/content/${contentId}/publish?authorId=${authorId}`, {
        method: 'PUT'
    });
    return response.json();
}

// 批量更新状态
async function batchUpdateStatus(contentIds, status) {
    const response = await fetch('/api/content/batch/status', {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            ids: contentIds,
            status: status
        })
    });
    return response.json();
}
```

## 🔧 性能优化建议

1. **缓存策略**:
   - 内容详情: ETag + Last-Modified
   - 热门内容: 缓存30分钟
   - 推荐列表: 缓存5分钟

2. **查询优化**:
   - 使用万能查询减少API调用次数
   - 并行请求无依赖的接口
   - 合理使用排序字段提升用户体验

3. **统计优化**:
   - 浏览量更新使用异步方式
   - 批量统计更新优于单个更新
   - 统计查询使用缓存

4. **请求优化**:
   ```javascript
   // 推荐：并行获取内容和统计信息
   Promise.all([
       getContentDetail(contentId),
       getContentStats(contentId)
   ]);
   
   // 推荐：使用万能查询获取不同排序的内容
   Promise.all([
       getHotContents('NOVEL'),        // 按观看数
       getPopularContents(),           // 按收藏数
       getMostSharedContents()         // 按分享数
   ]);
   ```

## 🚀 极简设计优势

1. **接口精简**: 从21个接口略微调整到19个，保持高效
2. **万能查询**: 1个查询接口替代6个具体查询接口，新增排序字段支持
3. **统一管理**: 状态和统计的集中管理
4. **便民接口**: 保留7个高频便民接口，平衡灵活性和易用性
5. **新增功能**: 支持按观看数、分享数、收藏数等新字段排序

## 🆕 新增排序功能

万能查询接口现在支持以下排序字段：
- **createTime**: 创建时间排序
- **updateTime**: 更新时间排序  
- **viewCount**: 观看数排序 ⭐ 新增
- **likeCount**: 点赞数排序 ⭐ 新增
- **favoriteCount**: 收藏数排序 ⭐ 新增
- **shareCount**: 分享数排序 ⭐ 新增
- **commentCount**: 评论数排序 ⭐ 新增
- **score**: 评分排序 ⭐ 新增

## 🔗 相关文档

- [ContentFacadeService API 文档](../facade/content-facade-service-api.md)
- [Content Chapter Controller API 文档](./content-chapter-controller-api.md)
- [Content Purchase Controller API 文档](./content-purchase-controller-api.md)
- [Content Payment Controller API 文档](./content-payment-controller-api.md)

---

**联系信息**:  
- 控制器: ContentController  
- 版本: 2.0.0 (极简版)  
- 基础路径: `/api/content`  
- 维护: GIG团队  
- 更新: 2024-01-01