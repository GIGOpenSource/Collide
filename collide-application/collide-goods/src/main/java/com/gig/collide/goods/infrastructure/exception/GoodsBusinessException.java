package com.gig.collide.goods.infrastructure.exception;

import com.gig.collide.base.exception.BusinessException;

/**
 * 商品业务异常
 * 
 * @author Collide Team
 * @since 2024-01-01
 */
public class GoodsBusinessException extends BusinessException {
    
    public GoodsBusinessException(String message) {
        super(message);
    }
    
    public GoodsBusinessException(String code, String message) {
        super(code, message);
    }
    
    public GoodsBusinessException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public GoodsBusinessException(String code, String message, Throwable cause) {
        super(code, message, cause);
    }
    
    // 商品相关的常见异常
    
    public static GoodsBusinessException goodsNotFound(Long goodsId) {
        return new GoodsBusinessException("GOODS_NOT_FOUND", "商品不存在：" + goodsId);
    }
    
    public static GoodsBusinessException goodsNotAvailable(Long goodsId) {
        return new GoodsBusinessException("GOODS_NOT_AVAILABLE", "商品当前不可购买：" + goodsId);
    }
    
    public static GoodsBusinessException stockNotEnough(Long goodsId, Integer requestStock, Integer availableStock) {
        return new GoodsBusinessException("STOCK_NOT_ENOUGH", 
            String.format("库存不足，商品ID：%d，请求数量：%d，可用库存：%d", goodsId, requestStock, availableStock));
    }
    
    public static GoodsBusinessException stockUpdateConflict(Long goodsId) {
        return new GoodsBusinessException("STOCK_UPDATE_CONFLICT", 
            "库存更新冲突，请重试，商品ID：" + goodsId);
    }
    
    public static GoodsBusinessException invalidGoodsType(String type) {
        return new GoodsBusinessException("INVALID_GOODS_TYPE", "不支持的商品类型：" + type);
    }
    
    public static GoodsBusinessException invalidOperationType(String operationType) {
        return new GoodsBusinessException("INVALID_OPERATION_TYPE", "不支持的操作类型：" + operationType);
    }
    
    public static GoodsBusinessException duplicateRequest(String idempotentKey) {
        return new GoodsBusinessException("DUPLICATE_REQUEST", "重复请求：" + idempotentKey);
    }
} 