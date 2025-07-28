package com.gig.collide.api.payment.request;

import com.gig.collide.base.request.BaseRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;

/**
 * 支付配置更新请求
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
public class PaymentConfigUpdateRequest extends BaseRequest {

    /**
     * 配置ID
     */
    @NotNull(message = "配置ID不能为空")
    private Long configId;

    /**
     * 配置名称
     */
    @NotBlank(message = "配置名称不能为空")
    private String configName;

    /**
     * 配置类型
     */
    @NotBlank(message = "配置类型不能为空")
    private String configType;

    /**
     * 配置内容
     */
    @NotBlank(message = "配置内容不能为空")
    private String configContent;

    /**
     * 配置参数
     */
    private Map<String, Object> configParams;

    /**
     * 环境标识
     */
    @NotBlank(message = "环境标识不能为空")
    private String envProfile;

    /**
     * 更新说明
     */
    private String updateRemark;
} 