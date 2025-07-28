package com.gig.collide.api.search.response;

import com.gig.collide.base.response.BaseResponse;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * 搜索排行榜响应
 * 
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
@Getter
@Setter
@ToString
public class SearchRankingResponse extends BaseResponse {

    private static final long serialVersionUID = 1L;

    /**
     * 排行榜类型（搜索次数/用户数/热度）
     */
    private String rankType;

    /**
     * 排行榜数据列表
     */
    private List<RankingItem> rankings;

    /**
     * 统计时间范围
     */
    private String timeRange;

    /**
     * 更新时间
     */
    private String updateTime;

    /**
     * 总数量
     */
    private Integer totalCount;

    /**
     * 排行榜项目
     */
    @Getter
    @Setter
    @ToString
    public static class RankingItem {

        /**
         * 排名
         */
        private Integer rank;

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
         * 上期排名
         */
        private Integer lastRank;

        /**
         * 排名变化
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
         * 点击率
         */
        private Double clickRate;

        /**
         * 转化率
         */
        private Double conversionRate;
    }
} 