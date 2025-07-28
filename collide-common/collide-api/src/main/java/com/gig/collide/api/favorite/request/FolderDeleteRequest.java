package com.gig.collide.api.favorite.request;

import com.gig.collide.base.request.BaseRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * 收藏夹删除请求
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
@Schema(description = "收藏夹删除请求")
public class FolderDeleteRequest extends BaseRequest {

    private static final long serialVersionUID = 1L;

    /**
     * 收藏夹ID
     */
    @NotNull(message = "收藏夹ID不能为空")
    @Schema(description = "收藏夹ID", required = true)
    private Long folderId;

    /**
     * 迁移目标收藏夹ID（删除收藏夹时，将其中的收藏移动到此收藏夹）
     */
    @Schema(description = "迁移目标收藏夹ID")
    private Long migrationTargetFolderId;

    /**
     * 是否强制删除（即使收藏夹中有收藏）
     */
    @Schema(description = "是否强制删除", example = "false")
    private Boolean forceDelete = false;

    /**
     * 删除原因
     */
    @Schema(description = "删除原因")
    private String deleteReason;

    /**
     * 版本号（乐观锁）
     */
    @Schema(description = "版本号")
    private Integer version;
} 