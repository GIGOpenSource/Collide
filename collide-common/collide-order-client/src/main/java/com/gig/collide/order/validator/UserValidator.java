package com.gig.collide.order.validator;

import com.gig.collide.api.order.request.OrderCreateRequest;
import com.gig.collide.api.user.constant.UserRole;
import com.gig.collide.api.user.constant.UserStateEnum;
import com.gig.collide.api.user.request.UserQueryRequest;
import com.gig.collide.api.user.response.UserQueryResponse;
import com.gig.collide.api.user.response.data.UserInfo;
import com.gig.collide.api.user.service.UserFacadeService;
import com.gig.collide.order.OrderException;

import static com.gig.collide.api.order.constant.OrderErrorCode.*;

/**
 * 用户校验器
 *
 * @author GIG
 */
public class UserValidator extends BaseOrderCreateValidator {

    private UserFacadeService userFacadeService;

    @Override
    public void doValidate(OrderCreateRequest request) throws OrderException {
        String buyerId = request.getBuyerId();
        UserQueryRequest userQueryRequest = new UserQueryRequest(Long.valueOf(buyerId));
        UserQueryResponse<UserInfo> userQueryResponse = userFacadeService.query(userQueryRequest);
        if (userQueryResponse.getSuccess() && userQueryResponse.getData() != null) {
            UserInfo userInfo = userQueryResponse.getData();
            if (userInfo.getUserRole() != null && !userInfo.getUserRole().equals(UserRole.CUSTOMER)) {
                throw new OrderException(BUYER_IS_PLATFORM_USER);
            }
            //判断买家状态
            if (userInfo.getState() != null && !userInfo.getState().equals(UserStateEnum.ACTIVE.name())) {
                throw new OrderException(BUYER_STATUS_ABNORMAL);
            }
        }
    }

    public UserValidator(UserFacadeService userFacadeService) {
        this.userFacadeService = userFacadeService;
    }

    public UserValidator() {
    }
}
