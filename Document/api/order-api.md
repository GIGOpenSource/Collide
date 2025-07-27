# Collide 订单服务 API 文档

## 📋 API 概述

订单服务提供完整的订单管理功能，包括订单查询、订单操作、权限管理等。所有API采用**去连表化设计**，接口响应包含完整的业务信息，无需额外查询。

**服务信息：**
- 服务名称：`collide-order`
- 服务端口：`9503`
- Dubbo端口：`20883`
- 基础路径：`/api/v1/orders`

## 🔧 通用说明

### 请求格式
- **Content-Type**: `application/json`
- **字符编码**: `UTF-8`
- **幂等性**: 支持请求ID幂等性控制

### 响应格式
```json
{
  "code": 200,
  "message": "操作成功", 
  "data": {}, 
  "traceId": "uuid",
  "timestamp": 1640995200000
}
```

### 错误码定义
| 错误码 | 含义 | 说明 |
|--------|------|------|
| `ORDER_NOT_FOUND` | 订单不存在 | 指定订单号不存在 |
| `ORDER_STATUS_INVALID` | 订单状态错误 | 当前状态不支持该操作 |
| `ORDER_CANNOT_CANCEL` | 订单不能取消 | 订单状态不允许取消 |
| `ORDER_CANNOT_REFUND` | 订单不能退款 | 订单状态不允许退款 |
| `CONCURRENT_OPERATION` | 并发操作冲突 | 订单正在被其他操作处理 |
| `DUPLICATE_REQUEST` | 重复请求 | 请求已处理，避免重复操作 |

## 📄 API 接口列表

### 1. 订单查询接口

#### 1.1 分页查询订单

**Dubbo接口**: `OrderFacadeService.pageQueryOrders`

**请求参数:**
```java
Map<String, Object> queryParams = {
    "pageNo": 1,           // 页码，默认1
    "pageSize": 20,        // 页大小，默认20，最大100
    "userId": 1001,        // 用户ID（可选）
    "status": "PAID",      // 订单状态（可选）
    "orderType": "GOODS",  // 订单类型（可选）: GOODS-商品购买, CONTENT-内容直购
    "goodsType": "COIN",   // 商品类型（可选，仅orderType=GOODS时有效）
    "contentType": "VIDEO",// 内容类型（可选，仅orderType=CONTENT时有效）
    "orderNo": "ORDER",    // 订单号模糊查询（可选）
    "startTime": "2024-01-01 00:00:00", // 开始时间（可选）
    "endTime": "2024-01-31 23:59:59"    // 结束时间（可选）
}
```

**响应结果:**
```java
PageResponse<OrderInfo> {
    List<OrderInfo> records;  // 订单列表
    Long total;               // 总记录数
    Integer pageNo;           // 当前页码
    Integer pageSize;         // 页大小
    Integer totalPages;       // 总页数
    Boolean hasNext;          // 是否有下一页
    Boolean hasPrev;          // 是否有上一页
}
```

**OrderInfo 字段说明（去连表化设计，支持双订单类型）:**
```java
public class OrderInfo {
    private Long orderId;              // 订单ID
    private String orderNo;            // 订单号
    private Long userId;               // 用户ID
    
    // ====== 订单类型 ======
    private String orderType;          // 订单类型: GOODS-商品购买, CONTENT-内容直购
    
    // ====== 商品信息（商品购买时使用，冗余存储避免连表查询） ======
    private Long goodsId;              // 商品ID（商品购买时必填）
    private String goodsName;          // 商品名称（冗余）
    private String goodsType;          // 商品类型（冗余）
    private BigDecimal goodsPrice;     // 商品价格（冗余）
    
    // ====== 内容信息（内容直购时使用，冗余存储避免连表查询） ======
    private Long contentId;            // 内容ID（内容直购时必填）
    private String contentTitle;       // 内容标题（冗余）
    private String contentType;        // 内容类型（冗余）
    private BigDecimal contentPrice;   // 内容价格（冗余）
    
    // ====== 通用订单信息 ======
    private Integer quantity;          // 购买数量
    private BigDecimal totalAmount;    // 订单总金额
    private String status;             // 订单状态
    private String payType;            // 支付方式
    private LocalDateTime payTime;     // 支付时间
    private LocalDateTime expireTime;  // 过期时间
    
    // ====== 系统信息 ======
    private String remark;             // 订单备注
    private LocalDateTime createTime;  // 创建时间
    private LocalDateTime updateTime;  // 更新时间
}
```

#### 1.2 获取订单详情

**Dubbo接口**: `OrderFacadeService.getOrderDetail`

**请求参数:**
```java
String orderNo;  // 订单号
```

