package com.gig.collide.favorite.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gig.collide.api.favorite.constant.FavoriteType;
import com.gig.collide.api.favorite.constant.FavoriteStatus;
import com.gig.collide.favorite.domain.entity.Favorite;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

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
        @Param("targetIds") java.util.List<Long> targetIds,
        @Param("status") FavoriteStatus status
    );
} 