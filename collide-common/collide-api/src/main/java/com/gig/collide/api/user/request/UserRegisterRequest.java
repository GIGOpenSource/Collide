package com.gig.collide.api.user.request;

import com.gig.collide.base.request.BaseRequest;
import lombok.*;

/**
 * @author GIG
 */
@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UserRegisterRequest extends BaseRequest {

    private String telephone;

    private String inviteCode;

    private String password;

}
