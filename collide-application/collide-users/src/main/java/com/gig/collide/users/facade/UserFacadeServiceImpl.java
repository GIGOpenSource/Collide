package com.gig.collide.users.facade;

import com.gig.collide.api.user.UserFacadeService;
import com.gig.collide.api.user.request.UserCreateRequest;
import com.gig.collide.api.user.request.UserQueryRequest;
import com.gig.collide.api.user.request.UserUpdateRequest;
import com.gig.collide.api.user.request.WalletOperationRequest;
import com.gig.collide.api.user.response.UserResponse;
import com.gig.collide.api.user.response.WalletResponse;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.users.domain.entity.User;
import com.gig.collide.users.domain.entity.UserWallet;
import com.gig.collide.users.domain.service.UserService;
import com.gig.collide.users.domain.service.WalletService;
import com.gig.collide.web.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.apache.dubbo.config.annotation.DubboService;
import com.alicp.jetcache.anno.Cached;
import com.alicp.jetcache.anno.CacheInvalidate;
import com.alicp.jetcache.anno.CacheUpdate;
import com.alicp.jetcache.anno.CacheType;
import com.gig.collide.users.infrastructure.cache.UserCacheConstant;

import java.util.List;
import java.util.stream.Collectors;
import java.util.concurrent.TimeUnit;

/**
 * 用户门面服务实现 - 简洁版
 * Dubbo独立微服务提供者
 * 
 * @author GIG Team
 * @version 2.0.0 (Dubbo微服务版)
 */
@Slf4j
@DubboService(version = "1.0.0", interfaceClass = UserFacadeService.class)
public class UserFacadeServiceImpl implements UserFacadeService {

    @Autowired
    private UserService userService;

    @Autowired
    private WalletService walletService;

    @Override
    @CacheInvalidate(name = UserCacheConstant.USER_LIST_CACHE)
    public Result<Void> createUser(UserCreateRequest request) {
        try {
            log.info("创建用户请求: {}", request.getUsername());
            
            User user = new User();
            BeanUtils.copyProperties(request, user);
            
            // 手动映射特殊字段
            user.setPasswordHash(request.getPassword()); // password -> passwordHash
            user.setStatus("active");
            
            User savedUser = userService.createUser(user);
            
            log.info("用户创建成功: ID={}", savedUser.getId());
            return Result.success(null);
        } catch (Exception e) {
            log.error("创建用户失败", e);
            return Result.error("USER_CREATE_ERROR", "创建用户失败: " + e.getMessage());
        }
    }

    @Override
    @CacheUpdate(name = UserCacheConstant.USER_DETAIL_CACHE,
                 key = UserCacheConstant.USER_DETAIL_KEY,
                 value = "#result.data")
    @CacheInvalidate(name = UserCacheConstant.USER_LIST_CACHE)
    @CacheInvalidate(name = UserCacheConstant.USER_USERNAME_CACHE)
    public Result<UserResponse> updateUser(UserUpdateRequest request) {
        try {
            log.info("更新用户请求: ID={}", request.getId());
            
            User user = userService.getUserById(request.getId());
            if (user == null) {
                return Result.error("USER_NOT_FOUND", "用户不存在");
            }
            
            BeanUtils.copyProperties(request, user);
            User updatedUser = userService.updateUser(user);
            UserResponse response = convertToResponse(updatedUser);
            
            log.info("用户更新成功: ID={}", updatedUser.getId());
            return Result.success(response);
        } catch (Exception e) {
            log.error("更新用户失败", e);
            return Result.error("USER_UPDATE_ERROR", "更新用户失败: " + e.getMessage());
        }
    }

    @Override
    @Cached(name = UserCacheConstant.USER_DETAIL_CACHE,
            key = UserCacheConstant.USER_DETAIL_KEY,
            expire = UserCacheConstant.USER_DETAIL_EXPIRE,
            timeUnit = TimeUnit.MINUTES,
            cacheType = CacheType.BOTH)
    public Result<UserResponse> getUserById(Long userId) {
        try {
            log.debug("获取用户详情: ID={}", userId);
            
            User user = userService.getUserById(userId);
            if (user == null) {
                return Result.error("USER_NOT_FOUND", "用户不存在");
            }
            
            UserResponse response = convertToResponse(user);
            return Result.success(response);
        } catch (Exception e) {
            log.error("查询用户失败", e);
            return Result.error("USER_NOT_FOUND","查询用户失败: " + e.getMessage());
        }
    }

