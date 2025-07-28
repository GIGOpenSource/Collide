package com.gig.collide.api.favorite.request.condition;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * 按用户ID查询收藏夹条件
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
@Schema(description = "按用户ID查询收藏夹条件")
public class FolderUserIdQueryCondition implements FolderQueryCondition {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    @NotNull(message = "用户ID不能为空")
    @Schema(description = "用户ID", required = true)
    private Long userId;

    @Override
    public String getConditionType() {
        return "USER_ID";
    }

    @Override
    public boolean isValid() {
        return userId != null && userId > 0;
    }
} 