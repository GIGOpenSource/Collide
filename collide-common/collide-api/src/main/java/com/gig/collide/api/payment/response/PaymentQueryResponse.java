package com.gig.collide.api.payment.response;

import com.gig.collide.api.payment.response.data.PaymentRecordInfo;
import com.gig.collide.base.response.BaseResponse;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * 支付查询响应
 *
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
@Setter
@Getter
@ToString
public class PaymentQueryResponse extends BaseResponse {

    private static final long serialVersionUID = 1L;

    /**
     * 支付记录列表
     */
    private List<PaymentRecordInfo> paymentRecords;

    /**
     * 查询条件匹配的总数
     */
    private Long totalCount;

    /**
     * 是否还有更多数据
     */
    private Boolean hasMore;
} 