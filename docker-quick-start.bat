@echo off
chcp 65001 >nul
echo.
echo ========================================
echo  ⚡ Collide 应用快速启动
echo ========================================
echo.

:: 设置颜色
set "GREEN=[92m"
set "RED=[91m"
set "YELLOW=[93m"
set "BLUE=[94m"
set "NC=[0m"

:: 检查 .env 配置文件
if not exist ".env" (
    echo %YELLOW%⚠️  未找到 .env 文件%NC%
    echo %BLUE%请选择环境配置：%NC%
    echo   1. 本地开发环境 (localhost)
    echo   2. 生产环境 (需要IP地址)
    echo   3. 自定义配置
    echo.
    set /p choice="请选择 (1-3): "
    
    if "!choice!"=="1" (
        copy "env.local.example" ".env" >nul
        echo %GREEN%✅ 已配置本地开发环境%NC%
    ) else if "!choice!"=="2" (
        copy "env.production.example" ".env" >nul
        echo %YELLOW%⚠️  已创建生产环境配置，请修改 .env 文件中的IP地址%NC%
        echo %YELLOW%按任意键打开配置文件...%NC%
        pause >nul
        notepad ".env"
    ) else (
        copy "env.example" ".env" >nul
        echo %YELLOW%⚠️  已创建配置模板，请修改 .env 文件%NC%
        echo %YELLOW%按任意键打开配置文件...%NC%
        pause >nul
        notepad ".env"
    )
    echo.
)

:: 外部中间件检查提醒
echo %YELLOW%📋 启动前确认：%NC%
echo   ✓ MySQL (默认端口: 3306)
echo   ✓ Redis (默认端口: 6379)
echo   ✓ Nacos (默认端口: 8848)
echo.
echo %BLUE%应用服务将使用以下端口：%NC%
echo   🌐 网关服务: 9500
echo   🔐 认证服务: 9501
echo   💼 业务服务: 9502
echo.
set /p confirm="外部中间件是否已就绪？(Y/N): "
if /i not "%confirm%"=="Y" (
    echo %YELLOW%❌ 请先启动外部中间件服务%NC%
    pause
    exit /b 0
)

:: 检查是否需要构建镜像
docker images | findstr "collide-gateway" >nul
if %errorlevel% neq 0 (
    echo %YELLOW%📦 首次运行，需要构建 Docker 镜像...%NC%
    goto :build_images
)

docker images | findstr "collide-auth" >nul
if %errorlevel% neq 0 (
    goto :build_images
)

docker images | findstr "collide-application" >nul
if %errorlevel% neq 0 (
    goto :build_images
)

echo %BLUE%🐳 Docker 镜像已存在，直接启动...%NC%
goto :start_services

:build_images
echo %BLUE%🔨 构建应用镜像...%NC%

:: Maven 打包
echo %BLUE%📦 Maven 打包中...%NC%
call mvn clean package -DskipTests -q
if %errorlevel% neq 0 (
    echo %RED%❌ Maven 构建失败！%NC%
    pause
    exit /b 1
)

:: Docker 镜像构建
echo %BLUE%🐳 构建 Docker 镜像...%NC%
docker build -t collide-gateway:latest -f collide-gateway/Dockerfile . || goto :error
docker build -t collide-auth:latest -f collide-auth/Dockerfile . || goto :error
docker build -t collide-application:latest -f collide-application/Dockerfile . || goto :error

echo %GREEN%✅ 镜像构建完成%NC%

:start_services
echo %BLUE%🚀 启动应用服务...%NC%

:: 停止现有服务
docker-compose down >nul 2>&1

:: 启动服务
docker-compose up -d
if %errorlevel% neq 0 (
    echo %RED%❌ 服务启动失败！%NC%
    echo %YELLOW%请检查日志: docker-compose logs%NC%
    pause
    exit /b 1
)

echo.
echo %GREEN%========================================%NC%
echo %GREEN%🎉 Collide 应用启动成功！%NC%
echo %GREEN%========================================%NC%
echo.
echo %BLUE%📋 服务地址：%NC%
echo   🌐 网关入口: http://localhost:9500
echo   🔐 认证服务: http://localhost:9501
echo   💼 业务服务: http://localhost:9502
echo.
echo %BLUE%📊 快捷命令：%NC%
echo   查看状态: docker-compose ps
echo   查看日志: docker-compose logs -f
echo   健康检查: ./test-deployment.bat
echo   停止服务: docker-compose down
echo.
echo %YELLOW%💡 提示：服务启动需要 30-60 秒，请稍等片刻再进行测试%NC%
echo.
pause
exit /b 0

:error
echo %RED%❌ 构建过程出错！%NC%
pause
exit /b 1 