package com.gig.collide.favorite.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gig.collide.favorite.domain.entity.Favorite;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 收藏数据访问层 - 简洁版
 * 基于MyBatis-Plus，实现简洁的数据访问
 * 
 * @author Collide
 * @version 2.0.0 (简洁版)
 * @since 2024-01-01
 */
@Mapper
public interface FavoriteMapper extends BaseMapper<Favorite> {

    /**
     * 根据用户、类型和目标查询收藏
     * 
     * @param userId 用户ID
     * @param favoriteType 收藏类型
     * @param targetId 目标ID
     * @param status 状态（可选）
     * @return 收藏记录
     */
    Favorite findByUserAndTarget(@Param("userId") Long userId,
                                @Param("favoriteType") String favoriteType,
                                @Param("targetId") Long targetId,
                                @Param("status") String status);

    /**
     * 检查收藏是否存在
     * 
     * @param userId 用户ID
     * @param favoriteType 收藏类型
     * @param targetId 目标ID
     * @param status 状态（可选）
     * @return 是否存在
     */
    boolean checkFavoriteExists(@Param("userId") Long userId,
                               @Param("favoriteType") String favoriteType,
                               @Param("targetId") Long targetId,
                               @Param("status") String status);

    /**
     * 获取用户的收藏列表
     * 
     * @param page 分页参数
     * @param userId 用户ID
     * @param favoriteType 收藏类型（可选）
     * @param status 状态（可选）
     * @return 收藏列表
     */
    IPage<Favorite> findUserFavorites(Page<Favorite> page,
                                     @Param("userId") Long userId,
                                     @Param("favoriteType") String favoriteType,
                                     @Param("status") String status);

    /**
     * 获取目标对象的收藏列表
     * 
     * @param page 分页参数
     * @param favoriteType 收藏类型
     * @param targetId 目标ID
     * @param status 状态（可选）
     * @return 收藏列表
     */
    IPage<Favorite> findTargetFavorites(Page<Favorite> page,
                                       @Param("favoriteType") String favoriteType,
                                       @Param("targetId") Long targetId,
                                       @Param("status") String status);

    /**
     * 根据标题搜索收藏
     * 
     * @param page 分页参数
     * @param userId 用户ID
     * @param titleKeyword 标题关键词
     * @param favoriteType 收藏类型（可选）
     * @param status 状态（可选）
     * @return 搜索结果
     */
    IPage<Favorite> searchByTitle(Page<Favorite> page,
                                 @Param("userId") Long userId,
                                 @Param("titleKeyword") String titleKeyword,
                                 @Param("favoriteType") String favoriteType,
                                 @Param("status") String status);

    /**
     * 获取热门收藏对象
     * 
     * @param page 分页参数
     * @param favoriteType 收藏类型
     * @param status 状态（可选）
     * @return 热门收藏列表
     */
    IPage<Favorite> findPopularFavorites(Page<Favorite> page,
                                        @Param("favoriteType") String favoriteType,
                                        @Param("status") String status);

    /**
     * 统计用户收藏数量
     * 
     * @param userId 用户ID
     * @param favoriteType 收藏类型（可选）
     * @param status 状态（可选）
     * @return 收藏数量
     */
    Long countUserFavorites(@Param("userId") Long userId,
                           @Param("favoriteType") String favoriteType,
                           @Param("status") String status);

    /**
     * 统计目标对象被收藏数量
     * 
     * @param favoriteType 收藏类型
     * @param targetId 目标ID
     * @param status 状态（可选）
     * @return 被收藏数量
     */
    Long countTargetFavorites(@Param("favoriteType") String favoriteType,
                             @Param("targetId") Long targetId,
                             @Param("status") String status);

    /**
     * 批量检查收藏状态
     * 
     * @param userId 用户ID
     * @param favoriteType 收藏类型
     * @param targetIds 目标ID列表
     * @param status 状态（可选）
     * @return 收藏状态列表
     */
    List<Map<String, Object>> batchCheckFavoriteStatus(@Param("userId") Long userId,
                                                      @Param("favoriteType") String favoriteType,
                                                      @Param("targetIds") List<Long> targetIds,
                                                      @Param("status") String status);

