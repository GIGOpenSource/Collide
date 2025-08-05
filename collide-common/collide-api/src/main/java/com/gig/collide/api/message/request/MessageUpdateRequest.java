package com.gig.collide.api.message.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Map;

/**
 * 消息更新请求 - 简洁版
 * 支持更新消息内容、状态、置顶等信息
 * 
 * @author GIG Team
 * @version 2.0.0 (简洁版)
 * @since 2024-01-16
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MessageUpdateRequest implements Serializable {

    /**
     * 消息ID
     */
    @NotNull(message = "消息ID不能为空")
    private Long messageId;

    /**
     * 操作用户ID（用于权限验证）
     */
    @NotNull(message = "操作用户ID不能为空")
    private Long operatorId;

    /**
     * 消息内容（可选）
     * 仅发送者可以修改内容
     */
    private String content;

    /**
     * 消息状态（可选）
     * sent、delivered、read、deleted
     */
    private String status;

    /**
     * 扩展数据（可选）
     */
    private Map<String, Object> extraData;

    /**
     * 是否置顶（可选）
     * 仅接收者可以设置置顶状态
     */
    private Boolean isPinned;

    /**
     * 更新类型
     * content、status、pin、extraData
     */
    @NotBlank(message = "更新类型不能为空")
    private String updateType;

    // =================== 业务验证方法 ===================

    /**
     * 判断是否为内容更新
     */
    public boolean isContentUpdate() {
        return "content".equals(updateType);
    }

    /**
     * 判断是否为状态更新
     */
    public boolean isStatusUpdate() {
        return "status".equals(updateType);
    }

    /**
     * 判断是否为置顶更新
     */
    public boolean isPinUpdate() {
        return "pin".equals(updateType);
    }

    /**
     * 判断是否为扩展数据更新
     */
    public boolean isExtraDataUpdate() {
        return "extraData".equals(updateType);
    }

    /**
     * 验证更新请求的有效性
     */
    public boolean isValidUpdate() {
        switch (updateType) {
            case "content":
                return content != null && !content.trim().isEmpty();
            case "status":
                return status != null && isValidStatus(status);
            case "pin":
                return isPinned != null;
            case "extraData":
                return extraData != null;
            default:
                return false;
        }
    }

    /**
     * 验证状态值的有效性
     */
    private boolean isValidStatus(String status) {
        return "sent".equals(status) || "delivered".equals(status) || 
               "read".equals(status) || "deleted".equals(status);
    }

    /**
     * 验证内容长度
     */
    public boolean isContentLengthValid() {
        if (content == null) {
            return true; // 允许为空，表示不更新内容
        }
        return content.length() <= 1000;
    }
}