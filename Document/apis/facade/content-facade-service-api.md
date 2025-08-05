# Content Facade Service API 文档

**Facade服务**: ContentFacadeService  
**版本**: 2.0.0 (极简版)  
**Dubbo版本**: 5.0.0  
**超时时间**: 5000ms  
**服务路径**: `com.gig.collide.api.content.ContentFacadeService`  
**方法数量**: 12个  
**更新时间**: 2024-01-01  

## 🚀 概述

内容门面服务接口 - 极简版，基于万能查询的12个核心方法设计。支持多种内容类型：NOVEL、COMIC、VIDEO、ARTICLE、AUDIO。

**核心职责**:
- **内容管理**: 创建、更新、删除、状态管理
- **万能查询**: 一个方法替代多个具体查询
- **统计服务**: 统一的统计信息管理
- **数据同步**: 外部数据同步

**设计理念**:
- **极简设计**: 12个核心方法替代原有26个方法
- **万能查询**: 统一的条件查询接口
- **统一管理**: 状态、统计的集中管理
- **高性能**: 优化的查询和批量操作

## 📋 接口分类

| 分类 | 方法数量 | 功能描述 |
|------|----------|----------|
| **核心CRUD功能** | 4个 | 内容的增删改查 |
| **万能查询功能** | 3个 | 条件查询、搜索、推荐 |
| **状态管理功能** | 2个 | 状态更新、批量操作 |
| **统计管理功能** | 2个 | 统计更新、浏览量增加 |
| **数据同步功能** | 1个 | 外部数据同步 |

---

## 🔧 1. 核心CRUD功能 (4个方法)

### 1.1 创建内容

**方法**: `createContent(ContentCreateRequest request)`

**描述**: 创建新的内容，支持多种内容类型

**参数**:
```java
@Data
public class ContentCreateRequest {
    private String title;              // 内容标题（必填）
    private String description;        // 内容描述
    private String contentType;        // 内容类型：NOVEL/COMIC/VIDEO/AUDIO/ARTICLE
    private String content;            // 内容正文
    private Long authorId;             // 作者ID（必填）
    private Long categoryId;           // 分类ID（必填）
    private List<String> tags;         // 标签列表
    private String coverImage;         // 封面图片URL
    private Boolean isPublic;          // 是否公开，默认true
    private Boolean allowComment;      // 是否允许评论，默认true
}
```

**返回值**: `Result<Void>`

**调用示例**:
```java
ContentCreateRequest request = new ContentCreateRequest();
request.setTitle("我的玄幻小说");
request.setContentType("NOVEL");
request.setAuthorId(1001L);
request.setCategoryId(201L);
request.setDescription("这是一部精彩的玄幻小说");

Result<Void> result = contentFacadeService.createContent(request);
if (result.isSuccess()) {
    System.out.println("内容创建成功");
}
```

### 1.2 更新内容

**方法**: `updateContent(ContentUpdateRequest request)`

**描述**: 更新已有内容的信息

**参数**:
```java
@Data
public class ContentUpdateRequest {
    private Long id;                   // 内容ID（必填）
    private String title;              // 内容标题
    private String description;        // 内容描述
    private String content;            // 内容正文
    private List<String> tags;         // 标签列表
    private String coverImage;         // 封面图片URL
    private Boolean isPublic;          // 是否公开
    private Boolean allowComment;      // 是否允许评论
}
```

**返回值**: `Result<ContentResponse>`

**调用示例**:
```java
ContentUpdateRequest request = new ContentUpdateRequest();
request.setId(12345L);
request.setTitle("更新后的标题");
request.setDescription("更新后的描述");

Result<ContentResponse> result = contentFacadeService.updateContent(request);
if (result.isSuccess()) {
    ContentResponse content = result.getData();
    System.out.println("更新成功: " + content.getTitle());
}
```

### 1.3 根据ID获取内容详情

