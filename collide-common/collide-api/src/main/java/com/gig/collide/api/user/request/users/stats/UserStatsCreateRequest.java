package com.gig.collide.api.user.request.users.stats;

import java.io.Serializable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 用户统计创建请求 - 对应 t_user_stats 表
 * 
 * @author GIG Team
 * @version 2.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserStatsCreateRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    /**
     * 粉丝数
     */
    @Min(value = 0, message = "粉丝数不能为负数")
    private Integer followerCount = 0;

    /**
     * 关注数
     */
    @Min(value = 0, message = "关注数不能为负数")
    private Integer followingCount = 0;

    /**
     * 内容数
     */
    @Min(value = 0, message = "内容数不能为负数")
    private Integer contentCount = 0;

    /**
     * 获得点赞数
     */
    @Min(value = 0, message = "点赞数不能为负数")
    private Integer likeCount = 0;

    /**
     * 登录次数
     */
    @Min(value = 0, message = "登录次数不能为负数")
    private Integer loginCount = 0;
}