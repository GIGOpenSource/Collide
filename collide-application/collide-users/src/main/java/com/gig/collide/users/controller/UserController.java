package com.gig.collide.users.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import com.gig.collide.api.user.constant.UserRole;
import com.gig.collide.api.user.constant.UserStateEnum;
import com.gig.collide.api.user.request.UserModifyRequest;
import com.gig.collide.api.user.response.data.UserInfo;
import com.gig.collide.users.domain.entity.User;
import com.gig.collide.users.domain.entity.UserProfile;
import com.gig.collide.users.domain.entity.convertor.UserConvertor;
import com.gig.collide.users.domain.service.UserDomainService;
import com.gig.collide.users.infrastructure.exception.UserBusinessException;
import com.gig.collide.web.vo.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 用户控制器
 *
 * @author GIG
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserDomainService userDomainService;

    /**
     * 获取当前用户信息
     *
     * @return 用户信息
     */
    @GetMapping("/me")
    @SaCheckLogin
    public Result<UserInfo> getCurrentUser() {
        try {
            Long userId = StpUtil.getLoginIdAsLong();
            User user = userDomainService.getUserById(userId);
            UserProfile profile = userDomainService.getUserProfile(userId);
            UserInfo userInfo = convertToUserInfo(user, profile);
            return Result.success(userInfo);
        } catch (UserBusinessException e) {
            log.error("获取当前用户信息失败：{}", e.getMessage());
            return Result.error(e.getErrorCode().getCode(), e.getErrorCode().getMessage());
        } catch (Exception e) {
            log.error("获取当前用户信息异常", e);
            return Result.error("SYSTEM_ERROR", "系统异常，请稍后重试");
        }
    }

    /**
     * 根据用户ID获取用户信息
     *
     * @param userId 用户ID
     * @return 用户信息
     */
    @GetMapping("/{userId}")
    public Result<UserInfo> getUserById(@PathVariable Long userId) {
        try {
            User user = userDomainService.getUserById(userId);
            UserProfile profile = userDomainService.getUserProfile(userId);
            UserInfo userInfo = convertToUserInfo(user, profile);
            return Result.success(userInfo);
        } catch (UserBusinessException e) {
            log.error("获取用户信息失败，用户ID：{}，错误：{}", userId, e.getMessage());
            return Result.error(e.getErrorCode().getCode(), e.getErrorCode().getMessage());
        } catch (Exception e) {
            log.error("获取用户信息异常，用户ID：{}", userId, e);
            return Result.error("SYSTEM_ERROR", "系统异常，请稍后重试");
        }
    }

    /**
     * 更新用户信息
     *
     * @param updateRequest 更新请求
     * @return 更新后的用户信息
     */
    @PutMapping("/me")
    @SaCheckLogin
    public Result<UserInfo> updateUserInfo(@Valid @RequestBody UserModifyRequest updateRequest) {
        try {
            Long userId = StpUtil.getLoginIdAsLong();
            // TODO: 实现用户信息更新逻辑
            User user = userDomainService.getUserById(userId);
            UserProfile profile = userDomainService.getUserProfile(userId);
            UserInfo userInfo = convertToUserInfo(user, profile);
            return Result.success(userInfo);
        } catch (UserBusinessException e) {
            log.error("更新用户信息失败：{}", e.getMessage());
            return Result.error(e.getErrorCode().getCode(), e.getErrorCode().getMessage());
        } catch (Exception e) {
            log.error("更新用户信息异常", e);
            return Result.error("SYSTEM_ERROR", "系统异常，请稍后重试");
        }
    }

    /**
     * 申请博主认证
     *
     * @return 申请结果
     */
    @PostMapping("/blogger/apply")
    @SaCheckLogin
    public Result<String> applyForBlogger() {
        try {
            Long userId = StpUtil.getLoginIdAsLong();
            String result = userDomainService.applyForBlogger(userId);
            return Result.success(result);
        } catch (UserBusinessException e) {
            log.error("申请博主认证失败：{}", e.getMessage());
            return Result.error(e.getErrorCode().getCode(), e.getErrorCode().getMessage());
        } catch (Exception e) {
            log.error("申请博主认证异常", e);
            return Result.error("SYSTEM_ERROR", "系统异常，请稍后重试");
        }
    }

    /**
     * 转换为UserInfo对象 - 使用MapStruct转换器
     */
    private UserInfo convertToUserInfo(User user, UserProfile profile) {
        return UserConvertor.INSTANCE.mapToUserInfo(user, profile);
    }
}
