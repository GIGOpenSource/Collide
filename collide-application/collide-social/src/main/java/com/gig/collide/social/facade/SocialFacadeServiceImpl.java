package com.gig.collide.social.facade;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.gig.collide.api.social.SocialFacadeService;
import com.gig.collide.api.social.request.SocialDynamicCreateRequest;
import com.gig.collide.api.social.request.SocialDynamicQueryRequest;
import com.gig.collide.api.social.request.SocialDynamicUpdateRequest;
import com.gig.collide.api.social.response.SocialDynamicResponse;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.social.domain.entity.SocialDynamic;
import com.gig.collide.social.domain.service.SocialDynamicService;
import com.gig.collide.web.vo.Result;
import com.gig.collide.api.user.UserFacadeService;
import com.gig.collide.api.like.LikeFacadeService;
import com.gig.collide.api.like.request.LikeQueryRequest;
import com.gig.collide.api.like.response.LikeResponse;
import com.gig.collide.api.comment.CommentFacadeService;
import com.gig.collide.api.comment.response.CommentResponse;
import com.gig.collide.api.social.response.SocialInteractionResponse;

import com.alicp.jetcache.anno.Cached;
import com.alicp.jetcache.anno.CacheInvalidate;
import com.alicp.jetcache.anno.CacheUpdate;
import com.alicp.jetcache.anno.CacheType;
import com.gig.collide.social.infrastructure.cache.SocialCacheConstant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;
import java.util.concurrent.TimeUnit;

/**
 * 社交动态门面服务实现类 - 缓存增强版
 * 对齐content模块设计风格，处理API层和业务层转换
 * 包含缓存功能、错误处理、数据转换
 * 
 * @author GIG Team
 * @version 2.0.0 (缓存增强版)
 * @since 2024-01-16
 */
@Slf4j
@DubboService(version = "1.0.0")
@RequiredArgsConstructor
public class SocialFacadeServiceImpl implements SocialFacadeService {

    private final SocialDynamicService socialDynamicService;
    
    // =================== 跨模块服务注入 ===================
    @Autowired
    private UserFacadeService userFacadeService;
    
    @Autowired
    private LikeFacadeService likeFacadeService;
    
    @Autowired
    private CommentFacadeService commentFacadeService;

    // =================== 动态管理 ===================

    @Override
    @CacheInvalidate(name = SocialCacheConstant.DYNAMIC_LIST_CACHE)
    @CacheInvalidate(name = SocialCacheConstant.DYNAMIC_LATEST_CACHE)
    @CacheInvalidate(name = SocialCacheConstant.DYNAMIC_USER_CACHE)
    public Result<Void> createDynamic(SocialDynamicCreateRequest request) {
        try {
            log.info("创建动态请求: {}", request.getContent());
            
            // =================== 跨模块验证 ===================
            
            // 1. 验证用户存在性
            Result<UserResponse> userResult = userFacadeService.getUserById(request.getUserId());
            if (!userResult.getSuccess() || userResult.getData() == null) {
                return Result.error("USER_NOT_FOUND", "用户不存在");
            }
            
            SocialDynamic dynamic = new SocialDynamic();
            BeanUtils.copyProperties(request, dynamic);
            
            SocialDynamic savedDynamic = socialDynamicService.createDynamic(dynamic);
            log.info("动态创建成功: ID={}", savedDynamic.getId());
            
            return Result.success(null);
        } catch (Exception e) {
            log.error("创建动态失败", e);
            return Result.error("DYNAMIC_CREATE_ERROR", "创建动态失败: " + e.getMessage());
        }
    }

