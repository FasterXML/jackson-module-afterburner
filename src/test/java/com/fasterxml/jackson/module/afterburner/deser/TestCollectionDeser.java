package com.fasterxml.jackson.module.afterburner.deser;

import java.util.*;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.module.afterburner.AfterburnerTestBase;

public class TestCollectionDeser extends AfterburnerTestBase
{
    // [Issue#36]
    static class CollectionBean
    {
        private Collection<String> x = new TreeSet<String>();

        public Collection<String> getStuff() { return x; }
    }
    
    /*
    /**********************************************************************
    /* Test methods, method access
    /**********************************************************************
     */

    // [Issue#36]
    public void testIntMethod() throws Exception
    {
        ObjectMapper mapper = mapperWithModule();
        mapper.configure(MapperFeature.USE_GETTERS_AS_SETTERS, true);
        CollectionBean bean = mapper.readValue("{\"stuff\":[\"a\",\"b\"]}",
                CollectionBean.class);
        assertEquals(2, bean.x.size());
        assertEquals(TreeSet.class, bean.x.getClass());
    }
    
}
