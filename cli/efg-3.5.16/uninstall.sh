#!/bin/sh
# ============================================================
#  EasyFK Generator 卸载脚本 (macOS / Linux)
#
#  使用方式：
#    chmod +x uninstall.sh && ./uninstall.sh
# ============================================================

INSTALL_DIR="${INSTALL_DIR:-$HOME/.easyfk-gen}"

echo ""
echo "============================================"
echo "  EasyFK Generator Uninstaller"
echo "============================================"
echo ""

# ---- 1. 删除安装目录 ----
if [ -d "${INSTALL_DIR}" ]; then
    echo "[1/2] 删除安装目录: ${INSTALL_DIR}"
    rm -rf "${INSTALL_DIR}"
    echo "  已删除"
else
    echo "[1/2] 安装目录不存在，跳过"
fi

# ---- 2. 提示清理 PATH ----
echo "[2/2] 清理 PATH..."
echo "  请手动删除 shell 配置文件中的以下行："
echo ""
echo "    export PATH=\"\$PATH:${INSTALL_DIR}\""
echo ""

SHELL_NAME="$(basename "$SHELL")"
case "${SHELL_NAME}" in
    zsh)  echo "  配置文件: ~/.zshrc" ;;
    bash) echo "  配置文件: ~/.bashrc 或 ~/.bash_profile" ;;
    fish) echo "  配置文件: ~/.config/fish/config.fish" ;;
    *)    echo "  配置文件: ~/.profile" ;;
esac

echo ""
echo "============================================"
echo "  卸载完成！"
echo "============================================"
echo ""
