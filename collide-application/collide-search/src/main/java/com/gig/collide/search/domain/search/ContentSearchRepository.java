package com.gig.collide.search.domain.search;

import com.gig.collide.api.search.response.data.SearchResult;
import com.gig.collide.api.search.response.data.SuggestionItem;

import java.util.List;

/**
 * 内容搜索仓储接口
 * 
 * @author GIG Team
 */
public interface ContentSearchRepository {

    /**
     * 搜索内容
     * 
     * @param keyword 关键词
     * @param contentType 内容类型
     * @param onlyPublished 是否只搜索已发布内容
     * @param timeRange 时间范围（天数）
     * @param minLikeCount 最小点赞数
     * @param pageNum 页码
     * @param pageSize 页大小
     * @param highlight 是否高亮
     * @return 搜索结果列表
     */
    List<SearchResult> searchContent(String keyword, String contentType, Boolean onlyPublished, 
                                    Integer timeRange, Integer minLikeCount, 
                                    Integer pageNum, Integer pageSize, Boolean highlight);

    /**
     * 增强的内容搜索（支持分类和标签筛选）
     * 
     * @param keyword 关键词
     * @param contentType 内容类型
     * @param onlyPublished 是否只搜索已发布内容
     * @param timeRange 时间范围（天数）
     * @param minLikeCount 最小点赞数
     * @param categoryIds 分类ID列表
     * @param tagIds 标签ID列表
     * @param tagType 标签类型
     * @param userId 用户ID（用于个性化推荐）
     * @param useUserInterest 是否基于用户兴趣筛选
     * @param hotContent 是否按热度排序
     * @param pageNum 页码
     * @param pageSize 页大小
     * @param highlight 是否高亮
     * @return 搜索结果列表
     */
    List<SearchResult> searchContentEnhanced(String keyword, String contentType, Boolean onlyPublished, 
                                            Integer timeRange, Integer minLikeCount,
                                            List<Long> categoryIds, List<Long> tagIds, String tagType,
                                            Long userId, Boolean useUserInterest, Boolean hotContent,
                                            Integer pageNum, Integer pageSize, Boolean highlight);

    /**
     * 统计内容搜索结果数量
     * 
     * @param keyword 关键词
     * @param contentType 内容类型
     * @param onlyPublished 是否只搜索已发布内容
     * @param timeRange 时间范围（天数）
     * @param minLikeCount 最小点赞数
     * @return 结果数量
     */
    long countContent(String keyword, String contentType, Boolean onlyPublished, 
                     Integer timeRange, Integer minLikeCount);

    /**
     * 统计增强搜索结果数量（支持分类和标签筛选）
     * 
     * @param keyword 关键词
     * @param contentType 内容类型
     * @param onlyPublished 是否只搜索已发布内容
     * @param timeRange 时间范围（天数）
     * @param minLikeCount 最小点赞数
     * @param categoryIds 分类ID列表
     * @param tagIds 标签ID列表
     * @param tagType 标签类型
     * @param userId 用户ID（用于个性化推荐）
     * @param useUserInterest 是否基于用户兴趣筛选
     * @return 结果数量
     */
    long countContentEnhanced(String keyword, String contentType, Boolean onlyPublished, 
                             Integer timeRange, Integer minLikeCount,
                             List<Long> categoryIds, List<Long> tagIds, String tagType,
                             Long userId, Boolean useUserInterest);

    /**
     * 获取标签建议
     * 
     * @param keyword 关键词前缀
     * @param limit 建议数量
     * @return 标签建议列表
     */
    List<SuggestionItem> getTagSuggestions(String keyword, Integer limit);
} 