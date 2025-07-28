package com.gig.collide.api.payment.response;

import com.gig.collide.api.payment.response.data.PaymentConfigInfo;
import com.gig.collide.base.response.BaseResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 支付配置更新响应
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
public class PaymentConfigUpdateResponse extends BaseResponse {

    /**
     * 更新后的配置信息
     */
    private PaymentConfigInfo configInfo;

    /**
     * 更新时间戳
     */
    private Long updateTimestamp;

    /**
     * 配置版本号
     */
    private String configVersion;
} 