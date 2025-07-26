package com.gig.collide.favorite.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gig.collide.api.favorite.constant.FolderType;
import com.gig.collide.favorite.domain.entity.FavoriteFolder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 收藏夹 Mapper 接口
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Mapper
public interface FavoriteFolderMapper extends BaseMapper<FavoriteFolder> {

    /**
     * 查询用户收藏夹列表
     *
     * @param userId 用户ID
     * @return 收藏夹列表
     */
    List<FavoriteFolder> selectUserFolders(@Param("userId") Long userId);

    /**
     * 查询用户默认收藏夹
     *
     * @param userId 用户ID
     * @return 默认收藏夹
     */
    FavoriteFolder selectDefaultFolder(@Param("userId") Long userId);

    /**
     * 查询用户指定类型的收藏夹
     *
     * @param userId 用户ID
     * @param folderType 收藏夹类型
     * @return 收藏夹列表
     */
    List<FavoriteFolder> selectUserFoldersByType(
        @Param("userId") Long userId, 
        @Param("folderType") FolderType folderType
    );

    /**
     * 根据ID查询收藏夹详情
     *
     * @param folderId 收藏夹ID
     * @return 收藏夹信息
     */
    FavoriteFolder selectByFolderId(@Param("folderId") Long folderId);

    /**
     * 检查收藏夹名称是否已存在
     *
     * @param userId 用户ID
     * @param folderName 收藏夹名称
     * @param excludeFolderId 排除的收藏夹ID（更新时使用）
     * @return 是否存在
     */
    Boolean checkFolderNameExists(
        @Param("userId") Long userId,
        @Param("folderName") String folderName,
        @Param("excludeFolderId") Long excludeFolderId
    );

    /**
     * 统计用户收藏夹数量
     *
     * @param userId 用户ID
     * @param excludeDefault 是否排除默认收藏夹
     * @return 收藏夹数量
     */
    Integer countUserFolders(
        @Param("userId") Long userId, 
        @Param("excludeDefault") Boolean excludeDefault
    );

    /**
     * 更新收藏夹收藏数量
     *
     * @param folderId 收藏夹ID
     * @param delta 变化量（+1或-1）
     * @return 更新行数
     */
    Integer updateFolderItemCount(@Param("folderId") Long folderId, @Param("delta") Integer delta);

    /**
     * 批量更新收藏夹收藏数量
     *
     * @param folderIds 收藏夹ID列表
     * @return 更新行数
     */
    Integer refreshFolderItemCounts(@Param("folderIds") List<Long> folderIds);

    /**
     * 重置收藏夹收藏数量
     *
     * @param folderId 收藏夹ID
     * @return 更新行数
     */
    Integer resetFolderItemCount(@Param("folderId") Long folderId);

    /**
     * 删除用户收藏夹（物理删除，清理数据用）
     *
     * @param userId 用户ID
     * @param excludeDefault 是否排除默认收藏夹
     * @return 删除数量
     */
    Integer deleteUserFolders(
        @Param("userId") Long userId, 
        @Param("excludeDefault") Boolean excludeDefault
    );
} 