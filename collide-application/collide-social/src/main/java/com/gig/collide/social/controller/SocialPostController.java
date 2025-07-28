package com.gig.collide.social.controller;

import com.gig.collide.api.social.constant.PostTypeEnum;
import com.gig.collide.api.social.request.SocialPostCreateRequest;
import com.gig.collide.api.social.request.SocialPostQueryRequest;
import com.gig.collide.api.social.request.SocialPostUpdateRequest;
import com.gig.collide.api.social.response.SocialPostOperationResponse;
import com.gig.collide.api.social.response.SocialPostQueryResponse;
import com.gig.collide.api.social.response.data.BasicSocialPostInfo;
import com.gig.collide.api.social.response.data.SocialPostInfo;
import com.gig.collide.api.social.service.SocialPostFacadeService;
import com.gig.collide.social.facade.SocialPostFacadeServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 社交动态控制器
 * 提供社交动态管理的HTTP API接口
 * 
 * @author Collide Team
 * @version 1.0.0
 * @since 2024-12-19
 */
@RestController
@RequestMapping("/api/social/posts")
public class SocialPostController {

    @Autowired
    private SocialPostFacadeServiceImpl socialPostFacadeService;

    /**
     * 创建动态
     * 
     * @param request 创建请求
     * @return 创建结果
     */
    @PostMapping
    public SocialPostOperationResponse createPost(@Valid @RequestBody SocialPostCreateRequest request) {
        return socialPostFacadeService.createPost(request);
    }

    /**
     * 更新动态
     * 
     * @param request 更新请求
     * @return 更新结果
     */
    @PutMapping
    public SocialPostOperationResponse updatePost(@Valid @RequestBody SocialPostUpdateRequest request) {
        return socialPostFacadeService.updatePost(request);
    }

    /**
     * 删除动态
     * 
     * @param postId 动态ID
     * @param operatorUserId 操作用户ID
     * @param version 版本号
     * @return 删除结果
     */
    @DeleteMapping("/{postId}")
    public SocialPostOperationResponse deletePost(
            @PathVariable Long postId,
            @RequestParam Long operatorUserId,
            @RequestParam Integer version) {
        return socialPostFacadeService.deletePost(postId, operatorUserId, version);
    }

    /**
     * 发布动态（草稿转发布）
     * 
     * @param postId 动态ID
     * @param operatorUserId 操作用户ID
     * @param version 版本号
     * @return 发布结果
     */
    @PutMapping("/{postId}/publish")
    public SocialPostOperationResponse publishPost(
            @PathVariable Long postId,
            @RequestParam Long operatorUserId,
            @RequestParam Integer version) {
        return socialPostFacadeService.publishPost(postId, operatorUserId, version);
    }

    /**
     * 查询动态详情
     * 
     * @param postId 动态ID
     * @param viewerUserId 查看用户ID（可选）
     * @return 动态详情
     */
    @GetMapping("/{postId}")
    public SocialPostQueryResponse<SocialPostInfo> getPostDetail(
            @PathVariable Long postId,
            @RequestParam(required = false) Long viewerUserId) {
        return socialPostFacadeService.getPostDetail(postId, viewerUserId);
    }

    /**
     * 分页查询动态
     * 
     * @param request 查询请求
     * @return 动态列表
     */
    @PostMapping("/query")
    public SocialPostQueryResponse<List<BasicSocialPostInfo>> queryPosts(
            @Valid @RequestBody SocialPostQueryRequest request) {
        return socialPostFacadeService.queryPosts(request);
    }

    /**
     * 获取用户动态列表
     * 
     * @param authorId 作者ID
     * @param viewerUserId 查看用户ID（可选）
     * @param pageNum 页码
     * @param pageSize 页大小
     * @return 用户动态列表
     */
    @GetMapping("/user/{authorId}")
    public SocialPostQueryResponse<List<BasicSocialPostInfo>> getUserPosts(
            @PathVariable Long authorId,
            @RequestParam(required = false) Long viewerUserId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        return socialPostFacadeService.getUserPosts(authorId, viewerUserId, pageNum, pageSize);
    }

    /**
     * 获取关注用户动态时间线
     * 
     * @param userId 用户ID
     * @param pageNum 页码
     * @param pageSize 页大小
     * @return 关注时间线
     */
    @GetMapping("/timeline/following")
    public SocialPostQueryResponse<List<BasicSocialPostInfo>> getFollowingTimeline(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        return socialPostFacadeService.getFollowingTimeline(userId, pageNum, pageSize);
    }

