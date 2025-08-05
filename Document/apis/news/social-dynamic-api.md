# 社交动态管理 REST API 接口文档

## 📋 概述

**版本**: 3.0.0 (严格对应接口版)  
**基础路径**: `/api/v1/social/dynamics`  
**作者**: GIG Team  
**更新时间**: 2024-01-30  

## 🔧 接口分类

本API严格按照SocialFacadeService接口设计，提供完整的社交动态管理功能，包含：

- **业务CRUD操作** - 动态的创建、更新、删除、查询
- **核心查询方法** - 多维度的动态查询功能  
- **统计计数方法** - 各种统计数据查询
- **互动统计更新** - 点赞、评论、分享数统计更新
- **状态管理** - 动态状态的管理操作
- **用户信息同步** - 用户信息的同步更新
- **数据清理** - 历史数据的清理功能
- **特殊查询** - 最新动态、分享动态等特殊查询
- **系统健康检查** - 系统运行状态检查

---

## 📋 公共响应格式

### 统一响应包装

```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "操作成功",
  "data": {},
  "timestamp": "2024-01-30T10:00:00"
}
```

### 分页响应格式

```json
{
  "success": true,
  "code": "SUCCESS", 
  "message": "查询成功",
  "data": {
    "datas": [],
    "total": 100,
    "currentPage": 1,
    "pageSize": 20,
    "totalPage": 5
  }
}
```

---

## 🎯 业务CRUD操作

### 1. 创建动态

**接口路径**: `POST /api/v1/social/dynamics/create`  
**接口描述**: 发布新的社交动态  

#### 请求参数

```json
{
  "userId": 1001,
  "content": "这是一条动态内容",
  "dynamicType": "text",
  "mediaUrls": ["https://example.com/image1.jpg"],
  "location": "北京市",
  "visibility": "public",
  "tags": ["生活", "美食"]
}
```

#### 响应示例

```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "动态创建成功",
  "data": {
    "id": 10001,
    "userId": 1001,
    "content": "这是一条动态内容",
    "dynamicType": "text",
    "status": "normal",
    "likeCount": 0,
    "commentCount": 0,
    "shareCount": 0,
    "createTime": "2024-01-30T10:00:00"
  }
}
```

### 2. 批量创建动态

**接口路径**: `POST /api/v1/social/dynamics/batch-create?operatorId=1001`  
**接口描述**: 批量发布多个社交动态  

#### 请求参数

```json
[
  {
    "userId": 1001,
    "content": "动态内容1",
    "dynamicType": "text"
  },
  {
    "userId": 1002, 
    "content": "动态内容2",
    "dynamicType": "image"
  }
]
```

#### 响应示例

```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "批量创建成功",
  "data": 2
}
```

### 3. 创建分享动态

**接口路径**: `POST /api/v1/social/dynamics/create-share`  
**接口描述**: 创建分享其他内容的动态  

#### 请求参数

```json
{
  "userId": 1001,
  "content": "分享一个有趣的内容",
  "dynamicType": "share", 
  "shareTargetType": "content",
  "shareTargetId": 2001,
  "shareTargetTitle": "原内容标题"
}
```

### 4. 更新动态内容

**接口路径**: `PUT /api/v1/social/dynamics/update`  
**接口描述**: 只允许更新动态内容，其他字段不允许修改  

#### 请求参数

```json
{
  "id": 10001,
  "userId": 1001,
  "content": "更新后的动态内容"
}
```

### 5. 删除动态

**接口路径**: `DELETE /api/v1/social/dynamics/{id}?operatorId=1001`  
**接口描述**: 逻辑删除动态（设为deleted状态）  

#### 路径参数
- `id`: 动态ID

#### 查询参数
- `operatorId`: 操作人ID

### 6. 获取动态详情

**接口路径**: `GET /api/v1/social/dynamics/{id}?includeDeleted=false`  
**接口描述**: 根据ID获取动态详情  

#### 路径参数
- `id`: 动态ID

#### 查询参数
- `includeDeleted`: 是否包含已删除的动态，默认false

### 7. 分页查询动态列表

**接口路径**: `POST /api/v1/social/dynamics/query`  
**接口描述**: 支持多条件分页查询动态  

#### 请求参数

```json
{
  "currentPage": 1,
  "pageSize": 20,
  "userId": 1001,
  "dynamicType": "text",
  "status": "normal",
  "keyword": "搜索关键词",
  "minLikeCount": 10,
  "sortBy": "create_time",
  "sortDirection": "desc"
}
```

