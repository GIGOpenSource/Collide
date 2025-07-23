#!/bin/bash

# Collide 项目服务停止脚本
# 用途：停止 Gateway、Auth、Application 三个服务

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

# 停止单个服务
stop_service() {
    local service_name=$1
    local pid_file="${service_name}.pid"
    local port=$2
    
    print_info "停止 $service_name 服务..."
    
    # 方法1：通过PID文件停止
    if [ -f "$pid_file" ]; then
        PID=$(cat "$pid_file")
        if ps -p $PID > /dev/null 2>&1; then
            print_info "通过PID文件停止进程：$PID"
            
            # 优雅停止
            kill -TERM $PID 2>/dev/null
            
            # 等待进程退出
            local count=0
            while [ $count -lt 30 ] && ps -p $PID > /dev/null 2>&1; do
                count=$((count + 1))
                sleep 1
                echo -n "."
            done
            echo ""
            
            # 如果还没退出，强制停止
            if ps -p $PID > /dev/null 2>&1; then
                print_warning "优雅停止超时，强制停止进程：$PID"
                kill -KILL $PID 2>/dev/null || true
                sleep 2
            fi
            
            # 验证进程是否已停止
            if ! ps -p $PID > /dev/null 2>&1; then
                print_success "$service_name 服务已停止"
                rm -f "$pid_file"
            else
                print_error "$service_name 服务停止失败"
            fi
        else
            print_warning "PID文件存在但进程不存在：$PID"
            rm -f "$pid_file"
        fi
    fi
    
    # 方法2：通过端口查找进程停止（备用方案）
    if [ -n "$port" ]; then
        local port_pids=$(lsof -ti :$port 2>/dev/null)
        if [ -n "$port_pids" ]; then
            print_info "通过端口 $port 查找到进程：$port_pids"
            for pid in $port_pids; do
                print_info "停止占用端口 $port 的进程：$pid"
                kill -TERM $pid 2>/dev/null || true
                sleep 2
                kill -KILL $pid 2>/dev/null || true
            done
        fi
    fi
}

# 停止所有Java进程（最后的清理步骤）
cleanup_java_processes() {
    print_info "清理残留的Collide相关Java进程..."
    
    # 查找包含collide关键字的Java进程
    local java_pids=$(ps aux | grep -i java | grep -i collide | grep -v grep | awk '{print $2}')
    
    if [ -n "$java_pids" ]; then
        print_warning "发现残留的Collide Java进程：$java_pids"
        for pid in $java_pids; do
            print_info "清理进程：$pid"
            kill -KILL $pid 2>/dev/null || true
        done
        sleep 2
    else
        print_success "没有发现残留的Collide Java进程"
    fi
}

# 验证所有服务是否已停止
verify_services_stopped() {
    print_info "验证服务停止状态..."
    
    local ports=(9501 9502 9503)
    local services=("Gateway" "Auth" "Application")
    local all_stopped=true
    
    for i in "${!ports[@]}"; do
        local port=${ports[$i]}
        local service=${services[$i]}
        
        if lsof -i :$port &> /dev/null; then
            print_error "$service 服务仍在运行（端口 $port）"
            all_stopped=false
        else
            print_success "$service 服务已停止"
        fi
    done
    
    if $all_stopped; then
        print_success "所有服务已成功停止"
    else
        print_error "部分服务停止失败，可能需要手动清理"
    fi
}

# 主停止流程
main() {
    local target_service=$1
    
    if [ -n "$target_service" ]; then
        # 停止指定服务
        case "$target_service" in
            "gateway"|"gw")
                stop_service "gateway" 9501
                ;;
            "auth")
                stop_service "auth" 9502
                ;;
            "app"|"application")
                stop_service "application" 9503
                ;;
            *)
                print_error "未知的服务名：$target_service"
                print_info "支持的服务名：gateway, auth, app"
                exit 1
                ;;
        esac
    else
        # 停止所有服务
        print_info "=== 停止所有 Collide 服务 ==="
        
        stop_service "gateway" 9501
        stop_service "auth" 9502
        stop_service "application" 9503
        
        # 清理残留进程
        cleanup_java_processes
        
        # 验证停止状态
        verify_services_stopped
        
        print_info "=== 服务停止完成 ==="
    fi
}

# 执行主流程
main "$@" 