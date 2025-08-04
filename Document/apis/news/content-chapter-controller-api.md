# Content Chapter Controller REST API 文档

**控制器**: ContentChapterController  
**版本**: 2.0.0 (内容付费版)  
**基础路径**: `/api/content/chapters`  
**接口数量**: 24个  
**更新时间**: 2024-01-31  

## 🚀 概述

内容章节控制器提供章节查询、统计和管理的REST API接口。专为多章节内容（如小说、漫画）设计，支持章节导航、字数统计、状态管理等功能。

**主要功能**:
- **章节查询**: 按内容、状态、字数等条件查询章节
- **章节导航**: 上一章、下一章、第一章、最后章导航
- **搜索功能**: 按标题关键词搜索章节
- **统计功能**: 章节数量、字数统计
- **管理功能**: 批量状态更新、章节重排序

## 📋 接口分类

| 分类 | 接口数量 | 功能描述 |
|------|----------|----------|
| **基础查询功能** | 14个 | 章节列表、导航、搜索、筛选 |
| **统计功能** | 5个 | 章节数量、字数、统计信息 |
| **管理功能** | 3个 | 批量更新、删除、重排序 |
| **高级查询** | 2个 | 字数范围、最新章节 |

---

## 📚 1. 基础查询功能 (14个接口)

### 1.1 获取内容的章节列表

**接口**: `GET /api/content/chapters/content/{contentId}`

**描述**: 根据内容ID查询所有章节列表（按章节号排序）

**路径参数**:
- `contentId` (Long): 内容ID

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 67890,
      "contentId": 12345,
      "chapterNum": 1,
      "title": "第一章 初入江湖",
      "wordCount": 2500,
      "status": "PUBLISHED",
      "isFree": true,
      "createTime": "2024-01-01T10:00:00",
      "updateTime": "2024-01-01T10:00:00"
    },
    {
      "id": 67891,
      "contentId": 12345,
      "chapterNum": 2,
      "title": "第二章 奇遇连连",
      "wordCount": 3000,
      "status": "PUBLISHED",
      "isFree": false,
      "createTime": "2024-01-02T10:00:00",
      "updateTime": "2024-01-02T10:00:00"
    }
  ]
}
```

**错误处理**:
- `GET_CHAPTERS_FAILED`: 获取章节列表失败

### 1.2 获取内容的已发布章节

**接口**: `GET /api/content/chapters/content/{contentId}/published`

**描述**: 根据内容ID查询已发布的章节列表

**路径参数**:
- `contentId` (Long): 内容ID

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 67890,
      "contentId": 12345,
      "chapterNum": 1,
      "title": "第一章 初入江湖",
      "wordCount": 2500,
      "status": "PUBLISHED",
      "createTime": "2024-01-01T10:00:00"
    }
  ]
}
```

**错误处理**:
- `GET_PUBLISHED_CHAPTERS_FAILED`: 获取已发布章节列表失败

### 1.3 分页获取章节列表

**接口**: `GET /api/content/chapters/content/{contentId}/paged`

**描述**: 根据内容ID分页查询章节

**路径参数**:
- `contentId` (Long): 内容ID

**查询参数**:
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
        "title": "第一章 初入江湖",
        "wordCount": 2500,
        "status": "PUBLISHED"
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

**错误处理**:
- `GET_CHAPTERS_PAGED_FAILED`: 分页获取章节列表失败

### 1.4 获取指定章节

**接口**: `GET /api/content/chapters/content/{contentId}/chapter/{chapterNum}`

**描述**: 根据内容ID和章节号查询指定章节

