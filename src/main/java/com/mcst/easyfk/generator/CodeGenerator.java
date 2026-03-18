package com.mcst.easyfk.generator;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ReUtil;
import com.mcst.easyfk.core.exception.BusinessException;
import com.mcst.easyfk.core.utils.common.EmptyUtil;
import com.mcst.easyfk.core.utils.common.StringUtil;
import com.mcst.easyfk.generator.enums.*;
import com.mcst.easyfk.generator.properties.CodeProperties;
import com.mcst.easyfk.generator.properties.ProjectProperties;
import com.mcst.easyfk.generator.util.CreateFileUtil;
import com.mcst.easyfk.generator.util.GeneratorUtil;
import com.mcst.easyfk.generator.vo.FieldInfo;
import com.mcst.easyfk.generator.vo.ImportPackage;
import com.mcst.easyfk.generator.vo.ModelInfo;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.*;

import static com.mcst.easyfk.generator.util.GeneratorUtil.*;

/**
 * @author eb-jack
 */
@Slf4j
public class CodeGenerator {
    private static final String BASE_TEMPLATE_PATH = "/templates/code";
    private static final String CODE_DIR_PRE = File.separator + "src" + File.separator + "main" + File.separator
            + "java" + File.separator;
    @Resource
    private ProjectProperties projectProperties;

    @Resource
    private CodeProperties codeProperties;
    private String basePackage;

    public CodeGenerator() {}

    public CodeGenerator(ProjectProperties projectProperties, CodeProperties codeProperties) {
        this.projectProperties = projectProperties;
        this.codeProperties = codeProperties;
    }

