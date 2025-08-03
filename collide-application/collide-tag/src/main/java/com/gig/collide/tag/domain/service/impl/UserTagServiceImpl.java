package com.gig.collide.tag.domain.service.impl;

import com.gig.collide.base.response.PageResponse;
import com.gig.collide.tag.domain.entity.UserTagFollow;
import com.gig.collide.tag.domain.entity.Tag;
import com.gig.collide.tag.domain.service.UserTagService;
import com.gig.collide.tag.domain.service.TagService;
import com.gig.collide.tag.infrastructure.mapper.UserTagFollowMapper;
import com.gig.collide.api.tag.request.UserTagQueryRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 用户标签领域服务实现
 * 
 * @author GIG Team
 * @version 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserTagServiceImpl implements UserTagService {

    private final UserTagFollowMapper userTagFollowMapper;
    private final TagService tagService;

    // 用户最多可关注的标签数量
    private static final int MAX_USER_FOLLOW_TAGS = 9;

    // =================== 用户关注标签管理 ===================

    @Override
    @Transactional
    public boolean followTag(Long userId, Long tagId) {
        try {
            log.info("用户关注标签: userId={}, tagId={}", userId, tagId);
            
            // 参数验证
            if (userId == null || userId <= 0) {
                throw new RuntimeException("用户ID无效");
            }
            
            if (tagId == null || tagId <= 0) {
                throw new RuntimeException("标签ID无效");
            }
            
            // 验证标签存在且启用
            Tag tag = tagService.getActiveTag(tagId);
            if (tag == null) {
                throw new RuntimeException("标签不存在或已禁用");
            }
            
            // 检查用户关注标签数量限制
            if (!canUserFollowMoreTags(userId)) {
                throw new RuntimeException("关注标签数量已达上限（" + MAX_USER_FOLLOW_TAGS + "个）");
            }
            
            // 检查是否已关注
            if (isFollowing(userId, tagId)) {
                log.warn("用户已关注该标签: userId={}, tagId={}", userId, tagId);
                return true; // 已关注视为成功
            }
            
            // 创建关注关系
            UserTagFollow follow = UserTagFollow.create(userId, tagId);
            int result = userTagFollowMapper.insert(follow);
            
            if (result > 0) {
                // 更新标签关注数
                tagService.incrementFollowCount(tagId, 1);
                
                log.info("用户关注标签成功: userId={}, tagId={}", userId, tagId);
                return true;
            } else {
                throw new RuntimeException("创建关注关系失败");
            }
        } catch (Exception e) {
            log.error("用户关注标签失败: userId={}, tagId={}", userId, tagId, e);
            throw new RuntimeException("关注标签失败: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public boolean unfollowTag(Long userId, Long tagId) {
        try {
            log.info("用户取消关注标签: userId={}, tagId={}", userId, tagId);
            
            // 参数验证
            if (userId == null || userId <= 0 || tagId == null || tagId <= 0) {
                throw new RuntimeException("参数无效");
            }
            
            // 检查是否已关注
            if (!isFollowing(userId, tagId)) {
                log.warn("用户未关注该标签: userId={}, tagId={}", userId, tagId);
                return true; // 未关注视为成功
            }
            
            // 删除关注关系
            int result = userTagFollowMapper.batchDeleteUserTagFollows(userId, Arrays.asList(tagId));
            
            if (result > 0) {
                // 更新标签关注数
                tagService.decrementFollowCount(tagId, 1);
                
                log.info("用户取消关注标签成功: userId={}, tagId={}", userId, tagId);
                return true;
            } else {
                throw new RuntimeException("删除关注关系失败");
            }
        } catch (Exception e) {
            log.error("用户取消关注标签失败: userId={}, tagId={}", userId, tagId, e);
            throw new RuntimeException("取消关注标签失败: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public List<Long> batchFollowTags(Long userId, List<Long> tagIds) {
        try {
            log.info("用户批量关注标签: userId={}, tagIds={}", userId, tagIds);
            
            if (userId == null || userId <= 0 || tagIds == null || tagIds.isEmpty()) {
                return new ArrayList<>();
            }
            
            // 去重并验证数量
            List<Long> uniqueTagIds = tagIds.stream().distinct().collect(Collectors.toList());
            
            // 检查用户当前关注数量
            Integer currentCount = getUserFollowedTagCount(userId);
            int remainingCount = MAX_USER_FOLLOW_TAGS - currentCount;
            
            if (remainingCount <= 0) {
                throw new RuntimeException("关注标签数量已达上限（" + MAX_USER_FOLLOW_TAGS + "个）");
            }
            
            // 限制批量关注数量
            if (uniqueTagIds.size() > remainingCount) {
                uniqueTagIds = uniqueTagIds.subList(0, remainingCount);
                log.warn("批量关注标签数量超限，已截取: userId={}, requested={}, actual={}", 
                        userId, tagIds.size(), uniqueTagIds.size());
            }
            
            List<Long> successTagIds = new ArrayList<>();
            
            for (Long tagId : uniqueTagIds) {
                try {
                    if (followTag(userId, tagId)) {
                        successTagIds.add(tagId);
                    }
                } catch (Exception e) {
                    log.warn("批量关注标签时跳过失败项: userId={}, tagId={}, error={}", 
                            userId, tagId, e.getMessage());
                }
            }
            
            log.info("用户批量关注标签完成: userId={}, requested={}, success={}", 
                    userId, uniqueTagIds.size(), successTagIds.size());
            
            return successTagIds;
        } catch (Exception e) {
            log.error("用户批量关注标签失败: userId={}, tagIds={}", userId, tagIds, e);
            throw new RuntimeException("批量关注标签失败: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public boolean batchUnfollowTags(Long userId, List<Long> tagIds) {
        try {
            log.info("用户批量取消关注标签: userId={}, tagIds={}", userId, tagIds);
            
            if (userId == null || userId <= 0 || tagIds == null || tagIds.isEmpty()) {
                return true;
            }
            
            // 获取实际已关注的标签
            List<Long> followedTagIds = userTagFollowMapper.batchCheckUserFollowing(userId, tagIds);
            if (followedTagIds.isEmpty()) {
                log.info("用户未关注任何指定标签，跳过操作: userId={}", userId);
                return true;
            }
            
            // 批量删除关注关系
            int result = userTagFollowMapper.batchDeleteUserTagFollows(userId, followedTagIds);
            
            if (result > 0) {
                // 批量更新标签关注数
                for (Long tagId : followedTagIds) {
                    tagService.decrementFollowCount(tagId, 1);
                }
                
                log.info("用户批量取消关注标签成功: userId={}, count={}", userId, result);
                return true;
            } else {
                throw new RuntimeException("批量删除关注关系失败");
            }
        } catch (Exception e) {
            log.error("用户批量取消关注标签失败: userId={}, tagIds={}", userId, tagIds, e);
            throw new RuntimeException("批量取消关注标签失败: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public boolean replaceUserFollowedTags(Long userId, List<Long> tagIds) {
        try {
            log.info("替换用户关注标签: userId={}, newTagIds={}", userId, tagIds);
            
            if (userId == null || userId <= 0) {
                throw new RuntimeException("用户ID无效");
            }
            
            // 验证新标签数量
            if (tagIds != null && tagIds.size() > MAX_USER_FOLLOW_TAGS) {
                throw new RuntimeException("关注标签数量超过上限（" + MAX_USER_FOLLOW_TAGS + "个）");
            }
            
            // 获取用户当前关注的标签
            List<Long> currentTagIds = getUserFollowedTagIds(userId);
            
            // 删除所有当前关注
            if (!currentTagIds.isEmpty()) {
                batchUnfollowTags(userId, currentTagIds);
            }
            
            // 添加新的关注
            if (tagIds != null && !tagIds.isEmpty()) {
                List<Long> successTagIds = batchFollowTags(userId, tagIds);
                log.info("替换用户关注标签完成: userId={}, success={}/{}", 
                        userId, successTagIds.size(), tagIds.size());
            }
            
            return true;
        } catch (Exception e) {
            log.error("替换用户关注标签失败: userId={}, tagIds={}", userId, tagIds, e);
            throw new RuntimeException("替换用户关注标签失败: " + e.getMessage(), e);
        }
    }

    // =================== 用户关注标签查询 ===================

    @Override
    public boolean isFollowing(Long userId, Long tagId) {
        try {
            if (userId == null || userId <= 0 || tagId == null || tagId <= 0) {
                return false;
            }
            return userTagFollowMapper.isUserFollowingTag(userId, tagId);
        } catch (Exception e) {
            log.error("检查用户是否关注标签失败: userId={}, tagId={}", userId, tagId, e);
            return false;
        }
    }

    @Override
    public List<Long> getUserFollowedTagIds(Long userId) {
        try {
            if (userId == null || userId <= 0) {
                return new ArrayList<>();
            }
            return userTagFollowMapper.getUserFollowedTagIds(userId);
        } catch (Exception e) {
            log.error("获取用户关注标签ID列表失败: userId={}", userId, e);
            return new ArrayList<>();
        }
    }

    @Override
    public List<UserTagFollow> getUserFollowedTags(Long userId) {
        try {
            if (userId == null || userId <= 0) {
                return new ArrayList<>();
            }
            return userTagFollowMapper.getUserFollowedTagsWithDetails(userId);
        } catch (Exception e) {
            log.error("获取用户关注标签详情失败: userId={}", userId, e);
            return new ArrayList<>();
        }
    }

    @Override
    public Integer getUserFollowedTagCount(Long userId) {
        try {
            if (userId == null || userId <= 0) {
                return 0;
            }
            Integer count = userTagFollowMapper.countUserFollowedTags(userId);
            return count != null ? count : 0;
        } catch (Exception e) {
            log.error("获取用户关注标签数量失败: userId={}", userId, e);
            return 0;
        }
    }

    @Override
    public PageResponse<UserTagFollow> queryUserFollowedTags(UserTagQueryRequest request) {
        try {
            log.debug("分页查询用户关注标签: userId={}, page={}, size={}", 
                    request.getUserId(), request.getCurrentPage(), request.getPageSize());
            
            // 计算分页参数
            int offset = (request.getCurrentPage() - 1) * request.getPageSize();
            
            // 查询关注记录列表
            List<UserTagFollow> follows = userTagFollowMapper.findUserTagFollowsByCondition(
                    request.getUserId(),
                    request.getTagId(),
                    request.getFollowStartDate(),
                    request.getFollowEndDate(),
                    request.getTagStatus(),
                    request.getSortField(),
                    request.getSortDirection(),
                    offset,
                    request.getPageSize()
            );
            
            // 查询总数
            Long total = userTagFollowMapper.countUserTagFollowsByCondition(
                    request.getUserId(),
                    request.getTagId(),
                    request.getFollowStartDate(),
                    request.getFollowEndDate(),
                    request.getTagStatus()
            );
            
            // 构建分页结果
            PageResponse<UserTagFollow> result = new PageResponse<>();
            result.setDatas(follows);
            result.setCurrentPage(request.getCurrentPage());
            result.setPageSize(request.getPageSize());
            result.setTotal(total);
            
            log.debug("用户关注标签查询结果: count={}, total={}", follows.size(), total);
            return result;
        } catch (Exception e) {
            log.error("分页查询用户关注标签失败: userId={}", request.getUserId(), e);
            throw new RuntimeException("查询用户关注标签失败", e);
        }
    }

    @Override
    public List<UserTagFollow> getUserRecentFollowedTags(Long userId, Integer days, Integer limit) {
        try {
            if (userId == null || userId <= 0) {
                return new ArrayList<>();
            }
            
            if (days == null || days <= 0) {
                days = 30; // 默认30天
            }
            
            if (limit == null || limit <= 0) {
                limit = 10; // 默认10个
            }
            
            return userTagFollowMapper.getUserRecentFollowedTags(userId, days, limit);
        } catch (Exception e) {
            log.error("获取用户最近关注标签失败: userId={}, days={}, limit={}", userId, days, limit, e);
            return new ArrayList<>();
        }
    }

    @Override
    public Map<Long, Boolean> batchCheckFollowing(Long userId, List<Long> tagIds) {
        try {
            Map<Long, Boolean> result = new HashMap<>();
            
            if (userId == null || userId <= 0 || tagIds == null || tagIds.isEmpty()) {
                return result;
            }
            
            // 初始化所有为false
            for (Long tagId : tagIds) {
                result.put(tagId, false);
            }
            
            // 获取已关注的标签
            List<Long> followedTagIds = userTagFollowMapper.batchCheckUserFollowing(userId, tagIds);
            
            // 更新已关注的为true
            for (Long tagId : followedTagIds) {
                result.put(tagId, true);
            }
            
            return result;
        } catch (Exception e) {
            log.error("批量检查用户关注状态失败: userId={}, tagIds={}", userId, tagIds, e);
            return new HashMap<>();
        }
    }

    // =================== 标签关注者查询 ===================

    @Override
    public Long getTagFollowerCount(Long tagId) {
        try {
            if (tagId == null || tagId <= 0) {
                return 0L;
            }
            return userTagFollowMapper.countTagFollowers(tagId);
        } catch (Exception e) {
            log.error("获取标签关注者数量失败: tagId={}", tagId, e);
            return 0L;
        }
    }

    @Override
    public List<Long> getTagFollowers(Long tagId, Integer limit) {
        try {
            if (tagId == null || tagId <= 0) {
                return new ArrayList<>();
            }
            
            if (limit == null || limit <= 0) {
                limit = 20; // 默认20个
            }
            
            return userTagFollowMapper.getTagFollowers(tagId, limit);
        } catch (Exception e) {
            log.error("获取标签关注者失败: tagId={}, limit={}", tagId, limit, e);
            return new ArrayList<>();
        }
    }

    @Override
    public List<Long> getTagRecentFollowers(Long tagId, Integer days, Integer limit) {
        try {
            if (tagId == null || tagId <= 0) {
                return new ArrayList<>();
            }
            
            if (days == null || days <= 0) {
                days = 7; // 默认7天
            }
            
            if (limit == null || limit <= 0) {
                limit = 10; // 默认10个
            }
            
            return userTagFollowMapper.getTagRecentFollowers(tagId, days, limit);
        } catch (Exception e) {
            log.error("获取标签最近关注者失败: tagId={}, days={}, limit={}", tagId, days, limit, e);
            return new ArrayList<>();
        }
    }

    // =================== 协同过滤基础支持 ===================

    @Override
    public List<Long> getActiveTagUsers(Integer minTagCount, Integer days, Integer limit) {
        try {
            if (minTagCount == null || minTagCount <= 0) {
                minTagCount = 2; // 默认至少关注2个标签
            }
            
            if (days == null || days <= 0) {
                days = 90; // 默认90天内活跃
            }
            
            if (limit == null || limit <= 0) {
                limit = 100; // 默认100个用户
            }
            
            return userTagFollowMapper.getActiveTagUsers(minTagCount, days, limit);
        } catch (Exception e) {
            log.error("获取活跃标签用户失败: minTagCount={}, days={}, limit={}", minTagCount, days, limit, e);
            return new ArrayList<>();
        }
    }

    @Override
    public List<Long> findUsersWithSimilarTags(List<Long> tagIds, Long excludeUserId, Integer limit) {
        try {
            if (tagIds == null || tagIds.isEmpty()) {
                return new ArrayList<>();
            }
            
            if (limit == null || limit <= 0) {
                limit = 50; // 默认50个用户
            }
            
            return userTagFollowMapper.findUsersWithSimilarTags(tagIds, excludeUserId, limit);
        } catch (Exception e) {
            log.error("查找相似标签用户失败: tagIds={}, excludeUserId={}, limit={}", tagIds, excludeUserId, limit, e);
            return new ArrayList<>();
        }
    }

    @Override
    public Integer countCommonTags(Long userId1, Long userId2) {
        try {
            if (userId1 == null || userId1 <= 0 || userId2 == null || userId2 <= 0) {
                return 0;
            }
            
            if (userId1.equals(userId2)) {
                return getUserFollowedTagCount(userId1);
            }
            
            Integer count = userTagFollowMapper.countCommonTags(userId1, userId2);
            return count != null ? count : 0;
        } catch (Exception e) {
            log.error("计算用户共同标签数量失败: userId1={}, userId2={}", userId1, userId2, e);
            return 0;
        }
    }

    @Override
    public List<Long> getCommonTagIds(Long userId1, Long userId2) {
        try {
            if (userId1 == null || userId1 <= 0 || userId2 == null || userId2 <= 0) {
                return new ArrayList<>();
            }
            
            if (userId1.equals(userId2)) {
                return getUserFollowedTagIds(userId1);
            }
            
            return userTagFollowMapper.getCommonTagIds(userId1, userId2);
        } catch (Exception e) {
            log.error("获取用户共同标签失败: userId1={}, userId2={}", userId1, userId2, e);
            return new ArrayList<>();
        }
    }

    @Override
    public Double calculateUserSimilarity(Long userId1, Long userId2) {
        try {
            if (userId1 == null || userId1 <= 0 || userId2 == null || userId2 <= 0) {
                return 0.0;
            }
            
            if (userId1.equals(userId2)) {
                return 1.0;
            }
            
            // 获取两个用户的标签集合
            Set<Long> tags1 = new HashSet<>(getUserFollowedTagIds(userId1));
            Set<Long> tags2 = new HashSet<>(getUserFollowedTagIds(userId2));
            
            if (tags1.isEmpty() && tags2.isEmpty()) {
                return 0.0;
            }
            
            // 计算Jaccard相似度：交集/并集
            Set<Long> intersection = new HashSet<>(tags1);
            intersection.retainAll(tags2);
            
            Set<Long> union = new HashSet<>(tags1);
            union.addAll(tags2);
            
            double similarity = (double) intersection.size() / union.size();
            
            log.debug("计算用户相似度: userId1={}, userId2={}, similarity={}", userId1, userId2, similarity);
            return similarity;
        } catch (Exception e) {
            log.error("计算用户相似度失败: userId1={}, userId2={}", userId1, userId2, e);
            return 0.0;
        }
    }

    // =================== 用户标签统计分析 ===================

    @Override
    public Map<String, Object> getUserTagStatistics(Long userId) {
        try {
            Map<String, Object> statistics = new HashMap<>();
            
            if (userId == null || userId <= 0) {
                return statistics;
            }
            
            // 基础统计
            statistics.put("followedTagCount", getUserFollowedTagCount(userId));
            statistics.put("remainingFollowCount", getUserRemainingFollowCount(userId));
            statistics.put("canFollowMore", canUserFollowMoreTags(userId));
            
            // 最近活动
            List<UserTagFollow> recentFollows = getUserRecentFollowedTags(userId, 7, 5);
            statistics.put("recentFollowedTags", recentFollows.size());
            
            // 权重分布
            Map<String, Integer> weightDistribution = getUserTagWeightDistribution(userId);
            statistics.put("weightDistribution", weightDistribution);
            
            return statistics;
        } catch (Exception e) {
            log.error("获取用户标签统计信息失败: userId={}", userId, e);
            return new HashMap<>();
        }
    }

    @Override
    public Map<String, Integer> getUserTagWeightDistribution(Long userId) {
        try {
            if (userId == null || userId <= 0) {
                return new HashMap<>();
            }
            
            List<Map<String, Object>> distributions = userTagFollowMapper.getUserTagWeightDistribution(userId);
            
            Map<String, Integer> result = new HashMap<>();
            for (Map<String, Object> item : distributions) {
                String weightRange = (String) item.get("weight_range");
                Object tagCountObj = item.get("tag_count");
                Integer tagCount = tagCountObj instanceof Long ? ((Long) tagCountObj).intValue() : (Integer) tagCountObj;
                result.put(weightRange, tagCount);
            }
            
            return result;
        } catch (Exception e) {
            log.error("获取用户标签权重分布失败: userId={}", userId, e);
            return new HashMap<>();
        }
    }

    @Override
    public Map<String, Integer> getUserFollowTimeDistribution(Long userId, Integer days) {
        try {
            if (userId == null || userId <= 0) {
                return new HashMap<>();
            }
            
            if (days == null || days <= 0) {
                days = 30; // 默认30天
            }
            
            List<Map<String, Object>> distributions = userTagFollowMapper.getUserFollowTimeDistribution(userId, days);
            
            Map<String, Integer> result = new HashMap<>();
            for (Map<String, Object> item : distributions) {
                String followDate = item.get("follow_date").toString();
                Object followCountObj = item.get("follow_count");
                Integer followCount = followCountObj instanceof Long ? ((Long) followCountObj).intValue() : (Integer) followCountObj;
                result.put(followDate, followCount);
            }
            
            return result;
        } catch (Exception e) {
            log.error("获取用户关注时间分布失败: userId={}, days={}", userId, days, e);
            return new HashMap<>();
        }
    }

    // =================== 权限验证 ===================

    @Override
    public boolean canUserFollowMoreTags(Long userId) {
        try {
            if (userId == null || userId <= 0) {
                return false;
            }
            
            Integer currentCount = getUserFollowedTagCount(userId);
            return currentCount < MAX_USER_FOLLOW_TAGS;
        } catch (Exception e) {
            log.error("检查用户是否可以关注更多标签失败: userId={}", userId, e);
            return false;
        }
    }

    @Override
    public boolean canUserFollowTag(Long userId, Long tagId) {
        try {
            if (userId == null || userId <= 0 || tagId == null || tagId <= 0) {
                return false;
            }
            
            // 检查标签是否存在且启用
            if (!tagService.existsActiveTag(tagId)) {
                return false;
            }
            
            // 检查是否已关注
            if (isFollowing(userId, tagId)) {
                return false;
            }
            
            // 检查是否还能关注更多标签
            return canUserFollowMoreTags(userId);
        } catch (Exception e) {
            log.error("检查用户是否可以关注指定标签失败: userId={}, tagId={}", userId, tagId, e);
            return false;
        }
    }

    @Override
    public Integer getUserRemainingFollowCount(Long userId) {
        try {
            if (userId == null || userId <= 0) {
                return 0;
            }
            
            Integer currentCount = getUserFollowedTagCount(userId);
            return Math.max(0, MAX_USER_FOLLOW_TAGS - currentCount);
        } catch (Exception e) {
            log.error("获取用户剩余可关注标签数量失败: userId={}", userId, e);
            return 0;
        }
    }

    // =================== 数据清理和管理 ===================

    @Override
    @Transactional
    public Integer cleanupInvalidUserTagFollows(Long userId) {
        try {
            log.info("清理用户无效标签关注: userId={}", userId);
            
            int cleanupCount = userTagFollowMapper.cleanupInvalidFollows(userId);
            
            if (cleanupCount > 0) {
                log.info("清理用户无效标签关注完成: userId={}, count={}", userId, cleanupCount);
            }
            
            return cleanupCount;
        } catch (Exception e) {
            log.error("清理用户无效标签关注失败: userId={}", userId, e);
            throw new RuntimeException("清理无效关注失败: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public boolean deleteAllUserFollows(Long userId) {
        try {
            log.info("删除用户所有标签关注: userId={}", userId);
            
            if (userId == null || userId <= 0) {
                throw new RuntimeException("用户ID无效");
            }
            
            // 获取用户当前关注的标签（用于更新标签关注数）
            List<Long> followedTagIds = getUserFollowedTagIds(userId);
            
            // 删除所有关注记录
            int result = userTagFollowMapper.deleteAllUserFollows(userId);
            
            if (result > 0) {
                // 更新标签关注数
                for (Long tagId : followedTagIds) {
                    tagService.decrementFollowCount(tagId, 1);
                }
                
                log.info("删除用户所有标签关注成功: userId={}, count={}", userId, result);
                return true;
            }
            
            return true; // 没有记录也视为成功
        } catch (Exception e) {
            log.error("删除用户所有标签关注失败: userId={}", userId, e);
            throw new RuntimeException("删除用户关注失败: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public boolean deleteAllTagFollows(Long tagId) {
        try {
            log.info("删除标签所有关注记录: tagId={}", tagId);
            
            if (tagId == null || tagId <= 0) {
                throw new RuntimeException("标签ID无效");
            }
            
            // 获取关注者数量（用于更新标签关注数）
            Long followerCount = getTagFollowerCount(tagId);
            
            // 删除所有关注记录
            int result = userTagFollowMapper.deleteAllTagFollows(tagId);
            
            if (result > 0) {
                // 重置标签关注数为0
                tagService.decrementFollowCount(tagId, followerCount.intValue());
                
                log.info("删除标签所有关注记录成功: tagId={}, count={}", tagId, result);
                return true;
            }
            
            return true; // 没有记录也视为成功
        } catch (Exception e) {
            log.error("删除标签所有关注记录失败: tagId={}", tagId, e);
            throw new RuntimeException("删除标签关注记录失败: " + e.getMessage(), e);
        }
    }

    // =================== 智能推荐支持 ===================

    @Override
    public List<Long> getHotTagsForNewUser(Long userId, Integer limit) {
        try {
            if (limit == null || limit <= 0) {
                limit = 9; // 默认推荐9个
            }
            
            // 获取热门标签
            List<Tag> hotTags = tagService.getHotTags(limit * 2); // 多获取一些备选
            
            // 过滤用户已关注的标签
            List<Long> followedTagIds = getUserFollowedTagIds(userId);
            Set<Long> followedSet = new HashSet<>(followedTagIds);
            
            return hotTags.stream()
                    .map(Tag::getId)
                    .filter(tagId -> !followedSet.contains(tagId))
                    .limit(limit)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("为新用户推荐热门标签失败: userId={}, limit={}", userId, limit, e);
            return new ArrayList<>();
        }
    }

    @Override
    public List<Long> getTagsByUserBehavior(Long userId, String behaviorType, Integer limit) {
        try {
            // 这里可以根据用户行为（浏览、点赞、分享、评论）推荐标签
            // 当前简化实现：返回热门标签
            log.debug("根据用户行为推荐标签: userId={}, behaviorType={}, limit={}", userId, behaviorType, limit);
            
            return getHotTagsForNewUser(userId, limit);
        } catch (Exception e) {
            log.error("根据用户行为推荐标签失败: userId={}, behaviorType={}, limit={}", userId, behaviorType, limit, e);
            return new ArrayList<>();
        }
    }

    @Override
    public List<Long> getInterestingTagsForUser(Long userId, Integer limit) {
        try {
            if (limit == null || limit <= 0) {
                limit = 5; // 默认推荐5个
            }
            
            // 获取用户关注的标签
            List<Long> userTagIds = getUserFollowedTagIds(userId);
            if (userTagIds.isEmpty()) {
                return getHotTagsForNewUser(userId, limit);
            }
            
            // 查找关注了相似标签的其他用户
            List<Long> similarUsers = findUsersWithSimilarTags(userTagIds, userId, 20);
            
            if (similarUsers.isEmpty()) {
                return getHotTagsForNewUser(userId, limit);
            }
            
            // 统计这些用户关注的标签频次
            Map<Long, Integer> tagFrequency = new HashMap<>();
            Set<Long> userFollowedSet = new HashSet<>(userTagIds);
            
            for (Long similarUserId : similarUsers) {
                List<Long> similarUserTags = getUserFollowedTagIds(similarUserId);
                for (Long tagId : similarUserTags) {
                    if (!userFollowedSet.contains(tagId)) { // 排除用户已关注的
                        tagFrequency.merge(tagId, 1, Integer::sum);
                    }
                }
            }
            
            // 按频次排序并返回
            return tagFrequency.entrySet().stream()
                    .sorted(Map.Entry.<Long, Integer>comparingByValue().reversed())
                    .limit(limit)
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("获取用户可能感兴趣的标签失败: userId={}, limit={}", userId, limit, e);
            return getHotTagsForNewUser(userId, limit);
        }
    }
}