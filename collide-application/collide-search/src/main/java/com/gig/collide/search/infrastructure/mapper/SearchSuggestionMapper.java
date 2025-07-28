package com.gig.collide.search.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gig.collide.search.domain.entity.SearchSuggestion;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 搜索建议 Mapper 接口
 * 
 * @author Collide Team
 * @version 1.0.0
 * @since 2024-01-01
 */
@Mapper
public interface SearchSuggestionMapper extends BaseMapper<SearchSuggestion> {

    /**
     * 分页查询搜索建议
     * 
     * @param page 分页参数
     * @param keyword 关键词（模糊匹配）
     * @param suggestionType 建议类型
     * @param status 状态
     * @param isManual 是否手动配置
     * @return 分页结果
     */
    IPage<SearchSuggestion> pageSuggestions(
            @Param("page") Page<SearchSuggestion> page,
            @Param("keyword") String keyword,
            @Param("suggestionType") String suggestionType,
            @Param("status") String status,
            @Param("isManual") Integer isManual
    );

    /**
     * 根据关键词和类型查询建议
     * 
     * @param keyword 关键词前缀
     * @param suggestionType 建议类型
     * @param limit 限制数量
     * @return 建议列表
     */
    List<SearchSuggestion> querySuggestionsByKeyword(
            @Param("keyword") String keyword,
            @Param("suggestionType") String suggestionType,
            @Param("limit") Integer limit
    );

    /**
     * 查询自动完成建议
     * 
     * @param prefix 关键词前缀
     * @param suggestionTypes 建议类型列表
     * @param limit 限制数量
     * @return 建议列表
     */
    List<SearchSuggestion> queryAutocompleteSuggestions(
            @Param("prefix") String prefix,
            @Param("suggestionTypes") List<String> suggestionTypes,
            @Param("limit") Integer limit
    );

    /**
     * 查询热门建议
     * 
     * @param suggestionType 建议类型
     * @param limit 限制数量
     * @return 建议列表
     */
    List<SearchSuggestion> queryHotSuggestions(
            @Param("suggestionType") String suggestionType,
            @Param("limit") Integer limit
    );

    /**
     * 增加搜索次数
     * 
     * @param suggestionId 建议ID
     * @return 更新数量
     */
    int increaseSearchCount(@Param("suggestionId") Long suggestionId);

    /**
     * 增加点击次数
     * 
     * @param suggestionId 建议ID
     * @return 更新数量
     */
    int increaseClickCount(@Param("suggestionId") Long suggestionId);

    /**
     * 批量更新权重
     * 
     * @param suggestions 建议列表（包含ID和权重）
     * @return 更新数量
     */
    int batchUpdateWeight(@Param("suggestions") List<SearchSuggestion> suggestions);

    /**
     * 批量更新评分
     * 
     * @param suggestions 建议列表（包含ID和评分）
     * @return 更新数量
     */
    int batchUpdateScore(@Param("suggestions") List<SearchSuggestion> suggestions);

    /**
     * 查询过期的建议
     * 
     * @param currentTime 当前时间
     * @return 过期建议列表
     */
    List<SearchSuggestion> queryExpiredSuggestions(@Param("currentTime") LocalDateTime currentTime);

    /**
     * 批量更新状态
     * 
     * @param suggestionIds 建议ID列表
     * @param status 状态
     * @return 更新数量
     */
    int batchUpdateStatus(
            @Param("suggestionIds") List<Long> suggestionIds,
            @Param("status") String status
    );

    /**
     * 根据目标信息查询建议
     * 
     * @param targetType 目标类型
     * @param targetId 目标ID
     * @return 建议列表
     */
    List<SearchSuggestion> querySuggestionsByTarget(
            @Param("targetType") String targetType,
            @Param("targetId") Long targetId
    );

    /**
     * 查询需要更新的建议（基于统计数据）
     * 
     * @param minSearchCount 最小搜索次数
     * @param limit 限制数量
     * @return 建议列表
     */
    List<SearchSuggestion> queryNeedUpdateSuggestions(
            @Param("minSearchCount") Long minSearchCount,
            @Param("limit") Integer limit
    );

    /**
     * 清理低质量建议
     * 
     * @param minWeight 最小权重阈值
     * @param minSearchCount 最小搜索次数阈值
     * @return 清理数量
     */
    int cleanLowQualitySuggestions(
            @Param("minWeight") Double minWeight,
            @Param("minSearchCount") Long minSearchCount
    );
} 