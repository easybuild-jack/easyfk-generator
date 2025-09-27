package com.mcst.easyfk.generator;

import com.mcst.easyfk.core.utils.common.EmptyUtil;
import com.mcst.easyfk.core.utils.common.StringUtil;
import com.mcst.easyfk.generator.enums.PrdType;
import com.mcst.easyfk.generator.enums.ProjectType;
import com.mcst.easyfk.generator.enums.RpcType;
import com.mcst.easyfk.generator.properties.CodeProperties;
import com.mcst.easyfk.generator.properties.ProjectProperties;
import com.mcst.easyfk.generator.util.CreateFileUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author liu yijun
 */
@Slf4j
public class ConfigGenerator {

    private static final String BASE_TEMPLATE_PATH = "/templates/code";

    private static final String CODE_DIR_PRE = File.separator + "src" + File.separator + "main" + File.separator + "java" + File.separator;
    private static final String RESOURCE_DIR_PRE = File.separator + "src" + File.separator + "main" + File.separator + "resources" + File.separator;

    @Resource
    private ProjectProperties projectProperties;

    @Resource
    private CodeProperties codeProperties;

    public void create() {
        String projectRootDir = projectProperties.getProjectDir() + File.separator + projectProperties.getProjectName();
        String auth = codeProperties.getAuthor();
        String moduleName = codeProperties.getModuleName();
        try {
            Map<String, Object> paramMap = new HashMap<>(100);
            paramMap.put("author", auth);
            paramMap.put("moduleName", moduleName);
            paramMap.put("projectType", projectProperties.getProjectType());
            boolean createServer = ProjectType.SMART.equals(projectProperties.getProjectType()) || !codeProperties.getSpringAnnotation();
            if (createServer) {
                this.createServerConfig(projectRootDir, paramMap);
            }
            boolean createRemote = !ProjectType.SINGLE.equals(projectProperties.getProjectType());
            if (createRemote) {
                this.createRemoteConfig(projectRootDir, paramMap);
            }
            boolean createPrd = projectProperties.getCreatePrdProject() && createRemote && projectProperties.getControllerAutoConfig();
            if (createPrd) {
                this.createPrdConfig(projectRootDir, paramMap);
            }
            log.info("生成项目自动装配配置成功！");
        } catch (Exception exception) {
            log.error("生成项目自动装配配置失败:{}", exception.getMessage());
        }

    }

