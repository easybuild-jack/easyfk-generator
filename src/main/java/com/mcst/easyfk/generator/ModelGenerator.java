package com.mcst.easyfk.generator;

import com.mcst.easyfk.core.exception.BusinessException;
import com.mcst.easyfk.core.utils.common.EmptyUtil;
import com.mcst.easyfk.core.utils.common.StringUtil;
import com.mcst.easyfk.generator.enums.OrmType;
import com.mcst.easyfk.generator.enums.ProjectType;
import com.mcst.easyfk.generator.properties.CodeProperties;
import com.mcst.easyfk.generator.properties.ProjectProperties;
import com.mcst.easyfk.generator.util.CreateFileUtil;
import com.mcst.easyfk.generator.util.GeneratorUtil;
import com.mcst.easyfk.generator.vo.ModelInfo;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author eb-jack
 * @version 1.0
 */
@Slf4j
public class ModelGenerator {
    private static final String BASE_TEMPLATE_PATH = "/templates/code";

    private static final String CODE_DIR_PRE = File.separator + "src" + File.separator + "main" + File.separator + "java" + File.separator;
    private static final String RESOURCE_DIR_PRE = File.separator + "src" + File.separator + "main" + File.separator + "resources" + File.separator;

    @Resource
    private ProjectProperties projectProperties;

    @Resource
    private CodeProperties codeProperties;

    public ModelGenerator() {}

    public ModelGenerator(ProjectProperties projectProperties, CodeProperties codeProperties) {
        this.projectProperties = projectProperties;
        this.codeProperties = codeProperties;
    }

