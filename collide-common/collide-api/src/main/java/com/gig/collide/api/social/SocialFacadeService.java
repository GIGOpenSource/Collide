package com.gig.collide.api.social;

import com.gig.collide.api.social.request.SocialDynamicCreateRequest;
import com.gig.collide.api.social.request.SocialDynamicQueryRequest;
import com.gig.collide.api.social.request.SocialDynamicUpdateRequest;
import com.gig.collide.api.social.response.SocialDynamicResponse;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.web.vo.Result;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 社交动态门面服务接口 - 重新设计版
 * 基于SocialDynamicService的25个核心方法，严格限制在Social模块内
 * 使用Result<T>统一包装返回结果，不包含跨模块调用
 *
 * @author GIG Team
 * @version 3.0.0 (重新设计版)
 * @since 2024-01-30
 */
public interface SocialFacadeService {

    // =================== 业务CRUD操作（Controller层需要） ===================

    /**
     * 创建动态
     * 包含用户验证、内容检查、冗余信息设置
     * 对应Service: createDynamic
     */
    Result<SocialDynamicResponse> createDynamic(SocialDynamicCreateRequest request);

    /**
     * 批量创建动态
     * 用于批量导入或管理员批量操作
     * 对应Service: batchCreateDynamics
     */
    Result<Integer> batchCreateDynamics(List<SocialDynamicCreateRequest> requests, Long operatorId);

    /**
     * 创建分享动态
     * 专门用于分享其他动态的场景
     * 对应Service: createShareDynamic
     */
    Result<SocialDynamicResponse> createShareDynamic(SocialDynamicCreateRequest request);

    /**
     * 更新动态内容
     * 只允许更新内容相关字段，包含权限验证
     */
    Result<SocialDynamicResponse> updateDynamic(SocialDynamicUpdateRequest request);

    /**
     * 删除动态
     * 逻辑删除，包含权限验证
     */
    Result<Void> deleteDynamic(Long dynamicId, Long operatorId);

    /**
     * 根据ID查询动态详情
     */
    Result<SocialDynamicResponse> getDynamicById(Long dynamicId, Boolean includeDeleted);

    /**
     * 分页查询动态列表
     * 支持多条件组合查询
     */
    Result<PageResponse<SocialDynamicResponse>> queryDynamics(SocialDynamicQueryRequest request);

    // =================== 核心查询方法（严格对应Service层7个） ===================

    /**
     * 根据用户ID分页查询动态
     * 对应Service: selectByUserId
     */
    Result<PageResponse<SocialDynamicResponse>> selectByUserId(Integer currentPage, Integer pageSize, Long userId, String status, String dynamicType);

    /**
     * 根据动态类型分页查询
     * 对应Service: selectByDynamicType
     */
    Result<PageResponse<SocialDynamicResponse>> selectByDynamicType(Integer currentPage, Integer pageSize, String dynamicType, String status);

    /**
     * 根据状态分页查询动态
     * 对应Service: selectByStatus
     */
    Result<PageResponse<SocialDynamicResponse>> selectByStatus(Integer currentPage, Integer pageSize, String status);

    /**
     * 获取关注用户的动态流
     * 对应Service: selectFollowingDynamics
     */
    Result<PageResponse<SocialDynamicResponse>> selectFollowingDynamics(Integer currentPage, Integer pageSize, Long userId, String status);

    /**
     * 搜索动态（按内容搜索）
     * 对应Service: searchByContent
     */
    Result<PageResponse<SocialDynamicResponse>> searchByContent(Integer currentPage, Integer pageSize, String keyword, String status);

    /**
     * 获取热门动态（按互动数排序）
     * 对应Service: selectHotDynamics
     */
    Result<PageResponse<SocialDynamicResponse>> selectHotDynamics(Integer currentPage, Integer pageSize, String status, String dynamicType);

    /**
     * 根据分享目标查询分享动态
     * 对应Service: selectByShareTarget
     */
    Result<PageResponse<SocialDynamicResponse>> selectByShareTarget(Integer currentPage, Integer pageSize, String shareTargetType, Long shareTargetId, String status);

    // =================== 统计计数方法（严格对应Service层3个） ===================

    /**
     * 统计用户动态数量
     * 对应Service: countByUserId
     */
    Result<Long> countByUserId(Long userId, String status, String dynamicType);

    /**
     * 统计动态类型数量
     * 对应Service: countByDynamicType
     */
    Result<Long> countByDynamicType(String dynamicType, String status);

    /**
     * 统计指定时间范围内的动态数量
     * 对应Service: countByTimeRange
     */
    Result<Long> countByTimeRange(LocalDateTime startTime, LocalDateTime endTime, String status);

    // =================== 互动统计更新（严格对应Service层5个） ===================

    /**
     * 增加点赞数
     * 对应Service: increaseLikeCount
     */
    Result<Integer> increaseLikeCount(Long dynamicId, Long operatorId);

    /**
     * 减少点赞数
     * 对应Service: decreaseLikeCount
     */
    Result<Integer> decreaseLikeCount(Long dynamicId, Long operatorId);

    /**
     * 增加评论数
     * 对应Service: increaseCommentCount
     */
    Result<Integer> increaseCommentCount(Long dynamicId, Long operatorId);

    /**
     * 增加分享数
     * 对应Service: increaseShareCount
     */
    Result<Integer> increaseShareCount(Long dynamicId, Long operatorId);

    /**
     * 批量更新统计数据
     * 对应Service: updateStatistics
     */
    Result<Integer> updateStatistics(Long dynamicId, Long likeCount, Long commentCount, Long shareCount, Long operatorId);

    // =================== 状态管理（严格对应Service层2个） ===================

    /**
     * 更新动态状态
     * 对应Service: updateStatus
     */
    Result<Integer> updateStatus(Long dynamicId, String status, Long operatorId);

    /**
     * 批量更新动态状态
     * 对应Service: batchUpdateStatus
     */
    Result<Integer> batchUpdateStatus(List<Long> dynamicIds, String status, Long operatorId);

    // =================== 用户信息同步（严格对应Service层1个） ===================

    /**
     * 批量更新用户冗余信息
     * 对应Service: updateUserInfo
     */
    Result<Integer> updateUserInfo(Long userId, String userNickname, String userAvatar, Long operatorId);

    // =================== 数据清理（严格对应Service层1个） ===================

    /**
     * 物理删除指定状态的历史动态
     * 对应Service: deleteByStatusAndTime
     */
    Result<Integer> deleteByStatusAndTime(String status, LocalDateTime beforeTime, Integer limit, Long operatorId);

    // =================== 特殊查询（严格对应Service层3个） ===================

    /**
     * 查询最新动态（全局）
     * 对应Service: selectLatestDynamics
     */
    Result<List<SocialDynamicResponse>> selectLatestDynamics(Integer limit, String status);

    /**
     * 查询用户最新动态
     * 对应Service: selectUserLatestDynamics
     */
    Result<List<SocialDynamicResponse>> selectUserLatestDynamics(Long userId, Integer limit, String status);

    /**
     * 查询分享动态列表
     * 对应Service: selectShareDynamics
     */
    Result<List<SocialDynamicResponse>> selectShareDynamics(String shareTargetType, Integer limit, String status);

    // =================== 系统健康检查 ===================

    /**
     * 社交动态系统健康检查
     */
    Result<String> healthCheck();
} 