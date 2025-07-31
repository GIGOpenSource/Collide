package com.gig.collide.users.domain.service;

import com.gig.collide.api.user.request.UserBlockQueryRequest;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.users.domain.entity.UserBlock;

/**
 * 用户拉黑领域服务接口 - 简洁版
 * 
 * @author GIG Team
 * @version 2.0.0
 */
public interface UserBlockService {

    /**
     * 拉黑用户
     */
    UserBlock blockUser(Long userId, Long blockedUserId, String userUsername, String blockedUsername, String reason);

    /**
     * 取消拉黑
     */
    void unblockUser(Long userId, Long blockedUserId);

    /**
     * 检查拉黑状态
     */
    boolean isBlocked(Long userId, Long blockedUserId);

    /**
     * 查询拉黑关系
     */
    UserBlock getBlockRelation(Long userId, Long blockedUserId);

    /**
     * 根据ID查询拉黑记录
     */
    UserBlock getBlockById(Long id);

    /**
     * 获取用户拉黑列表
     */
    PageResponse<UserBlock> getUserBlockList(Long userId, Integer pageNum, Integer pageSize);

    /**
     * 获取用户被拉黑列表
     */
    PageResponse<UserBlock> getUserBlockedList(Long blockedUserId, Integer pageNum, Integer pageSize);

    /**
     * 分页查询拉黑记录
     */
    PageResponse<UserBlock> queryBlocks(UserBlockQueryRequest request);

    /**
     * 统计用户拉黑数量
     */
    Long countUserBlocks(Long userId);

    /**
     * 统计用户被拉黑数量
     */
    Long countUserBlocked(Long blockedUserId);

    /**
     * 更新拉黑状态
     */
    void updateBlockStatus(Long id, String status);

    /**
     * 删除拉黑记录
     */
    void deleteBlock(Long id);
}