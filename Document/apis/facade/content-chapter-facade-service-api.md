# Content Chapter Facade Service API 文档

**Facade服务**: ContentChapterFacadeService  
**版本**: 2.0.0 (内容付费版)  
**Dubbo版本**: 5.0.0  
**超时时间**: 5000ms  
**服务路径**: `com.gig.collide.api.content.ContentChapterFacadeService`  
**方法数量**: 21个  
**更新时间**: 2024-01-31  

## 🚀 概述

内容章节外观服务接口专注于C端必需的章节查询功能，基于单表无连表设计。提供多章节内容（如小说、漫画）的章节管理RPC接口，支持章节导航、统计分析和管理操作。

**设计理念**:
- **C端简洁版**: 专注于C端必需功能，移除复杂的管理接口
- **单表设计**: 基于单表无连表设计，提升查询性能
- **高性能**: 针对高并发读取场景优化
- **导航友好**: 提供完整的章节导航功能

## 📋 接口分类

| 分类 | 方法数量 | 功能描述 |
|------|----------|----------|
| **基础查询功能** | 14个 | 章节列表、导航、搜索、筛选 |
| **统计功能** | 4个 | 章节数量、字数统计 |
| **管理功能** | 3个 | 批量更新、删除、重排序 |

---

## 📚 1. 基础查询功能 (14个方法)

### 1.1 根据内容ID查询章节列表

**方法**: `getChaptersByContentId(Long contentId)`

**描述**: 根据内容ID查询所有章节列表（按章节号排序）

**参数**:
- `contentId` (Long): 内容ID

**返回值**: `List<ChapterResponse>`

**调用示例**:
```java
List<ChapterResponse> chapters = contentChapterFacadeService.getChaptersByContentId(12345L);
```

**响应示例**:
```java
List<ChapterResponse> chapters = [
    ChapterResponse.builder()
        .id(67890L)
        .contentId(12345L)
        .chapterNum(1)
        .title("第一章 初入江湖")
        .wordCount(2500)
        .status("PUBLISHED")
        .createTime(LocalDateTime.of(2024, 1, 1, 10, 0))
        .build(),
    ChapterResponse.builder()
        .id(67891L)
        .contentId(12345L)
        .chapterNum(2)
        .title("第二章 奇遇连连")
        .wordCount(3000)
        .status("PUBLISHED")
        .createTime(LocalDateTime.of(2024, 1, 2, 10, 0))
        .build()
];
```

### 1.2 根据内容ID查询已发布章节列表

**方法**: `getPublishedChaptersByContentId(Long contentId)`

**描述**: 根据内容ID查询已发布的章节列表

**参数**:
- `contentId` (Long): 内容ID

**返回值**: `List<ChapterResponse>`

### 1.3 根据内容ID分页查询章节

**方法**: `getChaptersByContentIdPaged(Long contentId, Integer currentPage, Integer pageSize)`

**描述**: 根据内容ID分页查询章节

**参数**:
- `contentId` (Long): 内容ID
- `currentPage` (Integer): 当前页码
- `pageSize` (Integer): 页面大小

**返回值**: `PageResponse<ChapterResponse>`

**调用示例**:
```java
PageResponse<ChapterResponse> pageResponse = contentChapterFacadeService
    .getChaptersByContentIdPaged(12345L, 1, 20);
```

### 1.4 根据内容ID和章节号查询章节

**方法**: `getChapterByContentIdAndNum(Long contentId, Integer chapterNum)`

**描述**: 根据内容ID和章节号查询指定章节

**参数**:
- `contentId` (Long): 内容ID
- `chapterNum` (Integer): 章节号

**返回值**: `ChapterResponse`

**调用示例**:
```java
ChapterResponse chapter = contentChapterFacadeService
    .getChapterByContentIdAndNum(12345L, 1);
```

### 1.5 查询内容的下一章节

**方法**: `getNextChapter(Long contentId, Integer currentChapterNum)`

**描述**: 查询内容的下一章节

**参数**:
- `contentId` (Long): 内容ID
- `currentChapterNum` (Integer): 当前章节号

