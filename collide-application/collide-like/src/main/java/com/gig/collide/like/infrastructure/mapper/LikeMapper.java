package com.gig.collide.like.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gig.collide.like.domain.entity.Like;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 点赞数据访问层 - 简洁版
 * 基于MyBatis-Plus，实现简洁的数据访问
 * 
 * @author Collide
 * @version 2.0.0 (简洁版)
 * @since 2024-01-01
 */
@Mapper
public interface LikeMapper extends BaseMapper<Like> {

    /**
     * 查询用户对目标对象的点赞记录
     * 
     * @param userId 用户ID
     * @param likeType 点赞类型
     * @param targetId 目标对象ID
     * @return 点赞记录
     */
    Like findByUserAndTarget(@Param("userId") Long userId,
                            @Param("likeType") String likeType,
                            @Param("targetId") Long targetId);

    /**
     * 检查用户是否已点赞
     * 
     * @param userId 用户ID
     * @param likeType 点赞类型
     * @param targetId 目标对象ID
     * @return 是否已点赞
     */
    Boolean checkLikeExists(@Param("userId") Long userId,
                           @Param("likeType") String likeType,
                           @Param("targetId") Long targetId);

    /**
     * 分页查询用户点赞记录
     * 
     * @param page 分页参数
     * @param userId 用户ID
     * @param likeType 点赞类型（可选）
     * @param status 状态（可选）
     * @return 点赞分页数据
     */
    IPage<Like> findUserLikes(Page<Like> page,
                             @Param("userId") Long userId,
                             @Param("likeType") String likeType,
                             @Param("status") String status);

    /**
     * 分页查询目标对象的点赞记录
     * 
     * @param page 分页参数
     * @param targetId 目标对象ID
     * @param likeType 点赞类型
     * @param status 状态（可选）
     * @return 点赞分页数据
     */
    IPage<Like> findTargetLikes(Page<Like> page,
                               @Param("targetId") Long targetId,
                               @Param("likeType") String likeType,
                               @Param("status") String status);

    /**
     * 查询作者作品的点赞记录
     * 
     * @param page 分页参数
     * @param targetAuthorId 作品作者ID
     * @param likeType 点赞类型（可选）
     * @param status 状态（可选）
     * @return 点赞分页数据
     */
    IPage<Like> findAuthorLikes(Page<Like> page,
                               @Param("targetAuthorId") Long targetAuthorId,
                               @Param("likeType") String likeType,
                               @Param("status") String status);

    /**
     * 统计目标对象的点赞数量
     * 
     * @param targetId 目标对象ID
     * @param likeType 点赞类型
     * @param status 状态（默认active）
     * @return 点赞数量
     */
    Long countTargetLikes(@Param("targetId") Long targetId,
                         @Param("likeType") String likeType,
                         @Param("status") String status);

    /**
     * 统计用户的点赞数量
     * 
     * @param userId 用户ID
     * @param likeType 点赞类型（可选）
     * @param status 状态（默认active）
     * @return 点赞数量
     */
    Long countUserLikes(@Param("userId") Long userId,
                       @Param("likeType") String likeType,
                       @Param("status") String status);

    /**
     * 统计作者作品的被点赞数量
     * 
     * @param targetAuthorId 作品作者ID
     * @param likeType 点赞类型（可选）
     * @param status 状态（默认active）
     * @return 被点赞数量
     */
    Long countAuthorLikes(@Param("targetAuthorId") Long targetAuthorId,
                         @Param("likeType") String likeType,
                         @Param("status") String status);

    /**
     * 批量检查点赞状态
     * 
     * @param userId 用户ID
     * @param likeType 点赞类型
     * @param targetIds 目标对象ID列表
     * @return 已点赞的目标对象ID列表
     */
    List<Long> batchCheckLikeStatus(@Param("userId") Long userId,
                                   @Param("likeType") String likeType,
                                   @Param("targetIds") List<Long> targetIds);

    /**
     * 更新点赞状态
     * 
     * @param id 点赞ID
     * @param status 新状态
     * @return 更新行数
     */
    int updateLikeStatus(@Param("id") Long id,
                        @Param("status") String status);

    /**
     * 查询时间范围内的点赞记录
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param likeType 点赞类型（可选）
     * @param status 状态（可选）
     * @return 点赞列表
     */
    List<Like> findByTimeRange(@Param("startTime") LocalDateTime startTime,
                              @Param("endTime") LocalDateTime endTime,
                              @Param("likeType") String likeType,
                              @Param("status") String status);
}