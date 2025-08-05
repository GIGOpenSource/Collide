# Content Purchase Controller REST API 文档

**控制器**: ContentPurchaseController  
**版本**: 2.0.0 (极简版)  
**基础路径**: `/api/v1/content/purchase`  
**接口数量**: 20个  
**更新时间**: 2024-01-31  

## 🚀 概述

内容购买管理控制器 - 极简版，基于12个核心Facade方法设计的精简API。提供用户内容购买记录的管理、查询和统计功能。

**设计理念**:
- **极简设计**: 20个API接口替代原有15个接口，增加便民接口
- **万能查询**: 单个查询接口替代多个具体查询接口
- **统一权限**: 集中的权限验证机制
- **高效批量**: 支持批量操作，提升性能

**主要功能**:
- **权限管理**: 购买权限验证、访问权限检查
- **记录管理**: 购买记录的查询和管理
- **状态跟踪**: 购买状态变更和生命周期管理
- **统计分析**: 购买统计和数据分析
- **业务逻辑**: 购买完成、退款等核心业务流程

**购买状态流转**:
```
ACTIVE(有效) → EXPIRED(过期) → REFUNDED(已退款)
            ↘ CANCELLED(已取消)
```

## 📋 接口分类

| 分类 | 接口数量 | 功能描述 |
|------|----------|----------|
| **核心CRUD功能** | 2个 | 购买记录查询和删除 |
| **万能查询功能** | 6个 | 条件查询、推荐查询、过期查询 + 3个便民接口 |
| **权限验证功能** | 2个 | 访问权限检查 + 1个便民接口 |
| **状态管理功能** | 3个 | 状态更新、批量操作 + 1个便民接口 |
| **统计功能** | 2个 | 购买统计信息 + 1个便民接口 |
| **业务逻辑功能** | 5个 | 购买完成、退款、访问记录 + 2个便民接口 |

---

## 🔧 1. 核心CRUD功能 (2个接口)

### 1.1 获取购买记录详情

**接口**: `GET /api/v1/content/purchase/{id}`

**描述**: 根据购买记录ID获取详情

**路径参数**:
- `id` (Long): 购买记录ID

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 12345,
    "userId": 1001,
    "userNickname": "用户昵称",
    "contentId": 67890,
    "contentTitle": "我的玄幻小说",
    "contentType": "NOVEL",
    "authorId": 2001,
    "authorNickname": "知名作家",
    "orderId": 111222,
    "orderNo": "ORDER2024010112345",
    "paymentType": "COIN_PAY",
    "originalPrice": 100,
    "actualPrice": 80,
    "discountAmount": 20,
    "discountReason": "VIP折扣",
    "status": "ACTIVE",
    "purchaseTime": "2024-01-01T10:00:00",
    "expiryTime": "2024-12-31T23:59:59",
    "accessCount": 15,
    "lastAccessTime": "2024-01-15T14:30:00",
    "isRead": true,
    "totalReadTime": 7200
  }
}
```

### 1.2 删除购买记录

**接口**: `DELETE /api/v1/content/purchase/{id}`

**描述**: 逻辑删除指定的购买记录

**路径参数**:
- `id` (Long): 购买记录ID

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": true
}
```

---

## 🔍 2. 万能查询功能 (6个接口)

### 2.1 万能条件查询购买记录 ⭐

**接口**: `GET /api/v1/content/purchase/query`

**描述**: 根据多种条件查询购买记录列表，替代所有具体查询API

**核心功能**: 
- 替代`getUserPurchases`、`getContentPurchases`、`getUserValidPurchases`、`getUserPurchasesByContentType`等方法
- 支持按用户、内容、订单等多维度查询
- 支持金额范围和有效性筛选

**查询参数**:
- `userId` (Long, 可选): 用户ID
- `contentId` (Long, 可选): 内容ID
- `contentType` (String, 可选): 内容类型
- `orderId` (Long, 可选): 订单ID
- `orderNo` (String, 可选): 订单号
- `status` (String, 可选): 状态
- `isValid` (Boolean, 可选): 是否有效（true=未过期，false=已过期）
- `minAmount` (Long, 可选): 最小金额
- `maxAmount` (Long, 可选): 最大金额
- `orderBy` (String, 可选): 排序字段（createTime、purchaseAmount、accessCount），默认"createTime"
- `orderDirection` (String, 可选): 排序方向（ASC、DESC），默认"DESC"
- `currentPage` (Integer, 可选): 当前页码
- `pageSize` (Integer, 可选): 页面大小