    /**
     * 获取推荐动态
     * 
     * @param userId 用户ID
     * @param pageNum 页码
     * @param pageSize 页大小
     * @return 推荐动态列表
     */
    @GetMapping("/recommended")
    public SocialPostQueryResponse<List<BasicSocialPostInfo>> getRecommendedPosts(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        return socialPostFacadeService.getRecommendedPosts(userId, pageNum, pageSize);
    }

    /**
     * 获取热门动态
     * 
     * @param pageNum 页码
     * @param pageSize 页大小
     * @return 热门动态列表
     */
    @GetMapping("/hot")
    public SocialPostQueryResponse<List<BasicSocialPostInfo>> getHotPosts(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        return socialPostFacadeService.getHotPosts(pageNum, pageSize);
    }

    /**
     * 获取最新动态
     * 
     * @param pageNum 页码
     * @param pageSize 页大小
     * @return 最新动态列表
     */
    @GetMapping("/latest")
    public SocialPostQueryResponse<List<BasicSocialPostInfo>> getLatestPosts(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        return socialPostFacadeService.getLatestPosts(pageNum, pageSize);
    }

    /**
     * 根据话题搜索动态
     * 
     * @param topic 话题
     * @param pageNum 页码
     * @param pageSize 页大小
     * @return 搜索结果
     */
    @GetMapping("/search/topic")
    public SocialPostQueryResponse<List<BasicSocialPostInfo>> searchPostsByTopic(
            @RequestParam String topic,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        return socialPostFacadeService.searchPostsByTopic(topic, pageNum, pageSize);
    }

    /**
     * 根据关键词搜索动态
     * 
     * @param keyword 关键词
     * @param pageNum 页码
     * @param pageSize 页大小
     * @return 搜索结果
     */
    @GetMapping("/search/keyword")
    public SocialPostQueryResponse<List<BasicSocialPostInfo>> searchPostsByKeyword(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        return socialPostFacadeService.searchPostsByKeyword(keyword, pageNum, pageSize);
    }

    /**
     * 根据位置搜索动态
     * 
     * @param latitude 纬度
     * @param longitude 经度
     * @param radiusKm 搜索半径（公里）
     * @param pageNum 页码
     * @param pageSize 页大小
     * @return 搜索结果
     */
    @GetMapping("/search/location")
    public SocialPostQueryResponse<List<BasicSocialPostInfo>> searchPostsByLocation(
            @RequestParam BigDecimal latitude,
            @RequestParam BigDecimal longitude,
            @RequestParam Double radiusKm,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        return socialPostFacadeService.searchPostsByLocation(latitude, longitude, radiusKm, pageNum, pageSize);
    }

    /**
     * 获取动态类型统计
     * 
     * @param authorId 作者ID
     * @param startTime 开始时间（可选）
     * @param endTime 结束时间（可选）
     * @return 统计结果
     */
    @GetMapping("/statistics/type")
    public SocialPostQueryResponse<List<SocialPostFacadeService.PostTypeStats>> getPostTypeStatistics(
            @RequestParam Long authorId,
            @RequestParam(required = false) LocalDateTime startTime,
            @RequestParam(required = false) LocalDateTime endTime) {
        return socialPostFacadeService.getPostTypeStatistics(authorId, startTime, endTime);
    }

    /**
     * 批量获取动态详情
     * 
     * @param postIds 动态ID列表
     * @param viewerUserId 查看用户ID（可选）
     * @return 动态详情列表
     */
    @PostMapping("/batch")
    public SocialPostQueryResponse<List<SocialPostInfo>> batchGetPosts(
            @RequestBody List<Long> postIds,
            @RequestParam(required = false) Long viewerUserId) {
        return socialPostFacadeService.batchGetPosts(postIds, viewerUserId);
    }

    /**
     * 获取草稿动态列表
     * 
     * @param authorId 作者ID
     * @param pageNum 页码
     * @param pageSize 页大小
     * @return 草稿动态列表
     */
    @GetMapping("/drafts")
    public SocialPostQueryResponse<List<BasicSocialPostInfo>> getDraftPosts(
            @RequestParam Long authorId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        return socialPostFacadeService.getDraftPosts(authorId, pageNum, pageSize);
    }

    /**
     * 检查用户发布能力
     * 
     * @param userId 用户ID
     * @param postType 动态类型
     * @return 发布能力检查结果
     */
    @GetMapping("/capability/check")
    public SocialPostFacadeService.PostPublishCapability checkPostPublishCapability(
            @RequestParam Long userId,
            @RequestParam PostTypeEnum postType) {
        return socialPostFacadeService.checkPostPublishCapability(userId, postType);
    }
} 