**方法**: `getContentById(Long contentId, Boolean includeOffline)`

**描述**: 根据内容ID获取内容详细信息

**参数**:
- `contentId` (Long): 内容ID
- `includeOffline` (Boolean): 是否包含下线内容

**返回值**: `Result<ContentResponse>`

**调用示例**:
```java
Result<ContentResponse> result = contentFacadeService.getContentById(12345L, false);
if (result.isSuccess()) {
    ContentResponse content = result.getData();
    System.out.println("内容标题: " + content.getTitle());
    System.out.println("浏览量: " + content.getViewCount());
}
```

### 1.4 删除内容

**方法**: `deleteContent(Long contentId, Long operatorId)`

**描述**: 逻辑删除指定内容

**参数**:
- `contentId` (Long): 内容ID
- `operatorId` (Long): 操作人ID

**返回值**: `Result<Void>`

**调用示例**:
```java
Result<Void> result = contentFacadeService.deleteContent(12345L, 2001L);
if (result.isSuccess()) {
    System.out.println("内容删除成功");
}
```

---

## 🔍 2. 万能查询功能 (3个方法)

### 2.1 万能条件查询内容列表

**方法**: `queryContentsByConditions(Long authorId, Long categoryId, String contentType, String status, String reviewStatus, Double minScore, Integer timeRange, String orderBy, String orderDirection, Integer currentPage, Integer pageSize)`

**描述**: 根据多种条件查询内容列表，替代所有具体查询API

**核心功能**: 
- 替代`getContentsByAuthor`、`getContentsByCategory`、`getPopularContents`、`getLatestContents`等方法
- 支持灵活的排序和筛选条件
- 支持时间范围筛选（热门内容）

**参数**:
- `authorId` (Long): 作者ID（可选）
- `categoryId` (Long): 分类ID（可选）
- `contentType` (String): 内容类型（可选）
- `status` (String): 状态（可选）
- `reviewStatus` (String): 审核状态（可选）
- `minScore` (Double): 最小评分（可选）
- `timeRange` (Integer): 时间范围天数（可选，用于热门内容）
- `orderBy` (String): 排序字段（可选：createTime、updateTime、viewCount、likeCount、favoriteCount、shareCount、commentCount、score）
- `orderDirection` (String): 排序方向（可选：ASC、DESC）
- `currentPage` (Integer): 当前页码
- `pageSize` (Integer): 页面大小

**返回值**: `Result<PageResponse<ContentResponse>>`

**调用示例**:
```java
// 查询指定作者的小说（按发布时间排序）
Result<PageResponse<ContentResponse>> result1 = contentFacadeService
    .queryContentsByConditions(1001L, null, "NOVEL", "PUBLISHED", null, null, 
                              null, "publishTime", "DESC", 1, 20);

// 查询热门内容（最近7天，按浏览量排序）
Result<PageResponse<ContentResponse>> result2 = contentFacadeService
    .queryContentsByConditions(null, null, null, "PUBLISHED", null, null, 
                              7, "viewCount", "DESC", 1, 50);

// 查询高评分内容（评分>8.0，按评分排序）
Result<PageResponse<ContentResponse>> result3 = contentFacadeService
    .queryContentsByConditions(null, null, null, "PUBLISHED", null, 8.0, 
                              null, "score", "DESC", 1, 20);
```

### 2.2 搜索内容

**方法**: `searchContents(String keyword, String contentType, Integer currentPage, Integer pageSize)`

**描述**: 根据标题、描述、标签进行搜索

**参数**:
- `keyword` (String): 搜索关键词
- `contentType` (String): 内容类型（可选）
- `currentPage` (Integer): 页码
- `pageSize` (Integer): 页面大小

**返回值**: `Result<PageResponse<ContentResponse>>`

