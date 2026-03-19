package com.mcst.easyfk.generator.cli;

import com.mcst.easyfk.generator.enums.*;
import com.mcst.easyfk.generator.properties.CodeProperties;
import com.mcst.easyfk.generator.properties.ProjectProperties;
import com.mcst.easyfk.generator.vo.ModelInfo;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * CLI 配置加载器，从 YAML 文件解析生成器配置
 *
 * @author eb-jack
 */
public class CliConfigLoader {

    private final ProjectProperties projectProperties;
    private final CodeProperties codeProperties;

    public CliConfigLoader(String configFilePath) throws IOException {
        Yaml yaml = new Yaml();
        try (InputStream in = new FileInputStream(configFilePath)) {
            Map<String, Object> root = yaml.load(in);
            Map<String, Object> generatorConfig = navigate(root, "easyfk", "config", "generator");
            if (generatorConfig == null) {
                throw new IllegalArgumentException("配置文件格式错误：缺少 easyfk.config.generator 节点");
            }
            this.projectProperties = parseProjectProperties(getMap(generatorConfig, "project"));
            this.codeProperties = parseCodeProperties(getMap(generatorConfig, "code"));
            this.projectProperties.setDbType(this.codeProperties.getDbType());
        }
    }

    public ProjectProperties getProjectProperties() {
        return projectProperties;
    }

