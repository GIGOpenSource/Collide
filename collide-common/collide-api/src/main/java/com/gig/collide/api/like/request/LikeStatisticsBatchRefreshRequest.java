package com.gig.collide.api.like.request;

import com.gig.collide.api.like.constant.TargetTypeEnum;
import com.gig.collide.base.request.BaseRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

/**
 * 批量点赞统计刷新请求
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
@Schema(description = "批量点赞统计刷新请求")
public class LikeStatisticsBatchRefreshRequest extends BaseRequest {

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
} 