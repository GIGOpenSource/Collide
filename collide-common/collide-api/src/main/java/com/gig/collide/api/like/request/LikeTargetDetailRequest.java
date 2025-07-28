package com.gig.collide.api.like.request;

import com.gig.collide.api.like.constant.TargetTypeEnum;
import com.gig.collide.base.request.BaseRequest;
import lombok.*;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 目标对象点赞详情请求
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
@Schema(description = "目标对象点赞详情请求")
public class LikeTargetDetailRequest extends BaseRequest {

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
     * 是否包含点赞用户列表
     */
    @Schema(description = "是否包含点赞用户列表")
    private Boolean includeUserList = false;

    /**
     * 用户列表页码（如果包含）
     */
    @Schema(description = "用户列表页码", example = "1")
    @Min(value = 1, message = "页码必须大于0")
    private Integer userListPage = 1;

    /**
     * 用户列表每页大小（如果包含）
     */
    @Schema(description = "用户列表每页大小", example = "20")
    @Min(value = 1, message = "每页大小必须大于0")
    private Integer userListPageSize = 20;

    /**
     * 是否包含统计详情
     */
    @Schema(description = "是否包含统计详情")
    private Boolean includeStatistics = true;
} 