package com.gig.collide.content.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * 内容实体类 - 简洁版
 * 基于content-simple.sql的t_content表结构，包含评分功能和冗余字段
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

    // =================== 作者信息（冗余字段） ===================

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

    // =================== 分类信息（冗余字段） ===================

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

    // =================== 统计字段（冗余存储） ===================

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
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    // =================== 业务方法 ===================

    /**
     * 是否为草稿状态
     */
    public boolean isDraft() {
        return "DRAFT".equals(status);
    }

    /**
     * 是否已发布
     */
    public boolean isPublished() {
        return "PUBLISHED".equals(status);
    }

    /**
     * 是否已下线
     */
    public boolean isOffline() {
        return "OFFLINE".equals(status);
    }

    /**
     * 是否待审核
     */
    public boolean isPendingReview() {
        return "PENDING".equals(reviewStatus);
    }

    /**
     * 是否审核通过
     */
    public boolean isApproved() {
        return "APPROVED".equals(reviewStatus);
    }

    /**
     * 是否审核被拒
     */
    public boolean isRejected() {
        return "REJECTED".equals(reviewStatus);
    }

    /**
     * 获取平均评分
     */
    public Double getAverageScore() {
        if (scoreCount == null || scoreCount == 0 || scoreTotal == null) {
            return 0.0;
        }
        return (double) scoreTotal / scoreCount;
    }

    /**
     * 是否为小说类型
     */
    public boolean isNovel() {
        return "NOVEL".equals(contentType);
    }

    /**
     * 是否为漫画类型
     */
    public boolean isComic() {
        return "COMIC".equals(contentType);
    }

    /**
     * 是否为视频类型
     */
    public boolean isVideo() {
        return "VIDEO".equals(contentType);
    }

    /**
     * 是否为文章类型
     */
    public boolean isArticle() {
        return "ARTICLE".equals(contentType);
    }

    /**
     * 是否为音频类型
     */
    public boolean isAudio() {
        return "AUDIO".equals(contentType);
    }

    /**
     * 是否需要章节管理
     */
    public boolean needsChapterManagement() {
        return isNovel() || isComic();
    }

    /**
     * 发布内容
     */
    public void publish() {
        this.status = "PUBLISHED";
        this.publishTime = LocalDateTime.now();
    }

    /**
     * 下线内容
     */
    public void offline() {
        this.status = "OFFLINE";
    }

    /**
     * 通过审核
     */
    public void approveReview() {
        this.reviewStatus = "APPROVED";
    }

    /**
     * 拒绝审核
     */
    public void rejectReview() {
        this.reviewStatus = "REJECTED";
    }

    /**
     * 增加浏览量
     */
    public void increaseViewCount(Integer increment) {
        this.viewCount = (this.viewCount == null ? 0 : this.viewCount) + increment;
    }

    /**
     * 增加点赞数
     */
    public void increaseLikeCount(Integer increment) {
        this.likeCount = (this.likeCount == null ? 0 : this.likeCount) + increment;
    }

    /**
     * 增加评论数
     */
    public void increaseCommentCount(Integer increment) {
        this.commentCount = (this.commentCount == null ? 0 : this.commentCount) + increment;
    }

    /**
     * 增加收藏数
     */
    public void increaseFavoriteCount(Integer increment) {
        this.favoriteCount = (this.favoriteCount == null ? 0 : this.favoriteCount) + increment;
    }

    /**
     * 添加评分
     */
    public void addScore(Integer score) {
        this.scoreCount = (this.scoreCount == null ? 0 : this.scoreCount) + 1;
        this.scoreTotal = (this.scoreTotal == null ? 0 : this.scoreTotal) + score;
    }

    /**
     * 更新作者信息（冗余字段）
     */
    public void updateAuthorInfo(String nickname, String avatar) {
        this.authorNickname = nickname;
        this.authorAvatar = avatar;
    }

    /**
     * 更新分类信息（冗余字段）
     */
    public void updateCategoryInfo(String categoryName) {
        this.categoryName = categoryName;
    }

    /**
     * 是否可以删除
     */
    public boolean canDelete() {
        return isDraft() || isRejected();
    }

    /**
     * 是否可以编辑
     */
    public boolean canEdit() {
        return isDraft() || isPendingReview();
    }

    /**
     * 是否可以发布
     */
    public boolean canPublish() {
        return isDraft() && isApproved();
    }
}