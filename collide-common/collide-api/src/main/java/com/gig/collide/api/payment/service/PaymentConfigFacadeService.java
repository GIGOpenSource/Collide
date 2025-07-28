package com.gig.collide.api.payment.service;

import com.gig.collide.api.payment.request.*;
import com.gig.collide.api.payment.response.*;
import com.gig.collide.api.payment.response.data.PaymentConfigInfo;
import com.gig.collide.base.response.BaseResponse;
import com.gig.collide.base.response.PageResponse;

/**
 * 支付配置门面服务接口
 * 提供支付配置管理功能
 * 
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
public interface PaymentConfigFacadeService {

    /**
     * 创建支付配置
     * 
     * @param createRequest 创建配置请求
     * @return 创建配置响应
     */
    PaymentConfigCreateResponse createConfig(PaymentConfigCreateRequest createRequest);

    /**
     * 更新支付配置
     * 
     * @param updateRequest 更新配置请求
     * @return 更新配置响应
     */
    PaymentConfigUpdateResponse updateConfig(PaymentConfigUpdateRequest updateRequest);

    /**
     * 查询支付配置
     * 
     * @param queryRequest 查询请求
     * @return 查询响应
     */
    PaymentConfigQueryResponse queryConfig(PaymentConfigQueryRequest queryRequest);

    /**
     * 分页查询支付配置
     * 
     * @param queryRequest 分页查询请求
     * @return 分页响应
     */
    PageResponse<PaymentConfigInfo> pageQueryConfigs(PaymentConfigQueryRequest queryRequest);

    /**
     * 删除支付配置
     * 
     * @param deleteRequest 删除请求
     * @return 删除响应
     */
    BaseResponse deleteConfig(PaymentConfigDeleteRequest deleteRequest);

    /**
     * 启用支付配置
     * 
     * @param enableRequest 启用请求
     * @return 启用响应
     */
    BaseResponse enableConfig(PaymentConfigEnableRequest enableRequest);

    /**
     * 禁用支付配置
     * 
     * @param disableRequest 禁用请求
     * @return 禁用响应
     */
    BaseResponse disableConfig(PaymentConfigDisableRequest disableRequest);

    /**
     * 获取指定类型的配置
     * 
     * @param configType 配置类型
     * @param envProfile 环境标识
     * @return 配置响应
     */
    PaymentConfigQueryResponse getConfigByType(String configType, String envProfile);

    /**
     * 批量导入配置
     * 
     * @param importRequest 导入请求
     * @return 导入响应
     */
    PaymentConfigImportResponse importConfigs(PaymentConfigImportRequest importRequest);

    /**
     * 导出配置
     * 
     * @param exportRequest 导出请求
     * @return 导出响应
     */
    PaymentConfigExportResponse exportConfigs(PaymentConfigExportRequest exportRequest);
} 