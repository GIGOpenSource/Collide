package com.gig.collide.api.content.response;

import com.gig.collide.base.response.BaseResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 内容操作响应
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ContentOperatorResponse extends BaseResponse {

    /**
     * 内容ID
     */
    private Long contentId;

    /**
     * 审核ID（审核操作时返回）
     */
    private Long reviewId;

    public static ContentOperatorResponse success(Long contentId) {
        ContentOperatorResponse response = new ContentOperatorResponse();
        response.setSuccess(true);
        response.setContentId(contentId);
        return response;
    }

    public static ContentOperatorResponse success(Long contentId, Long reviewId) {
        ContentOperatorResponse response = new ContentOperatorResponse();
        response.setSuccess(true);
        response.setContentId(contentId);
        response.setReviewId(reviewId);
        return response;
    }

    public static ContentOperatorResponse fail(String message) {
        ContentOperatorResponse response = new ContentOperatorResponse();
        response.setSuccess(false);
        return response;
    }
} 