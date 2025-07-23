#!/bin/bash

# Collide 服务健康检查脚本
# 适用于 Ubuntu 22.04 环境

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 服务配置
declare -A SERVICES=(
    ["collide-auth"]="9502:/api/v1/auth/test"
    ["collide-application"]="9503:/actuator/health"
    ["collide-gateway"]="9501:/actuator/health"
)

# 日志函数
log_info() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

log_warn() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

log_step() {
    echo -e "${BLUE}[STEP]${NC} $1"
}

# 检查命令是否存在
check_command() {
    if ! command -v $1 &> /dev/null; then
        log_error "$1 命令未找到，请先安装"
        return 1
    fi
    return 0
}

# 检查服务进程状态
check_service_process() {
    local service_name=$1
    
    if systemctl is-active --quiet $service_name; then
        log_info "✅ $service_name 进程运行正常"
        return 0
    else
        log_error "❌ $service_name 进程未运行"
        return 1
    fi
}

# 检查端口监听
check_port_listening() {
    local port=$1
    local service_name=$2
    
    if netstat -tln 2>/dev/null | grep ":$port " > /dev/null; then
        log_info "✅ $service_name 端口 $port 正在监听"
        return 0
    else
        log_error "❌ $service_name 端口 $port 未监听"
        return 1
    fi
}

# 检查HTTP健康状态
check_http_health() {
    local url=$1
    local service_name=$2
    local timeout=${3:-10}
    
    if curl -s --max-time $timeout "$url" > /dev/null 2>&1; then
        log_info "✅ $service_name HTTP健康检查通过"
        return 0
    else
        log_error "❌ $service_name HTTP健康检查失败"
        return 1
    fi
}

# 检查Java进程
check_java_processes() {
    log_step "检查Java进程..."
    
    local java_processes=$(ps aux | grep java | grep -v grep | wc -l)
    if [[ $java_processes -gt 0 ]]; then
        log_info "✅ 发现 $java_processes 个Java进程"
        echo "Java进程详情:"
        ps aux | grep java | grep -v grep | awk '{print "  PID: " $2 ", CPU: " $3 "%, MEM: " $4 "%, CMD: " $11}'
    else
        log_warn "⚠️  未发现Java进程"
    fi
    echo ""
}

# 检查系统资源
check_system_resources() {
    log_step "检查系统资源..."
    
    # 内存使用情况
    local mem_info=$(free -m | grep '^Mem:')
    local total_mem=$(echo $mem_info | awk '{print $2}')
    local used_mem=$(echo $mem_info | awk '{print $3}')
    local mem_usage=$(echo "scale=1; $used_mem * 100 / $total_mem" | bc -l 2>/dev/null || echo "N/A")
    
    echo "内存使用: ${used_mem}MB / ${total_mem}MB (${mem_usage}%)"
    
    # 磁盘使用情况
    echo "磁盘使用:"
    df -h | grep -E '^/dev/' | awk '{print "  " $1 ": " $3 "/" $2 " (" $5 " 已使用)"}'
    
    # CPU负载
    local load_avg=$(uptime | awk -F'load average:' '{print $2}' | trim)
    echo "系统负载: $load_avg"
    echo ""
}

# 检查网络连接
check_network_connections() {
    log_step "检查网络连接..."
    
    echo "活跃的网络连接:"
    netstat -tlnp 2>/dev/null | grep -E ':(8081|8085|9500)' | while read line; do
        echo "  $line"
    done
    echo ""
}

# 检查日志文件
check_log_files() {
    log_step "检查日志文件..."
    
    local log_dirs=("/var/log/collide-auth" "/var/log/collide-application" "/var/log/collide-gateway")
    
    for log_dir in "${log_dirs[@]}"; do
        if [[ -d "$log_dir" ]]; then
            local service_name=$(basename "$log_dir")
            local log_file="$log_dir/$service_name.log"
            
            if [[ -f "$log_file" ]]; then
                local file_size=$(du -h "$log_file" | cut -f1)
                local last_modified=$(stat -c %y "$log_file" | cut -d'.' -f1)
                log_info "✅ $service_name 日志文件存在 (大小: $file_size, 最后修改: $last_modified)"
                
                # 检查最近的错误
                local error_count=$(tail -n 100 "$log_file" 2>/dev/null | grep -i error | wc -l)
                if [[ $error_count -gt 0 ]]; then
                    log_warn "⚠️  $service_name 日志中发现 $error_count 个错误（最近100行）"
                fi
            else
                log_error "❌ $service_name 日志文件不存在: $log_file"
            fi
        else
            log_error "❌ $service_name 日志目录不存在: $log_dir"
        fi
    done
    echo ""
}

