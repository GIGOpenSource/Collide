package com.gig.collide.artist.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gig.collide.artist.domain.entity.ArtistApplication;
import com.gig.collide.api.artist.constant.ArtistApplicationType;
import com.gig.collide.api.artist.constant.ArtistReviewResult;
import com.gig.collide.api.artist.constant.ArtistStatus;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 博主申请Mapper
 * @author GIG
 */
@Mapper
public interface ArtistApplicationMapper extends BaseMapper<ArtistApplication> {

    /**
     * 根据用户ID查询申请列表
     */
    List<ArtistApplication> findByUserId(@Param("userId") Long userId);

    /**
     * 根据用户ID查询最新申请
     */
    ArtistApplication findLatestByUserId(@Param("userId") Long userId);

    /**
     * 根据状态查询申请列表
     */
    List<ArtistApplication> findByStatus(@Param("status") ArtistStatus status, @Param("limit") Integer limit);

    /**
     * 查询待审核申请
     */
    List<ArtistApplication> findPendingReview(@Param("limit") Integer limit);

    /**
     * 根据审核员查询申请列表
     */
    List<ArtistApplication> findByReviewerId(@Param("reviewerId") Long reviewerId, @Param("limit") Integer limit);

    /**
     * 根据审核结果查询申请列表
     */
    List<ArtistApplication> findByReviewResult(@Param("reviewResult") ArtistReviewResult reviewResult, @Param("limit") Integer limit);

    /**
     * 分页查询申请列表
     */
    List<ArtistApplication> pageQuery(@Param("offset") Integer offset, @Param("pageSize") Integer pageSize, 
                                     @Param("status") ArtistStatus status, 
                                     @Param("applicationType") ArtistApplicationType applicationType,
                                     @Param("reviewResult") ArtistReviewResult reviewResult, 
                                     @Param("reviewerId") Long reviewerId);

    /**
     * 根据申请类型查询
     */
    List<ArtistApplication> findByApplicationType(@Param("applicationType") ArtistApplicationType applicationType, @Param("limit") Integer limit);

    /**
     * 查询指定时间范围内的申请
     */
    List<ArtistApplication> findBySubmitTimeRange(@Param("startTime") Date startTime, @Param("endTime") Date endTime);

    /**
     * 根据状态统计申请数量
     */
    Long countByStatus(@Param("status") ArtistStatus status);

    /**
     * 根据审核结果统计申请数量
     */
    Long countByReviewResult(@Param("reviewResult") ArtistReviewResult reviewResult);

    /**
     * 查询用户申请历史
     */
    List<ArtistApplication> findUserApplicationHistory(@Param("userId") Long userId);

    /**
     * 批量查询申请
     */
    List<ArtistApplication> findByIds(@Param("applicationIds") List<Long> applicationIds);

    /**
     * 查询超时未审核的申请
     */
    List<ArtistApplication> findTimeoutApplications(@Param("timeoutDays") Integer timeoutDays);

    /**
     * 检查用户是否有进行中的申请
     */
    int countOngoingApplicationByUserId(@Param("userId") Long userId);

    /**
     * 统计申请总数
     */
    Long countApplications();
} 