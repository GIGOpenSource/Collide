package com.gig.collide.api.order.response;

import com.gig.collide.base.response.BaseResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.Map;

/**
 * 批量检查内容访问权限响应
 * 
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
public class OrderContentBatchCheckResponse extends BaseResponse {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 内容访问权限结果列表
     */
    private List<ContentAccessResult> accessResults;

    /**
     * 有权限的内容ID列表
     */
    private List<Long> accessibleContentIds;

    /**
     * 无权限的内容ID列表
     */
    private List<Long> inaccessibleContentIds;

    /**
     * 检查时间戳
     */
    private Long checkTimestamp;

    /**
     * 内容访问权限结果
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    public static class ContentAccessResult {
        /**
         * 内容ID
         */
        private Long contentId;

        /**
         * 是否有访问权限
         */
        private Boolean hasAccess;

        /**
         * 访问权限类型
         */
        private String accessType;

        /**
         * 权限状态
         */
        private String accessStatus;

        /**
         * 权限结束时间
         */
        private Long accessEndTime;

        /**
         * 剩余天数
         */
        private Integer remainingDays;
    }
} 