# 分类模块 Facade 接口文档

## 📋 概述

本文档描述了分类模块的内部服务调用接口（Facade），供其他微服务进行内部调用。

**基础信息：**
- **服务名称**：分类管理服务
- **接口版本**：v1.0.0
- **服务类型**：Dubbo RPC 服务
- **服务状态**：✅ 生产就绪

## 🔧 接口概览

| 功能分类 | 接口数量 | 说明 |
|---------|----------|------|
| **基础查询** | 3个 | 分类详情、分页查询、搜索 |
| **层级查询** | 4个 | 根分类、子分类、分类树、分类路径 |
| **统计功能** | 3个 | 热门分类、分类建议、数量统计 |

## 📡 接口详情

### 🔹 基础查询功能

#### 1. 获取分类详情

**接口信息：**
- **方法名**：`getCategoryById`
- **描述**：根据分类ID获取分类详细信息
- **服务版本**：1.0.0

**参数说明：**
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| `categoryId` | Long | 是 | 分类ID |

**返回值：**
```java
Result<CategoryResponse>
```

**调用示例：**
```java
@Reference(version = "1.0.0")
private CategoryFacadeService categoryFacadeService;

public CategoryResponse getCategory(Long categoryId) {
    Result<CategoryResponse> result = categoryFacadeService.getCategoryById(categoryId);
    if (result.isSuccess()) {
        return result.getData();
    }
    throw new RuntimeException("获取分类失败：" + result.getMessage());
}
```

---

#### 2. 分页查询分类

**接口信息：**
- **方法名**：`queryCategories`
- **描述**：支持复杂查询条件的分页查询
- **服务版本**：1.0.0

**参数说明：**
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| `request` | CategoryQueryRequest | 是 | 查询请求对象 |

**CategoryQueryRequest 结构：**
```java
public class CategoryQueryRequest {
    private Long parentId;           // 父分类ID
    private String name;             // 分类名称（模糊匹配）
    private String status;           // 状态：active、inactive
    private Integer currentPage;     // 当前页码
    private Integer pageSize;        // 页面大小
    private String orderBy;          // 排序字段
    private String orderDirection;   // 排序方向
}
```

**返回值：**
```java
Result<PageResponse<CategoryResponse>>
```

**调用示例：**
```java
public PageResponse<CategoryResponse> queryCategories(Long parentId, String status) {
    CategoryQueryRequest request = new CategoryQueryRequest();
    request.setParentId(parentId);
    request.setStatus(status);
    request.setCurrentPage(1);
    request.setPageSize(20);
    request.setOrderBy("sort");
    request.setOrderDirection("ASC");
    
    Result<PageResponse<CategoryResponse>> result = categoryFacadeService.queryCategories(request);
    if (result.isSuccess()) {
        return result.getData();
    }
    throw new RuntimeException("查询分类失败：" + result.getMessage());
}
```

---

#### 3. 搜索分类

**接口信息：**
- **方法名**：`searchCategories`
- **描述**：根据关键词搜索分类
- **服务版本**：1.0.0

**参数说明：**
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| `keyword` | String | 是 | 搜索关键词 |
| `parentId` | Long | 否 | 父分类ID（限制搜索范围） |
| `currentPage` | Integer | 否 | 当前页码 |
| `pageSize` | Integer | 否 | 页面大小 |

**返回值：**
```java
Result<PageResponse<CategoryResponse>>
```

**调用示例：**
```java
public PageResponse<CategoryResponse> searchCategories(String keyword, Long parentId) {
    Result<PageResponse<CategoryResponse>> result = categoryFacadeService.searchCategories(
        keyword, parentId, 1, 20);
    
    if (result.isSuccess()) {
        return result.getData();
    }
    throw new RuntimeException("搜索分类失败：" + result.getMessage());
}
```

### 🔹 层级查询功能

#### 4. 获取根分类列表

**接口信息：**
- **方法名**：`getRootCategories`
- **描述**：获取所有顶级分类（parent_id = 0）
- **服务版本**：1.0.0

