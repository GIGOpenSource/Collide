package com.gig.collide.api.like.request;

import com.gig.collide.api.like.constant.TargetTypeEnum;
import com.gig.collide.base.request.BaseRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * 点赞排行榜请求
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
@Schema(description = "点赞排行榜请求")
public class LikeRankingRequest extends BaseRequest {

    /**
     * 目标类型
     */
    @Schema(description = "目标类型", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "目标类型不能为空")
    private TargetTypeEnum targetType;

    /**
     * 排行榜类型：TOTAL/TODAY/WEEK/MONTH
     */
    @Schema(description = "排行榜类型")
    private String rankingType = "TOTAL";

    /**
     * 排行榜大小
     */
    @Schema(description = "排行榜大小", example = "10")
    @Min(value = 1, message = "排行榜大小必须大于0")
    private Integer size = 10;
} 