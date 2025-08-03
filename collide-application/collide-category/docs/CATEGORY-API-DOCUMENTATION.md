# 分类模块 API 接口文档

## 📋 概述

本文档描述了分类模块的客户端API接口，专注于C端（客户端）使用的简单查询功能。

**基础信息：**
- **服务名称**：分类管理服务
- **API版本**：v1.0.0
- **基础路径**：`/api/v1/categories`
- **服务状态**：✅ 生产就绪

## 🔧 接口概览

| 功能分类 | 接口数量 | 说明 |
|---------|----------|------|
| **基础查询** | 4个 | 分类详情、分页查询、搜索 |
| **层级查询** | 4个 | 根分类、子分类、分类树、分类路径 |
| **统计功能** | 3个 | 热门分类、分类建议、数量统计 |

## 📡 接口详情

### 🔹 基础查询功能

#### 1. 获取分类详情

**接口信息：**
- **URL**：`GET /api/v1/categories/{categoryId}`
- **描述**：根据分类ID获取分类详细信息
- **权限**：公开接口

**路径参数：**
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| `categoryId` | Long | 是 | 分类ID |

**响应示例：**
```json
{
  "success": true,
  "code": "200",
  "message": "操作成功",
  "data": {
    "id": 1,
    "name": "小说",
    "description": "文学小说类内容",
    "parentId": 0,
    "iconUrl": "https://example.com/icon/novel.png",
    "sort": 1,
    "contentCount": 1250,
    "status": "active",
    "createTime": "2024-01-01T10:00:00",
    "updateTime": "2024-01-15T14:30:00"
  }
}
```

**错误响应：**
```json
{
  "success": false,
  "code": "CATEGORY_NOT_FOUND",
  "message": "分类不存在"
}
```

---

#### 2. 分页查询分类（POST方式）

**接口信息：**
- **URL**：`POST /api/v1/categories/query`
- **描述**：支持复杂查询条件的分页查询
- **权限**：公开接口

**请求参数：**
```json
{
  "parentId": 1,
  "name": "小说",
  "status": "active",
  "currentPage": 1,
  "pageSize": 20,
  "orderBy": "sort",
  "orderDirection": "ASC"
}
```

**参数说明：**
| 参数名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| `parentId` | Long | 否 | - | 父分类ID |
| `name` | String | 否 | - | 分类名称（模糊匹配） |
| `status` | String | 否 | "active" | 状态：active、inactive |
| `currentPage` | Integer | 否 | 1 | 当前页码 |
| `pageSize` | Integer | 否 | 20 | 页面大小 |
| `orderBy` | String | 否 | "sort" | 排序字段：name、sort、content_count、create_time |
| `orderDirection` | String | 否 | "ASC" | 排序方向：ASC、DESC |

**响应示例：**
```json
{
  "success": true,
  "datas": [
    {
      "id": 1,
      "name": "小说",
      "description": "文学小说类内容",
      "parentId": 0,
      "iconUrl": "https://example.com/icon/novel.png",
      "sort": 1,
      "contentCount": 1250,
      "status": "active",
      "createTime": "2024-01-01T10:00:00",
      "updateTime": "2024-01-15T14:30:00"
    }
  ],
  "total": 1,
  "currentPage": 1,
  "pageSize": 20,
  "totalPage": 1
}
```

---

#### 3. 分页查询分类（GET方式）

**接口信息：**
- **URL**：`GET /api/v1/categories/query`
- **描述**：支持基本查询参数的分页查询
- **权限**：公开接口

**查询参数：**
| 参数名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| `parentId` | Long | 否 | - | 父分类ID |
| `name` | String | 否 | - | 分类名称（模糊匹配） |
| `status` | String | 否 | "active" | 状态 |
| `currentPage` | Integer | 否 | 1 | 当前页码 |
| `pageSize` | Integer | 否 | 20 | 页面大小 |
| `orderBy` | String | 否 | "sort" | 排序字段 |
| `orderDirection` | String | 否 | "ASC" | 排序方向 |

**请求示例：**
```
GET /api/v1/categories/query?parentId=1&status=active&currentPage=1&pageSize=20
```

---

#### 4. 搜索分类

**接口信息：**
- **URL**：`GET /api/v1/categories/search`
- **描述**：根据关键词搜索分类
- **权限**：公开接口

**查询参数：**
| 参数名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| `keyword` | String | 是 | - | 搜索关键词 |
| `parentId` | Long | 否 | - | 父分类ID（限制搜索范围） |
| `currentPage` | Integer | 否 | 1 | 当前页码 |
| `pageSize` | Integer | 否 | 20 | 页面大小 |

**请求示例：**
```
GET /api/v1/categories/search?keyword=小说&parentId=1&currentPage=1&pageSize=10
```

### 🔹 层级查询功能

#### 5. 获取根分类列表

**接口信息：**
- **URL**：`GET /api/v1/categories/root`
- **描述**：获取所有顶级分类（parent_id = 0）
- **权限**：公开接口

