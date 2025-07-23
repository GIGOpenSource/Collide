#!/bin/bash

# Collide Application 启动脚本
# 使用方法: ./start-collide-application.sh [start|stop|restart|status]

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"
APP_NAME="collide-application"
JAR_PATH="$PROJECT_ROOT/collide-application/collide-app/target"
JAR_FILE="$(find "$JAR_PATH" -name "collide-app-*.jar" -type f | head -n 1)"
MAIN_CLASS="com.gig.collide.CollideBusinessApplication"
JAVA_OPTS="-Xms512m -Xmx1024m -Dspring.profiles.active=prod"
APP_PORT=9503
PID_FILE="/var/run/${APP_NAME}.pid"
LOG_FILE="/var/log/${APP_NAME}/${APP_NAME}.log"
LOG_DIR="$(dirname "$LOG_FILE")"

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 日志函数
log_info() {
    echo -e "${GREEN}[INFO]${NC} $(date '+%Y-%m-%d %H:%M:%S') $1"
}

log_warn() {
    echo -e "${YELLOW}[WARN]${NC} $(date '+%Y-%m-%d %H:%M:%S') $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $(date '+%Y-%m-%d %H:%M:%S') $1"
}

# 检查Java环境
check_java() {
    if ! command -v java &> /dev/null; then
        log_error "Java未安装或未在PATH中找到"
        exit 1
    fi
    
    JAVA_VERSION=$(java -version 2>&1 | head -n 1 | awk -F '"' '{print $2}')
    log_info "检测到Java版本: $JAVA_VERSION"
}

# 检查JAR文件
check_jar() {
    if [[ ! -f "$JAR_FILE" ]]; then
        log_error "JAR文件不存在: $JAR_FILE"
        log_info "请先执行 'mvn clean package' 编译项目"
        exit 1
    fi
    log_info "找到JAR文件: $JAR_FILE"
}

# 创建必要的目录
create_directories() {
    if [[ ! -d "$LOG_DIR" ]]; then
        sudo mkdir -p "$LOG_DIR"
        sudo chown $(whoami):$(whoami) "$LOG_DIR"
        log_info "创建日志目录: $LOG_DIR"
    fi
}

# 检查进程是否运行
is_running() {
    if [[ -f "$PID_FILE" ]]; then
        local pid=$(cat "$PID_FILE")
        if ps -p $pid > /dev/null 2>&1; then
            return 0
        else
            rm -f "$PID_FILE"
            return 1
        fi
    fi
    return 1
}

# 启动应用
start() {
    log_info "启动 $APP_NAME..."
    
    if is_running; then
        log_warn "$APP_NAME 已经在运行中"
        return 1
    fi
    
    check_java
    check_jar
    create_directories
    
    log_info "启动命令: java $JAVA_OPTS -jar $JAR_FILE"
    
    # 启动应用
    nohup java $JAVA_OPTS -jar "$JAR_FILE" > "$LOG_FILE" 2>&1 &
    local pid=$!
    
    # 保存PID
    echo $pid > "$PID_FILE"
    
    # 等待启动
    log_info "等待应用启动..."
    local count=0
    while [[ $count -lt 30 ]]; do
        if curl -s "http://localhost:$APP_PORT/actuator/health" > /dev/null 2>&1; then
            log_info "$APP_NAME 启动成功! PID: $pid, 端口: $APP_PORT"
            return 0
        fi
        sleep 2
        count=$((count + 1))
    done
    
    log_error "$APP_NAME 启动超时"
    return 1
}

# 停止应用
stop() {
    log_info "停止 $APP_NAME..."
    
    if ! is_running; then
        log_warn "$APP_NAME 未运行"
        return 1
    fi
    
    local pid=$(cat "$PID_FILE")
    log_info "正在停止进程 PID: $pid"
    
    # 优雅停止
    kill $pid
    
    # 等待进程结束
    local count=0
    while [[ $count -lt 15 ]] && ps -p $pid > /dev/null 2>&1; do
        sleep 1
        count=$((count + 1))
    done
    
    # 强制杀死
    if ps -p $pid > /dev/null 2>&1; then
        log_warn "优雅停止失败，强制终止进程"
        kill -9 $pid
    fi
    
    rm -f "$PID_FILE"
    log_info "$APP_NAME 已停止"
}

# 重启应用
restart() {
    log_info "重启 $APP_NAME..."
    stop
    sleep 3
    start
}

# 查看状态
status() {
    if is_running; then
        local pid=$(cat "$PID_FILE")
        log_info "$APP_NAME 正在运行 (PID: $pid, 端口: $APP_PORT)"
        
        # 显示内存使用情况
        local mem_info=$(ps -p $pid -o pid,ppid,cmd,pmem,rss --no-headers)
        echo "进程信息: $mem_info"
        
        # 检查端口
        if netstat -tlnp 2>/dev/null | grep ":$APP_PORT " > /dev/null; then
            log_info "端口 $APP_PORT 正在监听"
        else
            log_warn "端口 $APP_PORT 未监听"
        fi
        
        # 检查健康状态
        if curl -s "http://localhost:$APP_PORT/actuator/health" > /dev/null 2>&1; then
            log_info "健康检查通过"
        else
            log_warn "健康检查失败"
        fi
    else
        log_info "$APP_NAME 未运行"
    fi
}

# 查看日志
logs() {
    if [[ -f "$LOG_FILE" ]]; then
        tail -f "$LOG_FILE"
    else
        log_error "日志文件不存在: $LOG_FILE"
    fi
}

# 主逻辑
case "$1" in
    start)
        start
        ;;
    stop)
        stop
        ;;
    restart)
        restart
        ;;
    status)
        status
        ;;
    logs)
        logs
        ;;
    *)
        echo "使用方法: $0 {start|stop|restart|status|logs}"
        echo ""
        echo "命令说明:"
        echo "  start   - 启动应用"
        echo "  stop    - 停止应用"
        echo "  restart - 重启应用"
        echo "  status  - 查看应用状态"
        echo "  logs    - 查看实时日志"
        exit 1
        ;;
esac

exit $? 