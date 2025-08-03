# 广告管理模块 API 文档 - 极简版

## 概述

广告管理模块提供极简的广告管理功能，专注于核心需求：
- 广告 CRUD 管理
- 按类型查询广告
- 随机展示广告
- 权重排序

## 广告类型

系统支持以下广告类型：
- `banner`: 横幅广告（适用于页面顶部/底部）
- `sidebar`: 侧边栏广告（适用于页面侧边）
- `popup`: 弹窗广告（适用于弹窗展示）
- `modal`: 模态框广告（适用于遮罩层展示）

## API 接口

### 1. 创建广告

**接口路径：** `POST /api/v1/ads`

**请求参数：**
```json
{
  "adName": "首页推荐商品广告",
  "adTitle": "限时特惠 - 精选商品低至3折",
  "adDescription": "这是一个精美的商品推广广告",
  "adType": "banner",
  "imageUrl": "https://example.com/images/ad.jpg",
  "clickUrl": "https://example.com/products/1",
  "isActive": 1,
  "sortOrder": 100
}
```

**响应示例：**
```json
{
  "success": true,
  "errorCode": null,
  "errorMsg": null,
  "data": {
    "id": 1,
    "adName": "首页推荐商品广告",
    "adTitle": "限时特惠 - 精选商品低至3折",
    "adDescription": "这是一个精美的商品推广广告",
    "adType": "banner",
    "imageUrl": "https://example.com/images/ad.jpg",
    "clickUrl": "https://example.com/products/1",
    "isActive": 1,
    "sortOrder": 100,
    "createTime": "2024-01-16T10:30:00",
    "updateTime": "2024-01-16T10:30:00"
  }
}
```

### 2. 更新广告

**接口路径：** `PUT /api/v1/ads/{id}`

**请求参数：**
```json
{
  "adName": "更新后的广告名称",
  "adTitle": "更新后的广告标题",
  "isActive": 0
}
```

### 3. 删除广告

**接口路径：** `DELETE /api/v1/ads/{id}`

### 4. 获取广告详情

**接口路径：** `GET /api/v1/ads/{id}`

### 5. 分页查询广告

**接口路径：** `POST /api/v1/ads/query`

**请求参数：**
```json
{
  "adName": "商品",
  "adType": "banner",
  "isActive": 1,
  "currentPage": 1,
  "pageSize": 10
}
```

### 6. 根据类型获取广告列表

**接口路径：** `POST /api/v1/ads/type`

**请求参数：**
```json
{
  "adType": "banner",
  "limit": 10,
  "random": false
}
```

**响应示例：**
```json
{
  "success": true,
  "errorCode": null,
  "errorMsg": null,
  "data": [
    {
      "id": 1,
      "adName": "首页推荐商品广告",
      "adTitle": "限时特惠 - 精选商品低至3折",
      "adType": "banner",
      "imageUrl": "https://example.com/images/ad.jpg",
      "clickUrl": "https://example.com/products/1",
      "isActive": 1,
      "sortOrder": 100
    }
  ]
}
```

### 7. 随机获取单个广告

**接口路径：** `GET /api/v1/ads/random/{adType}`

**路径参数：**
- `adType`: 广告类型（banner、sidebar、popup、modal）

### 8. 随机获取广告列表

**接口路径：** `POST /api/v1/ads/random`

**请求参数：**
```json
{
  "adType": "banner",
  "limit": 5
}
```

### 9. 健康检查

**接口路径：** `GET /api/v1/ads/health`

## 使用示例

### 前端集成示例

```javascript
// 获取首页横幅广告
async function getBannerAds() {
  const response = await fetch('/api/v1/ads/type', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({
      adType: 'banner',
      limit: 3,
      random: false
    })
  });
  
  const result = await response.json();
  if (result.success) {
    return result.data;
  }
  return [];
}

// 随机获取弹窗广告
async function getRandomPopupAd() {
  const response = await fetch('/api/v1/ads/random/popup');
  const result = await response.json();
  if (result.success) {
    return result.data;
  }
  return null;
}
```

### 数据结构说明

**广告对象字段说明：**
- `id`: 广告唯一标识
- `adName`: 广告名称（用于后台管理）
- `adTitle`: 广告标题（用于展示）
- `adDescription`: 广告描述（可选）
- `adType`: 广告类型（banner/sidebar/popup/modal）
- `imageUrl`: 广告图片链接
- `clickUrl`: 点击跳转链接
- `isActive`: 是否启用（1:启用 0:禁用）
- `sortOrder`: 排序权重（数值越大越靠前）
- `createTime`: 创建时间
- `updateTime`: 更新时间

## 最佳实践

1. **权重设置**：建议使用 100 的倍数设置权重，便于后续调整
2. **图片规范**：建议使用 CDN 托管图片，确保加载速度
3. **链接检查**：定期检查 clickUrl 的有效性
4. **缓存策略**：推荐对广告数据进行适当缓存，提升性能
5. **响应式设计**：根据不同设备调整广告尺寸