package com.gig.collide.social.facade;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gig.collide.api.social.constant.SocialPostStatus;
import com.gig.collide.api.social.request.SocialPostCreateRequest;
import com.gig.collide.api.social.request.SocialPostQueryRequest;
import com.gig.collide.api.social.response.SocialPostResponse;
import com.gig.collide.api.social.response.data.SocialPostInfo;
import com.gig.collide.api.social.service.SocialFacadeService;
import com.gig.collide.api.user.service.UserFacadeService;
import com.gig.collide.api.user.request.UserQueryRequest;
import com.gig.collide.api.user.request.condition.UserIdQueryCondition;
import com.gig.collide.api.user.response.UserQueryResponse;
import com.gig.collide.api.user.response.data.UserInfo;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.social.domain.entity.SocialPost;
import com.gig.collide.social.domain.service.TimelineService;
import com.gig.collide.social.infrastructure.mapper.SocialPostMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import com.baomidou.mybatisplus.core.metadata.IPage;

/**
 * 社交服务 Facade 实现
 * 
 * <p>完全去连表化设计，通过应用层逻辑处理复杂关系</p>
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Slf4j
@Component
@DubboService(version = "1.0.0")
@RequiredArgsConstructor
public class SocialFacadeServiceImpl implements SocialFacadeService {

    private final SocialPostMapper socialPostMapper;
    private final TimelineService timelineService;

    @DubboReference(version = "1.0.0", check = false)
    private UserFacadeService userFacadeService;

    @Override
    public SocialPostResponse publishPost(SocialPostCreateRequest createRequest) {
        try {
            log.info("发布社交动态，作者ID：{}，动态类型：{}", createRequest.getAuthorId(), createRequest.getPostType());

            // 1. 获取作者信息（无连表，调用用户服务）
            UserInfo authorInfo = getUserInfo(createRequest.getAuthorId());
            if (authorInfo == null) {
                return SocialPostResponse.error("USER_NOT_FOUND", "用户不存在");
            }

            // 2. 创建动态实体（包含冗余的作者信息）
            SocialPost socialPost = new SocialPost();
            socialPost.setPostType(createRequest.getPostType());
            socialPost.setContent(createRequest.getContent());
            socialPost.setMediaUrls(createRequest.getMediaUrls());
            socialPost.setLocation(createRequest.getLocation());
            socialPost.setLongitude(createRequest.getLongitude());
            socialPost.setLatitude(createRequest.getLatitude());
            socialPost.setTopics(createRequest.getTopics());
            socialPost.setMentionedUserIds(createRequest.getMentionedUserIds());
            socialPost.setStatus(SocialPostStatus.PUBLISHED);
            socialPost.setVisibility(createRequest.getVisibility());
            socialPost.setAllowComments(createRequest.getAllowComments());
            socialPost.setAllowShares(createRequest.getAllowShares());

            // 设置冗余的作者信息
            socialPost.setAuthorId(authorInfo.getUserId());
            socialPost.setAuthorUsername(authorInfo.getUsername());
            socialPost.setAuthorNickname(authorInfo.getNickName());
            socialPost.setAuthorAvatar(authorInfo.getProfilePhotoUrl());
            socialPost.setAuthorVerified(authorInfo.isBlogger()); // 直接使用Boolean值

            // 初始化统计信息
            socialPost.setLikeCount(0L);
            socialPost.setCommentCount(0L);
            socialPost.setShareCount(0L);
            socialPost.setViewCount(0L);
            socialPost.setFavoriteCount(0L);
            socialPost.setHotScore(0.0);
            socialPost.setPublishedTime(LocalDateTime.now());

            // 3. 保存动态（单表插入）
            int result = socialPostMapper.insert(socialPost);
            if (result > 0) {
                log.info("动态发布成功，动态ID：{}", socialPost.getId());
                return SocialPostResponse.success(socialPost.getId(), "动态发布成功");
            } else {
                return SocialPostResponse.error("PUBLISH_FAILED", "动态发布失败");
            }

        } catch (Exception e) {
            log.error("发布社交动态失败，作者ID：{}", createRequest.getAuthorId(), e);
            return SocialPostResponse.error("SYSTEM_ERROR", "系统异常，发布失败");
        }
    }

    @Override
    public SocialPostResponse updatePost(Long postId, SocialPostCreateRequest updateRequest) {
        try {
            log.info("更新社交动态，动态ID：{}，作者ID：{}", postId, updateRequest.getAuthorId());

            // 1. 查询原动态（单表查询）
            SocialPost existingPost = socialPostMapper.selectById(postId);
            if (existingPost == null) {
                return SocialPostResponse.error("POST_NOT_FOUND", "动态不存在");
            }

            // 2. 权限验证
            if (!existingPost.getAuthorId().equals(updateRequest.getAuthorId())) {
                return SocialPostResponse.error("ACCESS_DENIED", "无权限编辑此动态");
            }

            // 3. 更新动态内容
            existingPost.setContent(updateRequest.getContent());
            existingPost.setMediaUrls(updateRequest.getMediaUrls());
            existingPost.setLocation(updateRequest.getLocation());
            existingPost.setLongitude(updateRequest.getLongitude());
            existingPost.setLatitude(updateRequest.getLatitude());
            existingPost.setTopics(updateRequest.getTopics());
            existingPost.setMentionedUserIds(updateRequest.getMentionedUserIds());
            existingPost.setVisibility(updateRequest.getVisibility());
            existingPost.setAllowComments(updateRequest.getAllowComments());
            existingPost.setAllowShares(updateRequest.getAllowShares());

            // 4. 保存更新（单表更新）
            int result = socialPostMapper.updateById(existingPost);
            if (result > 0) {
                log.info("动态更新成功，动态ID：{}", postId);
                return SocialPostResponse.success(postId, "动态更新成功");
            } else {
                return SocialPostResponse.error("UPDATE_FAILED", "动态更新失败");
            }

        } catch (Exception e) {
            log.error("更新社交动态失败，动态ID：{}", postId, e);
            return SocialPostResponse.error("SYSTEM_ERROR", "系统异常，更新失败");
        }
    }

    @Override
    public SocialPostResponse deletePost(Long postId, Long userId) {
        try {
            log.info("删除社交动态，动态ID：{}，用户ID：{}", postId, userId);

            // 1. 查询动态（单表查询）
            SocialPost post = socialPostMapper.selectById(postId);
            if (post == null) {
                return SocialPostResponse.error("POST_NOT_FOUND", "动态不存在");
            }

            // 2. 权限验证
            if (!post.getAuthorId().equals(userId)) {
                return SocialPostResponse.error("ACCESS_DENIED", "无权限删除此动态");
            }

            // 3. 逻辑删除（单表更新）
            post.setStatus(SocialPostStatus.DELETED);
            int result = socialPostMapper.updateById(post);
            
            if (result > 0) {
                log.info("动态删除成功，动态ID：{}", postId);
                return SocialPostResponse.success(postId, "动态删除成功");
            } else {
                return SocialPostResponse.error("DELETE_FAILED", "动态删除失败");
            }

        } catch (Exception e) {
            log.error("删除社交动态失败，动态ID：{}", postId, e);
            return SocialPostResponse.error("SYSTEM_ERROR", "系统异常，删除失败");
        }
    }

    @Override
    public PageResponse<SocialPostInfo> pageQueryPosts(SocialPostQueryRequest queryRequest) {
        try {
            log.info("分页查询社交动态，查询类型：{}", queryRequest.getQueryType());

            PageResponse<SocialPost> postPage;

            // 根据查询类型选择不同的查询策略（所有查询都是单表操作）
            switch (queryRequest.getQueryType()) {
                case "personal" -> {
                    // 个人动态
                    postPage = timelineService.getUserTimeline(
                        queryRequest.getAuthorId(), 
                        queryRequest.getCurrentPage(), 
                        queryRequest.getPageSize());
                }
                case "following" -> {
                    // 关注动态（通过时间线服务处理）
                    postPage = timelineService.getFollowingFeed(
                        queryRequest.getCurrentUserId(), 
                        queryRequest.getCurrentPage(), 
                        queryRequest.getPageSize());
                }
                case "hot" -> {
                    // 热门动态
                    postPage = timelineService.getRecommendedFeed(
                        queryRequest.getCurrentUserId(),
                        queryRequest.getCurrentPage(), 
                        queryRequest.getPageSize(),
                        "day");
                }
                case "nearby" -> {
                    // 附近动态
                    postPage = queryNearbyPosts(queryRequest);
                }
                default -> {
                    // 默认查询公开动态
                    postPage = queryPublicPosts(queryRequest);
                }
            }

            // 转换为响应DTO
            List<SocialPostInfo> postInfos = convertToPostInfos(postPage.getDatas());
            
            return PageResponse.of(postInfos, postPage.getTotal(), postPage.getPageSize(), postPage.getCurrentPage());

        } catch (Exception e) {
            log.error("分页查询社交动态失败", e);
            return PageResponse.empty();
        }
    }

    @Override
    public SocialPostInfo queryPostDetail(Long postId, Long currentUserId) {
        try {
            log.info("查询动态详情，动态ID：{}，当前用户ID：{}", postId, currentUserId);

            // 1. 查询动态（单表查询）
            SocialPost post = socialPostMapper.selectById(postId);
            if (post == null) {
                return null;
            }

            // 2. 权限验证（应用层处理）
            if (!hasViewPermission(post, currentUserId)) {
                log.warn("用户{}无权限查看动态{}", currentUserId, postId);
                return null;
            }

            // 3. 增加浏览数（异步处理，不影响查询性能）
            incrementViewCount(postId, currentUserId);

            // 4. 转换为响应DTO
            return convertToPostInfo(post, currentUserId);

        } catch (Exception e) {
            log.error("查询动态详情失败，动态ID：{}", postId, e);
            return null;
        }
    }

    @Override
    public PageResponse<SocialPostInfo> getUserTimeline(Long userId, Integer currentPage, Integer pageSize) {
        PageResponse<SocialPost> postPage = timelineService.getUserTimeline(userId, currentPage, pageSize);
        List<SocialPostInfo> postInfos = convertToPostInfos(postPage.getDatas());
        return PageResponse.of(postInfos, postPage.getTotal(), postPage.getPageSize(), postPage.getCurrentPage());
    }

    @Override
    public PageResponse<SocialPostInfo> getFollowingFeed(Long userId, Integer currentPage, Integer pageSize) {
        PageResponse<SocialPost> postPage = timelineService.getFollowingFeed(userId, currentPage, pageSize);
        List<SocialPostInfo> postInfos = convertToPostInfos(postPage.getDatas());
        return PageResponse.of(postInfos, postPage.getTotal(), postPage.getPageSize(), postPage.getCurrentPage());
    }

    @Override
    public PageResponse<SocialPostInfo> getHotPosts(Integer currentPage, Integer pageSize, String timeRange) {
        PageResponse<SocialPost> postPage = timelineService.getRecommendedFeed(null, currentPage, pageSize, timeRange);
        List<SocialPostInfo> postInfos = convertToPostInfos(postPage.getDatas());
        return PageResponse.of(postInfos, postPage.getTotal(), postPage.getPageSize(), postPage.getCurrentPage());
    }

    @Override
    public PageResponse<SocialPostInfo> getNearbyPosts(Double longitude, Double latitude, Double radius, 
                                                       Integer currentPage, Integer pageSize) {
        try {
            Page<SocialPost> page = new Page<>(currentPage, pageSize);
            IPage<SocialPost> resultPage = socialPostMapper.selectNearbyPostsPage(page, longitude, latitude, radius, SocialPostStatus.PUBLISHED);
            
            List<SocialPostInfo> postInfos = convertToPostInfos(resultPage.getRecords());
            return PageResponse.of(postInfos, (int) resultPage.getTotal(), (int) resultPage.getSize(), (int) resultPage.getCurrent());
        } catch (Exception e) {
            log.error("查询附近动态失败", e);
            return PageResponse.empty();
        }
    }

    @Override
    public PageResponse<SocialPostInfo> searchPosts(String keyword, Integer currentPage, Integer pageSize) {
        try {
            Page<SocialPost> page = new Page<>(currentPage, pageSize);
            IPage<SocialPost> resultPage = socialPostMapper.searchPostsPage(page, keyword, SocialPostStatus.PUBLISHED, null);
            
            List<SocialPostInfo> postInfos = convertToPostInfos(resultPage.getRecords());
            return PageResponse.of(postInfos, (int) resultPage.getTotal(), (int) resultPage.getSize(), (int) resultPage.getCurrent());
        } catch (Exception e) {
            log.error("搜索动态失败，关键词：{}", keyword, e);
            return PageResponse.empty();
        }
    }

    @Override
    public SocialPostResponse likePost(Long postId, Long userId, Boolean isLike) {
        try {
            log.info("动态点赞操作，动态ID：{}，用户ID：{}，操作：{}", postId, userId, isLike ? "点赞" : "取消点赞");

            // 更新点赞数（单表操作）
            int increment = isLike ? 1 : -1;
            int result = socialPostMapper.incrementLikeCount(postId, increment);
            
            if (result > 0) {
                // 这里可以异步记录点赞关系到 t_social_post_interaction 表
                // 或者发送消息队列事件
                
                String message = isLike ? "点赞成功" : "取消点赞成功";
                return SocialPostResponse.success(postId, message);
            } else {
                return SocialPostResponse.error("OPERATION_FAILED", "操作失败");
            }

        } catch (Exception e) {
            log.error("动态点赞操作失败，动态ID：{}", postId, e);
            return SocialPostResponse.error("SYSTEM_ERROR", "系统异常");
        }
    }

    @Override
    public SocialPostResponse sharePost(Long postId, Long userId, String comment) {
        // TODO: 实现转发功能
        log.info("转发动态功能待实现，动态ID：{}，用户ID：{}", postId, userId);
        return SocialPostResponse.error("NOT_IMPLEMENTED", "转发功能待实现");
    }

    @Override
    public SocialPostResponse reportPost(Long postId, Long userId, String reason) {
        // TODO: 实现举报功能
        log.info("举报动态功能待实现，动态ID：{}，用户ID：{}", postId, userId);
        return SocialPostResponse.error("NOT_IMPLEMENTED", "举报功能待实现");
    }

    @Override
    public Long countUserPosts(Long userId) {
        try {
            return socialPostMapper.countUserPosts(userId, SocialPostStatus.PUBLISHED);
        } catch (Exception e) {
            log.error("统计用户动态数量失败，用户ID：{}", userId, e);
            return 0L;
        }
    }

    @Override
    public void incrementViewCount(Long postId, Long userId) {
        try {
            socialPostMapper.incrementViewCount(postId);
        } catch (Exception e) {
            log.error("增加浏览数失败，动态ID：{}", postId, e);
        }
    }

    @Override
    public Double calculateHotScore(Long postId) {
        // TODO: 实现热度分数计算
        log.debug("热度分数计算功能待实现，动态ID：{}", postId);
        return 0.0;
    }

    // ===== 私有辅助方法 =====

    /**
     * 获取用户信息（调用用户服务）
     */
    private UserInfo getUserInfo(Long userId) {
        try {
            UserQueryRequest userQueryRequest = new UserQueryRequest();
            userQueryRequest.setUserIdQueryCondition(new UserIdQueryCondition());
            userQueryRequest.getUserIdQueryCondition().setUserId(userId);
            
            UserQueryResponse<UserInfo> response = userFacadeService.query(userQueryRequest);
            return response != null ? response.getData() : null;
        } catch (Exception e) {
            log.error("查询用户信息失败，用户ID：{}", userId, e);
            return null;
        }
    }

    /**
     * 检查查看权限
     */
    private boolean hasViewPermission(SocialPost post, Long currentUserId) {
        // 公开动态：所有人可见
        if (post.getVisibility() == 0) {
            return true;
        }
        
        // 作者本人：总是可见
        if (post.getAuthorId().equals(currentUserId)) {
            return true;
        }
        
        // 仅关注者可见：需要检查关注关系
        if (post.getVisibility() == 1) {
            List<Long> followingUserIds = timelineService.getFollowingUserIds(currentUserId);
            return followingUserIds.contains(post.getAuthorId());
        }
        
        // 仅自己可见：只有作者可见
        return false;
    }

    /**
     * 查询附近动态
     */
    private PageResponse<SocialPost> queryNearbyPosts(SocialPostQueryRequest queryRequest) {
        try {
            Page<SocialPost> page = new Page<>(queryRequest.getCurrentPage(), queryRequest.getPageSize());
            IPage<SocialPost> resultPage = socialPostMapper.selectNearbyPostsPage(
                page, queryRequest.getLongitude(), queryRequest.getLatitude(), 
                queryRequest.getRadius(), SocialPostStatus.PUBLISHED);
            
            return PageResponse.of(resultPage.getRecords(), (int) resultPage.getTotal(), (int) resultPage.getSize(), (int) resultPage.getCurrent());
        } catch (Exception e) {
            log.error("查询附近动态失败", e);
            return PageResponse.empty();
        }
    }

    /**
     * 查询公开动态
     */
    private PageResponse<SocialPost> queryPublicPosts(SocialPostQueryRequest queryRequest) {
        try {
            Page<SocialPost> page = new Page<>(queryRequest.getCurrentPage(), queryRequest.getPageSize());
            
            if (StringUtils.hasText(queryRequest.getKeyword())) {
                IPage<SocialPost> resultPage = socialPostMapper.searchPostsPage(page, queryRequest.getKeyword(), 
                    SocialPostStatus.PUBLISHED, queryRequest.getPostType());
                return PageResponse.of(resultPage.getRecords(), (int) resultPage.getTotal(), (int) resultPage.getSize(), (int) resultPage.getCurrent());
            } else {
                IPage<SocialPost> resultPage = socialPostMapper.selectHotPostsPage(page, SocialPostStatus.PUBLISHED, 
                    null, null, 0.0);
                return PageResponse.of(resultPage.getRecords(), (int) resultPage.getTotal(), (int) resultPage.getSize(), (int) resultPage.getCurrent());
            }
        } catch (Exception e) {
            log.error("查询公开动态失败", e);
            return PageResponse.empty();
        }
    }

    /**
     * 转换为响应DTO列表
     */
    private List<SocialPostInfo> convertToPostInfos(List<SocialPost> posts) {
        if (CollectionUtils.isEmpty(posts)) {
            return Collections.emptyList();
        }
        
        return posts.stream()
                .map(post -> convertToPostInfo(post, null))
                .collect(Collectors.toList());
    }

    /**
     * 转换为响应DTO
     */
    private SocialPostInfo convertToPostInfo(SocialPost post, Long currentUserId) {
        SocialPostInfo postInfo = new SocialPostInfo();
        
        // 基础信息
        postInfo.setPostId(post.getId());
        postInfo.setPostType(post.getPostType());
        postInfo.setContent(post.getContent());
        postInfo.setMediaUrls(post.getMediaUrls());
        postInfo.setLocation(post.getLocation());
        postInfo.setLongitude(post.getLongitude());
        postInfo.setLatitude(post.getLatitude());
        postInfo.setTopics(post.getTopics());
        postInfo.setMentionedUserIds(post.getMentionedUserIds());
        postInfo.setStatus(post.getStatus());
        postInfo.setVisibility(post.getVisibility());
        postInfo.setAllowComments(post.getAllowComments());
        postInfo.setAllowShares(post.getAllowShares());
        
        // 作者信息（冗余字段）
        postInfo.setAuthorId(post.getAuthorId());
        postInfo.setAuthorUsername(post.getAuthorUsername());
        postInfo.setAuthorNickname(post.getAuthorNickname());
        postInfo.setAuthorAvatar(post.getAuthorAvatar());
        postInfo.setAuthorVerified(post.getAuthorVerified());
        
        // 统计信息
        postInfo.setLikeCount(post.getLikeCount());
        postInfo.setCommentCount(post.getCommentCount());
        postInfo.setShareCount(post.getShareCount());
        postInfo.setViewCount(post.getViewCount());
        postInfo.setFavoriteCount(post.getFavoriteCount());
        
        // 时间信息
        postInfo.setCreateTime(post.getCreatedTime());
        postInfo.setUpdateTime(post.getUpdatedTime());
        postInfo.setPublishTime(post.getPublishedTime());
        
        // 权限信息
        if (currentUserId != null) {
            postInfo.setCanEdit(post.getAuthorId().equals(currentUserId));
            postInfo.setCanDelete(post.getAuthorId().equals(currentUserId));
            
            // TODO: 查询当前用户的互动状态（点赞、收藏等）
            // 这里可以批量查询互动关系，避免N+1查询
            postInfo.setCurrentUserLiked(false);
            postInfo.setCurrentUserFavorited(false);
            postInfo.setCurrentUserFollowed(false);
        }
        
        postInfo.setHotScore(post.getHotScore());
        
        return postInfo;
    }
} 