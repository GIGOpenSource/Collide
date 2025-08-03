package com.gig.collide.social.controller;

import com.gig.collide.api.social.SocialContentFacadeService;
import com.gig.collide.api.social.request.ContentCreateRequest;
import com.gig.collide.api.social.request.ContentQueryRequest;
import com.gig.collide.api.social.request.ContentUpdateRequest;
import com.gig.collide.api.social.vo.ContentStatsVO;
import com.gig.collide.api.social.vo.ContentVO;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.web.vo.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * 社交内容控制器 - DDD重构版
 * 
 * @author GIG Team
 * @since 2024-01-16
 */
@Slf4j
@RestController
@RequestMapping("/api/social/content")
@RequiredArgsConstructor
public class SocialContentController {

    private final SocialContentFacadeService contentFacadeService;

    /**
     * 创建内容
     */
    @PostMapping("/create")
    public Result<Long> createContent(@Valid @RequestBody ContentCreateRequest request) {
        return contentFacadeService.createContent(request);
    }

    /**
     * 更新内容
     */
    @PutMapping("/update")
    public Result<Boolean> updateContent(@Valid @RequestBody ContentUpdateRequest request) {
        return contentFacadeService.updateContent(request);
    }

    /**
     * 删除内容
     */
    @DeleteMapping("/{contentId}")
    public Result<Boolean> deleteContent(@PathVariable Long contentId, @RequestParam Long userId) {
        return contentFacadeService.deleteContent(contentId, userId);
    }

    /**
     * 获取内容详情
     */
    @GetMapping("/{contentId}")
    public Result<ContentVO> getContent(@PathVariable Long contentId, @RequestParam(required = false) Long viewerUserId) {
        // 先获取内容详情
        Result<ContentVO> result = contentFacadeService.getContentDetail(contentId, viewerUserId);
        
        // 如果获取成功且有查看者，增加浏览数
        if (result.getSuccess() && viewerUserId != null) {
            contentFacadeService.incrementViewCount(contentId, viewerUserId);
        }
        
        return result;
    }

    /**
     * 分页查询内容
     */
    @PostMapping("/query")
    public PageResponse<ContentVO> queryContent(@RequestBody ContentQueryRequest request) {
        return contentFacadeService.queryContent(request);
    }

    /**
     * 获取用户的内容列表
     */
    @GetMapping("/user/{userId}")
    public PageResponse<ContentVO> getUserContent(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "1") int currentPage,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(required = false) Long viewerUserId) {
        ContentQueryRequest request = new ContentQueryRequest();
        request.setUserId(userId);
        request.setCurrentPage(currentPage);
        request.setPageSize(pageSize);
        request.setViewerUserId(viewerUserId);
        return contentFacadeService.queryContent(request);
    }

    /**
     * 获取分类下的内容
     */
    @GetMapping("/category/{categoryId}")
    public PageResponse<ContentVO> getCategoryContent(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "1") int currentPage,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(required = false) Long viewerUserId) {
        ContentQueryRequest request = new ContentQueryRequest();
        request.setCategoryId(categoryId);
        request.setCurrentPage(currentPage);
        request.setPageSize(pageSize);
        request.setViewerUserId(viewerUserId);
        return contentFacadeService.queryContent(request);
    }

    /**
     * 获取热门内容
     */
    @GetMapping("/hot")
    public PageResponse<ContentVO> getHotContent(
            @RequestParam(defaultValue = "1") int currentPage,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(required = false) Long viewerUserId) {
        ContentQueryRequest request = new ContentQueryRequest();
        request.setQueryType(ContentQueryRequest.QueryType.HOT.getCode());
        request.setCurrentPage(currentPage);
        request.setPageSize(pageSize);
        request.setViewerUserId(viewerUserId);
        return contentFacadeService.queryContent(request);
    }

    /**
     * 获取最新内容
     */
    @GetMapping("/latest")
    public PageResponse<ContentVO> getLatestContent(
            @RequestParam(defaultValue = "1") int currentPage,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(required = false) Long viewerUserId) {
        ContentQueryRequest request = new ContentQueryRequest();
        request.setQueryType(ContentQueryRequest.QueryType.LATEST.getCode());
        request.setCurrentPage(currentPage);
        request.setPageSize(pageSize);
        request.setViewerUserId(viewerUserId);
        return contentFacadeService.queryContent(request);
    }

    /**
     * 搜索内容
     */
    @GetMapping("/search")
    public PageResponse<ContentVO> searchContent(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") int currentPage,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(required = false) Long viewerUserId) {
        ContentQueryRequest request = new ContentQueryRequest();
        request.setKeyword(keyword);
        request.setQueryType(ContentQueryRequest.QueryType.SEARCH.getCode());
        request.setCurrentPage(currentPage);
        request.setPageSize(pageSize);
        request.setViewerUserId(viewerUserId);
        return contentFacadeService.queryContent(request);
    }

    /**
     * 批量获取内容
     */
    @PostMapping("/batch")
    public Result<List<ContentVO>> getBatchContent(@RequestBody List<Long> contentIds, 
                                                   @RequestParam(required = false) Long viewerUserId) {
        return contentFacadeService.getBatchContent(contentIds, viewerUserId);
    }

    /**
     * 获取内容统计信息
     */
    @GetMapping("/{contentId}/stats")
    public Result<ContentStatsVO> getContentStats(@PathVariable Long contentId, 
                                                   @RequestParam(required = false) Long viewerUserId) {
        return contentFacadeService.getContentStats(contentId, viewerUserId);
    }

    /**
     * 批量获取内容统计信息
     */
    @PostMapping("/stats/batch")
    public Result<List<ContentStatsVO>> getBatchContentStats(@RequestBody List<Long> contentIds,
                                                              @RequestParam(required = false) Long viewerUserId) {
        return contentFacadeService.getBatchContentStats(contentIds, viewerUserId);
    }

    /**
     * 检查内容访问权限
     */
    @GetMapping("/{contentId}/access")
    public Result<Boolean> checkContentAccess(@PathVariable Long contentId, @RequestParam Long userId) {
        return contentFacadeService.checkContentAccess(contentId, userId);
    }

    /**
     * 获取用户内容数量
     */
    @GetMapping("/count/user/{userId}")
    public Result<Integer> getUserContentCount(@PathVariable Long userId) {
        return contentFacadeService.getUserContentCount(userId);
    }

    /**
     * 获取分类内容数量
     */
    @GetMapping("/count/category/{categoryId}")
    public Result<Integer> getCategoryContentCount(@PathVariable Long categoryId) {
        return contentFacadeService.getCategoryContentCount(categoryId);
    }
    
    // ========== 调试接口 ==========
    
    /**
     * 诊断内容统计数据（调试用）
     */
    @PostMapping("/{contentId}/diagnose")
    public Result<Boolean> diagnoseContentStats(@PathVariable Long contentId) {
        return contentFacadeService.diagnoseContentStats(contentId);
    }
    
    /**
     * 修复内容统计数据（调试用）
     */
    @PostMapping("/{contentId}/fix-stats")
    public Result<Boolean> fixContentStats(@PathVariable Long contentId) {
        return contentFacadeService.fixContentStats(contentId);
    }
}