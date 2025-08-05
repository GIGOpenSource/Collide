# Content Chapter Facade Service API 文档

**Facade服务**: ContentChapterFacadeService  
**版本**: 2.0.0 (极简版)  
**Dubbo版本**: 5.0.0  
**超时时间**: 5000ms  
**服务路径**: `com.gig.collide.api.content.ContentChapterFacadeService`  
**方法数量**: 8个  
**更新时间**: 2024-01-31  

## 🚀 概述

内容章节外观服务接口 - 极简版，专注于C端必需的章节查询功能，基于万能查询方法设计。极简8个核心方法满足所有章节管理需求。

**设计理念**:
- **极简设计**: 8个核心方法替代原有21个方法
- **万能查询**: 单个查询方法替代多个具体查询方法
- **高性能**: 基于条件查询和批量操作优化
- **统一接口**: 统一的参数结构和返回格式

## 📋 接口分类

| 分类 | 方法数量 | 功能描述 |
|------|----------|----------|
| **核心CRUD功能** | 2个 | 章节查询和删除 |
| **万能查询功能** | 3个 | 条件查询、导航查询、搜索 |
| **统计功能** | 1个 | 章节统计信息 |
| **批量操作功能** | 2个 | 批量状态更新、批量删除 |

---

## 🔧 1. 核心CRUD功能 (2个方法)

### 1.1 根据ID获取章节详情

**方法**: `getChapterById(Long id)`

**描述**: 根据章节ID获取章节详细信息

**参数**:
- `id` (Long): 章节ID

**返回值**: `Result<ChapterResponse>`

**调用示例**:
```java
Result<ChapterResponse> result = contentChapterFacadeService.getChapterById(67890L);
if (result.isSuccess()) {
    ChapterResponse chapter = result.getData();
    System.out.println("章节标题: " + chapter.getTitle());
    System.out.println("字数: " + chapter.getWordCount());
}
```

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

### 1.2 删除章节

**方法**: `deleteChapter(Long id)`

**描述**: 逻辑删除指定章节

**参数**:
- `id` (Long): 章节ID

**返回值**: `Result<Boolean>`

**调用示例**:
```java
Result<Boolean> result = contentChapterFacadeService.deleteChapter(67890L);
if (result.isSuccess() && result.getData()) {
    System.out.println("章节删除成功");
}
```

---

## 🔍 2. 万能查询功能 (3个方法)

### 2.1 万能条件查询章节列表

**方法**: `getChaptersByConditions(Long contentId, String status, Integer chapterNumStart, Integer chapterNumEnd, Integer minWordCount, Integer maxWordCount, String orderBy, String orderDirection, Integer currentPage, Integer pageSize)`

**描述**: 根据多种条件查询章节列表，替代所有具体查询API

**核心功能**: 
- 替代`getChaptersByContentId`、`getPublishedChapters`、`getChaptersByWordCount`等14个方法
- 支持分页和不分页查询
- 灵活的排序和筛选条件

**参数**:
- `contentId` (Long): 内容ID（可选）
- `status` (String): 章节状态（可选：DRAFT/PUBLISHED/DELETED）
- `chapterNumStart` (Integer): 章节号起始（可选）
- `chapterNumEnd` (Integer): 章节号结束（可选）
- `minWordCount` (Integer): 最小字数（可选）
- `maxWordCount` (Integer): 最大字数（可选）
- `orderBy` (String): 排序字段（可选：chapterNum、createTime、updateTime、wordCount）
- `orderDirection` (String): 排序方向（可选：ASC、DESC）
- `currentPage` (Integer): 当前页码（可选，不分页时传null）
- `pageSize` (Integer): 页面大小（可选，不分页时传null）

**返回值**: `Result<PageResponse<ChapterResponse>>`

**调用示例**:
```java
// 查询指定内容的已发布章节（按章节号排序）
Result<PageResponse<ChapterResponse>> result = contentChapterFacadeService
    .getChaptersByConditions(12345L, "PUBLISHED", null, null, null, null, 
                           "chapterNum", "ASC", 1, 20);

// 查询字数在2000-4000之间的章节
Result<PageResponse<ChapterResponse>> result2 = contentChapterFacadeService
    .getChaptersByConditions(12345L, null, null, null, 2000, 4000, 
                           "wordCount", "DESC", null, null);

// 查询最新更新的章节（跨所有内容）
Result<PageResponse<ChapterResponse>> result3 = contentChapterFacadeService
    .getChaptersByConditions(null, "PUBLISHED", null, null, null, null, 
                           "updateTime", "DESC", 1, 20);
```

