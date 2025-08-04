# Content Facade Service API 文档

**Facade服务**: ContentFacadeService  
**版本**: 2.0.0 (简洁版)  
**Dubbo版本**: 5.0.0  
**超时时间**: 5000ms  
**服务路径**: `com.gig.collide.api.content.ContentFacadeService`  
**方法数量**: 26个  
**更新时间**: 2024-01-01  

## 🚀 概述

内容管理Facade服务提供内容生命周期管理的RPC接口。采用Dubbo框架，支持高并发分布式调用，为内容的创建、发布、审核、统计等提供标准化的服务接口。

**核心职责**:
- **内容管理**: 创建、更新、删除、发布、下线内容
- **章节管理**: 多章节内容的创建和管理
- **统计服务**: 浏览量、点赞、评论、收藏等统计
- **查询服务**: 多维度内容查询和搜索
- **数据同步**: 作者、分类信息同步和审核

## 📋 接口分类

| 分类 | 方法数量 | 功能描述 |
|------|----------|----------|
| **内容管理** | 7个 | 内容的CRUD和状态管理 |
| **章节管理** | 4个 | 章节的创建和查询 |
| **统计管理** | 6个 | 各类统计数据更新 |
| **内容查询** | 6个 | 多维度内容查询 |
| **数据同步** | 3个 | 信息同步和审核 |

---

## 🔧 1. 内容管理 (7个方法)

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

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": null,
  "timestamp": 1704067200000
}
```

**错误处理**:
- `INVALID_PARAMETER`: 参数验证失败
- `AUTHOR_NOT_FOUND`: 作者不存在
- `CATEGORY_NOT_FOUND`: 分类不存在
- `CONTENT_CREATE_FAILED`: 内容创建失败

**调用示例**:
```java
ContentCreateRequest request = new ContentCreateRequest();
request.setTitle("我的玄幻小说");
request.setContentType("NOVEL");
request.setAuthorId(1001L);
request.setCategoryId(201L);
request.setDescription("这是一部精彩的玄幻小说");

Result<Void> result = contentFacadeService.createContent(request);
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

**方法**: `deleteContent(Long contentId, Long operatorId)`

**描述**: 逻辑删除指定内容

**参数**:
- `contentId` (Long): 内容ID
- `operatorId` (Long): 操作人ID

**返回值**: `Result<Void>`

### 1.4 根据ID获取内容详情

**方法**: `getContentById(Long contentId, Boolean includeOffline)`

**描述**: 根据内容ID获取内容详细信息

**参数**:
- `contentId` (Long): 内容ID
- `includeOffline` (Boolean): 是否包含下线内容

**返回值**: `Result<ContentResponse>`

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

### 1.5 分页查询内容

**方法**: `queryContents(ContentQueryRequest request)`

**描述**: 根据条件分页查询内容

**参数**:
```java
@Data
public class ContentQueryRequest {
    private String title;              // 标题关键词
    private String contentType;        // 内容类型
    private String status;             // 内容状态
    private Long authorId;             // 作者ID
    private Long categoryId;           // 分类ID
    private List<String> tags;         // 标签列表
    private String keyword;            // 搜索关键词
    private Double minScore;           // 最低评分
    private Double maxScore;           // 最高评分
    private String orderBy;            // 排序字段：viewCount/likeCount/score/createTime
    private String orderDirection;     // 排序方向：ASC/DESC
    private Integer currentPage;       // 当前页码
    private Integer pageSize;          // 页面大小
}
```

**返回值**: `Result<PageResponse<ContentResponse>>`

### 1.6 发布内容

**方法**: `publishContent(Long contentId, Long authorId)`

**描述**: 发布指定内容，将状态从DRAFT更新为PUBLISHED

**参数**:
- `contentId` (Long): 内容ID
- `authorId` (Long): 作者ID

**返回值**: `Result<ContentResponse>`

### 1.7 下线内容

