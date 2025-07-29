package com.gig.collide.api.favorite.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 取消收藏请求 - 简洁版
 * 逻辑删除，将状态更新为cancelled
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
public class FavoriteDeleteRequest {

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

    /**
     * 取消收藏原因
     */
    private String cancelReason;

    /**
     * 操作人ID（一般与用户ID相同）
     */
    private Long operatorId;
} 