### 2.2 章节导航查询

**方法**: `getChapterByNavigation(Long contentId, Integer currentChapterNum, String direction)`

**描述**: 章节导航查询，替代getNextChapter、getPreviousChapter、getFirstChapter、getLastChapter

**参数**:
- `contentId` (Long): 内容ID
- `currentChapterNum` (Integer): 当前章节号
- `direction` (String): 导航方向（next、previous、first、last）

**返回值**: `Result<ChapterResponse>`

**调用示例**:
```java
// 获取下一章节
Result<ChapterResponse> nextResult = contentChapterFacadeService
    .getChapterByNavigation(12345L, 1, "next");

// 获取上一章节
Result<ChapterResponse> prevResult = contentChapterFacadeService
    .getChapterByNavigation(12345L, 5, "previous");

// 获取第一章节
Result<ChapterResponse> firstResult = contentChapterFacadeService
    .getChapterByNavigation(12345L, null, "first");

// 获取最后章节
Result<ChapterResponse> lastResult = contentChapterFacadeService
    .getChapterByNavigation(12345L, null, "last");
```

### 2.3 搜索章节

**方法**: `searchChapters(String keyword, Long contentId, String status, Integer currentPage, Integer pageSize)`

**描述**: 按标题、内容搜索章节，替代searchChaptersByTitle

**参数**:
- `keyword` (String): 搜索关键词
- `contentId` (Long): 内容ID（可选，限定搜索范围）
- `status` (String): 章节状态（可选）
- `currentPage` (Integer): 当前页码
- `pageSize` (Integer): 页面大小

**返回值**: `Result<PageResponse<ChapterResponse>>`

**调用示例**:
```java
// 在指定内容中搜索包含"江湖"的章节
Result<PageResponse<ChapterResponse>> result = contentChapterFacadeService
    .searchChapters("江湖", 12345L, "PUBLISHED", 1, 20);

// 全局搜索包含"修炼"的章节
Result<PageResponse<ChapterResponse>> result2 = contentChapterFacadeService
    .searchChapters("修炼", null, null, 1, 50);
```

---

## 📊 3. 统计功能 (1个方法)

### 3.1 获取章节统计信息

**方法**: `getChapterStats(Long contentId)`

**描述**: 获取章节统计信息，替代所有单个统计方法

**核心功能**: 
- 替代`countChaptersByContentId`、`countPublishedChapters`、`countTotalWords`等4个方法
- 一次调用返回完整统计信息

**参数**:
- `contentId` (Long): 内容ID

**返回值**: `Result<Map<String, Object>>`

**调用示例**:
```java
Result<Map<String, Object>> result = contentChapterFacadeService.getChapterStats(12345L);
if (result.isSuccess()) {
    Map<String, Object> stats = result.getData();
    System.out.println("总章节数: " + stats.get("totalChapters"));
    System.out.println("已发布数: " + stats.get("publishedChapters"));
    System.out.println("总字数: " + stats.get("totalWords"));
}
```

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

## ⚙️ 4. 批量操作功能 (2个方法)

### 4.1 批量更新章节状态

**方法**: `batchUpdateChapterStatus(List<Long> ids, String status)`

**描述**: 批量更新指定章节的状态

**参数**:
- `ids` (List<Long>): 章节ID列表
- `status` (String): 新状态（DRAFT/PUBLISHED/DELETED）

**返回值**: `Result<Boolean>`

**调用示例**:
```java
List<Long> chapterIds = Arrays.asList(67890L, 67891L, 67892L);
Result<Boolean> result = contentChapterFacadeService
    .batchUpdateChapterStatus(chapterIds, "PUBLISHED");
if (result.isSuccess() && result.getData()) {
    System.out.println("批量发布成功");
}
```

### 4.2 批量删除章节

**方法**: `batchDeleteChapters(List<Long> ids)`

**描述**: 批量删除章节

**参数**:
- `ids` (List<Long>): 章节ID列表

**返回值**: `Result<Boolean>`

**调用示例**:
```java
List<Long> chapterIds = Arrays.asList(67890L, 67891L, 67892L);
Result<Boolean> result = contentChapterFacadeService.batchDeleteChapters(chapterIds);
if (result.isSuccess() && result.getData()) {
    System.out.println("批量删除成功");
}
```

---

## 🎯 数据模型