**路径参数**:
- `contentId` (Long): 内容ID
- `chapterNum` (Integer): 章节号

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
    "content": "章节内容正文...",
    "wordCount": 2500,
    "status": "PUBLISHED",
    "isFree": true,
    "summary": "本章简介",
    "createTime": "2024-01-01T10:00:00",
    "updateTime": "2024-01-01T10:00:00"
  }
}
```

**错误处理**:
- `GET_CHAPTER_FAILED`: 获取章节详情失败
- `CHAPTER_NOT_FOUND`: 章节不存在

### 1.5 获取下一章节

**接口**: `GET /api/content/chapters/content/{contentId}/chapter/{currentChapterNum}/next`

**描述**: 根据当前章节号获取下一章节

**路径参数**:
- `contentId` (Long): 内容ID
- `currentChapterNum` (Integer): 当前章节号

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
    "status": "PUBLISHED"
  }
}
```

**错误处理**:
- `GET_NEXT_CHAPTER_FAILED`: 获取下一章节失败
- `NEXT_CHAPTER_NOT_FOUND`: 没有下一章节

### 1.6 获取上一章节

**接口**: `GET /api/content/chapters/content/{contentId}/chapter/{currentChapterNum}/previous`

**描述**: 根据当前章节号获取上一章节

**路径参数**:
- `contentId` (Long): 内容ID
- `currentChapterNum` (Integer): 当前章节号

**错误处理**:
- `GET_PREVIOUS_CHAPTER_FAILED`: 获取上一章节失败
- `PREVIOUS_CHAPTER_NOT_FOUND`: 没有上一章节

### 1.7 获取第一章节

**接口**: `GET /api/content/chapters/content/{contentId}/first`

**描述**: 获取内容的第一章节

**路径参数**:
- `contentId` (Long): 内容ID

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
    "status": "PUBLISHED"
  }
}
```

**错误处理**:
- `GET_FIRST_CHAPTER_FAILED`: 获取第一章节失败
- `FIRST_CHAPTER_NOT_FOUND`: 第一章节不存在

### 1.8 获取最后章节

**接口**: `GET /api/content/chapters/content/{contentId}/last`

**描述**: 获取内容的最后一章节

**路径参数**:
- `contentId` (Long): 内容ID

**错误处理**:
- `GET_LAST_CHAPTER_FAILED`: 获取最后章节失败
- `LAST_CHAPTER_NOT_FOUND`: 最后章节不存在

### 1.9 根据状态查询章节

**接口**: `GET /api/content/chapters/status/{status}`

**描述**: 根据章节状态查询章节列表

**路径参数**:
- `status` (String): 章节状态 (DRAFT/PUBLISHED/DELETED)

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 67890,
      "contentId": 12345,
      "chapterNum": 1,
      "title": "第一章 初入江湖",
      "status": "PUBLISHED",
      "createTime": "2024-01-01T10:00:00"
    }
  ]
}
```

**错误处理**:
- `GET_CHAPTERS_BY_STATUS_FAILED`: 根据状态查询章节失败

### 1.10 搜索章节

**接口**: `GET /api/content/chapters/search`

**描述**: 根据标题关键词搜索章节

**查询参数**:
- `titleKeyword` (String): 标题关键词
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
        "title": "第一章 初入江湖",
        "wordCount": 2500,
        "status": "PUBLISHED",
        "highlight": {
          "title": "第一章 初入<em>江湖</em>"
        }
      }
    ],
    "totalCount": 5,
    "currentPage": 1,
    "pageSize": 20
  }
}
```

**错误处理**:
- `SEARCH_CHAPTERS_FAILED`: 搜索章节失败

### 1.11 按字数范围查询章节

**接口**: `GET /api/content/chapters/content/{contentId}/word-count-range`

**描述**: 根据字数范围查询章节

**路径参数**:
- `contentId` (Long): 内容ID

**查询参数**:
- `minWordCount` (Integer, 可选): 最小字数
- `maxWordCount` (Integer, 可选): 最大字数

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 67890,
      "contentId": 12345,
      "chapterNum": 1,
      "title": "第一章 初入江湖",
      "wordCount": 2500,
      "status": "PUBLISHED"
    }
  ]
}
```

