package com.fasterxml.jackson.module.afterburner.ser;

import java.util.*;

import org.codehaus.jackson.map.*;
import org.codehaus.jackson.map.introspect.AnnotatedMember;
import org.codehaus.jackson.map.introspect.AnnotatedMethod;
import org.codehaus.jackson.map.introspect.BasicBeanDescription;
import org.codehaus.jackson.map.ser.*;

import com.fasterxml.jackson.module.afterburner.util.MyClassLoader;

public class SerializerModifier extends BeanSerializerModifier
{
    public List<BeanPropertyWriter> changeProperties(SerializationConfig config,
            BasicBeanDescription beanDesc, List<BeanPropertyWriter> beanProperties)
    {
        PropertyCollector collector = new PropertyCollector();
        ListIterator<BeanPropertyWriter> it = beanProperties.listIterator();
        while (it.hasNext()) {
            BeanPropertyWriter bpw = it.next();
            Class<?> type = bpw.getPropertyType();
            AnnotatedMember member = bpw.getMember();
            boolean isMethod = (member instanceof AnnotatedMethod);
            if (type == Integer.TYPE) {
                if (isMethod) {
                    it.set(collector.addIntGetter(bpw));
                }
            }
        }
        // if we had a match, need to materialize
        if (collector.hasEntries()) {
            Class<?> beanClass = beanDesc.getBeanClass();
            MyClassLoader loader = new MyClassLoader(beanClass.getClassLoader());
        }
        return beanProperties;
    }
}
