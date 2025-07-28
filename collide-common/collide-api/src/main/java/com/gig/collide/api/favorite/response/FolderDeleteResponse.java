package com.gig.collide.api.favorite.response;

import com.gig.collide.base.response.BaseResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * 收藏夹删除响应
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
@Schema(description = "收藏夹删除响应")
public class FolderDeleteResponse extends BaseResponse {

    private static final long serialVersionUID = 1L;

    /**
     * 收藏夹ID
     */
    @Schema(description = "收藏夹ID")
    private Long folderId;

    /**
     * 迁移的收藏数量
     */
    @Schema(description = "迁移的收藏数量")
    private Integer migratedCount;

    /**
     * 迁移目标收藏夹ID
     */
    @Schema(description = "迁移目标收藏夹ID")
    private Long migrationTargetFolderId;

    // ===================== 静态工厂方法 =====================

    /**
     * 成功删除响应
     *
     * @param folderId 收藏夹ID
     * @return 删除响应
     */
    public static FolderDeleteResponse success(Long folderId) {
        FolderDeleteResponse response = new FolderDeleteResponse();
        response.setFolderId(folderId);
        response.setSuccess(true);
        return response;
    }

    /**
     * 成功删除响应（带迁移信息）
     *
     * @param folderId 收藏夹ID
     * @param migratedCount 迁移的收藏数量
     * @param migrationTargetFolderId 迁移目标收藏夹ID
     * @return 删除响应
     */
    public static FolderDeleteResponse success(Long folderId, Integer migratedCount, Long migrationTargetFolderId) {
        FolderDeleteResponse response = new FolderDeleteResponse();
        response.setFolderId(folderId);
        response.setMigratedCount(migratedCount);
        response.setMigrationTargetFolderId(migrationTargetFolderId);
        response.setSuccess(true);
        return response;
    }

    /**
     * 失败响应
     *
     * @param message 错误消息
     * @return 删除响应
     */
    public static FolderDeleteResponse failure(String message) {
        FolderDeleteResponse response = new FolderDeleteResponse();
        response.setSuccess(false);
        response.setMessage(message);
        return response;
    }
} 