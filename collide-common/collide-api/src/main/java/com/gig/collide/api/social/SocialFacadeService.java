package com.gig.collide.api.social;

import com.gig.collide.api.social.request.SocialDynamicCreateRequest;
import com.gig.collide.api.social.request.SocialDynamicQueryRequest;
import com.gig.collide.api.social.request.SocialDynamicUpdateRequest;
import com.gig.collide.api.social.response.SocialDynamicResponse;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.web.vo.Result;

import java.util.List;

/**
 * 社交动态门面服务接口 - 简洁版
 * 基于简洁版SQL设计（t_social_dynamic）
 *
 * @author GIG Team
 * @version 2.0.0
 */
public interface SocialFacadeService {

    /**
     * 发布动态
     */
    Result<SocialDynamicResponse> createDynamic(SocialDynamicCreateRequest request);

    /**
     * 更新动态
     */
    Result<SocialDynamicResponse> updateDynamic(SocialDynamicUpdateRequest request);

    /**
     * 删除动态（逻辑删除）
     */
    Result<Void> deleteDynamic(Long dynamicId);

    /**
     * 根据ID查询动态
     */
    Result<SocialDynamicResponse> getDynamicById(Long dynamicId);

    /**
     * 分页查询动态列表
     */
    Result<PageResponse<SocialDynamicResponse>> queryDynamics(SocialDynamicQueryRequest request);

    /**
     * 获取用户动态列表
     */
    Result<List<SocialDynamicResponse>> getUserDynamics(Long userId, Integer limit);

    /**
     * 获取关注用户的动态流
     */
    Result<PageResponse<SocialDynamicResponse>> getFollowingDynamics(Long userId, SocialDynamicQueryRequest request);

    /**
     * 根据类型获取动态列表
     */
    Result<List<SocialDynamicResponse>> getDynamicsByType(String dynamicType, Integer limit);

    /**
     * 点赞动态
     */
    Result<Void> likeDynamic(Long dynamicId, Long userId);

    /**
     * 取消点赞
     */
    Result<Void> unlikeDynamic(Long dynamicId, Long userId);

    /**
     * 评论动态
     */
    Result<Void> commentDynamic(Long dynamicId, Long userId, String content);

    /**
     * 分享动态
     */
    Result<SocialDynamicResponse> shareDynamic(Long dynamicId, Long userId, String shareContent);

    /**
     * 搜索动态
     */
    Result<List<SocialDynamicResponse>> searchDynamics(String keyword, Integer limit);

    /**
     * 获取热门动态
     */
    Result<List<SocialDynamicResponse>> getHotDynamics(Integer limit);
} 