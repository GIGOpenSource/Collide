package com.gig.collide.users.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import com.gig.collide.api.user.constant.UserRole;
import com.gig.collide.api.user.constant.UserStateEnum;
import com.gig.collide.api.user.request.UserActiveRequest;
import com.gig.collide.api.user.request.UserModifyRequest;
import com.gig.collide.api.user.request.UserPageQueryRequest;
import com.gig.collide.api.user.response.UserOperatorResponse;
import com.gig.collide.api.user.response.data.UserInfo;
import com.gig.collide.api.user.service.UserFacadeService;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.users.domain.entity.User;
import com.gig.collide.users.domain.entity.UserProfile;
import com.gig.collide.users.domain.entity.convertor.UserConvertor;
import com.gig.collide.users.domain.service.UserDomainService;
import com.gig.collide.users.infrastructure.exception.UserBusinessException;
import com.gig.collide.web.vo.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
@Tag(name = "用户管理", description = "用户相关接口")
public class UserController {

    @Autowired
    private UserDomainService userDomainService;
    
    @Autowired
    private UserFacadeService userFacadeService;

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
     * 分页查询用户列表
     *
     * @param pageNum         页码（从1开始）
     * @param pageSize        每页大小
     * @param usernameKeyword 用户名关键词
     * @param status          用户状态
     * @param role            用户角色
     * @return 用户分页列表
     */
    @GetMapping("/page")
    @Operation(summary = "分页查询用户列表", description = "支持按用户名、状态、角色等条件分页查询用户")
    public Result<PageResponse<UserInfo>> pageQueryUsers(
        @Parameter(description = "页码，从1开始", example = "1")
        @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
        
        @Parameter(description = "每页大小，最大100", example = "10")
        @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
        
        @Parameter(description = "用户名关键词", example = "张三")
        @RequestParam(value = "usernameKeyword", required = false) String usernameKeyword,
        
        @Parameter(description = "用户状态", example = "ACTIVE")
        @RequestParam(value = "status", required = false) String status,
        
        @Parameter(description = "用户角色", example = "USER")
        @RequestParam(value = "role", required = false) String role) {
        
        try {
            log.info("分页查询用户列表，页码：{}，页大小：{}，关键词：{}，状态：{}，角色：{}", 
                pageNum, pageSize, usernameKeyword, status, role);
            
            // 构建查询请求
            UserPageQueryRequest request = new UserPageQueryRequest();
            request.setPageNum(pageNum);
            request.setPageSize(pageSize);
            request.setUsernameKeyword(usernameKeyword);
            request.setStatus(status);
            request.setRole(role);
            
            // 调用Facade服务进行分页查询
            PageResponse<UserInfo> pageResponse = userFacadeService.pageQuery(request);
            
            log.info("分页查询用户列表完成，总记录数：{}，当前页记录数：{}", 
                pageResponse.getTotal(), pageResponse.getRecords().size());
            
            return Result.success(pageResponse);
            
        } catch (Exception e) {
            log.error("分页查询用户列表异常", e);
            return Result.error("SYSTEM_ERROR", "查询用户列表失败：" + e.getMessage());
                 }
     }

    /**
     * 发送激活码
     *
     * @param userId         用户ID
     * @param activationType 激活类型（EMAIL或SMS）
     * @return 发送结果
     */
    @PostMapping("/{userId}/send-activation-code")
    @Operation(summary = "发送激活码", description = "向用户邮箱或手机发送激活码")
    public Result<String> sendActivationCode(
        @Parameter(description = "用户ID", required = true)
        @PathVariable Long userId,
        
        @Parameter(description = "激活类型", example = "EMAIL")
        @RequestParam(value = "activationType", defaultValue = "EMAIL") String activationType) {
        
        try {
            log.info("发送激活码请求，用户ID：{}，激活类型：{}", userId, activationType);
            
            // 调用领域服务发送激活码
            String result = userDomainService.sendActivationCode(userId, activationType);
            
            log.info("激活码发送成功，用户ID：{}", userId);
            return Result.success(result);
            
        } catch (IllegalArgumentException e) {
            log.warn("发送激活码参数错误：{}", e.getMessage());
            return Result.error("PARAM_ERROR", e.getMessage());
        } catch (Exception e) {
            log.error("发送激活码异常，用户ID：{}", userId, e);
            return Result.error("SYSTEM_ERROR", "发送激活码失败：" + e.getMessage());
        }
    }

    /**
     * 激活用户账号
     *
     * @param request 激活请求
     * @return 激活结果
     */
    @PostMapping("/activate")
    @Operation(summary = "激活用户账号", description = "使用激活码激活用户账号")
    public Result<String> activateUser(@Valid @RequestBody UserActiveRequest request) {
        try {
            log.info("用户激活请求，用户ID：{}", request.getUserId());
            
            // 调用Facade服务激活用户
            UserOperatorResponse response = userFacadeService.active(request);
            
            if (response.getSuccess()) {
                log.info("用户激活成功，用户ID：{}", request.getUserId());
                return Result.success(response.getResponseMessage());
            } else {
                log.warn("用户激活失败，用户ID：{}，错误：{}", 
                    request.getUserId(), response.getResponseMessage());
                return Result.error(response.getResponseCode(), response.getResponseMessage());
            }
            
        } catch (Exception e) {
            log.error("用户激活异常，用户ID：{}", request.getUserId(), e);
            return Result.error("SYSTEM_ERROR", "用户激活失败：" + e.getMessage());
        }
    }

    /**
     * 检查用户激活状态
     *
     * @param userId 用户ID
     * @return 激活状态
     */
    @GetMapping("/{userId}/activation-status")
    @Operation(summary = "检查用户激活状态", description = "查询用户当前的激活状态")
    public Result<java.util.Map<String, Object>> getActivationStatus(@PathVariable Long userId) {
        try {
            log.info("查询用户激活状态，用户ID：{}", userId);
            
            // 查询用户信息
            User user = userDomainService.getUserById(userId);
            
            java.util.Map<String, Object> status = new java.util.HashMap<>();
            status.put("userId", userId);
            status.put("status", user.getStatus());
            status.put("isActive", user.getStatus() == UserStateEnum.ACTIVE);
            status.put("username", user.getUsername());
            status.put("email", user.getEmail());
            status.put("phone", user.getPhone());
            status.put("createTime", user.getCreateTime());
            
            return Result.success(status);
            
        } catch (Exception e) {
            log.error("查询用户激活状态异常，用户ID：{}", userId, e);
            return Result.error("SYSTEM_ERROR", "查询激活状态失败：" + e.getMessage());
        }
    }

    /**
     * 转换为UserInfo对象 - 使用MapStruct转换器
     */
    private UserInfo convertToUserInfo(User user, UserProfile profile) {
        return UserConvertor.INSTANCE.mapToUserInfo(user, profile);
    }
}
