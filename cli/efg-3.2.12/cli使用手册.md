# EasyFK Generator CLI 使用手册

> 一次配置，一条命令，全自动生成。

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

> **安装位置**：Windows — `%USERPROFILE%\.easyfk-gen`；macOS / Linux — `~/.easyfk-gen`。
> 安装脚本会自动将该目录加入用户 PATH，并在目录中放置 `efg` 启动脚本和 `easyfk-generator-3.2.12-cli.jar`。

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

> 如果目标文件已存在，`efg init` 会提示警告并退出，不会覆盖已有文件。

`efg init` 生成的模板内容如下：

```yaml
# ============================================================
#  EasyFK Generator 配置文件
#  使用方式：efg (在 generator.yml 所在目录执行)
# ============================================================

easyfk:
  config:
    generator:
      # ==================== 项目配置 ====================
      project:
        # [必填] 项目输出目录
        project-dir: D:\workspace\projects
        # [必填] Maven groupId
        group-id: com.example
        # [必填] 项目名称
        project-name: my-app
        # [必填] 项目包根路径
        base-package: com.example.myapp

        # 项目类型: single(单体) / microservice(微服务) / smart(多栈微服务)
        project-type: single
        # 构建工具: maven / gradle
        build-type: maven
        # ORM 框架: MYBATIS / MYBATIS_FLEX / HIBERNATE
        orm-type: MYBATIS
        # PRD 层策略: none(不生成) / single(单一) / separation(分离: client + bms)
        prd-type: single
        # 日志框架: LOGBACK / LOG4J2
        log-type: LOGBACK
        # 框架版本
        framework-version: 3.2.12
        # 项目版本
        project-version: 1.0.0-SNAPSHOT
        # [必填] 应用类型: BMS(后台管理端) / CLIENT(面向C端)
        app-type:
        # 业务模块列表 (可选)
        # modules:
        #   - name: auth
        #     prd-split: false
        #   - name: trading
        #     has-orm: false
        #   - name: payment

      # ==================== 代码配置 ====================
      code:
        # [必填] 模块名称
        module-name: myapp
        # 作者信息
        author: eb-jack
        # [必填] 数据库短连接 (host:port/database)
        db-short-url: localhost:3306/my_database
        # 数据库用户名
        db-user: root
        # 数据库密码
        db-pwd: your_password
        # ---------- 表配置 ----------
        # table-prefix: base_
        # ORM 类型: MYBATIS / MYBATIS_FLEX / HIBERNATE
        orm-type: MYBATIS
        # 从数据库读取的表名 (逗号分隔，会自动解析表结构)
        from-db-tables: product,order

        # ---------- 手动定义 Model (可选) ----------
        # model-list:
        #   - model-name: Product
        #     model-desc: 商品信息
        #   - model-name: Order
        #     model-desc: 订单信息
```

---

## 配置文件说明

执行 `efg init` 生成的 `generator.yml` 分为两大部分：**项目配置（project）** 和 **代码配置（code）**。

### 项目配置（project）

```yaml
easyfk.config.generator.project:
```

| 配置项 | 必填 | 默认值 | 说明 |
|--------|------|--------|------|
| `project-dir` | **是** | - | 项目输出目录，生成的项目会放在该目录下。例：`D:\workspace\projects` 或 `/home/user/projects` |
| `group-id` | **是** | - | Maven groupId，例：`com.example` |
| `project-name` | **是** | - | 项目名称，会作为项目根目录名和各子模块名的前缀。例：`my-app` |
| `base-package` | **是** | - | Java 包根路径，所有生成的代码都在此包下。例：`com.example.myapp` |
| `artifact-id` | 否 | 同 `project-name` | Maven artifactId，不填则默认取 `project-name` 的值 |
| `project-type` | 否 | `smart` | 项目架构类型，决定生成的项目结构 |
| | | | `single` — 单体项目，所有代码在一个工程中 |
| | | | `microservice` — 微服务，生成 API + Server + PRD 模块，支持一种 RPC |
| | | | `smart` — 多栈微服务，同时生成 Spring Cloud 和 Dubbo 两套 RPC 通道 |
| `rpc-type` | 否 | `cloud` | RPC 类型，仅 `microservice` 架构时生效 |
| | | | `cloud` — Spring Cloud Feign |
| | | | `dubbo` — Apache Dubbo |
| `build-type` | 否 | `maven` | 构建工具类型 |
| | | | `maven` — 生成 pom.xml |
| | | | `gradle` — 生成 build.gradle / settings.gradle |
| `gradle-type` | 否 | `groovy` | Gradle DSL 类型，仅 `build-type: gradle` 时生效 |
| | | | `groovy` — Groovy DSL (build.gradle) |
| | | | `kotlin` — Kotlin DSL (build.gradle.kts) |
| `orm-type` | 否 | `MYBATIS` | ORM 框架类型 |
| | | | `MYBATIS` — MyBatis-Plus |
| | | | `MYBATIS_FLEX` — MyBatis-Flex |
| | | | `HIBERNATE` — Hibernate (JPA) |
| `prd-type` | 否 | `none` | 前端对接层（Controller）策略 |
| | | | `none` — 不生成 Controller 层 |
| | | | `single` — 生成单一 PRD 项目，所有 Controller 集中 |
| | | | `separation` — 分离式：prd-client（C端）+ prd-bms（B端管理后台） |
| `log-type` | 否 | `LOGBACK` | 日志框架：`LOGBACK` 或 `LOG4J2` |
| `framework-version` | 否 | `3.2.12` | EasyFK 框架版本号 |
| `project-version` | 否 | `1.0.0-SNAPSHOT` | 生成项目的版本号 |
| `create-prd-project` | 否 | `true` | 是否生成 PRD（Controller）项目 |
| `create-repository` | 否 | `false` | 是否生成独立的 Repository 子项目 |
| `controller-auto-config` | 否 | `false` | 是否为 Controller 层生成 Spring Boot AutoConfiguration |
| `app-type` | **是** | - | 应用类型，决定模块 PRD 依赖的变体。`BMS` — 后台管理端；`CLIENT` — 面向 C 端 |
| `modules` | 否 | - | 业务模块列表，配置后自动在生成的项目中引入对应模块依赖。详见下方「模块配置」 |

