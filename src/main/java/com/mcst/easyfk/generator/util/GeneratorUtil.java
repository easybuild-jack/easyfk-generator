package com.mcst.easyfk.generator.util;

import com.mcst.easyfk.core.exception.BusinessException;
import com.mcst.easyfk.core.utils.common.EmptyUtil;
import com.mcst.easyfk.core.utils.common.StringUtil;
import com.mcst.easyfk.core.utils.date.LocalDateUtil;
import com.mcst.easyfk.generator.enums.DbType;
import com.mcst.easyfk.generator.enums.OrmType;
import com.mcst.easyfk.generator.properties.CodeProperties;
import com.mcst.easyfk.generator.vo.FieldInfo;
import com.mcst.easyfk.generator.vo.ModelInfo;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author eb-jack
 * @version 1.0
 */
@Slf4j
public class GeneratorUtil {

    private static final List<String> parentField = List.of("deleted", "insertTime", "lastUpdateTime");

    public static final String MYSQL_DRIVER = "com.mysql.cj.jdbc.Driver";
    public static final String POSTGRESQL_DRIVER = "org.postgresql.Driver";
    public static final String ORACLE_DRIVER = "oracle.jdbc.driver.OracleDriver";

    public static final String MYSQL_URL_TEMPLATE8 = "jdbc:mysql://%s?characterEncoding=UTF-8&useSSL=false&useInformationSchema=true&remarks=true&useUnicode=true&allowPublicKeyRetrieval=true";
    public static final String POSTGRESQL_URL_TEMPLATE = "jdbc:postgresql://%s";
    public static final String ORACLE_URL_TEMPLATE = "jdbc:oracle:thin:@%s";

    /**
     * 根据 ORM 类型和是否继承框架基类，自动解析 Entity 的父类全限定名
     */
    public static String resolveSuperClass(OrmType ormType, boolean extendsSupperClass) {
        if (extendsSupperClass) {
            return switch (ormType) {
                case MYBATIS -> "com.mcst.easyfk.service.mybatisplus.persistence.BaseMyBatisPlusEntity";
                case MYBATIS_FLEX -> "com.mcst.easyfk.service.flex.persistence.BaseFlexEntity";
                case HIBERNATE -> "com.mcst.easyfk.service.hibernate.persistence.BaseHibernateEntity";
                default -> null;
            };
        } else {
            return switch (ormType) {
                case MYBATIS -> "com.baomidou.mybatisplus.spring.activerecord.Model";
                case MYBATIS_FLEX -> "com.mybatisflex.core.activerecord.Model";
                default -> null;
            };
        }
    }

    public static String buildModelNameFromTable(String tableName, String tablePrefix) {
        return StringUtil.upperFirstChar(StringUtil.underlineToCamel(removeIgnoredTablePrefix(tableName, tablePrefix)));
    }

    public static String removeIgnoredTablePrefix(String tableName, String tablePrefix) {
        String modelName = tableName;
        if (EmptyUtil.isEmpty(modelName) || EmptyUtil.isEmpty(tablePrefix)) {
            return modelName;
        }
        for (String prefix : splitTablePrefixes(tablePrefix)) {
            if (modelName.startsWith(prefix)) {
                modelName = modelName.substring(prefix.length());
                if (modelName.startsWith("_")) {
                    modelName = modelName.substring(1);
                }
                break;
            }
        }
        return modelName;
    }

    public static String getPrimaryTablePrefix(String tablePrefix) {
        List<String> prefixes = splitTablePrefixes(tablePrefix);
        return prefixes.isEmpty() ? null : prefixes.get(0);
    }

    private static List<String> splitTablePrefixes(String tablePrefix) {
        List<String> prefixes = new ArrayList<>();
        if (EmptyUtil.isEmpty(tablePrefix)) {
            return prefixes;
        }
        for (String prefix : tablePrefix.split(",")) {
            String value = prefix.trim();
            if (!value.isEmpty()) {
                prefixes.add(value);
            }
        }
        return prefixes;
    }

