# Collide 搜索模块 API 文档

## 📖 概述

Collide 搜索模块提供基于数据库的全文搜索功能，支持用户、内容、评论的综合搜索。相比于 Elasticsearch，基于数据库的搜索更加轻量级，部署简单，适合中小型项目。

## 🚀 功能特性

### ✨ 已实现功能

- **多类型搜索**: 支持用户、内容、评论的搜索
- **综合搜索**: 一次搜索多种类型的结果
- **智能排序**: 相关度、时间、热度多种排序方式
- **搜索建议**: 关键词自动补全和搜索建议
- **高亮显示**: 搜索结果中关键词高亮
- **搜索统计**: 热门关键词和搜索行为记录
- **灵活过滤**: 按内容类型、时间范围、点赞数等过滤

### 🔧 技术实现

- **数据库搜索**: 基于 MySQL LIKE 查询和全文索引
- **相关度计算**: 按字段匹配权重计算相关度得分
- **性能优化**: 合理的索引设计和分页查询
- **搜索记录**: 记录搜索行为用于分析和建议生成

## 📝 API 接口

### 基础信息

- **Base URL**: `/api/v1/search`
- **认证方式**: 无需认证（搜索建议和热门关键词可匿名访问）
- **数据格式**: JSON

### 1. 综合搜索

#### GET `/api/v1/search`

```bash
curl -X GET "http://localhost:8080/api/v1/search?keyword=Java编程&searchType=ALL&pageNum=1&pageSize=10"
```

**请求参数**:
- `keyword` (必填): 搜索关键词
- `searchType` (可选): 搜索类型，默认为 `ALL`
  - `ALL`: 综合搜索
  - `USER`: 用户搜索
  - `CONTENT`: 内容搜索
  - `COMMENT`: 评论搜索
- `contentType` (可选): 内容类型过滤
  - `NOVEL`: 小说
  - `COMIC`: 漫画
  - `SHORT_VIDEO`: 短视频
  - `LONG_VIDEO`: 长视频
- `sortBy` (可选): 排序方式，默认为 `RELEVANCE`
  - `RELEVANCE`: 相关度
  - `TIME`: 时间
  - `POPULARITY`: 热度
- `pageNum` (可选): 页码，默认为 1
- `pageSize` (可选): 每页大小，默认为 10
- `highlight` (可选): 是否高亮，默认为 true
- `timeRange` (可选): 时间范围（天数），0表示不限制
- `minLikeCount` (可选): 最小点赞数过滤
- `onlyPublished` (可选): 是否只搜索已发布内容，默认为 true

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "keyword": "Java编程",
    "searchType": "ALL",
    "totalCount": 156,
    "searchTime": 45,
    "pageNum": 1,
    "pageSize": 10,
    "totalPages": 16,
    "hasNext": true,
    "results": [
      {
        "id": 1001,
        "resultType": "CONTENT",
        "title": "<mark>Java编程</mark>入门指南",
        "description": "详细介绍<mark>Java编程</mark>的基础知识和最佳实践",
        "contentPreview": "本文将带你从零开始学习<mark>Java编程</mark>...",
        "coverUrl": "https://example.com/cover.jpg",
        "author": {
          "userId": 12345,
          "username": "javacoder",
          "nickname": "Java大师",
          "avatar": "https://example.com/avatar.jpg",
          "verified": true
        },
        "statistics": {
          "viewCount": 5420,
          "likeCount": 234,
          "commentCount": 56,
          "favoriteCount": 123,
          "shareCount": 34
        },
        "tags": ["Java", "编程", "入门"],
        "contentType": "NOVEL",
        "createTime": "2024-01-15T10:30:00",
        "publishTime": "2024-01-15T14:00:00",
        "relevanceScore": 9.5
      }
    ],
    "statistics": {
      "userCount": 23,
      "contentCount": 128,
      "commentCount": 5
    },
    "suggestions": ["Java编程教程", "Java开发", "Java框架"],
    "relatedSearches": ["Spring Boot", "数据库设计", "算法"]
  }
}
```

### 2. 高级搜索（POST）

#### POST `/api/v1/search`

```bash
curl -X POST "http://localhost:8080/api/v1/search" \
  -H "Content-Type: application/json" \
  -d '{
    "keyword": "Spring Boot",
    "searchType": "CONTENT",
    "contentType": "NOVEL",
    "sortBy": "POPULARITY",
    "pageNum": 1,
    "pageSize": 20,
    "timeRange": 30,
    "minLikeCount": 10,
    "highlight": true
  }'
