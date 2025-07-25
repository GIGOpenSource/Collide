package com.gig.collide.api.search.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// import javax.validation.constraints.Max;
// import javax.validation.constraints.Min;
// import javax.validation.constraints.NotBlank;
// import javax.validation.constraints.Size;

/**
 * 搜索请求DTO
 *
 * @author GIG Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "搜索请求")
public class SearchRequest {

    @Schema(description = "搜索关键词", example = "Java编程", required = true)
    private String keyword;

    @Schema(description = "搜索类型：ALL-综合搜索, USER-用户搜索, CONTENT-内容搜索, COMMENT-评论搜索", 
            example = "ALL", 
            allowableValues = {"ALL", "USER", "CONTENT", "COMMENT"})
    @Builder.Default
    private String searchType = "ALL";

    @Schema(description = "内容类型过滤：NOVEL-小说, COMIC-漫画, SHORT_VIDEO-短视频, LONG_VIDEO-长视频", 
            example = "NOVEL",
            allowableValues = {"NOVEL", "COMIC", "SHORT_VIDEO", "LONG_VIDEO"})
    private String contentType;

    @Schema(description = "排序方式：RELEVANCE-相关度, TIME-时间, POPULARITY-热度", 
            example = "RELEVANCE", 
            allowableValues = {"RELEVANCE", "TIME", "POPULARITY"})
    @Builder.Default
    private String sortBy = "RELEVANCE";

    @Schema(description = "页码", example = "1")
    @Builder.Default
    private Integer pageNum = 1;

    @Schema(description = "每页大小", example = "10")
    @Builder.Default
    private Integer pageSize = 10;

    @Schema(description = "是否高亮显示", example = "true")
    @Builder.Default
    private Boolean highlight = true;

    @Schema(description = "搜索时间范围（天数），0表示不限制", example = "30")
    @Builder.Default
    private Integer timeRange = 0;

    @Schema(description = "最小点赞数过滤", example = "0")
    @Builder.Default
    private Integer minLikeCount = 0;

    @Schema(description = "是否只搜索已发布的内容", example = "true")
    @Builder.Default
    private Boolean onlyPublished = true;

    @Schema(description = "分类ID列表，用于分类筛选", example = "[1, 2, 3]")
    private java.util.List<Long> categoryIds;

    @Schema(description = "标签ID列表，用于标签筛选", example = "[10, 20, 30]")
    private java.util.List<Long> tagIds;

    @Schema(description = "标签类型过滤：content-内容标签, interest-兴趣标签", 
            example = "content", 
            allowableValues = {"content", "interest", "system"})
    private String tagType;

    @Schema(description = "用户ID，用于个性化推荐搜索")
    private Long userId;

    @Schema(description = "是否基于用户兴趣筛选", example = "false")
    @Builder.Default
    private Boolean useUserInterest = false;

    @Schema(description = "热门内容筛选，按热度分数排序", example = "false")
    @Builder.Default
    private Boolean hotContent = false;
} 