**方法**: `offlineContent(Long contentId, Long operatorId)`

**描述**: 下线指定内容，将状态更新为OFFLINE

**参数**:
- `contentId` (Long): 内容ID
- `operatorId` (Long): 操作人ID

**返回值**: `Result<Void>`

---

## 📚 2. 章节管理 (4个方法)

### 2.1 创建章节

**方法**: `createChapter(ChapterCreateRequest request)`

**描述**: 为内容创建新章节，用于小说、漫画等多章节内容

**参数**:
```java
@Data
public class ChapterCreateRequest {
    private Long contentId;            // 内容ID（必填）
    private Integer chapterNum;        // 章节号（必填）
    private String title;              // 章节标题（必填）
    private String content;            // 章节内容
    private Integer wordCount;         // 字数
    private Long authorId;             // 作者ID（必填）
    private Boolean isFree;            // 是否免费，默认true
    private String summary;            // 章节简介
}
```

**返回值**: `Result<Void>`

**调用示例**:
```java
ChapterCreateRequest request = new ChapterCreateRequest();
request.setContentId(12345L);
request.setChapterNum(1);
request.setTitle("第一章 开始");
request.setContent("章节内容正文...");
request.setWordCount(2000);
request.setAuthorId(1001L);
request.setIsFree(true);

Result<Void> result = contentFacadeService.createChapter(request);
```

### 2.2 获取内容的章节列表

**方法**: `getContentChapters(Long contentId, String status, Integer currentPage, Integer pageSize)`

**描述**: 分页获取指定内容的章节列表

**参数**:
- `contentId` (Long): 内容ID
- `status` (String): 章节状态 (DRAFT/PUBLISHED/DELETED，可选)
- `currentPage` (Integer): 页码
- `pageSize` (Integer): 页面大小

**返回值**: `Result<PageResponse<ChapterResponse>>`

### 2.3 获取章节详情

**方法**: `getChapterById(Long chapterId)`

**描述**: 根据章节ID获取章节详细信息

**参数**:
- `chapterId` (Long): 章节ID

**返回值**: `Result<ChapterResponse>`

### 2.4 发布章节

**方法**: `publishChapter(Long chapterId, Long authorId)`

**描述**: 发布指定章节

**参数**:
- `chapterId` (Long): 章节ID
- `authorId` (Long): 作者ID

**返回值**: `Result<ChapterResponse>`

---

## 📊 3. 统计管理 (6个方法)

### 3.1 增加浏览量

**方法**: `increaseViewCount(Long contentId, Integer increment)`

**描述**: 增加内容的浏览量

**参数**:
- `contentId` (Long): 内容ID
- `increment` (Integer): 增量

**返回值**: `Result<Long>` - 返回更新后的浏览量

**调用示例**:
```java
Result<Long> result = contentFacadeService.increaseViewCount(12345L, 1);
// result.getData() 返回新的浏览量，如: 10001
```

### 3.2 增加点赞数

**方法**: `increaseLikeCount(Long contentId, Integer increment)`

**描述**: 增加内容的点赞数

**参数**:
- `contentId` (Long): 内容ID
- `increment` (Integer): 增量

**返回值**: `Result<Long>` - 返回更新后的点赞数

### 3.3 增加评论数

**方法**: `increaseCommentCount(Long contentId, Integer increment)`

**描述**: 增加内容的评论数

**参数**:
- `contentId` (Long): 内容ID
- `increment` (Integer): 增量

**返回值**: `Result<Long>` - 返回更新后的评论数

### 3.4 增加收藏数

**方法**: `increaseFavoriteCount(Long contentId, Integer increment)`

**描述**: 增加内容的收藏数

**参数**:
- `contentId` (Long): 内容ID
- `increment` (Integer): 增量

**返回值**: `Result<Long>` - 返回更新后的收藏数

### 3.5 更新评分

**方法**: `updateScore(Long contentId, Integer score)`

