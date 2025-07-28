package com.gig.collide.api.user.request.condition;

import lombok.*;

import java.util.List;

/**
 * 用户操作日志-操作类型查询条件
 *
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UserOperateLogOperateTypeQueryCondition implements UserQueryCondition {

    private static final long serialVersionUID = 1L;

    /**
     * 操作类型枚举
     */
    public enum OperateType {
        /** 注册 */
        register,
        /** 登录 */
        login,
        /** 登出 */
        logout,
        /** 更新个人信息 */
        update_profile,
        /** 申请博主 */
        apply_blogger,
        /** 批准博主 */
        approve_blogger,
        /** 拒绝博主 */
        reject_blogger,
        /** 升级VIP */
        upgrade_vip,
        /** 邀请用户 */
        invite_user
    }

    /**
     * 操作类型
     */
    private OperateType operateType;

    /**
     * 操作类型列表（支持多类型查询）
     */
    private List<OperateType> operateTypes;
} 