    public static List<ModelInfo> getModelInfoList(CodeProperties codeProperties, OrmType ormType, boolean init) {
        List<ModelInfo> modelList = new ArrayList<>();
        boolean extendsSupperClass = codeProperties.getExtendsSupperClass();
        String dbTables = codeProperties.getDbTables();
        if (EmptyUtil.isNotEmpty(dbTables)) {
            String[] tables = dbTables.split(",");
            for (String table : tables) {
                table = table.trim();
                if (EmptyUtil.isEmpty(table)) {
                    continue;
                }
                ModelInfo modelInfo = new ModelInfo();
                modelInfo.setModelName(buildModelNameFromTable(table, codeProperties.getTablePrefix()));
                modelInfo.setTableName(table);
                modelList.add(modelInfo);
                if (init) {
                    try {
                        getModelInfoFromDatabase(codeProperties, table, modelInfo, extendsSupperClass);
                        if (EmptyUtil.isEmpty(modelInfo.getFieldList())) {
                            throw new BusinessException("从数据库未读取到表字段：" + table + "，请检查表名、schema、db-type、连接地址是否正确");
                        }
                    } catch (SQLException e) {
                        throw new BusinessException("从数据库读取表字段失败：" + table + "，原因：" + e.getMessage());
                    }
                }
            }
        }
        if (EmptyUtil.isNotEmpty(codeProperties.getModelList())) {
            for (ModelInfo overrideModelInfo : codeProperties.getModelList()) {
                ModelInfo matchedModelInfo = findMatchedModelInfo(modelList, overrideModelInfo);
                if (matchedModelInfo != null) {
                    mergeModelInfo(matchedModelInfo, overrideModelInfo);
                } else {
                    // 后续流程会改写 ModelInfo（父类、字段、导入包等），不能直接改调用方的配置对象：
                    // 同一个 CodeProperties 被重复或并发使用（Spring 单例、服务端）时会互相污染
                    modelList.add(copyModelInfo(overrideModelInfo));
                }
            }
        }

        if (EmptyUtil.isEmpty(modelList)) {
            return null;
        }
        validateModelNameUnique(modelList);

        AtomicInteger sort = new AtomicInteger(codeProperties.getIncrementStar());
        modelList.forEach(modelInfo -> {
            if (EmptyUtil.isEmpty(modelInfo.getSuperClass())) {
                modelInfo.setSuperClass(resolveSuperClass(ormType, extendsSupperClass));
            }
            if (EmptyUtil.isEmpty(modelInfo.getTableName())) {
                String tablePrefix = Optional.ofNullable(modelInfo.getTablePrefix())
                        .orElse(codeProperties.getTablePrefix());
                String primaryTablePrefix = getPrimaryTablePrefix(tablePrefix);
                if (EmptyUtil.isNotEmpty(primaryTablePrefix)) {
                    if (!primaryTablePrefix.endsWith("_")) {
                        primaryTablePrefix = primaryTablePrefix + "_";
                    }
                    modelInfo.setTableName(primaryTablePrefix + StringUtil.camelToUnderline(modelInfo.getModelName()));
                } else {
                    modelInfo.setTableName(StringUtil.camelToUnderline(modelInfo.getModelName()));
                }
            }
            // 处理自动生成资源注解
            if (codeProperties.getCreateResourceAnnotation()) {
                if (EmptyUtil.isEmpty(modelInfo.getResourceGroup())) {
                    modelInfo.setResourceGroup(codeProperties.getResourceGroup());
                }
                if (EmptyUtil.isEmpty(modelInfo.getResourceName())) {
                    modelInfo.setResourceName(codeProperties.getResourceName());
                }
                if (EmptyUtil.isEmpty(modelInfo.getResourceId())) {
                    modelInfo.setResourceId(codeProperties.getResourceId());
                }
                if (EmptyUtil.isEmpty(modelInfo.getResourcePath())) {
                    modelInfo.setResourcePath(codeProperties.getResourcePath());
                }
                if (EmptyUtil.isNotEmpty(modelInfo.getResourcePath()) && !modelInfo.getResourcePath().startsWith("/")) {
                    modelInfo.setResourcePath("/" + modelInfo.getResourcePath());
                }
                int moduleSort = codeProperties.getModuleResourceSort();
                int resourctGruopSort = codeProperties.getResourceSort();
                if (moduleSort < 1000) {
                    moduleSort = 1000;
                }
                if (resourctGruopSort < 1000) {
                    resourctGruopSort = 1000;
                }
                if (EmptyUtil.isEmpty(modelInfo.getResourceSort())) {
                    modelInfo.setResourceSort(resourctGruopSort);
                }
                if (EmptyUtil.isEmpty(modelInfo.getModelResourceSort())) {
                    modelInfo.setModelResourceSort(moduleSort + sort.get());
                    sort.set(sort.get() + 100);
                }
            }
        });
        return modelList;
    }

