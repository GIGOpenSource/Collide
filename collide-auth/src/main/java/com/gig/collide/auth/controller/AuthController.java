package com.gig.collide.auth.controller;

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
import com.gig.collide.auth.param.LoginParam;
import com.gig.collide.auth.param.RegisterParam;
import com.gig.collide.auth.vo.LoginVO;
import com.gig.collide.web.vo.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

/**
 * 认证相关接口
 * 参考 nft-turbo-auth 设计，支持用户名密码登录
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("auth")
public class AuthController {

    @DubboReference(version = "1.0.0", check = false)
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
    public Result<Boolean> register(@Valid @RequestBody RegisterParam registerParam) {
        log.info("用户注册请求，用户名：{}", registerParam.getUsername());

        try {
            // 检查用户名是否已存在
            UserQueryRequest queryRequest = new UserQueryRequest();
            UserUserNameQueryCondition condition = new UserUserNameQueryCondition();
            condition.setUserName(registerParam.getUsername());
            queryRequest.setUserUserNameQueryCondition(condition);
            UserQueryResponse<UserInfo> queryResponse = userFacadeService.query(queryRequest);
            
            if (queryResponse.getData() != null) {
                return Result.error("USER_EXISTS", "用户名已存在");
            }

            // 创建注册请求
            UserRegisterRequest userRegisterRequest = new UserRegisterRequest();
            userRegisterRequest.setUsername(registerParam.getUsername());
            userRegisterRequest.setPassword(passwordEncoder.encode(registerParam.getPassword()));
            userRegisterRequest.setEmail(registerParam.getEmail());
            userRegisterRequest.setPhone(registerParam.getPhone());
            
            UserOperatorResponse registerResult = userFacadeService.register(userRegisterRequest);
            
            if (registerResult.getSuccess()) {
                log.info("用户注册成功，用户名：{}", registerParam.getUsername());
                return Result.success(true);
            } else {
                return Result.error(registerResult.getResponseCode(), registerResult.getResponseMessage());
            }
            
        } catch (Exception e) {
            log.error("用户注册异常，用户名：{}", registerParam.getUsername(), e);
            return Result.error("REGISTER_ERROR", "注册失败，请稍后重试");
        }
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody LoginParam loginParam) {
        log.info("用户登录请求，用户名：{}", loginParam.getUsername());

        try {
            // 查询用户信息
            UserQueryRequest userQueryRequest = new UserQueryRequest();
            UserUserNameQueryCondition condition = new UserUserNameQueryCondition();
            condition.setUserName(loginParam.getUsername());
            userQueryRequest.setUserUserNameQueryCondition(condition);
            UserQueryResponse<UserInfo> userQueryResponse = userFacadeService.query(userQueryRequest);
            UserInfo userInfo = userQueryResponse.getData();
            
            if (userInfo == null) {
                throw new BizException(AuthErrorCode.USER_NOT_FOUND);
            }

            // 验证密码（这里需要从用户服务获取加密后的密码进行比对）
            // TODO: 需要用户服务提供密码验证接口
            // if (!passwordEncoder.matches(loginParam.getPassword(), userInfo.getPassword())) {
            //     throw new BizException(AuthErrorCode.PASSWORD_WRONG);
            // }

            // 执行登录
            StpUtil.login(userInfo.getUserId(), new SaLoginModel()
                .setIsLastingCookie(loginParam.getRememberMe())
                .setTimeout(DEFAULT_LOGIN_SESSION_TIMEOUT));
                
            // 将用户信息存储到会话中
            StpUtil.getSession().set(userInfo.getUserId().toString(), userInfo);
            
            LoginVO loginVO = new LoginVO(userInfo);
            
            log.info("用户登录成功，用户名：{}，用户ID：{}", loginParam.getUsername(), userInfo.getUserId());
            return Result.success(loginVO);
            
        } catch (BizException e) {
            log.error("用户登录失败，用户名：{}，错误：{}", loginParam.getUsername(), e.getMessage());
            return Result.error(e.getErrorCode().getCode(), e.getErrorCode().getMessage());
        } catch (Exception e) {
            log.error("用户登录异常，用户名：{}", loginParam.getUsername(), e);
            return Result.error("LOGIN_ERROR", "登录失败，请稍后重试");
        }
    }

    /**
     * 用户登出
     */
    @PostMapping("/logout")
    public Result<Boolean> logout() {
        try {
            StpUtil.logout();
            log.info("用户登出成功");
            return Result.success(true);
        } catch (Exception e) {
            log.error("用户登出异常", e);
            return Result.error("LOGOUT_ERROR", "登出失败");
        }
    }

    /**
     * 服务健康检查
     */
    @RequestMapping("test")
    public String test() {
        return "Collide Auth Service is running!";
    }
}

