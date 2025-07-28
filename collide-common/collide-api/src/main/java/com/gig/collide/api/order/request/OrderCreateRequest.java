package com.gig.collide.api.order.request;

import com.gig.collide.base.request.BaseRequest;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

/**
 * 创建订单请求
 * 
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
public class OrderCreateRequest extends BaseRequest {

    /**
     * 用户ID
     */
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    /**
     * 订单类型: GOODS-商品购买, CONTENT-内容直购
     */
    @NotBlank(message = "订单类型不能为空")
    private String orderType;

    /**
     * 商品ID（商品购买时必填）
     */
    private Long goodsId;

    /**
     * 商品名称
     */
    private String goodsName;

    /**
     * 商品类型: COIN-金币类, SUBSCRIPTION-订阅类
     */
    private String goodsType;

    /**
     * 商品价格
     */
    @DecimalMin(value = "0.00", message = "商品价格不能小于0")
    private BigDecimal goodsPrice;

    /**
     * 内容ID（内容直购时必填）
     */
    private Long contentId;

    /**
     * 内容标题
     */
    private String contentTitle;

    /**
     * 内容类型: VIDEO-视频, ARTICLE-文章, LIVE-直播, COURSE-课程
     */
    private String contentType;

    /**
     * 内容价格
     */
    @DecimalMin(value = "0.00", message = "内容价格不能小于0")
    private BigDecimal contentPrice;

    /**
     * 购买数量
     */
    @NotNull(message = "购买数量不能为空")
    @Min(value = 1, message = "购买数量不能小于1")
    private Integer quantity;

    /**
     * 订单总金额
     */
    @NotNull(message = "订单总金额不能为空")
    @DecimalMin(value = "0.01", message = "订单总金额不能小于0.01")
    private BigDecimal totalAmount;

    /**
     * 支付方式: ALIPAY-支付宝, WECHAT-微信, TEST-测试支付
     */
    private String payType;

    /**
     * 订单过期时间（分钟）
     */
    private Integer expireMinutes;

    /**
     * 订单备注
     */
    private String remark;

    /**
     * 客户端IP地址
     */
    private String clientIp;

    /**
     * 设备信息
     */
    private String deviceInfo;
} 