    public void create(boolean onlyUpdateDtoAndParam) {
        basePackage = projectProperties.getBasePackage();
        if (EmptyUtil.isEmpty(basePackage)) {
            throw new BusinessException("项目包根路径不可为空");
        }
        // 项目目录
        String projectRootDir = projectProperties.getProjectDir() + File.separator + projectProperties.getProjectName();
        String serverProjectOutputDir = projectRootDir + File.separator + projectProperties.getProjectName()
                + "-server";
        String apiProjectOutputDir = projectRootDir + File.separator + projectProperties.getProjectName() + "-api";
        String controllerProjectOutputDir = projectRootDir + File.separator + projectProperties.getProjectName()
                + "-prd";
        String scProjectOutputDir = projectRootDir + File.separator + projectProperties.getProjectName() + "-remote";

        // 项目包路径
        String serverBasePackage = basePackage + ".server";
        String apiBasePackage = basePackage + ".api";
        String servicePackage = serverBasePackage + ".service";
        String serviceImplPackage = servicePackage + ".impl";
        if (ProjectType.SINGLE.equals(projectProperties.getProjectType())) {
            serverProjectOutputDir = projectRootDir;
            apiProjectOutputDir = serverProjectOutputDir;
            controllerProjectOutputDir = serverProjectOutputDir;
            serverBasePackage = basePackage + ".service";
            servicePackage = apiBasePackage;
            serviceImplPackage = serverBasePackage + ".impl";
        }

        String dtoPackage = apiBasePackage + ".dto";
        String paramPackage = apiBasePackage + ".param";
        String reqPackage = apiBasePackage + ".request";
        String respPackage = apiBasePackage + ".response";
        String defaultRemoteId = "server";
        String remotePackage = basePackage + ".remote";
        String remoteImplPackage = ProjectType.MICROSERVICE.equals(projectProperties.getProjectType())
                ? serverBasePackage + ".impl"
                : remotePackage + ".provider.impl";
        String remoteApiImplPackage = remotePackage + ".impl";
        String serverApiImplPackage = basePackage + ".server.impl";
        String remotePropertyPackage = ProjectType.MICROSERVICE.equals(projectProperties.getProjectType())
                ? apiBasePackage + ".properties"
                : remotePackage + ".properties";
        String controllerBasePackage = basePackage + ".controller";
        String repositoryPackage = serverBasePackage + ".repository";
        String repositoryImpPackage = repositoryPackage + ".impl";
        String entityPackage = serverBasePackage + ".persistence.model";
        String mapperPackage = serverBasePackage + ".persistence.mapper";

        Map<String, Object> paramMap = GeneratorUtil.createBaseParamMap(codeProperties);
        paramMap.put("repositoryPackage", repositoryPackage);
        paramMap.put("repositoryImpPackage", repositoryImpPackage);
        paramMap.put("serverPackage", serverBasePackage);
        paramMap.put("serviceImplPackage", serviceImplPackage);
        paramMap.put("entityPackage", entityPackage);
        paramMap.put("mapperPackage", mapperPackage);
        paramMap.put("dtoPackage", dtoPackage);
        paramMap.put("reqPackage", reqPackage);
        paramMap.put("paramPackage", paramPackage);
        paramMap.put("respPackage", respPackage);
        paramMap.put("apiBasePackage", apiBasePackage);
        paramMap.put("createSpringAnnotation",
                projectProperties.getProjectType() != ProjectType.SMART && codeProperties.getSpringAnnotation());
        paramMap.put("serverApiImplPackage", serverApiImplPackage);
        paramMap.put("servicePackage", servicePackage);
        paramMap.put("basePackage", basePackage);
        paramMap.put("logName", projectProperties.getLogType().name().toLowerCase());
        paramMap.put("moduleName", codeProperties.getModuleName());
        paramMap.put("projectType", projectProperties.getProjectType());
        paramMap.put("appType", projectProperties.getAppType());
        paramMap.put("ormType", codeProperties.getOrmType());
        paramMap.put("rpcType", projectProperties.getRpcType());
        paramMap.put("logType", projectProperties.getLogType());
        paramMap.put("groupId", projectProperties.getGroupId());
        paramMap.put("createResourceAnnotation", codeProperties.getCreateResourceAnnotation());
        if (DbType.MYSQL.equals(codeProperties.getDbType())) {
            paramMap.put("dbUrl", String.format(MYSQL_URL_TEMPLATE8, codeProperties.getDbShortUrl()));
            paramMap.put("driverName", MYSQL_DRIVER);
        } else if (DbType.POSTGRE_SQL.equals(codeProperties.getDbType())) {
            paramMap.put("dbUrl", String.format(POSTGRESQL_URL_TEMPLATE, codeProperties.getDbShortUrl()));
            paramMap.put("driverName", POSTGRESQL_DRIVER);
        } else if (DbType.ORACLE_12C.equals(codeProperties.getDbType())
                || DbType.ORACLE.equals(codeProperties.getDbType())) {
            paramMap.put("dbUrl", String.format(ORACLE_URL_TEMPLATE, codeProperties.getDbShortUrl()));
            paramMap.put("driverName", ORACLE_DRIVER);
        }
        paramMap.put("dbUser", codeProperties.getDbUser());
        paramMap.put("dbPwd", codeProperties.getDbPwd());
        paramMap.put("dbType", codeProperties.getDbType());
        if (!projectProperties.getProjectType().equals(ProjectType.SINGLE)) {
            String scApiServiceId = "${easyfk.config.remote." + codeProperties.getModuleName() + ".service-id" + ":"
                    + defaultRemoteId + "}";
            paramMap.put("scApiServiceId", scApiServiceId);
            String scApiBasePath = "${easyfk.config.remote." + codeProperties.getModuleName() + ".base-path"
                    + ":/remote}";
            paramMap.put("scApiBasePath", scApiBasePath);
            paramMap.put("remotePackage", remotePackage);
            paramMap.put("remoteImplPackage", remoteImplPackage);
            paramMap.put("remotePropertyPackage", remotePropertyPackage);
            paramMap.put("remoteApiImplPackage", remoteApiImplPackage);
        }
        if (projectProperties.getCreatePrdProject() && codeProperties.getCreateController()) {
            paramMap.put("controllerBasePackage", controllerBasePackage);
        }
        if (!projectProperties.getProjectType().equals(ProjectType.SMART)) {
            createServerBaseCode(serverProjectOutputDir, paramMap);
            if (ProjectType.MICROSERVICE.equals(projectProperties.getProjectType())) {
                if (projectProperties.getCreatePrdProject() && codeProperties.getCreateController()) {
                    createPrdBaseCode(controllerProjectOutputDir, paramMap);
                }
            }
        }

        List<ModelInfo> modelList = GeneratorUtil.getModelInfoList(codeProperties, !onlyUpdateDtoAndParam);
        if (EmptyUtil.isEmpty(modelList)) {
            log.info("生成代码成功!");
            return;
        }
        for (ModelInfo modelInfo : modelList) {
            try {
                readModelFile(serverProjectOutputDir, entityPackage, modelInfo);
                paramMap.put("modelInfo", modelInfo);
                if (onlyUpdateDtoAndParam) {
                    // 生成 dto、param、req
                    createDtoParamReq(apiProjectOutputDir, dtoPackage, paramPackage, reqPackage, respPackage, paramMap,
                            modelInfo);
                } else {
                    // 生成 dto、param、req
                    createDtoParamReq(apiProjectOutputDir, dtoPackage, paramPackage, reqPackage, respPackage, paramMap,
                            modelInfo);
                    // 生成 repository
                    createRepository(serverProjectOutputDir, repositoryPackage, repositoryImpPackage, paramMap,
                            modelInfo);
                    if (!modelInfo.getOnlyRepository()) {
                        // 生成 service
                        createService(serverProjectOutputDir, servicePackage, serviceImplPackage, paramMap, modelInfo);
                        // 生成 api
                        createApi(apiProjectOutputDir, apiBasePackage, paramMap, modelInfo);
                        if (ProjectType.MICROSERVICE.equals(projectProperties.getProjectType())) {
                            if (RpcType.DUBBO.equals(projectProperties.getRpcType())) {
                                // 生成 Dubbo Remote/API（不生成 API 实现）
                                paramMap.put("dubboApiImplPackage", remoteApiImplPackage);
                                createDubbo(apiProjectOutputDir, serverProjectOutputDir, remotePackage,
                                        remoteImplPackage, remoteApiImplPackage, paramMap, modelInfo, false);
                            } else {
                                createSpringCloud(apiProjectOutputDir, serverProjectOutputDir, remotePackage,
                                        remoteImplPackage, remotePropertyPackage, remoteApiImplPackage, paramMap,
                                        modelInfo,
                                        false);
                            }
                        } else if (ProjectType.SMART.equals(projectProperties.getProjectType())) {
                            String scApiProjectOutputDir = scProjectOutputDir + File.separator
                                    + projectProperties.getProjectName() + "-cloud-api";
                            String scApiImplProjectOutputDir = scProjectOutputDir + File.separator
                                    + projectProperties.getProjectName() + "-cloud-provider";
                            createSpringCloud(scApiProjectOutputDir, scApiImplProjectOutputDir, remotePackage,
                                    remoteImplPackage, remotePropertyPackage, remoteApiImplPackage, paramMap, modelInfo,
                                    true);
                            String dubboApiProjectOutputDir = scProjectOutputDir + File.separator
                                    + projectProperties.getProjectName() + "-dubbo-api";
                            String dubboApiImplProjectOutputDir = scProjectOutputDir + File.separator
                                    + projectProperties.getProjectName() + "-dubbo-provider";
                            // 生成 Dubbo Remote/API/Provider，实现 API Dubbo 实现类
                            paramMap.put("dubboApiImplPackage", remoteApiImplPackage);
                            createDubbo(dubboApiProjectOutputDir, dubboApiImplProjectOutputDir, remotePackage,
                                    remoteImplPackage, remoteApiImplPackage, paramMap, modelInfo, true);
                            String apiImplPath = serverProjectOutputDir + CODE_DIR_PRE
                                    + serverApiImplPackage.replace(".", File.separator) + File.separator
                                    + StringUtil.upperFirstChar(modelInfo.getModelName()) + "ApiServerImpl.java";
                            CreateFileUtil.createOnNotExistFile("server/serverApiImpl.java.ftl", apiImplPath,
                                    paramMap, BASE_TEMPLATE_PATH);

                        }
                        // controller
                        if (projectProperties.getCreatePrdProject() && codeProperties.getCreateController()) {
                            createController(controllerProjectOutputDir, controllerBasePackage, paramMap, modelInfo);
                        }
                    }
                }
            } catch (Exception e) {
                log.error("生成代码失败：{}", e.getMessage());
            }
        }
        log.info("生成代码成功!");
    }

