package com.gig.collide.api.favorite.request.condition;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * 按目标ID查询收藏条件
 * 
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "按目标ID查询收藏条件")
public class FavoriteTargetIdQueryCondition implements FavoriteQueryCondition {

    private static final long serialVersionUID = 1L;

    /**
     * 目标ID
     */
    @NotNull(message = "目标ID不能为空")
    @Schema(description = "目标ID", required = true)
    private Long targetId;

    @Override
    public String getConditionType() {
        return "TARGET_ID";
    }

    @Override
    public boolean isValid() {
        return targetId != null && targetId > 0;
    }
} 