    @Override
    @CacheUpdate(name = SocialCacheConstant.DYNAMIC_DETAIL_CACHE, key = SocialCacheConstant.DYNAMIC_DETAIL_KEY, 
                value = "T(com.gig.collide.web.vo.Result).success(#result)")
    @CacheInvalidate(name = SocialCacheConstant.DYNAMIC_LIST_CACHE)
    @CacheInvalidate(name = SocialCacheConstant.DYNAMIC_USER_CACHE)
    public Result<SocialDynamicResponse> updateDynamic(SocialDynamicUpdateRequest request) {
        try {
            log.info("更新动态请求: ID={}", request.getId());
            
            // 只允许更新内容字段
            SocialDynamic existingDynamic = socialDynamicService.getDynamicById(request.getId());
            if (existingDynamic == null) {
                return Result.error("DYNAMIC_NOT_FOUND", "动态不存在");
            }
            
            // 验证权限（只能更新自己的动态）
            if (!existingDynamic.getUserId().equals(request.getUserId())) {
                return Result.error("NO_PERMISSION", "只能修改自己的动态");
            }
            
            // 只更新内容字段
            existingDynamic.setContent(request.getContent());
            SocialDynamic updatedDynamic = socialDynamicService.updateDynamic(existingDynamic);
            
            SocialDynamicResponse response = convertToResponse(updatedDynamic);
            log.info("动态更新成功: ID={}", updatedDynamic.getId());
            
            return Result.success(response);
        } catch (Exception e) {
            log.error("更新动态失败", e);
            return Result.error("DYNAMIC_UPDATE_ERROR", "更新动态失败: " + e.getMessage());
        }
    }

    @Override
    @CacheInvalidate(name = SocialCacheConstant.DYNAMIC_DETAIL_CACHE, key = SocialCacheConstant.DYNAMIC_DETAIL_KEY)
    @CacheInvalidate(name = SocialCacheConstant.DYNAMIC_LIST_CACHE)
    @CacheInvalidate(name = SocialCacheConstant.DYNAMIC_LATEST_CACHE)
    @CacheInvalidate(name = SocialCacheConstant.DYNAMIC_USER_CACHE)
    @CacheInvalidate(name = SocialCacheConstant.DYNAMIC_HOT_CACHE)
    public Result<Void> deleteDynamic(Long dynamicId, Long operatorId) {
        try {
            log.info("删除动态: ID={}, 操作人={}", dynamicId, operatorId);
            
            SocialDynamic dynamic = socialDynamicService.getDynamicById(dynamicId);
            if (dynamic == null) {
                return Result.error("DYNAMIC_NOT_FOUND", "动态不存在");
            }
            
            // 验证权限（只能删除自己的动态）
            if (!dynamic.getUserId().equals(operatorId)) {
                return Result.error("NO_PERMISSION", "只能删除自己的动态");
            }
            
            socialDynamicService.deleteDynamic(dynamicId);
            log.info("动态删除成功: ID={}", dynamicId);
            
            return Result.success(null);
        } catch (Exception e) {
            log.error("删除动态失败", e);
            return Result.error("DYNAMIC_DELETE_ERROR", "删除动态失败: " + e.getMessage());
        }
    }

    @Override
    @Cached(name = SocialCacheConstant.DYNAMIC_DETAIL_CACHE, key = SocialCacheConstant.DYNAMIC_DETAIL_KEY, 
            expire = SocialCacheConstant.DYNAMIC_DETAIL_EXPIRE, timeUnit = TimeUnit.MINUTES, cacheType = CacheType.BOTH)
    public Result<SocialDynamicResponse> getDynamicById(Long dynamicId, Boolean includeDeleted) {
        try {
            log.debug("查询动态详情: ID={}", dynamicId);
            
            SocialDynamic dynamic = socialDynamicService.getDynamicById(dynamicId);
            if (dynamic == null) {
                return Result.error("DYNAMIC_NOT_FOUND", "动态不存在");
            }
            
            // 检查是否已删除
            if (!includeDeleted && "deleted".equals(dynamic.getStatus())) {
                return Result.error("DYNAMIC_DELETED", "动态已删除");
            }
            
            SocialDynamicResponse response = convertToResponse(dynamic);
            
            // =================== 跨模块数据增强 ===================
            // 增强用户信息
            enrichUserInfo(response);
            // 增强统计信息
            enrichStatistics(response);
            
            return Result.success(response);
        } catch (Exception e) {
            log.error("查询动态失败", e);
            return Result.error("DYNAMIC_QUERY_ERROR", "查询动态失败: " + e.getMessage());
        }
    }

    // =================== 动态查询 ===================

