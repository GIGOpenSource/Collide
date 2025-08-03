package com.gig.collide.tag.facade;

import com.alicp.jetcache.anno.Cached;
import com.alicp.jetcache.anno.CacheInvalidate;
import com.alicp.jetcache.anno.CacheType;
import org.apache.dubbo.config.annotation.DubboService;
import com.gig.collide.api.tag.UserTagFacadeService;
import com.gig.collide.api.tag.request.UserTagQueryRequest;
import com.gig.collide.api.tag.response.TagResponse;
import com.gig.collide.api.tag.response.UserTagResponse;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.web.vo.Result;
import com.gig.collide.tag.domain.entity.Tag;
import com.gig.collide.tag.domain.entity.UserTagFollow;
import com.gig.collide.tag.domain.service.TagService;
import com.gig.collide.tag.domain.service.UserTagService;
import com.gig.collide.tag.infrastructure.cache.TagCacheConstant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 用户标签门面服务实现
 * 
 * @author GIG Team
 * @version 1.0.0
 */
@Slf4j
@DubboService(version = "1.0.0")
@RequiredArgsConstructor
public class UserTagFacadeServiceImpl implements UserTagFacadeService {

    private final UserTagService userTagService;
    private final TagService tagService;

    // =================== 用户关注标签管理 ===================

    @Override
    @CacheInvalidate(name = TagCacheConstant.USER_FOLLOWED_TAGS_CACHE, key = TagCacheConstant.USER_FOLLOWED_TAGS_KEY)
    @CacheInvalidate(name = TagCacheConstant.USER_TAG_COUNT_CACHE, key = TagCacheConstant.USER_TAG_COUNT_KEY)
    public Result<Void> followTag(Long userId, Long tagId) {
        try {
            log.info("用户关注标签: userId={}, tagId={}", userId, tagId);
            
            // 参数验证
            if (userId == null || userId <= 0) {
                return Result.error("USER_TAG_ERROR", "用户ID无效");
            }
            
            if (tagId == null || tagId <= 0) {
                return Result.error("USER_TAG_ERROR", "标签ID无效");
            }
            
            // 调用领域服务
            boolean success = userTagService.followTag(userId, tagId);
            
            if (success) {
                log.info("用户关注标签成功: userId={}, tagId={}", userId, tagId);
                return Result.success();
            } else {
                return Result.error("USER_TAG_ERROR", "关注标签失败");
            }
        } catch (Exception e) {
            log.error("用户关注标签失败: userId={}, tagId={}", userId, tagId, e);
            return Result.error("USER_TAG_ERROR", "关注标签失败: " + e.getMessage());
        }
    }

    @Override
    @CacheInvalidate(name = TagCacheConstant.USER_FOLLOWED_TAGS_CACHE, key = TagCacheConstant.USER_FOLLOWED_TAGS_KEY)
    @CacheInvalidate(name = TagCacheConstant.USER_TAG_COUNT_CACHE, key = TagCacheConstant.USER_TAG_COUNT_KEY)
    public Result<Void> unfollowTag(Long userId, Long tagId) {
        try {
            log.info("用户取消关注标签: userId={}, tagId={}", userId, tagId);
            
            // 参数验证
            if (userId == null || userId <= 0) {
                return Result.error("USER_TAG_ERROR", "用户ID无效");
            }
            
            if (tagId == null || tagId <= 0) {
                return Result.error("USER_TAG_ERROR", "标签ID无效");
            }
            
            // 调用领域服务
            boolean success = userTagService.unfollowTag(userId, tagId);
            
            if (success) {
                log.info("用户取消关注标签成功: userId={}, tagId={}", userId, tagId);
                return Result.success();
            } else {
                return Result.error("USER_TAG_ERROR", "取消关注标签失败");
            }
        } catch (Exception e) {
            log.error("用户取消关注标签失败: userId={}, tagId={}", userId, tagId, e);
            return Result.error("USER_TAG_ERROR", "取消关注标签失败: " + e.getMessage());
        }
    }

