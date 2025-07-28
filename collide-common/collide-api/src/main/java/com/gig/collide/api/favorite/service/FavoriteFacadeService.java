package com.gig.collide.api.favorite.service;

import com.gig.collide.api.favorite.request.*;
import com.gig.collide.api.favorite.response.*;
import com.gig.collide.api.favorite.response.data.BasicFavoriteInfo;
import com.gig.collide.api.favorite.response.data.FavoriteInfo;

/**
 * 收藏Facade服务接口
 * 
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
public interface FavoriteFacadeService {

    // ===================== 基础收藏操作 =====================

    /**
     * 查询收藏
     *
     * @param request 查询请求
     * @return 查询响应
     */
    FavoriteQueryResponse<FavoriteInfo> queryFavorites(FavoriteQueryRequest request);

    /**
     * 查询基础收藏信息
     *
     * @param request 查询请求
     * @return 查询响应
     */
    FavoriteQueryResponse<BasicFavoriteInfo> queryBasicFavorites(FavoriteQueryRequest request);

    /**
     * 创建收藏
     *
     * @param request 创建请求
     * @return 创建响应
     */
    FavoriteCreateResponse createFavorite(FavoriteCreateRequest request);

    /**
     * 更新收藏
     *
     * @param request 更新请求
     * @return 更新响应
     */
    FavoriteUpdateResponse updateFavorite(FavoriteUpdateRequest request);

    /**
     * 删除收藏
     *
     * @param request 删除请求
     * @return 删除响应
     */
    FavoriteDeleteResponse deleteFavorite(FavoriteDeleteRequest request);

    // ===================== 批量操作 =====================

    /**
     * 批量操作收藏
     *
     * @param request 批量操作请求
     * @return 批量操作响应
     */
    FavoriteBatchOperationResponse batchOperation(FavoriteBatchOperationRequest request);

    // ===================== 统计查询 =====================

    /**
     * 获取收藏统计
     *
     * @param request 统计请求
     * @return 统计响应
     */
    FavoriteStatisticsResponse getStatistics(FavoriteStatisticsRequest request);

    // ===================== 便捷方法 =====================

    /**
     * 检查是否已收藏
     *
     * @param userId 用户ID
     * @param targetId 目标ID
     * @param favoriteType 收藏类型
     * @return true-已收藏，false-未收藏
     */
    Boolean checkFavorited(Long userId, Long targetId, String favoriteType);

    /**
     * 获取用户收藏数量
     *
     * @param userId 用户ID
     * @param favoriteType 收藏类型（可选）
     * @return 收藏数量
     */
    Long getUserFavoriteCount(Long userId, String favoriteType);

    /**
     * 获取目标被收藏数量
     *
     * @param targetId 目标ID
     * @param favoriteType 收藏类型
     * @return 被收藏数量
     */
    Long getTargetFavoriteCount(Long targetId, String favoriteType);
} 