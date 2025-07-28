package com.gig.collide.users.facade;

import com.gig.collide.api.user.response.UserOperatorResponse;
import com.gig.collide.users.domain.service.UserDomainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Component;

/**
 * 用户管理服务 Dubbo RPC 接口实现
 * 主要负责管理员级别的用户操作
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Slf4j
@Component
@DubboService(version = "1.0.0")
@RequiredArgsConstructor
public class UserManageFacadeServiceImpl implements UserManageFacadeService {

    private final UserDomainService userDomainService;

    @Override
    public UserOperatorResponse registerAdmin(UserRegisterRequest userRegisterRequest) {
        try {
            log.info("管理员注册用户，请求参数：{}", userRegisterRequest);
            
            // 调用领域服务进行管理员注册
            UserOperatorResponse response = userDomainService.registerAdmin(
                userRegisterRequest.getPhone(), 
                userRegisterRequest.getPassword(),
                userRegisterRequest.getUsername(),
                userRegisterRequest.getEmail()
            );
            
            log.info("管理员注册用户成功，用户ID：{}", response.getData());
            return response;
            
        } catch (Exception e) {
            log.error("管理员注册用户失败：{}", e.getMessage(), e);
            return UserOperatorResponse.fail("注册失败：" + e.getMessage());
        }
    }

    @Override
    public UserOperatorResponse freeze(Long userId) {
        try {
            log.info("冻结用户，用户ID：{}", userId);
            
            UserOperatorResponse response = userDomainService.freezeUser(userId);
            
            log.info("冻结用户成功，用户ID：{}", userId);
            return response;
            
        } catch (Exception e) {
            log.error("冻结用户失败，用户ID：{}，错误：{}", userId, e.getMessage(), e);
            return UserOperatorResponse.fail("冻结失败：" + e.getMessage());
        }
    }

    @Override
    public UserOperatorResponse unfreeze(Long userId) {
        try {
            log.info("解冻用户，用户ID：{}", userId);
            
            UserOperatorResponse response = userDomainService.unfreezeUser(userId);
            
            log.info("解冻用户成功，用户ID：{}", userId);
            return response;
            
        } catch (Exception e) {
            log.error("解冻用户失败，用户ID：{}，错误：{}", userId, e.getMessage(), e);
            return UserOperatorResponse.fail("解冻失败：" + e.getMessage());
        }
    }
} 