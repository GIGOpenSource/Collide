@echo off
setlocal enabledelayedexpansion

:: Collide 项目服务停止脚本 (Windows版)
:: 用途：停止 Gateway、Auth、Application 三个服务

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

:: 停止单个服务
:stop_service
set SERVICE_NAME=%~1
set PID_FILE=%SERVICE_NAME%.pid
set PORT=%~2

call :print_info "停止 %SERVICE_NAME% 服务..."

:: 方法1：通过PID文件停止
if exist "%PID_FILE%" (
    set /p PID=<"%PID_FILE%"
    
    :: 检查进程是否存在
    tasklist /fi "PID eq !PID!" 2>nul | find "!PID!" >nul
    if not errorlevel 1 (
        call :print_info "通过PID文件停止进程：!PID!"
        
        :: 优雅停止（Windows没有SIGTERM，直接终止）
        taskkill /PID !PID! /T >nul 2>&1
        if not errorlevel 1 (
            call :print_success "%SERVICE_NAME% 服务已停止"
            del "%PID_FILE%"
        ) else (
            call :print_error "%SERVICE_NAME% 服务停止失败"
        )
    ) else (
        call :print_warning "PID文件存在但进程不存在：!PID!"
        del "%PID_FILE%"
    )
)

:: 方法2：通过端口查找进程停止（备用方案）
if not "%PORT%"=="" (
    for /f "tokens=5" %%a in ('netstat -ano ^| find ":%PORT%" ^| find "LISTENING" 2^>nul') do (
        set PID=%%a
        call :print_info "通过端口 %PORT% 查找到进程：!PID!"
        call :print_info "停止占用端口 %PORT% 的进程：!PID!"
        taskkill /PID !PID! /F >nul 2>&1
    )
)
goto :eof

:: 停止所有Java进程（最后的清理步骤）
:cleanup_java_processes
call :print_info "清理残留的Collide相关Java进程..."

:: 查找包含collide关键字的Java进程
set FOUND_PROCESSES=0
for /f "tokens=2" %%a in ('tasklist /fi "imagename eq java.exe" /fo csv 2^>nul ^| find "java.exe"') do (
    set PID=%%a
    set PID=!PID:"=!
    
    :: 检查进程命令行是否包含collide
    for /f "tokens=*" %%b in ('wmic process where "ProcessId=!PID!" get CommandLine /value 2^>nul ^| find "collide"') do (
        call :print_warning "发现残留的Collide Java进程：!PID!"
        call :print_info "清理进程：!PID!"
        taskkill /PID !PID! /F >nul 2>&1
        set FOUND_PROCESSES=1
    )
)

if !FOUND_PROCESSES!==0 (
    call :print_success "没有发现残留的Collide Java进程"
)
goto :eof

:: 验证所有服务是否已停止
:verify_services_stopped
call :print_info "验证服务停止状态..."

set PORTS=9501 9502 9503
set SERVICES=Gateway Auth Application
set RUNNING_COUNT=0
set TOTAL_COUNT=3

set /a INDEX=0
for %%p in (%PORTS%) do (
    set /a INDEX+=1
    set PORT=%%p
    
    if !INDEX!==1 set SERVICE=Gateway
    if !INDEX!==2 set SERVICE=Auth
    if !INDEX!==3 set SERVICE=Application
    
    netstat -an | find ":!PORT!" | find "LISTENING" >nul 2>&1
    if not errorlevel 1 (
        call :print_error "!SERVICE! 服务仍在运行（端口 !PORT!）"
        set /a RUNNING_COUNT+=1
    ) else (
        call :print_success "!SERVICE! 服务已停止"
    )
)

if !RUNNING_COUNT!==0 (
    call :print_success "所有服务已成功停止"
) else (
    call :print_error "部分服务停止失败，可能需要手动清理"
)
goto :eof

:: 主停止流程
:main
set TARGET_SERVICE=%~1

if not "%TARGET_SERVICE%"=="" (
    :: 停止指定服务
    if /i "%TARGET_SERVICE%"=="gateway" (
        call :stop_service "gateway" 9501
    ) else if /i "%TARGET_SERVICE%"=="gw" (
        call :stop_service "gateway" 9501
    ) else if /i "%TARGET_SERVICE%"=="auth" (
        call :stop_service "auth" 9502
    ) else if /i "%TARGET_SERVICE%"=="app" (
        call :stop_service "application" 9503
    ) else if /i "%TARGET_SERVICE%"=="application" (
        call :stop_service "application" 9503
    ) else (
        call :print_error "未知的服务名：%TARGET_SERVICE%"
        call :print_info "支持的服务名：gateway, auth, app"
        exit /b 1
    )
) else (
    :: 停止所有服务
    call :print_info "=== 停止所有 Collide 服务 ==="
    
    call :stop_service "gateway" 9501
    call :stop_service "auth" 9502
    call :stop_service "application" 9503
    
    :: 清理残留进程
    call :cleanup_java_processes
    
    :: 验证停止状态
    call :verify_services_stopped
    
    call :print_info "=== 服务停止完成 ==="
)
goto :eof

:: 执行主流程
call :main %* 