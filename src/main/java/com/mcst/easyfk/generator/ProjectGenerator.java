package com.mcst.easyfk.generator;

import cn.hutool.core.io.FileUtil;
import com.mcst.easyfk.core.exception.BusinessException;
import com.mcst.easyfk.core.utils.common.EmptyUtil;
import com.mcst.easyfk.generator.enums.BuildType;
import com.mcst.easyfk.generator.enums.GradleType;
import com.mcst.easyfk.generator.enums.OrmType;
import com.mcst.easyfk.generator.enums.PrdType;
import com.mcst.easyfk.generator.enums.ProjectType;
import com.mcst.easyfk.generator.properties.ProjectProperties;
import com.mcst.easyfk.generator.util.CreateFileUtil;
import com.mcst.easyfk.generator.util.GeneratorUtil;
import lombok.extern.slf4j.Slf4j;

import jakarta.annotation.Resource;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * @author eb-jack
 */
@Slf4j
public class ProjectGenerator {

    private static final String MAIN_CODE_DIR = "src" + File.separator + "main" + File.separator + "java";
    private static final String MAIN_SOURCE_DIR = "src" + File.separator + "main" + File.separator + "resources";
    private static final String TEST_CODE_DIR = "src" + File.separator + "test" + File.separator + "java";
    private static final String TEST_SOURCE_DIR = "src" + File.separator + "test" + File.separator + "resources";

    private static final String BASE_TEMPLATE_PATH = "/templates/project";

    @Resource
    private ProjectProperties projectProperties;

    public ProjectGenerator() {}

    public ProjectGenerator(ProjectProperties projectProperties) {
        this.projectProperties = projectProperties;
    }

    public void create() {
        if (EmptyUtil.isEmpty(projectProperties.getGroupId())) {
            throw new BusinessException("groupId不可为空");
        }
        String projectDir = projectProperties.getProjectDir();
        if (EmptyUtil.isEmpty(projectDir)) {
            throw new BusinessException("项目根路径不可为空");
        }
        String projectName = projectProperties.getProjectName();
        if (EmptyUtil.isEmpty(projectName)) {
            throw new BusinessException("项目名称不可为空");
        }
        String basePackage = projectProperties.getBasePackage();
        if (EmptyUtil.isEmpty(basePackage)) {
            throw new BusinessException("项目包根路径不可为空");
        }
        boolean appTypeRequired = ProjectType.SINGLE.equals(projectProperties.getProjectType())
                || PrdType.SINGLE.equals(projectProperties.getPrdType())
                || ProjectType.MICRO_PRD.equals(projectProperties.getProjectType());
        if (appTypeRequired && projectProperties.getAppType() == null) {
            throw new BusinessException("应用类型(appType)不可为空，请指定 BMS 或 CLIENT");
        }

        String projectRoot = projectDir + File.separator + projectName;
        Map<String, String> rootConfigFile = new HashMap<>();
        rootConfigFile.put("pomFile", projectRoot + File.separator + "pom.xml");
        rootConfigFile.put("rootGroovyBuild", projectRoot + File.separator + "build.gradle");
        rootConfigFile.put("rootKotlinBuild", projectRoot + File.separator + "build.gradle.kts");
        rootConfigFile.put("groovySettings", projectRoot + File.separator + "settings.gradle");
        rootConfigFile.put("kotlinSettings", projectRoot + File.separator + "settings.gradle.kts");

        try {
            //创建项目根路径文件夹
            FileUtil.mkdir(new File(projectRoot));
            if (ProjectType.SINGLE.equals(projectProperties.getProjectType())) {
                if (BuildType.MAVEN.equals(projectProperties.getBuildType())) {
                    String templateFileName = "single_pom.xml.ftl";
                    //创建根POM文件
                    CreateFileUtil.createOnNotExistFile(templateFileName, rootConfigFile.get("pomFile"), projectProperties, BASE_TEMPLATE_PATH);
                } else {
                    createRootGradleConfigFile(rootConfigFile, "single");
                }
                createCodeDir(projectRoot, true, true);
            } else if (ProjectType.MICRO_PRD.equals(projectProperties.getProjectType())) {
                if (BuildType.MAVEN.equals(projectProperties.getBuildType())) {
                    String templateFileName = "micro_prd_pom.xml.ftl";
                    CreateFileUtil.createOnNotExistFile(templateFileName, rootConfigFile.get("pomFile"), projectProperties, BASE_TEMPLATE_PATH);
                } else {
                    createRootGradleConfigFile(rootConfigFile, "micro_prd");
                }
                createCodeDir(projectRoot, true, true);
            } else if (ProjectType.MICROSERVICE.equals(projectProperties.getProjectType())) {
                createApiAndServerAndPrdFile(rootConfigFile, projectName, projectRoot);
            } else {
                createApiAndServerAndPrdFile(rootConfigFile, projectName, projectRoot);
                //创建Remote Parent项目
                String remoteParentProjectName = projectName + "-remote";
                String remoteParentProjectRoot = projectRoot + File.separator + remoteParentProjectName;
                createRemoteProjects(rootConfigFile, projectName, remoteParentProjectRoot);
                if (ProjectType.SMART_ORM.equals(projectProperties.getProjectType())) {
                    //创建ORM Parent项目（ORM 层抽离为独立模块）
                    String ormParentProjectName = projectName + "-orm";
                    String ormParentProjectRoot = projectRoot + File.separator + ormParentProjectName;
                    createOrmProjects(rootConfigFile, projectName, ormParentProjectRoot);
                }
            }
            log.info("生成项目成功");
        } catch (Exception e) {
            throw GeneratorUtil.generateException("生成项目失败", e);
        }
    }

