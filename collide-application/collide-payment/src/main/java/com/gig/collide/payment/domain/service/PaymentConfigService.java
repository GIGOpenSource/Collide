package com.gig.collide.payment.domain.service;

import com.gig.collide.payment.domain.entity.PaymentConfig;

import java.util.List;

/**
 * 支付配置服务接口
 * 提供支付平台配置的基础CRUD操作，仅供内部服务调用
 *
 * @author GIG Team
 * @version 1.0.0
 */
public interface PaymentConfigService {

    /**
     * 创建支付配置
     */
    PaymentConfig create(PaymentConfig config);

    /**
     * 更新支付配置
     */
    PaymentConfig update(PaymentConfig config);

    /**
     * 根据ID删除支付配置（逻辑删除）
     */
    boolean deleteById(Long id);

    /**
     * 根据ID查询支付配置
     */
    PaymentConfig getById(Long id);

    /**
     * 根据平台名称查询配置
     */
    PaymentConfig getByPlatformName(String platformName);

    /**
     * 查询所有启用的配置
     */
    List<PaymentConfig> getActiveConfigs();
}