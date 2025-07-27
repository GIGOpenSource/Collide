package com.gig.collide.goods.infrastructure.exception;

/**
 * 商品错误码枚举
 * 
 * @author Collide Team
 * @since 2024-01-01
 */
public enum GoodsErrorCode {
    
    // 参数错误
    PARAM_INVALID("G001", "参数无效"),
    GOODS_ID_EMPTY("G002", "商品ID不能为空"),
    
    // 商品基础错误
    GOODS_NOT_FOUND("G101", "商品不存在"),
    GOODS_NOT_AVAILABLE("G102", "商品当前不可购买"),
    INVALID_GOODS_TYPE("G103", "不支持的商品类型"),
    INVALID_OPERATION_TYPE("G104", "不支持的操作类型"),
    
    // 库存相关错误
    STOCK_NOT_ENOUGH("G201", "库存不足"),
    STOCK_UPDATE_CONFLICT("G202", "库存更新冲突"),
    
    // 业务流程错误
    DUPLICATE_REQUEST("G301", "重复请求"),
    CREATE_GOODS_FAILED("G302", "创建商品失败"),
    UPDATE_GOODS_FAILED("G303", "更新商品失败"),
    DELETE_GOODS_FAILED("G304", "删除商品失败"),
    
    // 系统错误
    SYSTEM_ERROR("G500", "系统错误"),
    CONCURRENT_UPDATE("G501", "并发更新失败");
    
    private final String code;
    private final String message;
    
    GoodsErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getMessage() {
        return message;
    }
} 