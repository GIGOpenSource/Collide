package com.gig.collide.api.favorite.request;

import com.gig.collide.api.favorite.constant.FavoriteType;
import com.gig.collide.base.request.BaseRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 收藏统计请求
 * 
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "收藏统计请求")
public class FavoriteStatisticsRequest extends BaseRequest {

    private static final long serialVersionUID = 1L;

    /**
     * 收藏类型列表
     */
    @Schema(description = "收藏类型列表")
    private List<FavoriteType> favoriteTypes;

    /**
     * 目标ID列表
     */
    @Schema(description = "目标ID列表")
    private List<Long> targetIds;

    /**
     * 用户ID列表
     */
    @Schema(description = "用户ID列表")
    private List<Long> userIds;

    /**
     * 统计开始时间
     */
    @Schema(description = "统计开始时间")
    private LocalDateTime startTime;

    /**
     * 统计结束时间
     */
    @Schema(description = "统计结束时间")
    private LocalDateTime endTime;

    /**
     * 是否按类型分组
     */
    @Schema(description = "是否按类型分组", example = "true")
    private Boolean groupByType = true;

    /**
     * 是否按目标分组
     */
    @Schema(description = "是否按目标分组", example = "false")
    private Boolean groupByTarget = false;

    /**
     * 是否按用户分组
     */
    @Schema(description = "是否按用户分组", example = "false")
    private Boolean groupByUser = false;

    /**
     * 是否按时间分组
     */
    @Schema(description = "是否按时间分组", example = "false")
    private Boolean groupByTime = false;
} 