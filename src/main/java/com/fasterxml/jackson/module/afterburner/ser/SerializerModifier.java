package com.fasterxml.jackson.module.afterburner.ser;

import java.lang.reflect.Modifier;
import java.util.*;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import com.fasterxml.jackson.databind.ser.*;

import com.fasterxml.jackson.module.afterburner.util.MyClassLoader;

public class SerializerModifier extends BeanSerializerModifier
{
    /**
     * Class loader to use for generated classes; if null, will try to
     * use class loader of the target class.
     */
    protected final MyClassLoader _classLoader;
    
    public SerializerModifier(ClassLoader cl)
    {
        // If we were given parent class loader explicitly, use that:
        _classLoader = (cl == null) ? null : new MyClassLoader(cl, false);
    }

    @Override
    public List<BeanPropertyWriter> changeProperties(SerializationConfig config,
            BeanDescription beanDesc, List<BeanPropertyWriter> beanProperties)
    {
        final Class<?> beanClass = beanDesc.getBeanClass();
        /* Hmmh. Can we access stuff from private classes?
         * Possibly, if we can use parent class loader.
         * (should probably skip all non-public?)
         */
        if (_classLoader != null) {
            if (Modifier.isPrivate(beanClass.getModifiers())) {
                return beanProperties;
            }
        }
        
        PropertyAccessorCollector collector = findProperties(beanProperties);
        if (collector.isEmpty()) {
            return beanProperties;
        }
        
        // if we had a match, need to materialize
        BeanPropertyAccessor acc = collector.findAccessor(beanClass, _classLoader);
        // and then link accessors to bean property writers:
        ListIterator<BeanPropertyWriter> it = beanProperties.listIterator();
        while (it.hasNext()) {
            BeanPropertyWriter bpw = it.next();
            if (bpw instanceof OptimizedBeanPropertyWriter<?>) {
                it.set(((OptimizedBeanPropertyWriter<?>) bpw).withAccessor(acc));
            }
        }
        return beanProperties;
    }

    protected PropertyAccessorCollector findProperties(List<BeanPropertyWriter> beanProperties)
    {
        PropertyAccessorCollector collector = new PropertyAccessorCollector();
        ListIterator<BeanPropertyWriter> it = beanProperties.listIterator();
        while (it.hasNext()) {
            BeanPropertyWriter bpw = it.next();
            Class<?> type = bpw.getPropertyType();
            AnnotatedMember member = bpw.getMember();

            // First: we can't access private fields or methods....
            if (Modifier.isPrivate(member.getMember().getModifiers())) {
                continue;
            }
            // (although, interestingly enough, can seem to access private classes...)

            // !!! TODO: skip entries with non-standard serializer
            // (may need to add accessor(s) to BeanPropertyWriter?)
            
            boolean isMethod = (member instanceof AnnotatedMethod);
            if (type.isPrimitive()) {
                if (type == Integer.TYPE) {
                    if (isMethod) {
                        it.set(collector.addIntGetter(bpw));
                    } else {
                        it.set(collector.addIntField(bpw));
                    }
                } else if (type == Long.TYPE) {
                    if (isMethod) {
                        it.set(collector.addLongGetter(bpw));
                    } else {
                        it.set(collector.addLongField(bpw));
                    }
                }
            } else {
                if (type == String.class) {
                    if (isMethod) {
                        it.set(collector.addStringGetter(bpw));
                    } else {
                        it.set(collector.addStringField(bpw));
                    }
                } else { // any other Object types; we can at least call accessor
                    if (isMethod) {
                        it.set(collector.addObjectGetter(bpw));
                    } else {
                        it.set(collector.addObjectField(bpw));
                    }
                }
            }
        }
        return collector;
    }
}
