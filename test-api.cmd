@echo off
echo 测试机器人龟棋API...

echo 测试后端API根路径...
curl http://localhost:5000/

echo.
echo 创建新游戏...
curl -X POST http://localhost:5000/api/games/new

echo.
pause 