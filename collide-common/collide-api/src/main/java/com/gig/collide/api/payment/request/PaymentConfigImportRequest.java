package com.gig.collide.api.payment.request;

import com.gig.collide.base.request.BaseRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.Map;

/**
 * 支付配置导入请求
 * 
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
public class PaymentConfigImportRequest extends BaseRequest {

    /**
     * 导入文件内容
     */
    @NotBlank(message = "导入文件内容不能为空")
    private String fileContent;

    /**
     * 文件类型（JSON、XML、YAML等）
     */
    @NotBlank(message = "文件类型不能为空")
    private String fileType;

    /**
     * 目标环境
     */
    @NotBlank(message = "目标环境不能为空")
    private String targetEnv;

    /**
     * 导入配置列表
     */
    @NotEmpty(message = "导入配置列表不能为空")
    private List<Map<String, Object>> configList;

    /**
     * 是否覆盖已存在的配置
     */
    private Boolean overwriteExisting = false;

    /**
     * 导入说明
     */
    private String importRemark;
} 