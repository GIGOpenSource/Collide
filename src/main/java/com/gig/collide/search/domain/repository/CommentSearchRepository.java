package com.gig.collide.search.domain.repository;

import com.gig.collide.api.search.response.data.SearchResultItem;

import java.util.List;

/**
 * 评论搜索仓储接口
 * 基于去连表设计，避免JOIN操作
 *
 * @author Collide Team
 * @version 1.0
 */
public interface CommentSearchRepository {

    /**
     * 搜索评论
     * 基于评论内容进行搜索
     *
     * @param keyword 搜索关键词
     * @param offset  偏移量
     * @param limit   限制数量
     * @return 评论搜索结果列表
     */
    List<SearchResultItem> searchComments(String keyword, Integer offset, Integer limit);

    /**
     * 统计评论搜索结果数量
     *
     * @param keyword 搜索关键词
     * @return 搜索结果总数
     */
    long countComments(String keyword);
} 