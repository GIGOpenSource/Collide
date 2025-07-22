package com.gig.collide.follow.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gig.collide.follow.domain.entity.FollowStatistics;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 关注统计数据访问映射器
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Mapper
public interface FollowStatisticsMapper extends BaseMapper<FollowStatistics> {

    /**
     * 增加关注数
     *
     * @param userId 用户ID
     * @return 影响行数
     */
    int incrementFollowingCount(@Param("userId") Long userId);

    /**
     * 减少关注数
     *
     * @param userId 用户ID
     * @return 影响行数
     */
    int decrementFollowingCount(@Param("userId") Long userId);

    /**
     * 增加粉丝数
     *
     * @param userId 用户ID
     * @return 影响行数
     */
    int incrementFollowerCount(@Param("userId") Long userId);

    /**
     * 减少粉丝数
     *
     * @param userId 用户ID
     * @return 影响行数
     */
    int decrementFollowerCount(@Param("userId") Long userId);

    /**
     * 批量查询关注统计
     *
     * @param userIds 用户ID列表
     * @return 关注统计列表
     */
    List<FollowStatistics> selectByUserIds(@Param("userIds") List<Long> userIds);

    /**
     * 重新计算用户关注统计
     *
     * @param userId 用户ID
     * @return 影响行数
     */
    int recalculateStatistics(@Param("userId") Long userId);
} 