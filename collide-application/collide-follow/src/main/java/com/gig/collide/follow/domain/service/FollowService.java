package com.gig.collide.follow.domain.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.gig.collide.follow.domain.entity.Follow;

import java.util.List;
import java.util.Map;

/**
 * 关注业务逻辑接口 - 简洁版
 * 基于follow-simple.sql的业务设计，实现核心关注功能
 * 
 * @author Collide
 * @version 2.0.0 (简洁版)
 * @since 2024-01-01
 */
public interface FollowService {

    /**
     * 关注用户
     * 包含关注者和被关注者信息冗余存储
     * 
     * @param follow 关注实体
     * @return 创建的关注关系
     */
    Follow followUser(Follow follow);

    /**
     * 取消关注
     * 逻辑删除，将状态更新为cancelled
     * 
     * @param followerId 关注者ID
     * @param followeeId 被关注者ID
     * @param cancelReason 取消原因
     * @param operatorId 操作人ID
     * @return 是否成功
     */
    boolean unfollowUser(Long followerId, Long followeeId, String cancelReason, Long operatorId);

    /**
     * 检查关注状态
     * 
     * @param followerId 关注者ID
     * @param followeeId 被关注者ID
     * @return 是否已关注
     */
    boolean checkFollowStatus(Long followerId, Long followeeId);

    /**
     * 获取关注关系详情
     * 
     * @param followerId 关注者ID
     * @param followeeId 被关注者ID
     * @return 关注关系
     */
    Follow getFollowRelation(Long followerId, Long followeeId);

    /**
     * 分页查询关注记录
     * 支持复合条件查询
     * 
     * @param currentPage 页码
     * @param pageSize 页面大小
     * @param followerId 关注者ID（可选）
     * @param followeeId 被关注者ID（可选）
     * @param followerNickname 关注者昵称关键词（可选）
     * @param followeeNickname 被关注者昵称关键词（可选）
     * @param status 状态（可选）
     * @param queryType 查询类型（可选）
     * @param orderBy 排序字段
     * @param orderDirection 排序方向
     * @return 分页结果
     */
    IPage<Follow> queryFollows(Integer currentPage, Integer pageSize, Long followerId, Long followeeId,
                              String followerNickname, String followeeNickname, String status,
                              String queryType, String orderBy, String orderDirection);

    /**
     * 获取用户的关注列表
     * 
     * @param followerId 关注者ID
     * @param currentPage 页码
     * @param pageSize 页面大小
     * @return 关注列表
     */
    IPage<Follow> getFollowing(Long followerId, Integer currentPage, Integer pageSize);

    /**
     * 获取用户的粉丝列表
     * 
     * @param followeeId 被关注者ID
     * @param currentPage 页码
     * @param pageSize 页面大小
     * @return 粉丝列表
     */
    IPage<Follow> getFollowers(Long followeeId, Integer currentPage, Integer pageSize);

    /**
     * 获取互相关注的好友
     * 
     * @param userId 用户ID
     * @param currentPage 页码
     * @param pageSize 页面大小
     * @return 互关好友列表
     */
    IPage<Follow> getMutualFollows(Long userId, Integer currentPage, Integer pageSize);

    /**
     * 获取用户关注数量
     * 
     * @param followerId 关注者ID
     * @return 关注数量
     */
    Long getFollowingCount(Long followerId);

    /**
     * 获取用户粉丝数量
     * 
     * @param followeeId 被关注者ID
     * @return 粉丝数量
     */
    Long getFollowersCount(Long followeeId);

    /**
     * 获取用户关注统计信息
     * 
     * @param userId 用户ID
     * @return 统计信息Map
     */
    Map<String, Object> getFollowStatistics(Long userId);

    /**
     * 批量检查关注状态
     * 
     * @param followerId 关注者ID
     * @param followeeIds 被关注者ID列表
     * @return 关注状态Map
     */
    Map<Long, Boolean> batchCheckFollowStatus(Long followerId, List<Long> followeeIds);

    /**
     * 根据昵称搜索关注关系
     * 
     * @param followerId 关注者ID（可选）
     * @param followeeId 被关注者ID（可选）
     * @param nicknameKeyword 昵称关键词
     * @param currentPage 页码
     * @param pageSize 页面大小
     * @return 搜索结果
     */
    IPage<Follow> searchByNickname(Long followerId, Long followeeId, String nicknameKeyword, 
                                  Integer currentPage, Integer pageSize);

    /**
     * 更新用户信息（冗余字段）
     * 当用户信息变更时，同步更新关注表中的冗余信息
     * 
     * @param userId 用户ID
     * @param nickname 新昵称
     * @param avatar 新头像
     * @return 更新成功的记录数
     */
    int updateUserInfo(Long userId, String nickname, String avatar);

    /**
     * 清理已取消的关注记录
     * 
     * @param days 保留天数
     * @return 清理数量
     */
    int cleanCancelledFollows(Integer days);

    /**
     * 查询用户间的关注关系链
     * 
     * @param userIdA 用户A ID
     * @param userIdB 用户B ID
     * @return 关注关系链
     */
    List<Follow> getRelationChain(Long userIdA, Long userIdB);

    /**
     * 验证关注请求参数
     * 
     * @param follow 关注对象
     * @return 验证结果信息
     */
    String validateFollowRequest(Follow follow);

    /**
     * 检查是否可以关注
     * 
     * @param followerId 关注者ID
     * @param followeeId 被关注者ID
     * @return 检查结果
     */
    String checkCanFollow(Long followerId, Long followeeId);

    /**
     * 检查是否已经存在关注关系
     * 
     * @param followerId 关注者ID
     * @param followeeId 被关注者ID
     * @return 是否存在（包括已取消的）
     */
    boolean existsFollowRelation(Long followerId, Long followeeId);

    /**
     * 重新激活已取消的关注关系
     * 
     * @param followerId 关注者ID
     * @param followeeId 被关注者ID
     * @return 是否成功
     */
    boolean reactivateFollow(Long followerId, Long followeeId);

    /**
     * 更新关注状态
     * 
     * @param followerId 关注者ID
     * @param followeeId 被关注者ID
     * @param status 新状态
     * @return 是否成功
     */
    boolean updateFollowStatus(Long followerId, Long followeeId, String status);
}