package com.gig.collide.api.like.request;

import com.gig.collide.api.like.constant.TargetTypeEnum;
import com.gig.collide.base.request.BaseRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import jakarta.validation.constraints.Min;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 点赞统计查询请求
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
@Schema(description = "点赞统计查询请求")
public class LikeStatisticsQueryRequest extends BaseRequest {

    /**
     * 目标对象ID列表
     */
    @Schema(description = "目标对象ID列表")
    private List<Long> targetIds;

    /**
     * 目标类型
     */
    @Schema(description = "目标类型")
    private TargetTypeEnum targetType;

    /**
     * 开始时间
     */
    @Schema(description = "开始时间")
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    @Schema(description = "结束时间")
    private LocalDateTime endTime;

    /**
     * 最小点赞数
     */
    @Schema(description = "最小点赞数")
    private Long minLikeCount;

    /**
     * 最大点赞数
     */
    @Schema(description = "最大点赞数")
    private Long maxLikeCount;

    /**
     * 当前页码
     */
    @Schema(description = "当前页码", example = "1")
    @Min(value = 1, message = "页码必须大于0")
    private Integer currentPage = 1;

    /**
     * 每页大小
     */
    @Schema(description = "每页大小", example = "20")
    @Min(value = 1, message = "每页大小必须大于0")
    private Integer pageSize = 20;

    /**
     * 排序字段
     */
    @Schema(description = "排序字段")
    private String sortField = "total_like_count";

    /**
     * 排序方向
     */
    @Schema(description = "排序方向：ASC/DESC")
    private String sortDirection = "DESC";
} 