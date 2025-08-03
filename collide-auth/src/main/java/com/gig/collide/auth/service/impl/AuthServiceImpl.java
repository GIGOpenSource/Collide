package com.gig.collide.auth.service.impl;

import cn.dev33.satoken.stp.SaLoginModel;
import cn.dev33.satoken.stp.StpUtil;
import com.gig.collide.api.user.UserFacadeService;
import com.gig.collide.api.user.request.UserCreateRequest;
import com.gig.collide.api.user.response.UserResponse;
import com.gig.collide.auth.param.LoginParam;
import com.gig.collide.auth.param.RegisterParam;
import com.gig.collide.auth.param.LoginOrRegisterParam;
import com.gig.collide.auth.service.AuthService;
import com.gig.collide.web.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 认证服务实现类
 * 
 * @author GIG Team
 * @version 2.0.0
 */
@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    @DubboReference(version = "1.0.0", timeout = 10000)
    private UserFacadeService userFacadeService;

    /**
     * 默认登录超时时间：7天
     */
    private static final Integer DEFAULT_LOGIN_SESSION_TIMEOUT = 60 * 60 * 24 * 7;

    @Override
    public Result<Object> register(RegisterParam registerParam) {
        log.info("用户注册请求，用户名：{}", registerParam.getUsername());
        
        // 检查用户是否已存在（使用高性能版本）
        Result<UserResponse> existingUser = userFacadeService.getUserByUsernameBasic(registerParam.getUsername());
        if (existingUser.getSuccess() && existingUser.getData() != null) {
            log.warn("用户名已存在：{}", registerParam.getUsername());
            return createErrorResult("USER_ALREADY_EXISTS", "用户名已存在");
        }
        
        // 构建注册请求
        UserCreateRequest userCreateRequest = new UserCreateRequest();
        userCreateRequest.setUsername(registerParam.getUsername());
        userCreateRequest.setPassword(registerParam.getPassword());
        userCreateRequest.setNickname(registerParam.getUsername());
        userCreateRequest.setRole("user");
        userCreateRequest.setInviteCode(registerParam.getInviteCode());

        // 执行注册
        Result<Void> registerResult = userFacadeService.createUser(userCreateRequest);
        
        if (!registerResult.getSuccess()) {
            log.error("用户注册失败：{}", registerResult.getMessage());
            return createErrorResult("USER_REGISTER_FAILED", registerResult.getMessage());
        }

        log.info("用户注册成功，用户名：{}", registerParam.getUsername());
        
        // 注册成功后自动登录
        return performAutoLogin(registerParam.getUsername(), registerParam.getPassword(), "注册成功");
    }

    @Override
    public Result<Object> login(LoginParam loginParam) {
        log.info("用户登录请求，用户名：{}", loginParam.getUsername());
        
        // 执行登录验证
        Result<UserResponse> loginResult = userFacadeService.login(loginParam.getUsername(), loginParam.getPassword());
        
        if (!loginResult.getSuccess() || loginResult.getData() == null) {
            log.warn("用户登录失败：{}", loginResult.getMessage());
            return createErrorResult("LOGIN_FAILED", loginResult.getMessage());
        }

        UserResponse userInfo = loginResult.getData();
        
        // 生成Token并设置Session
        String token = createUserSession(userInfo);
        
        log.info("用户登录成功，用户ID：{}，Token：{}", userInfo.getId(), token);
        
        Map<String, Object> response = new HashMap<>();
        response.put("user", userInfo);
        response.put("token", token);
        response.put("message", "登录成功");
        
        return createSuccessResult(response);
    }

    @Override
    public Result<Object> loginOrRegister(LoginOrRegisterParam loginParam) {
        log.info("登录或注册请求，用户名：{}", loginParam.getUsername());
        
        // 优化策略：直接尝试登录，失败后再判断是否需要注册
        Result<UserResponse> loginResult = userFacadeService.login(loginParam.getUsername(), loginParam.getPassword());
        
        if (loginResult.getSuccess() && loginResult.getData() != null) {
            // 登录成功
            UserResponse userInfo = loginResult.getData();
            String token = createUserSession(userInfo);
            
            log.info("用户登录成功，用户ID：{}", userInfo.getId());
            
            Map<String, Object> response = new HashMap<>();
            response.put("user", userInfo);
            response.put("token", token);
            response.put("message", "登录成功");
            response.put("isNewUser", false);
            
            return createSuccessResult(response);
        } else {
            // 登录失败，需要判断是用户不存在还是密码错误
            log.info("登录失败，检查失败原因：{}", loginResult.getMessage());
            
            // 检查用户是否存在
            Result<UserResponse> existingUser = userFacadeService.getUserByUsernameBasic(loginParam.getUsername());
            
            if (existingUser.getSuccess() && existingUser.getData() != null) {
                // 用户存在但登录失败，说明密码错误
                log.warn("用户{}存在但密码错误", loginParam.getUsername());
                return createErrorResult("PASSWORD_ERROR", "密码错误");
            } else {
                // 用户不存在，执行注册逻辑
                log.info("用户{}不存在，执行自动注册", loginParam.getUsername());
                return performAutoRegisterAndLogin(loginParam);
            }
        }
    }

    @Override
    public Result<String> logout() {
        Long userId = StpUtil.getLoginIdAsLong();
        StpUtil.logout();
        log.info("用户登出成功，用户ID：{}", userId);
        return createSuccessResult("登出成功");
    }

    @Override
    public Result<Object> validateInviteCode(String inviteCode) {
        if (!StringUtils.hasText(inviteCode)) {
            return createErrorResult("INVALID_INVITE_CODE", "邀请码不能为空");
        }
        
        log.info("验证邀请码：{}", inviteCode);
        
        // TODO: 调用用户服务的邀请码验证接口
        Map<String, Object> data = new HashMap<>();
        data.put("valid", true);
        data.put("inviter", Map.of(
            "id", 12345,
            "username", "inviter_user",
            "nickname", "邀请用户",
            "avatar", ""
        ));
        
        log.info("邀请码验证成功：{}", inviteCode);
        return createSuccessResult(data);
    }

    @Override
    public Result<Object> getMyInviteInfo() {
        Long userId = StpUtil.getLoginIdAsLong();
        log.info("获取用户邀请信息，用户ID：{}", userId);
        
        // 获取用户信息
        Result<UserResponse> userResult = userFacadeService.getUserById(userId);
        if (!userResult.getSuccess() || userResult.getData() == null) {
            return createErrorResult("USER_NOT_FOUND", "用户信息获取失败");
        }

        UserResponse user = userResult.getData();
        
        Map<String, Object> data = new HashMap<>();
        data.put("inviteCode", user.getInviteCode());
        data.put("totalInvitedCount", user.getInvitedCount() != null ? user.getInvitedCount() : 0);
        data.put("invitedUsers", java.util.List.of());
        
        return createSuccessResult(data);
    }

    @Override
    public Result<Object> getCurrentUser() {
        Long userId = StpUtil.getLoginIdAsLong();
        log.info("获取当前用户信息，用户ID：{}", userId);
        
        // 获取用户信息
        Result<UserResponse> userResult = userFacadeService.getUserById(userId);
        if (!userResult.getSuccess() || userResult.getData() == null) {
            return createErrorResult("USER_NOT_FOUND", "用户信息获取失败");
        }

        return createSuccessResult(userResult.getData());
    }

    @Override
    public Result<Object> verifyToken() {
        Long userId = StpUtil.getLoginIdAsLong();
        String token = StpUtil.getTokenValue();
        
        log.info("Token验证成功，用户ID：{}", userId);
        
        Map<String, Object> data = new HashMap<>();
        data.put("valid", true);
        data.put("userId", userId);
        data.put("token", token);
        data.put("message", "Token有效");
        
        return createSuccessResult(data);
    }

    // =================== 私有辅助方法 ===================

    /**
     * 执行自动登录
     */
    private Result<Object> performAutoLogin(String username, String password, String message) {
        Result<UserResponse> loginResult = userFacadeService.login(username, password);
        
        if (loginResult.getSuccess() && loginResult.getData() != null) {
            UserResponse userInfo = loginResult.getData();
            String token = createUserSession(userInfo);
            
            log.info("用户自动登录成功，用户ID：{}", userInfo.getId());
            
            // 简化返回，只包含token
            Map<String, Object> data = new HashMap<>();
            data.put("token", token);
            
            return createSuccessResult(data, message);
        } else {
            // 注册成功但自动登录失败，仍然返回成功
            log.warn("注册成功但自动登录失败，用户名：{}", username);
            return createSuccessResult(null, message);
        }
    }

    /**
     * 执行自动注册并登录
     */
    private Result<Object> performAutoRegisterAndLogin(LoginOrRegisterParam loginParam) {
        log.info("用户{}不存在，执行自动注册", loginParam.getUsername());
        
        UserCreateRequest userCreateRequest = new UserCreateRequest();
        userCreateRequest.setUsername(loginParam.getUsername());
        userCreateRequest.setPassword(loginParam.getPassword());
        userCreateRequest.setNickname(loginParam.getUsername());
        userCreateRequest.setRole("user");
        userCreateRequest.setInviteCode(loginParam.getInviteCode());
        
        Result<Void> registerResult = userFacadeService.createUser(userCreateRequest);
        
        if (!registerResult.getSuccess()) {
            log.error("用户自动注册失败：{} - {}", registerResult.getCode(), registerResult.getMessage());
            
            // 检查是否是因为用户已存在导致的注册失败（更全面的判断条件）
            String errorMessage = registerResult.getMessage();
            String errorCode = registerResult.getCode();
            
            boolean isDuplicateUser = false;
            if (errorMessage != null) {
                isDuplicateUser = errorMessage.contains("已存在") || 
                                errorMessage.contains("duplicate") || 
                                errorMessage.contains("Duplicate entry") ||
                                errorMessage.contains("uk_username");
            }
            if (errorCode != null) {
                isDuplicateUser = isDuplicateUser || errorCode.equals("USER_ALREADY_EXISTS");
            }
            
            if (isDuplicateUser) {
                log.info("检测到用户{}已存在（可能是并发注册），尝试登录", loginParam.getUsername());
                
                // 用户已存在，尝试登录 - 使用增强的登录逻辑
                UserResponse userInfo = null;
                
                // 方式1：尝试登录验证
                Result<UserResponse> loginResult = userFacadeService.login(loginParam.getUsername(), loginParam.getPassword());
                
                if (loginResult.getSuccess() && loginResult.getData() != null) {
                    userInfo = loginResult.getData();
                    log.info("并发情况下登录验证成功，用户ID：{}", userInfo.getId());
                } else {
                    log.warn("并发情况下登录验证失败：{}，尝试获取用户信息验证密码", loginResult.getMessage());
                    
                    // 方式2：获取用户信息，确认用户存在且验证密码
                    Result<UserResponse> getUserResult = userFacadeService.getUserByUsernameBasic(loginParam.getUsername());
                    
                    if (getUserResult.getSuccess() && getUserResult.getData() != null) {
                        // 用户存在，再次尝试登录
                        try {
                            Thread.sleep(50); // 短暂等待
                            Result<UserResponse> retryLoginResult = userFacadeService.login(loginParam.getUsername(), loginParam.getPassword());
                            
                            if (retryLoginResult.getSuccess() && retryLoginResult.getData() != null) {
                                userInfo = retryLoginResult.getData();
                                log.info("并发情况下重试登录成功，用户ID：{}", userInfo.getId());
                            } else {
                                log.warn("并发情况下重试登录失败，可能是密码错误：{}", retryLoginResult.getMessage());
                                return createErrorResult("PASSWORD_ERROR", "密码错误");
                            }
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            log.error("并发登录等待被中断", e);
                            return createErrorResult("PASSWORD_ERROR", "密码错误");
                        }
                    } else {
                        log.error("并发情况下无法获取用户信息：{}", getUserResult.getMessage());
                        return createErrorResult("PASSWORD_ERROR", "密码错误");
                    }
                }
                
                if (userInfo != null) {
                    String token = createUserSession(userInfo);
                    log.info("并发情况下用户{}最终登录成功，用户ID：{}", loginParam.getUsername(), userInfo.getId());
                    
                    Map<String, Object> response = new HashMap<>();
                    response.put("user", userInfo);
                    response.put("token", token);
                    response.put("message", "登录成功");
                    response.put("isNewUser", false);
                    
                    return createSuccessResult(response);
                } else {
                    log.warn("并发情况下所有登录尝试都失败了");
                    return createErrorResult("PASSWORD_ERROR", "密码错误");
                }
            }
            
            // 其他注册失败原因
            log.error("注册失败，非重复用户错误：{}", registerResult.getMessage());
            return createErrorResult("AUTO_REGISTER_FAILED", registerResult.getMessage());
        }

        log.info("用户自动注册成功，用户名：{}", loginParam.getUsername());
        
        // 注册成功后自动登录 - 使用重试机制和多种方式
        UserResponse userInfo = null;
        String token = null;
        
        // 方式1：尝试登录验证
        Result<UserResponse> autoLoginResult = userFacadeService.login(loginParam.getUsername(), loginParam.getPassword());
        
        if (autoLoginResult.getSuccess() && autoLoginResult.getData() != null) {
            userInfo = autoLoginResult.getData();
            log.info("注册后登录验证成功，用户ID：{}", userInfo.getId());
        } else {
            log.warn("注册后登录验证失败：{}，尝试直接获取用户信息", autoLoginResult.getMessage());
            
            // 方式2：直接通过用户名获取用户信息（注册刚成功，用户肯定存在）
            Result<UserResponse> getUserResult = userFacadeService.getUserByUsernameBasic(loginParam.getUsername());
            
            if (getUserResult.getSuccess() && getUserResult.getData() != null) {
                userInfo = getUserResult.getData();
                log.info("通过用户名获取信息成功，用户ID：{}", userInfo.getId());
            } else {
                log.error("注册成功但获取用户信息失败：{}", getUserResult.getMessage());
                
                // 方式3：短暂等待后重试登录（处理数据库事务延迟）
                try {
                    Thread.sleep(100); // 等待100ms
                    Result<UserResponse> retryLoginResult = userFacadeService.login(loginParam.getUsername(), loginParam.getPassword());
                    
                    if (retryLoginResult.getSuccess() && retryLoginResult.getData() != null) {
                        userInfo = retryLoginResult.getData();
                        log.info("重试登录成功，用户ID：{}", userInfo.getId());
                    } else {
                        log.error("重试登录仍然失败：{}", retryLoginResult.getMessage());
                        return createErrorResult("AUTO_LOGIN_FAILED", "注册成功但登录失败，请手动登录");
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    log.error("等待重试时被中断", e);
                    return createErrorResult("AUTO_LOGIN_FAILED", "注册成功但登录失败，请手动登录");
                }
            }
        }
        
        // 创建用户会话
        if (userInfo != null) {
            token = createUserSession(userInfo);
            log.info("用户自动注册并登录成功，用户ID：{}", userInfo.getId());
            
            Map<String, Object> response = new HashMap<>();
            response.put("user", userInfo);
            response.put("token", token);
            response.put("message", "注册并登录成功");
            response.put("isNewUser", true);
            
            return createSuccessResult(response);
        } else {
            log.error("所有自动登录方式都失败了");
            return createErrorResult("AUTO_LOGIN_FAILED", "注册成功但登录失败，请手动登录");
        }
    }

    /**
     * 创建用户会话并返回Token
     */
    private String createUserSession(UserResponse userInfo) {
        SaLoginModel loginModel = new SaLoginModel()
                .setDevice("web")
                .setTimeout(DEFAULT_LOGIN_SESSION_TIMEOUT)
                .setToken(null);
        StpUtil.login(userInfo.getId(), loginModel);
        
        // 存储用户信息到Session供网关鉴权使用
        StpUtil.getSession().set("userInfo", java.util.Map.of(
            "id", userInfo.getId(),
            "username", userInfo.getUsername(),
            "role", userInfo.getRole() != null ? userInfo.getRole() : "user",
            "status", userInfo.getStatus() != null ? userInfo.getStatus() : "active"
        ));
        
        return StpUtil.getTokenValue();
    }

    /**
     * 创建错误响应
     */
    private <T> Result<T> createErrorResult(String responseCode, String responseMessage) {
        Result<T> result = new Result<>();
        result.setSuccess(false);
        result.setCode(responseCode);
        result.setMessage(responseMessage);
        result.setData(null);
        return result;
    }

    /**
     * 创建成功响应
     */
    private <T> Result<T> createSuccessResult(T data) {
        Result<T> result = new Result<>();
        result.setSuccess(true);
        result.setCode("SUCCESS");
        result.setMessage("操作成功");
        result.setData(data);
        return result;
    }

    /**
     * 创建成功响应（自定义消息）
     */
    private <T> Result<T> createSuccessResult(T data, String message) {
        Result<T> result = new Result<>();
        result.setSuccess(true);
        result.setCode("SUCCESS");
        result.setMessage(message);
        result.setData(data);
        return result;
    }
}