package com.gig.collide.users.domain.service;

import com.gig.collide.api.user.constant.UserRole;
import com.gig.collide.api.user.constant.UserStateEnum;
import com.gig.collide.users.domain.entity.User;
import com.gig.collide.users.domain.entity.UserProfile;
import com.gig.collide.users.domain.repository.UserRepository;
import com.gig.collide.users.domain.repository.UserProfileRepository;
import com.gig.collide.api.user.request.UserModifyRequest;
import com.gig.collide.api.user.request.UserRegisterRequest;
import com.gig.collide.base.exception.BizException;
import com.gig.collide.users.infrastructure.exception.UserErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.Random;

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

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 根据ID查询用户信息
     *
     * @param userId 用户ID
     * @return 用户实体
     * @throws UserErrorCode 用户不存在时抛出
     */
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BizException(UserErrorCode.USER_NOT_FOUND));
    }

    /**
     * 根据用户名查询用户信息
     *
     * @param username 用户名
     * @return 用户实体
     * @throws BizException 用户不存在时抛出
     */
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new BizException(UserErrorCode.USER_NOT_FOUND));
    }

    /**
     * 根据手机号查询用户信息
     *
     * @param phone 手机号
     * @return 用户实体
     * @throws BizException 用户不存在时抛出
     */
    public User getUserByPhone(String phone) {
        return userRepository.findByPhone(phone)
                .orElseThrow(() -> new BizException(UserErrorCode.USER_NOT_FOUND));
    }

    /**
     * 根据邮箱查询用户信息
     *
     * @param email 邮箱
     * @return 用户实体
     * @throws BizException 用户不存在时抛出
     */
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new BizException(UserErrorCode.USER_NOT_FOUND));
    }

    /**
     * 用户注册
     *
     * @param registerRequest 注册请求
     * @return 新注册的用户实体
     */
    @Transactional(rollbackFor = Exception.class)
    public User registerUser(UserRegisterRequest registerRequest) {
        // 1. 校验用户名唯一性
        if (StringUtils.hasText(registerRequest.getUsername()) && 
            userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new BizException(UserErrorCode.USERNAME_ALREADY_EXISTS);
        }

        // 2. 校验邮箱唯一性
        if (StringUtils.hasText(registerRequest.getEmail()) && 
            userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new BizException(UserErrorCode.EMAIL_ALREADY_EXISTS);
        }

        // 3. 校验手机号唯一性
        if (StringUtils.hasText(registerRequest.getPhone()) && 
            userRepository.existsByPhone(registerRequest.getPhone())) {
            throw new BizException(UserErrorCode.PHONE_ALREADY_EXISTS);
        }

        // 4. 创建用户实体
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setNickname(StringUtils.hasText(registerRequest.getNickname()) ? 
                        registerRequest.getNickname() : generateDefaultNickname());
        user.setEmail(registerRequest.getEmail());
        user.setPhone(registerRequest.getPhone());
        
        // 5. 加密密码
        if (StringUtils.hasText(registerRequest.getPassword())) {
            String salt = UUID.randomUUID().toString();
            String passwordHash = passwordEncoder.encode(registerRequest.getPassword());
            user.setPasswordHash(passwordHash);
            user.setSalt(salt);
        }

        // 6. 设置用户默认状态
        user.setRole(UserRole.user); // 默认为普通用户
        user.setStatus(UserStateEnum.ACTIVE); // 直接激活，不需要邮箱验证
        
        // 7. 保存用户
        user = userRepository.save(user);
        
        // 8. 创建用户扩展信息
        createDefaultUserProfile(user.getId());
        
        log.info("新用户注册成功，用户ID: {}, 用户名: {}", user.getId(), user.getUsername());
        return user;
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
     * 创建默认的用户扩展信息
     *
     * @param userId 用户ID
     */
    private void createDefaultUserProfile(Long userId) {
        UserProfile userProfile = new UserProfile();
        userProfile.setUserId(userId);
        userProfile.setBloggerStatus(UserProfile.BloggerStatus.none);
        userProfile.setGender(UserProfile.Gender.unknown);
        userProfileRepository.save(userProfile);
    }

    /**
     * 更新用户基础信息
     *
     * @param userId        用户ID
     * @param updateRequest 更新请求
     * @return 更新后的用户实体
     */
    @Transactional(rollbackFor = Exception.class)
    public User updateUserInfo(Long userId, UserModifyRequest updateRequest) {
        User user = getUserById(userId);

        // 更新基础信息
        if (StringUtils.hasText(updateRequest.getNickname())) {
            user.setNickname(updateRequest.getNickname());
        }
        if (StringUtils.hasText(updateRequest.getAvatar())) {
            user.setAvatar(updateRequest.getAvatar());
        }
        if (StringUtils.hasText(updateRequest.getEmail())) {
            // 检查邮箱是否已被其他用户使用
            if (userRepository.existsByEmail(updateRequest.getEmail()) && 
                !updateRequest.getEmail().equals(user.getEmail())) {
                throw new BizException(UserErrorCode.EMAIL_ALREADY_EXISTS);
            }
            user.setEmail(updateRequest.getEmail());
        }
        if (StringUtils.hasText(updateRequest.getPhone())) {
            // 检查手机号是否已被其他用户使用
            if (userRepository.existsByPhone(updateRequest.getPhone()) && 
                !updateRequest.getPhone().equals(user.getPhone())) {
                throw new BizException(UserErrorCode.PHONE_ALREADY_EXISTS);
            }
            user.setPhone(updateRequest.getPhone());
        }

        // 保存用户基础信息
        user = userRepository.save(user);

        // 更新扩展信息
        updateUserProfile(userId, updateRequest);

        return user;
    }

    /**
     * 更新用户扩展信息
     *
     * @param userId        用户ID
     * @param updateRequest 更新请求
     */
    private void updateUserProfile(Long userId, UserModifyRequest updateRequest) {
        UserProfile userProfile = userProfileRepository.findByUserId(userId)
                .orElseGet(() -> {
                    UserProfile newProfile = new UserProfile();
                    newProfile.setUserId(userId);
                    return newProfile;
                });

        // 更新扩展信息
        if (StringUtils.hasText(updateRequest.getBio())) {
            userProfile.setBio(updateRequest.getBio());
        }
        if (StringUtils.hasText(updateRequest.getGender())) {
            try {
                UserProfile.Gender gender = UserProfile.Gender.valueOf(updateRequest.getGender().toLowerCase());
                userProfile.setGender(gender);
            } catch (IllegalArgumentException e) {
                throw new BizException(UserErrorCode.INVALID_GENDER);
            }
        }
        if (updateRequest.getBirthday() != null) {
            userProfile.setBirthday(updateRequest.getBirthday());
        }
        if (StringUtils.hasText(updateRequest.getLocation())) {
            userProfile.setLocation(updateRequest.getLocation());
        }

        userProfileRepository.save(userProfile);
    }

    /**
     * 申请博主认证
     *
     * @param userId 用户ID
     * @return 申请结果信息
     */
    @Transactional(rollbackFor = Exception.class)
    public String applyForBlogger(Long userId) {
        User user = getUserById(userId);

        // 检查用户状态
        if (user.getStatus() != UserStateEnum.ACTIVE) {
            throw new BizException(UserErrorCode.USER_STATUS_INVALID);
        }

        // 获取或创建用户扩展信息
        UserProfile userProfile = userProfileRepository.findByUserId(userId)
                .orElseGet(() -> {
                    UserProfile newProfile = new UserProfile();
                    newProfile.setUserId(userId);
                    return newProfile;
                });

        // 检查当前博主认证状态
        switch (userProfile.getBloggerStatus()) {
            case approved:
                return "您已经是认证博主了";
            case applying:
                return "您的博主申请正在审核中，请耐心等待";
            case rejected:
                // 检查是否可以重新申请（距离上次申请是否超过30天）
                if (userProfile.getBloggerApplyTime() != null &&
                        userProfile.getBloggerApplyTime().isAfter(LocalDateTime.now().minusDays(30))) {
                    return "博主申请被拒绝后需等待30天才能重新申请";
                }
                break;
        }

        // 更新博主申请状态
        userProfile.setBloggerStatus(UserProfile.BloggerStatus.applying);
        userProfile.setBloggerApplyTime(LocalDateTime.now());
        userProfileRepository.save(userProfile);

        log.info("用户 {} 申请博主认证", userId);
        return "博主认证申请已提交，我们将在3-5个工作日内完成审核";
    }

    /**
     * 检查用户是否为VIP
     *
     * @param userId 用户ID
     * @return 是否为VIP
     */
    public boolean isVipUser(Long userId) {
        User user = getUserById(userId);
        if (user.getRole() == UserRole.vip) {
            UserProfile profile = userProfileRepository.findByUserId(userId).orElse(null);
            if (profile != null && profile.getVipExpireTime() != null) {
                return profile.getVipExpireTime().isAfter(LocalDateTime.now());
            }
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
        User user = getUserById(userId);
        if (user.getRole() == UserRole.blogger) {
            UserProfile profile = userProfileRepository.findByUserId(userId).orElse(null);
            return profile != null && profile.getBloggerStatus() == UserProfile.BloggerStatus.approved;
        }
        return false;
    }

    /**
     * 更新用户最后登录时间
     *
     * @param userId 用户ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateLastLoginTime(Long userId) {
        User user = getUserById(userId);
        user.setLastLoginTime(LocalDateTime.now());
        userRepository.save(user);
    }

    /**
     * 获取用户扩展信息
     *
     * @param userId 用户ID
     * @return 用户扩展信息
     */
    public UserProfile getUserProfile(Long userId) {
        return userProfileRepository.findByUserId(userId).orElse(null);
    }

    /**
     * 验证用户密码
     *
     * @param user     用户实体
     * @param password 原始密码
     * @return 是否匹配
     */
    public boolean validatePassword(User user, String password) {
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
    public com.baomidou.mybatisplus.core.metadata.IPage<User> pageQueryUsers(
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
        return userRepository.pageQuery(pageNum, pageSize, usernameKeyword, status, role);
    }

    /**
     * 生成用户激活码
     *
     * @param userId 用户ID
     * @return 激活码
     */
    public String generateActivationCode(Long userId) {
        // 生成6位数字激活码
        Random random = new Random();
        String code = String.format("%06d", random.nextInt(1000000));
        
        log.info("为用户{}生成激活码：{}", userId, code);
        
        // TODO: 这里应该将激活码存储到Redis或数据库中，设置过期时间
        // 目前为了演示，直接返回激活码
        
        return code;
    }

    /**
     * 验证激活码并激活用户
     *
     * @param userId         用户ID
     * @param activationCode 激活码
     * @return 激活结果信息
     */
    @Transactional
    public String activateUser(Long userId, String activationCode) {
        log.info("激活用户账号，用户ID：{}，激活码：{}", userId, activationCode);
        
        // 1. 查询用户
        User user = getUserById(userId);
        
        // 2. 检查用户状态
        if (user.getStatus() == UserStateEnum.ACTIVE) {
            log.warn("用户{}已经是激活状态", userId);
            return "用户已经激活，无需重复激活";
        }
        
        if (user.getStatus() == UserStateEnum.BANNED) {
            log.warn("用户{}已被禁用，无法激活", userId);
            throw new BizException(UserErrorCode.USER_DISABLED);
        }
        
        // 3. 验证激活码
        // TODO: 这里应该从Redis或数据库中验证激活码的有效性
        // 目前为了演示，使用简单的验证逻辑
        if (!isValidActivationCode(userId, activationCode)) {
            log.warn("用户{}的激活码{}无效", userId, activationCode);
            throw new IllegalArgumentException("激活码无效或已过期");
        }
        
        // 4. 激活用户
        user.setStatus(UserStateEnum.ACTIVE);
        user.setUpdateTime(LocalDateTime.now());
        userRepository.save(user);
        
        log.info("用户{}激活成功", userId);
        return "用户激活成功";
    }

    /**
     * 发送激活码
     *
     * @param userId         用户ID
     * @param activationType 激活类型
     * @return 发送结果
     */
    public String sendActivationCode(Long userId, String activationType) {
        log.info("发送激活码，用户ID：{}，激活类型：{}", userId, activationType);
        
        // 1. 查询用户
        User user = getUserById(userId);
        
        // 2. 检查用户状态
        if (user.getStatus() == UserStateEnum.ACTIVE) {
            return "用户已经激活，无需发送激活码";
        }
        
        // 3. 生成激活码
        String activationCode = generateActivationCode(userId);
        
        // 4. 根据激活类型发送激活码
        boolean sendSuccess = false;
        String targetAddress = "";
        
        if ("EMAIL".equals(activationType)) {
            if (StringUtils.hasText(user.getEmail())) {
                targetAddress = user.getEmail();
                sendSuccess = sendActivationEmail(user.getEmail(), activationCode);
            } else {
                throw new IllegalArgumentException("用户邮箱为空，无法发送邮件激活码");
            }
        } else if ("SMS".equals(activationType)) {
            if (StringUtils.hasText(user.getPhone())) {
                targetAddress = user.getPhone();
                sendSuccess = sendActivationSms(user.getPhone(), activationCode);
            } else {
                throw new IllegalArgumentException("用户手机号为空，无法发送短信激活码");
            }
        } else {
            throw new IllegalArgumentException("不支持的激活类型：" + activationType);
        }
        
        if (sendSuccess) {
            log.info("激活码发送成功，用户ID：{}，发送地址：{}", userId, targetAddress);
            return String.format("激活码已发送至%s", maskSensitiveInfo(targetAddress, activationType));
        } else {
            log.error("激活码发送失败，用户ID：{}，发送地址：{}", userId, targetAddress);
            throw new RuntimeException("激活码发送失败，请稍后重试");
        }
    }

    /**
     * 验证激活码是否有效
     */
    private boolean isValidActivationCode(Long userId, String activationCode) {
        // TODO: 这里应该从Redis或数据库中验证激活码
        // 目前为了演示，使用简单的验证逻辑：6位数字
        return activationCode != null && activationCode.matches("\\d{6}");
    }

    /**
     * 发送激活邮件
     */
    private boolean sendActivationEmail(String email, String activationCode) {
        try {
            // TODO: 集成邮件服务发送激活邮件
            log.info("发送激活邮件到：{}，激活码：{}", email, activationCode);
            
            // 模拟邮件发送
            String emailContent = String.format(
                "您好！\n\n" +
                "欢迎注册Collide社交平台！\n\n" +
                "您的激活码是：%s\n\n" +
                "请在10分钟内使用此激活码激活您的账号。\n\n" +
                "如果您没有注册此账号，请忽略此邮件。\n\n" +
                "Collide团队",
                activationCode
            );
            
            log.info("邮件内容：{}", emailContent);
            return true;
            
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
            // TODO: 集成短信服务发送激活短信
            log.info("发送激活短信到：{}，激活码：{}", phone, activationCode);
            
            // 模拟短信发送
            String smsContent = String.format(
                "【Collide】您的激活码是：%s，请在10分钟内使用。如非本人操作，请忽略此短信。",
                activationCode
            );
            
            log.info("短信内容：{}", smsContent);
            return true;
            
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
} 