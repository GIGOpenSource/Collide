#!/bin/bash

# Collide 项目快速开始脚本
# 适用于 Ubuntu 22.04 环境

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

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

log_title() {
    echo -e "${CYAN}$1${NC}"
}

# 显示欢迎信息
show_welcome() {
    clear
    log_title "=================================================="
    log_title "     欢迎使用 Collide 项目快速开始脚本"
    log_title "          适用于 Ubuntu 22.04 环境"
    log_title "=================================================="
    echo ""
    echo "此脚本将帮助您："
    echo "  1. 检查系统环境"
    echo "  2. 设置脚本权限"
    echo "  3. 验证项目结构"
    echo "  4. 提供部署选项"
    echo ""
    read -p "按回车键继续..." -r
    echo ""
}

# 检查系统信息
check_system_info() {
    log_step "检查系统信息..."
    
    # 检查操作系统
    if [[ -f /etc/os-release ]]; then
        source /etc/os-release
        echo "操作系统: $PRETTY_NAME"
        
        if [[ "$ID" != "ubuntu" ]]; then
            log_warn "当前系统不是Ubuntu，脚本主要针对Ubuntu 22.04优化"
        elif [[ "$VERSION_ID" != "22.04" ]]; then
            log_warn "当前系统不是Ubuntu 22.04，可能存在兼容性问题"
        else
            log_info "✅ 系统环境符合要求 (Ubuntu 22.04)"
        fi
    else
        log_warn "无法检测操作系统版本"
    fi
    
    echo "内核版本: $(uname -r)"
    echo "架构: $(uname -m)"
    echo ""
}

# 检查必要工具
check_required_tools() {
    log_step "检查系统工具..."
    
    local tools=("java" "mvn" "curl" "systemctl" "netstat")
    local missing_tools=()
    
    for tool in "${tools[@]}"; do
        if command -v "$tool" &> /dev/null; then
            local version=""
            case "$tool" in
                "java")
                    version=$(java -version 2>&1 | head -n 1 | awk -F '"' '{print $2}')
                    ;;
                "mvn")
                    version=$(mvn -version 2>&1 | head -n 1 | awk '{print $3}')
                    ;;
                "curl")
                    version=$(curl --version 2>&1 | head -n 1 | awk '{print $2}')
                    ;;
            esac
            log_info "✅ $tool ${version:+($version)}"
        else
            log_error "❌ $tool 未安装"
            missing_tools+=("$tool")
        fi
    done
    
    if [[ ${#missing_tools[@]} -gt 0 ]]; then
        echo ""
        log_error "缺少必要工具，请先安装："
        for tool in "${missing_tools[@]}"; do
            case "$tool" in
                "java")
                    echo "  sudo apt install openjdk-21-jdk -y"
                    ;;
                "mvn")
                    echo "  sudo apt install maven -y"
                    ;;
                "curl"|"netstat")
                    echo "  sudo apt install curl net-tools -y"
                    ;;
                *)
                    echo "  请安装 $tool"
                    ;;
            esac
        done
        echo ""
        return 1
    fi
    
    echo ""
    return 0
}

# 设置脚本权限
setup_permissions() {
    log_step "设置脚本执行权限..."
    
    local script_files=(
        "$SCRIPT_DIR/start-collide-auth.sh"
        "$SCRIPT_DIR/start-collide-application.sh"
        "$SCRIPT_DIR/start-collide-gateway.sh"
        "$SCRIPT_DIR/deploy.sh"
        "$SCRIPT_DIR/health-check.sh"
        "$SCRIPT_DIR/quick-start.sh"
    )
    
    for script in "${script_files[@]}"; do
        if [[ -f "$script" ]]; then
            chmod +x "$script"
            log_info "✅ 设置 $(basename "$script") 执行权限"
        else
            log_warn "⚠️  脚本文件不存在: $(basename "$script")"
        fi
    done
    echo ""
}

# 验证项目结构
verify_project_structure() {
    log_step "验证项目结构..."
    
    local required_dirs=(
        "collide-auth"
        "collide-application"
        "collide-gateway"
        "scripts"
        "systemd"
    )
    
    local missing_dirs=()
    
    for dir in "${required_dirs[@]}"; do
        if [[ -d "$PROJECT_ROOT/$dir" ]]; then
            log_info "✅ $dir/ 目录存在"
        else
            log_error "❌ $dir/ 目录不存在"
            missing_dirs+=("$dir")
        fi
    done
    
    # 检查关键文件
    local required_files=(
        "pom.xml"
        "systemd/collide-auth.service"
        "systemd/collide-application.service"
        "systemd/collide-gateway.service"
        "DEPLOYMENT.md"
    )
    
    for file in "${required_files[@]}"; do
        if [[ -f "$PROJECT_ROOT/$file" ]]; then
            log_info "✅ $file 文件存在"
        else
            log_error "❌ $file 文件不存在"
        fi
    done
    
    echo ""
    
    if [[ ${#missing_dirs[@]} -gt 0 ]]; then
        log_error "项目结构不完整，请确保所有必要的目录和文件都存在"
        return 1
    fi
    
    return 0
}

# 编译项目
compile_project() {
    log_step "编译项目..."
    
    cd "$PROJECT_ROOT"
    
    if [[ -f "pom.xml" ]]; then
        log_info "开始编译项目，这可能需要几分钟..."
        
        if mvn clean package -DskipTests; then
            log_info "✅ 项目编译成功"
            
            # 检查生成的JAR文件
            local jar_files=(
                "collide-auth/target/collide-auth-*.jar"
                "collide-application/collide-app/target/collide-app-*.jar"
                "collide-gateway/target/collide-gateway-*.jar"
            )
            
            for jar_pattern in "${jar_files[@]}"; do
                if ls $jar_pattern 1> /dev/null 2>&1; then
                    local jar_file=$(ls $jar_pattern | head -n 1)
                    local jar_size=$(du -h "$jar_file" | cut -f1)
                    log_info "✅ $(basename "$jar_file") (大小: $jar_size)"
                else
                    log_warn "⚠️  JAR文件未生成: $jar_pattern"
                fi
            done
        else
            log_error "❌ 项目编译失败"
            return 1
        fi
    else
        log_error "❌ 未找到 pom.xml 文件"
        return 1
    fi
    
    echo ""
    return 0
}

# 显示部署选项
show_deployment_options() {
    log_title "=================================================="
    log_title "               部署选项"
    log_title "=================================================="
    echo ""
    echo "选择部署方式："
    echo ""
    echo "  1) 快速部署 - 一键安装所有服务到系统"
    echo "  2) 手动启动 - 使用启动脚本手动管理服务"
    echo "  3) 健康检查 - 检查已部署的服务状态"
    echo "  4) 查看文档 - 显示详细部署文档"
    echo "  5) 退出"
    echo ""
    
    while true; do
        read -p "请选择 (1-5): " choice
        case $choice in
            1)
                deploy_all_services
                break
                ;;
            2)
                manual_start_guide
                break
                ;;
            3)
                run_health_check
                break
                ;;
            4)
                show_documentation
                break
                ;;
            5)
                log_info "退出快速开始脚本"
                exit 0
                ;;
            *)
                log_warn "无效选择，请输入 1-5"
                ;;
        esac
    done
}

