@echo off
setlocal enabledelayedexpansion

:: Collide 项目一键部署脚本 (Windows版)
:: 用途：启动 Gateway、Auth、Application 三个服务

:: 配置信息
set GATEWAY_PORT=9501
set AUTH_PORT=9502
set APP_PORT=9503

:: 项目路径
set GATEWAY_DIR=collide-gateway
set AUTH_DIR=collide-auth
set APP_DIR=collide-application\collide-app

:: 日志目录
set LOGS_DIR=.\logs
set GATEWAY_LOG=%LOGS_DIR%\gateway.log
set AUTH_LOG=%LOGS_DIR%\auth.log
set APP_LOG=%LOGS_DIR%\application.log

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

:: 检查Java环境
:check_java
java -version >nul 2>&1
if errorlevel 1 (
    call :print_error "Java 未安装或不在PATH中"
    exit /b 1
)

:: 获取Java版本
for /f "tokens=3" %%i in ('java -version 2^>^&1 ^| find "version"') do set JAVA_VERSION=%%i
set JAVA_VERSION=%JAVA_VERSION:"=%
for /f "tokens=1 delims=." %%i in ("%JAVA_VERSION%") do set JAVA_MAJOR=%%i

if %JAVA_MAJOR% lss 21 (
    call :print_error "需要Java 21或更高版本，当前版本：%JAVA_VERSION%"
    exit /b 1
)

call :print_success "Java环境检查通过：%JAVA_VERSION%"
goto :eof

:: 创建日志目录
:create_logs_dir
if not exist "%LOGS_DIR%" (
    mkdir "%LOGS_DIR%"
    call :print_info "创建日志目录：%LOGS_DIR%"
)
goto :eof

:: 检查端口是否被占用
:check_port
set PORT=%~1
set SERVICE=%~2

netstat -an | find ":%PORT%" | find "LISTENING" >nul
if not errorlevel 1 (
    call :print_warning "端口 %PORT% 已被占用，可能是 %SERVICE% 已在运行"
    call :print_info "尝试停止占用端口 %PORT% 的进程..."
    
    :: 获取占用端口的进程ID
    for /f "tokens=5" %%a in ('netstat -ano ^| find ":%PORT%" ^| find "LISTENING"') do (
        set PID=%%a
        taskkill /PID !PID! /F >nul 2>&1
        if not errorlevel 1 (
            call :print_success "已停止进程 !PID!"
        )
    )
    timeout /t 2 >nul
)
goto :eof

:: 构建项目
:build_project
call :print_info "开始构建项目..."

mvn clean package -DskipTests -q
if errorlevel 1 (
    call :print_error "项目构建失败"
    exit /b 1
)

call :print_success "项目构建完成"
goto :eof

:: 启动Gateway服务
:start_gateway
call :print_info "启动 Gateway 服务..."

call :check_port %GATEWAY_PORT% "Gateway"

cd /d %GATEWAY_DIR%

:: 查找JAR文件 - 修正：使用实际的文件名模式
set JAR_FILE=
for %%f in (target\collide-gateway-*.jar) do (
    if not "%%~nxf"=="collide-gateway-*.jar" (
        if not "%%~nxf" == "*original*" (
            set JAR_FILE=%%f
            goto :gateway_jar_found
        )
    )
)

:gateway_jar_found
if not exist "%JAR_FILE%" (
    call :print_error "Gateway JAR文件不存在，请先构建项目"
    call :print_error "查找模式：target\collide-gateway-*.jar"
    dir target\ 2>nul
    exit /b 1
)

call :print_info "使用JAR文件：%JAR_FILE%"

:: 启动服务
start /b java -server -Xms256m -Xmx512m -Dspring.profiles.active=local -Dserver.port=%GATEWAY_PORT% -Dlogging.file.path=..\%LOGS_DIR% -jar "%JAR_FILE%" > "..\%GATEWAY_LOG%" 2>&1

:: 获取进程ID并保存
timeout /t 2 >nul
for /f "tokens=2" %%a in ('tasklist /fi "imagename eq java.exe" /fo csv ^| find "java.exe"') do (
    set GATEWAY_PID=%%a
    echo !GATEWAY_PID! > ..\gateway.pid
    goto :gateway_pid_saved
)

:gateway_pid_saved
cd ..
call :print_success "Gateway 服务启动成功，端口: %GATEWAY_PORT%"
goto :eof

:: 启动Auth服务
:start_auth
call :print_info "启动 Auth 服务..."

call :check_port %AUTH_PORT% "Auth"

cd /d %AUTH_DIR%

:: 查找JAR文件 - 修正：使用实际的文件名模式
set JAR_FILE=
for %%f in (target\collide-auth-*.jar) do (
    if not "%%~nxf"=="collide-auth-*.jar" (
        if not "%%~nxf" == "*original*" (
            set JAR_FILE=%%f
            goto :auth_jar_found
        )
    )
)