**描述**: 新增评分时调用，更新评分总数和评分数量

**参数**:
- `contentId` (Long): 内容ID
- `score` (Integer): 评分值（1-10）

**返回值**: `Result<Double>` - 返回更新后的平均评分

**调用示例**:
```java
// 用户给内容打8分
Result<Double> result = contentFacadeService.updateScore(12345L, 8);
// result.getData() 返回新的平均评分，如: 8.6
```

### 3.6 获取内容统计信息

**方法**: `getContentStatistics(Long contentId)`

**描述**: 获取内容的统计信息

**参数**:
- `contentId` (Long): 内容ID

**返回值**: `Result<Map<String, Object>>` - 返回统计信息Map

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

## 🔍 4. 内容查询 (6个方法)

### 4.1 根据作者查询内容

**方法**: `getContentsByAuthor(Long authorId, String contentType, String status, Integer currentPage, Integer pageSize)`

**描述**: 分页查询指定作者的内容

**参数**:
- `authorId` (Long): 作者ID
- `contentType` (String): 内容类型（可选）
- `status` (String): 状态（可选）
- `currentPage` (Integer): 页码
- `pageSize` (Integer): 页面大小

**返回值**: `Result<PageResponse<ContentResponse>>`

### 4.2 根据分类查询内容

**方法**: `getContentsByCategory(Long categoryId, String contentType, Integer currentPage, Integer pageSize)`

**描述**: 分页查询指定分类的内容

**参数**:
- `categoryId` (Long): 分类ID
- `contentType` (String): 内容类型（可选）
- `currentPage` (Integer): 页码
- `pageSize` (Integer): 页面大小

**返回值**: `Result<PageResponse<ContentResponse>>`

