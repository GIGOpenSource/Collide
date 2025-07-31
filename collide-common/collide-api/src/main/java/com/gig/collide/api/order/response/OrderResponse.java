package com.gig.collide.api.order.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单响应DTO
 * 统一的订单信息响应格式
 *
 * @author GIG Team
 * @version 2.0.0 (扩展版)
 * @since 2024-01-31
 */
@Data
@Accessors(chain = true)
@Schema(description = "订单响应信息")
public class OrderResponse {

    @Schema(description = "订单ID")
    private Long id;

    @Schema(description = "订单号")
    private String orderNo;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "用户昵称")
    private String userNickname;

    @Schema(description = "商品ID")
    private Long goodsId;

    @Schema(description = "商品名称")
    private String goodsName;

    @Schema(description = "商品类型", allowableValues = {"coin", "goods", "subscription", "content"})
    private String goodsType;

    @Schema(description = "商品类型说明")
    private String goodsTypeDesc;

    @Schema(description = "商品封面")
    private String goodsCover;

    @Schema(description = "商品分类名称")
    private String goodsCategoryName;

    @Schema(description = "金币数量（金币类商品）")
    private Long coinAmount;

    @Schema(description = "内容ID（内容类商品）")
    private Long contentId;

    @Schema(description = "内容标题（内容类商品）")
    private String contentTitle;

    @Schema(description = "订阅时长（天数，订阅类商品）")
    private Integer subscriptionDuration;

    @Schema(description = "订阅类型（订阅类商品）")
    private String subscriptionType;

    @Schema(description = "购买数量")
    private Integer quantity;

    @Schema(description = "支付模式", allowableValues = {"cash", "coin"})
    private String paymentMode;

    @Schema(description = "支付模式说明")
    private String paymentModeDesc;

    @Schema(description = "现金金额")
    private BigDecimal cashAmount;

    @Schema(description = "消耗金币数")
    private Long coinCost;

    @Schema(description = "订单总金额")
    private BigDecimal totalAmount;

    @Schema(description = "优惠金额")
    private BigDecimal discountAmount;

    @Schema(description = "实付金额")
    private BigDecimal finalAmount;

    @Schema(description = "有效金额（根据支付模式显示）")
    private Object effectiveAmount;

    @Schema(description = "金额单位", allowableValues = {"元", "金币"})
    private String amountUnit;

    @Schema(description = "订单状态", allowableValues = {"pending", "paid", "shipped", "completed", "cancelled"})
    private String status;

    @Schema(description = "订单状态说明")
    private String statusDesc;

    @Schema(description = "支付状态", allowableValues = {"unpaid", "paid", "refunded"})
    private String payStatus;

    @Schema(description = "支付状态说明")
    private String payStatusDesc;

    @Schema(description = "支付方式", allowableValues = {"alipay", "wechat", "balance", "coin"})
    private String payMethod;

    @Schema(description = "支付方式说明")
    private String payMethodDesc;

    @Schema(description = "支付时间")
    private LocalDateTime payTime;

    @Schema(description = "是否可以支付")
    private Boolean canPay;

    @Schema(description = "是否可以取消")
    private Boolean canCancel;

    @Schema(description = "是否可以退款")
    private Boolean canRefund;

    @Schema(description = "是否为虚拟商品")
    private Boolean isVirtualGoods;

    @Schema(description = "订单类型描述")
    private String orderTypeDesc;

    @Schema(description = "总价值描述")
    private String totalValue;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    /**
     * 获取商品类型描述
     */
    public String getGoodsTypeDesc() {
        if (goodsType == null) {
            return "未知";
        }
        switch (goodsType.toLowerCase()) {
            case "coin":
                return "金币充值包";
            case "goods":
                return "实体商品";
            case "subscription":
                return "订阅服务";
            case "content":
                return "付费内容";
            default:
                return "未知类型";
        }
    }

    /**
     * 获取支付模式描述
     */
    public String getPaymentModeDesc() {
        if (paymentMode == null) {
            return "未知";
        }
        switch (paymentMode.toLowerCase()) {
            case "cash":
                return "现金支付";
            case "coin":
                return "金币支付";
            default:
                return "未知模式";
        }
    }

    /**
     * 获取订单状态描述
     */
    public String getStatusDesc() {
        if (status == null) {
            return "未知";
        }
        switch (status.toLowerCase()) {
            case "pending":
                return "待支付";
            case "paid":
                return "已支付";
            case "shipped":
                return "已发货";
            case "completed":
                return "已完成";
            case "cancelled":
                return "已取消";
            default:
                return "未知状态";
        }
    }

    /**
     * 获取支付状态描述
     */
    public String getPayStatusDesc() {
        if (payStatus == null) {
            return "未知";
        }
        switch (payStatus.toLowerCase()) {
            case "unpaid":
                return "未支付";
            case "paid":
                return "已支付";
            case "refunded":
                return "已退款";
            default:
                return "未知状态";
        }
    }

    /**
     * 获取支付方式描述
     */
    public String getPayMethodDesc() {
        if (payMethod == null) {
            return "未选择";
        }
        switch (payMethod.toLowerCase()) {
            case "alipay":
                return "支付宝";
            case "wechat":
                return "微信支付";
            case "balance":
                return "余额支付";
            case "coin":
                return "金币支付";
            default:
                return "其他方式";
        }
    }

    /**
     * 获取有效金额
     */
    public Object getEffectiveAmount() {
        if ("coin".equals(paymentMode)) {
            return coinCost;
        }
        return finalAmount;
    }

    /**
     * 获取金额单位
     */
    public String getAmountUnit() {
        if ("coin".equals(paymentMode)) {
            return "金币";
        }
        return "元";
    }

    /**
     * 是否可以支付
     */
    public Boolean getCanPay() {
        return "pending".equals(status) && "unpaid".equals(payStatus);
    }

    /**
     * 是否可以取消
     */
    public Boolean getCanCancel() {
        return "pending".equals(status) || "paid".equals(status);
    }

    /**
     * 是否可以退款
     */
    public Boolean getCanRefund() {
        return "paid".equals(payStatus);
    }

    /**
     * 是否为虚拟商品
     */
    public Boolean getIsVirtualGoods() {
        if (goodsType == null) {
            return false;
        }
        return "coin".equals(goodsType) || "subscription".equals(goodsType) || "content".equals(goodsType);
    }

    /**
     * 获取订单类型描述
     */
    public String getOrderTypeDesc() {
        String typeDesc = getGoodsTypeDesc();
        String paymentDesc = getPaymentModeDesc();
        return typeDesc + "(" + paymentDesc + ")";
    }

    /**
     * 获取总价值描述
     */
    public String getTotalValue() {
        Object amount = getEffectiveAmount();
        String unit = getAmountUnit();
        return amount + unit;
    }
}