package com.gig.collide.api.user.request.users.profile;

import java.io.Serializable;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 用户资料查询请求
 * 
 * @author GIG Team
 * @version 2.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileQueryRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 昵称（模糊查询）
     */
    private String nickname;

    /**
     * 性别�?-unknown, 1-male, 2-female
     */
    private Integer gender;

    /**
     * 所在地（模糊查询）
     */
    private String location;

    /**
     * 当前页码
     */
    private Integer currentPage = 1;

    /**
     * 页面大小
     */
    private Integer pageSize = 20;
}
