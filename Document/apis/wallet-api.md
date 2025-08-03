# Collide 钱包服务 API 文档

## 概述

Collide 钱包服务提供安全的用户钱包管理功能，采用严格的安全设计：**金额变动只能通过订单系统内部调用**，对外仅提供查询功能。每个用户拥有唯一钱包，注册时自动创建。

**服务版本**: v2.0.0 (安全版)  
**基础路径**: `/api/v1/users/{userId}/wallet`  
**内部服务**: Dubbo RPC 调用  
**设计理念**: 
- 🔒 **安全第一**：禁止通过HTTP接口直接修改金额
- 💰 **单钱包设计**：每个用户只有一个钱包
- 🔄 **订单驱动**：金额变动只能通过订单系统
- 🚀 **自动创建**：用户注册时自动创建默认钱包

---

## 钱包查询 API（对外接口）

### 1. 获取用户钱包信息
**接口路径**: `GET /api/v1/users/{userId}/wallet`  
**接口描述**: 获取用户钱包详细信息，包括余额、冻结金额、收支统计等

#### 请求参数
- **userId** (path): 用户ID，必填

#### 响应示例
**成功响应 (200)**:
```json
{
  "code": "SUCCESS",
  "success": true,
  "message": null,
  "data": {
    "id": 1,
    "userId": 12345,
    "balance": 1250.50,
    "frozenAmount": 100.00,
    "availableBalance": 1150.50,
    "totalIncome": 5000.00,
    "totalExpense": 3749.50,
    "status": "active",
    "createTime": "2024-01-16T10:30:00",
    "updateTime": "2024-01-20T15:45:00"
  }
}
```

**失败响应 (404)**:
```json
{
  "code": "WALLET_NOT_FOUND",
  "success": false,
  "message": "钱包不存在",
  "data": null
}
```

---

### 2. 检查余额是否充足
**接口路径**: `GET /api/v1/users/{userId}/wallet/balance/check`  
**接口描述**: 检查用户可用余额是否满足指定金额要求

#### 请求参数
- **userId** (path): 用户ID，必填
- **amount** (query): 检查金额，必填

#### 响应示例
**成功响应 (200)**:
```json
{
  "code": "SUCCESS",
  "success": true,
  "message": null,
  "data": true  // 余额充足
}
```

```json
{
  "code": "SUCCESS", 
  "success": true,
  "message": null,
  "data": false // 余额不足
}
```

---

### 3. 获取钱包状态
**接口路径**: `GET /api/v1/users/{userId}/wallet/status`  
**接口描述**: 查询钱包的状态信息

#### 请求参数
- **userId** (path): 用户ID，必填

#### 响应示例
**成功响应 (200)**:
```json
{
  "code": "SUCCESS",
  "success": true,
  "message": null,
  "data": "active"  // 钱包状态：active、frozen
}
```

---

## ⚠️ 重要安全说明

### 禁止的操作
❌ **不再提供以下接口**（已移除）：
- 钱包充值接口
- 钱包提现接口  
- 钱包操作接口
- 资金冻结/解冻接口

### 金额变动规则
✅ **允许的金额变动方式**：
- 用户购买金币类订单 → 订单支付成功后自动增加钱包余额
- 用户消费金币类订单 → 订单确认时自动减少钱包余额
- 订单退款 → 系统自动退款到钱包

❌ **禁止的金额变动方式**：
- 直接通过HTTP接口修改金额
- 手动充值/提现操作
- 绕过订单系统的任何金额变动

---

## 内部服务接口（仅供订单系统调用）

> ⚠️ **重要**：以下接口仅供订单系统内部使用，不对外暴露，通过Dubbo RPC调用

### 1. 钱包扣款（订单支付时调用）
**接口描述**: 用户使用金币支付订单时，订单服务调用此接口扣减钱包余额  
**调用方式**: `userFacadeService.deductWalletBalance()`

#### 方法签名
```java
Result<Void> deductWalletBalance(Long userId, BigDecimal amount, String businessId, String description)
```

#### 参数说明
- **userId**: 用户ID
- **amount**: 扣款金额
- **businessId**: 业务ID（订单号）
- **description**: 操作描述（如"订单支付"）

