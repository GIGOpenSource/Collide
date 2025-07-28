package com.gig.collide.api.favorite.response;

import com.gig.collide.api.favorite.response.data.FavoriteFolderInfo;
import com.gig.collide.base.response.BaseResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * 收藏夹创建响应
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
@Schema(description = "收藏夹创建响应")
public class FolderCreateResponse extends BaseResponse {

    private static final long serialVersionUID = 1L;

    /**
     * 创建的收藏夹信息
     */
    @Schema(description = "创建的收藏夹信息")
    private FavoriteFolderInfo folderInfo;

    /**
     * 收藏夹ID
     */
    @Schema(description = "收藏夹ID")
    private Long folderId;

    // ===================== 静态工厂方法 =====================

    /**
     * 成功创建响应
     *
     * @param folderInfo 收藏夹信息
     * @return 创建响应
     */
    public static FolderCreateResponse success(FavoriteFolderInfo folderInfo) {
        FolderCreateResponse response = new FolderCreateResponse();
        response.setFolderInfo(folderInfo);
        response.setFolderId(folderInfo.getFolderId());
        response.setSuccess(true);
        return response;
    }

    /**
     * 失败响应
     *
     * @param message 错误消息
     * @return 创建响应
     */
    public static FolderCreateResponse failure(String message) {
        FolderCreateResponse response = new FolderCreateResponse();
        response.setSuccess(false);
        response.setMessage(message);
        return response;
    }
} 