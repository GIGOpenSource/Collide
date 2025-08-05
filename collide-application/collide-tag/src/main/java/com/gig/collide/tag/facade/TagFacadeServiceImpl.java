package com.gig.collide.tag.facade;

import com.gig.collide.api.tag.TagFacadeService;
import com.gig.collide.api.tag.request.TagCreateRequest;
import com.gig.collide.api.tag.request.TagQueryRequest;
import com.gig.collide.api.tag.request.TagUpdateRequest;
import com.gig.collide.api.tag.response.TagResponse;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.tag.domain.entity.Tag;
import com.gig.collide.tag.domain.service.TagService;
import com.gig.collide.web.vo.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 标签门面服务实现类 - 基础标签管理
 * 专注于标签本身的管理功能，参数验证、结果转换和错误处理
 *
 * @author GIG Team
 * @version 3.0.0
 */
@Slf4j
@Service
@DubboService
@RequiredArgsConstructor
public class TagFacadeServiceImpl implements TagFacadeService {

    private final TagService tagService;

    @Override
    public Result<TagResponse> createTag(TagCreateRequest request) {
        try {
            log.info("创建标签请求: 名称={}, 类型={}", request.getName(), request.getTagType());
            
            if (!StringUtils.hasText(request.getName())) {
                return Result.error("INVALID_PARAM", "标签名称不能为空");
            }
            if (!StringUtils.hasText(request.getTagType())) {
                return Result.error("INVALID_PARAM", "标签类型不能为空");
            }
            
            if (tagService.existsByNameAndType(request.getName(), request.getTagType())) {
                return Result.error("TAG_ALREADY_EXISTS", "标签名称已存在");
            }
            
            Tag tag = tagService.createTagSafely(request.getName(), request.getTagType(), 
                    request.getDescription(), request.getCategoryId());
            
            TagResponse response = convertToTagResponse(tag);
            log.info("标签创建成功: ID={}", tag.getId());
            return Result.success(response);
            
        } catch (Exception e) {
            log.error("创建标签失败", e);
            return Result.error("TAG_CREATE_ERROR", "创建标签失败: " + e.getMessage());
        }
    }

