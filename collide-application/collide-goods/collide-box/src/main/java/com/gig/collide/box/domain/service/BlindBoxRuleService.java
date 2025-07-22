package com.gig.collide.box.domain.service;

import com.gig.collide.box.domain.request.BlindBoxBindMatchRequest;

/**
 * 盲盒分配服务
 *
 * @author Hollis
 */
public interface BlindBoxRuleService {
    /**
     * 按照规则进行匹配
     * @param request
     * @return 匹配到的盲盒条目id
     */
    Long match(BlindBoxBindMatchRequest request);
}
