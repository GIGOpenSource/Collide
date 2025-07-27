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
import com.gig.collide.users.domain.entity.UserUnified;
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
            UserUnified user = null;
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
        try {
            log.info("用户分页查询请求：{}", userPageQueryRequest);
            
            // 调用领域服务进行分页查询
            com.baomidou.mybatisplus.core.metadata.IPage<UserUnified> pageResult = userDomainService.pageQueryUsers(
                userPageQueryRequest.getPageNum(),
                userPageQueryRequest.getPageSize(),
                userPageQueryRequest.getUsernameKeyword(),
                userPageQueryRequest.getStatus(),
                userPageQueryRequest.getRole()
            );
            
            // 转换实体为DTO
            java.util.List<UserInfo> userInfoList = pageResult.getRecords().stream()
                .map(UserConvertor.INSTANCE::mapToVo)
                .collect(java.util.stream.Collectors.toList());
            
            // 构建分页响应
            PageResponse<UserInfo> response = PageResponse.of(
                userInfoList,
                pageResult.getTotal(),
                (int) pageResult.getCurrent(),
                (int) pageResult.getSize()
            );
            
            log.info("用户分页查询完成，总记录数：{}，当前页记录数：{}", 
                pageResult.getTotal(), userInfoList.size());
            
            return response;
            
        } catch (Exception e) {
            log.error("用户分页查询失败", e);
            return PageResponse.of(java.util.Collections.emptyList(), 0L, 1, 10);
        }
    }

    @Override
    public UserOperatorResponse register(UserRegisterRequest userRegisterRequest) {
        try {
            log.info("用户注册请求，用户名：{}", userRegisterRequest.getUsername());
            
            // 调用领域服务进行用户注册
            UserUnified user = userDomainService.registerUser(userRegisterRequest);
            
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
            UserUnified user = userDomainService.updateUserInfo(userModifyRequest.getUserId(), userModifyRequest);
            
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
        try {
            log.info("用户激活请求，用户ID：{}，激活码：{}", 
                userActiveRequest.getUserId(), userActiveRequest.getActivationCode());
            
            // 调用领域服务激活用户
            String resultMessage = userDomainService.activateUser(
                userActiveRequest.getUserId(), 
                userActiveRequest.getActivationCode()
            );
            
            // 构建成功响应
            UserOperatorResponse response = new UserOperatorResponse();
            response.setSuccess(true);
            response.setResponseMessage(resultMessage);
            
            log.info("用户激活成功，用户ID：{}", userActiveRequest.getUserId());
            return response;
            
        } catch (IllegalArgumentException e) {
            log.warn("用户激活参数错误，用户ID：{}，错误：{}", 
                userActiveRequest.getUserId(), e.getMessage());
            UserOperatorResponse response = new UserOperatorResponse();
            response.setSuccess(false);
            response.setResponseCode("ACTIVATION_ERROR");
            response.setResponseMessage(e.getMessage());
            return response;
        } catch (Exception e) {
            log.error("用户激活失败，用户ID：{}", userActiveRequest.getUserId(), e);
            UserOperatorResponse response = new UserOperatorResponse();
            response.setSuccess(false);
            response.setResponseCode("ACTIVATION_ERROR");
            response.setResponseMessage("用户激活失败：" + e.getMessage());
            return response;
        }
    }

    @Override
    public UserOperatorResponse updateStatus(UserStatusRequest userStatusRequest) {
        try {
            log.info("用户状态更新请求，用户ID：{}，启用状态：{}", 
                userStatusRequest.getUserId(), userStatusRequest.getActive());
            
            // 调用领域服务进行幂等性状态更新
            String resultMessage = userDomainService.updateUserStatusIdempotent(
                userStatusRequest.getUserId(), 
                userStatusRequest.getActive()
            );
            
            // 构建成功响应
            UserOperatorResponse response = new UserOperatorResponse();
            response.setSuccess(true);
            response.setResponseMessage(resultMessage);
            
            log.info("用户状态更新成功，用户ID：{}", userStatusRequest.getUserId());
            return response;
            
        } catch (Exception e) {
            log.error("用户状态更新失败，用户ID：{}", userStatusRequest.getUserId(), e);
            UserOperatorResponse response = new UserOperatorResponse();
            response.setSuccess(false);
            response.setResponseCode("USER_STATUS_UPDATE_ERROR");
            response.setResponseMessage("用户状态更新失败：" + e.getMessage());
            return response;
        }
    }

} 