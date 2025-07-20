#!/bin/bash

# ==========================================
# Collide 应用服务快速启动脚本
# ==========================================

set -e

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

# 检查Docker和Docker Compose
check_requirements() {
    log_info "检查系统要求..."
    
    if ! command -v docker &> /dev/null; then
        log_error "Docker 未安装，请先安装 Docker"
        exit 1
    fi
    
    if ! command -v docker-compose &> /dev/null; then
        log_error "Docker Compose 未安装，请先安装 Docker Compose"
        exit 1
    fi
    
    # 检查Docker服务状态
    if ! docker info &> /dev/null; then
        log_error "Docker 服务未启动，请启动 Docker 服务"
        exit 1
    fi
    
    log_success "系统要求检查通过"
}

# 检查端口占用
check_ports() {
    log_info "检查应用服务端口占用情况..."
    
    PORTS=(8081 8082 8085)
    OCCUPIED_PORTS=()
    
    for port in "${PORTS[@]}"; do
        if netstat -tuln 2>/dev/null | grep ":$port " > /dev/null || \
           ss -tuln 2>/dev/null | grep ":$port " > /dev/null || \
           lsof -i :$port 2>/dev/null > /dev/null; then
            OCCUPIED_PORTS+=($port)
        fi
    done
    
    if [ ${#OCCUPIED_PORTS[@]} -gt 0 ]; then
        log_warning "以下端口已被占用: ${OCCUPIED_PORTS[*]}"
        log_warning "请确保这些端口可用，或停止占用这些端口的服务"
        read -p "是否继续部署? (y/N): " -n 1 -r
        echo
        if [[ ! $REPLY =~ ^[Yy]$ ]]; then
            exit 1
        fi
    else
        log_success "端口检查通过"
    fi
}

# 检查中间件连通性
check_middleware() {
    log_info "检查中间件连通性..."
    
    # 检查MySQL
    if ! nc -z localhost 3306 2>/dev/null; then
        log_warning "MySQL (3306) 连接失败，请确保MySQL服务已启动"
    else
        log_success "MySQL 连接正常"
    fi
    
    # 检查Redis
    if ! nc -z localhost 6379 2>/dev/null; then
        log_warning "Redis (6379) 连接失败，请确保Redis服务已启动"
    else
        log_success "Redis 连接正常"
    fi
    
    # 检查Nacos
    if ! nc -z localhost 8848 2>/dev/null; then
        log_warning "Nacos (8848) 连接失败，请确保Nacos服务已启动"
    else
        log_success "Nacos 连接正常"
    fi
    
    # 检查RocketMQ
    if ! nc -z localhost 9876 2>/dev/null; then
        log_warning "RocketMQ (9876) 连接失败，请确保RocketMQ服务已启动"
    else
        log_success "RocketMQ 连接正常"
    fi
}

# 启动应用服务
start_applications() {
    log_info "启动应用服务..."
    
    # 按依赖顺序启动
    log_info "启动认证服务..."
    docker-compose up -d collide-auth
    
    # 等待认证服务启动
    log_info "等待认证服务启动..."
    for i in {1..30}; do
        if curl -s http://localhost:8082/actuator/health | grep -q "UP"; then
            log_success "认证服务启动成功"
            break
        fi
        if [ $i -eq 30 ]; then
            log_error "认证服务启动超时，请检查日志: docker-compose logs collide-auth"
            exit 1
        fi
        sleep 3
    done
    
    log_info "启动应用服务..."
    docker-compose up -d collide-application
    
    # 等待应用服务启动
    log_info "等待应用服务启动..."
    for i in {1..60}; do
        if curl -s http://localhost:8085/actuator/health | grep -q "UP"; then
            log_success "应用服务启动成功"
            break
        fi
        if [ $i -eq 60 ]; then
            log_error "应用服务启动超时，请检查日志: docker-compose logs collide-application"
            exit 1
        fi
        sleep 3
    done
    
    log_info "启动网关服务..."
    docker-compose up -d collide-gateway
    
    # 等待网关服务启动
    log_info "等待网关服务启动..."
    for i in {1..30}; do
        if curl -s http://localhost:8081/actuator/health | grep -q "UP"; then
            log_success "网关服务启动成功"
            break
        fi
        if [ $i -eq 30 ]; then
            log_error "网关服务启动超时，请检查日志: docker-compose logs collide-gateway"
            exit 1
        fi
        sleep 3
    done
    
    log_success "所有应用服务启动完成"
}

# 显示服务状态
show_status() {
    log_info "服务状态:"
    docker-compose ps
    
    echo
    log_info "应用服务访问地址:"
    echo "🌐 网关服务:        http://localhost:8081"
    echo "🔐 认证服务:        http://localhost:8082"
    echo "🚀 应用服务:        http://localhost:8085"
    
    echo
    log_info "健康检查:"
    echo "🔍 网关健康检查:    curl http://localhost:8081/actuator/health"
    echo "🔍 认证健康检查:    curl http://localhost:8082/actuator/health"
    echo "🔍 应用健康检查:    curl http://localhost:8085/actuator/health"
    
    echo
    log_success "Collide 应用服务部署完成！"
}

# 清理函数
cleanup() {
    log_info "停止所有应用服务..."
    docker-compose down
    log_success "应用服务已停止"
}

# 主函数
main() {
    echo "=========================================="
    echo "🚀 Collide 应用服务快速部署脚本"
    echo "=========================================="
    echo
    
    # 检查参数
    case "${1:-}" in
        "stop")
            cleanup
            exit 0
            ;;
        "status")
            docker-compose ps
            exit 0
            ;;
        "logs")
            docker-compose logs -f "${2:-}"
            exit 0
            ;;
        "restart")
            cleanup
            sleep 3
            ;;
        "help"|"-h"|"--help")
            echo "用法: $0 [命令]"
            echo
            echo "命令:"
            echo "  (无参数)  - 启动所有应用服务"
            echo "  stop      - 停止所有应用服务"
            echo "  restart   - 重启所有应用服务"
            echo "  status    - 查看应用服务状态"
            echo "  logs [服务名] - 查看日志"
            echo "  help      - 显示帮助信息"
            echo
            echo "示例:"
            echo "  $0                         # 启动所有应用服务"
            echo "  $0 stop                   # 停止所有应用服务"
            echo "  $0 logs collide-auth      # 查看认证服务日志"
            echo "  $0 logs                   # 查看所有服务日志"
            exit 0
            ;;
    esac
    
    # 执行部署流程
    check_requirements
    check_ports
    check_middleware
    start_applications
    show_status
}

# 捕获中断信号
trap cleanup INT TERM

# 执行主函数
main "$@" 