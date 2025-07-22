package com.gig.collide.api.content.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 内容收藏请求
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Getter
@Setter
@NoArgsConstructor
public class ContentFavoriteRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 内容ID
     */
    @NotNull(message = "内容ID不能为空")
    private Long contentId;

    /**
     * 用户ID
     */
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    /**
     * 是否收藏（true-收藏，false-取消收藏）
     */
    @NotNull(message = "收藏状态不能为空")
    private Boolean favorited;

    /**
     * 收藏夹ID（可选）
     */
    private Long collectionId;

    /**
     * 收藏夹名称（可选）
     */
    private String collectionName;
} 