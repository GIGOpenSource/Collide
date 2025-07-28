package com.gig.collide.social.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.gig.collide.datasource.domain.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName(value = "t_social_post", autoResultMap = true)
public class SocialPost extends BaseEntity {
    private static final long serialVersionUID = 1L;

    @TableField("post_type")
    private String postType;

    @TableField("title")
    private String title;

    @TableField("content")
    private String content;

    @TableField(value = "media_urls", typeHandler = JacksonTypeHandler.class)
    private List<String> mediaUrls;

    @TableField("author_id")
    private Long authorId;

    @TableField("author_username")
    private String authorUsername;

    @TableField("author_nickname")
    private String authorNickname;

    @TableField("author_avatar")
    private String authorAvatar;

    @TableField("author_verified")
    private Boolean authorVerified;

    @TableField("visibility")
    private String visibility;

    @TableField("status")
    private String status;

    @TableField("published_time")
    private LocalDateTime publishedTime;

    @TableField("location")
    private String location;

    @TableField("source_platform")
    private String sourcePlatform;

    @TableField("original_post_id")
    private Long originalPostId;

    @TableField("like_count")
    private Long likeCount;

    @TableField("comment_count")
    private Long commentCount;

    @TableField("share_count")
    private Long shareCount;

    @TableField("view_count")
    private Long viewCount;

    @TableField("favorite_count")
    private Long favoriteCount;

    @TableField("hot_score")
    private BigDecimal hotScore;

    @TableField(value = "tags", typeHandler = JacksonTypeHandler.class)
    private List<String> tags;

    @TableField(value = "mentions", typeHandler = JacksonTypeHandler.class)
    private List<String> mentions;

    @TableField(value = "hashtags", typeHandler = JacksonTypeHandler.class)
    private List<String> hashtags;

    @TableField(value = "metadata", typeHandler = JacksonTypeHandler.class)
    private Object metadata;

    @TableField("language")
    private String language;

    @TableField("ip_address")
    private String ipAddress;

    @TableField("user_agent")
    private String userAgent;

    @TableField("audit_status")
    private String auditStatus;

    @TableField("audit_time")
    private LocalDateTime auditTime;

    @TableField("audit_remark")
    private String auditRemark;
} 