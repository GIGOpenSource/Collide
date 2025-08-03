package com.gig.collide.tag.domain.service.impl;

import com.gig.collide.base.response.PageResponse;
import com.gig.collide.tag.domain.entity.ContentTag;
import com.gig.collide.tag.domain.entity.Tag;
import com.gig.collide.tag.domain.service.ContentTagService;
import com.gig.collide.tag.domain.service.TagService;
import com.gig.collide.tag.domain.service.UserTagService;
import com.gig.collide.tag.infrastructure.mapper.ContentTagMapper;
import com.gig.collide.api.tag.request.ContentTagQueryRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 内容标签领域服务实现
 * 
 * @author GIG Team
 * @version 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ContentTagServiceImpl implements ContentTagService {

    private final ContentTagMapper contentTagMapper;
    private final TagService tagService;
    private final UserTagService userTagService;

    // 内容最多可拥有的标签数量
    private static final int MAX_CONTENT_TAGS = 9;

    // =================== 内容标签管理 ===================

    @Override
    @Transactional
    public boolean addContentTag(Long contentId, Long tagId) {
        try {
            log.info("为内容添加标签: contentId={}, tagId={}", contentId, tagId);
            
            // 参数验证
            if (contentId == null || contentId <= 0) {
                throw new RuntimeException("内容ID无效");
            }
            
            if (tagId == null || tagId <= 0) {
                throw new RuntimeException("标签ID无效");
            }
            
            // 验证标签存在且启用
            Tag tag = tagService.getActiveTag(tagId);
            if (tag == null) {
                throw new RuntimeException("标签不存在或已禁用");
            }
            
            // 检查内容标签数量限制
            if (!canContentHaveMoreTags(contentId)) {
                throw new RuntimeException("内容标签数量已达上限（" + MAX_CONTENT_TAGS + "个）");
            }
            
            // 检查是否已包含该标签
            if (hasContentTag(contentId, tagId)) {
                log.warn("内容已包含该标签: contentId={}, tagId={}", contentId, tagId);
                return true; // 已包含视为成功
            }
            
            // 创建内容标签关系
            ContentTag contentTag = ContentTag.create(contentId, tagId);
            int result = contentTagMapper.insert(contentTag);
            
            if (result > 0) {
                // 更新标签内容数
                tagService.incrementContentCount(tagId, 1);
                
                log.info("为内容添加标签成功: contentId={}, tagId={}", contentId, tagId);
                return true;
            } else {
                throw new RuntimeException("创建内容标签关系失败");
            }
        } catch (Exception e) {
            log.error("为内容添加标签失败: contentId={}, tagId={}", contentId, tagId, e);
            throw new RuntimeException("添加内容标签失败: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public boolean removeContentTag(Long contentId, Long tagId) {
        try {
            log.info("移除内容标签: contentId={}, tagId={}", contentId, tagId);
            
            // 参数验证
            if (contentId == null || contentId <= 0 || tagId == null || tagId <= 0) {
                throw new RuntimeException("参数无效");
            }
            
            // 检查是否包含该标签
            if (!hasContentTag(contentId, tagId)) {
                log.warn("内容不包含该标签: contentId={}, tagId={}", contentId, tagId);
                return true; // 不包含视为成功
            }
            
            // 删除内容标签关系
            int result = contentTagMapper.batchDeleteContentTags(contentId, Arrays.asList(tagId));
            
            if (result > 0) {
                // 更新标签内容数
                tagService.decrementContentCount(tagId, 1);
                
                log.info("移除内容标签成功: contentId={}, tagId={}", contentId, tagId);
                return true;
            } else {
                throw new RuntimeException("删除内容标签关系失败");
            }
        } catch (Exception e) {
            log.error("移除内容标签失败: contentId={}, tagId={}", contentId, tagId, e);
            throw new RuntimeException("移除内容标签失败: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public List<Long> batchAddContentTags(Long contentId, List<Long> tagIds) {
        try {
            log.info("批量为内容添加标签: contentId={}, tagIds={}", contentId, tagIds);
            
            if (contentId == null || contentId <= 0 || tagIds == null || tagIds.isEmpty()) {
                return new ArrayList<>();
            }
            
            // 去重并验证数量
            List<Long> uniqueTagIds = tagIds.stream().distinct().collect(Collectors.toList());
            
            // 检查内容当前标签数量
            Integer currentCount = getContentTagCount(contentId);
            int remainingCount = MAX_CONTENT_TAGS - currentCount;
            
            if (remainingCount <= 0) {
                throw new RuntimeException("内容标签数量已达上限（" + MAX_CONTENT_TAGS + "个）");
            }
            
            // 限制批量添加数量
            if (uniqueTagIds.size() > remainingCount) {
                uniqueTagIds = uniqueTagIds.subList(0, remainingCount);
                log.warn("批量添加内容标签数量超限，已截取: contentId={}, requested={}, actual={}", 
                        contentId, tagIds.size(), uniqueTagIds.size());
            }
            
            List<Long> successTagIds = new ArrayList<>();
            
            for (Long tagId : uniqueTagIds) {
                try {
                    if (addContentTag(contentId, tagId)) {
                        successTagIds.add(tagId);
                    }
                } catch (Exception e) {
                    log.warn("批量添加内容标签时跳过失败项: contentId={}, tagId={}, error={}", 
                            contentId, tagId, e.getMessage());
                }
            }
            
            log.info("批量为内容添加标签完成: contentId={}, requested={}, success={}", 
                    contentId, uniqueTagIds.size(), successTagIds.size());
            
            return successTagIds;
        } catch (Exception e) {
            log.error("批量为内容添加标签失败: contentId={}, tagIds={}", contentId, tagIds, e);
            throw new RuntimeException("批量添加内容标签失败: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public boolean batchRemoveContentTags(Long contentId, List<Long> tagIds) {
        try {
            log.info("批量移除内容标签: contentId={}, tagIds={}", contentId, tagIds);
            
            if (contentId == null || contentId <= 0 || tagIds == null || tagIds.isEmpty()) {
                return true;
            }
            
            // 获取实际包含的标签
            List<Long> containedTagIds = contentTagMapper.batchCheckContentTags(contentId, tagIds);
            if (containedTagIds.isEmpty()) {
                log.info("内容不包含任何指定标签，跳过操作: contentId={}", contentId);
                return true;
            }
            
            // 批量删除内容标签关系
            int result = contentTagMapper.batchDeleteContentTags(contentId, containedTagIds);
            
            if (result > 0) {
                // 批量更新标签内容数
                for (Long tagId : containedTagIds) {
                    tagService.decrementContentCount(tagId, 1);
                }
                
                log.info("批量移除内容标签成功: contentId={}, count={}", contentId, result);
                return true;
            } else {
                throw new RuntimeException("批量删除内容标签关系失败");
            }
        } catch (Exception e) {
            log.error("批量移除内容标签失败: contentId={}, tagIds={}", contentId, tagIds, e);
            throw new RuntimeException("批量移除内容标签失败: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public boolean replaceContentTags(Long contentId, List<Long> tagIds) {
        try {
            log.info("替换内容标签: contentId={}, newTagIds={}", contentId, tagIds);
            
            if (contentId == null || contentId <= 0) {
                throw new RuntimeException("内容ID无效");
            }
            
            // 验证新标签数量
            if (tagIds != null && tagIds.size() > MAX_CONTENT_TAGS) {
                throw new RuntimeException("内容标签数量超过上限（" + MAX_CONTENT_TAGS + "个）");
            }
            
            // 获取内容当前的标签
            List<Long> currentTagIds = getContentTagIds(contentId);
            
            // 删除所有当前标签
            if (!currentTagIds.isEmpty()) {
                batchRemoveContentTags(contentId, currentTagIds);
            }
            
            // 添加新的标签
            if (tagIds != null && !tagIds.isEmpty()) {
                List<Long> successTagIds = batchAddContentTags(contentId, tagIds);
                log.info("替换内容标签完成: contentId={}, success={}/{}", 
                        contentId, successTagIds.size(), tagIds.size());
            }
            
            return true;
        } catch (Exception e) {
            log.error("替换内容标签失败: contentId={}, tagIds={}", contentId, tagIds, e);
            throw new RuntimeException("替换内容标签失败: " + e.getMessage(), e);
        }
    }

    // =================== 内容标签查询 ===================

    @Override
    public List<Long> getContentTagIds(Long contentId) {
        try {
            if (contentId == null || contentId <= 0) {
                return new ArrayList<>();
            }
            return contentTagMapper.getContentTagIds(contentId);
        } catch (Exception e) {
            log.error("获取内容标签ID列表失败: contentId={}", contentId, e);
            return new ArrayList<>();
        }
    }

    @Override
    public List<ContentTag> getContentTags(Long contentId) {
        try {
            if (contentId == null || contentId <= 0) {
                return new ArrayList<>();
            }
            return contentTagMapper.getContentTagsWithDetails(contentId);
        } catch (Exception e) {
            log.error("获取内容标签详情失败: contentId={}", contentId, e);
            return new ArrayList<>();
        }
    }

    @Override
    public Integer getContentTagCount(Long contentId) {
        try {
            if (contentId == null || contentId <= 0) {
                return 0;
            }
            Integer count = contentTagMapper.countContentTags(contentId);
            return count != null ? count : 0;
        } catch (Exception e) {
            log.error("获取内容标签数量失败: contentId={}", contentId, e);
            return 0;
        }
    }

    @Override
    public boolean hasContentTag(Long contentId, Long tagId) {
        try {
            if (contentId == null || contentId <= 0 || tagId == null || tagId <= 0) {
                return false;
            }
            return contentTagMapper.hasContentTag(contentId, tagId);
        } catch (Exception e) {
            log.error("检查内容是否包含标签失败: contentId={}, tagId={}", contentId, tagId, e);
            return false;
        }
    }

    @Override
    public Map<Long, Boolean> batchCheckContentTags(Long contentId, List<Long> tagIds) {
        try {
            Map<Long, Boolean> result = new HashMap<>();
            
            if (contentId == null || contentId <= 0 || tagIds == null || tagIds.isEmpty()) {
                return result;
            }
            
            // 初始化所有为false
            for (Long tagId : tagIds) {
                result.put(tagId, false);
            }
            
            // 获取已包含的标签
            List<Long> containedTagIds = contentTagMapper.batchCheckContentTags(contentId, tagIds);
            
            // 更新已包含的为true
            for (Long tagId : containedTagIds) {
                result.put(tagId, true);
            }
            
            return result;
        } catch (Exception e) {
            log.error("批量检查内容标签失败: contentId={}, tagIds={}", contentId, tagIds, e);
            return new HashMap<>();
        }
    }

    // =================== 基于标签的内容查询 ===================

    @Override
    public List<Long> getContentsByTag(Long tagId, Integer limit) {
        try {
            if (tagId == null || tagId <= 0) {
                return new ArrayList<>();
            }
            
            if (limit == null || limit <= 0) {
                limit = 20; // 默认20个
            }
            
            return contentTagMapper.getContentsByTag(tagId, limit);
        } catch (Exception e) {
            log.error("根据标签查询内容失败: tagId={}, limit={}", tagId, limit, e);
            return new ArrayList<>();
        }
    }

    @Override
    public List<Long> getContentsByTagsAnd(List<Long> tagIds, Integer limit) {
        try {
            if (tagIds == null || tagIds.isEmpty()) {
                return new ArrayList<>();
            }
            
            if (limit == null || limit <= 0) {
                limit = 20; // 默认20个
            }
            
            return contentTagMapper.getContentsByTagsAnd(tagIds, limit);
        } catch (Exception e) {
            log.error("根据多标签AND查询内容失败: tagIds={}, limit={}", tagIds, limit, e);
            return new ArrayList<>();
        }
    }

    @Override
    public List<Long> getContentsByTagsOr(List<Long> tagIds, Integer limit) {
        try {
            if (tagIds == null || tagIds.isEmpty()) {
                return new ArrayList<>();
            }
            
            if (limit == null || limit <= 0) {
                limit = 20; // 默认20个
            }
            
            return contentTagMapper.getContentsByTagsOr(tagIds, limit);
        } catch (Exception e) {
            log.error("根据多标签OR查询内容失败: tagIds={}, limit={}", tagIds, limit, e);
            return new ArrayList<>();
        }
    }

    @Override
    public Long countContentsByTag(Long tagId) {
        try {
            if (tagId == null || tagId <= 0) {
                return 0L;
            }
            return contentTagMapper.countContentsByTag(tagId);
        } catch (Exception e) {
            log.error("统计标签内容数量失败: tagId={}", tagId, e);
            return 0L;
        }
    }

    @Override
    public List<Long> getTagLatestContents(Long tagId, Integer days, Integer limit) {
        try {
            if (tagId == null || tagId <= 0) {
                return new ArrayList<>();
            }
            
            if (days == null || days <= 0) {
                days = 7; // 默认7天
            }
            
            if (limit == null || limit <= 0) {
                limit = 10; // 默认10个
            }
            
            return contentTagMapper.getTagLatestContents(tagId, days, limit);
        } catch (Exception e) {
            log.error("获取标签最新内容失败: tagId={}, days={}, limit={}", tagId, days, limit, e);
            return new ArrayList<>();
        }
    }

    @Override
    public List<Long> getTagHotContents(Long tagId, Integer days, Integer limit) {
        try {
            if (tagId == null || tagId <= 0) {
                return new ArrayList<>();
            }
            
            if (days == null || days <= 0) {
                days = 7; // 默认7天
            }
            
            if (limit == null || limit <= 0) {
                limit = 10; // 默认10个
            }
            
            return contentTagMapper.getTagHotContents(tagId, days, limit);
        } catch (Exception e) {
            log.error("获取标签热门内容失败: tagId={}, days={}, limit={}", tagId, days, limit, e);
            return new ArrayList<>();
        }
    }

    // =================== 基于用户标签的内容推荐 ===================

    @Override
    public List<Long> getRecommendContentsByUserTags(Long userId, List<Long> excludeContentIds, Integer limit) {
        try {
            if (userId == null || userId <= 0) {
                return new ArrayList<>();
            }
            
            if (limit == null || limit <= 0) {
                limit = 20; // 默认20个
            }
            
            return contentTagMapper.getRecommendContentsByUserTags(userId, excludeContentIds, limit);
        } catch (Exception e) {
            log.error("基于用户标签推荐内容失败: userId={}, limit={}", userId, limit, e);
            return new ArrayList<>();
        }
    }

    @Override
    public List<Long> getRecommendHotContentsByUserTags(Long userId, Integer days, 
                                                       List<Long> excludeContentIds, Integer limit) {
        try {
            if (userId == null || userId <= 0) {
                return new ArrayList<>();
            }
            
            if (days == null || days <= 0) {
                days = 7; // 默认7天
            }
            
            if (limit == null || limit <= 0) {
                limit = 20; // 默认20个
            }
            
            return contentTagMapper.getRecommendHotContentsByUserTags(userId, days, excludeContentIds, limit);
        } catch (Exception e) {
            log.error("基于用户标签推荐热门内容失败: userId={}, days={}, limit={}", userId, days, limit, e);
            return new ArrayList<>();
        }
    }

    @Override
    public List<Long> getRelatedContentsByTags(Long contentId, Integer limit) {
        try {
            if (contentId == null || contentId <= 0) {
                return new ArrayList<>();
            }
            
            if (limit == null || limit <= 0) {
                limit = 10; // 默认10个
            }
            
            return contentTagMapper.getRelatedContentsByTags(contentId, limit);
        } catch (Exception e) {
            log.error("获取相关内容失败: contentId={}, limit={}", contentId, limit, e);
            return new ArrayList<>();
        }
    }

    @Override
    public List<Long> getRecommendContentsBySimilarUsers(Long userId, List<Long> similarUserIds, 
                                                        List<Long> excludeContentIds, Integer limit) {
        try {
            if (userId == null || userId <= 0 || similarUserIds == null || similarUserIds.isEmpty()) {
                return new ArrayList<>();
            }
            
            if (limit == null || limit <= 0) {
                limit = 20; // 默认20个
            }
            
            return contentTagMapper.getRecommendContentsBySimilarUsers(userId, similarUserIds, excludeContentIds, limit);
        } catch (Exception e) {
            log.error("基于相似用户推荐内容失败: userId={}, similarUsers={}, limit={}", 
                    userId, similarUserIds.size(), limit, e);
            return new ArrayList<>();
        }
    }

    // =================== 内容标签统计分析 ===================

    @Override
    public Map<String, Object> getTagContentStatistics(Long tagId) {
        try {
            Map<String, Object> statistics = new HashMap<>();
            
            if (tagId == null || tagId <= 0) {
                return statistics;
            }
            
            // 基础统计
            statistics.put("totalContentCount", countContentsByTag(tagId));
            
            // 最新内容
            List<Long> latestContents = getTagLatestContents(tagId, 7, 5);
            statistics.put("latestContentCount", latestContents.size());
            
            // 时间分布
            Map<String, Integer> timeDistribution = getTagContentTimeDistribution(tagId, 30);
            statistics.put("timeDistribution", timeDistribution);
            
            // 标签信息
            Tag tag = tagService.getTagById(tagId);
            if (tag != null) {
                statistics.put("tagName", tag.getTagName());
                statistics.put("tagWeight", tag.getWeight());
                statistics.put("tagHotness", tag.getHotness());
            }
            
            return statistics;
        } catch (Exception e) {
            log.error("获取标签内容统计信息失败: tagId={}", tagId, e);
            return new HashMap<>();
        }
    }

    @Override
    public Map<String, Integer> getTagContentTimeDistribution(Long tagId, Integer days) {
        try {
            if (tagId == null || tagId <= 0) {
                return new HashMap<>();
            }
            
            if (days == null || days <= 0) {
                days = 30; // 默认30天
            }
            
            List<Map<String, Object>> distributions = contentTagMapper.getTagContentTimeDistribution(tagId, days);
            
            Map<String, Integer> result = new HashMap<>();
            for (Map<String, Object> item : distributions) {
                String tagDate = item.get("tag_date").toString();
                Object contentCountObj = item.get("content_count");
                Integer contentCount = contentCountObj instanceof Long ? ((Long) contentCountObj).intValue() : (Integer) contentCountObj;
                result.put(tagDate, contentCount);
            }
            
            return result;
        } catch (Exception e) {
            log.error("获取标签内容时间分布失败: tagId={}, days={}", tagId, days, e);
            return new HashMap<>();
        }
    }

    @Override
    public Map<String, Integer> getContentTagWeightDistribution(Long contentId) {
        try {
            List<Map<String, Object>> distributions = contentTagMapper.getContentTagWeightDistribution(contentId);
            
            Map<String, Integer> result = new HashMap<>();
            for (Map<String, Object> item : distributions) {
                String weightRange = (String) item.get("weight_range");
                Object tagCountObj = item.get("tag_count");
                Integer tagCount = tagCountObj instanceof Long ? ((Long) tagCountObj).intValue() : (Integer) tagCountObj;
                result.put(weightRange, tagCount);
            }
            
            return result;
        } catch (Exception e) {
            log.error("获取内容标签权重分布失败: contentId={}", contentId, e);
            return new HashMap<>();
        }
    }

    @Override
    public Map<Long, Integer> getTagCooccurrenceAnalysis(Long tagId, Integer limit) {
        try {
            if (tagId == null || tagId <= 0) {
                return new HashMap<>();
            }
            
            if (limit == null || limit <= 0) {
                limit = 10; // 默认10个
            }
            
            List<Map<String, Object>> cooccurrences = contentTagMapper.getTagCooccurrenceAnalysis(tagId, limit);
            
            Map<Long, Integer> result = new HashMap<>();
            for (Map<String, Object> item : cooccurrences) {
                Object tagIdObj = item.get("cooccurrence_tag_id");
                Object countObj = item.get("cooccurrence_count");
                
                Long coTagId = tagIdObj instanceof Long ? (Long) tagIdObj : Long.valueOf(tagIdObj.toString());
                Integer count = countObj instanceof Long ? ((Long) countObj).intValue() : (Integer) countObj;
                
                result.put(coTagId, count);
            }
            
            return result;
        } catch (Exception e) {
            log.error("获取标签共现分析失败: tagId={}, limit={}", tagId, limit, e);
            return new HashMap<>();
        }
    }

    // =================== 分页查询接口 ===================

    @Override
    public PageResponse<ContentTag> queryContentTags(ContentTagQueryRequest request) {
        try {
            log.debug("分页查询内容标签: page={}, size={}", request.getCurrentPage(), request.getPageSize());
            
            // 计算分页参数
            int offset = (request.getCurrentPage() - 1) * request.getPageSize();
            
            // 查询内容标签列表
            List<ContentTag> contentTags = contentTagMapper.findContentTagsByCondition(
                    request.getTagId(),
                    request.getContentId(),
                    request.getTagStartDate(),
                    request.getTagEndDate(),
                    request.getTagStatus(),
                    request.getSortField(),
                    request.getSortDirection(),
                    offset,
                    request.getPageSize()
            );
            
            // 查询总数
            Long total = contentTagMapper.countContentTagsByCondition(
                    request.getTagId(),
                    request.getContentId(),
                    request.getTagStartDate(),
                    request.getTagEndDate(),
                    request.getTagStatus()
            );
            
            // 构建分页结果
            PageResponse<ContentTag> result = new PageResponse<>();
            result.setDatas(contentTags);
            result.setCurrentPage(request.getCurrentPage());
            result.setPageSize(request.getPageSize());
            result.setTotal(total);
            
            log.debug("内容标签查询结果: count={}, total={}", contentTags.size(), total);
            return result;
        } catch (Exception e) {
            log.error("分页查询内容标签失败", e);
            throw new RuntimeException("查询内容标签失败", e);
        }
    }

    @Override
    public PageResponse<ContentTag> queryTagContents(Long tagId, ContentTagQueryRequest request) {
        try {
            // 设置标签ID并查询
            request.setTagId(tagId);
            return queryContentTags(request);
        } catch (Exception e) {
            log.error("分页查询标签内容失败: tagId={}", tagId, e);
            throw new RuntimeException("查询标签内容失败", e);
        }
    }

    // =================== 智能推荐功能 ===================

    @Override
    public List<Long> getRecommendTagsForContent(Long contentId, String contentText, 
                                                List<Long> excludeTagIds, Integer limit) {
        try {
            if (contentId == null || contentId <= 0) {
                return new ArrayList<>();
            }
            
            if (limit == null || limit <= 0) {
                limit = 5; // 默认推荐5个
            }
            
            return contentTagMapper.getRecommendTagsForContent(contentId, excludeTagIds, limit);
        } catch (Exception e) {
            log.error("为内容推荐标签失败: contentId={}, limit={}", contentId, limit, e);
            return new ArrayList<>();
        }
    }

    @Override
    public List<Long> getRelatedContents(Long contentId, Integer limit) {
        return getRelatedContentsByTags(contentId, limit);
    }

    // =================== 权限验证 ===================

    @Override
    public boolean canContentHaveMoreTags(Long contentId) {
        try {
            if (contentId == null || contentId <= 0) {
                return false;
            }
            
            Integer currentCount = getContentTagCount(contentId);
            return currentCount < MAX_CONTENT_TAGS;
        } catch (Exception e) {
            log.error("检查内容是否可以添加更多标签失败: contentId={}", contentId, e);
            return false;
        }
    }

    @Override
    public boolean canContentHaveTag(Long contentId, Long tagId) {
        try {
            if (contentId == null || contentId <= 0 || tagId == null || tagId <= 0) {
                return false;
            }
            
            // 检查标签是否存在且启用
            if (!tagService.existsActiveTag(tagId)) {
                return false;
            }
            
            // 检查是否已包含该标签
            if (hasContentTag(contentId, tagId)) {
                return false;
            }
            
            // 检查是否还能添加更多标签
            return canContentHaveMoreTags(contentId);
        } catch (Exception e) {
            log.error("检查内容是否可以添加指定标签失败: contentId={}, tagId={}", contentId, tagId, e);
            return false;
        }
    }

    @Override
    public Integer getContentRemainingTagCount(Long contentId) {
        try {
            if (contentId == null || contentId <= 0) {
                return 0;
            }
            
            Integer currentCount = getContentTagCount(contentId);
            return Math.max(0, MAX_CONTENT_TAGS - currentCount);
        } catch (Exception e) {
            log.error("获取内容剩余可添加标签数量失败: contentId={}", contentId, e);
            return 0;
        }
    }

    // =================== 数据清理和管理 ===================

    @Override
    @Transactional
    public Integer cleanupInvalidContentTags(Long contentId) {
        try {
            log.info("清理内容无效标签: contentId={}", contentId);
            
            int cleanupCount = contentTagMapper.cleanupInvalidContentTags(contentId);
            
            if (cleanupCount > 0) {
                log.info("清理内容无效标签完成: contentId={}, count={}", contentId, cleanupCount);
            }
            
            return cleanupCount;
        } catch (Exception e) {
            log.error("清理内容无效标签失败: contentId={}", contentId, e);
            throw new RuntimeException("清理无效标签失败: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public boolean deleteAllContentTags(Long contentId) {
        try {
            log.info("删除内容所有标签: contentId={}", contentId);
            
            if (contentId == null || contentId <= 0) {
                throw new RuntimeException("内容ID无效");
            }
            
            // 获取内容当前的标签（用于更新标签内容数）
            List<Long> tagIds = getContentTagIds(contentId);
            
            // 删除所有标签记录
            int result = contentTagMapper.deleteAllContentTags(contentId);
            
            if (result > 0) {
                // 更新标签内容数
                for (Long tagId : tagIds) {
                    tagService.decrementContentCount(tagId, 1);
                }
                
                log.info("删除内容所有标签成功: contentId={}, count={}", contentId, result);
                return true;
            }
            
            return true; // 没有记录也视为成功
        } catch (Exception e) {
            log.error("删除内容所有标签失败: contentId={}", contentId, e);
            throw new RuntimeException("删除内容标签失败: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public boolean deleteAllTagContents(Long tagId) {
        try {
            log.info("删除标签所有内容关联: tagId={}", tagId);
            
            if (tagId == null || tagId <= 0) {
                throw new RuntimeException("标签ID无效");
            }
            
            // 获取内容数量（用于更新标签内容数）
            Long contentCount = countContentsByTag(tagId);
            
            // 删除所有内容关联
            int result = contentTagMapper.deleteAllTagContents(tagId);
            
            if (result > 0) {
                // 重置标签内容数为0
                tagService.decrementContentCount(tagId, contentCount.intValue());
                
                log.info("删除标签所有内容关联成功: tagId={}, count={}", tagId, result);
                return true;
            }
            
            return true; // 没有记录也视为成功
        } catch (Exception e) {
            log.error("删除标签所有内容关联失败: tagId={}", tagId, e);
            throw new RuntimeException("删除标签内容关联失败: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public boolean updateContentTagWeights(List<Long> tagIds) {
        try {
            log.info("批量更新内容标签权重影响: tagIds={}", tagIds);
            
            if (tagIds == null || tagIds.isEmpty()) {
                return true;
            }
            
            // 这里可以实现权重更新逻辑，比如重新计算标签热度等
            // 当前简化实现：触发标签热度重新计算
            for (Long tagId : tagIds) {
                tagService.recalculateTagHotness(tagId);
            }
            
            log.info("批量更新内容标签权重影响完成: count={}", tagIds.size());
            return true;
        } catch (Exception e) {
            log.error("批量更新内容标签权重失败: tagIds={}", tagIds, e);
            throw new RuntimeException("更新标签权重失败: " + e.getMessage(), e);
        }
    }

    // =================== 性能优化接口 ===================

    @Override
    public Map<Long, List<ContentTag>> batchGetContentTags(List<Long> contentIds) {
        try {
            if (contentIds == null || contentIds.isEmpty()) {
                return new HashMap<>();
            }
            
            List<ContentTag> allContentTags = contentTagMapper.batchGetContentTags(contentIds);
            
            // 按内容ID分组
            return allContentTags.stream()
                    .collect(Collectors.groupingBy(ContentTag::getContentId));
        } catch (Exception e) {
            log.error("批量获取内容标签失败: contentIds={}", contentIds, e);
            return new HashMap<>();
        }
    }

    @Override
    public Map<Long, Long> batchGetTagContentCounts(List<Long> tagIds) {
        try {
            if (tagIds == null || tagIds.isEmpty()) {
                return new HashMap<>();
            }
            
            List<Map<String, Object>> counts = contentTagMapper.batchGetTagContentCounts(tagIds);
            
            Map<Long, Long> result = new HashMap<>();
            for (Map<String, Object> item : counts) {
                Object tagIdObj = item.get("tag_id");
                Object countObj = item.get("content_count");
                
                Long tagId = tagIdObj instanceof Long ? (Long) tagIdObj : Long.valueOf(tagIdObj.toString());
                Long count = countObj instanceof Long ? (Long) countObj : Long.valueOf(countObj.toString());
                
                result.put(tagId, count);
            }
            
            return result;
        } catch (Exception e) {
            log.error("批量获取标签内容数量失败: tagIds={}", tagIds, e);
            return new HashMap<>();
        }
    }
}