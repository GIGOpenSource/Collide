package com.gig.collide.users.infrastructure.mapper;

import com.gig.collide.users.domain.entity.UserStats;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户统计Mapper接口 - 对应 t_user_stats 表
 * 负责用户统计数据管理
 * 
 * @author GIG Team
 * @version 2.0.0
 */
@Mapper
public interface UserStatsMapper {

    /**
     * 根据用户ID查询统计数据
     */
    UserStats findByUserId(@Param("userId") Long userId);

    /**
     * 批量查询用户统计数据
     */
    List<UserStats> findByUserIds(@Param("userIds") List<Long> userIds);

    /**
     * 分页查询用户统计数据
     */
    List<UserStats> findStatsByCondition(@Param("followerCountMin") Integer followerCountMin,
                                        @Param("followerCountMax") Integer followerCountMax,
                                        @Param("followingCountMin") Integer followingCountMin,
                                        @Param("followingCountMax") Integer followingCountMax,
                                        @Param("contentCountMin") Integer contentCountMin,
                                        @Param("contentCountMax") Integer contentCountMax,
                                        @Param("likeCountMin") Integer likeCountMin,
                                        @Param("likeCountMax") Integer likeCountMax,
                                        @Param("loginCountMin") Integer loginCountMin,
                                        @Param("loginCountMax") Integer loginCountMax,
                                        @Param("sortField") String sortField,
                                        @Param("sortDirection") String sortDirection,
                                        @Param("offset") Integer offset,
                                        @Param("size") Integer size);

    /**
     * 统计用户统计数据数量
     */
    Long countStatsByCondition(@Param("followerCountMin") Integer followerCountMin,
                              @Param("followerCountMax") Integer followerCountMax,
                              @Param("followingCountMin") Integer followingCountMin,
                              @Param("followingCountMax") Integer followingCountMax,
                              @Param("contentCountMin") Integer contentCountMin,
                              @Param("contentCountMax") Integer contentCountMax,
                              @Param("likeCountMin") Integer likeCountMin,
                              @Param("likeCountMax") Integer likeCountMax,
                              @Param("loginCountMin") Integer loginCountMin,
                              @Param("loginCountMax") Integer loginCountMax);

    /**
     * 查询粉丝数排行榜
     */
    List<UserStats> findTopFollowerUsers(@Param("limit") Integer limit);

    /**
     * 查询最活跃用户（登录次数排行）
     */
    List<UserStats> findMostActiveUsers(@Param("limit") Integer limit);

    /**
     * 查询内容数排行榜
     */
    List<UserStats> findTopContentUsers(@Param("limit") Integer limit);

    /**
     * 插入用户统计数据
     */
    int insert(UserStats userStats);

    /**
     * 更新用户统计数据
     */
    int updateByUserId(UserStats userStats);

    /**
     * 增加粉丝数
     */
    int incrementFollowerCount(@Param("userId") Long userId, @Param("increment") Integer increment);

    /**
     * 增加关注数
     */
    int incrementFollowingCount(@Param("userId") Long userId, @Param("increment") Integer increment);

    /**
     * 增加内容数
     */
    int incrementContentCount(@Param("userId") Long userId, @Param("increment") Integer increment);

    /**
     * 增加点赞数
     */
    int incrementLikeCount(@Param("userId") Long userId, @Param("increment") Integer increment);

    /**
     * 增加登录次数
     */
    int incrementLoginCount(@Param("userId") Long userId);

    /**
     * 重置用户统计数据
     */
    int resetUserStats(@Param("userId") Long userId);

    /**
     * 批量更新用户统计数据
     */
    int batchUpdateStats(@Param("statsUpdates") List<UserStats> statsUpdates);

    /**
     * 检查用户统计数据是否存在
     */
    int checkStatsExists(@Param("userId") Long userId);

    /**
     * 删除用户统计数据
     */
    int deleteByUserId(@Param("userId") Long userId);
}