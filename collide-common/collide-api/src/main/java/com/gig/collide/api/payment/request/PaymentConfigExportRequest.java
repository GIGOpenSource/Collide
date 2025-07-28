package com.gig.collide.api.payment.request;

import com.gig.collide.base.request.BaseRequest;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * 支付配置导出请求
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
public class PaymentConfigExportRequest extends BaseRequest {

    /**
     * 导出文件格式（JSON、XML、YAML等）
     */
    @NotBlank(message = "导出文件格式不能为空")
    private String exportFormat;

    /**
     * 环境标识
     */
    @NotBlank(message = "环境标识不能为空")
    private String envProfile;

    /**
     * 指定配置ID列表（为空则导出所有）
     */
    private List<Long> configIds;

    /**
     * 配置类型过滤
     */
    private List<String> configTypes;

    /**
     * 是否包含敏感信息
     */
    private Boolean includeSensitive = false;

    /**
     * 导出说明
     */
    private String exportRemark;
} 