package com.gig.collide.api.like.request;

import com.gig.collide.api.like.constant.TargetTypeEnum;
import com.gig.collide.base.request.BaseRequest;
import lombok.*;

import jakarta.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 切换点赞状态请求（如果已点赞则取消，如果未点赞则点赞）
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
@Schema(description = "切换点赞状态请求")
public class LikeToggleRequest extends BaseRequest {

    /**
     * 用户ID
     */
    @Schema(description = "用户ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    /**
     * 目标对象ID
     */
    @Schema(description = "目标对象ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "目标对象ID不能为空")
    private Long targetId;

    /**
     * 目标类型
     */
    @Schema(description = "目标类型", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "目标类型不能为空")
    private TargetTypeEnum targetType;

    /**
     * IP地址
     */
    @Schema(description = "IP地址")
    private String ipAddress;

    /**
     * 设备信息
     */
    @Schema(description = "设备信息")
    private String deviceInfo;
} 