    private void createController(String controllerProjectOutputDir, String controllerBasePackage,
                                  Map<String, Object> paramMap, ModelInfo modelInfo) {
        if (modelInfo.getOnlyRepository()) {
            return;
        }
        if (!modelInfo.getCreateController()) {
            return;
        }
        try {
            if (ProjectType.SINGLE.equals(projectProperties.getProjectType())) {
                String controllerPath = controllerProjectOutputDir + CODE_DIR_PRE
                        + controllerBasePackage.replace(".", File.separator) + File.separator
                        + StringUtil.upperFirstChar(modelInfo.getModelName()) + "Controller.java";
                CreateFileUtil.createOnNotExistFile("controller/single_local_controller.java.ftl", controllerPath,
                        paramMap, BASE_TEMPLATE_PATH);
            } else if (ProjectType.MICROSERVICE.equals(projectProperties.getProjectType())) {
                if (PrdType.SINGLE.equals(projectProperties.getPrdType())) {
                    // 生成Controller
                    String controllerPath = controllerProjectOutputDir + CODE_DIR_PRE
                            + controllerBasePackage.replace(".", File.separator) + File.separator
                            + StringUtil.upperFirstChar(modelInfo.getModelName()) + "Controller.java";
                    CreateFileUtil.createOnNotExistFile("controller/single_micro_controller.java.ftl", controllerPath,
                            paramMap, BASE_TEMPLATE_PATH);
                } else {
                    // client controller
                    String controllerPath = controllerProjectOutputDir + File.separator
                            + projectProperties.getProjectName() + "-prd-client" + CODE_DIR_PRE
                            + controllerBasePackage.replace(".", File.separator) + File.separator
                            + StringUtil.upperFirstChar(modelInfo.getModelName()) + "Controller.java";
                    CreateFileUtil.createOnNotExistFile("controller/client_micro_controller.java.ftl", controllerPath,
                            paramMap, BASE_TEMPLATE_PATH);
                    // bms controller
                    String bmsControllerPath = controllerProjectOutputDir + File.separator
                            + projectProperties.getProjectName() + "-prd-bms" + CODE_DIR_PRE
                            + controllerBasePackage.replace(".", File.separator) + File.separator
                            + StringUtil.upperFirstChar(modelInfo.getModelName()) + "BmsController" + ".java";
                    CreateFileUtil.createOnNotExistFile("controller/bms_micro_controller.java.ftl", bmsControllerPath,
                            paramMap, BASE_TEMPLATE_PATH);
                }
            } else {
                if (PrdType.SINGLE.equals(projectProperties.getPrdType())) {
                    String controllerPath = controllerProjectOutputDir + CODE_DIR_PRE
                            + controllerBasePackage.replace(".", File.separator) + File.separator
                            + StringUtil.upperFirstChar(modelInfo.getModelName()) + "Controller.java";
                    CreateFileUtil.createOnNotExistFile("controller/single_smart_controller.java.ftl", controllerPath,
                            paramMap, BASE_TEMPLATE_PATH);
                } else {
                    // client controller
                    String controllerPath = controllerProjectOutputDir + File.separator
                            + projectProperties.getProjectName() + "-prd-client" + CODE_DIR_PRE
                            + controllerBasePackage.replace(".", File.separator) + File.separator
                            + StringUtil.upperFirstChar(modelInfo.getModelName()) + "Controller.java";
                    CreateFileUtil.createOnNotExistFile("controller/client_smart_controller.java.ftl", controllerPath,
                            paramMap, BASE_TEMPLATE_PATH);
                    // bms controller
                    String bmsControllerPath = controllerProjectOutputDir + File.separator
                            + projectProperties.getProjectName() + "-prd-bms" + CODE_DIR_PRE
                            + controllerBasePackage.replace(".", File.separator) + File.separator
                            + StringUtil.upperFirstChar(modelInfo.getModelName()) + "BmsController" + ".java";
                    CreateFileUtil.createOnNotExistFile("controller/bms_smart_controller.java.ftl", bmsControllerPath,
                            paramMap, BASE_TEMPLATE_PATH);
                }
            }
        } catch (Exception e) {
            log.error("生成Controller模块失败：{}", e.getMessage());
        }

    }