**参数说明：**
| 参数名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| `currentPage` | Integer | 否 | 1 | 当前页码 |
| `pageSize` | Integer | 否 | 20 | 页面大小 |
| `orderBy` | String | 否 | "sort" | 排序字段 |
| `orderDirection` | String | 否 | "ASC" | 排序方向 |

**返回值：**
```java
Result<PageResponse<CategoryResponse>>
```

**调用示例：**
```java
public PageResponse<CategoryResponse> getRootCategories() {
    Result<PageResponse<CategoryResponse>> result = categoryFacadeService.getRootCategories(
        1, 20, "sort", "ASC");
    
    if (result.isSuccess()) {
        return result.getData();
    }
    throw new RuntimeException("获取根分类失败：" + result.getMessage());
}
```

---

#### 5. 获取子分类列表

**接口信息：**
- **方法名**：`getChildCategories`
- **描述**：获取指定分类的直接子分类
- **服务版本**：1.0.0

**参数说明：**
| 参数名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| `parentId` | Long | 是 | - | 父分类ID |
| `currentPage` | Integer | 否 | 1 | 当前页码 |
| `pageSize` | Integer | 否 | 20 | 页面大小 |
| `orderBy` | String | 否 | "sort" | 排序字段 |
| `orderDirection` | String | 否 | "ASC" | 排序方向 |

**返回值：**
```java
Result<PageResponse<CategoryResponse>>
```

**调用示例：**
```java
public PageResponse<CategoryResponse> getChildCategories(Long parentId) {
    Result<PageResponse<CategoryResponse>> result = categoryFacadeService.getChildCategories(
        parentId, 1, 20, "sort", "ASC");
    
    if (result.isSuccess()) {
        return result.getData();
    }
    throw new RuntimeException("获取子分类失败：" + result.getMessage());
}
```

---

#### 6. 获取分类树

**接口信息：**
- **方法名**：`getCategoryTree`
- **描述**：构建指定分类的树形结构
- **服务版本**：1.0.0

**参数说明：**
| 参数名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| `rootId` | Long | 否 | - | 根分类ID，null表示获取全部分类树 |
| `maxDepth` | Integer | 否 | 5 | 最大层级深度 |

**返回值：**
```java
Result<List<CategoryResponse>>
```

**调用示例：**
```java
public List<CategoryResponse> getCategoryTree(Long rootId, Integer maxDepth) {
    Result<List<CategoryResponse>> result = categoryFacadeService.getCategoryTree(rootId, maxDepth);
    
    if (result.isSuccess()) {
        return result.getData();
    }
    throw new RuntimeException("获取分类树失败：" + result.getMessage());
}
```

---

#### 7. 获取分类路径

**接口信息：**
- **方法名**：`getCategoryPath`
- **描述**：获取从根分类到指定分类的完整路径
- **服务版本**：1.0.0

**参数说明：**
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| `categoryId` | Long | 是 | 分类ID |

**返回值：**
```java
Result<List<CategoryResponse>>
```

**调用示例：**
```java
public List<CategoryResponse> getCategoryPath(Long categoryId) {
    Result<List<CategoryResponse>> result = categoryFacadeService.getCategoryPath(categoryId);
    
    if (result.isSuccess()) {
        return result.getData();
    }
    throw new RuntimeException("获取分类路径失败：" + result.getMessage());
}
```

### 🔹 统计功能

#### 8. 获取热门分类

**接口信息：**
- **方法名**：`getPopularCategories`
- **描述**：根据内容数量排序获取热门分类
- **服务版本**：1.0.0

**参数说明：**
| 参数名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| `parentId` | Long | 否 | - | 父分类ID（限制范围） |
| `currentPage` | Integer | 否 | 1 | 当前页码 |
| `pageSize` | Integer | 否 | 20 | 页面大小 |

**返回值：**
```java
Result<PageResponse<CategoryResponse>>
```

**调用示例：**
```java
public PageResponse<CategoryResponse> getPopularCategories(Long parentId) {
    Result<PageResponse<CategoryResponse>> result = categoryFacadeService.getPopularCategories(
        parentId, 1, 10);
    
    if (result.isSuccess()) {
        return result.getData();
    }
    throw new RuntimeException("获取热门分类失败：" + result.getMessage());
}
```

