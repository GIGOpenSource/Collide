package com.gig.collide.api.like.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * 点赞统一响应对象 - 简洁版
 * 基于like-simple.sql的字段结构，包含所有冗余信息
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
public class LikeResponse {

    /**
     * 点赞ID
     */
    private Long id;

    /**
     * 点赞类型：CONTENT、COMMENT、DYNAMIC
     */
    private String likeType;

    /**
     * 目标对象ID
     */
    private Long targetId;

    /**
     * 点赞用户ID
     */
    private Long userId;

    // =================== 目标对象信息（冗余字段） ===================
    
    /**
     * 目标对象标题（冗余）
     */
    private String targetTitle;

    /**
     * 目标对象作者ID（冗余）
     */
    private Long targetAuthorId;

    // =================== 用户信息（冗余字段） ===================
    
    /**
     * 用户昵称（冗余）
     */
    private String userNickname;

    /**
     * 用户头像（冗余）
     */
    private String userAvatar;

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
} 