    private void createServerConfig(String projectRootDir, Map<String, Object> paramMap) throws Exception {
        String serverBasePackage = projectProperties.getBasePackage() + ".server";
        String apiPackage = projectProperties.getBasePackage() + ".api";
        String servicePackage = serverBasePackage + ".service";
        String serviceImplPackage = servicePackage + ".impl";

        String repositoryPackage = serverBasePackage + ".repository";
        String repositoryImplPackage = repositoryPackage + ".impl";

        String apiImplPackage = serverBasePackage + ".impl";

        String serverConfigPackage = serverBasePackage + ".config";
        paramMap.put("serverBasePackage", serverBasePackage);
        paramMap.put("apiPackage", apiPackage);
        paramMap.put("servicePackage", servicePackage);
        paramMap.put("serviceImplPackage", serviceImplPackage);
        paramMap.put("repositoryPackage", repositoryPackage);
        paramMap.put("repositoryImplPackage", repositoryImplPackage);
        paramMap.put("apiImplPackage", apiImplPackage);
        paramMap.put("serverConfigPackage", serverConfigPackage);

        String serverProjectOutputDir = projectRootDir + File.separator + projectProperties.getProjectName() + "-server";
        if (ProjectType.SINGLE.equals(projectProperties.getProjectType())) {
            serverProjectOutputDir = projectRootDir;
        }

        Map<String, Set<String>> fileMap = new HashMap<>();
        Set<String> serviceImplFileSet = this.getFolderSubFileName(serverProjectOutputDir + CODE_DIR_PRE + serviceImplPackage.replace(".", "/"));
        if (EmptyUtil.isNotEmpty(serviceImplFileSet)) {
            fileMap.put("serviceImplFileSet", serviceImplFileSet);
        }
        Set<String> repositoryImplFileSet = this.getFolderSubFileName(serverProjectOutputDir + CODE_DIR_PRE + repositoryImplPackage.replace(".", "/"));
        if (EmptyUtil.isNotEmpty(repositoryImplFileSet)) {
            fileMap.put("repositoryImplFileSet", repositoryImplFileSet);
        }
        Set<String> apiImplFileSet = this.getFolderSubFileName(serverProjectOutputDir + CODE_DIR_PRE + apiImplPackage.replace(".", "/"));
        if (EmptyUtil.isNotEmpty(apiImplFileSet)) {
            fileMap.put("apiImplFileSet", apiImplFileSet);
        }
        paramMap.put("fileMap", fileMap);
        String codePath = serverProjectOutputDir + CODE_DIR_PRE + serverConfigPackage.replace(".", File.separator) + File.separator + StringUtil.upperFirstChar(codeProperties.getModuleName()) + "ServerConfig.java";
        CreateFileUtil.delAndCreateFile("server/serverConfig.java.ftl", codePath, paramMap, BASE_TEMPLATE_PATH);

        String factoriesPath = serverProjectOutputDir + RESOURCE_DIR_PRE + "META-INF" + File.separator + "spring" + File.separator + "org.springframework.boot.autoconfigure.AutoConfiguration.imports";
        CreateFileUtil.delAndCreateFile("server/serverConfig.factories.ftl", factoriesPath, paramMap, BASE_TEMPLATE_PATH);
    }

