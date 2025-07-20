# Pro权限使用示例

## 概述

本文档展示如何在业务代码中使用Pro权限配置系统，包括用户升级后权限自动开通、权限验证、权限使用统计等功能。

## 核心使用场景

### 1. 用户升级时自动权限开通

```java
// 用户升级示例
public class ProUpgradeExample {
    
    @Autowired
    private ProFacadeService proFacadeService;
    
    @Autowired 
    private ProPermissionConfigService permissionConfigService;
    
    /**
     * 用户支付成功后调用升级逻辑
     */
    public void handlePaymentSuccess(Long userId, ProPackageType packageType, String orderNo) {
        
        // 1. 创建升级请求
        ProUpgradeRequest request = new ProUpgradeRequest();
        request.setUserId(userId);
        request.setPackageType(packageType.name());
        request.setOrderNo(orderNo);
        request.setAutoGrantPermissions(true); // 自动开通默认权限
        
        // 2. 执行升级操作
        ProOperatorResponse response = proFacadeService.upgradeToPro(request);
        
        if (response.getSuccess()) {
            log.info("用户{}升级成功，套餐类型：{}", userId, packageType);
            
            // 3. 查询开通的权限
            List<ProPermissionType> grantedPermissions = 
                permissionConfigService.autoGrantPermissionsOnUpgrade(
                    userId, packageType, "v1.0");
            
            log.info("为用户{}自动开通权限：{}", userId, grantedPermissions);
            
            // 4. 发送权限开通通知（可选）
            sendPermissionGrantNotification(userId, grantedPermissions);
        }
    }
}
```

### 2. 权限验证注解使用

```java
// 控制器中使用权限验证
@RestController
@RequestMapping("/api/data")
public class DataController {
    
    @Autowired
    private ProPermissionConfigService permissionConfigService;
    
    /**
     * 数据导出接口 - 需要数据导出权限
     */
    @PostMapping("/export")
    public Result<?> exportData(@RequestBody DataExportRequest request) {
        Long userId = getCurrentUserId();
        
        // 手动权限验证
        if (!permissionConfigService.hasPermission(userId, ProPermissionType.DATA_EXPORT)) {
            return Result.error(AuthErrorCode.PERMISSION_DENIED, "您没有数据导出权限，请升级Pro套餐");
        }
        
        // 验证权限使用次数
        if (!permissionConfigService.validatePermissionUsage(userId, ProPermissionType.DATA_EXPORT)) {
            return Result.error(AuthErrorCode.PERMISSION_USAGE_EXCEEDED, "数据导出次数已用完，请升级套餐或等待下个周期");
        }
        
        try {
            // 执行导出逻辑
            String downloadUrl = dataService.exportData(request);
            
            // 记录权限使用
            permissionConfigService.recordPermissionUsage(userId, ProPermissionType.DATA_EXPORT);
            
            return Result.success(downloadUrl);
        } catch (Exception e) {
            log.error("数据导出失败", e);
            return Result.error("导出失败");
        }
    }
    
    /**
     * 高级搜索接口 - 需要高级搜索权限
     */
    @PostMapping("/advanced-search")
    public Result<?> advancedSearch(@RequestBody AdvancedSearchRequest request) {
        Long userId = getCurrentUserId();
        
        // 权限验证
        if (!permissionConfigService.hasPermission(userId, ProPermissionType.ADVANCED_SEARCH)) {
            return Result.error(AuthErrorCode.PERMISSION_DENIED, "请升级Pro套餐解锁高级搜索功能");
        }
        
        if (!permissionConfigService.validatePermissionUsage(userId, ProPermissionType.ADVANCED_SEARCH)) {
            return Result.error(AuthErrorCode.PERMISSION_USAGE_EXCEEDED, "高级搜索次数已用完");
        }
        
        // 执行搜索
        SearchResult result = searchService.advancedSearch(request);
        
        // 记录使用
        permissionConfigService.recordPermissionUsage(userId, ProPermissionType.ADVANCED_SEARCH);
        
        return Result.success(result);
    }
}
```

### 3. 批量操作权限验证

```java
// 批量操作示例
@Service
public class BatchOperationService {
    
    @Autowired
    private ProPermissionConfigService permissionConfigService;
    
    /**
     * 批量删除数据
     */
    public Result<?> batchDelete(Long userId, List<Long> dataIds) {
        
        // 权限验证
        if (!permissionConfigService.hasPermission(userId, ProPermissionType.BATCH_OPERATION)) {
            return Result.error("批量操作功能仅限Pro用户使用");
        }
        
        // 验证批量操作次数
        if (!permissionConfigService.validatePermissionUsage(userId, ProPermissionType.BATCH_OPERATION)) {
            return Result.error("批量操作次数已用完，请升级套餐");
        }
        
        // 执行批量删除
        int deletedCount = dataService.batchDelete(dataIds);
        
        // 记录使用
        permissionConfigService.recordPermissionUsage(userId, ProPermissionType.BATCH_OPERATION);
        
        return Result.success("成功删除" + deletedCount + "条数据");
    }
}
```

