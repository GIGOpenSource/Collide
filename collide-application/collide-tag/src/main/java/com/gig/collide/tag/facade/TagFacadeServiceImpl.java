package com.gig.collide.tag.facade;

import com.alicp.jetcache.anno.Cached;
import com.alicp.jetcache.anno.CacheInvalidate;
import com.alicp.jetcache.anno.CacheType;
import org.apache.dubbo.config.annotation.DubboService;
import com.gig.collide.api.tag.TagFacadeService;
import com.gig.collide.api.tag.request.TagCreateRequest;
import com.gig.collide.api.tag.request.TagUpdateRequest;
import com.gig.collide.api.tag.request.TagQueryRequest;
import com.gig.collide.api.tag.response.TagResponse;
import com.gig.collide.api.tag.constant.TagStatusConstant;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.web.vo.Result;
import com.gig.collide.tag.domain.entity.Tag;
import com.gig.collide.tag.domain.service.TagService;
import com.gig.collide.tag.infrastructure.cache.TagCacheConstant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 标签门面服务实现
 * 
 * @author GIG Team
 * @version 1.0.0
 */
@Slf4j
@DubboService(version = "1.0.0")
@RequiredArgsConstructor
public class TagFacadeServiceImpl implements TagFacadeService {

    private final TagService tagService;

    // =================== 标签基础管理 ===================

    @Override
    @CacheInvalidate(name = TagCacheConstant.HOT_TAGS_CACHE)
    @CacheInvalidate(name = TagCacheConstant.RECOMMEND_TAGS_CACHE)
    public Result<TagResponse> createTag(TagCreateRequest request) {
        try {
            log.info("创建标签请求: tagName={}", request.getTagName());
            
            // 参数验证
            if (request == null) {
                return Result.error("INVALID_PARAMETER", "请求参数不能为空");
            }
            
            // 转换为实体
            Tag tag = new Tag();
            BeanUtils.copyProperties(request, tag);
            
            // 调用领域服务
            Tag createdTag = tagService.createTag(tag);
            
            // 转换为响应
            TagResponse response = convertToTagResponse(createdTag);
            
            log.info("创建标签成功: tagId={}, tagName={}", createdTag.getId(), createdTag.getTagName());
            return Result.success(response);
        } catch (Exception e) {
            log.error("创建标签失败: tagName={}", request != null ? request.getTagName() : null, e);
            return Result.error("TAG_CREATE_ERROR", "创建标签失败: " + e.getMessage());
        }
    }

    @Override
    @CacheInvalidate(name = TagCacheConstant.TAG_DETAIL_CACHE, key = TagCacheConstant.TAG_DETAIL_KEY)
    @CacheInvalidate(name = TagCacheConstant.HOT_TAGS_CACHE)
    @CacheInvalidate(name = TagCacheConstant.RECOMMEND_TAGS_CACHE)
    public Result<TagResponse> updateTag(TagUpdateRequest request) {
        try {
            log.info("更新标签请求: tagId={}", request.getId());
            
            // 参数验证
            if (request == null || request.getId() == null) {
                return Result.error("INVALID_PARAMETER", "请求参数无效");
            }
            
            // 转换为实体
            Tag tag = new Tag();
            BeanUtils.copyProperties(request, tag);
            
            // 调用领域服务
            Tag updatedTag = tagService.updateTag(tag);
            
            // 转换为响应
            TagResponse response = convertToTagResponse(updatedTag);
            
            log.info("更新标签成功: tagId={}", request.getId());
            return Result.success(response);
        } catch (Exception e) {
            log.error("更新标签失败: tagId={}", request != null ? request.getId() : null, e);
            return Result.error("TAG_ERROR", "更新标签失败: " + e.getMessage());
        }
    }

