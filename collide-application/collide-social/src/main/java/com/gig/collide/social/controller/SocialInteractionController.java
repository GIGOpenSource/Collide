package com.gig.collide.social.controller;

import com.gig.collide.api.social.constant.InteractionTypeEnum;
import com.gig.collide.api.social.request.SocialInteractionRequest;
import com.gig.collide.api.social.response.SocialInteractionResponse;
import com.gig.collide.api.social.response.SocialPostQueryResponse;
import com.gig.collide.api.social.response.data.SocialInteractionInfo;
import com.gig.collide.api.social.service.SocialInteractionFacadeService;
import com.gig.collide.social.facade.SocialInteractionFacadeServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 社交互动控制器
 * 提供社交互动管理的HTTP API接口
 * 
 * @author Collide Team
 * @version 1.0.0
 * @since 2024-12-19
 */
@RestController
@RequestMapping("/api/social/interactions")
public class SocialInteractionController {

    @Autowired
    private SocialInteractionFacadeServiceImpl socialInteractionFacadeService;

    /**
     * 执行通用互动操作
     * 
     * @param request 互动请求
     * @return 互动结果
     */
    @PostMapping
    public SocialInteractionResponse interact(@Valid @RequestBody SocialInteractionRequest request) {
        return socialInteractionFacadeService.interact(request);
    }

    /**
     * 取消互动操作
     * 
     * @param postId 动态ID
     * @param userId 用户ID
     * @param interactionType 互动类型
     * @return 取消结果
     */
    @DeleteMapping
    public SocialInteractionResponse cancelInteraction(
            @RequestParam Long postId,
            @RequestParam Long userId,
            @RequestParam InteractionTypeEnum interactionType) {
        return socialInteractionFacadeService.cancelInteraction(postId, userId, interactionType);
    }

    /**
     * 点赞动态
     * 
     * @param postId 动态ID
     * @param userId 用户ID
     * @param deviceInfo 设备信息（可选）
     * @param ipAddress IP地址（可选）
     * @return 点赞结果
     */
    @PostMapping("/like")
    public SocialInteractionResponse likePost(
            @RequestParam Long postId,
            @RequestParam Long userId,
            @RequestParam(required = false) String deviceInfo,
            @RequestParam(required = false) String ipAddress) {
        return socialInteractionFacadeService.likePost(postId, userId, deviceInfo, ipAddress);
    }

    /**
     * 取消点赞
     * 
     * @param postId 动态ID
     * @param userId 用户ID
     * @return 取消点赞结果
     */
    @DeleteMapping("/like")
    public SocialInteractionResponse unlikePost(
            @RequestParam Long postId,
            @RequestParam Long userId) {
        return socialInteractionFacadeService.unlikePost(postId, userId);
    }

    /**
     * 收藏动态
     * 
     * @param postId 动态ID
     * @param userId 用户ID
     * @param deviceInfo 设备信息（可选）
     * @param ipAddress IP地址（可选）
     * @return 收藏结果
     */
    @PostMapping("/favorite")
    public SocialInteractionResponse favoritePost(
            @RequestParam Long postId,
            @RequestParam Long userId,
            @RequestParam(required = false) String deviceInfo,
            @RequestParam(required = false) String ipAddress) {
        return socialInteractionFacadeService.favoritePost(postId, userId, deviceInfo, ipAddress);
    }

    /**
     * 取消收藏
     * 
     * @param postId 动态ID
     * @param userId 用户ID
     * @return 取消收藏结果
     */
    @DeleteMapping("/favorite")
    public SocialInteractionResponse unfavoritePost(
            @RequestParam Long postId,
            @RequestParam Long userId) {
        return socialInteractionFacadeService.unfavoritePost(postId, userId);
    }

    /**
     * 转发动态
     * 
     * @param postId 动态ID
     * @param userId 用户ID
     * @param shareComment 转发评论（可选）
     * @param deviceInfo 设备信息（可选）
     * @param ipAddress IP地址（可选）
     * @return 转发结果
     */
    @PostMapping("/share")
    public SocialInteractionResponse sharePost(
            @RequestParam Long postId,
            @RequestParam Long userId,
            @RequestParam(required = false) String shareComment,
            @RequestParam(required = false) String deviceInfo,
            @RequestParam(required = false) String ipAddress) {
        return socialInteractionFacadeService.sharePost(postId, userId, shareComment, deviceInfo, ipAddress);
    }

    /**
     * 记录浏览
     * 
     * @param postId 动态ID
     * @param userId 用户ID
     * @param deviceInfo 设备信息（可选）
     * @param ipAddress IP地址（可选）
     * @return 浏览记录结果
     */
    @PostMapping("/view")
    public SocialInteractionResponse viewPost(
            @RequestParam Long postId,
            @RequestParam Long userId,
            @RequestParam(required = false) String deviceInfo,
            @RequestParam(required = false) String ipAddress) {
        return socialInteractionFacadeService.viewPost(postId, userId, deviceInfo, ipAddress);
    }

    /**
     * 查询用户互动记录
     * 
     * @param userId 用户ID
     * @param interactionType 互动类型（可选）
     * @param pageNum 页码
     * @param pageSize 页大小
     * @return 用户互动记录
     */
    @GetMapping("/user/{userId}")
    public SocialPostQueryResponse<List<SocialInteractionInfo>> getUserInteractions(
            @PathVariable Long userId,
            @RequestParam(required = false) InteractionTypeEnum interactionType,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        return socialInteractionFacadeService.getUserInteractions(userId, interactionType, pageNum, pageSize);
    }

