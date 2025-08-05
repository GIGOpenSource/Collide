# Content Chapter Controller REST API 文档

**控制器**: ContentChapterController  
**版本**: 2.0.0 (极简版)  
**基础路径**: `/api/v1/content/chapters`  
**接口数量**: 11个  
**更新时间**: 2024-01-31  

## 🚀 概述

内容章节控制器 - 极简版，基于8个核心Facade方法设计的精简API。专为多章节内容（如小说、漫画）设计，支持万能查询、章节导航、统计分析等功能。

**设计理念**:
- **极简设计**: 11个API接口替代原有24个接口
- **万能查询**: 单个查询接口替代多个具体查询接口
- **统一导航**: 一个接口支持所有导航操作
- **高效批量**: 支持批量操作，提升性能

**主要功能**:
- **万能查询**: 根据多种条件查询章节，替代所有具体查询
- **智能导航**: 统一的章节导航接口（上一章、下一章、首末章）
- **搜索功能**: 按标题、内容搜索章节
- **统计信息**: 一次性获取完整统计数据
- **批量操作**: 批量状态更新、批量删除

## 📋 接口分类

| 分类 | 接口数量 | 功能描述 |
|------|----------|----------|
| **核心CRUD功能** | 2个 | 章节查询、删除 |
| **万能查询功能** | 6个 | 条件查询、导航查询、搜索 + 3个便民接口 |
| **统计功能** | 1个 | 完整统计信息 |
| **批量操作功能** | 2个 | 批量状态更新、批量删除 |

---

## 🔧 1. 核心CRUD功能 (2个接口)

### 1.1 获取章节详情

**接口**: `GET /api/v1/content/chapters/{id}`

**描述**: 根据章节ID获取章节详情

**路径参数**:
- `id` (Long): 章节ID

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 67890,
    "contentId": 12345,
    "chapterNum": 1,
    "title": "第一章 初入江湖",
    "wordCount": 2500,
    "status": "PUBLISHED",
    "isFree": true,
    "createTime": "2024-01-01T10:00:00",
    "publishTime": "2024-01-01T10:30:00"
  }
}
```

**错误响应**:
```json
{
  "code": 404,
  "message": "CHAPTER_NOT_FOUND",
  "data": null
}
```

### 1.2 删除章节

**接口**: `DELETE /api/v1/content/chapters/{id}`

**描述**: 逻辑删除指定章节

**路径参数**:
- `id` (Long): 章节ID

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": true
}
```

---

## 🔍 2. 万能查询功能 (6个接口)

### 2.1 万能条件查询章节 ⭐

**接口**: `GET /api/v1/content/chapters/query`

**描述**: 根据多种条件查询章节列表，替代所有具体查询API

**核心功能**: 
- 替代原有14个具体查询接口
- 支持按内容、状态、章节号范围、字数范围查询
- 支持灵活排序和分页

**查询参数**:
- `contentId` (Long, 可选): 内容ID
- `status` (String, 可选): 章节状态
- `chapterNumStart` (Integer, 可选): 章节号起始
- `chapterNumEnd` (Integer, 可选): 章节号结束
- `minWordCount` (Integer, 可选): 最小字数
- `maxWordCount` (Integer, 可选): 最大字数
- `orderBy` (String, 可选): 排序字段，默认"chapterNum"
- `orderDirection` (String, 可选): 排序方向，默认"ASC"
- `currentPage` (Integer, 可选): 当前页码
- `pageSize` (Integer, 可选): 页面大小

