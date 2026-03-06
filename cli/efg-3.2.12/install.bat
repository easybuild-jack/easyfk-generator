@echo off
REM EasyFK Generator 安装入口（双击或命令行执行均可）
powershell -ExecutionPolicy Bypass -File "%~dp0install.ps1" %*
pause
