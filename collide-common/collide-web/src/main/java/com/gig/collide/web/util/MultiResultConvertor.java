package com.gig.collide.web.util;

import com.gig.collide.base.response.PageResponse;
import com.gig.collide.web.vo.MultiResult;

import static com.gig.collide.base.response.ResponseCode.SUCCESS;

/**
 * @author GIGTeam
 */
public class MultiResultConvertor {

    public static <T> MultiResult<T> convert(PageResponse<T> pageResponse) {
        MultiResult<T> multiResult = new MultiResult<T>(true, SUCCESS.name(), SUCCESS.name(), pageResponse.getDatas(), pageResponse.getTotal(), pageResponse.getCurrentPage(), pageResponse.getPageSize());
        return multiResult;
    }
}

