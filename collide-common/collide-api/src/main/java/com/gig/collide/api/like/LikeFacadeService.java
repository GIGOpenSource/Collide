package com.gig.collide.api.like;

import com.gig.collide.api.like.request.LikeRequest;
import com.gig.collide.api.like.request.LikeQueryRequest;
import com.gig.collide.api.like.request.LikeToggleRequest;
import com.gig.collide.api.like.request.LikeCancelRequest;
import com.gig.collide.api.like.response.LikeResponse;
import com.gig.collide.web.vo.Result;
import com.gig.collide.base.response.PageResponse;

/**
 * 点赞门面服务接口 - MySQL 8.0 优化版
 * 基于like-simple.sql的单表设计，与LikeMapper完全一致
 * 
 * 接口特性：
 * - 与底层Mapper方法完全对应
 * - 支持用户、目标对象、作者三个维度的查询
 * - 支持时间范围查询和批量操作
 * - 统一的命名规范和参数传递
 * 
 * @author Collide
 * @version 2.0.0 (MySQL 8.0 优化版)
 * @since 2024-01-01
 */
public interface LikeFacadeService {
    
    /**
     * 点赞操作
     * 支持内容、评论、动态的点赞，包含目标对象和用户信息冗余
     * 
     * @param request 点赞请求
     * @return 点赞结果
     */
    Result<LikeResponse> addLike(LikeRequest request);
    
    /**
     * 取消点赞
     * 将点赞状态更新为cancelled
     * 
     * @param request 取消点赞请求
     * @return 取消结果
     */
    Result<Void> cancelLike(LikeCancelRequest request);
    
    /**
     * 切换点赞状态
     * 如果已点赞则取消，如果未点赞则添加
     * 
     * @param request 切换请求
     * @return 切换结果
     */
    Result<LikeResponse> toggleLike(LikeToggleRequest request);
    
    /**
     * 检查点赞状态
     * 查询用户是否已对目标对象点赞
     * 
     * @param userId 用户ID
     * @param likeType 点赞类型
     * @param targetId 目标ID
     * @return 点赞状态
     */
    Result<Boolean> checkLikeStatus(Long userId, String likeType, Long targetId);
    
    /**
     * 分页查询用户点赞记录
     * 对应Service方法：findUserLikes
     * 
     * @param userId 用户ID
     * @param likeType 点赞类型（可选）
     * @param status 状态（可选）
     * @param currentPage 当前页码
     * @param pageSize 页面大小
     * @return 用户点赞记录列表
     */
    Result<PageResponse<LikeResponse>> findUserLikes(Long userId, String likeType, String status, 
                                                    Integer currentPage, Integer pageSize);

    /**
     * 分页查询目标对象的点赞记录
     * 对应Service方法：findTargetLikes
     * 
     * @param targetId 目标对象ID
     * @param likeType 点赞类型
     * @param status 状态（可选）
     * @param currentPage 当前页码
     * @param pageSize 页面大小
     * @return 目标对象点赞记录列表
     */
    Result<PageResponse<LikeResponse>> findTargetLikes(Long targetId, String likeType, String status,
                                                      Integer currentPage, Integer pageSize);

    /**
     * 分页查询作者作品的点赞记录
     * 对应Service方法：findAuthorLikes
     * 
     * @param targetAuthorId 作品作者ID
     * @param likeType 点赞类型（可选）
     * @param status 状态（可选）
     * @param currentPage 当前页码
     * @param pageSize 页面大小
     * @return 作者作品点赞记录列表
     */
    Result<PageResponse<LikeResponse>> findAuthorLikes(Long targetAuthorId, String likeType, String status,
                                                      Integer currentPage, Integer pageSize);
    
    /**
     * 统计目标对象的点赞数量
     * 对应Service方法：countTargetLikes
     * 
     * @param targetId 目标对象ID
     * @param likeType 点赞类型
     * @return 点赞数量
     */
    Result<Long> countTargetLikes(Long targetId, String likeType);
    
    /**
     * 统计用户的点赞数量
     * 对应Service方法：countUserLikes
     * 
     * @param userId 用户ID
     * @param likeType 点赞类型（可选）
     * @return 点赞数量
     */
    Result<Long> countUserLikes(Long userId, String likeType);

    /**
     * 统计作者作品的被点赞数量
     * 对应Service方法：countAuthorLikes
     * 
     * @param targetAuthorId 作品作者ID
     * @param likeType 点赞类型（可选）
     * @return 被点赞数量
     */
    Result<Long> countAuthorLikes(Long targetAuthorId, String likeType);
    
    /**
     * 批量检查点赞状态
     * 对应Service方法：batchCheckLikeStatus
     * 
     * @param userId 用户ID
     * @param likeType 点赞类型
     * @param targetIds 目标ID列表
     * @return 点赞状态Map (targetId -> isLiked)
     */
    Result<java.util.Map<Long, Boolean>> batchCheckLikeStatus(Long userId, String likeType, java.util.List<Long> targetIds);

    /**
     * 查询时间范围内的点赞记录
     * 对应Service方法：findByTimeRange
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param likeType 点赞类型（可选）
     * @param status 状态（可选）
     * @return 点赞记录列表
     */
    Result<java.util.List<LikeResponse>> findByTimeRange(java.time.LocalDateTime startTime, 
                                                        java.time.LocalDateTime endTime,
                                                        String likeType, String status);
} 