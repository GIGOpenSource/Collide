package com.gig.collide.business.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gig.collide.business.domain.search.entity.SearchStatistics;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 搜索统计数据访问映射器
 *
 * @author GIG Team
 */
@Mapper
public interface SearchStatisticsMapper extends BaseMapper<SearchStatistics> {

    /**
     * 增加搜索次数和用户数
     *
     * @param keyword 关键词
     * @param userId 用户ID（用于判断是否是新用户）
     * @return 影响行数
     */
    int incrementSearchCount(@Param("keyword") String keyword, @Param("userId") Long userId);

    /**
     * 获取热门搜索关键词
     *
     * @param limit 限制数量
     * @return 热门关键词列表
     */
    List<String> selectHotKeywords(@Param("limit") Integer limit);

    /**
     * 获取热门搜索统计
     *
     * @param limit 限制数量
     * @return 热门搜索统计列表
     */
    List<SearchStatistics> selectHotStatistics(@Param("limit") Integer limit);

    /**
     * 根据关键词查询统计信息
     *
     * @param keyword 关键词
     * @return 统计信息
     */
    SearchStatistics selectByKeyword(@Param("keyword") String keyword);

    /**
     * 根据关键词前缀查询相关关键词
     *
     * @param keywordPrefix 关键词前缀
     * @param limit 限制数量
     * @return 关键词列表
     */
    List<String> selectKeywordsByPrefix(@Param("keywordPrefix") String keywordPrefix, @Param("limit") Integer limit);
} 