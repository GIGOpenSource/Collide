package com.gig.collide.business.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gig.collide.business.domain.search.entity.SearchHistory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 搜索历史数据访问映射器
 *
 * @author GIG Team
 */
@Mapper
public interface SearchHistoryMapper extends BaseMapper<SearchHistory> {

    /**
     * 根据用户ID查询搜索历史
     *
     * @param userId 用户ID
     * @param limit 限制数量
     * @return 搜索历史列表
     */
    List<SearchHistory> selectByUserId(@Param("userId") Long userId, @Param("limit") Integer limit);

    /**
     * 查询用户最近的搜索关键词
     *
     * @param userId 用户ID
     * @param limit 限制数量
     * @return 关键词列表
     */
    List<String> selectRecentKeywordsByUserId(@Param("userId") Long userId, @Param("limit") Integer limit);

    /**
     * 删除用户过期的搜索历史
     *
     * @param userId 用户ID
     * @param beforeTime 时间界限
     * @return 删除的记录数
     */
    int deleteExpiredByUserId(@Param("userId") Long userId, @Param("beforeTime") LocalDateTime beforeTime);

    /**
     * 清空用户搜索历史
     *
     * @param userId 用户ID
     * @return 删除的记录数
     */
    int deleteByUserId(@Param("userId") Long userId);
} 