    private void createApiAndServerAndPrdFile(Map<String, String> rootConfigFile, String projectName, String projectRoot) throws Exception {
        //创建根配置文件
        createRootConfigFile(rootConfigFile);
        //创建API项目
        //Map<String,String> configFile=new HashMap<>();
        String apiProjectName = projectName + "-api";
        String apiProjectRoot = projectRoot + File.separator + apiProjectName;
        createCodeDir(apiProjectRoot, false, false);
        String templateFileName = null;
        if (ProjectType.MICROSERVICE.equals(projectProperties.getProjectType())) {
            if (BuildType.MAVEN.equals(projectProperties.getBuildType())) {
                templateFileName = "micro_api_pom.xml.ftl";
            } else {
                if (GradleType.GROOVY.equals(projectProperties.getGradleType())) {
                    templateFileName = "gradle/groovy/micro_api_build.gradle.ftl";
                } else {
                    templateFileName = "gradle/kotlin/micro_api_build.gradle.kts.ftl";
                }
            }
        } else {
            if (BuildType.MAVEN.equals(projectProperties.getBuildType())) {
                templateFileName = "smart_api_pom.xml.ftl";
            } else {
                if (GradleType.GROOVY.equals(projectProperties.getGradleType())) {
                    templateFileName = "gradle/groovy/smart_api_build.gradle.ftl";
                } else {
                    templateFileName = "gradle/kotlin/smart_api_build.gradle.kts.ftl";
                }
            }
        }
        rootConfigFile.put("pomFile", apiProjectRoot + File.separator + "pom.xml");
        rootConfigFile.put("groovyBuildFile", apiProjectRoot + File.separator + "build.gradle");
        rootConfigFile.put("kotlinBuildFile", apiProjectRoot + File.separator + "build.gradle.kts");
        rootConfigFile.put("templateFileName", templateFileName);
        this.createConfigFile(rootConfigFile);

        //创建Server项目
        String serverProjectName = projectName + "-server";
        String serverProjectRoot = projectRoot + File.separator + serverProjectName;
        createCodeDir(serverProjectRoot, true, true);
        if (BuildType.MAVEN.equals(projectProperties.getBuildType())) {
            templateFileName = "server_pom.xml.ftl";
        } else {
            if (GradleType.GROOVY.equals(projectProperties.getGradleType())) {
                templateFileName = "gradle/groovy/server_build.gradle.ftl";
            } else {
                templateFileName = "gradle/kotlin/server_build.gradle.kts.ftl";
            }
        }
        rootConfigFile.put("pomFile", serverProjectRoot + File.separator + "pom.xml");
        rootConfigFile.put("groovyBuildFile", serverProjectRoot + File.separator + "build.gradle");
        rootConfigFile.put("kotlinBuildFile", serverProjectRoot + File.separator + "build.gradle.kts");
        rootConfigFile.put("templateFileName", templateFileName);
        this.createConfigFile(rootConfigFile);

        //生成PRD层项目
        if (!PrdType.NONE.equals(projectProperties.getPrdType())) {
            if (PrdType.SINGLE.equals(projectProperties.getPrdType())) {
                //创建PRD Single项目
                String prdProjectName = projectName + "-prd";
                String prdProjectRoot = projectRoot + File.separator + prdProjectName;
                // String prdPomFile = prdProjectRoot + File.separator + "pom.xml";
                createCodeDir(prdProjectRoot, true, true);
                if (BuildType.MAVEN.equals(projectProperties.getBuildType())) {
                    templateFileName = "prd_single_pom.xml.ftl";
                } else {
                    if (GradleType.GROOVY.equals(projectProperties.getGradleType())) {
                        templateFileName = "gradle/groovy/prd_single_build.gradle.ftl";
                    } else {
                        templateFileName = "gradle/kotlin/prd_single_build.gradle.kts.ftl";
                    }
                }
                rootConfigFile.put("pomFile", prdProjectRoot + File.separator + "pom.xml");
                rootConfigFile.put("groovyBuildFile", prdProjectRoot + File.separator + "build.gradle");
                rootConfigFile.put("kotlinBuildFile", prdProjectRoot + File.separator + "build.gradle.kts");
                rootConfigFile.put("templateFileName", templateFileName);
                this.createConfigFile(rootConfigFile);
                // CreateFileUtil.createOnNotExistFile(templateFileName, prdPomFile, projectProperties,BASE_TEMPLATE_PATH);
            } else {
                //创建PRD Client项目（与server平级）
                String prdClientProjectName = projectName + "-prd-client";
                String prdClientProjectRoot = projectRoot + File.separator + prdClientProjectName;
                createCodeDir(prdClientProjectRoot, true, true);

                if (BuildType.MAVEN.equals(projectProperties.getBuildType())) {
                    templateFileName = "prd_client_pom.xml.ftl";
                } else {
                    if (GradleType.GROOVY.equals(projectProperties.getGradleType())) {
                        templateFileName = "gradle/groovy/prd_client_build.gradle.ftl";
                    } else {
                        templateFileName = "gradle/kotlin/prd_client_build.gradle.kts.ftl";
                    }
                }
                rootConfigFile.put("pomFile", prdClientProjectRoot + File.separator + "pom.xml");
                rootConfigFile.put("groovyBuildFile", prdClientProjectRoot + File.separator + "build.gradle");
                rootConfigFile.put("kotlinBuildFile", prdClientProjectRoot + File.separator + "build.gradle.kts");
                rootConfigFile.put("templateFileName", templateFileName);
                this.createConfigFile(rootConfigFile);

                //创建PRD Bms项目（与server平级）
                String prdBmsProjectName = projectName + "-prd-bms";
                String prdBmsProjectRoot = projectRoot + File.separator + prdBmsProjectName;
                createCodeDir(prdBmsProjectRoot, true, true);

                if (BuildType.MAVEN.equals(projectProperties.getBuildType())) {
                    templateFileName = "prd_bms_pom.xml.ftl";
                } else {
                    if (GradleType.GROOVY.equals(projectProperties.getGradleType())) {
                        templateFileName = "gradle/groovy/prd_bms_build.gradle.ftl";
                    } else {
                        templateFileName = "gradle/kotlin/prd_bms_build.gradle.kts.ftl";
                    }
                }
                rootConfigFile.put("pomFile", prdBmsProjectRoot + File.separator + "pom.xml");
                rootConfigFile.put("groovyBuildFile", prdBmsProjectRoot + File.separator + "build.gradle");
                rootConfigFile.put("kotlinBuildFile", prdBmsProjectRoot + File.separator + "build.gradle.kts");
                rootConfigFile.put("templateFileName", templateFileName);
                this.createConfigFile(rootConfigFile);
            }
        }
    }

