package com.gig.collide.api.payment.response.data;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 支付配置信息响应对象
 *
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
@Getter
@Setter
@NoArgsConstructor
public class PaymentConfigInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 配置ID
     */
    private Long id;

    /**
     * 配置键名
     */
    private String configKey;

    /**
     * 配置值内容
     */
    private String configValue;

    /**
     * 配置类型
     */
    private String configType;

    /**
     * 配置分组
     */
    private String configGroup;

    /**
     * 配置描述信息
     */
    private String configDesc;

    /**
     * 是否加密存储
     */
    private Boolean isEncrypted;

    /**
     * 是否启用
     */
    private Boolean isEnabled;

    /**
     * 是否只读
     */
    private Boolean isReadonly;

    /**
     * 优先级
     */
    private Integer priority;

    /**
     * 环境标识
     */
    private String envProfile;

    /**
     * 生效开始时间
     */
    private LocalDateTime validFrom;

    /**
     * 生效结束时间
     */
    private LocalDateTime validTo;

    /**
     * 配置版本号
     */
    private String configVersion;

    /**
     * 最后修改人
     */
    private String lastModifiedBy;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 是否当前有效（根据时间范围计算）
     */
    private Boolean isCurrentlyValid;

    /**
     * 配置状态描述
     */
    private String statusDescription;
} 