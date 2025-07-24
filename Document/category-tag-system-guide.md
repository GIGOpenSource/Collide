# Collide 分类和标签系统指南

## 📋 系统概述

Collide 分类和标签系统为内容管理和用户兴趣推荐提供强大的基础支撑，包含以下核心功能：

- 🗂️ **分层分类系统** - 支持无限级分类层次
- 🏷️ **多类型标签** - 内容标签、兴趣标签、系统标签
- 👤 **用户兴趣画像** - 基于标签的个性化推荐
- 🔍 **搜索增强** - 为搜索提供分类和标签筛选

## 🏗️ 系统架构

### 数据表结构

```sql
-- 分类表（支持层级）
t_category
├── 娱乐 (level=1)
│   ├── 音乐 (level=2)
│   ├── 影视 (level=2)
│   └── 游戏 (level=2)
├── 教育 (level=1)
│   ├── 编程 (level=2)
│   └── 语言学习 (level=2)
└── 生活 (level=1)

-- 标签表（多类型）
t_tag
├── content（内容标签）：热门、搞笑、教程...
├── interest（兴趣标签）：编程爱好者、音乐发烧友...
└── system（系统标签）：推荐、精选、新人...

-- 用户兴趣标签关联表
t_user_interest_tag
├── 用户-标签关联
├── 兴趣分数（0-5分）
└── 来源追踪（手动/行为/推荐）
```

### API接口结构

```
分类管理 API (/api/v1/categories)
├── POST   /                    创建分类
├── PUT    /{categoryId}        更新分类
├── DELETE /{categoryId}        删除分类
├── GET    /{categoryId}        获取分类详情
├── GET    /                    分页查询分类
├── GET    /tree                获取分类树
├── GET    /hot                 获取热门分类
└── GET    /search              搜索分类

标签管理 API (/api/v1/tags)
├── POST   /                           创建标签
├── PUT    /{tagId}                    更新标签
├── DELETE /{tagId}                    删除标签
├── GET    /{tagId}                    获取标签详情
├── GET    /                           分页查询标签
├── GET    /type/{tagType}             按类型获取标签
├── GET    /hot                        获取热门标签
├── GET    /search                     搜索标签
├── GET    /user/{userId}/interests    获取用户兴趣标签
├── POST   /user/{userId}/interests    设置用户兴趣标签
├── POST   /user/{userId}/interests/{tagId}    添加兴趣标签
├── DELETE /user/{userId}/interests/{tagId}    删除兴趣标签
└── GET    /user/{userId}/recommendations      标签推荐
```

## 🚀 快速开始

### 1. 初始化数据

执行数据库脚本创建初始分类和标签：

```bash
# 执行初始化脚本
mysql -u root -p collide < sql/07-category-tag-system.sql
```

### 2. 分类管理示例

```bash
# 获取分类树
curl -X GET "http://localhost:9501/api/v1/categories/tree"

# 创建新分类
curl -X POST "http://localhost:9501/api/v1/categories" \
  -H "Content-Type: application/json" \
  -d '{
    "parentId": 2,
    "name": "人工智能",
    "description": "AI相关技术内容",
    "sortOrder": 5
  }'

# 获取热门分类
curl -X GET "http://localhost:9501/api/v1/categories/hot?limit=5"
```

### 3. 标签管理示例

```bash
# 获取兴趣标签
curl -X GET "http://localhost:9501/api/v1/tags/type/interest"

# 设置用户兴趣标签
curl -X POST "http://localhost:9501/api/v1/tags/user/123/interests" \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 123,
    "interestTags": [
      {"tagId": 15, "interestScore": 4.5, "source": "manual"},
      {"tagId": 16, "interestScore": 3.8, "source": "manual"}
    ]
  }'

# 获取标签推荐
curl -X GET "http://localhost:9501/api/v1/tags/user/123/recommendations?limit=10"
```

## 📊 数据模型详解

### 分类模型