```

### 3. 搜索建议

#### GET `/api/v1/search/suggestions`

```bash
curl -X GET "http://localhost:8080/api/v1/search/suggestions?keyword=Java&suggestionType=KEYWORD&limit=10"
```

**请求参数**:
- `keyword` (必填): 搜索关键词前缀
- `suggestionType` (可选): 建议类型，默认为 `KEYWORD`
  - `KEYWORD`: 关键词建议
  - `USER`: 用户建议
  - `TAG`: 标签建议
- `limit` (可选): 建议数量，默认为 10

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "keyword": "Java",
    "suggestionType": "KEYWORD",
    "suggestions": [
      {
        "text": "Java编程",
        "type": "KEYWORD",
        "searchCount": 1250,
        "relevanceScore": 9.2,
        "highlightText": "<mark>Java</mark>编程"
      },
      {
        "text": "JavaScript",
        "type": "KEYWORD",
        "searchCount": 890,
        "relevanceScore": 8.5,
        "highlightText": "<mark>Java</mark>Script"
      }
    ],
    "hotKeywords": ["Java编程", "Spring Boot", "数据库", "前端开发", "算法"]
  }
}
```

### 4. 热门关键词

#### GET `/api/v1/search/hot-keywords`

```bash
curl -X GET "http://localhost:8080/api/v1/search/hot-keywords?limit=10"
```

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": ["Java编程", "Spring Boot", "微服务", "数据库设计", "前端开发"]
}
```

### 5. 搜索统计

#### GET `/api/v1/search/stats`

```bash
curl -X GET "http://localhost:8080/api/v1/search/stats"
```

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "hotKeywords": ["Java编程", "Spring Boot", "数据库", "算法", "前端"],
    "searchTips": [
      "使用引号搜索精确短语，如：\"Spring Boot\"",
      "使用空格分隔多个关键词",
      "可以按内容类型筛选搜索结果",
      "支持按时间范围和热度排序"
    ],
    "supportedTypes": ["ALL", "USER", "CONTENT", "COMMENT"],
    "supportedSorts": ["RELEVANCE", "TIME", "POPULARITY"]
  }
}
```

### 6. 记录搜索行为

#### POST `/api/v1/search/record`

```bash
curl -X POST "http://localhost:8080/api/v1/search/record" \
  -d "keyword=Java编程&userId=12345&resultCount=156"
```

## 🎨 搜索结果类型

### 用户搜索结果 (USER)

- **匹配字段**: 用户名、昵称、个人简介
- **排序权重**: 用户名 > 昵称 > 简介
- **显示信息**: 头像、昵称、用户名、简介、位置

### 内容搜索结果 (CONTENT)

- **匹配字段**: 标题、描述、标签
- **排序权重**: 标题 > 描述 > 标签
- **显示信息**: 封面、标题、描述、作者、统计数据、标签

### 评论搜索结果 (COMMENT)

- **匹配字段**: 评论内容
- **显示信息**: 评论内容、作者、点赞数、回复数、评论时间

## 🔍 搜索优化

### 相关度算法

1. **字段权重**: 不同字段匹配的权重不同
   - 标题/用户名匹配: 权重 10
   - 描述/昵称匹配: 权重 8
   - 标签/简介匹配: 权重 6
   
2. **热度加权**: 结合点赞数、浏览数等热度指标

3. **时间衰减**: 较新的内容获得额外加分

### 性能优化

1. **索引优化**: 在搜索字段上创建合适的索引
2. **分页查询**: 避免全表扫描
3. **缓存策略**: 热门搜索结果缓存
4. **异步记录**: 搜索行为异步记录，不影响搜索性能

## 🛠️ 使用示例

### JavaScript 前端调用

