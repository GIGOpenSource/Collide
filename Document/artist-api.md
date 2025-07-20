# 博主系统API接口文档

## 接口概述

博主系统提供完整的博主申请、审核、管理功能，支持基于ARTIST角色的博主生态建设。所有博主都需要经过严格的审核流程才能获得博主身份。

## 接口列表

### 1. 博主申请接口

#### 1.1 申请成为博主
- **接口地址**: `POST /api/artist/apply`
- **接口描述**: 用户申请成为博主，需要提供详细资料
- **请求参数**:

| 字段名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| userId | long | 是 | 用户ID |
| applicationType | enum | 是 | 申请类型（PERSONAL/ORGANIZATION/BRAND/MEDIA/KOL/PROFESSIONAL） |
| artistName | string | 是 | 博主名称，最大128字符 |
| bio | string | 是 | 博主简介 |
| categories | array | 是 | 博主分类列表 |
| applyReason | string | 是 | 申请理由 |
| resume | string | 否 | 个人简历/机构介绍 |
| portfolioUrl | string | 否 | 作品集链接 |
| socialProof | string | 否 | 社交媒体证明 |
| identityProof | string | 否 | 身份证明文件 |
| qualificationProof | string | 否 | 资质证明文件 |
| contactEmail | string | 是 | 联系邮箱 |
| contactPhone | string | 是 | 联系电话 |
| expectedCollaboration | string | 否 | 期望合作类型 |
| followersProof | string | 否 | 粉丝数量证明 |
| agreeTerms | boolean | 是 | 是否同意协议 |

- **响应示例**:
```json
{
  "code": 200,
  "message": "申请提交成功",
  "data": {
    "success": true,
    "timestamp": 1703123456789,
    "data": {
      "applicationId": 10001,
      "status": "APPLYING",
      "message": "申请已提交，请耐心等待审核"
    }
  }
}
```

#### 1.2 补充申请材料
- **接口地址**: `POST /api/artist/supplement/{applicationId}`
- **接口描述**: 补充申请所需材料
- **请求参数**:

| 字段名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| supplementInfo | string | 是 | 补充材料信息 |

#### 1.3 撤回申请
- **接口地址**: `POST /api/artist/withdraw/{applicationId}`
- **接口描述**: 撤回博主申请
- **请求参数**:

| 字段名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| userId | long | 是 | 用户ID |
| reason | string | 否 | 撤回原因 |

#### 1.4 重新申请
- **接口地址**: `POST /api/artist/reapply`
- **接口描述**: 重新申请成为博主
- **请求参数**: 同申请接口

### 2. 审核管理接口

#### 2.1 审核博主申请
- **接口地址**: `POST /api/artist/review`
- **接口描述**: 审核员审核博主申请（需要管理员权限）
- **请求参数**:

| 字段名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| applicationId | long | 是 | 申请ID |
| reviewerId | long | 是 | 审核员ID |
| reviewResult | enum | 是 | 审核结果（APPROVED/REJECTED/NEED_SUPPLEMENT/SUSPENDED/RE_REVIEW） |
| reviewComment | string | 否 | 审核意见 |
| reviewScore | integer | 否 | 审核评分（1-10分） |
| riskAssessment | string | 否 | 风险评估 |
| suggestedLevel | string | 否 | 建议等级 |
| internalNote | string | 否 | 内部备注 |

- **响应示例**:
```json
{
  "code": 200,
  "message": "审核完成",
  "data": {
    "success": true,
    "timestamp": 1703123456789,
    "data": {
      "applicationId": 10001,
      "reviewResult": "APPROVED",
      "artistId": 1001
    },
    "operationMessage": "申请已通过审核，博主账号已激活"
  }
}
```

#### 2.2 批量审核
- **接口地址**: `POST /api/artist/review/batch`
- **接口描述**: 批量审核博主申请
- **请求参数**: 审核请求数组

#### 2.3 获取待审核列表
- **接口地址**: `GET /api/artist/review/pending`
- **接口描述**: 获取待审核的申请列表
- **请求参数**:

| 字段名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| applicationType | enum | 否 | 申请类型过滤 |
| currentPage | integer | 否 | 当前页，默认1 |
| pageSize | integer | 否 | 页面大小，默认10 |
| onlyPendingReview | boolean | 否 | 是否只查询待审核，默认true |

