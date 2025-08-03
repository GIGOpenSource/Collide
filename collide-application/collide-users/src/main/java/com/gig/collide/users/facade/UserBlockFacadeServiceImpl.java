package com.gig.collide.users.facade;

import com.gig.collide.api.user.UserBlockFacadeService;
import com.gig.collide.api.user.request.block.UserBlockCreateRequest;
import com.gig.collide.api.user.request.block.UserBlockQueryRequest;
import com.gig.collide.api.user.request.block.UserBlockUpdateRequest;
import com.gig.collide.api.user.response.block.UserBlockResponse;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.users.domain.entity.UserBlock;
import com.gig.collide.users.domain.service.UserBlockService;
import com.gig.collide.users.infrastructure.cache.UserCacheConstant;
import com.gig.collide.web.vo.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.apache.dubbo.config.annotation.DubboService;
import com.alicp.jetcache.anno.Cached;
import com.alicp.jetcache.anno.CacheInvalidate;
import com.alicp.jetcache.anno.CacheUpdate;
import com.alicp.jetcache.anno.CacheType;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * 用户拉黑门面服务实现 - 对应 t_user_block 表
 * Dubbo独立微服务提供者 - 负责用户拉黑关系管理
 * 
 * @author GIG Team
 * @version 2.0.0 (Dubbo微服务版 - 6表架构)
 */
@Slf4j
@DubboService(version = "1.0.0", interfaceClass = UserBlockFacadeService.class)
@RequiredArgsConstructor
public class UserBlockFacadeServiceImpl implements UserBlockFacadeService {

    private final UserBlockService userBlockService;

    @Override
    @CacheInvalidate(name = UserCacheConstant.USER_BLOCK_LIST_CACHE)
    @CacheInvalidate(name = UserCacheConstant.USER_BLOCK_RELATION_CACHE)
    public Result<UserBlockResponse> blockUser(UserBlockCreateRequest request) {
        try {
            log.info("拉黑用户请求: userId={}, blockedUserId={}", request.getUserId(), request.getBlockedUserId());
            
            UserBlock savedBlock = userBlockService.blockUser(
                request.getUserId(), 
                request.getBlockedUserId(), 
                request.getUserUsername(), 
                request.getBlockedUsername(), 
                request.getReason()
            );
            UserBlockResponse response = convertToResponse(savedBlock);
            
            log.info("用户拉黑成功: userId={}, blockedUserId={}", request.getUserId(), request.getBlockedUserId());
            return Result.success(response);
        } catch (Exception e) {
            log.error("拉黑用户失败", e);
            return Result.error("BLOCK_USER_ERROR", "拉黑用户失败: " + e.getMessage());
        }
    }

    @Override
    @CacheUpdate(name = UserCacheConstant.USER_BLOCK_DETAIL_CACHE,
                 key = UserCacheConstant.USER_BLOCK_DETAIL_KEY,
                 value = "#result.data")
    @CacheInvalidate(name = UserCacheConstant.USER_BLOCK_LIST_CACHE)
    public Result<UserBlockResponse> updateBlock(UserBlockUpdateRequest request) {
        try {
            log.info("更新拉黑关系请求: id={}", request.getId());
            
            UserBlock userBlock = userBlockService.getBlockById(request.getId());
            if (userBlock == null) {
                return Result.error("BLOCK_NOT_FOUND", "拉黑关系不存在");
            }
            
            // 简化更新：删除旧记录，创建新记录
            userBlockService.deleteBlock(request.getId());
            UserBlock updatedBlock = userBlockService.blockUser(
                userBlock.getUserId(),
                userBlock.getBlockedUserId(), 
                userBlock.getUserUsername(),
                userBlock.getBlockedUsername(),
                request.getReason() != null ? request.getReason() : userBlock.getReason()
            );
            UserBlockResponse response = convertToResponse(updatedBlock);
            
            log.info("拉黑关系更新成功: id={}", updatedBlock.getId());
            return Result.success(response);
        } catch (Exception e) {
            log.error("更新拉黑关系失败", e);
            return Result.error("BLOCK_UPDATE_ERROR", "更新拉黑关系失败: " + e.getMessage());
        }
    }

