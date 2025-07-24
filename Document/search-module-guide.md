# Collide 搜索模块完整指南

## 📖 模块概述

Collide 搜索模块基于 **数据库实现**，提供轻量级、高性能的搜索服务。相比Elasticsearch方案，具有部署简单、维护成本低、与业务数据库统一等优势。

## 🚀 核心特性

### ✨ 搜索功能
- **多类型搜索**: 支持用户、内容、评论的综合搜索
- **智能排序**: 相关度、时间、热度多维度排序
- **实时高亮**: 关键词高亮显示
- **灵活过滤**: 内容类型、时间范围、热度过滤
- **分页查询**: 高性能分页支持

### 🎯 建议功能
- **实时建议**: 输入时提供搜索建议
- **多类型建议**: 关键词、用户、标签建议
- **热门推荐**: 基于搜索频率的热门关键词
- **智能补全**: 前缀匹配和模糊搜索

### 📊 统计功能
- **搜索历史**: 用户搜索行为记录
- **热门统计**: 关键词热度排行
- **搜索分析**: 搜索趋势和用户偏好分析

## 🗃️ 数据库设计

### 搜索历史表 (`t_search_history`)
```sql
CREATE TABLE `t_search_history` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '搜索历史ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `keyword` VARCHAR(255) NOT NULL COMMENT '搜索关键词',
    `search_type` VARCHAR(20) NOT NULL DEFAULT 'ALL' COMMENT '搜索类型',
    `content_type` VARCHAR(20) COMMENT '内容类型过滤',
    `result_count` BIGINT NOT NULL DEFAULT 0 COMMENT '搜索结果数量',
    `search_time` BIGINT NOT NULL DEFAULT 0 COMMENT '搜索耗时（毫秒）',
    `ip_address` VARCHAR(45) COMMENT 'IP地址',
    `device_info` VARCHAR(500) COMMENT '设备信息',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_keyword` (`keyword`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

### 搜索统计表 (`t_search_statistics`)
```sql
CREATE TABLE `t_search_statistics` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '统计ID',
    `keyword` VARCHAR(255) NOT NULL COMMENT '搜索关键词',
    `search_count` BIGINT NOT NULL DEFAULT 1 COMMENT '搜索次数',
    `user_count` BIGINT NOT NULL DEFAULT 1 COMMENT '搜索用户数',
    `last_search_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '最后搜索时间',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_keyword` (`keyword`),
    KEY `idx_search_count` (`search_count`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

### 搜索建议表 (`t_search_suggestion`)
```sql
CREATE TABLE `t_search_suggestion` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '建议ID',
    `keyword` VARCHAR(255) NOT NULL COMMENT '建议关键词',
    `suggestion_type` VARCHAR(20) NOT NULL DEFAULT 'KEYWORD' COMMENT '建议类型',
    `search_count` BIGINT NOT NULL DEFAULT 0 COMMENT '搜索次数',
    `weight` DOUBLE NOT NULL DEFAULT 1.0 COMMENT '权重（用于排序）',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_keyword_type` (`keyword`, `suggestion_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

## 📡 API接口文档

### 1. 综合搜索

**GET** `/api/v1/search`

**参数：**
- `keyword` (string, required): 搜索关键词
- `searchType` (string, optional): 搜索类型 [ALL, USER, CONTENT, COMMENT]
- `contentType` (string, optional): 内容类型 [NOVEL, COMIC, SHORT_VIDEO, LONG_VIDEO]
- `sortBy` (string, optional): 排序方式 [RELEVANCE, TIME, POPULARITY]
- `pageNum` (int, optional): 页码，默认1
- `pageSize` (int, optional): 每页大小，默认10
- `highlight` (boolean, optional): 是否高亮，默认true
- `timeRange` (int, optional): 时间范围（天数），0表示不限制
- `minLikeCount` (int, optional): 最小点赞数过滤
- `onlyPublished` (boolean, optional): 是否只搜索已发布内容

**响应示例：**
```json
{
  "success": true,
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
        "description": "详细的<mark>Java编程</mark>学习路径...",
        "coverUrl": "https://example.com/cover.jpg",
        "author": {
          "userId": 101,
          "username": "javateacher",
          "nickname": "Java老师",
          "avatar": "https://example.com/avatar.jpg"
        },
        "statistics": {
          "viewCount": 1250,
          "likeCount": 89,
          "commentCount": 23,
          "favoriteCount": 45
        },
        "tags": ["Java", "编程", "入门"],
        "createTime": "2024-01-15T10:30:00",
        "relevanceScore": 95.8
      }
    ],
    "statistics": {
      "userCount": 12,
      "contentCount": 134,
      "commentCount": 10
    },
    "suggestions": ["Java编程入门", "Java编程进阶", "Java编程实战"],
    "relatedSearches": ["Spring Boot", "数据结构", "算法"]
  }
}
```

### 2. 高级搜索

**POST** `/api/v1/search`

**请求体：**
```json
{
  "keyword": "Java编程",
  "searchType": "CONTENT",
  "contentType": "NOVEL",
  "sortBy": "POPULARITY",
  "pageNum": 1,
  "pageSize": 20,
  "highlight": true,
  "timeRange": 30,
  "minLikeCount": 10,
  "onlyPublished": true
}
```

### 3. 搜索建议

**GET** `/api/v1/search/suggestions`

**参数：**
- `keyword` (string, required): 关键词前缀
- `suggestionType` (string, optional): 建议类型 [KEYWORD, USER, TAG]
- `limit` (int, optional): 建议数量，默认10

**响应示例：**
```json
{
  "success": true,
  "data": {
    "keyword": "Java",
    "suggestionType": "KEYWORD",
    "suggestions": [
      {
        "text": "Java编程",
        "type": "KEYWORD",
        "searchCount": 1250,
        "relevanceScore": 95.8,
        "highlightText": "<mark>Java</mark>编程"
      },
      {
        "text": "Java Spring Boot",
        "type": "KEYWORD",
        "searchCount": 890,
        "relevanceScore": 92.3,
        "highlightText": "<mark>Java</mark> Spring Boot"
      }
    ],
    "hotKeywords": ["Java编程", "Spring Boot", "微服务", "数据库", "前端开发"]
  }
}
```

### 4. 热门搜索关键词

**GET** `/api/v1/search/hot-keywords`

**参数：**
- `limit` (int, optional): 数量限制，默认10

**响应示例：**
```json
{
  "success": true,
  "data": [
    "Java编程",
    "Spring Boot",
    "微服务架构",
    "数据库设计",
    "前端开发",
    "人工智能",
    "机器学习",
    "区块链",
    "云计算",
    "大数据"
  ]
}
```

### 5. 记录搜索行为

**POST** `/api/v1/search/record`

**参数：**
- `keyword` (string, required): 搜索关键词
- `userId` (long, optional): 用户ID
- `resultCount` (long, optional): 搜索结果数量

### 6. 搜索统计信息

**GET** `/api/v1/search/stats`

**响应示例：**
```json
{
  "success": true,
  "data": {
    "hotKeywords": ["Java编程", "Spring Boot", "微服务", "数据库", "前端"],
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

## 🛠️ 部署指南

### 1. 数据库初始化

执行搜索模块SQL脚本：
```bash
mysql -u root -p collide < sql/06-search-tables.sql
```

### 2. 配置说明

搜索模块无需额外配置，使用项目现有的数据库连接。

### 3. 启动服务

```bash
# 启动基础环境
docker-compose -f middleware/docker-compose.yml up -d

# 启动业务服务
java -jar collide-application/collide-app/target/collide-app-1.0.0-SNAPSHOT.jar
```

### 4. 访问测试

- **API文档**: http://localhost:8080/swagger-ui.html
- **搜索测试**: http://localhost:8080/api/v1/search?keyword=Java

## 📊 性能优化

### 1. 数据库索引优化

```sql
-- 内容表索引
ALTER TABLE t_content ADD FULLTEXT INDEX ft_title_description (title, description);
ALTER TABLE t_content ADD INDEX idx_author_status (author_id, status, review_status);

-- 用户表索引
ALTER TABLE t_user ADD INDEX idx_username_nickname (username, nickname);

-- 评论表索引
ALTER TABLE t_comment ADD FULLTEXT INDEX ft_content (content);
```

### 2. 查询优化建议

- 使用全文索引进行模糊搜索
- 合理使用分页，避免深度分页
- 缓存热门搜索结果
- 异步记录搜索历史

### 3. 缓存策略

```java
// 热门关键词缓存（建议1小时）
@Cacheable(value = "hot_keywords", key = "#limit")
public List<String> getHotKeywords(Integer limit) { ... }

// 搜索建议缓存（建议30分钟）
@Cacheable(value = "search_suggestions", key = "#keyword + '_' + #limit")
public List<SuggestionItem> getKeywordSuggestions(String keyword, Integer limit) { ... }
```

## 🔧 扩展功能

### 1. 搜索历史管理

```java
// 清理用户搜索历史
DELETE FROM t_search_history 
WHERE user_id = ? AND create_time < DATE_SUB(NOW(), INTERVAL 30 DAY);

// 导出用户搜索报告
SELECT keyword, COUNT(*) as search_count, DATE(create_time) as search_date
FROM t_search_history 
WHERE user_id = ? 
GROUP BY keyword, DATE(create_time)
ORDER BY search_date DESC, search_count DESC;
```

### 2. 搜索建议配置

```sql
-- 添加热门搜索建议
INSERT INTO t_search_suggestion (keyword, suggestion_type, weight, status)
VALUES 
('Java编程入门', 'KEYWORD', 10.0, 1),
('Spring Boot实战', 'KEYWORD', 9.5, 1),
('微服务架构设计', 'KEYWORD', 9.0, 1);

-- 添加用户建议
INSERT INTO t_search_suggestion (keyword, suggestion_type, weight, status)
VALUES 
('tech_expert', 'USER', 8.0, 1),
('java_master', 'USER', 7.5, 1);

-- 添加标签建议
INSERT INTO t_search_suggestion (keyword, suggestion_type, weight, status)
VALUES 
('编程技术', 'TAG', 6.0, 1),
('后端开发', 'TAG', 5.5, 1);
```

### 3. 搜索分析报告

```sql
-- 搜索热度趋势
SELECT 
    DATE(last_search_time) as search_date,
    keyword,
    search_count,
    user_count
FROM t_search_statistics
WHERE last_search_time >= DATE_SUB(NOW(), INTERVAL 7 DAY)
ORDER BY search_date DESC, search_count DESC;

-- 用户搜索行为分析
SELECT 
    u.id as user_id,
    u.username,
    COUNT(h.id) as total_searches,
    COUNT(DISTINCT h.keyword) as unique_keywords,
    AVG(h.result_count) as avg_results
FROM t_user u
LEFT JOIN t_search_history h ON u.id = h.user_id
WHERE h.create_time >= DATE_SUB(NOW(), INTERVAL 30 DAY)
GROUP BY u.id, u.username
ORDER BY total_searches DESC;
```

## 🚨 注意事项

### 1. 数据一致性
- 搜索索引与业务数据保持同步
- 定期清理过期搜索历史
- 监控搜索统计数据准确性

### 2. 性能监控
- 监控搜索响应时间
- 关注数据库连接池状态
- 定期分析慢查询日志

### 3. 安全考虑
- 防止SQL注入攻击
- 限制搜索频率避免滥用
- 敏感词过滤和内容审核

## 📞 技术支持

如有问题，请联系：
- **开发团队**: GIG Team
- **文档更新**: 2024-01-01
- **版本**: v1.0.0

---

**🎉 现在就开始使用 Collide 搜索模块，体验高效便捷的搜索服务吧！** 