package com.fasterxml.jackson.module.afterburner.ser;

import com.fasterxml.jackson.annotation.JsonInclude;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.fasterxml.jackson.module.afterburner.AfterburnerTestBase;

public class TestInclusionAnnotations extends AfterburnerTestBase
{
    public class IntWrapper
    {
        @JsonInclude(JsonInclude.Include.NON_NULL) 
        public Integer value;
        
        public IntWrapper(Integer v) { value = v; }
    }

    public class AnyWrapper
    {
        public String name = "Foo";
        
        @JsonInclude(JsonInclude.Include.NON_NULL) 
        public Object wrapped;
        
        public AnyWrapper(Object w) { wrapped = w; }
    }
    
    /*
    /**********************************************************************
    /* Test methods
    /**********************************************************************
     */

    public void testIncludeUsingAnnotation() throws Exception
    {
        ObjectMapper mapper = mapperWithModule();

        String json = mapper.writeValueAsString(new IntWrapper(3));
        assertEquals("{\"value\":3}", json);
        json = mapper.writeValueAsString(new IntWrapper(null));
        assertEquals("{}", json);

        json = mapper.writeValueAsString(new AnyWrapper(new IntWrapper(null)));
        assertEquals("{\"name\":\"Foo\",\"wrapped\":{}}", json);
        json = mapper.writeValueAsString(new AnyWrapper(null));
        assertEquals("{\"name\":\"Foo\"}", json);
    }
}
