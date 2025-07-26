package com.gig.collide.api.favorite.service;

import com.gig.collide.api.favorite.request.FavoriteRequest;
import com.gig.collide.api.favorite.request.FavoriteQueryRequest;
import com.gig.collide.api.favorite.request.FolderCreateRequest;
import com.gig.collide.api.favorite.request.FolderUpdateRequest;
import com.gig.collide.api.favorite.response.FavoriteResponse;
import com.gig.collide.api.favorite.response.data.FavoriteInfo;
import com.gig.collide.api.favorite.response.data.FolderInfo;
import com.gig.collide.base.response.PageResponse;

import java.util.List;

/**
 * 收藏服务 Facade 接口
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
public interface FavoriteFacadeService {

    // ========== 收藏管理 ==========

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

    // ========== 收藏夹管理 ==========

    /**
     * 创建收藏夹
     *
     * @param createRequest 创建请求
     * @return 收藏夹ID
     */
    FavoriteResponse createFolder(FolderCreateRequest createRequest);

    /**
     * 更新收藏夹
     *
     * @param updateRequest 更新请求
     * @return 操作结果
     */
    FavoriteResponse updateFolder(FolderUpdateRequest updateRequest);

    /**
     * 删除收藏夹
     *
     * @param folderId 收藏夹ID
     * @param userId 用户ID（权限验证）
     * @return 操作结果
     */
    FavoriteResponse deleteFolder(Long folderId, Long userId);

    /**
     * 查询用户收藏夹列表
     *
     * @param userId 用户ID
     * @return 收藏夹列表
     */
    List<FolderInfo> queryUserFolders(Long userId);

    /**
     * 查询收藏夹详情
     *
     * @param folderId 收藏夹ID
     * @param userId 查询用户ID（权限验证）
     * @return 收藏夹详情
     */
    FolderInfo queryFolderDetail(Long folderId, Long userId);

    /**
     * 初始化用户默认收藏夹
     *
     * @param userId 用户ID
     * @return 默认收藏夹ID
     */
    Long initDefaultFolder(Long userId);
} 