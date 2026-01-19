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
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author liu yijun
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

    public void create() {
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
            List<ModelInfo> modelList = GeneratorUtil.getModelInfoList(codeProperties, true);
            if (EmptyUtil.isEmpty(modelList)) {
                log.error("无Model信息！");
                return;
            }
            for (ModelInfo modelInfo : modelList) {
                try {
                    paramMap.put("modelInfo", modelInfo);
                    boolean isInfluxDb = Optional.ofNullable(modelInfo.getInfluxDb()).orElse(Boolean.FALSE);
                    String modelPath = serverProjectOutputDir + CODE_DIR_PRE + entityPackage.replace(".", File.separator) + File.separator + StringUtil.upperFirstChar(modelInfo.getModelName()) + ".java";
                    if (isInfluxDb) {
                        CreateFileUtil.delAndCreateFile("model/influxdb_model.java.ftl", modelPath, paramMap, BASE_TEMPLATE_PATH);
                    } else {
                        if (codeProperties.getOrmType().equals(OrmType.MYBATIS)) {
                            CreateFileUtil.delAndCreateFile("mybatis/my_entity.java.ftl", modelPath, paramMap, BASE_TEMPLATE_PATH);
                        } else {
                            CreateFileUtil.delAndCreateFile("mybatis/flex_entity.java.ftl", modelPath, paramMap, BASE_TEMPLATE_PATH);
                        }
                        String mapperXmlPath = mapperXMLOutputDir + File.separator + StringUtil.upperFirstChar(modelInfo.getModelName()) + "Mapper.xml";
                        CreateFileUtil.createOnNotExistFile("mybatis/myMapper.xml.ftl", mapperXmlPath, paramMap, BASE_TEMPLATE_PATH);
                        String mapperPath = serverProjectOutputDir + CODE_DIR_PRE + mapperPackage.replace(".", File.separator) + File.separator + StringUtil.upperFirstChar(modelInfo.getModelName()) + "Mapper.java";
                        CreateFileUtil.createOnNotExistFile("mybatis/mapper.java.ftl", mapperPath, paramMap, BASE_TEMPLATE_PATH);
                    }
                } catch (Exception ex) {
                    log.error("生成模型失败：{}，原因：{}", modelInfo.getModelName(), ex.getMessage());
                }
            }
            log.info("生成Model成功！");
        } catch (Exception e) {
            log.error("生成Model失败：{}", e.getMessage());
        }
    }

}
