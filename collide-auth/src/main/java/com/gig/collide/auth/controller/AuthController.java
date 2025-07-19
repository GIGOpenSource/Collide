package com.gig.collide.auth.controller;

import cn.dev33.satoken.stp.SaLoginModel;
import cn.dev33.satoken.stp.StpUtil;
import com.gig.collide.api.chain.service.ChainFacadeService;
import com.gig.collide.api.notice.response.NoticeResponse;
import com.gig.collide.api.notice.service.NoticeFacadeService;
import com.gig.collide.api.user.request.UserQueryRequest;
import com.gig.collide.api.user.request.UserRegisterRequest;
import com.gig.collide.api.user.request.UserUserNameQueryRequest;
import com.gig.collide.api.user.request.UserUserNameRegisterRequest;
import com.gig.collide.api.user.response.UserOperatorResponse;
import com.gig.collide.api.user.response.UserQueryResponse;
import com.gig.collide.api.user.response.data.UserInfo;
import com.gig.collide.api.user.service.UserFacadeService;
import com.gig.collide.auth.exception.AuthException;
import com.gig.collide.auth.param.LoginParam;
import com.gig.collide.auth.param.RegisterParam;
import com.gig.collide.auth.param.UserNameLoginParam;
import com.gig.collide.auth.param.UserNameRegisterParam;
import com.gig.collide.auth.vo.LoginVO;
import com.gig.collide.base.validator.IsMobile;
import com.gig.collide.web.vo.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static com.gig.collide.api.notice.constant.NoticeConstant.CAPTCHA_KEY_PREFIX;
import static com.gig.collide.auth.exception.AuthErrorCode.VERIFICATION_CODE_WRONG;

