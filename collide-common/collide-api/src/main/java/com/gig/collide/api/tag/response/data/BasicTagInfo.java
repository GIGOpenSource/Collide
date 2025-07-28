package com.gig.collide.api.tag.response.data;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 基础标签信息传输对象
 * 用于减少数据传输量的轻量级标签信息
 *
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class BasicTagInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 标签ID
     */
    private Long id;

    /**
     * 标签名称
     */
    private String name;

    /**
     * 标签颜色
     */
    private String color;

    /**
     * 标签图标URL
     */
    private String iconUrl;

    /**
     * 标签类型
     */
    private String tagType;

    /**
     * 使用次数
     */
    private Long usageCount;

    /**
     * 热度分数
     */
    private BigDecimal heatScore;

    /**
     * 状态
     */
    private String status;
} 