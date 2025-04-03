@echo off
chcp 65001 > nul
echo 正在停止机器人龟棋应用...

REM 停止并移除容器
docker-compose down

echo.
if %errorlevel% neq 0 (
    echo 停止失败，请检查错误信息。
) else (
    echo 机器人龟棋应用已成功停止！
)

pause 