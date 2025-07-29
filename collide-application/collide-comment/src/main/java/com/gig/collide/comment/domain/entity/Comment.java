package com.gig.collide.comment.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 评论实体 - 简洁版
 * 基于comment-simple.sql的t_comment表设计
 * 支持多级评论结构和冗余字段设计
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
@TableName("t_comment")
public class Comment {

    /**
     * 评论ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 评论类型：CONTENT、DYNAMIC
     */
    @TableField("comment_type")
    private String commentType;

    /**
     * 目标对象ID
     */
    @TableField("target_id")
    private Long targetId;

    /**
     * 父评论ID，0表示根评论
     */
    @TableField("parent_comment_id")
    private Long parentCommentId;

    /**
     * 评论内容
     */
    @TableField("content")
    private String content;

    // =================== 用户信息（冗余字段） ===================

    /**
     * 评论用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 用户昵称（冗余）
     */
    @TableField("user_nickname")
    private String userNickname;

    /**
     * 用户头像（冗余）
     */
    @TableField("user_avatar")
    private String userAvatar;

    // =================== 回复相关 ===================

    /**
     * 回复目标用户ID
     */
    @TableField("reply_to_user_id")
    private Long replyToUserId;

    /**
     * 回复目标用户昵称（冗余）
     */
    @TableField("reply_to_user_nickname")
    private String replyToUserNickname;

    /**
     * 回复目标用户头像（冗余）
     */
    @TableField("reply_to_user_avatar")
    private String replyToUserAvatar;

    // =================== 状态和统计 ===================

    /**
     * 状态：NORMAL、HIDDEN、DELETED
     */
    @TableField("status")
    private String status;

    /**
     * 点赞数（冗余统计）
     */
    @TableField("like_count")
    private Integer likeCount;

    /**
     * 回复数（冗余统计）
     */
    @TableField("reply_count")
    private Integer replyCount;

    // =================== 时间字段 ===================

    /**
     * 创建时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField("update_time")
    private LocalDateTime updateTime;

    // =================== 扩展字段（用于树形结构） ===================

    /**
     * 子评论列表（用于树形展示，不映射到数据库）
     */
    @TableField(exist = false)
    private List<Comment> children;

    /**
     * 评论层级深度（不映射到数据库）
     */
    @TableField(exist = false)
    private Integer level;

    // =================== 业务方法 ===================

    /**
     * 是否为正常状态
     */
    public boolean isNormal() {
        return "NORMAL".equals(status);
    }

    /**
     * 是否已隐藏
     */
    public boolean isHidden() {
        return "HIDDEN".equals(status);
    }

    /**
     * 是否已删除
     */
    public boolean isDeleted() {
        return "DELETED".equals(status);
    }

    /**
     * 是否为根评论
     */
    public boolean isRootComment() {
        return parentCommentId == null || parentCommentId == 0;
    }

    /**
     * 是否为回复评论
     */
    public boolean isReplyComment() {
        return !isRootComment();
    }

    /**
     * 是否为内容评论
     */
    public boolean isContentComment() {
        return "CONTENT".equals(commentType);
    }

    /**
     * 是否为动态评论
     */
    public boolean isDynamicComment() {
        return "DYNAMIC".equals(commentType);
    }

    /**
     * 删除评论（逻辑删除）
     */
    public void delete() {
        this.status = "DELETED";
        this.updateTime = LocalDateTime.now();
    }

    /**
     * 隐藏评论
     */
    public void hide() {
        this.status = "HIDDEN";
        this.updateTime = LocalDateTime.now();
    }

    /**
     * 恢复评论
     */
    public void restore() {
        this.status = "NORMAL";
        this.updateTime = LocalDateTime.now();
    }

    /**
     * 增加点赞数
     */
    public void increaseLikeCount(int increment) {
        if (this.likeCount == null) {
            this.likeCount = 0;
        }
        this.likeCount += Math.max(0, increment);
        this.updateTime = LocalDateTime.now();
    }

    /**
     * 减少点赞数
     */
    public void decreaseLikeCount(int decrement) {
        if (this.likeCount == null) {
            this.likeCount = 0;
        }
        this.likeCount = Math.max(0, this.likeCount - Math.max(0, decrement));
        this.updateTime = LocalDateTime.now();
    }

    /**
     * 增加回复数
     */
    public void increaseReplyCount(int increment) {
        if (this.replyCount == null) {
            this.replyCount = 0;
        }
        this.replyCount += Math.max(0, increment);
        this.updateTime = LocalDateTime.now();
    }

