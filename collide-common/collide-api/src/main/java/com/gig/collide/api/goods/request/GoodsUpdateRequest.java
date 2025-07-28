package com.gig.collide.api.goods.request;

import com.gig.collide.api.goods.constant.GoodsStatus;
import com.gig.collide.api.goods.constant.GoodsType;
import com.gig.collide.base.request.BaseRequest;
import lombok.*;
import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

/**
 * 商品更新请求
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
@Schema(description = "商品更新请求")
public class GoodsUpdateRequest extends BaseRequest {

    /**
     * 商品ID
     */
    @Schema(description = "商品ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "商品ID不能为空")
    private Long id;

    /**
     * 商品名称
     */
    @Schema(description = "商品名称")
    @Size(max = 100, message = "商品名称长度不能超过100个字符")
    private String name;

    /**
     * 商品描述
     */
    @Schema(description = "商品描述")
    @Size(max = 2000, message = "商品描述长度不能超过2000个字符")
    private String description;

    /**
     * 商品状态
     */
    @Schema(description = "商品状态")
    private GoodsStatus status;

    /**
     * 商品价格（元）
     */
    @Schema(description = "商品价格（元）")
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
    private Integer stock;

    /**
     * 订阅周期天数（订阅类商品使用）
     */
    @Schema(description = "订阅周期天数（订阅类商品使用）")
    @Min(value = 1, message = "订阅天数必须大于0")
    private Integer subscriptionDays;

    /**
     * 金币数量（金币类商品使用）
     */
    @Schema(description = "金币数量（金币类商品使用）")
    @Min(value = 1, message = "金币数量必须大于0")
    private Integer coinAmount;

    /**
     * 是否推荐
     */
    @Schema(description = "是否推荐")
    private Boolean recommended;

    /**
     * 是否热门
     */
    @Schema(description = "是否热门")
    private Boolean hot;

    /**
     * 版本号（乐观锁）
     */
    @Schema(description = "版本号（乐观锁）", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "版本号不能为空")
    private Integer version;

    // ===================== 便捷构造器 =====================

    /**
     * 基本更新构造器
     */
    public static GoodsUpdateRequest of(Long id, Integer version) {
        GoodsUpdateRequest request = new GoodsUpdateRequest();
        request.setId(id);
        request.setVersion(version);
        return request;
    }

    /**
     * 更新状态
     */
    public static GoodsUpdateRequest updateStatus(Long id, Integer version, GoodsStatus status) {
        GoodsUpdateRequest request = of(id, version);
        request.setStatus(status);
        return request;
    }

    /**
     * 更新价格
     */
    public static GoodsUpdateRequest updatePrice(Long id, Integer version, BigDecimal price) {
        GoodsUpdateRequest request = of(id, version);
        request.setPrice(price);
        return request;
    }

    /**
     * 更新库存
     */
    public static GoodsUpdateRequest updateStock(Long id, Integer version, Integer stock) {
        GoodsUpdateRequest request = of(id, version);
        request.setStock(stock);
        return request;
    }

    /**
     * 设置推荐
     */
    public GoodsUpdateRequest recommended(Boolean recommended) {
        this.recommended = recommended;
        return this;
    }

    /**
     * 设置热门
     */
    public GoodsUpdateRequest hot(Boolean hot) {
        this.hot = hot;
        return this;
    }
} 