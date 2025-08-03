package com.gig.collide.search.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gig.collide.search.domain.entity.HotSearch;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

/**
 * 热门搜索Mapper接口 - 简洁版
 *
 * @author GIG Team
 * @version 2.0.0
 */
@Mapper
public interface HotSearchMapper extends BaseMapper<HotSearch> {

    /**
     * 获取热门搜索关键词（按搜索次数排序）
     */
    List<HotSearch> selectHotKeywords(@Param("limit") Integer limit);

    /**
     * 根据搜索类型获取热门关键词
     */
    List<HotSearch> selectHotKeywordsByType(@Param("searchType") String searchType, @Param("limit") Integer limit);

    /**
     * 增加搜索次数
     */
    int increaseSearchCount(@Param("keyword") String keyword);

    /**
     * 更新趋势分数
     */
    int updateTrendScore(@Param("keyword") String keyword, @Param("trendScore") BigDecimal trendScore);

    /**
     * 根据关键词查询
     */
    HotSearch selectByKeyword(@Param("keyword") String keyword);

    /**
     * 获取活跃的热搜关键词
     */
    List<HotSearch> selectActiveHotKeywords(@Param("limit") Integer limit);

    /**
     * 批量插入或更新热搜数据
     */
    int insertOrUpdateHotSearch(@Param("keyword") String keyword, @Param("searchCount") Long searchCount);
} 