    @Override
    @CacheInvalidate(name = UserCacheConstant.USER_BLOCK_LIST_CACHE)
    @CacheInvalidate(name = UserCacheConstant.USER_BLOCK_RELATION_CACHE)
    public Result<Void> unblockUser(Long userId, Long blockedUserId) {
        try {
            log.info("取消拉黑用户请求: userId={}, blockedUserId={}", userId, blockedUserId);
            
            userBlockService.unblockUser(userId, blockedUserId);
            log.info("取消拉黑成功: userId={}, blockedUserId={}", userId, blockedUserId);
            return Result.success(null);
        } catch (Exception e) {
            log.error("取消拉黑用户失败", e);
            return Result.error("UNBLOCK_USER_ERROR", "取消拉黑用户失败: " + e.getMessage());
        }
    }

    @Override
    @Cached(name = UserCacheConstant.USER_BLOCK_RELATION_CACHE,
            key = UserCacheConstant.USER_BLOCK_RELATION_KEY,
            expire = UserCacheConstant.USER_BLOCK_RELATION_EXPIRE,
            timeUnit = TimeUnit.MINUTES,
            cacheType = CacheType.BOTH)
    public Result<Boolean> checkBlockStatus(Long userId, Long blockedUserId) {
        try {
            log.debug("检查拉黑状态: userId={}, blockedUserId={}", userId, blockedUserId);
            
            boolean isBlocked = userBlockService.isBlocked(userId, blockedUserId);
            return Result.success(isBlocked);
        } catch (Exception e) {
            log.error("检查拉黑状态失败", e);
            return Result.error("CHECK_BLOCK_STATUS_ERROR", "检查拉黑状态失败: " + e.getMessage());
        }
    }

    @Override
    @Cached(name = UserCacheConstant.USER_BLOCK_DETAIL_CACHE,
            key = UserCacheConstant.USER_BLOCK_DETAIL_KEY,
            expire = UserCacheConstant.USER_BLOCK_DETAIL_EXPIRE,
            timeUnit = TimeUnit.MINUTES,
            cacheType = CacheType.BOTH)
    public Result<UserBlockResponse> getBlockRelation(Long userId, Long blockedUserId) {
        try {
            log.debug("获取拉黑关系: userId={}, blockedUserId={}", userId, blockedUserId);
            
            UserBlock userBlock = userBlockService.getBlockRelation(userId, blockedUserId);
            if (userBlock == null) {
                return Result.error("BLOCK_RELATION_NOT_FOUND", "拉黑关系不存在");
            }
            
            UserBlockResponse response = convertToResponse(userBlock);
            return Result.success(response);
        } catch (Exception e) {
            log.error("查询拉黑关系失败", e);
            return Result.error("GET_BLOCK_RELATION_ERROR", "查询拉黑关系失败: " + e.getMessage());
        }
    }

    @Override
    @Cached(name = UserCacheConstant.USER_BLOCK_LIST_CACHE,
            key = UserCacheConstant.USER_BLOCK_LIST_KEY,
            expire = UserCacheConstant.USER_BLOCK_LIST_EXPIRE,
            timeUnit = TimeUnit.MINUTES,
            cacheType = CacheType.BOTH)
    public Result<PageResponse<UserBlockResponse>> getUserBlockList(Long userId, Integer currentPage, Integer pageSize) {
        try {
            log.debug("获取用户拉黑列表: userId={}, currentPage={}, pageSize={}", userId, currentPage, pageSize);
            
            PageResponse<UserBlock> pageResult = userBlockService.getUserBlockList(userId, currentPage, pageSize);
            
            List<UserBlockResponse> responses = pageResult.getDatas().stream()
                    .map(this::convertToResponse)
                    .toList();
            
            PageResponse<UserBlockResponse> result = new PageResponse<>();
            result.setDatas(responses);
            result.setCurrentPage(pageResult.getCurrentPage());
            result.setPageSize(pageResult.getPageSize());
            result.setTotal(pageResult.getTotal());
            
            return Result.success(result);
        } catch (Exception e) {
            log.error("查询用户拉黑列表失败", e);
            return Result.error("GET_USER_BLOCK_LIST_ERROR", "查询用户拉黑列表失败: " + e.getMessage());
        }
    }

