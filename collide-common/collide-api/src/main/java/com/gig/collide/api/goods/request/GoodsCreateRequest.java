package com.gig.collide.api.goods.request;

import com.gig.collide.api.goods.constant.GoodsType;
import com.gig.collide.base.request.BaseRequest;
import lombok.*;
import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

/**
 * 商品创建请求
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "商品创建请求")
public class GoodsCreateRequest extends BaseRequest {

    /**
     * 商品名称
     */
    @Schema(description = "商品名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "商品名称不能为空")
    @Size(max = 100, message = "商品名称长度不能超过100个字符")
    private String name;

    /**
     * 商品描述
     */
    @Schema(description = "商品描述")
    @Size(max = 2000, message = "商品描述长度不能超过2000个字符")
    private String description;

    /**
     * 商品类型
     */
    @Schema(description = "商品类型", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "商品类型不能为空")
    private GoodsType type;

    /**
     * 商品价格（元）
     */
    @Schema(description = "商品价格（元）", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "商品价格不能为空")
    @DecimalMin(value = "0.01", message = "商品价格必须大于0")
    @Digits(integer = 8, fraction = 2, message = "价格格式不正确，最多8位整数2位小数")
    private BigDecimal price;

    /**
     * 商品图片URL
     */
    @Schema(description = "商品图片URL")
    @Size(max = 500, message = "图片URL长度不能超过500个字符")
    private String imageUrl;

    /**
     * 库存数量（-1表示无限库存）
     */
    @Schema(description = "库存数量（-1表示无限库存）")
    @Min(value = -1, message = "库存数量不能小于-1")
    private Integer stock = -1;

    /**
     * 订阅周期天数（订阅类商品必填）
     */
    @Schema(description = "订阅周期天数（订阅类商品必填）")
    @Min(value = 1, message = "订阅天数必须大于0")
    private Integer subscriptionDays;

    /**
     * 金币数量（金币类商品必填）
     */
    @Schema(description = "金币数量（金币类商品必填）")
    @Min(value = 1, message = "金币数量必须大于0")
    private Integer coinAmount;

    /**
     * 是否推荐
     */
    @Schema(description = "是否推荐")
    private Boolean recommended = false;

    /**
     * 是否热门
     */
    @Schema(description = "是否热门")
    private Boolean hot = false;

    /**
     * 创建者ID
     */
    @Schema(description = "创建者ID")
    private Long creatorId;

    // ===================== 便捷构造器 =====================

    /**
     * 创建金币类商品
     */
    public static GoodsCreateRequest coin(String name, BigDecimal price, Integer coinAmount) {
        GoodsCreateRequest request = new GoodsCreateRequest();
        request.setName(name);
        request.setType(GoodsType.COIN);
        request.setPrice(price);
        request.setCoinAmount(coinAmount);
        return request;
    }

    /**
     * 创建订阅类商品
     */
    public static GoodsCreateRequest subscription(String name, BigDecimal price, Integer subscriptionDays) {
        GoodsCreateRequest request = new GoodsCreateRequest();
        request.setName(name);
        request.setType(GoodsType.SUBSCRIPTION);
        request.setPrice(price);
        request.setSubscriptionDays(subscriptionDays);
        return request;
    }

    // ===================== 数据验证 =====================

    /**
     * 验证商品类型相关字段
     */
    public boolean isValid() {
        if (type == GoodsType.COIN && coinAmount == null) {
            return false;
        }
        if (type == GoodsType.SUBSCRIPTION && subscriptionDays == null) {
            return false;
        }
        return true;
    }
} 