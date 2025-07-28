package com.gig.collide.api.order.service;

import com.gig.collide.api.order.request.*;
import com.gig.collide.api.order.response.*;
import com.gig.collide.api.order.response.data.OrderContentAssociationInfo;
import com.gig.collide.base.response.BaseResponse;
import com.gig.collide.base.response.PageResponse;

/**
 * 订单内容关联门面服务接口
 * 提供订单内容关联业务功能
 * 
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
public interface OrderContentFacadeService {

    /**
     * 查询订单内容关联
     * 
     * @param queryRequest 查询请求
     * @return 查询响应
     */
    OrderContentQueryResponse queryOrderContent(OrderContentQueryRequest queryRequest);

    /**
     * 分页查询订单内容关联
     * 
     * @param queryRequest 分页查询请求
     * @return 分页响应
     */
    PageResponse<OrderContentAssociationInfo> pageQueryOrderContents(OrderContentQueryRequest queryRequest);

    /**
     * 查询用户内容访问权限
     * 
     * @param accessRequest 访问权限查询请求
     * @return 访问权限响应
     */
    OrderContentAccessResponse checkContentAccess(OrderContentAccessRequest accessRequest);

    /**
     * 激活内容访问权限
     * 
     * @param activateRequest 激活请求
     * @return 激活响应
     */
    BaseResponse activateContentAccess(OrderContentActivateRequest activateRequest);

    /**
     * 撤销内容访问权限
     * 
     * @param revokeRequest 撤销请求
     * @return 撤销响应
     */
    BaseResponse revokeContentAccess(OrderContentRevokeRequest revokeRequest);

    /**
     * 续期内容访问权限
     * 
     * @param renewRequest 续期请求
     * @return 续期响应
     */
    OrderContentRenewResponse renewContentAccess(OrderContentRenewRequest renewRequest);

    /**
     * 获取用户所有有效内容权限
     * 
     * @param userId 用户ID
     * @return 用户内容权限列表
     */
    OrderUserContentResponse getUserValidContents(Long userId);

    /**
     * 批量检查内容访问权限
     * 
     * @param batchCheckRequest 批量检查请求
     * @return 批量检查响应
     */
    OrderContentBatchCheckResponse batchCheckContentAccess(OrderContentBatchCheckRequest batchCheckRequest);

    /**
     * 获取即将过期的内容权限
     * 
     * @param expiringRequest 即将过期查询请求
     * @return 即将过期权限响应
     */
    OrderContentExpiringResponse getExpiringContentAccess(OrderContentExpiringRequest expiringRequest);
} 