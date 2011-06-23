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
    
    /*
    /**********************************************************************
    /* Test methods
    /**********************************************************************
     */

    public void testInt() throws Exception {
        ObjectMapper mapper = mapperWithModule();
        assertEquals("{\"x\":123}", mapper.writeValueAsString(new IntBean()));
    }

    public void testLong() throws Exception {
        ObjectMapper mapper = mapperWithModule();
        assertEquals("{\"value\":-99}", mapper.writeValueAsString(new LongBean()));
    }
}
