package com.gig.collide.business.domain.search;

import com.gig.collide.api.search.response.data.SearchResult;
import com.gig.collide.api.search.response.data.SuggestionItem;

import java.util.List;

/**
 * 用户搜索仓储接口
 * 
 * @author GIG Team
 */
public interface UserSearchRepository {

    /**
     * 搜索用户
     * 
     * @param keyword 关键词
     * @param pageNum 页码
     * @param pageSize 页大小
     * @param highlight 是否高亮
     * @return 搜索结果列表
     */
    List<SearchResult> searchUsers(String keyword, Integer pageNum, Integer pageSize, Boolean highlight);

    /**
     * 统计用户搜索结果数量
     * 
     * @param keyword 关键词
     * @return 结果数量
     */
    long countUsers(String keyword);

    /**
     * 获取用户建议
     * 
     * @param keyword 关键词前缀
     * @param limit 建议数量
     * @return 用户建议列表
     */
    List<SuggestionItem> getUserSuggestions(String keyword, Integer limit);
} 