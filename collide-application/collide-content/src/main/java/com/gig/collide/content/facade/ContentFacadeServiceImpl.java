package com.gig.collide.content.facade;

import com.gig.collide.api.content.ContentFacadeService;
import com.gig.collide.api.content.request.ContentCreateRequest;
import com.gig.collide.api.content.request.ContentUpdateRequest;
import com.gig.collide.api.content.response.ContentResponse;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.web.vo.Result;
import com.gig.collide.content.domain.entity.Content;
import com.gig.collide.content.domain.service.ContentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 内容门面服务实现类 - 极简版
 * 专注于内容核心功能，12个核心方法
 * 
 * @author Collide
 * @version 2.0.0 (极简版)
 * @since 2024-01-01
 */
@Slf4j
@Service
@DubboService(version = "2.0.0", timeout = 5000)
@RequiredArgsConstructor
public class ContentFacadeServiceImpl implements ContentFacadeService {

    private final ContentService contentService;

    // =================== 核心CRUD功能（4个方法）===================

    @Override
    public Result<Void> createContent(ContentCreateRequest request) {
        try {
            log.info("创建内容请求: {}", request.getTitle());
            
            // 请求参数转换为实体
            Content content = convertToEntity(request);
            
            // 调用业务服务
            Content createdContent = contentService.createContent(content);
            
            log.info("内容创建成功: ID={}", createdContent.getId());
            return Result.success(null);
            
        } catch (IllegalArgumentException e) {
            log.warn("创建内容参数错误: {}", e.getMessage());
            return Result.error("INVALID_PARAMETER", e.getMessage());
        } catch (Exception e) {
            log.error("创建内容失败", e);
            return Result.error("CONTENT_CREATE_FAILED", "创建内容失败: " + e.getMessage());
        }
    }

    @Override
    public Result<ContentResponse> updateContent(ContentUpdateRequest request) {
        try {
            log.info("更新内容请求: ID={}", request.getId());
            
            // 请求参数转换为实体
            Content content = convertToEntity(request);
            
            // 调用业务服务
            Content updatedContent = contentService.updateContent(content);
            
            // 实体转换为响应
            ContentResponse response = convertToResponse(updatedContent);
            
            log.info("内容更新成功: ID={}", response.getId());
            return Result.success(response);
            
        } catch (IllegalArgumentException e) {
            log.warn("更新内容参数错误: {}", e.getMessage());
            return Result.error("INVALID_PARAMETER", e.getMessage());
        } catch (Exception e) {
            log.error("更新内容失败", e);
            return Result.error("CONTENT_UPDATE_FAILED", "更新内容失败: " + e.getMessage());
        }
    }

