package com.gig.collide.api.social.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 互动信息VO
 * 
 * @author GIG Team
 * @since 2024-01-16
 */
@Data
public class InteractionVO {

    private Long id;

    private Long userId;

    private Long contentId;

    private Long contentOwnerId;

    private LocalDateTime createTime;

    // 用户信息（冗余）
    private String userName;

    private String userAvatar;

    // 互动类型特定字段
    private Integer likeType; // 点赞类型

    private String folderName; // 收藏夹名称

    private Integer shareType; // 分享类型

    private String sharePlatform; // 分享平台

    private String shareComment; // 分享评论

    // 评论特有字段
    private Long parentCommentId;

    private Long replyToUserId;

    private String commentText;

    private String commentImages;

    private Integer likeCount;

    private Integer replyCount;

    private Integer status;

    private LocalDateTime updateTime;

    // 回复的用户信息
    private String replyToUserName;
}