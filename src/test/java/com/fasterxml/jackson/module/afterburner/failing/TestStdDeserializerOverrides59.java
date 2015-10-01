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
        final String json = "{\"field\": \"value &amp; value\"}";
        final String EXP = "value & value";

        final Module module = new SimpleModule("module", Version.unknownVersion())
                .addDeserializer(String.class, new DeAmpDeserializer());

        // First; passes with vanilla ObjectMapper
        SimpleRO resultVanilla = new ObjectMapper()
            .registerModule(module)
            .readValue(json, SimpleRO.class);
        assertEquals(EXP, resultVanilla.field);

        // but then fails with Afterburner
        SimpleRO resultAB = mapperWithModule()
            .registerModule(module)
            .readValue(json, SimpleRO.class);
        assertEquals(EXP, resultAB.field);
    }
}
