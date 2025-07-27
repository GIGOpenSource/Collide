# Collide 搜索 API 文档

## 概述

Collide 搜索服务提供高效的全文搜索功能，支持用户、内容、评论的综合搜索。基于完全去连表化设计，所有搜索操作都基于预构建的搜索索引表，确保高性能和可扩展性。

### 技术特性

- **完全去连表化设计**：所有搜索查询都基于单表操作，无JOIN查询
- **全文搜索支持**：基于MySQL FULLTEXT索引的高效全文搜索
- **智能相关度评分**：多维度相关度计算算法
- **实时搜索建议**：基于用户行为的智能搜索建议
- **分布式缓存**：基于Redis的搜索结果缓存
- **幂等性保护**：搜索记录的分布式锁保护机制

## 基础信息

- **服务名称**：collide-search
- **端口**：9503 (开发环境) / 8080 (生产环境)
- **版本**：v1.0.0
- **API前缀**：`/api/v1/search`

## 认证方式

所有API都需要在请求头中包含以下认证信息：

```http
Authorization: Bearer <access_token>
X-App-Id: <application_id>
X-User-Id: <user_id> (可选，用于个性化搜索)
```

## 数据模型

### SearchRequest - 搜索请求

```json
{
  "keyword": "string, required",           // 搜索关键词，1-255字符
  "searchType": "string, optional",        // 搜索类型：ALL|USER|CONTENT|COMMENT，默认ALL
  "contentType": "string, optional",       // 内容类型过滤：NOVEL|COMIC|SHORT_VIDEO|LONG_VIDEO|ARTICLE|AUDIO
  "userId": "long, optional",              // 用户ID，用于个性化搜索
  "pageNum": "integer, optional",          // 页码，默认1
  "pageSize": "integer, optional",         // 每页大小，默认20，最大100
  "sortType": "string, optional",          // 排序类型：RELEVANCE|TIME|POPULARITY，默认RELEVANCE
  "filters": "object, optional"            // 扩展过滤条件
}
```

### SearchResponse - 搜索响应

```json
{
  "keyword": "string",                     // 搜索关键词
  "searchType": "string",                  // 搜索类型
  "results": [                             // 搜索结果列表
    {
      "id": "long",                        // 结果ID
      "title": "string",                   // 标题
      "description": "string",             // 描述
      "type": "string",                    // 结果类型：USER|CONTENT|COMMENT
      "url": "string",                     // 详情页URL
      "imageUrl": "string",                // 图片URL
      "authorId": "long",                  // 作者ID
      "authorName": "string",              // 作者名称
      "authorAvatar": "string",            // 作者头像
      "viewCount": "long",                 // 查看数
      "likeCount": "long",                 // 点赞数
      "commentCount": "long",              // 评论数
      "relevanceScore": "double",          // 相关度评分
      "publishTime": "datetime",           // 发布时间
      "extraInfo": "string"                // 额外信息
    }
  ],
  "totalCount": "long",                    // 总结果数
  "searchTime": "long",                    // 搜索耗时（毫秒）
  "pageNum": "integer",                    // 当前页码
  "pageSize": "integer",                   // 每页大小
  "totalPages": "integer",                 // 总页数
  "hasNext": "boolean"                     // 是否有下一页
}
```

### SearchSuggestionRequest - 搜索建议请求

```json
{
  "keyword": "string, optional",           // 关键词前缀，可为空（获取热门关键词）
  "limit": "integer, optional",            // 建议数量限制，默认10，最大50
  "suggestionTypes": ["string"],           // 建议类型：KEYWORD|USER|TAG|CONTENT
  "userId": "long, optional"               // 用户ID，用于个性化建议
}
```

### SearchSuggestionResponse - 搜索建议响应

```json
{
  "keyword": "string",                     // 输入关键词
  "suggestions": [                         // 建议列表
    {
      "text": "string",                     // 建议文本
      "type": "string",                    // 建议类型：KEYWORD|USER|TAG|CONTENT
      "targetId": "long",                  // 关联目标ID（可选）
      "avatarUrl": "string",               // 头像URL（可选）
      "searchCount": "long",               // 搜索次数
      "relevanceScore": "double",          // 相关度评分
      "highlightText": "string",           // 高亮文本（HTML格式）
      "extraInfo": "string"                // 额外信息
    }
  ],
  "totalCount": "long"                     // 建议总数
}
```