    @Override
    @CacheInvalidate(name = TagCacheConstant.USER_FOLLOWED_TAGS_CACHE, key = TagCacheConstant.USER_FOLLOWED_TAGS_KEY)
    @CacheInvalidate(name = TagCacheConstant.USER_TAG_COUNT_CACHE, key = TagCacheConstant.USER_TAG_COUNT_KEY)
    public Result<List<String>> batchFollowTags(Long userId, List<Long> tagIds) {
        try {
            log.info("用户批量关注标签: userId={}, tagIds={}", userId, tagIds);
            
            // 参数验证
            if (userId == null || userId <= 0) {
                return Result.error("USER_TAG_ERROR", "用户ID无效");
            }
            
            if (tagIds == null || tagIds.isEmpty()) {
                return Result.error("USER_TAG_ERROR", "标签ID列表不能为空");
            }
            
            // 调用领域服务
            List<Long> successTagIds = userTagService.batchFollowTags(userId, tagIds);
            
            // 构建结果消息
            List<String> results = new ArrayList<>();
            for (Long tagId : tagIds) {
                if (successTagIds.contains(tagId)) {
                    results.add("标签" + tagId + "关注成功");
                } else {
                    results.add("标签" + tagId + "关注失败");
                }
            }
            
            log.info("用户批量关注标签完成: userId={}, success={}/{}", 
                    userId, successTagIds.size(), tagIds.size());
            
            return Result.success(results);
        } catch (Exception e) {
            log.error("用户批量关注标签失败: userId={}, tagIds={}", userId, tagIds, e);
            return Result.error("USER_TAG_ERROR", "批量关注标签失败: " + e.getMessage());
        }
    }

    @Override
    @CacheInvalidate(name = TagCacheConstant.USER_FOLLOWED_TAGS_CACHE, key = TagCacheConstant.USER_FOLLOWED_TAGS_KEY)
    @CacheInvalidate(name = TagCacheConstant.USER_TAG_COUNT_CACHE, key = TagCacheConstant.USER_TAG_COUNT_KEY)
    public Result<Void> batchUnfollowTags(Long userId, List<Long> tagIds) {
        try {
            log.info("用户批量取消关注标签: userId={}, tagIds={}", userId, tagIds);
            
            // 参数验证
            if (userId == null || userId <= 0) {
                return Result.error("USER_TAG_ERROR", "用户ID无效");
            }
            
            if (tagIds == null || tagIds.isEmpty()) {
                return Result.error("USER_TAG_ERROR", "标签ID列表不能为空");
            }
            
            // 调用领域服务
            boolean success = userTagService.batchUnfollowTags(userId, tagIds);
            
            if (success) {
                log.info("用户批量取消关注标签成功: userId={}, count={}", userId, tagIds.size());
                return Result.success();
            } else {
                return Result.error("USER_TAG_ERROR", "批量取消关注标签失败");
            }
        } catch (Exception e) {
            log.error("用户批量取消关注标签失败: userId={}, tagIds={}", userId, tagIds, e);
            return Result.error("USER_TAG_ERROR", "批量取消关注标签失败: " + e.getMessage());
        }
    }

    // =================== 用户关注标签查询 ===================

    @Override
    public Result<Boolean> isFollowing(Long userId, Long tagId) {
        try {
            log.debug("检查用户是否关注标签: userId={}, tagId={}", userId, tagId);
            
            // 参数验证
            if (userId == null || userId <= 0 || tagId == null || tagId <= 0) {
                return Result.error("USER_TAG_ERROR", "参数无效");
            }
            
            // 调用领域服务
            boolean isFollowing = userTagService.isFollowing(userId, tagId);
            
            return Result.success(isFollowing);
        } catch (Exception e) {
            log.error("检查用户是否关注标签失败: userId={}, tagId={}", userId, tagId, e);
            return Result.error("USER_TAG_ERROR", "检查关注状态失败: " + e.getMessage());
        }
    }

    @Override
    @Cached(name = TagCacheConstant.USER_FOLLOWED_TAGS_CACHE,
            key = TagCacheConstant.USER_FOLLOWED_TAGS_KEY,
            expire = TagCacheConstant.USER_FOLLOWED_TAGS_CACHE_EXPIRE,
            timeUnit = TimeUnit.MINUTES,
            cacheType = CacheType.BOTH)
    public Result<List<TagResponse>> getUserFollowedTags(Long userId) {
        try {
            log.debug("获取用户关注的标签: userId={}", userId);
            
            // 参数验证
            if (userId == null || userId <= 0) {
                return Result.error("USER_TAG_ERROR", "用户ID无效");
            }
            
            // 获取用户关注的标签ID列表
            List<Long> tagIds = userTagService.getUserFollowedTagIds(userId);
            
            if (tagIds.isEmpty()) {
                return Result.success(new ArrayList<>());
            }
            
            // 获取标签详情
            List<Tag> tags = tagService.getTagsByIds(tagIds);
            
            // 转换为响应
            List<TagResponse> responses = tags.stream()
                    .map(this::convertToTagResponse)
                    .collect(Collectors.toList());
            
            return Result.success(responses);
        } catch (Exception e) {
            log.error("获取用户关注标签失败: userId={}", userId, e);
            return Result.error("USER_TAG_ERROR", "获取关注标签失败: " + e.getMessage());
        }
    }

