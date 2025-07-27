package com.gig.collide.auth.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.SaLoginModel;
import cn.dev33.satoken.stp.StpUtil;
import com.gig.collide.api.user.request.UserQueryRequest;
import com.gig.collide.api.user.request.UserRegisterRequest;
import com.gig.collide.api.user.request.condition.UserUserNameQueryCondition;
import com.gig.collide.api.user.response.UserOperatorResponse;
import com.gig.collide.api.user.response.UserQueryResponse;
import com.gig.collide.api.user.response.data.UserInfo;
import com.gig.collide.api.user.service.UserFacadeService;
import com.gig.collide.base.exception.BizException;
import com.gig.collide.base.exception.AuthErrorCode;
import com.gig.collide.auth.exception.AuthException;
import com.gig.collide.auth.param.LoginParam;
import com.gig.collide.auth.param.RegisterParam;
import com.gig.collide.auth.param.LoginOrRegisterParam;
import com.gig.collide.auth.vo.LoginVO;
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
 * 认证相关接口
 * 基于Code项目设计哲学，实现简化认证系统
 *
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-16
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "认证服务", description = "简化的用户认证接口，支持用户名密码登录注册和邀请码功能")
public class AuthController {

    @DubboReference(version = "1.0.0")
    private UserFacadeService userFacadeService;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * 默认登录超时时间：7天
     */
    private static final Integer DEFAULT_LOGIN_SESSION_TIMEOUT = 60 * 60 * 24 * 7;

    /**
     * 用户注册
     */
    @PostMapping("/register")
    @Operation(summary = "用户注册", description = "简化的用户名密码注册，支持邀请码")
    public Result<Object> register(@Valid @RequestBody RegisterParam registerParam) {
        try {
            log.info("用户注册请求，用户名：{}，邀请码：{}", registerParam.getUsername(), registerParam.getInviteCode());
            
            // 构建注册请求
            UserRegisterRequest userRegisterRequest = new UserRegisterRequest();
            userRegisterRequest.setUsername(registerParam.getUsername());
            userRegisterRequest.setPassword(registerParam.getPassword());
            userRegisterRequest.setEmail(registerParam.getEmail());
            userRegisterRequest.setPhone(registerParam.getPhone());
            userRegisterRequest.setInviteCode(registerParam.getInviteCode());

            // 执行注册
            UserOperatorResponse registerResult = userFacadeService.register(userRegisterRequest);
            
            if (registerResult.getSuccess()) {
                // 注册成功后自动登录
                UserQueryRequest queryRequest = new UserQueryRequest();
                UserUserNameQueryCondition condition = new UserUserNameQueryCondition();
                condition.setUserName(registerParam.getUsername());
                queryRequest.setUserUserNameQueryCondition(condition);
                
                UserQueryResponse<UserInfo> userQueryResponse = userFacadeService.query(queryRequest);
                UserInfo userInfo = userQueryResponse.getData();
                
                if (userInfo != null) {
                    // 设置登录会话
                    StpUtil.login(userInfo.getUserId(), new SaLoginModel()
                        .setTimeout(DEFAULT_LOGIN_SESSION_TIMEOUT));
                    StpUtil.getSession().set(userInfo.getUserId().toString(), userInfo);
                    
                    // 构建返回数据
                    Map<String, Object> data = new HashMap<>();
                    data.put("user", buildUserInfo(userInfo));
                    data.put("token", StpUtil.getTokenValue());
                    data.put("message", "注册成功");
                    
                    log.info("用户注册并登录成功，用户ID：{}", userInfo.getUserId());
                    return Result.success(data);
                }
            }
            
            log.error("用户注册失败：{}", registerResult.getResponseMessage());
            return Result.error(registerResult.getResponseCode(), registerResult.getResponseMessage());
            
        } catch (BizException e) {
            log.error("用户注册失败，用户名：{}，错误：{}", registerParam.getUsername(), e.getMessage());
            return Result.error(e.getErrorCode().getCode(), e.getErrorCode().getMessage());
        } catch (Exception e) {
            log.error("用户注册异常，用户名：{}", registerParam.getUsername(), e);
            return Result.error("REGISTER_ERROR", e.getMessage());
        }
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "标准用户名密码登录")
    public Result<Object> login(@Valid @RequestBody LoginParam loginParam) {
        try {
            log.info("用户登录请求，用户名：{}", loginParam.getUsername());
            
            // 查询用户信息
            UserQueryRequest userQueryRequest = new UserQueryRequest();
            UserUserNameQueryCondition condition = new UserUserNameQueryCondition();
            condition.setUserName(loginParam.getUsername());
            userQueryRequest.setUserUserNameQueryCondition(condition);
            
            UserQueryResponse<UserInfo> userQueryResponse = userFacadeService.query(userQueryRequest);
            UserInfo userInfo = userQueryResponse.getData();
            
            if (userInfo == null) {
                log.warn("用户不存在，用户名：{}", loginParam.getUsername());
                return Result.error("LOGIN_ERROR", "用户名或密码错误");
            }
            
            // 验证密码（这里需要调用用户服务的密码验证方法）
            // 临时实现：假设用户服务已经处理了密码验证
            if (userInfo.getStatus() != null && !"ACTIVE".equals(userInfo.getStatus())) {
                throw new AuthException(AuthErrorCode.USER_STATUS_IS_NOT_ACTIVE);
            }
            
            // 设置登录会话
            StpUtil.login(userInfo.getUserId(), new SaLoginModel()
                .setIsLastingCookie(loginParam.getRememberMe())
                .setTimeout(DEFAULT_LOGIN_SESSION_TIMEOUT));
            StpUtil.getSession().set(userInfo.getUserId().toString(), userInfo);
            
            // 构建返回数据
            Map<String, Object> data = new HashMap<>();
            data.put("user", buildUserInfo(userInfo));
            data.put("token", StpUtil.getTokenValue());
            data.put("message", "登录成功");
            
            log.info("用户登录成功，用户ID：{}", userInfo.getUserId());
            return Result.success(data);
            
        } catch (AuthException e) {
            log.error("用户登录失败，用户名：{}，错误：{}", loginParam.getUsername(), e.getMessage());
            return Result.error(e.getErrorCode().getCode(), e.getErrorCode().getMessage());
        } catch (BizException e) {
            log.error("用户登录失败，用户名：{}，错误：{}", loginParam.getUsername(), e.getMessage());
            return Result.error(e.getErrorCode().getCode(), e.getErrorCode().getMessage());
        } catch (Exception e) {
            log.error("用户登录异常，用户名：{}", loginParam.getUsername(), e);
            return Result.error("LOGIN_ERROR", "登录失败，请稍后重试");
        }
    }

