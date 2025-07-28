package com.gig.collide.api.favorite.service;

import com.gig.collide.api.favorite.request.*;
import com.gig.collide.api.favorite.response.*;
import com.gig.collide.api.favorite.response.data.BasicFavoriteFolderInfo;
import com.gig.collide.api.favorite.response.data.FavoriteFolderInfo;

/**
 * 收藏夹Facade服务接口
 * 
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
public interface FavoriteFolderFacadeService {

    // ===================== 基础收藏夹操作 =====================

    /**
     * 查询收藏夹
     *
     * @param request 查询请求
     * @return 查询响应
     */
    FolderQueryResponse<FavoriteFolderInfo> queryFolders(FolderQueryRequest request);

    /**
     * 查询基础收藏夹信息
     *
     * @param request 查询请求
     * @return 查询响应
     */
    FolderQueryResponse<BasicFavoriteFolderInfo> queryBasicFolders(FolderQueryRequest request);

    /**
     * 创建收藏夹
     *
     * @param request 创建请求
     * @return 创建响应
     */
    FolderCreateResponse createFolder(FolderCreateRequest request);

    /**
     * 更新收藏夹
     *
     * @param request 更新请求
     * @return 更新响应
     */
    FolderUpdateResponse updateFolder(FolderUpdateRequest request);

    /**
     * 删除收藏夹
     *
     * @param request 删除请求
     * @return 删除响应
     */
    FolderDeleteResponse deleteFolder(FolderDeleteRequest request);

    // ===================== 收藏夹管理 =====================

    /**
     * 设置默认收藏夹
     *
     * @param userId 用户ID
     * @param folderId 收藏夹ID
     * @return 设置结果
     */
    Boolean setDefaultFolder(Long userId, Long folderId);

    /**
     * 调整收藏夹排序
     *
     * @param userId 用户ID
     * @param folderIds 收藏夹ID列表（按新顺序排列）
     * @return 调整结果
     */
    Boolean adjustFolderOrder(Long userId, java.util.List<Long> folderIds);

    // ===================== 便捷方法 =====================

    /**
     * 获取用户的默认收藏夹
     *
     * @param userId 用户ID
     * @return 默认收藏夹信息
     */
    FavoriteFolderInfo getUserDefaultFolder(Long userId);

    /**
     * 获取用户收藏夹列表
     *
     * @param userId 用户ID
     * @return 收藏夹列表
     */
    java.util.List<BasicFavoriteFolderInfo> getUserFolders(Long userId);

    /**
     * 检查收藏夹名称是否存在
     *
     * @param userId 用户ID
     * @param folderName 收藏夹名称
     * @param excludeFolderId 排除的收藏夹ID（更新时使用）
     * @return true-存在，false-不存在
     */
    Boolean checkFolderNameExists(Long userId, String folderName, Long excludeFolderId);
} 