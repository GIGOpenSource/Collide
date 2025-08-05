package com.gig.collide.tag.facade;

import com.gig.collide.api.tag.ContentTagFacadeService;
import com.gig.collide.api.tag.request.ContentTagRequest;
import com.gig.collide.api.tag.response.ContentTagResponse;
import com.gig.collide.tag.domain.entity.ContentTag;
import com.gig.collide.tag.domain.entity.Tag;
import com.gig.collide.tag.domain.service.ContentTagService;
import com.gig.collide.tag.domain.service.TagService;
import com.gig.collide.web.vo.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 内容标签门面服务实现类
 * 专注于内容与标签的关联管理
 *
 * @author GIG Team
 * @version 3.0.0
 */
@Slf4j
@Service
@DubboService
@RequiredArgsConstructor
public class ContentTagFacadeServiceImpl implements ContentTagFacadeService {

    private final ContentTagService contentTagService;
    private final TagService tagService;

    @Override
    public Result<ContentTagResponse> addContentTag(ContentTagRequest request) {
        try {
            log.info("为内容添加标签: 内容ID={}, 标签ID={}", request.getContentId(), request.getTagId());
            
            if (request.getContentId() == null) {
                return Result.error("INVALID_PARAM", "内容ID不能为空");
            }
            if (request.getTagId() == null) {
                return Result.error("INVALID_PARAM", "标签ID不能为空");
            }
            
            Tag tag = tagService.getTagById(request.getTagId());
            if (tag == null) {
                return Result.error("TAG_NOT_FOUND", "标签不存在");
            }
            
            ContentTag contentTag = contentTagService.addContentTagSafely(request.getContentId(), request.getTagId());
            ContentTagResponse response = convertToContentTagResponse(contentTag, tag);
            
            tagService.increaseUsageCount(request.getTagId());
            
            log.info("内容标签添加成功: ID={}", contentTag.getId());
            return Result.success(response);
            
        } catch (Exception e) {
            log.error("为内容添加标签失败", e);
            return Result.error("CONTENT_TAG_ADD_ERROR", "为内容添加标签失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Void> removeContentTag(Long contentId, Long tagId, Long operatorId) {
        try {
            log.info("移除内容标签: 内容ID={}, 标签ID={}, 操作人={}", contentId, tagId, operatorId);
            
            if (contentId == null || tagId == null) {
                return Result.error("INVALID_PARAM", "内容ID和标签ID不能为空");
            }
            
            boolean success = contentTagService.removeContentTag(contentId, tagId);
            if (success) {
                tagService.decreaseUsageCount(tagId);
                log.info("内容标签移除成功");
                return Result.success();
            } else {
                return Result.error("CONTENT_TAG_REMOVE_ERROR", "移除内容标签失败");
            }
            
        } catch (Exception e) {
            log.error("移除内容标签失败", e);
            return Result.error("CONTENT_TAG_REMOVE_ERROR", "移除内容标签失败: " + e.getMessage());
        }
    }

    @Override
    public Result<List<ContentTagResponse>> getContentTags(Long contentId) {
        try {
            log.debug("获取内容的标签列表: 内容ID={}", contentId);
            
            if (contentId == null) {
                return Result.error("INVALID_PARAM", "内容ID不能为空");
            }
            
            List<ContentTag> contentTags = contentTagService.selectByContentId(contentId);
            List<ContentTagResponse> responses = new ArrayList<>();
            
            for (ContentTag contentTag : contentTags) {
                Tag tag = tagService.getTagById(contentTag.getTagId());
                if (tag != null) {
                    responses.add(convertToContentTagResponse(contentTag, tag));
                }
            }
            
            return Result.success(responses);
            
        } catch (Exception e) {
            log.error("获取内容的标签列表失败", e);
            return Result.error("CONTENT_TAG_QUERY_ERROR", "获取内容的标签列表失败: " + e.getMessage());
        }
    }

    @Override
    public Result<List<ContentTagResponse>> getTagContents(Long tagId) {
        try {
            log.debug("获取标签的内容列表: 标签ID={}", tagId);
            
            if (tagId == null) {
                return Result.error("INVALID_PARAM", "标签ID不能为空");
            }
            
            List<ContentTag> contentTags = contentTagService.selectByTagId(tagId);
            Tag tag = tagService.getTagById(tagId);
            
            List<ContentTagResponse> responses = contentTags.stream()
                    .map(contentTag -> convertToContentTagResponse(contentTag, tag))
                    .collect(Collectors.toList());
            
            return Result.success(responses);
            
        } catch (Exception e) {
            log.error("获取标签的内容列表失败", e);
            return Result.error("CONTENT_TAG_QUERY_ERROR", "获取标签的内容列表失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Integer> batchAddContentTags(Long contentId, List<Long> tagIds, Long operatorId) {
        try {
            log.info("批量为内容添加标签: 内容ID={}, 标签数量={}", contentId, tagIds.size());
            
            if (contentId == null || tagIds == null || tagIds.isEmpty()) {
                return Result.error("INVALID_PARAM", "参数不能为空");
            }
            
            int result = contentTagService.batchAddContentTags(contentId, tagIds);
            tagIds.forEach(tagId -> {
                try { 
                    tagService.increaseUsageCount(tagId); 
                } catch (Exception e) { 
                    log.warn("增加标签使用次数失败: tagId={}", tagId, e);
                }
            });
            
            log.info("批量添加内容标签完成: 成功数量={}", result);
            return Result.success(result);
            
        } catch (Exception e) {
            log.error("批量为内容添加标签失败", e);
            return Result.error("CONTENT_TAG_BATCH_ERROR", "批量添加失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Integer> batchRemoveContentTags(Long contentId, List<Long> tagIds, Long operatorId) {
        try {
            log.info("批量移除内容标签: 内容ID={}, 标签数量={}", contentId, tagIds.size());
            
            if (contentId == null || tagIds == null || tagIds.isEmpty()) {
                return Result.error("INVALID_PARAM", "参数不能为空");
            }
            
            int result = contentTagService.batchRemoveContentTags(contentId, tagIds);
            tagIds.forEach(tagId -> {
                try { 
                    tagService.decreaseUsageCount(tagId); 
                } catch (Exception e) { 
                    log.warn("减少标签使用次数失败: tagId={}", tagId, e);
                }
            });
            
            log.info("批量移除内容标签完成: 成功数量={}", result);
            return Result.success(result);
            
        } catch (Exception e) {
            log.error("批量移除内容标签失败", e);
            return Result.error("CONTENT_TAG_BATCH_ERROR", "批量移除失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Integer> replaceContentTags(Long contentId, List<Long> newTagIds, Long operatorId) {
        try {
            log.info("替换内容标签: 内容ID={}, 新标签数量={}", contentId, newTagIds.size());
            
            if (contentId == null || newTagIds == null) {
                return Result.error("INVALID_PARAM", "参数不能为空");
            }
            
            List<Long> oldTagIds = contentTagService.getContentTagIds(contentId);
            int result = contentTagService.replaceContentTags(contentId, newTagIds);
            
            // 更新使用次数
            oldTagIds.forEach(tagId -> {
                try { 
                    tagService.decreaseUsageCount(tagId); 
                } catch (Exception e) { 
                    log.warn("减少标签使用次数失败: tagId={}", tagId, e);
                }
            });
            newTagIds.forEach(tagId -> {
                try { 
                    tagService.increaseUsageCount(tagId); 
                } catch (Exception e) { 
                    log.warn("增加标签使用次数失败: tagId={}", tagId, e);
                }
            });
            
            log.info("替换内容标签完成: 替换数量={}", result);
            return Result.success(result);
            
        } catch (Exception e) {
            log.error("替换内容标签失败", e);
            return Result.error("CONTENT_TAG_REPLACE_ERROR", "替换失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Integer> copyContentTags(Long sourceContentId, Long targetContentId, Long operatorId) {
        try {
            log.info("复制内容标签: 源内容ID={}, 目标内容ID={}", sourceContentId, targetContentId);
            
            if (sourceContentId == null || targetContentId == null) {
                return Result.error("INVALID_PARAM", "源内容ID和目标内容ID不能为空");
            }
            
            int result = contentTagService.copyContentTags(sourceContentId, targetContentId);
            log.info("复制内容标签完成: 复制数量={}", result);
            return Result.success(result);
            
        } catch (Exception e) {
            log.error("复制内容标签失败", e);
            return Result.error("CONTENT_TAG_COPY_ERROR", "复制失败: " + e.getMessage());
        }
    }

    @Override
    public Result<List<Long>> getRelatedContents(Long contentId, Integer limit) {
        try {
            log.debug("获取相关内容: 内容ID={}, 限制数量={}", contentId, limit);
            
            if (contentId == null) {
                return Result.error("INVALID_PARAM", "内容ID不能为空");
            }
            
            List<Long> relatedContentIds = contentTagService.getRelatedContentIds(contentId, limit);
            return Result.success(relatedContentIds);
            
        } catch (Exception e) {
            log.error("获取相关内容失败", e);
            return Result.error("CONTENT_RELATED_ERROR", "获取相关内容失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Boolean> hasCommonTags(Long contentId1, Long contentId2) {
        try {
            log.debug("检查内容是否有共同标签: 内容1={}, 内容2={}", contentId1, contentId2);
            
            if (contentId1 == null || contentId2 == null) {
                return Result.error("INVALID_PARAM", "内容ID不能为空");
            }
            
            boolean hasCommon = contentTagService.hasCommonTags(contentId1, contentId2);
            return Result.success(hasCommon);
            
        } catch (Exception e) {
            log.error("检查内容是否有共同标签失败", e);
            return Result.error("CONTENT_TAG_CHECK_ERROR", "检查失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Integer> getCommonTagCount(Long contentId1, Long contentId2) {
        try {
            log.debug("获取内容共同标签数量: 内容1={}, 内容2={}", contentId1, contentId2);
            
            if (contentId1 == null || contentId2 == null) {
                return Result.error("INVALID_PARAM", "内容ID不能为空");
            }
            
            int count = contentTagService.getCommonTagCount(contentId1, contentId2);
            return Result.success(count);
            
        } catch (Exception e) {
            log.error("获取内容共同标签数量失败", e);
            return Result.error("CONTENT_TAG_COUNT_ERROR", "统计失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Integer> countContentTags(Long contentId) {
        try {
            if (contentId == null) {
                return Result.error("INVALID_PARAM", "内容ID不能为空");
            }
            
            int count = contentTagService.countTagsByContentId(contentId);
            return Result.success(count);
            
        } catch (Exception e) {
            return Result.error("COUNT_ERROR", "统计失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Integer> countTagContents(Long tagId) {
        try {
            if (tagId == null) {
                return Result.error("INVALID_PARAM", "标签ID不能为空");
            }
            
            int count = contentTagService.countContentsByTagId(tagId);
            return Result.success(count);
            
        } catch (Exception e) {
            return Result.error("COUNT_ERROR", "统计失败: " + e.getMessage());
        }
    }

    @Override
    public Result<List<Map<String, Object>>> getRecentContentTags(Integer limit) {
        try {
            List<Map<String, Object>> recentTags = contentTagService.getRecentContentTags(limit);
            return Result.success(recentTags);
        } catch (Exception e) {
            return Result.error("QUERY_ERROR", "查询失败: " + e.getMessage());
        }
    }

    @Override
    public Result<List<Map<String, Object>>> getContentTagSummary(List<Long> contentIds) {
        try {
            if (contentIds == null || contentIds.isEmpty()) {
                return Result.error("INVALID_PARAM", "内容ID列表不能为空");
            }
            
            List<Map<String, Object>> summary = contentTagService.getContentTagSummary(contentIds);
            return Result.success(summary);
            
        } catch (Exception e) {
            log.error("获取内容标签摘要失败", e);
            return Result.error("CONTENT_TAG_SUMMARY_ERROR", "获取摘要失败: " + e.getMessage());
        }
    }

    @Override
    public Result<List<Map<String, Object>>> recommendTagsForContent(Long contentId, Integer limit) {
        // 简化实现
        return Result.error("NOT_IMPLEMENTED", "功能暂未实现");
    }

    @Override
    public Result<Map<String, Object>> getContentTagAnalysis(Long contentId) {
        // 简化实现
        return Result.error("NOT_IMPLEMENTED", "功能暂未实现");
    }

    @Override
    public Result<List<Map<String, Object>>> getContentTagsWithStats(Long contentId) {
        // 简化实现
        return Result.error("NOT_IMPLEMENTED", "功能暂未实现");
    }

    @Override
    public Result<Integer> cleanupInvalidContentTags(Long operatorId) {
        try {
            int cleanedCount = contentTagService.cleanupInvalidContentTags();
            return Result.success(cleanedCount);
        } catch (Exception e) {
            return Result.error("CLEANUP_ERROR", "清理失败: " + e.getMessage());
        }
    }

    @Override
    public Result<String> healthCheck() {
        try {
            List<ContentTag> allContentTags = contentTagService.getAllContentTags();
            String healthStatus = String.format("内容标签系统运行正常。关联数量: %d", allContentTags.size());
            return Result.success(healthStatus);
        } catch (Exception e) {
            return Result.error("HEALTH_CHECK_ERROR", "健康检查失败: " + e.getMessage());
        }
    }

    // 私有工具方法
    private ContentTagResponse convertToContentTagResponse(ContentTag contentTag, Tag tag) {
        ContentTagResponse response = new ContentTagResponse();
        BeanUtils.copyProperties(contentTag, response);
        if (tag != null) {
            response.setTagName(tag.getName());
            response.setTagType(tag.getTagType());
            response.setTagUsageCount(tag.getUsageCount());
        }
        return response;
    }
}