    /**
     * 更新收藏状态
     * 
     * @param userId 用户ID
     * @param favoriteType 收藏类型
     * @param targetId 目标ID
     * @param status 新状态
     * @return 更新行数
     */
    int updateFavoriteStatus(@Param("userId") Long userId,
                            @Param("favoriteType") String favoriteType,
                            @Param("targetId") Long targetId,
                            @Param("status") String status);

    /**
     * 更新用户信息（冗余字段）
     * 
     * @param userId 用户ID
     * @param nickname 新昵称
     * @return 更新行数
     */
    int updateUserInfo(@Param("userId") Long userId,
                      @Param("nickname") String nickname);

    /**
     * 更新目标对象信息（冗余字段）
     * 
     * @param favoriteType 收藏类型
     * @param targetId 目标ID
     * @param title 新标题
     * @param cover 新封面
     * @param authorId 新作者ID
     * @return 更新行数
     */
    int updateTargetInfo(@Param("favoriteType") String favoriteType,
                        @Param("targetId") Long targetId,
                        @Param("title") String title,
                        @Param("cover") String cover,
                        @Param("authorId") Long authorId);

    /**
     * 获取用户收藏统计信息
     * 
     * @param userId 用户ID
     * @return 统计信息
     */
    Map<String, Object> getUserFavoriteStatistics(@Param("userId") Long userId);

    /**
     * 清理已取消的收藏记录
     * 
     * @param days 保留天数
     * @return 清理数量
     */
    int cleanCancelledFavorites(@Param("days") Integer days);

    /**
     * 复合条件查询收藏记录
     * 
     * @param page 分页参数
     * @param userId 用户ID（可选）
     * @param favoriteType 收藏类型（可选）
     * @param targetId 目标ID（可选）
     * @param targetTitle 目标标题关键词（可选）
     * @param targetAuthorId 目标作者ID（可选）
     * @param userNickname 用户昵称关键词（可选）
     * @param status 状态（可选）
     * @param queryType 查询类型（可选）
     * @param orderBy 排序字段
     * @param orderDirection 排序方向
     * @return 查询结果
     */
    IPage<Favorite> findWithConditions(Page<Favorite> page,
                                      @Param("userId") Long userId,
                                      @Param("favoriteType") String favoriteType,
                                      @Param("targetId") Long targetId,
                                      @Param("targetTitle") String targetTitle,
                                      @Param("targetAuthorId") Long targetAuthorId,
                                      @Param("userNickname") String userNickname,
                                      @Param("status") String status,
                                      @Param("queryType") String queryType,
                                      @Param("orderBy") String orderBy,
                                      @Param("orderDirection") String orderDirection);

    /**
     * 根据作者查询收藏作品
     * 
     * @param page 分页参数
     * @param targetAuthorId 作者ID
     * @param favoriteType 收藏类型（可选）
     * @param status 状态（可选）
     * @return 收藏作品列表
     */
    IPage<Favorite> findByAuthor(Page<Favorite> page,
                                @Param("targetAuthorId") Long targetAuthorId,
                                @Param("favoriteType") String favoriteType,
                                @Param("status") String status);

    /**
     * 检查收藏关系是否存在（包括已取消的）
     * 
     * @param userId 用户ID
     * @param favoriteType 收藏类型
     * @param targetId 目标ID
     * @return 是否存在收藏关系
     */
    boolean existsFavoriteRelation(@Param("userId") Long userId,
                                  @Param("favoriteType") String favoriteType,
                                  @Param("targetId") Long targetId);

    /**
     * 重新激活已取消的收藏
     * 
     * @param userId 用户ID
     * @param favoriteType 收藏类型
     * @param targetId 目标ID
     * @return 更新行数
     */
    int reactivateFavorite(@Param("userId") Long userId,
                          @Param("favoriteType") String favoriteType,
                          @Param("targetId") Long targetId);
}