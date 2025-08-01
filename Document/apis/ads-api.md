# 广告管理 API 接口文档

## 概述

广告管理模块提供了完整的广告管理功能，包括广告的创建、更新、删除、查询等操作。

**基础路径**: `/api/v1/ads`

**版本**: 3.0.0 (极简版)

---

## 接口列表

### 1. 创建广告

**接口地址**: `POST /api/v1/ads`

**接口描述**: 创建新的广告

**请求参数**:
```json
{
  // AdCreateRequest 对象
  // 具体字段根据业务需求定义
}
```

**响应结果**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    // AdResponse 对象
    // 包含创建的广告详细信息
  }
}
```

**HTTP状态码**: 200

---

### 2. 更新广告

**接口地址**: `PUT /api/v1/ads/{id}`

**接口描述**: 更新指定广告信息

**路径参数**:
- `id` (Long): 广告ID

**请求参数**:
```json
{
  // AdUpdateRequest 对象
  // 具体字段根据业务需求定义
}
```

**响应结果**:
```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

**HTTP状态码**: 200

---

### 3. 删除广告

**接口地址**: `DELETE /api/v1/ads/{id}`

**接口描述**: 删除指定广告

**路径参数**:
- `id` (Long): 广告ID

**响应结果**:
```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

**HTTP状态码**: 200

---

### 4. 获取广告详情

**接口地址**: `GET /api/v1/ads/{id}`

**接口描述**: 根据ID获取广告详细信息

**路径参数**:
- `id` (Long): 广告ID

**响应结果**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    // AdResponse 对象
    // 包含广告详细信息
  }
}
```

**HTTP状态码**: 200

---

### 5. 分页查询广告

**接口地址**: `POST /api/v1/ads/query`

**接口描述**: 根据条件分页查询广告列表

**请求参数**:
```json
{
  // AdQueryRequest 对象
  // 包含查询条件和分页参数
}
```

**响应结果**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 100,
    "currentPage": 1,
    "pageSize": 10,
    "pages": 10,
    "list": [
      {
        // AdResponse 对象列表
      }
    ]
  }
}
```

**HTTP状态码**: 200

---

### 6. 根据类型获取广告

**接口地址**: `POST /api/v1/ads/type`

**接口描述**: 根据广告类型获取广告列表

**请求参数**:
```json
{
  // AdTypeRequest 对象
  // 包含广告类型等查询条件
}
```

**响应结果**:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      // AdResponse 对象列表
    }
  ]
}
```

**HTTP状态码**: 200

---

### 7. 随机获取广告

**接口地址**: `GET /api/v1/ads/random/{adType}`

**接口描述**: 随机获取指定类型的单个广告

**路径参数**:
- `adType` (String): 广告类型 (例如: "banner")

**响应结果**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    // AdResponse 对象
    // 随机返回的广告信息
  }
}
```

**HTTP状态码**: 200

---

### 8. 随机获取广告列表

**接口地址**: `POST /api/v1/ads/random`

**接口描述**: 随机获取指定类型的广告列表

**请求参数**:
```json
{
  // AdTypeRequest 对象
  // 系统会强制设置 random: true
}
```

**响应结果**:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      // AdResponse 对象列表
      // 随机返回的广告列表
    }
  ]
}
```

**HTTP状态码**: 200

**注意**: 此接口会强制设置为随机模式

---

### 9. 健康检查

**接口地址**: `GET /api/v1/ads/health`

**接口描述**: 检查广告系统运行状态

**响应结果**:
```json
{
  "code": 200,
  "message": "success",
  "data": "系统运行正常"
}
```

**HTTP状态码**: 200

---

## 通用响应格式

所有接口都遵循统一的响应格式：

```json
{
  "code": 200,          // 状态码
  "message": "success", // 响应消息
  "data": {}            // 具体数据，根据接口而定
}
```

## 状态码说明

- `200`: 请求成功
- `400`: 请求参数错误
- `401`: 未授权
- `403`: 禁止访问
- `404`: 资源不存在
- `500`: 服务器内部错误

## 注意事项

1. 所有请求和响应的 Content-Type 都为 `application/json`
2. 请求参数需要进行 `@Valid` 验证
3. 路径参数和请求体参数都是必需的
4. 随机获取接口会自动设置随机模式
5. 所有接口都返回统一的 `Result` 包装对象

---

**文档版本**: 1.0.0  
**最后更新**: 2024年  
**维护团队**: GIG Team
