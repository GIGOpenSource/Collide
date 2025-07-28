package com.gig.collide.api.follow.response;

import com.gig.collide.api.follow.response.data.FollowInfo;
import com.gig.collide.base.response.BaseResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * 关注创建响应
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "关注创建响应")
public class FollowCreateResponse extends BaseResponse {

    private static final long serialVersionUID = 1L;

    /**
     * 创建的关注信息
     */
    @Schema(description = "创建的关注信息")
    private FollowInfo followInfo;

    /**
     * 关注ID
     */
    @Schema(description = "关注ID")
    private Long followId;

    /**
     * 是否为新创建（false表示已存在）
     */
    @Schema(description = "是否为新创建")
    private Boolean isNewCreate;

    private String Message;

    // ===================== 静态工厂方法 =====================

    /**
     * 成功创建响应
     *
     * @param followInfo 关注信息
     * @return 创建响应
     */
    public static FollowCreateResponse success(FollowInfo followInfo) {
        FollowCreateResponse response = new FollowCreateResponse();
        response.setFollowInfo(followInfo);
        response.setFollowId(followInfo.getId());
        response.setIsNewCreate(true);
        response.setSuccess(true);
        return response;
    }

    /**
     * 成功创建响应（仅返回ID）
     *
     * @param followId 关注ID
     * @return 创建响应
     */
    public static FollowCreateResponse success(Long followId) {
        FollowCreateResponse response = new FollowCreateResponse();
        response.setFollowId(followId);
        response.setIsNewCreate(true);
        response.setSuccess(true);
        return response;
    }

    /**
     * 已存在响应
     *
     * @param followInfo 已存在的关注信息
     * @return 创建响应
     */
    public static FollowCreateResponse exists(FollowInfo followInfo) {
        FollowCreateResponse response = new FollowCreateResponse();
        response.setFollowInfo(followInfo);
        response.setFollowId(followInfo.getId());
        response.setIsNewCreate(false);
        response.setSuccess(true);
        response.setMessage("关注关系已存在");
        return response;
    }

    /**
     * 失败响应
     *
     * @param message 错误消息
     * @return 创建响应
     */
    public static FollowCreateResponse error(String message) {
        FollowCreateResponse response = new FollowCreateResponse();
        response.setSuccess(false);
        response.setMessage(message);
        return response;
    }

    // ===================== 业务方法 =====================

    /**
     * 是否创建成功
     *
     * @return true-成功，false-失败
     */
    public boolean isCreateSuccess() {
        return isSuccess() && followId != null;
    }

    /**
     * 是否为新创建的关注
     *
     * @return true-新创建，false-已存在或失败
     */
    public boolean isNewlyCreated() {
        return Boolean.TRUE.equals(isNewCreate);
    }

    /**
     * 是否已存在
     *
     * @return true-已存在，false-新创建或失败
     */
    public boolean isAlreadyExists() {
        return Boolean.FALSE.equals(isNewCreate);
    }
}