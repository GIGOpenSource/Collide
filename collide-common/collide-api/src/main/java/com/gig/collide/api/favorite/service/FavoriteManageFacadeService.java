package com.gig.collide.api.favorite.service;

import com.gig.collide.api.favorite.request.*;
import com.gig.collide.api.favorite.response.*;

/**
 * 收藏管理Facade服务接口
 * 
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
public interface FavoriteManageFacadeService {

    // ===================== 管理员操作 =====================

    /**
     * 管理员查询收藏（包含已删除数据）
     *
     * @param request 查询请求
     * @return 查询响应
     */
    FavoriteQueryResponse<?> adminQueryFavorites(FavoriteQueryRequest request);

    /**
     * 管理员删除收藏（物理删除）
     *
     * @param request 删除请求
     * @return 删除响应
     */
    FavoriteDeleteResponse adminDeleteFavorite(FavoriteDeleteRequest request);

    /**
     * 管理员批量操作收藏
     *
     * @param request 批量操作请求
     * @return 批量操作响应
     */
    FavoriteBatchOperationResponse adminBatchOperation(FavoriteBatchOperationRequest request);

    // ===================== 数据维护 =====================

    /**
     * 清理无效收藏数据
     *
     * @param daysBeforeCleanup 清理多少天前的数据
     * @return 清理结果
     */
    String cleanupInvalidFavorites(Integer daysBeforeCleanup);

    /**
     * 修复收藏统计数据
     *
     * @return 修复结果
     */
    String repairFavoriteStatistics();

    /**
     * 同步收藏夹计数
     *
     * @param folderId 收藏夹ID，null表示同步所有
     * @return 同步结果
     */
    String syncFolderItemCount(Long folderId);

    // ===================== 数据导出 =====================

    /**
     * 导出收藏数据
     *
     * @param request 查询请求
     * @param format 导出格式（CSV, EXCEL等）
     * @return 导出结果
     */
    String exportFavoriteData(FavoriteQueryRequest request, String format);
} 