    /**
     * 创建根配置文件
     */
    private void createRootConfigFile(Map<String, String> rootConfigFile) throws Exception {
        if (BuildType.MAVEN.equals(projectProperties.getBuildType())) {
            //创建根POM文件
            CreateFileUtil.createOnNotExistFile("master_pom.xml.ftl", rootConfigFile.get("pomFile"), projectProperties, BASE_TEMPLATE_PATH);
        } else {
            createRootGradleConfigFile(rootConfigFile, "master");
        }
    }

    /**
     * 创建根项目的 Gradle 构建脚本（build 脚本 + settings 脚本），按 gradleType 选择模板：
     * groovy 生成 build.gradle + settings.gradle，kotlin 生成 build.gradle.kts + settings.gradle.kts
     *
     * @param templatePrefix 构建脚本模板前缀：master（多模块根项目）/ single（单体）/ micro_prd（微服务产品层）
     */
    private void createRootGradleConfigFile(Map<String, String> rootConfigFile, String templatePrefix) throws Exception {
        // single 与 micro_prd 共用同一个 settings 模板
        String settingsTemplatePrefix = "master".equals(templatePrefix) ? "master" : "single";
        if (GradleType.GROOVY.equals(projectProperties.getGradleType())) {
            //创建根配置文件
            CreateFileUtil.createOnNotExistFile("gradle/groovy/" + templatePrefix + "_build.gradle.ftl",
                    rootConfigFile.get("rootGroovyBuild"), projectProperties, BASE_TEMPLATE_PATH);
            CreateFileUtil.createOnNotExistFile("gradle/groovy/" + settingsTemplatePrefix + "_settings.gradle.ftl",
                    rootConfigFile.get("groovySettings"), projectProperties, BASE_TEMPLATE_PATH);
        } else {
            CreateFileUtil.createOnNotExistFile("gradle/kotlin/" + templatePrefix + "_build.gradle.kts.ftl",
                    rootConfigFile.get("rootKotlinBuild"), projectProperties, BASE_TEMPLATE_PATH);
            CreateFileUtil.createOnNotExistFile("gradle/kotlin/" + settingsTemplatePrefix + "_settings.gradle.kts.ftl",
                    rootConfigFile.get("kotlinSettings"), projectProperties, BASE_TEMPLATE_PATH);
        }
    }

