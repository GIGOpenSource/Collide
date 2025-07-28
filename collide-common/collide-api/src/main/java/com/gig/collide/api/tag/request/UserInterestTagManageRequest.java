package com.gig.collide.api.tag.request;

import com.gig.collide.base.request.BaseRequest;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


import java.math.BigDecimal;
import java.util.List;

/**
 * 用户兴趣标签管理请求
 *
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class UserInterestTagManageRequest extends BaseRequest {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    @NotNull(message = "用户ID不能为空")
    @Positive(message = "用户ID必须为正数")
    private Long userId;

    /**
     * 标签ID
     */
    @NotNull(message = "标签ID不能为空")
    @Positive(message = "标签ID必须为正数")
    private Long tagId;

    /**
     * 兴趣分数（0-100）
     */
    @DecimalMin(value = "0.00", message = "兴趣分数不能小于0")
    @DecimalMax(value = "100.00", message = "兴趣分数不能大于100")
    private BigDecimal interestScore = BigDecimal.ZERO;

    /**
     * 兴趣来源（manual、behavior、system、algorithm）
     */
    @NotBlank(message = "兴趣来源不能为空")
    private String source = "manual";

    /**
     * 状态（active、inactive）
     */
    private String status = "active";

    /**
     * 操作类型（add、update、remove）
     */
    @NotBlank(message = "操作类型不能为空")
    private String operation;

    /**
     * 批量标签ID（用于批量操作）
     */
    private List<Long> tagIds;

    /**
     * 创建添加兴趣标签请求
     */
    public static UserInterestTagManageRequest addInterest(Long userId, Long tagId, BigDecimal score) {
        UserInterestTagManageRequest request = new UserInterestTagManageRequest();
        request.setUserId(userId);
        request.setTagId(tagId);
        request.setInterestScore(score);
        request.setOperation("add");
        return request;
    }

    /**
     * 创建移除兴趣标签请求
     */
    public static UserInterestTagManageRequest removeInterest(Long userId, Long tagId) {
        UserInterestTagManageRequest request = new UserInterestTagManageRequest();
        request.setUserId(userId);
        request.setTagId(tagId);
        request.setOperation("remove");
        return request;
    }

    /**
     * 创建批量添加兴趣标签请求
     */
    public static UserInterestTagManageRequest batchAddInterests(Long userId, List<Long> tagIds, BigDecimal score) {
        UserInterestTagManageRequest request = new UserInterestTagManageRequest();
        request.setUserId(userId);
        request.setTagIds(tagIds);
        request.setInterestScore(score);
        request.setOperation("batch_add");
        return request;
    }
} 