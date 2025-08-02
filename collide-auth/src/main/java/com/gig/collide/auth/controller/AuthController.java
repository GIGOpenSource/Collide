package com.gig.collide.auth.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.gig.collide.auth.param.LoginParam;
import com.gig.collide.auth.param.RegisterParam;
import com.gig.collide.auth.param.LoginOrRegisterParam;
import com.gig.collide.auth.service.AuthService;
import com.gig.collide.web.vo.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器 - 简洁版
 * 
 * @author GIG Team
 * @version 2.0.0 (简洁版)
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "认证服务", description = "简化的用户认证接口，支持用户名密码登录注册和邀请码功能")
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * 用户注册
     */
    @PostMapping("/register")
    @Operation(summary = "用户注册", description = "用户名密码注册，支持邀请码")
    public Result<Object> register(@Valid @RequestBody RegisterParam registerParam) {
        try {
            log.info("用户注册请求: {}", registerParam.getUsername());
            return authService.register(registerParam);
        } catch (IllegalArgumentException e) {
            log.warn("用户注册参数错误: {}", e.getMessage());
            return Result.error("INVALID_PARAMETER", e.getMessage());
        } catch (Exception e) {
            log.error("用户注册异常", e);
            return Result.error("USER_REGISTER_ERROR", "注册失败，请稍后重试");
        }
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "用户名密码登录")
    public Result<Object> login(@Valid @RequestBody LoginParam loginParam) {
        try {
            log.info("用户登录请求: {}", loginParam.getUsername());
            return authService.login(loginParam);
        } catch (IllegalArgumentException e) {
            log.warn("用户登录参数错误: {}", e.getMessage());
            return Result.error("INVALID_PARAMETER", e.getMessage());
        } catch (Exception e) {
            log.error("用户登录异常", e);
            return Result.error("USER_LOGIN_ERROR", "登录失败，请检查用户名和密码");
        }
    }

    /**
     * 登录或注册
     */
    @PostMapping("/login-or-register")
    @Operation(summary = "登录或注册", description = "核心接口：用户存在则登录，不存在则自动注册")
    public Result<Object> loginOrRegister(@Valid @RequestBody LoginOrRegisterParam loginParam) {
        try {
            log.info("登录或注册请求: {}", loginParam.getUsername());
            return authService.loginOrRegister(loginParam);
        } catch (IllegalArgumentException e) {
            log.warn("登录或注册参数错误: {}", e.getMessage());
            return Result.error("INVALID_PARAMETER", e.getMessage());
        } catch (Exception e) {
            log.error("登录或注册异常", e);
            return Result.error("LOGIN_OR_REGISTER_ERROR", "操作失败，请稍后重试");
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
            return authService.logout();
        } catch (Exception e) {
            log.error("用户登出异常", e);
            return Result.error("LOGOUT_ERROR", "登出失败: " + e.getMessage());
        }
    }



    /**
     * 验证Token
     */
    @GetMapping("/verify-token")
    @SaCheckLogin
    @Operation(summary = "验证Token", description = "验证当前Token是否有效")
    public Result<Object> verifyToken() {
        try {
            return authService.verifyToken();
        } catch (Exception e) {
            log.error("Token验证异常", e);
            return Result.error("TOKEN_INVALID", "Token验证失败: " + e.getMessage());
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

