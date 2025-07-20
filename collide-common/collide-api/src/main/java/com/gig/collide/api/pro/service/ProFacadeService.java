package com.gig.collide.api.pro.service;

import com.gig.collide.api.pro.request.*;
import com.gig.collide.api.pro.response.ProOperatorResponse;
import com.gig.collide.api.pro.response.ProQueryResponse;
import com.gig.collide.api.pro.response.data.ProInfo;
import com.gig.collide.api.pro.response.data.ProPermissionInfo;
import com.gig.collide.api.user.response.data.UserInfo;
import com.gig.collide.base.response.PageResponse;

/**
 * Pro付费用户服务门面接口
 * @author GIG
 */
public interface ProFacadeService {

    /**
     * 用户升级到付费用户
     * @param proUpgradeRequest
     * @return
     */
    ProOperatorResponse upgradeToPro(ProUpgradeRequest proUpgradeRequest);

    /**
     * 用户降级
     * @param proDowngradeRequest
     * @return
     */
    ProOperatorResponse downgrade(ProDowngradeRequest proDowngradeRequest);

    /**
     * 查询用户付费状态
     * @param proStatusQueryRequest
     * @return
     */
    ProQueryResponse<ProInfo> queryProStatus(ProStatusQueryRequest proStatusQueryRequest);

    /**
     * 分页查询付费用户列表
     * @param proPageQueryRequest
     * @return
     */
    PageResponse<UserInfo> pageQueryProUsers(ProPageQueryRequest proPageQueryRequest);

    /**
     * 付费用户续费
     * @param proRenewRequest
     * @return
     */
    ProOperatorResponse renewPro(ProRenewRequest proRenewRequest);

    /**
     * 查询用户付费权限
     * @param proPermissionQueryRequest
     * @return
     */
    ProQueryResponse<ProPermissionInfo> queryProPermissions(ProPermissionQueryRequest proPermissionQueryRequest);

    /**
     * 激活付费权限
     * @param proPermissionActivateRequest
     * @return
     */
    ProOperatorResponse activateProPermission(ProPermissionActivateRequest proPermissionActivateRequest);

    /**
     * 停用付费权限
     * @param proPermissionDeactivateRequest
     * @return
     */
    ProOperatorResponse deactivateProPermission(ProPermissionDeactivateRequest proPermissionDeactivateRequest);
} 