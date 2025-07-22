package com.gig.collide.box.domain.request;

import com.gig.collide.base.request.BaseRequest;
import lombok.*;

/**
 * @author GIG
 * @date 2024/01/17
 */

@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class BlindBoxBindMatchRequest extends BaseRequest {

    /**
     * '盲盒id'
     */
    private Long blindBoxId;


}
