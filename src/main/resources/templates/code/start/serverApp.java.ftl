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
public class ServerApp {
    public static void main(String[] args) {
    <#if ormType == "MYBATIS">
        ApplicationStarter.run(ServerApp.class, args);
    <#else >
        SpringApplication.run(ServerApp.class, args);
    </#if>
    }
}
