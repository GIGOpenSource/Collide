package com.gig.collide.tag.facade;

import com.gig.collide.api.tag.UserInterestTagFacadeService;
import com.gig.collide.api.tag.request.UserInterestTagRequest;
import com.gig.collide.api.tag.response.UserInterestTagResponse;
import com.gig.collide.tag.domain.entity.Tag;
import com.gig.collide.tag.domain.entity.UserInterestTag;
import com.gig.collide.tag.domain.service.TagService;
import com.gig.collide.tag.domain.service.UserInterestTagService;
import com.gig.collide.web.vo.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 用户兴趣标签门面服务实现类
 * 专注于用户与标签的兴趣关系管理
 *
 * @author GIG Team
 * @version 3.0.0
 */
@Slf4j
@Service
@DubboService
@RequiredArgsConstructor
public class UserInterestTagFacadeServiceImpl implements UserInterestTagFacadeService {

    private final UserInterestTagService userInterestTagService;
    private final TagService tagService;

    @Override
    public Result<UserInterestTagResponse> addUserInterest(UserInterestTagRequest request) {
        try {
            log.info("添加用户兴趣标签: 用户ID={}, 标签ID={}, 分数={}", 
                    request.getUserId(), request.getTagId(), request.getInterestScore());
            
            if (request.getUserId() == null) {
                return Result.error("INVALID_PARAM", "用户ID不能为空");
            }
            if (request.getTagId() == null) {
                return Result.error("INVALID_PARAM", "标签ID不能为空");
            }
            
            Tag tag = tagService.getTagById(request.getTagId());
            if (tag == null) {
                return Result.error("TAG_NOT_FOUND", "标签不存在");
            }
            
            UserInterestTag userInterestTag = userInterestTagService.addUserInterestSafely(
                    request.getUserId(), request.getTagId(),
                    request.getInterestScore() != null ? request.getInterestScore() :  BigDecimal.ZERO);
            
            UserInterestTagResponse response = convertToUserInterestTagResponse(userInterestTag, tag);
            
            tagService.increaseUsageCount(request.getTagId());
            
            log.info("用户兴趣标签添加成功: ID={}", userInterestTag.getId());
            return Result.success(response);
            
        } catch (Exception e) {
            log.error("添加用户兴趣标签失败", e);
            return Result.error("USER_INTEREST_ADD_ERROR", "添加用户兴趣标签失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Void> removeUserInterest(Long userId, Long tagId, Long operatorId) {
        try {
            log.info("移除用户兴趣标签: 用户ID={}, 标签ID={}, 操作人={}", userId, tagId, operatorId);
            
            if (userId == null || tagId == null) {
                return Result.error("INVALID_PARAM", "用户ID和标签ID不能为空");
            }
            
            boolean success = userInterestTagService.removeUserInterest(userId, tagId);
            if (success) {
                tagService.decreaseUsageCount(tagId);
                log.info("用户兴趣标签移除成功");
                return Result.success();
            } else {
                return Result.error("USER_INTEREST_REMOVE_ERROR", "移除用户兴趣标签失败");
            }
            
        } catch (Exception e) {
            log.error("移除用户兴趣标签失败", e);
            return Result.error("USER_INTEREST_REMOVE_ERROR", "移除用户兴趣标签失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Void> updateUserInterestScore(Long userId, Long tagId, BigDecimal interestScore, Long operatorId) {
        try {
            log.info("更新用户兴趣分数: 用户ID={}, 标签ID={}, 分数={}", userId, tagId, interestScore);
            
            if (userId == null || tagId == null || interestScore == null) {
                return Result.error("INVALID_PARAM", "参数不能为空");
            }
            
            boolean success = userInterestTagService.updateInterestScore(userId, tagId, interestScore);
            if (success) {
                log.info("用户兴趣分数更新成功");
                return Result.success();
            } else {
                return Result.error("USER_INTEREST_UPDATE_ERROR", "更新用户兴趣分数失败");
            }
            
        } catch (Exception e) {
            log.error("更新用户兴趣分数失败", e);
            return Result.error("USER_INTEREST_UPDATE_ERROR", "更新用户兴趣分数失败: " + e.getMessage());
        }
    }

    @Override
    public Result<List<UserInterestTagResponse>> getUserInterests(Long userId) {
        try {
            log.debug("获取用户兴趣标签列表: 用户ID={}", userId);
            
            if (userId == null) {
                return Result.error("INVALID_PARAM", "用户ID不能为空");
            }
            
            List<UserInterestTag> userInterests = userInterestTagService.selectByUserId(userId);
            List<UserInterestTagResponse> responses = new ArrayList<>();
            
            for (UserInterestTag userInterest : userInterests) {
                Tag tag = tagService.getTagById(userInterest.getTagId());
                if (tag != null) {
                    responses.add(convertToUserInterestTagResponse(userInterest, tag));
                }
            }
            
            return Result.success(responses);
            
        } catch (Exception e) {
            log.error("获取用户兴趣标签列表失败", e);
            return Result.error("USER_INTEREST_QUERY_ERROR", "获取用户兴趣标签列表失败: " + e.getMessage());
        }
    }

    @Override
    public Result<List<UserInterestTagResponse>> getUserTopInterests(Long userId, Integer limit) {
        try {
            log.debug("获取用户高分兴趣标签: 用户ID={}, 限制数量={}", userId, limit);
            
            if (userId == null) {
                return Result.error("INVALID_PARAM", "用户ID不能为空");
            }
            
            List<UserInterestTag> userInterests = userInterestTagService.selectTopInterestsByUserId(userId, limit);
            List<UserInterestTagResponse> responses = new ArrayList<>();
            
            for (UserInterestTag userInterest : userInterests) {
                Tag tag = tagService.getTagById(userInterest.getTagId());
                if (tag != null) {
                    responses.add(convertToUserInterestTagResponse(userInterest, tag));
                }
            }
            
            return Result.success(responses);
            
        } catch (Exception e) {
            log.error("获取用户高分兴趣标签失败", e);
            return Result.error("USER_INTEREST_QUERY_ERROR", "获取用户高分兴趣标签失败: " + e.getMessage());
        }
    }

    @Override
    public Result<List<UserInterestTagResponse>> getTagFollowers(Long tagId) {
        try {
            log.debug("获取标签的关注用户: 标签ID={}", tagId);
            
            if (tagId == null) {
                return Result.error("INVALID_PARAM", "标签ID不能为空");
            }
            
            List<UserInterestTag> followers = userInterestTagService.selectByTagId(tagId);
            Tag tag = tagService.getTagById(tagId);
            
            List<UserInterestTagResponse> responses = followers.stream()
                    .map(follower -> convertToUserInterestTagResponse(follower, tag))
                    .collect(Collectors.toList());
            
            return Result.success(responses);
            
        } catch (Exception e) {
            log.error("获取标签的关注用户失败", e);
            return Result.error("TAG_FOLLOWERS_ERROR", "获取标签的关注用户失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Integer> batchSetUserInterests(Long userId, List<Long> tagIds, BigDecimal defaultScore, Long operatorId) {
        try {
            log.info("批量设置用户兴趣标签: 用户ID={}, 标签数量={}, 默认分数={}", userId, tagIds.size(), defaultScore);
            
            if (userId == null || tagIds == null || tagIds.isEmpty()) {
                return Result.error("INVALID_PARAM", "参数不能为空");
            }
            
            int result = userInterestTagService.batchSetUserInterests(userId, tagIds, 
                    defaultScore != null ? defaultScore : BigDecimal.valueOf(50));
            
            for (Long tagId : tagIds) {
                try {
                    tagService.increaseUsageCount(tagId);
                } catch (Exception e) {
                    log.warn("增加标签使用次数失败: tagId={}", tagId, e);
                }
            }
            
            log.info("批量设置用户兴趣标签完成: 成功数量={}", result);
            return Result.success(result);
            
        } catch (Exception e) {
            log.error("批量设置用户兴趣标签失败", e);
            return Result.error("USER_INTEREST_BATCH_ERROR", "批量设置用户兴趣标签失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Integer> batchUpdateUserTagStatus(Long userId, List<Long> tagIds, String status, Long operatorId) {
        try {
            log.info("批量更新用户标签状态: 用户ID={}, 标签数量={}, 状态={}", userId, tagIds.size(), status);
            
            if (userId == null || tagIds == null || tagIds.isEmpty()) {
                return Result.error("INVALID_PARAM", "参数不能为空");
            }
            
            int result = userInterestTagService.batchUpdateStatus(userId, tagIds, status);
            log.info("批量更新用户标签状态完成: 更新数量={}", result);
            return Result.success(result);
            
        } catch (Exception e) {
            log.error("批量更新用户标签状态失败", e);
            return Result.error("USER_INTEREST_BATCH_UPDATE_ERROR", "批量更新失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Integer> batchActivateUserInterests(Long userId, List<Long> tagIds, Long operatorId) {
        try {
            log.info("批量激活用户兴趣标签: 用户ID={}, 标签数量={}", userId, tagIds.size());
            
            if (userId == null || tagIds == null || tagIds.isEmpty()) {
                return Result.error("INVALID_PARAM", "参数不能为空");
            }
            
            int result = userInterestTagService.batchUpdateStatus(userId, tagIds, "active");
            log.info("批量激活用户兴趣标签完成: 激活数量={}", result);
            return Result.success(result);
            
        } catch (Exception e) {
            log.error("批量激活用户兴趣标签失败", e);
            return Result.error("USER_INTEREST_BATCH_ACTIVATE_ERROR", "批量激活失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Integer> batchDeactivateUserInterests(Long userId, List<Long> tagIds, Long operatorId) {
        try {
            log.info("批量停用用户兴趣标签: 用户ID={}, 标签数量={}", userId, tagIds.size());
            
            if (userId == null || tagIds == null || tagIds.isEmpty()) {
                return Result.error("INVALID_PARAM", "参数不能为空");
            }
            
            int result = userInterestTagService.batchUpdateStatus(userId, tagIds, "inactive");
            log.info("批量停用用户兴趣标签完成: 停用数量={}", result);
            return Result.success(result);
            
        } catch (Exception e) {
            log.error("批量停用用户兴趣标签失败", e);
            return Result.error("USER_INTEREST_BATCH_DEACTIVATE_ERROR", "批量停用失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Void> toggleUserInterest(Long userId, Long tagId, boolean active, Long operatorId) {
        try {
            log.info("切换用户兴趣标签状态: 用户ID={}, 标签ID={}, 激活={}", userId, tagId, active);
            
            if (userId == null || tagId == null) {
                return Result.error("INVALID_PARAM", "用户ID和标签ID不能为空");
            }
            
            boolean success = active ? 
                    userInterestTagService.activateUserInterest(userId, tagId) :
                    userInterestTagService.deactivateUserInterest(userId, tagId);
            
            if (success) {
                log.info("用户兴趣标签状态切换成功");
                return Result.success();
            } else {
                return Result.error("USER_INTEREST_TOGGLE_ERROR", "切换用户兴趣标签状态失败");
            }
            
        } catch (Exception e) {
            log.error("切换用户兴趣标签状态失败", e);
            return Result.error("USER_INTEREST_TOGGLE_ERROR", "切换用户兴趣标签状态失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Void> increaseInterestScore(Long userId, Long tagId, BigDecimal increment, Long operatorId) {
        try {
            log.info("增加用户兴趣分数: 用户ID={}, 标签ID={}, 增量={}", userId, tagId, increment);
            
            if (userId == null || tagId == null || increment == null) {
                return Result.error("INVALID_PARAM", "参数不能为空");
            }
            
            boolean success = userInterestTagService.increaseInterestScore(userId, tagId, increment);
            if (success) {
                log.info("用户兴趣分数增加成功");
                return Result.success();
            } else {
                return Result.error("USER_INTEREST_INCREASE_ERROR", "增加用户兴趣分数失败");
            }
            
        } catch (Exception e) {
            log.error("增加用户兴趣分数失败", e);
            return Result.error("USER_INTEREST_INCREASE_ERROR", "增加用户兴趣分数失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Void> decreaseInterestScore(Long userId, Long tagId, BigDecimal decrement, Long operatorId) {
        try {
            log.info("减少用户兴趣分数: 用户ID={}, 标签ID={}, 减量={}", userId, tagId, decrement);
            
            if (userId == null || tagId == null || decrement == null) {
                return Result.error("INVALID_PARAM", "参数不能为空");
            }
            
            boolean success = userInterestTagService.decreaseInterestScore(userId, tagId, decrement);
            if (success) {
                log.info("用户兴趣分数减少成功");
                return Result.success();
            } else {
                return Result.error("USER_INTEREST_DECREASE_ERROR", "减少用户兴趣分数失败");
            }
            
        } catch (Exception e) {
            log.error("减少用户兴趣分数失败", e);
            return Result.error("USER_INTEREST_DECREASE_ERROR", "减少用户兴趣分数失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Void> resetInterestScore(Long userId, Long tagId, BigDecimal newScore, Long operatorId) {
        try {
            log.info("重置用户兴趣分数: 用户ID={}, 标签ID={}, 新分数={}", userId, tagId, newScore);
            
            if (userId == null || tagId == null || newScore == null) {
                return Result.error("INVALID_PARAM", "参数不能为空");
            }
            
            boolean success = userInterestTagService.resetInterestScore(userId, tagId, newScore);
            if (success) {
                log.info("用户兴趣分数重置成功");
                return Result.success();
            } else {
                return Result.error("USER_INTEREST_RESET_ERROR", "重置用户兴趣分数失败");
            }
            
        } catch (Exception e) {
            log.error("重置用户兴趣分数失败", e);
            return Result.error("USER_INTEREST_RESET_ERROR", "重置用户兴趣分数失败: " + e.getMessage());
        }
    }

    @Override
    public Result<List<UserInterestTagResponse>> getActiveUserInterests(Long userId) {
        try {
            log.debug("获取用户活跃兴趣标签: 用户ID={}", userId);
            
            if (userId == null) {
                return Result.error("INVALID_PARAM", "用户ID不能为空");
            }
            
            List<UserInterestTag> activeInterests = userInterestTagService.getActiveUserInterests(userId);
            List<UserInterestTagResponse> responses = new ArrayList<>();
            
            for (UserInterestTag userInterest : activeInterests) {
                Tag tag = tagService.getTagById(userInterest.getTagId());
                if (tag != null) {
                    responses.add(convertToUserInterestTagResponse(userInterest, tag));
                }
            }
            
            return Result.success(responses);
            
        } catch (Exception e) {
            log.error("获取用户活跃兴趣标签失败", e);
            return Result.error("USER_INTEREST_QUERY_ERROR", "获取用户活跃兴趣标签失败: " + e.getMessage());
        }
    }

    @Override
    public Result<List<UserInterestTagResponse>> getHighInterestTags(Long userId, BigDecimal minScore) {
        try {
            log.debug("获取用户高兴趣标签: 用户ID={}, 最小分数={}", userId, minScore);
            
            if (userId == null) {
                return Result.error("INVALID_PARAM", "用户ID不能为空");
            }
            
            List<UserInterestTag> highInterestTags = userInterestTagService.getHighInterestTags(userId, minScore);
            List<UserInterestTagResponse> responses = new ArrayList<>();
            
            for (UserInterestTag userInterest : highInterestTags) {
                Tag tag = tagService.getTagById(userInterest.getTagId());
                if (tag != null) {
                    responses.add(convertToUserInterestTagResponse(userInterest, tag));
                }
            }
            
            return Result.success(responses);
            
        } catch (Exception e) {
            log.error("获取用户高兴趣标签失败", e);
            return Result.error("USER_INTEREST_QUERY_ERROR", "获取用户高兴趣标签失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Boolean> isUserInterestedInTag(Long userId, Long tagId) {
        try {
            log.debug("检查用户是否已关注标签: 用户ID={}, 标签ID={}", userId, tagId);
            
            if (userId == null || tagId == null) {
                return Result.error("INVALID_PARAM", "用户ID和标签ID不能为空");
            }
            
            boolean isInterested = userInterestTagService.existsByUserIdAndTagId(userId, tagId);
            return Result.success(isInterested);
            
        } catch (Exception e) {
            log.error("检查用户是否已关注标签失败", e);
            return Result.error("USER_INTEREST_CHECK_ERROR", "检查失败: " + e.getMessage());
        }
    }

    @Override
    public Result<List<Map<String, Object>>> getUserInterestStats(Long userId, BigDecimal minScore) {
        try {
            log.debug("获取用户兴趣统计: 用户ID={}, 最小分数={}", userId, minScore);
            
            if (userId == null) {
                return Result.error("INVALID_PARAM", "用户ID不能为空");
            }
            
            List<Map<String, Object>> stats = userInterestTagService.getUserInterestStats(userId, minScore);
            return Result.success(stats);
            
        } catch (Exception e) {
            log.error("获取用户兴趣统计失败", e);
            return Result.error("USER_INTEREST_STATS_ERROR", "获取用户兴趣统计失败: " + e.getMessage());
        }
    }

    @Override
    public Result<List<Map<String, Object>>> getTagHotUsers(Long tagId, Integer limit) {
        try {
            log.debug("获取标签的热门关注用户: 标签ID={}, 限制数量={}", tagId, limit);
            
            if (tagId == null) {
                return Result.error("INVALID_PARAM", "标签ID不能为空");
            }
            
            List<Map<String, Object>> hotUsers = userInterestTagService.getTagHotUsers(tagId, limit);
            return Result.success(hotUsers);
            
        } catch (Exception e) {
            log.error("获取标签的热门关注用户失败", e);
            return Result.error("TAG_HOT_USERS_ERROR", "获取标签的热门关注用户失败: " + e.getMessage());
        }
    }

    // 以下方法为简化实现
    @Override
    public Result<Map<String, Object>> getUserInterestAnalysis(Long userId) {
        return Result.error("NOT_IMPLEMENTED", "功能暂未实现");
    }

    @Override
    public Result<List<Map<String, Object>>> getUserTagsWithInterest(Long userId) {
        return Result.error("NOT_IMPLEMENTED", "功能暂未实现");
    }

    @Override
    public Result<List<Map<String, Object>>> recommendTagsForUser(Long userId, Integer limit) {
        return Result.error("NOT_IMPLEMENTED", "功能暂未实现");
    }

    @Override
    public Result<List<Long>> recommendSimilarUsers(Long userId, Integer limit) {
        return Result.error("NOT_IMPLEMENTED", "功能暂未实现");
    }

    @Override
    public Result<Map<String, Object>> getUserInterestCorrelation(Long userId) {
        return Result.error("NOT_IMPLEMENTED", "功能暂未实现");
    }

    @Override
    public Result<Integer> cleanupInvalidUserInterests(Long operatorId) {
        try {
            // 简化实现，实际应该清理不存在的用户或标签的关联数据
            log.info("清理无效的用户兴趣标签: 操作人={}", operatorId);
            return Result.success(0);
        } catch (Exception e) {
            return Result.error("CLEANUP_ERROR", "清理失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Integer> recalculateUserInterestScores(Long userId, Long operatorId) {
        return Result.error("NOT_IMPLEMENTED", "功能暂未实现");
    }

    @Override
    public Result<String> healthCheck() {
        try {
            List<UserInterestTag> allUserInterests = userInterestTagService.getAllUserInterestTags();
            String healthStatus = String.format("用户兴趣标签系统运行正常。关联数量: %d", allUserInterests.size());
            return Result.success(healthStatus);
        } catch (Exception e) {
            return Result.error("HEALTH_CHECK_ERROR", "健康检查失败: " + e.getMessage());
        }
    }

    // 私有工具方法
    private UserInterestTagResponse convertToUserInterestTagResponse(UserInterestTag userInterestTag, Tag tag) {
        UserInterestTagResponse response = new UserInterestTagResponse();
        BeanUtils.copyProperties(userInterestTag, response);
        if (tag != null) {
            response.setTagName(tag.getName());
            response.setTagType(tag.getTagType());
        }
        return response;
    }
}