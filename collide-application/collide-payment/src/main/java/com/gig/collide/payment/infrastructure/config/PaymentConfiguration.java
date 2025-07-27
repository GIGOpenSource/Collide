package com.gig.collide.payment.infrastructure.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * 支付模块配置
 * 
 * @author Collide Team
 * @since 2024-01-01
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "collide.payment")
public class PaymentConfiguration {

    /**
     * 测试支付配置
     */
    private TestConfig test = new TestConfig();

    /**
     * 真实支付配置
     */
    private RealConfig real = new RealConfig();

    /**
     * 幂等性配置
     */
    private IdempotencyConfig idempotency = new IdempotencyConfig();

    /**
     * 缓存配置
     */
    private CacheConfig cache = new CacheConfig();

    @Data
    public static class TestConfig {
        /**
         * 是否启用测试支付
         */
        private Boolean enabled = false;

        /**
         * 自动成功延迟时间（毫秒）
         */
        private Integer autoSuccessDelay = 3000;

        /**
         * 支持的支付方式
         */
        private List<String> supportedPayTypes = List.of("TEST", "ALIPAY", "WECHAT", "UNIONPAY");
    }

    @Data
    public static class RealConfig {
        /**
         * 是否启用真实支付
         */
        private Boolean enabled = true;

        /**
         * 支付宝配置
         */
        private AlipayConfig alipay = new AlipayConfig();

        /**
         * 微信支付配置
         */
        private WechatConfig wechat = new WechatConfig();
    }

    @Data
    public static class AlipayConfig {
        /**
         * 应用ID
         */
        private String appId;

        /**
         * 私钥
         */
        private String privateKey;

        /**
         * 公钥
         */
        private String publicKey;

        /**
         * 签名类型
         */
        private String signType = "RSA2";

        /**
         * 字符集
         */
        private String charset = "UTF-8";

        /**
         * 网关地址
         */
        private String gatewayUrl = "https://openapi.alipay.com/gateway.do";
    }

    @Data
    public static class WechatConfig {
        /**
         * 应用ID
         */
        private String appId;

        /**
         * 商户号
         */
        private String mchId;

        /**
         * API密钥
         */
        private String apiKey;

        /**
         * 证书路径
         */
        private String certPath;
    }

    @Data
    public static class IdempotencyConfig {
        /**
         * 是否启用幂等性
         */
        private Boolean enabled = true;

        /**
         * Redis键前缀
         */
        private String keyPrefix = "payment:idempotent:";

        /**
         * 超时时间（秒）
         */
        private Integer timeout = 300;
    }

    @Data
    public static class CacheConfig {
        /**
         * 是否启用缓存
         */
        private Boolean enabled = true;

        /**
         * 支付状态缓存过期时间（秒）
         */
        private Integer paymentStatusTtl = 1800;

        /**
         * 回调记录缓存过期时间（秒）
         */
        private Integer callbackRecordTtl = 86400;
    }
} 