    /**
     * 查询动态的互动记录
     * 
     * @param postId 动态ID
     * @param interactionType 互动类型（可选）
     * @param pageNum 页码
     * @param pageSize 页大小
     * @return 动态互动记录
     */
    @GetMapping("/post/{postId}")
    public SocialPostQueryResponse<List<SocialInteractionInfo>> getPostInteractions(
            @PathVariable Long postId,
            @RequestParam(required = false) InteractionTypeEnum interactionType,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        return socialInteractionFacadeService.getPostInteractions(postId, interactionType, pageNum, pageSize);
    }

    /**
     * 查询用户的点赞列表
     * 
     * @param userId 用户ID
     * @param pageNum 页码
     * @param pageSize 页大小
     * @return 点赞列表
     */
    @GetMapping("/user/{userId}/likes")
    public SocialPostQueryResponse<List<SocialInteractionInfo>> getUserLikes(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        return socialInteractionFacadeService.getUserLikes(userId, pageNum, pageSize);
    }

    /**
     * 查询用户的收藏列表
     * 
     * @param userId 用户ID
     * @param pageNum 页码
     * @param pageSize 页大小
     * @return 收藏列表
     */
    @GetMapping("/user/{userId}/favorites")
    public SocialPostQueryResponse<List<SocialInteractionInfo>> getUserFavorites(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        return socialInteractionFacadeService.getUserFavorites(userId, pageNum, pageSize);
    }

    /**
     * 查询用户的转发列表
     * 
     * @param userId 用户ID
     * @param pageNum 页码
     * @param pageSize 页大小
     * @return 转发列表
     */
    @GetMapping("/user/{userId}/shares")
    public SocialPostQueryResponse<List<SocialInteractionInfo>> getUserShares(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        return socialInteractionFacadeService.getUserShares(userId, pageNum, pageSize);
    }

    /**
     * 查询动态的点赞用户列表
     * 
     * @param postId 动态ID
     * @param pageNum 页码
     * @param pageSize 页大小
     * @return 点赞用户列表
     */
    @GetMapping("/post/{postId}/likes")
    public SocialPostQueryResponse<List<SocialInteractionInfo>> getPostLikes(
            @PathVariable Long postId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        return socialInteractionFacadeService.getPostLikes(postId, pageNum, pageSize);
    }

    /**
     * 查询动态的收藏用户列表
     * 
     * @param postId 动态ID
     * @param pageNum 页码
     * @param pageSize 页大小
     * @return 收藏用户列表
     */
    @GetMapping("/post/{postId}/favorites")
    public SocialPostQueryResponse<List<SocialInteractionInfo>> getPostFavorites(
            @PathVariable Long postId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        return socialInteractionFacadeService.getPostFavorites(postId, pageNum, pageSize);
    }

    /**
     * 查询动态的转发用户列表
     * 
     * @param postId 动态ID
     * @param pageNum 页码
     * @param pageSize 页大小
     * @return 转发用户列表
     */
    @GetMapping("/post/{postId}/shares")
    public SocialPostQueryResponse<List<SocialInteractionInfo>> getPostShares(
            @PathVariable Long postId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        return socialInteractionFacadeService.getPostShares(postId, pageNum, pageSize);
    }

    /**
     * 批量查询用户对动态的互动状态
     * 
     * @param userId 用户ID
     * @param postIds 动态ID列表
     * @return 互动状态映射
     */
    @PostMapping("/status/batch")
    public Map<Long, SocialInteractionFacadeService.UserInteractionStatus> batchGetUserInteractionStatus(
            @RequestParam Long userId,
            @RequestBody List<Long> postIds) {
        return socialInteractionFacadeService.batchGetUserInteractionStatus(userId, postIds);
    }

    /**
     * 获取用户互动统计
     * 
     * @param userId 用户ID
     * @param startTime 开始时间（可选）
     * @param endTime 结束时间（可选）
     * @return 用户互动统计
     */
    @GetMapping("/statistics/user/{userId}")
    public SocialInteractionFacadeService.UserInteractionStatistics getUserInteractionStatistics(
            @PathVariable Long userId,
            @RequestParam(required = false) LocalDateTime startTime,
            @RequestParam(required = false) LocalDateTime endTime) {
        return socialInteractionFacadeService.getUserInteractionStatistics(userId, startTime, endTime);
    }

    /**
     * 获取动态互动统计
     * 
     * @param postId 动态ID
     * @return 动态互动统计
     */
    @GetMapping("/statistics/post/{postId}")
    public SocialInteractionFacadeService.PostInteractionStatistics getPostInteractionStatistics(
            @PathVariable Long postId) {
        return socialInteractionFacadeService.getPostInteractionStatistics(postId);
    }

    /**
     * 获取热门互动用户排行
     * 
     * @param interactionType 互动类型
     * @param startTime 开始时间（可选）
     * @param endTime 结束时间（可选）
     * @param limit 返回数量限制
     * @return 用户排行列表
     */
    @GetMapping("/ranking/users")
    public List<SocialInteractionFacadeService.UserInteractionRanking> getInteractionUserRanking(
            @RequestParam InteractionTypeEnum interactionType,
            @RequestParam(required = false) LocalDateTime startTime,
            @RequestParam(required = false) LocalDateTime endTime,
            @RequestParam(defaultValue = "50") Integer limit) {
        return socialInteractionFacadeService.getInteractionUserRanking(interactionType, startTime, endTime, limit);
    }
} 