    /**
     * 减少回复数
     */
    public void decreaseReplyCount(int decrement) {
        if (this.replyCount == null) {
            this.replyCount = 0;
        }
        this.replyCount = Math.max(0, this.replyCount - Math.max(0, decrement));
        this.updateTime = LocalDateTime.now();
    }

    /**
     * 更新用户信息（冗余字段同步）
     */
    public void updateUserInfo(String nickname, String avatar) {
        if (nickname != null) {
            this.userNickname = nickname;
        }
        if (avatar != null) {
            this.userAvatar = avatar;
        }
        this.updateTime = LocalDateTime.now();
    }

    /**
     * 更新回复目标用户信息（冗余字段同步）
     */
    public void updateReplyToUserInfo(String nickname, String avatar) {
        if (nickname != null) {
            this.replyToUserNickname = nickname;
        }
        if (avatar != null) {
            this.replyToUserAvatar = avatar;
        }
        this.updateTime = LocalDateTime.now();
    }

    /**
     * 更新评论内容
     */
    public void updateContent(String newContent) {
        if (newContent != null && !newContent.trim().isEmpty()) {
            this.content = newContent.trim();
            this.updateTime = LocalDateTime.now();
        }
    }

    /**
     * 是否有回复
     */
    public boolean hasReplies() {
        return replyCount != null && replyCount > 0;
    }

    /**
     * 是否有点赞
     */
    public boolean hasLikes() {
        return likeCount != null && likeCount > 0;
    }

    /**
     * 获取评论字数
     */
    public int getContentLength() {
        return content != null ? content.length() : 0;
    }

    /**
     * 是否为长评论（超过100字符）
     */
    public boolean isLongComment() {
        return getContentLength() > 100;
    }

    /**
     * 获取评论热度（点赞数权重）
     */
    public double getHotScore() {
        double likeScore = likeCount != null ? likeCount * 1.0 : 0.0;
        double replyScore = replyCount != null ? replyCount * 0.5 : 0.0;
        
        // 时间衰减：创建时间越近，分数越高
        long daysSinceCreation = createTime != null ? 
            java.time.temporal.ChronoUnit.DAYS.between(createTime, LocalDateTime.now()) : 0;
        double timeScore = Math.max(0, 7 - daysSinceCreation) * 0.1;
        
        return likeScore + replyScore + timeScore;
    }

    /**
     * 是否可以被删除
     */
    public boolean canBeDeleted() {
        return isNormal() || isHidden();
    }

    /**
     * 是否可以被隐藏
     */
    public boolean canBeHidden() {
        return isNormal();
    }

    /**
     * 是否可以被恢复
     */
    public boolean canBeRestored() {
        return isHidden() || isDeleted();
    }

    /**
     * 是否为自己回复自己
     */
    public boolean isSelfReply() {
        return replyToUserId != null && replyToUserId.equals(userId);
    }

    /**
     * 获取评论创建天数
     */
    public long getCreateDays() {
        if (createTime == null) {
            return 0;
        }
        return java.time.temporal.ChronoUnit.DAYS.between(createTime, LocalDateTime.now());
    }

    // =================== 树形结构方法 ===================

    /**
     * 初始化子评论列表
     */
    public void initChildren() {
        if (this.children == null) {
            this.children = new java.util.ArrayList<>();
        }
    }

    /**
     * 添加子评论
     */
    public void addChild(Comment child) {
        initChildren();
        if (child != null) {
            child.setLevel((this.level != null ? this.level : 0) + 1);
            this.children.add(child);
        }
    }

    /**
     * 是否有子评论
     */
    public boolean hasChildren() {
        return children != null && !children.isEmpty();
    }

    /**
     * 获取子评论数量
     */
    public int getChildrenCount() {
        return children != null ? children.size() : 0;
    }

    /**
     * 初始化默认值
     */
    public void initDefaults() {
        if (this.parentCommentId == null) {
            this.parentCommentId = 0L;
        }
        if (this.status == null) {
            this.status = "NORMAL";
        }
        if (this.likeCount == null) {
            this.likeCount = 0;
        }
        if (this.replyCount == null) {
            this.replyCount = 0;
        }
        if (this.createTime == null) {
            this.createTime = LocalDateTime.now();
        }
        if (this.updateTime == null) {
            this.updateTime = LocalDateTime.now();
        }
    }
}