    @Override
    @Cached(name = SocialCacheConstant.DYNAMIC_LIST_CACHE, key = SocialCacheConstant.DYNAMIC_LIST_KEY, 
            expire = SocialCacheConstant.DYNAMIC_LIST_EXPIRE, timeUnit = TimeUnit.MINUTES, cacheType = CacheType.BOTH)
    public PageResponse<SocialDynamicResponse> queryDynamics(SocialDynamicQueryRequest request) {
        try {
            log.debug("分页查询动态: 页码={}, 大小={}", request.getCurrentPage(), request.getPageSize());
            
            IPage<SocialDynamic> page = socialDynamicService.queryDynamics(
                request.getCurrentPage(), 
                request.getPageSize(),
                request.getUserId(),
                request.getDynamicType(),
                request.getStatus(),
                request.getKeyword(),
                request.getMinLikeCount(),
                request.getSortBy(),
                request.getSortDirection()
            );
            return convertToPageResponse(page);
        } catch (Exception e) {
            log.error("分页查询动态失败", e);
            return createErrorPageResponse();
        }
    }

    @Override
    @Cached(name = SocialCacheConstant.DYNAMIC_LATEST_CACHE, key = SocialCacheConstant.DYNAMIC_LATEST_KEY, 
            expire = SocialCacheConstant.DYNAMIC_LATEST_EXPIRE, timeUnit = TimeUnit.MINUTES, cacheType = CacheType.BOTH)
    public PageResponse<SocialDynamicResponse> getLatestDynamics(Integer currentPage, Integer pageSize, String dynamicType) {
        try {
            log.debug("查询最新动态: 页码={}, 大小={}, 类型={}", currentPage, pageSize, dynamicType);
            
            IPage<SocialDynamic> page = socialDynamicService.queryDynamics(
                currentPage, 
                pageSize,
                null,  // userId - 查询所有用户
                dynamicType,
                "normal",  // status - 只查询正常状态
                null,  // keyword
                null,  // minLikeCount
                "create_time",  // sortBy - 按创建时间排序
                "desc"  // sortDirection - 降序（最新的在前）
            );
            return convertToPageResponse(page);
        } catch (Exception e) {
            log.error("查询最新动态失败", e);
            return createErrorPageResponse();
        }
    }

    @Override
    @Cached(name = SocialCacheConstant.DYNAMIC_USER_CACHE, key = SocialCacheConstant.DYNAMIC_USER_KEY, 
            expire = SocialCacheConstant.DYNAMIC_USER_EXPIRE, timeUnit = TimeUnit.MINUTES, cacheType = CacheType.BOTH)
    public PageResponse<SocialDynamicResponse> getUserDynamics(Long userId, Integer currentPage, Integer pageSize, String dynamicType) {
        try {
            log.debug("查询用户动态: 用户={}, 页码={}, 大小={}", userId, currentPage, pageSize);
            
            IPage<SocialDynamic> page = socialDynamicService.queryDynamics(
                currentPage, 
                pageSize,
                userId,  // userId - 查询指定用户的动态
                dynamicType,
                "normal",  // status - 只查询正常状态
                null,  // keyword
                null,  // minLikeCount
                "create_time",  // sortBy - 按创建时间排序
                "desc"  // sortDirection - 降序（最新的在前）
            );
            return convertToPageResponse(page);
        } catch (Exception e) {
            log.error("查询用户动态失败", e);
            return createErrorPageResponse();
        }
    }

    // =================== 互动功能 ===================

    @Override
    @CacheInvalidate(name = SocialCacheConstant.DYNAMIC_STATISTICS_CACHE, key = SocialCacheConstant.DYNAMIC_STATISTICS_KEY)
    @CacheInvalidate(name = SocialCacheConstant.DYNAMIC_LIKES_CACHE)
    @CacheInvalidate(name = SocialCacheConstant.USER_LIKE_STATUS_CACHE)
    @CacheInvalidate(name = SocialCacheConstant.USER_INTERACTIONS_CACHE)
    public Result<Void> likeDynamic(Long dynamicId, Long userId) {
        try {
            log.info("点赞动态: 动态={}, 用户={}", dynamicId, userId);
            
            socialDynamicService.likeDynamic(dynamicId, userId);
            return Result.success(null);
        } catch (Exception e) {
            log.error("点赞动态失败", e);
            return Result.error("DYNAMIC_LIKE_ERROR", "点赞动态失败: " + e.getMessage());
        }
    }

