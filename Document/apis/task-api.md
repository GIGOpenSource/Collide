# Collide Task API 文档

## 📋 接口概述

基于 `TaskController.java` 提供的任务管理相关接口，包含用户任务管理、任务统计、用户行为处理、系统管理等功能。

**Base URL**: `/api/v1/task`

---

## 🎯 用户任务管理

### 1. 获取用户今日任务

**接口描述**: 获取用户当天的所有任务列表

```http
GET /api/v1/task/today/{userId}
```

**路径参数**:
| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| userId | Long | 是 | 用户ID，必须大于0 |

**响应示例**:
```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "userId": 123,
      "taskId": 1,
      "taskDate": "2024-01-16",
      "taskName": "每日登录",
      "taskDesc": "每日登录获得金币奖励",
      "taskType": "daily",
      "taskCategory": "login",
      "targetCount": 1,
      "currentCount": 0,
      "isCompleted": false,
      "isRewarded": false,
      "progressPercentage": 0.0,
      "remainingCount": 1,
      "canClaimReward": false,
      "createTime": "2024-01-16T08:00:00",
      "updateTime": "2024-01-16T08:00:00"
    }
  ]
}
```

---

### 2. 分页查询用户任务

**接口描述**: 根据条件分页查询用户任务记录

```http
POST /api/v1/task/user/query
```

**请求体**:
```json
{
  "userId": 123,
  "taskId": 1,
  "taskType": "daily",
  "taskCategory": "login",
  "isCompleted": true,
  "isRewarded": false,
  "startDate": "2024-01-01",
  "endDate": "2024-01-31",
  "orderBy": "taskDate",
  "orderDirection": "DESC",
  "currentPage": 1,
  "pageSize": 20
}
```

**请求参数**:
| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| userId | Long | 是 | 用户ID |
| taskId | Long | 否 | 任务ID |
| taskType | String | 否 | 任务类型: daily/weekly/achievement |
| taskCategory | String | 否 | 任务分类: login/content/social/consume |
| isCompleted | Boolean | 否 | 是否已完成 |
| isRewarded | Boolean | 否 | 是否已领取奖励 |
| startDate | String | 否 | 开始日期 |
| endDate | String | 否 | 结束日期 |
| orderBy | String | 否 | 排序字段，默认taskDate |
| orderDirection | String | 否 | 排序方向，默认DESC |
| currentPage | Integer | 否 | 当前页码，默认1 |
| pageSize | Integer | 否 | 页面大小，默认20 |

**响应示例**:
```json
{
  "success": true,
  "data": {
    "records": [
      {
        "id": 1,
        "userId": 123,
        "taskId": 1,
        "taskName": "每日登录",
        "taskType": "daily",
        "isCompleted": true,
        "progressPercentage": 100.0
      }
    ],
    "total": 50,
    "currentPage": 1,
    "pageSize": 20,
    "totalPage": 3
  }
}
```

---

### 3. 更新任务进度

**接口描述**: 更新用户任务完成进度

```http
POST /api/v1/task/progress/update
```

**请求体**:
```json
{
  "userId": 123,
  "taskId": 1,
  "taskAction": "login",
  "incrementCount": 1,
  "extraData": {
    "source": "mobile_app"
  }
}
```

**请求参数**:
| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| userId | Long | 是 | 用户ID，必须为正数 |
| taskId | Long | 是 | 任务ID，必须为正数 |
| taskAction | String | 是 | 任务动作 |
| incrementCount | Integer | 是 | 增加的完成次数，必须大于0，默认1 |
| extraData | Map | 否 | 扩展数据 |

**响应示例**:
```json
{
  "success": true,
  "data": {
    "id": 1,
    "userId": 123,
    "taskId": 1,
    "currentCount": 1,
    "targetCount": 1,
    "isCompleted": true,
    "progressPercentage": 100.0,
    "canClaimReward": true
  }
}
```

---

### 4. 领取任务奖励

**接口描述**: 领取已完成任务的奖励

```http
POST /api/v1/task/reward/claim?userId=123&taskId=1
```

**查询参数**:
| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| userId | Long | 是 | 用户ID，必须大于0 |
| taskId | Long | 是 | 任务ID，必须大于0 |

**响应示例**:
```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "taskId": 1,
      "rewardType": "coin",
      "rewardName": "金币",
      "rewardAmount": 10,
      "isMainReward": true,
      "status": "success",
      "grantTime": "2024-01-16T10:30:00"
    }
  ]
}
```

---

### 5. 获取可领取奖励任务

**接口描述**: 获取用户可以领取奖励的任务列表

```http
GET /api/v1/task/claimable/{userId}
```

**路径参数**:
| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| userId | Long | 是 | 用户ID，必须大于0 |

**响应示例**:
```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "userId": 123,
      "taskId": 1,
      "taskName": "每日登录",
      "isCompleted": true,
      "isRewarded": false,
      "canClaimReward": true,
      "completeTime": "2024-01-16T09:00:00"
    }
  ]
}
```

---

### 6. 初始化每日任务

**接口描述**: 初始化用户当天的每日任务

```http
POST /api/v1/task/daily/init?userId=123
```

**查询参数**:
| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| userId | Long | 是 | 用户ID，必须大于0 |

**响应示例**:
```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "userId": 123,
      "taskId": 1,
      "taskName": "每日登录",
      "taskType": "daily",
      "currentCount": 0,
      "targetCount": 1,
      "isCompleted": false
    }
  ]
}
```

---

## 📊 任务统计

### 7. 用户任务统计

**接口描述**: 获取用户任务完成统计信息

