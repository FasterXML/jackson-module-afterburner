package com.fasterxml.jackson.module.afterburner.ser;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.*;

import com.fasterxml.jackson.module.afterburner.AfterburnerTestBase;

public class TestSimpleSerialize extends AfterburnerTestBase
{
    /*
    /**********************************************************************
    /* Helper types
    /**********************************************************************
     */

    /* Keep this as package access, since we can't handle private; but
     * public is pretty much always available.
     */
    static class IntBean {
        @JsonProperty("x")
        int getX() { return 123; }
    }

    static class LongBean {
        public long getValue() { return -99L; }
    }

    static class IntFieldBean {
        @JsonProperty("x") int x = 17;
    }

    static class LongFieldBean {
        @JsonProperty("long") long l = -123L;
    }
    
    /*
    /**********************************************************************
    /* Test methods
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

    public void testIntField() throws Exception {
        ObjectMapper mapper = mapperWithModule();
        assertEquals("{\"x\":17}", mapper.writeValueAsString(new IntFieldBean()));
    }

    public void testLongField() throws Exception {
        ObjectMapper mapper = mapperWithModule();
        assertEquals("{\"long\":-123}", mapper.writeValueAsString(new LongFieldBean()));
    }
}