    @Override
    public Result<PageResponse<UserTagResponse>> queryUserFollowedTags(UserTagQueryRequest request) {
        try {
            log.debug("分页查询用户关注标签: userId={}, page={}, size={}", 
                    request.getUserId(), request.getCurrentPage(), request.getPageSize());
            
            // 参数验证
            if (request == null || request.getUserId() == null) {
                return Result.error("USER_TAG_ERROR", "请求参数无效");
            }
            
            // 调用领域服务
            PageResponse<UserTagFollow> followPage = userTagService.queryUserFollowedTags(request);
            
            // 转换为响应
            List<UserTagResponse> responses = new ArrayList<>();
            for (UserTagFollow follow : followPage.getDatas()) {
                UserTagResponse response = convertToUserTagResponse(follow);
                // 获取标签详情
                Tag tag = tagService.getTagById(follow.getTagId());
                if (tag != null) {
                    response.setTagName(tag.getTagName());
                    response.setTagDescription(tag.getTagDescription());
                    response.setTagIcon(tag.getTagIcon());
                    response.setTagWeight(tag.getWeight());
                    response.setTagHotness(tag.getHotness());
                }
                responses.add(response);
            }
            
            PageResponse<UserTagResponse> responsePage = new PageResponse<>();
            responsePage.setDatas(responses);
            responsePage.setCurrentPage(followPage.getCurrentPage());
            responsePage.setPageSize(followPage.getPageSize());
            responsePage.setTotal(followPage.getTotal());
            
            return Result.success(responsePage);
        } catch (Exception e) {
            log.error("分页查询用户关注标签失败: userId={}", request != null ? request.getUserId() : null, e);
            return Result.error("USER_TAG_ERROR", "查询用户关注标签失败: " + e.getMessage());
        }
    }