**调用示例**:
```bash
# 查询用户的购买记录（按购买时间排序）
GET /api/v1/content/purchase/query?userId=1001&orderBy=createTime&orderDirection=DESC&currentPage=1&pageSize=20

# 查询指定内容的购买记录
GET /api/v1/content/purchase/query?contentId=67890&orderBy=createTime&orderDirection=DESC&currentPage=1&pageSize=50

# 查询用户有效的购买记录
GET /api/v1/content/purchase/query?userId=1001&status=ACTIVE&isValid=true

# 查询高消费记录（金额>100）
GET /api/v1/content/purchase/query?minAmount=100&orderBy=purchaseAmount&orderDirection=DESC&currentPage=1&pageSize=20
```

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 12345,
        "userId": 1001,
        "contentTitle": "我的玄幻小说",
        "contentType": "NOVEL",
        "actualPrice": 80,
        "status": "ACTIVE",
        "purchaseTime": "2024-01-01T10:00:00",
        "expiryTime": "2024-12-31T23:59:59"
      }
    ],
    "totalCount": 50,
    "totalPage": 3,
    "currentPage": 1,
    "pageSize": 20,
    "hasNext": true,
    "hasPrevious": false
  }
}
```

### 2.2 推荐购买记录查询 ⭐

**接口**: `GET /api/v1/content/purchase/recommendations`

**描述**: 获取推荐的购买记录或内容

**查询参数**:
- `strategy` (String, 必需): 推荐策略（HOT、SIMILAR、RECENT）
- `userId` (Long, 必需): 用户ID
- `contentType` (String, 可选): 内容类型
- `excludeContentIds` (String, 可选): 排除的内容ID列表（逗号分隔）
- `limit` (Integer, 可选): 返回数量限制，默认10

**调用示例**:
```bash
# 获取热门购买推荐
GET /api/v1/content/purchase/recommendations?strategy=HOT&userId=1001&contentType=NOVEL&excludeContentIds=67890,67891&limit=10

# 获取相似购买推荐
GET /api/v1/content/purchase/recommendations?strategy=SIMILAR&userId=1001&limit=20
```

### 2.3 过期相关查询 ⭐

**接口**: `GET /api/v1/content/purchase/expiry`

**描述**: 查询过期相关的购买记录

**查询参数**:
- `type` (String, 必需): 查询类型（EXPIRED、EXPIRING_SOON）
- `beforeTime` (String, 可选): 时间点（ISO格式）
- `userId` (Long, 可选): 用户ID
- `limit` (Integer, 可选): 数量限制

**调用示例**:
```bash
# 查询已过期的购买记录
GET /api/v1/content/purchase/expiry?type=EXPIRED&limit=100

# 查询即将过期的购买记录（7天内）
GET /api/v1/content/purchase/expiry?type=EXPIRING_SOON&beforeTime=2024-02-07T23:59:59&limit=50

# 查询用户即将过期的购买记录
GET /api/v1/content/purchase/expiry?type=EXPIRING_SOON&userId=1001&limit=20
```

### 2.4 获取用户购买记录（便民接口）

**接口**: `GET /api/v1/content/purchase/user/{userId}`

**描述**: 便民接口，获取用户的购买记录

**路径参数**:
- `userId` (Long): 用户ID

**查询参数**:
- `currentPage` (Integer, 必需): 当前页码
- `pageSize` (Integer, 必需): 页面大小

**内部实现**: 调用万能查询接口

### 2.5 获取内容购买记录（便民接口）

**接口**: `GET /api/v1/content/purchase/content/{contentId}`

**描述**: 便民接口，获取指定内容的购买记录

**路径参数**:
- `contentId` (Long): 内容ID

**查询参数**:
- `currentPage` (Integer, 必需): 当前页码
- `pageSize` (Integer, 必需): 页面大小

### 2.6 获取用户有效购买（便民接口）

**接口**: `GET /api/v1/content/purchase/user/{userId}/valid`

**描述**: 便民接口，获取用户的有效购买记录

**路径参数**:
- `userId` (Long): 用户ID

**内部实现**: 调用万能查询接口，筛选有效购买

---

## 🔐 3. 权限验证功能 (2个接口)

### 3.1 检查访问权限 ⭐

**接口**: `GET /api/v1/content/purchase/permission`

**描述**: 检查用户是否有权限访问内容（已购买且未过期）

**查询参数**:
- `userId` (Long, 必需): 用户ID
- `contentId` (Long, 必需): 内容ID

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": true
}
```

