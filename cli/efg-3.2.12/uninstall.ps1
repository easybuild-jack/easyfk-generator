# ============================================================
#  EasyFK Generator 卸载脚本 (Windows PowerShell)
#
#  使用方式：
#    powershell -ExecutionPolicy Bypass -File uninstall.ps1
# ============================================================

param(
    [string]$InstallDir = "$env:USERPROFILE\.easyfk-gen"
)

$ErrorActionPreference = "Stop"

Write-Host ""
Write-Host "============================================" -ForegroundColor Cyan
Write-Host "  EasyFK Generator Uninstaller" -ForegroundColor Cyan
Write-Host "============================================" -ForegroundColor Cyan
Write-Host ""

# ---- 1. 删除安装目录 ----
if (Test-Path $InstallDir) {
    Write-Host "[1/2] 删除安装目录: $InstallDir" -ForegroundColor Yellow
    Remove-Item -Recurse -Force $InstallDir
    Write-Host "  已删除" -ForegroundColor Gray
} else {
    Write-Host "[1/2] 安装目录不存在，跳过" -ForegroundColor Gray
}

# ---- 2. 从 PATH 中移除 ----
Write-Host "[2/2] 清理系统 PATH..." -ForegroundColor Yellow
$userPath = [Environment]::GetEnvironmentVariable("Path", "User")
$parts = ($userPath -split ";") | Where-Object { $_ -ne $InstallDir -and $_ -ne "" }
$newPath = $parts -join ";"
[Environment]::SetEnvironmentVariable("Path", $newPath, "User")
Write-Host "  已从 PATH 中移除" -ForegroundColor Gray

# ---- 完成 ----
Write-Host ""
Write-Host "============================================" -ForegroundColor Green
Write-Host "  卸载完成！" -ForegroundColor Green
Write-Host "============================================" -ForegroundColor Green
Write-Host ""
