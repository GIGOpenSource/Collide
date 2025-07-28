package com.gig.collide.api.payment.request;

import com.gig.collide.api.payment.constant.PaymentTypeEnum;
import com.gig.collide.api.payment.constant.PaymentSceneEnum;
import com.gig.collide.base.request.BaseRequest;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 支付创建请求
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
public class PaymentCreateRequest extends BaseRequest {

    /**
     * 订单号（必填）
     */
    @NotBlank(message = "订单号不能为空")
    @Size(max = 64, message = "订单号长度不能超过64个字符")
    private String orderNo;

    /**
     * 用户ID（必填）
     */
    @NotNull(message = "用户ID不能为空")
    @Positive(message = "用户ID必须为正数")
    private Long userId;

    /**
     * 用户名称（必填）
     */
    @NotBlank(message = "用户名称不能为空")
    @Size(max = 64, message = "用户名称长度不能超过64个字符")
    private String userName;

    /**
     * 用户手机号（可选）
     */
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String userPhone;

    /**
     * 用户邮箱（可选）
     */
    @Email(message = "邮箱格式不正确")
    private String userEmail;

    /**
     * 订单标题（必填）
     */
    @NotBlank(message = "订单标题不能为空")
    @Size(max = 255, message = "订单标题长度不能超过255个字符")
    private String orderTitle;

    /**
     * 商品名称（可选）
     */
    @Size(max = 255, message = "商品名称长度不能超过255个字符")
    private String productName;

    /**
     * 商品类型（可选）
     */
    @Size(max = 50, message = "商品类型长度不能超过50个字符")
    private String productType;

    /**
     * 商户ID（可选）
     */
    private Long merchantId;

    /**
     * 商户名称（可选）
     */
    @Size(max = 128, message = "商户名称长度不能超过128个字符")
    private String merchantName;

    /**
     * 支付金额（必填）
     */
    @NotNull(message = "支付金额不能为空")
    @DecimalMin(value = "0.01", message = "支付金额必须大于0.01元")
    @DecimalMax(value = "999999.99", message = "支付金额不能超过999999.99元")
    private BigDecimal payAmount;

    /**
     * 优惠金额（可选）
     */
    @DecimalMin(value = "0.00", message = "优惠金额不能为负数")
    private BigDecimal discountAmount = BigDecimal.ZERO;

    /**
     * 货币代码（可选，默认CNY）
     */
    @Size(max = 10, message = "货币代码长度不能超过10个字符")
    private String currencyCode = "CNY";

    /**
     * 支付方式（必填）
     */
    @NotNull(message = "支付方式不能为空")
    private PaymentTypeEnum payType;

    /**
     * 支付场景（可选，默认WEB）
     */
    private PaymentSceneEnum payScene = PaymentSceneEnum.WEB;

    /**
     * 支付渠道（可选）
     */
    @Size(max = 50, message = "支付渠道长度不能超过50个字符")
    private String payChannel;

    /**
     * 具体支付方式（可选）
     */
    @Size(max = 50, message = "支付方式长度不能超过50个字符")
    private String payMethod;

    /**
     * 支付过期时间（可选）
     */
    private LocalDateTime expireTime;

    /**
     * 客户端IP地址（可选）
     */
    @Size(max = 45, message = "IP地址长度不能超过45个字符")
    private String clientIp;

    /**
     * 用户代理信息（可选）
     */
    @Size(max = 512, message = "用户代理信息长度不能超过512个字符")
    private String userAgent;

    /**
     * 设备信息（可选）
     */
    @Size(max = 255, message = "设备信息长度不能超过255个字符")
    private String deviceInfo;

    /**
     * 异步回调通知URL（可选）
     */
    @Size(max = 512, message = "回调URL长度不能超过512个字符")
    private String notifyUrl;

    /**
     * 同步返回URL（可选）
     */
    @Size(max = 512, message = "返回URL长度不能超过512个字符")
    private String returnUrl;

    /**
     * 备注信息（可选）
     */
    @Size(max = 500, message = "备注信息长度不能超过500个字符")
    private String remark;
} 