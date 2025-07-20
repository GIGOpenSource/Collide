package com.gig.collide.api.follow.request;

import com.gig.collide.base.request.BaseRequest;
import lombok.*;

/**
 * 粉丝统计查询请求
 * @author GIG
 */
@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class FollowerStatisticsRequest extends BaseRequest {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private Long userId;
} 