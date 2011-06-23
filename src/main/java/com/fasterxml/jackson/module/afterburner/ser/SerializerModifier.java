package com.fasterxml.jackson.module.afterburner.ser;

import java.util.*;

import org.codehaus.jackson.map.*;
import org.codehaus.jackson.map.introspect.AnnotatedMember;
import org.codehaus.jackson.map.introspect.AnnotatedMethod;
import org.codehaus.jackson.map.introspect.BasicBeanDescription;
import org.codehaus.jackson.map.ser.*;

public class SerializerModifier extends BeanSerializerModifier
{
    public List<BeanPropertyWriter> changeProperties(SerializationConfig config,
            BasicBeanDescription beanDesc, List<BeanPropertyWriter> beanProperties)
    {
        PropertyCollector collector = new PropertyCollector();
        ListIterator<BeanPropertyWriter> it = beanProperties.listIterator();
        int count = 0;
        while (it.hasNext()) {
            BeanPropertyWriter bpw = it.next();
            Class<?> type = bpw.getPropertyType();
            AnnotatedMember member = bpw.getMember();
            boolean isMethod = (member instanceof AnnotatedMethod);
            if (type == Integer.TYPE) {
                if (isMethod) {
                    it.set(collector.addIntGetter(bpw));
                    ++count;
                }
            }
        }
        if (count == 0) {
            return beanProperties;
        }
        
        // if we had a match, need to materialize
        Class<?> beanClass = beanDesc.getBeanClass();
        BeanPropertyAccessor acc = collector.findAccessor(beanClass);
        // and then link accessors to bean property writers:
        it = beanProperties.listIterator();
        while (it.hasNext()) {
            BeanPropertyWriter bpw = it.next();
            if (bpw instanceof OptimizedBeanPropertyWriter<?>) {
                it.set(((OptimizedBeanPropertyWriter<?>) bpw).withAccessor(acc));
            }
        }
        return beanProperties;
    }
}
