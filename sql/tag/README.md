# Tag 模块 SQL 设计说明

## 📋 概述

Tag 模块采用**强制去连表设计（No-Join Design）**，所有查询均基于单表操作，通过合理的数据冗余和业务逻辑来避免复杂的连表查询，提升查询性能和系统稳定性。

## 🎯 设计原则

### 1. 强制单表查询
- **禁止使用**: JOIN、INNER JOIN、LEFT JOIN、RIGHT JOIN、OUTER JOIN
- **只使用**: 单表查询、子查询（必要时）
- **数据获取**: 通过多次单表查询组装数据

### 2. 合理数据冗余
- 在关联表中存储必要的冗余字段
- 避免频繁的跨表查询
- 通过应用层逻辑保证数据一致性

### 3. ID 存储策略
- 所有外键字段仅存储 ID，不建立外键约束
- 通过应用层控制数据完整性
- 支持数据的独立维护和扩展

## 🗂️ 表结构设计

### 1. 主表：t_tag
**用途**: 存储标签的基本信息

**去连表化设计**:
- `category_id`: 仅存储分类ID，不做外键关联
- 不在该表中冗余分类名称等信息
- 通过独立查询获取分类信息

### 2. 关联表：t_user_interest_tag  
**用途**: 用户与标签的兴趣关联

**去连表化设计**:
- `user_id`: 仅存储用户ID
- `tag_id`: 仅存储标签ID
- 不冗余用户昵称、标签名称等信息
- 需要时通过独立查询获取详细信息

### 3. 关联表：t_content_tag
**用途**: 内容与标签的关联

**去连表化设计**:
- `content_id`: 仅存储内容ID
- `tag_id`: 仅存储标签ID
- 极简设计，只保留关联关系

### 4. 统计表：t_tag_statistics
**用途**: 标签的统计信息

**去连表化设计**:
- `tag_id`: 仅存储标签ID
- 通过定时任务统计和更新数据
- 避免实时统计查询的性能问题

## 📊 查询策略

### 1. 基础标签查询
```sql
-- ✅ 正确：单表查询
SELECT * FROM t_tag WHERE id = ?;
SELECT * FROM t_tag WHERE tag_type = ? AND status = 'active';

-- ❌ 禁止：连表查询
SELECT t.*, c.name as category_name 
FROM t_tag t LEFT JOIN t_category c ON t.category_id = c.id;
```

### 2. 用户兴趣标签查询
```sql
-- ✅ 正确：分步查询
-- Step 1: 查询用户兴趣标签关联
SELECT tag_id, interest_score FROM t_user_interest_tag WHERE user_id = ?;
-- Step 2: 查询标签详细信息
SELECT * FROM t_tag WHERE id IN (?, ?, ?);

-- ❌ 禁止：连表查询
SELECT t.*, uit.interest_score 
FROM t_tag t JOIN t_user_interest_tag uit ON t.id = uit.tag_id 
WHERE uit.user_id = ?;
```

### 3. 内容标签查询
```sql
-- ✅ 正确：分步查询
-- Step 1: 查询内容关联的标签ID
SELECT tag_id FROM t_content_tag WHERE content_id = ?;
-- Step 2: 查询标签信息
SELECT * FROM t_tag WHERE id IN (?, ?, ?);

-- ❌ 禁止：连表查询
SELECT t.* FROM t_tag t 
JOIN t_content_tag ct ON t.id = ct.tag_id 
WHERE ct.content_id = ?;
```

## 🔧 应用层处理

### 1. 数据组装
- 在 Service 层进行数据组装
- 通过 Repository 进行单表查询
- 避免在 SQL 层进行数据关联

### 2. 缓存策略
- 对热门标签进行缓存
- 缓存标签统计信息
- 减少数据库查询压力

### 3. 异步处理
- 统计数据异步更新
- 使用消息队列处理数据一致性
- 避免实时计算影响性能

## 📈 性能优化

### 1. 索引策略
- 为所有查询字段创建合适索引
- 避免多字段复合索引的过度使用
- 定期分析和优化索引效果

### 2. 数据分页
- 所有列表查询支持分页
- 使用 LIMIT 控制结果集大小
- 避免全表扫描

### 3. 批量操作
- 支持批量插入和删除
- 使用事务保证操作原子性
- 减少数据库连接开销

## ⚠️ 注意事项

### 1. 数据一致性
- 通过应用层逻辑保证数据一致性
- 使用分布式事务（如有必要）
- 定期进行数据校验和修复

### 2. 业务逻辑
- 在 Service 层处理复杂业务逻辑
- 避免在数据库层面处理业务规则
- 保持代码的可维护性

### 3. 扩展性
- 表结构支持水平扩展
- 避免外键约束的限制
- 支持数据的分库分表

## 🚀 部署建议

### 1. 执行顺序
1. 先执行主表创建（t_tag）
2. 再执行关联表创建
3. 最后插入初始数据

### 2. 数据迁移
- 如有旧数据，先备份
- 分批迁移数据，避免长时间锁表
- 验证数据完整性

### 3. 监控指标
- 监控查询性能
- 监控数据一致性
- 设置合理的告警阈值

---

**重要提醒**: 本模块严格遵循无连表设计原则，任何连表查询都是被禁止的。如需关联数据，请通过应用层的多次单表查询来实现。 