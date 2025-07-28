package com.gig.collide.api.content.request;

import com.gig.collide.api.content.enums.ContentStatusEnum;
import com.gig.collide.api.content.enums.ContentTypeEnum;
import com.gig.collide.base.request.BaseRequest;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 内容统一查询请求
 * 支持多种查询条件和排序方式
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ContentUnifiedQueryRequest extends BaseRequest {

    /**
     * 内容ID
     */
    private Long contentId;

    /**
     * 内容ID列表（批量查询）
     */
    private List<Long> contentIds;

    /**
     * 标题关键词
     */
    private String titleKeyword;

    /**
     * 内容类型
     */
    private ContentTypeEnum contentType;

    /**
     * 内容类型列表
     */
    private List<ContentTypeEnum> contentTypes;

    /**
     * 内容状态
     */
    private ContentStatusEnum status;

    /**
     * 内容状态列表
     */
    private List<ContentStatusEnum> statuses;

    /**
     * 作者ID
     */
    private Long authorId;

    /**
     * 作者ID列表
     */
    private List<Long> authorIds;

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 分类ID列表
     */
    private List<Long> categoryIds;

    /**
     * 标签列表（包含任一标签）
     */
    private List<String> tags;

    /**
     * 必须包含的所有标签
     */
    private List<String> requiredTags;

    /**
     * 是否推荐内容
     */
    private Boolean isRecommended;

    /**
     * 是否置顶内容
     */
    private Boolean isPinned;

    /**
     * 最小浏览数
     */
    private Long minViewCount;

    /**
     * 最小点赞数
     */
    private Long minLikeCount;

    /**
     * 创建时间范围 - 开始
     */
    private LocalDateTime createTimeStart;

    /**
     * 创建时间范围 - 结束
     */
    private LocalDateTime createTimeEnd;

    /**
     * 发布时间范围 - 开始
     */
    private LocalDateTime publishTimeStart;

    /**
     * 发布时间范围 - 结束
     */
    private LocalDateTime publishTimeEnd;

    /**
     * 排序字段
     * 可选值：create_time, published_time, update_time, view_count, like_count, favorite_count, weight_score
     */
    private String sortBy;

    /**
     * 排序方向（ASC/DESC）
     */
    private String sortDirection;

    /**
     * 是否包含统计信息
     */
    private Boolean includeStatistics;

    /**
     * 是否包含用户交互信息（点赞、收藏状态等）
     */
    private Boolean includeUserInteraction;

    /**
     * 当前用户ID（用于查询用户交互信息）
     */
    private Long currentUserId;

    // ===================== 便捷构造器 =====================

    /**
     * 根据内容ID查询
     */
    public ContentUnifiedQueryRequest(Long contentId) {
        this.contentId = contentId;
    }

    /**
     * 根据作者ID查询
     */
    public static ContentUnifiedQueryRequest byAuthor(Long authorId) {
        ContentUnifiedQueryRequest request = new ContentUnifiedQueryRequest();
        request.setAuthorId(authorId);
        return request;
    }

    /**
     * 根据分类ID查询
     */
    public static ContentUnifiedQueryRequest byCategory(Long categoryId) {
        ContentUnifiedQueryRequest request = new ContentUnifiedQueryRequest();
        request.setCategoryId(categoryId);
        return request;
    }

    /**
     * 根据内容类型查询
     */
    public static ContentUnifiedQueryRequest byType(ContentTypeEnum contentType) {
        ContentUnifiedQueryRequest request = new ContentUnifiedQueryRequest();
        request.setContentType(contentType);
        return request;
    }

    /**
     * 查询推荐内容
     */
    public static ContentUnifiedQueryRequest recommended() {
        ContentUnifiedQueryRequest request = new ContentUnifiedQueryRequest();
        request.setIsRecommended(true);
        request.setStatus(ContentStatusEnum.PUBLISHED);
        return request;
    }

    /**
     * 查询热门内容
     */
    public static ContentUnifiedQueryRequest hot() {
        ContentUnifiedQueryRequest request = new ContentUnifiedQueryRequest();
        request.setStatus(ContentStatusEnum.PUBLISHED);
        request.setSortBy("like_count");
        request.setSortDirection("DESC");
        return request;
    }

    /**
     * 查询最新内容
     */
    public static ContentUnifiedQueryRequest latest() {
        ContentUnifiedQueryRequest request = new ContentUnifiedQueryRequest();
        request.setStatus(ContentStatusEnum.PUBLISHED);
        request.setSortBy("published_time");
        request.setSortDirection("DESC");
        return request;
    }
} 