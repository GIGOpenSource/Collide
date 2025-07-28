package com.gig.collide.search.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gig.collide.search.domain.entity.SearchStatistics;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 搜索统计 Mapper 接口
 * 
 * @author Collide Team
 * @version 1.0.0
 * @since 2024-01-01
 */
@Mapper
public interface SearchStatisticsMapper extends BaseMapper<SearchStatistics> {

    /**
     * 分页查询搜索统计
     * 
     * @param page 分页参数
     * @param keyword 关键词（模糊匹配）
     * @param minSearchCount 最小搜索次数
     * @param maxSearchCount 最大搜索次数
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 分页结果
     */
    IPage<SearchStatistics> pageSearchStatistics(
            @Param("page") Page<SearchStatistics> page,
            @Param("keyword") String keyword,
            @Param("minSearchCount") Long minSearchCount,
            @Param("maxSearchCount") Long maxSearchCount,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );

    /**
     * 根据关键词查询或创建统计记录
     * 
     * @param keyword 关键词
     * @return 统计记录
     */
    SearchStatistics selectOrCreateByKeyword(@Param("keyword") String keyword);

    /**
     * 增加搜索次数
     * 
     * @param keyword 关键词
     * @param searchType 搜索类型
     * @param resultCount 结果数量
     * @param searchTime 搜索耗时
     * @param userId 用户ID（用于统计唯一用户）
     * @return 更新数量
     */
    int increaseSearchCount(
            @Param("keyword") String keyword,
            @Param("searchType") String searchType,
            @Param("resultCount") Long resultCount,
            @Param("searchTime") Long searchTime,
            @Param("userId") Long userId
    );

    /**
     * 查询热门搜索关键词
     * 
     * @param limit 限制数量
     * @param timeRange 时间范围（today/week/month）
     * @return 统计列表
     */
    List<SearchStatistics> queryHotKeywords(
            @Param("limit") Integer limit,
            @Param("timeRange") String timeRange
    );

    /**
     * 查询搜索趋势数据
     * 
     * @param keyword 关键词
     * @param days 统计天数
     * @return 趋势数据
     */
    List<SearchStatistics> querySearchTrend(
            @Param("keyword") String keyword,
            @Param("days") Integer days
    );

    /**
     * 查询搜索排行榜
     * 
     * @param rankType 排行类型（search_count/unique_user_count/hot_score）
     * @param limit 限制数量
     * @param timeRange 时间范围
     * @return 排行榜数据
     */
    List<SearchStatistics> querySearchRanking(
            @Param("rankType") String rankType,
            @Param("limit") Integer limit,
            @Param("timeRange") String timeRange
    );

    /**
     * 批量更新热度评分
     * 
     * @param keywords 关键词列表
     * @return 更新数量
     */
    int batchUpdateHotScore(@Param("keywords") List<String> keywords);

    /**
     * 更新今日搜索统计
     * 
     * @param keyword 关键词
     * @param todayCount 今日搜索次数
     * @return 更新数量
     */
    int updateTodayCount(
            @Param("keyword") String keyword,
            @Param("todayCount") Long todayCount
    );

    /**
     * 更新本周搜索统计
     * 
     * @param keyword 关键词
     * @param weekCount 本周搜索次数
     * @return 更新数量
     */
    int updateWeekCount(
            @Param("keyword") String keyword,
            @Param("weekCount") Long weekCount
    );

    /**
     * 更新本月搜索统计
     * 
     * @param keyword 关键词
     * @param monthCount 本月搜索次数
     * @return 更新数量
     */
    int updateMonthCount(
            @Param("keyword") String keyword,
            @Param("monthCount") Long monthCount
    );

    /**
     * 重置统计计数器
     * 
     * @param resetType 重置类型（today/week/month）
     * @return 更新数量
     */
    int resetStatisticsCount(@Param("resetType") String resetType);

    /**
     * 计算并更新热度评分
     * 
     * @return 更新数量
     */
    int calculateAndUpdateHotScore();

    /**
     * 清理过期统计数据
     * 
     * @param expireTime 过期时间
     * @return 清理数量
     */
    int cleanExpiredStatistics(@Param("expireTime") LocalDateTime expireTime);
} 