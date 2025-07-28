package com.gig.collide.admin.controller;

import com.gig.collide.admin.service.AdminOrderService;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.web.vo.Result;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 管理后台 - 订单管理控制器
 * 
 * 独立的管理端订单控制器，提供：
 * - 订单查询和统计
 * - 订单状态管理
 * - 退款处理
 * - 订单内容关联管理
 * - 数据导出
 * 
 * @author Collide Team
 * @since 2024-01-01
 */
@Slf4j
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "管理后台-订单管理", description = "管理员专用的订单管理接口")
public class AdminOrderController {
    
    private final AdminOrderService adminOrderService;
    
    /**
     * 分页查询订单列表
     */
    @GetMapping("/list")
    @Operation(summary = "分页查询订单", description = "管理员查看所有订单列表")
    public Result<PageResponse<OrderInfo>> getOrdersList(
            @Parameter(description = "页码", example = "1") 
            @RequestParam(defaultValue = "1") Integer pageNo,
            
            @Parameter(description = "每页大小", example = "20") 
            @RequestParam(defaultValue = "20") Integer pageSize,
            
            @Parameter(description = "订单状态") 
            @RequestParam(required = false) String status,
            
            @Parameter(description = "用户ID") 
            @RequestParam(required = false) Long userId,
            
            @Parameter(description = "商品ID") 
            @RequestParam(required = false) Long goodsId,
            
            @Parameter(description = "商品类型") 
            @RequestParam(required = false) String goodsType,
            
            @Parameter(description = "支付方式") 
            @RequestParam(required = false) String payType,
            
            @Parameter(description = "订单号") 
            @RequestParam(required = false) String orderNo,
            
            @Parameter(description = "创建时间开始", example = "2024-01-01") 
            @RequestParam(required = false) String createTimeStart,
            
            @Parameter(description = "创建时间结束", example = "2024-01-31") 
            @RequestParam(required = false) String createTimeEnd) {
        
        try {
            log.info("[管理后台] 分页查询订单，页码：{}，每页大小：{}，状态：{}，用户ID：{}，订单号：{}", 
                pageNo, pageSize, status, userId, orderNo);
            
            Map<String, Object> queryParams = Map.of(
                "pageNo", pageNo,
                "pageSize", pageSize,
                "status", status != null ? status : "",
                "userId", userId != null ? userId : 0L,
                "goodsId", goodsId != null ? goodsId : 0L,
                "goodsType", goodsType != null ? goodsType : "",
                "payType", payType != null ? payType : "",
                "orderNo", orderNo != null ? orderNo : "",
                "createTimeStart", createTimeStart != null ? createTimeStart : "",
                "createTimeEnd", createTimeEnd != null ? createTimeEnd : ""
            );
            
            PageResponse<OrderInfo> pageResponse = adminOrderService.pageQueryOrders(queryParams);
            
            log.info("[管理后台] 订单列表查询完成，总记录数：{}，当前页记录数：{}", 
                pageResponse.getTotal(), pageResponse.getRecords().size());
            
            return Result.success(pageResponse);
            
        } catch (Exception e) {
            log.error("[管理后台] 分页查询订单失败", e);
            return Result.error("QUERY_ORDERS_ERROR", "查询订单列表失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取订单详情
     */
    @GetMapping("/{orderNo}")
    @Operation(summary = "获取订单详情", description = "根据订单号获取订单详细信息")
    public Result<OrderInfo> getOrderDetail(
            @Parameter(description = "订单号", required = true) 
            @PathVariable String orderNo) {
        
        try {
            log.info("[管理后台] 获取订单详情，订单号：{}", orderNo);
            
            OrderInfo orderInfo = adminOrderService.getOrderDetail(orderNo);
            
            return Result.success(orderInfo);
            
        } catch (Exception e) {
            log.error("[管理后台] 获取订单详情失败，订单号：{}", orderNo, e);
            return Result.error("GET_ORDER_ERROR", "获取订单详情失败：" + e.getMessage());
        }
    }
    
    /**
     * 取消订单
     */
    @PostMapping("/{orderNo}/cancel")
    @Operation(summary = "取消订单", description = "管理员取消订单")
    public Result<Void> cancelOrder(
            @Parameter(description = "订单号", required = true) 
            @PathVariable String orderNo,
            
            @Parameter(description = "取消原因") 
            @RequestParam(required = false) String reason) {
        
        try {
            log.info("[管理后台] 取消订单，订单号：{}，原因：{}", orderNo, reason);
            
            adminOrderService.cancelOrder(orderNo, reason);
            
            log.info("[管理后台] 订单取消成功，订单号：{}", orderNo);
            return Result.success(null);
            
        } catch (Exception e) {
            log.error("[管理后台] 取消订单失败，订单号：{}", orderNo, e);
            return Result.error("CANCEL_ORDER_ERROR", "取消订单失败：" + e.getMessage());
        }
    }
    
    /**
     * 订单退款
     */
    @PostMapping("/{orderNo}/refund")
    @Operation(summary = "订单退款", description = "管理员处理订单退款")
    public Result<Void> refundOrder(
            @Parameter(description = "订单号", required = true) 
            @PathVariable String orderNo,
            
            @Parameter(description = "退款金额") 
            @RequestParam(required = false) String refundAmount,
            
            @Parameter(description = "退款原因") 
            @RequestParam(required = false) String reason) {
        
        try {
            log.info("[管理后台] 处理订单退款，订单号：{}，退款金额：{}，原因：{}", orderNo, refundAmount, reason);
            
            adminOrderService.refundOrder(orderNo, refundAmount, reason);
            
            log.info("[管理后台] 订单退款成功，订单号：{}", orderNo);
            return Result.success(null);
            
        } catch (Exception e) {
            log.error("[管理后台] 订单退款失败，订单号：{}", orderNo, e);
            return Result.error("REFUND_ORDER_ERROR", "订单退款失败：" + e.getMessage());
        }
    }
    
    /**
     * 批量处理订单
     */
    @PostMapping("/batch/{action}")
    @Operation(summary = "批量处理订单", description = "批量取消或退款订单")
    public Result<Map<String, Object>> batchProcessOrders(
            @Parameter(description = "操作类型", required = true)
            @PathVariable String action,
            
            @Parameter(description = "订单号列表", required = true)
            @RequestBody List<String> orderNos,
            
            @Parameter(description = "处理原因")
            @RequestParam(required = false) String reason) {
        
        try {
            log.info("[管理后台] 批量处理订单，操作：{}，订单数量：{}，原因：{}", action, orderNos.size(), reason);
            
            Map<String, Object> result = adminOrderService.batchProcessOrders(action, orderNos, reason);
            
            log.info("[管理后台] 批量处理订单完成，成功数量：{}", result.get("successCount"));
            return Result.success(result);
            
        } catch (Exception e) {
            log.error("[管理后台] 批量处理订单失败，操作：{}，订单列表：{}", action, orderNos, e);
            return Result.error("BATCH_PROCESS_ERROR", "批量操作失败：" + e.getMessage());
        }
    }
    
    /**
     * 订单统计
     */
    @GetMapping("/statistics")
    @Operation(summary = "订单统计", description = "获取订单统计信息")
    public Result<Map<String, Object>> getOrderStatistics(
            @Parameter(description = "统计开始日期", example = "2024-01-01")
            @RequestParam(required = false) String startDate,
            
            @Parameter(description = "统计结束日期", example = "2024-01-31")
            @RequestParam(required = false) String endDate,
            
            @Parameter(description = "统计维度")
            @RequestParam(defaultValue = "day") String dimension) {
        
        try {
            log.info("[管理后台] 获取订单统计，开始日期：{}，结束日期：{}，维度：{}", startDate, endDate, dimension);
            
            Map<String, Object> statistics = adminOrderService.getOrderStatistics(startDate, endDate, dimension);
            
            log.info("[管理后台] 订单统计查询完成");
            return Result.success(statistics);
            
        } catch (Exception e) {
            log.error("[管理后台] 获取订单统计失败", e);
            return Result.error("GET_STATISTICS_ERROR", "获取统计数据失败：" + e.getMessage());
        }
    }
    
    /**
     * 订单内容关联管理
     */
    @GetMapping("/{orderNo}/contents")
    @Operation(summary = "查询订单内容关联", description = "查询订单关联的内容权限")
    public Result<List<Map<String, Object>>> getOrderContents(
            @Parameter(description = "订单号", required = true) 
            @PathVariable String orderNo) {
        
        try {
            log.info("[管理后台] 查询订单内容关联，订单号：{}", orderNo);
            
            List<Map<String, Object>> contents = adminOrderService.getOrderContents(orderNo);
            
            return Result.success(contents);
            
        } catch (Exception e) {
            log.error("[管理后台] 查询订单内容关联失败，订单号：{}", orderNo, e);
            return Result.error("GET_ORDER_CONTENTS_ERROR", "查询订单内容关联失败：" + e.getMessage());
        }
    }
    
    /**
     * 订单权限管理
     */
    @PostMapping("/{orderNo}/permissions/{action}")
    @Operation(summary = "管理订单权限", description = "激活或撤销订单相关的内容观看权限")
    public Result<Void> manageOrderPermissions(
            @Parameter(description = "订单号", required = true) 
            @PathVariable String orderNo,
            
            @Parameter(description = "权限操作", required = true)
            @PathVariable String action,
            
            @Parameter(description = "操作原因")
            @RequestParam(required = false) String reason) {
        
        try {
            log.info("[管理后台] 管理订单权限，订单号：{}，操作：{}，原因：{}", orderNo, action, reason);
            
            adminOrderService.manageOrderPermissions(orderNo, action, reason);
            
            log.info("[管理后台] 订单权限管理成功，订单号：{}，操作：{}", orderNo, action);
            return Result.success(null);
            
        } catch (Exception e) {
            log.error("[管理后台] 管理订单权限失败，订单号：{}，操作：{}", orderNo, action, e);
            return Result.error("MANAGE_PERMISSIONS_ERROR", "权限操作失败：" + e.getMessage());
        }
    }
    
    /**
     * 导出订单数据
     */
    @GetMapping("/export")
    @Operation(summary = "导出订单数据", description = "导出订单数据到Excel")
    public Result<Map<String, String>> exportOrders(
            @Parameter(description = "订单状态筛选")
            @RequestParam(required = false) String status,
            
            @Parameter(description = "开始时间")
            @RequestParam(required = false) String startDate,
            
            @Parameter(description = "结束时间")
            @RequestParam(required = false) String endDate,
            
            @Parameter(description = "导出格式")
            @RequestParam(defaultValue = "xlsx") String format) {
        
        try {
            log.info("[管理后台] 导出订单数据，状态：{}，时间范围：{} - {}，格式：{}", 
                status, startDate, endDate, format);
            
            Map<String, String> result = adminOrderService.exportOrders(status, startDate, endDate, format);
            
            log.info("[管理后台] 订单数据导出成功，文件路径：{}", result.get("filePath"));
            return Result.success(result);
            
        } catch (Exception e) {
            log.error("[管理后台] 导出订单数据失败", e);
            return Result.error("EXPORT_ERROR", "数据导出失败：" + e.getMessage());
        }
    }
} 