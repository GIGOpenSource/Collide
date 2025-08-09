package com.gig.collide.users.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("t_user_role")
public class UserRole implements Serializable {
    private Long id;
    private Long userId;
    private Integer roleId;
    private LocalDateTime createTime;
}