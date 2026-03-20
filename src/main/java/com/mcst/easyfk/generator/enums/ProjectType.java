package com.mcst.easyfk.generator.enums;

/**
 * @author eb-jack
 */

public enum ProjectType {

    /**
     * 单体项目
     */
    SINGLE,
    /**
     * 微服务项目
     */
    MICROSERVICE,
    /**
     * 智能项目
     */
    SMART,
    /**
     * 微服务产品层项目（仅启动类+配置，通过RPC调用微服务）
     */
    MICRO_PRD
}
