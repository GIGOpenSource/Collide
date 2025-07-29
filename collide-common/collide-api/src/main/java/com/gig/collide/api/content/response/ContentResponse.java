package com.gig.collide.api.content.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * 内容统一响应对象 - 简洁版
 * 基于content-simple.sql的字段结构，包含所有冗余信息和评分功能
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
public class ContentResponse {

    /**
     * 内容ID
     */
    private Long id;

    /**
     * 内容标题
     */
    private String title;

    /**
     * 内容描述
     */
    private String description;

    /**
     * 内容类型：NOVEL、COMIC、VIDEO、ARTICLE、AUDIO
     */
    private String contentType;

    /**
     * 内容数据，JSON格式
     */
    private String contentData;

    /**
     * 封面图片URL
     */
    private String coverUrl;

    /**
     * 标签，JSON数组格式
     */
    private String tags;

    // =================== 作者信息（冗余字段） ===================

    /**
     * 作者用户ID
     */
    private Long authorId;

    /**
     * 作者昵称（冗余）
     */
    private String authorNickname;

    /**
     * 作者头像URL（冗余）
     */
    private String authorAvatar;

    // =================== 分类信息（冗余字段） ===================

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 分类名称（冗余）
     */
    private String categoryName;

    // =================== 状态相关字段 ===================

    /**
     * 状态：DRAFT、PUBLISHED、OFFLINE
     */
    private String status;

    /**
     * 审核状态：PENDING、APPROVED、REJECTED
     */
    private String reviewStatus;

    // =================== 统计字段（冗余存储） ===================

    /**
     * 查看数
     */
    private Long viewCount;

    /**
     * 点赞数
     */
    private Long likeCount;

    /**
     * 评论数
     */
    private Long commentCount;

    /**
     * 收藏数
     */
    private Long favoriteCount;

    /**
     * 评分数
     */
    private Long scoreCount;

    /**
     * 总评分
     */
    private Long scoreTotal;

    // =================== 时间字段 ===================

    /**
     * 发布时间
     */
    private LocalDateTime publishTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    // =================== 计算属性 ===================

    /**
     * 是否为草稿状态
     */
    @JsonIgnore
    public boolean isDraft() {
        return "DRAFT".equals(status);
    }

    /**
     * 是否已发布
     */
    @JsonIgnore
    public boolean isPublished() {
        return "PUBLISHED".equals(status);
    }

    /**
     * 是否已下线
     */
    @JsonIgnore
    public boolean isOffline() {
        return "OFFLINE".equals(status);
    }

    /**
     * 是否待审核
     */
    @JsonIgnore
    public boolean isPendingReview() {
        return "PENDING".equals(reviewStatus);
    }

    /**
     * 是否审核通过
     */
    @JsonIgnore
    public boolean isApproved() {
        return "APPROVED".equals(reviewStatus);
    }

    /**
     * 是否审核被拒
     */
    @JsonIgnore
    public boolean isRejected() {
        return "REJECTED".equals(reviewStatus);
    }

    /**
     * 获取平均评分
     */
    @JsonIgnore
    public Double getAverageScore() {
        if (scoreCount == null || scoreCount == 0 || scoreTotal == null) {
            return 0.0;
        }
        return (double) scoreTotal / scoreCount;
    }

    /**
     * 获取评分显示（保留1位小数）
     */
    @JsonIgnore
    public String getScoreDisplay() {
        return String.format("%.1f", getAverageScore());
    }

    /**
     * 是否为小说类型
     */
    @JsonIgnore
    public boolean isNovel() {
        return "NOVEL".equals(contentType);
    }

    /**
     * 是否为漫画类型
     */
    @JsonIgnore
    public boolean isComic() {
        return "COMIC".equals(contentType);
    }

    /**
     * 是否为视频类型
     */
    @JsonIgnore
    public boolean isVideo() {
        return "VIDEO".equals(contentType);
    }

    /**
     * 是否为文章类型
     */
    @JsonIgnore
    public boolean isArticle() {
        return "ARTICLE".equals(contentType);
    }

    /**
     * 是否为音频类型
     */
    @JsonIgnore
    public boolean isAudio() {
        return "AUDIO".equals(contentType);
    }

    /**
     * 是否需要章节管理
     */
    @JsonIgnore
    public boolean needsChapterManagement() {
        return isNovel() || isComic();
    }

    /**
     * 获取内容创建天数
     */
    @JsonIgnore
    public long getCreateDays() {
        if (createTime == null) {
            return 0;
        }
        return java.time.temporal.ChronoUnit.DAYS.between(createTime, LocalDateTime.now());
    }

    /**
     * 获取发布天数
     */
    @JsonIgnore
    public long getPublishDays() {
        if (publishTime == null) {
            return 0;
        }
        return java.time.temporal.ChronoUnit.DAYS.between(publishTime, LocalDateTime.now());
    }
}