package com.gig.collide.api.tag.service;

import com.gig.collide.api.tag.request.*;
import com.gig.collide.api.tag.response.*;
import com.gig.collide.api.tag.response.data.TagUnifiedInfo;
import com.gig.collide.api.tag.response.data.BasicTagInfo;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.base.response.BaseResponse;

import java.util.List;

/**
 * 标签统一门面服务接口
 * 提供标签核心业务功能
 * 
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
public interface TagUnifiedFacadeService {

    /**
     * 标签统一信息查询
     * 
     * @param queryRequest 查询请求
     * @return 查询响应
     */
    TagUnifiedQueryResponse<TagUnifiedInfo> queryTag(TagUnifiedQueryRequest queryRequest);

    /**
     * 基础标签信息查询（不包含统计信息）
     * 
     * @param queryRequest 查询请求
     * @return 基础标签信息响应
     */
    TagUnifiedQueryResponse<BasicTagInfo> queryBasicTag(TagUnifiedQueryRequest queryRequest);

    /**
     * 分页查询标签信息
     * 
     * @param queryRequest 分页查询请求
     * @return 分页响应
     */
    PageResponse<TagUnifiedInfo> pageQueryTags(TagUnifiedQueryRequest queryRequest);

    /**
     * 创建标签
     * 
     * @param createRequest 创建请求
     * @return 创建响应
     */
    TagManageResponse createTag(TagCreateRequest createRequest);

    /**
     * 更新标签
     * 
     * @param updateRequest 更新请求
     * @return 更新响应
     */
    TagManageResponse updateTag(TagUpdateRequest updateRequest);

    /**
     * 删除标签
     * 
     * @param tagId 标签ID
     * @param operatorId 操作员ID
     * @return 删除响应
     */
    BaseResponse deleteTag(Long tagId, Long operatorId);

    /**
     * 获取热门标签
     * 
     * @param tagType 标签类型
     * @param limit 限制数量
     * @return 热门标签列表
     */
    TagUnifiedQueryResponse<List<BasicTagInfo>> getHotTags(String tagType, Integer limit);

    /**
     * 搜索标签
     * 
     * @param keyword 关键词
     * @param tagType 标签类型
     * @param limit 限制数量
     * @return 搜索结果
     */
    TagUnifiedQueryResponse<List<BasicTagInfo>> searchTags(String keyword, String tagType, Integer limit);

    /**
     * 根据分类获取标签
     * 
     * @param categoryId 分类ID
     * @param status 状态
     * @return 标签列表
     */
    TagUnifiedQueryResponse<List<BasicTagInfo>> getTagsByCategory(Long categoryId, String status);

    /**
     * 批量操作标签状态
     * 
     * @param tagIds 标签ID列表
     * @param operation 操作类型（activate、deactivate、delete）
     * @param operatorId 操作员ID
     * @return 操作结果
     */
    BaseResponse batchOperateTags(List<Long> tagIds, String operation, Long operatorId);

    /**
     * 更新标签热度分数
     * 
     * @param tagId 标签ID
     * @return 更新结果
     */
    BaseResponse updateTagHeatScore(Long tagId);

    /**
     * 检查标签名称是否可用
     * 
     * @param name 标签名称
     * @param tagType 标签类型
     * @return 是否可用
     */
    Boolean checkTagNameAvailable(String name, String tagType);
} 