    @Override
    public Result<TagResponse> updateTag(TagUpdateRequest request) {
        try {
            log.info("更新标签请求: ID={}", request.getId());
            
            if (request.getId() == null) {
                return Result.error("INVALID_PARAM", "标签ID不能为空");
            }
            
            Tag existingTag = tagService.getTagById(request.getId());
            if (existingTag == null) {
                return Result.error("TAG_NOT_FOUND", "标签不存在");
            }
            
            if (StringUtils.hasText(request.getName())) {
                existingTag.setName(request.getName());
            }
            if (StringUtils.hasText(request.getDescription())) {
                existingTag.setDescription(request.getDescription());
            }
            if (request.getCategoryId() != null) {
                existingTag.setCategoryId(request.getCategoryId());
            }
            
            Tag updatedTag = tagService.updateTag(existingTag);
            TagResponse response = convertToTagResponse(updatedTag);
            
            log.info("标签更新成功: ID={}", updatedTag.getId());
            return Result.success(response);
            
        } catch (Exception e) {
            log.error("更新标签失败", e);
            return Result.error("TAG_UPDATE_ERROR", "更新标签失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Void> deleteTag(Long tagId, Long operatorId) {
        try {
            log.info("删除标签请求: ID={}, 操作人={}", tagId, operatorId);
            
            if (tagId == null) {
                return Result.error("INVALID_PARAM", "标签ID不能为空");
            }
            
            Tag existingTag = tagService.getTagById(tagId);
            if (existingTag == null) {
                return Result.error("TAG_NOT_FOUND", "标签不存在");
            }
            
            boolean success = tagService.deleteTagById(tagId);
            if (success) {
                log.info("标签删除成功: ID={}", tagId);
                return Result.success();
            } else {
                return Result.error("TAG_DELETE_ERROR", "标签删除失败");
            }
            
        } catch (Exception e) {
            log.error("删除标签失败", e);
            return Result.error("TAG_DELETE_ERROR", "删除标签失败: " + e.getMessage());
        }
    }

    @Override
    public Result<TagResponse> getTagById(Long tagId) {
        try {
            log.debug("查询标签详情: ID={}", tagId);
            
            if (tagId == null) {
                return Result.error("INVALID_PARAM", "标签ID不能为空");
            }
            
            Tag tag = tagService.getTagById(tagId);
            if (tag == null) {
                return Result.error("TAG_NOT_FOUND", "标签不存在");
            }
            
            TagResponse response = convertToTagResponse(tag);
            return Result.success(response);
            
        } catch (Exception e) {
            log.error("查询标签详情失败", e);
            return Result.error("TAG_QUERY_ERROR", "查询标签详情失败: " + e.getMessage());
        }
    }

    @Override
    public Result<PageResponse<TagResponse>> queryTags(TagQueryRequest request) {
        try {
            log.debug("分页查询标签: 页码={}, 大小={}", request.getCurrentPage(), request.getPageSize());
            
            // 委托给TagService处理复杂分页逻辑
            List<Tag> allTags = tagService.getAllTags();
            List<Tag> filteredTags = allTags.stream()
                    .filter(tag -> filterTag(tag, request))
                    .collect(Collectors.toList());
            
            int total = filteredTags.size();
            int start = (request.getCurrentPage() - 1) * request.getPageSize();
            int end = Math.min(start + request.getPageSize(), total);
            
            List<Tag> pagedTags = filteredTags.subList(start, end);
            List<TagResponse> responses = pagedTags.stream()
                    .map(this::convertToTagResponse)
                    .collect(Collectors.toList());
            
            PageResponse<TagResponse> pageResponse = new PageResponse<>();
            pageResponse.setDatas(responses);
            pageResponse.setTotal((long) total);
            pageResponse.setCurrentPage(request.getCurrentPage());
            pageResponse.setPageSize(request.getPageSize());
            pageResponse.setTotal((long) Math.ceil((double) total / request.getPageSize()));
            
            return Result.success(pageResponse);
            
        } catch (Exception e) {
            log.error("分页查询标签失败", e);
            return Result.error("TAG_QUERY_ERROR", "分页查询标签失败: " + e.getMessage());
        }
    }

    @Override
    public Result<List<TagResponse>> getTagsByType(String tagType) {
        try {
            log.debug("根据类型查询标签: 类型={}", tagType);
            
            if (!StringUtils.hasText(tagType)) {
                return Result.error("INVALID_PARAM", "标签类型不能为空");
            }
            
            List<Tag> tags = tagService.selectByTagType(tagType);
            List<TagResponse> responses = tags.stream()
                    .map(this::convertToTagResponse)
                    .collect(Collectors.toList());
            
            return Result.success(responses);
            
        } catch (Exception e) {
            log.error("根据类型查询标签失败", e);
            return Result.error("TAG_QUERY_ERROR", "根据类型查询标签失败: " + e.getMessage());
        }
    }

    @Override
    public Result<List<TagResponse>> searchTags(String keyword, Integer limit) {
        try {
            log.debug("搜索标签: 关键词={}, 限制数量={}", keyword, limit);
            
            if (!StringUtils.hasText(keyword)) {
                return Result.error("INVALID_PARAM", "搜索关键词不能为空");
            }
            
            List<Tag> tags = tagService.intelligentSearch(keyword, limit);
            List<TagResponse> responses = tags.stream()
                    .map(this::convertToTagResponse)
                    .collect(Collectors.toList());
            
            return Result.success(responses);
            
        } catch (Exception e) {
            log.error("搜索标签失败", e);
            return Result.error("TAG_SEARCH_ERROR", "搜索标签失败: " + e.getMessage());
        }
    }

    @Override
    public Result<List<TagResponse>> searchTagsByNameExact(String keyword, Integer limit) {
        try {
            log.debug("精确搜索标签: 关键词={}, 限制数量={}", keyword, limit);
            
            if (!StringUtils.hasText(keyword)) {
                return Result.error("INVALID_PARAM", "搜索关键词不能为空");
            }
            
            List<Tag> tags = tagService.searchByNameExact(keyword, limit);
            List<TagResponse> responses = tags.stream()
                    .map(this::convertToTagResponse)
                    .collect(Collectors.toList());
            
            return Result.success(responses);
            
        } catch (Exception e) {
            log.error("精确搜索标签失败", e);
            return Result.error("TAG_SEARCH_ERROR", "精确搜索标签失败: " + e.getMessage());
        }
    }

    @Override
    public Result<List<TagResponse>> getHotTags(Integer limit) {
        try {
            log.debug("获取热门标签: 限制数量={}", limit);
            
            List<Tag> tags = tagService.selectHotTags(limit);
            List<TagResponse> responses = tags.stream()
                    .map(this::convertToTagResponse)
                    .collect(Collectors.toList());
            
            return Result.success(responses);
            
        } catch (Exception e) {
            log.error("获取热门标签失败", e);
            return Result.error("TAG_QUERY_ERROR", "获取热门标签失败: " + e.getMessage());
        }
    }

    @Override
    public Result<List<TagResponse>> getTagsByCategory(Long categoryId) {
        try {
            log.debug("根据分类查询标签: 分类ID={}", categoryId);
            
            if (categoryId == null) {
                return Result.error("INVALID_PARAM", "分类ID不能为空");
            }
            
            List<Tag> tags = tagService.selectByCategoryId(categoryId);
            List<TagResponse> responses = tags.stream()
                    .map(this::convertToTagResponse)
                    .collect(Collectors.toList());
            
            return Result.success(responses);
            
        } catch (Exception e) {
            log.error("根据分类查询标签失败", e);
            return Result.error("TAG_QUERY_ERROR", "根据分类查询标签失败: " + e.getMessage());
        }
    }

    @Override
    public Result<List<TagResponse>> getActiveTags(Long categoryId) {
        try {
            log.debug("获取活跃标签: 分类ID={}", categoryId);
            
            List<Tag> tags = tagService.getActiveTags(categoryId);
            List<TagResponse> responses = tags.stream()
                    .map(this::convertToTagResponse)
                    .collect(Collectors.toList());
            
            return Result.success(responses);
            
        } catch (Exception e) {
            log.error("获取活跃标签失败", e);
            return Result.error("TAG_QUERY_ERROR", "获取活跃标签失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Void> activateTag(Long tagId, Long operatorId) {
        try {
            log.info("激活标签: ID={}, 操作人={}", tagId, operatorId);
            
            if (tagId == null) {
                return Result.error("INVALID_PARAM", "标签ID不能为空");
            }
            
            boolean success = tagService.activateTag(tagId);
            return success ? Result.success() : Result.error("TAG_ACTIVATE_ERROR", "标签激活失败");
            
        } catch (Exception e) {
            log.error("激活标签失败", e);
            return Result.error("TAG_ACTIVATE_ERROR", "激活标签失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Void> deactivateTag(Long tagId, Long operatorId) {
        try {
            log.info("停用标签: ID={}, 操作人={}", tagId, operatorId);
            
            if (tagId == null) {
                return Result.error("INVALID_PARAM", "标签ID不能为空");
            }
            
            boolean success = tagService.deactivateTag(tagId);
            return success ? Result.success() : Result.error("TAG_DEACTIVATE_ERROR", "标签停用失败");
            
        } catch (Exception e) {
            log.error("停用标签失败", e);
            return Result.error("TAG_DEACTIVATE_ERROR", "停用标签失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Integer> batchUpdateTagStatus(List<Long> tagIds, String status, Long operatorId) {
        try {
            log.info("批量更新标签状态: 数量={}, 状态={}, 操作人={}", tagIds.size(), status, operatorId);
            
            if (tagIds == null || tagIds.isEmpty()) {
                return Result.error("INVALID_PARAM", "标签ID列表不能为空");
            }
            if (!StringUtils.hasText(status)) {
                return Result.error("INVALID_PARAM", "状态不能为空");
            }
            
            int result = tagService.batchUpdateStatus(tagIds, status);
            log.info("批量更新标签状态完成: 更新数量={}", result);
            return Result.success(result);
            
        } catch (Exception e) {
            log.error("批量更新标签状态失败", e);
            return Result.error("TAG_UPDATE_ERROR", "批量更新标签状态失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Void> increaseTagUsage(Long tagId) {
        try {
            if (tagId == null) {
                return Result.error("INVALID_PARAM", "标签ID不能为空");
            }
            
            boolean success = tagService.increaseUsageCount(tagId);
            return success ? Result.success() : Result.error("TAG_UPDATE_ERROR", "增加标签使用次数失败");
            
        } catch (Exception e) {
            log.error("增加标签使用次数失败", e);
            return Result.error("TAG_UPDATE_ERROR", "增加标签使用次数失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Void> decreaseTagUsage(Long tagId) {
        try {
            if (tagId == null) {
                return Result.error("INVALID_PARAM", "标签ID不能为空");
            }
            
            boolean success = tagService.decreaseUsageCount(tagId);
            return success ? Result.success() : Result.error("TAG_UPDATE_ERROR", "减少标签使用次数失败");
            
        } catch (Exception e) {
            log.error("减少标签使用次数失败", e);
            return Result.error("TAG_UPDATE_ERROR", "减少标签使用次数失败: " + e.getMessage());
        }
    }

    @Override
    public Result<List<Map<String, Object>>> getTagUsageStats(String tagType, Integer limit) {
        try {
            List<Map<String, Object>> stats = tagService.getTagUsageStats(tagType, limit);
            return Result.success(stats);
        } catch (Exception e) {
            log.error("获取标签使用统计失败", e);
            return Result.error("TAG_STATS_ERROR", "获取标签使用统计失败: " + e.getMessage());
        }
    }

    @Override
    public Result<List<Map<String, Object>>> getTagSummary(List<Long> tagIds) {
        try {
            if (tagIds == null || tagIds.isEmpty()) {
                return Result.error("INVALID_PARAM", "标签ID列表不能为空");
            }
            
            List<Map<String, Object>> summary = tagService.selectTagSummary(tagIds);
            return Result.success(summary);
        } catch (Exception e) {
            log.error("批量获取标签基本信息失败", e);
            return Result.error("TAG_QUERY_ERROR", "批量获取标签基本信息失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Boolean> checkTagExists(String name, String tagType) {
        try {
            if (!StringUtils.hasText(name) || !StringUtils.hasText(tagType)) {
                return Result.error("INVALID_PARAM", "标签名称和类型不能为空");
            }
            
            boolean exists = tagService.existsByNameAndType(name, tagType);
            return Result.success(exists);
        } catch (Exception e) {
            log.error("检查标签是否存在失败", e);
            return Result.error("TAG_CHECK_ERROR", "检查标签是否存在失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Boolean> canDeleteTag(Long tagId) {
        try {
            if (tagId == null) {
                return Result.error("INVALID_PARAM", "标签ID不能为空");
            }
            
            Tag tag = tagService.getTagById(tagId);
            if (tag == null) {
                return Result.error("TAG_NOT_FOUND", "标签不存在");
            }
            
            // 简化实现：基于使用次数判断
            boolean canDelete = tag.getUsageCount() == null || tag.getUsageCount() == 0;
            return Result.success(canDelete);
        } catch (Exception e) {
            log.error("检查标签是否可以删除失败", e);
            return Result.error("TAG_CHECK_ERROR", "检查标签是否可以删除失败: " + e.getMessage());
        }
    }

    @Override
    public Result<List<Map<String, Object>>> getTagCloud(String tagType, Integer limit) {
        try {
            List<Map<String, Object>> tagStats = tagService.getTagUsageStats(tagType, limit);
            return Result.success(tagStats);
        } catch (Exception e) {
            log.error("获取标签云失败", e);
            return Result.error("TAG_CLOUD_ERROR", "获取标签云失败: " + e.getMessage());
        }
    }

    @Override
    public Result<List<TagResponse>> getSimilarTags(Long tagId, Integer limit) {
        try {
            if (tagId == null) {
                return Result.error("INVALID_PARAM", "标签ID不能为空");
            }
            
            Tag tag = tagService.getTagById(tagId);
            if (tag == null) {
                return Result.error("TAG_NOT_FOUND", "标签不存在");
            }
            
            // 简化实现：基于相同类型和分类获取相似标签
            List<Tag> similarTags = tagService.selectByCategoryId(tag.getCategoryId())
                    .stream()
                    .filter(t -> !t.getId().equals(tagId))
                    .limit(limit != null ? limit : 10)
                    .collect(Collectors.toList());
            
            List<TagResponse> responses = similarTags.stream()
                    .map(this::convertToTagResponse)
                    .collect(Collectors.toList());
            
            return Result.success(responses);
        } catch (Exception e) {
            log.error("获取相似标签失败", e);
            return Result.error("TAG_QUERY_ERROR", "获取相似标签失败: " + e.getMessage());
        }
    }

    // 其他方法简化实现
    @Override
    public Result<Integer> recalculateTagUsageCounts(Long operatorId) {
        return Result.error("NOT_IMPLEMENTED", "功能暂未实现");
    }

    @Override
    public Result<Integer> mergeDuplicateTags(Long mainTagId, List<Long> duplicateTagIds, Long operatorId) {
        return Result.error("NOT_IMPLEMENTED", "功能暂未实现");
    }

    @Override
    public Result<Integer> cleanupUnusedTags(Long operatorId) {
        return Result.error("NOT_IMPLEMENTED", "功能暂未实现");
    }

    @Override
    public Result<List<String>> getAllTagTypes() {
        try {
            List<String> tagTypes = tagService.getAllTags().stream()
                    .map(Tag::getTagType)
                    .distinct()
                    .collect(Collectors.toList());
            return Result.success(tagTypes);
        } catch (Exception e) {
            return Result.error("TAG_QUERY_ERROR", "获取所有标签类型失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Map<String, Object>> getTagSystemStats() {
        return Result.error("NOT_IMPLEMENTED", "功能暂未实现");
    }

    @Override
    public Result<String> healthCheck() {
        try {
            List<Tag> allTags = tagService.getAllTags();
            long activeTagCount = allTags.stream()
                    .filter(tag -> "active".equals(tag.getStatus()))
                    .count();
            
            String healthStatus = String.format("标签系统运行正常。活跃标签数量: %d", activeTagCount);
            return Result.success(healthStatus);
        } catch (Exception e) {
            return Result.error("HEALTH_CHECK_ERROR", "健康检查失败: " + e.getMessage());
        }
    }

    // 私有工具方法
    private TagResponse convertToTagResponse(Tag tag) {
        TagResponse response = new TagResponse();
        BeanUtils.copyProperties(tag, response);
        return response;
    }

    private boolean filterTag(Tag tag, TagQueryRequest request) {
        if (StringUtils.hasText(request.getName()) && !tag.getName().contains(request.getName())) {
            return false;
        }
        if (StringUtils.hasText(request.getTagType()) && !request.getTagType().equals(tag.getTagType())) {
            return false;
        }
        if (request.getCategoryId() != null && !request.getCategoryId().equals(tag.getCategoryId())) {
            return false;
        }
        if (StringUtils.hasText(request.getStatus()) && !request.getStatus().equals(tag.getStatus())) {
            return false;
        }
        return true;
    }
}