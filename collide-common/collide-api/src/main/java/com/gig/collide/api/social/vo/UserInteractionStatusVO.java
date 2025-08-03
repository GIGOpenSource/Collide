package com.gig.collide.api.social.vo;

import lombok.Data;

/**
 * 用户互动状态VO
 * 
 * @author GIG Team
 * @since 2024-01-16
 */
@Data
public class UserInteractionStatusVO {

    private Long userId;

    private Long contentId;

    private Boolean liked;

    private Boolean favorited;

    private Boolean commented;

    private Boolean shared;

    // 互动时间
    private String likeTime;

    private String favoriteTime;

    private String commentTime;

    private String shareTime;
}