**调用示例**:
```bash
# 查询指定内容的已发布章节（按章节号排序）
GET /api/v1/content/chapters/query?contentId=12345&status=PUBLISHED&orderBy=chapterNum&orderDirection=ASC&currentPage=1&pageSize=20

# 查询字数在2000-4000之间的章节
GET /api/v1/content/chapters/query?contentId=12345&minWordCount=2000&maxWordCount=4000&orderBy=wordCount&orderDirection=DESC

# 查询最新更新的章节（跨所有内容）
GET /api/v1/content/chapters/query?status=PUBLISHED&orderBy=updateTime&orderDirection=DESC&currentPage=1&pageSize=20
```

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
        "title": "第一章 初入江湖",
        "wordCount": 2500,
        "status": "PUBLISHED",
        "isFree": true,
        "createTime": "2024-01-01T10:00:00"
      }
    ],
    "totalCount": 50,
    "totalPage": 3,
    "currentPage": 1,
    "pageSize": 20,
    "hasNext": true,
    "hasPrevious": false
  }
}
```

### 2.2 章节导航查询 ⭐

**接口**: `GET /api/v1/content/chapters/navigation`

**描述**: 章节导航查询，替代上一章、下一章、首章、末章等4个接口

**查询参数**:
- `contentId` (Long, 必需): 内容ID
- `currentChapterNum` (Integer, 可选): 当前章节号
- `direction` (String, 必需): 导航方向（next、previous、first、last）

**调用示例**:
```bash
# 获取下一章节
GET /api/v1/content/chapters/navigation?contentId=12345&currentChapterNum=1&direction=next

# 获取上一章节
GET /api/v1/content/chapters/navigation?contentId=12345&currentChapterNum=5&direction=previous

# 获取第一章节
GET /api/v1/content/chapters/navigation?contentId=12345&direction=first

# 获取最后章节
GET /api/v1/content/chapters/navigation?contentId=12345&direction=last
```

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 67891,
    "contentId": 12345,
    "chapterNum": 2,
    "title": "第二章 奇遇连连",
    "wordCount": 3000,
    "status": "PUBLISHED",
    "isFree": true
  }
}
```

### 2.3 搜索章节 ⭐

**接口**: `GET /api/v1/content/chapters/search`

**描述**: 按标题、内容搜索章节，替代searchChaptersByTitle接口

**查询参数**:
- `keyword` (String, 必需): 搜索关键词
- `contentId` (Long, 可选): 内容ID（限定搜索范围）
- `status` (String, 可选): 章节状态
- `currentPage` (Integer, 必需): 当前页码
- `pageSize` (Integer, 必需): 页面大小

**调用示例**:
```bash
# 在指定内容中搜索包含"江湖"的章节
GET /api/v1/content/chapters/search?keyword=江湖&contentId=12345&status=PUBLISHED&currentPage=1&pageSize=20

# 全局搜索包含"修炼"的章节
GET /api/v1/content/chapters/search?keyword=修炼&currentPage=1&pageSize=50
```

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
        "title": "第一章 初入江湖",
        "wordCount": 2500,
        "status": "PUBLISHED",
        "highlight": {
          "title": "第一章 初入<em>江湖</em>"
        }
      }
    ],
    "totalCount": 5,
    "totalPage": 1,
    "currentPage": 1,
    "pageSize": 20
  }
}
```

### 2.4 获取内容的章节列表（便民接口）

**接口**: `GET /api/v1/content/chapters/content/{contentId}`

**描述**: 便民接口，获取指定内容的所有章节列表

**路径参数**:
- `contentId` (Long): 内容ID

**查询参数**:
- `status` (String, 可选): 章节状态，默认"PUBLISHED"

**内部实现**: 调用万能查询接口
```java
// 内部调用
contentChapterFacadeService.getChaptersByConditions(contentId, "PUBLISHED", null, null, null, null, "chapterNum", "ASC", null, null)
```

### 2.5 获取内容的已发布章节（便民接口）

**接口**: `GET /api/v1/content/chapters/content/{contentId}/published`

**描述**: 便民接口，获取指定内容的已发布章节列表

**路径参数**:
- `contentId` (Long): 内容ID

**内部实现**: 调用万能查询接口
```java
// 内部调用
contentChapterFacadeService.getChaptersByConditions(contentId, "PUBLISHED", null, null, null, null, "chapterNum", "ASC", null, null)
```

### 2.6 获取最新章节列表（便民接口）

**接口**: `GET /api/v1/content/chapters/latest`

**描述**: 便民接口，获取最新更新的章节列表

**查询参数**:
- `currentPage` (Integer, 必需): 当前页码
- `pageSize` (Integer, 必需): 页面大小

**内部实现**: 调用万能查询接口
```java
// 内部调用
contentChapterFacadeService.getChaptersByConditions(null, "PUBLISHED", null, null, null, null, "updateTime", "DESC", currentPage, pageSize)
```

---

## 📊 3. 统计功能 (1个接口)

### 3.1 获取章节统计信息 ⭐

**接口**: `GET /api/v1/content/chapters/content/{contentId}/stats`

**描述**: 获取指定内容的章节统计信息，替代所有单个统计接口

**核心功能**: 
- 替代原有5个统计接口
- 一次调用返回完整统计信息

**路径参数**:
- `contentId` (Long): 内容ID

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "totalChapters": 25,
    "publishedChapters": 20,
    "draftChapters": 5,
    "totalWords": 75000,
    "publishedWords": 60000,
    "avgWordsPerChapter": 3000,
    "maxWordCount": 5000,
    "minWordCount": 2000,
    "freeChapters": 3,
    "paidChapters": 17,
    "latestChapterNum": 25,
    "latestUpdateTime": "2024-01-31T14:30:00",
    "firstPublishTime": "2024-01-01T10:00:00"
  }
}
```

