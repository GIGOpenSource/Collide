package com.gig.collide.search.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gig.collide.search.domain.entity.SearchHistory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 搜索历史Mapper接口 - 简洁版
 *
 * @author GIG Team
 * @version 2.0.0
 */
@Mapper
public interface SearchHistoryMapper extends BaseMapper<SearchHistory> {

    /**
     * 根据用户ID查询搜索历史
     */
    List<SearchHistory> selectByUserId(@Param("userId") Long userId, @Param("limit") Integer limit);

    /**
     * 根据用户ID和搜索类型查询历史
     */
    List<SearchHistory> selectByUserIdAndType(@Param("userId") Long userId, 
                                             @Param("searchType") String searchType, 
                                             @Param("limit") Integer limit);

    /**
     * 获取用户搜索偏好（按搜索次数统计）
     */
    List<SearchHistory> selectUserSearchPreferences(@Param("userId") Long userId, @Param("limit") Integer limit);

    /**
     * 清空用户搜索历史
     */
    int deleteByUserId(@Param("userId") Long userId);

    /**
     * 检查关键词是否存在于用户历史中
     */
    int countByUserIdAndKeyword(@Param("userId") Long userId, @Param("keyword") String keyword);

    /**
     * 获取热门搜索关键词（基于搜索历史统计）
     */
    List<String> selectHotKeywords(@Param("limit") Integer limit);

    /**
     * 根据关键词前缀获取搜索建议
     */
    List<String> selectSuggestionsByPrefix(@Param("prefix") String prefix, @Param("limit") Integer limit);
} 