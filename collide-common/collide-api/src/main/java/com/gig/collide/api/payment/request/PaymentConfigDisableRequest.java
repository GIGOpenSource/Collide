package com.gig.collide.api.payment.request;

import com.gig.collide.base.request.BaseRequest;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * 支付配置禁用请求
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
public class PaymentConfigDisableRequest extends BaseRequest {

    /**
     * 配置ID
     */
    @NotNull(message = "配置ID不能为空")
    private Long configId;

    /**
     * 批量禁用配置ID列表
     */
    private List<Long> configIds;

    /**
     * 禁用原因
     */
    private String disableReason;

    /**
     * 根据单个配置ID禁用
     */
    public PaymentConfigDisableRequest(Long configId) {
        this.configId = configId;
    }

    /**
     * 根据批量配置ID禁用
     */
    public PaymentConfigDisableRequest(List<Long> configIds) {
        this.configIds = configIds;
    }
} 