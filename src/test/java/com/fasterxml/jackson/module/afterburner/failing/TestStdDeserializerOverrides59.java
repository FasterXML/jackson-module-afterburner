package com.fasterxml.jackson.module.afterburner.failing;

import java.io.IOException;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.module.afterburner.AfterburnerTestBase;

@SuppressWarnings("serial")
public class TestStdDeserializerOverrides59 extends AfterburnerTestBase
{
    // for [module-afterburner#59]
    static class SimpleRO {
        public String field;
    }

    static class DeAmpDeserializer extends StdDeserializer<String>
    {
        public DeAmpDeserializer() { super(String.class); }

        @Override
        public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            return p.getText().replaceAll("&amp;", "&");
        }
    }

    /*
    /**********************************************************************
    /* Test methods
    /**********************************************************************
     */

    // for [module-afterburner#59]
    public void testStringDeserializerOveride59() throws Exception
    {
        ObjectMapper mapper = mapperWithModule();
        SimpleModule module = new SimpleModule("module", Version.unknownVersion());
        module.addDeserializer(String.class, new DeAmpDeserializer());

        String json = "{\"field\": \"value &amp; value\"}";
        SimpleRO parsedRO = mapper.readValue(json, SimpleRO.class);
        assertEquals("value & value", parsedRO.field);
    }
}
