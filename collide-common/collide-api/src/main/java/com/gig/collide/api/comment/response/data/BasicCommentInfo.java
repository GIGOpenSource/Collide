package com.gig.collide.api.comment.response.data;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 基础评论信息
 * 包含最基本的评论标识信息
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Getter
@Setter
@NoArgsConstructor
public class BasicCommentInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 评论ID
     */
    private Long id;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户昵称
     */
    private String userNickname;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 用户认证状态
     */
    private Boolean userVerified;

    /**
     * 点赞数
     */
    private Integer likeCount;

    /**
     * 回复数
     */
    private Integer replyCount;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 是否置顶
     */
    private Boolean isPinned;

    /**
     * 是否热门
     */
    private Boolean isHot;

    /**
     * 是否精华
     */
    private Boolean isEssence;

    /**
     * 质量分数
     */
    private Double qualityScore;
} 