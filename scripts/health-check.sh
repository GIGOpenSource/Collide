#!/bin/bash

# Collide 项目健康检查脚本
# 用途：检查 Gateway、Auth、Application 三个服务的健康状态

# 配置信息
declare -A SERVICES=(
    ["gateway"]="9501:Gateway"
    ["auth"]="9502:Auth"
    ["application"]="9503:Application"
)

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
PURPLE='\033[0;35m'
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
    echo -e "${PURPLE}=== $1 ===${NC}"
}

# 检查进程状态
check_process() {
    local service_name=$1
    local pid_file="${service_name}.pid"
    
    if [ -f "$pid_file" ]; then
        local pid=$(cat "$pid_file")
        if ps -p $pid > /dev/null 2>&1; then
            echo -e "${GREEN}✓${NC} 进程运行中 (PID: $pid)"
            return 0
        else
            echo -e "${RED}✗${NC} PID文件存在但进程不存在 (PID: $pid)"
            return 1
        fi
    else
        echo -e "${YELLOW}?${NC} PID文件不存在"
        return 1
    fi
}

# 检查端口状态
check_port() {
    local port=$1
    
    if lsof -i :$port &> /dev/null; then
        local pid=$(lsof -ti :$port 2>/dev/null)
        echo -e "${GREEN}✓${NC} 端口 $port 监听中 (PID: $pid)"
        return 0
    else
        echo -e "${RED}✗${NC} 端口 $port 未监听"
        return 1
    fi
}

# 检查HTTP健康端点
check_http_health() {
    local port=$1
    local service_name=$2
    local url="http://localhost:$port/actuator/health"
    
    # 检查HTTP响应
    local start_time=$(date +%s%3N)
    local response=$(curl -s -w "\n%{http_code}" "$url" 2>/dev/null)
    local end_time=$(date +%s%3N)
    local response_time=$((end_time - start_time))
    
    if [ $? -eq 0 ]; then
        local http_code=$(echo "$response" | tail -n1)
        local body=$(echo "$response" | head -n -1)
        
        if [ "$http_code" = "200" ]; then
            # 解析健康状态
            local status=$(echo "$body" | grep -o '"status":"[^"]*"' | cut -d'"' -f4)
            if [ "$status" = "UP" ]; then
                echo -e "${GREEN}✓${NC} HTTP健康检查通过 (${response_time}ms)"
                
                # 显示组件状态
                if command -v jq &> /dev/null; then
                    local components=$(echo "$body" | jq -r '.components // {} | keys[]' 2>/dev/null)
                    if [ -n "$components" ]; then
                        echo "  组件状态："
                        for component in $components; do
                            local comp_status=$(echo "$body" | jq -r ".components.\"$component\".status" 2>/dev/null)
                            if [ "$comp_status" = "UP" ]; then
                                echo -e "    ${GREEN}✓${NC} $component"
                            else
                                echo -e "    ${RED}✗${NC} $component ($comp_status)"
                            fi
                        done
                    fi
                fi
                return 0
            else
                echo -e "${RED}✗${NC} 服务状态异常: $status (${response_time}ms)"
                return 1
            fi
        else
            echo -e "${RED}✗${NC} HTTP错误: $http_code (${response_time}ms)"
            return 1
        fi
    else
        echo -e "${RED}✗${NC} HTTP请求失败"
        return 1
    fi
}

# 检查JVM内存使用
check_jvm_memory() {
    local port=$1
    local url="http://localhost:$port/actuator/metrics/jvm.memory.used"
    
    local response=$(curl -s "$url" 2>/dev/null)
    if [ $? -eq 0 ] && command -v jq &> /dev/null; then
        local memory_used=$(echo "$response" | jq -r '.measurements[0].value' 2>/dev/null)
        if [ "$memory_used" != "null" ] && [ -n "$memory_used" ]; then
            local memory_mb=$((memory_used / 1024 / 1024))
            echo -e "${CYAN}ℹ${NC} JVM内存使用: ${memory_mb}MB"
        fi
    fi
}