    public void create() {
        if (ProjectType.MICRO_PRD.equals(projectProperties.getProjectType())) {
            log.info("项目类型为 MICRO_PRD，跳过 Model 生成");
            return;
        }
        if (ProjectType.SMART_ORM.equals(projectProperties.getProjectType())) {
            createSmartOrmModel();
            return;
        }
        if (OrmType.NONE.equals(projectProperties.getOrmType())) {
            log.info("OrmType 为 NONE，跳过 Model 生成");
            return;
        }
        if (!GeneratorUtil.hasModelSource(codeProperties)) {
            throw new BusinessException("db-tables 和 model-list 不能同时为空");
        }
        try {
            String basePackage = projectProperties.getBasePackage();
            if (EmptyUtil.isEmpty(basePackage)) {
                throw new BusinessException("项目包根路径不可为空");
            }
            String projectRootDir = projectProperties.getProjectDir() + File.separator + projectProperties.getProjectName();
            String serverProjectOutputDir;
            String serverBasePackage = basePackage + ".server";
            String apiBasePackage = basePackage + ".api";
            if (ProjectType.SINGLE.equals(projectProperties.getProjectType())) {
                serverProjectOutputDir = projectRootDir;
                serverBasePackage = basePackage + ".service";
            } else {
                serverProjectOutputDir = projectRootDir + File.separator + projectProperties.getProjectName() + "-server";
            }
            String mapperXMLOutputDir = serverProjectOutputDir + RESOURCE_DIR_PRE + "mappings";
            String entityPackage = serverBasePackage + ".persistence.model";
            String mapperPackage = serverBasePackage + ".persistence.mapper";
            String dtoPackage = apiBasePackage + ".dto";
            Map<String, Object> paramMap = GeneratorUtil.createBaseParamMap(codeProperties);
            paramMap.put("package", entityPackage);
            paramMap.put("mapperPackage", mapperPackage);
            paramMap.put("dtoPackage", dtoPackage);
            paramMap.put("ormType", projectProperties.getOrmType());
            List<ModelInfo> modelList = GeneratorUtil.getModelInfoList(codeProperties, projectProperties.getOrmType(), true);
            if (EmptyUtil.isEmpty(modelList)) {
                throw new BusinessException("无Model信息");
            }
            for (ModelInfo modelInfo : modelList) {
                try {
                    paramMap.put("modelInfo", modelInfo);
                    String modelPath = serverProjectOutputDir + CODE_DIR_PRE + entityPackage.replace(".", File.separator) + File.separator + StringUtil.upperFirstChar(modelInfo.getModelName()) + ".java";
                    if (projectProperties.getOrmType().equals(OrmType.MYBATIS)) {
                        CreateFileUtil.delAndCreateFile("mybatis/my_entity.java.ftl", modelPath, paramMap, BASE_TEMPLATE_PATH);
                    } else if (projectProperties.getOrmType().equals(OrmType.MYBATIS_FLEX)) {
                        CreateFileUtil.delAndCreateFile("mybatis/flex_entity.java.ftl", modelPath, paramMap, BASE_TEMPLATE_PATH);
                    } else if (projectProperties.getOrmType().equals(OrmType.HIBERNATE)) {
                        CreateFileUtil.delAndCreateFile("mybatis/hibernate_entity.java.ftl", modelPath, paramMap, BASE_TEMPLATE_PATH);
                    } else {
                        CreateFileUtil.delAndCreateFile("mybatis/my_entity.java.ftl", modelPath, paramMap, BASE_TEMPLATE_PATH);
                    }
                    String mapperPath = serverProjectOutputDir + CODE_DIR_PRE + mapperPackage.replace(".", File.separator) + File.separator + StringUtil.upperFirstChar(modelInfo.getModelName()) + "Mapper.java";
                    if (projectProperties.getOrmType().equals(OrmType.HIBERNATE)) {
                        CreateFileUtil.createOnNotExistFile("mybatis/jpa_repository.java.ftl", mapperPath, paramMap, BASE_TEMPLATE_PATH);
                    } else {
                        String mapperXmlPath = mapperXMLOutputDir + File.separator + StringUtil.upperFirstChar(modelInfo.getModelName()) + "Mapper.xml";
                        CreateFileUtil.createOnNotExistFile("mybatis/myMapper.xml.ftl", mapperXmlPath, paramMap, BASE_TEMPLATE_PATH);
                        CreateFileUtil.createOnNotExistFile("mybatis/mapper.java.ftl", mapperPath, paramMap, BASE_TEMPLATE_PATH);
                    }
                } catch (Exception ex) {
                    throw new BusinessException("生成模型失败：" + modelInfo.getModelName() + "，原因：" + ex.getMessage());
                }
            }
            log.info("生成Model成功！");
        } catch (Exception e) {
            log.error("生成Model失败：{}", e.getMessage());
            if (e instanceof BusinessException businessException) {
                throw businessException;
            }
            throw new BusinessException("生成Model失败：" + e.getMessage());
        }
    }