    @Override
    @Cached(name = TagCacheConstant.USER_TAG_COUNT_CACHE,
            key = TagCacheConstant.USER_TAG_COUNT_KEY,
            expire = TagCacheConstant.USER_TAG_COUNT_CACHE_EXPIRE,
            timeUnit = TimeUnit.MINUTES,
            cacheType = CacheType.BOTH)
    public Result<Integer> getUserFollowedTagCount(Long userId) {
        try {
            log.debug("获取用户关注标签数量: userId={}", userId);
            
            // 参数验证
            if (userId == null || userId <= 0) {
                return Result.error("USER_TAG_ERROR", "用户ID无效");
            }
            
            // 调用领域服务
            Integer count = userTagService.getUserFollowedTagCount(userId);
            
            return Result.success(count);
        } catch (Exception e) {
            log.error("获取用户关注标签数量失败: userId={}", userId, e);
            return Result.error("USER_TAG_ERROR", "获取关注标签数量失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Map<Long, Boolean>> batchCheckFollowing(Long userId, List<Long> tagIds) {
        try {
            log.debug("批量检查用户关注状态: userId={}, tagIds={}", userId, tagIds);
            
            // 参数验证
            if (userId == null || userId <= 0) {
                return Result.error("USER_TAG_ERROR", "用户ID无效");
            }
            
            if (tagIds == null || tagIds.isEmpty()) {
                return Result.success(new HashMap<>());
            }
            
            // 调用领域服务
            Map<Long, Boolean> result = userTagService.batchCheckFollowing(userId, tagIds);
            
            return Result.success(result);
        } catch (Exception e) {
            log.error("批量检查用户关注状态失败: userId={}, tagIds={}", userId, tagIds, e);
            return Result.error("USER_TAG_ERROR", "检查关注状态失败: " + e.getMessage());
        }
    }

    // =================== 协同过滤推荐 ===================

    @Override
    @Cached(name = TagCacheConstant.USER_TAG_RECOMMEND_CACHE,
            key = TagCacheConstant.USER_TAG_RECOMMEND_KEY,
            expire = TagCacheConstant.USER_TAG_RECOMMEND_CACHE_EXPIRE,
            timeUnit = TimeUnit.MINUTES,
            cacheType = CacheType.BOTH)
    public Result<List<TagResponse>> getRecommendTagsForUser(Long userId, Integer limit) {
        try {
            log.debug("为用户推荐标签: userId={}, limit={}", userId, limit);
            
            // 参数验证
            if (userId == null || userId <= 0) {
                return Result.error("USER_TAG_ERROR", "用户ID无效");
            }
            
            // 调用领域服务获取推荐标签ID
            List<Long> recommendTagIds = userTagService.getInterestingTagsForUser(userId, limit);
            
            if (recommendTagIds.isEmpty()) {
                // 如果没有推荐结果，返回热门标签
                recommendTagIds = userTagService.getHotTagsForNewUser(userId, limit);
            }
            
            // 获取标签详情
            List<Tag> tags = tagService.getTagsByIds(recommendTagIds);
            
            // 转换为响应
            List<TagResponse> responses = tags.stream()
                    .map(this::convertToTagResponse)
                    .collect(Collectors.toList());
            
            return Result.success(responses);
        } catch (Exception e) {
            log.error("为用户推荐标签失败: userId={}, limit={}", userId, limit, e);
            return Result.error("USER_TAG_ERROR", "推荐标签失败: " + e.getMessage());
        }
    }

    @Override
    public Result<List<Long>> getSimilarUsers(Long userId, Integer limit) {
        try {
            log.debug("获取相似用户: userId={}, limit={}", userId, limit);
            
            // 参数验证
            if (userId == null || userId <= 0) {
                return Result.error("USER_TAG_ERROR", "用户ID无效");
            }
            
            // 获取用户关注的标签
            List<Long> userTagIds = userTagService.getUserFollowedTagIds(userId);
            
            if (userTagIds.isEmpty()) {
                return Result.success(new ArrayList<>());
            }
            
            // 查找关注了相似标签的用户
            List<Long> similarUsers = userTagService.findUsersWithSimilarTags(userTagIds, userId, limit);
            
            return Result.success(similarUsers);
        } catch (Exception e) {
            log.error("获取相似用户失败: userId={}, limit={}", userId, limit, e);
            return Result.error("USER_TAG_ERROR", "获取相似用户失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Double> calculateUserSimilarity(Long userId1, Long userId2) {
        try {
            log.debug("计算用户相似度: userId1={}, userId2={}", userId1, userId2);
            
            // 参数验证
            if (userId1 == null || userId1 <= 0 || userId2 == null || userId2 <= 0) {
                return Result.error("USER_TAG_ERROR", "用户ID无效");
            }
            
            // 调用领域服务
            Double similarity = userTagService.calculateUserSimilarity(userId1, userId2);
            
            return Result.success(similarity);
        } catch (Exception e) {
            log.error("计算用户相似度失败: userId1={}, userId2={}", userId1, userId2, e);
            return Result.error("USER_TAG_ERROR", "计算用户相似度失败: " + e.getMessage());
        }
    }

    // =================== 用户标签统计分析 ===================

    @Override
    @Cached(name = TagCacheConstant.USER_TAG_STATISTICS_CACHE,
            key = TagCacheConstant.USER_TAG_STATISTICS_KEY,
            expire = TagCacheConstant.USER_TAG_STATISTICS_CACHE_EXPIRE,
            timeUnit = TimeUnit.MINUTES,
            cacheType = CacheType.BOTH)
    public Result<Map<String, Object>> getUserTagStatistics(Long userId) {
        try {
            log.debug("获取用户标签统计: userId={}", userId);
            
            // 参数验证
            if (userId == null || userId <= 0) {
                return Result.error("USER_TAG_ERROR", "用户ID无效");
            }
            
            // 调用领域服务
            Map<String, Object> statistics = userTagService.getUserTagStatistics(userId);
            
            return Result.success(statistics);
        } catch (Exception e) {
            log.error("获取用户标签统计失败: userId={}", userId, e);
            return Result.error("USER_TAG_ERROR", "获取统计信息失败: " + e.getMessage());
        }
    }

    @Override
    public Result<List<TagResponse>> getUserRecentFollowedTags(Long userId, Integer days, Integer limit) {
        try {
            log.debug("获取用户最近关注标签: userId={}, days={}, limit={}", userId, days, limit);
            
            // 参数验证
            if (userId == null || userId <= 0) {
                return Result.error("USER_TAG_ERROR", "用户ID无效");
            }
            
            // 调用领域服务
            List<UserTagFollow> recentFollows = userTagService.getUserRecentFollowedTags(userId, days, limit);
            
            // 获取标签详情
            List<Long> tagIds = recentFollows.stream()
                    .map(UserTagFollow::getTagId)
                    .collect(Collectors.toList());
            
            List<Tag> tags = tagService.getTagsByIds(tagIds);
            
            // 转换为响应
            List<TagResponse> responses = tags.stream()
                    .map(this::convertToTagResponse)
                    .collect(Collectors.toList());
            
            return Result.success(responses);
        } catch (Exception e) {
            log.error("获取用户最近关注标签失败: userId={}, days={}, limit={}", userId, days, limit, e);
            return Result.error("USER_TAG_ERROR", "获取最近关注标签失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Map<String, Integer>> getUserInterestWeightDistribution(Long userId) {
        try {
            log.debug("获取用户兴趣权重分布: userId={}", userId);
            
            // 参数验证
            if (userId == null || userId <= 0) {
                return Result.error("USER_TAG_ERROR", "用户ID无效");
            }
            
            // 调用领域服务
            Map<String, Integer> distribution = userTagService.getUserTagWeightDistribution(userId);
            
            return Result.success(distribution);
        } catch (Exception e) {
            log.error("获取用户兴趣权重分布失败: userId={}", userId, e);
            return Result.error("USER_TAG_ERROR", "获取权重分布失败: " + e.getMessage());
        }
    }

    // =================== 标签关注者查询 ===================

    @Override
    public Result<Long> getTagFollowerCount(Long tagId) {
        try {
            log.debug("获取标签关注者数量: tagId={}", tagId);
            
            // 参数验证
            if (tagId == null || tagId <= 0) {
                return Result.error("USER_TAG_ERROR", "标签ID无效");
            }
            
            // 调用领域服务
            Long count = userTagService.getTagFollowerCount(tagId);
            
            return Result.success(count);
        } catch (Exception e) {
            log.error("获取标签关注者数量失败: tagId={}", tagId, e);
            return Result.error("USER_TAG_ERROR", "获取关注者数量失败: " + e.getMessage());
        }
    }

    @Override
    public Result<List<Long>> getTagFollowers(Long tagId, Integer limit) {
        try {
            log.debug("获取标签关注者: tagId={}, limit={}", tagId, limit);
            
            // 参数验证
            if (tagId == null || tagId <= 0) {
                return Result.error("USER_TAG_ERROR", "标签ID无效");
            }
            
            // 调用领域服务
            List<Long> followers = userTagService.getTagFollowers(tagId, limit);
            
            return Result.success(followers);
        } catch (Exception e) {
            log.error("获取标签关注者失败: tagId={}, limit={}", tagId, limit, e);
            return Result.error("USER_TAG_ERROR", "获取关注者失败: " + e.getMessage());
        }
    }

    @Override
    public Result<List<Long>> getTagRecentFollowers(Long tagId, Integer days, Integer limit) {
        try {
            log.debug("获取标签最近关注者: tagId={}, days={}, limit={}", tagId, days, limit);
            
            // 参数验证
            if (tagId == null || tagId <= 0) {
                return Result.error("USER_TAG_ERROR", "标签ID无效");
            }
            
            // 调用领域服务
            List<Long> recentFollowers = userTagService.getTagRecentFollowers(tagId, days, limit);
            
            return Result.success(recentFollowers);
        } catch (Exception e) {
            log.error("获取标签最近关注者失败: tagId={}, days={}, limit={}", tagId, days, limit, e);
            return Result.error("USER_TAG_ERROR", "获取最近关注者失败: " + e.getMessage());
        }
    }

    // =================== 个性化推荐 ===================

    @Override
    public Result<List<TagResponse>> getHotTagsForNewUser(Long userId, Integer limit) {
        try {
            log.debug("为新用户推荐热门标签: userId={}, limit={}", userId, limit);
            
            // 参数验证
            if (userId == null || userId <= 0) {
                return Result.error("USER_TAG_ERROR", "用户ID无效");
            }
            
            // 调用领域服务
            List<Long> hotTagIds = userTagService.getHotTagsForNewUser(userId, limit);
            
            // 获取标签详情
            List<Tag> tags = tagService.getTagsByIds(hotTagIds);
            
            // 转换为响应
            List<TagResponse> responses = tags.stream()
                    .map(this::convertToTagResponse)
                    .collect(Collectors.toList());
            
            return Result.success(responses);
        } catch (Exception e) {
            log.error("为新用户推荐热门标签失败: userId={}, limit={}", userId, limit, e);
            return Result.error("USER_TAG_ERROR", "推荐热门标签失败: " + e.getMessage());
        }
    }

    @Override
    public Result<List<TagResponse>> getTagsByUserBehavior(Long userId, String behaviorType, Integer limit) {
        try {
            log.debug("根据用户行为推荐标签: userId={}, behaviorType={}, limit={}", userId, behaviorType, limit);
            
            // 参数验证
            if (userId == null || userId <= 0) {
                return Result.error("USER_TAG_ERROR", "用户ID无效");
            }
            
            // 调用领域服务
            List<Long> tagIds = userTagService.getTagsByUserBehavior(userId, behaviorType, limit);
            
            // 获取标签详情
            List<Tag> tags = tagService.getTagsByIds(tagIds);
            
            // 转换为响应
            List<TagResponse> responses = tags.stream()
                    .map(this::convertToTagResponse)
                    .collect(Collectors.toList());
            
            return Result.success(responses);
        } catch (Exception e) {
            log.error("根据用户行为推荐标签失败: userId={}, behaviorType={}, limit={}", userId, behaviorType, limit, e);
            return Result.error("USER_TAG_ERROR", "推荐标签失败: " + e.getMessage());
        }
    }

    // =================== 管理功能 ===================

    @Override
    @CacheInvalidate(name = TagCacheConstant.USER_FOLLOWED_TAGS_CACHE)
    @CacheInvalidate(name = TagCacheConstant.USER_TAG_COUNT_CACHE)
    public Result<Integer> cleanupInvalidUserTagFollows(Long userId) {
        try {
            log.info("清理用户无效标签关注: userId={}", userId);
            
            // 调用领域服务
            Integer cleanupCount = userTagService.cleanupInvalidUserTagFollows(userId);
            
            log.info("清理用户无效标签关注完成: userId={}, count={}", userId, cleanupCount);
            return Result.success(cleanupCount);
        } catch (Exception e) {
            log.error("清理用户无效标签关注失败: userId={}", userId, e);
            return Result.error("USER_TAG_ERROR", "清理无效关注失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Boolean> canUserFollowTag(Long userId, Long tagId) {
        try {
            log.debug("检查用户是否可以关注标签: userId={}, tagId={}", userId, tagId);
            
            // 参数验证
            if (userId == null || userId <= 0 || tagId == null || tagId <= 0) {
                return Result.error("USER_TAG_ERROR", "参数无效");
            }
            
            // 调用领域服务
            boolean canFollow = userTagService.canUserFollowTag(userId, tagId);
            
            return Result.success(canFollow);
        } catch (Exception e) {
            log.error("检查用户是否可以关注标签失败: userId={}, tagId={}", userId, tagId, e);
            return Result.error("USER_TAG_ERROR", "检查关注权限失败: " + e.getMessage());
        }
    }

    // =================== 私有辅助方法 ===================

    /**
     * 转换Tag实体为TagResponse
     */
    private TagResponse convertToTagResponse(Tag tag) {
        if (tag == null) {
            return null;
        }
        
        TagResponse response = new TagResponse();
        BeanUtils.copyProperties(tag, response);
        
        // 设置状态描述
        response.setStatusDesc(tag.getStatusDesc());
        
        // 设置推荐分数
        response.setRecommendScore(tag.calculateRecommendScore());
        
        return response;
    }

    /**
     * 转换UserTagFollow实体为UserTagResponse
     */
    private UserTagResponse convertToUserTagResponse(UserTagFollow follow) {
        if (follow == null) {
            return null;
        }
        
        UserTagResponse response = new UserTagResponse();
        BeanUtils.copyProperties(follow, response);
        
        // 设置关注天数
        response.setFollowDays((int) follow.getFollowDays());
        
        return response;
    }
}