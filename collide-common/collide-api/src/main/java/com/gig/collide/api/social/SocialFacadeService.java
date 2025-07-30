package com.gig.collide.api.social;

import com.gig.collide.api.social.request.SocialDynamicCreateRequest;
import com.gig.collide.api.social.request.SocialDynamicQueryRequest;
import com.gig.collide.api.social.request.SocialDynamicUpdateRequest;
import com.gig.collide.api.social.response.SocialDynamicResponse;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.web.vo.Result;

/**
 * 社交动态门面服务接口 - 缓存增强版
 * 基于简洁版SQL设计（t_social_dynamic），对齐content模块设计风格
 * 
 * 核心功能：
 * - 创建动态
 * - 查询最新动态列表
 * - 根据userId查询动态
 * - 点赞评论记录
 * - 删除动态
 * - 更新动态内容（仅内容字段）
 *
 * @author GIG Team
 * @version 2.0.0 (缓存增强版)
 * @since 2024-01-16
 */
public interface SocialFacadeService {

    // =================== 动态管理 ===================

    /**
     * 创建动态
     * @param request 创建请求
     * @return 创建结果（只返回成功状态，不返回数据）
     */
    Result<Void> createDynamic(SocialDynamicCreateRequest request);

    /**
     * 更新动态内容
     * 只允许更新动态内容，其他字段不允许修改
     * @param request 更新请求
     * @return 更新后的动态信息
     */
    Result<SocialDynamicResponse> updateDynamic(SocialDynamicUpdateRequest request);

    /**
     * 删除动态
     * 逻辑删除（设为deleted状态）
     * @param dynamicId 动态ID
     * @param operatorId 操作人ID
     * @return 删除结果
     */
    Result<Void> deleteDynamic(Long dynamicId, Long operatorId);

    /**
     * 根据ID查询动态详情
     * @param dynamicId 动态ID
     * @param includeDeleted 是否包含已删除的动态
     * @return 动态详情
     */
    Result<SocialDynamicResponse> getDynamicById(Long dynamicId, Boolean includeDeleted);

    // =================== 动态查询 ===================

    /**
     * 分页查询动态列表
     * @param request 查询请求
     * @return 分页结果
     */
    PageResponse<SocialDynamicResponse> queryDynamics(SocialDynamicQueryRequest request);

    /**
     * 查询最新动态列表
     * @param currentPage 当前页码
     * @param pageSize 页面大小
     * @param dynamicType 动态类型（可选）
     * @return 分页结果
     */
    PageResponse<SocialDynamicResponse> getLatestDynamics(Integer currentPage, Integer pageSize, String dynamicType);

    /**
     * 根据用户ID查询动态列表
     * @param userId 用户ID
     * @param currentPage 当前页码
     * @param pageSize 页面大小
     * @param dynamicType 动态类型（可选）
     * @return 分页结果
     */
    PageResponse<SocialDynamicResponse> getUserDynamics(Long userId, Integer currentPage, Integer pageSize, String dynamicType);

    // =================== 互动功能 ===================

    /**
     * 点赞动态
     * @param dynamicId 动态ID
     * @param userId 用户ID
     * @return 操作结果
     */
    Result<Void> likeDynamic(Long dynamicId, Long userId);

    /**
     * 取消点赞
     * @param dynamicId 动态ID
     * @param userId 用户ID
     * @return 操作结果
     */
    Result<Void> unlikeDynamic(Long dynamicId, Long userId);

    /**
     * 评论动态
     * @param dynamicId 动态ID
     * @param userId 用户ID
     * @param content 评论内容
     * @return 操作结果
     */
    Result<Void> commentDynamic(Long dynamicId, Long userId, String content);

    /**
     * 获取动态的点赞记录
     * @param dynamicId 动态ID
     * @param currentPage 当前页码
     * @param pageSize 页面大小
     * @return 点赞用户列表
     */
    PageResponse<Object> getDynamicLikes(Long dynamicId, Integer currentPage, Integer pageSize);

    /**
     * 获取动态的评论记录
     * @param dynamicId 动态ID
     * @param currentPage 当前页码
     * @param pageSize 页面大小
     * @return 评论列表
     */
    PageResponse<Object> getDynamicComments(Long dynamicId, Integer currentPage, Integer pageSize);

    // =================== 统计功能 ===================

    /**
     * 获取动态统计信息
     * @param dynamicId 动态ID
     * @return 统计信息（点赞数、评论数、分享数等）
     */
    Result<Object> getDynamicStatistics(Long dynamicId);

    // =================== 聚合功能 ===================

    /**
     * 获取用户互动记录聚合列表
     * 包含：用户点赞别人、被别人点赞、用户评论别人、被别人评论的所有记录
     * 按最新时间排序
     * @param userId 用户ID
     * @param currentPage 当前页码
     * @param pageSize 页面大小
     * @return 互动记录列表
     */
    PageResponse<Object> getUserInteractions(Long userId, Integer currentPage, Integer pageSize);
} 