package com.gig.collide.api.social.request;

import com.gig.collide.api.social.request.condition.SocialQueryCondition;
import com.gig.collide.base.request.BaseRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * 社交动态查询请求
 * @author GIG
 * @version 1.0.0
 * @since 2024-01-01
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class SocialPostQueryRequest extends BaseRequest {
    
    /**
     * 查询条件
     */
    private SocialQueryCondition condition;
    
    /**
     * 页码（从1开始）
     */
    @NotNull(message = "页码不能为空")
    @Min(value = 1, message = "页码必须大于0")
    private Integer pageNum = 1;
    
    /**
     * 每页数量
     */
    @NotNull(message = "每页数量不能为空")
    @Min(value = 1, message = "每页数量必须大于0")
    @Max(value = 100, message = "每页数量不能超过100")
    private Integer pageSize = 20;
    
    /**
     * 排序字段
     */
    private String sortField = "published_time";
    
    /**
     * 排序方向（asc-升序，desc-降序）
     */
    private String sortDirection = "desc";
    
    /**
     * 当前查看用户ID（用于权限控制）
     */
    private Long viewerUserId;
    
    /**
     * 构造简单查询请求
     */
    public SocialPostQueryRequest(SocialQueryCondition condition) {
        this.condition = condition;
    }
    
    /**
     * 构造带分页的查询请求
     */
    public SocialPostQueryRequest(SocialQueryCondition condition, Integer pageNum, Integer pageSize) {
        this.condition = condition;
        this.pageNum = pageNum;
        this.pageSize = pageSize;
    }
    
    /**
     * 构造带查看用户的查询请求
     */
    public static SocialPostQueryRequest withViewer(SocialQueryCondition condition, Long viewerUserId) {
        SocialPostQueryRequest request = new SocialPostQueryRequest(condition);
        request.setViewerUserId(viewerUserId);
        return request;
    }
    
    /**
     * 构造热度排序查询请求
     */
    public static SocialPostQueryRequest byHotScore(SocialQueryCondition condition) {
        SocialPostQueryRequest request = new SocialPostQueryRequest(condition);
        request.setSortField("hot_score");
        request.setSortDirection("desc");
        return request;
    }
    
    /**
     * 构造时间排序查询请求
     */
    public static SocialPostQueryRequest byTime(SocialQueryCondition condition) {
        SocialPostQueryRequest request = new SocialPostQueryRequest(condition);
        request.setSortField("published_time");
        request.setSortDirection("desc");
        return request;
    }
} 