package com.gig.collide.api.social.service;

import com.gig.collide.api.social.request.SocialPostCreateRequest;
import com.gig.collide.api.social.request.SocialPostQueryRequest;
import com.gig.collide.api.social.response.SocialPostResponse;
import com.gig.collide.api.social.response.data.SocialPostInfo;
import com.gig.collide.base.response.PageResponse;

/**
 * 社交服务 Facade 接口
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
public interface SocialFacadeService {

    /**
     * 发布社交动态
     *
     * @param createRequest 创建请求
     * @return 发布响应
     */
    SocialPostResponse publishPost(SocialPostCreateRequest createRequest);

    /**
     * 更新社交动态
     *
     * @param postId 动态ID
     * @param updateRequest 更新请求
     * @return 更新响应
     */
    SocialPostResponse updatePost(Long postId, SocialPostCreateRequest updateRequest);

    /**
     * 删除社交动态
     *
     * @param postId 动态ID
     * @param userId 用户ID（权限验证）
     * @return 删除响应
     */
    SocialPostResponse deletePost(Long postId, Long userId);

    /**
     * 分页查询社交动态
     *
     * @param queryRequest 查询请求
     * @return 动态列表响应
     */
    PageResponse<SocialPostInfo> pageQueryPosts(SocialPostQueryRequest queryRequest);

    /**
     * 查询动态详情
     *
     * @param postId 动态ID
     * @param currentUserId 当前用户ID（用于权限判断和个性化信息）
     * @return 动态详情
     */
    SocialPostInfo queryPostDetail(Long postId, Long currentUserId);

    /**
     * 获取用户时间线
     *
     * @param userId 用户ID
     * @param currentPage 当前页
     * @param pageSize 页大小
     * @return 时间线动态列表
     */
    PageResponse<SocialPostInfo> getUserTimeline(Long userId, Integer currentPage, Integer pageSize);

    /**
     * 获取关注用户的动态流
     *
     * @param userId 用户ID
     * @param currentPage 当前页
     * @param pageSize 页大小
     * @return 关注动态列表
     */
    PageResponse<SocialPostInfo> getFollowingFeed(Long userId, Integer currentPage, Integer pageSize);

    /**
     * 获取热门动态
     *
     * @param currentPage 当前页
     * @param pageSize 页大小
     * @param timeRange 时间范围（hour-近1小时，day-近1天，week-近1周）
     * @return 热门动态列表
     */
    PageResponse<SocialPostInfo> getHotPosts(Integer currentPage, Integer pageSize, String timeRange);

    /**
     * 获取附近动态
     *
     * @param longitude 经度
     * @param latitude 纬度
     * @param radius 搜索半径（公里）
     * @param currentPage 当前页
     * @param pageSize 页大小
     * @return 附近动态列表
     */
    PageResponse<SocialPostInfo> getNearbyPosts(Double longitude, Double latitude, Double radius, 
                                                Integer currentPage, Integer pageSize);

    /**
     * 搜索动态
     *
     * @param keyword 搜索关键词
     * @param currentPage 当前页
     * @param pageSize 页大小
     * @return 搜索结果
     */
    PageResponse<SocialPostInfo> searchPosts(String keyword, Integer currentPage, Integer pageSize);

    /**
     * 点赞/取消点赞动态
     *
     * @param postId 动态ID
     * @param userId 用户ID
     * @param isLike 是否点赞（true-点赞，false-取消点赞）
     * @return 操作响应
     */
    SocialPostResponse likePost(Long postId, Long userId, Boolean isLike);

    /**
     * 转发动态
     *
     * @param postId 动态ID
     * @param userId 用户ID
     * @param comment 转发评论（可选）
     * @return 转发响应
     */
    SocialPostResponse sharePost(Long postId, Long userId, String comment);

    /**
     * 举报动态
     *
     * @param postId 动态ID
     * @param userId 举报用户ID
     * @param reason 举报原因
     * @return 举报响应
     */
    SocialPostResponse reportPost(Long postId, Long userId, String reason);

    /**
     * 统计用户动态数量
     *
     * @param userId 用户ID
     * @return 动态数量
     */
    Long countUserPosts(Long userId);

    /**
     * 增加动态浏览数
     *
     * @param postId 动态ID
     * @param userId 用户ID（可选，用于去重）
     */
    void incrementViewCount(Long postId, Long userId);

    /**
     * 计算动态热度分数
     *
     * @param postId 动态ID
     * @return 热度分数
     */
    Double calculateHotScore(Long postId);
} 