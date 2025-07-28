package com.gig.collide.api.tag.response.data;

import com.gig.collide.api.tag.constant.InterestSourceEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户兴趣标签信息传输对象
 *
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class UserInterestTagInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 关系ID
     */
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 用户昵称
     */
    private String nickname;

    /**
     * 标签ID
     */
    private Long tagId;

    /**
     * 标签名称
     */
    private String tagName;

    /**
     * 标签颜色
     */
    private String tagColor;

    /**
     * 标签类型
     */
    private String tagType;

    /**
     * 兴趣分数（0-100）
     */
    private BigDecimal interestScore;

    /**
     * 兴趣来源
     */
    private InterestSourceEnum source;

    /**
     * 状态
     */
    private String status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 版本号
     */
    private Integer version;

    /**
     * 检查兴趣是否活跃
     */
    public boolean isActive() {
        return "active".equals(status);
    }

    /**
     * 检查是否为高兴趣度
     */
    public boolean isHighInterest() {
        return interestScore != null && interestScore.compareTo(BigDecimal.valueOf(70)) > 0;
    }

    /**
     * 获取兴趣等级描述
     */
    public String getInterestLevel() {
        if (interestScore == null) {
            return "未知";
        }
        
        if (interestScore.compareTo(BigDecimal.valueOf(80)) > 0) {
            return "极高";
        } else if (interestScore.compareTo(BigDecimal.valueOf(60)) > 0) {
            return "高";
        } else if (interestScore.compareTo(BigDecimal.valueOf(40)) > 0) {
            return "中等";
        } else if (interestScore.compareTo(BigDecimal.valueOf(20)) > 0) {
            return "较低";
        } else {
            return "很低";
        }
    }
} 