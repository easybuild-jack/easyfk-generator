package ${remotePropertyPackage};

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
/**
*
* @author ${author}
* @since ${date}
*/
@ConfigurationProperties(prefix = "easyfk.config.remote.${moduleName?uncap_first}")
@Data
public class ${moduleName?cap_first}ApiProperties {
    /**
    * ${moduleName?cap_first}模块的ServiceId配置
    */
    private String serviceId;
    /**
    * path 前缀地址，如：/remote 或/api 等，默认/remote
    */
    private String basePath;
}