**查询参数：**
| 参数名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| `currentPage` | Integer | 否 | 1 | 当前页码 |
| `pageSize` | Integer | 否 | 20 | 页面大小 |
| `orderBy` | String | 否 | "sort" | 排序字段 |
| `orderDirection` | String | 否 | "ASC" | 排序方向 |

**请求示例：**
```
GET /api/v1/categories/root?currentPage=1&pageSize=10&orderBy=sort&orderDirection=ASC
```

---

#### 6. 获取子分类列表

**接口信息：**
- **URL**：`GET /api/v1/categories/{parentId}/children`
- **描述**：获取指定分类的直接子分类
- **权限**：公开接口

**路径参数：**
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| `parentId` | Long | 是 | 父分类ID |

**查询参数：**
| 参数名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| `currentPage` | Integer | 否 | 1 | 当前页码 |
| `pageSize` | Integer | 否 | 20 | 页面大小 |
| `orderBy` | String | 否 | "sort" | 排序字段 |
| `orderDirection` | String | 否 | "ASC" | 排序方向 |

**请求示例：**
```
GET /api/v1/categories/1/children?currentPage=1&pageSize=10
```

---

#### 7. 获取分类树

**接口信息：**
- **URL**：`GET /api/v1/categories/tree`
- **描述**：构建指定分类的树形结构
- **权限**：公开接口

**查询参数：**
| 参数名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| `rootId` | Long | 否 | - | 根分类ID，null表示获取全部分类树 |
| `maxDepth` | Integer | 否 | 5 | 最大层级深度 |

**请求示例：**
```
GET /api/v1/categories/tree?rootId=1&maxDepth=3
```

**响应示例：**
```json
{
  "success": true,
  "code": "200",
  "message": "操作成功",
  "data": [
    {
      "id": 1,
      "name": "小说",
      "description": "文学小说类内容",
      "parentId": 0,
      "iconUrl": "https://example.com/icon/novel.png",
      "sort": 1,
      "contentCount": 1250,
      "status": "active",
      "createTime": "2024-01-01T10:00:00",
      "updateTime": "2024-01-15T14:30:00",
      "children": [
        {
          "id": 2,
          "name": "言情小说",
          "description": "言情类小说",
          "parentId": 1,
          "iconUrl": "https://example.com/icon/romance.png",
          "sort": 1,
          "contentCount": 500,
          "status": "active",
          "createTime": "2024-01-02T10:00:00",
          "updateTime": "2024-01-15T14:30:00",
          "children": []
        }
      ]
    }
  ]
}
```

---

#### 8. 获取分类路径

**接口信息：**
- **URL**：`GET /api/v1/categories/{categoryId}/path`
- **描述**：获取从根分类到指定分类的完整路径
- **权限**：公开接口

**路径参数：**
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| `categoryId` | Long | 是 | 分类ID |

**请求示例：**
```
GET /api/v1/categories/2/path
```

**响应示例：**
```json
{
  "success": true,
  "code": "200",
  "message": "操作成功",
  "data": [
    {
      "id": 1,
      "name": "小说",
      "description": "文学小说类内容",
      "parentId": 0,
      "iconUrl": "https://example.com/icon/novel.png",
      "sort": 1,
      "contentCount": 1250,
      "status": "active",
      "createTime": "2024-01-01T10:00:00",
      "updateTime": "2024-01-15T14:30:00"
    },
    {
      "id": 2,
      "name": "言情小说",
      "description": "言情类小说",
      "parentId": 1,
      "iconUrl": "https://example.com/icon/romance.png",
      "sort": 1,
      "contentCount": 500,
      "status": "active",
      "createTime": "2024-01-02T10:00:00",
      "updateTime": "2024-01-15T14:30:00"
    }
  ]
}
```

### 🔹 统计功能

#### 9. 获取热门分类

**接口信息：**
- **URL**：`GET /api/v1/categories/popular`
- **描述**：根据内容数量排序获取热门分类
- **权限**：公开接口

**查询参数：**
| 参数名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| `parentId` | Long | 否 | - | 父分类ID（限制范围） |
| `currentPage` | Integer | 否 | 1 | 当前页码 |
| `pageSize` | Integer | 否 | 20 | 页面大小 |

**请求示例：**
```
GET /api/v1/categories/popular?parentId=1&currentPage=1&pageSize=10
```

---

#### 10. 获取分类建议

**接口信息：**
- **URL**：`GET /api/v1/categories/suggestions`
- **描述**：用于输入提示功能的分类建议
- **权限**：公开接口

**查询参数：**
| 参数名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| `keyword` | String | 是 | - | 搜索关键词 |
| `limit` | Integer | 否 | 10 | 限制返回数量 |

**请求示例：**
```
GET /api/v1/categories/suggestions?keyword=小说&limit=5
```

**响应示例：**
```json
{
  "success": true,
  "code": "200",
  "message": "操作成功",
  "data": [
    {
      "id": 1,
      "name": "小说",
      "description": "文学小说类内容",
      "parentId": 0,
      "iconUrl": "https://example.com/icon/novel.png",
      "sort": 1,
      "contentCount": 1250,
      "status": "active",
      "createTime": "2024-01-01T10:00:00",
      "updateTime": "2024-01-15T14:30:00"
    },
    {
      "id": 2,
      "name": "言情小说",
      "description": "言情类小说",
      "parentId": 1,
      "iconUrl": "https://example.com/icon/romance.png",
      "sort": 1,
      "contentCount": 500,
      "status": "active",
      "createTime": "2024-01-02T10:00:00",
      "updateTime": "2024-01-15T14:30:00"
    }
  ]
}
```

