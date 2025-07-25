# 博主申请 API 文档

## 📋 概述

博主申请模块提供用户申请成为博主的完整流程，包括申请提交、审核管理、状态查询等功能。

**基础路径**: `/api/v1/blogger`

---

## 🔗 接口列表

### 博主申请接口

| 接口 | 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|------|
| 提交博主申请 | POST | `/api/v1/blogger/apply` | 提交博主申请 | 登录用户 |
| 获取申请状态 | GET | `/api/v1/blogger/status` | 获取当前用户的申请状态 | 登录用户 |
| 获取申请详情 | GET | `/api/v1/blogger/application/{applicationId}` | 获取申请详情 | 申请者/管理员 |
| 更新申请信息 | PUT | `/api/v1/blogger/application/{applicationId}` | 更新申请信息 | 申请者 |
| 取消申请 | DELETE | `/api/v1/blogger/application/{applicationId}` | 取消申请 | 申请者 |
| 重新申请 | POST | `/api/v1/blogger/reapply` | 重新提交申请 | 登录用户 |

### 博主管理接口

| 接口 | 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|------|
| 获取博主信息 | GET | `/api/v1/blogger/profile/{userId}` | 获取博主详细信息 | 公开 |
| 更新博主资料 | PUT | `/api/v1/blogger/profile` | 更新博主资料 | 博主本人 |
| 获取博主统计 | GET | `/api/v1/blogger/statistics/{userId}` | 获取博主统计数据 | 公开 |
| 获取博主列表 | GET | `/api/v1/blogger/list` | 获取博主列表 | 公开 |
| 搜索博主 | GET | `/api/v1/blogger/search` | 搜索博主 | 公开 |

### 审核管理接口（管理员）

| 接口 | 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|------|
| 获取待审核申请 | GET | `/api/v1/blogger/admin/pending` | 获取待审核的申请列表 | 管理员 |
| 审核申请 | POST | `/api/v1/blogger/admin/review` | 审核博主申请 | 管理员 |
| 获取审核历史 | GET | `/api/v1/blogger/admin/history` | 获取审核历史记录 | 管理员 |
| 批量审核 | POST | `/api/v1/blogger/admin/batch-review` | 批量审核申请 | 管理员 |

---

## 📝 详细接口

### 1. 提交博主申请

**接口地址**: `POST /api/v1/blogger/apply`

**功能描述**: 提交博主申请

**请求参数**:

```json
{
  "nickname": "技术博主",
  "bio": "专注于Java技术分享",
  "category": "TECH",
  "specialties": ["Java", "Spring Boot", "微服务"],
  "experience": "5年Java开发经验",
  "portfolio": "https://github.com/example",
  "socialMedia": {
    "weibo": "https://weibo.com/example",
    "zhihu": "https://zhihu.com/people/example"
  },
  "reason": "希望通过分享技术经验帮助更多人",
  "attachments": [
    {
      "type": "ID_CARD",
      "url": "https://example.com/id-card.jpg"
    },
    {
      "type": "PORTFOLIO",
      "url": "https://example.com/portfolio.pdf"
    }
  ]
}
```

**参数说明**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| nickname | String | 是 | 博主昵称 |
| bio | String | 是 | 个人简介 |
| category | String | 是 | 博主分类：TECH-技术, LIFE-生活, ENTERTAINMENT-娱乐 |
| specialties | List<String> | 是 | 专业领域列表 |
| experience | String | 是 | 相关经验描述 |
| portfolio | String | 否 | 作品集链接 |
| socialMedia | Object | 否 | 社交媒体链接 |
| reason | String | 是 | 申请理由 |
| attachments | List<Object> | 否 | 附件列表 |

**响应示例**:

```json
{
  "code": 200,
  "message": "申请提交成功",
  "data": {
    "applicationId": 123456,
    "status": "PENDING",
    "submitTime": "2024-01-15T10:30:00",
    "estimatedReviewTime": "3-5个工作日"
  },
  "timestamp": 1640995200000
}
```

---

### 2. 获取申请状态

**接口地址**: `GET /api/v1/blogger/status`

**功能描述**: 获取当前用户的申请状态

