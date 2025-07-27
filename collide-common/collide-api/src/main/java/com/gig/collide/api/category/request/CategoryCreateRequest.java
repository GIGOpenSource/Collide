package com.gig.collide.api.category.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import java.io.Serializable;

/**
 * 创建分类请求
 *
 * @author GIG Team
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryCreateRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 父分类ID，0表示根分类
     */
    private Long parentId;

    /**
     * 分类名称
     */
    private String name;

    /**
     * 分类描述
     */
    private String description;

    /**
     * 分类图标URL
     */
    private String iconUrl;

    /**
     * 分类封面URL
     */
    private String coverUrl;

    /**
     * 排序顺序
     */
    private Integer sortOrder;

    /**
     * 排序值（兼容性字段）
     */
    private Integer sort;

    /**
     * 状态：0-禁用，1-启用
     */
    private Integer status;

    /**
     * 创建者ID（去连表化字段）
     */
    private Long creatorId;

    /**
     * 创建者名称（冗余存储，避免连表查询）
     */
    private String creatorName;

    /**
     * 参数验证
     * 
     * @return 是否有效
     */
    public boolean isValid() {
        return StringUtils.hasText(name);
    }

    /**
     * 获取排序值（优先使用sort，然后是sortOrder）
     */
    public Integer getSort() {
        return sort != null ? sort : sortOrder;
    }
    
    /**
     * 获取状态
     */
    public Integer getStatus() {
        return status;
    }
    
    /**
     * 获取创建者ID
     */
    public Long getCreatorId() {
        return creatorId;
    }
    
    /**
     * 获取创建者名称
     */
    public String getCreatorName() {
        return creatorName;
    }
} 