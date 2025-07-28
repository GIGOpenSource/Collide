package com.gig.collide.api.like.request;

import com.gig.collide.api.like.constant.ActionTypeEnum;
import com.gig.collide.api.like.constant.TargetTypeEnum;
import com.gig.collide.base.request.BaseRequest;
import lombok.*;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * 用户点赞历史请求
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
@Schema(description = "用户点赞历史请求")
public class LikeUserHistoryRequest extends BaseRequest {

    /**
     * 用户ID
     */
    @Schema(description = "用户ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    /**
     * 目标类型（可选，为空则查询所有类型）
     */
    @Schema(description = "目标类型")
    private TargetTypeEnum targetType;

    /**
     * 操作类型（可选，为空则查询所有操作）
     */
    @Schema(description = "操作类型")
    private ActionTypeEnum actionType;

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
    @Schema(description = "排序字段：created_time/updated_time")
    private String sortField = "created_time";

    /**
     * 排序方向
     */
    @Schema(description = "排序方向：ASC/DESC")
    private String sortDirection = "DESC";
} 