package com.gig.collide.social.domain.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gig.collide.api.social.constant.SocialConstant;
import com.gig.collide.api.social.request.SocialPostCreateRequest;
import com.gig.collide.api.social.request.SocialPostQueryRequest;
import com.gig.collide.api.social.request.SocialPostUpdateRequest;
import com.gig.collide.api.social.response.data.BasicSocialPostInfo;
import com.gig.collide.api.social.response.data.SocialPostInfo;
import com.gig.collide.base.exception.BizException;
import com.gig.collide.base.exception.CommonErrorCode;
import com.gig.collide.cache.constant.CacheConstant;
import com.gig.collide.social.domain.entity.SocialPost;
import com.gig.collide.social.domain.entity.convertor.SocialPostConvertor;
import com.gig.collide.social.domain.service.SocialPostService;
import com.gig.collide.social.infrastructure.mapper.SocialPostMapper;
import com.alicp.jetcache.anno.CacheInvalidate;
import com.alicp.jetcache.anno.CacheType;
import com.alicp.jetcache.anno.Cached;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 社交动态服务实现
 * 
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SocialPostServiceImpl implements SocialPostService {

    private final SocialPostMapper socialPostMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheInvalidate(name = CacheConstant.SOCIAL_USER_POSTS_CACHE, key = "#request.authorId")
    @CacheInvalidate(name = CacheConstant.SOCIAL_USER_TIMELINE_CACHE, key = "'timeline:' + #request.authorId")
    @CacheInvalidate(name = CacheConstant.SOCIAL_HOT_POSTS_CACHE, key = "'hot*'")
    public SocialPost createPost(SocialPostCreateRequest request) {
        log.info("创建社交动态开始，请求参数：{}", request);
        
        // 参数验证
        if (request == null || !StringUtils.hasText(request.getContent())) {
            throw new BizException("动态内容不能为空", CommonErrorCode.PARAM_INVALID);
        }
        
        // 检查用户今日发布限制
        Long todayCount = getUserTodayPostCount(request.getAuthorId());
        if (todayCount >= SocialConstant.MAX_DAILY_POSTS) {
            throw new BizException("今日发布动态已达上限", CommonErrorCode.SYSTEM_ERROR);
        }
        
        // 转换为实体
        SocialPost socialPost = SocialPostConvertor.INSTANCE.createRequestToEntity(request);
        
        // 设置默认值
        socialPost.setStatus("published");
        socialPost.setPublishedTime(LocalDateTime.now());
        socialPost.setLikeCount(0L);
        socialPost.setCommentCount(0L);
        socialPost.setShareCount(0L);
        socialPost.setFavoriteCount(0L);
        socialPost.setViewCount(0L);
        socialPost.setHotScore(BigDecimal.ZERO);
        
        // 插入数据库
        int result = socialPostMapper.insert(socialPost);
        if (result != 1) {
            throw new BizException("创建动态失败", CommonErrorCode.SYSTEM_ERROR);
        }
        
        log.info("创建社交动态成功，动态ID：{}", socialPost.getId());
        return socialPost;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheInvalidate(name = CacheConstant.SOCIAL_POST_INFO_CACHE, key = "#request.postId")
    @CacheInvalidate(name = CacheConstant.SOCIAL_USER_POSTS_CACHE, key = "#request.authorId")
    @CacheInvalidate(name = CacheConstant.SOCIAL_HOT_POSTS_CACHE, key = "'hot*'")
    public SocialPost updatePost(SocialPostUpdateRequest request) {
        log.info("更新社交动态开始，请求参数：{}", request);
        
        // 查询现有动态
        SocialPost existingPost = findById(request.getPostId());
        if (existingPost == null) {
            throw new BizException("动态不存在", CommonErrorCode.RESOURCE_NOT_FOUND);
        }
        
        // 权限检查
        if (!existingPost.getAuthorId().equals(request.getOperatorUserId())) {
            throw new BizException("无权限修改此动态", CommonErrorCode.ACCESS_DENIED);
        }
        
        // 更新字段
        if (StringUtils.hasText(request.getContent())) {
            existingPost.setContent(request.getContent());
        }
        // SocialPostUpdateRequest没有title字段，只能更新content
        /* if (StringUtils.hasText(request.getTitle())) {
            existingPost.setTitle(request.getTitle());
        } */
        if (request.getMediaUrls() != null) {
            existingPost.setMediaUrls(request.getMediaUrls());
        }
        if (StringUtils.hasText(request.getLocation())) {
            existingPost.setLocation(request.getLocation());
        }
        if (request.getVisibility() != null) {
            existingPost.setVisibility(request.getVisibility().toString());
        }
        
        existingPost.setGmtModified(new Date());
        
        // 更新数据库
        int result = socialPostMapper.updateById(existingPost);
        if (result != 1) {
            throw new BizException("更新动态失败", CommonErrorCode.SYSTEM_ERROR);
        }
        
        log.info("更新社交动态成功，动态ID：{}", existingPost.getId());
        return existingPost;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheInvalidate(name = CacheConstant.SOCIAL_POST_INFO_CACHE, key = "#postId")
    @CacheInvalidate(name = CacheConstant.SOCIAL_USER_POSTS_CACHE, key = "#operatorUserId")
    @CacheInvalidate(name = CacheConstant.SOCIAL_HOT_POSTS_CACHE, key = "'hot*'")
    public SocialPost publishPost(Long postId, Long operatorUserId, Integer version) {
        log.info("发布社交动态，动态ID：{}，操作用户：{}", postId, operatorUserId);
        
        SocialPost post = findById(postId);
        if (post == null) {
            throw new BizException("动态不存在", CommonErrorCode.RESOURCE_NOT_FOUND);
        }
        
        if (!post.getAuthorId().equals(operatorUserId)) {
            throw new BizException("无权限发布此动态", CommonErrorCode.ACCESS_DENIED);
        }
        
        post.setStatus("published");
        post.setPublishedTime(LocalDateTime.now());
        post.setGmtModified(new Date());
        
        int result = socialPostMapper.updateById(post);
        if (result != 1) {
            throw new BizException("发布动态失败", CommonErrorCode.SYSTEM_ERROR);
        }
        
        return post;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheInvalidate(name = CacheConstant.SOCIAL_POST_INFO_CACHE, key = "#postId")
    @CacheInvalidate(name = CacheConstant.SOCIAL_USER_POSTS_CACHE, key = "#operatorUserId")
    @CacheInvalidate(name = CacheConstant.SOCIAL_HOT_POSTS_CACHE, key = "'hot*'")
    public boolean deletePost(Long postId, Long operatorUserId, Integer version) {
        log.info("删除社交动态，动态ID：{}，操作用户：{}", postId, operatorUserId);
        
        SocialPost post = findById(postId);
        if (post == null) {
            throw new BizException("动态不存在", CommonErrorCode.RESOURCE_NOT_FOUND);
        }
        
        if (!post.getAuthorId().equals(operatorUserId)) {
            throw new BizException("无权限删除此动态", CommonErrorCode.ACCESS_DENIED);
        }
        
        // 软删除
        post.setDeleted(1);
        post.setGmtModified(new Date());
        
        int result = socialPostMapper.updateById(post);
        return result > 0;
    }

    @Override
    @Cached(name = CacheConstant.SOCIAL_POST_INFO_CACHE, 
            key = "#postId", 
            expire = CacheConstant.SOCIAL_POST_CACHE_EXPIRE,
            cacheType = CacheType.BOTH)
    public SocialPost findById(Long postId) {
        if (postId == null) {
            return null;
        }
        
        return socialPostMapper.selectOne(
            new LambdaQueryWrapper<SocialPost>()
                .eq(SocialPost::getId, postId)
                .eq(SocialPost::getDeleted, false)
        );
    }

    @Override
    @Cached(name = CacheConstant.SOCIAL_POST_INFO_CACHE, 
            key = "'detail:' + #postId + ':' + #viewerUserId", 
            expire = CacheConstant.SOCIAL_POST_CACHE_EXPIRE,
            cacheType = CacheType.BOTH)
    public SocialPostInfo getPostDetail(Long postId, Long viewerUserId) {
        log.debug("查询动态详情，动态ID：{}，查看者：{}", postId, viewerUserId);
        
        SocialPost post = findById(postId);
        if (post == null) {
            return null;
        }
        
        // 异步增加浏览量（避免影响查询性能）
        if (viewerUserId != null && !post.getAuthorId().equals(viewerUserId)) {
            incrementViewCount(postId, viewerUserId);
        }
        
        return SocialPostConvertor.INSTANCE.entityToInfo(post);
    }

    @Override
    @Cached(name = CacheConstant.SOCIAL_USER_POSTS_CACHE, 
            key = "'user:' + #authorId + ':' + #pageNum + ':' + #pageSize", 
            expire = CacheConstant.SOCIAL_USER_POSTS_CACHE_EXPIRE,
            cacheType = CacheType.BOTH)
    public IPage<BasicSocialPostInfo> getUserPosts(Long authorId, Long viewerUserId, Integer pageNum, Integer pageSize) {
        log.debug("查询用户动态列表，作者ID：{}，页码：{}，大小：{}", authorId, pageNum, pageSize);
        
        Page<SocialPost> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SocialPost> wrapper = new LambdaQueryWrapper<SocialPost>()
            .eq(SocialPost::getAuthorId, authorId)
            .eq(SocialPost::getDeleted, false)
            .eq(SocialPost::getStatus, "published")
            .orderByDesc(SocialPost::getPublishedTime);
        
        IPage<SocialPost> result = socialPostMapper.selectPage(page, wrapper);
        
        // 转换为BasicSocialPostInfo
        List<BasicSocialPostInfo> infoList = result.getRecords()
            .stream()
            .map(SocialPostConvertor.INSTANCE::entityToBasicInfo)
            .collect(Collectors.toList());
        
        Page<BasicSocialPostInfo> infoPage = new Page<>(pageNum, pageSize, result.getTotal());
        infoPage.setRecords(infoList);
        
        return infoPage;
    }

    @Override
    @Cached(name = CacheConstant.SOCIAL_HOT_POSTS_CACHE, 
            key = "'hot:' + #pageNum + ':' + #pageSize", 
            expire = CacheConstant.SOCIAL_POST_CACHE_EXPIRE,
            cacheType = CacheType.BOTH)
    public IPage<BasicSocialPostInfo> getHotPosts(Integer pageNum, Integer pageSize) {
        log.debug("查询热门动态，页码：{}，大小：{}", pageNum, pageSize);
        
        Page<SocialPost> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SocialPost> wrapper = new LambdaQueryWrapper<SocialPost>()
            .eq(SocialPost::getDeleted, false)
            .eq(SocialPost::getStatus, "published")
            .orderByDesc(SocialPost::getHotScore)
            .orderByDesc(SocialPost::getPublishedTime);
        
        IPage<SocialPost> result = socialPostMapper.selectPage(page, wrapper);
        
        List<BasicSocialPostInfo> infoList = result.getRecords()
            .stream()
            .map(SocialPostConvertor.INSTANCE::entityToBasicInfo)
            .collect(Collectors.toList());
        
        Page<BasicSocialPostInfo> infoPage = new Page<>(pageNum, pageSize, result.getTotal());
        infoPage.setRecords(infoList);
        
        return infoPage;
    }

    @Override
    public IPage<BasicSocialPostInfo> queryPosts(SocialPostQueryRequest request) {
        // TODO: 实现复杂查询逻辑
        return getLatestPosts(request.getPageNum(), request.getPageSize());
    }

    @Override
    public IPage<BasicSocialPostInfo> getFollowingTimeline(Long userId, Integer pageNum, Integer pageSize) {
        // TODO: 实现关注时间线逻辑，需要与follow模块集成
        return getHotPosts(pageNum, pageSize);
    }

    @Override
    public IPage<BasicSocialPostInfo> getRecommendedPosts(Long userId, Integer pageNum, Integer pageSize) {
        // TODO: 实现推荐算法逻辑
        return getHotPosts(pageNum, pageSize);
    }

    @Override
    public IPage<BasicSocialPostInfo> getLatestPosts(Integer pageNum, Integer pageSize) {
        Page<SocialPost> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SocialPost> wrapper = new LambdaQueryWrapper<SocialPost>()
            .eq(SocialPost::getDeleted, false)
            .eq(SocialPost::getStatus, "published")
            .orderByDesc(SocialPost::getPublishedTime);
        
        IPage<SocialPost> result = socialPostMapper.selectPage(page, wrapper);
        
        List<BasicSocialPostInfo> infoList = result.getRecords()
            .stream()
            .map(SocialPostConvertor.INSTANCE::entityToBasicInfo)
            .collect(Collectors.toList());
        
        Page<BasicSocialPostInfo> infoPage = new Page<>(pageNum, pageSize, result.getTotal());
        infoPage.setRecords(infoList);
        
        return infoPage;
    }

    @Override
    public IPage<BasicSocialPostInfo> searchPostsByTopic(String topic, Integer pageNum, Integer pageSize) {
        // TODO: 实现话题搜索逻辑
        return getLatestPosts(pageNum, pageSize);
    }

    @Override
    public IPage<BasicSocialPostInfo> searchPostsByKeyword(String keyword, Integer pageNum, Integer pageSize) {
        // TODO: 实现关键词搜索逻辑
        return getLatestPosts(pageNum, pageSize);
    }

    @Override
    public IPage<BasicSocialPostInfo> searchPostsByLocation(BigDecimal latitude, BigDecimal longitude, 
                                                           Double radiusKm, Integer pageNum, Integer pageSize) {
        // TODO: 实现位置搜索逻辑
        return getLatestPosts(pageNum, pageSize);
    }

    @Override
    public List<SocialPostInfo> batchGetPosts(List<Long> postIds, Long viewerUserId) {
        if (postIds == null || postIds.isEmpty()) {
            return List.of();
        }
        
        List<SocialPost> posts = socialPostMapper.selectBatchIds(postIds);
        return posts.stream()
            .map(SocialPostConvertor.INSTANCE::entityToInfo)
            .collect(Collectors.toList());
    }

    @Override
    public IPage<BasicSocialPostInfo> getDraftPosts(Long authorId, Integer pageNum, Integer pageSize) {
        Page<SocialPost> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SocialPost> wrapper = new LambdaQueryWrapper<SocialPost>()
            .eq(SocialPost::getAuthorId, authorId)
            .eq(SocialPost::getDeleted, false)
            .eq(SocialPost::getStatus, "draft")
            .orderByDesc(SocialPost::getGmtCreate);
        
        IPage<SocialPost> result = socialPostMapper.selectPage(page, wrapper);
        
        List<BasicSocialPostInfo> infoList = result.getRecords()
            .stream()
            .map(SocialPostConvertor.INSTANCE::entityToBasicInfo)
            .collect(Collectors.toList());
        
        Page<BasicSocialPostInfo> infoPage = new Page<>(pageNum, pageSize, result.getTotal());
        infoPage.setRecords(infoList);
        
        return infoPage;
    }

    @Override
    @CacheInvalidate(name = CacheConstant.SOCIAL_POST_INFO_CACHE, key = "#postId")
    @CacheInvalidate(name = CacheConstant.SOCIAL_HOT_POSTS_CACHE, key = "'hot*'")
    public void updateHotScore(Long postId) {
        log.debug("更新动态热度分数，动态ID：{}", postId);
        // 计算热度分数 - 简单算法
        SocialPost post = findById(postId);
        if (post != null) {
            BigDecimal hotScore = calculateHotScore(post);
            socialPostMapper.updateHotScore(postId, hotScore);
        }
    }
    
    /**
     * 计算热度分数
     */
    private BigDecimal calculateHotScore(SocialPost post) {
        long likeCount = post.getLikeCount() != null ? post.getLikeCount() : 0;
        long commentCount = post.getCommentCount() != null ? post.getCommentCount() : 0;
        long shareCount = post.getShareCount() != null ? post.getShareCount() : 0;
        long favoriteCount = post.getFavoriteCount() != null ? post.getFavoriteCount() : 0;
        
        // 简单的热度计算公式
        double score = likeCount * SocialConstant.HOT_SCORE_LIKE_WEIGHT + 
                      commentCount * SocialConstant.HOT_SCORE_COMMENT_WEIGHT + 
                      shareCount * SocialConstant.HOT_SCORE_SHARE_WEIGHT + 
                      favoriteCount * SocialConstant.HOT_SCORE_FAVORITE_WEIGHT;
        
        return BigDecimal.valueOf(score);
    }

    @Override
    @CacheInvalidate(name = CacheConstant.SOCIAL_POST_INFO_CACHE, key = "'*'")
    @CacheInvalidate(name = CacheConstant.SOCIAL_USER_POSTS_CACHE, key = "#userId")
    public void batchUpdateUserInfo(Long userId, String username, String nickname, String avatar, Boolean verified) {
        log.info("批量更新用户信息，用户ID：{}", userId);
        
        LambdaUpdateWrapper<SocialPost> wrapper = new LambdaUpdateWrapper<SocialPost>()
            .eq(SocialPost::getAuthorId, userId)
            .set(SocialPost::getAuthorUsername, username)
            .set(SocialPost::getAuthorNickname, nickname)
            .set(SocialPost::getAuthorAvatar, avatar)
            .set(SocialPost::getAuthorVerified, verified)
            .set(SocialPost::getGmtModified, new Date());
        
        socialPostMapper.update(null, wrapper);
    }

    @Override
    public void incrementViewCount(Long postId, Long viewerUserId) {
        socialPostMapper.incrementViewCount(postId);
    }

    @Override
    @CacheInvalidate(name = CacheConstant.SOCIAL_POST_INFO_CACHE, key = "#postId")
    public void incrementLikeCount(Long postId, Integer increment) {
        socialPostMapper.incrementLikeCount(postId, increment);
    }

    @Override
    @CacheInvalidate(name = CacheConstant.SOCIAL_POST_INFO_CACHE, key = "#postId")
    public void incrementCommentCount(Long postId, Integer increment) {
        socialPostMapper.incrementCommentCount(postId, increment);
    }

    @Override
    @CacheInvalidate(name = CacheConstant.SOCIAL_POST_INFO_CACHE, key = "#postId")
    public void incrementShareCount(Long postId, Integer increment) {
        socialPostMapper.incrementShareCount(postId, increment);
    }

    @Override
    @CacheInvalidate(name = CacheConstant.SOCIAL_POST_INFO_CACHE, key = "#postId")
    public void incrementFavoriteCount(Long postId, Integer increment) {
        socialPostMapper.incrementFavoriteCount(postId, increment);
    }

    @Override
    public boolean checkPublishPermission(Long userId, String postType) {
        // TODO: 实现发布权限检查逻辑
        return true;
    }

    @Override
    @Cached(name = CacheConstant.SOCIAL_STATISTICS_CACHE, 
            key = "'today:' + #userId", 
            expire = CacheConstant.SOCIAL_STATISTICS_CACHE_EXPIRE,
            cacheType = CacheType.BOTH)
    public Long getUserTodayPostCount(Long userId) {
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime endOfDay = startOfDay.plusDays(1);
        
        LambdaQueryWrapper<SocialPost> wrapper = new LambdaQueryWrapper<SocialPost>()
            .eq(SocialPost::getAuthorId, userId)
            .eq(SocialPost::getDeleted, false)
            .between(SocialPost::getGmtCreate, java.sql.Timestamp.valueOf(startOfDay), java.sql.Timestamp.valueOf(endOfDay));
        
        return socialPostMapper.selectCount(wrapper);
    }

    @Override
    public Long countUserPosts(Long authorId, String status, LocalDateTime startTime, LocalDateTime endTime) {
        LambdaQueryWrapper<SocialPost> wrapper = new LambdaQueryWrapper<SocialPost>()
            .eq(SocialPost::getAuthorId, authorId)
            .eq(SocialPost::getDeleted, false);
        
        if (StringUtils.hasText(status)) {
            wrapper.eq(SocialPost::getStatus, status);
        }
        
        if (startTime != null && endTime != null) {
            wrapper.between(SocialPost::getGmtCreate, java.sql.Timestamp.valueOf(startTime), java.sql.Timestamp.valueOf(endTime));
        }
        
        return socialPostMapper.selectCount(wrapper);
    }
} 