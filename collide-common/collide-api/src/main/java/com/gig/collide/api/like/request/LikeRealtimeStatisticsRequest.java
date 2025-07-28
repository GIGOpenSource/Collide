package com.gig.collide.api.like.request;

import com.gig.collide.api.like.constant.TargetTypeEnum;
import com.gig.collide.base.request.BaseRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * 实时点赞统计请求
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
@Schema(description = "实时点赞统计请求")
public class LikeRealtimeStatisticsRequest extends BaseRequest {

    /**
     * 目标对象ID列表
     */
    @Schema(description = "目标对象ID列表", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "目标对象ID列表不能为空")
    private List<Long> targetIds;

    /**
     * 目标类型
     */
    @Schema(description = "目标类型", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "目标类型不能为空")
    private TargetTypeEnum targetType;

    /**
     * 是否包含详细统计
     */
    @Schema(description = "是否包含详细统计")
    private Boolean includeDetails = false;
} 