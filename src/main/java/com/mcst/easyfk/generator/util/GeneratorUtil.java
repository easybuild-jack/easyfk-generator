package com.mcst.easyfk.generator.util;

import com.mcst.easyfk.core.exception.BusinessException;
import com.mcst.easyfk.core.utils.common.EmptyUtil;
import com.mcst.easyfk.core.utils.common.StringUtil;
import com.mcst.easyfk.core.utils.date.LocalDateUtil;
import com.mcst.easyfk.generator.enums.DbType;
import com.mcst.easyfk.generator.properties.CodeProperties;
import com.mcst.easyfk.generator.vo.FieldInfo;
import com.mcst.easyfk.generator.vo.ModelInfo;

import java.sql.*;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author eb-jack
 * @version 1.0
 */
public class GeneratorUtil {

    private static final List<String> parentField = List.of("deleted", "insertTime", "lastUpdateTime");

    public static final String MYSQL_DRIVER = "com.mysql.cj.jdbc.Driver";
    public static final String POSTGRESQL_DRIVER = "org.postgresql.Driver";
    public static final String ORACLE_DRIVER = "oracle.jdbc.driver.OracleDriver";

    public static final String MYSQL_URL_TEMPLATE8 = "jdbc:mysql://%s?characterEncoding=UTF-8&useSSL=false&useInformationSchema=true&remarks=true&useUnicode=true&allowPublicKeyRetrieval=true";
    public static final String POSTGRESQL_URL_TEMPLATE = "jdbc:postgresql://%s";
    public static final String ORACLE_URL_TEMPLATE = "jdbc:oracle:thin:@%s";

    public static List<ModelInfo> getModelInfoList(CodeProperties codeProperties, boolean init) {
        List<ModelInfo> modelList = codeProperties.getModelList();
        Map<String, ModelInfo> modelInfoMap = new HashMap<>(10);
        if (EmptyUtil.isEmpty(modelList)) {
            modelList = new ArrayList<>();
        } else {
            // modelInfo to map
            for (ModelInfo modelInfo : modelList) {
                modelInfoMap.put(modelInfo.getModelName(), modelInfo);
            }
        }

        String fromDbTables = codeProperties.getFromDbTables();
        if (EmptyUtil.isNotEmpty(fromDbTables)) {
            String[] tables = fromDbTables.split(",");
            for (String table : tables) {
                ModelInfo modelInfo = new ModelInfo();
                String modelName = EmptyUtil.isEmpty(codeProperties.getTablePrefix()) ? table : table.replace(codeProperties.getTablePrefix(), "");
                // 转驼峰 并首字母大写
                modelInfo.setModelName(StringUtil.upperFirstChar(StringUtil.underlineToCamel(modelName)));
                if (modelInfoMap.containsKey(modelInfo.getModelName())) {
                    modelInfo = modelInfoMap.get(modelInfo.getModelName());
                } else {
                    modelInfoMap.put(modelInfo.getModelName(), modelInfo);
                    modelList.add(modelInfo);
                }
                if (init) {
                    try {
                        getModelInfoFromDatabase(codeProperties, table, modelInfo);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        if (EmptyUtil.isEmpty(modelList)) {
            return null;
        }

        AtomicInteger sort = new AtomicInteger(codeProperties.getIncrementStar());
        modelList.forEach(modelInfo -> {
            if (EmptyUtil.isEmpty(modelInfo.getTableName())) {
                String tablePrefix = Optional.ofNullable(modelInfo.getTablePrefix()).orElse(codeProperties.getTablePrefix());
                if (EmptyUtil.isNotEmpty(tablePrefix)) {
                    if (!tablePrefix.endsWith("_")) {
                        tablePrefix = tablePrefix + "_";
                    }
                    modelInfo.setTableName(tablePrefix + StringUtil.camelToUnderline(modelInfo.getModelName()));
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

    public static Map<String, Object> createBaseParamMap(CodeProperties codeProperties) {
        Map<String, Object> paramMap = new HashMap<>(10);
        paramMap.put("author", codeProperties.getAuthor());
        paramMap.put("date", LocalDateUtil.localDateToString(LocalDate.now()));
        return paramMap;
    }

    public static void getModelInfoFromDatabase(CodeProperties codeProperties, String tableName, ModelInfo modelInfo) throws SQLException {
        List<FieldInfo> fieldInfoList = new ArrayList<>();

        String jdbcUrl = codeProperties.getDbShortUrl();
        // 从jdbcUrl中提取数据库名称
        String databaseName = extractDatabaseName(jdbcUrl);
        if (EmptyUtil.isEmpty(databaseName)) {
            throw new BusinessException("jdbcUrl格式错误，无法提取数据库名称");
        }

        // 构建不包含数据库名称的连接字符串
        if (DbType.MYSQL.equals(codeProperties.getDbType())) {
            jdbcUrl = String.format(MYSQL_URL_TEMPLATE8, codeProperties.getDbShortUrl());
        } else if (DbType.POSTGRE_SQL.equals(codeProperties.getDbType())) {
            jdbcUrl = String.format(POSTGRESQL_URL_TEMPLATE, codeProperties.getDbShortUrl());

        } else if (DbType.ORACLE_12C.equals(codeProperties.getDbType()) || DbType.ORACLE.equals(codeProperties.getDbType())) {
            jdbcUrl = String.format(ORACLE_URL_TEMPLATE, codeProperties.getDbShortUrl());
        }
        Connection connection = DriverManager.getConnection(jdbcUrl, codeProperties.getDbUser(), codeProperties.getDbPwd());
        DatabaseMetaData metaData = connection.getMetaData();

        if (EmptyUtil.isEmpty(modelInfo.getModelDesc())) {
            // 获取 表注释，明确指定catalog
            ResultSet tables = metaData.getTables(databaseName, null, tableName, new String[]{"TABLE"});
            if (tables.next()) {
                String tableComment = tables.getString("REMARKS");
                if (EmptyUtil.isNotEmpty(tableComment)) {
                    modelInfo.setModelDesc(tableComment);
                }
            }
        }

        // 明确指定catalog获取字段信息
        ResultSet columns = metaData.getColumns(databaseName, null, tableName, null);
        ResultSet primaryKeys = metaData.getPrimaryKeys(databaseName, null, tableName);
        while (columns.next()) {
            createFieldInfo(columns, fieldInfoList);
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

    private static void createFieldInfo(ResultSet columns, List<FieldInfo> fieldInfoList) throws SQLException {
        FieldInfo fieldInfo = new FieldInfo();
        String fieldName = StringUtil.underlineToCamel(columns.getString("COLUMN_NAME"));
        // 排除父类的字段
        if (parentField.contains(fieldName)) {
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