#### 2.4 分配审核员
- **接口地址**: `POST /api/artist/assign-reviewer`
- **接口描述**: 为申请分配审核员
- **请求参数**:

| 字段名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| applicationIds | array | 是 | 申请ID列表 |
| reviewerId | long | 是 | 审核员ID |

### 3. 博主信息管理

#### 3.1 更新博主信息
- **接口地址**: `PUT /api/artist/update`
- **接口描述**: 更新博主信息
- **请求参数**:

| 字段名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| artistId | long | 是 | 博主ID |
| artistName | string | 否 | 博主名称 |
| bio | string | 否 | 博主简介 |
| categories | array | 否 | 博主分类 |
| avatarUrl | string | 否 | 头像URL |
| coverUrl | string | 否 | 封面图URL |
| personalUrl | string | 否 | 个人主页URL |
| contactEmail | string | 否 | 联系邮箱 |
| contactPhone | string | 否 | 联系电话 |
| socialMediaAccounts | string | 否 | 社交媒体账号 |
| expertiseTags | array | 否 | 擅长领域标签 |
| collaborationIntent | string | 否 | 合作意向 |
| updateReason | string | 否 | 更新原因 |

#### 3.2 博主状态管理
- **接口地址**: `POST /api/artist/status/{operation}`
- **接口描述**: 管理博主状态（需要管理员权限）
- **路径参数**: operation（activate/suspend/disable/restore/cancel）
- **请求参数**:

| 字段名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| artistId | long | 是 | 博主ID |
| operatorId | long | 是 | 操作者ID |
| reason | string | 否 | 操作理由 |

### 4. 博主查询接口

#### 4.1 查询博主详情
- **接口地址**: `GET /api/artist/{artistId}`
- **接口描述**: 根据ID查询博主详细信息
- **响应示例**:
```json
{
  "code": 200,
  "message": "查询成功",
  "data": {
    "artistId": 1001,
    "userId": 2001,
    "nickName": "技术达人",
    "artistName": "前端技术分享者",
    "bio": "专注于前端技术分享，React、Vue等框架深度解析",
    "status": "ACTIVE",
    "applicationType": "PERSONAL",
    "categories": ["TECHNOLOGY"],
    "level": "GROWING",
    "avatarUrl": "https://example.com/avatar.jpg",
    "followersCount": 5280,
    "followingCount": 128,
    "worksCount": 45,
    "totalLikes": 12850,
    "totalViews": 85200,
    "verified": true,
    "verificationType": "PERSONAL",
    "verificationDesc": "个人认证",
    "contactEmail": "tech@example.com",
    "expertiseTags": ["React", "Vue", "JavaScript"],
    "lastActiveTime": "2023-12-01T15:30:00",
    "createdTime": "2023-06-01T10:00:00"
  }
}
```

#### 4.2 根据用户ID查询博主
- **接口地址**: `GET /api/artist/user/{userId}`
- **接口描述**: 根据用户ID查询博主信息

#### 4.3 分页查询博主
- **接口地址**: `GET /api/artist/page`
- **接口描述**: 分页查询博主列表
- **请求参数**:

| 字段名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| currentPage | integer | 否 | 当前页，默认1 |
| pageSize | integer | 否 | 页面大小，默认10 |
| artistName | string | 否 | 博主名称（模糊查询） |
| status | enum | 否 | 博主状态 |
| applicationType | enum | 否 | 申请类型 |
| category | enum | 否 | 博主分类 |
| level | enum | 否 | 博主等级 |
| verified | boolean | 否 | 是否认证 |
| minFollowers | integer | 否 | 最小粉丝数 |
| maxFollowers | integer | 否 | 最大粉丝数 |
| keyword | string | 否 | 关键词搜索 |
| sortBy | string | 否 | 排序字段，默认followersCount |
| sortDirection | string | 否 | 排序方向，默认DESC |

#### 4.4 搜索博主
- **接口地址**: `GET /api/artist/search`
- **接口描述**: 根据关键词搜索博主
- **请求参数**:

| 字段名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| keyword | string | 是 | 搜索关键词 |
| limit | integer | 否 | 限制数量，默认10 |

#### 4.5 查询热门博主
- **接口地址**: `GET /api/artist/hot`
- **接口描述**: 查询热门博主列表
- **请求参数**:

| 字段名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| limit | integer | 否 | 限制数量，默认20 |

#### 4.6 查询推荐博主
- **接口地址**: `GET /api/artist/recommend/{userId}`
- **接口描述**: 为用户推荐博主
- **请求参数**:

