# 🔍 Elasticsearch 配置与使用指南

## 📋 概述

本指南介绍如何在 Collide 项目中配置和使用 Elasticsearch 进行高性能搜索。基于项目的 SQL 结构分析，我们为所有核心实体提供了完整的 ES 索引配置。

## 🏗️ 项目架构

### 核心实体索引

| 实体 | 索引名 | 描述 | 主要搜索场景 |
|------|--------|------|-------------|
| 👤 用户 | `collide_users` | 用户基础信息、认证状态 | 用户搜索、推荐 |
| 🎨 艺术家 | `collide_artist` | 博主信息、统计数据 | 艺术家发现、排行 |
| 📝 内容 | `collide_content` | 内容搜索、分类统计 | 内容检索、推荐 |
| 💎 藏品 | `collide_collection` | NFT藏品信息 | 藏品搜索、筛选 |
| 📦 盲盒 | `collide_blind_box` | 盲盒商品信息 | 盲盒搜索、预约 |
| 📋 订单 | `collide_trade_order` | 交易订单数据 | 订单查询、分析 |

## 🚀 快速开始

### 1. 执行自动化脚本

使用我们提供的 PowerShell 脚本一键创建所有索引：

```powershell
# 在项目根目录执行
.\scripts\create-es-indexes.ps1

# 强制重建所有索引
.\scripts\create-es-indexes.ps1 -Force

# 自定义 ES 地址
.\scripts\create-es-indexes.ps1 -EsHost "localhost:9200" -Username "elastic" -Password "yourpassword"
```

### 2. 验证索引状态

```bash
# 查看所有 collide 索引
curl -X GET "http://192.168.1.107:9200/_cat/indices/collide_*?v" \
     -H "Authorization: Basic $(echo -n elastic:123456 | base64)"

# 检查索引映射
curl -X GET "http://192.168.1.107:9200/collide_collection/_mapping" \
     -H "Authorization: Basic $(echo -n elastic:123456 | base64)"
```

## 🔧 配置详解

### 分析器配置

我们为中文内容优化了分析器：

```yaml
# 中文文本分析器 - 用于索引
chinese_text_analyzer:
  tokenizer: "ik_max_word"      # 最细粒度分词
  filter: ["lowercase", "stop"]

# 中文搜索分析器 - 用于查询  
chinese_search_analyzer:
  tokenizer: "ik_smart"         # 智能分词
  filter: ["lowercase", "stop"]
```

### 字段映射策略

- **文本字段**: 支持全文搜索 + 精确匹配
- **关键字字段**: 用于过滤、聚合
- **日期字段**: 支持多种时间格式
- **数值字段**: 支持范围查询、统计

## 📚 使用示例

### 藏品搜索

```java
// 在 CollectionController 中的搜索已经集成了 ES
@GetMapping("/collectionList")
public MultiResult<CollectionVO> collectionList(
    @RequestParam(defaultValue = "SUCCEED") String state,
    @RequestParam(required = false) String keyword,
    @RequestParam(defaultValue = "10") Integer pageSize,
    @RequestParam(defaultValue = "1") Integer currentPage) {
    
    // ES 搜索逻辑已实现
    return collectionService.pageQuery(request);
}
```

### 直接 ES 查询示例

```json
POST /collide_collection/_search
{
  "query": {
    "bool": {
      "must": [
        {
          "multi_match": {
            "query": "数字艺术",
            "fields": ["name^2", "detail"]
          }
        }
      ],
      "filter": [
        {"term": {"state": "SUCCEED"}},
        {"range": {"price": {"gte": 100, "lte": 1000}}}
      ]
    }
  },
  "sort": [
    {"sale_time": {"order": "desc"}}
  ],
  "from": 0,
  "size": 10
}
```

### 聚合统计示例

```json
POST /collide_artist/_search
{
  "size": 0,
  "aggs": {
    "level_stats": {
      "terms": {
        "field": "level",
        "size": 10
      },
      "aggs": {
        "avg_followers": {
          "avg": {
            "field": "followers_count"
          }
        }
      }
    },
    "hot_score_stats": {
      "histogram": {
        "field": "hot_score",
        "interval": 10
      }
    }
  }
}
```

