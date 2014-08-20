package com.fasterxml.jackson.module.afterburner.deser;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.module.afterburner.AfterburnerTestBase;

public class TestFinalFields extends AfterburnerTestBase
{
    static class Address {
        public int zip1, zip2;

        public Address() { }
        public Address(int z1, int z2) {
            zip1 = z1;
            zip2 = z2;
        }
    }
    
    @JsonInclude(JsonInclude.Include.NON_NULL)
    static class Organization
    {
        public final long id;
        public final String name;
        public final Address address;

        @JsonCreator
        public Organization(@JsonProperty("id") long id,
                @JsonProperty("name") String name,
                @JsonProperty("address") Address address)
        {
            this.id = id;
            this.name = name;
            this.address = address;
        }
    }

    /*
    /**********************************************************
    /* Unit tests
    /**********************************************************
     */

    public void testFinalFields() throws Exception
    {
        ObjectMapper mapper = mapperWithModule();
        String json = mapper.writeValueAsString(new Organization[] {
                new Organization(123L, "Corp", new Address(98040, 98021))
        });
        Organization[] result = mapper.readValue(json, Organization[].class);
        assertNotNull(result);
        assertEquals(1, result.length);
        assertNotNull(result[0]);
        assertNotNull(result[0].address);
        assertEquals(98021, result[0].address.zip2);
    }
}
