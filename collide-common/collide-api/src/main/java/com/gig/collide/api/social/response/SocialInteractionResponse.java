package com.gig.collide.api.social.response;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 社交互动记录响应对象
 * 用于聚合展示用户的点赞和评论互动记录
 * 
 * @author GIG Team
 * @version 2.0.0 (缓存增强版)
 * @since 2024-01-16
 */
@Data
public class SocialInteractionResponse {

    /**
     * 互动ID
     */
    private Long interactionId;

    /**
     * 互动类型
     * LIKE_GIVE: 用户点赞别人
     * LIKE_RECEIVE: 被别人点赞
     * COMMENT_GIVE: 用户评论别人
     * COMMENT_RECEIVE: 被别人评论
     */
    private String interactionType;

    /**
     * 互动时间
     */
    private LocalDateTime interactionTime;

    /**
     * 相关动态ID
     */
    private Long dynamicId;

    /**
     * 动态内容（摘要）
     */
    private String dynamicContent;

    /**
     * 动态作者ID
     */
    private Long dynamicAuthorId;

    /**
     * 动态作者用户名
     */
    private String dynamicAuthorName;

    /**
     * 动态作者头像
     */
    private String dynamicAuthorAvatar;

    /**
     * 互动用户ID（点赞/评论的用户）
     */
    private Long interactionUserId;

    /**
     * 互动用户名
     */
    private String interactionUserName;

    /**
     * 互动用户头像
     */
    private String interactionUserAvatar;

    /**
     * 评论内容（仅当互动类型为评论时有值）
     */
    private String commentContent;

    /**
     * 是否已读
     */
    private Boolean isRead;

    /**
     * 扩展信息
     */
    private Object extraData;
}