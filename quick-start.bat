@echo off
chcp 65001 >nul
setlocal EnableDelayedExpansion

:: ==========================================
:: Collide 应用服务快速启动脚本 (Windows版)
:: ==========================================

:: 设置颜色
set "RED=[91m"
set "GREEN=[92m"
set "YELLOW=[93m"
set "BLUE=[94m"
set "NC=[0m"

:: 日志函数
:log_info
echo %BLUE%[INFO]%NC% %~1
goto :eof

:log_success
echo %GREEN%[SUCCESS]%NC% %~1
goto :eof

:log_warning
echo %YELLOW%[WARNING]%NC% %~1
goto :eof

:log_error
echo %RED%[ERROR]%NC% %~1
goto :eof

:: 检查系统要求
:check_requirements
call :log_info "检查系统要求..."

docker --version >nul 2>&1
if %errorlevel% neq 0 (
    call :log_error "Docker 未安装或未添加到PATH，请先安装 Docker Desktop"
    exit /b 1
)

docker-compose --version >nul 2>&1
if %errorlevel% neq 0 (
    call :log_error "Docker Compose 未安装，请先安装 Docker Compose"
    exit /b 1
)

docker info >nul 2>&1
if %errorlevel% neq 0 (
    call :log_error "Docker 服务未启动，请启动 Docker Desktop"
    exit /b 1
)

call :log_success "系统要求检查通过"
goto :eof

:: 检查端口占用
:check_ports
call :log_info "检查应用服务端口占用情况..."

set "PORTS=8081 8082 8085"
set "OCCUPIED_PORTS="

for %%p in (%PORTS%) do (
    netstat -an | findstr ":%%p " >nul 2>&1
    if !errorlevel! equ 0 (
        set "OCCUPIED_PORTS=!OCCUPIED_PORTS! %%p"
    )
)

if defined OCCUPIED_PORTS (
    call :log_warning "以下端口已被占用:!OCCUPIED_PORTS!"
    call :log_warning "请确保这些端口可用，或停止占用这些端口的服务"
    set /p "REPLY=是否继续部署? (y/N): "
    if /i not "!REPLY!"=="y" exit /b 1
) else (
    call :log_success "端口检查通过"
)
goto :eof

:: 检查中间件连通性
:check_middleware
call :log_info "检查中间件连通性..."

:: 检查MySQL
netstat -an | findstr "127.0.0.1:3306" >nul 2>&1
if %errorlevel% neq 0 (
    call :log_warning "MySQL (3306) 连接失败，请确保MySQL服务已启动"
) else (
    call :log_success "MySQL 连接正常"
)

:: 检查Redis
netstat -an | findstr "127.0.0.1:6379" >nul 2>&1
if %errorlevel% neq 0 (
    call :log_warning "Redis (6379) 连接失败，请确保Redis服务已启动"
) else (
    call :log_success "Redis 连接正常"
)

:: 检查Nacos
netstat -an | findstr "127.0.0.1:8848" >nul 2>&1
if %errorlevel% neq 0 (
    call :log_warning "Nacos (8848) 连接失败，请确保Nacos服务已启动"
) else (
    call :log_success "Nacos 连接正常"
)

:: 检查RocketMQ
netstat -an | findstr "127.0.0.1:9876" >nul 2>&1
if %errorlevel% neq 0 (
    call :log_warning "RocketMQ (9876) 连接失败，请确保RocketMQ服务已启动"
) else (
    call :log_success "RocketMQ 连接正常"
)
goto :eof

:: 启动应用服务
:start_applications
call :log_info "启动应用服务..."

:: 启动认证服务
call :log_info "启动认证服务..."
docker-compose up -d collide-auth
if %errorlevel% neq 0 (
    call :log_error "认证服务启动失败"
    exit /b 1
)

:: 等待认证服务启动
call :log_info "等待认证服务启动..."
set /a count=0
:wait_auth
curl -s http://localhost:8082/actuator/health | findstr "UP" >nul 2>&1
if %errorlevel% equ 0 (
    call :log_success "认证服务启动成功"
    goto :auth_ready
)
set /a count+=1
if %count% geq 30 (
    call :log_error "认证服务启动超时，请检查日志: docker-compose logs collide-auth"
    exit /b 1
)
timeout /t 3 /nobreak >nul
goto :wait_auth
:auth_ready

