@echo off
setlocal enabledelayedexpansion
REM Collide 项目网络初始化脚本 (Windows版本)
REM 创建自定义Docker网络供中间件和业务服务使用

set NETWORK_NAME=collide-network
set SUBNET=172.20.0.0/16
set GATEWAY=172.20.0.1

REM MySQL 连接配置
set MYSQL_HOST=172.20.1.10
set MYSQL_PORT=3306
set MYSQL_ROOT_PASSWORD=123456
set MYSQL_USER=test_user
set MYSQL_PASSWORD=test123

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

REM 询问是否进行数据库初始化
echo.
set /p INIT_DB="🤔 是否需要初始化MySQL数据库？(y/n): "
if /I "%INIT_DB%"=="y" (
    call :init_mysql_database
) else (
    echo 跳过数据库初始化
)

echo.
echo 🎯 部署完成！可以开始使用Collide项目了！
echo.
pause
exit /b 0

:init_mysql_database
    echo.
    echo 🗄️ 开始MySQL数据库初始化...
    
    REM 检查MySQL容器是否运行
    docker ps | findstr "mysql" >nul 2>&1
    if %errorlevel% neq 0 (
        echo ⚠️ MySQL容器未运行，跳过数据库初始化
        exit /b 1
    )
    
    REM 等待MySQL启动完成
    echo ⏳ 等待MySQL服务启动完成...
    set /a counter=0
    :wait_mysql
    set /a counter+=1
    docker exec mysql mysqladmin ping -h localhost -u root -p%MYSQL_ROOT_PASSWORD% --silent >nul 2>&1
    if %errorlevel% equ 0 (
        echo ✅ MySQL服务已启动！
        goto mysql_ready
    )
    echo 等待MySQL启动... (%counter%/60)
    timeout /t 2 /nobreak >nul
    if %counter% lss 60 goto wait_mysql
    
    echo ❌ MySQL启动超时，跳过数据库初始化
    exit /b 1
    
    :mysql_ready
    REM 创建collide数据库
    echo 📦 创建数据库...
    docker exec mysql mysql -h localhost -u root -p%MYSQL_ROOT_PASSWORD% -e "CREATE DATABASE IF NOT EXISTS collide_auth CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci; CREATE DATABASE IF NOT EXISTS collide_business CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci; CREATE DATABASE IF NOT EXISTS nacos_mysql CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci; GRANT ALL PRIVILEGES ON collide_auth.* TO '%MYSQL_USER%'@'%%'; GRANT ALL PRIVILEGES ON collide_business.* TO '%MYSQL_USER%'@'%%'; GRANT ALL PRIVILEGES ON nacos_mysql.* TO '%MYSQL_USER%'@'%%'; FLUSH PRIVILEGES;"
    
    if %errorlevel% equ 0 (
        echo ✅ 数据库创建成功！
    ) else (
        echo ❌ 数据库创建失败！
        exit /b 1
    )
    
    REM 导入SQL文件
    echo 📥 开始导入SQL文件...
    
    REM SQL文件列表（按导入顺序）
    set SQL_FILES=01-init-database.sql nacos-mysql.sql 02-auth-tables.sql 02-user-profile-table.sql collide-business.sql 03-fix-status-field.sql 04-performance-optimization.sql 06-search-tables.sql 07-category-tag-system.sql
    
    for %%f in (%SQL_FILES%) do (
        if exist "sql\%%f" (
            echo 📄 导入 %%f...
            
            REM 根据文件名选择数据库
            set target_db=collide_business
            if "%%f"=="nacos-mysql.sql" set target_db=nacos_mysql
            if "%%f"=="02-auth-tables.sql" set target_db=collide_auth
            
            REM 复制SQL文件到容器
            docker cp "sql\%%f" mysql:/tmp/%%f
            
            REM 执行SQL文件
            docker exec mysql sh -c "mysql -h localhost -u root -p%MYSQL_ROOT_PASSWORD% !target_db! < /tmp/%%f"
            
            if !errorlevel! equ 0 (
                echo ✅ %%f 导入成功！
                REM 清理临时文件
                docker exec mysql rm -f /tmp/%%f
            ) else (
                echo ⚠️ %%f 导入失败，继续处理下一个文件
            )
        ) else (
            echo ⚠️ 文件 sql\%%f 不存在，跳过
        )
    )
    
    echo 🎉 MySQL数据库初始化完成！
    exit /b 0 