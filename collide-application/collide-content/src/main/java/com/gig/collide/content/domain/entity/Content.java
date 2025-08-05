package com.gig.collide.content.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 内容主表实体类
 * 基于无连表设计原则，包含冗余字段避免JOIN查询
 *
 * @author GIG Team
 * @version 2.0.0 (内容付费版)
 * @since 2024-01-31
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("t_content")
public class Content {

    /**
     * 内容ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 内容标题
     */
    @TableField("title")
    private String title;

    /**
     * 内容描述
     */
    @TableField("description")
    private String description;

    /**
     * 内容类型：NOVEL、COMIC、VIDEO、ARTICLE、AUDIO
     */
    @TableField("content_type")
    private String contentType;

    /**
     * 内容数据，JSON格式
     */
    @TableField("content_data")
    private String contentData;

    /**
     * 封面图片URL
     */
    @TableField("cover_url")
    private String coverUrl;

    /**
     * 标签，JSON数组格式
     */
    @TableField("tags")
    private String tags;

    // =================== 作者信息（冗余字段，避免连表） ===================

    /**
     * 作者用户ID
     */
    @TableField("author_id")
    private Long authorId;

    /**
     * 作者昵称（冗余）
     */
    @TableField("author_nickname")
    private String authorNickname;

    /**
     * 作者头像URL（冗余）
     */
    @TableField("author_avatar")
    private String authorAvatar;

    // =================== 分类信息（冗余字段，避免连表） ===================

    /**
     * 分类ID
     */
    @TableField("category_id")
    private Long categoryId;

    /**
     * 分类名称（冗余）
     */
    @TableField("category_name")
    private String categoryName;

    // =================== 状态相关字段 ===================

    /**
     * 状态：DRAFT、PUBLISHED、OFFLINE
     */
    @TableField("status")
    private String status;

    /**
     * 审核状态：PENDING、APPROVED、REJECTED
     */
    @TableField("review_status")
    private String reviewStatus;

    // =================== 统计字段（冗余存储，避免聚合查询） ===================

    /**
     * 查看数
     */
    @TableField("view_count")
    private Long viewCount;

    /**
     * 点赞数
     */
    @TableField("like_count")
    private Long likeCount;

    /**
     * 评论数
     */
    @TableField("comment_count")
    private Long commentCount;

    /**
     * 收藏数
     */
    @TableField("favorite_count")
    private Long favoriteCount;

    /**
     * 分享数
     */
    @TableField("share_count")
    private Long shareCount;

    /**
     * 评分数
     */
    @TableField("score_count")
    private Long scoreCount;

    /**
     * 总评分
     */
    @TableField("score_total")
    private Long scoreTotal;

    // =================== 时间字段 ===================

    /**
     * 发布时间
     */
    @TableField("publish_time")
    private LocalDateTime publishTime;

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

    // =================== 计算属性 ===================

    /**
     * 计算平均评分
     */
    public Double getAverageScore() {
        if (scoreCount == null || scoreCount == 0 || scoreTotal == null) {
            return 0.0;
        }
        return (double) scoreTotal / scoreCount;
    }

    /**
     * 判断是否已发布
     */
    public boolean isPublished() {
        return "PUBLISHED".equals(status);
    }

    /**
     * 判断是否已审核通过
     */
    public boolean isApproved() {
        return "APPROVED".equals(reviewStatus);
    }

    /**
     * 判断是否可以查看
     */
    public boolean isViewable() {
        return isPublished() && isApproved();
    }

    /**
     * 发布内容
     */
    public void publish() {
        this.status = "PUBLISHED";
        if (this.publishTime == null) {
            this.publishTime = LocalDateTime.now();
        }
    }

    /**
     * 下线内容
     */
    public void offline() {
        this.status = "OFFLINE";
    }

    /**
     * 审核通过
     */
    public void approveReview() {
        this.reviewStatus = "APPROVED";
    }

    /**
     * 审核拒绝
     */
    public void rejectReview() {
        this.reviewStatus = "REJECTED";
    }

    /**
     * 判断是否可以发布
     */
    public boolean canPublish() {
        return "DRAFT".equals(status) && "APPROVED".equals(reviewStatus);
    }

    /**
     * 判断是否可以编辑
     */
    public boolean canEdit() {
        return "DRAFT".equals(status) || "PUBLISHED".equals(status);
    }

    /**
     * 判断是否可以删除
     */
    public boolean canDelete() {
        return "DRAFT".equals(status);
    }
}