    @Override
    @Cached(name = UserCacheConstant.USER_USERNAME_CACHE,
            key = UserCacheConstant.USERNAME_KEY,
            expire = UserCacheConstant.USERNAME_EXPIRE,
            timeUnit = TimeUnit.MINUTES,
            cacheType = CacheType.BOTH)
    public Result<UserResponse> getUserByUsername(String username) {
        try {
            log.debug("根据用户名获取用户: username={}", username);
            
            User user = userService.getUserByUsername(username);
            if (user == null) {
                return Result.error("USER_NOT_FOUND", "用户不存在");
            }
            
            UserResponse response = convertToResponse(user);
            return Result.success(response);
        } catch (Exception e) {
            log.error("查询用户失败", e);
            return Result.error("USER_NOT_FOUND","查询用户失败: " + e.getMessage());
        }
    }

    @Override
    public Result<UserResponse> getUserProfile(Long userId) {
        try {
            log.debug("获取用户个人信息: ID={}", userId);
            
            // 获取基础用户信息
            User user = userService.getUserById(userId);
            if (user == null) {
                return Result.error("USER_NOT_FOUND", "用户不存在");
            }
            
            UserResponse response = convertToResponse(user);
            
            // 增强用户信息 - 获取钱包信息
            try {
                UserWallet wallet = walletService.getWalletByUserId(userId);
                if (wallet != null) {
                    response.setWalletBalance(wallet.getBalance());
                    response.setWalletFrozen(wallet.getFrozenAmount());
                    response.setWalletStatus(wallet.getStatus());
                }
            } catch (Exception e) {
                log.warn("获取钱包信息失败: userId={}, error={}", userId, e.getMessage());
                // 不影响主流程，继续返回用户信息
            }
            
            log.debug("用户个人信息获取成功: userId={}", userId);
            return Result.success(response);
        } catch (Exception e) {
            log.error("获取用户个人信息失败", e);
            return Result.error("USER_PROFILE_ERROR", "获取用户个人信息失败: " + e.getMessage());
        }
    }

