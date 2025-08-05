# Content Payment Controller REST API 文档

**控制器**: ContentPaymentController  
**版本**: 2.0.0 (极简版)  
**基础路径**: `/api/v1/content/payment`  
**接口数量**: 22个  
**更新时间**: 2024-01-31  

## 🚀 概述

内容付费配置控制器 - 极简版，基于12个核心Facade方法设计的精简API。提供内容付费配置的管理、查询和统计功能。

**设计理念**:
- **极简设计**: 22个API接口替代原有42个接口，大幅精简
- **万能查询**: 单个查询接口替代多个具体查询接口
- **智能推荐**: 内置多种推荐策略
- **统一权限**: 集中的权限验证机制

**主要功能**:
- **配置管理**: 付费配置的创建、查询、删除
- **价格管理**: 价格设置、折扣计算、实际价格计算
- **权限验证**: 访问权限检查、购买权限验证
- **统计分析**: 销售统计、收入分析、转化率统计
- **业务逻辑**: 内容状态同步、销售数据更新

**付费类型**:
```
FREE(免费) → COIN_PAY(金币付费) → VIP_FREE(VIP免费) → VIP_ONLY(VIP专享)
```

## 📋 接口分类

| 分类 | 接口数量 | 功能描述 |
|------|----------|----------|
| **核心CRUD功能** | 2个 | 配置查询和删除 |
| **万能查询功能** | 5个 | 条件查询、推荐查询 + 3个便民接口 |
| **状态管理功能** | 3个 | 状态更新、批量操作 + 1个便民接口 |
| **价格管理功能** | 4个 | 价格更新、实际价格计算 + 2个便民接口 |
| **权限验证功能** | 2个 | 访问权限检查 + 1个便民接口 |
| **销售统计功能** | 2个 | 销售统计更新 + 1个便民接口 |
| **统计分析功能** | 2个 | 付费统计信息 + 1个便民接口 |
| **业务逻辑功能** | 2个 | 内容状态同步 + 1个便民接口 |

---

## 🔧 1. 核心CRUD功能 (2个接口)

### 1.1 获取付费配置详情

**接口**: `GET /api/v1/content/payment/{id}`

**描述**: 根据配置ID获取付费配置详情

**路径参数**:
- `id` (Long): 配置ID

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 12345,
    "contentId": 67890,
    "contentTitle": "我的玄幻小说",
    "contentType": "NOVEL",
    "authorId": 2001,
    "authorNickname": "知名作家",
    "paymentType": "COIN_PAY",
    "price": 100,
    "originalPrice": 120,
    "discountStartTime": "2024-01-01T00:00:00",
    "discountEndTime": "2024-01-31T23:59:59",
    "isPermanent": true,
    "trialEnabled": true,
    "trialChapters": 3,
    "salesCount": 1500,
    "totalRevenue": 150000,
    "status": "ACTIVE",
    "createTime": "2024-01-01T10:00:00",
    "updateTime": "2024-01-15T14:30:00"
  }
}
```

### 1.2 删除付费配置

**接口**: `DELETE /api/v1/content/payment/{id}`

**描述**: 删除指定的付费配置

**路径参数**:
- `id` (Long): 配置ID

**查询参数**:
- `operatorId` (Long, 必需): 操作人ID

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": true
}
```

---

## 🔍 2. 万能查询功能 (5个接口)

### 2.1 万能条件查询付费配置 ⭐

**接口**: `GET /api/v1/content/payment/query`

**描述**: 根据多种条件查询付费配置列表，替代所有具体查询API

**核心功能**: 
- 替代`getPaymentConfigByContentId`、`getFreeContentConfigs`、`getCoinPayContentConfigs`等方法
- 支持按内容、付费类型、价格范围等多维度查询
- 支持试读、永久、折扣等特性筛选

**查询参数**:
- `contentId` (Long, 可选): 内容ID
- `paymentType` (String, 可选): 付费类型（FREE、COIN_PAY、VIP_FREE、VIP_ONLY）
- `status` (String, 可选): 状态
- `minPrice` (Long, 可选): 最小价格
- `maxPrice` (Long, 可选): 最大价格
- `trialEnabled` (Boolean, 可选): 是否支持试读
- `isPermanent` (Boolean, 可选): 是否永久
- `hasDiscount` (Boolean, 可选): 是否有折扣
- `orderBy` (String, 可选): 排序字段（createTime、price、salesCount、totalRevenue），默认"createTime"
- `orderDirection` (String, 可选): 排序方向（ASC、DESC），默认"DESC"
- `currentPage` (Integer, 可选): 当前页码
- `pageSize` (Integer, 可选): 页面大小

