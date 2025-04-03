@echo off
chcp 65001 > nul
echo 查看机器人龟棋应用日志...

echo 1. 查看所有日志
echo 2. 查看后端日志
echo 3. 查看前端日志
echo.

set /p choice="请选择(1-3): "

if "%choice%"=="1" (
    docker-compose logs -f
) else if "%choice%"=="2" (
    docker-compose logs -f backend
) else if "%choice%"=="3" (
    docker-compose logs -f frontend
) else (
    echo 选择无效，将显示所有日志
    docker-compose logs -f
)

pause 