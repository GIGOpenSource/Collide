package com.gig.collide.like.domain.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.gig.collide.like.domain.entity.Like;

import java.util.List;
import java.util.Map;

/**
 * 点赞业务逻辑接口 - MySQL 8.0 优化版
 * 基于like-simple.sql的业务设计，与LikeMapper完全对应
 * 
 * 接口特性：
 * - 与LikeMapper方法一一对应
 * - 支持用户、目标对象、作者三个维度的查询
 * - 支持时间范围查询和批量操作
 * - 统一的命名规范和参数传递
 * 
 * @author Collide
 * @version 2.0.0 (MySQL 8.0 优化版)
 * @since 2024-01-01
 */
public interface LikeService {

    /**
     * 添加点赞
     * 如果已存在且为取消状态，则重新激活；如果不存在，则创建新记录
     * 
     * @param like 点赞实体
     * @return 点赞记录
     */
    Like addLike(Like like);

    /**
     * 取消点赞
     * 将点赞状态更新为cancelled
     * 
     * @param userId 用户ID
     * @param likeType 点赞类型
     * @param targetId 目标对象ID
     * @return 是否成功
     */
    boolean cancelLike(Long userId, String likeType, Long targetId);

    /**
     * 切换点赞状态
     * 如果已点赞则取消，如果未点赞则添加
     * 
     * @param like 点赞信息
     * @return 切换后的点赞记录（如果是取消则返回null）
     */
    Like toggleLike(Like like);

    /**
     * 检查点赞状态
     * 查询用户是否已对目标对象点赞
     * 
     * @param userId 用户ID
     * @param likeType 点赞类型
     * @param targetId 目标对象ID
     * @return 是否已点赞
     */
    boolean checkLikeStatus(Long userId, String likeType, Long targetId);

    /**
     * 获取用户对目标对象的点赞记录
     * 
     * @param userId 用户ID
     * @param likeType 点赞类型
     * @param targetId 目标对象ID
     * @return 点赞记录
     */
    Like getLikeRecord(Long userId, String likeType, Long targetId);

    /**
     * 分页查询用户点赞记录
     * 对应Mapper方法：findUserLikes
     * 
     * @param pageNum 页码
     * @param pageSize 页面大小
     * @param userId 用户ID
     * @param likeType 点赞类型（可选）
     * @param status 状态（可选）
     * @return 分页结果
     */
    IPage<Like> findUserLikes(Integer pageNum, Integer pageSize, Long userId, String likeType, String status);

    /**
     * 分页查询目标对象的点赞记录
     * 对应Mapper方法：findTargetLikes
     * 
     * @param pageNum 页码
     * @param pageSize 页面大小
     * @param targetId 目标对象ID
     * @param likeType 点赞类型
     * @param status 状态（可选）
     * @return 分页结果
     */
    IPage<Like> findTargetLikes(Integer pageNum, Integer pageSize, Long targetId, String likeType, String status);

    /**
     * 分页查询作者作品的点赞记录
     * 对应Mapper方法：findAuthorLikes
     * 
     * @param pageNum 页码
     * @param pageSize 页面大小
     * @param targetAuthorId 作品作者ID
     * @param likeType 点赞类型（可选）
     * @param status 状态（可选）
     * @return 分页结果
     */
    IPage<Like> findAuthorLikes(Integer pageNum, Integer pageSize, Long targetAuthorId, String likeType, String status);

    /**
     * 统计目标对象的点赞数量
     * 对应Mapper方法：countTargetLikes
     * 
     * @param targetId 目标对象ID
     * @param likeType 点赞类型
     * @return 点赞数量
     */
    Long countTargetLikes(Long targetId, String likeType);

    /**
     * 获取用户的点赞数量
     * 对应Mapper方法：countUserLikes
     * 
     * @param userId 用户ID
     * @param likeType 点赞类型（可选）
     * @return 点赞数量
     */
    Long countUserLikes(Long userId, String likeType);

    /**
     * 统计作者作品的被点赞数量
     * 对应Mapper方法：countAuthorLikes
     * 
     * @param targetAuthorId 作品作者ID
     * @param likeType 点赞类型（可选）
     * @return 被点赞数量
     */
    Long countAuthorLikes(Long targetAuthorId, String likeType);

    /**
     * 批量检查点赞状态
     * 对应Mapper方法：batchCheckLikeStatus
     * 
     * @param userId 用户ID
     * @param likeType 点赞类型
     * @param targetIds 目标对象ID列表
     * @return 点赞状态Map (targetId -> isLiked)
     */
    Map<Long, Boolean> batchCheckLikeStatus(Long userId, String likeType, List<Long> targetIds);

    /**
     * 查询时间范围内的点赞记录
     * 对应Mapper方法：findByTimeRange
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param likeType 点赞类型（可选）
     * @param status 状态（可选）
     * @return 点赞列表
     */
    List<Like> findByTimeRange(java.time.LocalDateTime startTime, java.time.LocalDateTime endTime, 
                              String likeType, String status);

    /**
     * 根据ID获取点赞记录
     * 
     * @param id 点赞ID
     * @return 点赞记录
     */
    Like getLikeById(Long id);

    /**
     * 删除点赞记录（物理删除）
     * 仅用于数据清理，正常业务使用取消点赞
     * 
     * @param id 点赞ID
     * @return 是否成功
     */
    boolean deleteLike(Long id);

    /**
     * 验证点赞请求参数
     * 
     * @param like 点赞对象
     * @return 验证结果信息
     */
    String validateLikeRequest(Like like);
}