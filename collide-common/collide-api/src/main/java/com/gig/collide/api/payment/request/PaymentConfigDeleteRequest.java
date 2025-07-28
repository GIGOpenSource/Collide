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
 * 支付配置删除请求
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
public class PaymentConfigDeleteRequest extends BaseRequest {

    /**
     * 配置ID
     */
    @NotNull(message = "配置ID不能为空")
    private Long configId;

    /**
     * 批量删除配置ID列表
     */
    private List<Long> configIds;

    /**
     * 删除原因
     */
    private String deleteReason;

    /**
     * 是否强制删除
     */
    private Boolean forceDelete = false;

    /**
     * 根据单个配置ID删除
     */
    public PaymentConfigDeleteRequest(Long configId) {
        this.configId = configId;
    }

    /**
     * 根据批量配置ID删除
     */
    public PaymentConfigDeleteRequest(List<Long> configIds) {
        this.configIds = configIds;
    }
} 