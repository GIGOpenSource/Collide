package com.gig.collide.api.artist.request;

import com.gig.collide.api.artist.constant.ArtistApplicationType;
import com.gig.collide.api.artist.constant.ArtistReviewResult;
import com.gig.collide.api.artist.constant.ArtistStatus;
import com.gig.collide.base.request.BaseRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;
import java.util.List;

/**
 * 博主申请查询请求
 * @author GIG
 */
@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ArtistApplicationQueryRequest extends BaseRequest {

    /**
     * 申请ID列表
     */
    private List<Long> applicationIds;

    /**
     * 用户ID列表
     */
    private List<Long> userIds;

    /**
     * 申请类型
     */
    private ArtistApplicationType applicationType;

    /**
     * 申请状态
     */
    private ArtistStatus status;

    /**
     * 审核结果
     */
    private ArtistReviewResult reviewResult;

    /**
     * 审核员ID
     */
    private Long reviewerId;

    /**
     * 申请开始时间
     */
    private Date startTime;

    /**
     * 申请结束时间
     */
    private Date endTime;

    /**
     * 是否只查询待审核
     */
    private Boolean onlyPendingReview = false;

    /**
     * 排序字段
     */
    private String sortBy = "submitTime";

    /**
     * 排序方向
     */
    private String sortDirection = "DESC";

    /**
     * 当前页
     */
    private int currentPage = 1;

    /**
     * 页面大小
     */
    private int pageSize = 10;
} 