**响应示例**:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "hasApplication": true,
    "applicationId": 123456,
    "status": "PENDING",
    "submitTime": "2024-01-15T10:30:00",
    "reviewTime": null,
    "rejectReason": null,
    "canReapply": false,
    "nextReapplyTime": "2024-01-22T10:30:00"
  },
  "timestamp": 1640995200000
}
```

---

### 3. 获取申请详情

**接口地址**: `GET /api/v1/blogger/application/{applicationId}`

**功能描述**: 获取申请详情

**路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| applicationId | Long | 是 | 申请ID |

**响应示例**:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "applicationId": 123456,
    "userId": 789012,
    "userName": "张三",
    "userAvatar": "https://example.com/avatar.jpg",
    "nickname": "技术博主",
    "bio": "专注于Java技术分享",
    "category": "TECH",
    "specialties": ["Java", "Spring Boot", "微服务"],
    "experience": "5年Java开发经验",
    "portfolio": "https://github.com/example",
    "socialMedia": {
      "weibo": "https://weibo.com/example",
      "zhihu": "https://zhihu.com/people/example"
    },
    "reason": "希望通过分享技术经验帮助更多人",
    "attachments": [
      {
        "type": "ID_CARD",
        "url": "https://example.com/id-card.jpg",
        "name": "身份证照片"
      }
    ],
    "status": "PENDING",
    "submitTime": "2024-01-15T10:30:00",
    "reviewTime": null,
    "reviewerId": null,
    "reviewerName": null,
    "rejectReason": null,
    "reviewNotes": null
  },
  "timestamp": 1640995200000
}
```

---

### 4. 更新申请信息

**接口地址**: `PUT /api/v1/blogger/application/{applicationId}`

**功能描述**: 更新申请信息（仅限待审核状态）

**路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| applicationId | Long | 是 | 申请ID |

**请求参数**:

```json
{
  "nickname": "技术博主更新",
  "bio": "更新后的个人简介",
  "specialties": ["Java", "Spring Boot", "微服务", "Docker"],
  "experience": "6年Java开发经验",
  "portfolio": "https://github.com/example-updated"
}
```

**响应示例**:

```json
{
  "code": 200,
  "message": "申请信息更新成功",
  "data": null,
  "timestamp": 1640995200000
}
```

---

### 5. 取消申请

**接口地址**: `DELETE /api/v1/blogger/application/{applicationId}`

**功能描述**: 取消申请

**路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| applicationId | Long | 是 | 申请ID |

**响应示例**:

```json
{
  "code": 200,
  "message": "申请已取消",
  "data": null,
  "timestamp": 1640995200000
}
```

---

### 6. 重新申请

**接口地址**: `POST /api/v1/blogger/reapply`

**功能描述**: 重新提交申请

**请求参数**: 同提交申请接口

**响应示例**:

```json
{
  "code": 200,
  "message": "重新申请提交成功",
  "data": {
    "applicationId": 123457,
    "status": "PENDING",
    "submitTime": "2024-01-15T10:30:00",
    "estimatedReviewTime": "3-5个工作日"
  },
  "timestamp": 1640995200000
}
```

---

### 7. 获取博主信息

**接口地址**: `GET /api/v1/blogger/profile/{userId}`

**功能描述**: 获取博主详细信息

**路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| userId | Long | 是 | 用户ID |

**响应示例**:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "userId": 789012,
    "userName": "张三",
    "userAvatar": "https://example.com/avatar.jpg",
    "nickname": "技术博主",
    "bio": "专注于Java技术分享",
    "category": "TECH",
    "specialties": ["Java", "Spring Boot", "微服务"],
    "experience": "5年Java开发经验",
    "portfolio": "https://github.com/example",
    "socialMedia": {
      "weibo": "https://weibo.com/example",
      "zhihu": "https://zhihu.com/people/example"
    },
    "bloggerLevel": "SILVER",
    "certificationTime": "2024-01-20T10:30:00",
    "followerCount": 1000,
    "contentCount": 50,
    "totalLikes": 5000,
    "totalViews": 100000
  },
  "timestamp": 1640995200000
}
```

---

### 8. 更新博主资料

**接口地址**: `PUT /api/v1/blogger/profile`

**功能描述**: 更新博主资料

**请求参数**:

```json
{
  "nickname": "技术博主更新",
  "bio": "更新后的个人简介",
  "specialties": ["Java", "Spring Boot", "微服务", "Docker"],
  "experience": "6年Java开发经验",
  "portfolio": "https://github.com/example-updated",
  "socialMedia": {
    "weibo": "https://weibo.com/example-updated",
    "zhihu": "https://zhihu.com/people/example-updated"
  }
}
```

**响应示例**:

```json
{
  "code": 200,
  "message": "博主资料更新成功",
  "data": null,
  "timestamp": 1640995200000
}
```

---

### 9. 获取博主统计

**接口地址**: `GET /api/v1/blogger/statistics/{userId}`

**功能描述**: 获取博主统计数据

**路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| userId | Long | 是 | 用户ID |

**响应示例**:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "userId": 789012,
    "followerCount": 1000,
    "followingCount": 200,
    "contentCount": 50,
    "totalLikes": 5000,
    "totalViews": 100000,
    "totalComments": 800,
    "totalShares": 300,
    "monthlyGrowth": {
      "followerGrowth": 50,
      "contentGrowth": 5,
      "viewGrowth": 5000
    },
    "topContent": [
      {
        "contentId": 123456,
        "title": "最受欢迎的文章",
        "viewCount": 5000,
        "likeCount": 200
      }
    ]
  },
  "timestamp": 1640995200000
}
```

