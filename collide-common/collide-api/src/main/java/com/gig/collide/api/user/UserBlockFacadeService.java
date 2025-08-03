package com.gig.collide.api.user;

import com.gig.collide.api.user.request.block.UserBlockCreateRequest;
import com.gig.collide.api.user.request.block.UserBlockUpdateRequest;
import com.gig.collide.api.user.request.block.UserBlockQueryRequest;
import com.gig.collide.api.user.response.block.UserBlockResponse;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.web.vo.Result;

/**
 * 用户拉黑服务接口 - 对应 t_user_block 表
 * 负责用户拉黑关系管理功能
 * 
 * @author GIG Team
 * @version 2.0.0
 */
public interface UserBlockFacadeService {

    /**
     * 拉黑用户
     * 
     * @param request 拉黑创建请求
     * @return 拉黑结果
     */
    Result<UserBlockResponse> blockUser(UserBlockCreateRequest request);

    /**
     * 更新拉黑记录
     * 
     * @param request 拉黑更新请求
     * @return 更新结果
     */
    Result<UserBlockResponse> updateBlock(UserBlockUpdateRequest request);

    /**
     * 取消拉黑
     * 
     * @param userId 拉黑者用户ID
     * @param blockedUserId 被拉黑用户ID
     * @return 取消结果
     */
    Result<Void> unblockUser(Long userId, Long blockedUserId);

    /**
     * 检查拉黑状态
     * 
     * @param userId 拉黑者用户ID
     * @param blockedUserId 被拉黑用户ID
     * @return 是否已拉黑
     */
    Result<Boolean> checkBlockStatus(Long userId, Long blockedUserId);

    /**
     * 获取拉黑关系详情
     * 
     * @param userId 拉黑者用户ID
     * @param blockedUserId 被拉黑用户ID
     * @return 拉黑关系详情
     */
    Result<UserBlockResponse> getBlockRelation(Long userId, Long blockedUserId);

    /**
     * 获取用户拉黑列表
     * 
     * @param userId 用户ID
     * @param currentPage 当前页码
     * @param pageSize 页面大小
     * @return 拉黑用户列表
     */
    Result<PageResponse<UserBlockResponse>> getUserBlockList(Long userId, Integer currentPage, Integer pageSize);

    /**
     * 获取用户被拉黑列表
     * 
     * @param blockedUserId 被拉黑用户ID
     * @param currentPage 当前页码
     * @param pageSize 页面大小
     * @return 拉黑记录列表
     */
    Result<PageResponse<UserBlockResponse>> getUserBlockedList(Long blockedUserId, Integer currentPage, Integer pageSize);

    /**
     * 分页查询拉黑记录
     * 
     * @param request 查询请求
     * @return 分页拉黑记录列表
     */
    Result<PageResponse<UserBlockResponse>> queryBlocks(UserBlockQueryRequest request);

    /**
     * 统计用户拉黑数量
     * 
     * @param userId 用户ID
     * @return 拉黑数量
     */
    Result<Long> countUserBlocks(Long userId);

    /**
     * 统计用户被拉黑数量
     * 
     * @param blockedUserId 被拉黑用户ID
     * @return 被拉黑数量
     */
    Result<Long> countUserBlocked(Long blockedUserId);

    /**
     * 批量检查拉黑状态
     * 
     * @param userId 拉黑者用户ID
     * @param blockedUserIds 被拉黑用户ID列表
     * @return 拉黑状态Map（用户ID -> 是否已拉黑）
     */
    Result<java.util.Map<Long, Boolean>> batchCheckBlockStatus(Long userId, java.util.List<Long> blockedUserIds);

    /**
     * 批量拉黑用户
     * 
     * @param userId 拉黑者用户ID
     * @param blockedUserIds 被拉黑用户ID列表
     * @param reason 拉黑原因
     * @return 拉黑结果数量
     */
    Result<Integer> batchBlockUsers(Long userId, java.util.List<Long> blockedUserIds, String reason);

    /**
     * 批量取消拉黑
     * 
     * @param userId 拉黑者用户ID
     * @param blockedUserIds 被拉黑用户ID列表
     * @return 取消结果数量
     */
    Result<Integer> batchUnblockUsers(Long userId, java.util.List<Long> blockedUserIds);

    /**
     * 获取互相拉黑的用户列表
     * 
     * @param userId 用户ID
     * @param currentPage 当前页码
     * @param pageSize 页面大小
     * @return 互相拉黑的用户列表
     */
    Result<PageResponse<UserBlockResponse>> getMutualBlockList(Long userId, Integer currentPage, Integer pageSize);

    /**
     * 检查是否互相拉黑
     * 
     * @param userId1 用户1ID
     * @param userId2 用户2ID
     * @return 是否互相拉黑
     */
    Result<Boolean> checkMutualBlock(Long userId1, Long userId2);

    /**
     * 获取最近拉黑记录
     * 
     * @param userId 用户ID
     * @param limit 返回数量限制
     * @return 最近拉黑记录列表
     */
    Result<java.util.List<UserBlockResponse>> getRecentBlocks(Long userId, Integer limit);

    /**
     * 根据拉黑原因统计
     * 
     * @return 拉黑原因统计Map
     */
    Result<java.util.Map<String, Long>> getBlockReasonStatistics();

    /**
     * 获取平台拉黑统计信息
     * 
     * @return 平台拉黑统计数据
     */
    Result<java.util.Map<String, Object>> getPlatformBlockStats();

    /**
     * 删除拉黑记录（物理删除）
     * 
     * @param id 拉黑记录ID
     * @return 删除结果
     */
    Result<Void> deleteBlock(Long id);

    /**
     * 清理已取消的拉黑记录
     * 
     * @param days 清理多少天前的记录
     * @return 清理数量
     */
    Result<Integer> cleanCancelledBlocks(Integer days);
}