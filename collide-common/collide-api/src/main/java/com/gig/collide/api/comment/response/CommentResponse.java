package com.gig.collide.api.comment.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 评论统一响应对象 - 简洁版
 * 基于comment-simple.sql的字段结构，包含所有冗余信息
 * 支持多级评论和回复功能
 * 
 * @author Collide
 * @version 2.0.0 (简洁版)
 * @since 2024-01-01
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CommentResponse implements Serializable {

    /**
     * 评论ID
     */
    private Long id;

    /**
     * 评论类型：CONTENT、DYNAMIC
     */
    private String commentType;

    /**
     * 目标对象ID
     */
    private Long targetId;

    /**
     * 父评论ID，0表示根评论
     */
    private Long parentCommentId;

    /**
     * 评论内容
     */
    private String content;

    // =================== 用户信息（冗余字段） ===================

    /**
     * 评论用户ID
     */
    private Long userId;

    /**
     * 用户昵称（冗余）
     */
    private String userNickname;

    /**
     * 用户头像（冗余）
     */
    private String userAvatar;

    // =================== 回复相关 ===================

    /**
     * 回复目标用户ID
     */
    private Long replyToUserId;

    /**
     * 回复目标用户昵称（冗余）
     */
    private String replyToUserNickname;

    /**
     * 回复目标用户头像（冗余）
     */
    private String replyToUserAvatar;

    // =================== 状态和统计 ===================

    /**
     * 状态：NORMAL、HIDDEN、DELETED
     */
    private String status;

    /**
     * 点赞数（冗余统计）
     */
    private Integer likeCount;

    /**
     * 回复数（冗余统计）
     */
    private Integer replyCount;

    // =================== 时间字段 ===================

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    // =================== 扩展字段（用于树形结构） ===================

    /**
     * 子评论列表（用于树形展示）
     */
    private List<CommentResponse> children;

    /**
     * 评论层级深度
     */
    private Integer level;

    /**
     * 评论路径（用于快速定位）
     */
    private String commentPath;

    // =================== 计算属性 ===================

    /**
     * 是否为正常状态
     */
    @JsonIgnore
    public boolean isNormal() {
        return "NORMAL".equals(status);
    }

    /**
     * 是否已隐藏
     */
    @JsonIgnore
    public boolean isHidden() {
        return "HIDDEN".equals(status);
    }

    /**
     * 是否已删除
     */
    @JsonIgnore
    public boolean isDeleted() {
        return "DELETED".equals(status);
    }

    /**
     * 是否为根评论
     */
    @JsonIgnore
    public boolean isRootComment() {
        return parentCommentId == null || parentCommentId == 0;
    }

    /**
     * 是否为回复评论
     */
    @JsonIgnore
    public boolean isReplyComment() {
        return !isRootComment();
    }

    /**
     * 是否为内容评论
     */
    @JsonIgnore
    public boolean isContentComment() {
        return "CONTENT".equals(commentType);
    }

    /**
     * 是否为动态评论
     */
    @JsonIgnore
    public boolean isDynamicComment() {
        return "DYNAMIC".equals(commentType);
    }

    /**
     * 是否有回复
     */
    @JsonIgnore
    public boolean hasReplies() {
        return replyCount != null && replyCount > 0;
    }

    /**
     * 是否有点赞
     */
    @JsonIgnore
    public boolean hasLikes() {
        return likeCount != null && likeCount > 0;
    }

    /**
     * 获取评论创建天数
     */
    @JsonIgnore
    public long getCreateDays() {
        if (createTime == null) {
            return 0;
        }
        return java.time.temporal.ChronoUnit.DAYS.between(createTime, LocalDateTime.now());
    }

    /**
     * 获取评论摘要（前50个字符）
     */
    @JsonIgnore
    public String getSummary() {
        if (content == null || content.trim().isEmpty()) {
            return "";
        }
        String trimmed = content.trim();
        return trimmed.length() > 50 ? trimmed.substring(0, 50) + "..." : trimmed;
    }

    /**
     * 获取评论字数
     */
    @JsonIgnore
    public int getContentLength() {
        return content != null ? content.length() : 0;
    }

    /**
     * 是否为长评论（超过100字符）
     */
    @JsonIgnore
    public boolean isLongComment() {
        return getContentLength() > 100;
    }

    /**
     * 获取评论热度（点赞数权重）
     */
    @JsonIgnore
    public double getHotScore() {
        double likeScore = likeCount != null ? likeCount * 1.0 : 0.0;
        double replyScore = replyCount != null ? replyCount * 0.5 : 0.0;
        double timeScore = Math.max(0, 7 - getCreateDays()) * 0.1; // 时间衰减
        
        return likeScore + replyScore + timeScore;
    }

    /**
     * 初始化子评论列表
     */
    @JsonIgnore
    public void initChildren() {
        if (this.children == null) {
            this.children = new java.util.ArrayList<>();
        }
    }

    /**
     * 添加子评论
     */
    @JsonIgnore
    public void addChild(CommentResponse child) {
        initChildren();
        if (child != null) {
            child.setLevel((this.level != null ? this.level : 0) + 1);
            this.children.add(child);
        }
    }

    /**
     * 是否有子评论
     */
    @JsonIgnore
    public boolean hasChildren() {
        return children != null && !children.isEmpty();
    }

    /**
     * 获取子评论数量
     */
    @JsonIgnore
    public int getChildrenCount() {
        return children != null ? children.size() : 0;
    }
}