**返回值**: `ChapterResponse` - 如果没有下一章节返回null

**调用示例**:
```java
ChapterResponse nextChapter = contentChapterFacadeService
    .getNextChapter(12345L, 1);
if (nextChapter != null) {
    // 存在下一章节
    System.out.println("下一章: " + nextChapter.getTitle());
}
```

### 1.6 查询内容的上一章节

**方法**: `getPreviousChapter(Long contentId, Integer currentChapterNum)`

**描述**: 查询内容的上一章节

**参数**:
- `contentId` (Long): 内容ID
- `currentChapterNum` (Integer): 当前章节号

**返回值**: `ChapterResponse` - 如果没有上一章节返回null

### 1.7 查询内容的第一章节

**方法**: `getFirstChapter(Long contentId)`

**描述**: 查询内容的第一章节

**参数**:
- `contentId` (Long): 内容ID

**返回值**: `ChapterResponse`

### 1.8 查询内容的最后一章节

**方法**: `getLastChapter(Long contentId)`

**描述**: 查询内容的最后一章节

**参数**:
- `contentId` (Long): 内容ID

**返回值**: `ChapterResponse`

### 1.9 根据状态查询章节列表

**方法**: `getChaptersByStatus(String status)`

**描述**: 根据章节状态查询章节列表

**参数**:
- `status` (String): 章节状态 (DRAFT/PUBLISHED/DELETED)

**返回值**: `List<ChapterResponse>`

### 1.10 根据章节标题搜索

**方法**: `searchChaptersByTitle(String titleKeyword, Integer currentPage, Integer pageSize)`

**描述**: 根据章节标题关键词搜索

**参数**:
- `titleKeyword` (String): 标题关键词
- `currentPage` (Integer): 当前页码
- `pageSize` (Integer): 页面大小

**返回值**: `PageResponse<ChapterResponse>`

**调用示例**:
```java
PageResponse<ChapterResponse> searchResult = contentChapterFacadeService
    .searchChaptersByTitle("江湖", 1, 20);
```

### 1.11 根据内容ID和字数范围查询章节

**方法**: `getChaptersByWordCountRange(Long contentId, Integer minWordCount, Integer maxWordCount)`

**描述**: 根据内容ID和字数范围查询章节

**参数**:
- `contentId` (Long): 内容ID
- `minWordCount` (Integer): 最小字数（可选）
- `maxWordCount` (Integer): 最大字数（可选）

**返回值**: `List<ChapterResponse>`

**调用示例**:
```java
// 查询字数在2000-4000之间的章节
List<ChapterResponse> chapters = contentChapterFacadeService
    .getChaptersByWordCountRange(12345L, 2000, 4000);
```

### 1.12 查询字数最多的章节

**方法**: `getMaxWordCountChapter(Long contentId)`

**描述**: 查询指定内容中字数最多的章节

**参数**:
- `contentId` (Long): 内容ID

**返回值**: `ChapterResponse`

### 1.13 查询指定内容的最新章节

**方法**: `getLatestChapterByContentId(Long contentId)`

**描述**: 查询指定内容的最新更新章节

**参数**:
- `contentId` (Long): 内容ID

**返回值**: `ChapterResponse`

### 1.14 查询最新更新的章节

**方法**: `getLatestChapters(Integer currentPage, Integer pageSize)`

**描述**: 分页查询最新更新的章节列表（跨所有内容）

**参数**:
- `currentPage` (Integer): 当前页码
- `pageSize` (Integer): 页面大小

**返回值**: `PageResponse<ChapterResponse>`

**调用示例**:
```java
// 查询最新更新的20个章节
PageResponse<ChapterResponse> latestChapters = contentChapterFacadeService
    .getLatestChapters(1, 20);
```

---

## 📊 2. 统计功能 (4个方法)

### 2.1 统计内容的章节总数

**方法**: `countChaptersByContentId(Long contentId)`

**描述**: 统计指定内容的章节总数

**参数**:
- `contentId` (Long): 内容ID

**返回值**: `Long` - 章节总数

**调用示例**:
```java
Long totalChapters = contentChapterFacadeService.countChaptersByContentId(12345L);
System.out.println("总章节数: " + totalChapters);
```