```javascript
// 基础搜索
async function search(keyword, searchType = 'ALL') {
  const response = await fetch(`/api/v1/search?keyword=${encodeURIComponent(keyword)}&searchType=${searchType}`);
  const result = await response.json();
  return result.data;
}

// 搜索建议
async function getSuggestions(keyword) {
  const response = await fetch(`/api/v1/search/suggestions?keyword=${encodeURIComponent(keyword)}`);
  const result = await response.json();
  return result.data.suggestions;
}

// 热门关键词
async function getHotKeywords() {
  const response = await fetch('/api/v1/search/hot-keywords');
  const result = await response.json();
  return result.data;
}
```

### Vue.js 搜索组件示例

```vue
<template>
  <div class="search-container">
    <div class="search-box">
      <input
        v-model="keyword"
        @input="onKeywordChange"
        @keyup.enter="search"
        placeholder="搜索用户、内容、评论..."
        class="search-input"
      />
      <button @click="search" class="search-btn">搜索</button>
    </div>
    
    <!-- 搜索建议 -->
    <div v-if="suggestions.length > 0" class="suggestions">
      <div
        v-for="suggestion in suggestions"
        :key="suggestion.text"
        @click="selectSuggestion(suggestion)"
        class="suggestion-item"
        v-html="suggestion.highlightText"
      ></div>
    </div>
    
    <!-- 搜索结果 -->
    <div v-if="searchResults.length > 0" class="search-results">
      <div
        v-for="result in searchResults"
        :key="result.id"
        class="result-item"
      >
        <h3 v-html="result.title"></h3>
        <p v-html="result.description"></p>
        <div class="result-meta">
          <span>{{ result.author.nickname }}</span>
          <span>{{ formatDate(result.createTime) }}</span>
          <span>👍 {{ result.statistics?.likeCount || 0 }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      keyword: '',
      suggestions: [],
      searchResults: [],
      searchType: 'ALL'
    };
  },
  methods: {
    async onKeywordChange() {
      if (this.keyword.length >= 2) {
        const data = await this.getSuggestions(this.keyword);
        this.suggestions = data.suggestions;
      } else {
        this.suggestions = [];
      }
    },
    
    async search() {
      if (!this.keyword.trim()) return;
      
      const data = await this.searchAPI(this.keyword, this.searchType);
      this.searchResults = data.results;
      this.suggestions = [];
    },
    
    selectSuggestion(suggestion) {
      this.keyword = suggestion.text;
      this.search();
    },
    
    async searchAPI(keyword, searchType) {
      const response = await fetch(`/api/v1/search?keyword=${encodeURIComponent(keyword)}&searchType=${searchType}`);
      const result = await response.json();
      return result.data;
    },
    
    async getSuggestions(keyword) {
      const response = await fetch(`/api/v1/search/suggestions?keyword=${encodeURIComponent(keyword)}`);
      const result = await response.json();
      return result.data;
    },
    
    formatDate(dateString) {
      return new Date(dateString).toLocaleDateString();
    }
  }
};
</script>
```

## 🔮 扩展功能

### 未来计划

1. **全文索引**: 升级到 MySQL 8.0 的全文索引功能
2. **同义词扩展**: 支持同义词搜索
3. **搜索分析**: 更详细的搜索行为分析
4. **个性化推荐**: 基于搜索历史的个性化结果
5. **Elasticsearch 集成**: 可选的 Elasticsearch 支持

### 配置优化

```sql
-- 创建全文索引（MySQL 5.7+）
ALTER TABLE t_content ADD FULLTEXT(title, description);
ALTER TABLE t_user ADD FULLTEXT(username, nickname, bio);
ALTER TABLE t_comment ADD FULLTEXT(content);

-- 优化搜索性能的索引
CREATE INDEX idx_content_search ON t_content(status, review_status, created_time, like_count);
CREATE INDEX idx_user_search ON t_user(status, deleted, created_time);
CREATE INDEX idx_comment_search ON t_comment(status, is_deleted, create_time, like_count);
```

---

**🎉 现在就访问 [http://localhost:8080/api/v1/search?keyword=Java](http://localhost:8080/api/v1/search?keyword=Java) 开始体验搜索功能吧！** 