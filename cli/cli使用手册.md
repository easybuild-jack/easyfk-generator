# EasyFK Generator CLI 使用手册

> 一次配置，一条命令，全栈生成。

---

## 环境要求

- **JDK 21** 或更高版本（[下载地址](https://adoptium.net/zh-CN/)）

---

## 安装

解压 `efg-3.2.12.zip`，进入 `efg-3.2.12` 目录：

**Windows：** 双击 `install.bat`

**macOS / Linux：**

```bash
chmod +x install && ./install
```

安装完成后**重新打开终端**，输入 `efg -V` 验证：

```
easyfk-generator 3.2.12
```

---

## 卸载

**Windows：** 双击 `uninstall.bat`

**macOS / Linux：**

```bash
chmod +x uninstall && ./uninstall
```

---

## 快速上手

### 第一步：生成配置模板

```bash
efg init
```

当前目录会生成 `generator.yml`，用编辑器打开填入项目信息和数据库配置。

### 第二步：执行生成

```bash
efg
```

搞定！在 `project-dir` 指定的目录下已生成完整项目。

---

## 命令参考

### 基本语法

```
efg [选项]
efg init [-o 文件名]
```

### 生成命令

| 命令 | 说明 |
|------|------|
| `efg` | 全量生成（项目骨架 + Entity + 业务代码 + 自动装配） |
| `efg -p` | 仅生成项目骨架 |
| `efg -m` | 仅生成 Entity + Mapper |
| `efg -s` | 仅生成业务代码（Repository / Service / API / Remote / Controller） |
| `efg -a` | 仅生成自动装配配置 |
| `efg -d` | 仅刷新 DTO 和 Param（表结构变更后使用） |

### 组合使用

Flag 可以组合，按需选择：

| 命令 | 说明 |
|------|------|
| `efg -pm` | 项目骨架 + Entity + Mapper |
| `efg -sa` | 业务代码 + 自动装配配置 |
| `efg -pms` | 项目骨架 + Entity + 业务代码 |

### 其他选项

| 选项 | 说明 |
|------|------|
| `-c <文件>` | 指定配置文件路径（默认读取当前目录 `generator.yml`） |
| `-h, --help` | 查看帮助 |
| `-V, --version` | 查看版本 |

### init 子命令

| 命令 | 说明 |
|------|------|
| `efg init` | 在当前目录生成 `generator.yml` 配置模板 |
| `efg init -o my-config.yml` | 指定输出文件名 |

---

## 配置文件说明

`generator.yml` 配置结构如下（`efg init` 生成的模板包含完整注释）：

```yaml
easyfk:
  config:
    generator:
      project:                  # 项目配置
        project-dir: ...        # [必填] 项目输出目录
        group-id: ...           # [必填] Maven groupId
        project-name: ...       # [必填] 项目名称
        base-package: ...       # [必填] 包根路径
        project-type: smart     # single / microservice / smart
        build-type: maven       # maven / gradle
        orm-type: MYBATIS       # MYBATIS / MYBATIS_FLEX / HIBERNATE
        prd-type: separation    # none / single / separation
        # ... 更多配置见模板文件

      code:                     # 代码配置
        module-name: ...        # [必填] 模块名称
        db-short-url: ...       # [必填] 数据库连接 (host:port/database)
        db-user: root           # 数据库用户名
        db-pwd: ...             # 数据库密码
        from-db-tables: ...     # 从数据库读取的表名（逗号分隔）
        # ... 更多配置见模板文件
```

---

## 典型使用场景

### 场景一：从零创建新项目

```bash
mkdir my-project && cd my-project
efg init                # 生成配置模板
# 编辑 generator.yml
efg                     # 全量生成
```

### 场景二：已有项目新增业务表

```bash
# 修改 generator.yml 中 from-db-tables，添加新表名
efg -m                  # 生成新表的 Entity + Mapper
efg -s                  # 生成对应的业务代码
```

### 场景三：数据库表结构变更

```bash
efg -d                  # 仅刷新 DTO 和 Param，不影响其他代码
```

### 场景四：使用不同配置文件

```bash
efg -c dev-generator.yml        # 开发环境
efg -c prod-generator.yml -m    # 生产环境，仅生成 Model
```

---

## 常见问题

**Q: 提示"找不到 java"**

请安装 JDK 21：https://adoptium.net/zh-CN/

**Q: 生成的代码会覆盖我手动修改的文件吗？**

不会。Entity 和 DTO 会更新（跟随数据库结构），但 Mapper、Repository、Service、Controller 等文件使用"不存在才创建"策略，已有文件不会被覆盖。

**Q: 支持哪些数据库？**

MySQL、PostgreSQL、Oracle、MariaDB、SQL Server、达梦、人大金仓、GaussDB、OceanBase 等 22 种数据库。

**Q: 配置文件格式和 Spring Boot 的 application.yml 一样吗？**

完全一致。如果你已有 Spring Boot 项目中的生成器配置，直接复制过来即可。
