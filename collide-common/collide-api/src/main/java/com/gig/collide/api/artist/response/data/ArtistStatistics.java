package com.gig.collide.api.artist.response.data;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 * 博主统计信息
 * @author GIG
 */
@Getter
@Setter
@NoArgsConstructor
public class ArtistStatistics implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 博主ID
     */
    private Long artistId;

    /**
     * 博主名称
     */
    private String artistName;

    /**
     * 粉丝增长数（今日）
     */
    private Integer todayFollowersGrowth;

    /**
     * 粉丝增长数（本周）
     */
    private Integer weekFollowersGrowth;

    /**
     * 粉丝增长数（本月）
     */
    private Integer monthFollowersGrowth;

    /**
     * 作品发布数（今日）
     */
    private Integer todayWorksCount;

    /**
     * 作品发布数（本周）
     */
    private Integer weekWorksCount;

    /**
     * 作品发布数（本月）
     */
    private Integer monthWorksCount;

    /**
     * 获赞数（今日）
     */
    private Long todayLikes;

    /**
     * 获赞数（本周）
     */
    private Long weekLikes;

    /**
     * 获赞数（本月）
     */
    private Long monthLikes;

    /**
     * 浏览量（今日）
     */
    private Long todayViews;

    /**
     * 浏览量（本周）
     */
    private Long weekViews;

    /**
     * 浏览量（本月）
     */
    private Long monthViews;

    /**
     * 互动率（点赞+评论+分享/浏览量）
     */
    private Double engagementRate;

    /**
     * 粉丝活跃度
     */
    private Double followersActivity;

    /**
     * 内容质量分数
     */
    private Double contentQualityScore;

    /**
     * 影响力指数
     */
    private Double influenceIndex;

    /**
     * 粉丝增长趋势（近30天每日数据）
     */
    private Map<String, Integer> followersGrowthTrend;

    /**
     * 内容发布趋势（近30天每日数据）
     */
    private Map<String, Integer> contentPublishTrend;

    /**
     * 互动数据趋势（近30天每日数据）
     */
    private Map<String, Long> engagementTrend;

    /**
     * 热门标签
     */
    private Map<String, Integer> popularTags;

    /**
     * 粉丝年龄分布
     */
    private Map<String, Integer> followersAgeDistribution;

    /**
     * 粉丝性别分布
     */
    private Map<String, Integer> followersGenderDistribution;

    /**
     * 粉丝地区分布
     */
    private Map<String, Integer> followersRegionDistribution;

    /**
     * 最佳发布时间
     */
    private String bestPublishTime;

    /**
     * 平均互动时间
     */
    private Integer avgEngagementTime;

    /**
     * 统计周期开始时间
     */
    private Date periodStart;

    /**
     * 统计周期结束时间
     */
    private Date periodEnd;

    /**
     * 最后更新时间
     */
    private Date lastUpdated;
} 