# 检查单个服务
check_single_service() {
    local service_key=$1
    local service_config=${SERVICES[$service_key]}
    local port=$(echo $service_config | cut -d':' -f1)
    local display_name=$(echo $service_config | cut -d':' -f2)
    
    echo -e "\n${CYAN}▶${NC} $display_name 服务检查："
    echo "  端口: $port"
    
    local process_ok=false
    local port_ok=false
    local http_ok=false
    
    # 检查进程
    echo -n "  进程状态: "
    if check_process "$service_key"; then
        process_ok=true
    fi
    
    # 检查端口
    echo -n "  端口状态: "
    if check_port "$port"; then
        port_ok=true
    fi
    
    # 检查HTTP健康端点
    if $port_ok; then
        echo -n "  健康检查: "
        if check_http_health "$port" "$display_name"; then
            http_ok=true
        fi
        
        # 检查JVM内存
        echo -n "  内存使用: "
        check_jvm_memory "$port"
    fi
    
    # 返回综合状态
    if $process_ok && $port_ok && $http_ok; then
        return 0
    else
        return 1
    fi
}

# 显示系统资源信息
show_system_info() {
    print_header "系统资源信息"
    
    # CPU使用率
    if command -v top &> /dev/null; then
        local cpu_usage=$(top -bn1 | grep "Cpu(s)" | awk '{print $2}' | sed 's/%us,//')
        echo -e "${CYAN}CPU使用率:${NC} $cpu_usage"
    fi
    
    # 内存使用
    if command -v free &> /dev/null; then
        local memory_info=$(free -h | grep "Mem:")
        local total=$(echo $memory_info | awk '{print $2}')
        local used=$(echo $memory_info | awk '{print $3}')
        local available=$(echo $memory_info | awk '{print $7}')
        echo -e "${CYAN}内存使用:${NC} $used / $total (可用: $available)"
    fi
    
    # 磁盘使用
    if command -v df &> /dev/null; then
        local disk_usage=$(df -h . | tail -1 | awk '{print $5}')
        echo -e "${CYAN}磁盘使用:${NC} $disk_usage"
    fi
    
    # Java进程统计
    local java_count=$(pgrep -f java | wc -l)
    echo -e "${CYAN}Java进程数:${NC} $java_count"
}

# 显示服务状态总结
show_summary() {
    print_header "服务状态总结"
    
    local running_count=0
    local total_count=${#SERVICES[@]}
    
    # 表格头部
    printf "┌─────────────┬─────────┬─────────────────────────────────┐\n"
    printf "│   服务名    │  状态   │           访问地址              │\n"
    printf "├─────────────┼─────────┼─────────────────────────────────┤\n"
    
    # 检查每个服务
    for service_key in "${!SERVICES[@]}"; do
        local service_config=${SERVICES[$service_key]}
        local port=$(echo $service_config | cut -d':' -f1)
        local display_name=$(echo $service_config | cut -d':' -f2)
        local url="http://localhost:$port"
        
        # 简单检查服务是否运行
        if lsof -i :$port &> /dev/null; then
            printf "│ %-11s │ ${GREEN}运行中${NC}  │ %-31s │\n" "$display_name" "$url"
            ((running_count++))
        else
            printf "│ %-11s │ ${RED}已停止${NC}  │ %-31s │\n" "$display_name" "$url"
        fi
    done
    
    # 表格底部
    printf "└─────────────┴─────────┴─────────────────────────────────┘\n"
    
    # 总体状态
    echo ""
    if [ $running_count -eq $total_count ]; then
        echo -e "总体状态: ${GREEN}所有服务运行正常${NC} ✓ ($running_count/$total_count)"
    elif [ $running_count -gt 0 ]; then
        echo -e "总体状态: ${YELLOW}部分服务运行${NC} ⚠ ($running_count/$total_count)"
    else
        echo -e "总体状态: ${RED}所有服务已停止${NC} ✗ ($running_count/$total_count)"
    fi
}

# 主检查流程
main() {
    local target_service=$1
    
    if [ -n "$target_service" ]; then
        # 检查指定服务
        if [[ " ${!SERVICES[@]} " =~ " $target_service " ]]; then
            print_header "检查 $target_service 服务"
            if check_single_service "$target_service"; then
                print_success "$target_service 服务运行正常"
            else
                print_error "$target_service 服务存在问题"
                exit 1
            fi
        else
            print_error "未知的服务名：$target_service"
            print_info "支持的服务名：${!SERVICES[*]}"
            exit 1
        fi
    else
        # 检查所有服务
        print_header "Collide 项目健康检查"
        
        local all_healthy=true
        
        # 逐个检查服务
        for service_key in gateway auth application; do
            if ! check_single_service "$service_key"; then
                all_healthy=false
            fi
        done
        
        echo ""
        
        # 显示系统信息
        show_system_info
        
        echo ""
        
        # 显示总结
        show_summary
        
        # 设置退出码
        if ! $all_healthy; then
            exit 1
        fi
    fi
}

# 执行主流程
main "$@" 