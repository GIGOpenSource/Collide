package com.gig.collide.api.payment.request;

import com.gig.collide.base.request.BaseRequest;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

/**
 * 支付统计生成请求
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
public class PaymentStatisticsGenerateRequest extends BaseRequest {

    /**
     * 统计日期（必填）
     */
    @NotNull(message = "统计日期不能为空")
    private LocalDate statDate;

    /**
     * 是否包含小时统计（可选，默认false）
     */
    private Boolean includeHourly = false;

    /**
     * 是否强制重新生成（可选，默认false）
     */
    private Boolean forceRegenerate = false;

    /**
     * 操作员ID（可选）
     */
    private Long operatorId;

    /**
     * 生成原因（可选）
     */
    @Size(max = 255, message = "生成原因长度不能超过255个字符")
    private String generateReason;
} 