```json
{
  "id": 1,
  "parentId": 0,
  "name": "娱乐",
  "description": "娱乐内容分类",
  "iconUrl": "https://example.com/icon.png",
  "sortOrder": 1,
  "level": 1,
  "path": "/1",
  "contentCount": 1250,
  "status": "active",
  "children": [...]
}
```

### 标签模型

```json
{
  "id": 15,
  "name": "编程爱好者",
  "description": "对编程技术感兴趣",
  "color": "#1890ff",
  "tagType": "interest",
  "categoryId": 2,
  "usageCount": 1000,
  "heatScore": 85.5,
  "status": "active"
}
```

### 用户兴趣标签模型

```json
{
  "userId": 123,
  "tagId": 15,
  "interestScore": 4.5,
  "source": "manual",
  "status": "active"
}
```

## 🔍 与搜索系统集成

分类和标签系统为搜索提供强大的筛选能力：

### 按分类搜索

```json
{
  "keyword": "编程教程",
  "searchType": "content",
  "contentType": "video",
  "categories": ["编程", "教育"],
  "pageNo": 1,
  "pageSize": 20
}
```

### 按标签搜索

```json
{
  "keyword": "Java",
  "searchType": "content", 
  "tags": ["编程", "Java", "教程"],
  "pageNo": 1,
  "pageSize": 20
}
```

### 个性化推荐

基于用户兴趣标签，系统可以：

1. **内容推荐** - 推荐用户感兴趣的标签相关内容
2. **用户推荐** - 推荐有共同兴趣标签的用户
3. **标签推荐** - 基于行为分析推荐新的兴趣标签

## 🎯 最佳实践

### 分类设计原则

1. **层级合理** - 一般不超过3-4层
2. **互斥完备** - 同级分类互斥，全体分类完备
3. **易于理解** - 分类名称简洁明确

### 标签设计原则

1. **标签类型明确**
   - `content` - 描述内容特征
   - `interest` - 用户兴趣偏好
   - `system` - 系统业务标识

2. **兴趣分数设计**
   - 1.0-2.0：轻微兴趣
   - 2.0-3.5：一般兴趣
   - 3.5-4.5：较高兴趣
   - 4.5-5.0：非常感兴趣

3. **热度计算策略**
   - 使用次数权重：40%
   - 用户关注度：30%
   - 时间衰减因子：20%
   - 质量评分：10%

## 🔧 高级功能

### 智能标签推荐

```java
// 基于用户行为的标签推荐算法
GET /api/v1/tags/user/{userId}/recommendations
```

### 分类内容统计

```java
// 实时更新分类下的内容数量
PUT /api/v1/categories/{categoryId}/content-count
```

### 标签热度更新

```java
// 定期更新标签热度分数
PUT /api/v1/tags/{tagId}/heat-score
```

## 📈 性能优化

### 缓存策略

```yaml
cache:
  categories:
    tree: 30min        # 分类树缓存
    hot: 10min         # 热门分类缓存
  tags:
    hot: 5min          # 热门标签缓存
    user-interests: 60min  # 用户兴趣缓存
```

### 索引优化

```sql
-- 分类表索引
CREATE INDEX idx_parent_sort ON t_category(parent_id, sort_order);
CREATE INDEX idx_path ON t_category(path);

-- 标签表索引  
CREATE INDEX idx_type_heat ON t_tag(tag_type, heat_score);
CREATE INDEX idx_usage_count ON t_tag(usage_count);

-- 用户兴趣表索引
CREATE INDEX idx_user_score ON t_user_interest_tag(user_id, interest_score);
```

## 🚨 注意事项

1. **数据一致性** - 删除分类时要处理子分类和关联内容
2. **兴趣分数范围** - 确保分数在 0-5 范围内
3. **标签重复检查** - 同类型下标签名称不能重复
4. **分类路径维护** - 分类移动时要更新路径信息

---

**通过分类和标签系统，Collide 平台能够提供精准的内容组织和个性化推荐服务！** 🎯 