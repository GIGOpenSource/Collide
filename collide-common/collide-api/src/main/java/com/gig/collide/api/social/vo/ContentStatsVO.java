package com.gig.collide.api.social.vo;

import lombok.Data;

/**
 * 内容统计信息VO
 * 
 * @author GIG Team
 * @since 2024-01-16
 */
@Data
public class ContentStatsVO {

    private Long contentId;

    private Integer likeCount;

    private Integer commentCount;

    private Integer favoriteCount;

    private Integer shareCount;

    private Integer viewCount;

    // 用户互动状态
    private Boolean isLiked;

    private Boolean isFavorited;

    private Boolean isCommented;

    // 热度相关
    private Double hotScore; // 热度分数

    private Integer ranking; // 排名
}