## API 接口

### 1. 综合搜索

**接口描述**：执行综合搜索，支持用户、内容、评论的统一搜索。

```http
POST /api/v1/search/comprehensive
```

**请求参数**：

| 参数名 | 类型 | 必需 | 描述 | 示例 |
|--------|------|------|------|------|
| keyword | string | 是 | 搜索关键词 | "Java 微服务" |
| searchType | string | 否 | 搜索类型 | "ALL" |
| contentType | string | 否 | 内容类型过滤 | "ARTICLE" |
| userId | long | 否 | 用户ID | 123456 |
| pageNum | integer | 否 | 页码 | 1 |
| pageSize | integer | 否 | 每页大小 | 20 |
| sortType | string | 否 | 排序类型 | "RELEVANCE" |

**请求示例**：

```json
{
  "keyword": "Java 微服务架构",
  "searchType": "ALL",
  "contentType": "ARTICLE",
  "userId": 123456,
  "pageNum": 1,
  "pageSize": 20,
  "sortType": "RELEVANCE"
}
```

**响应示例**：

```json
{
  "code": 200,
  "message": "搜索成功",
  "data": {
    "keyword": "Java 微服务架构",
    "searchType": "ALL",
    "results": [
      {
        "id": 1001,
        "title": "深入理解Java微服务架构设计",
        "description": "本文详细介绍了Java微服务架构的设计原则和最佳实践...",
        "type": "CONTENT",
        "url": "/content/1001",
        "imageUrl": "https://example.com/covers/1001.jpg",
        "authorId": 2001,
        "authorName": "架构师张三",
        "authorAvatar": "https://example.com/avatars/2001.jpg",
        "viewCount": 15230,
        "likeCount": 892,
        "commentCount": 156,
        "relevanceScore": 9.8,
        "publishTime": "2024-01-15T10:30:00Z",
        "extraInfo": "文章 • 技术 • 作者: 架构师张三 [博主] ✓ • 热门 • 推荐 • 浏览: 15230 • 点赞: 892"
      },
      {
        "id": 3001,
        "title": "Java微服务实战经验分享",
        "description": "我在项目中使用Java微服务架构的一些心得体会...",
        "type": "USER",
        "url": "/user/3001",
        "imageUrl": "https://example.com/avatars/3001.jpg",
        "authorId": 3001,
        "authorName": "实战专家李四",
        "authorAvatar": "https://example.com/avatars/3001.jpg",
        "viewCount": 8650,
        "likeCount": 445,
        "commentCount": 89,
        "relevanceScore": 8.9,
        "publishTime": "2024-01-10T14:20:00Z",
        "extraInfo": "认证博主 • 已认证 • 粉丝: 8650 • 内容: 89 • 北京"
      }
    ],
    "totalCount": 1247,
    "searchTime": 125,
    "pageNum": 1,
    "pageSize": 20,
    "totalPages": 63,
    "hasNext": true
  },
  "timestamp": "2024-01-20T10:30:45Z"
}
```

### 2. 搜索建议

**接口描述**：获取搜索关键词建议，包括关键词、用户、标签等建议。

```http
POST /api/v1/search/suggestions
```

**请求参数**：

| 参数名 | 类型 | 必需 | 描述 | 示例 |
|--------|------|------|------|------|
| keyword | string | 否 | 关键词前缀 | "Java" |
| limit | integer | 否 | 建议数量限制 | 10 |
| suggestionTypes | array | 否 | 建议类型数组 | ["KEYWORD", "USER"] |
| userId | long | 否 | 用户ID | 123456 |

**请求示例**：

```json
{
  "keyword": "Java",
  "limit": 10,
  "suggestionTypes": ["KEYWORD", "USER", "TAG"],
  "userId": 123456
}
```

**响应示例**：

