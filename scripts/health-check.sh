#!/bin/bash

# Collide 项目健康检查脚本
# 用途：检查 Gateway、Auth、Application 三个服务的健康状态

set -e

# 配置信息
GATEWAY_PORT=9501
AUTH_PORT=9502
APP_PORT=9503

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
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

print_header() {
    echo -e "${CYAN}=== $1 ===${NC}"
}

# 检查单个服务健康状态
check_service_health() {
    local service_name=$1
    local port=$2
    local pid_file=$3
    
    echo ""
    print_header "$service_name 服务健康检查"
    
    # 检查进程状态
    local process_status="未知"
    local pid=""
    
    if [ -f "$pid_file" ]; then
        pid=$(cat "$pid_file" 2>/dev/null || echo "")
        if [ -n "$pid" ]; then
            if kill -0 "$pid" 2>/dev/null; then
                process_status="运行中"
                print_success "进程状态: $process_status (PID: $pid)"
            else
                process_status="已停止"
                print_error "进程状态: $process_status (PID文件存在但进程不存在)"
            fi
        else
            process_status="未知"
            print_warning "进程状态: $process_status (PID文件为空)"
        fi
    else
        # 通过端口检查进程
        if lsof -i :$port &> /dev/null; then
            pid=$(lsof -ti :$port 2>/dev/null || echo "")
            process_status="运行中"
            print_success "进程状态: $process_status (端口 $port 被占用，PID: $pid)"
        else
            process_status="已停止"
            print_error "进程状态: $process_status (端口 $port 未被占用)"
        fi
    fi
    
    # 检查端口监听状态
    if lsof -i :$port &> /dev/null; then
        print_success "端口状态: 端口 $port 正在监听"
    else
        print_error "端口状态: 端口 $port 未在监听"
        return 1
    fi
    
    # 检查HTTP健康端点
    local health_url="http://localhost:$port/actuator/health"
    local http_status=""
    local response_time=""
    
    print_info "检查健康端点: $health_url"
    
    # 使用curl检查健康状态，设置超时
    if command -v curl &> /dev/null; then
        local start_time=$(date +%s%3N)
        local curl_result=$(curl -s -w "HTTP_CODE:%{http_code}" --connect-timeout 5 --max-time 10 "$health_url" 2>/dev/null || echo "ERROR")
        local end_time=$(date +%s%3N)
        response_time=$((end_time - start_time))
        
        if [[ "$curl_result" != "ERROR" ]]; then
            http_status=$(echo "$curl_result" | grep -o "HTTP_CODE:[0-9]*" | cut -d: -f2)
            local response_body=$(echo "$curl_result" | sed 's/HTTP_CODE:[0-9]*$//')
            
            if [ "$http_status" = "200" ]; then
                print_success "HTTP状态: 200 OK (响应时间: ${response_time}ms)"
                
                # 解析健康状态详情
                if command -v jq &> /dev/null; then
                    local status=$(echo "$response_body" | jq -r '.status // "UNKNOWN"' 2>/dev/null || echo "UNKNOWN")
                    case "$status" in
                        "UP")
                            print_success "健康状态: UP ✓"
                            ;;
                        "DOWN")
                            print_error "健康状态: DOWN ✗"
                            ;;
                        "OUT_OF_SERVICE")
                            print_warning "健康状态: OUT_OF_SERVICE"
                            ;;
                        *)
                            print_warning "健康状态: $status"
                            ;;
                    esac
                    
                    # 显示组件健康状态
                    local components=$(echo "$response_body" | jq -r '.components // {} | keys[]' 2>/dev/null || echo "")
                    if [ -n "$components" ]; then
                        echo "  组件状态:"
                        echo "$components" | while read -r component; do
                            if [ -n "$component" ]; then
                                local comp_status=$(echo "$response_body" | jq -r ".components.\"$component\".status // \"UNKNOWN\"" 2>/dev/null || echo "UNKNOWN")
                                case "$comp_status" in
                                    "UP")
                                        echo -e "    ${GREEN}✓${NC} $component: $comp_status"
                                        ;;
                                    "DOWN")
                                        echo -e "    ${RED}✗${NC} $component: $comp_status"
                                        ;;
                                    *)
                                        echo -e "    ${YELLOW}?${NC} $component: $comp_status"
                                        ;;
                                esac
                            fi
                        done
                    fi
                else
                    print_info "健康响应: $(echo "$response_body" | head -c 200)..."
                fi
            else
                print_error "HTTP状态: $http_status (响应时间: ${response_time}ms)"
            fi
        else
            print_error "健康检查失败: 连接超时或服务不可用"
        fi
    else
        print_warning "curl 未安装，跳过HTTP健康检查"
    fi
    
    # 检查JVM内存使用情况（如果可能）
    if [ -n "$pid" ] && kill -0 "$pid" 2>/dev/null; then
        check_jvm_metrics "$service_name" "$port"
    fi
    
    return 0
}

