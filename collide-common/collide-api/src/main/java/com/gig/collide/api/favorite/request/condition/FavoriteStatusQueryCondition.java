package com.gig.collide.api.favorite.request.condition;

import com.gig.collide.api.favorite.constant.FavoriteStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * 按收藏状态查询条件
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
@Schema(description = "按收藏状态查询条件")
public class FavoriteStatusQueryCondition implements FavoriteQueryCondition {

    private static final long serialVersionUID = 1L;

    /**
     * 收藏状态
     */
    @NotNull(message = "收藏状态不能为空")
    @Schema(description = "收藏状态", required = true)
    private FavoriteStatus status;

    @Override
    public String getConditionType() {
        return "FAVORITE_STATUS";
    }

    @Override
    public boolean isValid() {
        return status != null;
    }
} 