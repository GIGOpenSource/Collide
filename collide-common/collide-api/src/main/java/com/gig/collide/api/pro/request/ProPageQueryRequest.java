package com.gig.collide.api.pro.request;

import com.gig.collide.base.request.BaseRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Pro用户分页查询请求
 * @author GIG
 */
@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ProPageQueryRequest extends BaseRequest {

    /**
     * 当前页
     */
    private int currentPage = 1;

    /**
     * 页面大小
     */
    private int pageSize = 10;

    /**
     * 付费状态过滤
     */
    private String proStatus;

    /**
     * 套餐类型过滤
     */
    private String packageType;
} 