    private void createSpringCloud(String apiProjectOutputDir, String serverProjectCodeOutputDir, String remotePackage,
                                   String remoteImplPackage, String propertyPackage, String remoteApiImplPackage, Map<String, Object> paramMap,
                                   ModelInfo modelInfo, boolean createApiScImpl) {
        try {
            // Remote API
            String remoteApiPath = apiProjectOutputDir + CODE_DIR_PRE + remotePackage.replace(".", File.separator)
                    + File.separator + "I" + StringUtil.upperFirstChar(modelInfo.getModelName()) + "Remote.java";
            CreateFileUtil.createOnNotExistFile("springcloud/springcloudRemote.java.ftl", remoteApiPath, paramMap,
                    BASE_TEMPLATE_PATH);
            // Remote Impl
            String remoteImplPath = serverProjectCodeOutputDir + CODE_DIR_PRE
                    + remoteImplPackage.replace(".", File.separator) + File.separator
                    + StringUtil.upperFirstChar(modelInfo.getModelName()) + "RemoteImpl.java";
            CreateFileUtil.createOnNotExistFile("springcloud/springcloudRemoteImpl.java.ftl", remoteImplPath,
                    paramMap, BASE_TEMPLATE_PATH);
            // SC Property
            String propertyPath = apiProjectOutputDir + CODE_DIR_PRE + propertyPackage.replace(".", File.separator)
                    + File.separator + StringUtil.upperFirstChar(codeProperties.getModuleName()) + "ApiProperties.java";
            CreateFileUtil.createOnNotExistFile("springcloud/springcloudApiProperty.java.ftl", propertyPath, paramMap,
                    BASE_TEMPLATE_PATH);
            if (createApiScImpl) {
                String apiScImplPath = apiProjectOutputDir + CODE_DIR_PRE
                        + remoteApiImplPackage.replace(".", File.separator) + File.separator
                        + StringUtil.upperFirstChar(modelInfo.getModelName()) + "ApiScImpl.java";
                CreateFileUtil.createOnNotExistFile("springcloud/springcloudApiImpl.java.ftl", apiScImplPath,
                        paramMap, BASE_TEMPLATE_PATH);
            }
        } catch (Exception e) {
            log.error("生成SpringCloud模块失败：{}", e.getMessage());
        }
    }

    private void createDubbo(String apiProjectOutputDir, String serverProjectCodeOutputDir, String remotePackage,
                             String remoteImplPackage, String apiImplPackage, Map<String, Object> paramMap, ModelInfo modelInfo,
                             boolean createApiDubboImpl) {
        try {
            // Remote API
            String remoteApiPath = apiProjectOutputDir + CODE_DIR_PRE + remotePackage.replace(".", File.separator)
                    + File.separator + "I" + StringUtil.upperFirstChar(modelInfo.getModelName()) + "Remote.java";
            CreateFileUtil.createOnNotExistFile("dubbo/dubboRemote.java.ftl", remoteApiPath, paramMap,
                    BASE_TEMPLATE_PATH);

            // Remote Impl
            String remoteImplPath = serverProjectCodeOutputDir + CODE_DIR_PRE
                    + remoteImplPackage.replace(".", File.separator) + File.separator
                    + StringUtil.upperFirstChar(modelInfo.getModelName()) + "RemoteImpl.java";
            CreateFileUtil.createOnNotExistFile("dubbo/dubboRemoteImpl.java.ftl", remoteImplPath, paramMap,
                    BASE_TEMPLATE_PATH);

            // Api Dubbo Impl (optional)
            if (createApiDubboImpl) {
                String apiDubboImplPath = apiProjectOutputDir + CODE_DIR_PRE
                        + apiImplPackage.replace(".", File.separator) + File.separator
                        + StringUtil.upperFirstChar(modelInfo.getModelName()) + "ApiDubboImpl.java";
                CreateFileUtil.createOnNotExistFile("dubbo/dubboApiImpl.java.ftl", apiDubboImplPath, paramMap,
                        BASE_TEMPLATE_PATH);
            }
        } catch (Exception e) {
            log.error("生成Dubbo模块失败：{}", e.getMessage());
        }
    }

