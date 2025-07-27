package com.gig.collide.search.domain.repository;

import com.gig.collide.api.search.response.data.SearchResultItem;
import com.gig.collide.api.search.response.data.SuggestionItem;

import java.util.List;

/**
 * 内容搜索仓储接口
 * 基于去连表设计，避免JOIN操作
 *
 * @author Collide Team
 * @version 1.0
 */
public interface ContentSearchRepository {

    /**
     * 搜索内容
     * 基于标题、内容等字段进行搜索
     *
     * @param keyword 搜索关键词
     * @param offset  偏移量
     * @param limit   限制数量
     * @return 内容搜索结果列表
     */
    List<SearchResultItem> searchContent(String keyword, Integer offset, Integer limit);

    /**
     * 统计内容搜索结果数量
     *
     * @param keyword 搜索关键词
     * @return 搜索结果总数
     */
    long countContent(String keyword);

    /**
     * 获取标签建议
     * 用于搜索提示功能
     *
     * @param keyword 关键词前缀
     * @param limit   限制数量
     * @return 标签建议列表
     */
    List<SuggestionItem> getTagSuggestions(String keyword, Integer limit);
} 