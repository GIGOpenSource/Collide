#!/bin/bash

# Collide 项目日志管理脚本
# 用途：查看和管理 Gateway、Auth、Application 三个服务的日志

# 配置信息
declare -A LOG_FILES=(
    ["gateway"]="logs/gateway.log"
    ["auth"]="logs/auth.log"
    ["app"]="logs/application.log"
    ["application"]="logs/application.log"
    ["gw"]="logs/gateway.log"
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

# 显示使用帮助
show_usage() {
    echo "用法: $0 <服务名> [选项]"
    echo ""
    echo "服务名:"
    echo "  gateway, gw     - Gateway服务日志"
    echo "  auth            - Auth服务日志"
    echo "  app, application - Application服务日志"
    echo "  all             - 所有服务日志"
    echo ""
    echo "选项:"
    echo "  -n <数量>       - 显示最后N行日志 (默认: 50)"
    echo "  -f, --follow    - 实时跟踪日志"
    echo "  -e, --error     - 只显示错误和警告日志"
    echo "  -t, --today     - 只显示今天的日志"
    echo "  -s, --search <关键词> - 搜索包含关键词的日志"
    echo "  --clear         - 清空日志文件"
    echo "  -h, --help      - 显示此帮助信息"
    echo ""
    echo "示例:"
    echo "  $0 gateway              # 查看Gateway最后50行日志"
    echo "  $0 auth -f              # 实时跟踪Auth日志"
    echo "  $0 app -n 100           # 查看Application最后100行日志"
    echo "  $0 gateway -e           # 只显示Gateway的错误日志"
    echo "  $0 auth -t              # 只显示Auth今天的日志"
    echo "  $0 app -s \"error\"       # 搜索Application日志中包含error的行"
    echo "  $0 gateway --clear      # 清空Gateway日志文件"
    echo "  $0 all -e -f            # 实时跟踪所有服务的错误日志"
}

# 获取日志文件路径
get_log_file() {
    local service_name=$1
    echo "${LOG_FILES[$service_name]}"
}

# 检查日志文件是否存在
check_log_file() {
    local log_file=$1
    local service_name=$2
    
    if [ ! -f "$log_file" ]; then
        print_error "$service_name 日志文件不存在: $log_file"
        print_info "请确保服务已启动并生成了日志文件"
        return 1
    fi
    
    if [ ! -r "$log_file" ]; then
        print_error "无法读取 $service_name 日志文件: $log_file"
        print_info "请检查文件权限"
        return 1
    fi
    
    return 0
}

# 过滤错误日志
filter_error_logs() {
    grep -i -E "(error|exception|fail|warn|fatal)" --color=always
}

# 过滤今天的日志
filter_today_logs() {
    local today=$(date '+%Y-%m-%d')
    grep "$today" --color=always
}

# 搜索日志
search_logs() {
    local keyword=$1
    grep -i "$keyword" --color=always
}

# 显示日志文件信息
show_log_info() {
    local log_file=$1
    local service_name=$2
    
    if [ -f "$log_file" ]; then
        local file_size=$(du -h "$log_file" | cut -f1)
        local line_count=$(wc -l < "$log_file")
        local last_modified=$(stat -c %y "$log_file" 2>/dev/null || stat -f %Sm "$log_file" 2>/dev/null)
        
        echo -e "${CYAN}文件信息:${NC}"
        echo "  路径: $log_file"
        echo "  大小: $file_size"
        echo "  行数: $line_count"
        echo "  修改时间: $last_modified"
        echo ""
    fi
}

# 查看单个服务日志
view_single_log() {
    local service_name=$1
    local log_file=$2
    local lines=$3
    local follow=$4
    local error_only=$5
    local today_only=$6
    local search_keyword=$7
    local clear_log=$8
    
    # 检查日志文件
    if ! check_log_file "$log_file" "$service_name"; then
        return 1
    fi
    
    # 清空日志文件
    if [ "$clear_log" = "true" ]; then
        print_warning "即将清空 $service_name 日志文件: $log_file"
        read -p "确认清空？(y/N): " -n 1 -r
        echo
        if [[ $REPLY =~ ^[Yy]$ ]]; then
            > "$log_file"
            print_success "$service_name 日志文件已清空"
        else
            print_info "操作已取消"
        fi
        return 0
    fi
    
    # 显示日志头信息
    print_header "$service_name 服务日志"
    show_log_info "$log_file" "$service_name"
    
    # 构建日志查看命令
    local cmd="tail"
    local filter_cmd=""
    
    # 设置行数
    if [ "$follow" = "true" ]; then
        cmd="$cmd -f"
        if [ "$lines" != "50" ]; then
            cmd="$cmd -n $lines"
        fi
    else
        cmd="$cmd -n $lines"
    fi
    
    # 构建过滤管道
    if [ "$error_only" = "true" ] && [ "$today_only" = "true" ] && [ -n "$search_keyword" ]; then
        filter_cmd="filter_today_logs | filter_error_logs | search_logs \"$search_keyword\""
    elif [ "$error_only" = "true" ] && [ "$today_only" = "true" ]; then
        filter_cmd="filter_today_logs | filter_error_logs"
    elif [ "$error_only" = "true" ] && [ -n "$search_keyword" ]; then
        filter_cmd="filter_error_logs | search_logs \"$search_keyword\""
    elif [ "$today_only" = "true" ] && [ -n "$search_keyword" ]; then
        filter_cmd="filter_today_logs | search_logs \"$search_keyword\""
    elif [ "$error_only" = "true" ]; then
        filter_cmd="filter_error_logs"
    elif [ "$today_only" = "true" ]; then
        filter_cmd="filter_today_logs"
    elif [ -n "$search_keyword" ]; then
        filter_cmd="search_logs \"$search_keyword\""
    fi
    
    # 执行日志查看命令
    if [ -n "$filter_cmd" ]; then
        if [ "$follow" = "true" ]; then
            print_info "实时跟踪 $service_name 日志 (带过滤)，按 Ctrl+C 退出..."
            eval "$cmd \"$log_file\" | $filter_cmd"
        else
            eval "$cmd \"$log_file\" | $filter_cmd"
        fi
    else
        if [ "$follow" = "true" ]; then
            print_info "实时跟踪 $service_name 日志，按 Ctrl+C 退出..."
        fi
        eval "$cmd \"$log_file\""
    fi
}

# 查看所有服务日志
view_all_logs() {
    local lines=$1
    local follow=$2
    local error_only=$3
    local today_only=$4
    local search_keyword=$5
    
    if [ "$follow" = "true" ]; then
        print_header "实时跟踪所有服务日志"
        print_info "按 Ctrl+C 退出..."
        echo ""
        
        # 使用multitail实时监控多个日志文件
        if command -v multitail &> /dev/null; then
            local cmd="multitail"
            for service in gateway auth application; do
                local log_file="${LOG_FILES[$service]}"
                if [ -f "$log_file" ]; then
                    cmd="$cmd -l \"$service: $log_file\""
                fi
            done
            eval "$cmd"
        else
            # 回退到tail -f，同时监控多个文件
            local log_files=""
            for service in gateway auth application; do
                local log_file="${LOG_FILES[$service]}"
                if [ -f "$log_file" ]; then
                    log_files="$log_files $log_file"
                fi
            done
            if [ -n "$log_files" ]; then
                tail -f $log_files
            else
                print_error "没有找到任何日志文件"
                return 1
            fi
        fi
    else
        # 逐个显示各服务日志
        for service in gateway auth application; do
            local log_file="${LOG_FILES[$service]}"
            if [ -f "$log_file" ]; then
                view_single_log "$service" "$log_file" "$lines" false "$error_only" "$today_only" "$search_keyword" false
                echo ""
                echo "----------------------------------------"
                echo ""
            fi
        done
    fi
}

# 主函数
main() {
    local service_name=""
    local lines=50
    local follow=false
    local error_only=false
    local today_only=false
    local search_keyword=""
    local clear_log=false
    
    # 解析参数
    while [[ $# -gt 0 ]]; do
        case $1 in
            gateway|gw|auth|app|application|all)
                service_name=$1
                shift
                ;;
            -n)
                if [[ $2 =~ ^[0-9]+$ ]]; then
                    lines=$2
                    shift 2
                else
                    print_error "参数 -n 需要一个数字"
                    exit 1
                fi
                ;;
            -f|--follow)
                follow=true
                shift
                ;;
            -e|--error)
                error_only=true
                shift
                ;;
            -t|--today)
                today_only=true
                shift
                ;;
            -s|--search)
                if [ -n "$2" ]; then
                    search_keyword=$2
                    shift 2
                else
                    print_error "参数 -s/--search 需要一个关键词"
                    exit 1
                fi
                ;;
            --clear)
                clear_log=true
                shift
                ;;
            -h|--help)
                show_usage
                exit 0
                ;;
            *)
                print_error "未知参数: $1"
                show_usage
                exit 1
                ;;
        esac
    done
    
    # 检查服务名是否提供
    if [ -z "$service_name" ]; then
        print_error "请指定服务名"
        show_usage
        exit 1
    fi
    
    # 处理服务名
    if [ "$service_name" = "all" ]; then
        if [ "$clear_log" = "true" ]; then
            print_error "不支持同时清空所有日志文件，请分别清空"
            exit 1
        fi
        view_all_logs "$lines" "$follow" "$error_only" "$today_only" "$search_keyword"
    else
        # 检查服务名是否有效
        local log_file=$(get_log_file "$service_name")
        if [ -z "$log_file" ]; then
            print_error "未知的服务名: $service_name"
            print_info "支持的服务名: gateway, auth, app, all"
            exit 1
        fi
        
        view_single_log "$service_name" "$log_file" "$lines" "$follow" "$error_only" "$today_only" "$search_keyword" "$clear_log"
    fi
}

# 执行主函数
main "$@" 