    private static ModelInfo findMatchedModelInfo(List<ModelInfo> modelList, ModelInfo overrideModelInfo) {
        if (EmptyUtil.isNotEmpty(overrideModelInfo.getTableName())) {
            for (ModelInfo modelInfo : modelList) {
                if (overrideModelInfo.getTableName().equals(modelInfo.getTableName())) {
                    return modelInfo;
                }
            }
        }
        if (EmptyUtil.isNotEmpty(overrideModelInfo.getModelName())) {
            for (ModelInfo modelInfo : modelList) {
                if (overrideModelInfo.getModelName().equals(modelInfo.getModelName())) {
                    return modelInfo;
                }
            }
        }
        return null;
    }

    private static ModelInfo copyModelInfo(ModelInfo source) {
        return new ModelInfo()
                .setModelName(source.getModelName())
                .setTableName(source.getTableName())
                .setModelDesc(source.getModelDesc())
                .setSuperClass(source.getSuperClass())
                .setIdType(source.getIdType())
                .setIdIsAuto(source.getIdIsAuto())
                .setOnlyRepository(source.getOnlyRepository())
                .setFieldList(source.getFieldList())
                .setBasicPackages(source.getBasicPackages())
                .setOtherPackages(source.getOtherPackages())
                .setForbiddenFiled(source.getForbiddenFiled())
                .setResourceGroup(source.getResourceGroup())
                .setResourceName(source.getResourceName())
                .setResourceId(source.getResourceId())
                .setResourcePath(source.getResourcePath())
                .setResourceSort(source.getResourceSort())
                .setModelResourceSort(source.getModelResourceSort())
                .setTablePrefix(source.getTablePrefix())
                .setCreateController(source.getCreateController());
    }

    private static void mergeModelInfo(ModelInfo modelInfo, ModelInfo overrideModelInfo) {
        if (EmptyUtil.isNotEmpty(overrideModelInfo.getModelName())) {
            modelInfo.setModelName(overrideModelInfo.getModelName());
        }
        if (EmptyUtil.isNotEmpty(overrideModelInfo.getTableName())) {
            modelInfo.setTableName(overrideModelInfo.getTableName());
        }
        if (EmptyUtil.isNotEmpty(overrideModelInfo.getModelDesc())) {
            modelInfo.setModelDesc(overrideModelInfo.getModelDesc());
        }
        if (EmptyUtil.isNotEmpty(overrideModelInfo.getSuperClass())) {
            modelInfo.setSuperClass(overrideModelInfo.getSuperClass());
        }
        if (EmptyUtil.isNotEmpty(overrideModelInfo.getIdType())) {
            modelInfo.setIdType(overrideModelInfo.getIdType());
        }
        if (EmptyUtil.isNotEmpty(overrideModelInfo.getTablePrefix())) {
            modelInfo.setTablePrefix(overrideModelInfo.getTablePrefix());
        }
        if (EmptyUtil.isNotEmpty(overrideModelInfo.getForbiddenFiled())) {
            modelInfo.setForbiddenFiled(overrideModelInfo.getForbiddenFiled());
        }
        if (EmptyUtil.isNotEmpty(overrideModelInfo.getResourceGroup())) {
            modelInfo.setResourceGroup(overrideModelInfo.getResourceGroup());
        }
        if (EmptyUtil.isNotEmpty(overrideModelInfo.getResourceName())) {
            modelInfo.setResourceName(overrideModelInfo.getResourceName());
        }
        if (EmptyUtil.isNotEmpty(overrideModelInfo.getResourceId())) {
            modelInfo.setResourceId(overrideModelInfo.getResourceId());
        }
        if (EmptyUtil.isNotEmpty(overrideModelInfo.getResourcePath())) {
            modelInfo.setResourcePath(overrideModelInfo.getResourcePath());
        }
        if (overrideModelInfo.getIdIsAuto() != null) {
            modelInfo.setIdIsAuto(overrideModelInfo.getIdIsAuto());
        }
        if (overrideModelInfo.getOnlyRepository() != null) {
            modelInfo.setOnlyRepository(overrideModelInfo.getOnlyRepository());
        }
        if (overrideModelInfo.getCreateController() != null) {
            modelInfo.setCreateController(overrideModelInfo.getCreateController());
        }
        if (overrideModelInfo.getResourceSort() != null) {
            modelInfo.setResourceSort(overrideModelInfo.getResourceSort());
        }
        if (overrideModelInfo.getModelResourceSort() != null) {
            modelInfo.setModelResourceSort(overrideModelInfo.getModelResourceSort());
        }
        if (EmptyUtil.isNotEmpty(overrideModelInfo.getFieldList())) {
            modelInfo.setFieldList(overrideModelInfo.getFieldList());
        }
        if (EmptyUtil.isNotEmpty(overrideModelInfo.getBasicPackages())) {
            modelInfo.setBasicPackages(overrideModelInfo.getBasicPackages());
        }
        if (EmptyUtil.isNotEmpty(overrideModelInfo.getOtherPackages())) {
            modelInfo.setOtherPackages(overrideModelInfo.getOtherPackages());
        }
    }

