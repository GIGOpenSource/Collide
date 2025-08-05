package com.gig.collide.payment.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gig.collide.payment.domain.entity.PaymentConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 支付配置Mapper接口
 * 提供支付平台配置的基础CRUD操作
 *
 * @author GIG Team
 * @version 1.0.0
 */
@Mapper
public interface PaymentConfigMapper extends BaseMapper<PaymentConfig> {

    /**
     * 根据平台名称查询配置
     */
    PaymentConfig selectByPlatformName(@Param("platformName") String platformName);

    /**
     * 根据商户ID查询配置
     */
    List<PaymentConfig> selectByMerchantId(@Param("merchantId") String merchantId);

    /**
     * 查询所有启用的配置
     */
    List<PaymentConfig> selectActiveConfigs();

    /**
     * 根据支付方式查询配置
     */
    List<PaymentConfig> selectByPaymentMethod(@Param("paymentMethod") String paymentMethod);

    /**
     * 根据签名类型查询配置
     */
    List<PaymentConfig> selectBySignType(@Param("signType") String signType);
}