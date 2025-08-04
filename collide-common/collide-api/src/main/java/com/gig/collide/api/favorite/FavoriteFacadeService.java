package com.gig.collide.api.favorite;

import com.gig.collide.api.favorite.request.FavoriteCreateRequest;
import com.gig.collide.api.favorite.request.FavoriteDeleteRequest;
import com.gig.collide.api.favorite.request.FavoriteQueryRequest;
import com.gig.collide.api.favorite.response.FavoriteResponse;
import com.gig.collide.web.vo.Result;
import com.gig.collide.base.response.PageResponse;

/**
 * 收藏门面服务接口 - 简洁版
 * 基于favorite-simple.sql的单表设计，实现核心收藏功能
 * 
 * @author Collide
 * @version 2.0.0 (简洁版)
 * @since 2024-01-01
 */
public interface FavoriteFacadeService {
    
    /**
     * 添加收藏
     * 支持收藏对象和用户信息冗余存储
     * 
     * @param request 收藏请求
     * @return 收藏结果
     */
    Result<FavoriteResponse> addFavorite(FavoriteCreateRequest request);
    
    /**
     * 取消收藏
     * 将收藏状态更新为cancelled
     * 
     * @param request 取消收藏请求
     * @return 取消结果
     */
    Result<Void> removeFavorite(FavoriteDeleteRequest request);
    
    /**
     * 检查收藏状态
     * 查询用户是否已收藏目标对象
     * 
     * @param userId 用户ID
     * @param favoriteType 收藏类型
     * @param targetId 目标ID
     * @return 收藏状态
     */
    Result<Boolean> checkFavoriteStatus(Long userId, String favoriteType, Long targetId);
    
    /**
     * 获取收藏详情
     * 
     * @param userId 用户ID
     * @param favoriteType 收藏类型
     * @param targetId 目标ID
     * @return 收藏详情
     */
    Result<FavoriteResponse> getFavoriteDetail(Long userId, String favoriteType, Long targetId);
    
    /**
     * 分页查询收藏记录
     * 支持按用户、类型、状态等条件查询
     * 
     * @param request 查询请求
     * @return 收藏记录列表
     */
    Result<PageResponse<FavoriteResponse>> queryFavorites(FavoriteQueryRequest request);
    
    /**
     * 获取用户的收藏列表
     * 查询某用户的所有收藏
     * 
     * @param userId 用户ID
     * @param favoriteType 收藏类型（可选）
     * @param currentPage 页码
     * @param pageSize 页面大小
     * @return 收藏列表
     */
    Result<PageResponse<FavoriteResponse>> getUserFavorites(Long userId, String favoriteType, 
                                                          Integer currentPage, Integer pageSize);
    
    /**
     * 获取目标对象的收藏列表
     * 查询收藏某个对象的所有用户
     * 
     * @param favoriteType 收藏类型
     * @param targetId 目标ID
     * @param currentPage 页码
     * @param pageSize 页面大小
     * @return 收藏用户列表
     */
    Result<PageResponse<FavoriteResponse>> getTargetFavorites(String favoriteType, Long targetId,
                                                            Integer currentPage, Integer pageSize);
    
    /**
     * 获取用户收藏数量
     * 统计用户收藏的数量
     * 
     * @param userId 用户ID
     * @param favoriteType 收藏类型（可选）
     * @return 收藏数量
     */
    Result<Long> getUserFavoriteCount(Long userId, String favoriteType);
    
    /**
     * 获取目标对象被收藏数量
     * 统计某个对象被收藏的次数
     * 
     * @param favoriteType 收藏类型
     * @param targetId 目标ID
     * @return 被收藏数量
     */
    Result<Long> getTargetFavoriteCount(String favoriteType, Long targetId);
    
    /**
     * 获取用户收藏统计信息
     * 包含各类型收藏数量统计
     * 
     * @param userId 用户ID
     * @return 统计信息Map (type -> count)
     */
    Result<java.util.Map<String, Object>> getUserFavoriteStatistics(Long userId);
    
