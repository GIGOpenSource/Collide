package com.gig.collide.api.payment.request;

import com.gig.collide.base.request.BaseRequest;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 支付配置创建请求
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
public class PaymentConfigCreateRequest extends BaseRequest {

    /**
     * 配置键名（必填）
     */
    @NotBlank(message = "配置键名不能为空")
    @Size(max = 100, message = "配置键名长度不能超过100个字符")
    private String configKey;

    /**
     * 配置值内容（必填）
     */
    @NotBlank(message = "配置值内容不能为空")
    private String configValue;

    /**
     * 配置类型（必填）
     */
    @NotBlank(message = "配置类型不能为空")
    @Size(max = 20, message = "配置类型长度不能超过20个字符")
    private String configType;

    /**
     * 配置分组（可选，默认DEFAULT）
     */
    @Size(max = 50, message = "配置分组长度不能超过50个字符")
    private String configGroup = "DEFAULT";

    /**
     * 配置描述信息（可选）
     */
    @Size(max = 255, message = "配置描述长度不能超过255个字符")
    private String configDesc;

    /**
     * 是否加密存储（可选，默认false）
     */
    private Boolean isEncrypted = false;

    /**
     * 是否启用（可选，默认true）
     */
    private Boolean isEnabled = true;

    /**
     * 是否只读（可选，默认false）
     */
    private Boolean isReadonly = false;

    /**
     * 优先级（可选，默认0）
     */
    @Min(value = 0, message = "优先级不能为负数")
    private Integer priority = 0;

    /**
     * 环境标识（可选，默认prod）
     */
    @Size(max = 20, message = "环境标识长度不能超过20个字符")
    private String envProfile = "prod";

    /**
     * 生效开始时间（可选）
     */
    private LocalDateTime validFrom;

    /**
     * 生效结束时间（可选）
     */
    private LocalDateTime validTo;

    /**
     * 配置版本号（可选，默认1.0）
     */
    @Size(max = 20, message = "配置版本号长度不能超过20个字符")
    private String configVersion = "1.0";

    /**
     * 创建人（可选）
     */
    @Size(max = 64, message = "创建人长度不能超过64个字符")
    private String createdBy;
} 