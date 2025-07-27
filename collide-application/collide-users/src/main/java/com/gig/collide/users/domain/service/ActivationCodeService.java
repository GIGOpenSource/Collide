package com.gig.collide.users.domain.service;

/**
 * 激活码服务接口
 * 处理用户激活码的生成、存储、验证等业务
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
public interface ActivationCodeService {

    /**
     * 生成并存储用户激活码
     *
     * @param userId 用户ID
     * @param codeType 激活码类型（ACCOUNT_ACTIVATION, PASSWORD_RESET, LOGIN_VERIFY等）
     * @param expireMinutes 过期时间（分钟）
     * @return 激活码
     */
    String generateAndStoreCode(Long userId, CodeType codeType, int expireMinutes);

    /**
     * 验证激活码
     *
     * @param userId 用户ID
     * @param code 激活码
     * @param codeType 激活码类型
     * @return 验证结果
     */
    ActivationCodeValidationResult validateCode(Long userId, String code, CodeType codeType);

    /**
     * 验证并消费激活码（验证成功后删除）
     *
     * @param userId 用户ID
     * @param code 激活码
     * @param codeType 激活码类型
     * @return 验证结果
     */
    ActivationCodeValidationResult validateAndConsumeCode(Long userId, String code, CodeType codeType);

    /**
     * 删除用户的指定类型激活码
     *
     * @param userId 用户ID
     * @param codeType 激活码类型
     * @return 是否删除成功
     */
    boolean removeCode(Long userId, CodeType codeType);

    /**
     * 检查用户是否可以生成新的激活码（防止频繁生成）
     *
     * @param userId 用户ID
     * @param codeType 激活码类型
     * @param intervalSeconds 生成间隔限制（秒）
     * @return 是否可以生成
     */
    boolean canGenerateNewCode(Long userId, CodeType codeType, int intervalSeconds);

    /**
     * 激活码类型枚举
     */
    enum CodeType {
        ACCOUNT_ACTIVATION,    // 账号激活
        PASSWORD_RESET,        // 密码重置
        LOGIN_VERIFY,          // 登录验证
        EMAIL_CHANGE,          // 邮箱更换
        PHONE_CHANGE          // 手机号更换
    }

    /**
     * 激活码验证结果
     */
    class ActivationCodeValidationResult {
        private final boolean valid;
        private final String message;
        private final ValidationFailureReason failureReason;

        public ActivationCodeValidationResult(boolean valid, String message, ValidationFailureReason failureReason) {
            this.valid = valid;
            this.message = message;
            this.failureReason = failureReason;
        }

        public static ActivationCodeValidationResult success() {
            return new ActivationCodeValidationResult(true, "验证成功", null);
        }

        public static ActivationCodeValidationResult failure(String message, ValidationFailureReason reason) {
            return new ActivationCodeValidationResult(false, message, reason);
        }

        public boolean isValid() { return valid; }
        public String getMessage() { return message; }
        public ValidationFailureReason getFailureReason() { return failureReason; }

        /**
         * 验证失败原因枚举
         */
        public enum ValidationFailureReason {
            CODE_NOT_FOUND,        // 激活码不存在
            CODE_EXPIRED,          // 激活码已过期
            CODE_MISMATCH,         // 激活码不匹配
            USER_NOT_FOUND,        // 用户不存在
            SYSTEM_ERROR          // 系统错误
        }
    }
} 