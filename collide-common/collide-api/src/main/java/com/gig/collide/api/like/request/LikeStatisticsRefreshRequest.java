package com.gig.collide.api.like.request;

import com.gig.collide.api.like.constant.TargetTypeEnum;
import com.gig.collide.base.request.BaseRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * 点赞统计刷新请求
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
@Schema(description = "点赞统计刷新请求")
public class LikeStatisticsRefreshRequest extends BaseRequest {

    /**
     * 目标对象ID
     */
    @Schema(description = "目标对象ID")
    private Long targetId;

    /**
     * 目标类型
     */
    @Schema(description = "目标类型")
    private TargetTypeEnum targetType;
} 