:: 启动应用服务
call :log_info "启动应用服务..."
docker-compose up -d collide-application
if %errorlevel% neq 0 (
    call :log_error "应用服务启动失败"
    exit /b 1
)

:: 等待应用服务启动
call :log_info "等待应用服务启动..."
set /a count=0
:wait_app
curl -s http://localhost:8085/actuator/health | findstr "UP" >nul 2>&1
if %errorlevel% equ 0 (
    call :log_success "应用服务启动成功"
    goto :app_ready
)
set /a count+=1
if %count% geq 60 (
    call :log_error "应用服务启动超时，请检查日志: docker-compose logs collide-application"
    exit /b 1
)
timeout /t 3 /nobreak >nul
goto :wait_app
:app_ready

:: 启动网关服务
call :log_info "启动网关服务..."
docker-compose up -d collide-gateway
if %errorlevel% neq 0 (
    call :log_error "网关服务启动失败"
    exit /b 1
)

:: 等待网关服务启动
call :log_info "等待网关服务启动..."
set /a count=0
:wait_gateway
curl -s http://localhost:8081/actuator/health | findstr "UP" >nul 2>&1
if %errorlevel% equ 0 (
    call :log_success "网关服务启动成功"
    goto :gateway_ready
)
set /a count+=1
if %count% geq 30 (
    call :log_error "网关服务启动超时，请检查日志: docker-compose logs collide-gateway"
    exit /b 1
)
timeout /t 3 /nobreak >nul
goto :wait_gateway
:gateway_ready

call :log_success "所有应用服务启动完成"
goto :eof

:: 显示服务状态
:show_status
call :log_info "服务状态:"
docker-compose ps

echo.
call :log_info "应用服务访问地址:"
echo 🌐 网关服务:        http://localhost:8081
echo 🔐 认证服务:        http://localhost:8082
echo 🚀 应用服务:        http://localhost:8085

echo.
call :log_info "健康检查:"
echo 🔍 网关健康检查:    curl http://localhost:8081/actuator/health
echo 🔍 认证健康检查:    curl http://localhost:8082/actuator/health
echo 🔍 应用健康检查:    curl http://localhost:8085/actuator/health

echo.
call :log_success "Collide 应用服务部署完成！"
goto :eof

:: 清理函数
:cleanup
call :log_info "停止所有应用服务..."
docker-compose down
call :log_success "应用服务已停止"
goto :eof

:: 显示帮助
:show_help
echo 用法: %~n0 [命令]
echo.
echo 命令:
echo   (无参数)  - 启动所有应用服务
echo   stop      - 停止所有应用服务
echo   restart   - 重启所有应用服务
echo   status    - 查看应用服务状态
echo   logs      - 查看日志
echo   help      - 显示帮助信息
echo.
echo 示例:
echo   %~n0                         # 启动所有应用服务
echo   %~n0 stop                   # 停止所有应用服务
echo   %~n0 logs                   # 查看所有服务日志
goto :eof

:: 主函数
:main
echo ==========================================
echo 🚀 Collide 应用服务快速部署脚本 (Windows版)
echo ==========================================
echo.

:: 检查参数
if "%~1"=="stop" (
    call :cleanup
    goto :eof
)
if "%~1"=="status" (
    docker-compose ps
    goto :eof
)
if "%~1"=="logs" (
    docker-compose logs -f
    goto :eof
)
if "%~1"=="restart" (
    call :cleanup
    timeout /t 3 /nobreak >nul
)
if "%~1"=="help" (
    call :show_help
    goto :eof
)
if "%~1"=="-h" (
    call :show_help
    goto :eof
)
if "%~1"=="--help" (
    call :show_help
    goto :eof
)

:: 执行部署流程
call :check_requirements
if %errorlevel% neq 0 exit /b 1

call :check_ports
if %errorlevel% neq 0 exit /b 1

call :check_middleware
if %errorlevel% neq 0 exit /b 1

call :start_applications
if %errorlevel% neq 0 exit /b 1

call :show_status
goto :eof

:: 入口点
call :main %* 