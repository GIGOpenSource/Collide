package com.gig.collide.api.favorite.request;

import com.gig.collide.api.favorite.constant.FavoriteStatus;
import com.gig.collide.base.request.BaseRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

/**
 * 收藏批量操作请求
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
@Schema(description = "收藏批量操作请求")
public class FavoriteBatchOperationRequest extends BaseRequest {

    private static final long serialVersionUID = 1L;

    /**
     * 收藏ID列表
     */
    @NotEmpty(message = "收藏ID列表不能为空")
    @Size(max = 100, message = "批量操作数量不能超过100")
    @Schema(description = "收藏ID列表", required = true)
    private List<Long> favoriteIds;

    /**
     * 操作类型
     */
    @Schema(description = "操作类型：MOVE-移动到收藏夹, UPDATE_STATUS-更新状态, DELETE-删除", 
            example = "MOVE", required = true)
    private String operationType;

    /**
     * 目标收藏夹ID（移动操作时使用）
     */
    @Schema(description = "目标收藏夹ID")
    private Long targetFolderId;

    /**
     * 目标状态（状态更新操作时使用）
     */
    @Schema(description = "目标状态")
    private FavoriteStatus targetStatus;

    /**
     * 是否物理删除（删除操作时使用）
     */
    @Schema(description = "是否物理删除", example = "false")
    private Boolean physicalDelete = false;

    /**
     * 操作原因
     */
    @Size(max = 200, message = "操作原因长度不能超过200字符")
    @Schema(description = "操作原因")
    private String operationReason;
} 