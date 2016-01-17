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

    public class NonEmptyIntWrapper {
        private int value;
        public NonEmptyIntWrapper(int v) { value = v; }
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        public int getValue() { return value; }
    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class NonEmptyIntWrapper2 {
        public int value;
        public NonEmptyIntWrapper2(int v) { value = v; }
    }

    public class NonEmptyStringWrapper {
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        public String value;
        public NonEmptyStringWrapper(String v) { value = v; }
    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public class NonEmptyStringWrapper2 {
        public String value;
        public NonEmptyStringWrapper2(String v) { value = v; }
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

    // [module-afterburner#39]
    public void testEmptyExclusion() throws Exception
    {
        ObjectMapper mapper = mapperWithModule();
        String json;
        
        json = mapper.writeValueAsString(new NonEmptyStringWrapper("x"));
        assertEquals("{\"value\":\"x\"}", json);
        json = mapper.writeValueAsString(new NonEmptyStringWrapper(""));
        assertEquals("{}", json);
        json = mapper.writeValueAsString(new NonEmptyStringWrapper(null));
        assertEquals("{}", json);
    }

    public void testEmptyExclusionViaClass() throws Exception
    {
        ObjectMapper mapper = mapperWithModule();

        assertEquals("{\"value\":\"x\"}",
                mapper.writeValueAsString(new NonEmptyStringWrapper2("x")));
        assertEquals("{}",
                mapper.writeValueAsString(new NonEmptyStringWrapper2("")));
        assertEquals("{}",
                mapper.writeValueAsString(new NonEmptyStringWrapper2(null)));
    }
}
