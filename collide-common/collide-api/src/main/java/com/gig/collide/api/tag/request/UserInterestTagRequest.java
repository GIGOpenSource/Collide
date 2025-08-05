package com.gig.collide.api.tag.request;

import lombok.Data;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 用户兴趣标签请求
 *
 * @author GIG Team
 * @version 3.0.0
 */
@Data
public class UserInterestTagRequest {

    /**
     * 用户ID
     */
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    /**
     * 标签ID
     */
    @NotNull(message = "标签ID不能为空")
    private Long tagId;

    /**
     * 兴趣分数（0-100）
     */
    @DecimalMin(value = "0.00", message = "兴趣分数不能小于0")
    @DecimalMax(value = "100.00", message = "兴趣分数不能大于100")
    private BigDecimal interestScore;

    /**
     * 操作人ID
     */
    @NotNull(message = "操作人ID不能为空")
    private Long operatorId;
}