    private static void validateModelNameUnique(List<ModelInfo> modelList) {
        Set<String> modelNameSet = new HashSet<>(modelList.size());
        for (ModelInfo modelInfo : modelList) {
            if (EmptyUtil.isEmpty(modelInfo.getModelName())) {
                continue;
            }
            if (!modelNameSet.add(modelInfo.getModelName())) {
                throw new BusinessException("Model名称重复：" + modelInfo.getModelName() + "，同一个Model不能重复定义");
            }
        }
    }

    /**
     * 生成失败时抛给调用方（CLI / IDEA 插件 / 服务端）的异常，保留原始异常便于排查
     */
    public static BusinessException generateException(String message, Throwable cause) {
        BusinessException exception = new BusinessException(message + "，原因：" + cause.getMessage());
        exception.initCause(cause);
        return exception;
    }

    public static Map<String, Object> createBaseParamMap(CodeProperties codeProperties) {
        Map<String, Object> paramMap = new HashMap<>(10);
        paramMap.put("author", codeProperties.getAuthor());
        paramMap.put("date", LocalDateUtil.localDateToString(LocalDate.now()));
        return paramMap;
    }

    public static boolean hasModelSource(CodeProperties codeProperties) {
        return EmptyUtil.isNotEmpty(codeProperties.getDbTables())
                || EmptyUtil.isNotEmpty(codeProperties.getModelList());
    }

    public static void getModelInfoFromDatabase(CodeProperties codeProperties, String tableName, ModelInfo modelInfo,
            boolean filterParentFields) throws SQLException {
        List<FieldInfo> fieldInfoList = new ArrayList<>();

        String jdbcUrl = codeProperties.getDbShortUrl();
        // 从jdbcUrl中提取数据库名称
        String databaseName = extractDatabaseName(jdbcUrl);
        if (EmptyUtil.isEmpty(databaseName)) {
            throw new BusinessException("jdbcUrl格式错误，无法提取数据库名称");
        }

        // 构建不包含数据库名称的连接字符串，并加载对应驱动
        String driverClass = null;
        if (DbType.MYSQL.equals(codeProperties.getDbType())) {
            jdbcUrl = String.format(MYSQL_URL_TEMPLATE8, codeProperties.getDbShortUrl());
            driverClass = MYSQL_DRIVER;
        } else if (DbType.POSTGRE_SQL.equals(codeProperties.getDbType())) {
            jdbcUrl = String.format(POSTGRESQL_URL_TEMPLATE, codeProperties.getDbShortUrl());
            driverClass = POSTGRESQL_DRIVER;
        } else if (DbType.ORACLE_12C.equals(codeProperties.getDbType())
                || DbType.ORACLE.equals(codeProperties.getDbType())) {
            jdbcUrl = String.format(ORACLE_URL_TEMPLATE, codeProperties.getDbShortUrl());
            driverClass = ORACLE_DRIVER;
        }
        if (driverClass != null) {
            try {
                Class.forName(driverClass);
            } catch (ClassNotFoundException ignored) {
            }
        }
        Connection connection = DriverManager.getConnection(jdbcUrl, codeProperties.getDbUser(),
                codeProperties.getDbPwd());
        DatabaseMetaData metaData = connection.getMetaData();

        // PostgreSQL 不同数据库/驱动下 schema 可能不是固定的 public，因此先定位真实的表元数据
        String catalogPattern = DbType.POSTGRE_SQL.equals(codeProperties.getDbType()) ? null : databaseName;
        String schemaPattern = DbType.POSTGRE_SQL.equals(codeProperties.getDbType()) ? connection.getSchema() : null;
        if (EmptyUtil.isEmpty(schemaPattern) && DbType.POSTGRE_SQL.equals(codeProperties.getDbType())) {
            schemaPattern = "public";
        }
        String tableCatalog = catalogPattern;
        String tableSchema = schemaPattern;
        ResultSet tables = metaData.getTables(catalogPattern, schemaPattern, tableName, new String[] { "TABLE" });
        boolean tableFound = tables.next();
        if (!tableFound && DbType.POSTGRE_SQL.equals(codeProperties.getDbType())) {
            tables = metaData.getTables(null, null, tableName, new String[] { "TABLE" });
            tableFound = tables.next();
        }
        if (tableFound) {
            tableCatalog = tables.getString("TABLE_CAT");
            tableSchema = tables.getString("TABLE_SCHEM");
            if (EmptyUtil.isEmpty(modelInfo.getModelDesc())) {
                String tableComment = tables.getString("REMARKS");
                if (EmptyUtil.isNotEmpty(tableComment)) {
                    modelInfo.setModelDesc(tableComment);
                }
            }
        }

        ResultSet columns = metaData.getColumns(tableCatalog, tableSchema, tableName, null);
        ResultSet primaryKeys = metaData.getPrimaryKeys(tableCatalog, tableSchema, tableName);
        while (columns.next()) {
            createFieldInfo(columns, fieldInfoList, filterParentFields);
        }
        if (EmptyUtil.isNotEmpty(fieldInfoList)) {
            while (primaryKeys.next()) {
                String columnName = StringUtil.underlineToCamel(primaryKeys.getString("COLUMN_NAME"));
                for (FieldInfo fieldInfo : fieldInfoList) {
                    if (fieldInfo.getName().equals(columnName)) {
                        fieldInfo.setKeyFlag(true);
                        if (fieldInfo.getAutoIncrement()) {
                            modelInfo.setIdIsAuto(true);
                        }
                        break;
                    }
                }
            }
        }
        connection.close();
        if (EmptyUtil.isNotEmpty(fieldInfoList)) {
            modelInfo.setFieldList(fieldInfoList);
        }
    }

