package com.gig.collide.api.favorite.request.condition;

import com.gig.collide.api.favorite.constant.FolderType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * 按收藏夹类型查询条件
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
@Schema(description = "按收藏夹类型查询条件")
public class FolderTypeQueryCondition implements FolderQueryCondition {

    private static final long serialVersionUID = 1L;

    /**
     * 收藏夹类型
     */
    @NotNull(message = "收藏夹类型不能为空")
    @Schema(description = "收藏夹类型", required = true)
    private FolderType folderType;

    @Override
    public String getConditionType() {
        return "FOLDER_TYPE";
    }

    @Override
    public boolean isValid() {
        return folderType != null;
    }
} 