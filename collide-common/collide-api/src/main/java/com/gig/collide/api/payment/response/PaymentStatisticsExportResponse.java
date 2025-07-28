package com.gig.collide.api.payment.response;

import com.gig.collide.base.response.BaseResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 支付统计导出响应
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
public class PaymentStatisticsExportResponse extends BaseResponse {

    /**
     * 导出文件ID
     */
    private String exportFileId;

    /**
     * 文件内容（Base64编码或文件路径）
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
     * 文件大小（字节）
     */
    private Long fileSize;

    /**
     * 导出数据行数
     */
    private Integer dataRowCount;

    /**
     * 导出时间戳
     */
    private Long exportTimestamp;

    /**
     * 下载链接
     */
    private String downloadUrl;

    /**
     * 链接过期时间
     */
    private Long urlExpireTime;
} 