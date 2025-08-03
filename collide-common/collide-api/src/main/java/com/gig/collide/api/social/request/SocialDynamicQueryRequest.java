package com.gig.collide.api.social.request;

import com.gig.collide.base.request.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 社交动态查询请求 - 简洁版
 * 
 * @author GIG Team
 * @version 2.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SocialDynamicQueryRequest extends PageRequest implements Serializable {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 动态类型：text、image、video、share
     */
    private String dynamicType;

    /**
     * 分享目标类型：content、goods
     */
    private String shareTargetType;

    /**
     * 状态：normal、hidden、deleted
     */
    private String status;

    /**
     * 关键词搜索（内容中搜索）
     */
    private String keyword;

    /**
     * 最小点赞数
     */
    private Long minLikeCount;

    /**
     * 排序方式：create_time、like_count、comment_count、share_count
     */
    private String sortBy = "create_time";

    /**
     * 排序方向：asc、desc
     */
    private String sortDirection = "desc";
} 