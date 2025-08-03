package com.gig.collide.social.facade;

import com.alicp.jetcache.anno.CacheInvalidate;
import com.alicp.jetcache.anno.CacheType;
import com.alicp.jetcache.anno.Cached;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.gig.collide.api.social.SocialContentFacadeService;
import com.gig.collide.api.social.request.ContentCreateRequest;
import com.gig.collide.api.social.request.ContentQueryRequest;
import com.gig.collide.api.social.request.ContentUpdateRequest;
import com.gig.collide.api.social.vo.ContentStatsVO;
import com.gig.collide.api.social.vo.ContentVO;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.social.domain.entity.SocialContent;
import com.gig.collide.social.domain.service.InteractionStatsService;
import com.gig.collide.social.domain.service.SocialContentService;
import com.gig.collide.social.domain.service.SocialInteractionService;
import com.gig.collide.social.infrastructure.cache.SocialCacheConstant;
import com.gig.collide.web.vo.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

// 用户服务相关导入
import com.gig.collide.api.user.UserProfileFacadeService;
import com.gig.collide.api.user.response.profile.UserProfileResponse;

// 标签服务相关导入
import com.gig.collide.api.tag.ContentTagFacadeService;
import com.gig.collide.api.tag.response.TagResponse;

import java.util.concurrent.TimeUnit;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 社交内容门面服务实现
 * 
 * @author GIG Team
 * @since 2024-01-16
 */
@Slf4j
@Service
@DubboService(version = "1.0.0")
@RequiredArgsConstructor
public class SocialContentFacadeServiceImpl implements SocialContentFacadeService {

    private final SocialContentService contentService;
    private final InteractionStatsService statsService;
    private final SocialInteractionService interactionService;
    
    @DubboReference(version = "1.0.0")
    private UserProfileFacadeService userProfileFacadeService;
    
    @DubboReference(version = "1.0.0")
    private ContentTagFacadeService contentTagFacadeService;

