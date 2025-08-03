package com.gig.collide.api.tag;

import com.gig.collide.api.tag.request.TagCreateRequest;
import com.gig.collide.api.tag.request.TagUpdateRequest;
import com.gig.collide.api.tag.request.TagQueryRequest;
import com.gig.collide.api.tag.response.TagResponse;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.web.vo.Result;

import java.util.List;
import java.util.Map;

/**
 * 标签门面服务接口 - 对应 t_tag 表
 * 支持标签的基础管理和查询功能
 * 
 * @author GIG Team
 * @version 1.0.0
 */
public interface TagFacadeService {

    // =================== 标签基础管理 ===================

    /**
     * 创建标签
     * 
     * @param request 标签创建请求
     * @return 创建结果
     */
    Result<TagResponse> createTag(TagCreateRequest request);

    /**
     * 更新标签
     * 
     * @param request 标签更新请求
     * @return 更新结果
     */
    Result<TagResponse> updateTag(TagUpdateRequest request);

    /**
     * 删除标签
     * 
     * @param tagId 标签ID
     * @return 删除结果
     */
    Result<Void> deleteTag(Long tagId);

    /**
     * 根据ID查询标签
     * 
     * @param tagId 标签ID
     * @return 标签信息
     */
    Result<TagResponse> getTag(Long tagId);

    /**
     * 根据名称查询标签
     * 
     * @param tagName 标签名称
     * @return 标签信息
     */
    Result<TagResponse> getTagByName(String tagName);

    // =================== 标签列表查询 ===================

    /**
     * 获取所有启用的标签
     * 
     * @return 标签列表
     */
    Result<List<TagResponse>> getAllActiveTags();

    /**
     * 分页查询标签
     * 
     * @param request 查询请求
     * @return 分页标签列表
     */
    Result<PageResponse<TagResponse>> queryTags(TagQueryRequest request);

    /**
     * 获取热门标签
     * 
     * @param limit 返回数量限制（默认10）
     * @return 热门标签列表
     */
    Result<List<TagResponse>> getHotTags(Integer limit);

    /**
     * 获取推荐标签（基于权重和热度）
     * 
     * @param limit 返回数量限制（默认10）
     * @return 推荐标签列表
     */
    Result<List<TagResponse>> getRecommendTags(Integer limit);

    // =================== 标签统计分析 ===================

    /**
     * 获取标签统计信息
     * 
     * @param tagId 标签ID
     * @return 统计信息
     */
    Result<Map<String, Object>> getTagStatistics(Long tagId);

    /**
     * 获取标签热度排行榜
     * 
     * @param limit 返回数量限制（默认20）
     * @return 热度排行榜
     */
    Result<List<TagResponse>> getHotnessRanking(Integer limit);

    /**
     * 获取关注数排行榜
     * 
     * @param limit 返回数量限制（默认20）
     * @return 关注数排行榜
     */
    Result<List<TagResponse>> getFollowCountRanking(Integer limit);

    /**
     * 获取内容数排行榜
     * 
     * @param limit 返回数量限制（默认20）
     * @return 内容数排行榜
     */
    Result<List<TagResponse>> getContentCountRanking(Integer limit);

    // =================== 标签状态管理 ===================

    /**
     * 更新标签状态
     * 
     * @param tagId 标签ID
     * @param status 状态：1-启用 0-禁用
     * @return 更新结果
     */
    Result<Void> updateTagStatus(Long tagId, Integer status);

    /**
     * 更新标签权重
     * 
     * @param tagId 标签ID
     * @param weight 权重（1-100）
     * @return 更新结果
     */
    Result<Void> updateTagWeight(Long tagId, Integer weight);

    /**
     * 批量更新标签状态
     * 
     * @param tagIds 标签ID列表
     * @param status 状态：1-启用 0-禁用
     * @return 更新结果
     */
    Result<Void> batchUpdateTagStatus(List<Long> tagIds, Integer status);

    // =================== 标签搜索 ===================

    /**
     * 搜索标签（支持名称模糊匹配）
     * 
     * @param keyword 搜索关键词
     * @param limit 返回数量限制（默认10）
     * @return 匹配的标签列表
     */
    Result<List<TagResponse>> searchTags(String keyword, Integer limit);

    /**
     * 根据权重范围查询标签
     * 
     * @param minWeight 最小权重
     * @param maxWeight 最大权重
     * @param limit 返回数量限制
     * @return 标签列表
     */
    Result<List<TagResponse>> getTagsByWeightRange(Integer minWeight, Integer maxWeight, Integer limit);

    // =================== 系统管理功能 ===================

    /**
     * 手动更新标签热度值
     * 
     * @param tagId 标签ID（为空时更新所有标签）
     * @return 更新结果
     */
    Result<Void> updateTagHotness(Long tagId);

    /**
     * 检查标签名称是否可用
     * 
     * @param tagName 标签名称
     * @param excludeId 排除的标签ID（用于更新时检查）
     * @return 是否可用
     */
    Result<Boolean> checkTagNameAvailable(String tagName, Long excludeId);
}