package com.gig.collide.users.facade;

import com.gig.collide.api.user.UserFacadeService;
import com.gig.collide.api.user.request.main.UserCoreCreateRequest;
import com.gig.collide.api.user.request.main.UserCoreQueryRequest;
import com.gig.collide.api.user.request.main.UserCoreUpdateRequest;
import com.gig.collide.api.user.request.main.UserLoginRequest;
import com.gig.collide.api.user.response.main.UserCoreResponse;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.users.domain.entity.UserCore;
import com.gig.collide.users.domain.service.UserCoreService;
import com.gig.collide.users.infrastructure.cache.UserCacheConstant;
import com.gig.collide.web.vo.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.apache.dubbo.config.annotation.DubboService;
import com.alicp.jetcache.anno.Cached;
import com.alicp.jetcache.anno.CacheInvalidate;
import com.alicp.jetcache.anno.CacheUpdate;
import com.alicp.jetcache.anno.CacheType;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 用户核心门面服务实现 - 对应 t_user 表
 * Dubbo独立微服务提供者 - 负责用户基础信息和认证相关功能
 * 
 * @author GIG Team
 * @version 2.0.0 (Dubbo微服务版 - 6表架构)
 */
@Slf4j
@DubboService(version = "1.0.0", interfaceClass = UserFacadeService.class)
@RequiredArgsConstructor
public class UserCoreFacadeServiceImpl implements UserFacadeService {

    private final UserCoreService userCoreService;

    @Override
    @CacheInvalidate(name = UserCacheConstant.USER_CORE_LIST_CACHE)
    public Result<UserCoreResponse> createUser(UserCoreCreateRequest request) {
        try {
            log.info("创建用户核心信息请求: {}", request.getUsername());
            
            UserCore userCore = new UserCore();
            BeanUtils.copyProperties(request, userCore);
            
            // 手动映射特殊字段
            userCore.setPasswordHash(request.getPassword()); // password -> passwordHash
            
            UserCore savedUser = userCoreService.createUser(userCore);
            UserCoreResponse response = convertToResponse(savedUser);
            
            log.info("用户核心信息创建成功: ID={}", savedUser.getId());
            return Result.success(response);
        } catch (Exception e) {
            log.error("创建用户核心信息失败", e);
            return Result.error("USER_CREATE_ERROR", "创建用户失败: " + e.getMessage());
        }
    }

    @Override
    @CacheUpdate(name = UserCacheConstant.USER_CORE_DETAIL_CACHE,
                 key = UserCacheConstant.USER_CORE_DETAIL_KEY,
                 value = "#result.data")
    @CacheInvalidate(name = UserCacheConstant.USER_CORE_LIST_CACHE)
    @CacheInvalidate(name = UserCacheConstant.USER_CORE_USERNAME_CACHE)
    public Result<UserCoreResponse> updateUser(UserCoreUpdateRequest request) {
        try {
            log.info("更新用户核心信息请求: ID={}", request.getUserId());
            
            UserCore userCore = userCoreService.getUserById(request.getUserId());
            if (userCore == null) {
                return Result.error("USER_NOT_FOUND", "用户不存在");
            }
            
            BeanUtils.copyProperties(request, userCore);
            UserCore updatedUser = userCoreService.updateUser(userCore);
            UserCoreResponse response = convertToResponse(updatedUser);
            
            log.info("用户核心信息更新成功: ID={}", updatedUser.getId());
            return Result.success(response);
        } catch (Exception e) {
            log.error("更新用户核心信息失败", e);
            return Result.error("USER_UPDATE_ERROR", "更新用户失败: " + e.getMessage());
        }
    }

    @Override
    public Result<UserCoreResponse> login(UserLoginRequest request) {
        try {
            log.info("用户登录请求: loginId={}", request.getLoginId());
            
            UserCore user = userCoreService.login(
                request.getLoginId(), 
                request.getPassword(), 
                "127.0.0.1"  // 简化实现，固定IP
            );
            
            if (user == null) {
                return Result.error("LOGIN_FAILED", "用户名或密码错误");
            }
            
            UserCoreResponse response = convertToResponse(user);
            log.info("用户登录成功: userId={}", user.getId());
            return Result.success(response);
        } catch (Exception e) {
            log.error("用户登录失败", e);
            return Result.error("USER_LOGIN_ERROR", "登录失败: " + e.getMessage());
        }
    }

