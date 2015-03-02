package com.fasterxml.jackson.module.afterburner.deser;

import java.io.IOException;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.SettableBeanProperty;

public final class SettableLongMethodProperty
    extends OptimizedSettableBeanProperty<SettableLongMethodProperty>
{
    private static final long serialVersionUID = 1L;

    public SettableLongMethodProperty(SettableBeanProperty src,
            BeanPropertyMutator mutator, int index)
    {
        super(src, mutator, index);
    }

    public SettableLongMethodProperty(SettableLongMethodProperty src, JsonDeserializer<?> deser) {
        super(src, deser);
    }

    public SettableLongMethodProperty(SettableLongMethodProperty src, PropertyName name) {
        super(src, name);
    }
    
    @Override
    public SettableLongMethodProperty withName(PropertyName name) {
        return new SettableLongMethodProperty(this, name);
    }
    
    @Override
    public SettableLongMethodProperty withValueDeserializer(JsonDeserializer<?> deser) {
        return new SettableLongMethodProperty(this, deser);
    }
    
    @Override
    public SettableLongMethodProperty withMutator(BeanPropertyMutator mut) {
        return new SettableLongMethodProperty(_originalSettable, mut, _optimizedIndex);
    }

    /*
    /********************************************************************** 
    /* Deserialization
    /********************************************************************** 
     */
    
    @Override
    public void deserializeAndSet(JsonParser p, DeserializationContext ctxt,
            Object bean) throws IOException
    {
        _propertyMutator.longSetter(bean, p.getValueAsLong());
    }

    @Override
    public void set(Object bean, Object value) throws IOException {
        // not optimal (due to boxing), but better than using reflection:
        _propertyMutator.longSetter(bean, ((Number) value).longValue());
    }

    @Override
    public Object deserializeSetAndReturn(JsonParser p,
            DeserializationContext ctxt, Object instance) throws IOException
    {
        return setAndReturn(instance, p.getValueAsLong());
    }    
}
