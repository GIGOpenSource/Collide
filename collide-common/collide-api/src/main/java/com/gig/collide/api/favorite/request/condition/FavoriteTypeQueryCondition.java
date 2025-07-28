package com.gig.collide.api.favorite.request.condition;

import com.gig.collide.api.favorite.constant.FavoriteType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * 按收藏类型查询条件
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
@Schema(description = "按收藏类型查询条件")
public class FavoriteTypeQueryCondition implements FavoriteQueryCondition {

    private static final long serialVersionUID = 1L;

    /**
     * 收藏类型
     */
    @NotNull(message = "收藏类型不能为空")
    @Schema(description = "收藏类型", required = true)
    private FavoriteType favoriteType;

    @Override
    public String getConditionType() {
        return "FAVORITE_TYPE";
    }

    @Override
    public boolean isValid() {
        return favoriteType != null;
    }
} 