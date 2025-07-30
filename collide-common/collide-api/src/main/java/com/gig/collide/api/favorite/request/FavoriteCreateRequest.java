package com.gig.collide.api.favorite.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * 收藏创建请求 - 简洁版
 * 基于favorite-simple.sql的无连表设计，包含收藏对象和用户信息冗余
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
public class FavoriteCreateRequest implements Serializable {

    /**
     * 收藏类型：CONTENT、GOODS
     */
    @NotBlank(message = "收藏类型不能为空")
    private String favoriteType;

    /**
     * 收藏目标ID
     */
    @NotNull(message = "收藏目标ID不能为空")
    private Long targetId;

    /**
     * 收藏用户ID
     */
    @NotNull(message = "用户ID不能为空")
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

    /**
     * 状态（默认为active）
     */
    private String status = "active";
} 