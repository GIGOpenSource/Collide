package com.gig.collide.api.search.request.condition;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * 搜索查询条件基类
 * 
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
@Getter
@Setter
@NoArgsConstructor
public abstract class SearchQueryCondition implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 获取查询条件类型
     * 
     * @return 查询条件类型
     */
    public abstract String getConditionType();

    /**
     * 检查查询条件是否有效
     * 
     * @return 是否有效
     */
    public abstract boolean isValid();
} 