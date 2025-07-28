package com.gig.collide.api.like.request;

import com.gig.collide.base.request.BaseRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import jakarta.validation.constraints.NotNull;

/**
 * 用户点赞统计请求
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
@Schema(description = "用户点赞统计请求")
public class LikeUserStatisticsRequest extends BaseRequest {

    /**
     * 用户ID
     */
    @Schema(description = "用户ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    /**
     * 是否包含详细统计
     */
    @Schema(description = "是否包含详细统计")
    private Boolean includeDetails = false;
} 