```http
GET /api/v1/task/statistics/user/{userId}
```

**路径参数**:
| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| userId | Long | 是 | 用户ID，必须大于0 |

**响应示例**:
```json
{
  "success": true,
  "data": {
    "totalTasks": 5,
    "completedTasks": 3,
    "rewardedTasks": 2,
    "incompleteTasks": 2,
    "todayProgress": {
      "daily": {
        "total": 3,
        "completed": 2
      }
    }
  }
}
```

---

### 8. 用户奖励统计

**接口描述**: 获取用户奖励获得统计信息

```http
GET /api/v1/task/statistics/reward/{userId}
```

**路径参数**:
| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| userId | Long | 是 | 用户ID，必须大于0 |

**响应示例**:
```json
{
  "success": true,
  "data": {
    "totalRewards": 10,
    "successRewards": 8,
    "pendingRewards": 1,
    "failedRewards": 1,
    "totalCoins": 150,
    "rewardsByType": {
      "coin": 8,
      "item": 1,
      "vip": 1
    }
  }
}
```

---

### 9. 任务完成排行榜

**接口描述**: 获取任务完成情况排行榜

```http
GET /api/v1/task/ranking?taskType=daily&limit=10
```

**查询参数**:
| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| taskType | String | 否 | 任务类型 |
| limit | Integer | 否 | 排行榜条数，默认10，最小1 |

**响应示例**:
```json
{
  "success": true,
  "data": [
    {
      "userId": 123,
      "completedCount": 5,
      "totalProgress": 50,
      "rank": 1
    },
    {
      "userId": 456,
      "completedCount": 4,
      "totalProgress": 45,
      "rank": 2
    }
  ]
}
```

---

## 🔄 用户行为处理

### 10. 处理用户行为

**接口描述**: 处理用户行为触发的任务进度更新

```http
POST /api/v1/task/action/handle?userId=123&actionType=login
```

**查询参数**:
| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| userId | Long | 是 | 用户ID，必须大于0 |
| actionType | String | 是 | 行为类型 |

**请求体** (可选):
```json
{
  "platform": "mobile",
  "timestamp": "2024-01-16T10:00:00",
  "extra": {
    "device": "iPhone"
  }
}
```

**响应示例**:
```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "taskName": "每日登录",
      "currentCount": 1,
      "targetCount": 1,
      "isCompleted": true,
      "progressPercentage": 100.0
    }
  ]
}
```

---

## ⚙️ 系统管理接口

### 11. 重置每日任务

**接口描述**: 系统定时调用 - 重置所有用户的每日任务

```http
POST /api/v1/task/system/reset-daily
```

**响应示例**:
```json
{
  "success": true,
  "data": 1
}
```

---

### 12. 自动发放奖励

**接口描述**: 系统定时调用 - 自动发放待发放的奖励

```http
POST /api/v1/task/system/process-rewards
```

**响应示例**:
```json
{
  "success": true,
  "data": 25
}
```

---

### 13. 系统任务统计

**接口描述**: 获取系统级任务统计信息

```http
GET /api/v1/task/system/statistics
```

**响应示例**:
```json
{
  "success": true,
  "data": {
    "totalTasks": 100,
    "activeTasks": 85,
    "expiredTasks": 15,
    "tasksByType": {
      "daily": 50,
      "weekly": 30,
      "achievement": 20
    },
    "tasksByCategory": {
      "login": 20,
      "content": 30,
      "social": 35,
      "consume": 15
    }
  }
}
```

---

## 🔍 健康检查

### 14. 健康检查

**接口描述**: 任务模块健康状态检查

```http
GET /api/v1/task/health
```

**响应示例**:
```json
{
  "success": true,
  "data": {
    "status": "UP",
    "module": "collide-task",
    "version": "2.0.0",
    "timestamp": 1705392000000
  }
}
```

---

## 📝 通用响应格式

### 成功响应
```json
{
  "success": true,
  "data": {},
  "message": "操作成功"
}
```

### 失败响应
```json
{
  "success": false,
  "errorCode": "INVALID_PARAM",
  "errorMessage": "参数无效",
  "data": null
}
```

---

## 🚨 错误码说明

| 错误码 | 描述 | 解决方案 |
|--------|------|----------|
| INVALID_PARAM | 参数无效 | 检查请求参数格式和取值范围 |
| USER_NOT_FOUND | 用户不存在 | 确认用户ID是否正确 |
| TASK_NOT_FOUND | 任务不存在 | 确认任务ID是否正确 |
| TASK_NOT_COMPLETED | 任务未完成 | 先完成任务再领取奖励 |
| REWARD_ALREADY_CLAIMED | 奖励已领取 | 不能重复领取奖励 |
| TASK_EXPIRED | 任务已过期 | 任务已过期无法操作 |
| SYSTEM_ERROR | 系统错误 | 联系技术支持 |

---

## 📚 使用说明

### 1. 任务流程
1. 用户登录后调用初始化每日任务接口
2. 获取用户今日任务列表
3. 用户执行相关行为，系统调用处理用户行为接口
4. 任务完成后，用户调用领取奖励接口

### 2. 权限说明
- 用户接口：需要用户身份验证
- 系统接口：需要系统级权限
- 健康检查：无需权限

### 3. 调用频率限制
- 用户接口：每用户每分钟100次
- 系统接口：每分钟10次
- 健康检查：无限制

### 4. 注意事项
- 所有时间字段采用ISO 8601格式
- 用户ID必须为正整数
- 分页参数pageSize最大100
- 系统接口仅供内部调用