## 🔍 搜索功能

### 1. 全文搜索

支持中文分词的全文搜索：

```bash
# 搜索藏品
GET /collide_collection/_search
{
  "query": {
    "multi_match": {
      "query": "限量版 数字艺术",
      "fields": ["name^2", "detail"],
      "type": "best_fields"
    }
  }
}
```

### 2. 精确筛选

```bash
# 按状态和价格筛选
GET /collide_collection/_search
{
  "query": {
    "bool": {
      "filter": [
        {"term": {"state": "SUCCEED"}},
        {"range": {"price": {"gte": 100}}}
      ]
    }
  }
}
```

### 3. 排序和分页

```bash
# 按价格排序分页
GET /collide_collection/_search
{
  "sort": [
    {"price": {"order": "desc"}},
    {"sale_time": {"order": "desc"}}
  ],
  "from": 0,
  "size": 20
}
```

## 📊 索引别名

我们配置了逻辑别名方便查询：

```bash
# 搜索所有商品（藏品+盲盒）
GET /goods_search/_search

# 用户相关搜索
GET /users_search/_search

# 订单分析
GET /order_analytics/_search
```

## 🔄 数据同步

### 1. 自动同步

建议在业务代码中添加 ES 同步逻辑：

```java
@Service
public class CollectionSyncService {
    
    @EventListener
    public void handleCollectionCreated(CollectionCreatedEvent event) {
        // 同步到 ES
        collectionEsRepository.save(event.getCollection());
    }
    
    @EventListener  
    public void handleCollectionUpdated(CollectionUpdatedEvent event) {
        // 更新 ES 索引
        collectionEsRepository.save(event.getCollection());
    }
}
```

### 2. 批量同步

```java
@Component
public class EsSyncTask {
    
    @Scheduled(cron = "0 0 2 * * ?") // 每天凌晨2点
    public void syncCollectionData() {
        // 从数据库读取数据并批量写入 ES
        List<Collection> collections = collectionService.getAllCollections();
        collectionEsRepository.saveAll(collections);
    }
}
```

## 🛠️ 维护管理

### 索引重建

```bash
# 删除并重建索引
.\scripts\create-es-indexes.ps1 -Force
```

### 健康检查

```bash
# 检查集群健康
curl -X GET "http://192.168.1.107:9200/_cluster/health?pretty"

# 检查索引状态
curl -X GET "http://192.168.1.107:9200/_cat/indices/collide_*?v&s=index"
```

### 性能优化

```bash
# 刷新索引
curl -X POST "http://192.168.1.107:9200/collide_collection/_refresh"

# 强制合并段
curl -X POST "http://192.168.1.107:9200/collide_collection/_forcemerge?max_num_segments=1"
```

## ⚡ 性能建议

1. **批量操作**: 使用 `_bulk` API 进行批量索引
2. **异步同步**: 业务写入和 ES 同步异步进行
3. **定期优化**: 定期执行 `_forcemerge` 优化索引
4. **监控告警**: 监控 ES 集群状态和性能指标

## 🔒 安全配置

```yaml
# 生产环境建议配置
elasticsearch:
  security:
    enabled: true
    username: ${ES_USERNAME:elastic}
    password: ${ES_PASSWORD:your_secure_password}
    ssl:
      enabled: true
      verification_mode: certificate
```

## 📈 监控指标

重要监控指标：

- **索引大小**: 监控各索引的存储空间
- **查询延迟**: P95/P99 查询响应时间
- **索引吞吐量**: 每秒索引文档数
- **集群状态**: Green/Yellow/Red 状态监控

## 🎯 最佳实践

1. **索引设计**: 根据查询模式设计映射
2. **分词优化**: 针对中文内容使用 IK 分词器
3. **缓存策略**: 热点查询结果缓存
4. **数据治理**: 定期清理历史数据
5. **版本管理**: 索引结构变更采用版本化管理

---

🎉 **恭喜！** 你现在拥有了一个完整的企业级 Elasticsearch 搜索系统！

如有问题，请参考日志或联系技术支持。 