#### 使用场景
- 用户使用金币购买商品/服务
- 用户支付VIP会员费用
- 用户消费金币类订单

#### 响应示例
```java
// 成功
Result.success(null)

// 失败
Result.error("WALLET_DEDUCT_ERROR", "扣款失败：余额不足")
```

---

### 2. 钱包充值（订单完成时调用）
**接口描述**: 用户购买金币或获得奖励时，订单服务调用此接口增加钱包余额  
**调用方式**: `userFacadeService.addWalletBalance()`

#### 方法签名
```java
Result<Void> addWalletBalance(Long userId, BigDecimal amount, String businessId, String description)
```

#### 参数说明
- **userId**: 用户ID
- **amount**: 充值金额
- **businessId**: 业务ID（订单号）
- **description**: 操作描述（如"购买金币"、"订单退款"）

#### 使用场景
- 用户购买金币充值包
- 订单退款到钱包
- 系统奖励金币

#### 响应示例
```java
// 成功
Result.success(null)

// 失败
Result.error("WALLET_ADD_ERROR", "充值失败：钱包状态异常")
```

---

### 3. 检查钱包余额（订单前检查）
**接口描述**: 订单系统在处理金币支付前，调用此接口检查用户余额是否充足  
**调用方式**: `userFacadeService.checkWalletBalance()`

#### 方法签名
```java
Result<Boolean> checkWalletBalance(Long userId, BigDecimal amount)
```

#### 使用场景
- 订单支付前余额检查
- 购买前资金验证

#### 响应示例
```java
// 余额充足
Result.success(true)

// 余额不足
Result.success(false)
```

---

### 4. 冻结金额（订单锁定资金）
**接口描述**: 订单创建时冻结用户资金，防止重复消费  
**调用方式**: `walletService.freezeAmount()`

#### 方法签名
```java
boolean freezeAmount(Long userId, BigDecimal amount, String businessId, String description)
```

---

### 5. 解冻金额（订单取消时）
**接口描述**: 订单取消时解冻之前锁定的资金  
**调用方式**: `walletService.unfreezeAmount()`

#### 方法签名
```java
boolean unfreezeAmount(Long userId, BigDecimal amount, String businessId, String description)
```

---

## 错误码定义

| 错误码 | 错误描述 | 解决方案 |
|--------|----------|----------|
| WALLET_NOT_FOUND | 钱包不存在 | 用户注册时自动创建钱包 |
| WALLET_QUERY_ERROR | 查询钱包失败 | 检查用户ID和网络连接 |
| BALANCE_CHECK_ERROR | 检查余额失败 | 检查用户ID和金额参数 |
| WALLET_STATUS_ERROR | 查询钱包状态失败 | 检查用户ID是否有效 |
| WALLET_DEDUCT_ERROR | 扣款失败（内部） | 检查余额是否充足，钱包状态 |
| WALLET_ADD_ERROR | 充值失败（内部） | 检查钱包状态和金额 |
| INSUFFICIENT_BALANCE | 余额不足 | 用户需要购买金币充值包 |
| INVALID_AMOUNT | 无效金额 | 金额必须大于0.01 |
| WALLET_FROZEN | 钱包已冻结 | 联系客服解冻钱包 |
| OPERATION_FORBIDDEN | 操作被禁止 | 金额变动只能通过订单系统 |

---

## 业务流程示例

### 1. 用户查询钱包余额流程
```javascript
// 查询用户钱包信息
const walletResponse = await fetch('/api/v1/users/12345/wallet');
const walletData = await walletResponse.json();

console.log('钱包余额:', walletData.data.balance);
console.log('可用余额:', walletData.data.availableBalance);
console.log('冻结金额:', walletData.data.frozenAmount);

// 检查余额是否充足
const checkResponse = await fetch('/api/v1/users/12345/wallet/balance/check?amount=100.00');
const canPay = await checkResponse.json();

if (canPay.data) {
    console.log('余额充足，可以支付');
} else {
    console.log('余额不足，请购买金币');
}
```

