@echo off
setlocal enabledelayedexpansion

:: Collide 项目 Docker 一键部署脚本 (Windows版)
:: 用途：使用 Docker Compose 启动三个应用服务

:: 打印带颜色的信息
:print_info
echo [INFO] %~1
goto :eof

:print_success
echo [SUCCESS] %~1
goto :eof

:print_warning
echo [WARNING] %~1
goto :eof

:print_error
echo [ERROR] %~1
goto :eof

:: 检查Docker环境
:check_docker
docker --version >nul 2>&1
if errorlevel 1 (
    call :print_error "Docker 未安装或不在PATH中"
    exit /b 1
)

docker-compose --version >nul 2>&1
if not errorlevel 1 (
    set COMPOSE_CMD=docker-compose
    goto :docker_daemon_check
)

docker compose version >nul 2>&1
if not errorlevel 1 (
    set COMPOSE_CMD=docker compose
    goto :docker_daemon_check
)

call :print_error "Docker Compose 未安装或不在PATH中"
exit /b 1

:docker_daemon_check
docker info >nul 2>&1
if errorlevel 1 (
    call :print_error "Docker 守护进程未运行"
    exit /b 1
)

call :print_success "Docker环境检查通过"
goto :eof

:: 检查必要的文件
:check_files
if not exist "docker-compose.yml" (
    call :print_error "docker-compose.yml 文件不存在"
    exit /b 1
)

if not exist "config\docker\application-host.yml" (
    call :print_error "config\docker\application-host.yml 配置文件不存在"
    exit /b 1
)

if not exist "config\docker\bootstrap-host.yml" (
    call :print_error "config\docker\bootstrap-host.yml 配置文件不存在"
    exit /b 1
)

call :print_success "配置文件检查通过"
goto :eof

:: 创建必要的目录
:create_directories
call :print_info "创建必要的目录..."

:: 创建日志目录
if not exist "logs\gateway" mkdir "logs\gateway"
if not exist "logs\auth" mkdir "logs\auth"
if not exist "logs\application" mkdir "logs\application"

:: 创建配置目录
if not exist "config\docker" mkdir "config\docker"

call :print_success "目录创建完成"
goto :eof

:: 构建和启动服务
:deploy_services
call :print_info "开始构建和部署服务..."

:: 先停止现有服务（如果存在）
call :print_info "停止现有服务..."
%COMPOSE_CMD% down --remove-orphans >nul 2>&1

:: 构建镜像
call :print_info "构建Docker镜像..."
%COMPOSE_CMD% build --no-cache
if errorlevel 1 (
    call :print_error "镜像构建失败"
    exit /b 1
)
call :print_success "镜像构建成功"

:: 启动服务
call :print_info "启动服务..."
%COMPOSE_CMD% up -d
if errorlevel 1 (
    call :print_error "服务启动失败"
    exit /b 1
)
call :print_success "服务启动成功"
goto :eof

:: 等待服务健康检查
:wait_for_services
call :print_info "等待服务健康检查..."

set SERVICES=collide-gateway:9501 collide-auth:9502 collide-application:9503
set TIMEOUT=120

for %%s in (%SERVICES%) do (
    for /f "tokens=1,2 delims=:" %%a in ("%%s") do (
        set SERVICE_NAME=%%a
        set PORT=%%b
        
        call :print_info "等待 !SERVICE_NAME! 服务就绪..."
        
        set COUNT=0
        :wait_loop
        curl -s http://localhost:!PORT!/actuator/health >nul 2>&1
        if not errorlevel 1 (
            call :print_success "!SERVICE_NAME! 服务已就绪"
            goto :next_service
        )
        
        set /a COUNT+=1
        if !COUNT! geq %TIMEOUT% (
            echo.
            call :print_error "!SERVICE_NAME! 服务启动超时"
            exit /b 1
        )
        
        echo|set /p="."
        timeout /t 1 >nul
        goto :wait_loop
        
        :next_service
        echo.
    )
)
goto :eof

:: 显示服务状态
:show_status
call :print_info "服务部署状态："
echo.
%COMPOSE_CMD% ps
echo.

call :print_info "服务访问地址："
echo   Gateway:     http://localhost:9501
echo   Auth:        http://localhost:9502
echo   Application: http://localhost:9503
echo.

call :print_info "管理命令："
echo   查看状态:    %COMPOSE_CMD% ps
echo   查看日志:    %COMPOSE_CMD% logs [service]
echo   停止服务:    %COMPOSE_CMD% down
echo   重启服务:    %COMPOSE_CMD% restart [service]
echo.
goto :eof

:: 主部署流程
:main
call :print_info "=== Collide Docker 部署开始 ==="

:: 环境检查
call :check_docker
if errorlevel 1 exit /b 1

call :check_files
if errorlevel 1 exit /b 1

call :create_directories

:: 构建项目 JAR 文件
call :print_info "构建项目 JAR 文件..."
mvn clean package -DskipTests -q
if errorlevel 1 (
    call :print_error "JAR 构建失败"
    exit /b 1
)
call :print_success "JAR 构建完成"

:: 部署服务
call :deploy_services
if errorlevel 1 exit /b 1

:: 显示状态
call :show_status

call :print_success "=== Docker 部署完成 ==="
goto :eof

:: 帮助信息
:show_help
echo 用法: %0 [选项]
echo.
echo 选项:
echo   -h, --help     显示帮助信息
echo   --no-wait      不等待服务健康检查
echo   --build-only   只构建镜像，不启动服务
echo.
echo 示例:
echo   %0              # 完整部署
echo   %0 --no-wait    # 部署但不等待健康检查
echo   %0 --build-only # 只构建镜像
goto :eof

:: 参数处理和主流程
set NO_WAIT=false
set BUILD_ONLY=false

:parse_args
if "%~1"=="" goto :args_parsed
if /i "%~1"=="-h" (
    call :show_help
    exit /b 0
)
if /i "%~1"=="--help" (
    call :show_help
    exit /b 0
)
if /i "%~1"=="--no-wait" (
    set NO_WAIT=true
    shift
    goto :parse_args
)
if /i "%~1"=="--build-only" (
    set BUILD_ONLY=true
    shift
    goto :parse_args
)
call :print_error "未知参数: %~1"
call :show_help
exit /b 1

:args_parsed

:: 执行主流程
if /i "%BUILD_ONLY%"=="true" (
    call :check_docker
    if errorlevel 1 exit /b 1
    call :check_files
    if errorlevel 1 exit /b 1
    %COMPOSE_CMD% build --no-cache
    call :print_success "镜像构建完成"
) else (
    call :main
    if errorlevel 1 exit /b 1
    
    if /i "%NO_WAIT%"=="false" (
        call :wait_for_services
    )
) 