**调用示例**:
```java
// 搜索包含"玄幻"的小说
Result<PageResponse<ContentResponse>> result = contentFacadeService
    .searchContents("玄幻", "NOVEL", 1, 20);

// 全局搜索
Result<PageResponse<ContentResponse>> result2 = contentFacadeService
    .searchContents("修炼", null, 1, 50);
```

### 2.3 获取推荐内容

**方法**: `getRecommendedContents(Long userId, List<Long> excludeContentIds, Integer limit)`

**描述**: 基于用户行为和内容特征获取推荐内容

**参数**:
- `userId` (Long): 用户ID
- `excludeContentIds` (List<Long>): 排除的内容ID列表
- `limit` (Integer): 返回数量限制

**返回值**: `Result<List<ContentResponse>>`

**调用示例**:
```java
List<Long> excludeIds = Arrays.asList(12345L, 12346L);
Result<List<ContentResponse>> result = contentFacadeService
    .getRecommendedContents(1001L, excludeIds, 10);

if (result.isSuccess()) {
    List<ContentResponse> recommendations = result.getData();
    System.out.println("推荐内容数量: " + recommendations.size());
}
```

---

## ⚙️ 3. 状态管理功能 (2个方法)

### 3.1 更新内容状态

**方法**: `updateContentStatus(Long contentId, String status, String reviewStatus, Long operatorId, String comment)`

**描述**: 统一状态管理，可实现发布、审核、下线等操作

**核心功能**: 
- 替代`publishContent`、`reviewContent`、`offlineContent`等方法
- 统一的状态更新接口

**参数**:
- `contentId` (Long): 内容ID
- `status` (String): 内容状态（DRAFT/PUBLISHED/OFFLINE）
- `reviewStatus` (String): 审核状态（PENDING/APPROVED/REJECTED）
- `operatorId` (Long): 操作人ID
- `comment` (String): 操作备注

**返回值**: `Result<Boolean>`

**调用示例**:
```java
// 发布内容
Result<Boolean> result1 = contentFacadeService
    .updateContentStatus(12345L, "PUBLISHED", null, 2001L, "内容发布");

// 审核通过
Result<Boolean> result2 = contentFacadeService
    .updateContentStatus(12345L, null, "APPROVED", 2001L, "审核通过");

// 下线内容
Result<Boolean> result3 = contentFacadeService
    .updateContentStatus(12345L, "OFFLINE", null, 2001L, "违规下线");
```

### 3.2 批量更新状态

**方法**: `batchUpdateStatus(List<Long> ids, String status)`

**描述**: 批量更新内容状态

**参数**:
- `ids` (List<Long>): 内容ID列表
- `status` (String): 目标状态

**返回值**: `Result<Boolean>`

**调用示例**:
```java
List<Long> contentIds = Arrays.asList(12345L, 12346L, 12347L);
Result<Boolean> result = contentFacadeService.batchUpdateStatus(contentIds, "PUBLISHED");
if (result.isSuccess() && result.getData()) {
    System.out.println("批量发布成功");
}
```

---

## 📊 4. 统计管理功能 (2个方法)

### 4.1 更新内容统计信息

**方法**: `updateContentStats(Long contentId, Long viewCount, Long likeCount, Long commentCount, Long favoriteCount, Double score)`

**描述**: 统一统计管理，可实现各种统计数据更新

**核心功能**: 
- 替代`increaseLikeCount`、`increaseCommentCount`、`increaseFavoriteCount`、`updateScore`等方法
- 支持多个统计字段同时更新

**参数**:
- `contentId` (Long): 内容ID
- `viewCount` (Long): 浏览量增量
- `likeCount` (Long): 点赞数增量
- `commentCount` (Long): 评论数增量
- `favoriteCount` (Long): 收藏数增量
- `score` (Double): 新增评分

**返回值**: `Result<Boolean>`