    private void createLogConfig(String serverProjectOutputDir, Map<String, Object> paramMap) throws Exception {
        String logName = "logback.xml";
        String logTemplateFile = "config/log_logback.xml.ftl";
        if (LogType.LOG4J2.equals(projectProperties.getLogType())) {
            logName = "log4j2.xml";
            logTemplateFile = "config/log_log4j2.xml.ftl";
        }
        String logPath = serverProjectOutputDir + File.separator + "config" + File.separator + logName;
        CreateFileUtil.createOnNotExistFile(logTemplateFile, logPath, paramMap, BASE_TEMPLATE_PATH);
    }

    private void createApi(String apiProjectOutputDir, String apiBasePackage, Map<String, Object> paramMap,
                           ModelInfo modelInfo) {
        try {
            if (ProjectType.SMART.equals(projectProperties.getProjectType()) && !modelInfo.getOnlyRepository()) {
                String apiPath = apiProjectOutputDir + CODE_DIR_PRE + apiBasePackage.replace(".", File.separator)
                        + File.separator + "I" + StringUtil.upperFirstChar(modelInfo.getModelName()) + "Api.java";
                CreateFileUtil.createOnNotExistFile("api/base_api.java.ftl", apiPath, paramMap, BASE_TEMPLATE_PATH);
            }
        } catch (Exception e) {
            log.error("生成Api模块失败：{}", e.getMessage());
        }
    }

    private void createDtoParamReq(String apiProjectOutputDir, String dtoPackage, String paramPackage,
                                   String reqPackage, String respPackage, Map<String, Object> paramMap, ModelInfo modelInfo) {
        String dtoPath = apiProjectOutputDir + CODE_DIR_PRE + dtoPackage.replace(".", File.separator) + File.separator
                + StringUtil.upperFirstChar(modelInfo.getModelName()) + "Dto.java";
        String paramPath = apiProjectOutputDir + CODE_DIR_PRE + paramPackage.replace(".", File.separator)
                + File.separator + StringUtil.upperFirstChar(modelInfo.getModelName()) + "Param.java";
        try {
            CreateFileUtil.delAndCreateFile("model/dto_model.java.ftl", dtoPath, paramMap, BASE_TEMPLATE_PATH);
            if (!modelInfo.getOnlyRepository()) {
                CreateFileUtil.delAndCreateFile("model/param_model.java.ftl", paramPath, paramMap, BASE_TEMPLATE_PATH);
                String reqPath = apiProjectOutputDir + CODE_DIR_PRE + reqPackage.replace(".", File.separator)
                        + File.separator + StringUtil.upperFirstChar(modelInfo.getModelName()) + "Req.java";
                CreateFileUtil.createOnNotExistFile("model/req_model.java.ftl", reqPath, paramMap, BASE_TEMPLATE_PATH);
                String respPath = apiProjectOutputDir + CODE_DIR_PRE + respPackage.replace(".", File.separator)
                        + File.separator + StringUtil.upperFirstChar(modelInfo.getModelName()) + "Resp.java";
                CreateFileUtil.createOnNotExistFile("model/resp_model.java.ftl", respPath, paramMap,
                        BASE_TEMPLATE_PATH);
                String editPath = apiProjectOutputDir + CODE_DIR_PRE + reqPackage.replace(".", File.separator)
                        + File.separator + StringUtil.upperFirstChar(modelInfo.getModelName()) + "EditReq.java";
                CreateFileUtil.createOnNotExistFile("model/edit_model.java.ftl", editPath, paramMap,
                        BASE_TEMPLATE_PATH);
            }
        } catch (Exception e) {
            log.error("生成DtoAndParam失败：{}", e.getMessage());
        }
    }

    /**
     * 生成 repository
     */
    private void createService(String serverProjectOutputDir, String serverPackage, String serviceImplPackage,
                               Map<String, Object> paramMap, ModelInfo modelInfo) {
        String path = serverProjectOutputDir + CODE_DIR_PRE + serverPackage.replace(".", File.separator)
                + File.separator + "I" + StringUtil.upperFirstChar(modelInfo.getModelName()) + "Service.java";
        String implPath = serverProjectOutputDir + CODE_DIR_PRE + serviceImplPackage.replace(".", File.separator)
                + File.separator + StringUtil.upperFirstChar(modelInfo.getModelName()) + "ServiceImpl.java";
        try {
            CreateFileUtil.createOnNotExistFile("server/my_service.java.ftl", path, paramMap, BASE_TEMPLATE_PATH);
            CreateFileUtil.createOnNotExistFile("server/my_serviceImpl.java.ftl", implPath, paramMap,
                    BASE_TEMPLATE_PATH);
        } catch (Exception e) {
            log.error("生成Service模块失败：{}", e.getMessage());
        }
    }

