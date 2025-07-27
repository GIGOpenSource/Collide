package com.gig.collide.api.tag.response.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 标签信息
 *
 * @author GIG Team
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TagInfo implements Serializable {

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
     * 标签描述
     */
    private String description;

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
     * 所属分类ID（仅存储ID，不做连表查询）
     */
    private Long categoryId;

    /**
     * 使用次数
     */
    private Long usageCount;

    /**
     * 热度分数
     */
    private BigDecimal heatScore;

    /**
     * 标签状态
     */
    private String status;

    /**
     * 用户兴趣分数（仅在用户兴趣标签查询时有值）
     */
    private BigDecimal interestScore;

    /**
     * 兴趣来源（仅在用户兴趣标签查询时有值）
     */
    private String interestSource;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
    
    /**
     * 设置标签ID（兼容性方法）
     */
    public void setTagId(Long tagId) {
        this.id = tagId;
    }
    
    /**
     * 获取标签ID（兼容性方法）
     */
    public Long getTagId() {
        return this.id;
    }
} 