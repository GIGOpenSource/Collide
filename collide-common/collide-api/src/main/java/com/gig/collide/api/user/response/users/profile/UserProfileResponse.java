package com.gig.collide.api.user.response.users.profile;

import java.io.Serializable;
import java.time.LocalDate;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 用户资料响应 - 对应 t_user_profile 表
 * 
 * @author GIG Team
 * @version 2.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 头像URL
     */
    private String avatar;

    /**
     * 个人简介
     */
    private String bio;

    /**
     * 生日
     */
    private LocalDate birthday;

    /**
     * 性别：0-unknown, 1-male, 2-female
     */
    private Integer gender;

    /**
     * 性别描述
     */
    private String genderDesc;

    /**
     * 所在地
     */
    private String location;

    /**
     * 设置性别并自动计算性别描述
     */
    public void setGender(Integer gender) {
        this.gender = gender;
        // 设置性别描述
        switch (gender) {
            case 1:
                this.genderDesc = "男";
                break;
            case 2:
                this.genderDesc = "女";
                break;
            default:
                this.genderDesc = "未知";
        }
    }
}