    @Override
    @Cached(name = UserCacheConstant.USER_CORE_DETAIL_CACHE,
            key = UserCacheConstant.USER_CORE_DETAIL_KEY,
            expire = UserCacheConstant.USER_CORE_DETAIL_EXPIRE,
            timeUnit = TimeUnit.MINUTES,
            cacheType = CacheType.BOTH)
    public Result<UserCoreResponse> getUserById(Long userId) {
        try {
            log.debug("获取用户核心信息: ID={}", userId);
            
            UserCore user = userCoreService.getUserById(userId);
            if (user == null) {
                return Result.error("USER_NOT_FOUND", "用户不存在");
            }
            
            UserCoreResponse response = convertToResponse(user);
            return Result.success(response);
        } catch (Exception e) {
            log.error("查询用户核心信息失败", e);
            return Result.error("USER_NOT_FOUND","查询用户失败: " + e.getMessage());
        }
    }

    @Override
    @Cached(name = UserCacheConstant.USER_CORE_USERNAME_CACHE,
            key = UserCacheConstant.USER_CORE_USERNAME_KEY,
            expire = UserCacheConstant.USER_CORE_USERNAME_EXPIRE,
            timeUnit = TimeUnit.MINUTES,
            cacheType = CacheType.BOTH)  
    public Result<UserCoreResponse> getUserByUsername(String username) {
        try {
            log.debug("根据用户名获取用户核心信息: username={}", username);
            
            UserCore user = userCoreService.getUserByUsername(username);
            if (user == null) {
                return Result.error("USER_NOT_FOUND", "用户不存在");
            }
            
            UserCoreResponse response = convertToResponse(user);
            return Result.success(response);
        } catch (Exception e) {
            log.error("查询用户核心信息失败", e);
            return Result.error("USER_NOT_FOUND","查询用户失败: " + e.getMessage());
        }
    }

    @Override
    public Result<UserCoreResponse> getUserByEmail(String email) {
        try {
            log.debug("根据邮箱获取用户核心信息: email={}", email);
            
            UserCore user = userCoreService.getUserByEmail(email);
            if (user == null) {
                return Result.error("USER_NOT_FOUND", "用户不存在");
            }
            
            UserCoreResponse response = convertToResponse(user);
            return Result.success(response);
        } catch (Exception e) {
            log.error("查询用户核心信息失败", e);
            return Result.error("USER_NOT_FOUND","查询用户失败: " + e.getMessage());
        }
    }

