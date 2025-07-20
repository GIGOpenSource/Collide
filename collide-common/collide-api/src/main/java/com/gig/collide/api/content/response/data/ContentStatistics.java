package com.gig.collide.api.content.response.data;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 内容统计信息
 */
@Data
public class ContentStatistics {

    /**
     * 统计ID
     */
    private Long statisticsId;

    /**
     * 内容ID
     */
    private Long contentId;

    /**
     * 统计日期
     */
    private String statisticsDate;

    /**
     * 查看次数
     */
    private Long viewCount;

    /**
     * 今日查看次数
     */
    private Long todayViewCount;

    /**
     * 点赞次数
     */
    private Long likeCount;

    /**
     * 今日点赞次数
     */
    private Long todayLikeCount;

    /**
     * 收藏次数
     */
    private Long collectCount;

    /**
     * 今日收藏次数
     */
    private Long todayCollectCount;

    /**
     * 分享次数
     */
    private Long shareCount;

    /**
     * 今日分享次数
     */
    private Long todayShareCount;

    /**
     * 评论次数
     */
    private Long commentCount;

    /**
     * 今日评论次数
     */
    private Long todayCommentCount;

    /**
     * 下载次数
     */
    private Long downloadCount;

    /**
     * 今日下载次数
     */
    private Long todayDownloadCount;

    /**
     * 播放完成率（视频专用）
     */
    private Double playCompletionRate;

    /**
     * 平均观看时长（视频专用，单位：秒）
     */
    private Integer avgWatchDuration;

    /**
     * 跳出率
     */
    private Double bounceRate;

    /**
     * 用户留存率
     */
    private Double retentionRate;

    /**
     * 收入（元）
     */
    private Long revenue;

    /**
     * 今日收入（元）
     */
    private Long todayRevenue;

    /**
     * 热度分数
     */
    private Double hotScore;

    /**
     * 质量分数
     */
    private Double qualityScore;

    /**
     * 推荐指数
     */
    private Double recommendIndex;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
} 