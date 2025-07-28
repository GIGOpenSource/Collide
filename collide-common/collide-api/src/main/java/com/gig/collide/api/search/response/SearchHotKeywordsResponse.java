package com.gig.collide.api.search.response;

import com.gig.collide.base.response.BaseResponse;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * 热门搜索关键词响应
 * 
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
@Getter
@Setter
@ToString
public class SearchHotKeywordsResponse extends BaseResponse {

    private static final long serialVersionUID = 1L;

    /**
     * 热门关键词列表
     */
    private List<HotKeywordInfo> keywords;

    /**
     * 总数量
     */
    private Integer totalCount;

    /**
     * 统计时间范围
     */
    private String timeRange;

    /**
     * 更新时间
     */
    private String updateTime;

    /**
     * 热门关键词信息
     */
    @Getter
    @Setter
    @ToString
    public static class HotKeywordInfo {

        /**
         * 关键词
         */
        private String keyword;

        /**
         * 搜索次数
         */
        private Long searchCount;

        /**
         * 用户数
         */
        private Long userCount;

        /**
         * 热度评分
         */
        private Double hotScore;

        /**
         * 排名
         */
        private Integer rank;

        /**
         * 上期排名
         */
        private Integer lastRank;

        /**
         * 排名变化（+2表示上升2位，-1表示下降1位）
         */
        private Integer rankChange;

        /**
         * 内容类型
         */
        private String contentType;

        /**
         * 搜索类型
         */
        private String searchType;

        /**
         * 最后搜索时间
         */
        private String lastSearchTime;
    }
} 