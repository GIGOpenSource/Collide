package com.gig.collide.api.box.request;

import com.gig.collide.base.request.PageRequest;
import lombok.Getter;
import lombok.Setter;

/**
 * @author GIG
 */
@Getter
@Setter
public class BlindBoxPageQueryRequest extends PageRequest {

    private String state;

    private String keyword;
}