### ChapterResponse 章节响应对象
```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChapterResponse {
    private Long id;                    // 章节ID
    private Long contentId;             // 内容ID
    private String contentTitle;        // 内容标题（仅在跨内容查询时返回）
    private Integer chapterNum;         // 章节号
    private String title;               // 章节标题
    private String content;             // 章节内容（仅在详情查询时返回）
    private String summary;             // 章节简介
    private Integer wordCount;          // 字数
    private String status;              // 状态（DRAFT/PUBLISHED/DELETED）
    private Boolean isFree;             // 是否免费
    private Long viewCount;             // 浏览量
    private Long likeCount;             // 点赞数
    private Long commentCount;          // 评论数
    private LocalDateTime createTime;   // 创建时间
    private LocalDateTime publishTime;  // 发布时间
    private LocalDateTime updateTime;   // 更新时间
    private Map<String, Object> highlight; // 搜索高亮信息（仅在搜索时返回）
}
```

## 🚨 错误代码

| 错误码 | 描述 | 解决方案 |
|--------|------|----------|
| CHAPTER_NOT_FOUND | 章节不存在 | 检查章节ID是否正确 |
| CONTENT_NOT_FOUND | 内容不存在 | 检查内容ID是否正确 |
| INVALID_PARAMETER | 参数验证失败 | 检查请求参数的格式和必填项 |
| DELETE_CHAPTER_FAILED | 删除章节失败 | 确认章节存在且有权限 |
| BATCH_UPDATE_FAILED | 批量更新失败 | 检查章节ID列表和状态值 |
| SEARCH_FAILED | 搜索失败 | 检查搜索关键词和参数 |
| STATS_CALCULATION_FAILED | 统计计算失败 | 检查统计参数 |
| NAVIGATION_FAILED | 导航查询失败 | 检查内容ID和导航方向 |

## 🔧 Dubbo配置示例

### 服务提供者配置
```yaml
dubbo:
  application:
    name: collide-content
  registry:
    address: nacos://localhost:8848
  protocol:
    name: dubbo
    port: 20885
  provider:
    timeout: 5000
    retries: 0
    version: 5.0.0
  scan:
    base-packages: com.gig.collide.content.facade
```

### 服务消费者配置
```yaml
dubbo:
  application:
    name: collide-reader
  registry:
    address: nacos://localhost:8848
  consumer:
    timeout: 5000
    retries: 1  # 查询类接口可以重试
    version: 5.0.0
```

## 📈 使用示例

### 阅读器导航实现
```java
@Service
@Slf4j
public class ChapterNavigationService {
    
    @DubboReference(version = "5.0.0", timeout = 5000)
    private ContentChapterFacadeService chapterFacadeService;
    
    public ChapterNavigation getChapterNavigation(Long contentId, Integer chapterNum) {
        try {
            // 获取当前章节
            Result<ChapterResponse> currentResult = chapterFacadeService
                .getChapterByNavigation(contentId, chapterNum, "current");
            
            if (!currentResult.isSuccess() || currentResult.getData() == null) {
                throw new BusinessException("章节不存在");
            }
            
            // 获取导航信息（使用统一的导航方法）
            Result<ChapterResponse> previousResult = chapterFacadeService
                .getChapterByNavigation(contentId, chapterNum, "previous");
            Result<ChapterResponse> nextResult = chapterFacadeService
                .getChapterByNavigation(contentId, chapterNum, "next");
            Result<ChapterResponse> firstResult = chapterFacadeService
                .getChapterByNavigation(contentId, null, "first");
            Result<ChapterResponse> lastResult = chapterFacadeService
                .getChapterByNavigation(contentId, null, "last");
            
            // 获取统计信息
            Result<Map<String, Object>> statsResult = chapterFacadeService.getChapterStats(contentId);
            Integer totalCount = statsResult.isSuccess() ? 
                (Integer) statsResult.getData().get("totalChapters") : 0;
            
            return ChapterNavigation.builder()
                .currentChapter(currentResult.getData())
                .previousChapter(previousResult.isSuccess() ? previousResult.getData() : null)
                .nextChapter(nextResult.isSuccess() ? nextResult.getData() : null)
                .firstChapter(firstResult.isSuccess() ? firstResult.getData() : null)
                .lastChapter(lastResult.isSuccess() ? lastResult.getData() : null)
                .totalChapters(totalCount)
                .currentPosition(chapterNum)
                .build();
                
        } catch (Exception e) {
            log.error("获取章节导航失败: contentId={}, chapterNum={}", contentId, chapterNum, e);
            throw new BusinessException("获取章节导航失败");
        }
    }
}
```

