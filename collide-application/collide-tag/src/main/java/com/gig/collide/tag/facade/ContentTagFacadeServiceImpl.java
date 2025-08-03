package com.gig.collide.tag.facade;

import com.alicp.jetcache.anno.Cached;
import com.alicp.jetcache.anno.CacheInvalidate;
import com.alicp.jetcache.anno.CacheType;
import org.apache.dubbo.config.annotation.DubboService;
import com.gig.collide.api.tag.ContentTagFacadeService;
import com.gig.collide.api.tag.request.ContentTagQueryRequest;
import com.gig.collide.api.tag.response.TagResponse;
import com.gig.collide.api.tag.response.ContentTagResponse;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.web.vo.Result;
import com.gig.collide.tag.domain.entity.Tag;
import com.gig.collide.tag.domain.entity.ContentTag;
import com.gig.collide.tag.domain.service.TagService;
import com.gig.collide.tag.domain.service.ContentTagService;
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
 * 内容标签门面服务实现
 * 
 * @author GIG Team
 * @version 1.0.0
 */
@Slf4j
@DubboService(version = "1.0.0")
@RequiredArgsConstructor
public class ContentTagFacadeServiceImpl implements ContentTagFacadeService {

    private final ContentTagService contentTagService;
    private final TagService tagService;
    private final UserTagService userTagService;

    // =================== 内容标签管理 ===================

    @Override
    @CacheInvalidate(name = TagCacheConstant.CONTENT_TAGS_CACHE, key = TagCacheConstant.CONTENT_TAGS_KEY)
    public Result<Void> addContentTag(Long contentId, Long tagId) {
        try {
            log.info("为内容添加标签: contentId={}, tagId={}", contentId, tagId);
            
            // 参数验证
            if (contentId == null || contentId <= 0) {
                return Result.error("CONTENT_TAG_ERROR", "内容ID无效");
            }
            
            if (tagId == null || tagId <= 0) {
                return Result.error("CONTENT_TAG_ERROR", "标签ID无效");
            }
            
            // 调用领域服务
            boolean success = contentTagService.addContentTag(contentId, tagId);
            
            if (success) {
                log.info("为内容添加标签成功: contentId={}, tagId={}", contentId, tagId);
                return Result.success();
            } else {
                return Result.error("CONTENT_TAG_ERROR", "添加内容标签失败");
            }
        } catch (Exception e) {
            log.error("为内容添加标签失败: contentId={}, tagId={}", contentId, tagId, e);
            return Result.error("CONTENT_TAG_ERROR", "添加内容标签失败: " + e.getMessage());
        }
    }

    @Override
    @CacheInvalidate(name = TagCacheConstant.CONTENT_TAGS_CACHE, key = TagCacheConstant.CONTENT_TAGS_KEY)
    public Result<Void> removeContentTag(Long contentId, Long tagId) {
        try {
            log.info("移除内容标签: contentId={}, tagId={}", contentId, tagId);
            
            // 参数验证
            if (contentId == null || contentId <= 0) {
                return Result.error("CONTENT_TAG_ERROR", "内容ID无效");
            }
            
            if (tagId == null || tagId <= 0) {
                return Result.error("CONTENT_TAG_ERROR", "标签ID无效");
            }
            
            // 调用领域服务
            boolean success = contentTagService.removeContentTag(contentId, tagId);
            
            if (success) {
                log.info("移除内容标签成功: contentId={}, tagId={}", contentId, tagId);
                return Result.success();
            } else {
                return Result.error("CONTENT_TAG_ERROR", "移除内容标签失败");
            }
        } catch (Exception e) {
            log.error("移除内容标签失败: contentId={}, tagId={}", contentId, tagId, e);
            return Result.error("CONTENT_TAG_ERROR", "移除内容标签失败: " + e.getMessage());
        }
    }

