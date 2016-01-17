package com.fasterxml.jackson.module.afterburner.ser;

import com.fasterxml.jackson.annotation.JsonInclude;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.fasterxml.jackson.module.afterburner.AfterburnerTestBase;

public class TestInclusionAnnotations extends AfterburnerTestBase
{
    static class IntWrapper
    {
        @JsonInclude(JsonInclude.Include.NON_NULL) 
        public Integer value;
        
        public IntWrapper(Integer v) { value = v; }
    }

    static class NonEmptyIntWrapper {
        private int value;
        public NonEmptyIntWrapper(int v) { value = v; }
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        public int getValue() { return value; }
    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    static class NonEmptyIntWrapper2 {
        public int value;
        public NonEmptyIntWrapper2(int v) { value = v; }
    }

    // But whereas 'empty' should NOT include '0', 'default' for property should
    static class NonDefaultIntWrapper {
        private int value;
        public NonDefaultIntWrapper(int v) { value = v; }
        @JsonInclude(JsonInclude.Include.NON_DEFAULT)
        public int getValue() { return value; }
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

        json = mapper.writeValueAsString(new NonEmptyIntWrapper(3));
        assertEquals("{\"value\":3}", json);
        // as per [module-afterburner#63], ints should not have "empty" value
        // (temporarily, for 2.6, they did have)
        json = mapper.writeValueAsString(new NonEmptyIntWrapper(0));
        assertEquals("{\"value\":0}", json);

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

        assertEquals("{\"value\":3}",
                mapper.writeValueAsString(new NonEmptyIntWrapper2(3)));
        assertEquals("{\"value\":0}",
                mapper.writeValueAsString(new NonEmptyIntWrapper2(0)));

        assertEquals("{\"value\":\"x\"}",
                mapper.writeValueAsString(new NonEmptyStringWrapper2("x")));
        assertEquals("{}",
                mapper.writeValueAsString(new NonEmptyStringWrapper2("")));
        assertEquals("{}",
                mapper.writeValueAsString(new NonEmptyStringWrapper2(null)));
    }

    public void testDefaultExclusion() throws Exception
    {
        ObjectMapper mapper = mapperWithModule();
        String json;

        json = mapper.writeValueAsString(new NonDefaultIntWrapper(3));
        assertEquals("{\"value\":3}", json);
        json = mapper.writeValueAsString(new NonDefaultIntWrapper(0));
        assertEquals("{}", json);
    }
}
