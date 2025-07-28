package com.gig.collide.api.order.response.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * 订单内容关联信息数据传输对象
 * 与 order_content_association 表结构完全对齐
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
public class OrderContentAssociationInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 关联ID - 主键
     */
    private Long id;

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 订单号（冗余存储）
     */
    private String orderNo;

    /**
     * 用户ID（冗余存储）
     */
    private Long userId;

    /**
     * 内容ID（关联 collide-content）
     */
    private Long contentId;

    /**
     * 内容类型: VIDEO-视频, ARTICLE-文章, LIVE-直播, COURSE-课程
     */
    private String contentType;

    /**
     * 内容标题（冗余存储）
     */
    private String contentTitle;

    /**
     * 访问权限类型: PERMANENT-永久访问, TEMPORARY-临时访问, SUBSCRIPTION_BASED-基于订阅
     */
    private String accessType;

    /**
     * 权限开始时间
     */
    private Long accessStartTime;

    /**
     * 权限结束时间（null表示永久）
     */
    private Long accessEndTime;

    /**
     * 权限状态: ACTIVE-激活状态, EXPIRED-已过期, REVOKED-已撤销
     */
    private String status;

    /**
     * 购买时的商品ID（冗余存储）
     */
    private Long goodsId;

    /**
     * 购买时的商品类型（冗余存储）
     */
    private String goodsType;

    /**
     * 消费的金币数量（如果是金币购买）
     */
    private Integer consumedCoins;

    /**
     * 备注
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