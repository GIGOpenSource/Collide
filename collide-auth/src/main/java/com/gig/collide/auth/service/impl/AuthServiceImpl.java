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
        return loginOrRegisterWithRetry(loginParam, 0);
    }

    /**
     * 登录或注册（带重试机制）
     */
    private Result<Object> loginOrRegisterWithRetry(LoginOrRegisterParam loginParam, int retryCount) {
        final int MAX_RETRY = 2; // 最大重试次数，避免无限递归

        log.info("登录或注册请求，用户名：{}，重试次数：{}", loginParam.getUsername(), retryCount);

        if (retryCount > MAX_RETRY) {
            log.error("登录或注册重试次数超限，用户名：{}", loginParam.getUsername());
            return createErrorResult("MAX_RETRY_EXCEEDED", "操作失败，请稍后重试");
        }

        // 第一步：检查用户是否存在（复用register的逻辑）
        Result<UserResponse> existingUser = userFacadeService.getUserByUsernameBasic(loginParam.getUsername());

        if (existingUser.getSuccess() && existingUser.getData() != null) {
            // 用户存在，执行登录逻辑（复用login的逻辑）
            log.info("用户{}已存在，执行登录", loginParam.getUsername());

            Result<UserResponse> loginResult = userFacadeService.login(loginParam.getUsername(), loginParam.getPassword());

            if (!loginResult.getSuccess() || loginResult.getData() == null) {
                log.warn("用户登录失败：{}", loginResult.getMessage());
                return createErrorResult("PASSWORD_ERROR", "密码错误");
            }

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
            // 用户不存在，执行注册逻辑（复用register的逻辑）
            log.info("用户{}不存在，执行注册", loginParam.getUsername());

            return performRegisterAndLogin(loginParam, retryCount);
        }
    }

    /**
     * 执行注册和登录
     */
    private Result<Object> performRegisterAndLogin(LoginOrRegisterParam loginParam, int retryCount) {
        // 构建注册请求
        UserCreateRequest userCreateRequest = new UserCreateRequest();
        userCreateRequest.setUsername(loginParam.getUsername());
        userCreateRequest.setPassword(loginParam.getPassword());
        userCreateRequest.setNickname(loginParam.getUsername());
        userCreateRequest.setRole("user");
        userCreateRequest.setInviteCode(loginParam.getInviteCode());

        try {
            // 执行注册
            Result<Void> registerResult = userFacadeService.createUser(userCreateRequest);

            if (!registerResult.getSuccess()) {
                return handleRegisterFailure(loginParam, registerResult, retryCount);
            }

            log.info("用户注册成功，用户名：{}", loginParam.getUsername());

            // 注册成功后自动登录（复用register的逻辑）
            Result<Object> autoLoginResult = performAutoLogin(loginParam.getUsername(), loginParam.getPassword(), "注册成功");

            // 如果自动登录成功，标记为新用户
            if (autoLoginResult.getSuccess() && autoLoginResult.getData() instanceof Map) {
                Map<String, Object> responseData = (Map<String, Object>) autoLoginResult.getData();
                responseData.put("message", "注册并登录成功");
                responseData.put("isNewUser", true);
            }

            return autoLoginResult;

        } catch (Exception e) {
            // 捕获可能的运行时异常（如数据库约束异常）
            log.error("注册过程异常，用户名：{}", loginParam.getUsername(), e);

            // 检查是否是重复用户异常
            String errorMessage = e.getMessage();
            if (isDuplicateUserError(errorMessage)) {
                log.info("捕获到并发注册异常，用户{}可能已被创建，尝试登录", loginParam.getUsername());

                // 短暂等待，让数据库事务提交
                try {
                    Thread.sleep(50);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }

                // 重试整个流程
                return loginOrRegisterWithRetry(loginParam, retryCount + 1);
            }

            return createErrorResult("REGISTER_EXCEPTION", "注册失败: " + e.getMessage());
        }
    }

    /**
     * 处理注册失败
     */
    private Result<Object> handleRegisterFailure(LoginOrRegisterParam loginParam, Result<Void> registerResult, int retryCount) {
        log.error("用户注册失败：{}", registerResult.getMessage());

        String errorMessage = registerResult.getMessage();
        if (isDuplicateUserError(errorMessage)) {
            log.info("注册失败：用户{}已存在（并发情况），尝试登录", loginParam.getUsername());

            // 短暂等待，让数据库事务提交
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            // 重试整个流程
            return loginOrRegisterWithRetry(loginParam, retryCount + 1);
        }

        return createErrorResult("USER_REGISTER_FAILED", registerResult.getMessage());
    }

    /**
     * 判断是否是重复用户错误
     */
    private boolean isDuplicateUserError(String errorMessage) {
        if (errorMessage == null) {
            return false;
        }

        return errorMessage.contains("已存在") ||
                errorMessage.contains("duplicate") ||
                errorMessage.contains("Duplicate entry") ||
                errorMessage.contains("uk_username") ||
                errorMessage.contains("UNIQUE constraint failed") ||
                errorMessage.contains("duplicate key");
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
                "user_id", userInfo.getId(),
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