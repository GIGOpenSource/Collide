package com.gig.collide.api.order.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

/**
 * 订单查询请求DTO
 * 用于订单列表查询的参数传递
 *
 * @author GIG Team
 * @version 2.0.0 (扩展版)
 * @since 2024-01-31
 */
@Data
@Accessors(chain = true)
@Schema(description = "订单查询请求")
public class OrderQueryRequest {

    @Min(value = 1, message = "当前页码必须大于0")
    @Schema(description = "当前页码", defaultValue = "1")
    private Integer currentPage = 1;

    @Min(value = 1, message = "页面大小必须大于0")
    @Schema(description = "页面大小", defaultValue = "20")
    private Integer pageSize = 20;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "商品ID")
    private Long goodsId;

    @Schema(description = "商家ID")
    private Long sellerId;

    @Pattern(regexp = "^(coin|goods|subscription|content)$", 
             message = "商品类型只能是: coin、goods、subscription、content")
    @Schema(description = "商品类型", allowableValues = {"coin", "goods", "subscription", "content"})
    private String goodsType;

    @Pattern(regexp = "^(pending|paid|shipped|completed|cancelled)$", 
             message = "订单状态只能是: pending、paid、shipped、completed、cancelled")
    @Schema(description = "订单状态", allowableValues = {"pending", "paid", "shipped", "completed", "cancelled"})
    private String status;

    @Pattern(regexp = "^(cash|coin)$", 
             message = "支付模式只能是: cash、coin")
    @Schema(description = "支付模式", allowableValues = {"cash", "coin"})
    private String paymentMode;

    @Pattern(regexp = "^(unpaid|paid|refunded)$", 
             message = "支付状态只能是: unpaid、paid、refunded")
    @Schema(description = "支付状态", allowableValues = {"unpaid", "paid", "refunded"})
    private String payStatus;

    @Pattern(regexp = "^(alipay|wechat|balance|coin)$", 
             message = "支付方式只能是: alipay、wechat、balance、coin")
    @Schema(description = "支付方式", allowableValues = {"alipay", "wechat", "balance", "coin"})
    private String payMethod;

    @Schema(description = "订单号")
    private String orderNo;

    @Schema(description = "搜索关键词（订单号、商品名称、用户昵称）")
    private String keyword;

    @Schema(description = "开始时间")
    private LocalDateTime startTime;

    @Schema(description = "结束时间")
    private LocalDateTime endTime;

    @Pattern(regexp = "^(create_time|pay_time|update_time|final_amount|coin_cost)$",
             message = "排序字段只能是: create_time、pay_time、update_time、final_amount、coin_cost")
    @Schema(description = "排序字段", 
            allowableValues = {"create_time", "pay_time", "update_time", "final_amount", "coin_cost"},
            defaultValue = "create_time")
    private String sortField = "create_time";

    @Pattern(regexp = "^(ASC|DESC)$", message = "排序方向只能是: ASC、DESC")
    @Schema(description = "排序方向", allowableValues = {"ASC", "DESC"}, defaultValue = "DESC")
    private String sortDirection = "DESC";

    @Schema(description = "是否只查询已支付订单", defaultValue = "false")
    private Boolean onlyPaid = false;

    @Schema(description = "是否只查询虚拟商品订单", defaultValue = "false")
    private Boolean onlyVirtual = false;

    @Schema(description = "是否只查询实体商品订单", defaultValue = "false")
    private Boolean onlyPhysical = false;

    @Schema(description = "是否包含已取消订单", defaultValue = "false")
    private Boolean includeCancelled = false;

    @Schema(description = "内容ID（查询内容类订单时使用）")
    private Long contentId;

    @Schema(description = "订阅类型（查询订阅类订单时使用）")
    private String subscriptionType;

    /**
     * 获取有效的订单状态列表
     */
    public String[] getEffectiveStatuses() {
        if (onlyPaid) {
            return new String[]{"paid", "shipped", "completed"};
        } else if (includeCancelled) {
            return new String[]{"pending", "paid", "shipped", "completed", "cancelled"};
        } else {
            return new String[]{"pending", "paid", "shipped", "completed"};
        }
    }

    /**
     * 获取有效的商品类型列表
     */
    public String[] getEffectiveGoodsTypes() {
        if (onlyVirtual) {
            return new String[]{"coin", "subscription", "content"};
        } else if (onlyPhysical) {
            return new String[]{"goods"};
        } else if (goodsType != null) {
            return new String[]{goodsType};
        } else {
            return new String[]{"coin", "goods", "subscription", "content"};
        }
    }

    /**
     * 获取有效的支付状态
     */
    public String getEffectivePayStatus() {
        if (onlyPaid) {
            return "paid";
        }
        return payStatus;
    }

    /**
     * 验证查询参数
     */
    public void validateParams() {
        if (onlyVirtual && onlyPhysical) {
            throw new IllegalArgumentException("不能同时只查询虚拟商品和实体商品");
        }
        
        if (startTime != null && endTime != null && startTime.isAfter(endTime)) {
            throw new IllegalArgumentException("开始时间不能晚于结束时间");
        }
        
        if (currentPage != null && currentPage <= 0) {
            throw new IllegalArgumentException("页码必须大于0");
        }
        
        if (pageSize != null && (pageSize <= 0 || pageSize > 100)) {
            throw new IllegalArgumentException("页面大小必须在1-100之间");
        }
    }

    /**
     * 是否有用户过滤条件
     */
    public boolean hasUserFilter() {
        return userId != null;
    }

    /**
     * 是否有时间过滤条件
     */
    public boolean hasTimeFilter() {
        return startTime != null || endTime != null;
    }

    /**
     * 是否有搜索条件
     */
    public boolean hasSearchFilter() {
        return keyword != null && !keyword.trim().isEmpty();
    }
}