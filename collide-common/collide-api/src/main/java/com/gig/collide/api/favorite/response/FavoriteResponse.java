package com.gig.collide.api.favorite.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 收藏统一响应对象 - 简洁版
 * 基于favorite-simple.sql的字段结构，包含所有冗余信息
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
public class FavoriteResponse implements Serializable {

    /**
     * 收藏ID
     */
    private Long id;

    /**
     * 收藏类型：CONTENT、GOODS
     */
    private String favoriteType;

    /**
     * 收藏目标ID
     */
    private Long targetId;

    /**
     * 收藏用户ID
     */
    private Long userId;

    // =================== 收藏对象信息（冗余字段） ===================

    /**
     * 收藏对象标题（冗余）
     */
    private String targetTitle;

    /**
     * 收藏对象封面（冗余）
     */
    private String targetCover;

    /**
     * 收藏对象作者ID（冗余）
     */
    private Long targetAuthorId;

    // =================== 用户信息（冗余字段） ===================

    /**
     * 用户昵称（冗余）
     */
    private String userNickname;

    // =================== 状态和时间信息 ===================

    /**
     * 状态：active、cancelled
     */
    private String status;

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
     * 是否为活跃收藏
     */
    @JsonIgnore
    public boolean isActive() {
        return "active".equals(status);
    }

    /**
     * 是否已取消收藏
     */
    @JsonIgnore
    public boolean isCancelled() {
        return "cancelled".equals(status);
    }

    /**
     * 获取收藏天数
     */
    @JsonIgnore
    public long getFavoriteDays() {
        if (createTime == null) {
            return 0;
        }
        return java.time.temporal.ChronoUnit.DAYS.between(createTime, LocalDateTime.now());
    }

    /**
     * 是否为内容类型收藏
     */
    @JsonIgnore
    public boolean isContentFavorite() {
        return "CONTENT".equals(favoriteType);
    }

    /**
     * 是否为商品类型收藏
     */
    @JsonIgnore
    public boolean isGoodsFavorite() {
        return "GOODS".equals(favoriteType);
    }
}