    /**
     * 从jdbcUrl中提取数据库名称
     *
     * @param jdbcUrl 格式如: 127.0.0.1:3306/flopotech
     * @return 数据库名称
     */
    private static String extractDatabaseName(String jdbcUrl) {
        if (EmptyUtil.isEmpty(jdbcUrl)) {
            return null;
        }

        // 查找最后一个斜杠的位置
        int lastSlashIndex = jdbcUrl.lastIndexOf('/');
        if (lastSlashIndex != -1 && lastSlashIndex < jdbcUrl.length() - 1) {
            return jdbcUrl.substring(lastSlashIndex + 1);
        }

        return null;
    }

    private static void createFieldInfo(ResultSet columns, List<FieldInfo> fieldInfoList, boolean filterParentFields)
            throws SQLException {
        FieldInfo fieldInfo = new FieldInfo();
        String fieldName = StringUtil.underlineToCamel(columns.getString("COLUMN_NAME"));
        if (filterParentFields && parentField.contains(fieldName)) {
            return;
        }
        fieldInfo.setName(fieldName);
        String fieldType = columns.getString("TYPE_NAME").toUpperCase();
        switch (fieldType) {
            case "DATETIME", "TIMESTAMP", "TIMESTAMPTZ", "TIMESTAMP WITH TIME ZONE" ->
                fieldInfo.setType("LocalDateTime");
            case "DATE" -> fieldInfo.setType("LocalDate");
            case "TIME" -> fieldInfo.setType("LocalTime");
            case "DECIMAL", "NUMERIC" -> fieldInfo.setType("BigDecimal");
            case "TINYINT", "BOOLEAN", "BOOL", "BIT" -> fieldInfo.setType("Boolean");
            case "INT", "INT4", "INTEGER", "SMALLINT", "INT2", "SERIAL", "SMALLSERIAL" -> fieldInfo.setType("Integer");
            case "BIGINT", "NUMBER", "BIGSERIAL", "IDENTITY", "INT8" -> fieldInfo.setType("Long");
            case "REAL", "FLOAT4" -> fieldInfo.setType("Float");
            case "DOUBLE", "DOUBLE PRECISION", "FLOAT", "FLOAT8" -> fieldInfo.setType("Double");
            case "BYTEA", "BLOB", "VARBINARY", "BINARY" -> fieldInfo.setType("byte[]");
            case "UUID" -> fieldInfo.setType("UUID");
            default -> fieldInfo.setType("String");
        }
        fieldInfo.setLength(columns.getInt("COLUMN_SIZE"));
        fieldInfo.setDesc(columns.getString("REMARKS"));
        fieldInfo.setDefaultValue(columns.getString("COLUMN_DEF"));
        fieldInfo.setDecimalLength(columns.getInt("DECIMAL_DIGITS"));
        String isAutoIncrement = columns.getString("IS_AUTOINCREMENT");
        if ("YES".equalsIgnoreCase(isAutoIncrement)) {
            fieldInfo.setAutoIncrement(true);
        }
        fieldInfoList.add(fieldInfo);
    }
}
