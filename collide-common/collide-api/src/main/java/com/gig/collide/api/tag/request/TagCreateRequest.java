package com.gig.collide.api.tag.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import java.io.Serializable;

/**
 * 创建标签请求
 *
 * @author GIG Team
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TagCreateRequest implements Serializable {

    private static final long serialVersionUID = 1L;

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
     * 标签类型：content-内容标签，interest-兴趣标签，system-系统标签
     */
    private String tagType;

    /**
     * 所属分类ID
     */
    private Long categoryId;

    /**
     * 排序值
     */
    private Integer sort;

    /**
     * 状态：0-禁用，1-启用
     */
    private Integer status;

    /**
     * 参数验证
     * 
     * @return 是否有效
     */
    public boolean isValid() {
        return StringUtils.hasText(name);
    }
    
    /**
     * 获取名称
     */
    public String getName() {
        return name;
    }
    
    /**
     * 获取描述
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * 获取颜色
     */
    public String getColor() {
        return color;
    }
    
    /**
     * 获取图标URL
     */
    public String getIconUrl() {
        return iconUrl;
    }
    
    /**
     * 获取标签类型
     */
    public String getTagType() {
        return tagType;
    }
    
    /**
     * 获取分类ID
     */
    public Long getCategoryId() {
        return categoryId;
    }
    
    /**
     * 获取排序值
     */
    public Integer getSort() {
        return sort;
    }
    
    /**
     * 获取状态
     */
    public Integer getStatus() {
        return status;
    }
} 