---

## 🔍 核心查询方法

### 1. 根据用户ID查询动态

**接口路径**: `GET /api/v1/social/dynamics/user/{userId}`  
**接口描述**: 获取指定用户的动态列表  

#### 路径参数
- `userId`: 用户ID

#### 查询参数
- `currentPage`: 当前页码，默认1
- `pageSize`: 页面大小，默认20  
- `status`: 状态筛选，可选
- `dynamicType`: 动态类型筛选，可选

### 2. 根据动态类型查询

**接口路径**: `GET /api/v1/social/dynamics/type/{dynamicType}`  
**接口描述**: 获取指定类型的动态列表  

#### 路径参数
- `dynamicType`: 动态类型（text/image/video/share）

### 3. 根据状态查询动态

**接口路径**: `GET /api/v1/social/dynamics/status/{status}`  
**接口描述**: 获取指定状态的动态列表  

#### 路径参数
- `status`: 动态状态（normal/hidden/deleted）

### 4. 获取关注用户的动态流

**接口路径**: `GET /api/v1/social/dynamics/following/{userId}`  
**接口描述**: 获取用户关注的人发布的动态  

#### 路径参数
- `userId`: 用户ID

### 5. 搜索动态内容

**接口路径**: `GET /api/v1/social/dynamics/search?keyword=关键词`  
**接口描述**: 按关键词搜索动态内容  

#### 查询参数
- `keyword`: 搜索关键词（必填）
- `currentPage`: 当前页码，默认1
- `pageSize`: 页面大小，默认20
- `status`: 状态筛选，可选

### 6. 获取热门动态

**接口路径**: `GET /api/v1/social/dynamics/hot`  
**接口描述**: 按互动数排序获取热门动态  

#### 查询参数
- `currentPage`: 当前页码，默认1
- `pageSize`: 页面大小，默认20
- `status`: 状态筛选，可选
- `dynamicType`: 动态类型筛选，可选

### 7. 根据分享目标查询分享动态

**接口路径**: `GET /api/v1/social/dynamics/share-target`  
**接口描述**: 获取分享指定内容的动态列表  

#### 查询参数
- `shareTargetType`: 分享目标类型（必填）
- `shareTargetId`: 分享目标ID（必填）
- `currentPage`: 当前页码，默认1
- `pageSize`: 页面大小，默认20
- `status`: 状态筛选，可选

---

## 📊 统计计数方法

### 1. 统计用户动态数量

**接口路径**: `GET /api/v1/social/dynamics/count/user/{userId}`  
**接口描述**: 统计指定用户的动态数量  

#### 路径参数
- `userId`: 用户ID

#### 查询参数
- `status`: 状态筛选，可选
- `dynamicType`: 动态类型筛选，可选

#### 响应示例

```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "统计成功",
  "data": 25
}
```

### 2. 统计动态类型数量

**接口路径**: `GET /api/v1/social/dynamics/count/type/{dynamicType}`  
**接口描述**: 统计指定类型的动态数量  

#### 路径参数
- `dynamicType`: 动态类型

### 3. 统计时间范围内动态数量

**接口路径**: `GET /api/v1/social/dynamics/count/time-range`  
**接口描述**: 统计指定时间范围内的动态数量  

#### 查询参数
- `startTime`: 开始时间（必填，格式：2024-01-30T10:00:00）
- `endTime`: 结束时间（必填，格式：2024-01-30T18:00:00）
- `status`: 状态筛选，可选

---

## 💝 互动统计更新

### 1. 增加点赞数

**接口路径**: `POST /api/v1/social/dynamics/{id}/like?operatorId=1001`  
**接口描述**: 为指定动态增加点赞数  

#### 路径参数
- `id`: 动态ID

#### 查询参数
- `operatorId`: 操作人ID

#### 响应示例

```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "点赞成功",
  "data": 1
}
```

### 2. 减少点赞数

**接口路径**: `DELETE /api/v1/social/dynamics/{id}/like?operatorId=1001`  
**接口描述**: 为指定动态减少点赞数  

### 3. 增加评论数

**接口路径**: `POST /api/v1/social/dynamics/{id}/comment?operatorId=1001`  
**接口描述**: 为指定动态增加评论数  

### 4. 增加分享数

**接口路径**: `POST /api/v1/social/dynamics/{id}/share?operatorId=1001`  
**接口描述**: 为指定动态增加分享数  

### 5. 批量更新统计数据

