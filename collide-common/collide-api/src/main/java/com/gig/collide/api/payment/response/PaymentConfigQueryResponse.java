package com.gig.collide.api.payment.response;

import com.gig.collide.api.payment.response.data.PaymentConfigInfo;
import com.gig.collide.base.response.BaseResponse;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * 支付配置查询响应
 *
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
@Setter
@Getter
@ToString
public class PaymentConfigQueryResponse extends BaseResponse {

    private static final long serialVersionUID = 1L;

    /**
     * 配置记录列表
     */
    private List<PaymentConfigInfo> configRecords;

    /**
     * 查询条件匹配的总数
     */
    private Long totalCount;

    /**
     * 是否还有更多数据
     */
    private Boolean hasMore;

    /**
     * 启用配置数量
     */
    private Integer enabledCount;

    /**
     * 禁用配置数量
     */
    private Integer disabledCount;

    /**
     * 配置分组统计
     */
    private List<ConfigGroupStat> groupStats;

    /**
     * 配置分组统计信息
     */
    @Getter
    @Setter
    public static class ConfigGroupStat {
        private String configGroup;
        private Integer count;
        private Integer enabledCount;
        private Integer disabledCount;
    }
} 