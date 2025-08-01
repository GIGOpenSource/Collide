package com.gig.collide.api.order.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

/**
 * 订单创建请求DTO
 * 用于创建新订单的参数传递
 *
 * @author GIG Team
 * @version 2.0.0 (扩展版)
 * @since 2024-01-31
 */
@Data
@Accessors(chain = true)
@Schema(description = "订单创建请求")
public class OrderCreateRequest {

    @NotNull(message = "用户ID不能为空")
    @Positive(message = "用户ID必须为正数")
    @Schema(description = "用户ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long userId;

    @Size(max = 100, message = "用户昵称长度不能超过100字符")
    @Schema(description = "用户昵称（可选，系统会自动填充）")
    private String userNickname;

    @NotNull(message = "商品ID不能为空")
    @Positive(message = "商品ID必须为正数")
    @Schema(description = "商品ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long goodsId;

    @NotNull(message = "购买数量不能为空")
    @Min(value = 1, message = "购买数量必须大于0")
    @Max(value = 999, message = "购买数量不能超过999")
    @Schema(description = "购买数量", requiredMode = Schema.RequiredMode.REQUIRED, minimum = "1", maximum = "999")
    private Integer quantity;

    @DecimalMin(value = "0.00", message = "优惠金额不能为负数")
    @Digits(integer = 8, fraction = 2, message = "优惠金额格式不正确")
    @Schema(description = "优惠金额（可选）")
    private BigDecimal discountAmount;

    @Size(max = 200, message = "备注长度不能超过200字符")
    @Schema(description = "订单备注（可选）")
    private String remarks;

    @Schema(description = "优惠券代码（可选）")
    private String couponCode;

    @Schema(description = "推广员ID（可选）")
    private Long promoterId;

    @Pattern(regexp = "^(web|app|h5|mini)$", message = "订单来源只能是: web、app、h5、mini")
    @Schema(description = "订单来源", allowableValues = {"web", "app", "h5", "mini"}, defaultValue = "web")
    private String source = "web";

    // =================== 扩展字段（用于支持不同商品类型和支付模式） ===================

    @Pattern(regexp = "^(coin|goods|subscription|content)$", message = "商品类型只能是: coin、goods、subscription、content")
    @Schema(description = "商品类型", allowableValues = {"coin", "goods", "subscription", "content"})
    private String goodsType;

    @Pattern(regexp = "^(cash|coin)$", message = "支付模式只能是: cash、coin")
    @Schema(description = "支付模式", allowableValues = {"cash", "coin"})
    private String paymentMode;

    @DecimalMin(value = "0.00", message = "现金金额不能为负数")
    @Digits(integer = 10, fraction = 2, message = "现金金额格式不正确")
    @Schema(description = "现金金额（现金支付时使用）")
    private BigDecimal cashAmount;

    @Min(value = 0, message = "金币成本不能为负数")
    @Schema(description = "金币成本（金币支付时使用）")
    private Long coinCost;

    @Schema(description = "内容ID（内容类商品时使用）")
    private Long contentId;

    /**
     * 验证请求参数
     */
    public void validateParams() {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("用户ID无效");
        }
        
        if (goodsId == null || goodsId <= 0) {
            throw new IllegalArgumentException("商品ID无效");
        }
        
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("购买数量必须大于0");
        }
        
        if (discountAmount != null && discountAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("优惠金额不能为负数");
        }
        
        // 验证扩展字段
        if (goodsType != null && !goodsType.matches("^(coin|goods|subscription|content)$")) {
            throw new IllegalArgumentException("商品类型无效");
        }
        
        if (paymentMode != null && !paymentMode.matches("^(cash|coin)$")) {
            throw new IllegalArgumentException("支付模式无效");
        }
        
        // 验证支付金额的一致性
        if ("cash".equals(paymentMode)) {
            if (cashAmount == null || cashAmount.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("现金支付模式下，现金金额必须大于0");
            }
        } else if ("coin".equals(paymentMode)) {
            if (coinCost == null || coinCost <= 0) {
                throw new IllegalArgumentException("金币支付模式下，金币成本必须大于0");
            }
        }
        
        // 验证内容类商品的contentId
        if ("content".equals(goodsType) && contentId == null) {
            throw new IllegalArgumentException("内容类商品必须提供内容ID");
        }
    }

    /**
     * 获取有效的优惠金额
     */
    public BigDecimal getEffectiveDiscountAmount() {
        return discountAmount == null ? BigDecimal.ZERO : discountAmount;
    }
}