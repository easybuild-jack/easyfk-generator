package com.mcst.easyfk.generator.cli;

import com.mcst.easyfk.generator.EasyfkGenerator;
import com.mcst.easyfk.generator.properties.CodeProperties;
import com.mcst.easyfk.generator.properties.ProjectProperties;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.*;
import java.util.concurrent.Callable;

/**
 * EasyFK Generator CLI 入口
 *
 * @author eb-jack
 */
@Command(name = "efg",
        mixinStandardHelpOptions = true,
        version = "easyfk-generator 3.2.12",
        description = "EasyFK 代码生成器命令行工具",
        subcommands = {GeneratorCli.InitCommand.class})
public class GeneratorCli implements Callable<Integer> {

    @Option(names = {"-c", "--config"}, description = "配置文件路径 (默认: generator.yml)", defaultValue = "generator.yml")
    private String configFile;

    @Option(names = "-p", description = "生成项目骨架")
    private boolean project;

    @Option(names = "-m", description = "生成 Entity + Mapper")
    private boolean model;

    @Option(names = "-s", description = "生成业务代码 (Repository/Service/API/Remote/Controller)")
    private boolean service;

    @Option(names = "-a", description = "生成自动装配配置")
    private boolean autoConfig;

    @Option(names = "-d", description = "刷新 DTO 和 Param")
    private boolean dto;

    @Override
    public Integer call() {
        File file = new File(configFile);
        if (!file.exists()) {
            System.err.println("[错误] 配置文件不存在: " + file.getAbsolutePath());
            System.err.println("请先执行 efg init 生成配置模板");
            return 1;
        }

        CliConfigLoader loader;
        try {
            loader = new CliConfigLoader(configFile);
        } catch (Exception e) {
            System.err.println("[错误] 解析配置文件失败: " + e.getMessage());
            return 1;
        }

        ProjectProperties pp = loader.getProjectProperties();
        CodeProperties cp = loader.getCodeProperties();
        EasyfkGenerator generator = new EasyfkGenerator(pp, cp);

        boolean hasFlag = project || model || service || autoConfig || dto;

        try {
            if (!hasFlag) {
                System.out.println("[开始] 全量生成...");
                generator.generateAll();
            } else {
                if (project && model && !service && !autoConfig && !dto) {
                    System.out.println("[开始] 生成项目骨架 + Entity + Mapper...");
                    generator.generateProjectAndModel();
                } else if (service && autoConfig && !project && !model && !dto) {
                    System.out.println("[开始] 生成业务代码 + 自动装配配置...");
                    generator.generateCodeAndConfig();
                } else {
                    if (project) {
                        System.out.println("[开始] 生成项目骨架...");
                        generator.generateProject();
                    }
                    if (model) {
                        System.out.println("[开始] 生成 Entity + Mapper...");
                        generator.generateModel();
                    }
                    if (service) {
                        System.out.println("[开始] 生成业务代码...");
                        generator.generateCode();
                    }
                    if (autoConfig) {
                        System.out.println("[开始] 生成自动装配配置...");
                        generator.generateConfig();
                    }
                    if (dto) {
                        System.out.println("[开始] 刷新 DTO 和 Param...");
                        generator.updateDtoAndParam();
                    }
                }
            }
            System.out.println("[完成] 代码生成成功！");
            return 0;
        } catch (Exception e) {
            System.err.println("[错误] 代码生成失败: " + e.getMessage());
            e.printStackTrace();
            return 1;
        }
    }

    public static void main(String[] args) {
        int exitCode = new CommandLine(new GeneratorCli()).execute(args);
        System.exit(exitCode);
    }

    /**
     * init 子命令：生成配置模板文件
     */
    @Command(name = "init", description = "生成配置模板文件 generator.yml")
    static class InitCommand implements Callable<Integer> {

        @Option(names = {"-o", "--output"}, description = "输出文件路径 (默认: generator.yml)", defaultValue = "generator.yml")
        private String output;

        @Override
        public Integer call() {
            File file = new File(output);
            if (file.exists()) {
                System.err.println("[警告] 文件已存在: " + file.getAbsolutePath());
                System.err.println("如需覆盖请先手动删除，或使用 -o 指定其他路径");
                return 1;
            }
            try (InputStream tpl = getClass().getResourceAsStream("/generator-template.yml")) {
                if (tpl == null) {
                    System.err.println("[错误] 找不到内置配置模板");
                    return 1;
                }
                try (OutputStream out = new FileOutputStream(file)) {
                    tpl.transferTo(out);
                }
            } catch (IOException e) {
                System.err.println("[错误] 写入文件失败: " + e.getMessage());
                return 1;
            }
            System.out.println("[成功] 配置模板已生成: " + file.getAbsolutePath());
            System.out.println("请编辑该文件填入项目信息和数据库配置，然后执行：");
            System.out.println("  efg");
            return 0;
        }
    }
}