    public CodeProperties getCodeProperties() {
        return codeProperties;
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> navigate(Map<String, Object> map, String... keys) {
        Map<String, Object> current = map;
        for (String key : keys) {
            if (current == null) return null;
            Object val = current.get(key);
            if (val instanceof Map) {
                current = (Map<String, Object>) val;
            } else {
                return null;
            }
        }
        return current;
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> getMap(Map<String, Object> map, String key) {
        if (map == null) return null;
        Object val = map.get(key);
        return val instanceof Map ? (Map<String, Object>) val : null;
    }

    private static String getString(Map<String, Object> map, String key) {
        if (map == null) return null;
        Object val = map.get(key);
        return val != null ? val.toString() : null;
    }

    private static Boolean getBoolean(Map<String, Object> map, String key) {
        if (map == null) return null;
        Object val = map.get(key);
        if (val instanceof Boolean) return (Boolean) val;
        if (val != null) return Boolean.parseBoolean(val.toString());
        return null;
    }

    private static Integer getInteger(Map<String, Object> map, String key) {
        if (map == null) return null;
        Object val = map.get(key);
        if (val instanceof Number) return ((Number) val).intValue();
        if (val != null) {
            try { return Integer.parseInt(val.toString()); } catch (NumberFormatException e) { return null; }
        }
        return null;
    }

    private static <E extends Enum<E>> E getEnum(Map<String, Object> map, String key, Class<E> enumType) {
        String val = getString(map, key);
        if (val == null) return null;
        try {
            return Enum.valueOf(enumType, val.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("无效的配置值 " + key + "=" + val + "，可选值: " + java.util.Arrays.toString(enumType.getEnumConstants()));
        }
    }

    private static ProjectProperties parseProjectProperties(Map<String, Object> map) {
        ProjectProperties pp = new ProjectProperties();
        if (map == null) return pp;
        String v;
        Boolean b;
        if ((v = getString(map, "project-name")) != null) pp.setProjectName(v);
        if ((v = getString(map, "project-dir")) != null) pp.setProjectDir(v);
        if ((v = getString(map, "group-id")) != null) pp.setGroupId(v);
        if ((v = getString(map, "artifact-id")) != null) pp.setArtifactId(v);
        if ((v = getString(map, "base-package")) != null) pp.setBasePackage(v);
        if ((v = getString(map, "framework-version")) != null) pp.setFrameworkVersion(v);
        if ((v = getString(map, "project-version")) != null) pp.setProjectVersion(v);

        ProjectType projectType = getEnum(map, "project-type", ProjectType.class);
        if (projectType != null) pp.setProjectType(projectType);

        RpcType rpcType = getEnum(map, "rpc-type", RpcType.class);
        if (rpcType != null) pp.setRpcType(rpcType);

        OrmType ormType = getEnum(map, "orm-type", OrmType.class);
        if (ormType != null) pp.setOrmType(ormType);

        PrdType prdType = getEnum(map, "prd-type", PrdType.class);
        if (prdType != null) pp.setPrdType(prdType);

        BuildType buildType = getEnum(map, "build-type", BuildType.class);
        if (buildType != null) pp.setBuildType(buildType);

        GradleType gradleType = getEnum(map, "gradle-type", GradleType.class);
        if (gradleType != null) pp.setGradleType(gradleType);

        LogType logType = getEnum(map, "log-type", LogType.class);
        if (logType != null) pp.setLogType(logType);

        if ((b = getBoolean(map, "create-repository")) != null) pp.setCreateRepository(b);
        if ((b = getBoolean(map, "controller-auto-config")) != null) pp.setControllerAutoConfig(b);
        if ((b = getBoolean(map, "create-prd-project")) != null) pp.setCreatePrdProject(b);

        return pp;
    }

    @SuppressWarnings("unchecked")
    private static CodeProperties parseCodeProperties(Map<String, Object> map) {
        CodeProperties cp = new CodeProperties();
        if (map == null) return cp;
        String v;
        Boolean b;
        Integer i;

        if ((v = getString(map, "module-name")) != null) cp.setModuleName(v);
        if ((v = getString(map, "author")) != null) cp.setAuthor(v);
        if ((v = getString(map, "table-prefix")) != null) cp.setTablePrefix(v);
        if ((v = getString(map, "db-short-url")) != null) cp.setDbShortUrl(v);
        if ((v = getString(map, "db-user")) != null) cp.setDbUser(v);
        if ((v = getString(map, "db-pwd")) != null) cp.setDbPwd(v);
        if ((v = getString(map, "from-db-tables")) != null) cp.setFromDbTables(v);
        if ((v = getString(map, "resource-group")) != null) cp.setResourceGroup(v);
        if ((v = getString(map, "resource-name")) != null) cp.setResourceName(v);
        if ((v = getString(map, "resource-id")) != null) cp.setResourceId(v);
        if ((v = getString(map, "resource-path")) != null) cp.setResourcePath(v);
        OrmType ormType = getEnum(map, "orm-type", OrmType.class);
        if (ormType != null) cp.setOrmType(ormType);

        DbType dbType = getEnum(map, "db-type", DbType.class);
        if (dbType != null) cp.setDbType(dbType);

        if ((b = getBoolean(map, "spring-annotation")) != null) cp.setSpringAnnotation(b);
        if ((b = getBoolean(map, "create-controller")) != null) cp.setCreateController(b);
        if ((b = getBoolean(map, "extends-supper-class")) != null) cp.setExtendsSupperClass(b);
        if ((b = getBoolean(map, "create-resource-annotation")) != null) cp.setCreateResourceAnnotation(b);

        if ((i = getInteger(map, "resource-sort")) != null) cp.setResourceSort(i);
        if ((i = getInteger(map, "module-resource-sort")) != null) cp.setModuleResourceSort(i);
        if ((i = getInteger(map, "increment-star")) != null) cp.setIncrementStar(i);

        Object modelListObj = map.get("model-list");
        if (modelListObj instanceof List<?> rawList) {
            List<ModelInfo> modelList = new ArrayList<>();
            for (Object item : rawList) {
                if (item instanceof Map) {
                    Map<String, Object> m = (Map<String, Object>) item;
                    ModelInfo mi = new ModelInfo();
                    if ((v = getString(m, "model-name")) != null) mi.setModelName(v);
                    if ((v = getString(m, "model-desc")) != null) mi.setModelDesc(v);
                    if ((v = getString(m, "table-name")) != null) mi.setTableName(v);
                    if ((v = getString(m, "super-class")) != null) mi.setSuperClass(v);
                    if ((v = getString(m, "id-type")) != null) mi.setIdType(v);
                    if ((v = getString(m, "table-prefix")) != null) mi.setTablePrefix(v);
                    if ((v = getString(m, "forbidden-filed")) != null) mi.setForbiddenFiled(v);
                    if ((v = getString(m, "resource-group")) != null) mi.setResourceGroup(v);
                    if ((v = getString(m, "resource-name")) != null) mi.setResourceName(v);
                    if ((v = getString(m, "resource-id")) != null) mi.setResourceId(v);
                    if ((v = getString(m, "resource-path")) != null) mi.setResourcePath(v);

                    Boolean mb;
                    Integer mi2;
                    if ((mb = getBoolean(m, "id-is-auto")) != null) mi.setIdIsAuto(mb);
                    if ((mb = getBoolean(m, "only-repository")) != null) mi.setOnlyRepository(mb);
                    if ((mb = getBoolean(m, "create-controller")) != null) mi.setCreateController(mb);
                    if ((mi2 = getInteger(m, "resource-sort")) != null) mi.setResourceSort(mi2);
                    if ((mi2 = getInteger(m, "model-resource-sort")) != null) mi.setModelResourceSort(mi2);
                    modelList.add(mi);
                }
            }
            cp.setModelList(modelList);
        }

        return cp;
    }
}