| 字段名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| limit | integer | 否 | 推荐数量，默认10 |

### 5. 申请查询接口

#### 5.1 查询申请详情
- **接口地址**: `GET /api/artist/application/{applicationId}`
- **接口描述**: 查询申请详细信息
- **响应示例**:
```json
{
  "code": 200,
  "message": "查询成功",
  "data": {
    "applicationId": 10001,
    "userId": 2001,
    "nickName": "技术爱好者",
    "applicationType": "PERSONAL",
    "status": "REVIEWING",
    "artistName": "前端技术分享者",
    "bio": "专注前端技术分享",
    "categories": ["TECHNOLOGY"],
    "applyReason": "希望分享技术经验，帮助其他开发者",
    "contactEmail": "tech@example.com",
    "contactPhone": "13800138000",
    "reviewerId": 3001,
    "reviewerName": "审核员张三",
    "reviewResult": null,
    "reviewComment": null,
    "submitTime": "2023-12-01T10:00:00",
    "applicationRound": 1
  }
}
```

#### 5.2 查询用户申请历史
- **接口地址**: `GET /api/artist/application/history/{userId}`
- **接口描述**: 查询用户的申请历史记录

#### 5.3 分页查询申请列表
- **接口地址**: `GET /api/artist/application/page`
- **接口描述**: 分页查询申请列表（管理员权限）
- **请求参数**:

| 字段名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| applicationType | enum | 否 | 申请类型 |
| status | enum | 否 | 申请状态 |
| reviewResult | enum | 否 | 审核结果 |
| reviewerId | long | 否 | 审核员ID |
| startTime | date | 否 | 申请开始时间 |
| endTime | date | 否 | 申请结束时间 |
| currentPage | integer | 否 | 当前页，默认1 |
| pageSize | integer | 否 | 页面大小，默认10 |

### 6. 博主统计接口

#### 6.1 查询博主统计
- **接口地址**: `GET /api/artist/statistics/{artistId}`
- **接口描述**: 查询博主统计信息
- **响应示例**:
```json
{
  "code": 200,
  "message": "查询成功",
  "data": {
    "artistId": 1001,
    "artistName": "前端技术分享者",
    "todayFollowersGrowth": 25,
    "weekFollowersGrowth": 156,
    "monthFollowersGrowth": 680,
    "todayWorksCount": 2,
    "weekWorksCount": 8,
    "monthWorksCount": 25,
    "todayLikes": 285,
    "weekLikes": 1250,
    "monthLikes": 5680,
    "engagementRate": 0.0856,
    "followersActivity": 0.7234,
    "contentQualityScore": 0.8567,
    "influenceIndex": 1256.75,
    "followersGrowthTrend": {
      "2023-12-01": 25,
      "2023-11-30": 18,
      "2023-11-29": 32
    },
    "popularTags": {
      "React": 15,
      "JavaScript": 12,
      "Vue": 8
    },
    "bestPublishTime": "19:00-21:00",
    "lastUpdated": "2023-12-01T16:00:00"
  }
}
```

#### 6.2 查询博主排行榜
- **接口地址**: `GET /api/artist/ranking`
- **接口描述**: 查询博主排行榜
- **请求参数**:

| 字段名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| rankType | string | 是 | 排行类型（followers/likes/views/influence） |
| limit | integer | 否 | 限制数量，默认10 |

#### 6.3 批量查询统计
- **接口地址**: `POST /api/artist/statistics/batch`
- **接口描述**: 批量查询博主统计信息
- **请求参数**:

| 字段名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| artistIds | array | 是 | 博主ID列表 |

### 7. 认证管理接口

#### 7.1 申请认证
- **接口地址**: `POST /api/artist/verification/apply`
- **接口描述**: 申请博主认证
- **请求参数**:

| 字段名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| artistId | long | 是 | 博主ID |
| verificationType | string | 是 | 认证类型 |
| verificationProof | string | 是 | 认证证明材料 |

#### 7.2 审核认证
- **接口地址**: `POST /api/artist/verification/review`
- **接口描述**: 审核博主认证申请（管理员权限）
- **请求参数**:

| 字段名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| artistId | long | 是 | 博主ID |
| approved | boolean | 是 | 是否通过 |
| reviewComment | string | 否 | 审核意见 |
| reviewerId | long | 是 | 审核员ID |

