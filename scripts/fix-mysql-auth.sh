#!/bin/bash

# ==========================================
# MySQL 认证插件修复脚本
# ==========================================

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

# 修复 MySQL 认证配置
fix_mysql_auth() {
    print_info "开始修复 MySQL 认证插件问题..."
    
    # 检查 MySQL 容器
    MYSQL_CONTAINER=$(docker ps --filter "name=mysql" --format "{{.Names}}" | head -1)
    if [ -z "$MYSQL_CONTAINER" ]; then
        print_error "未找到 MySQL 容器"
        return 1
    fi
    
    print_info "找到 MySQL 容器: $MYSQL_CONTAINER"
    
    # 检查 MySQL 版本和认证插件
    print_info "检查 MySQL 认证插件配置..."
    docker exec "$MYSQL_CONTAINER" mysql -u root -p123456 -e "
    SELECT user, host, plugin 
    FROM mysql.user 
    WHERE user IN ('root', 'collide', 'test_user');"
    
    # 修改用户认证插件为 mysql_native_password
    print_info "修改用户认证插件为 mysql_native_password..."
    docker exec "$MYSQL_CONTAINER" mysql -u root -p123456 -e "
    ALTER USER 'root'@'%' IDENTIFIED WITH mysql_native_password BY '123456';
    ALTER USER 'collide'@'%' IDENTIFIED WITH mysql_native_password BY '123456';
    ALTER USER 'test_user'@'%' IDENTIFIED WITH mysql_native_password BY 'test123';
    FLUSH PRIVILEGES;"
    
    if [ $? -eq 0 ]; then
        print_success "MySQL 认证插件修改成功"
    else
        print_warning "MySQL 认证插件修改可能失败，但继续执行"
    fi
    
    # 验证修改结果
    print_info "验证认证插件修改结果..."
    docker exec "$MYSQL_CONTAINER" mysql -u root -p123456 -e "
    SELECT user, host, plugin 
    FROM mysql.user 
    WHERE user IN ('root', 'collide', 'test_user');"
}

# 重启服务
restart_services() {
    print_info "重启 collide-application 服务..."
    
    # 检查 Docker 是否运行
    if ! docker info > /dev/null 2>&1; then
        print_error "Docker 未运行，请先启动 Docker"
        return 1
    fi
    
    # 重启服务
    if docker-compose restart collide-application; then
        print_success "collide-application 服务重启成功"
    else
        print_error "collide-application 服务重启失败"
        return 1
    fi
}

# 检查服务状态
check_service_status() {
    print_info "检查服务状态..."
    
    # 等待服务启动
    sleep 15
    
    # 检查服务日志
    if docker-compose logs --tail=20 collide-application | grep -q "Started CollideBusinessApplication"; then
        print_success "collide-application 服务启动成功！"
        
        # 检查数据库连接
        print_info "测试数据库连接..."
        if docker-compose logs --tail=50 collide-application | grep -q "HikariPool-1 - Starting"; then
            print_success "数据库连接池启动成功！"
        else
            print_warning "数据库连接池状态未知"
        fi
        return 0
    else
        print_error "collide-application 服务启动失败，请检查日志"
        docker-compose logs --tail=50 collide-application
        return 1
    fi
}

# 测试数据库连接
test_database_connection() {
    print_info "测试数据库连接..."
    
    # 使用 MySQL 客户端测试连接
    MYSQL_CONTAINER=$(docker ps --filter "name=mysql" --format "{{.Names}}" | head -1)
    if [ -n "$MYSQL_CONTAINER" ]; then
        if docker exec "$MYSQL_CONTAINER" mysql -u root -p123456 -e "SELECT 1;" > /dev/null 2>&1; then
            print_success "数据库连接测试成功"
            return 0
        else
            print_error "数据库连接测试失败"
            return 1
        fi
    else
        print_error "未找到 MySQL 容器"
        return 1
    fi
}

# 主函数
main() {
    print_info "开始修复 MySQL 认证插件问题..."
    
    # 测试数据库连接
    if ! test_database_connection; then
        print_error "数据库连接失败，请检查 MySQL 服务状态"
        exit 1
    fi
    
    # 修复 MySQL 认证配置
    fix_mysql_auth
    
    # 重启服务
    if restart_services; then
        # 检查服务状态
        check_service_status
    else
        print_error "服务重启失败"
        exit 1
    fi
}

# 执行主函数
main "$@" 