package com.gig.collide.follow.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gig.collide.follow.domain.entity.FollowStatistics;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 关注统计数据访问层
 * @author GIG
 */
@Mapper
public interface FollowStatisticsMapper extends BaseMapper<FollowStatistics> {

    /**
     * 增加关注数
     */
    void incrementFollowingCount(@Param("userId") Long userId);

    /**
     * 减少关注数
     */
    void decrementFollowingCount(@Param("userId") Long userId);

    /**
     * 增加粉丝数
     */
    void incrementFollowerCount(@Param("userId") Long userId);

    /**
     * 减少粉丝数
     */
    void decrementFollowerCount(@Param("userId") Long userId);

    /**
     * 初始化用户统计记录
     */
    void initUserStatistics(@Param("userId") Long userId);
} 