    /**
     * 批量检查收藏状态
     * 检查用户对多个目标对象的收藏状态
     * 
     * @param userId 用户ID
     * @param favoriteType 收藏类型
     * @param targetIds 目标ID列表
     * @return 收藏状态Map (targetId -> isFavorited)
     */
    Result<java.util.Map<Long, Boolean>> batchCheckFavoriteStatus(Long userId, String favoriteType, 
                                                                java.util.List<Long> targetIds);
    
    /**
     * 根据标题搜索收藏
     * 根据收藏对象标题进行模糊搜索
     * 
     * @param userId 用户ID
     * @param titleKeyword 标题关键词
     * @param favoriteType 收藏类型（可选）
     * @param currentPage 页码
     * @param pageSize 页面大小
     * @return 搜索结果
     */
    Result<PageResponse<FavoriteResponse>> searchFavoritesByTitle(Long userId, String titleKeyword, 
                                                                String favoriteType, Integer currentPage, Integer pageSize);
    
    /**
     * 获取热门收藏对象
     * 查询被收藏次数最多的对象
     * 
     * @param favoriteType 收藏类型
     * @param currentPage 页码
     * @param pageSize 页面大小
     * @return 热门收藏对象列表
     */
    Result<PageResponse<FavoriteResponse>> getPopularFavorites(String favoriteType, Integer currentPage, Integer pageSize);
    
    /**
     * 清理已取消的收藏记录
     * 物理删除cancelled状态的记录（可选功能）
     * 
     * @param days 保留天数
     * @return 清理数量
     */
    Result<Integer> cleanCancelledFavorites(Integer days);
    
    /**
     * 更新用户信息（冗余字段同步）
     * 当用户信息变更时，同步更新收藏表中的冗余信息
     * 
     * @param userId 用户ID
     * @param nickname 新昵称
     * @return 更新成功的记录数
     */
    Result<Integer> updateUserInfo(Long userId, String nickname);
    
    /**
     * 更新目标对象信息（冗余字段同步）
     * 当目标对象信息变更时，同步更新收藏表中的冗余信息
     * 
     * @param favoriteType 收藏类型
     * @param targetId 目标ID
     * @param title 新标题
     * @param cover 新封面
     * @param authorId 新作者ID
     * @return 更新成功的记录数
     */
    Result<Integer> updateTargetInfo(String favoriteType, Long targetId, String title, String cover, Long authorId);
    
    /**
     * 根据作者查询收藏作品
     * 查询某作者的作品被收藏情况
     * 
     * @param targetAuthorId 作者ID
     * @param favoriteType 收藏类型（可选）
     * @param currentPage 页码
     * @param pageSize 页面大小
     * @return 收藏作品列表
     */
    Result<PageResponse<FavoriteResponse>> getFavoritesByAuthor(Long targetAuthorId, String favoriteType,
                                                              Integer currentPage, Integer pageSize);
    
    /**
     * 检查是否已经存在收藏关系
     * 包括已取消的收藏关系
     * 
     * @param userId 用户ID
     * @param favoriteType 收藏类型
     * @param targetId 目标ID
     * @return 是否存在收藏关系
     */
    Result<Boolean> existsFavoriteRelation(Long userId, String favoriteType, Long targetId);
    
    /**
     * 重新激活已取消的收藏
     * 将cancelled状态的收藏重新设置为active
     * 
     * @param userId 用户ID
     * @param favoriteType 收藏类型
     * @param targetId 目标ID
     * @return 是否成功重新激活
     */
    Result<Boolean> reactivateFavorite(Long userId, String favoriteType, Long targetId);
    
    /**
     * 验证收藏请求参数
     * 校验请求参数的有效性
     * 
     * @param request 收藏请求
     * @return 验证结果信息
     */
    Result<String> validateFavoriteRequest(FavoriteCreateRequest request);
    
    /**
     * 检查是否可以收藏
     * 检查业务规则是否允许收藏
     * 
     * @param userId 用户ID
     * @param favoriteType 收藏类型
     * @param targetId 目标ID
     * @return 检查结果信息
     */
    Result<String> checkCanFavorite(Long userId, String favoriteType, Long targetId);
}