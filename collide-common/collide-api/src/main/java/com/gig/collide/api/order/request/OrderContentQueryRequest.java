package com.gig.collide.api.order.request;

import com.gig.collide.base.request.BaseRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * 订单内容关联查询请求
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
public class OrderContentQueryRequest extends BaseRequest {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 内容ID
     */
    private Long contentId;

    /**
     * 内容类型: VIDEO-视频, ARTICLE-文章, LIVE-直播, COURSE-课程
     */
    private String contentType;

    /**
     * 访问权限类型: PERMANENT-永久访问, TEMPORARY-临时访问, SUBSCRIPTION_BASED-基于订阅
     */
    private String accessType;

    /**
     * 权限状态: ACTIVE-激活状态, EXPIRED-已过期, REVOKED-已撤销
     */
    private String status;

    /**
     * 商品ID
     */
    private Long goodsId;

    /**
     * 商品类型
     */
    private String goodsType;

    /**
     * 查询开始时间
     */
    private Long startTime;

    /**
     * 查询结束时间
     */
    private Long endTime;

    /**
     * 分页页码
     */
    private Integer pageNo = 1;

    /**
     * 分页大小
     */
    private Integer pageSize = 10;

    /**
     * 排序字段
     */
    private String sortField = "createTime";

    /**
     * 排序方向: ASC-升序, DESC-降序
     */
    private String sortOrder = "DESC";
} 