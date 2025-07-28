package com.gig.collide.api.favorite.response.data;

import com.gig.collide.api.favorite.constant.FavoriteType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 收藏统计信息数据传输对象
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
@EqualsAndHashCode(callSuper = false)
@Schema(description = "收藏统计信息")
public class FavoriteStatisticsInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 收藏类型
     */
    @Schema(description = "收藏类型")
    private FavoriteType favoriteType;

    /**
     * 目标ID
     */
    @Schema(description = "目标ID")
    private Long targetId;

    /**
     * 收藏总数
     */
    @Schema(description = "收藏总数")
    private Long favoriteCount;

    /**
     * 收藏用户数
     */
    @Schema(description = "收藏用户数")
    private Long userCount;

    /**
     * 首次收藏时间
     */
    @Schema(description = "首次收藏时间")
    private LocalDateTime firstFavoriteTime;

    /**
     * 最新收藏时间
     */
    @Schema(description = "最新收藏时间")
    private LocalDateTime lastFavoriteTime;

    /**
     * 今日收藏数
     */
    @Schema(description = "今日收藏数")
    private Long todayCount;

    /**
     * 本周收藏数
     */
    @Schema(description = "本周收藏数")
    private Long weekCount;

    /**
     * 本月收藏数
     */
    @Schema(description = "本月收藏数")
    private Long monthCount;

    /**
     * 统计时间
     */
    @Schema(description = "统计时间")
    private LocalDateTime statisticsTime;
} 