```json
{
  "code": 200,
  "message": "获取建议成功",
  "data": {
    "keyword": "Java",
    "suggestions": [
      {
        "text": "Java 微服务",
        "type": "KEYWORD",
        "searchCount": 15230,
        "relevanceScore": 9.8,
        "highlightText": "<mark>Java</mark> 微服务",
        "extraInfo": "搜索: 15230 • 用户: 2456 • 🔥 热门"
      },
      {
        "text": "Java架构师 (@java_master)",
        "type": "USER",
        "targetId": 2001,
        "avatarUrl": "https://example.com/avatars/2001.jpg",
        "searchCount": 8650,
        "relevanceScore": 8.9,
        "highlightText": "<mark>Java</mark>架构师 (@java_master)",
        "extraInfo": "粉丝: 8650"
      },
      {
        "text": "Java开发",
        "type": "TAG",
        "searchCount": 12450,
        "relevanceScore": 8.5,
        "highlightText": "<mark>Java</mark>开发",
        "extraInfo": "使用次数: 12450"
      }
    ],
    "totalCount": 3
  },
  "timestamp": "2024-01-20T10:30:45Z"
}
```

### 3. 热门搜索

**接口描述**：获取热门搜索关键词列表。

```http
GET /api/v1/search/hot-keywords
```

**请求参数**：

| 参数名 | 类型 | 必需 | 描述 | 默认值 |
|--------|------|------|------|--------|
| limit | integer | 否 | 关键词数量限制 | 10 |

**请求示例**：

```http
GET /api/v1/search/hot-keywords?limit=15
```

**响应示例**：

```json
{
  "code": 200,
  "message": "获取热门关键词成功",
  "data": {
    "keyword": "",
    "suggestions": [
      {
        "text": "Java 微服务",
        "type": "HOT_KEYWORD",
        "searchCount": 15230,
        "relevanceScore": 10.0,
        "extraInfo": "搜索: 15230 • 用户: 2456 • 🔥 热门"
      },
      {
        "text": "Spring Boot",
        "type": "HOT_KEYWORD", 
        "searchCount": 12890,
        "relevanceScore": 9.5,
        "extraInfo": "搜索: 12890 • 用户: 2105 • 📈 上升"
      },
      {
        "text": "React 开发",
        "type": "HOT_KEYWORD",
        "searchCount": 9876,
        "relevanceScore": 8.8,
        "extraInfo": "搜索: 9876 • 用户: 1678"
      }
    ],
    "totalCount": 15
  },
  "timestamp": "2024-01-20T10:30:45Z"
}
```

## 错误码

### 通用错误码

| 错误码 | HTTP状态码 | 描述 | 解决方案 |
|--------|------------|------|----------|
| 400001 | 400 | 请求参数无效 | 检查请求参数格式和必需字段 |
| 400002 | 400 | 搜索关键词为空 | 提供有效的搜索关键词 |
| 400003 | 400 | 搜索关键词过长 | 关键词长度不超过255字符 |
| 400004 | 400 | 页码参数无效 | 页码必须大于0 |
| 400005 | 400 | 页面大小超限 | 每页大小不超过100 |
| 401001 | 401 | 认证失败 | 检查访问令牌的有效性 |
| 403001 | 403 | 访问权限不足 | 检查用户权限或联系管理员 |
| 429001 | 429 | 请求频率超限 | 降低请求频率，遵守限流规则 |
| 500001 | 500 | 搜索服务异常 | 联系技术支持 |
| 500002 | 500 | 缓存服务异常 | 稍后重试或联系技术支持 |

### 搜索特定错误码

| 错误码 | HTTP状态码 | 描述 | 解决方案 |
|--------|------------|------|----------|
| 404001 | 404 | 搜索结果为空 | 尝试其他关键词或调整搜索条件 |
| 422001 | 422 | 搜索类型不支持 | 使用支持的搜索类型：ALL|USER|CONTENT|COMMENT |
| 422002 | 422 | 内容类型不支持 | 使用支持的内容类型 |
| 422003 | 422 | 排序类型不支持 | 使用支持的排序类型：RELEVANCE|TIME|POPULARITY |

## 性能指标

### 响应时间

| 操作类型 | 平均响应时间 | P95响应时间 | P99响应时间 |
|----------|-------------|-------------|-------------|
| 综合搜索 | 50ms | 120ms | 200ms |
| 搜索建议 | 20ms | 45ms | 80ms |
| 热门关键词 | 15ms | 30ms | 50ms |

