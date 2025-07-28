package com.gig.collide.api.search.request;

import com.gig.collide.base.request.BaseRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * 搜索建议导入请求
 * 
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class SearchSuggestionImportRequest extends BaseRequest {

    private static final long serialVersionUID = 1L;

    /**
     * 导入类型（必填）：file/url/text
     */
    @NotBlank(message = "导入类型不能为空")
    @Pattern(regexp = "^(file|url|text)$", message = "导入类型只能是file、url或text")
    private String importType;

    /**
     * 文件路径或URL（导入类型为file或url时必填）
     */
    @Size(max = 500, message = "文件路径或URL长度不能超过500个字符")
    private String filePathOrUrl;

    /**
     * 文本内容（导入类型为text时必填）
     */
    @Size(max = 10000, message = "文本内容长度不能超过10000个字符")
    private String textContent;

    /**
     * 数据格式（必填）：json/csv/excel/txt
     */
    @NotBlank(message = "数据格式不能为空")
    @Pattern(regexp = "^(json|csv|excel|txt)$", message = "数据格式只能是json、csv、excel或txt")
    private String dataFormat;

    /**
     * 字段映射配置（可选，JSON格式）
     */
    @Size(max = 2000, message = "字段映射配置长度不能超过2000个字符")
    private String fieldMapping;

    /**
     * 默认建议类型（可选）
     */
    private String defaultSuggestionType;

    /**
     * 默认内容类型（可选）
     */
    private String defaultContentType;

    /**
     * 默认搜索类型（可选）
     */
    private String defaultSearchType;

    /**
     * 默认权重（可选，默认为100）
     */
    private Integer defaultWeight = 100;

    /**
     * 是否跳过重复项（可选，默认为true）
     */
    private Boolean skipDuplicates = true;

    /**
     * 是否校验数据（可选，默认为true）
     */
    private Boolean validateData = true;

    /**
     * 批量大小（可选，默认为1000）
     */
    private Integer batchSize = 1000;

    /**
     * 导入人ID（必填）
     */
    @NotNull(message = "导入人ID不能为空")
    private Long importerId;

    /**
     * 导入备注（可选）
     */
    @Size(max = 200, message = "导入备注长度不能超过200个字符")
    private String remark;

    /**
     * 是否异步处理（可选，默认为false）
     */
    private Boolean asyncProcess = false;

    /**
     * 回调URL（异步处理时可选）
     */
    @Size(max = 500, message = "回调URL长度不能超过500个字符")
    private String callbackUrl;
} 