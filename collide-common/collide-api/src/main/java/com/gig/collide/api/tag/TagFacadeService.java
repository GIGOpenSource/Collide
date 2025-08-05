package com.gig.collide.api.tag;

import com.gig.collide.api.tag.request.TagCreateRequest;
import com.gig.collide.api.tag.request.TagQueryRequest;
import com.gig.collide.api.tag.request.TagUpdateRequest;
import com.gig.collide.api.tag.response.TagResponse;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.web.vo.Result;

import java.util.List;
import java.util.Map;

/**
 * 标签门面服务接口 - 基础标签管理
 * 专注于标签本身的增删改查和管理功能
 * 使用Result<T>统一包装返回结果
 *
 * @author GIG Team
 * @version 3.0.0
 * @since 2024-01-30
 */
public interface TagFacadeService {

    // =================== 标签基础管理 ===================

    /**
     * 创建标签
     * 包含唯一性验证、参数检查、默认值设置
     */
    Result<TagResponse> createTag(TagCreateRequest request);

    /**
     * 更新标签
     * 只允许更新部分字段，包含权限验证
     */
    Result<TagResponse> updateTag(TagUpdateRequest request);

    /**
     * 删除标签
     * 逻辑删除，包含关联数据检查
     */
    Result<Void> deleteTag(Long tagId, Long operatorId);

    /**
     * 根据ID查询标签详情
     */
    Result<TagResponse> getTagById(Long tagId);

    /**
     * 分页查询标签列表
     * 支持多条件组合查询
     */
    Result<PageResponse<TagResponse>> queryTags(TagQueryRequest request);

    // =================== 标签查询功能 ===================

    /**
     * 根据类型查询标签列表
     */
    Result<List<TagResponse>> getTagsByType(String tagType);

    /**
     * 搜索标签（智能搜索）
     * 先精确匹配，再模糊搜索
     */
    Result<List<TagResponse>> searchTags(String keyword, Integer limit);

    /**
     * 按名称精确搜索标签
     */
    Result<List<TagResponse>> searchTagsByNameExact(String keyword, Integer limit);

    /**
     * 获取热门标签
     * 按使用次数排序
     */
    Result<List<TagResponse>> getHotTags(Integer limit);

    /**
     * 根据分类查询标签
     */
    Result<List<TagResponse>> getTagsByCategory(Long categoryId);

    /**
     * 获取活跃标签列表
     */
    Result<List<TagResponse>> getActiveTags(Long categoryId);

    // =================== 标签状态管理 ===================

    /**
     * 激活标签
     */
    Result<Void> activateTag(Long tagId, Long operatorId);

    /**
     * 停用标签
     */
    Result<Void> deactivateTag(Long tagId, Long operatorId);

    /**
     * 批量更新标签状态
     */
    Result<Integer> batchUpdateTagStatus(List<Long> tagIds, String status, Long operatorId);

    // =================== 标签使用统计 ===================

    /**
     * 增加标签使用次数
     */
    Result<Void> increaseTagUsage(Long tagId);

    /**
     * 减少标签使用次数
     */
    Result<Void> decreaseTagUsage(Long tagId);

    /**
     * 获取标签使用统计
     */
    Result<List<Map<String, Object>>> getTagUsageStats(String tagType, Integer limit);

    /**
     * 批量获取标签基本信息
     */
    Result<List<Map<String, Object>>> getTagSummary(List<Long> tagIds);

    // =================== 标签检查功能 ===================

    /**
     * 检查标签名称是否存在
     */
    Result<Boolean> checkTagExists(String name, String tagType);

    /**
     * 检查标签是否可以删除
     * 检查是否有关联的内容或用户兴趣
     */
    Result<Boolean> canDeleteTag(Long tagId);

    // =================== 标签云和推荐 ===================

    /**
     * 获取标签云数据
     * 包含标签和权重信息
     */
    Result<List<Map<String, Object>>> getTagCloud(String tagType, Integer limit);

    /**
     * 获取相似标签
     * 基于标签使用模式和分类
     */
    Result<List<TagResponse>> getSimilarTags(Long tagId, Integer limit);

    // =================== 数据维护 ===================

    /**
     * 重新计算标签使用次数
     * 基于实际关联数据重新统计
     */
    Result<Integer> recalculateTagUsageCounts(Long operatorId);

    /**
     * 合并重复标签
     * 将重复的标签合并到主标签
     */
    Result<Integer> mergeDuplicateTags(Long mainTagId, List<Long> duplicateTagIds, Long operatorId);

    /**
     * 清理无效标签
     * 清理没有任何关联的废弃标签
     */
    Result<Integer> cleanupUnusedTags(Long operatorId);

    // =================== 系统功能 ===================

    /**
     * 获取所有标签类型
     */
    Result<List<String>> getAllTagTypes();

    /**
     * 获取标签系统统计信息
     */
    Result<Map<String, Object>> getTagSystemStats();

    /**
     * 标签系统健康检查
     */
    Result<String> healthCheck();
}