package com.gig.collide.api.search.response;

import com.gig.collide.api.search.response.data.SearchSuggestionInfo;
import com.gig.collide.base.response.BaseResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

/**
 * 搜索建议响应
 *
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
@Getter
@Setter
@NoArgsConstructor
public class SearchSuggestionResponse extends BaseResponse {

    /**
     * 建议列表
     */
    private List<SearchSuggestionInfo> suggestions;

    /**
     * 关键词建议
     */
    private List<String> keywordSuggestions;

    /**
     * 用户建议
     */
    private List<UserSuggestion> userSuggestions;

    /**
     * 内容建议
     */
    private List<ContentSuggestion> contentSuggestions;

    /**
     * 热门搜索
     */
    private List<String> hotSearches;

    /**
     * 历史搜索
     */
    private List<String> historicalSearches;

    /**
     * 建议总数
     */
    private Integer totalCount;

    /**
     * 是否个性化推荐
     */
    private Boolean personalized;

    /**
     * 扩展信息
     */
    private Map<String, Object> extraInfo;

    /**
     * 用户建议内嵌类
     */
    @Getter
    @Setter
    @NoArgsConstructor
    public static class UserSuggestion {
        private Long userId;
        private String nickname;
        private String avatar;
        private String highlightText;
        private Boolean verified;
    }

    /**
     * 内容建议内嵌类
     */
    @Getter
    @Setter
    @NoArgsConstructor
    public static class ContentSuggestion {
        private Long contentId;
        private String title;
        private String coverUrl;
        private String highlightText;
        private String contentType;
    }

    // ===================== 便捷方法 =====================

    /**
     * 检查是否有建议
     */
    public boolean hasSuggestions() {
        return (suggestions != null && !suggestions.isEmpty()) ||
               (keywordSuggestions != null && !keywordSuggestions.isEmpty()) ||
               (userSuggestions != null && !userSuggestions.isEmpty()) ||
               (contentSuggestions != null && !contentSuggestions.isEmpty());
    }

    /**
     * 获取所有建议数量
     */
    public int getAllSuggestionCount() {
        int count = 0;
        if (suggestions != null) count += suggestions.size();
        if (keywordSuggestions != null) count += keywordSuggestions.size();
        if (userSuggestions != null) count += userSuggestions.size();
        if (contentSuggestions != null) count += contentSuggestions.size();
        return count;
    }

    /**
     * 检查是否有热门搜索
     */
    public boolean hasHotSearches() {
        return hotSearches != null && !hotSearches.isEmpty();
    }

    /**
     * 检查是否有历史搜索
     */
    public boolean hasHistoricalSearches() {
        return historicalSearches != null && !historicalSearches.isEmpty();
    }
} 