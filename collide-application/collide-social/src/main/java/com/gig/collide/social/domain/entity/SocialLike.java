package com.gig.collide.social.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 点赞实体
 * 对应表: t_social_like
 * 
 * @author GIG Team
 * @since 2024-01-16
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_social_like")
public class SocialLike extends SocialInteraction {

    @TableField("like_type")
    private Integer likeType; // 1-普通点赞,2-超级点赞

    // 点赞类型枚举
    public enum LikeType {
        NORMAL(1, "普通点赞"),
        SUPER(2, "超级点赞");

        private final int code;
        private final String desc;

        LikeType(int code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        public int getCode() { return code; }
        public String getDesc() { return desc; }
    }
}