    @Override
    @CacheInvalidate(name = TagCacheConstant.CONTENT_TAGS_CACHE, key = TagCacheConstant.CONTENT_TAGS_KEY)
    public Result<List<Long>> batchAddContentTags(Long contentId, List<Long> tagIds) {
        try {
            log.info("批量为内容添加标签: contentId={}, tagIds={}", contentId, tagIds);
            
            // 参数验证
            if (contentId == null || contentId <= 0) {
                return Result.error("CONTENT_TAG_ERROR", "内容ID无效");
            }
            
            if (tagIds == null || tagIds.isEmpty()) {
                return Result.error("CONTENT_TAG_ERROR", "标签ID列表不能为空");
            }
            
            // 调用领域服务
            List<Long> successTagIds = contentTagService.batchAddContentTags(contentId, tagIds);
            
            log.info("批量为内容添加标签完成: contentId={}, success={}/{}", 
                    contentId, successTagIds.size(), tagIds.size());
            
            return Result.success(successTagIds);
        } catch (Exception e) {
            log.error("批量为内容添加标签失败: contentId={}, tagIds={}", contentId, tagIds, e);
            return Result.error("CONTENT_TAG_ERROR", "批量添加内容标签失败: " + e.getMessage());
        }
    }

    @Override
    @CacheInvalidate(name = TagCacheConstant.CONTENT_TAGS_CACHE, key = TagCacheConstant.CONTENT_TAGS_KEY)
    public Result<Void> batchRemoveContentTags(Long contentId, List<Long> tagIds) {
        try {
            log.info("批量移除内容标签: contentId={}, tagIds={}", contentId, tagIds);
            
            // 参数验证
            if (contentId == null || contentId <= 0) {
                return Result.error("CONTENT_TAG_ERROR", "内容ID无效");
            }
            
            if (tagIds == null || tagIds.isEmpty()) {
                return Result.error("CONTENT_TAG_ERROR", "标签ID列表不能为空");
            }
            
            // 调用领域服务
            boolean success = contentTagService.batchRemoveContentTags(contentId, tagIds);
            
            if (success) {
                log.info("批量移除内容标签成功: contentId={}, count={}", contentId, tagIds.size());
                return Result.success();
            } else {
                return Result.error("CONTENT_TAG_ERROR", "批量移除内容标签失败");
            }
        } catch (Exception e) {
            log.error("批量移除内容标签失败: contentId={}, tagIds={}", contentId, tagIds, e);
            return Result.error("CONTENT_TAG_ERROR", "批量移除内容标签失败: " + e.getMessage());
        }
    }

    @Override
    @CacheInvalidate(name = TagCacheConstant.CONTENT_TAGS_CACHE, key = TagCacheConstant.CONTENT_TAGS_KEY)
    public Result<Void> replaceContentTags(Long contentId, List<Long> tagIds) {
        try {
            log.info("替换内容标签: contentId={}, newTagIds={}", contentId, tagIds);
            
            // 参数验证
            if (contentId == null || contentId <= 0) {
                return Result.error("CONTENT_TAG_ERROR", "内容ID无效");
            }
            
            // 调用领域服务
            boolean success = contentTagService.replaceContentTags(contentId, tagIds);
            
            if (success) {
                log.info("替换内容标签成功: contentId={}", contentId);
                return Result.success();
            } else {
                return Result.error("CONTENT_TAG_ERROR", "替换内容标签失败");
            }
        } catch (Exception e) {
            log.error("替换内容标签失败: contentId={}, tagIds={}", contentId, tagIds, e);
            return Result.error("CONTENT_TAG_ERROR", "替换内容标签失败: " + e.getMessage());
        }
    }

    // =================== 内容标签查询 ===================

