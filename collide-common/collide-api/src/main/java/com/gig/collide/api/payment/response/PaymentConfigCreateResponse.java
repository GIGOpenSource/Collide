package com.gig.collide.api.payment.response;

import com.gig.collide.base.response.BaseResponse;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 支付配置创建响应
 *
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
@Setter
@Getter
@ToString
public class PaymentConfigCreateResponse extends BaseResponse {

    private static final long serialVersionUID = 1L;

    /**
     * 配置ID
     */
    private Long configId;

    /**
     * 配置键名
     */
    private String configKey;

    /**
     * 配置类型
     */
    private String configType;

    /**
     * 配置分组
     */
    private String configGroup;

    /**
     * 环境标识
     */
    private String envProfile;

    /**
     * 配置版本号
     */
    private String configVersion;

    /**
     * 是否启用
     */
    private Boolean isEnabled;

    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 创建人
     */
    private String createdBy;
} 