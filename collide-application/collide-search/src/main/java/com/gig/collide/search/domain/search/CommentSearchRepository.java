package com.gig.collide.search.domain.search;

import com.gig.collide.api.search.response.data.SearchResult;

import java.util.List;

/**
 * 评论搜索仓储接口
 * 
 * @author GIG Team
 */
public interface CommentSearchRepository {

    /**
     * 搜索评论
     * 
     * @param keyword 关键词
     * @param timeRange 时间范围（天数）
     * @param pageNum 页码
     * @param pageSize 页大小
     * @param highlight 是否高亮
     * @return 搜索结果列表
     */
    List<SearchResult> searchComments(String keyword, Integer timeRange, 
                                     Integer pageNum, Integer pageSize, Boolean highlight);

    /**
     * 统计评论搜索结果数量
     * 
     * @param keyword 关键词
     * @param timeRange 时间范围（天数）
     * @return 结果数量
     */
    long countComments(String keyword, Integer timeRange);
} 