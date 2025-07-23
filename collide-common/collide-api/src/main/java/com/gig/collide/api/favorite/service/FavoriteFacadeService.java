package com.gig.collide.api.favorite.service;

import com.gig.collide.api.favorite.request.FavoriteRequest;
import com.gig.collide.api.favorite.request.FavoriteQueryRequest;
import com.gig.collide.api.favorite.response.FavoriteResponse;
import com.gig.collide.api.favorite.response.data.FavoriteInfo;
import com.gig.collide.base.response.PageResponse;

/**
 * 收藏服务 Facade 接口
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
public interface FavoriteFacadeService {

    /**
     * 收藏/取消收藏
     *
     * @param favoriteRequest 收藏请求
     * @return 收藏响应
     */
    FavoriteResponse favorite(FavoriteRequest favoriteRequest);

    /**
     * 批量收藏
     *
     * @param favoriteRequest 收藏请求（可包含多个目标ID）
     * @return 收藏响应
     */
    FavoriteResponse batchFavorite(FavoriteRequest favoriteRequest);

    /**
     * 移动收藏到其他收藏夹
     *
     * @param favoriteId 收藏ID
     * @param targetFolderId 目标收藏夹ID
     * @param userId 用户ID
     * @return 收藏响应
     */
    FavoriteResponse moveFavorite(Long favoriteId, Long targetFolderId, Long userId);

    /**
     * 分页查询用户收藏列表
     *
     * @param queryRequest 查询请求
     * @return 收藏列表响应
     */
    PageResponse<FavoriteInfo> pageQueryFavorites(FavoriteQueryRequest queryRequest);

    /**
     * 查询收藏详情
     *
     * @param favoriteId 收藏ID
     * @param userId 查询用户ID
     * @return 收藏详情
     */
    FavoriteInfo queryFavoriteDetail(Long favoriteId, Long userId);

    /**
     * 检查是否已收藏
     *
     * @param favoriteType 收藏类型
     * @param targetId 目标ID
     * @param userId 用户ID
     * @return 是否已收藏
     */
    Boolean checkFavorited(String favoriteType, Long targetId, Long userId);

    /**
     * 统计用户收藏数量
     *
     * @param userId 用户ID
     * @param favoriteType 收藏类型（可选）
     * @return 收藏数量
     */
    Long countUserFavorites(Long userId, String favoriteType);

    /**
     * 统计目标被收藏数量
     *
     * @param favoriteType 收藏类型
     * @param targetId 目标ID
     * @return 被收藏数量
     */
    Long countTargetFavorites(String favoriteType, Long targetId);
} 