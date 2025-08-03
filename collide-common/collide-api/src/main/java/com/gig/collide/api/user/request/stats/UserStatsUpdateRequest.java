package com.gig.collide.api.user.request.stats;

import java.io.Serializable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 用户统计更新请求 - 对应 t_user_stats �?
 * 
 * @author GIG Team
 * @version 2.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserStatsUpdateRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    /**
     * 粉丝�?
     */
    @Min(value = 0, message = "粉丝数不能为负数")
    private Integer followerCount;

    /**
     * 关注�?
     */
    @Min(value = 0, message = "关注数不能为负数")
    private Integer followingCount;

    /**
     * 内容�?
     */
    @Min(value = 0, message = "内容数不能为负数")
    private Integer contentCount;

    /**
     * 获得点赞�?
     */
    @Min(value = 0, message = "点赞数不能为负数")
    private Integer likeCount;

    /**
     * 登录次数
     */
    @Min(value = 0, message = "登录次数不能为复数")
    private Integer loginCount;
}