    /**
     * SMART_ORM 模式：Model 生成到各 ORM 实现模块（plus/flex/hibernate）中。
     * 每个 ORM 模块独立生成实体、Mapper（Hibernate 为 JPA Repository），
     * Mapper XML 仅 MyBatis(plus) 生成。
     */
    private void createSmartOrmModel() {
        if (!GeneratorUtil.hasModelSource(codeProperties)) {
            throw new BusinessException("db-tables 和 model-list 不能同时为空");
        }
        try {
            String basePackage = projectProperties.getBasePackage();
            if (EmptyUtil.isEmpty(basePackage)) {
                throw new BusinessException("项目包根路径不可为空");
            }
            String projectRootDir = projectProperties.getProjectDir() + File.separator
                    + projectProperties.getProjectName();
            String apiBasePackage = basePackage + ".api";
            String dtoPackage = apiBasePackage + ".dto";

            List<ModelInfo> modelList = GeneratorUtil.getModelInfoList(codeProperties,
                    projectProperties.getOrmType(), true);
            if (EmptyUtil.isEmpty(modelList)) {
                throw new BusinessException("无Model信息");
            }
            // 记录用户显式配置的父类，多 ORM 循环时保持尊重
            Map<String, String> explicitSuperClass = new HashMap<>();
            for (ModelInfo modelInfo : modelList) {
                if (EmptyUtil.isNotEmpty(modelInfo.getSuperClass())) {
                    explicitSuperClass.put(modelInfo.getModelName(), modelInfo.getSuperClass());
                }
            }
            for (OrmType ormType : projectProperties.getOrmModules()) {
                String ormModuleName = ProjectProperties.ormModuleName(ormType);
                String ormProjectOutputDir = projectRootDir + File.separator
                        + projectProperties.getProjectName() + "-orm" + File.separator
                        + projectProperties.getProjectName() + "-orm-" + ormModuleName;
                String ormBasePackage = basePackage + ".orm." + ormModuleName;
                String entityPackage = ormBasePackage + ".persistence.model";
                String mapperPackage = ormBasePackage + ".persistence.mapper";
                Map<String, Object> paramMap = GeneratorUtil.createBaseParamMap(codeProperties);
                paramMap.put("package", entityPackage);
                paramMap.put("mapperPackage", mapperPackage);
                paramMap.put("dtoPackage", dtoPackage);
                paramMap.put("ormType", ormType);

                for (ModelInfo modelInfo : modelList) {
                    // 每个 ORM 模块使用各自的基类；用户显式配置的父类优先
                    modelInfo.setSuperClass(explicitSuperClass.getOrDefault(modelInfo.getModelName(),
                            GeneratorUtil.resolveSuperClass(ormType,
                                    codeProperties.getExtendsSupperClass())));
                    paramMap.put("modelInfo", modelInfo);
                    try {
                        String modelPath = ormProjectOutputDir + CODE_DIR_PRE
                                + entityPackage.replace(".", File.separator) + File.separator
                                + StringUtil.upperFirstChar(modelInfo.getModelName()) + ".java";
                        String entityTemplate = switch (ormType) {
                            case MYBATIS -> "mybatis/my_entity.java.ftl";
                            case MYBATIS_FLEX -> "mybatis/flex_entity.java.ftl";
                            case HIBERNATE -> "mybatis/hibernate_entity.java.ftl";
                            default -> throw new BusinessException("不支持的 ORM 实现模块类型: " + ormType);
                        };
                        CreateFileUtil.delAndCreateFile(entityTemplate, modelPath, paramMap, BASE_TEMPLATE_PATH);

                        String mapperPath = ormProjectOutputDir + CODE_DIR_PRE
                                + mapperPackage.replace(".", File.separator) + File.separator
                                + StringUtil.upperFirstChar(modelInfo.getModelName()) + "Mapper.java";
                        if (OrmType.HIBERNATE.equals(ormType)) {
                            CreateFileUtil.createOnNotExistFile("mybatis/jpa_repository.java.ftl", mapperPath,
                                    paramMap, BASE_TEMPLATE_PATH);
                        } else {
                            CreateFileUtil.createOnNotExistFile("mybatis/mapper.java.ftl", mapperPath, paramMap,
                                    BASE_TEMPLATE_PATH);
                            if (OrmType.MYBATIS.equals(ormType)) {
                                String mapperXmlPath = ormProjectOutputDir + RESOURCE_DIR_PRE + "mappings"
                                        + File.separator + StringUtil.upperFirstChar(modelInfo.getModelName())
                                        + "Mapper.xml";
                                CreateFileUtil.createOnNotExistFile("mybatis/myMapper.xml.ftl", mapperXmlPath,
                                        paramMap, BASE_TEMPLATE_PATH);
                            }
                        }
                    } catch (Exception ex) {
                        throw new BusinessException("生成模型失败：" + modelInfo.getModelName() + "，原因："
                                + ex.getMessage());
                    }
                }
            }
            log.info("生成Model成功！");
        } catch (Exception e) {
            log.error("生成Model失败：{}", e.getMessage());
            if (e instanceof BusinessException businessException) {
                throw businessException;
            }
            throw new BusinessException("生成Model失败：" + e.getMessage());
        }
    }

}
