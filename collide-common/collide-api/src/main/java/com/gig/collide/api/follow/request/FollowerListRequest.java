package com.gig.collide.api.follow.request;

import com.gig.collide.base.request.BaseRequest;
import lombok.*;

/**
 * 粉丝列表查询请求
 * @author GIG
 */
@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class FollowerListRequest extends BaseRequest {

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

    public FollowerListRequest(Long userId) {
        this.userId = userId;
    }

}