**调用示例**:
```java
// 增加点赞和收藏
Result<Boolean> result1 = contentFacadeService
    .updateContentStats(12345L, null, 1L, null, 1L, null);

// 新增评分
Result<Boolean> result2 = contentFacadeService
    .updateContentStats(12345L, null, null, null, null, 8.5);

// 增加评论数
Result<Boolean> result3 = contentFacadeService
    .updateContentStats(12345L, null, null, 1L, null, null);
```

### 4.2 增加浏览量

**方法**: `increaseViewCount(Long contentId, Integer increment)`

**描述**: 最常用的统计操作单独提供，优化性能

**参数**:
- `contentId` (Long): 内容ID
- `increment` (Integer): 增量

**返回值**: `Result<Long>` - 返回更新后的浏览量

**调用示例**:
```java
Result<Long> result = contentFacadeService.increaseViewCount(12345L, 1);
if (result.isSuccess()) {
    Long newViewCount = result.getData();
    System.out.println("新浏览量: " + newViewCount);
}
```

---

## 🔄 5. 数据同步功能 (1个方法)

### 5.1 同步外部数据

**方法**: `syncExternalData(String syncType, Long targetId, Map<String, Object> syncData)`

**描述**: 统一数据同步，可实现作者信息、分类信息等同步

**核心功能**: 
- 替代`updateAuthorInfo`、`updateCategoryInfo`等方法
- 统一的外部数据同步接口

**参数**:
- `syncType` (String): 同步类型（AUTHOR、CATEGORY）
- `targetId` (Long): 目标ID（作者ID或分类ID）
- `syncData` (Map<String, Object>): 同步数据

**返回值**: `Result<Integer>` - 返回更新成功的记录数

**调用示例**:
```java
// 同步作者信息
Map<String, Object> authorData = new HashMap<>();
authorData.put("nickname", "新的作家昵称");
authorData.put("avatar", "https://example.com/new-avatar.jpg");

Result<Integer> result1 = contentFacadeService
    .syncExternalData("AUTHOR", 1001L, authorData);

// 同步分类信息
Map<String, Object> categoryData = new HashMap<>();
categoryData.put("categoryName", "新的分类名称");

Result<Integer> result2 = contentFacadeService
    .syncExternalData("CATEGORY", 201L, categoryData);

if (result1.isSuccess()) {
    System.out.println("更新了 " + result1.getData() + " 条记录");
}
```

---

## 🎯 数据模型

### ContentResponse 内容响应对象
```java
@Data
public class ContentResponse {
    private Long id;                    // 内容ID
    private String title;               // 内容标题
    private String description;         // 内容描述
    private String contentType;         // 内容类型
    private String content;             // 内容正文
    private String status;              // 状态
    private Long authorId;              // 作者ID
    private String authorNickname;      // 作者昵称
    private String authorAvatar;        // 作者头像
    private Long categoryId;            // 分类ID
    private String categoryName;        // 分类名称
    private List<String> tags;          // 标签列表
    private String coverImage;          // 封面图片URL
    private Long viewCount;             // 浏览量
    private Long likeCount;             // 点赞数
    private Long commentCount;          // 评论数
    private Long favoriteCount;         // 收藏数
    private Long shareCount;            // 分享数
    private Double score;               // 评分
    private Long scoreCount;            // 评分人数
    private Long wordCount;             // 字数
    private Long chapterCount;          // 章节数
    private Boolean isPaid;             // 是否付费
    private Boolean isPublic;           // 是否公开
    private Boolean allowComment;       // 是否允许评论
    private String reviewStatus;        // 审核状态
    private String reviewComment;       // 审核意见
    private LocalDateTime createTime;   // 创建时间
    private LocalDateTime publishTime;  // 发布时间
    private LocalDateTime updateTime;   // 更新时间
}
```

### PageResponse 分页响应对象
```java
@Data
public class PageResponse<T> {
    private List<T> records;           // 记录列表
    private Long totalCount;           // 总记录数
    private Long totalPage;            // 总页数
    private Integer currentPage;       // 当前页码
    private Integer pageSize;          // 页面大小
    private Boolean hasNext;           // 是否有下一页
    private Boolean hasPrevious;       // 是否有上一页
}
```

