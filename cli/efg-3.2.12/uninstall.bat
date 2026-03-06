@echo off
REM EasyFK Generator 卸载入口（双击或命令行执行均可）
powershell -ExecutionPolicy Bypass -File "%~dp0uninstall.ps1" %*
pause
