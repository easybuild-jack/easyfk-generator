package com.mcst.easyfk.generator.config;

import com.mcst.easyfk.generator.*;
import com.mcst.easyfk.generator.properties.CodeProperties;
import com.mcst.easyfk.generator.properties.ProjectProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author liu yijun
 */
@Configuration
@EnableConfigurationProperties({ProjectProperties.class, CodeProperties.class})
public class GeneratorConfig {

    @Bean
    public CodeGenerator codeGenerator() {
        return new CodeGenerator();
    }

    @Bean
    public ProjectGenerator projectGenerator() {
        return new ProjectGenerator();
    }

    @Bean
    public ConfigGenerator projectConfigGenerator() {
        return new ConfigGenerator();
    }

    @Bean
    public ModelGenerator modelGenerator() {
        return new ModelGenerator();
    }

    @Bean
    public EasyfkGenerator easyfkGenerator() {
        return new EasyfkGenerator();
    }

}
