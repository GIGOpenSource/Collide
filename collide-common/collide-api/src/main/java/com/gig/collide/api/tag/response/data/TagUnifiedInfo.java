package com.gig.collide.api.tag.response.data;

import com.gig.collide.api.tag.constant.TagTypeEnum;
import com.gig.collide.api.tag.constant.TagStatusEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 标签统一信息传输对象
 *
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class TagUnifiedInfo implements Serializable {

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
    private TagTypeEnum tagType;

    /**
     * 所属分类ID
     */
    private Long categoryId;

    /**
     * 分类名称
     */
    private String categoryName;

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
    private TagStatusEnum status;

    /**
     * 排序值
     */
    private Integer sort;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 版本号
     */
    private Integer version;

    /**
     * 关联用户数（兴趣标签）
     */
    private Long interestUserCount;

    /**
     * 关联内容数
     */
    private Long contentCount;

    /**
     * 检查标签是否可用
     */
    public boolean isAvailable() {
        return status != null && status.isAvailable();
    }

    /**
     * 检查是否为热门标签
     */
    public boolean isHot() {
        return heatScore != null && heatScore.compareTo(BigDecimal.valueOf(50)) > 0;
    }

    /**
     * 获取简短描述
     */
    public String getShortDescription() {
        if (description == null || description.length() <= 50) {
            return description;
        }
        return description.substring(0, 50) + "...";
    }
} 