package com.gig.collide.api.content.request;

import com.gig.collide.api.content.constant.ContentStatus;
import com.gig.collide.api.content.constant.ContentType;
import com.gig.collide.api.content.constant.QueryType;
import com.gig.collide.base.request.PageRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * 内容查询请求
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Getter
@Setter
@NoArgsConstructor
public class ContentQueryRequest extends PageRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 内容ID（查询单个内容时使用）
     */
    private Long contentId;

    /**
     * 查询类型
     */
    private QueryType queryType;

    /**
     * 内容类型筛选
     */
    private ContentType contentType;

    /**
     * 作者用户ID
     */
    private Long authorId;

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 内容状态筛选
     */
    private ContentStatus status;

    /**
     * 标签筛选
     */
    private List<String> tags;

    /**
     * 关键词搜索
     */
    private String keyword;

    /**
     * 查询用户ID（用于获取用户相关的推荐内容等）
     */
    private Long userId;

    /**
     * 是否包含内容详细数据
     */
    private Boolean viewContent = false;

    /**
     * 热门内容的时间范围（天数）
     */
    private Integer hotDays;
} 