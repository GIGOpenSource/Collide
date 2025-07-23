#!/bin/bash

# 检查Dubbo相关端口脚本
echo "=== Dubbo 端口检查工具 ==="

# 检查服务是否运行
check_service_running() {
    local service_name=$1
    local port=$2
    
    echo "检查 $service_name 服务..."
    
    # 检查端口是否被占用
    if command -v lsof >/dev/null 2>&1; then
        local pid=$(lsof -ti :$port 2>/dev/null)
        if [ ! -z "$pid" ]; then
            echo "✅ $service_name 正在运行，PID: $pid，端口: $port"
            return 0
        else
            echo "❌ $service_name 未运行或端口 $port 未被占用"
            return 1
        fi
    else
        echo "⚠️  lsof 命令不可用，尝试使用 netstat"
        if command -v netstat >/dev/null 2>&1; then
            local result=$(netstat -tulpn 2>/dev/null | grep ":$port ")
            if [ ! -z "$result" ]; then
                echo "✅ $service_name 正在运行，端口: $port"
                echo "   详情: $result"
                return 0
            else
                echo "❌ $service_name 未运行或端口 $port 未被占用"
                return 1
            fi
        fi
    fi
}

# 查找Dubbo协议端口
find_dubbo_ports() {
    echo ""
    echo "=== 查找 Dubbo RPC 端口 ==="
    
    # 查找20880端口附近的端口（Dubbo默认端口范围）
    echo "扫描 Dubbo 默认端口范围 (20880-20890):"
    for port in $(seq 20880 20890); do
        if command -v lsof >/dev/null 2>&1; then
            local result=$(lsof -ti :$port 2>/dev/null)
            if [ ! -z "$result" ]; then
                local process_info=$(ps -p $result -o comm= 2>/dev/null)
                echo "  端口 $port: 被进程 $result ($process_info) 占用"
            fi
        elif command -v netstat >/dev/null 2>&1; then
            local result=$(netstat -tulpn 2>/dev/null | grep ":$port ")
            if [ ! -z "$result" ]; then
                echo "  端口 $port: $result"
            fi
        fi
    done
    
    # 查找Java进程占用的所有端口
    echo ""
    echo "=== Java 进程占用的端口 ==="
    if command -v lsof >/dev/null 2>&1; then
        echo "Java相关进程占用的端口:"
        lsof -i -P -n | grep java | while read line; do
            echo "  $line"
        done
    elif command -v netstat >/dev/null 2>&1; then
        echo "Java相关进程占用的端口:"
        netstat -tulpn | grep java | while read line; do
            echo "  $line"
        done
    fi
}

# 检查QoS端口
check_qos_port() {
    echo ""
    echo "=== 检查 Dubbo QoS 端口 ==="
    
    local qos_ports=(22222 22223 22224)
    
    for port in "${qos_ports[@]}"; do
        if command -v lsof >/dev/null 2>&1; then
            local result=$(lsof -ti :$port 2>/dev/null)
            if [ ! -z "$result" ]; then
                local process_info=$(ps -p $result -o comm= 2>/dev/null)
                echo "✅ QoS端口 $port: 被进程 $result ($process_info) 占用"
                
                # 尝试访问QoS接口
                echo "  尝试访问 QoS 接口:"
                if command -v curl >/dev/null 2>&1; then
                    echo "    服务列表: curl http://localhost:$port/ls"
                    echo "    帮助信息: curl http://localhost:$port/help"
                fi
            fi
        fi
    done
}

# 显示Dubbo配置信息
show_dubbo_config() {
    echo ""
    echo "=== Dubbo 配置参考 ==="
    echo "常见的 Dubbo 端口配置:"
    echo "  协议端口 (dubbo.protocol.port):"
    echo "    默认值: 20880"
    echo "    自动分配: -1"
    echo "    范围: 20880-20890"
    echo ""
    echo "  QoS端口 (dubbo.application.qos-port):"
    echo "    默认值: 22222"
    echo "    您的配置: 22223"
    echo ""
    echo "  应用端口 (server.port):"
    echo "    Gateway: 9501"
    echo "    Auth: 9502"
    echo "    Application: 9503"
}

# 主执行逻辑
main() {
    # 检查应用服务端口
    check_service_running "Gateway" "9501"
    check_service_running "Auth" "9502"
    check_service_running "Application" "9503"
    
    # 查找Dubbo端口
    find_dubbo_ports
    
    # 检查QoS端口
    check_qos_port
    
    # 显示配置信息
    show_dubbo_config
    
    echo ""
    echo "=== 检查完成 ==="
    echo "提示: 如果需要查看更详细的端口信息，可以使用:"
    echo "  lsof -i :端口号"
    echo "  netstat -tulpn | grep 端口号"
    echo "  ss -tulpn | grep 端口号"
}

# 执行主函数
main 