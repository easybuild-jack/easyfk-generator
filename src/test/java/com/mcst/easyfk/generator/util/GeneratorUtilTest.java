package com.mcst.easyfk.generator.util;

import com.mcst.easyfk.core.exception.BusinessException;
import com.mcst.easyfk.generator.enums.OrmType;
import com.mcst.easyfk.generator.properties.CodeProperties;
import com.mcst.easyfk.generator.vo.ModelInfo;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GeneratorUtilTest {

    @Test
    void shouldMergeDbTablesAndModelList() {
        CodeProperties codeProperties = new CodeProperties();
        codeProperties.setDbTables("t_user");
        codeProperties.setModelList(List.of(new ModelInfo().setModelName("Order")));

        List<ModelInfo> modelInfoList = GeneratorUtil.getModelInfoList(codeProperties, OrmType.MYBATIS, false);

        assertEquals(2, modelInfoList.size());
        assertEquals("User", modelInfoList.get(0).getModelName());
        assertEquals("t_user", modelInfoList.get(0).getTableName());
        assertEquals("Order", modelInfoList.get(1).getModelName());
    }

    @Test
    void shouldThrowWhenModelNameDuplicated() {
        CodeProperties codeProperties = new CodeProperties();
        codeProperties.setDbTables("t_user");
        codeProperties.setModelList(List.of(new ModelInfo().setModelName("User")));

        assertThrows(BusinessException.class,
                () -> GeneratorUtil.getModelInfoList(codeProperties, OrmType.MYBATIS, false));
    }
}