    @Override
    @Cached(name = UserCacheConstant.USER_LIST_CACHE,
            key = UserCacheConstant.USER_LIST_KEY,
            expire = UserCacheConstant.USER_LIST_EXPIRE,
            timeUnit = TimeUnit.MINUTES,
            cacheType = CacheType.BOTH)
    public Result<PageResponse<UserResponse>> queryUsers(UserQueryRequest request) {
        try {
            log.debug("分页查询用户列表: currentPage={}, pageSize={}", request.getCurrentPage(), request.getPageSize());
            
            PageResponse<User> pageResult = userService.queryUsers(request);
            
            List<UserResponse> responses = pageResult.getDatas().stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());
            
            PageResponse<UserResponse> result = new PageResponse<>();
            result.setDatas(responses);
            result.setCurrentPage(pageResult.getCurrentPage());
            result.setPageSize(pageResult.getPageSize());
            result.setTotal(pageResult.getTotal());
            
            return Result.success(result);
        } catch (Exception e) {
            log.error("查询用户列表失败", e);
            return Result.error("USER_LIST_QUERY_ERROR", "查询用户列表失败: " + e.getMessage());
        }
    }

    @Override
    public Result<UserResponse> login(String username, String password) {
        try {
            User user = userService.login(username, password);
            if (user == null) {
                return Result.error("LOGIN_FAILED", "用户名或密码错误");
            }
            
            UserResponse response = convertToResponse(user);
            return Result.success(response);
        } catch (Exception e) {
            log.error("用户登录失败", e);
            return Result.error("USER_LOGIN_ERROR", "登录失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Void> updateUserStatus(Long userId, String status) {
        try {
            userService.updateUserStatus(userId, status);
            return Result.success(null);
        } catch (Exception e) {
            log.error("更新用户状态失败", e);
            return Result.error("USER_STATUS_UPDATE_ERROR", "更新用户状态失败: " + e.getMessage());
        }
    }

    @Override
    @CacheInvalidate(name = UserCacheConstant.USER_DETAIL_CACHE)
    @CacheInvalidate(name = UserCacheConstant.USER_LIST_CACHE)
    @CacheInvalidate(name = UserCacheConstant.USER_USERNAME_CACHE)
    @CacheInvalidate(name = UserCacheConstant.WALLET_DETAIL_CACHE)
    @CacheInvalidate(name = UserCacheConstant.WALLET_BALANCE_CACHE)
    public Result<Void> deleteUser(Long userId) {
        try {
            log.info("删除用户请求: ID={}", userId);
            
            userService.deleteUser(userId);
            
            log.info("用户删除成功: ID={}", userId);
            return Result.success(null);
        } catch (Exception e) {
            log.error("删除用户失败", e);
            return Result.error("USER_DELETE_ERROR", "删除用户失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Void> updateUserStats(Long userId, String statsType, Integer increment) {
        try {
            userService.updateUserStats(userId, statsType, increment);
            return Result.success(null);
        } catch (Exception e) {
            log.error("更新用户统计失败", e);
            return Result.error("USER_STATS_UPDATE_ERROR", "更新用户统计失败: " + e.getMessage());
        }
    }

    // =================== 钱包管理功能实现 ===================

    @Override
    @Cached(name = UserCacheConstant.WALLET_DETAIL_CACHE,
            key = UserCacheConstant.WALLET_DETAIL_KEY,
            expire = UserCacheConstant.WALLET_DETAIL_EXPIRE,
            timeUnit = TimeUnit.MINUTES,
            cacheType = CacheType.BOTH)
    public Result<WalletResponse> getUserWallet(Long userId) {
        try {
            log.debug("获取用户钱包: userId={}", userId);
            
            UserWallet wallet = walletService.getWalletByUserId(userId);
            if (wallet == null) {
                // 自动创建钱包
                wallet = walletService.createWallet(userId);
            }
            WalletResponse response = convertToWalletResponse(wallet);
            return Result.success(response);
        } catch (Exception e) {
            log.error("获取用户钱包失败", e);
            return Result.error("WALLET_GET_ERROR", "获取用户钱包失败: " + e.getMessage());
        }
    }

    @Override
    public Result<WalletResponse> createUserWallet(Long userId) {
        try {
            UserWallet wallet = walletService.createWallet(userId);
            WalletResponse response = convertToWalletResponse(wallet);
            return Result.success(response);
        } catch (Exception e) {
            log.error("创建用户钱包失败", e);
            return Result.error("WALLET_CREATE_ERROR", "创建用户钱包失败: " + e.getMessage());
        }
    }

    @Override
    @CacheInvalidate(name = UserCacheConstant.WALLET_DETAIL_CACHE)
    @CacheInvalidate(name = UserCacheConstant.WALLET_BALANCE_CACHE)
    public Result<WalletResponse> walletOperation(WalletOperationRequest request) {
        try {
            UserWallet wallet;
            String operationType = request.getOperationType();
            
            switch (operationType) {
                case "recharge":
                    wallet = walletService.recharge(request.getUserId(), request.getAmount(), 
                                                  request.getDescription());
                    break;
                case "withdraw":
                    wallet = walletService.withdraw(request.getUserId(), request.getAmount(), 
                                                  request.getDescription());
                    break;
                case "freeze":
                    wallet = walletService.freezeAmount(request.getUserId(), request.getAmount(), 
                                                      request.getDescription());
                    break;
                case "unfreeze":
                    wallet = walletService.unfreezeAmount(request.getUserId(), request.getAmount(), 
                                                        request.getDescription());
                    break;
                default:
                    return Result.error("INVALID_OPERATION", "不支持的操作类型: " + operationType);
            }
            
            WalletResponse response = convertToWalletResponse(wallet);
            return Result.success(response);
        } catch (Exception e) {
            log.error("钱包操作失败", e);
            return Result.error("WALLET_OPERATION_ERROR", "钱包操作失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Boolean> checkWalletBalance(Long userId, java.math.BigDecimal amount) {
        try {
            boolean sufficient = walletService.checkBalance(userId, amount);
            return Result.success(sufficient);
        } catch (Exception e) {
            log.error("检查钱包余额失败", e);
            return Result.error("WALLET_CHECK_ERROR", "检查钱包余额失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Void> deductWalletBalance(Long userId, java.math.BigDecimal amount, String businessId, String description) {
        try {
            boolean success = walletService.deductBalance(userId, amount, businessId, description);
            if (success) {
                return Result.success(null);
            } else {
                return Result.error("WALLET_DEDUCT_ERROR", "扣款失败：余额不足或钱包状态异常");
            }
        } catch (Exception e) {
            log.error("钱包扣款失败", e);
            return Result.error("WALLET_DEDUCT_ERROR", "钱包扣款失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Void> addWalletBalance(Long userId, java.math.BigDecimal amount, String businessId, String description) {
        try {
            boolean success = walletService.addBalance(userId, amount, businessId, description);
            if (success) {
                return Result.success(null);
            } else {
                return Result.error("WALLET_ADD_ERROR", "充值失败：钱包状态异常");
            }
        } catch (Exception e) {
            log.error("钱包充值失败", e);
            return Result.error("WALLET_ADD_ERROR", "钱包充值失败: " + e.getMessage());
        }
    }

    /**
     * 转换为响应对象
     */
    private UserResponse convertToResponse(User user) {
        UserResponse response = new UserResponse();
        BeanUtils.copyProperties(user, response);
        return response;
    }

    /**
     * 转换为钱包响应对象
     */
    private WalletResponse convertToWalletResponse(UserWallet wallet) {
        WalletResponse response = new WalletResponse();
        BeanUtils.copyProperties(wallet, response);
        // 计算可用余额
        response.setAvailableBalance(wallet.getAvailableBalance());
        return response;
    }
} 