#### 模块配置（modules）

用于声明项目需要引入的业务模块（如权限模块、交易模块、支付模块等）。生成器会根据项目类型自动将模块依赖分配到对应的子项目中。

```yaml
modules:
  - name: auth           # [必填] 模块名称
    prd-split: false     # auth 只有后台 prd，不区分 bms/client
  - name: trading
    has-orm: false        # trading 不需要持久化层
  - name: payment         # 使用全部默认值
```

每个模块支持以下配置项：

| 配置项 | 默认值 | 说明 |
|--------|--------|------|
| `name` | - | **必填**。模块名称，如 `auth`、`trading`、`payment` |
| `has-prd` | `true` | 是否引入 PRD 子依赖 |
| `prd-split` | `true` | PRD 是否区分 bms/client。`true` — 根据 `app-type` 或子项目类型自动选择 `{module}-prd-bms` / `{module}-prd-client`；`false` — 统一使用 `{module}-prd` |
| `has-server` | `true` | 是否引入 Server 子依赖（`{module}-server`） |
| `has-orm` | `true` | 是否引入 ORM 子依赖。具体 artifactId 由 `orm-type` 决定：MYBATIS → `{module}-orm-plus`，MYBATIS_FLEX → `{module}-orm-flex`，HIBERNATE → `{module}-orm-hibernate` |

**依赖分配规则：**

| 项目类型 | 子项目 | 引入的模块依赖 |
|----------|--------|----------------|
| **单体项目** | 主 pom / build.gradle | prd + server + orm 全部引入 |
| **微服务 / Smart** | server | server + orm |
| **微服务 / Smart** | prd-single | prd（根据 `app-type` + `prd-split` 决定变体） |
| **微服务 / Smart** | prd-parent | 仅非拆分模块的 prd（如 `auth-prd`） |
| **微服务 / Smart** | prd-bms | 拆分模块的 `{module}-prd-bms` |
| **微服务 / Smart** | prd-client | 拆分模块的 `{module}-prd-client` |
| **Smart** | cloud-api / cloud-provider | `{module}-cloud-api` / `{module}-cloud-provider` |
| **Smart** | dubbo-api / dubbo-provider | `{module}-dubbo-api` / `{module}-dubbo-provider` |

### 代码配置（code）

```yaml
easyfk.config.generator.code:
```

#### 基本配置

| 配置项 | 必填 | 默认值 | 说明 |
|--------|------|--------|------|
| `module-name` | **是** | - | 模块名称，用于生成配置类名、远程调用 serviceId 等。例：`myapp` |
| `author` | 否 | `eb-jack` | 代码注释中的作者信息 |
| `orm-type` | 否 | `MYBATIS` | ORM 类型（同 project 中的 orm-type） |
| `spring-annotation` | 否 | `true` | 是否在生成的类上添加 Spring 注解（如 `@Repository`、`@Service`） |
| `create-controller` | 否 | `true` | 是否生成 Controller 层代码 |
| `extends-supper-class` | 否 | `true` | Entity 是否继承框架基类（基类提供 deleted、insertTime、lastUpdateTime 字段） |

#### 数据库连接

| 配置项 | 必填 | 默认值 | 说明 |
|--------|------|--------|------|
| `db-type` | 否 | `MYSQL` | 数据库类型。可选值见下方「支持的数据库类型」 |
| `db-short-url` | **是**★ | - | 数据库短连接地址，格式：`host:port/database`。例：`localhost:3306/my_db`。★ 仅在使用 `from-db-tables` 时必填 |
| `db-user` | 否 | `root` | 数据库用户名 |
| `db-pwd` | **是**★ | - | 数据库密码。★ 仅在使用 `from-db-tables` 时必填 |

**支持的数据库类型（db-type）：**