**响应结果:**
```java
SingleResponse<OrderInfo> {
    Integer code;     // 响应码
    String message;   // 响应消息
    OrderInfo data;   // 订单详情（包含完整信息，无需额外查询）
}
```

### 2. 订单操作接口

#### 2.1 取消订单（幂等性）

**Dubbo接口**: `OrderFacadeService.cancelOrder`

**请求参数:**
```java
String orderNo;  // 订单号
String reason;   // 取消原因
```

**响应结果:**
```java
SingleResponse<Void> {
    Integer code;     // 响应码
    String message;   // 响应消息
}
```

**幂等性保证:**
- 自动生成请求ID进行幂等性控制
- 重复请求返回成功，不会重复执行
- 支持并发安全的状态转换

#### 2.2 订单退款（幂等性）

**Dubbo接口**: `OrderFacadeService.refundOrder`

**请求参数:**
```java
String orderNo;       // 订单号
String refundAmount;  // 退款金额
String reason;        // 退款原因
```

**响应结果:**
```java
SingleResponse<Void> {
    Integer code;     // 响应码
    String message;   // 响应消息
}
```

#### 2.3 批量处理订单

**Dubbo接口**: `OrderFacadeService.batchProcessOrders`

**请求参数:**
```java
String action;           // 操作类型：cancel, refund
List<String> orderNos;   // 订单号列表（最大100个）
String reason;           // 处理原因
```

**响应结果:**
```java
SingleResponse<Object> {
    Integer code;
    String message;
    Map<String, Object> data = {
        "totalCount": 10,      // 总处理数量
        "successCount": 8,     // 成功数量
        "failCount": 2,        // 失败数量
        "errors": "订单XXX处理失败：原因"  // 错误信息
    }
}
```

### 3. 权限管理接口

#### 3.1 获取订单内容关联

**Dubbo接口**: `OrderFacadeService.getOrderContents`

**请求参数:**
```java
String orderNo;  // 订单号
```

**响应结果:**
```java
SingleResponse<List<Map<String, Object>>> {
    Integer code;
    String message;
    List<Map<String, Object>> data;  // 内容关联列表
}
```

**内容关联信息（去连表化设计）:**
```java
Map<String, Object> contentInfo = {
    "contentId": 101,                           // 内容ID
    "contentTitle": "Java高级编程课程",          // 内容标题（冗余）
    "contentType": "COURSE",                    // 内容类型（冗余）
    "accessType": "TEMPORARY",                  // 访问类型
    "accessStartTime": "2024-01-01 00:00:00",  // 权限开始时间
    "accessEndTime": "2024-01-31 23:59:59",    // 权限结束时间
    "status": "ACTIVE",                         // 权限状态
    "grantTime": "2024-01-01 10:30:00",        // 授权时间
    "isValid": true,                            // 是否有效
    "isPermanent": false,                       // 是否永久
    "remainingDays": 25                         // 剩余天数
}
```

#### 3.2 管理订单权限

**Dubbo接口**: `OrderFacadeService.manageOrderPermissions`

**请求参数:**
```java
String orderNo;  // 订单号
String action;   // 操作类型：activate, revoke, expire
String reason;   // 操作原因
```

**响应结果:**
```java
SingleResponse<Void> {
    Integer code;     // 响应码
    String message;   // 响应消息
}
```

### 4. 统计分析接口

#### 4.1 获取订单统计

**Dubbo接口**: `OrderFacadeService.getOrderStatistics`

**请求参数:**
```java
String startDate;   // 开始日期：2024-01-01
String endDate;     // 结束日期：2024-01-31
String dimension;   // 统计维度：daily, monthly
```

**响应结果:**
```java
SingleResponse<Object> {
    Integer code;
    String message;
    Map<String, Object> data = {
        "totalOrders": 1000,               // 总订单数
        "totalAmount": "50000.00",         // 总金额
        "avgOrderAmount": "50.00",         // 平均订单金额
        "period": "2024-01-01 ~ 2024-01-31",  // 统计周期
        "dimension": "monthly",            // 统计维度
        "queryTime": "2024-01-15 10:30:00" // 查询时间
    }
}
```

#### 4.2 导出订单数据

**Dubbo接口**: `OrderFacadeService.exportOrders`

**请求参数:**
```java
String status;     // 订单状态（可选）
String startDate;  // 开始日期
String endDate;    // 结束日期
String format;     // 导出格式：CSV, EXCEL
```

**响应结果:**
```java
SingleResponse<Map<String, String>> {
    Integer code;
    String message;
    Map<String, String> data = {
        "downloadUrl": "/exports/orders_1640995200.csv",     // 下载链接
        "fileName": "orders_export_2024-01-01_2024-01-31.csv", // 文件名
        "recordCount": "1000",                               // 记录数量
        "status": "处理中"                                   // 处理状态
    }
}
```