    private void createConfigFile(Map<String, String> configFile) throws Exception {
        if (BuildType.MAVEN.equals(projectProperties.getBuildType())) {
            //创建根POM文件
            CreateFileUtil.createOnNotExistFile(configFile.get("templateFileName"), configFile.get("pomFile"), projectProperties, BASE_TEMPLATE_PATH);
        } else {
            if (GradleType.GROOVY.equals(projectProperties.getGradleType())) {
                CreateFileUtil.createOnNotExistFile(configFile.get("templateFileName"), configFile.get("groovyBuildFile"), projectProperties, BASE_TEMPLATE_PATH);
            } else {
                CreateFileUtil.createOnNotExistFile(configFile.get("templateFileName"), configFile.get("kotlinBuildFile"), projectProperties, BASE_TEMPLATE_PATH);
            }
        }
    }

    /**
     * 使用 File 重载：hutool 的 String 重载会把相对路径解析到 classpath 下，与 CreateFileUtil 按工作目录解析不一致
     */
    private void createCodeDir(String rootPath, boolean createResource, boolean createTest) {
        FileUtil.mkParentDirs(new File(rootPath, MAIN_CODE_DIR));
        if (createResource) {
            FileUtil.mkParentDirs(new File(rootPath, MAIN_SOURCE_DIR));
        }
        if (createTest) {
            FileUtil.mkParentDirs(new File(rootPath, TEST_CODE_DIR));
            FileUtil.mkParentDirs(new File(rootPath, TEST_SOURCE_DIR));
        }
    }

    /**
     * 创建Remote项目结构
     */
    private void createRemoteProjects(Map<String, String> rootConfigFile, String projectName, String remoteParentProjectRoot) throws Exception {
        // 创建Remote Parent项目
        String templateFileName = null;
        if (BuildType.MAVEN.equals(projectProperties.getBuildType())) {
            templateFileName = "remote_parent_pom.xml.ftl";
        } else {
            if (GradleType.GROOVY.equals(projectProperties.getGradleType())) {
                templateFileName = "gradle/groovy/remote_parent_build.gradle.ftl";
            } else {
                templateFileName = "gradle/kotlin/remote_parent_build.gradle.kts.ftl";
            }
        }
        rootConfigFile.put("pomFile", remoteParentProjectRoot + File.separator + "pom.xml");
        rootConfigFile.put("groovyBuildFile", remoteParentProjectRoot + File.separator + "build.gradle");
        rootConfigFile.put("kotlinBuildFile", remoteParentProjectRoot + File.separator + "build.gradle.kts");
        rootConfigFile.put("templateFileName", templateFileName);
        this.createConfigFile(rootConfigFile);
        // 创建SpringCloud和Dubbo两套项目
        createSpringCloudProjects(rootConfigFile, projectName, remoteParentProjectRoot);
        createDubboProjects(rootConfigFile, projectName, remoteParentProjectRoot);
    }