# 一键部署所有服务
deploy_all_services() {
    log_step "准备一键部署所有服务..."
    echo ""
    log_warn "注意：此操作需要 root 权限，将会："
    echo "  - 创建系统用户 'collide'"
    echo "  - 复制文件到 /opt/collide"
    echo "  - 安装 systemd 服务"
    echo "  - 创建必要的日志目录"
    echo ""
    
    read -p "确认继续部署吗？(y/N): " -r
    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        log_info "取消部署"
        return 0
    fi
    
    log_info "开始部署，请输入sudo密码..."
    
    if sudo "$SCRIPT_DIR/deploy.sh" all install; then
        log_info "🎉 部署成功！"
        echo ""
        echo "现在可以启动服务："
        echo "  sudo systemctl start collide-auth"
        echo "  sudo systemctl start collide-application"
        echo "  sudo systemctl start collide-gateway"
        echo ""
        echo "或使用健康检查脚本监控服务状态："
        echo "  sudo $SCRIPT_DIR/health-check.sh"
    else
        log_error "💥 部署失败，请查看错误信息"
    fi
}

# 手动启动指南
manual_start_guide() {
    log_step "手动启动指南"
    echo ""
    echo "使用启动脚本手动管理服务："
    echo ""
    echo "1. 认证服务 (端口 9500):"
    echo "   启动: $SCRIPT_DIR/start-collide-auth.sh start"
    echo "   状态: $SCRIPT_DIR/start-collide-auth.sh status"
    echo "   日志: $SCRIPT_DIR/start-collide-auth.sh logs"
    echo ""
    echo "2. 应用服务 (端口 8085):"
    echo "   启动: $SCRIPT_DIR/start-collide-application.sh start"
    echo "   状态: $SCRIPT_DIR/start-collide-application.sh status"
    echo "   日志: $SCRIPT_DIR/start-collide-application.sh logs"
    echo ""
    echo "3. 网关服务 (端口 8081):"
    echo "   启动: $SCRIPT_DIR/start-collide-gateway.sh start"
    echo "   状态: $SCRIPT_DIR/start-collide-gateway.sh status"
    echo "   日志: $SCRIPT_DIR/start-collide-gateway.sh logs"
    echo ""
    echo "推荐启动顺序: 认证服务 -> 应用服务 -> 网关服务"
    echo ""
    
    read -p "是否现在启动认证服务？(y/N): " -r
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        log_info "启动认证服务..."
        "$SCRIPT_DIR/start-collide-auth.sh" start
    fi
}

# 运行健康检查
run_health_check() {
    log_step "运行健康检查..."
    echo ""
    
    if [[ -f "$SCRIPT_DIR/health-check.sh" ]]; then
        "$SCRIPT_DIR/health-check.sh"
    else
        log_error "健康检查脚本不存在"
    fi
}

# 显示文档
show_documentation() {
    log_step "显示部署文档..."
    echo ""
    
    if [[ -f "$PROJECT_ROOT/DEPLOYMENT.md" ]]; then
        if command -v less &> /dev/null; then
            less "$PROJECT_ROOT/DEPLOYMENT.md"
        else
            cat "$PROJECT_ROOT/DEPLOYMENT.md"
        fi
    else
        log_error "部署文档不存在: DEPLOYMENT.md"
    fi
}

# 主流程
main() {
    show_welcome
    
    if ! check_system_info; then
        exit 1
    fi
    
    if ! check_required_tools; then
        log_error "请先安装缺少的工具，然后重新运行此脚本"
        exit 1
    fi
    
    setup_permissions
    
    if ! verify_project_structure; then
        log_error "项目结构验证失败"
        exit 1
    fi
    
    log_info "✅ 环境检查完成，所有前置条件满足"
    echo ""
    
    read -p "是否需要编译项目？(Y/n): " -r
    if [[ ! $REPLY =~ ^[Nn]$ ]]; then
        if ! compile_project; then
            log_error "项目编译失败，请检查错误信息"
            exit 1
        fi
    fi
    
    show_deployment_options
}

# 如果不是被source调用，则执行主流程
if [[ "${BASH_SOURCE[0]}" == "${0}" ]]; then
    main "$@"
fi 