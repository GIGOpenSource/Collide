package com.gig.collide.users.domain.service;

import com.gig.collide.api.user.constant.UserRole;
import com.gig.collide.api.user.constant.UserStateEnum;
import com.gig.collide.users.domain.entity.UserUnified;
import com.gig.collide.users.domain.repository.UserUnifiedRepository;
import com.gig.collide.api.user.request.UserModifyRequest;
import com.gig.collide.api.user.request.UserRegisterRequest;
import com.gig.collide.base.exception.BizException;
import com.gig.collide.users.infrastructure.exception.UserErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.alicp.jetcache.anno.Cached;
import com.alicp.jetcache.anno.CacheInvalidate;
import com.alicp.jetcache.anno.CacheRefresh;
import com.alicp.jetcache.anno.CacheType;
import com.gig.collide.cache.constant.CacheConstant;

import java.util.concurrent.TimeUnit;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.Random;
import java.util.List;
import java.util.Map;

/**
 * 用户领域服务
 * 实现用户相关的核心业务逻辑
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserDomainService {

    private final UserUnifiedRepository userUnifiedRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final SmsService smsService;
    private final ActivationCodeService activationCodeService;

    /**
     * 根据ID查询用户信息
     *
     * @param userId 用户ID
     * @return 用户实体
     * @throws UserErrorCode 用户不存在时抛出
     */
    @Cached(name = CacheConstant.USER_INFO_CACHE, 
            cacheType = CacheType.BOTH, 
            key = "#userId", 
            expire = CacheConstant.USER_CACHE_EXPIRE, 
            localExpire = CacheConstant.LOCAL_CACHE_EXPIRE,
            timeUnit = TimeUnit.MINUTES,
            cacheNullValue = true)
    @CacheRefresh(refresh = CacheConstant.USER_CACHE_EXPIRE, timeUnit = TimeUnit.MINUTES)
    public UserUnified getUserById(Long userId) {
        log.debug("从数据库查询用户信息，用户ID：{}", userId);
        return userUnifiedRepository.findById(userId)
                .orElseThrow(() -> new BizException(UserErrorCode.USER_NOT_FOUND));
    }

    /**
     * 根据用户名查询用户信息
     *
     * @param username 用户名
     * @return 用户实体
     * @throws BizException 用户不存在时抛出
     */
    @Cached(name = CacheConstant.USER_USERNAME_CACHE, 
            cacheType = CacheType.BOTH, 
            key = "#username", 
            expire = CacheConstant.USER_QUERY_CACHE_EXPIRE, 
            localExpire = CacheConstant.LOCAL_CACHE_EXPIRE,
            timeUnit = TimeUnit.MINUTES,
            cacheNullValue = true)
    public UserUnified getUserByUsername(String username) {
        log.debug("从数据库根据用户名查询用户信息：{}", username);
        return userUnifiedRepository.findByUsername(username)
                .orElseThrow(() -> new BizException(UserErrorCode.USER_NOT_FOUND));
    }

    /**
     * 根据手机号查询用户信息
     *
     * @param phone 手机号
     * @return 用户实体
     * @throws BizException 用户不存在时抛出
     */
    @Cached(name = CacheConstant.USER_PHONE_CACHE, 
            cacheType = CacheType.BOTH, 
            key = "#phone", 
            expire = CacheConstant.USER_QUERY_CACHE_EXPIRE, 
            localExpire = CacheConstant.LOCAL_CACHE_EXPIRE,
            timeUnit = TimeUnit.MINUTES,
            cacheNullValue = true)
    public UserUnified getUserByPhone(String phone) {
        log.debug("从数据库根据手机号查询用户信息：{}", phone);
        return userUnifiedRepository.findByPhone(phone)
                .orElseThrow(() -> new BizException(UserErrorCode.USER_NOT_FOUND));
    }

    /**
     * 根据邮箱查询用户信息
     *
     * @param email 邮箱
     * @return 用户实体
     * @throws BizException 用户不存在时抛出
     */
    @Cached(name = CacheConstant.USER_EMAIL_CACHE, 
            cacheType = CacheType.BOTH, 
            key = "#email", 
            expire = CacheConstant.USER_QUERY_CACHE_EXPIRE, 
            localExpire = CacheConstant.LOCAL_CACHE_EXPIRE,
            timeUnit = TimeUnit.MINUTES,
            cacheNullValue = true)
    public UserUnified getUserByEmail(String email) {
        log.debug("从数据库根据邮箱查询用户信息：{}", email);
        return userUnifiedRepository.findByEmail(email)
                .orElseThrow(() -> new BizException(UserErrorCode.USER_NOT_FOUND));
    }

    /**
     * 用户注册（已弃用，使用 simpleRegister 替代）
     * @deprecated 使用 simpleRegister(String username, String password, String inviteCode) 替代
     */
    @Deprecated
    @Transactional(rollbackFor = Exception.class)
    public UserUnified registerUser(UserRegisterRequest registerRequest) {
        // 兼容性保留，建议使用 simpleRegister 方法
        return simpleRegister(
            registerRequest.getUsername(),
            registerRequest.getPassword(),
            null // 旧接口不支持邀请码
        );
    }

    /**
     * 生成默认昵称
     *
     * @return 默认昵称
     */
    private String generateDefaultNickname() {
        return "用户" + System.currentTimeMillis();
    }

    /**
     * 更新用户基础信息
     *
     * @param userId        用户ID
     * @param updateRequest 更新请求
     * @return 更新后的用户实体
     */
    @Transactional(rollbackFor = Exception.class)
    @CacheInvalidate(name = CacheConstant.USER_INFO_CACHE, key = "#userId")
    @CacheInvalidate(name = CacheConstant.USER_PROFILE_CACHE, key = "#userId")
    public UserUnified updateUserInfo(Long userId, UserModifyRequest updateRequest) {
        UserUnified user = getUserById(userId);

        // 更新基础信息
        if (StringUtils.hasText(updateRequest.getNickname())) {
            user.setNickname(updateRequest.getNickname());
        }
        if (StringUtils.hasText(updateRequest.getAvatar())) {
            user.setAvatar(updateRequest.getAvatar());
        }
        if (StringUtils.hasText(updateRequest.getEmail())) {
            // 检查邮箱是否已被其他用户使用
            if (userUnifiedRepository.existsByEmail(updateRequest.getEmail()) && 
                !updateRequest.getEmail().equals(user.getEmail())) {
                throw new BizException(UserErrorCode.EMAIL_ALREADY_EXISTS);
            }
            user.setEmail(updateRequest.getEmail());
        }
        if (StringUtils.hasText(updateRequest.getPhone())) {
            // 检查手机号是否已被其他用户使用
            if (userUnifiedRepository.existsByPhone(updateRequest.getPhone()) && 
                !updateRequest.getPhone().equals(user.getPhone())) {
                throw new BizException(UserErrorCode.PHONE_ALREADY_EXISTS);
            }
            user.setPhone(updateRequest.getPhone());
        }

        // 更新扩展信息（原本在UserProfile中的字段）
        if (StringUtils.hasText(updateRequest.getBio())) {
            user.setBio(updateRequest.getBio());
        }
        if (StringUtils.hasText(updateRequest.getGender())) {
            // 验证性别值的合法性
            if (!updateRequest.getGender().matches("^(male|female|unknown)$")) {
                throw new BizException(UserErrorCode.INVALID_GENDER);
            }
            user.setGender(updateRequest.getGender());
        }
        if (updateRequest.getBirthday() != null) {
            user.setBirthday(updateRequest.getBirthday());
        }
        if (StringUtils.hasText(updateRequest.getLocation())) {
            user.setLocation(updateRequest.getLocation());
        }

        // 保存用户统一信息
        user = userUnifiedRepository.save(user);
        return user;
    }

    /**
     * 申请博主认证
     *
     * @param userId 用户ID
     * @return 申请结果信息
     */
    @Transactional(rollbackFor = Exception.class)
    public String applyForBlogger(Long userId) {
        UserUnified user = getUserById(userId);

        // 检查用户状态
        if (user.getStatus() != UserStateEnum.ACTIVE) {
            throw new BizException(UserErrorCode.USER_STATUS_INVALID);
        }

        // 检查当前博主认证状态
        String currentBloggerStatus = user.getBloggerStatus();
        switch (currentBloggerStatus) {
            case "approved":
                return "您已经是认证博主了";
            case "applying":
                return "您的博主申请正在审核中，请耐心等待";
            case "rejected":
                // 检查是否可以重新申请（距离上次申请是否超过30天）
                if (user.getBloggerApplyTime() != null &&
                        user.getBloggerApplyTime().isAfter(LocalDateTime.now().minusDays(30))) {
                    return "博主申请被拒绝后需等待30天才能重新申请";
                }
                break;
        }

        // 更新博主申请状态
        user.setBloggerStatus("applying");
        user.setBloggerApplyTime(LocalDateTime.now());
        userUnifiedRepository.save(user);

        log.info("用户 {} 申请博主认证", userId);
        return "博主认证申请已提交，我们将在3-5个工作日内完成审核";
    }

    /**
     * 取消博主申请
     *
     * @param userId 用户ID
     * @return 取消结果信息
     */
    @Transactional(rollbackFor = Exception.class)
    public String cancelBloggerApply(Long userId) {
        UserUnified user = getUserById(userId);

        // 检查当前博主认证状态
        if (!"applying".equals(user.getBloggerStatus())) {
            return "当前没有待审核的申请";
        }

        // 重置博主申请状态
        user.setBloggerStatus("none");
        user.setBloggerApplyTime(null);
        userUnifiedRepository.save(user);

        log.info("用户 {} 取消博主申请", userId);
        return "博主申请已取消";
    }

    /**
     * 检查用户是否为VIP
     *
     * @param userId 用户ID
     * @return 是否为VIP
     */
    public boolean isVipUser(Long userId) {
        UserUnified user = getUserById(userId);
        if (user.getRole() == UserRole.vip) {
            return user.getVipExpireTime() != null && user.getVipExpireTime().isAfter(LocalDateTime.now());
        }
        return false;
    }

    /**
     * 检查用户是否为博主
     *
     * @param userId 用户ID
     * @return 是否为博主
     */
    public boolean isBloggerUser(Long userId) {
        UserUnified user = getUserById(userId);
        return user.getRole() == UserRole.blogger && "approved".equals(user.getBloggerStatus());
    }

    /**
     * 更新用户最后登录时间
     *
     * @param userId 用户ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateLastLoginTime(Long userId) {
        UserUnified user = getUserById(userId);
        user.setLastLoginTime(LocalDateTime.now());
        userUnifiedRepository.save(user);
    }

    /**
     * 获取用户完整信息（包含扩展信息）
     * 在UserUnified统一架构中，用户基础信息和扩展信息已合并
     *
     * @param userId 用户ID
     * @return 用户完整信息
     */
    @Cached(name = CacheConstant.USER_PROFILE_CACHE, 
            cacheType = CacheType.BOTH, 
            key = "#userId", 
            expire = CacheConstant.USER_CACHE_EXPIRE, 
            localExpire = CacheConstant.LOCAL_CACHE_EXPIRE,
            timeUnit = TimeUnit.MINUTES,
            cacheNullValue = true)
    public UserUnified getUserProfile(Long userId) {
        log.debug("从数据库查询用户完整信息，用户ID：{}", userId);
        return userUnifiedRepository.findById(userId).orElse(null);
    }

    /**
     * 验证用户密码
     *
     * @param user     用户实体
     * @param password 原始密码
     * @return 是否匹配
     */
    public boolean validatePassword(UserUnified user, String password) {
        if (user.getPasswordHash() == null || password == null) {
            return false;
        }
        return passwordEncoder.matches(password, user.getPasswordHash());
    }

    /**
     * 分页查询用户列表
     *
     * @param pageNum         页码（从1开始）
     * @param pageSize        每页大小
     * @param usernameKeyword 用户名关键词
     * @param status          用户状态
     * @param role            用户角色
     * @return 分页结果
     */
    public com.baomidou.mybatisplus.core.metadata.IPage<UserUnified> pageQueryUsers(
            Integer pageNum, Integer pageSize, String usernameKeyword, 
            String status, String role) {
        
        log.info("分页查询用户，页码：{}，页大小：{}，关键词：{}，状态：{}，角色：{}", 
            pageNum, pageSize, usernameKeyword, status, role);

        // 参数验证
        if (pageNum == null || pageNum < 1) {
            pageNum = 1;
        }
        if (pageSize == null || pageSize < 1 || pageSize > 100) {
            pageSize = 10; // 默认每页10条，最大100条
        }

        // 调用Repository层进行分页查询
        return userUnifiedRepository.pageQuery(pageNum, pageSize, usernameKeyword, status, role);
    }

    /**
     * 生成用户激活码（已弃用，简化认证系统不再使用）
     * @deprecated 系统已简化为直接激活，不再需要激活码验证
     */
    @Deprecated
    public String generateActivationCode(Long userId) {
        log.warn("generateActivationCode 方法已弃用，系统已简化为直接激活模式");
        return "000000"; // 返回虚拟激活码以保持兼容性
    }

    /**
     * 验证激活码并激活用户（已弃用，简化认证系统不再使用）
     * @deprecated 系统已简化为注册时直接激活，不再需要激活码验证
     */
    @Deprecated
    @Transactional
    public String activateUser(Long userId, String activationCode) {
        log.warn("activateUser 方法已弃用，系统已简化为注册时直接激活");
        
        // 为了兼容性，确保用户是激活状态
        UserUnified user = getUserById(userId);
        if (user.getStatus() != UserStateEnum.ACTIVE) {
            user.setStatus(UserStateEnum.ACTIVE);
            userUnifiedRepository.save(user);
            return "用户已激活";
        }
        
        return "用户已经是激活状态";
    }

    /**
     * 发送激活码（已弃用，简化认证系统不再使用）
     * @deprecated 系统已简化为注册时直接激活，不再需要发送激活码
     */
    @Deprecated
    public String sendActivationCode(Long userId, String activationType) {
        log.warn("sendActivationCode 方法已弃用，系统已简化为注册时直接激活");
        
        // 为了兼容性，确保用户是激活状态
        UserUnified user = getUserById(userId);
        if (user.getStatus() != UserStateEnum.ACTIVE) {
            user.setStatus(UserStateEnum.ACTIVE);
            userUnifiedRepository.save(user);
        }
        
        return "系统已简化为直接激活模式，无需发送激活码";
    }

    /**
     * 验证激活码是否有效
     */
    private boolean isValidActivationCode(Long userId, String activationCode) {
        log.debug("验证用户{}的激活码", userId);
        
        // 使用ActivationCodeService验证并消费激活码
        ActivationCodeService.ActivationCodeValidationResult result = 
            activationCodeService.validateAndConsumeCode(userId, activationCode, ActivationCodeService.CodeType.ACCOUNT_ACTIVATION);
        
        if (!result.isValid()) {
            log.warn("激活码验证失败，用户ID：{}，原因：{}", userId, result.getMessage());
        }
        
        return result.isValid();
    }

    /**
     * 发送激活邮件
     */
    private boolean sendActivationEmail(String email, String activationCode) {
        try {
            log.info("发送激活邮件到：{}，激活码：{}", email, activationCode);
            return emailService.sendActivationEmail(email, activationCode);
        } catch (Exception e) {
            log.error("发送激活邮件失败：{}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * 发送激活短信
     */
    private boolean sendActivationSms(String phone, String activationCode) {
        try {
            log.info("发送激活短信到：{}，激活码：{}", phone, activationCode);
            return smsService.sendActivationSms(phone, activationCode);
        } catch (Exception e) {
            log.error("发送激活短信失败：{}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * 脱敏敏感信息
     */
    private String maskSensitiveInfo(String info, String type) {
        if (!StringUtils.hasText(info)) {
            return "";
        }
        
        if ("EMAIL".equals(type)) {
            // 邮箱脱敏：user***@example.com
            int atIndex = info.indexOf("@");
            if (atIndex > 0) {
                String username = info.substring(0, atIndex);
                String domain = info.substring(atIndex);
                return username.substring(0, Math.min(4, username.length())) + "***" + domain;
            }
        } else if ("SMS".equals(type)) {
            // 手机号脱敏：138****5678
            if (info.length() >= 7) {
                return info.substring(0, 3) + "****" + info.substring(info.length() - 4);
            }
        }
        
        return info;
    }

    /**
     * 检查用户名是否存在
     */
    public boolean checkUsernameExists(String username) {
        return userUnifiedRepository.existsByUsername(username);
    }

    /**
     * 检查邮箱是否存在
     */
    public boolean checkEmailExists(String email) {
        return userUnifiedRepository.existsByEmail(email);
    }

    /**
     * 检查手机号是否存在
     */
    public boolean checkPhoneExists(String phone) {
        return userUnifiedRepository.existsByPhone(phone);
    }

    // ================================ 幂等性操作方法（新增） ================================

    /**
     * 幂等性更新用户状态
     * 使用乐观锁机制，确保状态更新的幂等性和并发安全
     *
     * @param userId 用户ID
     * @param active 是否激活（true=激活，false=禁用）
     * @return 更新结果消息
     */
    @Transactional(rollbackFor = Exception.class)
    public String updateUserStatusIdempotent(Long userId, Boolean active) {
        log.info("幂等性更新用户状态，用户ID：{}，激活状态：{}", userId, active);
        
        // 1. 查询当前用户信息（包含版本号）
        UserUnified currentUser = getUserById(userId);
        
        // 2. 确定目标状态
        UserStateEnum targetStatus = active ? UserStateEnum.ACTIVE : UserStateEnum.SUSPENDED;
        
        // 3. 检查当前状态，如果已经是目标状态则直接返回
        if (currentUser.getStatus() == targetStatus) {
            String message = active ? "用户已经是激活状态，无需重复操作" : "用户已经是禁用状态，无需重复操作";
            log.info("用户状态无需更新，用户ID：{}，当前状态：{}", userId, currentUser.getStatus());
            return message;
        }
        
        // 4. 状态合法性检查
        if (!isValidStatusTransition(currentUser.getStatus(), targetStatus)) {
            throw new BizException(UserErrorCode.INVALID_STATUS_TRANSITION);
        }
        
        // 5. 使用幂等性更新（最多重试3次处理并发冲突）
        int maxRetries = 3;
        for (int i = 0; i < maxRetries; i++) {
            try {
                // 获取最新的用户信息和版本号
                UserUnified latestUser = getUserById(userId);
                
                // 使用幂等性更新
                boolean updateSuccess = userUnifiedRepository.updateStatusIdempotent(
                    userId,
                    latestUser.getStatus().name(),
                    targetStatus.name(),
                    latestUser.getVersion()
                );
                
                if (updateSuccess) {
                    // 清除相关缓存
                    invalidateUserCache(userId);
                    
                    String message = active ? "用户状态已更新为激活" : "用户状态已更新为禁用";
                    log.info("用户状态更新成功，用户ID：{}，新状态：{}", userId, targetStatus);
                    return message;
                } else {
                    // 更新失败，可能是版本冲突，重试
                    log.warn("用户状态更新失败，重试第{}次，用户ID：{}", i + 1, userId);
                    if (i == maxRetries - 1) {
                        throw new BizException(UserErrorCode.STATUS_UPDATE_CONFLICT);
                    }
                    // 短暂等待后重试
                    Thread.sleep(50 * (i + 1)); // 递增等待时间
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new BizException(UserErrorCode.STATUS_UPDATE_ERROR);
            }
        }
        
        throw new BizException(UserErrorCode.STATUS_UPDATE_ERROR);
    }

    /**
     * 检查状态转换是否合法
     */
    private boolean isValidStatusTransition(UserStateEnum fromStatus, UserStateEnum toStatus) {
        // 定义合法的状态转换规则
        switch (fromStatus) {
            case INACTIVE:
                return toStatus == UserStateEnum.ACTIVE || toStatus == UserStateEnum.SUSPENDED;
            case ACTIVE:
                return toStatus == UserStateEnum.SUSPENDED || toStatus == UserStateEnum.BANNED;
            case SUSPENDED:
                return toStatus == UserStateEnum.ACTIVE || toStatus == UserStateEnum.BANNED;
            case BANNED:
                return toStatus == UserStateEnum.ACTIVE; // 只允许管理员恢复被封禁用户
            default:
                return false;
        }
    }

    /**
     * 清除用户相关缓存
     */
    @CacheInvalidate(name = CacheConstant.USER_INFO_CACHE, key = "#userId")
    @CacheInvalidate(name = CacheConstant.USER_PROFILE_CACHE, key = "#userId")
    private void invalidateUserCache(Long userId) {
        log.debug("清除用户缓存，用户ID：{}", userId);
    }

    // ================================ 批量操作功能（新增） ================================

    /**
     * 批量保存用户
     * 适用于用户导入、批量注册等场景
     *
     * @param users 用户列表
     * @return 成功保存的数量
     */
    @Transactional(rollbackFor = Exception.class)
    public int batchSaveUsers(List<UserUnified> users) {
        log.info("批量保存用户，数量：{}", users.size());
        
        if (users == null || users.isEmpty()) {
            log.warn("批量保存用户列表为空");
            return 0;
        }
        
        // 验证数据完整性
        for (UserUnified user : users) {
            validateUserForBatchSave(user);
        }
        
        return userUnifiedRepository.batchSave(users);
    }

    /**
     * 批量更新用户状态
     * 适用于批量激活、禁用用户等场景
     *
     * @param userIds 用户ID列表
     * @param status 新状态
     * @return 成功更新的数量
     */
    @Transactional(rollbackFor = Exception.class)
    public int batchUpdateUserStatus(List<Long> userIds, UserStateEnum status) {
        log.info("批量更新用户状态，用户数：{}，新状态：{}", userIds.size(), status);
        
        if (userIds == null || userIds.isEmpty()) {
            log.warn("批量更新用户状态，用户ID列表为空");
            return 0;
        }
        
        int result = userUnifiedRepository.batchUpdateStatus(userIds, status.name());
        
        // 批量清除缓存
        for (Long userId : userIds) {
            invalidateUserCache(userId);
        }
        
        return result;
    }

    /**
     * 批量更新用户统计字段
     * 适用于批量增加关注数、内容数等统计字段
     *
     * @param userIds 用户ID列表
     * @param field 统计字段名
     * @param increment 增量
     * @return 成功更新的数量
     */
    @Transactional(rollbackFor = Exception.class)
    public int batchUpdateStatisticsField(List<Long> userIds, String field, long increment) {
        log.info("批量更新用户统计字段，用户数：{}，字段：{}，增量：{}", userIds.size(), field, increment);
        
        if (userIds == null || userIds.isEmpty()) {
            log.warn("批量更新统计字段，用户ID列表为空");
            return 0;
        }
        
        // 验证字段名是否合法
        if (!isValidStatisticsField(field)) {
            throw new IllegalArgumentException("无效的统计字段：" + field);
        }
        
        return userUnifiedRepository.batchUpdateStatisticsField(userIds, field, increment);
    }

    /**
     * 更新单个用户的统计字段
     * 适用于单个用户的统计数据更新
     *
     * @param userId 用户ID
     * @param field 统计字段名
     * @param increment 增量
     * @return 是否更新成功
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean updateUserStatisticsField(Long userId, String field, long increment) {
        log.debug("更新用户统计字段，用户ID：{}，字段：{}，增量：{}", userId, field, increment);
        
        // 验证字段名是否合法
        if (!isValidStatisticsField(field)) {
            throw new IllegalArgumentException("无效的统计字段：" + field);
        }
        
        return userUnifiedRepository.updateStatisticsField(userId, field, increment);
    }

    // ================================ 统计查询功能（新增） ================================

    /**
     * 获取系统全局用户统计信息
     * 包含总用户数、活跃用户数、博主数等
     *
     * @return 统计信息Map
     */
    public Map<String, Long> getGlobalUserStatistics() {
        log.info("获取全局用户统计信息");
        return userUnifiedRepository.getUserStatistics();
    }

    /**
     * 获取活跃用户统计
     * 统计指定天数内有登录记录的用户数量
     *
     * @param days 统计天数
     * @return 活跃用户数
     */
    public long getActiveUsersCount(int days) {
        log.info("获取{}天内活跃用户统计", days);
        
        if (days <= 0) {
            throw new IllegalArgumentException("统计天数必须大于0");
        }
        
        return userUnifiedRepository.countActiveUsers(days);
    }

    /**
     * 按博主状态统计用户数量
     *
     * @param bloggerStatus 博主状态
     * @return 用户数量
     */
    public long countUsersByBloggerStatus(String bloggerStatus) {
        log.info("按博主状态统计用户数量，状态：{}", bloggerStatus);
        return userUnifiedRepository.countByBloggerStatus(bloggerStatus);
    }

    /**
     * 获取邀请排行榜
     * 按邀请人数倒序排列
     *
     * @param limit 返回数量限制
     * @return 邀请排行榜
     */
    public List<UserUnified> getInviteRanking(int limit) {
        log.info("获取邀请排行榜，限制：{}", limit);
        
        if (limit <= 0 || limit > 100) {
            limit = 10; // 默认返回前10名
        }
        
        return userUnifiedRepository.getInviteRanking(limit);
    }

    /**
     * 用户搜索功能
     * 支持用户名、昵称、简介的模糊搜索
     *
     * @param keyword 搜索关键词
     * @param pageNum 页码
     * @param pageSize 页大小
     * @return 搜索结果
     */
    public com.baomidou.mybatisplus.core.metadata.IPage<UserUnified> searchUsers(
            String keyword, Integer pageNum, Integer pageSize) {
        log.info("用户搜索，关键词：{}，页码：{}，页大小：{}", keyword, pageNum, pageSize);
        
        // 参数验证
        if (pageNum == null || pageNum < 1) {
            pageNum = 1;
        }
        if (pageSize == null || pageSize < 1 || pageSize > 100) {
            pageSize = 10;
        }
        
        return userUnifiedRepository.searchUsers(keyword, pageNum, pageSize);
    }

    // ================================ 工具方法 ================================

    /**
     * 验证批量保存用户数据的完整性
     */
    private void validateUserForBatchSave(UserUnified user) {
        if (user == null) {
            throw new IllegalArgumentException("用户信息不能为空");
        }
        
        if (!StringUtils.hasText(user.getUsername())) {
            throw new IllegalArgumentException("用户名不能为空");
        }
        
        // 检查用户名唯一性
        if (userUnifiedRepository.existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("用户名已存在：" + user.getUsername());
        }
        
        // 检查邮箱唯一性（如果提供）
        if (StringUtils.hasText(user.getEmail()) && 
            userUnifiedRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("邮箱已存在：" + user.getEmail());
        }
        
        // 检查手机号唯一性（如果提供）
        if (StringUtils.hasText(user.getPhone()) && 
            userUnifiedRepository.existsByPhone(user.getPhone())) {
            throw new IllegalArgumentException("手机号已存在：" + user.getPhone());
        }
    }

    /**
     * 验证统计字段名是否合法
     */
    private boolean isValidStatisticsField(String field) {
        if (!StringUtils.hasText(field)) {
            return false;
        }
        
        // 定义允许的统计字段
        String[] validFields = {
            "follower_count", "following_count", "content_count", 
            "like_count", "invited_count", "view_count"
        };
        
        for (String validField : validFields) {
            if (validField.equals(field)) {
                return true;
            }
        }
        
        return false;
    }

    // ================================ 简化的注册登录功能（新增） ================================

    /**
     * 简化的用户注册（仅用户名密码）
     * 支持邀请码功能
     *
     * @param username 用户名
     * @param password 密码
     * @param inviteCode 邀请码（可选）
     * @return 注册后的用户实体
     */
    @Transactional(rollbackFor = Exception.class)
    public UserUnified simpleRegister(String username, String password, String inviteCode) {
        log.info("简化注册，用户名：{}，邀请码：{}", username, inviteCode);
        
        // 1. 校验用户名唯一性
        if (userUnifiedRepository.existsByUsername(username)) {
            throw new BizException(UserErrorCode.USERNAME_ALREADY_EXISTS);
        }
        
        // 2. 处理邀请码
        Long inviterId = null;
        if (StringUtils.hasText(inviteCode)) {
            UserUnified inviter = userUnifiedRepository.findByInviteCode(inviteCode).orElse(null);
            if (inviter != null) {
                inviterId = inviter.getId();
                log.info("用户{}通过邀请码{}注册，邀请人：{}", username, inviteCode, inviter.getUsername());
            } else {
                log.warn("无效的邀请码：{}", inviteCode);
                // 不抛异常，允许继续注册，只是没有邀请关系
            }
        }
        
        // 3. 创建用户实体
        UserUnified user = new UserUnified();
        user.setUsername(username);
        user.setNickname(generateDefaultNickname());
        
        // 4. 加密密码
        String salt = UUID.randomUUID().toString();
        String passwordHash = passwordEncoder.encode(password);
        user.setPasswordHash(passwordHash);
        user.setSalt(salt);
        
        // 5. 设置基本信息
        user.setRole(UserRole.user);
        user.setStatus(UserStateEnum.ACTIVE); // 直接激活，无需邮件验证
        user.setInviterId(inviterId);
        user.setInviteCode(generateInviteCode()); // 生成自己的邀请码
        
        // 6. 设置默认的扩展信息
        user.setBloggerStatus("none");
        user.setGender("unknown");
        user.setFollowerCount(0);
        user.setFollowingCount(0);
        user.setContentCount(0);
        user.setLikeCount(0);
        user.setInvitedCount(0);
        
        // 7. 保存用户
        user = userUnifiedRepository.save(user);
        
        // 8. 更新邀请人的邀请统计
        if (inviterId != null) {
            updateUserStatisticsField(inviterId, "invited_count", 1);
            log.info("更新邀请人{}的邀请统计", inviterId);
        }
        
        log.info("用户注册成功，用户ID: {}, 用户名: {}, 邀请人ID: {}", user.getId(), user.getUsername(), inviterId);
        return user;
    }

    /**
     * 用户登录（支持自动注册）
     *
     * @param username 用户名
     * @param password 密码
     * @param autoRegister 如果用户不存在是否自动注册
     * @param inviteCode 自动注册时使用的邀请码（可选）
     * @return 登录结果
     */
    @Transactional(rollbackFor = Exception.class)
    public LoginResult loginWithAutoRegister(String username, String password, boolean autoRegister, String inviteCode) {
        log.info("用户登录，用户名：{}，自动注册：{}", username, autoRegister);
        
        try {
            // 1. 尝试查找用户
            UserUnified user = userUnifiedRepository.findByUsername(username).orElse(null);
            
            if (user != null) {
                // 用户存在，验证密码
                if (validatePassword(user, password)) {
                    // 密码正确，更新登录信息
                    updateLoginInfo(user);
                    log.info("用户登录成功，用户ID：{}", user.getId());
                    return LoginResult.success(user, false);
                } else {
                    // 密码错误
                    log.warn("用户{}密码错误", username);
                    return LoginResult.failure("用户名或密码错误");
                }
            } else {
                // 用户不存在
                if (autoRegister) {
                    // 自动注册
                    log.info("用户{}不存在，执行自动注册", username);
                    user = simpleRegister(username, password, inviteCode);
                    updateLoginInfo(user);
                    log.info("用户自动注册并登录成功，用户ID：{}", user.getId());
                    return LoginResult.success(user, true);
                } else {
                    // 不自动注册
                    log.warn("用户{}不存在", username);
                    return LoginResult.failure("用户名或密码错误");
                }
            }
        } catch (BizException e) {
            log.error("登录失败：{}", e.getMessage());
            return LoginResult.failure(e.getMessage());
        } catch (Exception e) {
            log.error("登录异常", e);
            return LoginResult.failure("系统异常，请稍后重试");
        }
    }

    /**
     * 标准用户登录（不自动注册）
     *
     * @param username 用户名
     * @param password 密码
     * @return 登录结果
     */
    public LoginResult login(String username, String password) {
        return loginWithAutoRegister(username, password, false, null);
    }

    /**
     * 根据邀请码查找邀请人信息
     *
     * @param inviteCode 邀请码
     * @return 邀请人信息
     */
    public UserUnified findInviterByCode(String inviteCode) {
        if (!StringUtils.hasText(inviteCode)) {
            return null;
        }
        
        return userUnifiedRepository.findByInviteCode(inviteCode).orElse(null);
    }

    /**
     * 获取用户的邀请统计信息
     *
     * @param userId 用户ID
     * @return 邀请统计信息
     */
    public InviteStatistics getInviteStatistics(Long userId) {
        UserUnified user = getUserById(userId);
        
        // 获取被该用户邀请的用户列表
        List<UserUnified> invitedUsers = userUnifiedRepository.findByInviterId(userId, 1, 100).getRecords();
        
        return new InviteStatistics(
            user.getInviteCode(),
            user.getInvitedCount(),
            invitedUsers.size(),
            invitedUsers
        );
    }

    /**
     * 更新用户登录信息
     */
    private void updateLoginInfo(UserUnified user) {
        user.setLastLoginTime(LocalDateTime.now());
        user.setLoginCount(user.getLoginCount() + 1);
        userUnifiedRepository.save(user);
        
        // 清除缓存
        invalidateUserCache(user.getId());
    }

    /**
     * 生成邀请码
     */
    private String generateInviteCode() {
        String code;
        int attempts = 0;
        do {
            code = UUID.randomUUID().toString()
                      .replace("-", "")
                      .substring(0, 8)
                      .toUpperCase();
            attempts++;
        } while (userUnifiedRepository.existsByInviteCode(code) && attempts < 10);
        
        if (attempts >= 10) {
            throw new RuntimeException("生成邀请码失败，请重试");
        }
        
        return code;
    }

    // ================================ 内部类定义 ================================

    /**
     * 登录结果类
     */
    public static class LoginResult {
        private final boolean success;
        private final String message;
        private final UserUnified user;
        private final boolean isNewUser;

        public LoginResult(boolean success, String message, UserUnified user, boolean isNewUser) {
            this.success = success;
            this.message = message;
            this.user = user;
            this.isNewUser = isNewUser;
        }

        public static LoginResult success(UserUnified user, boolean isNewUser) {
            return new LoginResult(true, "登录成功", user, isNewUser);
        }

        public static LoginResult failure(String message) {
            return new LoginResult(false, message, null, false);
        }

        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public UserUnified getUser() { return user; }
        public boolean isNewUser() { return isNewUser; }
    }

    /**
     * 邀请统计信息类
     */
    public static class InviteStatistics {
        private final String inviteCode;
        private final Long totalInvitedCount;
        private final Integer currentPageCount;
        private final List<UserUnified> invitedUsers;

        public InviteStatistics(String inviteCode, Long totalInvitedCount, Integer currentPageCount, List<UserUnified> invitedUsers) {
            this.inviteCode = inviteCode;
            this.totalInvitedCount = totalInvitedCount;
            this.currentPageCount = currentPageCount;
            this.invitedUsers = invitedUsers;
        }

        public String getInviteCode() { return inviteCode; }
        public Long getTotalInvitedCount() { return totalInvitedCount; }
        public Integer getCurrentPageCount() { return currentPageCount; }
        public List<UserUnified> getInvitedUsers() { return invitedUsers; }
    }
} 