### 2. 金币支付订单流程（订单系统内部）
```java
@Service
public class OrderPaymentService {
    
    @DubboReference
    private UserFacadeService userFacadeService;
    
    public Result<Void> processPayment(Long userId, String orderNo, BigDecimal amount) {
        // 1. 检查余额是否充足
        Result<Boolean> checkResult = userFacadeService.checkWalletBalance(userId, amount);
        if (!checkResult.getSuccess() || !checkResult.getData()) {
            return Result.error("INSUFFICIENT_BALANCE", "余额不足，请购买金币充值包");
        }
        
        // 2. 执行扣款
        Result<Void> deductResult = userFacadeService.deductWalletBalance(
            userId, amount, orderNo, "订单支付：" + orderNo
        );
        
        if (deductResult.getSuccess()) {
            // 3. 更新订单状态为已支付
            updateOrderStatus(orderNo, "PAID");
            log.info("订单支付成功: orderNo={}, userId={}, amount={}", orderNo, userId, amount);
            return Result.success(null);
        } else {
            // 4. 支付失败处理
            log.error("订单支付失败: orderNo={}, error={}", orderNo, deductResult.getMessage());
            return deductResult;
        }
    }
}
```

### 3. 用户购买金币流程（订单系统内部）
```java
@Service
public class CoinPurchaseService {
    
    @DubboReference
    private UserFacadeService userFacadeService;
    
    public Result<Void> processCoinPurchase(Long userId, String orderNo, BigDecimal coinAmount) {
        // 用户购买金币，第三方支付成功后增加钱包余额
        Result<Void> addResult = userFacadeService.addWalletBalance(
            userId, coinAmount, orderNo, "购买金币：" + orderNo
        );
        
        if (addResult.getSuccess()) {
            // 更新订单状态
            updateOrderStatus(orderNo, "COMPLETED");
            log.info("金币购买成功: orderNo={}, userId={}, amount={}", orderNo, userId, coinAmount);
            return Result.success(null);
        } else {
            log.error("金币充值失败: orderNo={}, error={}", orderNo, addResult.getMessage());
            return addResult;
        }
    }
}
```

### 4. 订单退款流程（订单系统内部）
```java
@Service
public class RefundService {
    
    @DubboReference
    private UserFacadeService userFacadeService;
    
    public Result<Void> processRefund(Long userId, String orderNo, BigDecimal refundAmount) {
        // 将退款金额退回用户钱包
        Result<Void> refundResult = userFacadeService.addWalletBalance(
            userId, refundAmount, orderNo, "订单退款：" + orderNo
        );
        
        if (refundResult.getSuccess()) {
            // 更新订单状态为已退款
            updateOrderStatus(orderNo, "REFUNDED");
            log.info("订单退款成功: orderNo={}, userId={}, amount={}", orderNo, userId, refundAmount);
            return Result.success(null);
        } else {
            // 退款失败处理
            log.error("订单退款失败: orderNo={}, error={}", orderNo, refundResult.getMessage());
            handleRefundFailure(orderNo);
            return refundResult;
        }
    }
}
```