### 章节目录服务
```java
@Service
public class ChapterCatalogService {
    
    @DubboReference(version = "5.0.0", timeout = 5000)
    private ContentChapterFacadeService chapterFacadeService;
    
    @Cacheable(value = "chapter_catalog", key = "#contentId")
    public List<ChapterResponse> getChapterCatalog(Long contentId) {
        // 使用万能查询获取已发布的章节列表
        Result<PageResponse<ChapterResponse>> result = chapterFacadeService
            .getChaptersByConditions(contentId, "PUBLISHED", null, null, null, null, 
                                   "chapterNum", "ASC", null, null);
        
        return result.isSuccess() ? result.getData().getRecords() : Collections.emptyList();
    }
    
    @Cacheable(value = "chapter_stats", key = "#contentId", unless = "#result == null")
    public Map<String, Object> getChapterStatistics(Long contentId) {
        Result<Map<String, Object>> result = chapterFacadeService.getChapterStats(contentId);
        return result.isSuccess() ? result.getData() : Collections.emptyMap();
    }
}
```

### 搜索服务集成
```java
@Service
public class ChapterSearchService {
    
    @DubboReference(version = "5.0.0", timeout = 5000)
    private ContentChapterFacadeService chapterFacadeService;
    
    public PageResponse<ChapterResponse> searchChapters(String keyword, Integer page, Integer size) {
        // 设置默认值
        page = page != null ? page : 1;
        size = size != null ? size : 20;
        
        Result<PageResponse<ChapterResponse>> result = chapterFacadeService
            .searchChapters(keyword, null, null, page, size);
        
        return result.isSuccess() ? result.getData() : PageResponse.empty();
    }
    
    public List<ChapterResponse> getChaptersByWordCount(Long contentId, Integer minWords, Integer maxWords) {
        Result<PageResponse<ChapterResponse>> result = chapterFacadeService
            .getChaptersByConditions(contentId, null, null, null, minWords, maxWords, 
                                   "wordCount", "DESC", null, null);
        
        return result.isSuccess() ? result.getData().getRecords() : Collections.emptyList();
    }
}
```

### 异步批量操作
```java
@Service
public class ChapterManagementService {
    
    @DubboReference(version = "5.0.0", timeout = 10000) // 管理操作超时时间更长
    private ContentChapterFacadeService chapterFacadeService;
    
    @Async
    public CompletableFuture<Boolean> batchPublishChapters(List<Long> chapterIds) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Result<Boolean> result = chapterFacadeService
                    .batchUpdateChapterStatus(chapterIds, "PUBLISHED");
                return result.isSuccess() && result.getData();
            } catch (Exception e) {
                log.error("批量发布章节失败: chapterIds={}", chapterIds, e);
                return false;
            }
        });
    }
    
    @Async
    public CompletableFuture<Boolean> batchDeleteChapters(List<Long> chapterIds) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Result<Boolean> result = chapterFacadeService.batchDeleteChapters(chapterIds);
                return result.isSuccess() && result.getData();
            } catch (Exception e) {
                log.error("批量删除章节失败: chapterIds={}", chapterIds, e);
                return false;
            }
        });
    }
}
```

## 🔧 性能优化建议

1. **缓存策略**: 
   - 章节列表: TTL 10分钟
   - 章节统计: TTL 5分钟
   - 导航信息: TTL 15分钟

2. **查询优化**: 
   - 使用万能查询减少接口调用次数
   - 批量操作优于单个操作
   - 合理使用分页避免大结果集

3. **连接池配置**:
   ```yaml
   dubbo:
     consumer:
       connections: 5   # 极简版减少连接数
       actives: 100     # 每个连接的最大活跃请求数
       timeout: 5000    # 合理超时时间
   ```

4. **接口使用建议**:
   - 优先使用万能查询方法
   - 统计信息一次性获取
   - 批量操作替代循环调用

## 🚀 极简设计优势

1. **方法精简**: 从21个方法缩减到8个，学习成本降低75%
2. **万能查询**: 一个方法替代14个具体查询方法
3. **统一返回**: 所有方法返回`Result<T>`统一结构
4. **批量优化**: 支持批量操作，提升性能
5. **参数灵活**: 可选参数设计，适应各种查询场景

## 🔗 相关文档

- [ContentChapterController REST API 文档](../news/content-chapter-controller-api.md)
- [ContentFacadeService 文档](./content-facade-service-api.md)
- [ContentPurchaseFacadeService 文档](./content-purchase-facade-service-api.md)
- [ContentPaymentFacadeService 文档](./content-payment-facade-service-api.md)

---

**联系信息**:  
- Facade服务: ContentChapterFacadeService  
- 版本: 2.0.0 (极简版)  
- Dubbo版本: 5.0.0  
- 维护: GIG团队  
- 更新: 2024-01-31