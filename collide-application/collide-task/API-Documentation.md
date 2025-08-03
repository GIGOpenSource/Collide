# Collide Task API 文档

## 📋 模块概述

Collide Task 模块基于简洁版 SQL 设计，提供完整的任务管理功能，包括每日任务、周常任务、成就任务等，支持多种奖励类型的自动发放。

## 🎯 核心功能

### 任务模板管理
- ✅ 创建、查询、更新、删除任务模板
- ✅ 支持每日、周常、成就等任务类型
- ✅ 灵活的任务分类和动作配置

### 用户任务跟踪
- ✅ 实时记录用户任务进度
- ✅ 自动判断任务完成状态
- ✅ 支持任务进度手动和自动更新

### 奖励系统
- ✅ 金币、道具、VIP、经验等多种奖励类型
- ✅ 自动奖励发放机制
- ✅ 与用户钱包系统无缝集成

### 统计分析
- ✅ 用户任务完成统计
- ✅ 奖励获得统计
- ✅ 任务完成排行榜

## 🔗 API 端点

### 用户任务管理

#### 获取用户今日任务
```
GET /api/v1/task/today/{userId}
```

#### 分页查询用户任务
```
POST /api/v1/task/user/query
```

#### 更新任务进度
```
POST /api/v1/task/progress/update
```

#### 领取任务奖励
```
POST /api/v1/task/reward/claim?userId={userId}&taskId={taskId}
```

#### 获取可领取奖励任务
```
GET /api/v1/task/claimable/{userId}
```

#### 初始化每日任务
```
POST /api/v1/task/daily/init?userId={userId}
```

### 任务统计

#### 用户任务统计
```
GET /api/v1/task/statistics/user/{userId}
```

#### 用户奖励统计
```
GET /api/v1/task/statistics/reward/{userId}
```

#### 任务完成排行榜
```
GET /api/v1/task/ranking?taskType={taskType}&limit={limit}
```

### 用户行为处理

#### 处理用户行为
```
POST /api/v1/task/action/handle?userId={userId}&actionType={actionType}
```

### 系统管理

#### 重置每日任务
```
POST /api/v1/task/system/reset-daily
```

#### 自动发放奖励
```
POST /api/v1/task/system/process-rewards
```

#### 系统任务统计
```
GET /api/v1/task/system/statistics
```

#### 健康检查
```
GET /api/v1/task/health
```

## 📊 数据模型

### TaskTemplate (任务模板)
- `id` - 任务模板ID
- `taskName` - 任务名称
- `taskDesc` - 任务描述
- `taskType` - 任务类型 (daily/weekly/achievement)
- `taskCategory` - 任务分类 (login/content/social/consume)
- `taskAction` - 任务动作
- `targetCount` - 目标完成次数
- `sortOrder` - 排序值
- `isActive` - 是否启用
- `startDate` - 开始日期
- `endDate` - 结束日期

### TaskReward (任务奖励)
- `id` - 奖励ID
- `taskId` - 任务模板ID
- `rewardType` - 奖励类型 (coin/item/vip/experience)
- `rewardName` - 奖励名称
- `rewardAmount` - 奖励数量
- `rewardData` - 奖励扩展数据
- `isMainReward` - 是否主要奖励

### UserTaskRecord (用户任务记录)
- `id` - 记录ID
- `userId` - 用户ID
- `taskId` - 任务模板ID
- `taskDate` - 任务日期
- `currentCount` - 当前完成次数
- `targetCount` - 目标完成次数
- `isCompleted` - 是否已完成
- `isRewarded` - 是否已领取奖励
- `completeTime` - 完成时间
- `rewardTime` - 奖励领取时间

### UserRewardRecord (用户奖励记录)
- `id` - 奖励记录ID
- `userId` - 用户ID
- `taskRecordId` - 任务记录ID
- `rewardSource` - 奖励来源
- `rewardType` - 奖励类型
- `rewardAmount` - 奖励数量
- `status` - 发放状态
- `grantTime` - 发放时间

## 🚀 服务配置

### 端口配置
- HTTP 端口: 8207
- Dubbo 端口: 20207

### 依赖服务
- MySQL 数据库
- Redis 缓存
- Nacos 注册中心

### 环境变量
- `DB_HOST` - 数据库主机
- `DB_PORT` - 数据库端口
- `DB_NAME` - 数据库名称
- `DB_USERNAME` - 数据库用户名
- `DB_PASSWORD` - 数据库密码
- `REDIS_HOST` - Redis主机
- `REDIS_PORT` - Redis端口
- `NACOS_ADDR` - Nacos地址

## 🎨 特性亮点

1. **无连表设计** - 通过冗余字段避免复杂JOIN查询，提升性能
2. **智能缓存** - 使用JetCache提供分布式缓存，减少数据库压力
3. **自动化处理** - 支持任务进度自动更新和奖励自动发放
4. **灵活配置** - 支持多种任务类型和奖励配置
5. **完善监控** - 提供详细的统计信息和健康检查接口

## 📈 业务流程

### 任务完成流程
1. 用户执行某个行为（登录、发布内容等）
2. 系统调用 `handleUserAction` 接口
3. 自动匹配相关任务并更新进度
4. 达到目标时自动标记任务完成
5. 触发奖励发放流程

### 奖励发放流程
1. 任务完成后创建奖励记录
2. 根据奖励类型调用相应的发放逻辑
3. 金币奖励直接发放到用户钱包
4. 其他类型奖励记录到奖励表
5. 更新发放状态和时间

## 🔧 技术架构

- **框架**: Spring Boot 3.x + Dubbo 3.x
- **数据库**: MySQL 8.0 + MyBatis-Plus
- **缓存**: Redis + JetCache
- **注册中心**: Nacos
- **API文档**: Swagger 3.0
- **监控**: Spring Boot Actuator

## 🛠️ 开发指南

### 本地开发
1. 启动 MySQL、Redis、Nacos 服务
2. 执行 SQL 脚本初始化数据库
3. 配置环境变量或修改配置文件
4. 运行 `CollideTaskApplication` 启动服务

### 接口调试
- Swagger UI: http://localhost:8207/swagger-ui.html
- API 文档: http://localhost:8207/v3/api-docs
- 健康检查: http://localhost:8207/actuator/health