    @Override
    @Cached(name = TagCacheConstant.CONTENT_TAGS_CACHE,
            key = TagCacheConstant.CONTENT_TAGS_KEY,
            expire = TagCacheConstant.CONTENT_TAGS_CACHE_EXPIRE,
            timeUnit = TimeUnit.MINUTES,
            cacheType = CacheType.BOTH)
    public Result<List<TagResponse>> getContentTags(Long contentId) {
        try {
            log.debug("获取内容标签: contentId={}", contentId);
            
            // 参数验证
            if (contentId == null || contentId <= 0) {
                return Result.error("CONTENT_TAG_ERROR", "内容ID无效");
            }
            
            // 获取内容标签ID列表
            List<Long> tagIds = contentTagService.getContentTagIds(contentId);
            
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
            log.error("获取内容标签失败: contentId={}", contentId, e);
            return Result.error("CONTENT_TAG_ERROR", "获取内容标签失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Integer> getContentTagCount(Long contentId) {
        try {
            log.debug("获取内容标签数量: contentId={}", contentId);
            
            // 参数验证
            if (contentId == null || contentId <= 0) {
                return Result.error("CONTENT_TAG_ERROR", "内容ID无效");
            }
            
            // 调用领域服务
            Integer count = contentTagService.getContentTagCount(contentId);
            
            return Result.success(count);
        } catch (Exception e) {
            log.error("获取内容标签数量失败: contentId={}", contentId, e);
            return Result.error("CONTENT_TAG_ERROR", "获取内容标签数量失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Boolean> hasContentTag(Long contentId, Long tagId) {
        try {
            log.debug("检查内容是否包含标签: contentId={}, tagId={}", contentId, tagId);
            
            // 参数验证
            if (contentId == null || contentId <= 0 || tagId == null || tagId <= 0) {
                return Result.error("CONTENT_TAG_ERROR", "参数无效");
            }
            
            // 调用领域服务
            boolean hasTag = contentTagService.hasContentTag(contentId, tagId);
            
            return Result.success(hasTag);
        } catch (Exception e) {
            log.error("检查内容是否包含标签失败: contentId={}, tagId={}", contentId, tagId, e);
            return Result.error("CONTENT_TAG_ERROR", "检查内容标签失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Map<Long, Boolean>> batchCheckContentTags(Long contentId, List<Long> tagIds) {
        try {
            log.debug("批量检查内容标签: contentId={}, tagIds={}", contentId, tagIds);
            
            // 参数验证
            if (contentId == null || contentId <= 0) {
                return Result.error("CONTENT_TAG_ERROR", "内容ID无效");
            }
            
            if (tagIds == null || tagIds.isEmpty()) {
                return Result.success(new HashMap<>());
            }
            
            // 调用领域服务
            Map<Long, Boolean> result = contentTagService.batchCheckContentTags(contentId, tagIds);
            
            return Result.success(result);
        } catch (Exception e) {
            log.error("批量检查内容标签失败: contentId={}, tagIds={}", contentId, tagIds, e);
            return Result.error("CONTENT_TAG_ERROR", "检查内容标签失败: " + e.getMessage());
        }
    }

    // =================== 基于标签的内容查询 ===================

    @Override
    @Cached(name = TagCacheConstant.TAG_CONTENTS_CACHE,
            key = TagCacheConstant.TAG_CONTENTS_KEY,
            expire = TagCacheConstant.TAG_CONTENTS_CACHE_EXPIRE,
            timeUnit = TimeUnit.MINUTES,
            cacheType = CacheType.BOTH)
    public Result<PageResponse<Long>> getContentsByTag(Long tagId, ContentTagQueryRequest request) {
        try {
            log.debug("根据标签查询内容: tagId={}", tagId);
            
            // 参数验证
            if (tagId == null || tagId <= 0) {
                return Result.error("CONTENT_TAG_ERROR", "标签ID无效");
            }
            
            if (request == null) {
                request = new ContentTagQueryRequest();
                request.setCurrentPage(1);
                request.setPageSize(20);
            }
            
            // 设置标签ID并分页查询
            request.setTagId(tagId);
            PageResponse<ContentTag> contentTagPage = contentTagService.queryContentTags(request);
            
            // 提取内容ID列表
            List<Long> contentIds = contentTagPage.getDatas().stream()
                    .map(ContentTag::getContentId)
                    .collect(Collectors.toList());
            
            // 构建分页结果
            PageResponse<Long> responsePage = new PageResponse<>();
            responsePage.setDatas(contentIds);
            responsePage.setCurrentPage(contentTagPage.getCurrentPage());
            responsePage.setPageSize(contentTagPage.getPageSize());
            responsePage.setTotal(contentTagPage.getTotal());
            
            return Result.success(responsePage);
        } catch (Exception e) {
            log.error("根据标签查询内容失败: tagId={}", tagId, e);
            return Result.error("CONTENT_TAG_ERROR", "查询标签内容失败: " + e.getMessage());
        }
    }

    @Override
    public Result<PageResponse<Long>> getContentsByTagsAnd(List<Long> tagIds, ContentTagQueryRequest request) {
        try {
            log.debug("根据多标签AND查询内容: tagIds={}", tagIds);
            
            // 参数验证
            if (tagIds == null || tagIds.isEmpty()) {
                return Result.error("CONTENT_TAG_ERROR", "标签ID列表不能为空");
            }
            
            if (request == null) {
                request = new ContentTagQueryRequest();
                request.setCurrentPage(1);
                request.setPageSize(20);
            }
            
            // 设置标签ID列表
            request.setTagIds(tagIds);
            
            // 简化实现：使用第一个标签查询，后续可优化为真正的AND查询
            List<Long> contentIds = contentTagService.getContentsByTagsAnd(tagIds, request.getPageSize());
            
            // 构建分页结果（简化版）
            PageResponse<Long> responsePage = new PageResponse<>();
            responsePage.setDatas(contentIds);
            responsePage.setCurrentPage(request.getCurrentPage());
            responsePage.setPageSize(request.getPageSize());
            responsePage.setTotal((long) contentIds.size());
            
            return Result.success(responsePage);
        } catch (Exception e) {
            log.error("根据多标签AND查询内容失败: tagIds={}", tagIds, e);
            return Result.error("CONTENT_TAG_ERROR", "查询内容失败: " + e.getMessage());
        }
    }

    @Override
    public Result<PageResponse<Long>> getContentsByTagsOr(List<Long> tagIds, ContentTagQueryRequest request) {
        try {
            log.debug("根据多标签OR查询内容: tagIds={}", tagIds);
            
            // 参数验证
            if (tagIds == null || tagIds.isEmpty()) {
                return Result.error("CONTENT_TAG_ERROR", "标签ID列表不能为空");
            }
            
            if (request == null) {
                request = new ContentTagQueryRequest();
                request.setCurrentPage(1);
                request.setPageSize(20);
            }
            
            // 调用领域服务
            List<Long> contentIds = contentTagService.getContentsByTagsOr(tagIds, request.getPageSize());
            
            // 构建分页结果（简化版）
            PageResponse<Long> responsePage = new PageResponse<>();
            responsePage.setDatas(contentIds);
            responsePage.setCurrentPage(request.getCurrentPage());
            responsePage.setPageSize(request.getPageSize());
            responsePage.setTotal((long) contentIds.size());
            
            return Result.success(responsePage);
        } catch (Exception e) {
            log.error("根据多标签OR查询内容失败: tagIds={}", tagIds, e);
            return Result.error("CONTENT_TAG_ERROR", "查询内容失败: " + e.getMessage());
        }
    }

    @Override
    public Result<List<Long>> getHotContentsByTag(Long tagId, Integer days, Integer limit) {
        try {
            log.debug("获取标签热门内容: tagId={}, days={}, limit={}", tagId, days, limit);
            
            // 参数验证
            if (tagId == null || tagId <= 0) {
                return Result.error("CONTENT_TAG_ERROR", "标签ID无效");
            }
            
            // 调用领域服务
            List<Long> hotContents = contentTagService.getTagHotContents(tagId, days, limit);
            
            return Result.success(hotContents);
        } catch (Exception e) {
            log.error("获取标签热门内容失败: tagId={}, days={}, limit={}", tagId, days, limit, e);
            return Result.error("CONTENT_TAG_ERROR", "获取热门内容失败: " + e.getMessage());
        }
    }

    // =================== 基于用户标签的内容推荐 ===================

    @Override
    @Cached(name = TagCacheConstant.USER_CONTENT_RECOMMEND_CACHE,
            key = TagCacheConstant.USER_CONTENT_RECOMMEND_KEY,
            expire = TagCacheConstant.USER_CONTENT_RECOMMEND_CACHE_EXPIRE,
            timeUnit = TimeUnit.MINUTES,
            cacheType = CacheType.BOTH)
    public Result<List<Long>> getRecommendContentsByUserTags(Long userId, Integer limit) {
        try {
            log.debug("基于用户标签推荐内容: userId={}, limit={}", userId, limit);
            
            // 参数验证
            if (userId == null || userId <= 0) {
                return Result.error("CONTENT_TAG_ERROR", "用户ID无效");
            }
            
            // 调用领域服务
            List<Long> recommendContents = contentTagService.getRecommendContentsByUserTags(userId, null, limit);
            
            return Result.success(recommendContents);
        } catch (Exception e) {
            log.error("基于用户标签推荐内容失败: userId={}, limit={}", userId, limit, e);
            return Result.error("CONTENT_TAG_ERROR", "推荐内容失败: " + e.getMessage());
        }
    }

    @Override
    public Result<List<Long>> getRecommendHotContentsByUserTags(Long userId, Integer days, Integer limit) {
        try {
            log.debug("基于用户标签推荐热门内容: userId={}, days={}, limit={}", userId, days, limit);
            
            // 参数验证
            if (userId == null || userId <= 0) {
                return Result.error("CONTENT_TAG_ERROR", "用户ID无效");
            }
            
            // 调用领域服务
            List<Long> hotContents = contentTagService.getRecommendHotContentsByUserTags(userId, days, null, limit);
            
            return Result.success(hotContents);
        } catch (Exception e) {
            log.error("基于用户标签推荐热门内容失败: userId={}, days={}, limit={}", userId, days, limit, e);
            return Result.error("CONTENT_TAG_ERROR", "推荐热门内容失败: " + e.getMessage());
        }
    }

    @Override
    public Result<List<Long>> getRecommendContentsBySimilarUsers(Long userId, Integer limit) {
        try {
            log.debug("基于相似用户推荐内容: userId={}, limit={}", userId, limit);
            
            // 参数验证
            if (userId == null || userId <= 0) {
                return Result.error("CONTENT_TAG_ERROR", "用户ID无效");
            }
            
            // 获取相似用户
            List<Long> userTagIds = userTagService.getUserFollowedTagIds(userId);
            List<Long> similarUserIds = userTagService.findUsersWithSimilarTags(userTagIds, userId, 20);
            
            if (similarUserIds.isEmpty()) {
                return Result.success(new ArrayList<>());
            }
            
            // 调用领域服务
            List<Long> recommendContents = contentTagService.getRecommendContentsBySimilarUsers(userId, similarUserIds, null, limit);
            
            return Result.success(recommendContents);
        } catch (Exception e) {
            log.error("基于相似用户推荐内容失败: userId={}, limit={}", userId, limit, e);
            return Result.error("CONTENT_TAG_ERROR", "推荐内容失败: " + e.getMessage());
        }
    }

    // =================== 内容标签统计分析 ===================

    @Override
    public Result<Map<String, Object>> getTagContentStatistics(Long tagId) {
        try {
            log.debug("获取标签内容统计: tagId={}", tagId);
            
            // 参数验证
            if (tagId == null || tagId <= 0) {
                return Result.error("CONTENT_TAG_ERROR", "标签ID无效");
            }
            
            // 调用领域服务
            Map<String, Object> statistics = contentTagService.getTagContentStatistics(tagId);
            
            return Result.success(statistics);
        } catch (Exception e) {
            log.error("获取标签内容统计失败: tagId={}", tagId, e);
            return Result.error("CONTENT_TAG_ERROR", "获取统计信息失败: " + e.getMessage());
        }
    }

    @Override
    public Result<List<Long>> getTagLatestContents(Long tagId, Integer days, Integer limit) {
        try {
            log.debug("获取标签最新内容: tagId={}, days={}, limit={}", tagId, days, limit);
            
            // 参数验证
            if (tagId == null || tagId <= 0) {
                return Result.error("CONTENT_TAG_ERROR", "标签ID无效");
            }
            
            // 调用领域服务
            List<Long> latestContents = contentTagService.getTagLatestContents(tagId, days, limit);
            
            return Result.success(latestContents);
        } catch (Exception e) {
            log.error("获取标签最新内容失败: tagId={}, days={}, limit={}", tagId, days, limit, e);
            return Result.error("CONTENT_TAG_ERROR", "获取最新内容失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Map<String, Integer>> getTagContentTimeDistribution(Long tagId, Integer days) {
        try {
            log.debug("获取标签内容时间分布: tagId={}, days={}", tagId, days);
            
            // 参数验证
            if (tagId == null || tagId <= 0) {
                return Result.error("CONTENT_TAG_ERROR", "标签ID无效");
            }
            
            // 调用领域服务
            Map<String, Integer> distribution = contentTagService.getTagContentTimeDistribution(tagId, days);
            
            return Result.success(distribution);
        } catch (Exception e) {
            log.error("获取标签内容时间分布失败: tagId={}, days={}", tagId, days, e);
            return Result.error("CONTENT_TAG_ERROR", "获取时间分布失败: " + e.getMessage());
        }
    }

    // =================== 分页查询接口 ===================

    @Override
    public Result<PageResponse<ContentTagResponse>> queryContentTags(ContentTagQueryRequest request) {
        try {
            log.debug("分页查询内容标签: page={}, size={}", request.getCurrentPage(), request.getPageSize());
            
            // 参数验证
            if (request == null) {
                return Result.error("CONTENT_TAG_ERROR", "请求参数不能为空");
            }
            
            // 调用领域服务
            PageResponse<ContentTag> contentTagPage = contentTagService.queryContentTags(request);
            
            // 转换为响应
            List<ContentTagResponse> responses = new ArrayList<>();
            for (ContentTag contentTag : contentTagPage.getDatas()) {
                ContentTagResponse response = convertToContentTagResponse(contentTag);
                
                // 获取标签详情
                if (request.getIncludeTagDetails() != null && request.getIncludeTagDetails()) {
                    Tag tag = tagService.getTagById(contentTag.getTagId());
                    if (tag != null) {
                        response.setTagName(tag.getTagName());
                        response.setTagDescription(tag.getTagDescription());
                        response.setTagIcon(tag.getTagIcon());
                        response.setTagWeight(tag.getWeight());
                        response.setTagHotness(tag.getHotness());
                    }
                }
                
                responses.add(response);
            }
            
            PageResponse<ContentTagResponse> responsePage = new PageResponse<>();
            responsePage.setDatas(responses);
            responsePage.setCurrentPage(contentTagPage.getCurrentPage());
            responsePage.setPageSize(contentTagPage.getPageSize());
            responsePage.setTotal(contentTagPage.getTotal());
            
            return Result.success(responsePage);
        } catch (Exception e) {
            log.error("分页查询内容标签失败", e);
            return Result.error("CONTENT_TAG_ERROR", "查询内容标签失败: " + e.getMessage());
        }
    }

    @Override
    public Result<PageResponse<ContentTagResponse>> queryTagContents(Long tagId, ContentTagQueryRequest request) {
        try {
            log.debug("分页查询标签内容: tagId={}", tagId);
            
            // 参数验证
            if (tagId == null || tagId <= 0) {
                return Result.error("CONTENT_TAG_ERROR", "标签ID无效");
            }
            
            if (request == null) {
                request = new ContentTagQueryRequest();
                request.setCurrentPage(1);
                request.setPageSize(20);
            }
            
            // 设置标签ID并查询
            request.setTagId(tagId);
            return queryContentTags(request);
        } catch (Exception e) {
            log.error("分页查询标签内容失败: tagId={}", tagId, e);
            return Result.error("CONTENT_TAG_ERROR", "查询标签内容失败: " + e.getMessage());
        }
    }

    // =================== 智能推荐功能 ===================

    @Override
    public Result<List<TagResponse>> getRecommendTagsForContent(Long contentId, String contentText, Integer limit) {
        try {
            log.debug("为内容推荐标签: contentId={}, limit={}", contentId, limit);
            
            // 参数验证
            if (contentId == null || contentId <= 0) {
                return Result.error("CONTENT_TAG_ERROR", "内容ID无效");
            }
            
            // 获取当前内容的标签（用于排除）
            List<Long> excludeTagIds = contentTagService.getContentTagIds(contentId);
            
            // 调用领域服务
            List<Long> recommendTagIds = contentTagService.getRecommendTagsForContent(contentId, contentText, excludeTagIds, limit);
            
            // 获取标签详情
            List<Tag> tags = tagService.getTagsByIds(recommendTagIds);
            
            // 转换为响应
            List<TagResponse> responses = tags.stream()
                    .map(this::convertToTagResponse)
                    .collect(Collectors.toList());
            
            return Result.success(responses);
        } catch (Exception e) {
            log.error("为内容推荐标签失败: contentId={}, limit={}", contentId, limit, e);
            return Result.error("CONTENT_TAG_ERROR", "推荐标签失败: " + e.getMessage());
        }
    }

    @Override
    public Result<List<Long>> getRelatedContentsByTags(Long contentId, Integer limit) {
        try {
            log.debug("获取相关内容: contentId={}, limit={}", contentId, limit);
            
            // 参数验证
            if (contentId == null || contentId <= 0) {
                return Result.error("CONTENT_TAG_ERROR", "内容ID无效");
            }
            
            // 调用领域服务
            List<Long> relatedContents = contentTagService.getRelatedContentsByTags(contentId, limit);
            
            return Result.success(relatedContents);
        } catch (Exception e) {
            log.error("获取相关内容失败: contentId={}, limit={}", contentId, limit, e);
            return Result.error("CONTENT_TAG_ERROR", "获取相关内容失败: " + e.getMessage());
        }
    }

    // =================== 管理功能 ===================

    @Override
    @CacheInvalidate(name = TagCacheConstant.CONTENT_TAGS_CACHE)
    public Result<Integer> cleanupInvalidContentTags(Long contentId) {
        try {
            log.info("清理内容无效标签: contentId={}", contentId);
            
            // 调用领域服务
            Integer cleanupCount = contentTagService.cleanupInvalidContentTags(contentId);
            
            log.info("清理内容无效标签完成: contentId={}, count={}", contentId, cleanupCount);
            return Result.success(cleanupCount);
        } catch (Exception e) {
            log.error("清理内容无效标签失败: contentId={}", contentId, e);
            return Result.error("CONTENT_TAG_ERROR", "清理无效标签失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Boolean> canContentHaveTag(Long contentId, Long tagId) {
        try {
            log.debug("检查内容是否可以添加标签: contentId={}, tagId={}", contentId, tagId);
            
            // 参数验证
            if (contentId == null || contentId <= 0 || tagId == null || tagId <= 0) {
                return Result.error("CONTENT_TAG_ERROR", "参数无效");
            }
            
            // 调用领域服务
            boolean canHave = contentTagService.canContentHaveTag(contentId, tagId);
            
            return Result.success(canHave);
        } catch (Exception e) {
            log.error("检查内容是否可以添加标签失败: contentId={}, tagId={}", contentId, tagId, e);
            return Result.error("CONTENT_TAG_ERROR", "检查标签权限失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Void> updateContentTagWeights(List<Long> tagIds) {
        try {
            log.info("批量更新内容标签权重: tagIds={}", tagIds);
            
            // 参数验证
            if (tagIds == null || tagIds.isEmpty()) {
                return Result.error("CONTENT_TAG_ERROR", "标签ID列表不能为空");
            }
            
            // 调用领域服务
            boolean success = contentTagService.updateContentTagWeights(tagIds);
            
            if (success) {
                log.info("批量更新内容标签权重成功: count={}", tagIds.size());
                return Result.success();
            } else {
                return Result.error("CONTENT_TAG_ERROR", "更新标签权重失败");
            }
        } catch (Exception e) {
            log.error("批量更新内容标签权重失败: tagIds={}", tagIds, e);
            return Result.error("CONTENT_TAG_ERROR", "更新标签权重失败: " + e.getMessage());
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
     * 转换ContentTag实体为ContentTagResponse
     */
    private ContentTagResponse convertToContentTagResponse(ContentTag contentTag) {
        if (contentTag == null) {
            return null;
        }
        
        ContentTagResponse response = new ContentTagResponse();
        BeanUtils.copyProperties(contentTag, response);
        
        return response;
    }
}