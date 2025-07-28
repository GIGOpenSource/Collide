package com.gig.collide.auth.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.SaLoginModel;
import cn.dev33.satoken.stp.StpUtil;
import com.gig.collide.api.user.UserFacadeService;
import com.gig.collide.api.user.request.UserCreateRequest;
import com.gig.collide.api.user.response.UserResponse;

import com.gig.collide.auth.param.LoginParam;
import com.gig.collide.auth.param.RegisterParam;
import com.gig.collide.auth.param.LoginOrRegisterParam;
import com.gig.collide.web.vo.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 认证相关接口 - 简洁版
 * 基于简洁版用户API重构，保持认证功能完整
 *
 * @author GIG Team
 * @version 2.0.0
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "认证服务", description = "简化的用户认证接口，支持用户名密码登录注册和邀请码功能")
public class AuthController {

    @DubboReference(version = "2.0.0")
    private UserFacadeService userFacadeService;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * 默认登录超时时间：7天
     */
    private static final Integer DEFAULT_LOGIN_SESSION_TIMEOUT = 60 * 60 * 24 * 7;

    /**
     * 用户注册 - 基于简洁版API
     */
    @PostMapping("/register")
    @Operation(summary = "用户注册", description = "用户名密码注册，支持邀请码")
    public Result<Object> register(@Valid @RequestBody RegisterParam registerParam) {
        try {
            log.info("用户注册请求，用户名：{}，邀请码：{}", registerParam.getUsername(), registerParam.getInviteCode());
            
            // 检查用户是否已存在
            Result<UserResponse> existingUser = userFacadeService.getUserByUsername(registerParam.getUsername());
            if (existingUser.getSuccess() && existingUser.getData() != null) {
                log.warn("用户名已存在：{}", registerParam.getUsername());
                return Result.error("USER_ALREADY_EXISTS", "用户名已存在");
            }
            
            // 构建注册请求
            UserCreateRequest userCreateRequest = new UserCreateRequest();
            userCreateRequest.setUsername(registerParam.getUsername());
            userCreateRequest.setPassword(registerParam.getPassword()); // 密码将在UserService中加密
            userCreateRequest.setNickname(registerParam.getUsername()); // 默认昵称为用户名
            userCreateRequest.setRole("user"); // 默认角色
            userCreateRequest.setInviteCode(registerParam.getInviteCode());

            // 执行注册
            Result<UserResponse> registerResult = userFacadeService.createUser(userCreateRequest);
            
            if (registerResult.getSuccess() && registerResult.getData() != null) {
                UserResponse userInfo = registerResult.getData();
                
                // 注册成功后自动登录
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
                
                String token = StpUtil.getTokenValue();
                
                log.info("用户注册并登录成功，用户ID：{}，Token：{}", userInfo.getId(), token);
                
                Map<String, Object> response = new HashMap<>();
                response.put("user", userInfo);
                response.put("token", token);
                response.put("message", "注册成功");
                
                return Result.success(response);
            } else {
                log.error("用户注册失败：{}", registerResult.getMessage());
                return Result.error("USER_REGISTER_FAILED", registerResult.getMessage());
            }
        } catch (Exception e) {
            log.error("注册过程中发生异常，用户名：{}", registerParam.getUsername(), e);
            return Result.error("SYSTEM_ERROR", "注册失败，请稍后重试");
        }
    }

