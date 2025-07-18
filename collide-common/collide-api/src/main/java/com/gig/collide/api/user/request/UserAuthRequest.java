package com.gig.collide.api.user.request;

import com.gig.collide.base.request.BaseRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UserAuthRequest extends BaseRequest {

    private Long userId;
    private String realName;
    private String idCard;

}

