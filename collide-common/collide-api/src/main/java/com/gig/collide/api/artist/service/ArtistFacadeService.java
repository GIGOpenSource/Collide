package com.gig.collide.api.artist.service;

import com.gig.collide.api.artist.request.*;
import com.gig.collide.api.artist.response.ArtistOperatorResponse;
import com.gig.collide.api.artist.response.ArtistQueryResponse;
import com.gig.collide.api.artist.response.data.ArtistApplicationInfo;
import com.gig.collide.api.artist.response.data.ArtistInfo;
import com.gig.collide.api.artist.response.data.ArtistStatistics;
import com.gig.collide.base.response.PageResponse;

import java.util.List;

/**
 * 博主门面服务接口
 * @author GIG
 */
public interface ArtistFacadeService {

    // ================ 博主申请管理 ================

    /**
     * 申请成为博主
     * @param applicationRequest
     * @return
     */
    ArtistOperatorResponse applyToBeArtist(ArtistApplicationRequest applicationRequest);

    /**
     * 补充申请材料
     * @param applicationId
     * @param supplementInfo
     * @return
     */
    ArtistOperatorResponse supplementApplication(Long applicationId, String supplementInfo);

    /**
     * 撤回申请
     * @param applicationId
     * @param userId
     * @param reason
     * @return
     */
    ArtistOperatorResponse withdrawApplication(Long applicationId, Long userId, String reason);

    /**
     * 重新申请
     * @param applicationRequest
     * @return
     */
    ArtistOperatorResponse reapplyToBeArtist(ArtistApplicationRequest applicationRequest);

    // ================ 博主审核管理 ================

    /**
     * 审核博主申请
     * @param reviewRequest
     * @return
     */
    ArtistOperatorResponse reviewArtistApplication(ArtistReviewRequest reviewRequest);

    /**
     * 批量审核博主申请
     * @param reviewRequests
     * @return
     */
    ArtistOperatorResponse batchReviewApplications(List<ArtistReviewRequest> reviewRequests);

    /**
     * 获取待审核申请列表
     * @param queryRequest
     * @return
     */
    PageResponse<ArtistApplicationInfo> getPendingReviewApplications(ArtistApplicationQueryRequest queryRequest);

    /**
     * 分配审核员
     * @param applicationIds
     * @param reviewerId
     * @return
     */
    ArtistOperatorResponse assignReviewer(List<Long> applicationIds, Long reviewerId);

    // ================ 博主信息管理 ================

    /**
     * 更新博主信息
     * @param updateRequest
     * @return
     */
    ArtistOperatorResponse updateArtistInfo(ArtistUpdateRequest updateRequest);

    /**
     * 激活博主
     * @param artistId
     * @param operatorId
     * @return
     */
    ArtistOperatorResponse activateArtist(Long artistId, Long operatorId);

    /**
     * 暂停博主
     * @param artistId
     * @param operatorId
     * @param reason
     * @return
     */
    ArtistOperatorResponse suspendArtist(Long artistId, Long operatorId, String reason);

    /**
     * 禁用博主
     * @param artistId
     * @param operatorId
     * @param reason
     * @return
     */
    ArtistOperatorResponse disableArtist(Long artistId, Long operatorId, String reason);

    /**
     * 恢复博主
     * @param artistId
     * @param operatorId
     * @param reason
     * @return
     */
    ArtistOperatorResponse restoreArtist(Long artistId, Long operatorId, String reason);

    /**
     * 注销博主
     * @param artistId
     * @param operatorId
     * @param reason
     * @return
     */
    ArtistOperatorResponse cancelArtist(Long artistId, Long operatorId, String reason);

    // ================ 博主查询 ================

    /**
     * 根据ID查询博主信息
     * @param artistId
     * @return
     */
    ArtistQueryResponse<ArtistInfo> queryArtistById(Long artistId);

    /**
     * 根据用户ID查询博主信息
     * @param userId
     * @return
     */
    ArtistQueryResponse<ArtistInfo> queryArtistByUserId(Long userId);

    /**
     * 查询博主列表
     * @param queryRequest
     * @return
     */
    ArtistQueryResponse<List<ArtistInfo>> queryArtists(ArtistQueryRequest queryRequest);

    /**
     * 分页查询博主
     * @param pageQueryRequest
     * @return
     */
    PageResponse<ArtistInfo> pageQueryArtists(ArtistPageQueryRequest pageQueryRequest);

    /**
     * 搜索博主
     * @param keyword
     * @param limit
     * @return
     */
    ArtistQueryResponse<List<ArtistInfo>> searchArtists(String keyword, Integer limit);

    /**
     * 查询热门博主
     * @param limit
     * @return
     */
    ArtistQueryResponse<List<ArtistInfo>> queryHotArtists(Integer limit);

    /**
     * 查询推荐博主
     * @param userId
     * @param limit
     * @return
     */
    ArtistQueryResponse<List<ArtistInfo>> queryRecommendedArtists(Long userId, Integer limit);

    // ================ 申请查询 ================

    /**
     * 查询用户申请历史
     * @param userId
     * @return
     */
    ArtistQueryResponse<List<ArtistApplicationInfo>> queryUserApplicationHistory(Long userId);

    /**
     * 查询申请详情
     * @param applicationId
     * @return
     */
    ArtistQueryResponse<ArtistApplicationInfo> queryApplicationById(Long applicationId);

    /**
     * 分页查询申请列表
     * @param queryRequest
     * @return
     */
    PageResponse<ArtistApplicationInfo> pageQueryApplications(ArtistApplicationQueryRequest queryRequest);

    // ================ 博主统计 ================

    /**
     * 查询博主统计信息
     * @param artistId
     * @return
     */
    ArtistQueryResponse<ArtistStatistics> queryArtistStatistics(Long artistId);

    /**
     * 查询博主排行榜
     * @param rankType 排行类型（followers/likes/views/influence）
     * @param limit
     * @return
     */
    ArtistQueryResponse<List<ArtistInfo>> queryArtistRanking(String rankType, Integer limit);

    /**
     * 批量查询博主统计
     * @param artistIds
     * @return
     */
    ArtistQueryResponse<List<ArtistStatistics>> batchQueryArtistStatistics(List<Long> artistIds);

    // ================ 认证管理 ================

    /**
     * 申请认证
     * @param artistId
     * @param verificationType
     * @param verificationProof
     * @return
     */
    ArtistOperatorResponse applyVerification(Long artistId, String verificationType, String verificationProof);

    /**
     * 审核认证
     * @param artistId
     * @param approved
     * @param reviewComment
     * @param reviewerId
     * @return
     */
    ArtistOperatorResponse reviewVerification(Long artistId, Boolean approved, String reviewComment, Long reviewerId);

    /**
     * 取消认证
     * @param artistId
     * @param operatorId
     * @param reason
     * @return
     */
    ArtistOperatorResponse revokeVerification(Long artistId, Long operatorId, String reason);

    // ================ 等级管理 ================

    /**
     * 更新博主等级
     * @param artistId
     * @return
     */
    ArtistOperatorResponse updateArtistLevel(Long artistId);

    /**
     * 批量更新博主等级
     * @param artistIds
     * @return
     */
    ArtistOperatorResponse batchUpdateArtistLevel(List<Long> artistIds);

    /**
     * 手动设置博主等级
     * @param artistId
     * @param level
     * @param operatorId
     * @param reason
     * @return
     */
    ArtistOperatorResponse setArtistLevel(Long artistId, String level, Long operatorId, String reason);
} 