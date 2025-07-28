package com.gig.collide.api.social.response;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 社交动态响应 - 简洁版
 * 基于t_social_dynamic表结构
 * 
 * @author GIG Team
 * @version 2.0.0
 */
@Data
public class SocialDynamicResponse {

    private Long id;

    private String content;

    private String dynamicType;

    private String images;

    private String videoUrl;

    /**
     * 用户信息（冗余字段）
     */
    private Long userId;
    private String userNickname;
    private String userAvatar;

    /**
     * 分享相关字段
     */
    private String shareTargetType;
    private Long shareTargetId;
    private String shareTargetTitle;

    /**
     * 统计字段（冗余存储）
     */
    private Long likeCount;
    private Long commentCount;
    private Long shareCount;

    private String status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
} 