    private void createRepository(String serverProjectOutputDir, String repositoryPackage, String repositoryImpPackage,
                                  Map<String, Object> paramMap, ModelInfo modelInfo) {
        String path = serverProjectOutputDir + CODE_DIR_PRE + repositoryPackage.replace(".", File.separator)
                + File.separator + "I" + StringUtil.upperFirstChar(modelInfo.getModelName()) + "Repository.java";
        String implPath = serverProjectOutputDir + CODE_DIR_PRE + repositoryImpPackage.replace(".", File.separator)
                + File.separator + StringUtil.upperFirstChar(modelInfo.getModelName()) + "RepositoryImpl.java";
        try {
            CreateFileUtil.createOnNotExistFile("repository/repository.java.ftl", path, paramMap,
                    BASE_TEMPLATE_PATH);
            CreateFileUtil.createOnNotExistFile("repository/repositoryImpl.java.ftl", implPath, paramMap,
                    BASE_TEMPLATE_PATH);
        } catch (Exception e) {
            log.error("生成Repository模块失败：{}", e.getMessage());
        }
    }

    private void readModelFile(String serverProjectOutputDir, String entityPackage, ModelInfo modelInfo) {
        String modelPath = serverProjectOutputDir + CODE_DIR_PRE + entityPackage.replace(".", File.separator)
                + File.separator + StringUtil.upperFirstChar(modelInfo.getModelName()) + ".java";
        List<String> fileLines = FileUtil.readLines(new File(modelPath), "UTF-8");
        if (EmptyUtil.isEmpty(fileLines)) {
            return;
        }
        LinkedList<FieldInfo> fieldList = new LinkedList<>();
        List<ImportPackage> packageList = new ArrayList<>();
        List<ImportPackage> otherPackageList = new ArrayList<>();
        boolean newField = true;
        for (String fileLine : fileLines) {
            if (EmptyUtil.isEmpty(fileLine)) {
                continue;
            }
            if (fileLine.contains("import java.math.BigDecimal;")) {
                packageList.add(new ImportPackage().setPackageName("import java.math.BigDecimal;"));
            } else if (fileLine.contains("import java.util.Date;")) {
                packageList.add(new ImportPackage().setPackageName("import java.util.Date;"));
            } else if (fileLine.contains("import java.util.List;")) {
                packageList.add(new ImportPackage().setPackageName("import java.util.List;"));
            } else if (fileLine.contains("import java.time.Instant;")) {
                packageList.add(new ImportPackage().setPackageName("import java.time.Instant;"));
            } else if (fileLine.contains("import java.util.Map;")) {
                packageList.add(new ImportPackage().setPackageName("import java.util.Map;"));
            } else if (fileLine.contains("import java.time.LocalDateTime;")) {
                packageList.add(new ImportPackage().setPackageName("import java.time.LocalDateTime;"));
            } else if (fileLine.contains("import java.time.LocalDate;")) {
                packageList.add(new ImportPackage().setPackageName("import java.time.LocalDate;"));
            } else if (fileLine.contains("@Column")) {
                String desc = extractComment(fileLine);
                FieldInfo fieldInfo = null;
                if (newField) {
                    fieldInfo = new FieldInfo();
                    fieldList.addLast(fieldInfo);
                    newField = false;
                } else {
                    fieldInfo = fieldList.getLast();
                }
                fieldInfo.setDesc(desc);
            } else if (fileLine.contains("private")) {
                String[] split = fileLine.trim().split(" ");
                FieldInfo fieldInfo = null;
                if (newField) {
                    fieldInfo = new FieldInfo();
                    fieldList.addLast(fieldInfo);
                } else {
                    fieldInfo = fieldList.getLast();
                }
                fieldInfo.setName(split[2].replace(";", "")).setType(split[1]);
                newField = true;
            } else if (fileLine.contains("@TableId") || fileLine.contains("@Id")) {
                FieldInfo fieldInfo = null;
                if (newField) {
                    fieldInfo = new FieldInfo();
                    fieldList.addLast(fieldInfo);
                    newField = false;
                } else {
                    fieldInfo = fieldList.getLast();
                }
                fieldInfo.setKeyFlag(true);
            } else if (fileLine.contains("@SingleUniqueField")) {
                FieldInfo fieldInfo = null;
                if (newField) {
                    fieldInfo = new FieldInfo();
                    fieldList.addLast(fieldInfo);
                    newField = false;
                } else {
                    fieldInfo = fieldList.getLast();
                }
                fieldInfo.setSingleUniqueField(true);
                String repetitionMsg = extractRepetitionMsg(fileLine);
                fieldInfo.setRepetitionMsg(repetitionMsg);
                otherPackageList.add(new ImportPackage()
                        .setPackageName("import com.mcst.easyfk.core.annotation.SingleUniqueField;"));
            } else if (fileLine.contains("@CombUniqueField")) {
                FieldInfo fieldInfo = null;
                if (newField) {
                    fieldInfo = new FieldInfo();
                    fieldList.addLast(fieldInfo);
                    newField = false;
                } else {
                    fieldInfo = fieldList.getLast();
                }
                fieldInfo.setCombUniqueField(true);
                String combinationField = extractCombinationField(fileLine);
                fieldInfo.setCombinationField(combinationField);
                String repetitionMsg = extractRepetitionMsg(fileLine);
                fieldInfo.setRepetitionMsg(repetitionMsg);
                otherPackageList.add(new ImportPackage()
                        .setPackageName("import com.mcst.easyfk.core.annotation.CombUniqueField;"));
            } else if (fileLine.contains("@BetweenConditionField")) {
                FieldInfo fieldInfo = null;
                if (newField) {
                    fieldInfo = new FieldInfo();
                    fieldList.addLast(fieldInfo);
                    newField = false;
                } else {
                    fieldInfo = fieldList.getLast();
                }
                fieldInfo.setBetweenField(true);
            } else if (fileLine.contains("@ForbiddenField")) {
                String fileName = extractForbiddenField(fileLine);
                modelInfo.setForbiddenFiled(fileName);
            }
        }
        modelInfo.setOtherPackages(otherPackageList).setBasicPackages(packageList).setFieldList(fieldList);
    }

