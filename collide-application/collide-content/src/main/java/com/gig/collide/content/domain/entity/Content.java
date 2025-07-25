package com.gig.collide.content.domain.entity;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.annotation.*;
import com.gig.collide.api.content.constant.ContentStatus;
import com.gig.collide.api.content.constant.ContentType;
import com.gig.collide.api.content.constant.ReviewStatus;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 内容实体类
 * 对应 t_content 表
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Data
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
     * 内容描述/摘要
     */
    @TableField("description")
    private String description;

    /**
     * 内容类型
     */
    @TableField("content_type")
    private ContentType contentType;

    /**
     * 内容数据（JSON格式）
     */
    @TableField("content_data")
    private String contentData;

    /**
     * 封面图片URL
     */
    @TableField("cover_url")
    private String coverUrl;

    /**
     * 作者用户ID
     */
    @TableField("author_id")
    private Long authorId;

    /**
     * 分类ID
     */
    @TableField("category_id")
    private Long categoryId;

    /**
     * 标签（JSON数组格式存储）
     */
    @TableField("tags")
    private String tags;

    /**
     * 内容状态
     */
    @TableField("status")
    private ContentStatus status;

    /**
     * 审核状态
     */
    @TableField("review_status")
    private ReviewStatus reviewStatus;

    /**
     * 审核意见
     */
    @TableField("review_comment")
    private String reviewComment;

    /**
     * 审核员ID
     */
    @TableField("reviewer_id")
    private Long reviewerId;

    /**
     * 审核时间
     */
    @TableField("reviewed_time")
    private LocalDateTime reviewedTime;

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
     * 点踩数
     */
    @TableField("dislike_count")
    private Long dislikeCount;

    /**
     * 评论数
     */
    @TableField("comment_count")
    private Long commentCount;

    /**
     * 分享数
     */
    @TableField("share_count")
    private Long shareCount;

    /**
     * 收藏数
     */
    @TableField("favorite_count")
    private Long favoriteCount;

    /**
     * 权重分数（用于推荐算法）
     */
    @TableField("weight_score")
    private Double weightScore;

    /**
     * 是否推荐
     */
    @TableField("is_recommended")
    private Boolean isRecommended;

    /**
     * 是否置顶
     */
    @TableField("is_pinned")
    private Boolean isPinned;

    /**
     * 是否允许评论
     */
    @TableField("allow_comment")
    private Boolean allowComment;

    /**
     * 是否允许分享
     */
    @TableField("allow_share")
    private Boolean allowShare;

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

    /**
     * 发布时间
     */
    @TableField("published_time")
    private LocalDateTime publishedTime;

    /**
     * 逻辑删除标记
     */
    @TableLogic
    @TableField("deleted")
    private Integer deleted;

    // 业务方法

    /**
     * 获取内容数据的JSON对象
     */
    public JSONObject getContentDataJson() {
        if (contentData == null || contentData.trim().isEmpty()) {
            return new JSONObject();
        }
        try {
            return JSONObject.parseObject(contentData);
        } catch (Exception e) {
            return new JSONObject();
        }
    }

    /**
     * 设置内容数据JSON对象
     */
    public void setContentDataJson(JSONObject contentDataJson) {
        if (contentDataJson == null) {
            this.contentData = null;
        } else {
            this.contentData = contentDataJson.toJSONString();
        }
    }

    /**
     * 获取标签列表
     */
    public java.util.List<String> getTagList() {
        if (tags == null || tags.trim().isEmpty()) {
            return new java.util.ArrayList<>();
        }
        try {
            return com.alibaba.fastjson2.JSON.parseArray(tags, String.class);
        } catch (Exception e) {
            return new java.util.ArrayList<>();
        }
    }

    /**
     * 设置标签列表
     */
    public void setTagList(java.util.List<String> tagList) {
        if (tagList == null || tagList.isEmpty()) {
            this.tags = null;
        } else {
            this.tags = com.alibaba.fastjson2.JSON.toJSONString(tagList);
        }
    }

    /**
     * 判断内容是否可见
     */
    public boolean isVisible() {
        return status == ContentStatus.PUBLISHED && reviewStatus == ReviewStatus.APPROVED;
    }

    /**
     * 判断内容是否可编辑
     */
    public boolean isEditable() {
        return status == ContentStatus.DRAFT || status == ContentStatus.REJECTED;
    }

    /**
     * 判断内容是否可删除
     */
    public boolean isDeletable() {
        // 只有草稿状态和被拒绝状态的内容可以删除
        // 已发布的内容不允许删除，只能下线
        return status == ContentStatus.DRAFT || status == ContentStatus.REJECTED;
    }

    /**
     * 计算总互动数
     */
    public Long getTotalInteractions() {
        return (likeCount != null ? likeCount : 0L)
             + (commentCount != null ? commentCount : 0L)
             + (shareCount != null ? shareCount : 0L)
             + (favoriteCount != null ? favoriteCount : 0L)
             - (dislikeCount != null ? dislikeCount : 0L);
    }
} 