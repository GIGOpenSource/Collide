package com.gig.collide.api.order.request;

import com.gig.collide.base.request.BaseRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 即将过期内容权限查询请求
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
public class OrderContentExpiringRequest extends BaseRequest {

    /**
     * 用户ID（可选，不填则查询所有用户）
     */
    private Long userId;

    /**
     * 提前预警天数（默认7天）
     */
    private Integer alertDays = 7;

    /**
     * 内容类型过滤
     */
    private String contentType;

    /**
     * 商品类型过滤
     */
    private String goodsType;

    /**
     * 分页页码
     */
    private Integer pageNo = 1;

    /**
     * 分页大小
     */
    private Integer pageSize = 10;
} 