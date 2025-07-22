package com.gig.collide.auth.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.gig.collide.base.exception.AuthErrorCode;
import com.gig.collide.base.exception.BizException;
import com.gig.collide.web.vo.Result;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Token控制器
 * 
 * @author GIGOpenTeam
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("token")
public class TokenController {

    @GetMapping("/get")
    public Result<String> get(@NotBlank String scene, @NotBlank String key) {
        log.info("Token请求，场景：{}，键：{}", scene, key);
        
        if (StpUtil.isLogin()) {
            String userId = StpUtil.getLoginId().toString();
            // 简化的token生成逻辑
            String tokenValue = "token_" + scene + "_" + userId + "_" + key + "_" + System.currentTimeMillis();
            
            log.info("生成Token成功，用户ID：{}", userId);
            return Result.success(tokenValue);
        }
        
        log.warn("用户未登录，无法获取Token");
        throw new BizException(AuthErrorCode.NOT_LOGGED_IN);
    }

    /**
     * 验证Token（简化版）
     */
    @GetMapping("/verify")
    public Result<Boolean> verify(@NotBlank String token) {
        log.info("验证Token：{}", token);
        
        if (StpUtil.isLogin()) {
            return Result.success(true);
        }
        
        return Result.success(false);
    }
}
