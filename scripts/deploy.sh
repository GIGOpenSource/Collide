#!/bin/bash

# Collide 项目一键部署脚本
# 使用方法: ./deploy.sh [all|application|auth|gateway] [install|uninstall]

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
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

log_step() {
    echo -e "${BLUE}[STEP]${NC} $1"
}

# 检查是否是root用户
check_root() {
    if [[ $EUID -ne 0 ]]; then
        log_error "此脚本需要root权限运行"
        log_info "请使用: sudo $0 $*"
        exit 1
    fi
}

# 创建用户和组
create_user() {
    log_step "创建collide用户和组..."
    
    if ! getent group collide > /dev/null 2>&1; then
        groupadd collide
        log_info "创建collide组成功"
    else
        log_info "collide组已存在"
    fi
    
    if ! getent passwd collide > /dev/null 2>&1; then
        useradd -r -g collide -s /bin/bash -d /opt/collide collide
        log_info "创建collide用户成功"
    else
        log_info "collide用户已存在"
    fi
}

# 创建目录结构
create_directories() {
    log_step "创建目录结构..."
    
    # 创建主目录
    mkdir -p /opt/collide
    chown collide:collide /opt/collide
    
    # 创建应用内部日志目录（用于Logback配置）
    mkdir -p /opt/collide/collide-auth/logs
    mkdir -p /opt/collide/collide-application/logs
    mkdir -p /opt/collide/collide-gateway/logs
    chown -R collide:collide /opt/collide/collide-*/logs
    
    # 创建系统日志目录（用于systemd重定向）
    mkdir -p /var/log/collide-application
    mkdir -p /var/log/collide-auth
    mkdir -p /var/log/collide-gateway
    chown collide:collide /var/log/collide-*
    
    # 创建PID目录权限
    mkdir -p /var/run
    chown collide:collide /var/run
    
    log_info "目录创建完成"
}

