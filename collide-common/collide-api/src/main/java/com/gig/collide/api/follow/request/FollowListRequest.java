package com.gig.collide.api.follow.request;

import com.gig.collide.api.follow.constant.FollowTypeEnum;
import com.gig.collide.base.request.BaseRequest;
import lombok.*;

/**
 * 关注列表查询请求
 * @author GIG
 */
@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class FollowListRequest extends BaseRequest {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 当前页
     */
    private Integer currentPage = 1;

    /**
     * 页面大小
     */
    private Integer pageSize = 10;

    /**
     * 关注类型（可选）
     */
    private FollowTypeEnum followType;

    public FollowListRequest(Long userId) {
        this.userId = userId;
    }

    public FollowListRequest(Long userId, Integer currentPage, Integer pageSize) {
        this.userId = userId;
        this.currentPage = currentPage;
        this.pageSize = pageSize;
    }
} 