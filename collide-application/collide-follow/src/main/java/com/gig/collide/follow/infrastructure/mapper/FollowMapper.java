package com.gig.collide.follow.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gig.collide.follow.domain.entity.Follow;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 关注数据访问层 - 简洁版
 * 基于MyBatis-Plus，实现简洁的数据访问
 * 
 * @author Collide
 * @version 2.0.0 (简洁版)
 * @since 2024-01-01
 */
@Mapper
public interface FollowMapper extends BaseMapper<Follow> {

    /**
     * 根据关注者和被关注者查询关注关系
     * 
     * @param followerId 关注者ID
     * @param followeeId 被关注者ID
     * @param status 状态（可选）
     * @return 关注关系
     */
    Follow findByFollowerAndFollowee(@Param("followerId") Long followerId,
                                   @Param("followeeId") Long followeeId,
                                   @Param("status") String status);

    /**
     * 检查关注关系是否存在
     * 
     * @param followerId 关注者ID
     * @param followeeId 被关注者ID
     * @param status 状态（可选）
     * @return 是否存在
     */
    boolean checkFollowExists(@Param("followerId") Long followerId,
                             @Param("followeeId") Long followeeId,
                             @Param("status") String status);

    /**
     * 获取用户的关注列表
     * 查询某用户关注的所有人
     * 
     * @param page 分页参数
     * @param followerId 关注者ID
     * @param status 状态（可选）
     * @return 关注列表
     */
    IPage<Follow> findFollowing(Page<Follow> page,
                               @Param("followerId") Long followerId,
                               @Param("status") String status);

    /**
     * 获取用户的粉丝列表
     * 查询关注某用户的所有人
     * 
     * @param page 分页参数
     * @param followeeId 被关注者ID
     * @param status 状态（可选）
     * @return 粉丝列表
     */
    IPage<Follow> findFollowers(Page<Follow> page,
                               @Param("followeeId") Long followeeId,
                               @Param("status") String status);

    /**
     * 获取互相关注的好友
     * 查询双向关注关系
     * 
     * @param page 分页参数
     * @param userId 用户ID
     * @param status 状态（可选）
     * @return 互关好友列表
     */
    IPage<Follow> findMutualFollows(Page<Follow> page,
                                   @Param("userId") Long userId,
                                   @Param("status") String status);

    /**
     * 根据昵称搜索关注关系
     * 
     * @param page 分页参数
     * @param followerId 关注者ID（可选）
     * @param followeeId 被关注者ID（可选）
     * @param nicknameKeyword 昵称关键词
     * @param status 状态（可选）
     * @return 搜索结果
     */
    IPage<Follow> searchByNickname(Page<Follow> page,
                                  @Param("followerId") Long followerId,
                                  @Param("followeeId") Long followeeId,
                                  @Param("nicknameKeyword") String nicknameKeyword,
                                  @Param("status") String status);

    /**
     * 统计用户的关注数量
     * 
     * @param followerId 关注者ID
     * @param status 状态（可选）
     * @return 关注数量
     */
    Long countFollowing(@Param("followerId") Long followerId,
                       @Param("status") String status);

    /**
     * 统计用户的粉丝数量
     * 
     * @param followeeId 被关注者ID
     * @param status 状态（可选）
     * @return 粉丝数量
     */
    Long countFollowers(@Param("followeeId") Long followeeId,
                       @Param("status") String status);

    /**
     * 批量检查关注状态
     * 
     * @param followerId 关注者ID
     * @param followeeIds 被关注者ID列表
     * @param status 状态（可选）
     * @return 关注状态列表
     */
    List<Map<String, Object>> batchCheckFollowStatus(@Param("followerId") Long followerId,
                                                    @Param("followeeIds") List<Long> followeeIds,
                                                    @Param("status") String status);

    /**
     * 更新关注状态
     * 
     * @param followerId 关注者ID
     * @param followeeId 被关注者ID
     * @param status 新状态
     * @return 更新行数
     */
    int updateFollowStatus(@Param("followerId") Long followerId,
                          @Param("followeeId") Long followeeId,
                          @Param("status") String status);

    /**
     * 更新用户信息（冗余字段）
     * 
     * @param userId 用户ID
     * @param nickname 新昵称
     * @param avatar 新头像
     * @param updateAsFollower 是否更新作为关注者的信息
     * @param updateAsFollowee 是否更新作为被关注者的信息
     * @return 更新行数
     */
    int updateUserInfo(@Param("userId") Long userId,
                      @Param("nickname") String nickname,
                      @Param("avatar") String avatar,
                      @Param("updateAsFollower") Boolean updateAsFollower,
                      @Param("updateAsFollowee") Boolean updateAsFollowee);

    /**
     * 获取用户的关注统计信息
     * 
     * @param userId 用户ID
     * @return 统计信息
     */
    Map<String, Object> getUserFollowStatistics(@Param("userId") Long userId);

    /**
     * 清理已取消的关注记录
     * 
     * @param days 保留天数
     * @return 清理数量
     */
    int cleanCancelledFollows(@Param("days") Integer days);

    /**
     * 复合条件查询关注记录
     * 
     * @param page 分页参数
     * @param followerId 关注者ID（可选）
     * @param followeeId 被关注者ID（可选）
     * @param followerNickname 关注者昵称关键词（可选）
     * @param followeeNickname 被关注者昵称关键词（可选）
     * @param status 状态（可选）
     * @param queryType 查询类型（可选）
     * @param orderBy 排序字段
     * @param orderDirection 排序方向
     * @return 查询结果
     */
    IPage<Follow> findWithConditions(Page<Follow> page,
                                    @Param("followerId") Long followerId,
                                    @Param("followeeId") Long followeeId,
                                    @Param("followerNickname") String followerNickname,
                                    @Param("followeeNickname") String followeeNickname,
                                    @Param("status") String status,
                                    @Param("queryType") String queryType,
                                    @Param("orderBy") String orderBy,
                                    @Param("orderDirection") String orderDirection);

    /**
     * 查询用户间的关注关系链
     * 检查是否存在A关注B且B关注A的情况
     * 
     * @param userIdA 用户A ID
     * @param userIdB 用户B ID
     * @return 关注关系信息
     */
    List<Follow> findRelationChain(@Param("userIdA") Long userIdA,
                                  @Param("userIdB") Long userIdB);
}