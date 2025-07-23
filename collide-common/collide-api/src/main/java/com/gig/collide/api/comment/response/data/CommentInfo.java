package com.gig.collide.api.comment.response.data;

import com.gig.collide.api.comment.constant.CommentStatus;
import com.gig.collide.api.comment.constant.CommentType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 评论信息响应对象
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Getter
@Setter
@NoArgsConstructor
public class CommentInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 评论ID
     */
    private Long commentId;

    /**
     * 评论类型
     */
    private CommentType commentType;

    /**
     * 目标ID（内容ID或父评论ID等）
     */
    private Long targetId;

    /**
     * 父评论ID
     */
    private Long parentCommentId;

    /**
     * 根评论ID（用于树形结构）
     */
    private Long rootCommentId;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 评论者用户ID
     */
    private Long userId;

    /**
     * 评论者用户名
     */
    private String username;

    /**
     * 评论者昵称
     */
    private String nickname;

    /**
     * 评论者头像
     */
    private String avatar;

    /**
     * 被回复用户ID
     */
    private Long replyToUserId;

    /**
     * 被回复用户名
     */
    private String replyToUsername;

    /**
     * 被回复用户昵称
     */
    private String replyToNickname;

    /**
     * 评论状态
     */
    private CommentStatus status;

    /**
     * 点赞数
     */
    private Integer likeCount;

    /**
     * 回复数
     */
    private Integer replyCount;

    /**
     * 当前用户是否已点赞
     */
    private Boolean isLiked;

    /**
     * 是否可删除（用户自己的评论或管理员）
     */
    private Boolean isDeletable;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 子评论列表（树形结构使用）
     */
    private List<CommentInfo> children;
} 