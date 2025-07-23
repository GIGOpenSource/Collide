package com.gig.collide.users.facade;

import com.gig.collide.api.user.request.*;
import com.gig.collide.api.user.request.condition.UserIdQueryCondition;
import com.gig.collide.api.user.request.condition.UserUserNameQueryCondition;
import com.gig.collide.api.user.request.condition.UserPhoneQueryCondition;
import com.gig.collide.api.user.response.UserOperatorResponse;
import com.gig.collide.api.user.response.UserQueryResponse;
import com.gig.collide.api.user.response.data.UserInfo;
import com.gig.collide.api.user.service.UserFacadeService;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.users.domain.entity.User;
import com.gig.collide.users.domain.entity.convertor.UserConvertor;
import com.gig.collide.users.domain.service.UserDomainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Component;

/**
 * 用户服务 Dubbo RPC 接口实现
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Slf4j
@Component
@DubboService(version = "1.0.0")
@RequiredArgsConstructor
public class UserFacadeServiceImpl implements UserFacadeService {

    private final UserDomainService userDomainService;

    @Override
    public UserQueryResponse<UserInfo> query(UserQueryRequest userQueryRequest) {
        try {
            log.info("查询用户信息，请求参数：{}", userQueryRequest);
            
            // 根据不同查询条件调用对应的查询方法
            User user = null;
            if (userQueryRequest.getUserIdQueryCondition() != null) {
                user = userDomainService.getUserById(userQueryRequest.getUserIdQueryCondition().getUserId());
            } else if (userQueryRequest.getUserUserNameQueryCondition() != null) {
                user = userDomainService.getUserByUsername(userQueryRequest.getUserUserNameQueryCondition().getUserName());
            } else if (userQueryRequest.getUserPhoneQueryCondition() != null) {
                user = userDomainService.getUserByPhone(userQueryRequest.getUserPhoneQueryCondition().getTelephone());
            } else {
                throw new IllegalArgumentException("查询条件不能为空");
            }

            UserQueryResponse<UserInfo> response = new UserQueryResponse<>();
            if (user != null) {
                response.setSuccess(true);
                UserInfo userInfo = UserConvertor.INSTANCE.mapToVo(user);
                response.setData(userInfo);
            } else {
                response.setSuccess(false);
                response.setResponseCode("USER_NOT_FOUND");
                response.setResponseMessage("用户不存在");
            }
            return response;

        } catch (Exception e) {
            log.error("查询用户信息失败", e);
            UserQueryResponse<UserInfo> response = new UserQueryResponse<>();
            response.setSuccess(false);
            response.setResponseCode("USER_QUERY_ERROR");
            response.setResponseMessage("查询用户信息失败: " + e.getMessage());
            return response;
        }
    }

    @Override
    public PageResponse<UserInfo> pageQuery(UserPageQueryRequest userPageQueryRequest) {
        log.warn("分页查询用户信息功能暂未实现");
        return PageResponse.of(java.util.Collections.emptyList(), 0, 10, 1);
    }

    @Override
    public UserOperatorResponse register(UserRegisterRequest userRegisterRequest) {
        try {
            log.info("用户注册请求，用户名：{}", userRegisterRequest.getUsername());
            
            // 调用领域服务进行用户注册
            User user = userDomainService.registerUser(userRegisterRequest);
            
            // 构建成功响应
            UserOperatorResponse response = new UserOperatorResponse();
            response.setSuccess(true);
            response.setResponseMessage("用户注册成功");
            
            // 转换用户信息
            UserInfo userInfo = UserConvertor.INSTANCE.mapToVo(user);
            response.setUser(userInfo);
            
            log.info("用户注册成功，用户ID：{}，用户名：{}", user.getId(), user.getUsername());
            return response;
            
        } catch (Exception e) {
            log.error("用户注册失败，用户名：{}", userRegisterRequest.getUsername(), e);
            UserOperatorResponse response = new UserOperatorResponse();
            response.setSuccess(false);
            response.setResponseCode("USER_REGISTER_ERROR");
            response.setResponseMessage("用户注册失败：" + e.getMessage());
            return response;
        }
    }

    @Override
    public UserOperatorResponse modify(UserModifyRequest userModifyRequest) {
        try {
            log.info("用户信息修改请求，用户ID：{}", userModifyRequest.getUserId());
            
            // 调用领域服务修改用户信息
            User user = userDomainService.updateUserInfo(userModifyRequest.getUserId(), userModifyRequest);
            
            // 构建成功响应
            UserOperatorResponse response = new UserOperatorResponse();
            response.setSuccess(true);
            response.setResponseMessage("用户信息修改成功");
            
            // 转换用户信息
            UserInfo userInfo = UserConvertor.INSTANCE.mapToVo(user);
            response.setUser(userInfo);
            
            log.info("用户信息修改成功，用户ID：{}", user.getId());
            return response;
            
        } catch (Exception e) {
            log.error("用户信息修改失败，用户ID：{}", userModifyRequest.getUserId(), e);
            UserOperatorResponse response = new UserOperatorResponse();
            response.setSuccess(false);
            response.setResponseCode("USER_MODIFY_ERROR");
            response.setResponseMessage("用户信息修改失败：" + e.getMessage());
            return response;
        }
    }

    @Override
    public UserOperatorResponse auth(UserAuthRequest userAuthRequest) {
        try {
            log.info("用户认证申请请求，用户ID：{}", userAuthRequest.getUserId());
            
            // 调用领域服务申请博主认证
            String resultMessage = userDomainService.applyForBlogger(userAuthRequest.getUserId());
            
            // 构建成功响应
            UserOperatorResponse response = new UserOperatorResponse();
            response.setSuccess(true);
            response.setResponseMessage(resultMessage);
            
            log.info("用户认证申请处理完成，用户ID：{}", userAuthRequest.getUserId());
            return response;
            
        } catch (Exception e) {
            log.error("用户认证申请失败，用户ID：{}", userAuthRequest.getUserId(), e);
            UserOperatorResponse response = new UserOperatorResponse();
            response.setSuccess(false);
            response.setResponseCode("USER_AUTH_ERROR");
            response.setResponseMessage("用户认证申请失败：" + e.getMessage());
            return response;
        }
    }

    @Override
    public UserOperatorResponse active(UserActiveRequest userActiveRequest) {
        log.warn("用户激活功能暂未实现");
        UserOperatorResponse response = new UserOperatorResponse();
        response.setSuccess(false);
        response.setResponseCode("NOT_IMPLEMENTED");
        response.setResponseMessage("该功能暂未实现");
        return response;
    }




} 