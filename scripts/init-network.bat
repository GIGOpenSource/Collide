@echo off
REM Collide 项目网络初始化脚本 (Windows版本)
REM 创建自定义Docker网络供中间件和业务服务使用

set NETWORK_NAME=collide-network
set SUBNET=172.20.0.0/16
set GATEWAY=172.20.0.1

echo 正在检查网络 %NETWORK_NAME% 是否存在...

REM 检查网络是否已存在
docker network ls | findstr %NETWORK_NAME% >nul 2>&1
if %errorlevel% equ 0 (
    echo 网络 %NETWORK_NAME% 已存在
    docker network inspect %NETWORK_NAME%
) else (
    echo 创建网络 %NETWORK_NAME%...
    docker network create --driver bridge --subnet=%SUBNET% --gateway=%GATEWAY% %NETWORK_NAME%
    
    if %errorlevel% equ 0 (
        echo ✅ 网络 %NETWORK_NAME% 创建成功！
        echo    子网: %SUBNET%
        echo    网关: %GATEWAY%
        echo.
        echo IP分配规划：
        echo   中间件服务: 172.20.1.x
        echo     - MySQL: 172.20.1.10
        echo     - MinIO: 172.20.1.20
        echo     - Nacos: 172.20.1.30
        echo     - Redis: 172.20.1.40
        echo     - Seata: 172.20.1.50
        echo     - Sentinel: 172.20.1.60
        echo     - RocketMQ NameServer: 172.20.1.70
        echo     - RocketMQ Broker: 172.20.1.71
        echo     - Elasticsearch: 172.20.1.80
        echo.
        echo   业务服务: 172.20.2.x
        echo     - Gateway: 172.20.2.10
        echo     - Auth: 172.20.2.20
        echo     - Application: 172.20.2.30
    ) else (
        echo ❌ 网络创建失败！
        pause
        exit /b 1
    )
)

echo.
echo 🚀 现在可以启动Docker Compose服务了：
echo    cd middleware ^&^& docker-compose up -d  # 启动中间件
echo    cd .. ^&^& docker-compose up -d          # 启动业务服务
echo.
pause 