### 3.2 批量检查访问权限（便民接口）

**接口**: `POST /api/v1/content/purchase/permission/batch`

**描述**: 便民接口，批量检查用户对多个内容的访问权限

**请求体**:
```json
{
  "userId": 1001,
  "contentIds": [67890, 67891, 67892]
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "67890": true,
    "67891": false,
    "67892": true
  }
}
```

---

## ⚙️ 4. 状态管理功能 (3个接口)

### 4.1 更新购买记录状态 ⭐

**接口**: `PUT /api/v1/content/purchase/{purchaseId}/status`

**描述**: 更新购买记录状态

**路径参数**:
- `purchaseId` (Long): 购买记录ID

**请求体**:
```json
{
  "status": "EXPIRED"
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": true
}
```

### 4.2 批量更新购买记录状态 ⭐

**接口**: `PUT /api/v1/content/purchase/batch/status`

**描述**: 批量更新购买记录状态

**请求体**:
```json
{
  "ids": [12345, 12346, 12347],
  "status": "EXPIRED"
}
```

### 4.3 设置为过期（便民接口）

**接口**: `PUT /api/v1/content/purchase/{purchaseId}/expire`

**描述**: 便民接口，设置购买记录为过期状态

**路径参数**:
- `purchaseId` (Long): 购买记录ID

**内部实现**: 调用状态更新接口，设置状态为"EXPIRED"

---

## 📊 5. 统计功能 (2个接口)

### 5.1 获取购买统计信息 ⭐

**接口**: `GET /api/v1/content/purchase/stats`

**描述**: 获取购买统计信息

**查询参数**:
- `statsType` (String, 必需): 统计类型（USER、CONTENT、DISCOUNT、RANKING、REVENUE_ANALYSIS）
- 其他参数根据统计类型而定

