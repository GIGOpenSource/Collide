package com.gig.collide.api.like.request;

import com.gig.collide.api.like.constant.TargetTypeEnum;
import com.gig.collide.base.request.BaseRequest;
import lombok.*;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * 批量检查用户点赞状态请求
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
@Schema(description = "批量检查用户点赞状态请求")
public class LikeBatchCheckRequest extends BaseRequest {

    /**
     * 用户ID
     */
    @Schema(description = "用户ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    /**
     * 目标对象ID列表
     */
    @Schema(description = "目标对象ID列表", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "目标对象ID列表不能为空")
    @Size(max = 100, message = "单次批量查询不能超过100个")
    private List<Long> targetIds;

    /**
     * 目标类型
     */
    @Schema(description = "目标类型", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "目标类型不能为空")
    private TargetTypeEnum targetType;

    /**
     * 是否包含统计信息
     */
    @Schema(description = "是否包含统计信息")
    private Boolean includeStatistics = false;
} 