    @Override
    @CacheInvalidate(name = SocialCacheConstant.DYNAMIC_STATISTICS_CACHE, key = SocialCacheConstant.DYNAMIC_STATISTICS_KEY)
    @CacheInvalidate(name = SocialCacheConstant.DYNAMIC_LIKES_CACHE)
    @CacheInvalidate(name = SocialCacheConstant.USER_LIKE_STATUS_CACHE)
    @CacheInvalidate(name = SocialCacheConstant.USER_INTERACTIONS_CACHE)
    public Result<Void> unlikeDynamic(Long dynamicId, Long userId) {
        try {
            log.info("取消点赞: 动态={}, 用户={}", dynamicId, userId);
            
            socialDynamicService.unlikeDynamic(dynamicId, userId);
            return Result.success(null);
        } catch (Exception e) {
            log.error("取消点赞失败", e);
            return Result.error("DYNAMIC_UNLIKE_ERROR", "取消点赞失败: " + e.getMessage());
        }
    }

    @Override
    @CacheInvalidate(name = SocialCacheConstant.DYNAMIC_STATISTICS_CACHE, key = SocialCacheConstant.DYNAMIC_STATISTICS_KEY)
    @CacheInvalidate(name = SocialCacheConstant.DYNAMIC_COMMENTS_CACHE)
    @CacheInvalidate(name = SocialCacheConstant.USER_INTERACTIONS_CACHE)
    public Result<Void> commentDynamic(Long dynamicId, Long userId, String content) {
        try {
            log.info("评论动态: 动态={}, 用户={}", dynamicId, userId);
            
            socialDynamicService.commentDynamic(dynamicId, userId, content);
            return Result.success(null);
        } catch (Exception e) {
            log.error("评论动态失败", e);
            return Result.error("DYNAMIC_COMMENT_ERROR", "评论动态失败: " + e.getMessage());
        }
    }

    @Override
    @Cached(name = SocialCacheConstant.DYNAMIC_LIKES_CACHE, key = SocialCacheConstant.DYNAMIC_LIKES_KEY, 
            expire = SocialCacheConstant.DYNAMIC_LIKES_EXPIRE, timeUnit = TimeUnit.MINUTES, cacheType = CacheType.BOTH)
    public PageResponse<Object> getDynamicLikes(Long dynamicId, Integer currentPage, Integer pageSize) {
        try {
            log.debug("查询动态点赞记录: 动态={}", dynamicId);
            
            // TODO: 实现点赞记录查询逻辑
            PageResponse<Object> response = new PageResponse<>();
            response.setDatas(Collections.emptyList());
            response.setTotal(0);
            response.setCurrentPage(currentPage);
            response.setPageSize(pageSize);
            response.setSuccess(true);
            
            return response;
        } catch (Exception e) {
            log.error("查询点赞记录失败", e);
            return createErrorObjectPageResponse();
        }
    }

    @Override
    @Cached(name = SocialCacheConstant.DYNAMIC_COMMENTS_CACHE, key = SocialCacheConstant.DYNAMIC_COMMENTS_KEY, 
            expire = SocialCacheConstant.DYNAMIC_COMMENTS_EXPIRE, timeUnit = TimeUnit.MINUTES, cacheType = CacheType.BOTH)
    public PageResponse<Object> getDynamicComments(Long dynamicId, Integer currentPage, Integer pageSize) {
        try {
            log.debug("查询动态评论记录: 动态={}", dynamicId);
            
            // TODO: 实现评论记录查询逻辑
            PageResponse<Object> response = new PageResponse<>();
            response.setDatas(Collections.emptyList());
            response.setTotal(0);
            response.setCurrentPage(currentPage);
            response.setPageSize(pageSize);
            response.setSuccess(true);
            
            return response;
        } catch (Exception e) {
            log.error("查询评论记录失败", e);
            return createErrorObjectPageResponse();
        }
    }

    // =================== 统计功能 ===================

    @Override
    @Cached(name = SocialCacheConstant.DYNAMIC_STATISTICS_CACHE, key = SocialCacheConstant.DYNAMIC_STATISTICS_KEY, 
            expire = SocialCacheConstant.DYNAMIC_STATISTICS_EXPIRE, timeUnit = TimeUnit.MINUTES, cacheType = CacheType.BOTH)
    public Result<Object> getDynamicStatistics(Long dynamicId) {
        try {
            log.debug("查询动态统计: 动态={}", dynamicId);
            
            // TODO: 实现统计信息查询逻辑
            return Result.success(Collections.emptyMap());
        } catch (Exception e) {
            log.error("查询动态统计失败", e);
            return Result.error("STATISTICS_QUERY_ERROR", "查询统计失败: " + e.getMessage());
        }
    }

