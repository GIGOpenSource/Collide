package com.gig.collide.favorite.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * 收藏实体类 - 简洁版
 * 基于favorite-simple.sql的t_favorite表结构
 * 采用无连表设计，包含收藏对象和用户信息冗余字段
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
@TableName("t_favorite")
public class Favorite {

    /**
     * 收藏ID - 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 收藏类型：CONTENT、GOODS
     */
    @TableField("favorite_type")
    private String favoriteType;

    /**
     * 收藏目标ID
     */
    @TableField("target_id")
    private Long targetId;

    /**
     * 收藏用户ID
     */
    @TableField("user_id")
    private Long userId;

    // =================== 收藏对象信息（冗余字段，避免连表） ===================

    /**
     * 收藏对象标题（冗余）
     */
    @TableField("target_title")
    private String targetTitle;

    /**
     * 收藏对象封面（冗余）
     */
    @TableField("target_cover")
    private String targetCover;

    /**
     * 收藏对象作者ID（冗余）
     */
    @TableField("target_author_id")
    private Long targetAuthorId;

    // =================== 用户信息（冗余字段，避免连表） ===================

    /**
     * 用户昵称（冗余）
     */
    @TableField("user_nickname")
    private String userNickname;

    /**
     * 状态：active、cancelled
     */
    @TableField("status")
    private String status;

    /**
     * 创建时间 - 自动填充
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间 - 自动填充
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    // =================== 业务方法 ===================

    /**
     * 判断是否为活跃收藏
     */
    public boolean isActive() {
        return "active".equals(status);
    }

    /**
     * 判断是否已取消收藏
     */
    public boolean isCancelled() {
        return "cancelled".equals(status);
    }

    /**
     * 激活收藏
     */
    public void activate() {
        this.status = "active";
    }

    /**
     * 取消收藏
     */
    public void cancel() {
        this.status = "cancelled";
    }

    /**
     * 是否为内容类型收藏
     */
    public boolean isContentFavorite() {
        return "CONTENT".equals(favoriteType);
    }

    /**
     * 是否为商品类型收藏
     */
    public boolean isGoodsFavorite() {
        return "GOODS".equals(favoriteType);
    }

    /**
     * 获取收藏天数
     */
    public long getFavoriteDays() {
        if (createTime == null) {
            return 0;
        }
        return java.time.temporal.ChronoUnit.DAYS.between(createTime, LocalDateTime.now());
    }

    /**
     * 检查收藏是否有效
     */
    public boolean isValidFavorite() {
        return userId != null && targetId != null && favoriteType != null;
    }

    /**
     * 更新收藏对象信息
     */
    public void updateTargetInfo(String title, String cover, Long authorId) {
        this.targetTitle = title;
        this.targetCover = cover;
        this.targetAuthorId = authorId;
    }

    /**
     * 更新用户信息
     */
    public void updateUserInfo(String nickname) {
        this.userNickname = nickname;
    }

    /**
     * 创建收藏的唯一标识
     */
    public String createUniqueKey() {
        return String.format("%s_%s_%s", userId, favoriteType, targetId);
    }
}