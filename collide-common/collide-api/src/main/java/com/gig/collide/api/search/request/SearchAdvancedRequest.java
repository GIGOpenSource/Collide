package com.gig.collide.api.search.request;

import com.gig.collide.api.search.constant.ContentTypeEnum;
import com.gig.collide.api.search.constant.SearchTypeEnum;
import com.gig.collide.api.search.constant.SortTypeEnum;
import com.gig.collide.api.search.request.condition.SearchQueryCondition;
import com.gig.collide.base.request.BaseRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 高级搜索请求
 *
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class SearchAdvancedRequest extends BaseRequest {

    /**
     * 搜索关键词列表
     */
    private List<String> keywords;

    /**
     * 必须包含的关键词
     */
    private List<String> mustKeywords;

    /**
     * 必须不包含的关键词
     */
    private List<String> mustNotKeywords;

    /**
     * 可选关键词
     */
    private List<String> shouldKeywords;

    /**
     * 搜索类型
     */
    private SearchTypeEnum searchType = SearchTypeEnum.ALL;

    /**
     * 内容类型过滤
     */
    private List<ContentTypeEnum> contentTypes;

    /**
     * 排序类型
     */
    private SortTypeEnum sortType = SortTypeEnum.RELEVANCE;

    /**
     * 页码（从1开始）
     */
    @Min(value = 1, message = "页码必须大于0")
    private Integer pageNum = 1;

    /**
     * 每页大小
     */
    @Min(value = 1, message = "每页大小必须大于0")
    @Max(value = 100, message = "每页大小不能超过100")
    private Integer pageSize = 20;

    /**
     * 搜索用户ID
     */
    private Long userId;

    /**
     * 作者ID列表
     */
    private List<Long> authorIds;

    /**
     * 分类ID列表
     */
    private List<Long> categoryIds;

    /**
     * 标签列表
     */
    private List<String> tags;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;

    /**
     * 最小点赞数
     */
    private Long minLikeCount;

    /**
     * 最大点赞数
     */
    private Long maxLikeCount;

    /**
     * 最小浏览数
     */
    private Long minViewCount;

    /**
     * 最大浏览数
     */
    private Long maxViewCount;

    /**
     * 最小评论数
     */
    private Long minCommentCount;

    /**
     * 最大评论数
     */
    private Long maxCommentCount;

    /**
     * 质量评分范围
     */
    private Double minQualityScore;
    private Double maxQualityScore;

    /**
     * 高级查询条件
     */
    @Valid
    private List<SearchQueryCondition> queryConditions;

    /**
     * 自定义过滤条件
     */
    private Map<String, Object> customFilters;

    /**
     * 是否高亮显示
     */
    private Boolean highlight = true;

    /**
     * 高亮标签
     */
    private String highlightTag = "em";

    /**
     * 搜索字段权重
     */
    private Map<String, Double> fieldWeights;

    /**
     * 是否启用模糊搜索
     */
    private Boolean fuzzySearch = false;

    /**
     * 模糊搜索相似度阈值
     */
    private Double fuzziness = 0.8;

    /**
     * 是否记录搜索历史
     */
    private Boolean recordHistory = true;

    /**
     * 设备信息
     */
    private String deviceInfo;

    /**
     * IP地址
     */
    private String ipAddress;

    // ===================== 便捷方法 =====================

    /**
     * 设置分页信息
     */
    public SearchAdvancedRequest withPagination(Integer pageNum, Integer pageSize) {
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        return this;
    }

    /**
     * 设置时间范围
     */
    public SearchAdvancedRequest withTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
        return this;
    }

    /**
     * 设置统计范围
     */
    public SearchAdvancedRequest withStatRange(Long minLike, Long maxLike, Long minView, Long maxView) {
        this.minLikeCount = minLike;
        this.maxLikeCount = maxLike;
        this.minViewCount = minView;
        this.maxViewCount = maxView;
        return this;
    }

    /**
     * 检查是否有有效的搜索条件
     */
    public boolean hasValidSearchConditions() {
        return (keywords != null && !keywords.isEmpty()) ||
               (mustKeywords != null && !mustKeywords.isEmpty()) ||
               (shouldKeywords != null && !shouldKeywords.isEmpty()) ||
               (queryConditions != null && !queryConditions.isEmpty());
    }

    /**
     * 获取搜索偏移量
     */
    public Integer getOffset() {
        if (pageNum == null || pageSize == null || pageNum <= 0 || pageSize <= 0) {
            return 0;
        }
        return (pageNum - 1) * pageSize;
    }
} 