    // =================== 聚合功能 ===================

    @Override
    @Cached(name = SocialCacheConstant.USER_INTERACTIONS_CACHE, key = SocialCacheConstant.USER_INTERACTIONS_KEY, 
            expire = SocialCacheConstant.USER_INTERACTIONS_EXPIRE, timeUnit = TimeUnit.MINUTES, cacheType = CacheType.BOTH)
    public PageResponse<Object> getUserInteractions(Long userId, Integer currentPage, Integer pageSize) {
        try {
            log.debug("查询用户互动记录: 用户={}, 页码={}, 大小={}", userId, currentPage, pageSize);

            // 1. 查询用户点赞别人的记录 (LIKE_GIVE)
            List<SocialInteractionResponse> likeGiveList = getLikeGiveInteractions(userId);
            List<SocialInteractionResponse> allInteractions = new ArrayList<>(likeGiveList);
            log.debug("用户点赞别人记录数: {}", likeGiveList.size());
            
            // 2. 查询被别人点赞的记录 (LIKE_RECEIVE)
            List<SocialInteractionResponse> likeReceiveList = getLikeReceiveInteractions(userId);
            allInteractions.addAll(likeReceiveList);
            log.debug("被别人点赞记录数: {}", likeReceiveList.size());
            
            // 3. 查询用户评论别人的记录 (COMMENT_GIVE)
            List<SocialInteractionResponse> commentGiveList = getCommentGiveInteractions(userId);
            allInteractions.addAll(commentGiveList);
            log.debug("用户评论别人记录数: {}", commentGiveList.size());
            
            // 4. 查询被别人评论的记录 (COMMENT_RECEIVE)
            List<SocialInteractionResponse> commentReceiveList = getCommentReceiveInteractions(userId);
            allInteractions.addAll(commentReceiveList);
            log.debug("被别人评论记录数: {}", commentReceiveList.size());
            
            // 5. 按时间排序合并结果（最新的在前）
            allInteractions.sort(Comparator.comparing(SocialInteractionResponse::getInteractionTime).reversed());
            
            // 6. 分页处理
            int total = allInteractions.size();
            int offset = (currentPage - 1) * pageSize;
            int end = Math.min(offset + pageSize, total);
            
            List<SocialInteractionResponse> pagedData;
            if (offset >= total) {
                pagedData = Collections.emptyList();
            } else {
                pagedData = allInteractions.subList(offset, end);
            }
            
            PageResponse<Object> response = new PageResponse<>();
            response.setDatas(new ArrayList<>(pagedData));
            response.setTotal(total);
            response.setCurrentPage(currentPage);
            response.setPageSize(pageSize);
            response.setTotalPage((int) Math.ceil((double) total / pageSize));
            response.setSuccess(true);
            
            log.debug("用户互动记录查询完成: 用户={}, 总记录数={}, 当前页记录数={}", userId, total, pagedData.size());
            return response;
        } catch (Exception e) {
            log.error("查询用户互动记录失败", e);
            return createErrorObjectPageResponse();
        }
    }

    // =================== 互动记录查询方法 ===================

    /**
     * 获取用户点赞别人的记录 (LIKE_GIVE)
     */
    private List<SocialInteractionResponse> getLikeGiveInteractions(Long userId) {
        try {
            LikeQueryRequest likeQuery = new LikeQueryRequest();
            likeQuery.setUserId(userId);
            likeQuery.setLikeType("DYNAMIC");
            likeQuery.setStatus("active");
            likeQuery.setCurrentPage(1);
            likeQuery.setPageSize(100); // 获取前100条记录
            likeQuery.setOrderBy("create_time");
            likeQuery.setOrderDirection("DESC");
            
            Result<PageResponse<LikeResponse>> likeResult = likeFacadeService.queryLikes(likeQuery);
            if (!likeResult.getSuccess() || likeResult.getData() == null) {
                log.warn("查询用户点赞记录失败: userId={}", userId);
                return Collections.emptyList();
            }
            
            List<LikeResponse> likes = likeResult.getData().getDatas();
            return likes.stream().map(like -> {
                SocialInteractionResponse interaction = new SocialInteractionResponse();
                interaction.setInteractionId(like.getId());
                interaction.setInteractionType("LIKE_GIVE");
                interaction.setInteractionTime(like.getCreateTime());
                interaction.setDynamicId(like.getTargetId());
                interaction.setInteractionUserId(userId);
                interaction.setInteractionUserName(like.getUserNickname());
                interaction.setInteractionUserAvatar(like.getUserAvatar());
                interaction.setDynamicAuthorId(like.getTargetAuthorId());
                interaction.setIsRead(true);
                return interaction;
            }).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("查询用户点赞记录失败", e);
            return Collections.emptyList();
        }
    }

