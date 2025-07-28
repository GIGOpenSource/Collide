package com.gig.collide.api.payment.response;

import com.gig.collide.base.response.BaseResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.Map;

/**
 * 支付配置导入响应
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
public class PaymentConfigImportResponse extends BaseResponse {

    /**
     * 导入成功数量
     */
    private Integer successCount;

    /**
     * 导入失败数量
     */
    private Integer failedCount;

    /**
     * 总计数量
     */
    private Integer totalCount;

    /**
     * 成功导入的配置ID列表
     */
    private List<Long> successConfigIds;

    /**
     * 失败的配置信息
     */
    private List<Map<String, Object>> failedConfigs;

    /**
     * 导入时间戳
     */
    private Long importTimestamp;

    /**
     * 导入批次号
     */
    private String importBatchNo;
} 