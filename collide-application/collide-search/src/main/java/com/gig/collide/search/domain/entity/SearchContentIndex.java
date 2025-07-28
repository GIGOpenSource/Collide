package com.gig.collide.search.domain.entity;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 搜索内容索引表实体
 * 
 * @author Collide Team
 * @version 1.0.0
 * @since 2024-01-01
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_search_content_index")
public class SearchContentIndex implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 索引ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 内容ID
     */
    @TableField("content_id")
    private Long contentId;

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
     * 内容类型：NOVEL/COMIC/SHORT_VIDEO/LONG_VIDEO/ARTICLE/AUDIO
     */
    @TableField("content_type")
    private String contentType;

    /**
     * 封面图片URL
     */
    @TableField("cover_url")
    private String coverUrl;

    /**
     * 内容文本（用于全文搜索）
     */
    @TableField("content_text")
    private String contentText;

    /**
     * 作者用户ID
     */
    @TableField("author_id")
    private Long authorId;

    /**
     * 作者昵称
     */
    @TableField("author_nickname")
    private String authorNickname;

    /**
     * 作者头像URL
     */
    @TableField("author_avatar")
    private String authorAvatar;

    /**
     * 作者角色：user/vip/blogger/admin
     */
    @TableField("author_role")
    private String authorRole;

    /**
     * 作者是否认证：0-未认证，1-已认证
     */
    @TableField("author_verified")
    private Integer authorVerified;

    /**
     * 分类ID
     */
    @TableField("category_id")
    private Long categoryId;

    /**
     * 分类名称
     */
    @TableField("category_name")
    private String categoryName;

    /**
     * 分类路径（如：科技/人工智能/机器学习）
     */
    @TableField("category_path")
    private String categoryPath;

    /**
     * 标签列表，JSON数组格式
     */
    @TableField("tags")
    private String tags;

    /**
     * 标签名称（用于搜索，逗号分隔）
     */
    @TableField("tag_names")
    private String tagNames;

    /**
     * 内容状态：DRAFT/PENDING/PUBLISHED/REJECTED/OFFLINE
     */
    @TableField("status")
    private String status;

    /**
     * 审核状态：PENDING/APPROVED/REJECTED
     */
    @TableField("review_status")
    private String reviewStatus;

    /**
     * 是否置顶：0-否，1-是
     */
    @TableField("is_top")
    private Integer isTop;

    /**
     * 是否热门：0-否，1-是
     */
    @TableField("is_hot")
    private Integer isHot;

    /**
     * 是否推荐：0-否，1-是
     */
    @TableField("is_recommended")
    private Integer isRecommended;

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
     * 内容质量评分
     */
    @TableField("quality_score")
    private BigDecimal qualityScore;

    /**
     * 热度评分
     */
    @TableField("popularity_score")
    private BigDecimal popularityScore;

    /**
     * 搜索权重
     */
    @TableField("search_weight")
    private Double searchWeight;

    /**
     * 相关度评分
     */
    @TableField("relevance_score")
    private Double relevanceScore;

    /**
     * 发布时间
     */
    @TableField("published_time")
    private LocalDateTime publishedTime;

    /**
     * 最后修改时间
     */
    @TableField("last_modified_time")
    private LocalDateTime lastModifiedTime;

    /**
     * 关键词（用于全文搜索）
     */
    @TableField("keywords")
    private String keywords;

    /**
     * 搜索加权系数
     */
    @TableField("search_boost")
    private Double searchBoost;

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
     * 逻辑删除：0-未删除，1-已删除
     */
    @TableField("deleted")
    @TableLogic
    private Integer deleted;

    /**
     * 获取标签数组
     */
    public String[] getTagsArray() {
        if (tags == null) {
            return new String[0];
        }
        return JSON.parseObject(tags, String[].class);
    }

    /**
     * 设置标签数组
     */
    public void setTagsArray(String[] tagsArray) {
        this.tags = tagsArray == null ? null : JSON.toJSONString(tagsArray);
    }
} 