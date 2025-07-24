package com.gig.collide.business.domain.search;

import com.gig.collide.api.search.response.data.SuggestionItem;

import java.util.List;

/**
 * 搜索记录仓储接口
 * 
 * @author GIG Team
 */
public interface SearchRecordRepository {

    /**
     * 记录搜索行为
     * 
     * @param keyword 搜索关键词
     * @param userId 用户ID
     * @param resultCount 搜索结果数量
     */
    void recordSearch(String keyword, Long userId, Long resultCount);

    /**
     * 获取关键词建议
     * 
     * @param keyword 关键词前缀
     * @param limit 建议数量
     * @return 关键词建议列表
     */
    List<SuggestionItem> getKeywordSuggestions(String keyword, Integer limit);

    /**
     * 获取热门搜索关键词
     * 
     * @param limit 数量限制
     * @return 热门关键词列表
     */
    List<String> getHotKeywords(Integer limit);
} 