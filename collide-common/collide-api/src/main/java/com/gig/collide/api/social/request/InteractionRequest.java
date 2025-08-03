package com.gig.collide.api.social.request;

import lombok.Data;

import jakarta.validation.constraints.NotNull;

/**
 * 互动操作请求
 * 
 * @author GIG Team
 * @since 2024-01-16
 */
@Data
public class InteractionRequest {

    @NotNull(message = "用户ID不能为空")
    private Long userId;

    @NotNull(message = "内容ID不能为空")
    private Long contentId;

    // 点赞相关
    private Integer likeType; // 1-普通点赞,2-超级点赞

    // 收藏相关
    private Long folderId; // 收藏夹ID

    private String folderName; // 收藏夹名称

    // 分享相关
    private Integer shareType; // 分享类型

    private String sharePlatform; // 分享平台

    private String shareComment; // 分享评论

    // 评论相关
    private Long parentCommentId; // 父评论ID

    private Long replyToUserId; // 回复的用户ID

    private String commentText; // 评论内容

    private String commentImages; // 评论图片(JSON)
}