### 5. 资金冻结/解冻流程（订单系统内部）
```java
@Service
public class OrderReservationService {
    
    @Autowired
    private WalletService walletService;
    
    public Result<Void> reserveAmount(Long userId, String orderNo, BigDecimal amount) {
        // 1. 预订商品时冻结资金
        boolean freezeSuccess = walletService.freezeAmount(
            userId, amount, orderNo, "商品预订冻结：" + orderNo
        );
        
        if (freezeSuccess) {
            log.info("资金冻结成功: orderNo={}, userId={}, amount={}", orderNo, userId, amount);
            return Result.success(null);
        } else {
            return Result.error("FREEZE_FAILED", "资金冻结失败");
        }
    }
    
    public Result<Void> confirmPurchase(Long userId, String orderNo, BigDecimal amount) {
        // 2. 确认购买时解冻资金并扣款
        boolean unfreezeSuccess = walletService.unfreezeAmount(
            userId, amount, orderNo, "确认购买解冻：" + orderNo
        );
        
        if (unfreezeSuccess) {
            // 然后执行扣款
            return processPayment(userId, orderNo, amount);
        } else {
            return Result.error("UNFREEZE_FAILED", "资金解冻失败");
        }
    }
}

---

## 安全性说明

### 1. 核心安全原则 🔒
- **金额变动隔离**：HTTP接口完全禁止金额修改操作
- **订单系统专属**：只有订单系统可以通过内部接口变动金额
- **单钱包设计**：每个用户唯一钱包，防止多钱包安全风险
- **自动创建**：用户注册时自动创建钱包，避免手动创建漏洞

### 2. 数据安全
- 所有金额操作使用 `BigDecimal` 类型，确保精度
- 数据库层面使用原子操作，防止并发问题
- 所有操作都有事务保护
- 严格的数据校验和边界检查

### 3. 权限控制
- **HTTP接口**：用户只能查询自己的钱包信息
- **内部接口**：通过 Dubbo RPC 调用，不对外暴露
- **操作权限**：金额变动只能由认证的订单系统发起
- **身份验证**：所有操作都需要用户身份验证

### 4. 操作审计
- 所有钱包操作都有详细日志记录
- 支持操作追踪和审计
- 异常情况自动告警
- 完整的业务流程追踪

### 5. 风控机制
- **余额检查**：支付前强制检查余额充足性
- **状态管控**：支持钱包冻结和解冻
- **异常监控**：异常操作自动风控
- **资金安全**：冻结机制防止重复扣款

---

## 技术实现

### 1. 数据库设计（基于users-simple.sql）
```sql
CREATE TABLE `t_user_wallet` (
    `id`           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '钱包ID',
    `user_id`      BIGINT       NOT NULL                COMMENT '用户ID',
    `balance`      DECIMAL(15,2) NOT NULL DEFAULT 0.00  COMMENT '余额',
    `frozen_amount` DECIMAL(15,2) NOT NULL DEFAULT 0.00 COMMENT '冻结金额',
    `total_income` DECIMAL(15,2) NOT NULL DEFAULT 0.00  COMMENT '总收入',
    `total_expense` DECIMAL(15,2) NOT NULL DEFAULT 0.00 COMMENT '总支出',
    `status`       VARCHAR(20)  NOT NULL DEFAULT 'active' COMMENT '状态：active、frozen',
    `create_time`  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_id` (`user_id`)  -- 🔑 确保每个用户只有一个钱包
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户钱包表';
```

### 2. 架构特性
- **微服务架构**：通过 Dubbo 提供内部服务
- **分层设计**：Controller-Facade-Service-Mapper 四层架构
- **安全隔离**：HTTP接口与内部接口完全分离
- **数据一致性**：使用 MyBatis 事务保护

### 3. 核心组件
```java
// HTTP控制器（仅查询）
@RestController
@RequestMapping("/api/v1/users/{userId}/wallet")
public class WalletController

// 门面服务（Dubbo提供者）
@DubboService
public class UserFacadeServiceImpl implements UserFacadeService

// 业务逻辑层
@Service  
public class WalletServiceImpl implements WalletService

// 数据访问层
@Mapper
public interface UserWalletMapper
```

### 4. 性能优化
- **数据库优化**：唯一索引避免重复钱包
- **连接池优化**：数据库连接池配置
- **异步日志**：操作日志异步记录
- **缓存支持**：热点数据缓存

### 5. 关键设计决策
- ✅ **单钱包设计**：基于SQL约束的安全保证
- ✅ **查询分离**：HTTP接口只提供查询功能
- ✅ **内部专用**：金额变动接口只供订单系统使用
- ✅ **自动创建**：用户注册时自动创建钱包

---

## 📝 文档更新记录

### v2.0.0 (安全版) - 2024-01-16
- 🔒 **重大安全更新**：移除所有HTTP金额修改接口
- 💰 **单钱包设计**：基于SQL约束确保每用户一钱包
- 🚫 **接口精简**：只保留查询、余额检查、状态查询接口
- 🔄 **订单驱动**：金额变动完全由订单系统控制
- 🚀 **自动创建**：用户注册时自动创建默认钱包
- 📖 **文档重构**：更新所有API示例和业务流程

### 安全升级说明
此版本是重大安全升级，**不向后兼容**。原有的充值、提现、操作接口已完全移除，请使用订单系统进行金额变动操作。

---

**注意**: 本文档基于 Collide v2.0.0 (安全版) 微服务架构设计，钱包功能集成在用户服务中，严格遵循单钱包安全设计原则。