**调用示例**:
```bash
# 获取用户购买统计
GET /api/v1/content/purchase/stats?statsType=USER&userId=1001

# 获取内容销售统计
GET /api/v1/content/purchase/stats?statsType=CONTENT&contentId=67890

# 获取折扣统计
GET /api/v1/content/purchase/stats?statsType=DISCOUNT&userId=1001&startDate=2024-01-01&endDate=2024-01-31
```

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "totalPurchases": 25,
    "validPurchases": 20,
    "expiredPurchases": 3,
    "refundedPurchases": 2,
    "totalExpense": 2500,
    "totalSavings": 500,
    "avgExpensePerPurchase": 100,
    "favoriteContentType": "NOVEL",
    "favoriteAuthor": {
      "authorId": 2001,
      "authorNickname": "知名作家",
      "purchaseCount": 8
    }
  }
}
```

### 5.2 获取用户购买统计（便民接口）

**接口**: `GET /api/v1/content/purchase/user/{userId}/stats`

**描述**: 便民接口，获取用户的购买统计信息

**路径参数**:
- `userId` (Long): 用户ID

**内部实现**: 调用统计接口，statsType=USER

---

## 🎯 6. 业务逻辑功能 (5个接口)

### 6.1 处理内容购买完成 ⭐

**接口**: `POST /api/v1/content/purchase/complete`

**描述**: 处理内容购买完成，创建购买记录

**请求体**:
```json
{
  "userId": 1001,
  "contentId": 67890,
  "orderId": 111222,
  "orderNo": "ORDER2024010112345",
  "purchaseAmount": 80,
  "originalPrice": 100,
  "expireTime": "2024-12-31T23:59:59"
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 12345,
    "userId": 1001,
    "contentId": 67890,
    "actualPrice": 80,
    "status": "ACTIVE",
    "purchaseTime": "2024-01-01T10:00:00",
    "expiryTime": "2024-12-31T23:59:59"
  }
}
```

### 6.2 处理退款 ⭐

**接口**: `POST /api/v1/content/purchase/{purchaseId}/refund`

**描述**: 处理购买记录的退款

**路径参数**:
- `purchaseId` (Long): 购买记录ID

**请求体**:
```json
{
  "refundReason": "用户主动申请退款",
  "refundAmount": 80
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": true
}
```

### 6.3 记录内容访问 ⭐

**接口**: `POST /api/v1/content/purchase/access`

**描述**: 记录用户访问内容，更新访问统计

**请求体**:
```json
{
  "userId": 1001,
  "contentId": 67890
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": true
}
```

### 6.4 订单支付成功处理（便民接口）

**接口**: `POST /api/v1/content/purchase/order/{orderId}/success`

**描述**: 便民接口，处理订单支付成功

**路径参数**:
- `orderId` (Long): 订单ID

**内部实现**: 根据订单信息调用购买完成接口

### 6.5 快速退款（便民接口）

**接口**: `POST /api/v1/content/purchase/{purchaseId}/quick-refund`

**描述**: 便民接口，快速退款处理

**路径参数**:
- `purchaseId` (Long): 购买记录ID

**查询参数**:
- `reason` (String, 可选): 退款原因，默认"系统退款"

**内部实现**: 调用退款接口，自动计算退款金额

---

## 🎯 数据模型

### ContentPurchaseResponse 购买记录响应对象
```json
{
  "id": 12345,                      // 购买记录ID
  "userId": 1001,                   // 用户ID
  "userNickname": "用户昵称",        // 用户昵称
  "contentId": 67890,               // 内容ID
  "contentTitle": "我的玄幻小说",     // 内容标题
  "contentType": "NOVEL",           // 内容类型
  "authorId": 2001,                 // 作者ID
  "authorNickname": "知名作家",      // 作者昵称
  "orderId": 111222,                // 订单ID
  "orderNo": "ORDER2024010112345",  // 订单号
  "paymentType": "COIN_PAY",        // 付费类型
  "originalPrice": 100,             // 原价
  "actualPrice": 80,                // 实际支付价格
  "discountAmount": 20,             // 优惠金额
  "discountReason": "VIP折扣",       // 优惠原因
  "status": "ACTIVE",               // 状态
  "purchaseTime": "2024-01-01T10:00:00", // 购买时间
  "expiryTime": "2024-12-31T23:59:59",   // 过期时间
  "accessCount": 15,                // 访问次数
  "lastAccessTime": "2024-01-15T14:30:00", // 最后访问时间
  "isRead": true,                   // 是否已阅读
  "totalReadTime": 7200,            // 总阅读时长（秒）
  "remainingDays": 365,             // 剩余天数
  "createTime": "2024-01-01T10:00:00",   // 创建时间
  "updateTime": "2024-01-15T14:30:00"    // 更新时间
}
```

## 🚨 错误代码

| HTTP状态码 | 错误码 | 描述 | 解决方案 |
|-----------|--------|------|----------|
| 400 | INVALID_PARAMETER | 参数验证失败 | 检查请求参数的格式和必填项 |
| 404 | PURCHASE_RECORD_NOT_FOUND | 购买记录不存在 | 检查购买记录ID |
| 404 | CONTENT_NOT_FOUND | 内容不存在 | 检查内容ID是否正确 |
| 404 | USER_NOT_FOUND | 用户不存在 | 检查用户ID是否正确 |
| 500 | DELETE_PURCHASE_FAILED | 删除购买记录失败 | 确认操作权限 |
| 500 | ACCESS_PERMISSION_CHECK_FAILED | 访问权限检查失败 | 检查用户和内容信息 |
| 500 | BATCH_UPDATE_FAILED | 批量更新失败 | 检查购买记录ID列表 |
| 500 | STATS_CALCULATION_FAILED | 统计计算失败 | 检查统计参数 |
| 500 | PURCHASE_COMPLETE_FAILED | 购买完成处理失败 | 检查订单信息 |
| 500 | REFUND_PROCESS_FAILED | 退款处理失败 | 检查退款条件 |
| 500 | RECORD_ACCESS_FAILED | 记录访问失败 | 检查访问参数 |

## 📈 接口使用示例

### 购买流程处理
```javascript
// 处理购买完成
async function completePurchase(purchaseData) {
    const response = await fetch('/api/v1/content/purchase/complete', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(purchaseData)
    });
    return response.json();
}

