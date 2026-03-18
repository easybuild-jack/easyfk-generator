package com.mcst.easyfk.generator.properties;

import lombok.Data;

/**
 * 业务模块配置
 *
 * @author eb-jack
 */
@Data
public class ModuleInfo {

    /**
     * 模块名称，如 auth、trading、payment
     */
    private String name;

    /**
     * 是否包含PRD子依赖
     */
    private Boolean hasPrd = Boolean.TRUE;

    /**
     * PRD是否区分 bms/client，true: {module}-prd-bms / {module}-prd-client; false: {module}-prd
     */
    private Boolean prdSplit = Boolean.TRUE;

    /**
     * 是否包含Server子依赖
     */
    private Boolean hasServer = Boolean.TRUE;

    /**
     * 是否包含ORM子依赖
     */
    private Boolean hasOrm = Boolean.TRUE;

}