    @Override
    @CacheInvalidate(name = SocialCacheConstant.HOT_CONTENT_CACHE, key = "*")
    @CacheInvalidate(name = SocialCacheConstant.LATEST_CONTENT_CACHE, key = "*")
    @CacheInvalidate(name = SocialCacheConstant.USER_CONTENT_CACHE, key = "#request.userId + '_*'")
    @CacheInvalidate(name = SocialCacheConstant.USER_CONTENT_COUNT_CACHE, key = "#request.userId")
    public Result<Long> createContent(ContentCreateRequest request) {
        try {
            // DTO转Entity
            SocialContent content = new SocialContent();
            BeanUtils.copyProperties(request, content);
            
            Long contentId = contentService.createContent(content);
            if (contentId != null) {
                return Result.success("内容创建成功", contentId);
            }
            return Result.failure("内容创建失败");
        } catch (Exception e) {
            log.error("创建内容失败", e);
            return Result.failure("CONTENT_CREATE_ERROR", "创建内容失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Boolean> updateContent(ContentUpdateRequest request) {
        try {
            SocialContent content = new SocialContent();
            BeanUtils.copyProperties(request, content);
            content.setId(request.getContentId());
            
            boolean result = contentService.updateContent(content);
            if (result) {
                return Result.success("内容更新成功", true);
            }
            return Result.failure("内容更新失败");
        } catch (Exception e) {
            log.error("更新内容失败", e);
            return Result.failure("CONTENT_UPDATE_ERROR", "更新内容失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Boolean> deleteContent(Long contentId, Long userId) {
        try {
            boolean result = contentService.deleteContent(contentId, userId);
            if (result) {
                return Result.success("内容删除成功", true);
            }
            return Result.failure("内容删除失败");
        } catch (Exception e) {
            log.error("删除内容失败", e);
            return Result.failure("CONTENT_DELETE_ERROR", "删除内容失败: " + e.getMessage());
        }
    }

    @Override
    @Cached(
        name = SocialCacheConstant.CONTENT_DETAIL_CACHE,
        key = SocialCacheConstant.CONTENT_DETAIL_KEY,
        expire = SocialCacheConstant.CONTENT_DETAIL_CACHE_EXPIRE,
        timeUnit = TimeUnit.MINUTES,
        cacheType = CacheType.BOTH
    )
    public Result<ContentVO> getContentDetail(Long contentId, Long viewerUserId) {
        try {
            SocialContent content = contentService.getById(contentId);
            if (content == null) {
                return Result.failure("CONTENT_NOT_FOUND", "内容不存在");
            }

            // 检查访问权限
            if (viewerUserId != null) {
                boolean hasAccess = contentService.checkContentAccess(contentId, viewerUserId);
                if (!hasAccess) {
                    return Result.failure("ACCESS_DENIED", "无权访问该内容");
                }
            }

            // Entity转VO
            ContentVO contentVO = convertToContentVO(content);
            
            // 获取用户互动状态
            if (viewerUserId != null) {
                SocialInteractionService.UserInteractionStatus status = 
                    interactionService.getUserInteractionStatus(viewerUserId, contentId);
                contentVO.setIsLiked(status.isLiked());
                contentVO.setIsFavorited(status.isFavorited());
                // TODO: 检查是否已购买
                contentVO.setIsPurchased(false);
            }

            return Result.success(contentVO);
        } catch (Exception e) {
            log.error("获取内容详情失败", e);
            return Result.failure("CONTENT_DETAIL_ERROR", "获取内容详情失败: " + e.getMessage());
        }
    }

    @Override
    public PageResponse<ContentVO> queryContent(ContentQueryRequest request) {
        try {
            IPage<SocialContent> page;
            
            // 根据查询类型选择不同的查询方法
            if (request.getQueryType() != null) {
                switch (request.getQueryType()) {
                    case 1: // 最新
                        page = getLatestContentWithCache(request.getCurrentPage(), request.getPageSize());
                        break;
                    case 2: // 热门
                        page = getHotContentWithCache(request.getCurrentPage(), request.getPageSize());
                        break;
                    case 3: // 搜索
                        if (request.getKeyword() != null) {
                            page = contentService.searchContent(request.getKeyword(), request.getCurrentPage(), request.getPageSize());
                        } else {
                            return PageResponse.fail("INVALID_PARAM", "搜索关键词不能为空");
                        }
                        break;
                    default:
                        page = getLatestContentWithCache(request.getCurrentPage(), request.getPageSize());
                }
            } else if (request.getUserId() != null) {
                page = getUserContentWithCache(request.getUserId(), request.getCurrentPage(), request.getPageSize());
            } else if (request.getCategoryId() != null) {
                page = getCategoryContentWithCache(request.getCategoryId(), request.getCurrentPage(), request.getPageSize());
            } else {
                page = getLatestContentWithCache(request.getCurrentPage(), request.getPageSize());
            }

            // Entity转VO（批量优化）
            List<ContentVO> contentVOs = convertToContentVOsBatch(page.getRecords());

            // 批量获取用户互动状态
            if (request.getViewerUserId() != null && !contentVOs.isEmpty()) {
                List<Long> contentIds = contentVOs.stream().map(ContentVO::getId).collect(Collectors.toList());
                // 批量获取互动状态并填充到VO中
                fillBatchInteractionStatus(contentVOs, request.getViewerUserId(), contentIds);
            }

            return PageResponse.of(contentVOs, page.getTotal(), request.getPageSize(), request.getCurrentPage());
        } catch (Exception e) {
            log.error("查询内容失败", e);
            return PageResponse.fail("CONTENT_QUERY_ERROR", "查询内容失败: " + e.getMessage());
        }
    }

    @Override
    public Result<List<ContentVO>> getBatchContent(List<Long> contentIds, Long viewerUserId) {
        try {
            List<SocialContent> contents = contentService.getBatchByIds(contentIds);
            List<ContentVO> contentVOs = contents.stream()
                .map(this::convertToContentVO)
                .collect(Collectors.toList());

            return Result.success(contentVOs);
        } catch (Exception e) {
            log.error("批量获取内容失败", e);
            return Result.failure("BATCH_CONTENT_ERROR", "批量获取内容失败: " + e.getMessage());
        }
    }

    @Override
    @Cached(
        name = SocialCacheConstant.CONTENT_STATS_CACHE,
        key = SocialCacheConstant.CONTENT_STATS_KEY,
        expire = SocialCacheConstant.CONTENT_STATS_CACHE_EXPIRE,
        timeUnit = TimeUnit.MINUTES,
        cacheType = CacheType.BOTH
    )
    public Result<ContentStatsVO> getContentStats(Long contentId, Long viewerUserId) {
        try {
            InteractionStatsService.ContentStats stats = statsService.getContentStats(contentId);
            if (stats == null) {
                return Result.failure("CONTENT_NOT_FOUND", "内容不存在");
            }

            ContentStatsVO statsVO = new ContentStatsVO();
            statsVO.setContentId(contentId);
            BeanUtils.copyProperties(stats, statsVO);

            // 获取用户互动状态
            if (viewerUserId != null) {
                SocialInteractionService.UserInteractionStatus status = 
                    interactionService.getUserInteractionStatus(viewerUserId, contentId);
                statsVO.setIsLiked(status.isLiked());
                statsVO.setIsFavorited(status.isFavorited());
                statsVO.setIsCommented(status.isCommented());
            }

            return Result.success(statsVO);
        } catch (Exception e) {
            log.error("获取内容统计失败", e);
            return Result.failure("CONTENT_STATS_ERROR", "获取内容统计失败: " + e.getMessage());
        }
    }

    @Override
    public Result<List<ContentStatsVO>> getBatchContentStats(List<Long> contentIds, Long viewerUserId) {
        try {
            var statsMap = statsService.getContentStatsBatch(contentIds);
            
            List<ContentStatsVO> statsVOs = contentIds.stream()
                .map(contentId -> {
                    ContentStatsVO statsVO = new ContentStatsVO();
                    statsVO.setContentId(contentId);
                    
                    InteractionStatsService.ContentStats stats = statsMap.get(contentId);
                    if (stats != null) {
                        BeanUtils.copyProperties(stats, statsVO);
                    }
                    
                    return statsVO;
                })
                .collect(Collectors.toList());

            return Result.success(statsVOs);
        } catch (Exception e) {
            log.error("批量获取内容统计失败", e);
            return Result.failure("BATCH_STATS_ERROR", "批量获取内容统计失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Boolean> checkContentAccess(Long contentId, Long userId) {
        try {
            boolean hasAccess = contentService.checkContentAccess(contentId, userId);
            return Result.success(hasAccess);
        } catch (Exception e) {
            log.error("检查内容访问权限失败", e);
            return Result.failure("ACCESS_CHECK_ERROR", "检查访问权限失败: " + e.getMessage());
        }
    }

    @Override
    @Cached(
        name = SocialCacheConstant.USER_CONTENT_COUNT_CACHE,
        key = SocialCacheConstant.USER_CONTENT_COUNT_KEY,
        expire = SocialCacheConstant.USER_CONTENT_COUNT_CACHE_EXPIRE,
        timeUnit = TimeUnit.MINUTES,
        cacheType = CacheType.BOTH
    )
    public Result<Integer> getUserContentCount(Long userId) {
        try {
            int count = contentService.getUserContentCount(userId);
            return Result.success(count);
        } catch (Exception e) {
            log.error("获取用户内容数量失败", e);
            return Result.failure("USER_CONTENT_COUNT_ERROR", "获取用户内容数量失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Integer> getCategoryContentCount(Long categoryId) {
        try {
            int count = contentService.getCategoryContentCount(categoryId);
            return Result.success(count);
        } catch (Exception e) {
            log.error("获取分类内容数量失败", e);
            return Result.failure("CATEGORY_CONTENT_COUNT_ERROR", "获取分类内容数量失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Boolean> incrementViewCount(Long contentId, Long viewerUserId) {
        try {
            statsService.incrementViewCount(contentId);
            return Result.success(true);
        } catch (Exception e) {
            log.error("增加浏览数失败", e);
            return Result.failure("INCREMENT_VIEW_ERROR", "增加浏览数失败: " + e.getMessage());
        }
    }
    
    /**
     * 诊断内容统计数据一致性（调试用）
     */
    public Result<Boolean> diagnoseContentStats(Long contentId) {
        try {
            statsService.diagnoseContentStats(contentId);
            return Result.success("诊断完成，请查看日志", true);
        } catch (Exception e) {
            log.error("诊断内容统计失败", e);
            return Result.failure("DIAGNOSE_ERROR", "诊断失败: " + e.getMessage());
        }
    }
    
    /**
     * 修复内容统计数据（调试用）
     */
    public Result<Boolean> fixContentStats(Long contentId) {
        try {
            statsService.recalculateContentStats(contentId);
            return Result.success("统计数据修复完成", true);
        } catch (Exception e) {
            log.error("修复内容统计失败", e);
            return Result.failure("FIX_STATS_ERROR", "修复失败: " + e.getMessage());
        }
    }

    // ========== 缓存辅助方法 ==========

    @Cached(
        name = SocialCacheConstant.HOT_CONTENT_CACHE,
        key = SocialCacheConstant.HOT_CONTENT_KEY,
        expire = SocialCacheConstant.HOT_CONTENT_CACHE_EXPIRE,
        timeUnit = TimeUnit.MINUTES,
        cacheType = CacheType.BOTH
    )
    private IPage<SocialContent> getHotContentWithCache(int currentPage, int pageSize) {
        return contentService.getHotContent(currentPage, pageSize);
    }

    @Cached(
        name = SocialCacheConstant.LATEST_CONTENT_CACHE,
        key = SocialCacheConstant.LATEST_CONTENT_KEY,
        expire = SocialCacheConstant.LATEST_CONTENT_CACHE_EXPIRE,
        timeUnit = TimeUnit.MINUTES,
        cacheType = CacheType.BOTH
    )
    private IPage<SocialContent> getLatestContentWithCache(int currentPage, int pageSize) {
        return contentService.getLatestContent(currentPage, pageSize);
    }

    @Cached(
        name = SocialCacheConstant.USER_CONTENT_CACHE,
        key = SocialCacheConstant.USER_CONTENT_KEY,
        expire = SocialCacheConstant.USER_CONTENT_CACHE_EXPIRE,
        timeUnit = TimeUnit.MINUTES,
        cacheType = CacheType.BOTH
    )
    private IPage<SocialContent> getUserContentWithCache(Long userId, int currentPage, int pageSize) {
        return contentService.getByUserId(userId, currentPage, pageSize);
    }

    @Cached(
        name = SocialCacheConstant.CATEGORY_CONTENT_CACHE,
        key = SocialCacheConstant.CATEGORY_CONTENT_KEY,
        expire = SocialCacheConstant.CATEGORY_CONTENT_CACHE_EXPIRE,
        timeUnit = TimeUnit.MINUTES,
        cacheType = CacheType.BOTH
    )
    private IPage<SocialContent> getCategoryContentWithCache(Long categoryId, int currentPage, int pageSize) {
        return contentService.getByCategoryId(categoryId, currentPage, pageSize);
    }

    /**
     * Entity转VO
     */
    private ContentVO convertToContentVO(SocialContent content) {
        ContentVO contentVO = new ContentVO();
        BeanUtils.copyProperties(content, contentVO);
        
        // 获取作者信息
        try {
            Result<UserProfileResponse> userResult = userProfileFacadeService.getProfileByUserId(content.getUserId());
            if (userResult.getSuccess() && userResult.getData() != null) {
                UserProfileResponse profile = userResult.getData();
                contentVO.setAuthorName(profile.getNickname());
                contentVO.setAuthorAvatar(profile.getAvatar());
            } else {
                // 设置默认值
                contentVO.setAuthorName("未知用户");
                contentVO.setAuthorAvatar(null);
            }
        } catch (Exception e) {
            log.warn("获取用户信息失败, userId: {}", content.getUserId(), e);
            // 设置默认值
            contentVO.setAuthorName("未知用户");
            contentVO.setAuthorAvatar(null);
        }
        
        // 获取标签信息
        try {
            Result<List<TagResponse>> tagResult = contentTagFacadeService.getContentTags(content.getId());
            if (tagResult.getSuccess() && tagResult.getData() != null) {
                List<ContentVO.TagInfo> tagInfos = tagResult.getData().stream()
                    .map(tag -> {
                        ContentVO.TagInfo tagInfo = new ContentVO.TagInfo();
                        tagInfo.setId(tag.getId());
                        tagInfo.setTagName(tag.getTagName());
                        tagInfo.setTagIcon(tag.getTagIcon());
                        tagInfo.setWeight(tag.getWeight());
                        return tagInfo;
                    })
                    .collect(Collectors.toList());
                contentVO.setTags(tagInfos);
            } else {
                contentVO.setTags(List.of());
            }
        } catch (Exception e) {
            log.warn("获取内容标签失败, contentId: {}", content.getId(), e);
            contentVO.setTags(List.of());
        }
        
        return contentVO;
    }
    
    /**
     * 批量转换Entity到VO，优化用户信息查询
     */
    private List<ContentVO> convertToContentVOsBatch(List<SocialContent> contents) {
        if (contents == null || contents.isEmpty()) {
            return List.of();
        }
        
        // 收集所有需要查询的用户ID
        List<Long> userIds = contents.stream()
            .map(SocialContent::getUserId)
            .distinct()
            .collect(Collectors.toList());
        
        // 批量查询用户信息
        Map<Long, UserProfileResponse> userInfoMap = new HashMap<>();
        try {
            Result<List<UserProfileResponse>> batchResult = userProfileFacadeService.batchGetProfiles(userIds);
            if (batchResult.getSuccess() && batchResult.getData() != null) {
                userInfoMap = batchResult.getData().stream()
                    .collect(Collectors.toMap(UserProfileResponse::getUserId, profile -> profile));
            }
        } catch (Exception e) {
            log.warn("批量获取用户信息失败", e);
        }
        
        // 批量获取标签信息
        Map<Long, List<ContentVO.TagInfo>> contentTagsMap = batchGetContentTags(contents);
        
        // 转换VO并填充用户信息和标签信息
        final Map<Long, UserProfileResponse> finalUserInfoMap = userInfoMap;
        final Map<Long, List<ContentVO.TagInfo>> finalTagsMap = contentTagsMap;
        return contents.stream()
            .map(content -> {
                ContentVO contentVO = new ContentVO();
                BeanUtils.copyProperties(content, contentVO);
                
                // 填充作者信息
                UserProfileResponse profile = finalUserInfoMap.get(content.getUserId());
                if (profile != null) {
                    contentVO.setAuthorName(profile.getNickname());
                    contentVO.setAuthorAvatar(profile.getAvatar());
                } else {
                    contentVO.setAuthorName("未知用户");
                    contentVO.setAuthorAvatar(null);
                }
                
                // 填充标签信息
                List<ContentVO.TagInfo> tags = finalTagsMap.get(content.getId());
                contentVO.setTags(tags != null ? tags : List.of());
                
                return contentVO;
            })
            .collect(Collectors.toList());
    }
    
    /**
     * 批量填充用户互动状态
     */
    private void fillBatchInteractionStatus(List<ContentVO> contentVOs, Long viewerUserId, List<Long> contentIds) {
        try {
            // 这里调用互动服务的批量查询方法
            // 暂时保持原有的逐个查询方式，未来可以进一步优化
            for (ContentVO contentVO : contentVOs) {
                SocialInteractionService.UserInteractionStatus status = 
                    interactionService.getUserInteractionStatus(viewerUserId, contentVO.getId());
                contentVO.setIsLiked(status.isLiked());
                contentVO.setIsFavorited(status.isFavorited());
            }
        } catch (Exception e) {
            log.warn("批量获取互动状态失败", e);
        }
    }
    
    /**
     * 批量获取内容标签信息
     */
    private Map<Long, List<ContentVO.TagInfo>> batchGetContentTags(List<SocialContent> contents) {
        Map<Long, List<ContentVO.TagInfo>> contentTagsMap = new HashMap<>();
        
        try {
            // 暂时使用逐个查询的方式，未来可以考虑实现批量接口
            for (SocialContent content : contents) {
                Result<List<TagResponse>> tagResult = contentTagFacadeService.getContentTags(content.getId());
                if (tagResult.getSuccess() && tagResult.getData() != null) {
                    List<ContentVO.TagInfo> tagInfos = tagResult.getData().stream()
                        .map(tag -> {
                            ContentVO.TagInfo tagInfo = new ContentVO.TagInfo();
                            tagInfo.setId(tag.getId());
                            tagInfo.setTagName(tag.getTagName());
                            tagInfo.setTagIcon(tag.getTagIcon());
                            tagInfo.setWeight(tag.getWeight());
                            return tagInfo;
                        })
                        .collect(Collectors.toList());
                    contentTagsMap.put(content.getId(), tagInfos);
                } else {
                    contentTagsMap.put(content.getId(), List.of());
                }
            }
        } catch (Exception e) {
            log.warn("批量获取内容标签失败", e);
        }
        
        return contentTagsMap;
    }
}