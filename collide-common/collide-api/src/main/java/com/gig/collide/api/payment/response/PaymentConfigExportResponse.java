package com.gig.collide.api.payment.response;

import com.gig.collide.base.response.BaseResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 支付配置导出响应
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
public class PaymentConfigExportResponse extends BaseResponse {

    /**
     * 导出文件内容
     */
    private String fileContent;

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 文件格式
     */
    private String fileFormat;

    /**
     * 导出配置数量
     */
    private Integer configCount;

    /**
     * 导出时间戳
     */
    private Long exportTimestamp;

    /**
     * 文件大小（字节）
     */
    private Long fileSize;
} 