**调用示例**:
```bash
# 查询指定内容的付费配置
GET /api/v1/content/payment/query?contentId=67890&status=ACTIVE

# 查询金币付费的内容（按销量排序）
GET /api/v1/content/payment/query?paymentType=COIN_PAY&status=ACTIVE&orderBy=salesCount&orderDirection=DESC&currentPage=1&pageSize=20

# 查询价格在50-200之间的付费内容
GET /api/v1/content/payment/query?status=ACTIVE&minPrice=50&maxPrice=200&orderBy=price&orderDirection=ASC&currentPage=1&pageSize=50

# 查询支持试读且有折扣的内容
GET /api/v1/content/payment/query?status=ACTIVE&trialEnabled=true&hasDiscount=true&orderBy=totalRevenue&orderDirection=DESC&currentPage=1&pageSize=30
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
        "contentTitle": "我的玄幻小说",
        "paymentType": "COIN_PAY",
        "price": 100,
        "originalPrice": 120,
        "salesCount": 1500,
        "totalRevenue": 150000,
        "status": "ACTIVE"
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

### 2.2 推荐付费内容查询 ⭐

**接口**: `GET /api/v1/content/payment/recommendations`

**描述**: 获取推荐的付费内容

**查询参数**:
- `strategy` (String, 必需): 推荐策略（HOT、HIGH_VALUE、VALUE_FOR_MONEY、SALES_RANKING）
- `paymentType` (String, 可选): 付费类型
- `excludeContentIds` (String, 可选): 排除的内容ID列表（逗号分隔）
- `limit` (Integer, 可选): 返回数量限制，默认10

**调用示例**:
```bash
# 获取热门付费内容
GET /api/v1/content/payment/recommendations?strategy=HOT&paymentType=COIN_PAY&excludeContentIds=67890,67891&limit=10

# 获取高价值内容（性价比高）
GET /api/v1/content/payment/recommendations?strategy=VALUE_FOR_MONEY&limit=20

# 获取销量排行
GET /api/v1/content/payment/recommendations?strategy=SALES_RANKING&limit=50
```

### 2.3 获取内容付费配置（便民接口）

**接口**: `GET /api/v1/content/payment/content/{contentId}`

**描述**: 便民接口，获取指定内容的付费配置

**路径参数**:
- `contentId` (Long): 内容ID

**内部实现**: 调用万能查询接口

### 2.4 获取免费内容（便民接口）

**接口**: `GET /api/v1/content/payment/free`

**描述**: 便民接口，获取免费内容列表

**查询参数**:
- `currentPage` (Integer, 必需): 当前页码
- `pageSize` (Integer, 必需): 页面大小

**内部实现**: 调用万能查询接口，paymentType=FREE

### 2.5 获取金币付费内容（便民接口）

**接口**: `GET /api/v1/content/payment/coin-pay`

**描述**: 便民接口，获取金币付费内容列表

**查询参数**:
- `currentPage` (Integer, 必需): 当前页码
- `pageSize` (Integer, 必需): 页面大小

**内部实现**: 调用万能查询接口，paymentType=COIN_PAY

---

## ⚙️ 3. 状态管理功能 (3个接口)

### 3.1 更新付费配置状态 ⭐

**接口**: `PUT /api/v1/content/payment/{configId}/status`

**描述**: 更新付费配置状态

**路径参数**:
- `configId` (Long): 配置ID

**请求体**:
```json
{
  "status": "ACTIVE"
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

### 3.2 批量更新状态 ⭐

**接口**: `PUT /api/v1/content/payment/batch/status`

**描述**: 批量更新付费配置状态

**请求体**:
```json
{
  "ids": [12345, 12346, 12347],
  "status": "ACTIVE"
}
```

### 3.3 激活付费配置（便民接口）

**接口**: `PUT /api/v1/content/payment/{configId}/activate`

**描述**: 便民接口，激活付费配置

**路径参数**:
- `configId` (Long): 配置ID

**内部实现**: 调用状态更新接口，设置状态为"ACTIVE"

---

## 💰 4. 价格管理功能 (4个接口)

### 4.1 更新付费配置价格信息 ⭐

**接口**: `PUT /api/v1/content/payment/{configId}/price`

**描述**: 更新付费配置的价格信息

**路径参数**:
- `configId` (Long): 配置ID

**请求体**:
```json
{
  "price": 80,
  "originalPrice": 100,
  "discountStartTime": "2024-01-01T00:00:00",
  "discountEndTime": "2024-01-31T23:59:59"
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

### 4.2 计算用户实际需要支付的价格 ⭐

**接口**: `GET /api/v1/content/payment/calculate-price`

**描述**: 根据用户级别、内容配置计算实际需要支付的价格

**查询参数**:
- `userId` (Long, 必需): 用户ID
- `contentId` (Long, 必需): 内容ID

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": 80
}
```

### 4.3 设置折扣价格（便民接口）

**接口**: `PUT /api/v1/content/payment/{configId}/discount`

**描述**: 便民接口，设置折扣价格

**路径参数**:
- `configId` (Long): 配置ID

**请求体**:
```json
{
  "discountPrice": 80,
  "durationDays": 30
}
```

**内部实现**: 调用价格更新接口，自动计算折扣时间

### 4.4 获取价格信息（便民接口）

**接口**: `GET /api/v1/content/payment/content/{contentId}/price`

**描述**: 便民接口，获取内容的价格信息

**路径参数**:
- `contentId` (Long): 内容ID

**查询参数**:
- `userId` (Long, 可选): 用户ID（用于计算实际价格）

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "originalPrice": 100,
    "currentPrice": 80,
    "actualPrice": 80,
    "discountAmount": 20,
    "hasDiscount": true,
    "paymentType": "COIN_PAY"
  }
}
```

---

## 🔐 5. 权限验证功能 (2个接口)

### 5.1 检查访问权限 ⭐

**接口**: `GET /api/v1/content/payment/permission`

**描述**: 检查访问权限，包含购买权限和免费访问检查

**查询参数**:
- `userId` (Long, 必需): 用户ID
- `contentId` (Long, 必需): 内容ID

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "canAccess": false,
    "accessType": "COIN_PAY",
    "price": 80,
    "originalPrice": 100,
    "discountAmount": 20,
    "discountReason": "VIP折扣",
    "trialEnabled": true,
    "trialChapters": 3,
    "isPermanent": true,
    "userLevel": "VIP",
    "hasDiscount": true
  }
}
```

