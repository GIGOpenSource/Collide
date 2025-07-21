package com.gig.collide.artist.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gig.collide.datasource.domain.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 博主统计实体
 * @author GIG
 */
@Setter
@Getter
@TableName("artist_statistics")
public class ArtistStatistics extends BaseEntity {

    /**
     * 博主ID
     */
    private Long artistId;

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
     * 互动率
     */
    private BigDecimal engagementRate;

    /**
     * 粉丝活跃度
     */
    private BigDecimal followersActivity;

    /**
     * 内容质量分数
     */
    private BigDecimal contentQualityScore;

    /**
     * 影响力指数
     */
    private BigDecimal influenceIndex;

    /**
     * 粉丝增长趋势（JSON格式）
     */
    @TableField(typeHandler = org.apache.ibatis.type.StringTypeHandler.class)
    private String followersGrowthTrend;

    /**
     * 内容发布趋势（JSON格式）
     */
    @TableField(typeHandler = org.apache.ibatis.type.StringTypeHandler.class)
    private String contentPublishTrend;

    /**
     * 互动数据趋势（JSON格式）
     */
    @TableField(typeHandler = org.apache.ibatis.type.StringTypeHandler.class)
    private String engagementTrend;

    /**
     * 热门标签（JSON格式）
     */
    @TableField(typeHandler = org.apache.ibatis.type.StringTypeHandler.class)
    private String popularTags;

    /**
     * 粉丝年龄分布（JSON格式）
     */
    @TableField(typeHandler = org.apache.ibatis.type.StringTypeHandler.class)
    private String followersAgeDistribution;

    /**
     * 粉丝性别分布（JSON格式）
     */
    @TableField(typeHandler = org.apache.ibatis.type.StringTypeHandler.class)
    private String followersGenderDistribution;

    /**
     * 粉丝地区分布（JSON格式）
     */
    @TableField(typeHandler = org.apache.ibatis.type.StringTypeHandler.class)
    private String followersRegionDistribution;

    /**
     * 最佳发布时间
     */
    private String bestPublishTime;

    /**
     * 平均互动时间（分钟）
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

    // ================== 业务方法 ==================

    /**
     * 更新今日数据
     */
    public void updateTodayData(Integer followersGrowth, Integer worksCount, Long likes, Long views) {
        this.todayFollowersGrowth = followersGrowth;
        this.todayWorksCount = worksCount;
        this.todayLikes = likes;
        this.todayViews = views;
        this.lastUpdated = new Date();
    }

    /**
     * 更新本周数据
     */
    public void updateWeekData(Integer followersGrowth, Integer worksCount, Long likes, Long views) {
        this.weekFollowersGrowth = followersGrowth;
        this.weekWorksCount = worksCount;
        this.weekLikes = likes;
        this.weekViews = views;
        this.lastUpdated = new Date();
    }

    /**
     * 更新本月数据
     */
    public void updateMonthData(Integer followersGrowth, Integer worksCount, Long likes, Long views) {
        this.monthFollowersGrowth = followersGrowth;
        this.monthWorksCount = worksCount;
        this.monthLikes = likes;
        this.monthViews = views;
        this.lastUpdated = new Date();
    }

    /**
     * 计算互动率
     */
    public void calculateEngagementRate() {
        if (this.todayViews != null && this.todayViews > 0 && this.todayLikes != null) {
            // 简单的互动率计算：点赞数/浏览量
            this.engagementRate = new BigDecimal(this.todayLikes)
                    .divide(new BigDecimal(this.todayViews), 4, BigDecimal.ROUND_HALF_UP);
        } else {
            this.engagementRate = BigDecimal.ZERO;
        }
    }

    /**
     * 计算影响力指数
     */
    public void calculateInfluenceIndex() {
        // 综合考虑粉丝增长、内容质量、互动率等因素
        BigDecimal followersWeight = new BigDecimal("0.4");
        BigDecimal engagementWeight = new BigDecimal("0.3");
        BigDecimal qualityWeight = new BigDecimal("0.3");

        BigDecimal followersScore = this.monthFollowersGrowth != null ? 
                new BigDecimal(this.monthFollowersGrowth) : BigDecimal.ZERO;
        BigDecimal engagementScore = this.engagementRate != null ? 
                this.engagementRate.multiply(new BigDecimal("100")) : BigDecimal.ZERO;
        BigDecimal qualityScore = this.contentQualityScore != null ? 
                this.contentQualityScore : BigDecimal.ZERO;

        this.influenceIndex = followersScore.multiply(followersWeight)
                .add(engagementScore.multiply(engagementWeight))
                .add(qualityScore.multiply(qualityWeight));
    }

    /**
     * 设置统计周期
     */
    public void setPeriod(Date start, Date end) {
        this.periodStart = start;
        this.periodEnd = end;
    }

    /**
     * 重置统计数据
     */
    public void resetStatistics() {
        this.todayFollowersGrowth = 0;
        this.weekFollowersGrowth = 0;
        this.monthFollowersGrowth = 0;
        this.todayWorksCount = 0;
        this.weekWorksCount = 0;
        this.monthWorksCount = 0;
        this.todayLikes = 0L;
        this.weekLikes = 0L;
        this.monthLikes = 0L;
        this.todayViews = 0L;
        this.weekViews = 0L;
        this.monthViews = 0L;
        this.engagementRate = BigDecimal.ZERO;
        this.followersActivity = BigDecimal.ZERO;
        this.contentQualityScore = BigDecimal.ZERO;
        this.influenceIndex = BigDecimal.ZERO;
        this.lastUpdated = new Date();
    }
} 