# 主健康检查函数
main_health_check() {
    echo "==========================================="
    echo "        Collide 服务健康检查报告"
    echo "        时间: $(date '+%Y-%m-%d %H:%M:%S')"
    echo "        环境: Ubuntu 22.04"
    echo "==========================================="
    echo ""
    
    # 检查必要命令
    if ! check_command "curl" || ! check_command "systemctl" || ! check_command "netstat"; then
        log_error "缺少必要的系统命令，请先安装相关工具"
        exit 1
    fi
    
    local all_healthy=true
    
    # 检查每个服务
    for service_name in "${!SERVICES[@]}"; do
        log_step "检查 $service_name 服务..."
        
        local service_config="${SERVICES[$service_name]}"
        local port=$(echo "$service_config" | cut -d':' -f1)
        local health_path=$(echo "$service_config" | cut -d':' -f2-)
        local health_url="http://localhost:$port$health_path"
        
        local service_healthy=true
        
        # 检查进程状态
        if ! check_service_process "$service_name"; then
            service_healthy=false
        fi
        
        # 检查端口监听
        if ! check_port_listening "$port" "$service_name"; then
            service_healthy=false
        fi
        
        # 检查HTTP健康状态
        if ! check_http_health "$health_url" "$service_name"; then
            service_healthy=false
        fi
        
        if $service_healthy; then
            log_info "🎉 $service_name 服务完全健康"
        else
            log_error "💥 $service_name 服务存在问题"
            all_healthy=false
        fi
        
        echo ""
    done
    
    # 系统资源检查
    check_system_resources
    check_java_processes
    check_network_connections
    check_log_files
    
    # 总结
    echo "==========================================="
    if $all_healthy; then
        log_info "🎉 所有服务健康检查通过！"
        echo "系统运行正常，所有服务都在正常工作。"
    else
        log_error "💥 部分服务存在问题！"
        echo "请查看上述详细信息，并进行相应的故障排除。"
    fi
    echo "==========================================="
    
    return $all_healthy
}

# 显示详细服务信息
show_service_details() {
    local service_name=$1
    
    if [[ -z "$service_name" ]]; then
        echo "请指定服务名称: auth, application, gateway"
        return 1
    fi
    
    case "$service_name" in
        "auth")
            service_name="collide-auth"
            ;;
        "application")
            service_name="collide-application"
            ;;
        "gateway")
            service_name="collide-gateway"
            ;;
        "collide-"*)
            # 已经是完整的服务名称
            ;;
        *)
            echo "未知的服务名称: $service_name"
            echo "支持的服务: auth, application, gateway"
            return 1
            ;;
    esac
    
    echo "========== $service_name 详细信息 =========="
    
    # systemd 状态
    echo "🔧 systemd 状态:"
    systemctl status "$service_name" --no-pager || true
    echo ""
    
    # 最近日志
    echo "📋 最近日志 (最后20行):"
    journalctl -u "$service_name" --lines=20 --no-pager || true
    echo ""
    
    # 进程信息
    echo "💻 进程信息:"
    ps aux | grep "$service_name" | grep -v grep || echo "未找到相关进程"
    echo ""
}

# 显示帮助信息
show_help() {
    echo "Collide 健康检查脚本"
    echo ""
    echo "使用方法:"
    echo "  $0                     # 执行完整健康检查"
    echo "  $0 --detail <service>  # 显示特定服务详细信息"
    echo "  $0 --help             # 显示此帮助信息"
    echo ""
    echo "服务名称:"
    echo "  auth          - 认证服务"
    echo "  application   - 应用服务"
    echo "  gateway       - 网关服务"
    echo ""
    echo "示例:"
    echo "  $0                     # 检查所有服务"
    echo "  $0 --detail auth       # 查看认证服务详细信息"
}

# 主逻辑
case "${1:-}" in
    "--detail")
        show_service_details "$2"
        ;;
    "--help"|"-h")
        show_help
        ;;
    "")
        main_health_check
        ;;
    *)
        echo "未知参数: $1"
        show_help
        exit 1
        ;;
esac

exit $? 