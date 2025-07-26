package com.gig.collide.content.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gig.collide.content.domain.entity.ContentFavorite;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 内容收藏记录数据访问映射器
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Mapper
public interface ContentFavoriteMapper extends BaseMapper<ContentFavorite> {

    /**
     * 检查用户是否已经收藏了指定内容
     *
     * @param contentId 内容ID
     * @param userId    用户ID
     * @return 是否已收藏
     */
    boolean existsByContentIdAndUserId(@Param("contentId") Long contentId, @Param("userId") Long userId);

    /**
     * 删除用户对指定内容的收藏记录（取消收藏）
     *
     * @param contentId 内容ID
     * @param userId    用户ID
     * @return 影响行数
     */
    int deleteByContentIdAndUserId(@Param("contentId") Long contentId, @Param("userId") Long userId);

    /**
     * 统计指定内容的收藏数量
     *
     * @param contentId 内容ID
     * @return 收藏数量
     */
    int countByContentId(@Param("contentId") Long contentId);

    /**
     * 统计用户的收藏数量
     *
     * @param userId 用户ID
     * @return 收藏数量
     */
    int countByUserId(@Param("userId") Long userId);
} 