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
    }

    /**
     * 获取有效的优惠金额
     */
    public BigDecimal getEffectiveDiscountAmount() {
        return discountAmount == null ? BigDecimal.ZERO : discountAmount;
    }
}