package com.fasterxml.jackson.module.afterburner.deser;

import java.io.IOException;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.SettableBeanProperty;

public final class SettableBooleanMethodProperty
    extends OptimizedSettableBeanProperty<SettableBooleanMethodProperty>
{
    private static final long serialVersionUID = 1L;

    public SettableBooleanMethodProperty(SettableBeanProperty src,
            BeanPropertyMutator mutator, int index)
    {
        super(src, mutator, index);
    }

    public SettableBooleanMethodProperty(SettableBooleanMethodProperty src, JsonDeserializer<?> deser) {
        super(src, deser);
    }

    public SettableBooleanMethodProperty(SettableBooleanMethodProperty src, PropertyName name) {
        super(src, name);
    }

    @Override
    public SettableBooleanMethodProperty withName(PropertyName name) {
        return new SettableBooleanMethodProperty(this, name);
    }
    
    @Override
    public SettableBooleanMethodProperty withValueDeserializer(JsonDeserializer<?> deser) {
        return new SettableBooleanMethodProperty(this, deser);
    }
    
    @Override
    public SettableBooleanMethodProperty withMutator(BeanPropertyMutator mut) {
        return new SettableBooleanMethodProperty(_originalSettable, mut, _optimizedIndex);
    }

    /*
    /********************************************************************** 
    /* Deserialization
    /********************************************************************** 
     */
    
    @Override
    public void deserializeAndSet(JsonParser jp, DeserializationContext ctxt, Object bean) throws IOException {
        _propertyMutator.booleanSetter(_originalSettable, bean, _optimizedIndex, _deserializeBoolean(jp, ctxt));
    }

    @Override
    public void set(Object bean, Object value) throws IOException {
        // not optimal (due to boxing), but better than using reflection:
        _propertyMutator.booleanSetter(_originalSettable, bean, _optimizedIndex, ((Boolean) value).booleanValue());
    }

    @Override
    public Object deserializeSetAndReturn(JsonParser jp,
            DeserializationContext ctxt, Object instance) throws IOException
    {
        return setAndReturn(instance, _deserializeBoolean(jp, ctxt));
    }    
}