    @Override
    public Result<UserCoreResponse> getUserByPhone(String phone) {
        try {
            log.debug("根据手机号获取用户核心信息: phone={}", phone);
            
            UserCore user = userCoreService.getUserByPhone(phone);
            if (user == null) {
                return Result.error("USER_NOT_FOUND", "用户不存在");
            }
            
            UserCoreResponse response = convertToResponse(user);
            return Result.success(response);
        } catch (Exception e) {
            log.error("查询用户核心信息失败", e);
            return Result.error("USER_NOT_FOUND","查询用户失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Boolean> checkUsernameExists(String username) {
        try {
            boolean exists = userCoreService.checkUsernameExists(username);
            return Result.success(exists);
        } catch (Exception e) {
            log.error("检查用户名是否存在失败", e);
            return Result.error("CHECK_USERNAME_ERROR", "检查用户名失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Boolean> checkEmailExists(String email) {
        try {
            boolean exists = userCoreService.checkEmailExists(email);
            return Result.success(exists);
        } catch (Exception e) {
            log.error("检查邮箱是否存在失败", e);
            return Result.error("CHECK_EMAIL_ERROR", "检查邮箱失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Boolean> checkPhoneExists(String phone) {
        try {
            boolean exists = userCoreService.checkPhoneExists(phone);
            return Result.success(exists);
        } catch (Exception e) {
            log.error("检查手机号是否存在失败", e);
            return Result.error("CHECK_PHONE_ERROR", "检查手机号失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Void> updateUserStatus(Long userId, Integer status) {
        try {
            boolean success = userCoreService.updateUserStatus(userId, status);
            if (success) {
                return Result.success(null);
            } else {
                return Result.error("USER_STATUS_UPDATE_ERROR", "更新用户状态失败");
            }
        } catch (Exception e) {
            log.error("更新用户状态失败", e);
            return Result.error("USER_STATUS_UPDATE_ERROR", "更新用户状态失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Void> changePassword(Long userId, String oldPassword, String newPassword) {
        try {
            boolean success = userCoreService.changePassword(userId, oldPassword, newPassword);
            if (success) {
                return Result.success(null);
            } else {
                return Result.error("PASSWORD_CHANGE_ERROR", "修改密码失败：旧密码错误");
            }
        } catch (Exception e) {
            log.error("修改密码失败", e);
            return Result.error("PASSWORD_CHANGE_ERROR", "修改密码失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Void> resetPassword(Long userId, String newPassword) {
        try {
            boolean success = userCoreService.resetPassword(userId, newPassword);
            if (success) {
                return Result.success(null);
            } else {
                return Result.error("PASSWORD_RESET_ERROR", "重置密码失败");
            }
        } catch (Exception e) {
            log.error("重置密码失败", e);
            return Result.error("PASSWORD_RESET_ERROR", "重置密码失败: " + e.getMessage());
        }
    }

    @Override
    @Cached(name = UserCacheConstant.USER_CORE_LIST_CACHE,
            key = UserCacheConstant.USER_CORE_LIST_KEY,
            expire = UserCacheConstant.USER_CORE_LIST_EXPIRE,
            timeUnit = TimeUnit.MINUTES,
            cacheType = CacheType.BOTH)
    public Result<PageResponse<UserCoreResponse>> queryUsers(UserCoreQueryRequest request) {
        try {
            log.debug("分页查询用户核心信息: currentPage={}, pageSize={}", request.getCurrentPage(), request.getPageSize());
            
            PageResponse<UserCore> pageResult = userCoreService.queryUsers(request);
            
            List<UserCoreResponse> responses = pageResult.getDatas().stream()
                    .map(this::convertToResponse)
                    .toList();
            
            PageResponse<UserCoreResponse> result = new PageResponse<>();
            result.setDatas(responses);
            result.setCurrentPage(pageResult.getCurrentPage());
            result.setPageSize(pageResult.getPageSize());
            result.setTotal(pageResult.getTotal());
            
            return Result.success(result);
        } catch (Exception e) {
            log.error("查询用户核心信息列表失败", e);
            return Result.error("USER_LIST_QUERY_ERROR", "查询用户列表失败: " + e.getMessage());
        }
    }

    @Override
    public Result<List<UserCoreResponse>> batchGetUsers(List<Long> userIds) {
        try {
            List<UserCore> users = userCoreService.batchGetUsers(userIds);
            List<UserCoreResponse> responses = users.stream()
                    .map(this::convertToResponse)
                    .toList();
            
            return Result.success(responses);
        } catch (Exception e) {
            log.error("批量查询用户核心信息失败", e);
            return Result.error("BATCH_GET_USERS_ERROR", "批量查询用户失败: " + e.getMessage());
        }
    }

    @Override
    @CacheInvalidate(name = UserCacheConstant.USER_CORE_DETAIL_CACHE)
    @CacheInvalidate(name = UserCacheConstant.USER_CORE_LIST_CACHE)
    @CacheInvalidate(name = UserCacheConstant.USER_CORE_USERNAME_CACHE)
    public Result<Void> deleteUser(Long userId) {
        try {
            log.info("删除用户核心信息请求: ID={}", userId);
            
            boolean success = userCoreService.deleteUser(userId);
            if (success) {
                log.info("用户核心信息删除成功: ID={}", userId);
                return Result.success(null);
            } else {
                return Result.error("USER_DELETE_ERROR", "删除用户失败");
            }
        } catch (Exception e) {
            log.error("删除用户核心信息失败", e);
            return Result.error("USER_DELETE_ERROR", "删除用户失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Boolean> verifyPassword(Long userId, String password) {
        try {
            boolean valid = userCoreService.verifyPassword(userId, password);
            return Result.success(valid);
        } catch (Exception e) {
            log.error("验证密码失败", e);
            return Result.error("PASSWORD_VERIFY_ERROR", "验证密码失败: " + e.getMessage());
        }
    }

    /**
     * 转换为核心用户响应对象
     */
    private UserCoreResponse convertToResponse(UserCore userCore) {
        UserCoreResponse response = new UserCoreResponse();
        BeanUtils.copyProperties(userCore, response);
        return response;
    }
}