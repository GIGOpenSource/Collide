package com.gig.collide.tag.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gig.collide.tag.domain.entity.TagStatistics;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;

/**
 * 标签统计Mapper接口
 * 
 * @author GIG Team
 * @version 1.0.0
 * @since 2024-12-19
 */
@Mapper
public interface TagStatisticsMapper extends BaseMapper<TagStatistics> {

    /**
     * 根据标签ID和统计日期查询统计记录
     * 
     * @param tagId 标签ID
     * @param statDate 统计日期
     * @return 标签统计记录
     */
    @Select("SELECT * FROM t_tag_statistics WHERE tag_id = #{tagId} AND stat_date = #{statDate} LIMIT 1")
    TagStatistics selectByTagIdAndStatDate(@Param("tagId") Long tagId, @Param("statDate") LocalDate statDate);

    /**
     * 根据标签ID查询统计记录（按日期倒序）
     * 
     * @param tagId 标签ID
     * @param limit 限制数量
     * @return 标签统计记录列表
     */
    @Select("SELECT * FROM t_tag_statistics WHERE tag_id = #{tagId} ORDER BY stat_date DESC LIMIT #{limit}")
    List<TagStatistics> selectByTagIdOrderByDateDesc(@Param("tagId") Long tagId, @Param("limit") Integer limit);

    /**
     * 根据统计日期查询所有标签统计
     * 
     * @param statDate 统计日期
     * @return 标签统计记录列表
     */
    @Select("SELECT * FROM t_tag_statistics WHERE stat_date = #{statDate} ORDER BY daily_usage_count DESC")
    List<TagStatistics> selectByStatDate(@Param("statDate") LocalDate statDate);

    /**
     * 查询日期范围内的标签统计
     * 
     * @param tagId 标签ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 标签统计记录列表
     */
    @Select("SELECT * FROM t_tag_statistics WHERE tag_id = #{tagId} " +
            "AND stat_date BETWEEN #{startDate} AND #{endDate} ORDER BY stat_date DESC")
    List<TagStatistics> selectByTagIdAndDateRange(@Param("tagId") Long tagId, 
                                                  @Param("startDate") LocalDate startDate, 
                                                  @Param("endDate") LocalDate endDate);

    /**
     * 查询最近N天的热门标签统计
     * 
     * @param days 天数
     * @param limit 限制数量
     * @return 标签统计记录列表
     */
    @Select("SELECT tag_id, SUM(daily_usage_count) as total_usage, " +
            "SUM(active_user_count) as total_active_users, " +
            "SUM(content_count) as total_content " +
            "FROM t_tag_statistics " +
            "WHERE stat_date >= DATE_SUB(CURDATE(), INTERVAL #{days} DAY) " +
            "GROUP BY tag_id " +
            "ORDER BY total_usage DESC " +
            "LIMIT #{limit}")
    List<TagStatistics> selectHotTagsInRecentDays(@Param("days") Integer days, @Param("limit") Integer limit);

    /**
     * 删除指定日期之前的统计数据（用于清理过期数据）
     * 
     * @param beforeDate 指定日期
     * @return 删除行数
     */
    @Select("DELETE FROM t_tag_statistics WHERE stat_date < #{beforeDate}")
    int deleteBeforeDate(@Param("beforeDate") LocalDate beforeDate);
} 