// 检查访问权限
async function checkAccess(userId, contentId) {
    const response = await fetch(`/api/v1/content/purchase/permission?userId=${userId}&contentId=${contentId}`);
    return response.json();
}
```

### 购买记录查询
```javascript
// 获取用户购买记录
async function getUserPurchases(userId, page = 1, size = 20) {
    const params = new URLSearchParams({
        userId: userId,
        orderBy: 'createTime',
        orderDirection: 'DESC',
        currentPage: page,
        pageSize: size
    });
    
    const response = await fetch(`/api/v1/content/purchase/query?${params}`);
    return response.json();
}

// 获取用户有效购买
async function getUserValidPurchases(userId) {
    const response = await fetch(`/api/v1/content/purchase/user/${userId}/valid`);
    return response.json();
}

// 查询高消费记录
async function getHighValuePurchases(minAmount = 100, page = 1, size = 20) {
    const params = new URLSearchParams({
        minAmount: minAmount,
        orderBy: 'purchaseAmount',
        orderDirection: 'DESC',
        currentPage: page,
        pageSize: size
    });
    
    const response = await fetch(`/api/v1/content/purchase/query?${params}`);
    return response.json();
}
```

### 状态管理
```javascript
// 处理退款
async function processRefund(purchaseId, reason, amount) {
    const response = await fetch(`/api/v1/content/purchase/${purchaseId}/refund`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            refundReason: reason,
            refundAmount: amount
        })
    });
    return response.json();
}

// 批量更新状态
async function batchUpdateStatus(purchaseIds, status) {
    const response = await fetch('/api/v1/content/purchase/batch/status', {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            ids: purchaseIds,
            status: status
        })
    });
    return response.json();
}
```

### 统计分析
```javascript
// 获取用户购买统计
async function getUserPurchaseStats(userId) {
    const response = await fetch(`/api/v1/content/purchase/user/${userId}/stats`);
    return response.json();
}

// 获取内容销售统计
async function getContentSalesStats(contentId) {
    const params = new URLSearchParams({
        statsType: 'CONTENT',
        contentId: contentId
    });
    
    const response = await fetch(`/api/v1/content/purchase/stats?${params}`);
    return response.json();
}
```

### 访问记录
```javascript
// 记录内容访问
async function recordAccess(userId, contentId) {
    const response = await fetch('/api/v1/content/purchase/access', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            userId: userId,
            contentId: contentId
        })
    });
    return response.json();
}
```

## 🔧 性能优化建议

1. **缓存策略**:
   - 购买记录: 缓存5分钟
   - 访问权限: 缓存2分钟
   - 统计数据: 缓存10分钟

2. **查询优化**:
   - 使用万能查询减少API调用次数
   - 批量权限检查优于单个检查
   - 合理使用分页避免大结果集

3. **异步处理**:
   - 访问记录异步写入
   - 统计计算异步更新
   - 退款处理异步执行

4. **请求优化**:
   ```javascript
   // 推荐：并行获取购买记录和统计信息
   Promise.all([
       getUserPurchases(userId),
       getUserPurchaseStats(userId)
   ]);
   
   // 推荐：批量检查权限
   batchCheckPermission(userId, contentIds);
   ```

## 🚀 极简设计优势

1. **接口增强**: 从15个接口增加到20个，增加便民接口提升易用性
2. **万能查询**: 1个查询接口替代10个具体查询接口
3. **统一权限**: 集中的权限验证机制
4. **批量优化**: 支持批量操作，提升性能
5. **业务集成**: 核心业务逻辑内置，简化调用

## 🔗 相关文档

- [ContentPurchaseFacadeService API 文档](../facade/content-purchase-facade-service-api.md)
- [Content Payment Controller API 文档](./content-payment-controller-api.md)
- [Content Controller API 文档](./content-controller-api.md)
- [Content Chapter Controller API 文档](./content-chapter-controller-api.md)

---

**联系信息**:  
- 控制器: ContentPurchaseController  
- 版本: 2.0.0 (极简版)  
- 基础路径: `/api/v1/content/purchase`  
- 维护: GIG团队  
- 更新: 2024-01-31