### 缓存策略

| 数据类型 | 缓存时间 | 缓存键格式 | 备注 |
|----------|----------|------------|------|
| 搜索结果 | 10分钟 | `search:results:{keyword}:{type}:{page}` | 根据搜索参数缓存 |
| 搜索建议 | 30分钟 | `search:suggestions:{keyword}:{limit}` | 基于关键词前缀缓存 |
| 热门关键词 | 1小时 | `search:hot:keywords:{limit}` | 全局热门关键词缓存 |

## 限流规则

### API限流

| 用户类型 | 每分钟请求限制 | 每小时请求限制 | 每日请求限制 |
|----------|----------------|----------------|--------------|
| 匿名用户 | 30 | 500 | 5000 |
| 普通用户 | 60 | 1000 | 10000 |
| VIP用户 | 120 | 2000 | 20000 |
| 企业用户 | 300 | 5000 | 50000 |

## SDK 和示例

### Java SDK 示例

```java
@Service
public class SearchService {
    
    @Reference(version = "1.0.0")
    private SearchFacadeService searchFacadeService;
    
    public SearchResponse search(String keyword, String searchType) {
        SearchRequest request = SearchRequest.builder()
            .keyword(keyword)
            .searchType(searchType)
            .pageNum(1)
            .pageSize(20)
            .sortType("RELEVANCE")
            .build();
            
        return searchFacadeService.search(request);
    }
}
```

### JavaScript SDK 示例

```javascript
import axios from 'axios';

class SearchAPI {
    constructor(baseURL, token) {
        this.client = axios.create({
            baseURL: baseURL,
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            }
        });
    }
    
    async search(keyword, options = {}) {
        const request = {
            keyword,
            searchType: options.searchType || 'ALL',
            pageNum: options.pageNum || 1,
            pageSize: options.pageSize || 20,
            sortType: options.sortType || 'RELEVANCE'
        };
        
        const response = await this.client.post('/api/v1/search/comprehensive', request);
        return response.data;
    }
    
    async getSuggestions(keyword, limit = 10) {
        const request = { keyword, limit };
        const response = await this.client.post('/api/v1/search/suggestions', request);
        return response.data;
    }
    
    async getHotKeywords(limit = 10) {
        const response = await this.client.get(`/api/v1/search/hot-keywords?limit=${limit}`);
        return response.data;
    }
}
```

## 最佳实践

### 1. 搜索关键词优化

- **关键词预处理**：去除特殊字符，统一大小写
- **分词策略**：支持中英文混合搜索
- **同义词扩展**：自动扩展相关关键词
- **拼写纠错**：提供拼写错误纠正建议

### 2. 搜索结果优化

- **相关度调优**：根据用户反馈调整相关度算法
- **个性化推荐**：基于用户历史行为个性化搜索结果
- **多样性保证**：确保搜索结果的多样性
- **实时性平衡**：平衡搜索结果的实时性和准确性

### 3. 性能优化

- **缓存策略**：合理使用多级缓存
- **索引优化**：定期更新和优化搜索索引
- **分片策略**：大数据量下的水平分片
- **异步处理**：搜索行为记录的异步处理

### 4. 监控和告警

- **性能监控**：监控API响应时间和成功率
- **业务监控**：监控搜索量、热门关键词变化
- **异常告警**：搜索服务异常的及时告警
- **容量规划**：基于监控数据进行容量规划

## 更新日志

### v1.0.0 (2024-01-20)

- ✅ 实现基础综合搜索功能
- ✅ 支持用户、内容、评论搜索
- ✅ 实现搜索建议和热门关键词
- ✅ 完全去连表化架构设计
- ✅ 集成分布式缓存和限流
- ✅ 实现幂等性保护机制

### 计划中的功能

- 🔄 高级搜索过滤器
- 🔄 搜索结果聚合统计
- 🔄 多语言搜索支持
- 🔄 搜索结果导出功能
- 🔄 搜索分析报表

---

## 联系方式

- **技术支持**：tech-support@collide.com
- **API文档**：https://api.collide.com/docs/search
- **状态页面**：https://status.collide.com
- **GitHub**：https://github.com/collide-platform/search-service 