### 5.2 快速权限检查（便民接口）

**接口**: `GET /api/v1/content/payment/can-access`

**描述**: 便民接口，快速检查用户是否可以访问内容

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

---

## 📊 6. 销售统计功能 (2个接口)

### 6.1 更新销售统计 ⭐

**接口**: `PUT /api/v1/content/payment/{configId}/sales`

**描述**: 更新销售统计数据

**路径参数**:
- `configId` (Long): 配置ID

**请求体**:
```json
{
  "salesIncrement": 1,
  "revenueIncrement": 80
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

### 6.2 记录销售（便民接口）

**接口**: `POST /api/v1/content/payment/{configId}/sale`

**描述**: 便民接口，记录一次销售

**路径参数**:
- `configId` (Long): 配置ID

**请求体**:
```json
{
  "amount": 80
}
```

**内部实现**: 调用销售统计接口，销量+1，收入+amount

---

## 📈 7. 统计分析功能 (2个接口)

### 7.1 获取付费统计信息 ⭐

**接口**: `GET /api/v1/content/payment/stats`

**描述**: 获取付费统计信息

**查询参数**:
- `statsType` (String, 必需): 统计类型（PAYMENT_TYPE、PRICE、SALES、CONVERSION、REVENUE_ANALYSIS）
- 其他参数根据统计类型而定

**调用示例**:
```bash
# 获取付费类型统计
GET /api/v1/content/payment/stats?statsType=PAYMENT_TYPE&contentType=NOVEL

# 获取价格分布统计
GET /api/v1/content/payment/stats?statsType=PRICE&minPrice=50&maxPrice=200

# 获取销售统计
GET /api/v1/content/payment/stats?statsType=SALES&authorId=2001&startDate=2024-01-01&endDate=2024-01-31
```

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "freeCount": 150,
    "coinPayCount": 800,
    "vipFreeCount": 200,
    "vipOnlyCount": 50,
    "totalCount": 1200,
    "avgPrice": 85.5,
    "maxPrice": 500,
    "minPrice": 10,
    "totalRevenue": 2500000,
    "totalSales": 30000
  }
}
```

### 7.2 获取内容收入统计（便民接口）

**接口**: `GET /api/v1/content/payment/content/{contentId}/revenue`

**描述**: 便民接口，获取内容的收入统计