---

### 10. 获取博主列表

**接口地址**: `GET /api/v1/blogger/list`

**功能描述**: 获取博主列表

**查询参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| pageNo | Integer | 否 | 页码，默认1 |
| pageSize | Integer | 否 | 每页大小，默认20 |
| category | String | 否 | 博主分类过滤 |
| level | String | 否 | 博主等级过滤：BRONZE, SILVER, GOLD, PLATINUM |
| sortBy | String | 否 | 排序方式：FOLLOWER-粉丝数, CONTENT-内容数, POPULARITY-热度 |

**响应示例**:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "records": [
      {
        "userId": 789012,
        "userName": "张三",
        "userAvatar": "https://example.com/avatar.jpg",
        "nickname": "技术博主",
        "bio": "专注于Java技术分享",
        "category": "TECH",
        "bloggerLevel": "SILVER",
        "followerCount": 1000,
        "contentCount": 50,
        "totalLikes": 5000,
        "totalViews": 100000
      }
    ],
    "total": 100,
    "pageNo": 1,
    "pageSize": 20,
    "hasNext": true
  },
  "timestamp": 1640995200000
}
```

---

### 11. 搜索博主

**接口地址**: `GET /api/v1/blogger/search`

**功能描述**: 搜索博主

**查询参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| keyword | String | 是 | 搜索关键词 |
| category | String | 否 | 博主分类过滤 |
| level | String | 否 | 博主等级过滤 |

**响应示例**:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "userId": 789012,
      "userName": "张三",
      "userAvatar": "https://example.com/avatar.jpg",
      "nickname": "技术博主",
      "bio": "专注于Java技术分享",
      "category": "TECH",
      "bloggerLevel": "SILVER",
      "followerCount": 1000,
      "contentCount": 50,
      "matchScore": 0.95
    }
  ],
  "timestamp": 1640995200000
}
```

---

### 12. 获取待审核申请（管理员）

**接口地址**: `GET /api/v1/blogger/admin/pending`

**功能描述**: 获取待审核的申请列表

**查询参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| pageNo | Integer | 否 | 页码，默认1 |
| pageSize | Integer | 否 | 每页大小，默认20 |
| category | String | 否 | 申请分类过滤 |
| sortBy | String | 否 | 排序方式：SUBMIT_TIME-提交时间, PRIORITY-优先级 |