    @Override
    @Cached(name = UserCacheConstant.USER_BLOCK_LIST_CACHE,
            key = UserCacheConstant.USER_BLOCKED_LIST_KEY,
            expire = UserCacheConstant.USER_BLOCK_LIST_EXPIRE,
            timeUnit = TimeUnit.MINUTES,
            cacheType = CacheType.BOTH)
    public Result<PageResponse<UserBlockResponse>> getUserBlockedList(Long blockedUserId, Integer currentPage, Integer pageSize) {
        try {
            log.debug("获取用户被拉黑列表: blockedUserId={}, currentPage={}, pageSize={}", blockedUserId, currentPage, pageSize);
            
            PageResponse<UserBlock> pageResult = userBlockService.getUserBlockedList(blockedUserId, currentPage, pageSize);
            
            List<UserBlockResponse> responses = pageResult.getDatas().stream()
                    .map(this::convertToResponse)
                    .toList();
            
            PageResponse<UserBlockResponse> result = new PageResponse<>();
            result.setDatas(responses);
            result.setCurrentPage(pageResult.getCurrentPage());
            result.setPageSize(pageResult.getPageSize());
            result.setTotal(pageResult.getTotal());
            
            return Result.success(result);
        } catch (Exception e) {
            log.error("查询用户被拉黑列表失败", e);
            return Result.error("GET_USER_BLOCKED_LIST_ERROR", "查询用户被拉黑列表失败: " + e.getMessage());
        }
    }