    @Override
    @CacheInvalidate(name = TagCacheConstant.TAG_DETAIL_CACHE, key = TagCacheConstant.TAG_DETAIL_KEY)
    @CacheInvalidate(name = TagCacheConstant.HOT_TAGS_CACHE)
    @CacheInvalidate(name = TagCacheConstant.RECOMMEND_TAGS_CACHE)
    public Result<Void> deleteTag(Long tagId) {
        try {
            log.info("删除标签请求: tagId={}", tagId);
            
            // 参数验证
            if (tagId == null || tagId <= 0) {
                return Result.error("INVALID_PARAMETER", "标签ID无效");
            }
            
            // 调用领域服务
            boolean success = tagService.deleteTag(tagId);
            
            if (success) {
                log.info("删除标签成功: tagId={}", tagId);
                return Result.success();
            } else {
                return Result.error("TAG_ERROR", "删除标签失败");
            }
        } catch (Exception e) {
            log.error("删除标签失败: tagId={}", tagId, e);
            return Result.error("TAG_ERROR", "删除标签失败: " + e.getMessage());
        }
    }

    @Override
    @Cached(name = TagCacheConstant.TAG_DETAIL_CACHE,
            key = TagCacheConstant.TAG_DETAIL_KEY,
            expire = TagCacheConstant.TAG_DETAIL_CACHE_EXPIRE,
            timeUnit = TimeUnit.MINUTES,
            cacheType = CacheType.BOTH)
    public Result<TagResponse> getTag(Long tagId) {
        try {
            log.debug("查询标签详情: tagId={}", tagId);
            
            // 参数验证
            if (tagId == null || tagId <= 0) {
                return Result.error("INVALID_PARAMETER", "标签ID无效");
            }
            
            // 调用领域服务
            Tag tag = tagService.getTagById(tagId);
            if (tag == null) {
                return Result.error("TAG_ERROR", "标签不存在");
            }
            
            // 转换为响应
            TagResponse response = convertToTagResponse(tag);
            
            return Result.success(response);
        } catch (Exception e) {
            log.error("查询标签详情失败: tagId={}", tagId, e);
            return Result.error("TAG_QUERY_ERROR", "查询标签失败: " + e.getMessage());
        }
    }

    @Override
    public Result<TagResponse> getTagByName(String tagName) {
        try {
            log.debug("根据名称查询标签: tagName={}", tagName);
            
            // 参数验证
            if (tagName == null || tagName.trim().isEmpty()) {
                return Result.error("TAG_ERROR", "标签名称不能为空");
            }
            
            // 调用领域服务
            Tag tag = tagService.getTagByName(tagName.trim());
            if (tag == null) {
                return Result.error("TAG_ERROR", "标签不存在");
            }
            
            // 转换为响应
            TagResponse response = convertToTagResponse(tag);
            
            return Result.success(response);
        } catch (Exception e) {
            log.error("根据名称查询标签失败: tagName={}", tagName, e);
            return Result.error("TAG_QUERY_ERROR", "查询标签失败: " + e.getMessage());
        }
    }

    // =================== 标签列表查询 ===================

    @Override
    public Result<List<TagResponse>> getAllActiveTags() {
        try {
            log.debug("查询所有启用标签");
            
            // 调用领域服务
            List<Tag> tags = tagService.getAllActiveTags();
            
            // 转换为响应
            List<TagResponse> responses = tags.stream()
                    .map(this::convertToTagResponse)
                    .collect(Collectors.toList());
            
            return Result.success(responses);
        } catch (Exception e) {
            log.error("查询所有启用标签失败", e);
            return Result.error("TAG_QUERY_ERROR", "查询标签失败: " + e.getMessage());
        }
    }

    @Override
    public Result<PageResponse<TagResponse>> queryTags(TagQueryRequest request) {
        try {
            log.debug("分页查询标签: page={}, size={}", request.getCurrentPage(), request.getPageSize());
            
            // 参数验证
            if (request == null) {
                return Result.error("INVALID_PARAMETER", "请求参数不能为空");
            }
            
            // 调用领域服务
            PageResponse<Tag> tagPage = tagService.queryTags(request);
            
            // 转换为响应
            List<TagResponse> responses = tagPage.getDatas().stream()
                    .map(this::convertToTagResponse)
                    .collect(Collectors.toList());
            
            PageResponse<TagResponse> responsePage = new PageResponse<>();
            responsePage.setDatas(responses);
            responsePage.setCurrentPage(tagPage.getCurrentPage());
            responsePage.setPageSize(tagPage.getPageSize());
            responsePage.setTotal(tagPage.getTotal());
            
            return Result.success(responsePage);
        } catch (Exception e) {
            log.error("分页查询标签失败", e);
            return Result.error("TAG_QUERY_ERROR", "查询标签失败: " + e.getMessage());
        }
    }