#### 7.3 取消认证
- **接口地址**: `POST /api/artist/verification/revoke`
- **接口描述**: 取消博主认证（管理员权限）
- **请求参数**:

| 字段名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| artistId | long | 是 | 博主ID |
| operatorId | long | 是 | 操作者ID |
| reason | string | 是 | 取消原因 |

### 8. 等级管理接口

#### 8.1 更新博主等级
- **接口地址**: `POST /api/artist/level/update/{artistId}`
- **接口描述**: 根据粉丝数自动更新博主等级

#### 8.2 批量更新等级
- **接口地址**: `POST /api/artist/level/batch-update`
- **接口描述**: 批量更新博主等级
- **请求参数**:

| 字段名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| artistIds | array | 是 | 博主ID列表 |

#### 8.3 手动设置等级
- **接口地址**: `POST /api/artist/level/set`
- **接口描述**: 手动设置博主等级（管理员权限）
- **请求参数**:

| 字段名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| artistId | long | 是 | 博主ID |
| level | string | 是 | 目标等级 |
| operatorId | long | 是 | 操作者ID |
| reason | string | 是 | 设置原因 |

## 错误码说明

| 错误码 | 错误信息 | 描述 |
|--------|----------|------|
| 50001 | 用户不存在 | 指定的用户ID不存在 |
| 50002 | 用户已是博主 | 用户已经是博主身份 |
| 50003 | 申请已存在 | 用户已有待审核的申请 |
| 50004 | 申请不存在 | 指定的申请ID不存在 |
| 50005 | 博主不存在 | 指定的博主ID不存在 |
| 50006 | 权限不足 | 当前用户权限不足 |
| 50007 | 审核状态错误 | 申请状态不允许当前操作 |
| 50008 | 博主名称已存在 | 申请的博主名称已被使用 |
| 50009 | 申请材料不完整 | 缺少必要的申请材料 |
| 50010 | 申请次数超限 | 申请次数超过限制 |

## 使用示例

### 1. 用户申请成为博主
```javascript
const response = await fetch('/api/artist/apply', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json',
    'Authorization': 'Bearer ' + token
  },
  body: JSON.stringify({
    userId: 1001,
    applicationType: 'PERSONAL',
    artistName: '技术分享达人',
    bio: '专注前端技术分享，帮助开发者成长',
    categories: ['TECHNOLOGY'],
    applyReason: '希望通过平台分享技术经验',
    contactEmail: 'tech@example.com',
    contactPhone: '13800138000',
    agreeTerms: true
  })
});
```

### 2. 审核员审核申请
```javascript
const response = await fetch('/api/artist/review', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json',
    'Authorization': 'Bearer ' + adminToken
  },
  body: JSON.stringify({
    applicationId: 10001,
    reviewerId: 3001,
    reviewResult: 'APPROVED',
    reviewComment: '申请资料完整，符合博主要求',
    reviewScore: 8
  })
});
```

### 3. 查询热门博主
```javascript
const response = await fetch('/api/artist/hot?limit=20');
const data = await response.json();
console.log('热门博主:', data.data);
```

## 注意事项

### 1. 申请管理
- **申请限制**: 每个用户24小时内只能提交一次申请
- **材料要求**: 不同申请类型需要提供不同的证明材料
- **审核周期**: 一般审核周期为3-7个工作日
- **重复申请**: 支持重新申请，最多3轮申请机会

### 2. 审核流程
- **审核权限**: 只有管理员角色可以进行审核
- **分级审核**: 不同类型申请需要不同级别审核员
- **审核记录**: 所有审核操作都有完整的操作日志
- **申诉机制**: 被拒绝的申请可以进行申诉

### 3. 博主管理
- **状态管理**: 博主状态变更需要管理员权限
- **等级更新**: 系统每天自动更新博主等级
- **认证管理**: 认证申请和审核需要单独流程
- **数据统计**: 统计数据每小时更新一次

### 4. 性能优化
- **缓存机制**: 热门博主数据会被缓存
- **分页查询**: 大量数据查询请使用分页接口
- **批量操作**: 支持批量审核和更新操作
- **异步处理**: 统计数据更新采用异步处理

### 5. 数据安全
- **敏感信息**: 身份证明等敏感信息加密存储
- **权限控制**: 严格的权限控制和操作日志
- **数据脱敏**: 对外接口中敏感信息脱敏处理
- **审计日志**: 完整的操作审计日志记录 