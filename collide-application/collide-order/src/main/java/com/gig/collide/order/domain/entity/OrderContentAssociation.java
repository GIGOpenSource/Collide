package com.gig.collide.order.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 订单内容关联实体
 * 用于管理订单购买后用户对特定内容的访问权限
 * 
 * @author Collide Team
 * @since 2024-01-01
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("order_content_association")
public class OrderContentAssociation {
    
    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;
    
    /**
     * 订单ID
     */
    @TableField("order_id")
    private Long orderId;
    
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
     * 内容ID（关联 collide-content 的内容）
     */
    @TableField("content_id")
    private Long contentId;
    
    /**
     * 内容类型
     * VIDEO - 视频内容
     * ARTICLE - 文章内容  
     * LIVE - 直播内容
     * COURSE - 课程内容
     */
    @TableField("content_type")
    private String contentType;
    
    /**
     * 内容标题（冗余存储，便于查询展示）
     */
    @TableField("content_title")
    private String contentTitle;
    
    /**
     * 访问权限类型
     * PERMANENT - 永久访问
     * TEMPORARY - 临时访问  
     * SUBSCRIPTION_BASED - 基于订阅
     */
    @TableField("access_type")
    private String accessType;
    
    /**
     * 权限开始时间
     */
    @TableField("access_start_time")
    private LocalDateTime accessStartTime;
    
    /**
     * 权限结束时间（null表示永久）
     */
    @TableField("access_end_time")
    private LocalDateTime accessEndTime;
    
    /**
     * 权限状态
     * ACTIVE - 激活状态，可以访问
     * EXPIRED - 已过期
     * REVOKED - 已撤销
     */
    @TableField("status")
    private String status;
    
    /**
     * 购买时的商品ID
     */
    @TableField("goods_id")
    private Long goodsId;
    
    /**
     * 购买时的商品类型
     */
    @TableField("goods_type")
    private String goodsType;
    
    /**
     * 消费的金币数量（如果是金币购买）
     */
    @TableField("consumed_coins")
    private Integer consumedCoins;
    
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
     * 检查权限是否有效
     */
    public boolean isAccessValid() {
        if (!"ACTIVE".equals(this.status)) {
            return false;
        }
        
        LocalDateTime now = LocalDateTime.now();
        
        // 检查开始时间
        if (this.accessStartTime != null && now.isBefore(this.accessStartTime)) {
            return false;
        }
        
        // 检查结束时间
        if (this.accessEndTime != null && now.isAfter(this.accessEndTime)) {
            return false;
        }
        
        return true;
    }
    
    /**
     * 是否为永久访问权限
     */
    public boolean isPermanentAccess() {
        return "PERMANENT".equals(this.accessType) || this.accessEndTime == null;
    }
    
    /**
     * 获取剩余访问天数
     */
    public Long getRemainingDays() {
        if (isPermanentAccess()) {
            return -1L; // -1表示永久
        }
        
        if (this.accessEndTime == null) {
            return -1L;
        }
        
        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(this.accessEndTime)) {
            return 0L; // 已过期
        }
        
        return java.time.Duration.between(now, this.accessEndTime).toDays();
    }
} 