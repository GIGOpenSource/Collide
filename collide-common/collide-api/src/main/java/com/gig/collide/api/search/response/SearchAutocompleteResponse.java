package com.gig.collide.api.search.response;

import com.gig.collide.base.response.BaseResponse;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * 搜索自动完成响应
 * 
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
@Getter
@Setter
@ToString
public class SearchAutocompleteResponse extends BaseResponse {

    private static final long serialVersionUID = 1L;

    /**
     * 输入前缀
     */
    private String prefix;

    /**
     * 建议列表
     */
    private List<AutocompleteSuggestion> suggestions;

    /**
     * 返回数量
     */
    private Integer count;

    /**
     * 响应时间（毫秒）
     */
    private Long responseTime;

    /**
     * 是否有更多建议
     */
    private Boolean hasMore;

    /**
     * 自动完成建议
     */
    @Getter
    @Setter
    @ToString
    public static class AutocompleteSuggestion {

        /**
         * 建议文本
         */
        private String text;

        /**
         * 显示文本（可能包含高亮）
         */
        private String displayText;

        /**
         * 建议类型（history/hot/smart/suggestion）
         */
        private String type;

        /**
         * 内容类型
         */
        private String contentType;

        /**
         * 搜索类型
         */
        private String searchType;

        /**
         * 权重/评分
         */
        private Integer weight;

        /**
         * 搜索次数
         */
        private Long searchCount;

        /**
         * 图标URL
         */
        private String iconUrl;

        /**
         * 描述信息
         */
        private String description;

        /**
         * 跳转链接
         */
        private String redirectUrl;

        /**
         * 是否为个性化推荐
         */
        private Boolean personalized;

        /**
         * 匹配度评分（0-1）
         */
        private Double matchScore;

        /**
         * 标签
         */
        private List<String> tags;
    }
} 