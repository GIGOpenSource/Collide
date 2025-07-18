package com.gig.collide.users.facade;

import com.gig.collide.api.user.request.UserRegisterRequest;
import com.gig.collide.api.user.response.UserOperatorResponse;
import com.gig.collide.api.user.service.UserManageFacadeService;
import com.gig.collide.rpc.facade.Facade;
import com.gig.collide.users.domain.service.UserService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author GIGOpenTeam
 */
@DubboService(version = "1.0.0")
public class UserManageFacadeServiceImpl implements UserManageFacadeService {

    @Autowired
    private UserService userService;

    @Override
    @Facade
    public UserOperatorResponse registerAdmin(UserRegisterRequest userRegisterRequest) {
        return userService.registerAdmin(userRegisterRequest.getTelephone(), userRegisterRequest.getPassword());
    }

    @Override
    public UserOperatorResponse freeze(Long userId) {
        return userService.freeze(userId);
    }

    @Override
    public UserOperatorResponse unfreeze(Long userId) {
        return userService.unfreeze(userId);
    }
}