    /**
     * 用户登录 - 基于简洁版API
     */
    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "用户名密码登录")
    public Result<Object> login(@Valid @RequestBody LoginParam loginParam) {
        try {
            log.info("用户登录请求，用户名：{}", loginParam.getUsername());
            
            // 执行登录验证
            Result<UserResponse> loginResult = userFacadeService.login(loginParam.getUsername(), loginParam.getPassword());
            
            if (loginResult.getSuccess() && loginResult.getData() != null) {
                UserResponse userInfo = loginResult.getData();
                
                // 登录成功，生成Token
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
                
                String token = StpUtil.getTokenValue();
                
                log.info("用户登录成功，用户ID：{}，Token：{}", userInfo.getId(), token);
                
                Map<String, Object> response = new HashMap<>();
                response.put("user", userInfo);
                response.put("token", token);
                response.put("message", "登录成功");
                
                return Result.success(response);
            } else {
                log.warn("用户登录失败：{}", loginResult.getMessage());
                return Result.error("LOGIN_FAILED", loginResult.getMessage());
            }
        } catch (Exception e) {
            log.error("登录过程中发生异常，用户名：{}", loginParam.getUsername(), e);
            return Result.error("SYSTEM_ERROR", "登录失败，请稍后重试");
        }
    }

    /**
     * 登录或注册 - 核心接口，基于简洁版API
     */
    @PostMapping("/login-or-register")
    @Operation(summary = "登录或注册", description = "核心接口：用户存在则登录，不存在则自动注册")
    public Result<Object> loginOrRegister(@Valid @RequestBody LoginOrRegisterParam loginParam) {
        try {
            log.info("登录或注册请求，用户名：{}", loginParam.getUsername());
            
            // 先尝试登录
            Result<UserResponse> loginResult = userFacadeService.login(loginParam.getUsername(), loginParam.getPassword());
            
            if (loginResult.getSuccess() && loginResult.getData() != null) {
                // 登录成功
                UserResponse userInfo = loginResult.getData();
                
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
                
                String token = StpUtil.getTokenValue();
                
                log.info("用户登录成功，用户ID：{}，Token：{}", userInfo.getId(), token);
                
                Map<String, Object> response = new HashMap<>();
                response.put("user", userInfo);
                response.put("token", token);
                response.put("message", "登录成功");
                response.put("isNewUser", false);
                
                return Result.success(response);
            } else {
                // 登录失败，检查是否用户不存在，如果是则自动注册
                Result<UserResponse> existingUser = userFacadeService.getUserByUsername(loginParam.getUsername());
                
                if (!existingUser.getSuccess() || existingUser.getData() == null) {
                    // 用户不存在，自动注册
                    log.info("用户{}不存在，执行自动注册", loginParam.getUsername());
                    
                    UserCreateRequest userCreateRequest = new UserCreateRequest();
                    userCreateRequest.setUsername(loginParam.getUsername());
                    userCreateRequest.setPassword(loginParam.getPassword());
                    userCreateRequest.setNickname(loginParam.getUsername()); // 默认昵称为用户名
                    userCreateRequest.setRole("user"); // 默认角色
                    userCreateRequest.setInviteCode(loginParam.getInviteCode());
                    
                    Result<UserResponse> registerResult = userFacadeService.createUser(userCreateRequest);
                    
                    if (registerResult.getSuccess() && registerResult.getData() != null) {
                        // 注册成功，自动登录
                        UserResponse userInfo = registerResult.getData();
                        
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
                        
                        String token = StpUtil.getTokenValue();
                        
                        log.info("用户自动注册并登录成功，用户ID：{}", userInfo.getId());
                        
                        Map<String, Object> response = new HashMap<>();
                        response.put("user", userInfo);
                        response.put("token", token);
                        response.put("message", "注册并登录成功");
                        response.put("isNewUser", true);
                        
                        return Result.success(response);
                    } else {
                        log.error("用户自动注册失败：{}", registerResult.getMessage());
                        return Result.error("AUTO_REGISTER_FAILED", registerResult.getMessage());
                    }
                } else {
                    // 用户存在但密码错误
                    log.warn("用户{}密码错误", loginParam.getUsername());
                    return Result.error("PASSWORD_ERROR", "密码错误");
                }
            }
        } catch (Exception e) {
            log.error("登录或注册过程中发生异常，用户名：{}", loginParam.getUsername(), e);
            return Result.error("SYSTEM_ERROR", "操作失败，请稍后重试");
        }
    }

    /**
     * 用户登出
     */
    @PostMapping("/logout")
    @SaCheckLogin
    @Operation(summary = "用户登出", description = "退出登录")
    public Result<String> logout() {
        try {
            Long userId = StpUtil.getLoginIdAsLong();
            StpUtil.logout();
            log.info("用户登出成功，用户ID：{}", userId);
            return Result.success("登出成功");
        } catch (Exception e) {
            log.error("用户登出失败", e);
            return Result.error("LOGOUT_ERROR", "登出失败");
        }
    }

    /**
     * 验证邀请码 - 基于简洁版API
     */
    @GetMapping("/validate-invite-code")
    @Operation(summary = "验证邀请码", description = "检查邀请码是否有效并返回邀请人信息")
    public Result<Object> validateInviteCode(
            @Parameter(description = "邀请码", required = true) @RequestParam String inviteCode) {
        try {
            if (!StringUtils.hasText(inviteCode)) {
                return Result.error("INVALID_INVITE_CODE", "邀请码不能为空");
            }
            
            log.info("验证邀请码：{}", inviteCode);
            
            // TODO: 这里需要调用用户服务的邀请码验证接口
            // 目前先返回模拟数据，等用户服务支持邀请码功能后再实现
            Map<String, Object> data = new HashMap<>();
            data.put("valid", true);
            data.put("inviter", Map.of(
                "id", 12345,
                "username", "inviter_user",
                "nickname", "邀请用户",
                "avatar", ""
            ));
            
            log.info("邀请码验证成功：{}", inviteCode);
            return Result.success(data);
            
        } catch (Exception e) {
            log.error("验证邀请码失败", e);
            return Result.error("VALIDATE_ERROR", "验证失败，请稍后重试");
        }
    }

    /**
     * 获取我的邀请信息 - 基于简洁版API
     */
    @GetMapping("/my-invite-info")
    @SaCheckLogin
    @Operation(summary = "获取邀请信息", description = "获取当前用户的邀请码和邀请统计")
    public Result<Object> getMyInviteInfo() {
        try {
            Long userId = StpUtil.getLoginIdAsLong();
            log.info("获取用户邀请信息，用户ID：{}", userId);
            
            // 获取用户信息
            Result<UserResponse> userResult = userFacadeService.getUserById(userId);
            if (userResult.getSuccess() && userResult.getData() != null) {
                UserResponse user = userResult.getData();
                
                Map<String, Object> data = new HashMap<>();
                data.put("inviteCode", user.getInviteCode());
                data.put("totalInvitedCount", user.getInvitedCount() != null ? user.getInvitedCount() : 0);
                
                // TODO: 邀请用户列表功能暂时不实现，需要更复杂的查询
                data.put("invitedUsers", java.util.List.of());
                
                return Result.success(data);
            } else {
                return Result.error("USER_NOT_FOUND", "用户信息获取失败");
            }
            
        } catch (Exception e) {
            log.error("获取邀请信息失败", e);
            return Result.error("GET_INVITE_INFO_ERROR", "获取失败，请稍后重试");
        }
    }

    /**
     * 服务健康检查
     */
    @GetMapping("/test")
    @Operation(summary = "健康检查", description = "检查认证服务状态")
    public Result<String> test() {
        return Result.success("Collide Auth v2.0 - 简洁版认证服务运行正常");
    }
}

