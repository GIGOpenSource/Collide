package com.gig.collide.social.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.gig.collide.api.social.constant.SocialPostType;
import com.gig.collide.api.social.constant.SocialPostStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 社交动态实体
 * 
 * <p>基于去连表化设计，包含作者信息等冗余字段以避免连表查询</p>
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Data
@TableName(value = "t_social_post", autoResultMap = true)
public class SocialPost {

    /**
     * 动态ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 动态类型
     */
    @TableField("post_type")
    private SocialPostType postType;

    /**
     * 动态内容
     */
    @TableField("content")
    private String content;

    /**
     * 媒体文件URL列表（JSON存储）
     */
    @TableField(value = "media_urls", typeHandler = JacksonTypeHandler.class)
    private List<String> mediaUrls;

    /**
     * 位置信息
     */
    @TableField("location")
    private String location;

    /**
     * 经度
     */
    @TableField("longitude")
    private Double longitude;

    /**
     * 纬度
     */
    @TableField("latitude")
    private Double latitude;

    /**
     * 话题标签列表（JSON存储）
     */
    @TableField(value = "topics", typeHandler = JacksonTypeHandler.class)
    private List<String> topics;

    /**
     * 提及的用户ID列表（JSON存储）
     */
    @TableField(value = "mentioned_user_ids", typeHandler = JacksonTypeHandler.class)
    private List<Long> mentionedUserIds;

    /**
     * 动态状态
     */
    @TableField("status")
    private SocialPostStatus status;

    /**
     * 可见性设置（0-公开，1-仅关注者，2-仅自己）
     */
    @TableField("visibility")
    private Integer visibility;

    /**
     * 是否允许评论
     */
    @TableField("allow_comments")
    private Boolean allowComments;

    /**
     * 是否允许转发
     */
    @TableField("allow_shares")
    private Boolean allowShares;

    // === 作者信息（冗余字段） ===

    /**
     * 作者用户ID
     */
    @TableField("author_id")
    private Long authorId;

    /**
     * 作者用户名（冗余）
     */
    @TableField("author_username")
    private String authorUsername;

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

    /**
     * 作者认证状态（冗余）
     */
    @TableField("author_verified")
    private Boolean authorVerified;

    // === 统计信息（冗余字段） ===

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
     * 转发数
     */
    @TableField("share_count")
    private Long shareCount;

    /**
     * 浏览数
     */
    @TableField("view_count")
    private Long viewCount;

    /**
     * 收藏数
     */
    @TableField("favorite_count")
    private Long favoriteCount;

    /**
     * 热度分数（用于热门排序）
     */
    @TableField("hot_score")
    private Double hotScore;

    // === 时间信息 ===

    /**
     * 创建时间
     */
    @TableField(value = "created_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(value = "updated_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 发布时间
     */
    @TableField("published_time")
    private LocalDateTime publishTime;

    /**
     * 逻辑删除标志
     */
    @TableLogic
    @TableField("deleted")
    private Boolean deleted;

    /**
     * 版本号（乐观锁）
     */
    @Version
    @TableField("version")
    private Integer version;
} 