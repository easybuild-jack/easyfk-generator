package com.mcst.easyfk.generator.vo;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author eb-jack
 * @version 1.0
 */
@Data
@Accessors(chain = true)
public class ImportPackage {
    /**
     * 包名
     */
    private String packageName;
}
