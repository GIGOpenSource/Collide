package com.gig.collide.api.favorite.request.condition;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * 按收藏夹ID查询收藏条件
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
@Schema(description = "按收藏夹ID查询收藏条件")
public class FavoriteFolderIdQueryCondition implements FavoriteQueryCondition {

    private static final long serialVersionUID = 1L;

    /**
     * 收藏夹ID
     */
    @NotNull(message = "收藏夹ID不能为空")
    @Schema(description = "收藏夹ID", required = true)
    private Long folderId;

    @Override
    public String getConditionType() {
        return "FOLDER_ID";
    }

    @Override
    public boolean isValid() {
        return folderId != null && folderId > 0;
    }
} 