---

#### 11. 统计分类数量

**接口信息：**
- **URL**：`GET /api/v1/categories/count`
- **描述**：统计符合条件的分类数量
- **权限**：公开接口

**查询参数：**
| 参数名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| `parentId` | Long | 否 | - | 父分类ID |
| `status` | String | 否 | "active" | 状态 |

**请求示例：**
```
GET /api/v1/categories/count?parentId=1&status=active
```

**响应示例：**
```json
{
  "success": true,
  "code": "200",
  "message": "操作成功",
  "data": 5
}
```

## 🚨 错误码说明

| 错误码 | 说明 | HTTP状态码 |
|--------|------|------------|
| `200` | 操作成功 | 200 |
| `INVALID_REQUEST` | 请求参数无效 | 400 |
| `CATEGORY_NOT_FOUND` | 分类不存在 | 404 |
| `CATEGORY_QUERY_ERROR` | 查询分类失败 | 500 |
| `CATEGORY_SEARCH_ERROR` | 搜索分类失败 | 500 |
| `CATEGORY_TREE_ERROR` | 获取分类树失败 | 500 |
| `CATEGORY_PATH_ERROR` | 获取分类路径失败 | 500 |
| `CATEGORY_POPULAR_ERROR` | 获取热门分类失败 | 500 |
| `CATEGORY_SUGGESTIONS_ERROR` | 获取分类建议失败 | 500 |
| `CATEGORY_COUNT_ERROR` | 统计分类数量失败 | 500 |

## 📊 数据模型

### CategoryResponse
```json
{
  "id": "Long",
  "name": "String",
  "description": "String",
  "parentId": "Long",
  "iconUrl": "String",
  "sort": "Integer",
  "contentCount": "Long",
  "status": "String",
  "createTime": "LocalDateTime",
  "updateTime": "LocalDateTime",
  "children": "List<CategoryResponse>"
}
```

### PageResponse
```json
{
  "success": "Boolean",
  "datas": "List<T>",
  "total": "Long",
  "currentPage": "Integer",
  "pageSize": "Integer",
  "totalPage": "Integer"
}
```

## 🔧 使用示例

### JavaScript 示例
```javascript
// 获取分类详情
const getCategory = async (categoryId) => {
  const response = await fetch(`/api/v1/categories/${categoryId}`);
  const result = await response.json();
  return result.data;
};

// 搜索分类
const searchCategories = async (keyword, parentId = null) => {
  const params = new URLSearchParams({
    keyword,
    currentPage: 1,
    pageSize: 20
  });
  if (parentId) params.append('parentId', parentId);
  
  const response = await fetch(`/api/v1/categories/search?${params}`);
  const result = await response.json();
  return result.data;
};

// 获取分类树
const getCategoryTree = async (rootId = null, maxDepth = 5) => {
  const params = new URLSearchParams({
    maxDepth
  });
  if (rootId) params.append('rootId', rootId);
  
  const response = await fetch(`/api/v1/categories/tree?${params}`);
  const result = await response.json();
  return result.data;
};
```

### Java 示例
```java
// 使用 RestTemplate
@Autowired
private RestTemplate restTemplate;

public CategoryResponse getCategory(Long categoryId) {
    String url = "/api/v1/categories/" + categoryId;
    ResponseEntity<Result<CategoryResponse>> response = 
        restTemplate.getForEntity(url, new ParameterizedTypeReference<Result<CategoryResponse>>() {});
    return response.getBody().getData();
}

public PageResponse<CategoryResponse> searchCategories(String keyword, Long parentId) {
    String url = "/api/v1/categories/search";
    UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
        .queryParam("keyword", keyword)
        .queryParam("currentPage", 1)
        .queryParam("pageSize", 20);
    
    if (parentId != null) {
        builder.queryParam("parentId", parentId);
    }
    
    ResponseEntity<PageResponse<CategoryResponse>> response = 
        restTemplate.getForEntity(builder.toUriString(), 
            new ParameterizedTypeReference<PageResponse<CategoryResponse>>() {});
    return response.getBody();
}
```

## 📝 注意事项

1. **状态过滤**：所有接口默认只返回 `active` 状态的分类
2. **分页参数**：`currentPage` 从 1 开始，`pageSize` 建议不超过 100
3. **排序字段**：支持 `name`、`sort`、`content_count`、`create_time`、`update_time`
4. **搜索功能**：支持分类名称的模糊匹配
5. **树形结构**：`children` 字段包含子分类，支持无限层级
6. **路径查询**：返回从根到指定分类的完整路径数组

## 🔄 版本历史

| 版本 | 日期 | 变更说明 |
|------|------|----------|
| v1.0.0 | 2024-01-01 | 初始版本，C端简化接口 | 