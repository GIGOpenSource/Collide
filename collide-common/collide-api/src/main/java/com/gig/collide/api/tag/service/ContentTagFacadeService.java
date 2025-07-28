package com.gig.collide.api.tag.service;

import com.gig.collide.api.tag.request.*;
import com.gig.collide.api.tag.response.*;
import com.gig.collide.api.tag.response.data.ContentTagInfo;
import com.gig.collide.api.tag.response.data.BasicTagInfo;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.base.response.BaseResponse;

import java.util.List;

/**
 * 内容标签门面服务接口
 * 提供内容标签管理功能
 * 
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
public interface ContentTagFacadeService {

    /**
     * 为内容添加标签
     * 
     * @param manageRequest 管理请求
     * @return 添加响应
     */
    ContentTagResponse addContentTag(ContentTagManageRequest manageRequest);

    /**
     * 移除内容标签
     * 
     * @param manageRequest 管理请求
     * @return 移除响应
     */
    BaseResponse removeContentTag(ContentTagManageRequest manageRequest);

    /**
     * 替换内容的所有标签
     * 
     * @param manageRequest 管理请求
     * @return 替换响应
     */
    ContentTagResponse replaceContentTags(ContentTagManageRequest manageRequest);

    /**
     * 清除内容的所有标签
     * 
     * @param manageRequest 管理请求
     * @return 清除响应
     */
    BaseResponse clearContentTags(ContentTagManageRequest manageRequest);

    /**
     * 获取内容的标签列表
     * 
     * @param contentId 内容ID
     * @param tagType 标签类型
     * @return 标签列表
     */
    TagUnifiedQueryResponse<List<ContentTagInfo>> getContentTags(Long contentId, String tagType);

    /**
     * 获取标签的内容列表
     * 
     * @param tagId 标签ID
     * @param contentType 内容类型
     * @param pageNum 页码
     * @param pageSize 页大小
     * @return 分页响应
     */
    PageResponse<ContentTagInfo> getTagContents(Long tagId, String contentType, Integer pageNum, Integer pageSize);

    /**
     * 自动推荐内容标签
     * 
     * @param manageRequest 管理请求
     * @return 推荐响应
     */
    ContentTagResponse autoRecommendContentTags(ContentTagManageRequest manageRequest);

    /**
     * 批量为内容添加标签
     * 
     * @param manageRequest 管理请求
     * @return 批量操作响应
     */
    ContentTagResponse batchAddContentTags(ContentTagManageRequest manageRequest);

    /**
     * 批量移除内容标签
     * 
     * @param contentIds 内容ID列表
     * @param tagIds 标签ID列表
     * @param operatorId 操作员ID
     * @return 操作结果
     */
    BaseResponse batchRemoveContentTags(List<Long> contentIds, List<Long> tagIds, Long operatorId);

    /**
     * 获取热门内容标签组合
     * 
     * @param limit 限制数量
     * @param timeRange 时间范围（today、week、month）
     * @return 热门标签组合
     */
    TagUnifiedQueryResponse<List<List<BasicTagInfo>>> getHotTagCombinations(Integer limit, String timeRange);

    /**
     * 根据内容相似性推荐标签
     * 
     * @param contentId 内容ID
     * @param limit 推荐数量
     * @param algorithm 推荐算法（content、collaborative、hybrid）
     * @return 推荐标签列表
     */
    TagUnifiedQueryResponse<List<BasicTagInfo>> recommendTagsByContentSimilarity(Long contentId, Integer limit, String algorithm);

    /**
     * 检查内容是否包含指定标签
     * 
     * @param contentId 内容ID
     * @param tagId 标签ID
     * @return 是否包含
     */
    Boolean checkContentHasTag(Long contentId, Long tagId);

    /**
     * 获取内容标签权重分布
     * 
     * @param contentId 内容ID
     * @return 权重分布
     */
    TagUnifiedQueryResponse<List<ContentTagInfo>> getContentTagWeights(Long contentId);

    /**
     * 同步内容标签使用统计
     * 
     * @param tagId 标签ID
     * @return 同步结果
     */
    BaseResponse syncContentTagUsageStats(Long tagId);
} 