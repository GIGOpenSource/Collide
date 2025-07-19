package com.gig.collide.users.facade;

import com.gig.collide.api.user.request.*;
import com.gig.collide.api.user.request.condition.*;
import com.gig.collide.api.user.response.UserOperatorResponse;
import com.gig.collide.api.user.response.UserQueryResponse;
import com.gig.collide.api.user.response.data.UserInfo;
import com.gig.collide.api.user.service.UserFacadeService;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.rpc.facade.Facade;
import com.gig.collide.users.domain.entity.User;
import com.gig.collide.users.domain.entity.convertor.UserConvertor;
import com.gig.collide.users.domain.service.UserService;
import com.gig.collide.users.infrastructure.exception.UserException;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author GIGOpenTeam
 */
@DubboService(version = "1.0.0")
public class UserFacadeServiceImpl implements UserFacadeService {

    @Autowired
    private UserService userService;

    @Override
    public UserQueryResponse<UserInfo> query(UserQueryRequest userQueryRequest) {
        //使用switch表达式精简代码，如果这里编译不过，参考我的文档调整IDEA的JDK版本
        //文档地址：https://thoughts.aliyun.com/workspaces/6655879cf459b7001ba42f1b/docs/6673f26c5e11940001c810fb#667971268a5c151234adcf92
        User user = switch (userQueryRequest.getUserQueryCondition()) {
            case UserIdQueryCondition userIdQueryCondition:
                yield userService.findById(userIdQueryCondition.getUserId());
            case UserPhoneQueryCondition userPhoneQueryCondition:
                yield userService.findByTelephone(userPhoneQueryCondition.getTelephone());
            case UserPhoneAndPasswordQueryCondition userPhoneAndPasswordQueryCondition:
                yield userService.findByTelephoneAndPass(userPhoneAndPasswordQueryCondition.getTelephone(), userPhoneAndPasswordQueryCondition.getPassword());
            default:
                throw new UnsupportedOperationException(userQueryRequest.getUserQueryCondition() + "'' is not supported");
        };

        UserQueryResponse<UserInfo> response = new UserQueryResponse();
        response.setSuccess(true);
        UserInfo userInfo = UserConvertor.INSTANCE.mapToVo(user);
        response.setData(userInfo);
        return response;
    }

    @Override
    public UserQueryResponse<UserInfo> query(UserUserNameQueryRequest userUserNameQueryRequest) {
        //使用switch表达式精简代码，如果这里编译不过，参考我的文档调整IDEA的JDK版本
        //文档地址：https://thoughts.aliyun.com/workspaces/6655879cf459b7001ba42f1b/docs/6673f26c5e11940001c810fb#667971268a5c151234adcf92
        User user = switch (userUserNameQueryRequest.getUserUserNameQueryCondition()) {
            case UserIdQueryCondition userIdQueryCondition:
                yield userService.findById(userIdQueryCondition.getUserId());
            case UserUserNameQueryCondition userUserNameQueryCondition:
                yield userService.findByUserName(userUserNameQueryCondition.getUserName());
            case UserUserNameAndPasswordQueryCondition userUserNameAndPasswordQueryCondition:
                yield userService.findByTelephoneAndPass(userUserNameAndPasswordQueryCondition.getUserName(), userUserNameAndPasswordQueryCondition.getPassword());
            default:
                throw new UnsupportedOperationException(userUserNameQueryRequest.getUserUserNameQueryCondition() + "'' is not supported");
        };

        UserQueryResponse<UserInfo> response = new UserQueryResponse();
        response.setSuccess(true);
        UserInfo userInfo = UserConvertor.INSTANCE.mapToVo(user);
        response.setData(userInfo);
        return response;
    }

    @Override
    public PageResponse<UserInfo> pageQuery(UserPageQueryRequest userPageQueryRequest) {
        var queryResult = userService.pageQueryByState(userPageQueryRequest.getKeyWord(), userPageQueryRequest.getState(), userPageQueryRequest.getCurrentPage(), userPageQueryRequest.getPageSize());
        PageResponse<UserInfo> response = new PageResponse<>();
        if (!queryResult.getSuccess()) {
            response.setSuccess(false);
            return response;
        }
        response.setSuccess(true);
        response.setDatas(UserConvertor.INSTANCE.mapToVo(queryResult.getDatas()));
        response.setCurrentPage(queryResult.getCurrentPage());
        response.setPageSize(queryResult.getPageSize());
        return response;
    }

    @Override
    @Facade
    public UserOperatorResponse register(UserRegisterRequest userRegisterRequest) {
        try {
            return userService.register(
                    userRegisterRequest.getTelephone(),
                    userRegisterRequest.getInviteCode());
        }catch (UserException e){
            UserOperatorResponse response = new UserOperatorResponse();
            response.setSuccess(false);
            response.setResponseCode(e.getErrorCode().getCode());
            response.setResponseMessage(e.getErrorCode().getMessage());
            return response;
        }
    }

    @Override
    @Facade
    public UserOperatorResponse userNameRegister(UserUserNameRegisterRequest userUserNameRegisterRequest) {
        try {
            return userService.userNameRegister(
                    userUserNameRegisterRequest.getUserName(),
                    userUserNameRegisterRequest.getPassword(),
                    userUserNameRegisterRequest.getInviteCode());
        } catch (UserException e) {
            // 手动处理UserException，返回失败的响应
            UserOperatorResponse response = new UserOperatorResponse();
            response.setSuccess(false);
            response.setResponseCode(e.getErrorCode().getCode());
            response.setResponseMessage(e.getErrorCode().getMessage());
            return response;
        }
    }

    @Override
    @Facade
    public UserOperatorResponse modify(UserModifyRequest userModifyRequest) {
        try {
            return userService.modify(userModifyRequest);
        }catch (UserException e){
            // 手动处理UserException，返回失败的响应
            UserOperatorResponse response = new UserOperatorResponse();
            response.setSuccess(false);
            response.setResponseCode(e.getErrorCode().getCode());
            response.setResponseMessage(e.getErrorCode().getMessage());
            return response;
        }
    }

    @Override
    @Facade
    public UserOperatorResponse auth(UserAuthRequest userAuthRequest) {
        try {
            return userService.auth(userAuthRequest);
        }catch (UserException e){
            // 手动处理UserException，返回失败的响应
            UserOperatorResponse response = new UserOperatorResponse();
            response.setSuccess(false);
            response.setResponseCode(e.getErrorCode().getCode());
            response.setResponseMessage(e.getErrorCode().getMessage());
            return response;
        }
    }

    @Override
    @Facade
    public UserOperatorResponse active(UserActiveRequest userActiveRequest) {
        try {
            return userService.active(userActiveRequest);
        }catch (UserException e){
            // 手动处理UserException，返回失败的响应
            UserOperatorResponse response = new UserOperatorResponse();
            response.setSuccess(false);
            response.setResponseCode(e.getErrorCode().getCode());
            response.setResponseMessage(e.getErrorCode().getMessage());
            return response;
        }
    }
}