    @Override
    @Cached(name = TagCacheConstant.HOT_TAGS_CACHE,
            key = TagCacheConstant.HOT_TAGS_KEY,
            expire = TagCacheConstant.HOT_TAGS_CACHE_EXPIRE,
            timeUnit = TimeUnit.MINUTES,
            cacheType = CacheType.BOTH)
    public Result<List<TagResponse>> getHotTags(Integer limit) {
        try {
            log.debug("查询热门标签: limit={}", limit);
            
            // 调用领域服务
            List<Tag> tags = tagService.getHotTags(limit);
            
            // 转换为响应
            List<TagResponse> responses = tags.stream()
                    .map(this::convertToTagResponse)
                    .collect(Collectors.toList());
            
            return Result.success(responses);
        } catch (Exception e) {
            log.error("查询热门标签失败: limit={}", limit, e);
            return Result.error("TAG_ERROR", "查询热门标签失败: " + e.getMessage());
        }
    }

    @Override
    @Cached(name = TagCacheConstant.RECOMMEND_TAGS_CACHE,
            key = TagCacheConstant.RECOMMEND_TAGS_KEY,
            expire = TagCacheConstant.RECOMMEND_TAGS_CACHE_EXPIRE,
            timeUnit = TimeUnit.MINUTES,
            cacheType = CacheType.BOTH)
    public Result<List<TagResponse>> getRecommendTags(Integer limit) {
        try {
            log.debug("查询推荐标签: limit={}", limit);
            
            // 调用领域服务
            List<Tag> tags = tagService.getRecommendTags(limit);
            
            // 转换为响应
            List<TagResponse> responses = tags.stream()
                    .map(this::convertToTagResponse)
                    .collect(Collectors.toList());
            
            return Result.success(responses);
        } catch (Exception e) {
            log.error("查询推荐标签失败: limit={}", limit, e);
            return Result.error("TAG_ERROR", "查询推荐标签失败: " + e.getMessage());
        }
    }

    // =================== 标签统计分析 ===================

    @Override
    @Cached(name = TagCacheConstant.TAG_STATISTICS_CACHE,
            key = TagCacheConstant.TAG_STATISTICS_KEY,
            expire = TagCacheConstant.TAG_STATISTICS_CACHE_EXPIRE,
            timeUnit = TimeUnit.MINUTES,
            cacheType = CacheType.BOTH)
    public Result<Map<String, Object>> getTagStatistics(Long tagId) {
        try {
            log.debug("查询标签统计信息: tagId={}", tagId);
            
            // 调用领域服务
            Map<String, Object> statistics = tagService.getTagStatistics(tagId);
            
            return Result.success(statistics);
        } catch (Exception e) {
            log.error("查询标签统计信息失败: tagId={}", tagId, e);
            return Result.error("TAG_ERROR", "查询统计信息失败: " + e.getMessage());
        }
    }

    @Override
    public Result<List<TagResponse>> getHotnessRanking(Integer limit) {
        try {
            log.debug("查询热度排行榜: limit={}", limit);
            
            // 调用领域服务
            List<Tag> tags = tagService.getHotnessRanking(limit);
            
            // 转换为响应
            List<TagResponse> responses = tags.stream()
                    .map(this::convertToTagResponse)
                    .collect(Collectors.toList());
            
            return Result.success(responses);
        } catch (Exception e) {
            log.error("查询热度排行榜失败: limit={}", limit, e);
            return Result.error("TAG_ERROR", "查询排行榜失败: " + e.getMessage());
        }
    }

    @Override
    public Result<List<TagResponse>> getFollowCountRanking(Integer limit) {
        try {
            log.debug("查询关注数排行榜: limit={}", limit);
            
            // 调用领域服务
            List<Tag> tags = tagService.getFollowCountRanking(limit);
            
            // 转换为响应
            List<TagResponse> responses = tags.stream()
                    .map(this::convertToTagResponse)
                    .collect(Collectors.toList());
            
            return Result.success(responses);
        } catch (Exception e) {
            log.error("查询关注数排行榜失败: limit={}", limit, e);
            return Result.error("TAG_ERROR", "查询排行榜失败: " + e.getMessage());
        }
    }

