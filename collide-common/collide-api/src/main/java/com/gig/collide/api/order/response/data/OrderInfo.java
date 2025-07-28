package com.gig.collide.api.order.response.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 订单信息数据传输对象
 * 与 order_info 表结构完全对齐
 * 
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class OrderInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 订单号 - 业务唯一标识
     */
    private String orderNo;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 订单类型: GOODS-商品购买, CONTENT-内容直购
     */
    private String orderType;

    /**
     * 商品ID（商品购买时必填）
     */
    private Long goodsId;

    /**
     * 商品名称（冗余存储）
     */
    private String goodsName;

    /**
     * 商品类型（冗余存储）: COIN-金币类, SUBSCRIPTION-订阅类
     */
    private String goodsType;

    /**
     * 商品价格（冗余存储）
     */
    private BigDecimal goodsPrice;

    /**
     * 内容ID（内容直购时必填）
     */
    private Long contentId;

    /**
     * 内容标题（冗余存储）
     */
    private String contentTitle;

    /**
     * 内容类型（冗余存储）: VIDEO-视频, ARTICLE-文章, LIVE-直播, COURSE-课程
     */
    private String contentType;

    /**
     * 内容价格（冗余存储）
     */
    private BigDecimal contentPrice;

    /**
     * 购买数量
     */
    private Integer quantity;

    /**
     * 订单总金额
     */
    private BigDecimal totalAmount;

    /**
     * 订单状态: CREATE-创建订单, UNPAID-未付款, PAID-已支付, CANCELLED-已取消, REFUNDED-已退款
     */
    private String status;

    /**
     * 支付方式: ALIPAY-支付宝, WECHAT-微信, TEST-测试支付
     */
    private String payType;

    /**
     * 支付时间
     */
    private Long payTime;

    /**
     * 订单过期时间
     */
    private Long expireTime;

    /**
     * 订单备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private Long createTime;

    /**
     * 更新时间
     */
    private Long updateTime;

    /**
     * 逻辑删除标识: 0-未删除, 1-已删除
     */
    private Integer deleted;

    /**
     * 版本号（乐观锁）
     */
    private Integer version;
} 