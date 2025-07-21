package com.gig.collide.artist.domain.repository.impl;

import com.gig.collide.artist.domain.entity.ArtistStatistics;
import com.gig.collide.artist.domain.repository.ArtistStatisticsRepository;
import com.gig.collide.artist.infrastructure.mapper.ArtistStatisticsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * 博主统计仓储实现
 * @author GIG
 */
@Repository
public class ArtistStatisticsRepositoryImpl implements ArtistStatisticsRepository {

    @Autowired
    private ArtistStatisticsMapper artistStatisticsMapper;

    @Override
    public boolean save(ArtistStatistics statistics) {
        return artistStatisticsMapper.insert(statistics) > 0;
    }

    @Override
    public boolean updateById(ArtistStatistics statistics) {
        return artistStatisticsMapper.updateById(statistics) > 0;
    }

    @Override
    public ArtistStatistics findById(Long id) {
        return artistStatisticsMapper.selectById(id);
    }

    @Override
    public ArtistStatistics findByArtistId(Long artistId) {
        return artistStatisticsMapper.findByArtistId(artistId);
    }

    @Override
    public ArtistStatistics findByArtistIdAndPeriod(Long artistId, Date periodStart, Date periodEnd) {
        return artistStatisticsMapper.findByArtistIdAndPeriod(artistId, periodStart, periodEnd);
    }

    @Override
    public ArtistStatistics findLatestByArtistId(Long artistId) {
        return artistStatisticsMapper.findLatestByArtistId(artistId);
    }

    @Override
    public List<ArtistStatistics> findByArtistIds(List<Long> artistIds) {
        return artistStatisticsMapper.findByArtistIds(artistIds);
    }

    @Override
    public List<ArtistStatistics> findByPeriodRange(Date startTime, Date endTime) {
        return artistStatisticsMapper.findByPeriodRange(startTime, endTime);
    }

    @Override
    public List<ArtistStatistics> findByInfluenceIndexDesc(Integer limit) {
        return artistStatisticsMapper.findByInfluenceIndexDesc(limit);
    }

    @Override
    public List<ArtistStatistics> findByEngagementRateDesc(Integer limit) {
        return artistStatisticsMapper.findByEngagementRateDesc(limit);
    }

    @Override
    public List<ArtistStatistics> findByFollowersGrowthDesc(Integer limit) {
        return artistStatisticsMapper.findByFollowersGrowthDesc(limit);
    }

    @Override
    public List<ArtistStatistics> findNeedUpdateStatistics(Date cutoffTime) {
        return artistStatisticsMapper.findNeedUpdateStatistics(cutoffTime);
    }

    @Override
    public boolean batchUpdate(List<ArtistStatistics> statisticsList) {
        return artistStatisticsMapper.batchUpdateStatistics(statisticsList) > 0;
    }

    @Override
    public boolean deleteExpiredStatistics(Date expiredTime) {
        return artistStatisticsMapper.deleteExpiredStatistics(expiredTime) > 0;
    }

    @Override
    public Long countStatistics() {
        return artistStatisticsMapper.countStatistics();
    }

    @Override
    public boolean deleteByArtistId(Long artistId) {
        return artistStatisticsMapper.deleteByArtistId(artistId) > 0;
    }

    @Override
    public List<ArtistStatistics> getRankingData(String rankType, Integer limit) {
        return artistStatisticsMapper.getRankingData(rankType, limit);
    }

    @Override
    public ArtistStatistics saveOrUpdate(ArtistStatistics statistics) {
        // 先尝试根据artistId查找现有记录
        ArtistStatistics existing = findByArtistId(statistics.getArtistId());
        
        if (existing == null) {
            // 如果不存在，执行保存操作
            if (save(statistics)) {
                return statistics;
            }
        } else {
            // 如果存在，更新现有记录的ID并执行更新操作
            statistics.setId(existing.getId());
            if (updateById(statistics)) {
                return statistics;
            }
        }
        
        return null;
    }
} 