---

#### 9. 获取分类建议

**接口信息：**
- **方法名**：`getCategorySuggestions`
- **描述**：用于输入提示功能的分类建议
- **服务版本**：1.0.0

**参数说明：**
| 参数名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| `keyword` | String | 是 | - | 搜索关键词 |
| `limit` | Integer | 否 | 10 | 限制返回数量 |

**返回值：**
```java
Result<List<CategoryResponse>>
```

**调用示例：**
```java
public List<CategoryResponse> getCategorySuggestions(String keyword, Integer limit) {
    Result<List<CategoryResponse>> result = categoryFacadeService.getCategorySuggestions(keyword, limit);
    
    if (result.isSuccess()) {
        return result.getData();
    }
    throw new RuntimeException("获取分类建议失败：" + result.getMessage());
}
```

---

#### 10. 统计分类数量

**接口信息：**
- **方法名**：`countCategories`
- **描述**：统计符合条件的分类数量
- **服务版本**：1.0.0

**参数说明：**
| 参数名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| `parentId` | Long | 否 | - | 父分类ID |
| `status` | String | 否 | "active" | 状态 |

**返回值：**
```java
Result<Long>
```

**调用示例：**
```java
public Long countCategories(Long parentId, String status) {
    Result<Long> result = categoryFacadeService.countCategories(parentId, status);
    
    if (result.isSuccess()) {
        return result.getData();
    }
    throw new RuntimeException("统计分类数量失败：" + result.getMessage());
}
```

## 📊 数据模型

### CategoryResponse
```java
public class CategoryResponse {
    private Long id;                    // 分类ID
    private String name;                // 分类名称
    private String description;         // 分类描述
    private Long parentId;             // 父分类ID
    private String iconUrl;            // 分类图标URL
    private Integer sort;              // 排序值
    private Long contentCount;         // 内容数量
    private String status;             // 状态
    private LocalDateTime createTime;  // 创建时间
    private LocalDateTime updateTime;  // 更新时间
    private List<CategoryResponse> children; // 子分类列表
}
```

### PageResponse
```java
public class PageResponse<T> {
    private Boolean success;           // 是否成功
    private List<T> datas;            // 数据列表
    private Long total;               // 总记录数
    private Integer currentPage;      // 当前页码
    private Integer pageSize;         // 页面大小
    private Integer totalPage;        // 总页数
}
```

### Result
```java
public class Result<T> {
    private Boolean success;           // 是否成功
    private String code;              // 错误码
    private String message;           // 错误信息
    private T data;                  // 数据
}
```

## 🚨 错误码说明

| 错误码 | 说明 | 处理建议 |
|--------|------|----------|
| `200` | 操作成功 | 正常处理 |
| `INVALID_REQUEST` | 请求参数无效 | 检查参数格式 |
| `CATEGORY_NOT_FOUND` | 分类不存在 | 检查分类ID是否正确 |
| `CATEGORY_QUERY_ERROR` | 查询分类失败 | 检查查询条件 |
| `CATEGORY_SEARCH_ERROR` | 搜索分类失败 | 检查搜索关键词 |
| `CATEGORY_TREE_ERROR` | 获取分类树失败 | 检查树形结构参数 |
| `CATEGORY_PATH_ERROR` | 获取分类路径失败 | 检查分类ID |
| `CATEGORY_POPULAR_ERROR` | 获取热门分类失败 | 检查查询条件 |
| `CATEGORY_SUGGESTIONS_ERROR` | 获取分类建议失败 | 检查关键词参数 |
| `CATEGORY_COUNT_ERROR` | 统计分类数量失败 | 检查统计条件 |

## 🔧 服务配置

### Dubbo 服务配置
```yaml
# 服务提供方配置
dubbo:
  application:
    name: collide-category-service
  protocol:
    name: dubbo
    port: 20880
  registry:
    address: zookeeper://localhost:2181
  provider:
    version: 1.0.0
    timeout: 5000
    retries: 2
```

