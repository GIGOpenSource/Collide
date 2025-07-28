package com.gig.collide.api.favorite.response;

import com.gig.collide.api.favorite.response.data.FavoriteInfo;
import com.gig.collide.base.response.BaseResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * 收藏创建响应
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
@Schema(description = "收藏创建响应")
public class FavoriteCreateResponse extends BaseResponse {

    private static final long serialVersionUID = 1L;

    /**
     * 创建的收藏信息
     */
    @Schema(description = "创建的收藏信息")
    private FavoriteInfo favoriteInfo;

    /**
     * 收藏ID
     */
    @Schema(description = "收藏ID")
    private Long favoriteId;

    /**
     * 是否为新创建（false表示已存在）
     */
    @Schema(description = "是否为新创建")
    private Boolean isNewCreate;

    // ===================== 静态工厂方法 =====================

    /**
     * 成功创建响应
     *
     * @param favoriteInfo 收藏信息
     * @return 创建响应
     */
    public static FavoriteCreateResponse success(FavoriteInfo favoriteInfo) {
        FavoriteCreateResponse response = new FavoriteCreateResponse();
        response.setFavoriteInfo(favoriteInfo);
        response.setFavoriteId(favoriteInfo.getFavoriteId());
        response.setIsNewCreate(true);
        response.setSuccess(true);
        return response;
    }

    /**
     * 成功创建响应（仅返回ID）
     *
     * @param favoriteId 收藏ID
     * @return 创建响应
     */
    public static FavoriteCreateResponse success(Long favoriteId) {
        FavoriteCreateResponse response = new FavoriteCreateResponse();
        response.setFavoriteId(favoriteId);
        response.setIsNewCreate(true);
        response.setSuccess(true);
        return response;
    }

    /**
     * 已存在响应
     *
     * @param favoriteInfo 已存在的收藏信息
     * @return 创建响应
     */
    public static FavoriteCreateResponse exists(FavoriteInfo favoriteInfo) {
        FavoriteCreateResponse response = new FavoriteCreateResponse();
        response.setFavoriteInfo(favoriteInfo);
        response.setFavoriteId(favoriteInfo.getFavoriteId());
        response.setIsNewCreate(false);
        response.setSuccess(true);
        response.setResponseMessage("收藏已存在");
        return response;
    }

    /**
     * 失败响应
     *
     * @param message 错误消息
     * @return 创建响应
     */
    public static FavoriteCreateResponse failure(String message) {
        FavoriteCreateResponse response = new FavoriteCreateResponse();
        response.setSuccess(false);
        response.setResponseMessage(message);
        return response;
    }

    // ===================== 业务方法 =====================

    /**
     * 是否创建成功
     *
     * @return true-成功，false-失败
     */
    public boolean isCreateSuccess() {
        return getSuccess() && favoriteId != null;
    }

    /**
     * 是否为新创建的收藏
     *
     * @return true-新创建，false-已存在或失败
     */
    public boolean isNewlyCreated() {
        return Boolean.TRUE.equals(isNewCreate);
    }
} 