    @Override
    public Result<List<TagResponse>> getContentCountRanking(Integer limit) {
        try {
            log.debug("查询内容数排行榜: limit={}", limit);
            
            // 调用领域服务
            List<Tag> tags = tagService.getContentCountRanking(limit);
            
            // 转换为响应
            List<TagResponse> responses = tags.stream()
                    .map(this::convertToTagResponse)
                    .collect(Collectors.toList());
            
            return Result.success(responses);
        } catch (Exception e) {
            log.error("查询内容数排行榜失败: limit={}", limit, e);
            return Result.error("TAG_ERROR", "查询排行榜失败: " + e.getMessage());
        }
    }

    // =================== 标签状态管理 ===================

    @Override
    @CacheInvalidate(name = TagCacheConstant.TAG_DETAIL_CACHE, key = TagCacheConstant.TAG_DETAIL_KEY)
    @CacheInvalidate(name = TagCacheConstant.HOT_TAGS_CACHE)
    @CacheInvalidate(name = TagCacheConstant.RECOMMEND_TAGS_CACHE)
    public Result<Void> updateTagStatus(Long tagId, Integer status) {
        try {
            log.info("更新标签状态: tagId={}, status={}", tagId, status);
            
            // 参数验证
            if (tagId == null || tagId <= 0) {
                return Result.error("INVALID_PARAMETER", "标签ID无效");
            }
            
            if (!TagStatusConstant.isValidStatus(status)) {
                return Result.error("TAG_ERROR", "状态值无效");
            }
            
            // 调用领域服务
            boolean success = tagService.updateTagStatus(tagId, status);
            
            if (success) {
                log.info("更新标签状态成功: tagId={}, status={}", tagId, status);
                return Result.success();
            } else {
                return Result.error("TAG_ERROR", "更新标签状态失败");
            }
        } catch (Exception e) {
            log.error("更新标签状态失败: tagId={}, status={}", tagId, status, e);
            return Result.error("TAG_ERROR", "更新标签状态失败: " + e.getMessage());
        }
    }

    @Override
    @CacheInvalidate(name = TagCacheConstant.TAG_DETAIL_CACHE, key = TagCacheConstant.TAG_DETAIL_KEY)
    @CacheInvalidate(name = TagCacheConstant.HOT_TAGS_CACHE)
    @CacheInvalidate(name = TagCacheConstant.RECOMMEND_TAGS_CACHE)
    public Result<Void> updateTagWeight(Long tagId, Integer weight) {
        try {
            log.info("更新标签权重: tagId={}, weight={}", tagId, weight);
            
            // 参数验证
            if (tagId == null || tagId <= 0) {
                return Result.error("INVALID_PARAMETER", "标签ID无效");
            }
            
            if (weight == null || weight < 1 || weight > 100) {
                return Result.error("TAG_ERROR", "权重值无效，必须在1-100之间");
            }
            
            // 调用领域服务
            boolean success = tagService.updateTagWeight(tagId, weight);
            
            if (success) {
                log.info("更新标签权重成功: tagId={}, weight={}", tagId, weight);
                return Result.success();
            } else {
                return Result.error("TAG_ERROR", "更新标签权重失败");
            }
        } catch (Exception e) {
            log.error("更新标签权重失败: tagId={}, weight={}", tagId, weight, e);
            return Result.error("TAG_ERROR", "更新标签权重失败: " + e.getMessage());
        }
    }

    @Override
    @CacheInvalidate(name = TagCacheConstant.TAG_DETAIL_CACHE)
    @CacheInvalidate(name = TagCacheConstant.HOT_TAGS_CACHE)
    @CacheInvalidate(name = TagCacheConstant.RECOMMEND_TAGS_CACHE)
    public Result<Void> batchUpdateTagStatus(List<Long> tagIds, Integer status) {
        try {
            log.info("批量更新标签状态: tagIds={}, status={}", tagIds, status);
            
            // 参数验证
            if (tagIds == null || tagIds.isEmpty()) {
                return Result.error("TAG_ERROR", "标签ID列表不能为空");
            }
            
            if (!TagStatusConstant.isValidStatus(status)) {
                return Result.error("TAG_ERROR", "状态值无效");
            }
            
            // 调用领域服务
            boolean success = tagService.batchUpdateTagStatus(tagIds, status);
            
            if (success) {
                log.info("批量更新标签状态成功: count={}, status={}", tagIds.size(), status);
                return Result.success();
            } else {
                return Result.error("TAG_ERROR", "批量更新标签状态失败");
            }
        } catch (Exception e) {
            log.error("批量更新标签状态失败: tagIds={}, status={}", tagIds, status, e);
            return Result.error("TAG_ERROR", "批量更新标签状态失败: " + e.getMessage());
        }
    }

