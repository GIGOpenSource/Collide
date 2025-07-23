package com.gig.collide.api.favorite.request;

import com.gig.collide.api.favorite.constant.FavoriteType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 收藏操作请求
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Data
@Schema(description = "收藏操作请求")
public class FavoriteRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 收藏类型
     */
    @NotNull(message = "收藏类型不能为空")
    @Schema(description = "收藏类型", example = "CONTENT")
    private FavoriteType favoriteType;

    /**
     * 目标ID（内容ID、用户ID等）
     */
    @NotNull(message = "目标ID不能为空")
    @Schema(description = "目标ID", example = "123")
    private Long targetId;

    /**
     * 收藏用户ID
     */
    @NotNull(message = "用户ID不能为空")
    @Schema(description = "收藏用户ID", example = "456")
    private Long userId;

    /**
     * 收藏夹ID（可选，不指定则收藏到默认收藏夹）
     */
    @Schema(description = "收藏夹ID", example = "789")
    private Long folderId;

    /**
     * 是否收藏（true=收藏，false=取消收藏）
     */
    @NotNull(message = "收藏操作不能为空")
    @Schema(description = "是否收藏", example = "true")
    private Boolean isFavorite;

    /**
     * 收藏备注（可选）
     */
    @Schema(description = "收藏备注", example = "很棒的内容")
    private String remark;
} 