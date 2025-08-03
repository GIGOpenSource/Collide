package com.gig.collide.api.goods.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 商品响应DTO
 * 统一的商品信息响应格式
 *
 * @author GIG Team
 * @version 2.0.0 (扩展版)
 * @since 2024-01-31
 */
@Data
@Accessors(chain = true)
@Schema(description = "商品响应信息")
public class GoodsResponse {

    @Schema(description = "商品ID")
    private Long id;

    @Schema(description = "商品名称")
    private String name;

    @Schema(description = "商品描述")
    private String description;

    @Schema(description = "分类ID")
    private Long categoryId;

    @Schema(description = "分类名称")
    private String categoryName;

    @Schema(description = "商品类型", allowableValues = {"coin", "goods", "subscription", "content"})
    private String goodsType;

    @Schema(description = "商品类型说明")
    private String goodsTypeDesc;

    @Schema(description = "现金价格")
    private BigDecimal price;

    @Schema(description = "原价")
    private BigDecimal originalPrice;

    @Schema(description = "金币价格（内容类型专用）")
    private Long coinPrice;

    @Schema(description = "金币数量（金币类商品专用）")
    private Long coinAmount;

    @Schema(description = "有效价格（根据类型显示现金或金币价格）")
    private Object effectivePrice;

    @Schema(description = "价格单位", allowableValues = {"元", "金币"})
    private String priceUnit;

    @Schema(description = "关联内容ID")
    private Long contentId;

    @Schema(description = "内容标题")
    private String contentTitle;

    @Schema(description = "订阅时长（天数）")
    private Integer subscriptionDuration;

    @Schema(description = "订阅类型")
    private String subscriptionType;

    @Schema(description = "库存数量（-1表示无限库存）")
    private Integer stock;

    @Schema(description = "库存状态", allowableValues = {"充足", "紧张", "无限库存"})
    private String stockStatus;

    @Schema(description = "商品封面图")
    private String coverUrl;

    @Schema(description = "商品图片列表")
    private List<String> images;

    @Schema(description = "商家ID")
    private Long sellerId;

    @Schema(description = "商家名称")
    private String sellerName;

    @Schema(description = "商品状态", allowableValues = {"active", "inactive", "sold_out"})
    private String status;

    @Schema(description = "商品状态说明")
    private String statusDesc;

    @Schema(description = "销量")
    private Long salesCount;

    @Schema(description = "浏览量")
    private Long viewCount;

    @Schema(description = "是否为虚拟商品")
    private Boolean isVirtual;

    @Schema(description = "是否可购买")
    private Boolean canPurchase;

    @Schema(description = "支付方式", allowableValues = {"现金", "金币"})
    private String paymentMethod;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    /**
     * 获取库存状态描述
     */
    public String getStockStatus() {
        if (stock == null) {
            return "未知";
        }
        if (stock == -1) {
            return "无限库存";
        }
        if (stock <= 0) {
            return "缺货";
        }
        if (stock <= 10) {
            return "紧张";
        }
        return "充足";
    }

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
     * 获取状态描述
     */
    public String getStatusDesc() {
        if (status == null) {
            return "未知";
        }
        switch (status.toLowerCase()) {
            case "active":
                return "正常销售";
            case "inactive":
                return "已下架";
            case "sold_out":
                return "售罄";
            default:
                return "未知状态";
        }
    }

    /**
     * 获取支付方式
     */
    public String getPaymentMethod() {
        if ("content".equals(goodsType)) {
            return "金币";
        }
        return "现金";
    }

    /**
     * 获取价格单位
     */
    public String getPriceUnit() {
        if ("content".equals(goodsType)) {
            return "金币";
        }
        return "元";
    }

    /**
     * 获取有效价格
     */
    public Object getEffectivePrice() {
        if ("content".equals(goodsType)) {
            return coinPrice;
        }
        return price;
    }

    /**
     * 是否为虚拟商品
     */
    public Boolean getIsVirtual() {
        if (goodsType == null) {
            return false;
        }
        return "coin".equals(goodsType) || "subscription".equals(goodsType) || "content".equals(goodsType);
    }

    /**
     * 是否可购买
     */
    public Boolean getCanPurchase() {
        // 检查状态
        if (!"active".equals(status)) {
            return false;
        }
        
        // 检查库存
        if (stock != null && stock == 0) {
            return false;
        }
        
        return true;
    }
}