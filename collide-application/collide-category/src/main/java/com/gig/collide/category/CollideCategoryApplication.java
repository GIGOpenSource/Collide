package com.gig.collide.category;

import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 分类服务启动类 - 简洁版
 * 基于category-simple.sql的设计，支持层级分类管理
 * 
 * @author Collide
 * @version 2.0.0 (简洁版)
 * @since 2024-01-01
 */
@Slf4j
@SpringBootApplication
@EnableDubbo
@EnableTransactionManagement
@MapperScan("com.gig.collide.category.infrastructure.mapper")
public class CollideCategoryApplication {

    public static void main(String[] args) {
        try {
            SpringApplication.run(CollideCategoryApplication.class, args);
            
            log.info("========================================");
            log.info("🚀 分类服务启动成功！");
            log.info("📋 服务信息:");
            log.info("   • 服务名称: collide-category");
            log.info("   • 版本号: 2.0.0 (简洁版)");
            log.info("   • 架构: 无连表设计 + KISS原则");
            log.info("");
            log.info("🏗️ 核心特性:");
            log.info("   • 层级分类 - 支持多级分类结构");
            log.info("   • 状态管理 - active/inactive状态控制");
            log.info("   • 冗余统计 - content_count内容数量");
            log.info("   • 树形结构 - 完整的树形操作支持");
            log.info("   • 智能搜索 - 分类名称模糊搜索");
            log.info("   • 热门分类 - 基于内容数量排序");
            log.info("");
            log.info("🔌 API接口:");
            log.info("   • Dubbo RPC: CategoryFacadeService v2.0.0");
            log.info("   • REST API: /api/v1/categories/*");
            log.info("   • 管理功能: 创建/更新/删除/查询");
            log.info("   • 层级操作: 根分类/子分类/分类树");
            log.info("   • 状态管理: 激活/停用/批量操作");
            log.info("   • 统计功能: 内容数量/热门分类");
            log.info("");
            log.info("📊 数据库设计:");
            log.info("   • 主表: t_category (单表设计)");
            log.info("   • 层级字段: parent_id (支持无限层级)");
            log.info("   • 冗余字段: content_count (内容统计)");
            log.info("   • 状态字段: status (active/inactive)");
            log.info("   • 排序字段: sort (显示顺序控制)");
            log.info("");
            log.info("🛠️ 技术栈:");
            log.info("   • Spring Boot 3.x - 核心框架");
            log.info("   • Apache Dubbo - RPC通信");
            log.info("   • MyBatis-Plus - ORM框架");
            log.info("   • Jakarta Validation - 参数验证");
            log.info("   • Lombok - 代码简化");
            log.info("");
            log.info("📈 性能优化:");
            log.info("   • 无连表查询 - 避免复杂JOIN操作");
            log.info("   • 冗余字段设计 - 减少数据库访问");
            log.info("   • 索引优化 - parent_id, status, sort");
            log.info("   • 分页查询 - 大数据量处理优化");
            log.info("   • 树形缓存 - 内存构建树形结构");
            log.info("");
            log.info("🔐 安全特性:");
            log.info("   • 参数验证 - Jakarta Validation注解");
            log.info("   • 权限控制 - 操作人ID验证");
            log.info("   • 状态管理 - 逻辑删除设计");
            log.info("   • 循环检测 - 防止层级循环引用");
            log.info("");
            log.info("📊 监控指标:");
            log.info("   • 分类总数 - 系统分类统计");
            log.info("   • 层级深度 - 分类树深度监控");
            log.info("   • 热门分类 - 内容数量排行");
            log.info("   • 状态分布 - active/inactive分布");
            log.info("");
            log.info("🔧 运维功能:");
            log.info("   • 数据同步 - 冗余字段同步修复");
            log.info("   • 统计重算 - 内容数量重新计算");
            log.info("   • 层级修复 - 分类关系一致性检查");
            log.info("   • 批量操作 - 状态批量更新");
            log.info("");
            log.info("⚡ 即将支持:");
            log.info("   • 分类克隆 - 快速复制分类结构");
            log.info("   • 分类合并 - 多分类数据整合");
            log.info("   • 排序调整 - 拖拽式排序支持");
            log.info("   • 叶子分类 - 无子分类筛选");
            log.info("   • 名称检查 - 同级分类名称唯一性");
            log.info("");
            log.info("✨ 分类服务已就绪，支持层级分类的完整生命周期管理！");
            log.info("========================================");
            
        } catch (Exception e) {
            log.error("❌ 分类服务启动失败：", e);
            System.exit(1);
        }
    }
}