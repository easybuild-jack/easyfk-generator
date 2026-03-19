package ${basePackage};

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
<#if rpcType == "CLOUD">
import org.springframework.cloud.openfeign.EnableFeignClients;
</#if>


/**
* @author ${author}
* @since ${date}
*/
@ComponentScan(value = {"${controllerBasePackage}"})
<#if rpcType == "CLOUD">
@EnableFeignClients(basePackages = "${groupId }.**.api.remote")
</#if>
@SpringBootApplication
public class ${appClassName} {
    public static void main(String[] args) {
        SpringApplication.run(${appClassName}.class, args);
    }
}
