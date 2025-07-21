package com.gig.collide.artist.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gig.collide.artist.domain.entity.ArtistStatistics;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 博主统计Mapper
 * @author GIG
 */
@Mapper
public interface ArtistStatisticsMapper extends BaseMapper<ArtistStatistics> {

    /**
     * 根据博主ID查询统计信息
     */
    ArtistStatistics findByArtistId(@Param("artistId") Long artistId);

    /**
     * 根据博主ID和统计周期查询
     */
    ArtistStatistics findByArtistIdAndPeriod(@Param("artistId") Long artistId, 
                                            @Param("periodStart") Date periodStart, 
                                            @Param("periodEnd") Date periodEnd);

    /**
     * 根据博主ID查询最新统计信息
     */
    ArtistStatistics findLatestByArtistId(@Param("artistId") Long artistId);

    /**
     * 批量查询博主统计信息
     */
    List<ArtistStatistics> findByArtistIds(@Param("artistIds") List<Long> artistIds);

    /**
     * 查询指定时间范围内的统计数据
     */
    List<ArtistStatistics> findByPeriodRange(@Param("startTime") Date startTime, @Param("endTime") Date endTime);

    /**
     * 根据影响力指数排序查询
     */
    List<ArtistStatistics> findByInfluenceIndexDesc(@Param("limit") Integer limit);

    /**
     * 根据互动率排序查询
     */
    List<ArtistStatistics> findByEngagementRateDesc(@Param("limit") Integer limit);

    /**
     * 根据粉丝增长排序查询
     */
    List<ArtistStatistics> findByFollowersGrowthDesc(@Param("limit") Integer limit);

    /**
     * 查询需要更新的统计数据
     */
    List<ArtistStatistics> findNeedUpdateStatistics(@Param("cutoffTime") Date cutoffTime);

    /**
     * 批量更新统计数据
     */
    int batchUpdateStatistics(@Param("statisticsList") List<ArtistStatistics> statisticsList);

    /**
     * 删除过期统计数据
     */
    int deleteExpiredStatistics(@Param("expiredTime") Date expiredTime);

    /**
     * 根据博主ID删除统计数据
     */
    int deleteByArtistId(@Param("artistId") Long artistId);

    /**
     * 获取博主排行榜数据
     */
    List<ArtistStatistics> getRankingData(@Param("rankType") String rankType, @Param("limit") Integer limit);

    /**
     * 统计总记录数
     */
    Long countStatistics();

    /**
     * 根据博主ID和周期查询是否存在记录
     */
    int countByArtistIdAndPeriod(@Param("artistId") Long artistId, 
                                @Param("periodStart") Date periodStart, 
                                @Param("periodEnd") Date periodEnd);
} 