| 值 | 数据库 | 值 | 数据库 |
|---|---|---|---|
| `MYSQL` | MySQL | `MARIADB` | MariaDB |
| `POSTGRE_SQL` | PostgreSQL | `ORACLE` | Oracle |
| `ORACLE_12C` | Oracle 12c+ | `SQL_SERVER` | SQL Server |
| `SQL_SERVER2005` | SQL Server 2005 | `DM` | 达梦 |
| `KINGBASE_ES` | 人大金仓 | `GAUSS` | GaussDB |
| `OCEAN_BASE` | OceanBase | `CLICK_HOUSE` | ClickHouse |
| `DB2` | IBM DB2 | `H2` | H2 |
| `HSQL` | HSQLDB | `SQLITE` | SQLite |
| `PHOENIX` | Apache Phoenix | `GBASE` | 南大通用 GBase |
| `OSCAR` | 神通 OSCAR | `SYBASE` | Sybase |
| `XU_GU` | 虚谷 | `FIREBIRD` | Firebird |
| `OTHER` | 其他 | | |

#### 表配置

| 配置项 | 必填 | 默认值 | 说明 |
|--------|------|--------|------|
| `table-prefix` | 否 | - | 表名前缀，生成类名时自动去除。例：配置 `base_`，表 `base_product` → 类 `Product` |
| `from-db-tables` | 否 | - | 需要从数据库读取结构的表名，逗号分隔。例：`product,order,user_info`。配置后会自动连接数据库解析字段 |

#### 手动定义 Model（可选）

除了 `from-db-tables` 自动解析，也可以手动定义模型：

```yaml
model-list:
  - model-name: Product         # [必填] 模型名称（大驼峰），对应生成的类名
    model-desc: 商品信息         # 模型描述，用于代码注释
    table-name: base_product    # 数据库表名（不填则自动根据 model-name + table-prefix 推导）
    id-type: String             # 主键类型：String（默认）或 Long
    id-is-auto: false           # 主键是否自增（默认 false，使用 UUID）
    super-class: com.mcst...    # Entity 父类全路径（有默认值，一般不用改）
    only-repository: false      # 是否只生成 Repository 层，不生成 Service/Controller
    create-controller: true     # 是否为该 Model 生成 Controller
    table-prefix: base_         # 单独指定该 Model 的表前缀（覆盖全局 table-prefix）
    forbidden-filed: field1,field2  # 需要排除的字段，逗号分隔，生成时会跳过这些字段
    model-resource-sort: 10100  # 该 Model 的资源权限排序值
```

> `from-db-tables` 和 `model-list` 可以同时使用。如果同一个 Model 在两边都有定义，`model-list` 中的配置优先（可用于覆盖自动解析的默认值）。

#### 资源权限配置（可选）

用于在 Controller 接口上自动生成资源权限注解，与 EasyFK 框架权限体系对接：

| 配置项 | 必填 | 默认值 | 说明 |
|--------|------|--------|------|
| `create-resource-annotation` | 否 | `false` | 是否生成资源权限注解 |
| `resource-group` | 否 | - | 资源所属分组名称 |
| `resource-name` | 否 | - | 资源权限名称 |
| `resource-id` | 否 | - | 资源权限 ID |
| `resource-path` | 否 | - | 资源权限指向的前端路由路径 |
| `resource-sort` | 否 | `10000` | 资源组权限排序值 |
| `module-resource-sort` | 否 | `10000` | 当前模块资源排序基础值 |
| `increment-star` | 否 | `100` | 多个 Model 排序时的自增步长 |

> 以上资源权限配置也可以在 `model-list` 中为每个 Model 单独指定（字段名相同），单独指定的值优先于全局配置。

### 最小配置示例

只需填必填项即可运行：

```yaml
easyfk:
  config:
    generator:
      project:
        project-dir: D:\workspace\projects
        group-id: com.example
        project-name: my-app
        base-package: com.example.myapp
        app-type: BMS
      code:
        module-name: myapp
        db-short-url: localhost:3306/my_database
        db-pwd: 123456
        from-db-tables: product,order
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

MySQL、PostgreSQL、Oracle、MariaDB、SQL Server、达梦、人大金仓、GaussDB、OceanBase、ClickHouse、DB2、H2、SQLite、GBase、OSCAR、Sybase、Firebird 等 23 种数据库。完整列表见上方「支持的数据库类型」表格。

**Q: 配置文件格式和 Spring Boot 的 application.yml 一样吗？**

完全一致。如果你已有 Spring Boot 项目中的生成器配置，直接复制过来即可。

**Q: `efg init` 会覆盖已有的配置文件吗？**

不会。如果目标文件已存在，`efg init` 会提示警告并退出，不覆盖。

**Q: 安装目录在哪里？如何完全卸载？**

安装目录为 Windows `%USERPROFILE%\.easyfk-gen`、macOS / Linux `~/.easyfk-gen`。运行卸载脚本（`uninstall.bat` 或 `./uninstall`）会自动删除该目录并清理 PATH。macOS / Linux 卸载后需手动删除 shell 配置文件（如 `~/.zshrc`）中的 PATH 行。