    /**
     * 获取被别人点赞的记录 (LIKE_RECEIVE)
     */
    private List<SocialInteractionResponse> getLikeReceiveInteractions(Long userId) {
        try {
            LikeQueryRequest likeQuery = new LikeQueryRequest();
            likeQuery.setTargetAuthorId(userId);
            likeQuery.setLikeType("DYNAMIC");
            likeQuery.setStatus("active");
            likeQuery.setCurrentPage(1);
            likeQuery.setPageSize(100); // 获取前100条记录
            likeQuery.setOrderBy("create_time");
            likeQuery.setOrderDirection("DESC");
            
            Result<PageResponse<LikeResponse>> likeResult = likeFacadeService.queryLikes(likeQuery);
            if (!likeResult.getSuccess() || likeResult.getData() == null) {
                log.warn("查询被点赞记录失败: userId={}", userId);
                return Collections.emptyList();
            }
            
            List<LikeResponse> likes = likeResult.getData().getDatas();
            return likes.stream().map(like -> {
                SocialInteractionResponse interaction = new SocialInteractionResponse();
                interaction.setInteractionId(like.getId());
                interaction.setInteractionType("LIKE_RECEIVE");
                interaction.setInteractionTime(like.getCreateTime());
                interaction.setDynamicId(like.getTargetId());
                interaction.setDynamicAuthorId(userId);
                interaction.setInteractionUserId(like.getUserId());
                interaction.setInteractionUserName(like.getUserNickname());
                interaction.setInteractionUserAvatar(like.getUserAvatar());
                interaction.setIsRead(false); // 默认未读
                return interaction;
            }).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("查询被点赞记录失败", e);
            return Collections.emptyList();
        }
    }

    /**
     * 获取用户评论别人的记录 (COMMENT_GIVE)
     */
    private List<SocialInteractionResponse> getCommentGiveInteractions(Long userId) {
        try {
            Result<PageResponse<CommentResponse>> commentResult = commentFacadeService.getUserComments(
                userId, "DYNAMIC", "NORMAL", 1, 100);
            
            if (!commentResult.getSuccess() || commentResult.getData() == null) {
                log.warn("查询用户评论记录失败: userId={}", userId);
                return Collections.emptyList();
            }
            
            List<CommentResponse> comments = commentResult.getData().getDatas();
            return comments.stream().map(comment -> {
                SocialInteractionResponse interaction = new SocialInteractionResponse();
                interaction.setInteractionId(comment.getId());
                interaction.setInteractionType("COMMENT_GIVE");
                interaction.setInteractionTime(comment.getCreateTime());
                interaction.setDynamicId(comment.getTargetId());
                interaction.setInteractionUserId(userId);
                interaction.setInteractionUserName(comment.getUserNickname());
                interaction.setInteractionUserAvatar(comment.getUserAvatar());
                interaction.setCommentContent(comment.getContent());
                interaction.setIsRead(true);
                return interaction;
            }).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("查询用户评论记录失败", e);
            return Collections.emptyList();
        }
    }

