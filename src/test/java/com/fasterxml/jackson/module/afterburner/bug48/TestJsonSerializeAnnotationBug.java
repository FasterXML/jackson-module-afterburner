package com.fasterxml.jackson.module.afterburner.bug48;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.afterburner.AfterburnerModule;
import com.fasterxml.jackson.module.afterburner.AfterburnerTestBase;

import java.math.BigDecimal;

/**
 * @author Joost van de Wijgerd
 */
public class TestJsonSerializeAnnotationBug extends AfterburnerTestBase {
    public void testAfterburnerModule() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new AfterburnerModule());

        String value = objectMapper.writeValueAsString(new TestObjectWithJsonSerialize(new BigDecimal("870.04")));


    }
}
