package com.gig.collide.order.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单实体类
 * 
 * @author Collide Team
 * @since 2024-01-01
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("order_info")
public class OrderInfo {
    
    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;
    
    /**
     * 订单号
     */
    @TableField("order_no")
    private String orderNo;
    
    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;
    
    /**
     * 商品ID
     */
    @TableField("goods_id")
    private Long goodsId;
    
    /**
     * 商品名称
     */
    @TableField("goods_name")
    private String goodsName;
    
    /**
     * 商品类型
     */
    @TableField("goods_type")
    private String goodsType;
    
    /**
     * 商品价格
     */
    @TableField("goods_price")
    private BigDecimal goodsPrice;
    
    /**
     * 购买数量
     */
    @TableField("quantity")
    private Integer quantity;
    
    /**
     * 订单总金额
     */
    @TableField("total_amount")
    private BigDecimal totalAmount;
    
    /**
     * 订单状态：CREATE-创建订单，UNPAID-未付款，PAID-已支付
     */
    @TableField("status")
    private String status;
    
    /**
     * 支付方式
     */
    @TableField("pay_type")
    private String payType;
    
    /**
     * 支付时间
     */
    @TableField("pay_time")
    private LocalDateTime payTime;
    
    /**
     * 过期时间
     */
    @TableField("expire_time")
    private LocalDateTime expireTime;
    
    /**
     * 备注
     */
    @TableField("remark")
    private String remark;
    
    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    /**
     * 逻辑删除标识
     */
    @TableLogic
    @TableField("deleted")
    private Boolean deleted;
    
    /**
     * 版本号（乐观锁）
     */
    @Version
    @TableField("version")
    private Integer version;
    
    /**
     * 业务方法 - 是否已支付
     */
    public boolean isPaid() {
        return "PAID".equals(this.status);
    }
    
    /**
     * 业务方法 - 是否已过期
     */
    public boolean isExpired() {
        return expireTime != null && LocalDateTime.now().isAfter(expireTime);
    }
    
    /**
     * 业务方法 - 是否可以支付
     */
    public boolean canPay() {
        return ("CREATE".equals(this.status) || "UNPAID".equals(this.status)) && !isExpired();
    }
} 