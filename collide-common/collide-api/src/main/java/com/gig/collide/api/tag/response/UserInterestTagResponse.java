package com.gig.collide.api.tag.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户兴趣标签响应
 *
 * @author GIG Team
 * @version 3.0.0
 */
@Data
public class UserInterestTagResponse {

    /**
     * 记录ID
     */
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 标签ID
     */
    private Long tagId;

    /**
     * 标签名称
     */
    private String tagName;

    /**
     * 标签类型
     */
    private String tagType;

    /**
     * 兴趣分数（0-100）
     */
    private BigDecimal interestScore;

    /**
     * 状态：active、inactive
     */
    private String status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}