    /**
     * 创建SpringCloud项目
     */
    private void createSpringCloudProjects(Map<String, String> rootConfigFile, String projectName, String remoteParentProjectRoot) throws Exception {
        // 创建SC API项目
        String scApiProjectName = projectName + "-cloud-api";
        String scApiProjectRoot = remoteParentProjectRoot + File.separator + scApiProjectName;
        createCodeDir(scApiProjectRoot, true, false);

        String templateFileName = null;
        if (BuildType.MAVEN.equals(projectProperties.getBuildType())) {
            templateFileName = "sc_api_pom.xml.ftl";
        } else {
            if (GradleType.GROOVY.equals(projectProperties.getGradleType())) {
                templateFileName = "gradle/groovy/sc_api_build.gradle.ftl";
            } else {
                templateFileName = "gradle/kotlin/sc_api_build.gradle.kts.ftl";
            }
        }
        rootConfigFile.put("pomFile", scApiProjectRoot + File.separator + "pom.xml");
        rootConfigFile.put("groovyBuildFile", scApiProjectRoot + File.separator + "build.gradle");
        rootConfigFile.put("kotlinBuildFile", scApiProjectRoot + File.separator + "build.gradle.kts");
        rootConfigFile.put("templateFileName", templateFileName);
        this.createConfigFile(rootConfigFile);

        // 创建SC Provider项目
        String scProviderProjectName = projectName + "-cloud-provider";
        String scProviderProjectRoot = remoteParentProjectRoot + File.separator + scProviderProjectName;
        createCodeDir(scProviderProjectRoot, true, false);

        if (BuildType.MAVEN.equals(projectProperties.getBuildType())) {
            templateFileName = "sc_provider_pom.xml.ftl";
        } else {
            if (GradleType.GROOVY.equals(projectProperties.getGradleType())) {
                templateFileName = "gradle/groovy/sc_provider_build.gradle.ftl";
            } else {
                templateFileName = "gradle/kotlin/sc_provider_build.gradle.kts.ftl";
            }
        }
        rootConfigFile.put("pomFile", scProviderProjectRoot + File.separator + "pom.xml");
        rootConfigFile.put("groovyBuildFile", scProviderProjectRoot + File.separator + "build.gradle");
        rootConfigFile.put("kotlinBuildFile", scProviderProjectRoot + File.separator + "build.gradle.kts");
        rootConfigFile.put("templateFileName", templateFileName);
        this.createConfigFile(rootConfigFile);
    }

    /**
     * 创建Dubbo项目
     */
    private void createDubboProjects(Map<String, String> rootConfigFile, String projectName, String remoteParentProjectRoot) throws Exception {
        // 创建Dubbo API项目
        String dubboApiProjectName = projectName + "-dubbo-api";
        String dubboApiProjectRoot = remoteParentProjectRoot + File.separator + dubboApiProjectName;
        createCodeDir(dubboApiProjectRoot, true, false);

        String templateFileName = null;
        if (BuildType.MAVEN.equals(projectProperties.getBuildType())) {
            templateFileName = "dubbo_api_pom.xml.ftl";
        } else {
            if (GradleType.GROOVY.equals(projectProperties.getGradleType())) {
                templateFileName = "gradle/groovy/dubbo_api_build.gradle.ftl";
            } else {
                templateFileName = "gradle/kotlin/dubbo_api_build.gradle.kts.ftl";
            }
        }
        rootConfigFile.put("pomFile", dubboApiProjectRoot + File.separator + "pom.xml");
        rootConfigFile.put("groovyBuildFile", dubboApiProjectRoot + File.separator + "build.gradle");
        rootConfigFile.put("kotlinBuildFile", dubboApiProjectRoot + File.separator + "build.gradle.kts");
        rootConfigFile.put("templateFileName", templateFileName);
        this.createConfigFile(rootConfigFile);

        // 创建Dubbo Provider项目
        String dubboProviderProjectName = projectName + "-dubbo-provider";
        String dubboProviderProjectRoot = remoteParentProjectRoot + File.separator + dubboProviderProjectName;
        createCodeDir(dubboProviderProjectRoot, true, false);

        if (BuildType.MAVEN.equals(projectProperties.getBuildType())) {
            templateFileName = "dubbo_provider_pom.xml.ftl";
        } else {
            if (GradleType.GROOVY.equals(projectProperties.getGradleType())) {
                templateFileName = "gradle/groovy/dubbo_provider_build.gradle.ftl";
            } else {
                templateFileName = "gradle/kotlin/dubbo_provider_build.gradle.kts.ftl";
            }
        }
        rootConfigFile.put("pomFile", dubboProviderProjectRoot + File.separator + "pom.xml");
        rootConfigFile.put("groovyBuildFile", dubboProviderProjectRoot + File.separator + "build.gradle");
        rootConfigFile.put("kotlinBuildFile", dubboProviderProjectRoot + File.separator + "build.gradle.kts");
        rootConfigFile.put("templateFileName", templateFileName);
        this.createConfigFile(rootConfigFile);
    }