:auth_jar_found
if not exist "%JAR_FILE%" (
    call :print_error "Auth JAR文件不存在，请先构建项目"
    call :print_error "查找模式：target\collide-auth-*.jar"
    dir target\ 2>nul
    exit /b 1
)

call :print_info "使用JAR文件：%JAR_FILE%"

:: 启动服务
start /b java -server -Xms256m -Xmx512m -Dspring.profiles.active=local -Dserver.port=%AUTH_PORT% -Dlogging.file.path=..\%LOGS_DIR% -jar "%JAR_FILE%" > "..\%AUTH_LOG%" 2>&1

:: 获取进程ID并保存
timeout /t 2 >nul
for /f "tokens=2" %%a in ('tasklist /fi "imagename eq java.exe" /fo csv ^| find "java.exe"') do (
    set AUTH_PID=%%a
    echo !AUTH_PID! > ..\auth.pid
    goto :auth_pid_saved
)

:auth_pid_saved
cd ..
call :print_success "Auth 服务启动成功，端口: %AUTH_PORT%"
goto :eof

:: 启动Application服务
:start_application
call :print_info "启动 Application 服务..."

call :check_port %APP_PORT% "Application"

cd /d %APP_DIR%

:: 查找JAR文件 - 修正：Application使用固定文件名
set JAR_FILE=target\collide-app.jar
if not exist "%JAR_FILE%" (
    call :print_error "Application JAR文件不存在，请先构建项目"
    call :print_error "期望文件：%JAR_FILE%"
    dir target\ 2>nul
    exit /b 1
)

call :print_info "使用JAR文件：%JAR_FILE%"

:: 启动服务
start /b java -server -Xms512m -Xmx1024m -Dspring.profiles.active=local -Dserver.port=%APP_PORT% -Dlogging.file.path=..\..\%LOGS_DIR% -jar "%JAR_FILE%" > "..\..\%APP_LOG%" 2>&1

:: 获取进程ID并保存
timeout /t 2 >nul
for /f "tokens=2" %%a in ('tasklist /fi "imagename eq java.exe" /fo csv ^| find "java.exe"') do (
    set APP_PID=%%a
    echo !APP_PID! > ..\..\application.pid
    goto :app_pid_saved
)

:app_pid_saved
cd ..\..
call :print_success "Application 服务启动成功，端口: %APP_PORT%"
goto :eof

:: 等待服务启动
:wait_for_service
set PORT=%~1
set SERVICE=%~2
set MAX_WAIT=60
set COUNT=0

call :print_info "等待 %SERVICE% 服务启动..."

:wait_loop
curl -s http://localhost:%PORT%/actuator/health >nul 2>&1
if not errorlevel 1 (
    call :print_success "%SERVICE% 服务已就绪"
    goto :eof
)

set /a COUNT=%COUNT%+1
if %COUNT% geq %MAX_WAIT% (
    echo.
    call :print_error "%SERVICE% 服务启动超时"
    exit /b 1
)

echo|set /p="."
timeout /t 1 >nul
goto :wait_loop

:: 主部署流程
:main
call :print_info "=== Collide 项目一键部署开始 ==="

:: 环境检查
call :check_java
if errorlevel 1 exit /b 1

call :create_logs_dir

:: 构建项目
call :build_project
if errorlevel 1 exit /b 1

:: 启动服务
call :start_gateway
if errorlevel 1 exit /b 1

timeout /t 3 >nul

call :start_auth
if errorlevel 1 exit /b 1

timeout /t 3 >nul

call :start_application
if errorlevel 1 exit /b 1

call :print_info "等待所有服务启动完成..."
timeout /t 5 >nul

:: 检查服务状态
call :print_info "检查服务健康状态..."

call :wait_for_service %GATEWAY_PORT% "Gateway"
if errorlevel 1 exit /b 1

call :wait_for_service %AUTH_PORT% "Auth"
if errorlevel 1 exit /b 1

call :wait_for_service %APP_PORT% "Application"
if errorlevel 1 exit /b 1

call :print_success "=== 所有服务部署完成 ==="
echo.
call :print_info "服务访问地址："
echo   Gateway:     http://localhost:%GATEWAY_PORT%
echo   Auth:        http://localhost:%AUTH_PORT%
echo   Application: http://localhost:%APP_PORT%
echo.
call :print_info "管理命令："
echo   查看状态:    .\scripts\health-check.bat
echo   停止服务:    .\scripts\stop.bat
echo   查看日志:    .\scripts\logs.bat [gateway^|auth^|app]

goto :eof

:: 执行主流程
call :main %* 