    // =================== 标签搜索 ===================

    @Override
    public Result<List<TagResponse>> searchTags(String keyword, Integer limit) {
        try {
            log.debug("搜索标签: keyword={}, limit={}", keyword, limit);
            
            // 参数验证
            if (keyword == null || keyword.trim().isEmpty()) {
                return Result.error("TAG_ERROR", "搜索关键词不能为空");
            }
            
            // 调用领域服务
            List<Tag> tags = tagService.searchTags(keyword.trim(), limit);
            
            // 转换为响应
            List<TagResponse> responses = tags.stream()
                    .map(this::convertToTagResponse)
                    .collect(Collectors.toList());
            
            return Result.success(responses);
        } catch (Exception e) {
            log.error("搜索标签失败: keyword={}, limit={}", keyword, limit, e);
            return Result.error("TAG_ERROR", "搜索标签失败: " + e.getMessage());
        }
    }

    @Override
    public Result<List<TagResponse>> getTagsByWeightRange(Integer minWeight, Integer maxWeight, Integer limit) {
        try {
            log.debug("根据权重范围查询标签: minWeight={}, maxWeight={}, limit={}", minWeight, maxWeight, limit);
            
            // 调用领域服务
            List<Tag> tags = tagService.getTagsByWeightRange(minWeight, maxWeight, limit);
            
            // 转换为响应
            List<TagResponse> responses = tags.stream()
                    .map(this::convertToTagResponse)
                    .collect(Collectors.toList());
            
            return Result.success(responses);
        } catch (Exception e) {
            log.error("根据权重范围查询标签失败: minWeight={}, maxWeight={}, limit={}", minWeight, maxWeight, limit, e);
            return Result.error("TAG_QUERY_ERROR", "查询标签失败: " + e.getMessage());
        }
    }

    // =================== 系统管理功能 ===================

    @Override
    @CacheInvalidate(name = TagCacheConstant.TAG_DETAIL_CACHE)
    @CacheInvalidate(name = TagCacheConstant.HOT_TAGS_CACHE)
    @CacheInvalidate(name = TagCacheConstant.RECOMMEND_TAGS_CACHE)
    public Result<Void> updateTagHotness(Long tagId) {
        try {
            log.info("手动更新标签热度: tagId={}", tagId);
            
            if (tagId == null) {
                // 更新所有标签热度
                int updateCount = tagService.updateAllTagHotness();
                log.info("更新所有标签热度完成: count={}", updateCount);
                return Result.success();
            } else {
                // 更新指定标签热度
                boolean success = tagService.recalculateTagHotness(tagId);
                if (success) {
                    log.info("更新标签热度成功: tagId={}", tagId);
                    return Result.success();
                } else {
                    return Result.error("TAG_ERROR", "更新标签热度失败");
                }
            }
        } catch (Exception e) {
            log.error("更新标签热度失败: tagId={}", tagId, e);
            return Result.error("TAG_ERROR", "更新标签热度失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Boolean> checkTagNameAvailable(String tagName, Long excludeId) {
        try {
            log.debug("检查标签名称可用性: tagName={}, excludeId={}", tagName, excludeId);
            
            // 参数验证
            if (tagName == null || tagName.trim().isEmpty()) {
                return Result.error("TAG_ERROR", "标签名称不能为空");
            }
            
            // 调用领域服务
            boolean available = tagService.checkTagNameAvailable(tagName.trim(), excludeId);
            
            return Result.success(available);
        } catch (Exception e) {
            log.error("检查标签名称可用性失败: tagName={}, excludeId={}", tagName, excludeId, e);
            return Result.error("TAG_ERROR", "检查标签名称失败: " + e.getMessage());
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
}