---

## ⚙️ 4. 批量操作功能 (2个接口)

### 4.1 批量更新章节状态

**接口**: `PUT /api/v1/content/chapters/batch/status`

**描述**: 批量更新指定章节的状态

**请求体**:
```json
{
  "ids": [67890, 67891, 67892],
  "status": "PUBLISHED"
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

### 4.2 批量删除章节

**接口**: `DELETE /api/v1/content/chapters/batch`

**描述**: 批量删除章节

**请求体**:
```json
{
  "ids": [67890, 67891, 67892]
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

---

## 🎯 数据模型

### ChapterResponse 章节响应对象
```json
{
  "id": 67890,                    // 章节ID
  "contentId": 12345,             // 内容ID
  "contentTitle": "我的玄幻小说",   // 内容标题（仅在跨内容查询时返回）
  "chapterNum": 1,                // 章节号
  "title": "第一章 初入江湖",      // 章节标题
  "content": "章节内容...",       // 章节内容（仅在详情查询时返回）
  "summary": "章节简介",          // 章节简介
  "wordCount": 2500,              // 字数
  "status": "PUBLISHED",          // 状态（DRAFT/PUBLISHED/DELETED）
  "isFree": true,                 // 是否免费
  "viewCount": 1000,              // 浏览量
  "likeCount": 50,                // 点赞数
  "commentCount": 20,             // 评论数
  "createTime": "2024-01-01T10:00:00",  // 创建时间
  "publishTime": "2024-01-01T10:30:00", // 发布时间
  "updateTime": "2024-01-01T11:00:00",  // 更新时间
  "highlight": {                  // 搜索高亮信息（仅在搜索时返回）
    "title": "第一章 初入<em>江湖</em>"
  }
}
```

### PageResponse 分页响应对象
```json
{
  "records": [],           // 记录列表
  "totalCount": 100,       // 总记录数
  "totalPage": 5,          // 总页数
  "currentPage": 1,        // 当前页码
  "pageSize": 20,          // 页面大小
  "hasNext": true,         // 是否有下一页
  "hasPrevious": false     // 是否有上一页
}
```

## 🚨 错误代码

| HTTP状态码 | 错误码 | 描述 | 解决方案 |
|-----------|--------|------|----------|
| 400 | INVALID_PARAMETER | 参数验证失败 | 检查请求参数的格式和必填项 |
| 404 | CHAPTER_NOT_FOUND | 章节不存在 | 检查章节ID是否正确 |
| 404 | CONTENT_NOT_FOUND | 内容不存在 | 检查内容ID是否正确 |
| 500 | DELETE_CHAPTER_FAILED | 删除章节失败 | 确认章节存在且有权限 |
| 500 | BATCH_UPDATE_FAILED | 批量更新失败 | 检查章节ID列表和状态值 |
| 500 | SEARCH_FAILED | 搜索失败 | 检查搜索关键词和参数 |
| 500 | STATS_CALCULATION_FAILED | 统计计算失败 | 检查统计参数 |
| 500 | NAVIGATION_FAILED | 导航查询失败 | 检查内容ID和导航方向 |

## 📈 接口使用示例

### 阅读器章节导航
```javascript
// 获取章节导航信息
async function getChapterNavigation(contentId, currentChapterNum) {
    const promises = [
        fetch(`/api/v1/content/chapters/navigation?contentId=${contentId}&currentChapterNum=${currentChapterNum}&direction=previous`),
        fetch(`/api/v1/content/chapters/navigation?contentId=${contentId}&currentChapterNum=${currentChapterNum}&direction=next`),
        fetch(`/api/v1/content/chapters/navigation?contentId=${contentId}&direction=first`),
        fetch(`/api/v1/content/chapters/navigation?contentId=${contentId}&direction=last`)
    ];
    
    const [prevRes, nextRes, firstRes, lastRes] = await Promise.all(promises);
    
    return {
        previous: prevRes.ok ? await prevRes.json() : null,
        next: nextRes.ok ? await nextRes.json() : null,
        first: firstRes.ok ? await firstRes.json() : null,
        last: lastRes.ok ? await lastRes.json() : null
    };
}
```

### 章节目录服务
```javascript
// 获取章节目录和统计信息
async function getChapterCatalog(contentId) {
    const [chaptersRes, statsRes] = await Promise.all([
        fetch(`/api/v1/content/chapters/content/${contentId}/published`),
        fetch(`/api/v1/content/chapters/content/${contentId}/stats`)
    ]);
    
    const chapters = await chaptersRes.json();
    const stats = await statsRes.json();
    
    return {
        chapters: chapters.data || [],
        stats: stats.data || {}
    };
}
```

### 章节搜索
```javascript
// 搜索章节
async function searchChapters(keyword, contentId = null, page = 1, size = 20) {
    let url = `/api/v1/content/chapters/search?keyword=${encodeURIComponent(keyword)}&currentPage=${page}&pageSize=${size}`;
    
    if (contentId) {
        url += `&contentId=${contentId}`;
    }
    
    const response = await fetch(url);
    return response.json();
}
```

### 批量操作
```javascript
// 批量发布章节
async function batchPublishChapters(chapterIds) {
    const response = await fetch('/api/v1/content/chapters/batch/status', {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            ids: chapterIds,
            status: 'PUBLISHED'
        })
    });
    
    return response.json();
}
```

## 🔧 性能优化建议

1. **缓存策略**:
   - 章节列表: 使用 ETag 和 Last-Modified
   - 章节统计: 缓存5分钟
   - 导航信息: 缓存15分钟

2. **分页优化**:
   - 建议页面大小: 10-50
   - 使用游标分页替代offset分页（大数据量时）

3. **查询优化**:
   - 使用万能查询减少API调用次数
   - 并行请求多个无依赖的接口
   - 合理使用条件参数减少结果集

4. **请求优化**:
   ```javascript
   // 推荐：并行获取章节和统计信息
   Promise.all([
       getChapterList(contentId),
       getChapterStats(contentId)
   ]);
   
   // 避免：串行调用
   const chapters = await getChapterList(contentId);
   const stats = await getChapterStats(contentId);
   ```

## 🚀 极简设计优势

1. **接口精简**: 从24个接口缩减到11个，开发效率提升55%
2. **万能查询**: 1个查询接口替代14个具体查询接口
3. **统一导航**: 1个导航接口替代4个导航接口
4. **批量优化**: 支持批量操作，减少网络开销
5. **便民接口**: 保留3个高频便民接口，平衡灵活性和易用性

## 🔗 相关文档

- [ContentChapterFacadeService API 文档](../facade/content-chapter-facade-service-api.md)
- [Content Controller API 文档](./content-controller-api.md)
- [Content Purchase Controller API 文档](./content-purchase-controller-api.md)
- [Content Payment Controller API 文档](./content-payment-controller-api.md)

---

**联系信息**:  
- 控制器: ContentChapterController  
- 版本: 2.0.0 (极简版)  
- 基础路径: `/api/v1/content/chapters`  
- 维护: GIG团队  
- 更新: 2024-01-31