    @Override
    public Result<ContentResponse> getContentById(Long contentId, Boolean includeOffline) {
        try {
            log.debug("获取内容详情: ID={}", contentId);
            
            Content content = contentService.getContentById(contentId, includeOffline);
            
            if (content == null) {
                return Result.error("CONTENT_NOT_FOUND", "内容不存在");
            }
            
            ContentResponse response = convertToResponse(content);
            
            return Result.success(response);
            
        } catch (Exception e) {
            log.error("获取内容详情失败", e);
            return Result.error("CONTENT_GET_FAILED", "获取内容详情失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Void> deleteContent(Long contentId, Long operatorId) {
        try {
            log.info("删除内容请求: ID={}, 操作人={}", contentId, operatorId);
            
            boolean deleted = contentService.deleteContent(contentId, operatorId);
            
            if (deleted) {
                log.info("内容删除成功: ID={}", contentId);
                return Result.success(null);
            } else {
                return Result.error("CONTENT_NOT_FOUND", "内容删除失败，内容不存在或无权限");
            }
            
        } catch (IllegalArgumentException e) {
            log.warn("删除内容参数错误: {}", e.getMessage());
            return Result.error("INVALID_PARAMETER", e.getMessage());
        } catch (Exception e) {
            log.error("删除内容失败", e);
            return Result.error("CONTENT_DELETE_FAILED", "删除内容失败: " + e.getMessage());
        }
    }

    // =================== 万能查询功能（3个方法）===================

    @Override
    public Result<PageResponse<ContentResponse>> queryContentsByConditions(Long authorId, Long categoryId, String contentType,
                                                                          String status, String reviewStatus, Double minScore,
                                                                          Integer timeRange, String orderBy, String orderDirection,
                                                                          Integer currentPage, Integer pageSize) {
        try {
            log.debug("万能条件查询内容: authorId={}, categoryId={}, contentType={}", 
                     authorId, categoryId, contentType);
            
            // 调用Service层的万能查询方法
            List<Content> contents = contentService.getContentsByConditions(
                authorId, categoryId, contentType, status, reviewStatus, minScore,
                timeRange, orderBy, orderDirection, currentPage, pageSize
            );
            
            // 转换响应
            PageResponse<ContentResponse> pageResponse = convertToPageResponse(contents, currentPage, pageSize);
            
            return Result.success(pageResponse);
            
        } catch (Exception e) {
            log.error("万能条件查询内容失败", e);
            return Result.error("QUERY_FAILED", "查询内容失败: " + e.getMessage());
        }
    }

    @Override
    public Result<PageResponse<ContentResponse>> searchContents(String keyword, String contentType,
                                                              Integer currentPage, Integer pageSize) {
        try {
            log.debug("搜索内容: keyword={}, contentType={}", keyword, contentType);
            
            List<Content> contents = contentService.searchContents(keyword, contentType, null, currentPage, pageSize);
            
            PageResponse<ContentResponse> pageResponse = convertToPageResponse(contents, currentPage, pageSize);
            
            return Result.success(pageResponse);
            
        } catch (Exception e) {
            log.error("搜索内容失败", e);
            return Result.error("SEARCH_FAILED", "搜索失败: " + e.getMessage());
        }
    }

    @Override
    public Result<List<ContentResponse>> getRecommendedContents(Long userId, List<Long> excludeContentIds, Integer limit) {
        try {
            log.debug("获取推荐内容: userId={}, excludeContentIds.size={}, limit={}", 
                     userId, excludeContentIds != null ? excludeContentIds.size() : 0, limit);
            
            List<Content> contents = contentService.getRecommendedContents(userId, excludeContentIds, limit);
            
            if (CollectionUtils.isEmpty(contents)) {
                return Result.success(Collections.emptyList());
            }
            
            List<ContentResponse> responses = contents.stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());
            
            return Result.success(responses);
            
        } catch (Exception e) {
            log.error("获取推荐内容失败", e);
            return Result.error("GET_RECOMMENDED_FAILED", "获取推荐内容失败: " + e.getMessage());
        }
    }

    // =================== 状态管理功能（2个方法）===================

    @Override
    public Result<Boolean> updateContentStatus(Long contentId, String status, String reviewStatus, 
                                              Long operatorId, String comment) {
        try {
            log.info("更新内容状态: contentId={}, status={}, reviewStatus={}", contentId, status, reviewStatus);
            
            boolean result = true;
            
            // 更新内容状态
            if (status != null) {
                result = contentService.updateContentStatus(contentId, status);
            }
            
            // 更新审核状态
            if (result && reviewStatus != null) {
                result = contentService.updateReviewStatus(contentId, reviewStatus);
            }
            
            if (result) {
                return Result.success(true);
            } else {
                return Result.error("UPDATE_STATUS_FAILED", "更新状态失败");
            }
        } catch (Exception e) {
            log.error("更新内容状态失败", e);
            return Result.error("UPDATE_STATUS_FAILED", "更新状态失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Boolean> batchUpdateStatus(List<Long> ids, String status) {
        try {
            log.info("批量更新内容状态: ids.size={}, status={}", 
                    ids != null ? ids.size() : 0, status);
            
            boolean result = contentService.batchUpdateStatus(ids, status);
            
            if (result) {
                return Result.success(true);
            } else {
                return Result.error("BATCH_UPDATE_FAILED", "批量更新状态失败");
            }
        } catch (Exception e) {
            log.error("批量更新内容状态失败", e);
            return Result.error("BATCH_UPDATE_FAILED", "批量更新失败: " + e.getMessage());
        }
    }

    // =================== 统计管理功能（2个方法）===================

    @Override
    public Result<Boolean> updateContentStats(Long contentId, Long viewCount, Long likeCount, 
                                             Long commentCount, Long favoriteCount, Double score) {
        try {
            log.info("更新内容统计信息: contentId={}", contentId);
            
            boolean result = contentService.updateContentStats(contentId, viewCount, likeCount, 
                                                             commentCount, favoriteCount);
            
            if (result) {
                return Result.success(true);
            } else {
                return Result.error("UPDATE_STATS_FAILED", "更新统计信息失败");
            }
        } catch (Exception e) {
            log.error("更新内容统计信息失败", e);
            return Result.error("UPDATE_STATS_FAILED", "更新统计信息失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Long> increaseViewCount(Long contentId, Integer increment) {
        try {
            log.debug("增加浏览量: contentId={}, increment={}", contentId, increment);
            
            Long newCount = contentService.increaseViewCount(contentId, increment);
            
            return Result.success(newCount);
        } catch (Exception e) {
            log.error("增加浏览量失败", e);
            return Result.error("INCREASE_VIEW_FAILED", "增加浏览量失败: " + e.getMessage());
        }
    }

    // =================== 数据同步功能（1个方法）===================

    @Override
    public Result<Integer> syncExternalData(String syncType, Long targetId, Map<String, Object> syncData) {
        try {
            log.info("同步外部数据: syncType={}, targetId={}", syncType, targetId);
            
            int count = 0;
            
            switch (syncType.toUpperCase()) {
                case "AUTHOR":
                    // 同步作者信息
                    String nickname = (String) syncData.get("nickname");
                    String avatar = (String) syncData.get("avatar");
                    // 这里应该调用具体的同步方法，但由于Service层已经简化，暂时返回0
                    count = 0; // contentService.updateAuthorInfo(targetId, nickname, avatar);
                    break;
                case "CATEGORY":
                    // 同步分类信息
                    String categoryName = (String) syncData.get("categoryName");
                    // 这里应该调用具体的同步方法，但由于Service层已经简化，暂时返回0
                    count = 0; // contentService.updateCategoryInfo(targetId, categoryName);
                    break;
                default:
                    log.warn("不支持的同步类型: {}", syncType);
                    return Result.error("UNSUPPORTED_SYNC_TYPE", "不支持的同步类型: " + syncType);
            }
            
            return Result.success(count);
        } catch (Exception e) {
            log.error("同步外部数据失败", e);
            return Result.error("SYNC_FAILED", "同步外部数据失败: " + e.getMessage());
        }
    }

    // =================== 私有转换方法 ===================

    /**
     * 创建请求转换为实体
     */
    private Content convertToEntity(ContentCreateRequest request) {
        Content content = new Content();
        BeanUtils.copyProperties(request, content);
        return content;
    }

    /**
     * 更新请求转换为实体
     */
    private Content convertToEntity(ContentUpdateRequest request) {
        Content content = new Content();
        BeanUtils.copyProperties(request, content);
        return content;
    }

    /**
     * 内容实体转换为响应
     */
    private ContentResponse convertToResponse(Content content) {
        ContentResponse response = new ContentResponse();
        BeanUtils.copyProperties(content, response);
        
        // 处理JSON字段的安全性
        if (content.getTags() == null || content.getTags().trim().isEmpty()) {
            response.setTags("[]");  // 默认空数组
        }
        if (content.getContentData() == null || content.getContentData().trim().isEmpty()) {
            response.setContentData("{}");  // 默认空对象
        }
        
        return response;
    }

    /**
     * 分页结果转换
     */
    private PageResponse<ContentResponse> convertToPageResponse(List<Content> contents, Integer currentPage, Integer pageSize) {
        PageResponse<ContentResponse> pageResponse = new PageResponse<>();
        
        if (CollectionUtils.isEmpty(contents)) {
            pageResponse.setDatas(Collections.emptyList());
            pageResponse.setTotal(0L);
        } else {
            List<ContentResponse> responseList = contents.stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());
            pageResponse.setDatas(responseList);
            pageResponse.setTotal((long) contents.size());
        }
        
        pageResponse.setCurrentPage(currentPage != null ? currentPage : 1);
        pageResponse.setPageSize(pageSize != null ? pageSize : 20);
        if (pageResponse.getPageSize() > 0) {
            pageResponse.setTotalPage((int) Math.ceil((double) pageResponse.getTotal() / pageResponse.getPageSize()));
        } else {
            pageResponse.setTotalPage(0);
        }
        pageResponse.setSuccess(true);
        
        return pageResponse;
    }
}