package com.gig.collide.api.collection.request;

import com.gig.collide.base.request.PageRequest;
import lombok.Getter;
import lombok.Setter;

/**
 * 空投记录分页查询参数
 *
 * @author GIG
 */
@Getter
@Setter
public class AirDropPageQueryRequest extends PageRequest {

    private String collectionId;

    private String userId;
}
