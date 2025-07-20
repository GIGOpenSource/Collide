package com.gig.collide.api.collection.request;

import com.gig.collide.base.request.PageRequest;
import lombok.Getter;
import lombok.Setter;

/**
 * @author GIG
 */
@Getter
@Setter
public class CollectionPageQueryRequest extends PageRequest {

    private String state;

    private String keyword;

}