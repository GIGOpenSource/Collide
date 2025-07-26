package com.gig.collide.favorite.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gig.collide.api.favorite.constant.FavoriteType;
import com.gig.collide.api.favorite.constant.FavoriteStatus;
import com.gig.collide.favorite.domain.entity.Favorite;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 收藏 Mapper 接口
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Mapper
public interface FavoriteMapper extends BaseMapper<Favorite> {

    /**
     * 分页查询用户收藏列表
     *
     * @param page 分页参数
     * @param userId 用户ID
     * @param favoriteType 收藏类型
     * @param folderId 收藏夹ID
     * @param status 收藏状态
     * @return 收藏列表
     */
    IPage<Favorite> selectUserFavoritePage(
        Page<Favorite> page,
        @Param("userId") Long userId,
        @Param("favoriteType") FavoriteType favoriteType,
        @Param("folderId") Long folderId,
        @Param("status") FavoriteStatus status
    );

    /**
     * 检查是否已收藏
     *
     * @param userId 用户ID
     * @param favoriteType 收藏类型
     * @param targetId 目标ID
     * @return 收藏记录ID，未收藏返回null
     */
    Long selectFavoriteId(
        @Param("userId") Long userId,
        @Param("favoriteType") FavoriteType favoriteType,
        @Param("targetId") Long targetId
    );

    /**
     * 根据收藏ID查询收藏记录
     *
     * @param favoriteId 收藏ID
     * @return 收藏记录
     */
    Favorite selectByFavoriteId(@Param("favoriteId") Long favoriteId);

    /**
     * 统计用户收藏数量
     *
     * @param userId 用户ID
     * @param favoriteType 收藏类型（可选）
     * @return 收藏数量
     */
    Long countUserFavorites(
        @Param("userId") Long userId,
        @Param("favoriteType") FavoriteType favoriteType
    );

    /**
     * 统计目标被收藏数量
     *
     * @param favoriteType 收藏类型
     * @param targetId 目标ID
     * @return 被收藏数量
     */
    Long countTargetFavorites(
        @Param("favoriteType") FavoriteType favoriteType,
        @Param("targetId") Long targetId
    );

    /**
     * 批量更新收藏状态
     *
     * @param userId 用户ID
     * @param targetIds 目标ID列表
     * @param status 新状态
     * @return 更新数量
     */
    Integer batchUpdateStatus(
        @Param("userId") Long userId,
        @Param("targetIds") List<Long> targetIds,
        @Param("status") FavoriteStatus status
    );

    /**
     * 查询用户收藏的目标ID列表
     *
     * @param userId 用户ID
     * @param favoriteType 收藏类型
     * @param targetIds 目标ID列表（过滤条件）
     * @return 已收藏的目标ID列表
     */
    List<Long> selectUserFavoriteTargetIds(
        @Param("userId") Long userId,
        @Param("favoriteType") FavoriteType favoriteType,
        @Param("targetIds") List<Long> targetIds
    );

    /**
     * 查询收藏夹中的收藏数量
     *
     * @param folderId 收藏夹ID
     * @return 收藏数量
     */
    Long countFolderFavorites(@Param("folderId") Long folderId);

    /**
     * 删除用户收藏记录（物理删除，清理数据用）
     *
     * @param userId 用户ID
     * @param favoriteType 收藏类型（可选）
     * @return 删除数量
     */
    Integer deleteUserFavorites(
        @Param("userId") Long userId,
        @Param("favoriteType") FavoriteType favoriteType
    );
} 