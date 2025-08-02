package com.gig.collide.auth.service.impl;

import cn.dev33.satoken.stp.SaLoginModel;
import cn.dev33.satoken.stp.StpUtil;
import com.gig.collide.api.user.UserFacadeService;
import com.gig.collide.api.user.UserProfileFacadeService;
import com.gig.collide.api.user.UserRoleFacadeService;
import com.gig.collide.api.user.constant.UserStatusConstant;
import com.gig.collide.api.user.request.main.UserCoreCreateRequest;
import com.gig.collide.api.user.request.main.UserLoginRequest;
import com.gig.collide.api.user.request.profile.UserProfileCreateRequest;
import com.gig.collide.api.user.request.role.UserRoleCreateRequest;
import com.gig.collide.api.user.response.main.UserCoreResponse;
import com.gig.collide.api.user.response.profile.UserProfileResponse;
import com.gig.collide.auth.param.LoginParam;
import com.gig.collide.auth.param.RegisterParam;
import com.gig.collide.auth.param.LoginOrRegisterParam;
import com.gig.collide.auth.service.AuthService;
import com.gig.collide.web.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

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

    @DubboReference(version = "1.0.0", timeout = 10000)
    private UserProfileFacadeService userProfileFacadeService;

    @DubboReference(version = "1.0.0", timeout = 10000)
    private UserRoleFacadeService userRoleFacadeService;

    /**
     * 默认登录超时时间：7天
     */
    private static final Integer DEFAULT_LOGIN_SESSION_TIMEOUT = 60 * 60 * 24 * 7;

    @Override
    public Result<Object> register(RegisterParam registerParam) {
        log.info("用户注册请求，用户名：{}", registerParam.getUsername());
        
        // 检查用户是否已存在
        Result<Boolean> existsResult = userFacadeService.checkUsernameExists(registerParam.getUsername());
        if (existsResult.getSuccess() && Boolean.TRUE.equals(existsResult.getData())) {
            log.warn("用户名已存在：{}", registerParam.getUsername());
            return createErrorResult("USER_ALREADY_EXISTS", "用户名已存在");
        }
        
        // 构建用户核心信息创建请求
        UserCoreCreateRequest userCreateRequest = new UserCoreCreateRequest();
        userCreateRequest.setUsername(registerParam.getUsername());
        userCreateRequest.setPassword(registerParam.getPassword());
        userCreateRequest.setStatus(UserStatusConstant.ACTIVE);

        // 执行用户核心信息创建
        Result<UserCoreResponse> createResult = userFacadeService.createUser(userCreateRequest);
        
        if (!createResult.getSuccess() || createResult.getData() == null) {
            log.error("用户注册失败：{}", createResult.getMessage());
            return createErrorResult("USER_REGISTER_FAILED", createResult.getMessage());
        }

        UserCoreResponse newUser = createResult.getData();
        log.info("用户核心信息创建成功，用户ID：{}, 用户名：{}", newUser.getId(), newUser.getUsername());

        // 创建用户资料
        try {
            UserProfileCreateRequest profileRequest = new UserProfileCreateRequest();
            profileRequest.setUserId(newUser.getId());
            profileRequest.setNickname(registerParam.getUsername()); // 默认昵称为用户名
            
            Result<UserProfileResponse> profileResult = userProfileFacadeService.createProfile(profileRequest);
            if (profileResult.getSuccess()) {
                log.info("用户资料创建成功，用户ID：{}", newUser.getId());
            } else {
                log.warn("用户资料创建失败，用户ID：{}，原因：{}", newUser.getId(), profileResult.getMessage());
            }
        } catch (Exception e) {
            log.warn("用户资料创建异常，用户ID：{}，错误：{}", newUser.getId(), e.getMessage());
        }

        // 分配默认用户角色
        try {
            UserRoleCreateRequest roleRequest = new UserRoleCreateRequest();
            roleRequest.setUserId(newUser.getId());
            roleRequest.setRole("user");
            
            Result<?> roleResult = userRoleFacadeService.assignRole(roleRequest);
            if (roleResult.getSuccess()) {
                log.info("用户角色分配成功，用户ID：{}", newUser.getId());
            } else {
                log.warn("用户角色分配失败，用户ID：{}，原因：{}", newUser.getId(), roleResult.getMessage());
            }
        } catch (Exception e) {
            log.warn("用户角色分配异常，用户ID：{}，错误：{}", newUser.getId(), e.getMessage());
        }

        log.info("用户注册成功，用户名：{}", registerParam.getUsername());
        
        // 注册成功后自动登录
        return performAutoLogin(registerParam.getUsername(), registerParam.getPassword(), "注册成功");
    }

    @Override
    public Result<Object> login(LoginParam loginParam) {
        log.info("用户登录请求，用户名：{}", loginParam.getUsername());
        
        // 构建登录请求
        UserLoginRequest loginRequest = new UserLoginRequest();
        loginRequest.setLoginId(loginParam.getUsername());
        loginRequest.setPassword(loginParam.getPassword());
        loginRequest.setLoginType("username");
        
        // 执行登录验证
        Result<UserCoreResponse> loginResult = userFacadeService.login(loginRequest);
        
        if (!loginResult.getSuccess() || loginResult.getData() == null) {
            log.warn("用户登录失败：{}", loginResult.getMessage());
            return createErrorResult("LOGIN_FAILED", loginResult.getMessage());
        }

        UserCoreResponse userInfo = loginResult.getData();
        
        // 检查用户状态
        if (!UserStatusConstant.isValidStatus(userInfo.getStatus())) {
            log.warn("用户状态异常，无法登录，用户ID：{}，状态：{}", userInfo.getId(), userInfo.getStatus());
            return createErrorResult("USER_STATUS_INVALID", "用户状态异常，无法登录");
        }
        
        // 获取用户资料信息（可选）
        UserProfileResponse profileInfo = null;
        try {
            Result<UserProfileResponse> profileResult = userProfileFacadeService.getProfileByUserId(userInfo.getId());
            if (profileResult.getSuccess()) {
                profileInfo = profileResult.getData();
            }
        } catch (Exception e) {
            log.warn("获取用户资料失败，用户ID：{}，错误：{}", userInfo.getId(), e.getMessage());
        }

        // 获取用户角色信息（可选）
        String userRole = "user"; // 默认角色
        try {
            Result<String> roleResult = userRoleFacadeService.getHighestRole(userInfo.getId());
            if (roleResult.getSuccess() && roleResult.getData() != null) {
                userRole = roleResult.getData();
            }
        } catch (Exception e) {
            log.warn("获取用户角色失败，用户ID：{}，错误：{}", userInfo.getId(), e.getMessage());
        }
        
        // 生成Token并设置Session
        String token = createUserSession(userInfo, userRole);
        
        log.info("用户登录成功，用户ID：{}，Token：{}", userInfo.getId(), token);
        
        // 构建完整的响应数据
        Map<String, Object> userData = new HashMap<>();
        userData.put("id", userInfo.getId());
        userData.put("username", userInfo.getUsername());
        userData.put("email", userInfo.getEmail());
        userData.put("phone", userInfo.getPhone());
        userData.put("status", userInfo.getStatus());
        userData.put("statusDesc", userInfo.getStatusDesc());
        userData.put("role", userRole);
        if (profileInfo != null) {
            userData.put("nickname", profileInfo.getNickname());
            userData.put("avatar", profileInfo.getAvatar());
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("user", userData);
        response.put("token", token);
        response.put("message", "登录成功");
        
        return createSuccessResult(response);
    }

    @Override
    public Result<Object> loginOrRegister(LoginOrRegisterParam loginParam) {
        log.info("登录或注册请求，用户名：{}", loginParam.getUsername());
        
        // 构建登录请求
        UserLoginRequest loginRequest = new UserLoginRequest();
        loginRequest.setLoginId(loginParam.getUsername());
        loginRequest.setPassword(loginParam.getPassword());
        loginRequest.setLoginType("username");
        
        // 先尝试登录
        Result<UserCoreResponse> loginResult = userFacadeService.login(loginRequest);
        
        if (loginResult.getSuccess() && loginResult.getData() != null) {
            // 登录成功
            UserCoreResponse userInfo = loginResult.getData();
            
            // 检查用户状态
            if (!UserStatusConstant.isValidStatus(userInfo.getStatus())) {
                log.warn("用户状态异常，无法登录，用户ID：{}，状态：{}", userInfo.getId(), userInfo.getStatus());
                return createErrorResult("USER_STATUS_INVALID", "用户状态异常，无法登录");
            }
            
            // 获取用户角色
            String userRole = "user";
            try {
                Result<String> roleResult = userRoleFacadeService.getHighestRole(userInfo.getId());
                if (roleResult.getSuccess() && roleResult.getData() != null) {
                    userRole = roleResult.getData();
                }
            } catch (Exception e) {
                log.warn("获取用户角色失败，用户ID：{}，错误：{}", userInfo.getId(), e.getMessage());
            }
            
            String token = createUserSession(userInfo, userRole);
            
            log.info("用户登录成功，用户ID：{}", userInfo.getId());
            
            Map<String, Object> response = new HashMap<>();
            response.put("user", buildUserData(userInfo, userRole));
            response.put("token", token);
            response.put("message", "登录成功");
            response.put("isNewUser", false);
            
            return createSuccessResult(response);
        }
        
        // 登录失败，检查是否用户不存在，如果是则自动注册
        Result<Boolean> existsResult = userFacadeService.checkUsernameExists(loginParam.getUsername());
        
        if (!existsResult.getSuccess() || !Boolean.TRUE.equals(existsResult.getData())) {
            // 用户不存在，自动注册
            return performAutoRegisterAndLogin(loginParam);
        } else {
            // 用户存在但密码错误
            log.warn("用户{}密码错误", loginParam.getUsername());
            return createErrorResult("PASSWORD_ERROR", "密码错误");
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
        UserLoginRequest loginRequest = new UserLoginRequest();
        loginRequest.setLoginId(username);
        loginRequest.setPassword(password);
        loginRequest.setLoginType("username");
        
        Result<UserCoreResponse> loginResult = userFacadeService.login(loginRequest);
        
        if (loginResult.getSuccess() && loginResult.getData() != null) {
            UserCoreResponse userInfo = loginResult.getData();
            
            // 获取用户角色
            String userRole = "user";
            try {
                Result<String> roleResult = userRoleFacadeService.getHighestRole(userInfo.getId());
                if (roleResult.getSuccess() && roleResult.getData() != null) {
                    userRole = roleResult.getData();
                }
            } catch (Exception e) {
                log.warn("获取用户角色失败，用户ID：{}，错误：{}", userInfo.getId(), e.getMessage());
            }
            
            String token = createUserSession(userInfo, userRole);
            
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
        
        // 创建用户核心信息
        UserCoreCreateRequest userCreateRequest = new UserCoreCreateRequest();
        userCreateRequest.setUsername(loginParam.getUsername());
        userCreateRequest.setPassword(loginParam.getPassword());
        userCreateRequest.setStatus(UserStatusConstant.ACTIVE);
        
        Result<UserCoreResponse> createResult = userFacadeService.createUser(userCreateRequest);
        
        if (!createResult.getSuccess() || createResult.getData() == null) {
            log.error("用户自动注册失败：{}", createResult.getMessage());
            return createErrorResult("AUTO_REGISTER_FAILED", createResult.getMessage());
        }

        UserCoreResponse newUser = createResult.getData();
        log.info("用户自动注册成功，用户ID：{}，用户名：{}", newUser.getId(), newUser.getUsername());

        // 创建用户资料（异步，失败不影响主流程）
        try {
            UserProfileCreateRequest profileRequest = new UserProfileCreateRequest();
            profileRequest.setUserId(newUser.getId());
            profileRequest.setNickname(loginParam.getUsername());
            
            userProfileFacadeService.createProfile(profileRequest);
        } catch (Exception e) {
            log.warn("用户资料创建异常，用户ID：{}，错误：{}", newUser.getId(), e.getMessage());
        }

        // 分配默认角色（异步，失败不影响主流程）
        try {
            UserRoleCreateRequest roleRequest = new UserRoleCreateRequest();
            roleRequest.setUserId(newUser.getId());
            roleRequest.setRole("user");
            
            userRoleFacadeService.assignRole(roleRequest);
        } catch (Exception e) {
            log.warn("用户角色分配异常，用户ID：{}，错误：{}", newUser.getId(), e.getMessage());
        }
        
        // 注册成功后自动登录
        UserLoginRequest loginRequest = new UserLoginRequest();
        loginRequest.setLoginId(loginParam.getUsername());
        loginRequest.setPassword(loginParam.getPassword());
        loginRequest.setLoginType("username");
        
        Result<UserCoreResponse> autoLoginResult = userFacadeService.login(loginRequest);
        
        if (!autoLoginResult.getSuccess() || autoLoginResult.getData() == null) {
            log.error("注册成功但自动登录失败：{}", autoLoginResult.getMessage());
            return createErrorResult("AUTO_LOGIN_FAILED", "注册成功但登录失败，请手动登录");
        }

        UserCoreResponse userInfo = autoLoginResult.getData();
        String token = createUserSession(userInfo, "user");
        
        log.info("用户自动注册并登录成功，用户ID：{}", userInfo.getId());
        
        Map<String, Object> response = new HashMap<>();
        response.put("user", buildUserData(userInfo, "user"));
        response.put("token", token);
        response.put("message", "注册并登录成功");
        response.put("isNewUser", true);
        
        return createSuccessResult(response);
    }

    /**
     * 创建用户会话并返回Token
     */
    private String createUserSession(UserCoreResponse userInfo, String userRole) {
        SaLoginModel loginModel = new SaLoginModel()
                .setDevice("web")
                .setTimeout(DEFAULT_LOGIN_SESSION_TIMEOUT)
                .setToken(null);
        StpUtil.login(userInfo.getId(), loginModel);
        
        // 存储用户信息到Session供网关鉴权使用
        StpUtil.getSession().set("userInfo", java.util.Map.of(
            "id", userInfo.getId(),
            "username", userInfo.getUsername(),
            "role", userRole != null ? userRole : "user",
            "status", UserStatusConstant.getStatusString(userInfo.getStatus())
        ));
        
        return StpUtil.getTokenValue();
    }

    /**
     * 构建用户数据对象
     */
    private Map<String, Object> buildUserData(UserCoreResponse userInfo, String userRole) {
        Map<String, Object> userData = new HashMap<>();
        userData.put("id", userInfo.getId());
        userData.put("username", userInfo.getUsername());
        userData.put("email", userInfo.getEmail());
        userData.put("phone", userInfo.getPhone());
        userData.put("status", userInfo.getStatus());
        userData.put("statusDesc", userInfo.getStatusDesc());
        userData.put("role", userRole);
        
        // 尝试获取用户资料信息
        try {
            Result<UserProfileResponse> profileResult = userProfileFacadeService.getProfileByUserId(userInfo.getId());
            if (profileResult.getSuccess() && profileResult.getData() != null) {
                UserProfileResponse profile = profileResult.getData();
                userData.put("nickname", profile.getNickname());
                userData.put("avatar", profile.getAvatar());
                userData.put("bio", profile.getBio());
            }
        } catch (Exception e) {
            log.debug("获取用户资料失败，用户ID：{}，错误：{}", userInfo.getId(), e.getMessage());
        }
        
        return userData;
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