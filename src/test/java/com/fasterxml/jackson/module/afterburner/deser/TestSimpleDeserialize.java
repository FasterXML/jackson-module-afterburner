package com.fasterxml.jackson.module.afterburner.deser;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonPropertyOrder;
import org.codehaus.jackson.map.*;

import com.fasterxml.jackson.module.afterburner.AfterburnerTestBase;

public class TestSimpleDeserialize extends AfterburnerTestBase
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
        protected int _x;
        
        void setX(int v) { _x = v; }
    }

    @JsonPropertyOrder({"c","a","b","e","d"})
    static class IntsBean {
        protected int _a, _b, _c, _d, _e;
        
        void setA(int v) { _a = v; }
        void setB(int v) { _b = v; }
        void setC(int v) { _c = v; }
        void setD(int v) { _d = v; }
        void setE(int v) { _e = v; }
    }
    
    public static class LongBean {
        protected long _x;
        
        public void setX(long v) { _x = v; }
    }

    public static class StringBean {
        protected String _x;
        
        public void setX(String v) { _x = v; }
    }

    public static class EnumBean {
        protected MyEnum _x;
        
        public void setX(MyEnum v) { _x = v; }
    }
    
    public static class IntFieldBean {
        @JsonProperty("value") int x;
    }
    static class LongFieldBean {
        public long value;
    }
    static class StringFieldBean {
        public String x;
    }
    static class EnumFieldBean {
        public MyEnum x;
    }

    @JsonPropertyOrder
    ({"stringField", "string", "intField", "int", "longField", "long", "enumField", "enum"})
    static class MixedBean {
        public String stringField;
        public int intField;
        public long longField;
        public MyEnum enumField;

        protected String stringMethod;
        protected int intMethod;
        protected long longMethod;
        protected MyEnum enumMethod;

        public void setInt(int i) { intMethod = i; }
        public void setLong(long l) { longMethod = l; }
        public void setString(String s) { stringMethod = s; }
        public void setEnum(MyEnum e) { enumMethod = e; }
    }
    
    /*
    /**********************************************************************
    /* Test methods, method access
    /**********************************************************************
     */

    public void testIntMethod() throws Exception {
        ObjectMapper mapper = mapperWithModule();
        IntBean bean = mapper.readValue("{\"x\":13}", IntBean.class);
        assertEquals(13, bean._x);
    }

    public void testMultiIntMethod() throws Exception {
        ObjectMapper mapper = mapperWithModule();
        IntsBean bean = mapper.readValue("{\"c\":3,\"a\":9,\"b\":111,\"e\":-9999,\"d\":1}", IntsBean.class);
        assertEquals(9, bean._a);
        assertEquals(111, bean._b);
        assertEquals(3, bean._c);
        assertEquals(1, bean._d);
        assertEquals(-9999, bean._e);
    }
    
    public void testLongMethod() throws Exception {
        ObjectMapper mapper = mapperWithModule();
        LongBean bean = mapper.readValue("{\"x\":-1}", LongBean.class);
        assertEquals(-1, bean._x);
    }

    public void testStringMethod() throws Exception {
        ObjectMapper mapper = mapperWithModule();
        StringBean bean = mapper.readValue("{\"x\":\"zoobar\"}", StringBean.class);
        assertEquals("zoobar", bean._x);
    }

    public void testObjectMethod() throws Exception {
        ObjectMapper mapper = mapperWithModule();
        EnumBean bean = mapper.readValue("{\"x\":\"A\"}", EnumBean.class);
        assertEquals(MyEnum.A, bean._x);
    }
    
    /*
    /**********************************************************************
    /* Test methods, field access
    /**********************************************************************
     */

    public void testIntField() throws Exception {
        ObjectMapper mapper = mapperWithModule();
        IntFieldBean bean = mapper.readValue("{\"value\":-92}", IntFieldBean.class);
        assertEquals(-92, bean.x);
    }

    public void testLongField() throws Exception {
        ObjectMapper mapper = mapperWithModule();
        LongFieldBean bean = mapper.readValue("{\"value\":-92}", LongFieldBean.class);
        assertEquals(-92, bean.value);
    }

    public void testStringField() throws Exception {
        ObjectMapper mapper = mapperWithModule();
        StringFieldBean bean = mapper.readValue("{\"x\":\"\"}", StringFieldBean.class);
        assertEquals("", bean.x);
    }

    public void testEnumField() throws Exception {
        ObjectMapper mapper = mapperWithModule();
        EnumFieldBean bean = mapper.readValue("{\"x\":\"C\"}", EnumFieldBean.class);
        assertEquals(MyEnum.C, bean.x);
    }
    
    /*
    /**********************************************************************
    /* Test methods, other
    /**********************************************************************
     */

    public void testFiveMinuteDoc() throws Exception
    {
        ObjectMapper abMapper = mapperWithModule();
        FiveMinuteUser input = new FiveMinuteUser("First", "Name", true,
                FiveMinuteUser.Gender.FEMALE, new byte[] { 1 } );
        String jsonAb = abMapper.writeValueAsString(input);

        FiveMinuteUser output = abMapper.readValue(jsonAb, FiveMinuteUser.class);
        if (!output.equals(input)) {
            fail("Round-trip test failed: intermediate JSON = "+jsonAb);
        }
    }
    
    public void testMixed() throws Exception
    {
        ObjectMapper mapper = mapperWithModule();
        MixedBean bean = mapper.readValue("{"
                +"\"stringField\":\"a\","
                +"\"string\":\"b\","
                +"\"intField\":3,"
                +"\"int\":4,"
                +"\"longField\":-3,"
                +"\"long\":11,"
                +"\"enumField\":\"A\","
                +"\"enum\":\"B\""
                +"}", MixedBean.class);

        assertEquals("a", bean.stringField);
        assertEquals("b", bean.stringMethod);
        assertEquals(3, bean.intField);
        assertEquals(4, bean.intMethod);
        assertEquals(-3L, bean.longField);
        assertEquals(11L, bean.longMethod);
        assertEquals(MyEnum.A, bean.enumField);
        assertEquals(MyEnum.B, bean.enumMethod);
    }
}
