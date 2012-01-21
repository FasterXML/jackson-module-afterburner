package com.fasterxml.jackson.module.afterburner.ser;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.fasterxml.jackson.module.afterburner.AfterburnerTestBase;

public class TestSimpleSerialize extends AfterburnerTestBase
{
    /*
    /**********************************************************************
    /* Helper types
    /**********************************************************************
     */

    public enum MyEnum {
        A, B, C;
    }
    
    /* Keep this as package access, since we can't handle private; but
     * public is pretty much always available.
     */
    static class IntBean {
        @JsonProperty("x")
        int getX() { return 123; }
    }

    public static class LongBean {
        public long getValue() { return -99L; }
    }

    static class StringBean {
        public String getName() { return "abc"; }
    }

    static class EnumBean {
        public MyEnum getEnum() { return MyEnum.B; }
    }
    
    static class IntFieldBean {
        @JsonProperty("intF") int x = 17;
    }

    static class LongFieldBean {
        @JsonProperty("long") long l = -123L;
    }

    public static class StringFieldBean {
        public String foo = "bar";
    }

    public static class EnumFieldBean {
        public MyEnum value = MyEnum.C;
    }
    
    /*
    /**********************************************************************
    /* Test methods, method access
    /**********************************************************************
     */

    public void testIntMethod() throws Exception {
        ObjectMapper mapper = mapperWithModule();
        assertEquals("{\"x\":123}", mapper.writeValueAsString(new IntBean()));
    }

    public void testLongMethod() throws Exception {
        ObjectMapper mapper = mapperWithModule();
        assertEquals("{\"value\":-99}", mapper.writeValueAsString(new LongBean()));
    }

    public void testStringMethod() throws Exception {
        ObjectMapper mapper = mapperWithModule();
        assertEquals("{\"name\":\"abc\"}", mapper.writeValueAsString(new StringBean()));
    }

    public void testObjectMethod() throws Exception {
        ObjectMapper mapper = mapperWithModule();
        assertEquals("{\"enum\":\"B\"}", mapper.writeValueAsString(new EnumBean()));
    }
    
    /*
    /**********************************************************************
    /* Test methods, field access
    /**********************************************************************
     */
    
    public void testIntField() throws Exception {
        ObjectMapper mapper = mapperWithModule();
        assertEquals("{\"intF\":17}", mapper.writeValueAsString(new IntFieldBean()));
    }

    public void testLongField() throws Exception {
        ObjectMapper mapper = mapperWithModule();
        assertEquals("{\"long\":-123}", mapper.writeValueAsString(new LongFieldBean()));
    }

    public void testStringField() throws Exception {
        ObjectMapper mapper = mapperWithModule();
        assertEquals("{\"foo\":\"bar\"}", mapper.writeValueAsString(new StringFieldBean()));
    }

    public void testObjectField() throws Exception {
        ObjectMapper mapper = mapperWithModule();
        assertEquals("{\"value\":\"C\"}", mapper.writeValueAsString(new EnumFieldBean()));
    }

    /*
    /**********************************************************************
    /* Test methods, other
    /**********************************************************************
     */
    
    public void testFiveMinuteDoc() throws Exception
    {
        ObjectMapper plainMapper = new ObjectMapper();
        ObjectMapper abMapper = mapperWithModule();
        FiveMinuteUser input = new FiveMinuteUser("First", "Name", true,
                FiveMinuteUser.Gender.FEMALE, new byte[] { 1 } );
        String jsonPlain = plainMapper.writeValueAsString(input);
        String jsonAb = abMapper.writeValueAsString(input);
        assertEquals(jsonPlain, jsonAb);
    }

}
