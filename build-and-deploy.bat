@echo off
chcp 65001 >nul
echo.
echo ========================================
echo  🚀 Collide 应用服务构建和部署
echo ========================================
echo.

:: 设置颜色
set "GREEN=[92m"
set "RED=[91m"
set "YELLOW=[93m"
set "BLUE=[94m"
set "NC=[0m"

:: 检查 .env 文件
if not exist ".env" (
    echo %YELLOW%⚠️  未找到 .env 文件，正在创建示例配置...%NC%
    copy "env.example" ".env" >nul
    echo %GREEN%✅ 已创建 .env 文件，请检查并修改其中的中间件配置%NC%
    echo.
)

:: 提醒用户确认外部中间件
echo %YELLOW%📋 部署前检查清单：%NC%
echo   ✓ 确保 MySQL 服务已启动并可访问
echo   ✓ 确保 Redis 服务已启动并可访问  
echo   ✓ 确保 Nacos 服务已启动并可访问
echo   ✓ 检查 .env 文件中的中间件地址配置
echo.
set /p confirm="是否继续部署？(Y/N): "
if /i not "%confirm%"=="Y" (
    echo %YELLOW%❌ 用户取消部署%NC%
    pause
    exit /b 0
)

echo.
echo %BLUE%🔨 开始构建 Maven 项目...%NC%

:: 清理并构建项目
echo %BLUE%📦 执行 Maven 打包...%NC%
call mvn clean package -DskipTests -q
if %errorlevel% neq 0 (
    echo %RED%❌ Maven 构建失败！%NC%
    pause
    exit /b 1
)
echo %GREEN%✅ Maven 构建完成%NC%

:: 构建 Docker 镜像
echo.
echo %BLUE%🐳 构建 Docker 镜像...%NC%

:: 构建网关镜像
echo %BLUE%📦 构建网关服务镜像...%NC%
docker build -t collide-gateway:latest -f collide-gateway/Dockerfile .
if %errorlevel% neq 0 (
    echo %RED%❌ 网关镜像构建失败！%NC%
    pause
    exit /b 1
)

:: 构建认证服务镜像
echo %BLUE%📦 构建认证服务镜像...%NC%
docker build -t collide-auth:latest -f collide-auth/Dockerfile .
if %errorlevel% neq 0 (
    echo %RED%❌ 认证服务镜像构建失败！%NC%
    pause
    exit /b 1
)

:: 构建业务应用镜像
echo %BLUE%📦 构建业务应用镜像...%NC%
docker build -t collide-application:latest -f collide-application/Dockerfile .
if %errorlevel% neq 0 (
    echo %RED%❌ 业务应用镜像构建失败！%NC%
    pause
    exit /b 1
)

echo %GREEN%✅ 所有镜像构建完成%NC%

:: 部署应用
echo.
echo %BLUE%🚀 启动应用服务...%NC%
docker-compose down
docker-compose up -d

if %errorlevel% neq 0 (
    echo %RED%❌ 应用启动失败！%NC%
    pause
    exit /b 1
)

echo.
echo %GREEN%========================================%NC%
echo %GREEN%🎉 Collide 应用服务部署完成！%NC%
echo %GREEN%========================================%NC%
echo.
echo %BLUE%📋 服务信息：%NC%
echo   🌐 网关服务: http://localhost:9500
echo   🔐 认证服务: http://localhost:9501
echo   💼 业务服务: http://localhost:9502
echo.
echo %BLUE%📊 健康检查：%NC%
echo   curl http://localhost:9500/actuator/health
echo   curl http://localhost:9501/actuator/health
echo   curl http://localhost:9502/actuator/health
echo.
echo %YELLOW%💡 提示：%NC%
echo   - 使用 docker-compose logs [service] 查看日志
echo   - 使用 docker-compose ps 查看服务状态
echo   - 使用 ./test-deployment.bat 进行健康检查
echo.
pause 