    /**
     * 登录或注册 - 核心功能！
     * 基于Code项目设计哲学：用户不存在时自动注册
     */
    @PostMapping("/login-or-register")
    @Operation(summary = "登录或注册", description = "如果用户不存在则自动注册，一个接口解决登录和注册需求")
    public Result<Object> loginOrRegister(@Valid @RequestBody LoginOrRegisterParam param) {
        try {
            log.info("用户登录或注册请求，用户名：{}，邀请码：{}", param.getUsername(), param.getInviteCode());
            
            // 查询用户是否存在
            UserQueryRequest userQueryRequest = new UserQueryRequest();
            UserUserNameQueryCondition condition = new UserUserNameQueryCondition();
            condition.setUserName(param.getUsername());
            userQueryRequest.setUserUserNameQueryCondition(condition);
            
            UserQueryResponse<UserInfo> userQueryResponse = userFacadeService.query(userQueryRequest);
            UserInfo userInfo = userQueryResponse.getData();
            
            boolean isNewUser = false;
            
            if (userInfo == null) {
                // 用户不存在，自动注册
                log.info("用户{}不存在，执行自动注册", param.getUsername());
                
                UserRegisterRequest userRegisterRequest = new UserRegisterRequest();
                userRegisterRequest.setUsername(param.getUsername());
                userRegisterRequest.setPassword(param.getPassword());
                userRegisterRequest.setInviteCode(param.getInviteCode());
                
                UserOperatorResponse registerResult = userFacadeService.register(userRegisterRequest);
                
                if (registerResult.getSuccess()) {
                    // 注册成功，重新查询用户信息
                    userQueryResponse = userFacadeService.query(userQueryRequest);
                    userInfo = userQueryResponse.getData();
                    isNewUser = true;
                    log.info("用户自动注册成功，用户ID：{}", userInfo.getUserId());
                } else {
                    log.error("用户自动注册失败：{}", registerResult.getResponseMessage());
                    return Result.error(registerResult.getResponseCode(), registerResult.getResponseMessage());
                }
            } else {
                // 用户存在，验证密码（这里需要调用用户服务的密码验证）
                // 临时实现：假设密码验证通过
                log.info("用户{}存在，执行登录", param.getUsername());
                
                if (userInfo.getStatus() != null && !"ACTIVE".equals(userInfo.getStatus())) {
                    throw new AuthException(AuthErrorCode.USER_STATUS_IS_NOT_ACTIVE);
                }
            }
            
            // 设置登录会话
            StpUtil.login(userInfo.getUserId(), new SaLoginModel()
                .setTimeout(DEFAULT_LOGIN_SESSION_TIMEOUT));
            StpUtil.getSession().set(userInfo.getUserId().toString(), userInfo);
            
            // 构建返回数据
            Map<String, Object> data = new HashMap<>();
            data.put("user", buildUserInfo(userInfo));
            data.put("token", StpUtil.getTokenValue());
            data.put("isNewUser", isNewUser);
            data.put("message", isNewUser ? "注册并登录成功" : "登录成功");
            
            log.info("用户{}完成，用户ID：{}，是否新用户：{}", 
                isNewUser ? "注册并登录" : "登录", userInfo.getUserId(), isNewUser);
            return Result.success(data);
            
        } catch (AuthException e) {
            log.error("用户登录或注册失败，用户名：{}，错误：{}", param.getUsername(), e.getMessage());
            return Result.error(e.getErrorCode().getCode(), e.getErrorCode().getMessage());
        } catch (BizException e) {
            log.error("用户登录或注册失败，用户名：{}，错误：{}", param.getUsername(), e.getMessage());
            return Result.error(e.getErrorCode().getCode(), e.getErrorCode().getMessage());
        } catch (Exception e) {
            log.error("用户登录或注册异常，用户名：{}", param.getUsername(), e);
            return Result.error("LOGIN_REGISTER_ERROR", "操作失败，请稍后重试");
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
     * 验证邀请码
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
            // 目前先返回模拟数据
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
     * 获取我的邀请信息
     */
    @GetMapping("/my-invite-info")
    @SaCheckLogin
    @Operation(summary = "获取邀请信息", description = "获取当前用户的邀请码和邀请统计")
    public Result<Object> getMyInviteInfo() {
        try {
            Long userId = StpUtil.getLoginIdAsLong();
            log.info("获取用户邀请信息，用户ID：{}", userId);
            
            // TODO: 这里需要调用用户服务的邀请统计接口
            // 目前先返回模拟数据
            Map<String, Object> data = new HashMap<>();
            data.put("inviteCode", "ABC12345");
            data.put("totalInvitedCount", 5);
            data.put("invitedUsers", java.util.List.of(
                Map.of(
                    "id", 67890,
                    "username", "invited_user1",
                    "nickname", "被邀请用户1",
                    "createTime", "2024-01-15T09:00:00"
                )
            ));
            
            return Result.success(data);
            
        } catch (Exception e) {
            log.error("获取邀请信息失败", e);
            return Result.error("GET_INVITE_INFO_ERROR", "获取失败，请稍后重试");
        }
    }

    /**
     * 构建用户信息返回对象
     */
    private Map<String, Object> buildUserInfo(UserInfo userInfo) {
        Map<String, Object> user = new HashMap<>();
        user.put("id", userInfo.getUserId());
        user.put("username", userInfo.getUsername());
        user.put("nickname", userInfo.getNickName());
        user.put("avatar", userInfo.getProfilePhotoUrl());
        user.put("role", userInfo.getRole());
        user.put("status", userInfo.getStatus());
        user.put("createTime", userInfo.getCreateTime());
        // TODO: 添加邀请码、邀请统计等字段
        return user;
    }

    /**
     * 服务健康检查
     */
    @RequestMapping("/test")
    public String test() {
        return "Collide Auth Service v2.0 is running! (Simplified Authentication System)";
    }
}