    @Override
    @Cached(name = UserCacheConstant.USER_BLOCK_LIST_CACHE,
            key = UserCacheConstant.USER_BLOCK_QUERY_KEY,
            expire = UserCacheConstant.USER_BLOCK_LIST_EXPIRE,
            timeUnit = TimeUnit.MINUTES,
            cacheType = CacheType.BOTH)
    public Result<PageResponse<UserBlockResponse>> queryBlocks(UserBlockQueryRequest request) {
        try {
            log.debug("分页查询拉黑记录: currentPage={}, pageSize={}", request.getCurrentPage(), request.getPageSize());
            
            PageResponse<UserBlock> pageResult = userBlockService.queryBlocks(request);
            
            List<UserBlockResponse> responses = pageResult.getDatas().stream()
                    .map(this::convertToResponse)
                    .toList();
            
            PageResponse<UserBlockResponse> result = new PageResponse<>();
            result.setDatas(responses);
            result.setCurrentPage(pageResult.getCurrentPage());
            result.setPageSize(pageResult.getPageSize());
            result.setTotal(pageResult.getTotal());
            
            return Result.success(result);
        } catch (Exception e) {
            log.error("查询拉黑记录列表失败", e);
            return Result.error("BLOCK_LIST_QUERY_ERROR", "查询拉黑记录列表失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Long> countUserBlocks(Long userId) {
        try {
            Long count = userBlockService.countUserBlocks(userId);
            return Result.success(count);
        } catch (Exception e) {
            log.error("统计用户拉黑数量失败", e);
            return Result.error("COUNT_USER_BLOCKS_ERROR", "统计用户拉黑数量失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Long> countUserBlocked(Long blockedUserId) {
        try {
            Long count = userBlockService.countUserBlocked(blockedUserId);
            return Result.success(count);
        } catch (Exception e) {
            log.error("统计用户被拉黑数量失败", e);
            return Result.error("COUNT_USER_BLOCKED_ERROR", "统计用户被拉黑数量失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Map<Long, Boolean>> batchCheckBlockStatus(Long userId, List<Long> blockedUserIds) {
        try {
            Map<Long, Boolean> resultMap = new HashMap<>();
            for (Long blockedUserId : blockedUserIds) {
                boolean isBlocked = userBlockService.isBlocked(userId, blockedUserId);
                resultMap.put(blockedUserId, isBlocked);
            }
            return Result.success(resultMap);
        } catch (Exception e) {
            log.error("批量检查拉黑状态失败", e);
            return Result.error("BATCH_CHECK_BLOCK_STATUS_ERROR", "批量检查拉黑状态失败: " + e.getMessage());
        }
    }

    @Override
    @CacheInvalidate(name = UserCacheConstant.USER_BLOCK_LIST_CACHE)
    @CacheInvalidate(name = UserCacheConstant.USER_BLOCK_RELATION_CACHE)
    public Result<Integer> batchBlockUsers(Long userId, List<Long> blockedUserIds, String reason) {
        try {
            int successCount = 0;
            for (Long blockedUserId : blockedUserIds) {
                try {
                    userBlockService.blockUser(userId, blockedUserId, null, null, reason);
                    successCount++;
                } catch (Exception e) {
                    log.warn("拉黑用户失败: userId={}, blockedUserId={}", userId, blockedUserId);
                }
            }
            return Result.success(successCount);
        } catch (Exception e) {
            log.error("批量拉黑用户失败", e);
            return Result.error("BATCH_BLOCK_USERS_ERROR", "批量拉黑用户失败: " + e.getMessage());
        }
    }

    @Override
    @CacheInvalidate(name = UserCacheConstant.USER_BLOCK_LIST_CACHE)
    @CacheInvalidate(name = UserCacheConstant.USER_BLOCK_RELATION_CACHE)
    public Result<Integer> batchUnblockUsers(Long userId, List<Long> blockedUserIds) {
        try {
            int successCount = 0;
            for (Long blockedUserId : blockedUserIds) {
                try {
                    userBlockService.unblockUser(userId, blockedUserId);
                    successCount++;
                } catch (Exception e) {
                    log.warn("取消拉黑失败: userId={}, blockedUserId={}", userId, blockedUserId);
                }
            }
            return Result.success(successCount);
        } catch (Exception e) {
            log.error("批量取消拉黑失败", e);
            return Result.error("BATCH_UNBLOCK_ERROR", "批量取消拉黑失败: " + e.getMessage());
        }
    }

    @Override
    @Cached(name = UserCacheConstant.USER_BLOCK_LIST_CACHE,
            key = UserCacheConstant.USER_BLOCK_LIST_KEY,
            expire = UserCacheConstant.USER_BLOCK_LIST_EXPIRE,
            timeUnit = TimeUnit.MINUTES,
            cacheType = CacheType.BOTH)
    public Result<PageResponse<UserBlockResponse>> getMutualBlockList(Long userId, Integer currentPage, Integer pageSize) {
        try {
            log.debug("获取互相拉黑列表: userId={}, currentPage={}, pageSize={}", userId, currentPage, pageSize);
            
            // 简化实现：获取用户拉黑列表，然后检查双向拉黑
            PageResponse<UserBlock> userBlocks = userBlockService.getUserBlockList(userId, currentPage, pageSize);
            List<UserBlockResponse> mutualBlocks = new java.util.ArrayList<>();
            
            for (UserBlock block : userBlocks.getDatas()) {
                // 检查是否互相拉黑
                boolean isMutual = userBlockService.isBlocked(block.getBlockedUserId(), userId);
                if (isMutual) {
                    mutualBlocks.add(convertToResponse(block));
                }
            }
            
            PageResponse<UserBlockResponse> result = new PageResponse<>();
            result.setDatas(mutualBlocks);
            result.setCurrentPage(currentPage);
            result.setPageSize(pageSize);
            result.setTotal((long) mutualBlocks.size());
            
            return Result.success(result);
        } catch (Exception e) {
            log.error("查询互相拉黑列表失败", e);
            return Result.error("GET_MUTUAL_BLOCK_LIST_ERROR", "查询互相拉黑列表失败: " + e.getMessage());
        }
    }

    @Override
    @Cached(name = UserCacheConstant.USER_BLOCK_RELATION_CACHE,
            key = UserCacheConstant.USER_BLOCK_RELATION_KEY,
            expire = UserCacheConstant.USER_BLOCK_RELATION_EXPIRE,
            timeUnit = TimeUnit.MINUTES,
            cacheType = CacheType.BOTH)
    public Result<Boolean> checkMutualBlock(Long userId1, Long userId2) {
        try {
            boolean block1 = userBlockService.isBlocked(userId1, userId2);
            boolean block2 = userBlockService.isBlocked(userId2, userId1);
            boolean isMutual = block1 && block2;
            return Result.success(isMutual);
        } catch (Exception e) {
            log.error("检查互相拉黑状态失败", e);
            return Result.error("CHECK_MUTUAL_BLOCK_ERROR", "检查互相拉黑状态失败: " + e.getMessage());
        }
    }

    @Override
    public Result<List<UserBlockResponse>> getRecentBlocks(Long userId, Integer limit) {
        try {
            // 简化实现：获取用户拉黑列表的前N条
            PageResponse<UserBlock> pageResult = userBlockService.getUserBlockList(userId, 1, limit);
            List<UserBlockResponse> responses = pageResult.getDatas().stream()
                    .map(this::convertToResponse)
                    .toList();
            
            return Result.success(responses);
        } catch (Exception e) {
            log.error("获取最近拉黑记录失败", e);
            return Result.error("GET_RECENT_BLOCKS_ERROR", "获取最近拉黑记录失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Map<String, Long>> getBlockReasonStatistics() {
        try {
            // 简化实现：返回空统计
            Map<String, Long> stats = new HashMap<>();
            stats.put("spam", 0L);
            stats.put("harassment", 0L);
            stats.put("inappropriate", 0L);
            stats.put("other", 0L);
            return Result.success(stats);
        } catch (Exception e) {
            log.error("获取拉黑原因统计失败", e);
            return Result.error("GET_BLOCK_REASON_STATS_ERROR", "获取拉黑原因统计失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Map<String, Object>> getPlatformBlockStats() {
        try {
            // 简化实现：返回空统计
            Map<String, Object> stats = new HashMap<>();
            stats.put("totalBlocks", 0L);
            stats.put("activeBlocks", 0L);
            stats.put("mutualBlocks", 0L);
            return Result.success(stats);
        } catch (Exception e) {
            log.error("获取平台拉黑统计失败", e);
            return Result.error("GET_PLATFORM_BLOCK_STATS_ERROR", "获取平台拉黑统计失败: " + e.getMessage());
        }
    }

    @Override
    @CacheInvalidate(name = UserCacheConstant.USER_BLOCK_DETAIL_CACHE)
    @CacheInvalidate(name = UserCacheConstant.USER_BLOCK_LIST_CACHE)
    @CacheInvalidate(name = UserCacheConstant.USER_BLOCK_RELATION_CACHE)
    public Result<Void> deleteBlock(Long id) {
        try {
            log.info("删除拉黑关系请求: id={}", id);
            
            userBlockService.deleteBlock(id);
            log.info("拉黑关系删除成功: id={}", id);
            return Result.success(null);
        } catch (Exception e) {
            log.error("删除拉黑关系失败", e);
            return Result.error("BLOCK_DELETE_ERROR", "删除拉黑关系失败: " + e.getMessage());
        }
    }

    @Override
    @CacheInvalidate(name = UserCacheConstant.USER_BLOCK_LIST_CACHE)
    public Result<Integer> cleanCancelledBlocks(Integer days) {
        try {
            // 简化实现：返回清理数量0
            log.info("清理已取消的拉黑记录: days={}", days);
            return Result.success(0);
        } catch (Exception e) {
            log.error("清理已取消的拉黑记录失败", e);
            return Result.error("CLEAN_CANCELLED_BLOCKS_ERROR", "清理失败: " + e.getMessage());
        }
    }

    /**
     * 转换为用户拉黑响应对象
     */
    private UserBlockResponse convertToResponse(UserBlock block) {
        UserBlockResponse response = new UserBlockResponse();
        BeanUtils.copyProperties(block, response);
        return response;
    }
}