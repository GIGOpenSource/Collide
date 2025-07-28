package com.gig.collide.api.payment.request;

import com.gig.collide.base.request.BaseRequest;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 支付配置查询请求
 *
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class PaymentConfigQueryRequest extends BaseRequest {

    /**
     * 配置键名（可选）
     */
    @Size(max = 100, message = "配置键名长度不能超过100个字符")
    private String configKey;

    /**
     * 配置类型（可选）
     */
    @Size(max = 20, message = "配置类型长度不能超过20个字符")
    private String configType;

    /**
     * 配置分组（可选）
     */
    @Size(max = 50, message = "配置分组长度不能超过50个字符")
    private String configGroup;

    /**
     * 是否启用（可选）
     */
    private Boolean isEnabled;

    /**
     * 是否只读（可选）
     */
    private Boolean isReadonly;

    /**
     * 环境标识（可选）
     */
    @Size(max = 20, message = "环境标识长度不能超过20个字符")
    private String envProfile;

    /**
     * 配置版本号（可选）
     */
    @Size(max = 20, message = "配置版本号长度不能超过20个字符")
    private String configVersion;

    /**
     * 创建时间开始（可选）
     */
    private LocalDateTime createTimeStart;

    /**
     * 创建时间结束（可选）
     */
    private LocalDateTime createTimeEnd;

    /**
     * 是否包含敏感信息（可选，默认false）
     */
    private Boolean includeSensitive = false;

    /**
     * 关键字搜索（可选）
     */
    @Size(max = 100, message = "关键字长度不能超过100个字符")
    private String keyword;
} 