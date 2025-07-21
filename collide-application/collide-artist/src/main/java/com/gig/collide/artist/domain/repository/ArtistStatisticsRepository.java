package com.gig.collide.artist.domain.repository;

import com.gig.collide.artist.domain.entity.ArtistStatistics;

import java.util.Date;
import java.util.List;

/**
 * 博主统计仓储接口
 * @author GIG
 */
public interface ArtistStatisticsRepository {

    /**
     * 保存统计信息
     */
    boolean save(ArtistStatistics statistics);

    /**
     * 更新统计信息
     */
    boolean updateById(ArtistStatistics statistics);

    /**
     * 根据ID查询统计信息
     */
    ArtistStatistics findById(Long id);

    /**
     * 根据博主ID查询统计信息
     */
    ArtistStatistics findByArtistId(Long artistId);

    /**
     * 根据博主ID和统计周期查询
     */
    ArtistStatistics findByArtistIdAndPeriod(Long artistId, Date periodStart, Date periodEnd);

    /**
     * 根据博主ID查询最新统计信息
     */
    ArtistStatistics findLatestByArtistId(Long artistId);

    /**
     * 批量查询博主统计信息
     */
    List<ArtistStatistics> findByArtistIds(List<Long> artistIds);

    /**
     * 查询指定时间范围内的统计数据
     */
    List<ArtistStatistics> findByPeriodRange(Date startTime, Date endTime);

    /**
     * 根据影响力指数排序查询
     */
    List<ArtistStatistics> findByInfluenceIndexDesc(Integer limit);

    /**
     * 根据互动率排序查询
     */
    List<ArtistStatistics> findByEngagementRateDesc(Integer limit);

    /**
     * 根据粉丝增长排序查询
     */
    List<ArtistStatistics> findByFollowersGrowthDesc(Integer limit);

    /**
     * 查询需要更新的统计数据
     */
    List<ArtistStatistics> findNeedUpdateStatistics(Date cutoffTime);

    /**
     * 批量更新统计数据
     */
    boolean batchUpdate(List<ArtistStatistics> statisticsList);

    /**
     * 删除过期统计数据
     */
    boolean deleteExpiredStatistics(Date expiredTime);

    /**
     * 统计总记录数
     */
    Long countStatistics();

    /**
     * 根据博主ID删除统计数据
     */
    boolean deleteByArtistId(Long artistId);

    /**
     * 获取博主排行榜数据
     */
    List<ArtistStatistics> getRankingData(String rankType, Integer limit);

    /**
     * 创建或更新统计数据
     */
    ArtistStatistics saveOrUpdate(ArtistStatistics statistics);
} 