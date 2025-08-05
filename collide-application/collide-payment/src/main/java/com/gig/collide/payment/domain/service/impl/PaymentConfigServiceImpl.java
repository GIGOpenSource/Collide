package com.gig.collide.payment.domain.service.impl;

import com.gig.collide.payment.domain.entity.PaymentConfig;
import com.gig.collide.payment.domain.service.PaymentConfigService;
import com.gig.collide.payment.infrastructure.mapper.PaymentConfigMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 支付配置服务实现类
 * 提供支付平台配置的基础CRUD操作
 *
 * @author GIG Team
 * @version 1.0.0
 */
@Slf4j
@Service
public class PaymentConfigServiceImpl implements PaymentConfigService {

    @Autowired
    private PaymentConfigMapper paymentConfigMapper;

    @Override
    @Transactional
    public PaymentConfig create(PaymentConfig config) {
        log.info("创建支付配置 - 平台名称: {}", config.getPlatformName());
        paymentConfigMapper.insert(config);
        return config;
    }

    @Override
    @Transactional
    public PaymentConfig update(PaymentConfig config) {
        log.info("更新支付配置 - ID: {}, 平台名称: {}", config.getId(), config.getPlatformName());
        paymentConfigMapper.updateById(config);
        return config;
    }

    @Override
    @Transactional
    public boolean deleteById(Long id) {
        log.info("删除支付配置 - ID: {}", id);
        int result = paymentConfigMapper.deleteById(id);
        return result > 0;
    }

    @Override
    public PaymentConfig getById(Long id) {
        log.debug("查询支付配置 - ID: {}", id);
        return paymentConfigMapper.selectById(id);
    }

    @Override
    public PaymentConfig getByPlatformName(String platformName) {
        log.debug("根据平台名称查询支付配置 - 平台名称: {}", platformName);
        return paymentConfigMapper.selectByPlatformName(platformName);
    }

    @Override
    public List<PaymentConfig> getActiveConfigs() {
        log.debug("查询所有启用的支付配置");
        return paymentConfigMapper.selectActiveConfigs();
    }
}