    /**
     * 生成Server模块 基础代码，包括配置文件及启动类
     */
    private void createServerBaseCode(String serverProjectOutputDir, Map<String, Object> paramMap) {
        try {
            // 生成启动类
            String path = basePackage.replace(".", File.separator);
            String filePath = serverProjectOutputDir + CODE_DIR_PRE + path + File.separator + "ServerApp.java";
            CreateFileUtil.createOnNotExistFile("start/serverApp.java.ftl", filePath, paramMap, BASE_TEMPLATE_PATH);
            // 生成yml配置文件
            String configPath = serverProjectOutputDir + File.separator + "config" + File.separator + "application"
                    + ".yml";
            CreateFileUtil.createOnNotExistFile("config/serverApplication.yml.ftl", configPath, paramMap,
                    BASE_TEMPLATE_PATH);
            String configDevPath = serverProjectOutputDir + File.separator + "config" + File.separator
                    + "application-dev" + ".yml";
            CreateFileUtil.createOnNotExistFile("config/serverApplicationDev.yml.ftl", configDevPath, paramMap,
                    BASE_TEMPLATE_PATH);
            String configTestPath = serverProjectOutputDir + File.separator + "config" + File.separator
                    + "application-test" + ".yml";
            CreateFileUtil.createOnNotExistFile("config/serverApplicationTest.yml.ftl", configTestPath, paramMap,
                    BASE_TEMPLATE_PATH);
            String configProdPath = serverProjectOutputDir + File.separator + "config" + File.separator
                    + "application-prod" + ".yml";
            CreateFileUtil.createOnNotExistFile("config/serverApplicationProd.yml.ftl", configProdPath, paramMap,
                    BASE_TEMPLATE_PATH);
            // 生成日志配置文件
            createLogConfig(serverProjectOutputDir, paramMap);
        } catch (Exception e) {
            log.error("生成Server模块基础代码失败：", e);
        }

    }