/**
 * 认证相关接口
 *
 * @author GIGOpenTeam
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("auth")
public class AuthController {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @DubboReference(version = "1.0.0")
    private UserFacadeService userFacadeService;

    @DubboReference(version = "1.0.0")
    private NoticeFacadeService noticeFacadeService;

    @DubboReference(version = "1.0.0")
    private ChainFacadeService chainFacadeService;

    private static final String ROOT_CAPTCHA = "8888";

    /**
     * 默认登录超时时间：7天
     */
    private static final Integer DEFAULT_LOGIN_SESSION_TIMEOUT = 60 * 60 * 24 * 7;

    @GetMapping("/sendCaptcha")
    public Result<Boolean> sendCaptcha(@IsMobile String telephone) {
        NoticeResponse noticeResponse = noticeFacadeService.generateAndSendSmsCaptcha(telephone);
        return Result.success(noticeResponse.getSuccess());
    }

    @PostMapping("/register")
    public Result<Boolean> register(@Valid @RequestBody RegisterParam registerParam) {

        //验证码校验
        String cachedCode = redisTemplate.opsForValue().get(CAPTCHA_KEY_PREFIX + registerParam.getTelephone());
        if (!StringUtils.equalsIgnoreCase(cachedCode, registerParam.getCaptcha())) {
            throw new AuthException(VERIFICATION_CODE_WRONG);
        }

        //注册
        UserRegisterRequest userRegisterRequest = new UserRegisterRequest();
        userRegisterRequest.setTelephone(registerParam.getTelephone());
        userRegisterRequest.setInviteCode(registerParam.getInviteCode());

        UserOperatorResponse registerResult = userFacadeService.register(userRegisterRequest);
        if(registerResult.getSuccess()){
            return Result.success(true);
        }
        return Result.error(registerResult.getResponseCode(), registerResult.getResponseMessage());
    }

    @PostMapping("/username/register")
    public Result<Boolean> userNameRegister(@Valid @RequestBody UserNameRegisterParam registerParam) {

        //注册
        UserUserNameRegisterRequest userUserNameRegisterRequest = new UserUserNameRegisterRequest();
        userUserNameRegisterRequest.setUserName(registerParam.getUserName());
        userUserNameRegisterRequest.setPassword(registerParam.getPassword());
        userUserNameRegisterRequest.setInviteCode(registerParam.getInviteCode());

        UserOperatorResponse registerResult = userFacadeService.userNameRegister(userUserNameRegisterRequest);
        if(registerResult.getSuccess()){
            return Result.success(true);
        }
        return Result.error(registerResult.getResponseCode(), registerResult.getResponseMessage());
    }

    /**
     * 登录方法
     *
     * @param loginParam 登录信息
     * @return 结果
     */
    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody LoginParam loginParam) {

        //查询用户信息
        UserQueryRequest userQueryRequest = new UserQueryRequest(loginParam.getTelephone());
        UserQueryResponse<UserInfo> userQueryResponse = userFacadeService.query(userQueryRequest);
        UserInfo userInfo = userQueryResponse.getData();
        if (userInfo == null) {
            //需要注册
            UserRegisterRequest userRegisterRequest = new UserRegisterRequest();
            userRegisterRequest.setTelephone(loginParam.getTelephone());
            userRegisterRequest.setInviteCode(loginParam.getInviteCode());

            UserOperatorResponse response = userFacadeService.register(userRegisterRequest);
            if (response.getSuccess()) {
                userQueryResponse = userFacadeService.query(userQueryRequest);
                userInfo = userQueryResponse.getData();
                StpUtil.login(userInfo.getUserId(), new SaLoginModel().setIsLastingCookie(loginParam.getRememberMe())
                        .setTimeout(DEFAULT_LOGIN_SESSION_TIMEOUT));
                StpUtil.getSession().set(userInfo.getUserId().toString(), userInfo);
                LoginVO loginVO = new LoginVO(userInfo);
                return Result.success(loginVO);
            }

            return Result.error(response.getResponseCode(), response.getResponseMessage());
        } else {
            //登录
            StpUtil.login(userInfo.getUserId(), new SaLoginModel().setIsLastingCookie(loginParam.getRememberMe())
                    .setTimeout(DEFAULT_LOGIN_SESSION_TIMEOUT));
            StpUtil.getSession().set(userInfo.getUserId().toString(), userInfo);
            LoginVO loginVO = new LoginVO(userInfo);
            return Result.success(loginVO);
        }
    }

    @PostMapping("/username/login")
    public Result<LoginVO> userNameLogin(@Valid @RequestBody UserNameLoginParam userNameLoginParam) {

        //判断是注册还是登陆
        //查询用户信息
        UserUserNameQueryRequest userUserNameQueryRequest = new UserUserNameQueryRequest(userNameLoginParam.getUserName());
        UserQueryResponse<UserInfo> userQueryResponse = userFacadeService.queryByUserName(userUserNameQueryRequest);
        UserInfo userInfo = userQueryResponse.getData();
        if (userInfo == null) {
            //需要注册
            UserUserNameRegisterRequest userUserNameRegisterRequest = new UserUserNameRegisterRequest();
            userUserNameRegisterRequest.setUserName(userNameLoginParam.getUserName());
            userUserNameRegisterRequest.setPassword(userNameLoginParam.getPassword());
            userUserNameRegisterRequest.setInviteCode(userNameLoginParam.getInviteCode());

            UserOperatorResponse response = userFacadeService.userNameRegister(userUserNameRegisterRequest);
            if (response.getSuccess()) {
                userQueryResponse = userFacadeService.queryByUserName(userUserNameQueryRequest);
                userInfo = userQueryResponse.getData();
                StpUtil.login(userInfo.getUserId(), new SaLoginModel().setIsLastingCookie(userNameLoginParam.getRememberMe())
                        .setTimeout(DEFAULT_LOGIN_SESSION_TIMEOUT));
                StpUtil.getSession().set(userInfo.getUserId().toString(), userInfo);
                LoginVO loginVO = new LoginVO(userInfo);
                return Result.success(loginVO);
            }

            return Result.error(response.getResponseCode(), response.getResponseMessage());
        } else {
            //登录
            StpUtil.login(userInfo.getUserId(), new SaLoginModel().setIsLastingCookie(userNameLoginParam.getRememberMe())
                    .setTimeout(DEFAULT_LOGIN_SESSION_TIMEOUT));
            StpUtil.getSession().set(userInfo.getUserId().toString(), userInfo);
            LoginVO loginVO = new LoginVO(userInfo);
            return Result.success(loginVO);
        }
    }

    @PostMapping("/logout")
    public Result<Boolean> logout() {
        StpUtil.logout();
        return Result.success(true);
    }

    @RequestMapping("test")
    public String test() {
        return "test";
    }

}