## 🚨 错误代码

| 错误码 | 描述 | 解决方案 |
|--------|------|----------|
| INVALID_PARAMETER | 参数验证失败 | 检查请求参数的格式和必填项 |
| CONTENT_NOT_FOUND | 内容不存在 | 检查内容ID是否正确 |
| AUTHOR_NOT_FOUND | 作者不存在 | 检查作者ID是否正确 |
| CATEGORY_NOT_FOUND | 分类不存在 | 检查分类ID是否正确 |
| CONTENT_CREATE_FAILED | 内容创建失败 | 检查数据完整性和权限 |
| CONTENT_UPDATE_FAILED | 内容更新失败 | 确认内容存在且有权限 |
| CONTENT_DELETE_FAILED | 内容删除失败 | 确认内容存在且有权限 |
| INVALID_CONTENT_STATUS | 内容状态无效 | 检查状态值是否正确 |
| INSUFFICIENT_PERMISSION | 权限不足 | 确认操作权限 |
| STATISTICS_UPDATE_FAILED | 统计更新失败 | 检查统计参数 |
| SEARCH_FAILED | 搜索失败 | 检查搜索参数 |
| SYNC_DATA_FAILED | 数据同步失败 | 检查同步参数和数据格式 |

## 🔧 Dubbo配置

### 服务提供者配置
```yaml
dubbo:
  application:
    name: collide-content
  registry:
    address: nacos://localhost:8848
  protocol:
    name: dubbo
    port: 20884
  provider:
    timeout: 5000
    retries: 0
    version: 5.0.0
```

### 服务消费者配置
```yaml
dubbo:
  application:
    name: collide-gateway
  registry:
    address: nacos://localhost:8848
  consumer:
    timeout: 5000
    retries: 0
    version: 5.0.0
```

## 📈 使用示例

### Spring Boot集成
```java
@Service
@Slf4j
public class ContentBizService {
    
    @DubboReference(version = "5.0.0", timeout = 5000)
    private ContentFacadeService contentFacadeService;
    
    public void createNovel(String title, Long authorId) {
        ContentCreateRequest request = new ContentCreateRequest();
        request.setTitle(title);
        request.setContentType("NOVEL");
        request.setAuthorId(authorId);
        request.setCategoryId(201L); // 小说分类
        
        Result<Void> result = contentFacadeService.createContent(request);
        if (result.isSuccess()) {
            log.info("小说创建成功: {}", title);
        } else {
            log.error("小说创建失败: {}, 错误: {}", title, result.getMessage());
        }
    }
    
    public ContentResponse getContent(Long contentId) {
        Result<ContentResponse> result = contentFacadeService.getContentById(contentId, false);
        if (result.isSuccess()) {
            return result.getData();
        }
        throw new BusinessException("内容不存在");
    }
}
```

### 万能查询服务
```java
@Service
public class ContentQueryService {
    
    @DubboReference(version = "5.0.0", timeout = 5000)
    private ContentFacadeService contentFacadeService;
    
    // 获取热门内容
    public PageResponse<ContentResponse> getHotContents(String contentType, Integer days, Integer page, Integer size) {
        Result<PageResponse<ContentResponse>> result = contentFacadeService
            .queryContentsByConditions(null, null, contentType, "PUBLISHED", null, null,
                                     days, "viewCount", "DESC", page, size);
        return result.isSuccess() ? result.getData() : PageResponse.empty();
    }
    
    // 获取作者的内容
    public PageResponse<ContentResponse> getAuthorContents(Long authorId, Integer page, Integer size) {
        Result<PageResponse<ContentResponse>> result = contentFacadeService
            .queryContentsByConditions(authorId, null, null, "PUBLISHED", null, null,
                                     null, "publishTime", "DESC", page, size);
        return result.isSuccess() ? result.getData() : PageResponse.empty();
    }
    
    // 获取高评分内容
    public PageResponse<ContentResponse> getHighScoreContents(Double minScore, Integer page, Integer size) {
        Result<PageResponse<ContentResponse>> result = contentFacadeService
            .queryContentsByConditions(null, null, null, "PUBLISHED", null, minScore,
                                     null, "score", "DESC", page, size);
        return result.isSuccess() ? result.getData() : PageResponse.empty();
    }
}
```

