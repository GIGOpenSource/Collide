package com.gig.collide.follow.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gig.collide.api.follow.constant.FollowStatus;
import com.gig.collide.follow.domain.entity.Follow;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 关注关系数据访问映射器
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Mapper
public interface FollowMapper extends BaseMapper<Follow> {

    /**
     * 查询关注关系
     *
     * @param followerUserId 关注者用户ID
     * @param followedUserId 被关注者用户ID
     * @param status 状态
     * @return 关注关系
     */
    Follow selectFollowRelation(@Param("followerUserId") Long followerUserId,
                               @Param("followedUserId") Long followedUserId,
                               @Param("status") FollowStatus status);

    /**
     * 分页查询用户关注列表
     *
     * @param page 分页参数
     * @param followerUserId 关注者用户ID
     * @param status 状态
     * @return 关注列表
     */
    IPage<Follow> selectFollowingPage(Page<Follow> page,
                                     @Param("followerUserId") Long followerUserId,
                                     @Param("status") FollowStatus status);

    /**
     * 分页查询用户粉丝列表
     *
     * @param page 分页参数
     * @param followedUserId 被关注者用户ID
     * @param status 状态
     * @return 粉丝列表
     */
    IPage<Follow> selectFollowersPage(Page<Follow> page,
                                     @Param("followedUserId") Long followedUserId,
                                     @Param("status") FollowStatus status);

    /**
     * 查询相互关注列表
     *
     * @param userId 用户ID
     * @param status 状态
     * @return 相互关注列表
     */
    List<Follow> selectMutualFollows(@Param("userId") Long userId,
                                    @Param("status") FollowStatus status);

    /**
     * 批量查询关注关系
     *
     * @param followerUserId 关注者用户ID
     * @param followedUserIds 被关注者用户ID列表
     * @param status 状态
     * @return 关注关系列表
     */
    List<Follow> selectFollowRelations(@Param("followerUserId") Long followerUserId,
                                      @Param("followedUserIds") List<Long> followedUserIds,
                                      @Param("status") FollowStatus status);

    /**
     * 统计用户关注数
     *
     * @param followerUserId 关注者用户ID
     * @param status 状态
     * @return 关注数
     */
    Integer countFollowing(@Param("followerUserId") Long followerUserId,
                          @Param("status") FollowStatus status);

    /**
     * 统计用户粉丝数
     *
     * @param followedUserId 被关注者用户ID
     * @param status 状态
     * @return 粉丝数
     */
    Integer countFollowers(@Param("followedUserId") Long followedUserId,
                          @Param("status") FollowStatus status);
} 