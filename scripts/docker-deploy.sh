#!/bin/bash

# Collide 项目 Docker 一键部署脚本
# 用途：使用 Docker Compose 启动三个应用服务

set -e

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 打印带颜色的信息
print_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# 检查Docker环境
check_docker() {
    if ! command -v docker &> /dev/null; then
        print_error "Docker 未安装或不在PATH中"
        exit 1
    fi
    
    if ! command -v docker-compose &> /dev/null && ! docker compose version &> /dev/null; then
        print_error "Docker Compose 未安装或不在PATH中"
        exit 1
    fi
    
    if ! docker info &> /dev/null; then
        print_error "Docker 守护进程未运行"
        exit 1
    fi
    
    print_success "Docker环境检查通过"
}

# 检查必要的文件
check_files() {
    if [ ! -f "docker-compose.yml" ]; then
        print_error "docker-compose.yml 文件不存在"
        exit 1
    fi
    
    if [ ! -f "config/docker/application-host.yml" ]; then
        print_error "config/docker/application-host.yml 配置文件不存在"
        exit 1
    fi
    
    if [ ! -f "config/docker/bootstrap-host.yml" ]; then
        print_error "config/docker/bootstrap-host.yml 配置文件不存在"
        exit 1
    fi
    
    print_success "配置文件检查通过"
}

# 创建必要的目录
create_directories() {
    print_info "创建必要的目录..."
    
    # 创建日志目录
    mkdir -p logs/gateway logs/auth logs/application
    
    # 创建配置目录
    mkdir -p config/docker
    
    print_success "目录创建完成"
}

# 构建和启动服务
deploy_services() {
    print_info "开始构建和部署服务..."
    
    # 先停止现有服务（如果存在）
    print_info "停止现有服务..."
    docker-compose down --remove-orphans 2>/dev/null || true
    
    # 构建镜像
    print_info "构建Docker镜像..."
    if docker-compose build --no-cache; then
        print_success "镜像构建成功"
    else
        print_error "镜像构建失败"
        exit 1
    fi
    
    # 启动服务
    print_info "启动服务..."
    if docker-compose up -d; then
        print_success "服务启动成功"
    else
        print_error "服务启动失败"
        exit 1
    fi
}

# 等待服务健康检查
wait_for_services() {
    print_info "等待服务健康检查..."
    
    local services=("collide-gateway:9501" "collide-auth:9502" "collide-application:9503")
    local timeout=120
    
    for service_info in "${services[@]}"; do
        local service_name=$(echo $service_info | cut -d':' -f1)
        local port=$(echo $service_info | cut -d':' -f2)
        
        print_info "等待 $service_name 服务就绪..."
        
        local count=0
        while [ $count -lt $timeout ]; do
            if curl -s http://localhost:$port/actuator/health &> /dev/null; then
                print_success "$service_name 服务已就绪"
                break
            fi
            
            count=$((count + 1))
            echo -n "."
            sleep 1
        done
        
        if [ $count -ge $timeout ]; then
            echo ""
            print_error "$service_name 服务启动超时"
            return 1
        fi
        echo ""
    done
}

# 显示服务状态
show_status() {
    print_info "服务部署状态："
    echo ""
    docker-compose ps
    echo ""
    
    print_info "服务访问地址："
    echo "  Gateway:     http://localhost:9501"
    echo "  Auth:        http://localhost:9502"
    echo "  Application: http://localhost:9503"
    echo ""
    
    print_info "管理命令："
    echo "  查看状态:    docker-compose ps"
    echo "  查看日志:    docker-compose logs [service]"
    echo "  停止服务:    docker-compose down"
    echo "  重启服务:    docker-compose restart [service]"
    echo ""
}

# 主部署流程
main() {
    print_info "=== Collide Docker 部署开始 ==="
    
    # 环境检查
    check_docker
    check_files
    create_directories
    
    # 构建项目 JAR 文件
    print_info "构建项目 JAR 文件..."
    if mvn clean package -DskipTests -q; then
        print_success "JAR 构建完成"
    else
        print_error "JAR 构建失败"
        exit 1
    fi
    
    # 部署服务
    deploy_services
    
    # 等待服务启动
    wait_for_services
    
    # 显示状态
    show_status
    
    print_success "=== Docker 部署完成 ==="
}

# 帮助信息
show_help() {
    echo "用法: $0 [选项]"
    echo ""
    echo "选项:"
    echo "  -h, --help     显示帮助信息"
    echo "  --no-wait      不等待服务健康检查"
    echo "  --build-only   只构建镜像，不启动服务"
    echo ""
    echo "示例:"
    echo "  $0              # 完整部署"
    echo "  $0 --no-wait    # 部署但不等待健康检查"
    echo "  $0 --build-only # 只构建镜像"
}

# 参数处理
NO_WAIT=false
BUILD_ONLY=false

while [[ $# -gt 0 ]]; do
    case $1 in
        -h|--help)
            show_help
            exit 0
            ;;
        --no-wait)
            NO_WAIT=true
            shift
            ;;
        --build-only)
            BUILD_ONLY=true
            shift
            ;;
        *)
            print_error "未知参数: $1"
            show_help
            exit 1
            ;;
    esac
done

# 执行主流程
if [ "$BUILD_ONLY" = true ]; then
    check_docker
    check_files
    docker-compose build --no-cache
    print_success "镜像构建完成"
else
    main
    
    if [ "$NO_WAIT" = false ]; then
        wait_for_services
    fi
fi 