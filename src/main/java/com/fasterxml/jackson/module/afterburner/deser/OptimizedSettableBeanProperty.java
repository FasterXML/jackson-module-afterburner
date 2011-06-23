package com.fasterxml.jackson.module.afterburner.deser;

import java.io.IOException;
import java.lang.annotation.Annotation;

import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.*;
import org.codehaus.jackson.map.deser.*;
import org.codehaus.jackson.map.introspect.AnnotatedMember;

/**
 * Base class for concrete type-specific {@link SettableBeanProperty}
 * implementations.
 */
abstract class OptimizedSettableBeanProperty<T extends OptimizedSettableBeanProperty<T>>
    extends SettableBeanProperty
{
    /**
     * We will need to keep the original instance handy as
     * some calls are best just delegated
     */
    protected final SettableBeanProperty _originalSettable;
    
    protected final BeanPropertyMutator _propertyMutator;
    protected final int _propertyIndex;
    
    public OptimizedSettableBeanProperty(SettableBeanProperty src,
            BeanPropertyMutator mutator, int index)
    {
        super(src);
        _originalSettable = src;
        _propertyMutator = mutator;
        _propertyIndex = index;
    }

    public abstract T withMutator(BeanPropertyMutator mut);

    @Override
    public abstract void deserializeAndSet(JsonParser jp, DeserializationContext ctxt,
            Object arg2) throws IOException, JsonProcessingException;

    @Override
    public <A extends Annotation> A getAnnotation(Class<A> ann) {
        return _originalSettable.getAnnotation(ann);
    }

    @Override
    public AnnotatedMember getMember() {
        return _originalSettable.getMember();
    }

    @Override
    public abstract void set(Object bean, Object value) throws IOException;
}