### 服务消费方配置
```yaml
# 服务消费方配置
dubbo:
  application:
    name: your-service-name
  registry:
    address: zookeeper://localhost:2181
  consumer:
    timeout: 5000
    retries: 2
    check: false
```

## 🔧 使用示例

### 完整的服务调用示例
```java
@Service
public class ContentService {
    
    @Reference(version = "1.0.0", check = false)
    private CategoryFacadeService categoryFacadeService;
    
    /**
     * 根据分类ID获取分类信息
     */
    public CategoryResponse getCategoryInfo(Long categoryId) {
        try {
            Result<CategoryResponse> result = categoryFacadeService.getCategoryById(categoryId);
            if (result.isSuccess()) {
                return result.getData();
            } else {
                log.error("获取分类失败：{}", result.getMessage());
                return null;
            }
        } catch (Exception e) {
            log.error("调用分类服务失败", e);
            return null;
        }
    }
    
    /**
     * 获取分类树用于内容分类
     */
    public List<CategoryResponse> getCategoryTreeForContent() {
        try {
            Result<List<CategoryResponse>> result = categoryFacadeService.getCategoryTree(null, 3);
            if (result.isSuccess()) {
                return result.getData();
            } else {
                log.error("获取分类树失败：{}", result.getMessage());
                return new ArrayList<>();
            }
        } catch (Exception e) {
            log.error("调用分类服务失败", e);
            return new ArrayList<>();
        }
    }
    
    /**
     * 搜索分类用于内容关联
     */
    public PageResponse<CategoryResponse> searchCategoriesForContent(String keyword) {
        try {
            Result<PageResponse<CategoryResponse>> result = categoryFacadeService.searchCategories(
                keyword, null, 1, 10);
            if (result.isSuccess()) {
                return result.getData();
            } else {
                log.error("搜索分类失败：{}", result.getMessage());
                return new PageResponse<>();
            }
        } catch (Exception e) {
            log.error("调用分类服务失败", e);
            return new PageResponse<>();
        }
    }
}
```

### 异常处理示例
```java
public class CategoryServiceWrapper {
    
    @Reference(version = "1.0.0")
    private CategoryFacadeService categoryFacadeService;
    
    /**
     * 安全的分类查询方法
     */
    public CategoryResponse getCategorySafely(Long categoryId) {
        try {
            Result<CategoryResponse> result = categoryFacadeService.getCategoryById(categoryId);
            
            if (!result.isSuccess()) {
                log.warn("获取分类失败，categoryId: {}, error: {}", categoryId, result.getMessage());
                return null;
            }
            
            return result.getData();
            
        } catch (Exception e) {
            log.error("调用分类服务异常，categoryId: {}", categoryId, e);
            return null;
        }
    }
    
    /**
     * 批量获取分类信息
     */
    public Map<Long, CategoryResponse> batchGetCategories(List<Long> categoryIds) {
        Map<Long, CategoryResponse> result = new HashMap<>();
        
        for (Long categoryId : categoryIds) {
            CategoryResponse category = getCategorySafely(categoryId);
            if (category != null) {
                result.put(categoryId, category);
            }
        }
        
        return result;
    }
}
```

## 📝 注意事项

1. **服务版本**：确保使用正确的服务版本号（1.0.0）
2. **异常处理**：所有服务调用都应该包含异常处理
3. **超时设置**：根据业务需求设置合适的超时时间
4. **重试机制**：对于重要操作，建议实现重试机制
5. **日志记录**：记录服务调用的关键信息，便于问题排查
6. **性能考虑**：避免频繁调用，考虑使用缓存
7. **数据一致性**：注意处理服务调用失败时的数据一致性

## 🔄 版本历史

| 版本 | 日期 | 变更说明 |
|------|------|----------|
| v1.0.0 | 2024-01-01 | 初始版本，C端简化接口 |

## 📞 技术支持

如有问题，请联系：
- **技术负责人**：Collide Team
- **邮箱**：tech@collide.com
- **文档更新**：2024-01-01 