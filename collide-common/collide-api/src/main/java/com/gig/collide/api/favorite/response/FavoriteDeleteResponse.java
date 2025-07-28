package com.gig.collide.api.favorite.response;

import com.gig.collide.base.response.BaseResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * 收藏删除响应
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
@Schema(description = "收藏删除响应")
public class FavoriteDeleteResponse extends BaseResponse {

    private static final long serialVersionUID = 1L;

    /**
     * 收藏ID
     */
    @Schema(description = "收藏ID")
    private Long favoriteId;

    /**
     * 是否为物理删除
     */
    @Schema(description = "是否为物理删除")
    private Boolean isPhysicalDelete;

    /**
     * 删除类型描述
     */
    @Schema(description = "删除类型描述")
    private String deleteType;

    /**
     * 是否有实际删除（记录是否存在并被删除）
     */
    @Schema(description = "是否有实际删除")
    private Boolean hasActualDelete;

    // ===================== 静态工厂方法 =====================

    /**
     * 成功删除响应（逻辑删除）
     *
     * @param favoriteId 收藏ID
     * @return 删除响应
     */
    public static FavoriteDeleteResponse successLogical(Long favoriteId) {
        FavoriteDeleteResponse response = new FavoriteDeleteResponse();
        response.setFavoriteId(favoriteId);
        response.setIsPhysicalDelete(false);
        response.setDeleteType("逻辑删除");
        response.setHasActualDelete(true);
        response.setSuccess(true);
        return response;
    }

    /**
     * 成功删除响应（物理删除）
     *
     * @param favoriteId 收藏ID
     * @return 删除响应
     */
    public static FavoriteDeleteResponse successPhysical(Long favoriteId) {
        FavoriteDeleteResponse response = new FavoriteDeleteResponse();
        response.setFavoriteId(favoriteId);
        response.setIsPhysicalDelete(true);
        response.setDeleteType("物理删除");
        response.setHasActualDelete(true);
        response.setSuccess(true);
        return response;
    }

    /**
     * 记录不存在响应
     *
     * @param favoriteId 收藏ID
     * @return 删除响应
     */
    public static FavoriteDeleteResponse notFound(Long favoriteId) {
        FavoriteDeleteResponse response = new FavoriteDeleteResponse();
        response.setFavoriteId(favoriteId);
        response.setHasActualDelete(false);
        response.setSuccess(true);
        response.setResponseMessage("收藏记录不存在或已删除");
        return response;
    }

    /**
     * 失败响应
     *
     * @param message 错误消息
     * @return 删除响应
     */
    public static FavoriteDeleteResponse failure(String message) {
        FavoriteDeleteResponse response = new FavoriteDeleteResponse();
        response.setSuccess(false);
        response.setResponseMessage(message);
        return response;
    }

    // ===================== 业务方法 =====================

    /**
     * 是否删除成功
     *
     * @return true-成功，false-失败
     */
    public boolean isDeleteSuccess() {
        return getSuccess() && favoriteId != null;
    }

    /**
     * 是否为物理删除
     *
     * @return true-物理删除，false-逻辑删除
     */
    public boolean isPhysicalDeleted() {
        return Boolean.TRUE.equals(isPhysicalDelete);
    }
} 