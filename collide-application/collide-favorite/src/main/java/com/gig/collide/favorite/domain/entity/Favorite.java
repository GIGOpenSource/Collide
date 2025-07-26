package com.gig.collide.favorite.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.gig.collide.api.favorite.constant.FavoriteType;
import com.gig.collide.api.favorite.constant.FavoriteStatus;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 收藏实体
 * 
 * <p>基于去连表化设计，包含目标信息冗余字段</p>
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Data
@TableName("t_favorite")
public class Favorite implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 收藏ID
     */
    @TableId(value = "favorite_id", type = IdType.ASSIGN_ID)
    private Long favoriteId;

    /**
     * 收藏类型
     */
    @TableField("favorite_type")
    private FavoriteType favoriteType;

    /**
     * 目标ID（内容ID、用户ID等）
     */
    @TableField("target_id")
    private Long targetId;

    /**
     * 收藏用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 收藏夹ID
     */
    @TableField("folder_id")
    private Long folderId;

    /**
     * 收藏状态
     */
    @TableField("status")
    private FavoriteStatus status;

    /**
     * 收藏备注
     */
    @TableField("remark")
    private String remark;

    /**
     * 收藏时间
     */
    @TableField("favorite_time")
    private LocalDateTime favoriteTime;

    // === 去连表化冗余字段 ===

    /**
     * 目标标题（冗余字段）
     */
    @TableField("target_title")
    private String targetTitle;

    /**
     * 目标封面/头像（冗余字段）
     */
    @TableField("target_cover")
    private String targetCover;

    /**
     * 目标作者ID（冗余字段）
     */
    @TableField("target_author_id")
    private Long targetAuthorId;

    /**
     * 目标作者名称（冗余字段）
     */
    @TableField("target_author_name")
    private String targetAuthorName;

    /**
     * 目标作者头像（冗余字段）
     */
    @TableField("target_author_avatar")
    private String targetAuthorAvatar;

    /**
     * 目标发布时间（冗余字段）
     */
    @TableField("target_publish_time")
    private LocalDateTime targetPublishTime;

    /**
     * 目标摘要/描述（冗余字段）
     */
    @TableField("target_summary")
    private String targetSummary;

    // === 审计字段 ===

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
     * 是否删除（0-否，1-是）
     */
    @TableLogic
    @TableField("is_deleted")
    private Integer isDeleted;

    /**
     * 版本号（乐观锁）
     */
    @Version
    @TableField("version")
    private Integer version;
} 