# 检查JVM指标
check_jvm_metrics() {
    local service_name=$1
    local port=$2
    
    local metrics_url="http://localhost:$port/actuator/metrics/jvm.memory.used"
    
    if command -v curl &> /dev/null; then
        local memory_info=$(curl -s --connect-timeout 3 --max-time 5 "$metrics_url" 2>/dev/null || echo "")
        
        if [ -n "$memory_info" ] && command -v jq &> /dev/null; then
            local memory_used=$(echo "$memory_info" | jq -r '.measurements[0].value // 0' 2>/dev/null || echo "0")
            if [ "$memory_used" != "0" ]; then
                local memory_mb=$((memory_used / 1024 / 1024))
                print_info "JVM内存使用: ${memory_mb}MB"
            fi
        fi
    fi
}

# 生成状态报告
generate_status_report() {
    local gateway_status=$1
    local auth_status=$2
    local app_status=$3
    
    echo ""
    print_header "服务状态总结"
    
    echo "┌─────────────┬─────────┬─────────────────────────────────┐"
    echo "│   服务名    │  状态   │           访问地址              │"
    echo "├─────────────┼─────────┼─────────────────────────────────┤"
    
    # Gateway状态
    if [ "$gateway_status" = "0" ]; then
        echo -e "│ Gateway     │ ${GREEN}运行中${NC}  │ http://localhost:$GATEWAY_PORT    │"
    else
        echo -e "│ Gateway     │ ${RED}异常${NC}    │ http://localhost:$GATEWAY_PORT    │"
    fi
    
    # Auth状态
    if [ "$auth_status" = "0" ]; then
        echo -e "│ Auth        │ ${GREEN}运行中${NC}  │ http://localhost:$AUTH_PORT      │"
    else
        echo -e "│ Auth        │ ${RED}异常${NC}    │ http://localhost:$AUTH_PORT      │"
    fi
    
    # Application状态
    if [ "$app_status" = "0" ]; then
        echo -e "│ Application │ ${GREEN}运行中${NC}  │ http://localhost:$APP_PORT      │"
    else
        echo -e "│ Application │ ${RED}异常${NC}    │ http://localhost:$APP_PORT      │"
    fi
    
    echo "└─────────────┴─────────┴─────────────────────────────────┘"
    
    # 总体状态
    local total_healthy=0
    [ "$gateway_status" = "0" ] && total_healthy=$((total_healthy + 1))
    [ "$auth_status" = "0" ] && total_healthy=$((total_healthy + 1))
    [ "$app_status" = "0" ] && total_healthy=$((total_healthy + 1))
    
    echo ""
    if [ "$total_healthy" = "3" ]; then
        print_success "总体状态: 所有服务运行正常 ✓ ($total_healthy/3)"
    elif [ "$total_healthy" = "0" ]; then
        print_error "总体状态: 所有服务异常 ✗ ($total_healthy/3)"
    else
        print_warning "总体状态: 部分服务异常 ⚠ ($total_healthy/3)"
    fi
}

# 显示系统资源使用情况
show_system_resources() {
    echo ""
    print_header "系统资源使用情况"
    
    # CPU使用率
    if command -v top &> /dev/null; then
        local cpu_usage=$(top -bn1 | grep "Cpu(s)" | sed "s/.*, *\([0-9.]*\)%* id.*/\1/" | awk '{print 100 - $1}')
        print_info "CPU使用率: ${cpu_usage}%"
    fi
    
    # 内存使用情况
    if command -v free &> /dev/null; then
        local mem_info=$(free -m | awk 'NR==2{printf "%.1f%% (%d/%dMB)", $3*100/$2, $3, $2}')
        print_info "内存使用率: $mem_info"
    fi
    
    # 磁盘使用情况
    if command -v df &> /dev/null; then
        local disk_usage=$(df -h . | awk 'NR==2{printf "%s (%s)", $5, $4}')
        print_info "磁盘使用率: $disk_usage 剩余"
    fi
    
    # Java进程数量
    local java_processes=$(pgrep -f "java.*\.jar" | wc -l)
    print_info "Java进程数量: $java_processes"
}

# 主检查流程
main() {
    print_header "Collide 项目健康检查"
    echo "检查时间: $(date '+%Y-%m-%d %H:%M:%S')"
    
    # 检查各个服务
    check_service_health "Gateway" "$GATEWAY_PORT" "gateway.pid"
    local gateway_status=$?
    
    check_service_health "Auth" "$AUTH_PORT" "auth.pid"
    local auth_status=$?
    
    check_service_health "Application" "$APP_PORT" "application.pid"
    local app_status=$?
    
    # 生成报告
    generate_status_report "$gateway_status" "$auth_status" "$app_status"
    
    # 显示系统资源
    show_system_resources
    
    echo ""
    print_header "管理命令"
    echo "  启动服务: ./deploy.sh"
    echo "  停止服务: ./stop.sh"
    echo "  查看日志: ./logs.sh [gateway|auth|app]"
    echo "  实时监控: watch -n 5 ./health-check.sh"
    
    # 返回总体状态码
    local total_errors=$((gateway_status + auth_status + app_status))
    return $total_errors
}

# 支持单个服务检查
if [ $# -eq 1 ]; then
    case $1 in
        gateway|gw)
            check_service_health "Gateway" "$GATEWAY_PORT" "gateway.pid"
            ;;
        auth)
            check_service_health "Auth" "$AUTH_PORT" "auth.pid"
            ;;
        app|application)
            check_service_health "Application" "$APP_PORT" "application.pid"
            ;;
        *)
            echo "用法: $0 [gateway|auth|app]"
            exit 1
            ;;
    esac
else
    main
fi 