    /**
     * 获取被别人评论的记录 (COMMENT_RECEIVE)
     */
    private List<SocialInteractionResponse> getCommentReceiveInteractions(Long userId) {
        try {
            Result<PageResponse<CommentResponse>> commentResult = commentFacadeService.getUserReplies(userId, 1, 100);
            
            if (!commentResult.getSuccess() || commentResult.getData() == null) {
                log.warn("查询被评论记录失败: userId={}", userId);
                return Collections.emptyList();
            }
            
            List<CommentResponse> comments = commentResult.getData().getDatas();
            return comments.stream().map(comment -> {
                SocialInteractionResponse interaction = new SocialInteractionResponse();
                interaction.setInteractionId(comment.getId());
                interaction.setInteractionType("COMMENT_RECEIVE");
                interaction.setInteractionTime(comment.getCreateTime());
                interaction.setDynamicId(comment.getTargetId());
                interaction.setDynamicAuthorId(userId);
                interaction.setInteractionUserId(comment.getUserId());
                interaction.setInteractionUserName(comment.getUserNickname());
                interaction.setInteractionUserAvatar(comment.getUserAvatar());
                interaction.setCommentContent(comment.getContent());
                interaction.setIsRead(false); // 默认未读
                return interaction;
            }).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("查询被评论记录失败", e);
            return Collections.emptyList();
        }
    }

    // =================== 私有方法 ===================

    /**
     * 转换为PageResponse
     */
    private PageResponse<SocialDynamicResponse> convertToPageResponse(IPage<SocialDynamic> page) {
        if (page == null) {
            return createEmptyPageResponse(1, 20);
        }
        
        if (CollectionUtils.isEmpty(page.getRecords())) {
            return createEmptyPageResponse((int) page.getCurrent(), (int) page.getSize());
        }

        List<SocialDynamicResponse> responses = page.getRecords().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        // 批量增强数据
        batchEnrichData(responses);

        PageResponse<SocialDynamicResponse> response = new PageResponse<>();
        response.setDatas(responses);
        response.setTotal((int) page.getTotal());
        response.setCurrentPage((int) page.getCurrent());
        response.setPageSize((int) page.getSize());
        response.setTotalPage((int) page.getPages());
        response.setSuccess(true);

        return response;
    }

    /**
     * 转换为响应对象
     */
    private SocialDynamicResponse convertToResponse(SocialDynamic dynamic) {
        if (dynamic == null) {
            return null;
        }
        
        SocialDynamicResponse response = new SocialDynamicResponse();
        BeanUtils.copyProperties(dynamic, response);
        return response;
    }

    /**
     * 批量增强数据
     */
    private void batchEnrichData(List<SocialDynamicResponse> responses) {
        if (CollectionUtils.isEmpty(responses)) {
            return;
        }

        responses.forEach(response -> {
            enrichUserInfo(response);
            enrichStatistics(response);
        });
    }

    /**
     * 增强用户信息
     */
    private void enrichUserInfo(SocialDynamicResponse response) {
        try {
            // TODO: 调用用户服务获取用户信息
        } catch (Exception e) {
            log.warn("增强用户信息失败: {}", e.getMessage());
        }
    }

    /**
     * 增强统计信息
     */
    private void enrichStatistics(SocialDynamicResponse response) {
        try {
            // TODO: 获取实时统计信息
        } catch (Exception e) {
            log.warn("增强统计信息失败: {}", e.getMessage());
        }
    }

    /**
     * 创建空的分页响应
     */
    private PageResponse<SocialDynamicResponse> createEmptyPageResponse(int currentPage, int pageSize) {
        PageResponse<SocialDynamicResponse> response = new PageResponse<>();
        response.setDatas(Collections.emptyList());
        response.setTotal(0);
        response.setCurrentPage(currentPage);
        response.setPageSize(pageSize);
        response.setTotalPage(0);
        response.setSuccess(true);
        return response;
    }

    /**
     * 创建错误的分页响应
     */
    private PageResponse<SocialDynamicResponse> createErrorPageResponse() {
        PageResponse<SocialDynamicResponse> response = new PageResponse<>();
        response.setDatas(Collections.emptyList());
        response.setTotal(0);
        response.setCurrentPage(1);
        response.setPageSize(20);
        response.setTotalPage(0);
        response.setSuccess(false);
        return response;
    }

    /**
     * 创建错误的Object分页响应
     */
    private PageResponse<Object> createErrorObjectPageResponse() {
        PageResponse<Object> response = new PageResponse<>();
        response.setDatas(Collections.emptyList());
        response.setTotal(0);
        response.setCurrentPage(1);
        response.setPageSize(20);
        response.setTotalPage(0);
        response.setSuccess(false);
        return response;
    }
} 