### 2.2 统计内容的已发布章节数

**方法**: `countPublishedChaptersByContentId(Long contentId)`

**描述**: 统计指定内容的已发布章节数

**参数**:
- `contentId` (Long): 内容ID

**返回值**: `Long` - 已发布章节数

### 2.3 统计内容的总字数

**方法**: `countTotalWordsByContentId(Long contentId)`

**描述**: 统计指定内容的总字数

**参数**:
- `contentId` (Long): 内容ID

**返回值**: `Long` - 总字数

### 2.4 获取内容的章节统计信息

**方法**: `getChapterStats(Long contentId)`

**描述**: 获取指定内容的章节统计信息

**参数**:
- `contentId` (Long): 内容ID

**返回值**: `Map<String, Object>` - 统计信息

**调用示例**:
```java
Map<String, Object> stats = contentChapterFacadeService.getChapterStats(12345L);
```

**响应示例**:
```java
Map<String, Object> stats = Map.of(
    "totalChapters", 25,
    "publishedChapters", 20,
    "draftChapters", 5,
    "totalWords", 75000L,
    "publishedWords", 60000L,
    "avgWordsPerChapter", 3000,
    "maxWordCount", 5000,
    "minWordCount", 2000,
    "freeChapters", 3,
    "paidChapters", 17,
    "latestChapterNum", 25,
    "latestUpdateTime", LocalDateTime.of(2024, 1, 31, 14, 30),
    "firstPublishTime", LocalDateTime.of(2024, 1, 1, 10, 0)
);
```

---

## ⚙️ 3. 管理功能 (3个方法)

### 3.1 批量更新章节状态

**方法**: `batchUpdateChapterStatus(List<Long> ids, String status)`

**描述**: 批量更新指定章节的状态

**参数**:
- `ids` (List<Long>): 章节ID列表
- `status` (String): 新状态 (DRAFT/PUBLISHED/DELETED)

**返回值**: `boolean` - 是否更新成功

**调用示例**:
```java
List<Long> chapterIds = Arrays.asList(67890L, 67891L, 67892L);
boolean success = contentChapterFacadeService.batchUpdateChapterStatus(chapterIds, "PUBLISHED");
```

### 3.2 删除内容的所有章节

**方法**: `deleteAllChaptersByContentId(Long contentId)`

**描述**: 删除指定内容的所有章节

**参数**:
- `contentId` (Long): 内容ID

**返回值**: `boolean` - 是否删除成功

**调用示例**:
```java
boolean success = contentChapterFacadeService.deleteAllChaptersByContentId(12345L);
```

### 3.3 重新排序章节号

**方法**: `reorderChapterNumbers(Long contentId)`

**描述**: 重新排序指定内容的章节号（用于章节删除后的重新编号）

**参数**:
- `contentId` (Long): 内容ID

**返回值**: `boolean` - 是否重排序成功