### 异步统计服务
```java
@Service
public class ContentStatsService {
    
    @DubboReference(version = "5.0.0", timeout = 5000)
    private ContentFacadeService contentFacadeService;
    
    @Async
    public CompletableFuture<Void> increaseViewCountAsync(Long contentId) {
        return CompletableFuture.runAsync(() -> {
            contentFacadeService.increaseViewCount(contentId, 1);
        });
    }
    
    @Async
    public CompletableFuture<Void> updateStatsAsync(Long contentId, Long likeCount, Long commentCount) {
        return CompletableFuture.runAsync(() -> {
            contentFacadeService.updateContentStats(contentId, null, likeCount, commentCount, null, null);
        });
    }
}
```

### 缓存集成示例
```java
@Service
public class CachedContentService {
    
    @DubboReference(version = "5.0.0", timeout = 5000)
    private ContentFacadeService contentFacadeService;
    
    @Cacheable(value = "content", key = "#contentId")
    public ContentResponse getCachedContent(Long contentId) {
        Result<ContentResponse> result = contentFacadeService.getContentById(contentId, false);
        return result.isSuccess() ? result.getData() : null;
    }
    
    @CacheEvict(value = "content", key = "#contentId")
    public void evictContentCache(Long contentId) {
        // 缓存失效
    }
    
    @Cacheable(value = "hot_contents", key = "#contentType + '_' + #days")
    public List<ContentResponse> getHotContentsCached(String contentType, Integer days) {
        Result<PageResponse<ContentResponse>> result = contentFacadeService
            .queryContentsByConditions(null, null, contentType, "PUBLISHED", null, null,
                                     days, "viewCount", "DESC", 1, 20);
        return result.isSuccess() ? result.getData().getRecords() : Collections.emptyList();
    }
}
```

## 🔧 性能优化建议

1. **缓存策略**: 
   - 热点内容: TTL 30分钟
   - 内容详情: TTL 10分钟
   - 推荐列表: TTL 5分钟

2. **查询优化**: 
   - 使用万能查询减少接口调用次数
   - 合理使用分页避免大结果集
   - 统计操作异步执行

3. **连接池配置**:
   ```yaml
   dubbo:
     consumer:
       connections: 10  # 每个服务提供者的连接数
       actives: 200     # 每个连接的最大活跃请求数
       timeout: 5000    # 合理超时时间
   ```

4. **接口使用建议**:
   - 优先使用万能查询方法
   - 批量操作替代循环调用
   - 统计更新使用异步方式

## 🚀 极简设计优势

1. **方法精简**: 从26个方法缩减到12个，学习成本降低55%
2. **万能查询**: 一个方法替代6个具体查询方法
3. **统一管理**: 状态和统计的集中管理
4. **高性能**: 优化的查询和批量操作
5. **易维护**: 减少接口变更，提升稳定性

## 🔗 相关文档

- [ContentController REST API 文档](../news/content-controller-api.md)
- [ContentChapterFacadeService 文档](./content-chapter-facade-service-api.md)
- [ContentPurchaseFacadeService 文档](./content-purchase-facade-service-api.md)
- [ContentPaymentFacadeService 文档](./content-payment-facade-service-api.md)

---

**联系信息**:  
- Facade服务: ContentFacadeService  
- 版本: 2.0.0 (极简版)  
- Dubbo版本: 5.0.0  
- 维护: GIG团队  
- 更新: 2024-01-01