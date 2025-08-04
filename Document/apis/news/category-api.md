# Category Controller API Documentation

## 📋 接口概述

**版本**: 5.0.0 (与Content模块一致版)  
**基础路径**: `/api/v1/categories`  
**设计原则**: 参考Content模块设计，直接返回Facade层的Result包装  
**更新时间**: 2024-01-01

## 🎯 接口分类

### 1. 基础查询接口
- [获取分类详情](#获取分类详情)
- [分页查询分类(POST)](#分页查询分类post)
- [分页查询分类(GET)](#分页查询分类get)
- [获取分类列表](#获取分类列表)
- [搜索分类](#搜索分类)

### 2. 层级查询接口
- [获取根分类列表](#获取根分类列表)
- [获取子分类列表](#获取子分类列表)
- [获取分类树](#获取分类树)
- [获取分类路径](#获取分类路径)

### 3. 统计功能接口
- [获取热门分类](#获取热门分类)
- [获取分类建议](#获取分类建议)
- [统计分类数量](#统计分类数量)

---

## 📖 接口详情

### 基础查询接口

#### 获取分类详情

**接口描述**: 根据分类ID获取分类详细信息

```http
GET /api/v1/categories/{categoryId}
```

**路径参数**:
| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| categoryId | Long | ✅ | 分类ID |

**响应示例**:
```json
{
  "code": "SUCCESS",
  "message": "操作成功",
  "data": {
    "id": 1,
    "name": "科技",
    "parentId": null,
    "description": "科技类分类",
    "iconUrl": "https://example.com/tech.png",
    "status": "active",
    "sort": 1,
    "contentCount": 150,
    "createTime": "2024-01-01T00:00:00",
    "updateTime": "2024-01-01T00:00:00"
  },
  "success": true
}
```

---

#### 分页查询分类(POST)

**接口描述**: 使用POST方式进行复杂条件的分页查询

```http
POST /api/v1/categories/query
Content-Type: application/json
```

**请求体**:
```json
{
  "parentId": 1,
  "name": "科技",
  "status": "active",
  "currentPage": 1,
  "pageSize": 20,
  "orderBy": "sort",
  "orderDirection": "ASC"
}
```

**请求参数**:
| 参数名 | 类型 | 必填 | 默认值 | 描述 |
|--------|------|------|--------|------|
| parentId | Long | ❌ | null | 父分类ID |
| name | String | ❌ | null | 分类名称（模糊匹配） |
| status | String | ❌ | "active" | 状态 |
| currentPage | Integer | ❌ | 1 | 当前页码 |
| pageSize | Integer | ❌ | 20 | 页面大小 |
| orderBy | String | ❌ | "sort" | 排序字段 |
| orderDirection | String | ❌ | "ASC" | 排序方向 |

**响应示例**:
```json
{
  "code": "SUCCESS",
  "message": "操作成功",
  "data": {
    "datas": [
      {
        "id": 1,
        "name": "科技",
        "parentId": null,
        "description": "科技类分类",
        "iconUrl": "https://example.com/tech.png",
        "status": "active",
        "sort": 1,
        "contentCount": 150
      }
    ],
    "total": 100,
    "currentPage": 1,
    "pageSize": 20,
    "totalPage": 5,
    "success": true
  },
  "success": true
}
```

---

#### 分页查询分类(GET)

**接口描述**: 使用GET方式进行基本条件的分页查询

```http
GET /api/v1/categories/query?parentId=1&status=active&currentPage=1&pageSize=20
```

**查询参数**:
| 参数名 | 类型 | 必填 | 默认值 | 描述 |
|--------|------|------|--------|------|
| parentId | Long | ❌ | null | 父分类ID |
| name | String | ❌ | null | 分类名称 |
| status | String | ❌ | "active" | 状态 |
| currentPage | Integer | ❌ | 1 | 当前页码 |
| pageSize | Integer | ❌ | 20 | 页面大小 |
| orderBy | String | ❌ | "sort" | 排序字段 |
| orderDirection | String | ❌ | "ASC" | 排序方向 |

**响应格式**: 同POST查询接口

---

#### 获取分类列表

**接口描述**: 获取分类列表（默认接口，支持分页）

```http
GET /api/v1/categories?status=active&currentPage=1&pageSize=20
```

**查询参数**: 同GET查询接口

**响应格式**: 同POST查询接口

---

#### 搜索分类

**接口描述**: 根据关键词搜索分类

```http
GET /api/v1/categories/search?keyword=科技&status=active&currentPage=1&pageSize=20
```

**查询参数**:
| 参数名 | 类型 | 必填 | 默认值 | 描述 |
|--------|------|------|--------|------|
| keyword | String | ✅ | - | 搜索关键词 |
| parentId | Long | ❌ | null | 父分类ID |
| status | String | ❌ | "active" | 状态 |
| currentPage | Integer | ❌ | 1 | 当前页码 |
| pageSize | Integer | ❌ | 20 | 页面大小 |
| orderBy | String | ❌ | "sort" | 排序字段 |
| orderDirection | String | ❌ | "ASC" | 排序方向 |

**响应格式**: 同分页查询接口

---

### 层级查询接口

#### 获取根分类列表

**接口描述**: 获取所有根分类（父分类为null的分类）

```http
GET /api/v1/categories/root?status=active&currentPage=1&pageSize=20
```

**查询参数**:
| 参数名 | 类型 | 必填 | 默认值 | 描述 |
|--------|------|------|--------|------|
| status | String | ❌ | "active" | 状态 |
| currentPage | Integer | ❌ | 1 | 当前页码 |
| pageSize | Integer | ❌ | 20 | 页面大小 |
| orderBy | String | ❌ | "sort" | 排序字段 |
| orderDirection | String | ❌ | "ASC" | 排序方向 |

**响应格式**: 同分页查询接口

---

#### 获取子分类列表

**接口描述**: 获取指定分类的所有子分类

```http
GET /api/v1/categories/{parentId}/children?status=active&currentPage=1&pageSize=20
```

**路径参数**:
| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| parentId | Long | ✅ | 父分类ID |

**查询参数**:
| 参数名 | 类型 | 必填 | 默认值 | 描述 |
|--------|------|------|--------|------|
| status | String | ❌ | "active" | 状态 |
| currentPage | Integer | ❌ | 1 | 当前页码 |
| pageSize | Integer | ❌ | 20 | 页面大小 |
| orderBy | String | ❌ | "sort" | 排序字段 |
| orderDirection | String | ❌ | "ASC" | 排序方向 |

**响应格式**: 同分页查询接口

---

#### 获取分类树

**接口描述**: 获取分类树形结构数据

```http
GET /api/v1/categories/tree?rootId=1&maxDepth=5&status=active
```

**查询参数**:
| 参数名 | 类型 | 必填 | 默认值 | 描述 |
|--------|------|------|--------|------|
| rootId | Long | ❌ | null | 根分类ID，null表示获取全部 |
| maxDepth | Integer | ❌ | 5 | 最大层级深度 |
| status | String | ❌ | "active" | 状态 |
| orderBy | String | ❌ | "sort" | 排序字段 |
| orderDirection | String | ❌ | "ASC" | 排序方向 |

**响应示例**:
```json
{
  "code": "SUCCESS",
  "message": "操作成功",
  "data": [
    {
      "id": 1,
      "name": "科技",
      "parentId": null,
      "description": "科技类分类",
      "status": "active",
      "children": [
        {
          "id": 2,
          "name": "人工智能",
          "parentId": 1,
          "description": "AI相关内容",
          "status": "active",
          "children": []
        }
      ]
    }
  ],
  "success": true
}
```

---

#### 获取分类路径

**接口描述**: 获取从根分类到指定分类的完整路径

```http
GET /api/v1/categories/{categoryId}/path
```

**路径参数**:
| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| categoryId | Long | ✅ | 分类ID |

**响应示例**:
```json
{
  "code": "SUCCESS",
  "message": "操作成功",
  "data": [
    {
      "id": 1,
      "name": "科技",
      "parentId": null
    },
    {
      "id": 2,
      "name": "人工智能",
      "parentId": 1
    },
    {
      "id": 3,
      "name": "机器学习",
      "parentId": 2
    }
  ],
  "success": true
}
```

---

### 统计功能接口

#### 获取热门分类

**接口描述**: 根据内容数量获取热门分类

```http
GET /api/v1/categories/popular?parentId=1&status=active&currentPage=1&pageSize=20
```

**查询参数**:
| 参数名 | 类型 | 必填 | 默认值 | 描述 |
|--------|------|------|--------|------|
| parentId | Long | ❌ | null | 父分类ID |
| status | String | ❌ | "active" | 状态 |
| currentPage | Integer | ❌ | 1 | 当前页码 |
| pageSize | Integer | ❌ | 20 | 页面大小 |

**响应格式**: 同分页查询接口

---

#### 获取分类建议

**接口描述**: 根据关键词获取分类建议列表（用于输入提示）

```http
GET /api/v1/categories/suggestions?keyword=科&limit=10&status=active
```

**查询参数**:
| 参数名 | 类型 | 必填 | 默认值 | 描述 |
|--------|------|------|--------|------|
| keyword | String | ✅ | - | 搜索关键词 |
| limit | Integer | ❌ | 10 | 限制数量 |
| status | String | ❌ | "active" | 状态 |

**响应示例**:
```json
{
  "code": "SUCCESS",
  "message": "操作成功",
  "data": [
    {
      "id": 1,
      "name": "科技",
      "description": "科技类分类"
    },
    {
      "id": 5,
      "name": "科学",
      "description": "科学类分类"
    }
  ],
  "success": true
}
```

---

#### 统计分类数量

**接口描述**: 统计指定条件下的分类数量

```http
GET /api/v1/categories/count?parentId=1&status=active
```

**查询参数**:
| 参数名 | 类型 | 必填 | 默认值 | 描述 |
|--------|------|------|--------|------|
| parentId | Long | ❌ | null | 父分类ID |
| status | String | ❌ | "active" | 状态 |

**响应示例**:
```json
{
  "code": "SUCCESS",
  "message": "操作成功",
  "data": 150,
  "success": true
}
```

---

## 📊 数据模型

### CategoryResponse

```json
{
  "id": "分类ID (Long)",
  "name": "分类名称 (String)",
  "parentId": "父分类ID (Long, 可选)",
  "description": "分类描述 (String, 可选)",
  "iconUrl": "图标URL (String, 可选)",
  "status": "状态 (String, active/inactive)",
  "sort": "排序值 (Integer)",
  "contentCount": "内容数量 (Long)",
  "createTime": "创建时间 (DateTime)",
  "updateTime": "更新时间 (DateTime)",
  "children": "子分类列表 (List<CategoryResponse>, 仅树形结构时包含)"
}
```

### CategoryQueryRequest

```json
{
  "parentId": "父分类ID (Long, 可选)",
  "name": "分类名称 (String, 可选)",
  "status": "状态 (String, 可选)",
  "currentPage": "当前页码 (Integer, 默认1)",
  "pageSize": "页面大小 (Integer, 默认20)",
  "orderBy": "排序字段 (String, 默认sort)",
  "orderDirection": "排序方向 (String, 默认ASC)"
}
```

### PageResponse

```json
{
  "datas": "数据列表 (List<T>)",
  "total": "总记录数 (Long)",
  "currentPage": "当前页码 (Integer)",
  "pageSize": "页面大小 (Integer)",
  "totalPage": "总页数 (Integer)",
  "success": "是否成功 (Boolean)"
}
```

### Result

```json
{
  "code": "响应码 (String)",
  "message": "响应消息 (String)",
  "data": "响应数据 (T)",
  "success": "是否成功 (Boolean)"
}
```

---

## 🔧 状态码说明

| 状态码 | 描述 |
|--------|------|
| SUCCESS | 操作成功 |
| CATEGORY_NOT_FOUND | 分类不存在 |
| CATEGORY_GET_ERROR | 获取分类详情失败 |
| CATEGORY_QUERY_ERROR | 查询分类列表失败 |
| CATEGORY_SEARCH_ERROR | 搜索分类失败 |
| ROOT_CATEGORIES_ERROR | 获取根分类列表失败 |
| CHILD_CATEGORIES_ERROR | 获取子分类列表失败 |
| CATEGORY_TREE_ERROR | 获取分类树失败 |
| CATEGORY_PATH_ERROR | 获取分类路径失败 |
| POPULAR_CATEGORIES_ERROR | 获取热门分类失败 |
| CATEGORY_SUGGESTIONS_ERROR | 获取分类建议失败 |
| CATEGORY_COUNT_ERROR | 统计分类数量失败 |

---

## 📝 使用示例

### JavaScript/Ajax 示例

```javascript
// 获取分类详情
$.get('/api/v1/categories/1', function(result) {
    if (result.success) {
        console.log('分类详情:', result.data);
    }
});

// 分页查询分类
$.ajax({
    url: '/api/v1/categories/query',
    method: 'POST',
    contentType: 'application/json',
    data: JSON.stringify({
        parentId: 1,
        status: 'active',
        currentPage: 1,
        pageSize: 20
    }),
    success: function(result) {
        if (result.success) {
            console.log('分类列表:', result.data.datas);
            console.log('总数:', result.data.total);
        }
    }
});

// 搜索分类
$.get('/api/v1/categories/search', {
    keyword: '科技',
    currentPage: 1,
    pageSize: 10
}, function(result) {
    if (result.success) {
        console.log('搜索结果:', result.data.datas);
    }
});
```

### cURL 示例

```bash
# 获取分类详情
curl -X GET "http://localhost:8080/api/v1/categories/1"

# 分页查询分类
curl -X POST "http://localhost:8080/api/v1/categories/query" \
  -H "Content-Type: application/json" \
  -d '{"parentId": 1, "status": "active", "currentPage": 1, "pageSize": 20}'

# 搜索分类
curl -X GET "http://localhost:8080/api/v1/categories/search?keyword=科技&currentPage=1&pageSize=10"

# 获取分类树
curl -X GET "http://localhost:8080/api/v1/categories/tree?maxDepth=3&status=active"
```

---

## 📋 注意事项

1. **分页参数**: 所有分页接口的页码从1开始
2. **状态值**: status字段支持 "active"（启用）和 "inactive"（禁用）
3. **排序字段**: orderBy支持所有CategoryResponse字段名
4. **排序方向**: orderDirection支持 "ASC"（升序）和 "DESC"（降序）
5. **树形结构**: 获取分类树时，children字段会包含子分类信息
6. **性能考虑**: 建议合理设置maxDepth避免过深的树形查询
7. **错误处理**: 所有接口都返回统一的Result格式，请检查success字段
8. **搜索功能**: 支持按分类名称模糊搜索
9. **缓存**: 接口实现了适当的缓存机制以提升性能

---

**文档版本**: 5.0.0  
**最后更新**: 2024-01-01  
**维护人员**: Collide Team