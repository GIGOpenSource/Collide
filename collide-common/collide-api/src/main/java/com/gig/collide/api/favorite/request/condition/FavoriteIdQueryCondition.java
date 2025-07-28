package com.gig.collide.api.favorite.request.condition;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * 按收藏ID查询条件
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
@Schema(description = "按收藏ID查询条件")
public class FavoriteIdQueryCondition implements FavoriteQueryCondition {

    private static final long serialVersionUID = 1L;

    /**
     * 收藏ID
     */
    @NotNull(message = "收藏ID不能为空")
    @Schema(description = "收藏ID", required = true)
    private Long favoriteId;

    @Override
    public String getConditionType() {
        return "FAVORITE_ID";
    }

    @Override
    public boolean isValid() {
        return favoriteId != null && favoriteId > 0;
    }
} 