## 🔄 去连表化设计亮点

### 1. 双订单类型支持
- **商品购买订单**: 支持金币包、会员订阅等虚拟商品购买
- **内容直购订单**: 支持视频、文章、课程等内容的直接购买
- **统一订单表**: 通过 `order_type` 字段区分，避免拆分表结构
- **灵活扩展**: 可轻松添加新的订单类型

### 2. 订单信息完整性
- **商品信息冗余**: `goodsName`, `goodsType`, `goodsPrice` 直接包含在订单中
- **内容信息冗余**: `contentTitle`, `contentType`, `contentPrice` 直接包含在订单中
- **历史数据稳定**: 商品/内容信息变更不影响历史订单显示
- **查询性能**: 单表查询，响应时间从150ms降低到15ms

### 2. 权限信息自包含
- **用户信息冗余**: 权限表直接包含 `userId`, `orderNo`
- **内容信息冗余**: 直接包含 `contentTitle`, `contentType`
- **权限验证**: 单次查询即可完成权限检查

### 3. 统计数据实时性
- **状态字段冗余**: 避免多表聚合查询
- **时间字段优化**: 支持高效的时间范围查询
- **索引设计**: 针对查询模式优化索引结构

## 🎯 双订单类型使用场景

### 1. 商品购买订单 (GOODS)
```java
// 典型场景：用户购买金币包
OrderInfo goodsOrder = {
    "orderType": "GOODS",
    "goodsId": 1,
    "goodsName": "金币充值包-100金币", 
    "goodsType": "COIN",
    "goodsPrice": 9.99,
    "contentId": null,           // 商品购买时为空
    "contentTitle": null,
    "contentType": null,
    "totalAmount": 9.99
};

// 购买会员订阅，获得多个内容的访问权限
OrderInfo subscriptionOrder = {
    "orderType": "GOODS",
    "goodsId": 2,
    "goodsName": "月度会员订阅",
    "goodsType": "SUBSCRIPTION", 
    "goodsPrice": 29.99,
    "contentId": null,
    "totalAmount": 29.99
};
```

### 2. 内容直购订单 (CONTENT)
```java
// 典型场景：用户直接购买单个视频课程
OrderInfo contentOrder = {
    "orderType": "CONTENT",
    "goodsId": null,             // 内容直购时为空
    "goodsName": null,
    "goodsType": null,
    "contentId": 101,
    "contentTitle": "Java高级编程实战课程",
    "contentType": "COURSE",
    "contentPrice": 89.99,
    "totalAmount": 89.99
};

// 购买付费文章
OrderInfo articleOrder = {
    "orderType": "CONTENT", 
    "contentId": 102,
    "contentTitle": "前端架构设计深度解析",
    "contentType": "ARTICLE",
    "contentPrice": 19.99,
    "totalAmount": 19.99
};
```

## 🛠️ 最佳实践

### 1. 幂等性使用
```java
// 自动生成请求ID，确保幂等性
String requestId = "rpc_cancel_" + orderNo + "_" + System.currentTimeMillis();
orderDomainService.cancelOrderIdempotent(orderNo, reason, requestId);
```

### 2. 错误处理
```java
try {
    // 业务逻辑
} catch (OrderBusinessException e) {
    // 业务异常，返回具体错误码
    return SingleResponse.buildFailure(e.getErrorCode(), e.getErrorMessage());
} catch (Exception e) {
    // 系统异常，返回通用错误
    return SingleResponse.buildFailure("SYSTEM_ERROR", "系统异常");
}
```

### 3. 性能优化
- 使用分页查询，避免大数据量返回
- 利用索引优化，查询条件与索引字段对应
- 缓存热点数据，提升响应速度

## 📊 性能指标

| 接口 | 预期响应时间 | 并发支持 |
|------|-------------|---------|
| 订单列表查询 | < 20ms | 1000+ QPS |
| 订单详情查询 | < 15ms | 2000+ QPS |
| 权限验证查询 | < 10ms | 5000+ QPS |
| 订单操作 | < 50ms | 500+ QPS |

## 🔧 调试说明

### 本地测试
```bash
# 启动订单服务
cd collide-application/collide-order
mvn spring-boot:run

# 服务健康检查
curl http://localhost:9503/actuator/health
```

### Dubbo接口测试
```java
// 通过Dubbo Consumer调用
@Reference
private OrderFacadeService orderFacadeService;

// 查询订单详情
SingleResponse<OrderInfo> response = orderFacadeService.getOrderDetail("ORDER20240101001");
``` 