**错误处理**:
- `GET_CHAPTERS_BY_WORD_COUNT_FAILED`: 按字数范围查询章节失败

### 1.12 获取字数最多的章节

**接口**: `GET /api/content/chapters/content/{contentId}/max-word-count`

**描述**: 查询指定内容中字数最多的章节

**路径参数**:
- `contentId` (Long): 内容ID

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 67892,
    "contentId": 12345,
    "chapterNum": 10,
    "title": "第十章 巅峰对决",
    "wordCount": 5000,
    "status": "PUBLISHED"
  }
}
```

**错误处理**:
- `GET_MAX_WORD_COUNT_CHAPTER_FAILED`: 获取字数最多的章节失败
- `MAX_WORD_COUNT_CHAPTER_NOT_FOUND`: 字数最多的章节不存在

### 1.13 获取最新章节

**接口**: `GET /api/content/chapters/content/{contentId}/latest`

**描述**: 获取内容的最新更新章节

**路径参数**:
- `contentId` (Long): 内容ID

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 67900,
    "contentId": 12345,
    "chapterNum": 25,
    "title": "第二十五章 新的开始",
    "wordCount": 3200,
    "status": "PUBLISHED",
    "updateTime": "2024-01-31T14:30:00"
  }
}
```

**错误处理**:
- `GET_LATEST_CHAPTER_FAILED`: 获取最新章节失败
- `LATEST_CHAPTER_NOT_FOUND`: 最新章节不存在

### 1.14 获取最新更新的章节

**接口**: `GET /api/content/chapters/latest`

**描述**: 分页获取最新更新的章节列表

**查询参数**:
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
        "id": 67900,
        "contentId": 12345,
        "contentTitle": "我的玄幻小说",
        "chapterNum": 25,
        "title": "第二十五章 新的开始",
        "wordCount": 3200,
        "status": "PUBLISHED",
        "updateTime": "2024-01-31T14:30:00"
      }
    ],
    "totalCount": 100,
    "currentPage": 1,
    "pageSize": 20
  }
}
```

**错误处理**:
- `GET_LATEST_CHAPTERS_FAILED`: 获取最新更新的章节失败

---

## 📊 2. 统计功能 (5个接口)

### 2.1 统计章节总数

**接口**: `GET /api/content/chapters/content/{contentId}/count`

**描述**: 统计指定内容的章节总数

**路径参数**:
- `contentId` (Long): 内容ID

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": 25
}
```

**错误处理**:
- `COUNT_CHAPTERS_FAILED`: 统计章节总数失败

### 2.2 统计已发布章节数

**接口**: `GET /api/content/chapters/content/{contentId}/published-count`

**描述**: 统计指定内容的已发布章节数量

**路径参数**:
- `contentId` (Long): 内容ID

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": 20
}
```

**错误处理**:
- `COUNT_PUBLISHED_CHAPTERS_FAILED`: 统计已发布章节数失败

### 2.3 统计总字数

**接口**: `GET /api/content/chapters/content/{contentId}/total-words`

**描述**: 统计指定内容的总字数

**路径参数**:
- `contentId` (Long): 内容ID

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": 75000
}
```

**错误处理**:
- `COUNT_TOTAL_WORDS_FAILED`: 统计总字数失败

### 2.4 获取章节统计信息

**接口**: `GET /api/content/chapters/content/{contentId}/stats`

**描述**: 获取指定内容的章节统计信息

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

**错误处理**:
- `GET_CHAPTER_STATS_FAILED`: 获取章节统计信息失败

---

## ⚙️ 3. 管理功能 (3个接口)

### 3.1 批量更新章节状态

**接口**: `PUT /api/content/chapters/batch-status`

**描述**: 批量更新指定章节的状态

**查询参数**:
- `ids` (List<Long>): 章节ID列表
- `status` (String): 目标状态 (DRAFT/PUBLISHED/DELETED)