    /**
     * 生成 prd 模块基础代码 ，包括项目配置及启动类
     */
    private void createPrdBaseCode(String controllerProjectOutputDir, Map<String, Object> paramMap) {
        try {
            if (PrdType.SINGLE.equals(projectProperties.getPrdType())) {
                // 生成启动类
                String mainClassPath = basePackage.replace(".", File.separator);
                String mainClassFile = controllerProjectOutputDir + CODE_DIR_PRE + mainClassPath + File.separator
                        + "ClientApp.java";
                CreateFileUtil.createOnNotExistFile("start/clientApp.java.ftl", mainClassFile, paramMap,
                        BASE_TEMPLATE_PATH);
                // 生成配置文件
                String configPath = controllerProjectOutputDir + File.separator + "config" + File.separator
                        + "application.yml";
                CreateFileUtil.createOnNotExistFile("config/prdApplication.yml.ftl", configPath, paramMap,
                        BASE_TEMPLATE_PATH);
                String configDevPath = controllerProjectOutputDir + File.separator + "config" + File.separator
                        + "application-dev.yml";
                CreateFileUtil.createOnNotExistFile("config/prdApplicationDev.yml.ftl", configDevPath, paramMap,
                        BASE_TEMPLATE_PATH);
                String configTestPath = controllerProjectOutputDir + File.separator + "config" + File.separator
                        + "application-test.yml";
                CreateFileUtil.createOnNotExistFile("config/prdApplicationTest.yml.ftl", configTestPath, paramMap,
                        BASE_TEMPLATE_PATH);
                String configProdPath = controllerProjectOutputDir + File.separator + "config" + File.separator
                        + "application-prod.yml";
                CreateFileUtil.createOnNotExistFile("config/prdApplicationProd.yml.ftl", configProdPath, paramMap,
                        BASE_TEMPLATE_PATH);
                // 生成日志配置文件
                createLogConfig(controllerProjectOutputDir, paramMap);
            } else {
                // 生成Client启动类
                String mainClassPath = basePackage.replace(".", File.separator);
                String mainClassFile = controllerProjectOutputDir + File.separator + projectProperties.getProjectName()
                        + "-prd-client" + CODE_DIR_PRE + mainClassPath + File.separator + "ClientApp" + ".java";
                CreateFileUtil.createOnNotExistFile("start/clientApp.java.ftl", mainClassFile, paramMap,
                        BASE_TEMPLATE_PATH);
                // 生成配置文件
                String configPath = controllerProjectOutputDir + File.separator + projectProperties.getProjectName()
                        + "-prd-client" + File.separator + "config" + File.separator + "application" + ".yml";
                CreateFileUtil.createOnNotExistFile("config/prdApplication.yml.ftl", configPath, paramMap,
                        BASE_TEMPLATE_PATH);
                String configDevPath = controllerProjectOutputDir + File.separator + projectProperties.getProjectName()
                        + "-prd-client" + File.separator + "config" + File.separator + "application" + "-dev.yml";
                CreateFileUtil.createOnNotExistFile("config/prdApplicationDev.yml.ftl", configDevPath, paramMap,
                        BASE_TEMPLATE_PATH);
                String configTestPath = controllerProjectOutputDir + File.separator + projectProperties.getProjectName()
                        + "-prd-client" + File.separator + "config" + File.separator + "application" + "-test.yml";
                CreateFileUtil.createOnNotExistFile("config/prdApplicationTest.yml.ftl", configTestPath, paramMap,
                        BASE_TEMPLATE_PATH);
                String configProdPath = controllerProjectOutputDir + File.separator + projectProperties.getProjectName()
                        + "-prd-client" + File.separator + "config" + File.separator + "application" + "-prod.yml";
                CreateFileUtil.createOnNotExistFile("config/prdApplicationProd.yml.ftl", configProdPath, paramMap,
                        BASE_TEMPLATE_PATH);
                // 生成日志配置文件
                String logName = "logback.xml";
                String logTemplateFile = "config/log_logback.xml.ftl";
                if (LogType.LOG4J2.equals(projectProperties.getLogType())) {
                    logName = "log4j2.xml";
                    logTemplateFile = "config/log_log4j2.xml.ftl";
                }
                String logPath = controllerProjectOutputDir + File.separator + projectProperties.getProjectName()
                        + "-prd-client" + File.separator + "config" + File.separator + logName;
                CreateFileUtil.createOnNotExistFile(logTemplateFile, logPath, paramMap, BASE_TEMPLATE_PATH);
                // 生成Bms启动类
                String bmsMainClassPath = basePackage.replace(".", File.separator);
                String bmsMainClassFile = controllerProjectOutputDir + File.separator
                        + projectProperties.getProjectName() + "-prd-bms" + CODE_DIR_PRE + bmsMainClassPath
                        + File.separator + "BmsApp" + ".java";
                CreateFileUtil.createOnNotExistFile("start/bmsApp.java.ftl", bmsMainClassFile, paramMap,
                        BASE_TEMPLATE_PATH);
                // 生成配置文件
                String bmsConfigPath = controllerProjectOutputDir + File.separator + projectProperties.getProjectName()
                        + "-prd-bms" + File.separator + "config" + File.separator + "application" + ".yml";
                CreateFileUtil.createOnNotExistFile("config/prdApplication.yml.ftl", bmsConfigPath, paramMap,
                        BASE_TEMPLATE_PATH);
                String bmsConfigDevPath = controllerProjectOutputDir + File.separator
                        + projectProperties.getProjectName() + "-prd-bms" + File.separator + "config" + File.separator
                        + "application" + "-dev.yml";
                CreateFileUtil.createOnNotExistFile("config/prdApplicationDev.yml.ftl", bmsConfigDevPath, paramMap,
                        BASE_TEMPLATE_PATH);
                String bmsConfigTestPath = controllerProjectOutputDir + File.separator
                        + projectProperties.getProjectName() + "-prd-bms" + File.separator + "config" + File.separator
                        + "application" + "-test.yml";
                CreateFileUtil.createOnNotExistFile("config/prdApplicationTest.yml.ftl", bmsConfigTestPath, paramMap,
                        BASE_TEMPLATE_PATH);
                String bmsConfigProdPath = controllerProjectOutputDir + File.separator
                        + projectProperties.getProjectName() + "-prd-bms" + File.separator + "config" + File.separator
                        + "application" + "-prod.yml";
                CreateFileUtil.createOnNotExistFile("config/prdApplicationProd.yml.ftl", bmsConfigProdPath, paramMap,
                        BASE_TEMPLATE_PATH);
                // 生成日志配置文件
                String bmsLogPath = controllerProjectOutputDir + File.separator + projectProperties.getProjectName()
                        + "-prd-bms" + File.separator + "config" + File.separator + logName;
                CreateFileUtil.createOnNotExistFile(logTemplateFile, bmsLogPath, paramMap, BASE_TEMPLATE_PATH);
            }
        } catch (Exception e) {
            log.error("生成Prd基础代码失败：{}", e.getMessage());
        }
    }

    public String extractComment(String content) {
        // 使用正则表达式匹配 comment 属性的值
        String pattern = "comment\\s*=\\s*\"([^\"]*)\"";
        return ReUtil.get(pattern, content, 1);
    }

    public String extractRepetitionMsg(String content) {
        // 使用正则表达式匹配 comment 属性的值
        String pattern = "repetitionMsg\\s*=\\s*\"([^\"]*)\"";
        return ReUtil.get(pattern, content, 1);
    }

    public String extractCombinationField(String content) {
        // 使用正则表达式匹配 comment 属性的值
        String pattern = "combinationField\\s*=\\s*\"([^\"]*)\"";
        return ReUtil.get(pattern, content, 1);
    }

    public String extractForbiddenField(String content) {
        // 使用正则表达式匹配 comment 属性的值
        String pattern = "value\\s*=\\s*\"([^\"]*)\"";
        return ReUtil.get(pattern, content, 1);
    }
}
