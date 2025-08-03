package com.gig.collide.api.social;

import com.gig.collide.api.social.request.ContentCreateRequest;
import com.gig.collide.api.social.request.ContentQueryRequest;
import com.gig.collide.api.social.request.ContentUpdateRequest;
import com.gig.collide.api.social.vo.ContentStatsVO;
import com.gig.collide.api.social.vo.ContentVO;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.web.vo.Result;

import java.util.List;

/**
 * 社交内容门面服务接口
 * 
 * @author GIG Team
 * @since 2024-01-16
 */
public interface SocialContentFacadeService {

    /**
     * 创建内容
     */
    Result<Long> createContent(ContentCreateRequest request);

    /**
     * 更新内容
     */
    Result<Boolean> updateContent(ContentUpdateRequest request);

    /**
     * 删除内容
     */
    Result<Boolean> deleteContent(Long contentId, Long userId);

    /**
     * 获取内容详情
     */
    Result<ContentVO> getContentDetail(Long contentId, Long viewerUserId);

    /**
     * 分页查询内容
     */
    PageResponse<ContentVO> queryContent(ContentQueryRequest request);

    /**
     * 批量获取内容
     */
    Result<List<ContentVO>> getBatchContent(List<Long> contentIds, Long viewerUserId);

    /**
     * 获取内容统计信息
     */
    Result<ContentStatsVO> getContentStats(Long contentId, Long viewerUserId);

    /**
     * 批量获取内容统计信息
     */
    Result<List<ContentStatsVO>> getBatchContentStats(List<Long> contentIds, Long viewerUserId);

    /**
     * 检查内容访问权限
     */
    Result<Boolean> checkContentAccess(Long contentId, Long userId);

    /**
     * 获取用户内容数量
     */
    Result<Integer> getUserContentCount(Long userId);

    /**
     * 获取分类内容数量
     */
    Result<Integer> getCategoryContentCount(Long categoryId);

    /**
     * 增加内容浏览数
     */
    Result<Boolean> incrementViewCount(Long contentId, Long viewerUserId);
    
    // ========== 调试方法 ==========
    
    /**
     * 诊断内容统计数据一致性（调试用）
     */
    Result<Boolean> diagnoseContentStats(Long contentId);
    
    /**
     * 修复内容统计数据（调试用）
     */
    Result<Boolean> fixContentStats(Long contentId);
}