**调用示例**:
```java
// 当删除了中间章节后，重新排序章节号
boolean success = contentChapterFacadeService.reorderChapterNumbers(12345L);
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

### ChapterNavigation 章节导航对象
```java
@Data
@Builder
public class ChapterNavigation {
    private ChapterResponse currentChapter;   // 当前章节
    private ChapterResponse previousChapter;  // 上一章节
    private ChapterResponse nextChapter;      // 下一章节
    private ChapterResponse firstChapter;     // 第一章节
    private ChapterResponse lastChapter;      // 最后章节
    private Integer totalChapters;            // 总章节数
    private Integer currentPosition;          // 当前位置
}
```

### ChapterStats 章节统计对象
```java
@Data
@Builder
public class ChapterStats {
    private Integer totalChapters;           // 总章节数
    private Integer publishedChapters;       // 已发布章节数
    private Integer draftChapters;           // 草稿章节数
    private Long totalWords;                 // 总字数
    private Long publishedWords;             // 已发布字数
    private Integer avgWordsPerChapter;      // 平均每章字数
    private Integer maxWordCount;            // 最大章节字数
    private Integer minWordCount;            // 最小章节字数
    private Integer freeChapters;            // 免费章节数
    private Integer paidChapters;            // 付费章节数
    private Integer latestChapterNum;        // 最新章节号
    private LocalDateTime latestUpdateTime;  // 最新更新时间
    private LocalDateTime firstPublishTime; // 首次发布时间
}
```

## 🚨 错误处理

由于这是一个接口定义（interface），具体的错误处理由实现类负责。常见的异常情况包括：

- **参数异常**: `contentId`为null或无效
- **数据不存在**: 指定的内容或章节不存在
- **状态异常**: 无效的章节状态值
- **权限异常**: 没有足够的操作权限

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
            ChapterResponse currentChapter = chapterFacadeService
                .getChapterByContentIdAndNum(contentId, chapterNum);
            
            if (currentChapter == null) {
                throw new BusinessException("章节不存在");
            }
            
            // 获取导航信息
            ChapterResponse previousChapter = chapterFacadeService
                .getPreviousChapter(contentId, chapterNum);
            ChapterResponse nextChapter = chapterFacadeService
                .getNextChapter(contentId, chapterNum);
            ChapterResponse firstChapter = chapterFacadeService
                .getFirstChapter(contentId);
            ChapterResponse lastChapter = chapterFacadeService
                .getLastChapter(contentId);
            
            // 获取总章节数
            Long totalCount = chapterFacadeService.countChaptersByContentId(contentId);
            
            return ChapterNavigation.builder()
                .currentChapter(currentChapter)
                .previousChapter(previousChapter)
                .nextChapter(nextChapter)
                .firstChapter(firstChapter)
                .lastChapter(lastChapter)
                .totalChapters(totalCount.intValue())
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
        // 获取已发布的章节列表
        return chapterFacadeService.getPublishedChaptersByContentId(contentId);
    }
    
    @Cacheable(value = "chapter_stats", key = "#contentId", unless = "#result == null")
    public ChapterStats getChapterStatistics(Long contentId) {
        Map<String, Object> stats = chapterFacadeService.getChapterStats(contentId);
        
        return ChapterStats.builder()
            .totalChapters((Integer) stats.get("totalChapters"))
            .publishedChapters((Integer) stats.get("publishedChapters"))
            .totalWords((Long) stats.get("totalWords"))
            .avgWordsPerChapter((Integer) stats.get("avgWordsPerChapter"))
            .latestUpdateTime((LocalDateTime) stats.get("latestUpdateTime"))
            .build();
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
        
        return chapterFacadeService.searchChaptersByTitle(keyword, page, size);
    }
    
    public List<ChapterResponse> getChaptersByWordCount(Long contentId, Integer minWords, Integer maxWords) {
        return chapterFacadeService.getChaptersByWordCountRange(contentId, minWords, maxWords);
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
                return chapterFacadeService.batchUpdateChapterStatus(chapterIds, "PUBLISHED");
            } catch (Exception e) {
                log.error("批量发布章节失败: chapterIds={}", chapterIds, e);
                return false;
            }
        });
    }
}
```

## 🔧 性能优化建议

1. **缓存策略**: 
   - 章节列表: TTL 10分钟
   - 章节内容: TTL 30分钟
   - 统计信息: TTL 5分钟

2. **分页优化**: 
   - 建议页面大小不超过50
   - 使用游标分页代替offset分页

3. **查询优化**:
   - 章节导航建议批量查询
   - 搜索结果建议异步获取

4. **连接池配置**:
   ```yaml
   dubbo:
     consumer:
       connections: 10  # 每个服务提供者的连接数
       actives: 200     # 每个连接的最大活跃请求数
   ```

## 🔗 相关文档

- [ContentChapterController REST API 文档](./content-chapter-controller-api.md)
- [ContentFacadeService 文档](./content-facade-service-api.md)
- [章节数据模型](../models/chapter-model.md)
- [阅读器设计文档](../design/reader-design.md)

---

**联系信息**:  
- Facade服务: ContentChapterFacadeService  
- 版本: 2.0.0 (内容付费版)  
- Dubbo版本: 5.0.0  
- 维护: GIG团队  
- 更新: 2024-01-31