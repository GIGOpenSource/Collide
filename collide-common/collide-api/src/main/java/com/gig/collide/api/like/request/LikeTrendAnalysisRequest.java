package com.gig.collide.api.like.request;

import com.gig.collide.api.like.constant.TargetTypeEnum;
import com.gig.collide.base.request.BaseRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * 点赞趋势分析请求
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
@Schema(description = "点赞趋势分析请求")
public class LikeTrendAnalysisRequest extends BaseRequest {

    /**
     * 目标对象ID
     */
    @Schema(description = "目标对象ID")
    private Long targetId;

    /**
     * 目标类型
     */
    @Schema(description = "目标类型", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "目标类型不能为空")
    private TargetTypeEnum targetType;

    /**
     * 开始时间
     */
    @Schema(description = "开始时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "开始时间不能为空")
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    @Schema(description = "结束时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "结束时间不能为空")
    private LocalDateTime endTime;

    /**
     * 时间间隔类型：HOUR/DAY/WEEK/MONTH
     */
    @Schema(description = "时间间隔类型")
    private String intervalType = "DAY";
} 