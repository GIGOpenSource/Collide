package com.gig.collide.search.domain.repository;

import com.gig.collide.api.search.response.data.SuggestionItem;

import java.util.List;

/**
 * 搜索记录仓储接口
 * 用于记录搜索行为和提供搜索建议
 * 支持幂等性操作
 *
 * @author Collide Team
 * @version 1.0
 */
public interface SearchRecordRepository {

    /**
     * 记录搜索行为
     * 支持幂等性，防止重复记录
     *
     * @param keyword     搜索关键词
     * @param userId      用户ID（可为空）
     * @param resultCount 搜索结果数量
     */
    void recordSearch(String keyword, Long userId, Long resultCount);

    /**
     * 获取关键词建议
     * 基于历史搜索记录
     *
     * @param keyword 关键词前缀
     * @param limit   限制数量
     * @return 关键词建议列表
     */
    List<SuggestionItem> getKeywordSuggestions(String keyword, Integer limit);

    /**
     * 获取热门关键词
     * 基于搜索频次统计
     *
     * @param limit 限制数量
     * @return 热门关键词列表
     */
    List<String> getHotKeywords(Integer limit);
} 