**接口路径**: `PUT /api/v1/social/dynamics/{id}/statistics`  
**接口描述**: 批量更新动态的统计数据  

#### 路径参数
- `id`: 动态ID

#### 查询参数
- `likeCount`: 点赞数，可选
- `commentCount`: 评论数，可选
- `shareCount`: 分享数，可选
- `operatorId`: 操作人ID（必填）

---

## 🔄 状态管理

### 1. 更新动态状态

**接口路径**: `PUT /api/v1/social/dynamics/{id}/status`  
**接口描述**: 更新指定动态的状态  

#### 路径参数
- `id`: 动态ID

#### 查询参数
- `status`: 新状态（normal/hidden/deleted）
- `operatorId`: 操作人ID

### 2. 批量更新动态状态

**接口路径**: `PUT /api/v1/social/dynamics/batch-status`  
**接口描述**: 批量更新多个动态的状态  

#### 请求参数

```json
[10001, 10002, 10003]
```

#### 查询参数
- `status`: 新状态
- `operatorId`: 操作人ID

---

## 👤 用户信息同步

### 更新用户冗余信息

**接口路径**: `PUT /api/v1/social/dynamics/user/{userId}/info`  
**接口描述**: 同步更新动态中的用户信息  

#### 路径参数
- `userId`: 用户ID

#### 查询参数
- `userNickname`: 用户昵称，可选
- `userAvatar`: 用户头像，可选
- `operatorId`: 操作人ID

---

## 🗑️ 数据清理

### 数据清理

**接口路径**: `DELETE /api/v1/social/dynamics/cleanup`  
**接口描述**: 物理删除指定状态的历史动态  

⚠️ **危险操作，谨慎使用！**

#### 查询参数
- `status`: 要清理的状态
- `beforeTime`: 截止时间
- `limit`: 限制数量，可选
- `operatorId`: 操作人ID

---

## 🌟 特殊查询

### 1. 查询最新动态

**接口路径**: `GET /api/v1/social/dynamics/latest`  
**接口描述**: 获取最新发布的动态列表  

#### 查询参数
- `limit`: 限制数量，默认10，最大100
- `status`: 状态筛选，可选

#### 响应示例

```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "查询成功",
  "data": [
    {
      "id": 10001,
      "userId": 1001,
      "content": "最新动态内容",
      "createTime": "2024-01-30T10:00:00"
    }
  ]
}
```

### 2. 查询用户最新动态

**接口路径**: `GET /api/v1/social/dynamics/user/{userId}/latest`  
**接口描述**: 获取指定用户最新发布的动态列表  

#### 路径参数
- `userId`: 用户ID

#### 查询参数
- `limit`: 限制数量，默认10
- `status`: 状态筛选，可选

### 3. 查询分享动态列表

**接口路径**: `GET /api/v1/social/dynamics/share/{shareTargetType}`  
**接口描述**: 获取指定类型的分享动态列表  

#### 路径参数
- `shareTargetType`: 分享目标类型（content/goods等）

#### 查询参数
- `limit`: 限制数量，默认10
- `status`: 状态筛选，可选

---

## 🏥 系统健康检查

### 系统健康检查

**接口路径**: `GET /api/v1/social/dynamics/health`  
**接口描述**: 检查社交动态系统运行状态  

#### 响应示例

```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "系统运行正常",
  "data": "社交动态系统运行正常，当前正常文本动态数量: 1250"
}
```

---

## 📚 附录

### 错误码说明

| 错误码 | 说明 |
|--------|------|
| INVALID_PARAM | 参数无效 |
| DYNAMIC_NOT_FOUND | 动态不存在 |
| USER_NOT_FOUND | 用户不存在 |
| NO_PERMISSION | 无权限操作 |
| DYNAMIC_CREATE_ERROR | 动态创建失败 |
| DYNAMIC_UPDATE_ERROR | 动态更新失败 |
| DYNAMIC_DELETE_ERROR | 动态删除失败 |
| COUNT_ERROR | 统计查询失败 |
| UPDATE_ERROR | 更新操作失败 |

### 动态类型说明

| 类型 | 说明 |
|------|------|
| text | 文本动态 |
| image | 图片动态 |
| video | 视频动态 |
| share | 分享动态 |

### 动态状态说明

| 状态 | 说明 |
|------|------|
| normal | 正常 |
| hidden | 隐藏 |
| deleted | 已删除 |

---

**文档版本**: 3.0.0  
**最后更新**: 2024-01-30  
**维护团队**: GIG Team