    private void createRemoteConfig(String projectRootDir, Map<String, Object> paramMap) throws Exception {
        if (ProjectType.SMART.equals(projectProperties.getProjectType())) {
            String remoteBasePackage = projectProperties.getBasePackage() + ".remote";
            String remoteApiPackage = projectProperties.getBasePackage() + ".remote";
            String remoteClientImplPackage = remoteBasePackage + ".impl";
            String remoteProviderPackage = remoteBasePackage + ".provider.impl";
            String remoteConfigPackage = remoteBasePackage + ".config";
            String remoteProviderConfigPackage = remoteBasePackage + ".provider.config";

            paramMap.put("remoteClientImplPackage", remoteClientImplPackage);
            paramMap.put("remoteApiPackage", remoteApiPackage);
            paramMap.put("remoteProviderPackage", remoteProviderPackage);
            paramMap.put("remoteConfigPackage", remoteConfigPackage);
            paramMap.put("remoteBasePackage", remoteBasePackage);
            paramMap.put("remoteProviderConfigPackage", remoteProviderConfigPackage);
            Map<String, Set<String>> fileMap = new HashMap<>();
            String remoteClientOutputDir = projectRootDir + File.separator + projectProperties.getProjectName() + "-remote" + File.separator + projectProperties.getProjectName() + "-cloud-api";
            Set<String> remoteImplFileSet = this.getFolderSubFileName(remoteClientOutputDir + CODE_DIR_PRE + remoteClientImplPackage.replace(".", "/"));
            if (EmptyUtil.isNotEmpty(remoteImplFileSet)) {
                fileMap.put("remoteImplFileSet", remoteImplFileSet);
            }
            paramMap.put("fileMap", fileMap);
            //springcloud api config
            String clientCodePath = remoteClientOutputDir + CODE_DIR_PRE + remoteConfigPackage.replace(".", File.separator) + File.separator + StringUtil.upperFirstChar(codeProperties.getModuleName()) + "ApiConfig.java";
            CreateFileUtil.delAndCreateFile("springcloud/springcloudApiConfig.java.ftl", clientCodePath, paramMap, BASE_TEMPLATE_PATH);

            String clientFactoriesPath = remoteClientOutputDir + RESOURCE_DIR_PRE + "META-INF" + File.separator + "spring" + File.separator + "org.springframework.boot.autoconfigure.AutoConfiguration.imports";
            CreateFileUtil.delAndCreateFile("springcloud/springcloudApiConfig.factories.ftl", clientFactoriesPath, paramMap, BASE_TEMPLATE_PATH);
            //dubbo api config
            String dubboRemoteClientOutputDir = projectRootDir + File.separator + projectProperties.getProjectName() + "-remote" + File.separator + projectProperties.getProjectName() + "-dubbo-api";
            Set<String> dubboRemoteImplFileSet = this.getFolderSubFileName(dubboRemoteClientOutputDir + CODE_DIR_PRE + remoteClientImplPackage.replace(".", "/"));
            if (EmptyUtil.isNotEmpty(dubboRemoteImplFileSet)) {
                fileMap.put("dubboRemoteImplFileSet", dubboRemoteImplFileSet);
            }
            String dubboClientCodePath = dubboRemoteClientOutputDir + CODE_DIR_PRE + remoteConfigPackage.replace(".", File.separator) + File.separator + StringUtil.upperFirstChar(codeProperties.getModuleName()) + "ApiConfig.java";
            CreateFileUtil.delAndCreateFile("dubbo/dubboApiConfig.java.ftl", dubboClientCodePath, paramMap, BASE_TEMPLATE_PATH);

            String dubboClientFactoriesPath = dubboRemoteClientOutputDir + RESOURCE_DIR_PRE + "META-INF" + File.separator + "spring" + File.separator + "org.springframework.boot.autoconfigure.AutoConfiguration.imports";
            CreateFileUtil.delAndCreateFile("dubbo/dubboApiConfig.factories.ftl", dubboClientFactoriesPath, paramMap, BASE_TEMPLATE_PATH);

            // springcloud provider config
            String remoteProviderOutputDir = projectRootDir + File.separator + projectProperties.getProjectName() + "-remote" + File.separator + projectProperties.getProjectName() + "-cloud-provider";
            Set<String> remoteProviderFileSet = this.getFolderSubFileName(remoteProviderOutputDir + CODE_DIR_PRE + remoteProviderPackage.replace(".", "/"));
            if (EmptyUtil.isNotEmpty(remoteProviderFileSet)) {
                fileMap.put("remoteProviderFileSet", remoteProviderFileSet);
            }
            String providerCodePath = remoteProviderOutputDir + CODE_DIR_PRE + remoteProviderConfigPackage.replace(".", File.separator) + File.separator + StringUtil.upperFirstChar(codeProperties.getModuleName()) + "ProviderConfig.java";
            CreateFileUtil.delAndCreateFile("springcloud/springcloudProviderConfig.java.ftl", providerCodePath, paramMap, BASE_TEMPLATE_PATH);

            String providerFactoriesPath = remoteProviderOutputDir + RESOURCE_DIR_PRE + "META-INF" + File.separator + "spring" + File.separator + "org.springframework.boot.autoconfigure.AutoConfiguration.imports";
            CreateFileUtil.delAndCreateFile("springcloud/springcloudProviderConfig.factories.ftl", providerFactoriesPath, paramMap, BASE_TEMPLATE_PATH);
            // dubbo provider config
            String dubboRemoteProviderOutputDir = projectRootDir + File.separator + projectProperties.getProjectName() + "-remote" + File.separator + projectProperties.getProjectName() + "-dubbo-provider";
            Set<String> dubboRemoteProviderFileSet = this.getFolderSubFileName(dubboRemoteProviderOutputDir + CODE_DIR_PRE + remoteProviderPackage.replace(".", "/"));
            if (EmptyUtil.isNotEmpty(dubboRemoteProviderFileSet)) {
                fileMap.put("dubboRemoteProviderFileSet", dubboRemoteProviderFileSet);
            }
            String dubboProviderCodePath = dubboRemoteProviderOutputDir + CODE_DIR_PRE + remoteProviderConfigPackage.replace(".", File.separator) + File.separator + StringUtil.upperFirstChar(codeProperties.getModuleName()) + "ProviderConfig.java";
            CreateFileUtil.delAndCreateFile("dubbo/dubboProviderConfig.java.ftl", dubboProviderCodePath, paramMap, BASE_TEMPLATE_PATH);
            String dubboProviderFactoriesPath = dubboRemoteProviderOutputDir + RESOURCE_DIR_PRE + "META-INF" + File.separator + "spring" + File.separator + "org.springframework.boot.autoconfigure.AutoConfiguration.imports";
            CreateFileUtil.delAndCreateFile("dubbo/dubboProviderConfig.factories.ftl", dubboProviderFactoriesPath, paramMap, BASE_TEMPLATE_PATH);

        } else if (ProjectType.MICROSERVICE.equals(projectProperties.getProjectType())) {
            // 处理新的remote项目结构
            String remoteParentDir = projectRootDir + File.separator + projectProperties.getProjectName() + "-remote";

            // 根据RpcType处理不同的子项目
            if (projectProperties.getRpcType().equals(RpcType.CLOUD)) {
                // 处理SpringCloud API项目
                String scApiDir = remoteParentDir + File.separator + projectProperties.getProjectName() + "-cloud-api";
                String apiPackage = projectProperties.getBasePackage() + ".api";
                String apiConfigPackage = apiPackage + ".config";
                String apiPropertyPackage = apiPackage + ".properties";
                paramMap.put("apiConfigPackage", apiConfigPackage);
                paramMap.put("apiPropertyPackage", apiPropertyPackage);
                String clientCodePath = scApiDir + CODE_DIR_PRE + apiConfigPackage.replace(".", File.separator) + File.separator + StringUtil.upperFirstChar(codeProperties.getModuleName()) + "ApiConfig.java";
                CreateFileUtil.delAndCreateFile("springcloud/springcloudApiConfig.java.ftl", clientCodePath, paramMap, BASE_TEMPLATE_PATH);
                String factoriesPath = scApiDir + RESOURCE_DIR_PRE + "META-INF" + File.separator + "spring" + File.separator + "org.springframework.boot.autoconfigure.AutoConfiguration.imports";
                CreateFileUtil.delAndCreateFile("springcloud/springcloudApiConfig.factories.ftl", factoriesPath, paramMap, BASE_TEMPLATE_PATH);
            }
            // TODO: 如果需要支持Dubbo配置，可以在这里添加相应的逻辑
            if (projectProperties.getRpcType().equals(RpcType.DUBBO)) {
                String dubboApiDir = remoteParentDir + File.separator + projectProperties.getProjectName() + "-dubbo-api";
                String apiPackage = projectProperties.getBasePackage() + ".api";
                String apiConfigPackage = apiPackage + ".config";
                String apiPropertyPackage = apiPackage + ".properties";
                paramMap.put("apiConfigPackage", apiConfigPackage);
                paramMap.put("apiPropertyPackage", apiPropertyPackage);
                String dubboClientCodePath = dubboApiDir + CODE_DIR_PRE + apiConfigPackage.replace(".", File.separator) + File.separator + StringUtil.upperFirstChar(codeProperties.getModuleName()) + "ApiConfig.java";
                CreateFileUtil.delAndCreateFile("dubbo/dubboApiConfig.java.ftl", dubboClientCodePath, paramMap, BASE_TEMPLATE_PATH);
                String dubboClientFactoriesPath = dubboApiDir + RESOURCE_DIR_PRE + "META-INF" + File.separator + "spring" + File.separator + "org.springframework.boot.autoconfigure.AutoConfiguration.imports";
                CreateFileUtil.delAndCreateFile("dubbo/dubboApiConfig.factories.ftl", dubboClientFactoriesPath, paramMap, BASE_TEMPLATE_PATH);
            }
        }
    }

