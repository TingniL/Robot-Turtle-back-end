@echo off
chcp 65001 > nul
echo 正在测试机器人龟棋API...

REM 测试API根路径
echo 测试API根路径...
curl http://localhost:5000/

echo.
echo 测试创建新游戏...
curl -X POST http://localhost:5000/api/games/new

echo.
echo 测试获取历史...
curl http://localhost:5000/api/games/history

echo.
echo API测试完成！
echo.
pause 