### 4. 权限状态查询

```java
// 用户权限查询示例
@RestController
@RequestMapping("/api/user")
public class UserProController {
    
    @Autowired
    private ProFacadeService proFacadeService;
    
    /**
     * 查询用户Pro状态和权限
     */
    @GetMapping("/pro-status")
    public Result<?> getProStatus() {
        Long userId = getCurrentUserId();
        
        // 查询Pro状态
        ProStatusQueryRequest statusRequest = new ProStatusQueryRequest(userId);
        ProQueryResponse<ProInfo> statusResponse = proFacadeService.queryProStatus(statusRequest);
        
        // 查询权限详情
        ProPermissionQueryRequest permissionRequest = new ProPermissionQueryRequest(userId);
        ProQueryResponse<ProPermissionInfo> permissionResponse = 
            proFacadeService.queryProPermissions(permissionRequest);
        
        // 组装返回数据
        Map<String, Object> result = new HashMap<>();
        result.put("proInfo", statusResponse.getData());
        result.put("permissions", permissionResponse.getData());
        
        return Result.success(result);
    }
    
    /**
     * 获取用户可升级的套餐选项
     */
    @GetMapping("/upgrade-options")
    public Result<?> getUpgradeOptions() {
        Long userId = getCurrentUserId();
        
        // 获取当前用户Pro状态
        ProInfo currentProInfo = getCurrentProInfo(userId);
        
        // 根据当前状态返回可升级选项
        List<UpgradeOption> options = new ArrayList<>();
        
        if (currentProInfo.getPackageType() == null || 
            currentProInfo.getPackageType() == ProPackageType.MONTHLY) {
            
            options.add(new UpgradeOption(ProPackageType.QUARTERLY, "季费套餐", "更多权限"));
            options.add(new UpgradeOption(ProPackageType.YEARLY, "年费套餐", "全功能权限"));
            options.add(new UpgradeOption(ProPackageType.LIFETIME, "终身套餐", "永久全功能"));
        }
        
        return Result.success(options);
    }
}
```

### 5. 管理员权限操作

```java
// 管理员权限管理示例
@RestController
@RequestMapping("/api/admin/pro")
@RequirePermission("ADMIN")
public class AdminProController {
    
    @Autowired
    private ProFacadeService proFacadeService;
    
    /**
     * 为用户手动开通权限
     */
    @PostMapping("/grant-permission")
    public Result<?> grantPermission(@RequestBody GrantPermissionRequest request) {
        Long adminId = getCurrentUserId();
        
        ProPermissionActivateRequest activateRequest = new ProPermissionActivateRequest();
        activateRequest.setUserId(request.getUserId());
        activateRequest.setPermissionTypes(request.getPermissionTypes());
        activateRequest.setOperatorId(adminId);
        activateRequest.setReason(request.getReason());
        activateRequest.setAutoActivate(false); // 管理员手动开通
        
        ProOperatorResponse response = proFacadeService.activateProPermission(activateRequest);
        
        if (response.getSuccess()) {
            return Result.success("权限开通成功");
        } else {
            return Result.error("权限开通失败");
        }
    }
    
    /**
     * 回收用户权限
     */
    @PostMapping("/revoke-permission") 
    public Result<?> revokePermission(@RequestBody RevokePermissionRequest request) {
        Long adminId = getCurrentUserId();
        
        ProPermissionDeactivateRequest deactivateRequest = new ProPermissionDeactivateRequest();
        deactivateRequest.setUserId(request.getUserId());
        deactivateRequest.setPermissionTypes(request.getPermissionTypes());
        deactivateRequest.setOperatorId(adminId);
        deactivateRequest.setReason(request.getReason());
        
        ProOperatorResponse response = proFacadeService.deactivateProPermission(deactivateRequest);
        
        return response.getSuccess() ? Result.success("权限回收成功") : Result.error("权限回收失败");
    }
}
```

## 权限配置管理

### 1. 动态权限配置查询

```java
// 权限配置查询示例
@Service
public class ProConfigService {
    
    @Autowired
    private ProPermissionConfigService configService;
    
    /**
     * 获取套餐权限配置信息
     */
    public PackagePermissionInfo getPackagePermissionInfo(ProPackageType packageType) {
        String currentVersion = configService.getCurrentConfigVersion();
        
        // 获取默认权限
        List<ProPermissionType> defaultPermissions = 
            configService.getDefaultPermissions(packageType, currentVersion);
        
        // 获取所有可用权限
        List<ProPermissionType> availablePermissions = 
            configService.getAvailablePermissions(packageType, currentVersion);
        
        // 获取权限详细配置
        List<PermissionDetail> permissionDetails = new ArrayList<>();
        for (ProPermissionType permissionType : availablePermissions) {
            ProPermissionConfig.PermissionConfigItem config = 
                configService.getPermissionConfig(packageType, permissionType, currentVersion);
            
            PermissionDetail detail = new PermissionDetail();
            detail.setPermissionType(permissionType);
            detail.setDefaultEnabled(config.getDefaultEnabled());
            detail.setUsageLimit(config.getUsageLimit());
            detail.setValidDays(config.getValidDays());
            
            permissionDetails.add(detail);
        }
        
        PackagePermissionInfo info = new PackagePermissionInfo();
        info.setPackageType(packageType);
        info.setDefaultPermissions(defaultPermissions);
        info.setAvailablePermissions(availablePermissions);
        info.setPermissionDetails(permissionDetails);
        
        return info;
    }
}
```

