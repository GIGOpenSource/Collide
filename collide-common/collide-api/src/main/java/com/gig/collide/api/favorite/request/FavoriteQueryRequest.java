package com.gig.collide.api.favorite.request;

import com.gig.collide.api.favorite.constant.FavoriteType;
import com.gig.collide.api.favorite.constant.FavoriteStatus;
import com.gig.collide.base.request.PageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 收藏查询请求
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "收藏查询请求")
public class FavoriteQueryRequest extends PageRequest {

    private static final long serialVersionUID = 1L;

    /**
     * 收藏ID（查询单个收藏时使用）
     */
    @Schema(description = "收藏ID", example = "123")
    private Long favoriteId;

    /**
     * 收藏类型
     */
    @Schema(description = "收藏类型", example = "CONTENT")
    private FavoriteType favoriteType;

    /**
     * 收藏用户ID
     */
    @Schema(description = "收藏用户ID", example = "456")
    private Long userId;

    /**
     * 目标ID（内容ID、用户ID等）
     */
    @Schema(description = "目标ID", example = "789")
    private Long targetId;

    /**
     * 收藏夹ID
     */
    @Schema(description = "收藏夹ID", example = "101112")
    private Long folderId;

    /**
     * 收藏状态
     */
    @Schema(description = "收藏状态", example = "NORMAL")
    private FavoriteStatus status;

    /**
     * 查询用户ID（用于权限判断）
     */
    @Schema(description = "查询用户ID", example = "456")
    private Long currentUserId;

    /**
     * 排序方式（time: 时间排序）
     */
    @Schema(description = "排序方式", example = "time")
    private String sortBy = "time";

    /**
     * 排序方向（asc: 升序, desc: 降序）
     */
    @Schema(description = "排序方向", example = "desc")
    private String sortOrder = "desc";
} 