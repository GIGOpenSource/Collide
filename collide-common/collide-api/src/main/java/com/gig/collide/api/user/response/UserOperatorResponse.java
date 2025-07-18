package com.gig.collide.api.user.response;

import com.gig.collide.api.user.response.data.UserInfo;
import com.gig.collide.base.response.BaseResponse;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class UserOperatorResponse extends BaseResponse {

    private UserInfo user;
}
