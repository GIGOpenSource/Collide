package com.gig.collide.search.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gig.collide.search.domain.entity.SearchHistory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 搜索历史 Mapper 接口
 * 
 * @author Collide Team
 * @version 1.0.0
 * @since 2024-01-01
 */
@Mapper
public interface SearchHistoryMapper extends BaseMapper<SearchHistory> {

    /**
     * 分页查询用户搜索历史
     * 
     * @param page 分页参数
     * @param userId 用户ID
     * @param keyword 关键词（模糊匹配）
     * @param searchType 搜索类型
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 分页结果
     */
    IPage<SearchHistory> pageUserSearchHistory(
            @Param("page") Page<SearchHistory> page,
            @Param("userId") Long userId,
            @Param("keyword") String keyword,
            @Param("searchType") String searchType,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );

    /**
     * 查询用户最近搜索历史
     * 
     * @param userId 用户ID
     * @param limit 限制数量
     * @return 搜索历史列表
     */
    List<SearchHistory> queryRecentSearchHistory(
            @Param("userId") Long userId,
            @Param("limit") Integer limit
    );

    /**
     * 查询用户热门搜索历史
     * 
     * @param userId 用户ID
     * @param limit 限制数量
     * @return 搜索历史列表
     */
    List<SearchHistory> queryHotSearchHistory(
            @Param("userId") Long userId,
            @Param("limit") Integer limit
    );

    /**
     * 查询用户搜索关键词列表（去重）
     * 
     * @param userId 用户ID
     * @param limit 限制数量
     * @return 关键词列表
     */
    List<String> queryUserSearchKeywords(
            @Param("userId") Long userId,
            @Param("limit") Integer limit
    );

    /**
     * 删除用户指定关键词的搜索历史
     * 
     * @param userId 用户ID
     * @param keyword 关键词
     * @return 删除数量
     */
    int deleteUserSearchHistory(
            @Param("userId") Long userId,
            @Param("keyword") String keyword
    );

    /**
     * 清空用户搜索历史
     * 
     * @param userId 用户ID
     * @return 删除数量
     */
    int clearUserSearchHistory(@Param("userId") Long userId);

    /**
     * 批量插入搜索历史
     * 
     * @param searchHistoryList 搜索历史列表
     * @return 插入数量
     */
    int batchInsert(@Param("list") List<SearchHistory> searchHistoryList);

    /**
     * 统计用户搜索次数
     * 
     * @param userId 用户ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 搜索次数
     */
    Long countUserSearches(
            @Param("userId") Long userId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );

    /**
     * 查询热门搜索关键词
     * 
     * @param limit 限制数量
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 关键词列表
     */
    List<String> queryHotKeywords(
            @Param("limit") Integer limit,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );
} 