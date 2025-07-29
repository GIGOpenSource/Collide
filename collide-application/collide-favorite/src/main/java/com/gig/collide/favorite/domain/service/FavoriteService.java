package com.gig.collide.favorite.domain.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.gig.collide.favorite.domain.entity.Favorite;

import java.util.List;
import java.util.Map;

/**
 * 收藏业务逻辑接口 - 简洁版
 * 基于favorite-simple.sql的业务设计，实现核心收藏功能
 * 
 * @author Collide
 * @version 2.0.0 (简洁版)
 * @since 2024-01-01
 */
public interface FavoriteService {

    /**
     * 添加收藏
     * 包含收藏对象和用户信息冗余存储
     * 
     * @param favorite 收藏实体
     * @return 创建的收藏记录
     */
    Favorite addFavorite(Favorite favorite);

    /**
     * 取消收藏
     * 逻辑删除，将状态更新为cancelled
     * 
     * @param userId 用户ID
     * @param favoriteType 收藏类型
     * @param targetId 目标ID
     * @param cancelReason 取消原因
     * @param operatorId 操作人ID
     * @return 是否成功
     */
    boolean removeFavorite(Long userId, String favoriteType, Long targetId, String cancelReason, Long operatorId);

    /**
     * 检查收藏状态
     * 
     * @param userId 用户ID
     * @param favoriteType 收藏类型
     * @param targetId 目标ID
     * @return 是否已收藏
     */
    boolean checkFavoriteStatus(Long userId, String favoriteType, Long targetId);

    /**
     * 获取收藏详情
     * 
     * @param userId 用户ID
     * @param favoriteType 收藏类型
     * @param targetId 目标ID
     * @return 收藏详情
     */
    Favorite getFavoriteDetail(Long userId, String favoriteType, Long targetId);

    /**
     * 分页查询收藏记录
     * 支持复合条件查询
     * 
     * @param pageNum 页码
     * @param pageSize 页面大小
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
     * @return 分页结果
     */
    IPage<Favorite> queryFavorites(Integer pageNum, Integer pageSize, Long userId, String favoriteType,
                                  Long targetId, String targetTitle, Long targetAuthorId, String userNickname,
                                  String status, String queryType, String orderBy, String orderDirection);

    /**
     * 获取用户的收藏列表
     * 
     * @param userId 用户ID
     * @param favoriteType 收藏类型（可选）
     * @param pageNum 页码
     * @param pageSize 页面大小
     * @return 收藏列表
     */
    IPage<Favorite> getUserFavorites(Long userId, String favoriteType, Integer pageNum, Integer pageSize);

    /**
     * 获取目标对象的收藏列表
     * 
     * @param favoriteType 收藏类型
     * @param targetId 目标ID
     * @param pageNum 页码
     * @param pageSize 页面大小
     * @return 收藏用户列表
     */
    IPage<Favorite> getTargetFavorites(String favoriteType, Long targetId, Integer pageNum, Integer pageSize);

    /**
     * 获取用户收藏数量
     * 
     * @param userId 用户ID
     * @param favoriteType 收藏类型（可选）
     * @return 收藏数量
     */
    Long getUserFavoriteCount(Long userId, String favoriteType);

    /**
     * 获取目标对象被收藏数量
     * 
     * @param favoriteType 收藏类型
     * @param targetId 目标ID
     * @return 被收藏数量
     */
    Long getTargetFavoriteCount(String favoriteType, Long targetId);

    /**
     * 获取用户收藏统计信息
     * 
     * @param userId 用户ID
     * @return 统计信息Map
     */
    Map<String, Object> getUserFavoriteStatistics(Long userId);

    /**
     * 批量检查收藏状态
     * 
     * @param userId 用户ID
     * @param favoriteType 收藏类型
     * @param targetIds 目标ID列表
     * @return 收藏状态Map
     */
    Map<Long, Boolean> batchCheckFavoriteStatus(Long userId, String favoriteType, List<Long> targetIds);

    /**
     * 根据标题搜索收藏
     * 
     * @param userId 用户ID
     * @param titleKeyword 标题关键词
     * @param favoriteType 收藏类型（可选）
     * @param pageNum 页码
     * @param pageSize 页面大小
     * @return 搜索结果
     */
    IPage<Favorite> searchFavoritesByTitle(Long userId, String titleKeyword, String favoriteType, 
                                          Integer pageNum, Integer pageSize);

    /**
     * 获取热门收藏对象
     * 
     * @param favoriteType 收藏类型
     * @param pageNum 页码
     * @param pageSize 页面大小
     * @return 热门收藏对象列表
     */
    IPage<Favorite> getPopularFavorites(String favoriteType, Integer pageNum, Integer pageSize);

    /**
     * 更新用户信息（冗余字段）
     * 当用户信息变更时，同步更新收藏表中的冗余信息
     * 
     * @param userId 用户ID
     * @param nickname 新昵称
     * @return 更新成功的记录数
     */
    int updateUserInfo(Long userId, String nickname);

    /**
     * 更新目标对象信息（冗余字段）
     * 当目标对象信息变更时，同步更新收藏表中的冗余信息
     * 
     * @param favoriteType 收藏类型
     * @param targetId 目标ID
     * @param title 新标题
     * @param cover 新封面
     * @param authorId 新作者ID
     * @return 更新成功的记录数
     */
    int updateTargetInfo(String favoriteType, Long targetId, String title, String cover, Long authorId);

    /**
     * 清理已取消的收藏记录
     * 
     * @param days 保留天数
     * @return 清理数量
     */
    int cleanCancelledFavorites(Integer days);

    /**
     * 根据作者查询收藏作品
     * 
     * @param targetAuthorId 作者ID
     * @param favoriteType 收藏类型（可选）
     * @param pageNum 页码
     * @param pageSize 页面大小
     * @return 收藏作品列表
     */
    IPage<Favorite> getFavoritesByAuthor(Long targetAuthorId, String favoriteType, Integer pageNum, Integer pageSize);

    /**
     * 验证收藏请求参数
     * 
     * @param favorite 收藏对象
     * @return 验证结果信息
     */
    String validateFavoriteRequest(Favorite favorite);

    /**
     * 检查是否可以收藏
     * 
     * @param userId 用户ID
     * @param favoriteType 收藏类型
     * @param targetId 目标ID
     * @return 检查结果
     */
    String checkCanFavorite(Long userId, String favoriteType, Long targetId);

    /**
     * 检查是否已经存在收藏关系
     * 
     * @param userId 用户ID
     * @param favoriteType 收藏类型
     * @param targetId 目标ID
     * @return 是否存在（包括已取消的）
     */
    boolean existsFavoriteRelation(Long userId, String favoriteType, Long targetId);

    /**
     * 重新激活已取消的收藏
     * 
     * @param userId 用户ID
     * @param favoriteType 收藏类型
     * @param targetId 目标ID
     * @return 是否成功
     */
    boolean reactivateFavorite(Long userId, String favoriteType, Long targetId);
}