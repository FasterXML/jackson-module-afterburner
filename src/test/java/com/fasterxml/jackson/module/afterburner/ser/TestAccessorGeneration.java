package com.fasterxml.jackson.module.afterburner.ser;

import java.lang.reflect.Method;

import org.codehaus.jackson.io.SerializedString;
import org.codehaus.jackson.map.introspect.AnnotatedMethod;
import org.codehaus.jackson.map.introspect.AnnotationMap;
import org.codehaus.jackson.map.ser.BeanPropertyWriter;

import com.fasterxml.jackson.module.afterburner.AfterburnerTestBase;

public class TestAccessorGeneration extends AfterburnerTestBase
{
    /*
    /**********************************************************************
    /* Helper methods
    /**********************************************************************
     */

    public static class Bean {
        public int getX() { return 13; }
        public int getY() { return 27; }
    }
    
    /*
    /**********************************************************************
    /* Test methods
    /**********************************************************************
     */
    
    public void testSingleIntAccessorGeneration() throws Exception
    {
        Method method = Bean.class.getDeclaredMethod("getX");
        AnnotatedMethod annMethod = new AnnotatedMethod(method, null, null);
        PropertyCollector coll = new PropertyCollector();
        BeanPropertyWriter bpw = new BeanPropertyWriter(annMethod, null,
                new SerializedString("x"), null,
                null, null, null,
                method, null, false, null);
        coll.addIntGetter(bpw);
        BeanPropertyAccessor acc = coll.findAccessor(Bean.class);
        Bean bean = new Bean();
        int value = acc.intGetter(bean, 0);
        assertEquals(bean.getX(), value);
    }

    public void testDualIntAccessorGeneration() throws Exception
    {
        PropertyCollector coll = new PropertyCollector();

        Method method1 = Bean.class.getDeclaredMethod("getX");
        AnnotatedMethod annMethod1 = new AnnotatedMethod(method1, null, null);
        // ok; two int getters...
        coll.addIntGetter(new BeanPropertyWriter(annMethod1, null,
                new SerializedString("x"), null,
                null, null, null,
                method1, null, false, null));
        Method method2 = Bean.class.getDeclaredMethod("getY");
        AnnotatedMethod annMethod2 = new AnnotatedMethod(method2, null, null);
        coll.addIntGetter(new BeanPropertyWriter(annMethod2, null,
                new SerializedString("y"), null,
                null, null, null,
                method2, null, false, null));

        BeanPropertyAccessor acc = coll.findAccessor(Bean.class);
        Bean bean = new Bean();

        assertEquals(bean.getX(), acc.intGetter(bean, 0));
        assertEquals(bean.getY(), acc.intGetter(bean, 1));
    }

}