    private void createPrdConfig(String projectRootDir, Map<String, Object> paramMap) throws Exception {
        String prdConfigPackage = projectProperties.getBasePackage() + ".config";
        String prdBaseDir = projectRootDir + File.separator + projectProperties.getProjectName() + "-prd";
        String prdControllerPackage = projectProperties.getBasePackage() + ".controller";
        paramMap.put("prdConfigPackage", prdConfigPackage);
        paramMap.put("prdControllerPackage", prdControllerPackage);
        String configJavaName = StringUtil.upperFirstChar(codeProperties.getModuleName()) + "ControllerConfig";
        String configFileSetName = "controllerFileSet";
        if (PrdType.SEPARATION.equals(projectProperties.getPrdType())) {
            String prdClientDir = prdBaseDir + File.separator + projectProperties.getProjectName() + "-prd-client";
            configJavaName = StringUtil.upperFirstChar(codeProperties.getModuleName()) + "ClientControllerConfig";
            createControllerConfig(prdClientDir, prdControllerPackage, configFileSetName, prdConfigPackage, configJavaName, paramMap);
            String prdBmsDir = prdBaseDir + File.separator + projectProperties.getProjectName() + "-prd-bms";
            configJavaName = StringUtil.upperFirstChar(codeProperties.getModuleName()) + "BmsControllerConfig";
            configFileSetName = "bmsControllerFileSet";
            createControllerConfig(prdBmsDir, prdControllerPackage, configFileSetName, prdConfigPackage, configJavaName, paramMap);
        } else {
            createControllerConfig(prdBaseDir, prdControllerPackage, configFileSetName, prdConfigPackage, configJavaName, paramMap);
        }

    }