    /**
     * 创建 ORM 模块项目结构（仅 SMART_ORM 项目类型）：
     * -orm 父模块 + -orm-api(契约) + 各 ORM 实现模块(plus/flex/hibernate)
     */
    private void createOrmProjects(Map<String, String> rootConfigFile, String projectName, String ormParentProjectRoot) throws Exception {
        // 创建 ORM 父模块
        String templateFileName = null;
        if (BuildType.MAVEN.equals(projectProperties.getBuildType())) {
            templateFileName = "orm_parent_pom.xml.ftl";
        } else {
            if (GradleType.GROOVY.equals(projectProperties.getGradleType())) {
                templateFileName = "gradle/groovy/orm_parent_build.gradle.ftl";
            } else {
                templateFileName = "gradle/kotlin/orm_parent_build.gradle.kts.ftl";
            }
        }
        rootConfigFile.put("pomFile", ormParentProjectRoot + File.separator + "pom.xml");
        rootConfigFile.put("groovyBuildFile", ormParentProjectRoot + File.separator + "build.gradle");
        rootConfigFile.put("kotlinBuildFile", ormParentProjectRoot + File.separator + "build.gradle.kts");
        rootConfigFile.put("templateFileName", templateFileName);
        this.createConfigFile(rootConfigFile);

        // 创建 ORM API（契约）模块
        String ormApiProjectName = projectName + "-orm-api";
        String ormApiProjectRoot = ormParentProjectRoot + File.separator + ormApiProjectName;
        createCodeDir(ormApiProjectRoot, true, false);
        if (BuildType.MAVEN.equals(projectProperties.getBuildType())) {
            templateFileName = "orm_api_pom.xml.ftl";
        } else {
            if (GradleType.GROOVY.equals(projectProperties.getGradleType())) {
                templateFileName = "gradle/groovy/orm_api_build.gradle.ftl";
            } else {
                templateFileName = "gradle/kotlin/orm_api_build.gradle.kts.ftl";
            }
        }
        rootConfigFile.put("pomFile", ormApiProjectRoot + File.separator + "pom.xml");
        rootConfigFile.put("groovyBuildFile", ormApiProjectRoot + File.separator + "build.gradle");
        rootConfigFile.put("kotlinBuildFile", ormApiProjectRoot + File.separator + "build.gradle.kts");
        rootConfigFile.put("templateFileName", templateFileName);
        this.createConfigFile(rootConfigFile);

        // 创建各 ORM 实现模块（plus/flex/hibernate）
        for (OrmType ormType : projectProperties.getOrmModules()) {
            String ormModuleName = ProjectProperties.ormModuleName(ormType);
            String ormImplProjectName = projectName + "-orm-" + ormModuleName;
            String ormImplProjectRoot = ormParentProjectRoot + File.separator + ormImplProjectName;
            createCodeDir(ormImplProjectRoot, true, false);

            if (BuildType.MAVEN.equals(projectProperties.getBuildType())) {
                templateFileName = "orm_" + ormModuleName + "_pom.xml.ftl";
            } else {
                if (GradleType.GROOVY.equals(projectProperties.getGradleType())) {
                    templateFileName = "gradle/groovy/orm_" + ormModuleName + "_build.gradle.ftl";
                } else {
                    templateFileName = "gradle/kotlin/orm_" + ormModuleName + "_build.gradle.kts.ftl";
                }
            }
            rootConfigFile.put("pomFile", ormImplProjectRoot + File.separator + "pom.xml");
            rootConfigFile.put("groovyBuildFile", ormImplProjectRoot + File.separator + "build.gradle");
            rootConfigFile.put("kotlinBuildFile", ormImplProjectRoot + File.separator + "build.gradle.kts");
            rootConfigFile.put("templateFileName", templateFileName);
            this.createConfigFile(rootConfigFile);
        }
    }

}