**路径参数**:
- `contentId` (Long): 内容ID

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "totalRevenue": 150000,
    "totalSales": 1500,
    "avgPrice": 100,
    "currentPrice": 80,
    "revenueGrowth": 0.15,
    "salesGrowth": 0.12
  }
}
```

---

## 🔄 8. 业务逻辑功能 (2个接口)

### 8.1 同步内容状态 ⭐

**接口**: `PUT /api/v1/content/payment/sync`

**描述**: 统一业务逻辑处理

**请求体**:
```json
{
  "operationType": "SYNC_STATUS",
  "operationData": {
    "contentId": 67890,
    "newStatus": "PUBLISHED"
  }
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "updatedCount": 1,
    "message": "同步成功"
  }
}
```

### 8.2 批量同步内容状态（便民接口）

**接口**: `PUT /api/v1/content/payment/sync/batch`

**描述**: 便民接口，批量同步内容状态

**请求体**:
```json
{
  "contentIds": [67890, 67891, 67892],
  "newStatus": "OFFLINE"
}
```

**内部实现**: 调用同步接口，operationType=BATCH_SYNC

---

## 🎯 数据模型

### ContentPaymentConfigResponse 付费配置响应对象
```json
{
  "id": 12345,                      // 配置ID
  "contentId": 67890,               // 内容ID
  "contentTitle": "我的玄幻小说",     // 内容标题
  "contentType": "NOVEL",           // 内容类型
  "authorId": 2001,                 // 作者ID
  "authorNickname": "知名作家",      // 作者昵称
  "paymentType": "COIN_PAY",        // 付费类型
  "price": 100,                     // 当前价格
  "originalPrice": 120,             // 原价
  "discountStartTime": "2024-01-01T00:00:00", // 折扣开始时间
  "discountEndTime": "2024-01-31T23:59:59",   // 折扣结束时间
  "isPermanent": true,              // 是否永久
  "trialEnabled": true,             // 是否支持试读
  "trialChapters": 3,               // 试读章节数
  "salesCount": 1500,               // 销量
  "totalRevenue": 150000,           // 总收入
  "status": "ACTIVE",               // 状态
  "createTime": "2024-01-01T10:00:00",   // 创建时间
  "updateTime": "2024-01-15T14:30:00"    // 更新时间
}
```

## 🚨 错误代码

| HTTP状态码 | 错误码 | 描述 | 解决方案 |
|-----------|--------|------|----------|
| 400 | INVALID_PARAMETER | 参数验证失败 | 检查请求参数的格式和必填项 |
| 404 | PAYMENT_CONFIG_NOT_FOUND | 付费配置不存在 | 检查配置ID |
| 404 | CONTENT_NOT_FOUND | 内容不存在 | 检查内容ID是否正确 |
| 404 | USER_NOT_FOUND | 用户不存在 | 检查用户ID是否正确 |
| 500 | DELETE_CONFIG_FAILED | 删除配置失败 | 确认操作权限 |
| 500 | PRICE_UPDATE_FAILED | 价格更新失败 | 检查价格参数 |
| 500 | BATCH_UPDATE_FAILED | 批量更新失败 | 检查配置ID列表 |
| 500 | STATS_CALCULATION_FAILED | 统计计算失败 | 检查统计参数 |
| 500 | ACCESS_CHECK_FAILED | 权限检查失败 | 检查用户和内容信息 |
| 500 | SALES_UPDATE_FAILED | 销售统计更新失败 | 检查统计数据 |
| 500 | SYNC_OPERATION_FAILED | 同步操作失败 | 检查操作参数 |

## 📈 接口使用示例

### 付费配置管理
```javascript
// 获取内容付费配置
async function getContentPaymentConfig(contentId) {
    const response = await fetch(`/api/v1/content/payment/content/${contentId}`);
    return response.json();
}

// 检查用户访问权限
async function checkUserAccess(userId, contentId) {
    const response = await fetch(`/api/v1/content/payment/permission?userId=${userId}&contentId=${contentId}`);
    return response.json();
}

// 计算实际价格
async function calculatePrice(userId, contentId) {
    const response = await fetch(`/api/v1/content/payment/calculate-price?userId=${userId}&contentId=${contentId}`);
    return response.json();
}
```

### 付费内容查询
```javascript
// 获取热门付费内容
async function getHotPaidContents(paymentType = 'COIN_PAY', limit = 10) {
    const params = new URLSearchParams({
        strategy: 'HOT',
        paymentType: paymentType,
        limit: limit
    });
    
    const response = await fetch(`/api/v1/content/payment/recommendations?${params}`);
    return response.json();
}

