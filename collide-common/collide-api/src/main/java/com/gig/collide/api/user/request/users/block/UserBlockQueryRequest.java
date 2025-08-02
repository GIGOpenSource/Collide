package com.gig.collide.api.user.request.users.block;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 用户拉黑查询请求
 * 
 * @author GIG Team
 * @version 2.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserBlockQueryRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 拉黑者用户ID
     */
    private Long userId;

    /**
     * 被拉黑用户ID
     */
    private Long blockedUserId;

    /**
     * 拉黑者用户名（模糊查询）
     */
    private String userUsername;

    /**
     * 被拉黑用户名（模糊查询）
     */
    private String blockedUsername;

    /**
     * 拉黑状态：active、cancelled
     */
    private String status;

    /**
     * 拉黑原因（模糊查询）
     */
    private String reason;

    /**
     * 拉黑时间开�?
     */
    private LocalDateTime createTimeStart;

    /**
     * 拉黑时间结束
     */
    private LocalDateTime createTimeEnd;

    /**
     * 排序字段：create_time、update_time、user_id、blocked_user_id
     */
    private String sortField = "create_time";

    /**
     * 排序方向：asc、desc
     */
    private String sortDirection = "desc";

    /**
     * 当前页码
     */
    private Integer currentPage = 1;

    /**
     * 页面大小
     */
    private Integer pageSize = 20;
}
