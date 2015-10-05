package com.fasterxml.jackson.module.afterburner.failing;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.fasterxml.jackson.module.afterburner.AfterburnerTestBase;

public class TestStdSerializerOverrides extends AfterburnerTestBase
{
    // for [module-afterburner#59]
    static class SimpleRO {
        public String field = "foo";
    }

    @SuppressWarnings("serial")
    static class MeaningOfLifeSerializer extends StdSerializer<String>
    {
        public MeaningOfLifeSerializer() { super(String.class); }

        @Override
        public void serialize(String value, JsonGenerator gen,
                SerializerProvider provider) throws IOException {
            gen.writeNumber(42);
        }
    }    

    /*
    /**********************************************************************
    /* Test methods
    /**********************************************************************
     */

    public void testStringSerOverideNoAfterburner() throws Exception
    {
        final SimpleRO input = new SimpleRO();
        final String EXP = "{\"field\":42}";
        String json = new ObjectMapper()
            .registerModule(new SimpleModule("module", Version.unknownVersion())
                .addSerializer(String.class, new MeaningOfLifeSerializer()))
            .writeValueAsString(input);
        assertEquals(EXP, json);
    }

    public void testStringSerOverideWithAfterburner() throws Exception
    {
        final SimpleRO input = new SimpleRO();
        final String EXP = "{\"field\":42}";
        String json = mapperWithModule()
            .registerModule(new SimpleModule("module", Version.unknownVersion())
                .addSerializer(String.class, new MeaningOfLifeSerializer()))
            .writeValueAsString(input);
        assertEquals(EXP, json);
    }
}
