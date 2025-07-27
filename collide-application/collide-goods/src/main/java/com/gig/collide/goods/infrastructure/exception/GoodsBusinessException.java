package com.gig.collide.goods.infrastructure.exception;

/**
 * 商品业务异常
 * 
 * @author Collide Team
 * @since 2024-01-01
 */
public class GoodsBusinessException extends RuntimeException {

    private final GoodsErrorCode errorCode;
    private final Object data;

    public GoodsBusinessException(GoodsErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.data = null;
    }

    public GoodsBusinessException(GoodsErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.data = null;
    }

    public GoodsBusinessException(GoodsErrorCode errorCode, String message, Object data) {
        super(message);
        this.errorCode = errorCode;
        this.data = data;
    }

    public GoodsBusinessException(GoodsErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.data = null;
    }

    public GoodsBusinessException(GoodsErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
        this.data = null;
    }

    public GoodsErrorCode getErrorCode() {
        return errorCode;
    }

    public Object getData() {
        return data;
    }

    /**
     * 静态工厂方法 - 创建简单业务异常
     */
    public static GoodsBusinessException of(GoodsErrorCode errorCode) {
        return new GoodsBusinessException(errorCode);
    }

    /**
     * 静态工厂方法 - 创建带自定义消息的业务异常
     */
    public static GoodsBusinessException of(GoodsErrorCode errorCode, String message) {
        return new GoodsBusinessException(errorCode, message);
    }

    /**
     * 静态工厂方法 - 创建带数据的业务异常
     */
    public static GoodsBusinessException of(GoodsErrorCode errorCode, String message, Object data) {
        return new GoodsBusinessException(errorCode, message, data);
    }

    /**
     * 静态工厂方法 - 创建带原因的业务异常
     */
    public static GoodsBusinessException of(GoodsErrorCode errorCode, String message, Throwable cause) {
        return new GoodsBusinessException(errorCode, message, cause);
    }
    
    // 商品相关的常见异常静态方法
    
    public static GoodsBusinessException goodsNotFound(Long goodsId) {
        return new GoodsBusinessException(GoodsErrorCode.GOODS_NOT_FOUND, "商品不存在：" + goodsId);
    }
    
    public static GoodsBusinessException goodsNotAvailable(Long goodsId) {
        return new GoodsBusinessException(GoodsErrorCode.GOODS_NOT_AVAILABLE, "商品当前不可购买：" + goodsId);
    }
    
    public static GoodsBusinessException stockNotEnough(Long goodsId, Integer requestStock, Integer availableStock) {
        return new GoodsBusinessException(GoodsErrorCode.STOCK_NOT_ENOUGH, 
            String.format("库存不足，商品ID：%d，请求数量：%d，可用库存：%d", goodsId, requestStock, availableStock));
    }
    
    public static GoodsBusinessException stockUpdateConflict(Long goodsId) {
        return new GoodsBusinessException(GoodsErrorCode.STOCK_UPDATE_CONFLICT, 
            "库存更新冲突，请重试，商品ID：" + goodsId);
    }
    
    public static GoodsBusinessException invalidGoodsType(String type) {
        return new GoodsBusinessException(GoodsErrorCode.INVALID_GOODS_TYPE, "不支持的商品类型：" + type);
    }
    
    public static GoodsBusinessException invalidOperationType(String operationType) {
        return new GoodsBusinessException(GoodsErrorCode.INVALID_OPERATION_TYPE, "不支持的操作类型：" + operationType);
    }
    
    public static GoodsBusinessException duplicateRequest(String idempotentKey) {
        return new GoodsBusinessException(GoodsErrorCode.DUPLICATE_REQUEST, "重复请求：" + idempotentKey);
    }
} 