package com.fasterxml.jackson.module.afterburner.deser;

import java.util.*;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.module.afterburner.AfterburnerTestBase;

public class TestCollectionDeser extends AfterburnerTestBase
{
    // [module-afterburner#36]
    static class CollectionBean
    {
        private Collection<String> x = new TreeSet<String>();

        public Collection<String> getStuff() { return x; }
    }

    static class IntBean {
        public int value;
    }
    
    /*
    /**********************************************************************
    /* Test methods
    /**********************************************************************
     */

    // [module-afterburner#36]
    public void testIntMethod() throws Exception
    {
        ObjectMapper mapper = mapperWithModule();
        mapper.configure(MapperFeature.USE_GETTERS_AS_SETTERS, true);
        CollectionBean bean = mapper.readValue("{\"stuff\":[\"a\",\"b\"]}",
                CollectionBean.class);
        assertEquals(2, bean.x.size());
        assertEquals(TreeSet.class, bean.x.getClass());
    }

    // [module-afterburner#56]
    public void testUnwrapSingleArray() throws Exception
    {
        final ObjectMapper mapper = mapperWithModule();
        mapper.enable(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS);

        /*
        final Integer intValue = mapper.readValue("[ 1 ]", Integer.class);
        assertEquals(Integer.valueOf(1), intValue);

        final String strValue = mapper.readValue("[ \"abc\" ]", String.class);
        assertEquals("abc", strValue);

        // and then via POJO. First, array of POJOs
        IntBean b1 = mapper.readValue(aposToQuotes("[{ 'value' : 123 }]"), IntBean.class);
        assertNotNull(b1);
        assertEquals(123, b1.value);
*/

        // and then array of ints within POJO
        IntBean b2 = mapper.readValue(aposToQuotes("{ 'value' : [ 123 ] }"), IntBean.class);
        assertNotNull(b2);
        assertEquals(123, b2.value);
    }
}
