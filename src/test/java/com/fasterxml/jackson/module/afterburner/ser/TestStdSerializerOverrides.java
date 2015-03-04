package com.fasterxml.jackson.module.afterburner.ser;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.fasterxml.jackson.module.afterburner.AfterburnerTestBase;

public class TestStdSerializerOverrides extends AfterburnerTestBase
{
    static class ClassWithPropOverrides
    {
        public String a = "a";
        
        @JsonSerialize(using=MyStringSerializer.class)
        public String b = "b";
    }

    @SuppressWarnings("serial")
    static class MyStringSerializer extends StdSerializer<String>
    {
        public MyStringSerializer() { super(String.class); }

        @Override
        public void serialize(String value, JsonGenerator jgen,
                SerializerProvider provider) throws IOException,
                JsonGenerationException {
            jgen.writeString("Foo:"+value);
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
        ClassWithPropOverrides input = new ClassWithPropOverrides();
        String jsonPlain = plainMapper.writeValueAsString(input);
        String jsonAb = abMapper.writeValueAsString(input);
        assertEquals(jsonPlain, jsonAb);
    }
}
