package com.fasterxml.jackson.module.afterburner.deser;

import com.fasterxml.jackson.annotation.*;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.fasterxml.jackson.module.afterburner.AfterburnerTestBase;

public class TestPolymorphic extends AfterburnerTestBase
{
    static class Envelope {
        @JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.EXTERNAL_PROPERTY, property="class")
        private Object payload;

        public Envelope(@JsonProperty("payload") Object payload) {
            this.payload = payload;
        }
        public Envelope() { }

        @JsonProperty
        public Object getPayload() {
            return payload;
        }
    }

    static class Payload {
        private String something;

        public Payload(@JsonProperty("something") String something) {
            this.something = something;
        }
        @JsonProperty
        public Object getSomething() {
            return something;
        }
    }

    public void testAfterburner() throws Exception {
        ObjectMapper mapper = mapperWithModule();
        Envelope envelope = new Envelope(new Payload("test"));
        String json = mapper.writeValueAsString(envelope);
        Envelope result = mapper.readValue(json, Envelope.class);

        assertNotNull(result);
        assertNotNull(result.payload);
        assertEquals(Payload.class, result.payload.getClass());
    }
}
