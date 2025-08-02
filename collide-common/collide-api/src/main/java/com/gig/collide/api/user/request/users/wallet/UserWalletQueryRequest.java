package com.gig.collide.api.user.request.users.wallet;

import java.io.Serializable;
import java.math.BigDecimal;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 用户钱包查询请求
 * 
 * @author GIG Team
 * @version 2.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserWalletQueryRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 钱包状态：active、frozen
     */
    private String status;

    /**
     * 现金余额最小�?
     */
    private BigDecimal balanceMin;

    /**
     * 现金余额最大�?
     */
    private BigDecimal balanceMax;

    /**
     * 金币余额最小�?
     */
    private Long coinBalanceMin;

    /**
     * 金币余额最大�?
     */
    private Long coinBalanceMax;

    /**
     * 排序字段：balance、coin_balance、total_income、total_expense、create_time
     */
    private String sortField = "create_time";

    /**
     * 排序方向：asc、desc
     */
    private String sortDirection = "desc";

    /**
     * 当前页码
     */
    private Integer currentPage = 1;

    /**
     * 页面大小
     */
    private Integer pageSize = 20;
}
