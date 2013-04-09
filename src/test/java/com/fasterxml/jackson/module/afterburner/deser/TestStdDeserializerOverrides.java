package com.fasterxml.jackson.module.afterburner.deser;

import java.io.IOException;

import com.fasterxml.jackson.core.*;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import com.fasterxml.jackson.module.afterburner.AfterburnerTestBase;

public class TestStdDeserializerOverrides extends AfterburnerTestBase
{
    static class ClassWithPropOverrides
    {
        public String a;
        
        @JsonDeserialize(using=MyStringDeserializer.class)
        public String b;
    }

    static class MyStringDeserializer extends StdDeserializer<String>
    {
        private static final long serialVersionUID = 1L;

        public MyStringDeserializer() { super(String.class); }

        @Override
        public String deserialize(JsonParser jp, DeserializationContext ctxt)
                throws IOException, JsonProcessingException {
            return "Foo:"+jp.getText();
        }
    }
    
    /*
    /**********************************************************************
    /* Test methods
    /**********************************************************************
     */

    public void testFiveMinuteDoc() throws Exception
    {
        ObjectMapper plainMapper = new ObjectMapper();
        ObjectMapper abMapper = mapperWithModule();
        final String JSON = "{\"a\":\"a\",\"b\":\"b\"}";
        
        ClassWithPropOverrides vanilla = plainMapper.readValue(JSON, ClassWithPropOverrides.class);
        ClassWithPropOverrides burnt = abMapper.readValue(JSON, ClassWithPropOverrides.class);
        
        assertEquals("a", vanilla.a);
        assertEquals("Foo:b", vanilla.b);
        
        assertEquals("a", burnt.a);
        assertEquals("Foo:b", burnt.b);
    }
}
