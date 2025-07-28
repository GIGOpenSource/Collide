package com.gig.collide.api.favorite.response;

import com.gig.collide.api.favorite.response.data.FavoriteInfo;
import com.gig.collide.base.response.BaseResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * 收藏更新响应
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
@Schema(description = "收藏更新响应")
public class FavoriteUpdateResponse extends BaseResponse {

    private static final long serialVersionUID = 1L;

    /**
     * 更新后的收藏信息
     */
    @Schema(description = "更新后的收藏信息")
    private FavoriteInfo favoriteInfo;

    /**
     * 收藏ID
     */
    @Schema(description = "收藏ID")
    private Long favoriteId;

    /**
     * 更新的字段数量
     */
    @Schema(description = "更新的字段数量")
    private Integer updatedFieldCount;

    /**
     * 是否有实际更新（数据是否发生变化）
     */
    @Schema(description = "是否有实际更新")
    private Boolean hasActualUpdate;

    // ===================== 静态工厂方法 =====================

    /**
     * 成功更新响应
     *
     * @param favoriteInfo 更新后的收藏信息
     * @return 更新响应
     */
    public static FavoriteUpdateResponse success(FavoriteInfo favoriteInfo) {
        FavoriteUpdateResponse response = new FavoriteUpdateResponse();
        response.setFavoriteInfo(favoriteInfo);
        response.setFavoriteId(favoriteInfo.getFavoriteId());
        response.setHasActualUpdate(true);
        response.setSuccess(true);
        return response;
    }

    /**
     * 成功更新响应（带更新字段数量）
     *
     * @param favoriteInfo 更新后的收藏信息
     * @param updatedFieldCount 更新的字段数量
     * @return 更新响应
     */
    public static FavoriteUpdateResponse success(FavoriteInfo favoriteInfo, Integer updatedFieldCount) {
        FavoriteUpdateResponse response = new FavoriteUpdateResponse();
        response.setFavoriteInfo(favoriteInfo);
        response.setFavoriteId(favoriteInfo.getFavoriteId());
        response.setUpdatedFieldCount(updatedFieldCount);
        response.setHasActualUpdate(updatedFieldCount != null && updatedFieldCount > 0);
        response.setSuccess(true);
        return response;
    }

    /**
     * 无更新响应（数据未发生变化）
     *
     * @param favoriteId 收藏ID
     * @return 更新响应
     */
    public static FavoriteUpdateResponse noChange(Long favoriteId) {
        FavoriteUpdateResponse response = new FavoriteUpdateResponse();
        response.setFavoriteId(favoriteId);
        response.setUpdatedFieldCount(0);
        response.setHasActualUpdate(false);
        response.setSuccess(true);
        response.setResponseMessage("数据未发生变化");
        return response;
    }

    /**
     * 记录不存在响应
     *
     * @param favoriteId 收藏ID
     * @return 更新响应
     */
    public static FavoriteUpdateResponse notFound(Long favoriteId) {
        FavoriteUpdateResponse response = new FavoriteUpdateResponse();
        response.setFavoriteId(favoriteId);
        response.setSuccess(false);
        response.setResponseMessage("收藏记录不存在");
        return response;
    }

    /**
     * 失败响应
     *
     * @param message 错误消息
     * @return 更新响应
     */
    public static FavoriteUpdateResponse failure(String message) {
        FavoriteUpdateResponse response = new FavoriteUpdateResponse();
        response.setSuccess(false);
        response.setResponseMessage(message);
        return response;
    }

    // ===================== 业务方法 =====================

    /**
     * 是否更新成功
     *
     * @return true-成功，false-失败
     */
    public boolean isUpdateSuccess() {
        return getSuccess() && favoriteId != null;
    }

    /**
     * 是否有实际数据变化
     *
     * @return true-有变化，false-无变化
     */
    public boolean hasDataChanged() {
        return Boolean.TRUE.equals(hasActualUpdate);
    }
} 