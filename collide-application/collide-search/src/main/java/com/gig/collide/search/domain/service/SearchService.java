package com.gig.collide.search.domain.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.gig.collide.search.domain.entity.SearchHistory;
import com.gig.collide.search.domain.entity.HotSearch;

import java.util.List;

/**
 * 搜索服务接口 - 简洁版
 *
 * @author GIG Team
 * @version 2.0.0
 */
public interface SearchService {

    /**
     * 执行搜索并记录历史
     */
    List<Object> search(String keyword, String searchType, Long userId, 
                       Integer pageNum, Integer pageSize, String sortBy);

    /**
     * 记录搜索历史
     */
    void recordSearchHistory(Long userId, String keyword, String searchType, Integer resultCount);

    /**
     * 获取用户搜索历史
     */
    IPage<SearchHistory> getSearchHistory(Long userId, String searchType, String keyword,
                                         int pageNum, int pageSize, String sortBy, String sortDirection);

    /**
     * 清空用户搜索历史
     */
    void clearSearchHistory(Long userId);

    /**
     * 删除指定搜索历史
     */
    void deleteSearchHistory(Long historyId);

    /**
     * 获取热门搜索关键词
     */
    List<HotSearch> getHotSearchKeywords(Integer limit);

    /**
     * 获取搜索建议
     */
    List<String> getSearchSuggestions(String keyword, Integer limit);

    /**
     * 根据搜索类型获取热门关键词
     */
    List<HotSearch> getHotKeywordsByType(String searchType, Integer limit);

    /**
     * 获取用户搜索偏好
     */
    List<SearchHistory> getUserSearchPreferences(Long userId);

    /**
     * 更新热搜趋势分数
     */
    void updateHotSearchTrend(String keyword, Double trendScore);

    /**
     * 更新热搜统计
     */
    void updateHotSearchStats(String keyword);
} 