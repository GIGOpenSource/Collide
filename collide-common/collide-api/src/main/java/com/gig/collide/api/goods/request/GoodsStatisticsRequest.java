package com.gig.collide.api.goods.request;

import com.gig.collide.api.goods.constant.GoodsType;
import com.gig.collide.base.request.BaseRequest;
import lombok.*;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * 商品统计请求
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "商品统计请求")
public class GoodsStatisticsRequest extends BaseRequest {

    /**
     * 商品类型（可选，为空则统计所有类型）
     */
    @Schema(description = "商品类型")
    private GoodsType type;

    /**
     * 创建者ID（可选，为空则统计所有创建者）
     */
    @Schema(description = "创建者ID")
    private Long creatorId;

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
     * 是否包含销售统计
     */
    @Schema(description = "是否包含销售统计")
    private Boolean includeSalesStats = true;

    /**
     * 是否包含状态统计
     */
    @Schema(description = "是否包含状态统计")
    private Boolean includeStatusStats = true;

    /**
     * 是否包含推荐热门统计
     */
    @Schema(description = "是否包含推荐热门统计")
    private Boolean includeFeatureStats = true;

    // ===================== 便捷构造器 =====================

    /**
     * 全体统计
     */
    public static GoodsStatisticsRequest all() {
        return new GoodsStatisticsRequest();
    }

    /**
     * 按类型统计
     */
    public static GoodsStatisticsRequest byType(GoodsType type) {
        GoodsStatisticsRequest request = new GoodsStatisticsRequest();
        request.setType(type);
        return request;
    }

    /**
     * 按创建者统计
     */
    public static GoodsStatisticsRequest byCreator(Long creatorId) {
        GoodsStatisticsRequest request = new GoodsStatisticsRequest();
        request.setCreatorId(creatorId);
        return request;
    }

    /**
     * 按时间范围统计
     */
    public static GoodsStatisticsRequest byTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        GoodsStatisticsRequest request = new GoodsStatisticsRequest();
        request.setStartTime(startTime);
        request.setEndTime(endTime);
        return request;
    }
} 