# 复制项目文件
copy_project() {
    log_step "复制项目文件到/opt/collide..."
    
    # 复制整个项目
    cp -r "$PROJECT_ROOT"/* /opt/collide/
    chown -R collide:collide /opt/collide
    
    log_info "项目文件复制完成"
}

# 设置脚本权限
set_permissions() {
    log_step "设置脚本执行权限..."
    
    chmod +x /opt/collide/scripts/*.sh
    chown collide:collide /opt/collide/scripts/*.sh
    
    log_info "脚本权限设置完成"
}

# 安装systemd服务
install_systemd_service() {
    local service_name=$1
    log_step "安装 $service_name systemd服务..."
    
    # 复制service文件
    cp "$PROJECT_ROOT/systemd/${service_name}.service" /etc/systemd/system/
    
    # 重新加载systemd
    systemctl daemon-reload
    
    # 启用服务
    systemctl enable ${service_name}.service
    
    log_info "$service_name 服务安装完成"
}

# 卸载systemd服务
uninstall_systemd_service() {
    local service_name=$1
    log_step "卸载 $service_name systemd服务..."
    
    # 停止服务
    systemctl stop ${service_name}.service
    
    # 禁用服务
    systemctl disable ${service_name}.service
    
    # 删除service文件
    rm -f /etc/systemd/system/${service_name}.service
    
    # 重新加载systemd
    systemctl daemon-reload
    
    log_info "$service_name 服务卸载完成"
}

# 编译项目
build_project() {
    log_step "编译项目..."
    
    cd /opt/collide
    
    # 检查Maven
    if ! command -v mvn &> /dev/null; then
        log_error "Maven未安装，请先安装Maven"
        exit 1
    fi
    
    # 编译项目
    sudo -u collide mvn clean package -DskipTests
    
    if [[ $? -eq 0 ]]; then
        log_info "项目编译成功"
    else
        log_error "项目编译失败"
        exit 1
    fi
}

# 安装服务
install_service() {
    local service_name=$1
    
    create_user
    create_directories
    copy_project
    set_permissions
    build_project
    install_systemd_service $service_name
    
    log_info "$service_name 安装完成！"
    echo ""
    echo "使用方法："
    echo "  启动服务: sudo systemctl start $service_name"
    echo "  停止服务: sudo systemctl stop $service_name"
    echo "  重启服务: sudo systemctl restart $service_name"
    echo "  查看状态: sudo systemctl status $service_name"
    echo "  查看日志: sudo journalctl -u $service_name -f"
    echo ""
    echo "或使用启动脚本："
    echo "  /opt/collide/scripts/start-${service_name}.sh {start|stop|restart|status|logs}"
}

# 卸载服务
uninstall_service() {
    local service_name=$1
    
    uninstall_systemd_service $service_name
    
    log_info "$service_name 卸载完成！"
}

# 安装所有服务
install_all() {
    log_step "开始安装所有Collide服务..."
    
    create_user
    create_directories
    copy_project
    set_permissions
    build_project
    
    install_systemd_service "collide-auth"
    install_systemd_service "collide-application"
    install_systemd_service "collide-gateway"
    
    log_info "所有服务安装完成！"
    echo ""
    echo "服务列表："
    echo "  - collide-auth (端口: 9502)"
    echo "  - collide-application (端口: 9503)"
    echo "  - collide-gateway (端口: 9501)"
    echo ""
    echo "建议启动顺序："
    echo "  1. sudo systemctl start collide-auth"
    echo "  2. sudo systemctl start collide-application"
    echo "  3. sudo systemctl start collide-gateway"
}

# 卸载所有服务
uninstall_all() {
    log_step "开始卸载所有Collide服务..."
    
    uninstall_systemd_service "collide-gateway"
    uninstall_systemd_service "collide-application"
    uninstall_systemd_service "collide-auth"
    
    log_info "所有服务卸载完成！"
}

# 显示帮助信息
show_help() {
    echo "Collide 项目部署脚本"
    echo ""
    echo "使用方法:"
    echo "  $0 [服务名] [操作]"
    echo ""
    echo "服务名:"
    echo "  all           - 所有服务"
    echo "  application   - 应用服务"
    echo "  auth          - 认证服务"
    echo "  gateway       - 网关服务"
    echo ""
    echo "操作:"
    echo "  install       - 安装服务"
    echo "  uninstall     - 卸载服务"
    echo ""
    echo "示例:"
    echo "  sudo $0 all install         # 安装所有服务"
    echo "  sudo $0 auth install        # 只安装认证服务"
    echo "  sudo $0 gateway uninstall   # 卸载网关服务"
}

# 主逻辑
SERVICE_NAME=$1
OPERATION=$2

if [[ -z "$SERVICE_NAME" ]] || [[ -z "$OPERATION" ]]; then
    show_help
    exit 1
fi

check_root

case "$SERVICE_NAME" in
    "all")
        case "$OPERATION" in
            "install")
                install_all
                ;;
            "uninstall")
                uninstall_all
                ;;
            *)
                log_error "未知操作: $OPERATION"
                show_help
                exit 1
                ;;
        esac
        ;;
    "application")
        case "$OPERATION" in
            "install")
                install_service "collide-application"
                ;;
            "uninstall")
                uninstall_service "collide-application"
                ;;
            *)
                log_error "未知操作: $OPERATION"
                show_help
                exit 1
                ;;
        esac
        ;;
    "auth")
        case "$OPERATION" in
            "install")
                install_service "collide-auth"
                ;;
            "uninstall")
                uninstall_service "collide-auth"
                ;;
            *)
                log_error "未知操作: $OPERATION"
                show_help
                exit 1
                ;;
        esac
        ;;
    "gateway")
        case "$OPERATION" in
            "install")
                install_service "collide-gateway"
                ;;
            "uninstall")
                uninstall_service "collide-gateway"
                ;;
            *)
                log_error "未知操作: $OPERATION"
                show_help
                exit 1
                ;;
        esac
        ;;
    *)
        log_error "未知服务: $SERVICE_NAME"
        show_help
        exit 1
        ;;
esac

exit 0 