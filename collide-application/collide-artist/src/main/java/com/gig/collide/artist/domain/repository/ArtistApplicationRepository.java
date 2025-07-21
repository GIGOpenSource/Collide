package com.gig.collide.artist.domain.repository;

import com.gig.collide.artist.domain.entity.ArtistApplication;
import com.gig.collide.api.artist.constant.ArtistApplicationType;
import com.gig.collide.api.artist.constant.ArtistReviewResult;
import com.gig.collide.api.artist.constant.ArtistStatus;

import java.util.Date;
import java.util.List;

/**
 * 博主申请仓储接口
 * @author GIG
 */
public interface ArtistApplicationRepository {

    /**
     * 保存申请信息
     */
    boolean save(ArtistApplication application);

    /**
     * 更新申请信息
     */
    boolean updateById(ArtistApplication application);

    /**
     * 根据ID查询申请
     */
    ArtistApplication findById(Long applicationId);

    /**
     * 根据用户ID查询申请列表
     */
    List<ArtistApplication> findByUserId(Long userId);

    /**
     * 根据用户ID查询最新申请
     */
    ArtistApplication findLatestByUserId(Long userId);

    /**
     * 根据状态查询申请列表
     */
    List<ArtistApplication> findByStatus(ArtistStatus status);

    /**
     * 查询待审核申请
     */
    List<ArtistApplication> findPendingReview();

    /**
     * 根据审核员查询申请列表
     */
    List<ArtistApplication> findByReviewerId(Long reviewerId);

    /**
     * 根据审核结果查询申请列表
     */
    List<ArtistApplication> findByReviewResult(ArtistReviewResult reviewResult);

    /**
     * 分页查询申请列表
     */
    List<ArtistApplication> pageQuery(Integer currentPage, Integer pageSize, 
                                     ArtistStatus status, ArtistApplicationType applicationType,
                                     ArtistReviewResult reviewResult, Long reviewerId);

    /**
     * 根据申请类型查询
     */
    List<ArtistApplication> findByApplicationType(ArtistApplicationType applicationType);

    /**
     * 查询指定时间范围内的申请
     */
    List<ArtistApplication> findBySubmitTimeRange(Date startTime, Date endTime);

    /**
     * 统计申请总数
     */
    Long countApplications();

    /**
     * 根据状态统计申请数量
     */
    Long countByStatus(ArtistStatus status);

    /**
     * 根据审核结果统计申请数量
     */
    Long countByReviewResult(ArtistReviewResult reviewResult);

    /**
     * 查询用户申请历史
     */
    List<ArtistApplication> findUserApplicationHistory(Long userId);

    /**
     * 批量查询申请
     */
    List<ArtistApplication> findByIds(List<Long> applicationIds);

    /**
     * 查询超时未审核的申请
     */
    List<ArtistApplication> findTimeoutApplications(Integer timeoutDays);

    /**
     * 检查用户是否有进行中的申请
     */
    boolean hasOngoingApplication(Long userId);
} 