**请求示例**:
```
PUT /api/content/chapters/batch-status?ids=67890,67891,67892&status=PUBLISHED
```

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": true
}
```

**错误处理**:
- `BATCH_UPDATE_STATUS_FAILED`: 批量更新章节状态失败

### 3.2 删除内容的所有章节

**接口**: `DELETE /api/content/chapters/content/{contentId}/all`

**描述**: 删除指定内容的所有章节

**路径参数**:
- `contentId` (Long): 内容ID

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": true
}
```

**错误处理**:
- `DELETE_ALL_CHAPTERS_FAILED`: 删除内容的所有章节失败

### 3.3 重新排序章节号

**接口**: `PUT /api/content/chapters/content/{contentId}/reorder`

**描述**: 重新排序指定内容的章节号

**路径参数**:
- `contentId` (Long): 内容ID

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": true
}
```

**错误处理**:
- `REORDER_CHAPTER_NUMBERS_FAILED`: 重新排序章节号失败

---

## 🎯 数据模型

### ChapterResponse 章节响应对象
```json
{
  "id": "章节ID",
  "contentId": "内容ID",
  "contentTitle": "内容标题（仅在跨内容查询时返回）",
  "chapterNum": "章节号",
  "title": "章节标题",
  "content": "章节内容（仅在详情查询时返回）",
  "summary": "章节简介",
  "wordCount": "字数",
  "status": "状态（DRAFT/PUBLISHED/DELETED）",
  "isFree": "是否免费",
  "viewCount": "浏览量",
  "likeCount": "点赞数",
  "commentCount": "评论数",
  "createTime": "创建时间",
  "publishTime": "发布时间",
  "updateTime": "更新时间",
  "highlight": "搜索高亮信息（仅在搜索时返回）"
}
```

### ChapterStats 章节统计对象
```json
{
  "totalChapters": "总章节数",
  "publishedChapters": "已发布章节数",
  "draftChapters": "草稿章节数",
  "totalWords": "总字数",
  "publishedWords": "已发布字数",
  "avgWordsPerChapter": "平均每章字数",
  "maxWordCount": "最大章节字数",
  "minWordCount": "最小章节字数",
  "freeChapters": "免费章节数",
  "paidChapters": "付费章节数",
  "latestChapterNum": "最新章节号",
  "latestUpdateTime": "最新更新时间",
  "firstPublishTime": "首次发布时间"
}
```

## 🚨 错误代码

| 错误码 | 描述 | 解决方案 |
|--------|------|----------|
| GET_CHAPTERS_FAILED | 获取章节列表失败 | 检查内容ID是否正确 |
| GET_PUBLISHED_CHAPTERS_FAILED | 获取已发布章节列表失败 | 确认内容存在 |
| GET_CHAPTERS_PAGED_FAILED | 分页获取章节列表失败 | 检查分页参数 |
| GET_CHAPTER_FAILED | 获取章节详情失败 | 检查章节ID或章节号 |
| CHAPTER_NOT_FOUND | 章节不存在 | 确认章节存在 |
| GET_NEXT_CHAPTER_FAILED | 获取下一章节失败 | 检查章节号 |
| NEXT_CHAPTER_NOT_FOUND | 没有下一章节 | 已是最后一章 |
| GET_PREVIOUS_CHAPTER_FAILED | 获取上一章节失败 | 检查章节号 |
| PREVIOUS_CHAPTER_NOT_FOUND | 没有上一章节 | 已是第一章 |
| GET_FIRST_CHAPTER_FAILED | 获取第一章节失败 | 检查内容ID |
| FIRST_CHAPTER_NOT_FOUND | 第一章节不存在 | 内容没有章节 |
| GET_LAST_CHAPTER_FAILED | 获取最后章节失败 | 检查内容ID |
| LAST_CHAPTER_NOT_FOUND | 最后章节不存在 | 内容没有章节 |
| GET_CHAPTERS_BY_STATUS_FAILED | 根据状态查询章节失败 | 检查状态值 |
| SEARCH_CHAPTERS_FAILED | 搜索章节失败 | 检查搜索关键词 |
| GET_CHAPTERS_BY_WORD_COUNT_FAILED | 按字数范围查询章节失败 | 检查字数范围 |
| GET_MAX_WORD_COUNT_CHAPTER_FAILED | 获取字数最多的章节失败 | 检查内容ID |
| MAX_WORD_COUNT_CHAPTER_NOT_FOUND | 字数最多的章节不存在 | 内容没有章节 |
| GET_LATEST_CHAPTER_FAILED | 获取最新章节失败 | 检查内容ID |
| LATEST_CHAPTER_NOT_FOUND | 最新章节不存在 | 内容没有章节 |
| GET_LATEST_CHAPTERS_FAILED | 获取最新更新的章节失败 | 检查查询参数 |
| COUNT_CHAPTERS_FAILED | 统计章节总数失败 | 检查内容ID |
| COUNT_PUBLISHED_CHAPTERS_FAILED | 统计已发布章节数失败 | 检查内容ID |
| COUNT_TOTAL_WORDS_FAILED | 统计总字数失败 | 检查内容ID |
| GET_CHAPTER_STATS_FAILED | 获取章节统计信息失败 | 检查内容ID |
| BATCH_UPDATE_STATUS_FAILED | 批量更新章节状态失败 | 检查章节ID列表和状态 |
| DELETE_ALL_CHAPTERS_FAILED | 删除内容的所有章节失败 | 确认操作权限 |
| REORDER_CHAPTER_NUMBERS_FAILED | 重新排序章节号失败 | 确认内容存在 |

## 📈 使用场景

### 1. 阅读器导航
```javascript
// 获取章节内容和导航信息
const getChapterWithNavigation = async (contentId, chapterNum) => {
  // 获取当前章节
  const currentChapter = await fetch(
    `/api/content/chapters/content/${contentId}/chapter/${chapterNum}`
  );
  
  // 获取上一章
  const previousChapter = await fetch(
    `/api/content/chapters/content/${contentId}/chapter/${chapterNum}/previous`
  );
  
  // 获取下一章
  const nextChapter = await fetch(
    `/api/content/chapters/content/${contentId}/chapter/${chapterNum}/next`
  );
  
  return {
    current: await currentChapter.json(),
    previous: await previousChapter.json(),
    next: await nextChapter.json()
  };
};
```

### 2. 章节目录
```javascript
// 获取章节目录
const getChapterCatalog = async (contentId, page = 1) => {
  const response = await fetch(
    `/api/content/chapters/content/${contentId}/paged?currentPage=${page}&pageSize=50`
  );
  return response.json();
};
```

### 3. 统计仪表板
```javascript
// 获取内容统计信息
const getContentStats = async (contentId) => {
  const response = await fetch(
    `/api/content/chapters/content/${contentId}/stats`
  );
  return response.json();
};
```

## 🔧 性能优化建议

1. **缓存策略**: 章节列表和统计信息建议使用Redis缓存，TTL设置为10分钟
2. **分页优化**: 章节目录建议使用游标分页，提升大量章节的查询性能
3. **内容预加载**: 阅读器可以预加载下一章内容，提升用户体验
4. **搜索优化**: 章节标题搜索建议使用Elasticsearch全文索引
5. **统计优化**: 字数统计可以通过定时任务异步计算并缓存

## 🔗 相关文档

- [ContentController API 文档](./content-controller-api.md)
- [ContentChapterFacadeService 文档](./content-chapter-facade-service-api.md)
- [章节数据模型](../models/chapter-model.md)
- [阅读器设计](../design/reader-design.md)

---

**联系信息**:  
- 控制器: ContentChapterController  
- 版本: 2.0.0 (内容付费版)  
- 维护: GIG团队  
- 更新: 2024-01-31