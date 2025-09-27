package com.mcst.easyfk.generator.util;

import cn.hutool.core.io.FileUtil;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.Version;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * @author liu yijun
 * @version 1.0
 */
public class CreateFileUtil {

    /**
     * 创建文件 文件存在时，不生成
     *
     */
    public static synchronized void createOnNotExistFile(String templateFile, String filePath, Object param,
            String templateBasePath) throws Exception {
        File file = new File(filePath);
        if (file.exists()) {
            return;
        }
        createFile(templateFile, filePath, param, templateBasePath);
    }

    public static synchronized void delAndCreateFile(String templateFile, String filePath, Object param,
            String templateBasePath) throws Exception {
        File file = new File(filePath);
        if (file.exists()) {
            if (!file.delete()) {
                // fallback
                cn.hutool.core.io.FileUtil.del(file);
            }
        }
        createFile(templateFile, filePath, param, templateBasePath);
    }

    private static synchronized void createFile(String templateFile, String filePath, Object param,
            String templateBasePath) throws Exception {
        Configuration configuration = new Configuration(new Version("2.3.31"));
        configuration.setClassForTemplateLoading(CreateFileUtil.class, templateBasePath);
        Template template = configuration.getTemplate(templateFile);
        FileUtil.touch(filePath);
        File docFile = new File(filePath);
        try (Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(docFile), StandardCharsets.UTF_8))) {
            template.process(param, out);
        }
    }
}
