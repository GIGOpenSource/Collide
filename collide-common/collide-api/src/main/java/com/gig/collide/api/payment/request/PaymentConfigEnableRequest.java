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
 * 支付配置启用请求
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
public class PaymentConfigEnableRequest extends BaseRequest {

    /**
     * 配置ID
     */
    @NotNull(message = "配置ID不能为空")
    private Long configId;

    /**
     * 批量启用配置ID列表
     */
    private List<Long> configIds;

    /**
     * 启用说明
     */
    private String enableRemark;

    /**
     * 根据单个配置ID启用
     */
    public PaymentConfigEnableRequest(Long configId) {
        this.configId = configId;
    }

    /**
     * 根据批量配置ID启用
     */
    public PaymentConfigEnableRequest(List<Long> configIds) {
        this.configIds = configIds;
    }
} 