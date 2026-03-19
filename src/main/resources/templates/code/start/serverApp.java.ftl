package ${basePackage};

<#if ormType == "MYBATIS">
import com.mcst.easyfk.service.mybatisplus.ApplicationStarter;
<#else >
import org.springframework.boot.SpringApplication;
</#if>
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
*
* @author ${author}
* @since ${date}
*/
@SpringBootApplication
public class ${appClassName} {
    public static void main(String[] args) {
    <#if ormType == "MYBATIS">
        ApplicationStarter.run(${appClassName}.class, args);
    <#else >
        SpringApplication.run(${appClassName}.class, args);
    </#if>
    }
}
