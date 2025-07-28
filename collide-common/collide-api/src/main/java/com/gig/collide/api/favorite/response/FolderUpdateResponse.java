package com.gig.collide.api.favorite.response;

import com.gig.collide.api.favorite.response.data.FavoriteFolderInfo;
import com.gig.collide.base.response.BaseResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * 收藏夹更新响应
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
@Schema(description = "收藏夹更新响应")
public class FolderUpdateResponse extends BaseResponse {

    private static final long serialVersionUID = 1L;

    /**
     * 更新后的收藏夹信息
     */
    @Schema(description = "更新后的收藏夹信息")
    private FavoriteFolderInfo folderInfo;

    /**
     * 收藏夹ID
     */
    @Schema(description = "收藏夹ID")
    private Long folderId;

    // ===================== 静态工厂方法 =====================

    /**
     * 成功更新响应
     *
     * @param folderInfo 更新后的收藏夹信息
     * @return 更新响应
     */
    public static FolderUpdateResponse success(FavoriteFolderInfo folderInfo) {
        FolderUpdateResponse response = new FolderUpdateResponse();
        response.setFolderInfo(folderInfo);
        response.setFolderId(folderInfo.getFolderId());
        response.setSuccess(true);
        return response;
    }

    /**
     * 失败响应
     *
     * @param message 错误消息
     * @return 更新响应
     */
    public static FolderUpdateResponse failure(String message) {
        FolderUpdateResponse response = new FolderUpdateResponse();
        response.setSuccess(false);
        response.setResponseMessage(message);
        return response;
    }
} 