package com.gig.collide.auth.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import com.gig.collide.api.user.UserFacadeService;
import com.gig.collide.api.user.response.UserResponse;
import com.gig.collide.web.vo.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Token相关接口 - 简洁版
 * 
 * @author GIG Team
 * @version 2.0.0
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Token服务", description = "Token验证和用户信息获取")
public class TokenController {

    @DubboReference(version = "2.0.0")
    private UserFacadeService userFacadeService;

    /**
     * 验证Token并获取用户信息
     */
    @GetMapping("/me")
    @SaCheckLogin
    @Operation(summary = "获取当前用户信息", description = "通过Token验证并返回当前用户信息")
    public Result<UserResponse> getCurrentUser() {
        try {
            Long userId = StpUtil.getLoginIdAsLong();
            log.info("获取当前用户信息，用户ID：{}", userId);
            
            Result<UserResponse> userResult = userFacadeService.getUserById(userId);
            if (userResult.getSuccess() && userResult.getData() != null) {
                return Result.success(userResult.getData());
            } else {
                log.warn("用户信息获取失败，用户ID：{}", userId);
                return Result.error("USER_NOT_FOUND", "用户信息获取失败");
            }
        } catch (Exception e) {
            log.error("获取当前用户信息失败", e);
            return Result.error("SYSTEM_ERROR", "获取用户信息失败，请稍后重试");
        }
    }

    /**
     * 验证Token有效性
     */
    @GetMapping("/verify-token")
    @SaCheckLogin
    @Operation(summary = "验证Token", description = "验证当前Token是否有效")
    public Result<Object> verifyToken() {
        try {
            Long userId = StpUtil.getLoginIdAsLong();
            String tokenValue = StpUtil.getTokenValue();
            
            log.info("Token验证成功，用户ID：{}，Token：{}", userId, tokenValue);
            
            return Result.success(java.util.Map.of(
                "valid", true,
                "userId", userId,
                "token", tokenValue,
                "message", "Token有效"
            ));
        } catch (Exception e) {
            log.error("Token验证失败", e);
            return Result.error("TOKEN_INVALID", "Token验证失败");
        }
    }
}
