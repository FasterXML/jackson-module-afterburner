package com.fasterxml.jackson.module.afterburner.deser;

import java.lang.reflect.Modifier;
import java.util.*;

import org.codehaus.jackson.map.*;
import org.codehaus.jackson.map.deser.*;
import org.codehaus.jackson.map.introspect.AnnotatedField;
import org.codehaus.jackson.map.introspect.AnnotatedMember;
import org.codehaus.jackson.map.introspect.AnnotatedMethod;
import org.codehaus.jackson.map.introspect.BasicBeanDescription;

import com.fasterxml.jackson.module.afterburner.util.MyClassLoader;

public class DeserializerModifier extends BeanDeserializerModifier
{
    /**
     * Class loader to use for generated classes; if null, will try to
     * use class loader of the target class.
     */
    protected final MyClassLoader _classLoader;
    
    public DeserializerModifier(ClassLoader cl) {
        // If we were given parent class loader explicitly, use that:
        _classLoader = (cl == null) ? null : new MyClassLoader(cl, false);
    }
    
    public BeanDeserializerBuilder updateBuilder(DeserializationConfig config,
            BasicBeanDescription beanDesc, BeanDeserializerBuilder builder) 
    {
        final Class<?> beanClass = beanDesc.getBeanClass();
        /* Hmmh. Can we access stuff from private classes?
         * Possibly, if we can use parent class loader.
         * (should probably skip all non-public?)
         */
        if (_classLoader != null) {
            if (Modifier.isPrivate(beanClass.getModifiers())) {
                return builder;
            }
        }
        PropertyMutatorCollector collector = new PropertyMutatorCollector();
        ArrayList<OptimizedSettableBeanProperty<?>> newProps = new ArrayList<OptimizedSettableBeanProperty<?>>();
        Iterator<SettableBeanProperty> propIterator = builder.getProperties();

        // Ok, then, find any properties for which we could generate accessors
        while (propIterator.hasNext()) {
            SettableBeanProperty prop = propIterator.next();
            AnnotatedMember member = prop.getMember();

            // First: we can't access private fields or methods....
            if (Modifier.isPrivate(member.getMember().getModifiers())) {
                continue;
            }
            // (although, interestingly enough, can seem to access private classes...)

            // !!! TODO: skip entries with non-standard serializer
            // (may need to add accessor(s) to BeanPropertyWriter?)
            
            if (prop instanceof SettableBeanProperty.MethodProperty) { // simple setter methods
                Class<?> type = ((AnnotatedMethod) member).getParameterClass(0);
                if (type.isPrimitive()) {
                    if (type == Integer.TYPE) {
                        newProps.add(collector.addIntSetter(prop));
                    } else if (type == Long.TYPE) {
                        newProps.add(collector.addLongSetter(prop));
                    }
                } else {
                    /*
                    if (type == String.class) {
                        it.set(collector.addStringGetter(bpw));
                    } else { // any other Object types; we can at least call accessor
                        it.set(collector.addObjectGetter(bpw));
                    }
                    */
                }
            } else if (prop instanceof SettableBeanProperty.FieldProperty) { // regular fields
                Class<?> type = ((AnnotatedField) member).getRawType();
                if (type.isPrimitive()) {
                    if (type == Integer.TYPE) {
                        newProps.add(collector.addIntField(prop));
                    } else if (type == Long.TYPE) {
                        newProps.add(collector.addLongField(prop));
                    }
                } else {
            }
            }
        }
        // and if we found any, create mutator proxy, replace property objects
        if (!newProps.isEmpty()) {
            BeanPropertyMutator mutator = collector.buildMutator(beanClass, _classLoader);
            for (OptimizedSettableBeanProperty<?> prop : newProps) {
                builder.addOrReplaceProperty(prop.withMutator(mutator), true);
            }
        }
        return builder;
    }
}