### 4.3 搜索内容

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
Result<PageResponse<ContentResponse>> result = contentFacadeService.searchContents(
    "玄幻", "NOVEL", 1, 20
);
```

### 4.4 获取热门内容

**方法**: `getPopularContents(String contentType, Integer timeRange, Integer currentPage, Integer pageSize)`

**描述**: 根据浏览量、点赞数等综合排序

**参数**:
- `contentType` (String): 内容类型（可选）
- `timeRange` (Integer): 时间范围（天，可选）
- `currentPage` (Integer): 页码
- `pageSize` (Integer): 页面大小

**返回值**: `Result<PageResponse<ContentResponse>>`

### 4.5 获取最新内容

**方法**: `getLatestContents(String contentType, Integer currentPage, Integer pageSize)`

**描述**: 按发布时间排序

**参数**:
- `contentType` (String): 内容类型（可选）
- `currentPage` (Integer): 页码
- `pageSize` (Integer): 页面大小

**返回值**: `Result<PageResponse<ContentResponse>>`

---

## 🔄 5. 数据同步 (3个方法)

### 5.1 更新作者信息

**方法**: `updateAuthorInfo(Long authorId, String nickname, String avatar)`

**描述**: 当作者信息变更时，同步更新内容表中的冗余信息

**参数**:
- `authorId` (Long): 作者ID
- `nickname` (String): 新昵称
- `avatar` (String): 新头像URL（可选）

**返回值**: `Result<Integer>` - 返回更新成功的记录数

**调用示例**:
```java
Result<Integer> result = contentFacadeService.updateAuthorInfo(
    1001L, "新的作家昵称", "https://example.com/new-avatar.jpg"
);
// result.getData() 返回更新的内容记录数，如: 15
```

### 5.2 更新分类信息

**方法**: `updateCategoryInfo(Long categoryId, String categoryName)`

**描述**: 当分类信息变更时，同步更新内容表中的冗余信息

**参数**:
- `categoryId` (Long): 分类ID
- `categoryName` (String): 新分类名称

**返回值**: `Result<Integer>` - 返回更新成功的记录数

### 5.3 内容审核

**方法**: `reviewContent(Long contentId, String reviewStatus, Long reviewerId, String reviewComment)`

**描述**: 更新审核状态

**参数**:
- `contentId` (Long): 内容ID
- `reviewStatus` (String): 审核状态：APPROVED、REJECTED
- `reviewerId` (Long): 审核人ID
- `reviewComment` (String): 审核意见（可选）

**返回值**: `Result<ContentResponse>` - 返回审核结果

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

### ChapterResponse 章节响应对象
```java
@Data
public class ChapterResponse {
    private Long id;                    // 章节ID
    private Long contentId;             // 内容ID
    private Integer chapterNum;         // 章节号
    private String title;               // 章节标题
    private String content;             // 章节内容
    private String summary;             // 章节简介
    private Integer wordCount;          // 字数
    private String status;              // 状态
    private Boolean isFree;             // 是否免费
    private Long viewCount;             // 浏览量
    private Long likeCount;             // 点赞数
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
| CHAPTER_NOT_FOUND | 章节不存在 | 检查章节ID是否正确 |
| CONTENT_CREATE_FAILED | 内容创建失败 | 检查数据完整性和权限 |
| CONTENT_UPDATE_FAILED | 内容更新失败 | 确认内容存在且有权限 |
| CONTENT_DELETE_FAILED | 内容删除失败 | 确认内容存在且有权限 |
| CHAPTER_CREATE_FAILED | 章节创建失败 | 检查章节数据和权限 |
| INVALID_CONTENT_STATUS | 内容状态无效 | 检查状态值是否正确 |
| INSUFFICIENT_PERMISSION | 权限不足 | 确认操作权限 |
| DUPLICATE_CHAPTER_NUM | 章节号重复 | 使用不同的章节号 |
| CONTENT_ALREADY_PUBLISHED | 内容已发布 | 不能重复发布 |
| SCORE_OUT_OF_RANGE | 评分超出范围 | 评分值应在1-10之间 |
| STATISTICS_UPDATE_FAILED | 统计更新失败 | 检查统计参数 |

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

### 异步调用示例
```java
@Service
public class AsyncContentService {
    
    @DubboReference(version = "5.0.0", timeout = 5000)
    private ContentFacadeService contentFacadeService;
    
    @Async
    public CompletableFuture<Void> increaseViewCountAsync(Long contentId) {
        return CompletableFuture.runAsync(() -> {
            contentFacadeService.increaseViewCount(contentId, 1);
        });
    }
    
    @Async
    public CompletableFuture<PageResponse<ContentResponse>> searchContentsAsync(String keyword) {
        return CompletableFuture.supplyAsync(() -> {
            Result<PageResponse<ContentResponse>> result = 
                contentFacadeService.searchContents(keyword, null, 1, 20);
            return result.getData();
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
        return result.getData();
    }
    
    @CacheEvict(value = "content", key = "#contentId")
    public void evictContentCache(Long contentId) {
        // 缓存失效
    }
}
```

## 🔧 性能优化建议

1. **连接池配置**: 调整Dubbo连接池大小，建议设置为CPU核心数的2倍
2. **超时设置**: 根据业务复杂度调整超时时间，查询类接口3-5秒，更新类接口5-10秒
3. **序列化优化**: 使用高效的序列化方式，如Kryo或FastJson
4. **缓存策略**: 热点内容建议使用本地缓存+分布式缓存双层策略
5. **异步处理**: 统计类操作建议使用异步处理，避免阻塞主流程

## 🔗 相关文档

- [ContentController REST API 文档](./content-controller-api.md)
- [ContentChapterFacadeService 文档](./content-chapter-facade-service-api.md)
- [内容数据模型](../models/content-model.md)
- [Dubbo服务配置](../config/dubbo-config.md)

---

**联系信息**:  
- Facade服务: ContentFacadeService  
- 版本: 2.0.0 (简洁版)  
- Dubbo版本: 5.0.0  
- 维护: GIG团队  
- 更新: 2024-01-01