**响应示例**:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "records": [
      {
        "applicationId": 123456,
        "userId": 789012,
        "userName": "张三",
        "userAvatar": "https://example.com/avatar.jpg",
        "nickname": "技术博主",
        "bio": "专注于Java技术分享",
        "category": "TECH",
        "specialties": ["Java", "Spring Boot"],
        "submitTime": "2024-01-15T10:30:00",
        "priority": "HIGH"
      }
    ],
    "total": 10,
    "pageNo": 1,
    "pageSize": 20,
    "hasNext": false
  },
  "timestamp": 1640995200000
}
```

---

### 13. 审核申请（管理员）

**接口地址**: `POST /api/v1/blogger/admin/review`

**功能描述**: 审核博主申请

**请求参数**:

```json
{
  "applicationId": 123456,
  "action": "APPROVE",
  "reason": "申请材料完整，符合博主要求",
  "notes": "建议重点关注Java技术分享",
  "bloggerLevel": "BRONZE"
}
```

**参数说明**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| applicationId | Long | 是 | 申请ID |
| action | String | 是 | 审核动作：APPROVE-通过, REJECT-拒绝 |
| reason | String | 是 | 审核理由 |
| notes | String | 否 | 审核备注 |
| bloggerLevel | String | 否 | 博主等级（通过时必填） |

**响应示例**:

```json
{
  "code": 200,
  "message": "审核完成",
  "data": {
    "applicationId": 123456,
    "status": "APPROVED",
    "reviewTime": "2024-01-15T14:30:00"
  },
  "timestamp": 1640995200000
}
```

---

## 📊 数据模型

### BloggerApplication

| 字段名 | 类型 | 说明 |
|--------|------|------|
| applicationId | Long | 申请ID |
| userId | Long | 用户ID |
| userName | String | 用户名称 |
| userAvatar | String | 用户头像 |
| nickname | String | 博主昵称 |
| bio | String | 个人简介 |
| category | String | 博主分类 |
| specialties | List<String> | 专业领域 |
| experience | String | 相关经验 |
| portfolio | String | 作品集链接 |
| socialMedia | Object | 社交媒体链接 |
| reason | String | 申请理由 |
| attachments | List<Object> | 附件列表 |
| status | String | 申请状态 |
| submitTime | String | 提交时间 |
| reviewTime | String | 审核时间 |
| reviewerId | Long | 审核员ID |
| reviewerName | String | 审核员名称 |
| rejectReason | String | 拒绝理由 |
| reviewNotes | String | 审核备注 |

### BloggerProfile

| 字段名 | 类型 | 说明 |
|--------|------|------|
| userId | Long | 用户ID |
| userName | String | 用户名称 |
| userAvatar | String | 用户头像 |
| nickname | String | 博主昵称 |
| bio | String | 个人简介 |
| category | String | 博主分类 |
| specialties | List<String> | 专业领域 |
| experience | String | 相关经验 |
| portfolio | String | 作品集链接 |
| socialMedia | Object | 社交媒体链接 |
| bloggerLevel | String | 博主等级 |
| certificationTime | String | 认证时间 |
| followerCount | Integer | 粉丝数 |
| contentCount | Integer | 内容数 |
| totalLikes | Integer | 总点赞数 |
| totalViews | Integer | 总浏览量 |

---

## 🔒 权限说明

- **登录用户**: 提交申请、查看自己的申请状态
- **申请者**: 更新、取消自己的申请
- **博主**: 更新自己的博主资料
- **管理员**: 审核申请、查看审核历史
- **公开访问**: 查看博主信息和统计

---

## 📝 错误码

| 错误码 | 说明 |
|--------|------|
| APPLICATION_NOT_FOUND | 申请不存在 |
| APPLICATION_ALREADY_EXISTS | 已有待审核申请 |
| APPLICATION_NOT_PENDING | 申请状态不允许操作 |
| INVALID_BLOGGER_LEVEL | 无效的博主等级 |
| PERMISSION_DENIED | 权限不足 |
| USER_NOT_FOUND | 用户不存在 |
| BLOGGER_NOT_FOUND | 博主不存在 |

---

## 🚀 使用示例

### 提交博主申请

```bash
curl -X POST "http://localhost:9503/api/v1/blogger/apply" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {token}" \
  -d '{
    "nickname": "技术博主",
    "bio": "专注于Java技术分享",
    "category": "TECH",
    "specialties": ["Java", "Spring Boot"],
    "experience": "5年Java开发经验",
    "reason": "希望通过分享技术经验帮助更多人"
  }'
```

### 获取申请状态

```bash
curl -X GET "http://localhost:9503/api/v1/blogger/status" \
  -H "Authorization: Bearer {token}"
```

### 获取博主信息

```bash
curl -X GET "http://localhost:9503/api/v1/blogger/profile/123456"
```

### 审核申请（管理员）

```bash
curl -X POST "http://localhost:9503/api/v1/blogger/admin/review" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {admin-token}" \
  -d '{
    "applicationId": 123456,
    "action": "APPROVE",
    "reason": "申请材料完整，符合博主要求",
    "bloggerLevel": "BRONZE"
  }'
```

---

## 📚 相关资源

- [用户管理API](./user-api.md)
- [内容管理API](./content-api.md)
- [关注管理API](./follow-api.md) 