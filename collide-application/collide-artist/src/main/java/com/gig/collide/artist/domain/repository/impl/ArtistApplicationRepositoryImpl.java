package com.gig.collide.artist.domain.repository.impl;

import com.gig.collide.artist.domain.entity.ArtistApplication;
import com.gig.collide.artist.domain.repository.ArtistApplicationRepository;
import com.gig.collide.artist.infrastructure.mapper.ArtistApplicationMapper;
import com.gig.collide.api.artist.constant.ArtistApplicationType;
import com.gig.collide.api.artist.constant.ArtistReviewResult;
import com.gig.collide.api.artist.constant.ArtistStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * 博主申请仓储实现
 * @author GIG
 */
@Repository
public class ArtistApplicationRepositoryImpl implements ArtistApplicationRepository {

    @Autowired
    private ArtistApplicationMapper artistApplicationMapper;

    @Override
    public ArtistApplication findById(Long applicationId) {
        return artistApplicationMapper.selectById(applicationId);
    }

    @Override
    public List<ArtistApplication> findByUserId(Long userId) {
        return artistApplicationMapper.findByUserId(userId);
    }

    @Override
    public ArtistApplication findLatestByUserId(Long userId) {
        return artistApplicationMapper.findLatestByUserId(userId);
    }

    @Override
    public List<ArtistApplication> findByStatus(ArtistStatus status) {
        return artistApplicationMapper.findByStatus(status, null);
    }

    @Override
    public List<ArtistApplication> findPendingReview() {
        return artistApplicationMapper.findPendingReview(null);
    }

    @Override
    public List<ArtistApplication> findByReviewerId(Long reviewerId) {
        return artistApplicationMapper.findByReviewerId(reviewerId, null);
    }

    @Override
    public List<ArtistApplication> findByReviewResult(ArtistReviewResult reviewResult) {
        return artistApplicationMapper.findByReviewResult(reviewResult, null);
    }

    @Override
    public List<ArtistApplication> pageQuery(Integer currentPage, Integer pageSize, 
                                           ArtistStatus status, ArtistApplicationType applicationType,
                                           ArtistReviewResult reviewResult, Long reviewerId) {
        Integer offset = (currentPage - 1) * pageSize;
        return artistApplicationMapper.pageQuery(offset, pageSize, status, applicationType, reviewResult, reviewerId);
    }

    @Override
    public List<ArtistApplication> findByApplicationType(ArtistApplicationType applicationType) {
        return artistApplicationMapper.findByApplicationType(applicationType, null);
    }

    @Override
    public List<ArtistApplication> findBySubmitTimeRange(Date startTime, Date endTime) {
        return artistApplicationMapper.findBySubmitTimeRange(startTime, endTime);
    }

    @Override
    public Long countApplications() {
        return artistApplicationMapper.countApplications();
    }

    @Override
    public Long countByStatus(ArtistStatus status) {
        return artistApplicationMapper.countByStatus(status);
    }

    @Override
    public Long countByReviewResult(ArtistReviewResult reviewResult) {
        return artistApplicationMapper.countByReviewResult(reviewResult);
    }

    @Override
    public List<ArtistApplication> findUserApplicationHistory(Long userId) {
        return artistApplicationMapper.findUserApplicationHistory(userId);
    }

    @Override
    public List<ArtistApplication> findByIds(List<Long> applicationIds) {
        return artistApplicationMapper.findByIds(applicationIds);
    }

    @Override
    public List<ArtistApplication> findTimeoutApplications(Integer timeoutDays) {
        return artistApplicationMapper.findTimeoutApplications(timeoutDays);
    }

    @Override
    public boolean hasOngoingApplication(Long userId) {
        return artistApplicationMapper.countOngoingApplicationByUserId(userId) > 0;
    }

    @Override
    public boolean save(ArtistApplication application) {
        return artistApplicationMapper.insert(application) > 0;
    }

    @Override
    public boolean updateById(ArtistApplication application) {
        return artistApplicationMapper.updateById(application) > 0;
    }
} 