    private void createControllerConfig(String projectDir, String prdControllerPackage, String controllerFileSetName, String configPackage, String configJavaName, Map<String, Object> paramMap) throws Exception {
        Map<String, Set<String>> fileMap = new HashMap<>();
        Set<String> controllerFileSet = this.getFolderSubFileName(projectDir + CODE_DIR_PRE + prdControllerPackage.replace(".", "/"));
        if (EmptyUtil.isNotEmpty(controllerFileSet)) {
            fileMap.put(controllerFileSetName, controllerFileSet);
        }
        paramMap.put("configJavaName", configJavaName);
        paramMap.put("fileMap", fileMap);

        String codePath = projectDir + CODE_DIR_PRE + configPackage.replace(".", File.separator) + File.separator + configJavaName + ".java";
        CreateFileUtil.delAndCreateFile("controller/controllerConfig.java.ftl", codePath, paramMap, BASE_TEMPLATE_PATH);

        String factoriesPath = projectDir + RESOURCE_DIR_PRE + "META-INF" + File.separator + "spring" + File.separator + "org.springframework.boot.autoconfigure.AutoConfiguration.imports";
        CreateFileUtil.delAndCreateFile("controller/controllerConfig.factories.ftl", factoriesPath, paramMap, BASE_TEMPLATE_PATH);
    }

    private Set<String> getFolderSubFileName(String folderPath) {
        Set<String> fileNames = new HashSet<>();
        File folder = new File(folderPath);
        File[] files = folder.listFiles();
        if (EmptyUtil.isNotEmpty(files)) {
            for (File file : files) {
                if (file.isFile()) {
                    if (EmptyUtil.isNotEmpty(file.getName())) {
                        String fileName = file.getName();
                        String baseName = fileName;
                        int dotIndex = fileName.lastIndexOf(".");
                        if (dotIndex > 0) {
                            baseName = fileName.substring(0, dotIndex);
                        }
                        if (EmptyUtil.isNotEmpty(baseName)) {
                            String cleaned = baseName
                                    .replace("BmsController", "")
                                    .replace("Controller", "")
                                    .replace("ApiScImpl", "")
                                    .replace("ApiServerImpl", "")
                                    .replace("ApiDubboImpl", "")
                                    .replace("RemoteImpl", "")
                                    .replace("ServerImpl", "")
                                    .replace("ServiceImpl", "")
                                    .replace("RepositoryImpl", "")
                                    .replace("Repository", "");
                            fileNames.add(cleaned);
                        }
                    }
                }
            }
        }
        return fileNames;
    }

}