### 2. 权限使用统计

```java
// 权限使用统计示例
@Service
public class ProUsageStatisticsService {
    
    /**
     * 生成用户权限使用报告
     */
    public UserPermissionUsageReport generateUsageReport(Long userId, Date startDate, Date endDate) {
        
        // 查询用户权限使用记录
        List<PermissionUsageRecord> usageRecords = getPermissionUsageRecords(userId, startDate, endDate);
        
        // 统计各权限使用次数
        Map<ProPermissionType, Integer> usageCount = new HashMap<>();
        Map<ProPermissionType, Date> lastUsageTime = new HashMap<>();
        
        for (PermissionUsageRecord record : usageRecords) {
            ProPermissionType permissionType = record.getPermissionType();
            usageCount.put(permissionType, usageCount.getOrDefault(permissionType, 0) + 1);
            
            Date currentLastTime = lastUsageTime.get(permissionType);
            if (currentLastTime == null || record.getUsageTime().after(currentLastTime)) {
                lastUsageTime.put(permissionType, record.getUsageTime());
            }
        }
        
        // 构建报告
        UserPermissionUsageReport report = new UserPermissionUsageReport();
        report.setUserId(userId);
        report.setStartDate(startDate);
        report.setEndDate(endDate);
        report.setUsageCount(usageCount);
        report.setLastUsageTime(lastUsageTime);
        report.setTotalUsage(usageRecords.size());
        
        return report;
    }
}
```

## 错误处理

### 1. 权限相关异常定义

```java
// 权限异常枚举
public enum ProPermissionErrorCode {
    
    PERMISSION_DENIED(40001, "权限不足"),
    PERMISSION_EXPIRED(40002, "权限已过期"),
    PERMISSION_USAGE_EXCEEDED(40003, "权限使用次数已用完"),
    PERMISSION_NOT_FOUND(40004, "权限不存在"),
    PERMISSION_CONFIG_ERROR(50001, "权限配置错误"),
    PERMISSION_GRANT_FAILED(50002, "权限开通失败");
    
    private final int code;
    private final String message;
}

// 权限异常处理
@ControllerAdvice
public class ProPermissionExceptionHandler {
    
    @ExceptionHandler(ProPermissionException.class)
    public Result<?> handleProPermissionException(ProPermissionException e) {
        log.warn("权限异常：{}", e.getMessage());
        return Result.error(e.getErrorCode(), e.getMessage());
    }
}
```

### 2. 权限验证工具类

```java
// 权限验证工具
@Component
public class ProPermissionValidator {
    
    @Autowired
    private ProPermissionConfigService configService;
    
    /**
     * 验证并记录权限使用
     */
    public void validateAndRecordPermission(Long userId, ProPermissionType permissionType) {
        // 检查权限
        if (!configService.hasPermission(userId, permissionType)) {
            throw new ProPermissionException(ProPermissionErrorCode.PERMISSION_DENIED);
        }
        
        // 验证使用次数
        if (!configService.validatePermissionUsage(userId, permissionType)) {
            throw new ProPermissionException(ProPermissionErrorCode.PERMISSION_USAGE_EXCEEDED);
        }
        
        // 记录使用
        configService.recordPermissionUsage(userId, permissionType);
    }
    
    /**
     * 批量权限验证
     */
    public void validatePermissions(Long userId, List<ProPermissionType> requiredPermissions) {
        for (ProPermissionType permission : requiredPermissions) {
            if (!configService.hasPermission(userId, permission)) {
                throw new ProPermissionException(
                    ProPermissionErrorCode.PERMISSION_DENIED, 
                    "缺少权限：" + permission.getDesc()
                );
            }
        }
    }
}
```

## 总结

通过以上示例，可以看到Pro权限配置系统的主要特点：

1. **自动化权限开通**：用户升级后自动根据套餐配置开通相应权限
2. **灵活的权限验证**：支持注解和手动验证两种方式
3. **使用次数控制**：支持权限使用次数限制和统计
4. **管理员操作**：支持管理员手动开通/回收权限
5. **动态配置**：支持运行时查询和修改权限配置
6. **完善的异常处理**：统一的权限异常处理机制

这样的设计既保证了权限控制的严密性，又提供了良好的可扩展性和易用性。 