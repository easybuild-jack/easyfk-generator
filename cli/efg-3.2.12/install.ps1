# ============================================================
#  EasyFK Generator 一键安装脚本 (Windows PowerShell)
#
#  使用方式（管理员终端或普通终端均可）：
#    powershell -ExecutionPolicy Bypass -File install.ps1
#
#  安装后直接使用：
#    easyfk-gen init
#    easyfk-gen generate -c generator.yml
# ============================================================

param(
    [string]$Version = "3.2.12",
    [string]$InstallDir = "$env:USERPROFILE\.easyfk-gen",
    # Gitee Release 下载地址，请替换为你的实际仓库路径
    [string]$DownloadUrl = ""
)

$ErrorActionPreference = "Stop"
$JarName = "easyfk-generator-$Version-cli.jar"

Write-Host ""
Write-Host "============================================" -ForegroundColor Cyan
Write-Host "  EasyFK Generator Installer v$Version" -ForegroundColor Cyan
Write-Host "============================================" -ForegroundColor Cyan
Write-Host ""

# ---- 1. 检查 Java 环境 ----
Write-Host "[1/4] 检查 Java 环境..." -ForegroundColor Yellow
$javaCmd = Get-Command java -ErrorAction SilentlyContinue
if (-not $javaCmd) {
    Write-Host "[错误] 未检测到 Java，请先安装 JDK 21 或更高版本" -ForegroundColor Red
    Write-Host "  下载地址: https://adoptium.net/zh-CN/" -ForegroundColor Gray
    exit 1
}
$javaVersion = cmd /c "java -version 2>&1" | Select-Object -First 1
Write-Host "  Java: $javaVersion" -ForegroundColor Gray

# ---- 2. 创建安装目录 ----
Write-Host "[2/4] 创建安装目录: $InstallDir" -ForegroundColor Yellow
if (-not (Test-Path $InstallDir)) {
    New-Item -ItemType Directory -Path $InstallDir -Force | Out-Null
}

# ---- 3. 获取 jar 文件 ----
Write-Host "[3/4] 获取 $JarName ..." -ForegroundColor Yellow
$jarTarget = Join-Path $InstallDir $JarName

# 清理旧版本 jar
$oldJars = Get-ChildItem -Path $InstallDir -Filter "easyfk-generator-*-cli.jar" -ErrorAction SilentlyContinue | Where-Object { $_.Name -ne $JarName }
if ($oldJars) {
    foreach ($old in $oldJars) {
        Write-Host "  清理旧版本: $($old.Name)" -ForegroundColor Gray
        Remove-Item $old.FullName -Force
    }
}

if ($DownloadUrl -ne "") {
    # 从远程下载
    Write-Host "  下载: $DownloadUrl" -ForegroundColor Gray
    try {
        Invoke-WebRequest -Uri $DownloadUrl -OutFile $jarTarget -UseBasicParsing
    } catch {
        Write-Host "[错误] 下载失败: $_" -ForegroundColor Red
        exit 1
    }
} else {
    # 本地模式：从脚本同级目录查找 jar
    $localJar = Join-Path $PSScriptRoot $JarName
    if (Test-Path $localJar) {
        Write-Host "  复制本地文件: $localJar" -ForegroundColor Gray
        Copy-Item $localJar $jarTarget -Force
    } else {
        # 从 build 输出目录查找
        $buildJar = Join-Path $PSScriptRoot "..\build\libs\$JarName"
        if (Test-Path $buildJar) {
            Write-Host "  复制构建产物: $buildJar" -ForegroundColor Gray
            Copy-Item $buildJar $jarTarget -Force
        } else {
            Write-Host "[错误] 找不到 $JarName" -ForegroundColor Red
            Write-Host "  请将 $JarName 放到本脚本同级目录，或使用 -DownloadUrl 参数指定下载地址" -ForegroundColor Gray
            Write-Host "  构建方式: gradle fatJar" -ForegroundColor Gray
            exit 1
        }
    }
}

if (-not (Test-Path $jarTarget)) {
    Write-Host "[错误] jar 文件不存在: $jarTarget" -ForegroundColor Red
    exit 1
}

# ---- 生成 easyfk-gen.bat ----
$batContent = @"
@echo off
java -jar "%~dp0$JarName" %*
"@
$batPath = Join-Path $InstallDir "efg.bat"
Set-Content -Path $batPath -Value $batContent -Encoding ASCII

# ---- 4. 配置 PATH ----
Write-Host "[4/4] 配置系统 PATH..." -ForegroundColor Yellow
$userPath = [Environment]::GetEnvironmentVariable("Path", "User")
if ($userPath -split ";" | Where-Object { $_ -eq $InstallDir }) {
    Write-Host "  PATH 中已包含 $InstallDir，跳过" -ForegroundColor Gray
} else {
    $newPath = $userPath.TrimEnd(";") + ";$InstallDir"
    [Environment]::SetEnvironmentVariable("Path", $newPath, "User")
    $env:Path = $env:Path + ";$InstallDir"
    Write-Host "  已添加到用户 PATH" -ForegroundColor Gray
}

# ---- 完成 ----
Write-Host ""
Write-Host "============================================" -ForegroundColor Green
Write-Host "  安装成功！" -ForegroundColor Green
Write-Host "============================================" -ForegroundColor Green
Write-Host ""
Write-Host "安装位置: $InstallDir" -ForegroundColor White
Write-Host ""
Write-Host "请 重新打开终端 后使用以下命令：" -ForegroundColor White
Write-Host ""
Write-Host "  efg init       # 生成配置模板" -ForegroundColor Cyan
Write-Host "  efg            # 全量生成" -ForegroundColor Cyan
Write-Host "  efg -p         # 仅生成项目骨架" -ForegroundColor Cyan
Write-Host "  efg -m         # 仅生成 Entity + Mapper" -ForegroundColor Cyan
Write-Host "  efg -pm        # 项目骨架 + Entity + Mapper" -ForegroundColor Cyan
Write-Host "  efg -h         # 查看帮助" -ForegroundColor Cyan
Write-Host ""