// 查询金币付费内容
async function getCoinPayContents(page = 1, size = 20) {
    const params = new URLSearchParams({
        paymentType: 'COIN_PAY',
        status: 'ACTIVE',
        orderBy: 'salesCount',
        orderDirection: 'DESC',
        currentPage: page,
        pageSize: size
    });
    
    const response = await fetch(`/api/v1/content/payment/query?${params}`);
    return response.json();
}

// 查询价格范围内的内容
async function getContentsByPriceRange(minPrice, maxPrice, page = 1, size = 20) {
    const params = new URLSearchParams({
        status: 'ACTIVE',
        minPrice: minPrice,
        maxPrice: maxPrice,
        orderBy: 'price',
        orderDirection: 'ASC',
        currentPage: page,
        pageSize: size
    });
    
    const response = await fetch(`/api/v1/content/payment/query?${params}`);
    return response.json();
}
```

### 价格管理
```javascript
// 设置折扣价格
async function setDiscount(configId, discountPrice, durationDays) {
    const response = await fetch(`/api/v1/content/payment/${configId}/discount`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            discountPrice: discountPrice,
            durationDays: durationDays
        })
    });
    return response.json();
}

// 更新价格信息
async function updatePrice(configId, priceData) {
    const response = await fetch(`/api/v1/content/payment/${configId}/price`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(priceData)
    });
    return response.json();
}
```

### 销售统计
```javascript
// 记录销售
async function recordSale(configId, amount) {
    const response = await fetch(`/api/v1/content/payment/${configId}/sale`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            amount: amount
        })
    });
    return response.json();
}

// 获取内容收入统计
async function getContentRevenue(contentId) {
    const response = await fetch(`/api/v1/content/payment/content/${contentId}/revenue`);
    return response.json();
}

// 获取付费类型统计
async function getPaymentTypeStats(contentType) {
    const params = new URLSearchParams({
        statsType: 'PAYMENT_TYPE',
        contentType: contentType
    });
    
    const response = await fetch(`/api/v1/content/payment/stats?${params}`);
    return response.json();
}
```

### 状态管理
```javascript
// 批量更新状态
async function batchUpdateStatus(configIds, status) {
    const response = await fetch('/api/v1/content/payment/batch/status', {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            ids: configIds,
            status: status
        })
    });
    return response.json();
}

// 同步内容状态
async function syncContentStatus(contentIds, newStatus) {
    const response = await fetch('/api/v1/content/payment/sync/batch', {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            contentIds: contentIds,
            newStatus: newStatus
        })
    });
    return response.json();
}
```

## 🔧 性能优化建议

1. **缓存策略**:
   - 付费配置: 缓存30分钟
   - 权限检查: 缓存5分钟
   - 推荐列表: 缓存10分钟

2. **查询优化**:
   - 使用万能查询减少API调用
   - 价格计算使用缓存
   - 统计数据定期预计算

3. **异步处理**:
   - 销售统计异步更新
   - 价格变更异步通知
   - 批量操作异步执行

4. **请求优化**:
   ```javascript
   // 推荐：并行获取配置和权限信息
   Promise.all([
       getContentPaymentConfig(contentId),
       checkUserAccess(userId, contentId)
   ]);
   
   // 推荐：使用万能查询获取不同类型的付费内容
   Promise.all([
       getHotPaidContents('COIN_PAY'),
       getCoinPayContents(),
       getContentsByPriceRange(50, 200)
   ]);
   ```

## 🚀 极简设计优势

1. **接口精简**: 从42个接口大幅缩减到22个，学习成本降低48%
2. **万能查询**: 1个查询接口替代多个具体查询接口
3. **智能推荐**: 内置4种推荐策略
4. **统一权限**: 集中的权限验证机制
5. **便民接口**: 保留10个高频便民接口，平衡灵活性和易用性

## 🔗 相关文档

- [ContentPaymentFacadeService API 文档](../facade/content-payment-facade-service-api.md)
- [Content Purchase Controller API 文档](./content-purchase-controller-api.md)
- [Content Controller API 文档](./content-controller-api.md)
- [Content Chapter Controller API 文档](./content-chapter-controller-api.md)

---

**联系信息**:  
- 控制器: ContentPaymentController  
- 版本: 2.0.0 (极简版)  
- 基础路径: `/api/v1/content/payment`  
- 维护: GIG团队  
- 更新: 2024-01-31