package com.gig.collide.social.domain.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gig.collide.api.follow.service.FollowFacadeService;
import com.gig.collide.api.follow.request.FollowQueryRequest;
import com.gig.collide.api.follow.response.data.FollowInfo;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.social.domain.entity.SocialPost;
import com.gig.collide.social.infrastructure.mapper.SocialPostMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import com.alicp.jetcache.anno.CacheType;
import com.alicp.jetcache.anno.Cached;
import com.gig.collide.cache.constant.CacheConstant;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 时间线服务
 * 
 * <p>专门处理时间线生成逻辑，完全去连表化设计</p>
 * <p>通过应用层逻辑整合关注关系和动态数据，避免数据库连表操作</p>
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TimelineService {

    private final SocialPostMapper socialPostMapper;

    @DubboReference(version = "1.0.0", check = false)
    private FollowFacadeService followFacadeService;

    /**
     * 获取用户的关注动态流
     * 步骤：1. 查询关注关系  2. 根据关注用户ID查询动态  3. 过滤可见性
     *
     * @param userId 用户ID
     * @param currentPage 当前页
     * @param pageSize 页大小
     * @return 关注动态列表
     */
    public PageResponse<SocialPost> getFollowingFeed(Long userId, Integer currentPage, Integer pageSize) {
        try {
            log.info("获取用户关注动态流，用户ID：{}，页码：{}，页大小：{}", userId, currentPage, pageSize);

            // 1. 查询用户关注的所有用户ID（无连表，单独查询关注关系表）
            List<Long> followingUserIds = getFollowingUserIds(userId);
            
            if (CollectionUtils.isEmpty(followingUserIds)) {
                log.info("用户{}没有关注任何人，返回空动态流", userId);
                return PageResponse.empty();
            }

            log.info("用户{}关注了{}个用户", userId, followingUserIds.size());

            // 2. 根据关注用户ID查询动态（无连表，单表查询）
            Page<SocialPost> page = new Page<>(currentPage, pageSize);
            IPage<SocialPost> resultPage = socialPostMapper.selectFollowingFeedPage(
                page, userId, followingUserIds, SocialPostStatus.PUBLISHED);

            // 3. 应用层处理可见性逻辑（如果需要更复杂的权限控制）
            List<SocialPost> filteredPosts = filterPostsByVisibility(resultPage.getRecords(), userId, followingUserIds);

            return PageResponse.of(filteredPosts, (int) resultPage.getTotal(), (int) resultPage.getSize(), (int) resultPage.getCurrent());

        } catch (Exception e) {
            log.error("获取用户关注动态流失败，用户ID：{}", userId, e);
            return PageResponse.empty();
        }
    }

    /**
     * 获取用户时间线（个人动态）
     *
     * @param userId 用户ID
     * @param currentPage 当前页
     * @param pageSize 页大小
     * @return 个人动态列表
     */
    public PageResponse<SocialPost> getUserTimeline(Long userId, Integer currentPage, Integer pageSize) {
        try {
            log.info("获取用户个人时间线，用户ID：{}，页码：{}，页大小：{}", userId, currentPage, pageSize);

            Page<SocialPost> page = new Page<>(currentPage, pageSize);
            IPage<SocialPost> resultPage = socialPostMapper.selectUserTimelinePage(page, userId, null);

            return PageResponse.of(resultPage.getRecords(), (int) resultPage.getTotal(), (int) resultPage.getSize(), (int) resultPage.getCurrent());

        } catch (Exception e) {
            log.error("获取用户个人时间线失败，用户ID：{}", userId, e);
            return PageResponse.empty();
        }
    }

    /**
     * 生成推荐时间线（基于热度）
     *
     * @param userId 用户ID
     * @param currentPage 当前页
     * @param pageSize 页大小
     * @param timeRange 时间范围
     * @return 推荐动态列表
     */
    public PageResponse<SocialPost> getRecommendedFeed(Long userId, Integer currentPage, Integer pageSize, String timeRange) {
        try {
            log.info("获取推荐时间线，用户ID：{}，页码：{}，页大小：{}，时间范围：{}", userId, currentPage, pageSize, timeRange);

            // 计算时间范围
            LocalDateTime startTime = calculateStartTime(timeRange);
            
            Page<SocialPost> page = new Page<>(currentPage, pageSize);
            IPage<SocialPost> resultPage = socialPostMapper.selectHotPostsPage(
                page, SocialPostStatus.PUBLISHED, startTime, null, 0.0);

            return PageResponse.of(resultPage.getRecords(), (int) resultPage.getTotal(), (int) resultPage.getSize(), (int) resultPage.getCurrent());

        } catch (Exception e) {
            log.error("获取推荐时间线失败，用户ID：{}", userId, e);
            return PageResponse.empty();
        }
    }

    /**
     * 获取用户关注的用户ID列表（缓存1小时）
     * 
     * @param userId 用户ID
     * @return 关注用户ID列表
     */
    @Cached(name = CacheConstant.SOCIAL_FOLLOWING_USERS_CACHE, 
            key = "#userId",
            expire = CacheConstant.SOCIAL_FOLLOWING_CACHE_EXPIRE, 
            timeUnit = TimeUnit.MINUTES,
            cacheType = CacheType.BOTH)
    public List<Long> getFollowingUserIds(Long userId) {
        try {
            log.debug("查询用户关注列表，用户ID：{}", userId);

            // 调用关注服务，获取关注列表（这是另一个微服务的单表查询）
            FollowQueryRequest queryRequest = new FollowQueryRequest();
            queryRequest.setFollowerUserId(userId);
            queryRequest.setPageNo(1);
            queryRequest.setPageSize(1000); // 假设一个用户最多关注1000人

            PageResponse<FollowInfo> followResponse = followFacadeService.pageQueryFollowing(queryRequest);
            
            if (followResponse == null || CollectionUtils.isEmpty(followResponse.getDatas())) {
                return Collections.emptyList();
            }

            // 提取关注用户ID列表
            return followResponse.getDatas().stream()
                    .map(FollowInfo::getFollowedUserId)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("查询用户关注列表失败，用户ID：{}", userId, e);
            return Collections.emptyList();
        }
    }

    /**
     * 应用层过滤动态可见性
     * 这里可以实现更复杂的可见性逻辑，比如好友可见、部分可见等
     *
     * @param posts 动态列表
     * @param currentUserId 当前用户ID
     * @param followingUserIds 关注用户ID列表
     * @return 过滤后的动态列表
     */
    private List<SocialPost> filterPostsByVisibility(List<SocialPost> posts, Long currentUserId, List<Long> followingUserIds) {
        if (CollectionUtils.isEmpty(posts)) {
            return Collections.emptyList();
        }

        List<SocialPost> filteredPosts = new ArrayList<>();
        
        for (SocialPost post : posts) {
            // 0-公开：所有人可见
            if (post.getVisibility() == 0) {
                filteredPosts.add(post);
                continue;
            }

            // 1-仅关注者：需要验证关注关系
            if (post.getVisibility() == 1) {
                // 如果是作者本人，可见
                if (post.getAuthorId().equals(currentUserId)) {
                    filteredPosts.add(post);
                    continue;
                }
                
                // 如果当前用户关注了作者，可见
                if (followingUserIds.contains(post.getAuthorId())) {
                    filteredPosts.add(post);
                    continue;
                }
            }

            // 2-仅自己：只有作者本人可见
            if (post.getVisibility() == 2 && post.getAuthorId().equals(currentUserId)) {
                filteredPosts.add(post);
            }

            // 其他情况不可见，跳过
        }

        return filteredPosts;
    }

    /**
     * 根据时间范围字符串计算开始时间
     *
     * @param timeRange 时间范围字符串
     * @return 开始时间
     */
    private LocalDateTime calculateStartTime(String timeRange) {
        LocalDateTime now = LocalDateTime.now();
        
        return switch (timeRange) {
            case "hour" -> now.minusHours(1);
            case "day" -> now.minusDays(1);
            case "week" -> now.minusWeeks(1);
            case "month" -> now.minusMonths(1);
            default -> now.minusDays(1); // 默认近1天
        };
    }

    /**
     * 预热用户时间线缓存
     * 可以在用户登录时调用，提前加载关注用户列表
     *
     * @param userId 用户ID
     */
    public void preloadUserTimeline(Long userId) {
        try {
            log.info("预热用户时间线缓存，用户ID：{}", userId);
            
            // 预加载关注用户列表到缓存
            getFollowingUserIds(userId);
            
        } catch (Exception e) {
            log.error("预热用户时间线缓存失败，用户ID：{}", userId, e);
        }
    }

    /**
     * 清理用户时间线缓存
     * 当用户关注关系发生变化时调用
     *
     * @param userId 用户ID
     */
    public void clearUserTimelineCache(Long userId) {
        try {
            log.info("清理用户时间线缓存，用户ID：{}", userId);
            
            // 这里可以调用缓存管理器清理相关缓存
            // cacheManager.getCache("followingUserIds").evict(userId);
            
        } catch (Exception e) {
            log.error("清理用户时间线缓存失败，用户ID：{}", userId, e);
        }
    }
} 