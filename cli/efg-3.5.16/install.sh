#!/bin/sh
# ============================================================
#  EasyFK Generator 一键安装脚本 (macOS / Linux)
#
#  使用方式：
#    chmod +x install.sh && ./install.sh
#  或：
#    curl -sSL https://your-gitee-url/install.sh | sh
#
#  安装后直接使用：
#    easyfk-gen init
#    easyfk-gen generate -c generator.yml
# ============================================================

VERSION="${VERSION:-3.5.16}"
INSTALL_DIR="${INSTALL_DIR:-$HOME/.easyfk-gen}"
JAR_NAME="easyfk-generator-${VERSION}-cli.jar"
# Gitee Release 下载地址，请替换为你的实际仓库路径
DOWNLOAD_URL="${DOWNLOAD_URL:-}"

echo ""
echo "============================================"
echo "  EasyFK Generator Installer v${VERSION}"
echo "============================================"
echo ""

# ---- 1. 检查 Java 环境 ----
echo "[1/4] 检查 Java 环境..."
if ! command -v java >/dev/null 2>&1; then
    echo "[错误] 未检测到 Java，请先安装 JDK 21 或更高版本"
    echo "  macOS:  brew install openjdk@21"
    echo "  Ubuntu: sudo apt install openjdk-21-jdk"
    echo "  通用:   https://adoptium.net/zh-CN/"
    exit 1
fi
JAVA_VER=$(java -version 2>&1 | head -n 1)
echo "  Java: ${JAVA_VER}"

# ---- 2. 创建安装目录 ----
echo "[2/4] 创建安装目录: ${INSTALL_DIR}"
mkdir -p "${INSTALL_DIR}"

# ---- 3. 获取 jar 文件 ----
echo "[3/4] 获取 ${JAR_NAME} ..."
JAR_TARGET="${INSTALL_DIR}/${JAR_NAME}"
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"

# 清理旧版本 jar
for old_jar in "${INSTALL_DIR}"/easyfk-generator-*-cli.jar; do
    [ -f "$old_jar" ] || continue
    old_name="$(basename "$old_jar")"
    if [ "$old_name" != "${JAR_NAME}" ]; then
        echo "  清理旧版本: ${old_name}"
        rm -f "$old_jar"
    fi
done

if [ -n "${DOWNLOAD_URL}" ]; then
    echo "  下载: ${DOWNLOAD_URL}"
    if command -v curl >/dev/null 2>&1; then
        curl -fSL -o "${JAR_TARGET}" "${DOWNLOAD_URL}"
    elif command -v wget >/dev/null 2>&1; then
        wget -q -O "${JAR_TARGET}" "${DOWNLOAD_URL}"
    else
        echo "[错误] 未找到 curl 或 wget，无法下载"
        exit 1
    fi
elif [ -f "${SCRIPT_DIR}/${JAR_NAME}" ]; then
    echo "  复制本地文件: ${SCRIPT_DIR}/${JAR_NAME}"
    cp "${SCRIPT_DIR}/${JAR_NAME}" "${JAR_TARGET}"
elif [ -f "${SCRIPT_DIR}/../build/libs/${JAR_NAME}" ]; then
    echo "  复制构建产物: ${SCRIPT_DIR}/../build/libs/${JAR_NAME}"
    cp "${SCRIPT_DIR}/../build/libs/${JAR_NAME}" "${JAR_TARGET}"
else
    echo "[错误] 找不到 ${JAR_NAME}"
    echo "  请将 ${JAR_NAME} 放到本脚本同级目录，或设置 DOWNLOAD_URL 环境变量"
    echo "  构建方式: gradle fatJar"
    exit 1
fi

if [ ! -f "${JAR_TARGET}" ]; then
    echo "[错误] jar 文件不存在: ${JAR_TARGET}"
    exit 1
fi

# ---- 生成 easyfk-gen 启动脚本 ----
cat > "${INSTALL_DIR}/efg" <<EOF
#!/bin/sh
exec java -jar "${INSTALL_DIR}/${JAR_NAME}" "\$@"
EOF
chmod +x "${INSTALL_DIR}/efg"

# ---- 4. 配置 PATH ----
echo "[4/4] 配置 PATH..."
SHELL_NAME="$(basename "$SHELL")"
PROFILE=""

case "${SHELL_NAME}" in
    zsh)  PROFILE="$HOME/.zshrc" ;;
    bash)
        if [ -f "$HOME/.bash_profile" ]; then
            PROFILE="$HOME/.bash_profile"
        else
            PROFILE="$HOME/.bashrc"
        fi
        ;;
    fish) PROFILE="$HOME/.config/fish/config.fish" ;;
    *)    PROFILE="$HOME/.profile" ;;
esac

PATH_LINE="export PATH=\"\$PATH:${INSTALL_DIR}\""
if [ "${SHELL_NAME}" = "fish" ]; then
    PATH_LINE="set -gx PATH \$PATH ${INSTALL_DIR}"
fi

if [ -n "${PROFILE}" ] && [ -f "${PROFILE}" ]; then
    if grep -q "${INSTALL_DIR}" "${PROFILE}" 2>/dev/null; then
        echo "  PATH 已配置，跳过"
    else
        echo "" >> "${PROFILE}"
        echo "# EasyFK Generator" >> "${PROFILE}"
        echo "${PATH_LINE}" >> "${PROFILE}"
        echo "  已写入 ${PROFILE}"
    fi
elif [ -n "${PROFILE}" ]; then
    echo "${PATH_LINE}" > "${PROFILE}"
    echo "  已创建 ${PROFILE}"
else
    echo "  [警告] 未能识别 Shell 类型，请手动将以下内容加入你的 shell 配置文件："
    echo "    ${PATH_LINE}"
fi

# 当前会话立即生效
export PATH="$PATH:${INSTALL_DIR}"

# ---- 完成 ----
echo ""
echo "============================================"
echo "  安装成功！"
echo "============================================"
echo ""
echo "安装位置: ${INSTALL_DIR}"
echo ""
echo "请 重新打开终端 或执行以下命令使 PATH 生效："
echo "  source ${PROFILE}"
echo ""
echo "然后使用："
echo "  efg init       # 生成配置模板"
echo "  efg            # 全量生成"
echo "  efg -p         # 仅生成项目骨架"
echo "  efg -m         # 仅生成 Entity + Mapper"
echo "  efg -pm        # 项目骨架 + Entity + Mapper"
echo "  efg -h         # 查看帮助"
echo ""
