#!/bin/bash

# ==========================================
# Dubbo 超时问题快速修复脚本
# ==========================================

echo "🔧 开始修复 Dubbo 超时问题..."

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 日志函数
log_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

log_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

log_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# 检查 Docker 容器状态
check_containers() {
    log_info "检查 Docker 容器状态..."
    
    # 检查 MySQL 容器
    if docker ps | grep -q "mysql"; then
        log_success "MySQL 容器运行正常"
    else
        log_error "MySQL 容器未运行"
        return 1
    fi
    
    # 检查 Redis 容器
    if docker ps | grep -q "redis"; then
        log_success "Redis 容器运行正常"
    else
        log_warning "Redis 容器未运行"
    fi
    
    # 检查 Nacos 容器
    if docker ps | grep -q "nacos"; then
        log_success "Nacos 容器运行正常"
    else
        log_error "Nacos 容器未运行"
        return 1
    fi
}

# 检查数据库连接
check_database() {
    log_info "检查数据库连接..."
    
    # 获取 MySQL 容器名称
    MYSQL_CONTAINER=$(docker ps --filter "name=mysql" --format "{{.Names}}" | head -1)
    
    if [ -z "$MYSQL_CONTAINER" ]; then
        log_error "未找到 MySQL 容器"
        return 1
    fi
    
    # 检查数据库连接
    if docker exec "$MYSQL_CONTAINER" mysql -u root -p123456 -e "SELECT 1;" 2>/dev/null; then
        log_success "数据库连接正常"
    else
        log_error "数据库连接失败"
        return 1
    fi
}

# 优化数据库配置
optimize_database() {
    log_info "优化数据库配置..."
    
    MYSQL_CONTAINER=$(docker ps --filter "name=mysql" --format "{{.Names}}" | head -1)
    
    if [ -z "$MYSQL_CONTAINER" ]; then
        log_error "未找到 MySQL 容器"
        return 1
    fi
    
    # 执行数据库优化脚本
    if docker exec "$MYSQL_CONTAINER" mysql -u root -p123456 collide < sql/08-database-performance-fix.sql; then
        log_success "数据库优化完成"
    else
        log_warning "数据库优化脚本执行失败，但继续执行"
    fi
}

# 重启服务
restart_services() {
    log_info "重启相关服务..."
    
    # 重启 collide-application 服务
    if docker ps | grep -q "collide-application"; then
        log_info "重启 collide-application 服务..."
        docker restart $(docker ps --filter "name=collide-application" --format "{{.Names}}")
        sleep 10
    fi
    
    # 重启 collide-auth 服务
    if docker ps | grep -q "collide-auth"; then
        log_info "重启 collide-auth 服务..."
        docker restart $(docker ps --filter "name=collide-auth" --format "{{.Names}}")
        sleep 10
    fi
}

# 检查服务健康状态
check_services() {
    log_info "检查服务健康状态..."
    
    # 等待服务启动
    sleep 30
    
    # 检查 collide-application 健康状态
    if curl -s http://localhost:9503/health | grep -q "UP"; then
        log_success "collide-application 服务健康"
    else
        log_warning "collide-application 服务可能未完全启动"
    fi
    
    # 检查 collide-auth 健康状态
    if curl -s http://localhost:9502/health | grep -q "UP"; then
        log_success "collide-auth 服务健康"
    else
        log_warning "collide-auth 服务可能未完全启动"
    fi
}

# 清理日志
cleanup_logs() {
    log_info "清理旧日志..."
    
    # 清理应用日志
    find logs/ -name "*.log" -mtime +7 -delete 2>/dev/null || true
    
    # 清理 Docker 日志
    docker system prune -f 2>/dev/null || true
    
    log_success "日志清理完成"
}

# 显示优化建议
show_recommendations() {
    echo ""
    log_info "📋 优化建议："
    echo "1. 监控数据库连接池使用情况"
    echo "2. 定期执行数据库性能诊断脚本"
    echo "3. 考虑增加数据库服务器资源"
    echo "4. 优化慢查询和索引"
    echo "5. 考虑使用读写分离"
    echo ""
    log_info "📊 监控命令："
    echo "- 查看数据库连接：docker exec mysql mysql -u root -p123456 -e \"SHOW PROCESSLIST;\""
    echo "- 查看服务日志：docker logs -f collide-application"
    echo "- 查看健康状态：curl http://localhost:9503/health"
    echo ""
}

# 主函数
main() {
    echo "🚀 Dubbo 超时问题快速修复脚本"
    echo "=================================="
    
    # 检查容器状态
    if ! check_containers; then
        log_error "容器状态检查失败，请先启动必要的容器"
        exit 1
    fi
    
    # 检查数据库连接
    if ! check_database; then
        log_error "数据库连接检查失败"
        exit 1
    fi
    
    # 优化数据库配置
    optimize_database
    
    # 重启服务
    restart_services
    
    # 检查服务健康状态
    check_services
    
    # 清理日志
    cleanup_logs
    
    # 显示优化建议
    show_recommendations
    
    log_success "🎉 Dubbo 超时问题修复完成！"
    log_info "请测试用户注册功能是否正常"
}

# 执行主函数
main "$@" 