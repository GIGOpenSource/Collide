#!/bin/bash

# Collide 项目一键部署脚本
# 用途：启动 Gateway、Auth、Application 三个服务

set -e

# 配置信息
GATEWAY_PORT=9501
AUTH_PORT=9502
APP_PORT=9503

# 项目路径
GATEWAY_DIR="collide-gateway"
AUTH_DIR="collide-auth" 
APP_DIR="collide-application/collide-app"

# 日志目录
LOGS_DIR="./logs"
GATEWAY_LOG="$LOGS_DIR/gateway.log"
AUTH_LOG="$LOGS_DIR/auth.log"
APP_LOG="$LOGS_DIR/application.log"

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

# 检查Java环境
check_java() {
    if ! command -v java &> /dev/null; then
        print_error "Java 未安装或不在PATH中"
        exit 1
    fi
    
    JAVA_VERSION=$(java -version 2>&1 | head -n1 | cut -d'"' -f2 | cut -d'.' -f1)
    if [ "$JAVA_VERSION" -lt "21" ]; then
        print_error "需要Java 21或更高版本，当前版本：$JAVA_VERSION"
        exit 1
    fi
    
    print_success "Java环境检查通过：$(java -version 2>&1 | head -n1)"
}

# 创建日志目录
create_logs_dir() {
    if [ ! -d "$LOGS_DIR" ]; then
        mkdir -p "$LOGS_DIR"
        print_info "创建日志目录：$LOGS_DIR"
    fi
}

# 检查端口是否被占用
check_port() {
    local port=$1
    local service=$2
    
    if lsof -i :$port &> /dev/null; then
        print_warning "端口 $port 已被占用，可能是 $service 已在运行"
        print_info "尝试停止占用端口 $port 的进程..."
        
        # 获取占用端口的进程ID
        PID=$(lsof -ti :$port)
        if [ -n "$PID" ]; then
            kill -9 $PID 2>/dev/null || true
            print_success "已停止进程 $PID"
            sleep 2
        fi
    fi
}

# 构建项目
build_project() {
    print_info "开始构建项目..."
    
    if ! mvn clean package -DskipTests -q; then
        print_error "项目构建失败"
        exit 1
    fi
    
    print_success "项目构建完成"
}

# 启动Gateway服务
start_gateway() {
    print_info "启动 Gateway 服务..."
    
    check_port $GATEWAY_PORT "Gateway"
    
    cd $GATEWAY_DIR
    
    # 查找JAR文件
    JAR_FILE=$(find target -name "collide-gateway-*.jar" | head -n1)
    if [ ! -f "$JAR_FILE" ]; then
        print_error "Gateway JAR文件不存在，请先构建项目"
        exit 1
    fi
    
    # 启动服务
    nohup java -server \
        -Xms256m -Xmx512m \
        -Dspring.profiles.active=local \
        -Dserver.port=$GATEWAY_PORT \
        -Dlogging.file.path=../$LOGS_DIR \
        -jar "$JAR_FILE" \
        > "../$GATEWAY_LOG" 2>&1 &
    
    GATEWAY_PID=$!
    echo $GATEWAY_PID > ../gateway.pid
    
    cd ..
    print_success "Gateway 服务启动成功，PID: $GATEWAY_PID，端口: $GATEWAY_PORT"
}

# 启动Auth服务
start_auth() {
    print_info "启动 Auth 服务..."
    
    check_port $AUTH_PORT "Auth"
    
    cd $AUTH_DIR
    
    # 查找JAR文件
    JAR_FILE=$(find target -name "collide-auth-*.jar" | head -n1)
    if [ ! -f "$JAR_FILE" ]; then
        print_error "Auth JAR文件不存在，请先构建项目"
        exit 1
    fi
    
    # 启动服务
    nohup java -server \
        -Xms256m -Xmx512m \
        -Dspring.profiles.active=local \
        -Dserver.port=$AUTH_PORT \
        -Dlogging.file.path=../$LOGS_DIR \
        -jar "$JAR_FILE" \
        > "../$AUTH_LOG" 2>&1 &
    
    AUTH_PID=$!
    echo $AUTH_PID > ../auth.pid
    
    cd ..
    print_success "Auth 服务启动成功，PID: $AUTH_PID，端口: $AUTH_PORT"
}

# 启动Application服务
start_application() {
    print_info "启动 Application 服务..."
    
    check_port $APP_PORT "Application"
    
    cd $APP_DIR
    
    # 查找JAR文件
    JAR_FILE=$(find target -name "collide-app-*.jar" | head -n1)
    if [ ! -f "$JAR_FILE" ]; then
        print_error "Application JAR文件不存在，请先构建项目"
        exit 1
    fi
    
    # 启动服务
    nohup java -server \
        -Xms512m -Xmx1024m \
        -Dspring.profiles.active=local \
        -Dserver.port=$APP_PORT \
        -Dlogging.file.path=../../$LOGS_DIR \
        -jar "$JAR_FILE" \
        > "../../$APP_LOG" 2>&1 &
    
    APP_PID=$!
    echo $APP_PID > ../../application.pid
    
    cd ../..
    print_success "Application 服务启动成功，PID: $APP_PID，端口: $APP_PORT"
}

# 等待服务启动
wait_for_service() {
    local port=$1
    local service=$2
    local max_wait=60
    local count=0
    
    print_info "等待 $service 服务启动..."
    
    while [ $count -lt $max_wait ]; do
        if curl -s http://localhost:$port/actuator/health &> /dev/null; then
            print_success "$service 服务已就绪"
            return 0
        fi
        
        count=$((count + 1))
        echo -n "."
        sleep 1
    done
    
    echo ""
    print_error "$service 服务启动超时"
    return 1
}

# 主部署流程
main() {
    print_info "=== Collide 项目一键部署开始 ==="
    
    # 环境检查
    check_java
    create_logs_dir
    
    # 构建项目
    build_project
    
    # 启动服务
    start_gateway
    start_auth  
    start_application
    
    print_info "等待所有服务启动完成..."
    sleep 5
    
    # 检查服务状态
    print_info "检查服务健康状态..."
    
    wait_for_service $GATEWAY_PORT "Gateway"
    wait_for_service $AUTH_PORT "Auth"
    wait_for_service $APP_PORT "Application"
    
    print_success "=== 所有服务部署完成 ==="
    echo ""
    print_info "服务访问地址："
    echo "  Gateway:     http://localhost:$GATEWAY_PORT"
    echo "  Auth:        http://localhost:$AUTH_PORT" 
    echo "  Application: http://localhost:$APP_PORT"
    echo ""
    print_info "管理命令："
    echo "  查看状态:    ./health-check.sh"
    echo "  停止服务:    ./stop.sh"
    echo "  查看日志:    ./logs.sh [gateway|auth|app]"
}

# 执行主流程
main "$@" 