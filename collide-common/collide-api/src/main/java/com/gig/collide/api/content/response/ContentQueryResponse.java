package com.gig.collide.api.content.response;

import com.gig.collide.api.content.response.data.ContentInfo;
import com.gig.collide.api.content.response.data.ContentReviewInfo;
import com.gig.collide.api.content.response.data.ContentStatistics;
import com.gig.collide.base.response.BaseResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 内容查询响应
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ContentQueryResponse extends BaseResponse {

    /**
     * 内容信息
     */
    private ContentInfo contentInfo;

    /**
     * 内容列表
     */
    private List<ContentInfo> contentList;

    /**
     * 审核信息
     */
    private ContentReviewInfo reviewInfo;

    /**
     * 审核信息列表
     */
    private List<ContentReviewInfo> reviewList;

    /**
     * 统计信息
     */
    private ContentStatistics statistics;

    /**
     * 统计信息列表
     */
    private List<ContentStatistics> statisticsList;

    /**
     * 总数
     */
    private Long total;

    /**
     * 当前页
     */
    private Integer currentPage;

    /**
     * 每页大小
     */
    private Integer pageSize;

    public static ContentQueryResponse success(ContentInfo contentInfo) {
        ContentQueryResponse response = new ContentQueryResponse();
        response.setSuccess(true);
        response.setContentInfo(contentInfo);
        return response;
    }

    public static ContentQueryResponse success(List<ContentInfo> contentList) {
        ContentQueryResponse response = new ContentQueryResponse();
        response.setSuccess(true);
        response.setContentList(contentList);
        return response;
    }

    public static ContentQueryResponse success(List<ContentInfo> contentList, Long total, Integer currentPage, Integer pageSize) {
        ContentQueryResponse response = new ContentQueryResponse();
        response.setSuccess(true);
        response.setContentList(contentList);
        response.setTotal(total);
        response.setCurrentPage(currentPage);
        response.setPageSize(pageSize);
        return response;
    }

    public static ContentQueryResponse fail(String message) {
        ContentQueryResponse response = new ContentQueryResponse();
        response.setSuccess(false);
        return response;
    }
} 